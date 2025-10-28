package com.bipinexports.productionqrnew;

public class Overall_Datewise_Piece_Scan_Data_Object {

    private String job_ref;
    private String shipcode;
    private String sectionname;
    private String bundle_qty;
    private String scanned_pcno;
    private String diff_qty;

    private int orderid;
    private int index;
    private int sectionid;


    public Overall_Datewise_Piece_Scan_Data_Object(int indx, int or_id, int secid,  String refn,  String shipc, String secn, String bundleq, String pcno
            , String dif_cnt){
        orderid = or_id;
        index = indx;
        sectionid = secid;

        job_ref = refn;
        sectionname = secn;
        shipcode = shipc;
        bundle_qty = bundleq;
        scanned_pcno = pcno;
        diff_qty = dif_cnt;
    }

    public int getOrderid() {
        return orderid;
    }
    public int getIndex() {
        return index;
    }
    public int getSectionid() {
        return sectionid;
    }

    public String getJob_ref() { return job_ref; }
    public String getShipcode() {
        return shipcode;
    }
    public String getSectionname() {
        return sectionname;
    }
    public String getBundle_qty() {
        return bundle_qty;
    }
    public String getScanned_pcno() {
        return scanned_pcno;
    }
    public String getDiff_qty() {
        return diff_qty;
    }
}
