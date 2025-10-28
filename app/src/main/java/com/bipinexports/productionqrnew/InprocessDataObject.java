package com.bipinexports.productionqrnew;

public class InprocessDataObject {
    private String weekyear;
    private String inprocessbundlecnt;
    private String inprocessbundleqty;
    private String inprocessprice;
    private int week;
    private int year;

    public InprocessDataObject(int wk, int yr, String wkykey, String abcnt, String abqty, String abprice){
        week = wk;
        year = yr;
        weekyear = wkykey;
        inprocessbundlecnt = abcnt;
        inprocessbundleqty = abqty;
        inprocessprice = abprice;
    }

    public int getWeek() {
        return week;
    }

    public int getYear() {
        return year;
    }

    public String getWeekyear() {
        return weekyear;
    }

    public String getInprocessbundlecnt() {
        return inprocessbundlecnt;
    }

    public String getInprocessbundleqty() {
        return inprocessbundleqty;
    }

    public String getInprocessprice() {
        return inprocessprice;
    }
}
