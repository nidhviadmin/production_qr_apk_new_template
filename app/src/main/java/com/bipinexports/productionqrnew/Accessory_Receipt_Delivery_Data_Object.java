package com.bipinexports.productionqrnew;

public class Accessory_Receipt_Delivery_Data_Object {

    private String dcno;
    private String dcdate;
    private String pono;
    private String podate;
    private String  vendorcode;
    private String  vendorname;
    private String  type;
    private String  quantity;

    private int dcid;
    private int poid;
    private int indexd;

    public Accessory_Receipt_Delivery_Data_Object(int did, String dno, String ddate, int pid, String pno, String pdate, String vcode, String vname, String ty, String qty, int indx){
        dcid = did;
        poid = pid;
        dcno = dno;
        dcdate = ddate;
        podate = pdate;
        pono = pno;
        vendorcode = vcode;
        vendorname = vname;
        type = ty;
        quantity = qty;
        indexd = indx;
    }

    public int getDcid() {
        return dcid;
    }

    public int getPoid() {
        return poid;
    }

    public String getDcno() {
        return dcno;
    }

    public String getDcdate() {
        return dcdate;
    }

    public String getPono() {
        return pono;
    }

    public String getPodate() {
        return podate;
    }

    public String getVendorcode() {
        return vendorcode;
    }

    public String getVendorname() {
        return vendorname;
    }

    public String getType() {
        return type;
    }

    public String getQuantity() {
        return quantity;
    }

    public int getIndex() {
        return indexd;
    }

}
