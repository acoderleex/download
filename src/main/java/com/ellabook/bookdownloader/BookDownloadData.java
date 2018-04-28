package com.ellabook.bookdownloader;

/**
 * Created by mpokh on 2018/3/19.
 */

public class BookDownloadData {
    //恢复也需要的 必须参数
    private String bookcode;//书籍id
    private String url;//下载url
    private String targetPath;//下载路径
    private boolean iswifi = true;//仅wifi
    private boolean isSyncCallback = true;//是否是设置了所有FileDownloadListener中的回调都直接在下载线程直接回调而不抛到ui线程

    //恢复可以不填 可选参数
    private String name;//下载的大名
    private Object tag;//标记
    private String unzippath;//解压路径
    private BookDownloadStatus UIStatus = BookDownloadStatus.STOP;//界面的状态
    private int progress = 0;//下载进度
    private float speed;//下载速度
    private SimpleBookDownloadCallback listener;//下载回调函数
    private ErrorData error;//下载的错误信息

    public static BookDownloadData newInstance(String bookcode){
        BookDownloadData data = null;
        data = BookDownloadDataMap.getInstance().getData(bookcode);
        if (data == null)
            data = new BookDownloadData().setBookcode(bookcode)
                    .setTag(null).setProgress(0).setSpeed(0).setError(null)
                    .setUIStatus(BookDownloadStatus.STOP);
        return data;
    }

    public BookDownloadData copy(BookDownloadData data){
        if (data != null) {
            bookcode = data.getBookcode();
            url = data.getUrl();
            targetPath = data.getTargetPath();
            iswifi = data.isIswifi();
            isSyncCallback = data.isSyncCallback();
            name = data.getName();
            tag = data.getTag();
            unzippath = data.getUnzippath();
            UIStatus = data.getUIStatus();
            progress = data.getProgress();
            speed = data.getSpeed();
            listener = data.getListener();
            error = data.getError();
        }else{
            return null;
        }
        return this;
    }

    public boolean reload(BookDownloadData older){
        if (older == null)
            return false;
        try {
            if (older.getBookcode().equals(bookcode)
                    && older.getUrl().equals(url)
                    && older.getTargetPath().equals(targetPath)) {
                if (older.getName() != null && !older.getName().isEmpty()){
                    name = older.getName();
                }
                if (older.getUnzippath() != null && !older.getUnzippath().isEmpty()){
                    unzippath = older.getUnzippath();
                }
                if (older.getProgress() > 0){
                    progress = older.getProgress();
                }
                if (older.getSpeed() > 0){
                    speed = older.getSpeed();
                }
                if (listener == null){
                    listener = older.getListener();
                }
                if (tag == null){
                    tag = older.getTag();
                }
                return true;
            }
        }catch (Exception e){
            DownloadLogcat.e("201803201331",e.toString());
        }
        return false;
    }

    public String getUrl() {
        return url;
    }

    public BookDownloadData setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getName() {
        return name;
    }

    public BookDownloadData setName(String name) {
        this.name = name;
        return this;
    }

    public String getTargetPath() {
        return targetPath;
    }

    public BookDownloadData setTargetPath(String targetPath) {
        this.targetPath = targetPath;
        return this;
    }

    public String getUnzippath() {
        return unzippath;
    }

    public BookDownloadData setUnzippath(String unzippath) {
        this.unzippath = unzippath;
        return this;
    }

    public Object getTag() {
        return tag;
    }

    public BookDownloadData setTag(Object tag) {
        this.tag = tag;
        return this;
    }

    public BookDownloadStatus getUIStatus() {
        return UIStatus;
    }

    public BookDownloadData setUIStatus(BookDownloadStatus UIStatus) {
        this.UIStatus = UIStatus;
        return this;
    }

    public int getProgress() {
        if (progress > 100 || progress <0)
            progress = 0;
        return progress;
    }

    public BookDownloadData setProgress(int progress) {
        this.progress = progress;
        return this;
    }

    public float getSpeed() {
        return speed;
    }

    public BookDownloadData setSpeed(float speed) {
        this.speed = speed;
        return this;
    }

    public SimpleBookDownloadCallback getListener() {
        return listener;
    }

    public BookDownloadData setListener(SimpleBookDownloadCallback listener) {
        this.listener = listener;
        return this;
    }

    public ErrorData getError() {
        return error;
    }

    public BookDownloadData setError(ErrorData error) {
        this.error = error;
        return this;
    }

    public boolean isIswifi() {
        return iswifi;
    }

    public BookDownloadData setIswifi(boolean iswifi) {
        this.iswifi = iswifi;
        return this;
    }

    public boolean isSyncCallback() {
        return isSyncCallback;
    }

    public BookDownloadData setSyncCallback(boolean syncCallback) {
        isSyncCallback = syncCallback;
        return this;
    }

    public String getBookcode() {
        return bookcode;
    }

    public BookDownloadData setBookcode(String bookcode) {
        this.bookcode = bookcode;
        return this;
    }

}
