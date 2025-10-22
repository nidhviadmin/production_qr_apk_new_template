package com.bipinexports.productionqr;

public class Datewise_Piece_Scan_List_Data_Object {

    private String job_ref;
    private String shipcode;
    private String sectionname;
    private String  partname;
    private String sizename;
    private String sizewiseqty;
    private String bundle_qty;
    private String scanned_pcno;
    private String bundlecnt;
    private int orderid;
    private int index;
    private int sizeid;
    private int sectionid;
    private int part_id;
    private  String diff_qty;


    public Datewise_Piece_Scan_List_Data_Object(int did, int indx, String refn, String secn, String shipc, String partn, String bundleq, String sizen, String sizqty, String pcno
    , int sid, int secid,int partid, String bcnt, String dif_qty){
        orderid = did;
        index = indx;

        sizeid = sid;
        sectionid = secid;
        part_id = partid;

        job_ref = refn;
        sectionname = secn;
        shipcode = shipc;
        partname = partn;
        bundle_qty = bundleq;
        sizename = sizen;
        sizewiseqty = sizqty;
        scanned_pcno = pcno;
        bundlecnt = bcnt;
        diff_qty = dif_qty;
    }

    public int getOrderid() {
        return orderid;
    }
    public int getIndex() {
        return index;
    }
    public int getSizeid() {
        return sizeid;
    }
    public int getSectionid() {
        return sectionid;
    }
    public int getPart_id() {
        return part_id;
    }

    public String getJob_ref() { return job_ref; }
    public String getShipcode() {
        return shipcode;
    }
    public String getSectionname() {
        return sectionname;
    }
    public String getPartname() {
        return partname;
    }
    public String getBundle_qty() {
        return bundle_qty;
    }
    public String getSizename() {
        return sizename;
    }
    public String getSizewiseqty() {
        return sizewiseqty;
    }
    public String getScanned_pcno() {
        return scanned_pcno;
    }
    public String getBundlecnt() {
        return bundlecnt;
    }

    public String getDiff_qty() {
        return diff_qty;
    }
}
