package com.ellabook.bookdownloader

import android.content.Context
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloadListener
import com.liulishuo.filedownloader.FileDownloader
import com.liulishuo.filedownloader.exception.*
import com.liulishuo.filedownloader.model.FileDownloadStatus
import com.liulishuo.filedownloader.util.FileDownloadUtils
import java.io.File
import java.util.HashMap
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * 对外公开下载类
 * Created by mpokh on 2018/3/19.
 */

object BookDownloader{
    val BOOKCODE = 1000
    val BOOKTAG = 1001
    var executorService : ExecutorService? = null

    //初始化
    fun init(context : Context,loglv:Int) {
        DownloadLogcat.LOG_LEVEL = loglv    //1-5之间
        FileDownloader.setup(context);
        FileDownloader.enableAvoidDropFrame();//开启 避免掉帧处理。就是将抛消息到ui线程的间隔设为默认值10ms, 很明显会影响的是回调不会立马通知到监听器(FileDownloadListener)中，默认值是: 最多10ms处理5个回调到监听器中
        FileDownloader.setGlobalPost2UIInterval(100);//为了避免掉帧，这里是设置了最多每interval毫秒抛一个消息到ui线程(使用Handler)，防止由于回调的过于频繁导致ui线程被ddos导致掉帧。 默认值: 10ms. 如果设置小于0，将会失效，也就是说每个回调都直接抛一个消息到ui线程
        FileDownloader.setGlobalHandleSubPackageSize(6);//为了避免掉帧, 如果上面的方法设置的间隔是一个小于0的数，这个packageSize将不会生效。packageSize这个值是为了避免在ui线程中一次处理过多回调，结合上面的间隔，就是每个interval毫秒间隔抛一个消息到ui线程，而每个消息在ui线程中处理packageSize个回调。默认值: 5
        FileDownloader.getImpl().setMaxNetworkThreadCount(2);//设置最大并行下载的数目(网络下载线程数), [1,12]
    }

    fun setLogLV(lv:Int){
        DownloadLogcat.LOG_LEVEL = lv    //1-5之间
    }

