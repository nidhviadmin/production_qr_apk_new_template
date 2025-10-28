package com.bipinexports.productionqrnew;

public class Fabric_Receipt_Data_Object {

    private String dcno;
    private String dcdate;
    private String fromvendor;
    private String joborderno;
    private String  styleno;
    private String  shipcode;
    private String  partname;
    private String  sentrolls;

    private int dcid;
    private String sentweight;
    private String status;
    private int index;

    public Fabric_Receipt_Data_Object(int did, String dno, String ddate, String swt, String jono, String fvendor, String sty, String ship, String part, String srol, String st, int indx){
        dcid = did;
        sentweight = swt;
        dcno = dno;
        dcdate = ddate;
        joborderno = jono;
        fromvendor = fvendor;
        styleno = sty;
        shipcode = ship;
        partname = part;
        sentrolls = srol;
        status = st;
        index = indx;
    }

    public int getDcid() {
        return dcid;
    }

    public String getSentweight() {
        return sentweight;
    }

    public String getDcno() {
        return dcno;
    }

    public String getDcdate() {
        return dcdate;
    }

    public String getFromvendor() {
        return fromvendor;
    }

    public String getJoborderno() {
        return joborderno;
    }

    public String getStyleno() {
        return styleno;
    }

    public String getShipcode() {
        return shipcode;
    }

    public String getPartname() {
        return partname;
    }

    public String getSentrolls() {
        return sentrolls;
    }

    public String getStatus() {
        return status;
    }

    public int getIndex() {
        return index;
    }

}
