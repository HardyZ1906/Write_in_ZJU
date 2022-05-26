package com.wzju.util;

public class OTInsert extends OTOperation {
    String content;
    
    public OTInsert(String content) {
        super(OTOperation.INSERT);
        this.content = content;
    }

    public String toString() {
        return "INSERT: " + this.content;
    }
}