    //线程池
    fun getCachedThreadPool(): ExecutorService? {
        if (executorService == null)
            executorService = ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L
                    , TimeUnit.SECONDS, SynchronousQueue<Runnable>())
        return executorService
    }

    //下载
    fun startDownload(bookdata : BookDownloadData){
        try {
            val url = bookdata.url
            val targetPath = bookdata.targetPath
            val wifi = bookdata.isIswifi
            val sync = bookdata.isSyncCallback
            val listener: SimpleBookDownloadCallback = bookdata.listener
            val stauts = FileDownloader.getImpl().getStatus(url, targetPath)
            getDownloadData(bookdata.bookcode)?.reload(bookdata)
            var run = false
            when (stauts) {
                FileDownloadStatus.INVALID_STATUS -> {
                    run = true
                    DownloadLogcat.d("show", targetPath + " => FileDownloadStatus.INVALID_STATUS")
                }
                FileDownloadStatus.started -> {
                    run = false
                    DownloadLogcat.d("show", targetPath + " => FileDownloadStatus.started")
                }
                FileDownloadStatus.blockComplete -> {
                    run = false
                    DownloadLogcat.d("show", targetPath + " => FileDownloadStatus.blockComplete")
                }
                FileDownloadStatus.completed -> {
                    run = false
                    DownloadLogcat.d("show", targetPath + " => FileDownloadStatus.completed")
                }
                FileDownloadStatus.connected -> {
                    run = false
                    DownloadLogcat.d("show", targetPath + " => FileDownloadStatus.connected")
                }
                FileDownloadStatus.error -> {
                    run = true
                    DownloadLogcat.d("show", targetPath + " => FileDownloadStatus.error")
                }
                FileDownloadStatus.paused -> {
                    run = true
                    DownloadLogcat.d("show", targetPath + " => FileDownloadStatus.paused")
                }
                FileDownloadStatus.pending -> {
                    run = false
                    DownloadLogcat.d("show", targetPath + " => FileDownloadStatus.pending")
                }
                FileDownloadStatus.progress -> {
                    run = false
                    DownloadLogcat.d("show", targetPath + " => FileDownloadStatus.progress")
                }
                FileDownloadStatus.warn -> {
                    run = false
                    DownloadLogcat.d("show", targetPath + " => FileDownloadStatus.warn")
                }
            }
            DownloadLogcat.d("show", targetPath + "startDownload => " + run)
            if (run) {
                FileDownloader.getImpl().create(url).setPath(targetPath)
                        .setWifiRequired(wifi).setSyncCallback(sync)
                        .setListener(setDownloadListener(listener)).setForceReDownload(true)
                        .setTag(BOOKCODE,bookdata.bookcode)
                        .setTag(BOOKTAG,bookdata.tag)
                        .start()
                bookdata.setUIStatus(BookDownloadStatus.START)
            }
        }catch (e : Exception){
            DownloadLogcat.e("201803201336",e.toString())
            bookdata.setUIStatus(BookDownloadStatus.STOP)
        }
    }

    //暂停下载
    fun pauseDownload(bookdata : BookDownloadData?){
        bookdata?.setUIStatus(BookDownloadStatus.PAUSED)
        if (bookdata == null)
            return
        val url = bookdata.url
        val targetPath = bookdata.targetPath
        val size : Int = FileDownloader.getImpl().pause(FileDownloadUtils.generateId(url, targetPath))
        if (size == 0)
            FileDownloader.getImpl().clear(FileDownloadUtils.generateId(url, targetPath),targetPath)
    }

    //全部暂停
    fun pauseAll(){
        FileDownloader.getImpl().pauseAll()
        FileDownloader.getImpl().clearAllTaskData()
        BookDownloadDataMap.clear()
    }

    //全部关闭下载
    fun stopAll(){
        FileDownloader.getImpl().pauseAll()
        FileDownloader.getImpl().clearAllTaskData()
        FileDownloader.getImpl().unBindService()
        BookDownloadDataMap.clear()
    }

    //获取全部任务
    fun getAllTask() : HashMap<String, BookDownloadData>
    {
        return BookDownloadDataMap.getInstance().hashMap
    }
    //获取一个空的下载信息类
    fun newBookDownloadInstance(bookcode: String,url: String,targetpath: String,iswifi: Boolean ): BookDownloadData {
        val data= BookDownloadData.newInstance(bookcode).setIswifi(iswifi).setUrl(url).setTargetPath(targetpath)
        BookDownloadDataMap.getInstance().addData(data)
        return data
    }

    //获取下载信息
    fun getDownloadData(bookcode: String) : BookDownloadData?{
        return BookDownloadDataMap.getInstance().getData(bookcode)
    }

    //获取状态
    fun getStatus(bookcode :String) : BookDownloadStatus {
        val bookdata = BookDownloadDataMap.getInstance().getData(bookcode)
        return bookdata?.uiStatus?: BookDownloadStatus.STOP
    }

    //设置状态
    fun setStatusUI(bookdata: BookDownloadData, status: BookDownloadStatus) {
        bookdata.setUIStatus(status)
    }

    //设置回调
    fun setDownloadListener(listener: BookDownloadCallback?): FileDownloadListener {
        return object : FileDownloadListener(){

            override fun pending(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                val bookcode : String = task?.getTag(BOOKCODE).toString()
                val bookdata = getDownloadData(bookcode)
                bookdata?.setProgress(0)?.speed = task?.speed?.toFloat()?:0f
                listener?.pending(bookdata,soFarBytes,totalBytes)
            }

            override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                var tot = 50000000
                if (totalBytes > 0 ) {
                    tot = totalBytes
                }
                val bookcode : String = task?.getTag(BOOKCODE).toString()
                val bookdata = getDownloadData(bookcode)
                var p = (soFarBytes * 1.0 /tot * 100).toInt()
                if (p >100 || p <0)
                    p = 8
                bookdata?.setProgress(p)?.speed = task?.speed?.toFloat()?:0f
                listener?.progress(bookdata,soFarBytes,totalBytes)
            }

            override fun completed(task: BaseDownloadTask?) {
                val bookcode: String = task?.getTag(BOOKCODE).toString()
                val bookdata = BookDownloadDataMap.getInstance().getData(bookcode)
                bookdata?.setProgress(100)?.setUIStatus(BookDownloadStatus.START)
                listener?.completed(bookdata)
                val zip = bookdata?.targetPath ?: ""
                val unzip = bookdata?.unzippath ?: ""
                val listener1 = bookdata?.listener
                unZip(bookcode, zip, unzip, listener1)
            }

            override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                val bookcode : String = task?.getTag(BOOKCODE).toString()
                BookDownloadDataMap.getInstance().getData(bookcode)?.setSpeed(0f)
                val bookdata = getDownloadData(bookcode)
                listener?.paused(bookdata,soFarBytes,totalBytes)
                DownloadLogcat.d("show",bookcode + " paused")
            }

            override fun error(task: BaseDownloadTask?, e: Throwable?) {
                val bookcode : String = task?.getTag(BOOKCODE).toString()
                val bookdata = BookDownloadDataMap.getInstance().getData(bookcode)
                bookdata?.setSpeed(0f)
                        ?.setProgress(0)
                        ?.setUIStatus(BookDownloadStatus.STOP)
                if (e is FileDownloadOutOfSpaceException){
                    bookdata?.setError(ErrorData().setError(true).setErrorStr("磁盘空间不足").setE(e))
                }
                else if(e is FileDownloadNetworkPolicyException){
                    bookdata?.setError(ErrorData().setError(true).setErrorStr("wifi断开").setE(e))
                }
                else if(e is PathConflictException){
                    bookdata?.setError(ErrorData().setError(true).setErrorStr("重复下载").setE(e))
                }
                else if(e is FileDownloadHttpException){
                    bookdata?.setError(ErrorData().setError(true).setErrorStr("未下载成功").setE(e))
                }
                else if(e is FileDownloadGiveUpRetryException){
                    bookdata?.setError(ErrorData().setError(true).setErrorStr("下载异常").setE(e))
                }
                else{
                    bookdata?.setError(ErrorData().setError(true).setErrorStr("下载错误").setE(e))
                }
                getCachedThreadPool()?.execute{
                    deleteFile(File(bookdata?.targetPath))
                    deleteFile(File(bookdata?.unzippath))
                }
                listener?.error(bookdata,e)
                DownloadLogcat.d("show",bookcode + " error "+e.toString())
            }

            override fun warn(task: BaseDownloadTask?) {
                val bookcode : String = task?.getTag(BOOKCODE).toString()
                val bookdata = getDownloadData(bookcode)
                listener?.warn(bookdata)
            }

        }
    }

    //删除下载的书籍
    fun deleteFile(file :File?) {
        try {
            if (file?.exists() ?: false) {
                if (file!!.isFile()) {
                    val ddl = file.delete()
                    if (!ddl) {
                        if (!file.delete())
                            DownloadLogcat.e("201803161742", file.getAbsolutePath() + " DeleteFile Fail");
                    }
                } else if (file.isDirectory()) {
                    val files = file.listFiles()
                    for (item in files) {
                        deleteFile(item)
                    }
                }
            }
        }catch (e : Exception){
            DownloadLogcat.e("201803201447",e.toString())
        }
    }

    //删除任务
    fun deleteTask(bookcode: String?){
        pauseDownload(BookDownloader.getDownloadData(bookcode?:""))
        BookDownloadDataMap.getInstance().removeData(bookcode)
    }

    //解压
    fun unZip(bookcode : String ,zippath: String, unzip: String, callback: BookDownloadCallback?) {
        val bookdata = getDownloadData(bookcode)
        bookdata?.setProgress(0)?.setUIStatus(BookDownloadStatus.UNZIP)
        callback?.onUnzipBegin(bookcode)
        getCachedThreadPool()?.execute {
            try {
                ZIPUtils.decompress(zippath, unzip,null) { progress ->
                    try{
                        if (progress < 0 || progress >100)
                            return@decompress
                        if (bookdata?.progress?:0 <= progress) {
                            bookdata?.setProgress(progress)?.setUIStatus(BookDownloadStatus.UNZIP)
                            callback?.onUnzipProgress(bookcode, progress)
                        }
                    }catch (e : Exception){
                        DownloadLogcat.e("201804190917",e.toString())
                    }
                }
            } catch (e: Exception) {
                DownloadLogcat.e("201801031619", e.toString())
                getCachedThreadPool()?.execute {
                    deleteFile(File(unzip))
                }
                bookdata?.setError(ErrorData().setError(true).setErrorStr("解压错误").setE(e))
                callback?.error(bookdata, Exception())
            }
            bookdata?.setProgress(100)?.setUIStatus(BookDownloadStatus.UNZIP)
            callback?.onUnzipEnd(bookcode)
            Thread.sleep(1000)
            bookdata?.setProgress(0)?.setUIStatus(BookDownloadStatus.STOP)
            callback?.onEnd(bookdata)
            getCachedThreadPool()?.execute {
                deleteFile(File(zippath))
                BookDownloadDataMap.getInstance().removeData(bookcode)
                callback?.onDestory(bookcode)
            }
        }
    }

}
