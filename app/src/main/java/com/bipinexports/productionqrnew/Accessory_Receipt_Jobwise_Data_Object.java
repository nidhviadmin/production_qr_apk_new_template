package com.bipinexports.productionqrnew;

public class Accessory_Receipt_Jobwise_Data_Object {

    private String  joborderno;
    private String  stylename;
    private String  count;
    private String  quantity;
    private int orderid;
    private int indexd;

    public Accessory_Receipt_Jobwise_Data_Object(int oid, String jno, String sname, String cnt, String qty, int indx){

        orderid = oid;
        joborderno = jno;
        stylename = sname;
        count = cnt;
        quantity = qty;
        indexd = indx;
    }

    public int getOrderid() {
        return orderid;
    }

    public String getJoborderno() {
        return joborderno;
    }

    public String getStylename() {
        return stylename;
    }

    public String getCount() {
        return count;
    }

    public String getQuantity() {
        return quantity;
    }

    public int getIndex() {
        return indexd;
    }

}
