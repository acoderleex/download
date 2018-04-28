package com.ellabook.bookdownloader;

/**
 * Created by mpokh on 2018/3/19.
 */

public class ErrorData {
    private Object e;//异常类
    private boolean isError;//是否有错误
    private String errorStr;//错误文字

    public Object getE() {
        return e;
    }

    public ErrorData setE(Object e) {
        this.e = e;
        return this;
    }

    public boolean isError() {
        return isError;
    }

    public ErrorData setError(boolean error) {
        isError = error;
        return this;
    }

    public String getErrorStr() {
        return errorStr;
    }

    public ErrorData setErrorStr(String errorStr) {
        this.errorStr = errorStr;
        return this;
    }
}
