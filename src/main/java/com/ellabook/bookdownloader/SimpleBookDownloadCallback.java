package com.ellabook.bookdownloader;

/**
 * Created by mpokh on 2018/3/20.
 */

public class SimpleBookDownloadCallback implements BookDownloadCallback {


    @Override
    public void warn(BookDownloadData task) {

    }

    @Override
    public void completed(BookDownloadData task) {

    }

    @Override
    public void pending(BookDownloadData task, int soFarBytes, int totalBytes) {

    }

    @Override
    public void error(BookDownloadData task, Throwable throwable) {

    }

    @Override
    public void progress(BookDownloadData task, int soFarBytes, int totalBytes) {

    }

    @Override
    public void paused(BookDownloadData task, int soFarBytes, int totalBytes) {

    }

    @Override
    public void onUnzipBegin(String bookcode) {

    }

    @Override
    public void onUnzipProgress(String bookcode, int p) {

    }

    @Override
    public void onUnzipEnd(String bookcode) {

    }

    @Override
    public void onEnd(BookDownloadData task) {

    }

    @Override
    public void onDestory(String bookcode) {

    }

}
