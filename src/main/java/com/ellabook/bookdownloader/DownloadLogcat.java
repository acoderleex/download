package com.ellabook.bookdownloader;

import android.util.Log;

/**
 * Created by Administrator on 2018/2/3.
 */

class DownloadLogcat {
    public static int LOG_LEVEL = 6;
    private static int ERROR = 1;
    private static int WARN = 2;
    private static int INFO = 3;
    private static int DEBUG = 4;
    private static int VERBOS = 5;

    public static void e(String tag, String msg) {
        if (LOG_LEVEL > ERROR) Log.e(tag, msg);
    }

    public static void w(String tag, String msg) {
        if (LOG_LEVEL > WARN) Log.w(tag, msg);
    }

    public static void i(String tag, String msg) {
        if (LOG_LEVEL > INFO) Log.i(tag, msg);
    }

    public static void d(String tag, String msg) {
        if (LOG_LEVEL > DEBUG) Log.d(tag, msg);
    }

    public static void v(String tag, String msg) {
        if (LOG_LEVEL > VERBOS) Log.v(tag, msg);
    }
}