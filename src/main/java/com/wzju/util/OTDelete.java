package com.wzju.util;

public class OTDelete extends OTOperation {
    int length;
    
    public OTDelete(int length) {
        super(OTOperation.DELETE);
        this.length = length;
    }

    public String toString() {
        return "DELETE: " + this.length;
    }
}
