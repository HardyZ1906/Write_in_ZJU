package com.wzju.rabbitmq.entity;

import java.io.Serializable;

public class Produceentity implements Serializable {

    private String id;
    private String docname;// Document name
    private String usrname;// User name
    private String optype;
    private String opinput;
    private int excel_x;
    private int excel_y;
    private int doctype;

    public Produceentity() {
    }

    public Produceentity(String id, String docname, String usrname, String optype, String opinput, int doctype) {
        super();
        this.id = id;
        this.docname = docname;
        this.usrname = usrname;
        this.optype = optype;
        this.opinput = opinput;
        this.doctype = doctype;
    }

    public Produceentity(String id, String docname, String usrname, String opinput, int excel_x, int excel_y,
            int doctype) {
        super();
        this.id = id;
        this.docname = docname;
        this.usrname = usrname;
        this.opinput = opinput;
        this.doctype = doctype;
        this.excel_x = excel_x;
        this.excel_y = excel_y;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDocname() {
        return docname;
    }

    public void setDocname(String docname) {
        this.docname = docname;
    }

    public String getUsrname() {
        return usrname;
    }

    public void setUsrname(String usrname) {
        this.usrname = usrname;
    }

    public String getOptype() {
        return optype;
    }

    public void setOptype(String optype) {
        this.optype = optype;
    }

    public String getOpinput() {
        return opinput;
    }

    public void setOpinput(String opinput) {
        this.opinput = opinput;
    }

    public int getExcel_x() {
        return this.excel_x;
    }

    public void setExcel_x(int x) {
        this.excel_x = x;
    }

    public int getExcel_y() {
        return this.excel_y;
    }

    public void setExcel_y(int y) {
        this.excel_y = y;
    }

    public int getDoctype() {
        return this.doctype;
    }

    public void setDoctype(int doctype) {
        this.doctype = doctype;
    }

    public String toString(int j) {
        StringBuilder temp = new StringBuilder();
        temp.append("{doctype:");
        temp.append(this.doctype);
        temp.append(",");
        temp.append("id:");
        temp.append(this.id);
        temp.append(",");
        temp.append("docname:");
        temp.append(this.docname);
        temp.append(",");
        temp.append("usrname:");
        temp.append(this.usrname);
        temp.append(",");
        temp.append("optype:");
        temp.append(this.optype);
        temp.append(",");
        temp.append("opinput:");
        temp.append(this.opinput);
        temp.append(",");
        temp.append("excel_x:");
        temp.append(this.excel_x);
        temp.append(",");
        temp.append("excel_y:");
        temp.append(this.excel_y);
        temp.append("}");
        return new String(temp);
    }

    @Override
    public String toString() {
        StringBuilder temp = new StringBuilder();
        temp.append(this.doctype);
        temp.append(",");
        temp.append(this.id);
        temp.append(",");
        temp.append(this.docname);
        temp.append(",");
        temp.append(this.usrname);
        temp.append(",");
        temp.append(this.optype);
        temp.append(",");
        temp.append(this.opinput);
        temp.append(",");
        temp.append(this.excel_x);
        temp.append(",");
        temp.append(this.excel_y);
        return new String(temp);
    }

    public Produceentity(String ninput) {
        super();
        String[] args = ninput.split(",");
        this.doctype = Integer.parseInt(args[0]);
        this.id = args[1];
        this.docname = args[2];
        this.docname = args[3];
        this.usrname = args[4];
        this.opinput = args[5];
        this.excel_x = Integer.parseInt(args[6]);
        this.excel_y = Integer.parseInt(args[7]);
    }
}
