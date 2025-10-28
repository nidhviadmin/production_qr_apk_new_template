package com.bipinexports.productionqrnew;

public class Datewise_Bundle_Piece_Data_Object {

    private String job_ref;
    private String shipcode;
    private String sectionname;
    private String  partname;
    private String sizename;
    private String sizewiseqty;
    private String bundleno;
    private String bundle_qty;
    private String scanned_pcno;
    private int orderid;
    private int index;
    private int sizeid;
    private int sectionid;
    private int part_id;

    private String diff_qty;


    public Datewise_Bundle_Piece_Data_Object(int did, int indx, String refn, String secn, String shipc, String partn, String bundleq, String sizen, String sizqty, String bundlen, String pcno
    , int sid, int secid, int partid , String  difqt){
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
        bundleno = bundlen;
        scanned_pcno = pcno;
        diff_qty = difqt;

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
    public String getBundleno() {
        return bundleno;
    }
    public String getScanned_pcno() {
        return scanned_pcno;
    }
    public String getDiff_qty() {
        return diff_qty;
    }

}
