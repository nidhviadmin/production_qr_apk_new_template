package com.bipinexports.productionqrnew;

public class Accessory_Receipt_Vendor_Data_Object {

    private String  vendorname;
    private String  count;
    private String  quantity;
    private int vendorid;
    private int indexd;

    public Accessory_Receipt_Vendor_Data_Object(int vid,  String vname, String cnt, String qty, int indx){

        vendorid = vid;

        vendorname = vname;
        count = cnt;
        quantity = qty;
        indexd = indx;
    }

    public int getVendorid() {
        return vendorid;
    }

    public String getVendorname() {
        return vendorname;
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
