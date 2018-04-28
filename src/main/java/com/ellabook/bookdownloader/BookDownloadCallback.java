package com.ellabook.bookdownloader;

import com.ellabook.bookdownloader.BookDownloadData;

/**
 * Created by mpokh on 2018/3/20.
 */

 public interface BookDownloadCallback {
    void warn(BookDownloadData task);//下载重复
    void completed(BookDownloadData task);//下载完成
    void pending(BookDownloadData task,int soFarBytes,int totalBytes);//链接
    void error(BookDownloadData task,Throwable throwable);//异常
    void progress(BookDownloadData task,int soFarBytes,int totalBytes);//下载中
    void paused(BookDownloadData task,int soFarBytes,int totalBytes);//暂停
    void onUnzipBegin(String bookcode);//解压前
    void onUnzipProgress(String bookcode, int p);//解压中
    void onUnzipEnd(String bookcode);//解压完成
    void onEnd(BookDownloadData task);//任务完成未移除的回调
    void onDestory(String bookcode);//任务完成从队列移除后的回调
}
