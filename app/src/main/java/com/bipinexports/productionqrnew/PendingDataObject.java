package com.bipinexports.productionqrnew;

public class PendingDataObject {
    private String weekyear;
    private String pendingbundlecnt;
    private String pendingbundleqty;
    private String pendingprice;
    private int week;
    private int year;

    public PendingDataObject(int wk, int yr, String wkykey, String abcnt, String abqty, String abprice){
        week = wk;
        year = yr;
        weekyear = wkykey;
        pendingbundlecnt = abcnt;
        pendingbundleqty = abqty;
        pendingprice = abprice;
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

    public String getPendingbundlecnt() {
        return pendingbundlecnt;
    }

    public String getPendingbundleqty() {
        return pendingbundleqty;
    }

    public String getPendingprice() {
        return pendingprice;
    }
}
