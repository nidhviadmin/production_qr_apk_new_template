package com.bipinexports.productionqrnew;

public class CompletedDataObject {
    private String weekyear;
    private String completedbundlecnt;
    private String completedbundleqty;
    private String icompletedprice;
    private int week;
    private int year;

    public CompletedDataObject(int wk, int yr, String wkykey, String abcnt, String abqty, String abprice){
        week = wk;
        year = yr;
        weekyear = wkykey;
        completedbundlecnt = abcnt;
        completedbundleqty = abqty;
        icompletedprice = abprice;
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

    public String getCompletedbundlecnt() {
        return completedbundlecnt;
    }

    public String getCompletedbundleqty() {
        return completedbundleqty;
    }

    public String getCompletedprice() {
        return icompletedprice;
    }
}
