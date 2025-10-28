package com.bipinexports.productionqrnew;

public class AssignedDataObject {
    private String weekyear;
    private String assignedbundlecnt;
    private String assignedbundleqty;
    private String assignedprice;
    private int week;
    private int year;

    public AssignedDataObject(int wk, int yr, String wkykey, String abcnt, String abqty, String abprice){
        week = wk;
        year = yr;
        weekyear = wkykey;
        assignedbundlecnt = abcnt;
        assignedbundleqty = abqty;
        assignedprice = abprice;
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

    public String getAssignedbundlecnt() {
        return assignedbundlecnt;
    }

    public String getAssignedbundleqty() {
        return assignedbundleqty;
    }

    public String getAssignedprice() {
        return assignedprice;
    }
}
