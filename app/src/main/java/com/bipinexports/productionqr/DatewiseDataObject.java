package com.bipinexports.productionqr;

public class DatewiseDataObject {
    private String datewisebundlecnt;
    private String datewisebundleqty;
    private String datewiseprice;
    private String day;
    private String date;


    public DatewiseDataObject(String d,  String dt,String abcnt, String abqty, String abprice){


        day = d;
        date = dt;
        datewisebundlecnt = abcnt;
        datewisebundleqty = abqty;
        datewiseprice = abprice;
    }

    public String getDay() {
        return day;
    }

    public String getDate() {
        return date;
    }

    public String getDatewisebundlecnt() {
        return datewisebundlecnt;
    }

    public String getDatewisebundleqty() {
        return datewisebundleqty;
    }

    public String getDatewiseprice() {
        return datewiseprice;
    }
}
