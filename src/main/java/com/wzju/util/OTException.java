package com.wzju.util;

public class OTException extends Exception {
    public final static int
        FILE_NOT_FOUND = 123,
        INCORRECT_OPERATION = 233,
        INCORRECT_SIZE = 666,
        IO_FAILURE = 999;

    public int code;
    public String msg;

    public OTException(int code, String msg) {
        this.code = code;
        this.msg  = msg;
    }

    @Override
    public String getMessage() {
        return "Error " + this.code + ": " + this.msg;
    }

    public void printMessage() {
        System.out.println(this.getMessage());
    }
}
