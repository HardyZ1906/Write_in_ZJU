package com.wzju.util;

public class OTOperation {
    final static int RETAIN = 0, INSERT = 1, DELETE = 2;
    int type;

    public OTOperation(int type) {
        this.type = type;
    }

    public String toString() {
        switch (type) {
            case RETAIN:
                return ((OTRetain)this).toString();
            case INSERT:
                return ((OTInsert)this).toString();
            case DELETE:
                return ((OTDelete)this).toString();
            default:
                return null;
        }
    }
}
