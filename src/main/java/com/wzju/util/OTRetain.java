package com.wzju.util;

public class OTRetain extends OTOperation {
    int length;
    
    public OTRetain(int length) {
        super(OTOperation.RETAIN);
        this.length = length;
    }

    public String toString() {
        return "RETAIN: " + this.length;
    }
}
