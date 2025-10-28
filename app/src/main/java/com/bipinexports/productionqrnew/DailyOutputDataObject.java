package com.bipinexports.productionqrnew;

public class DailyOutputDataObject {
    private String datewisebundlecnt;
    private String datewisebundleqty;
    private String datewiseprice;
    private String day;


    public DailyOutputDataObject(String d, String abcnt, String abqty, String abprice){


        day = d;
        datewisebundlecnt = abcnt;
        datewisebundleqty = abqty;
        datewiseprice = abprice;
    }

    public String getDay() {
        return day;
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
