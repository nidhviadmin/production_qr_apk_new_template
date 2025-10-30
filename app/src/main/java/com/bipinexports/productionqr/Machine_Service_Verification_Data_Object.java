package com.bipinexports.productionqr;

public class Machine_Service_Verification_Data_Object {

    private String service_refno;
    private String service_date;
    private String service_reason;
    private String machine_no;
    private String  vendorname;
    private String  notes;

    private int mac_id;
    private int index_id;

    public Machine_Service_Verification_Data_Object(int id, String dservice_refno, String dservice_date, String dservice_reason, String dmachine_no, String vname, String dnotes, int indx){

        mac_id = id;

        service_refno = dservice_refno;
        service_date = dservice_date;
        machine_no = dmachine_no;
        service_reason = dservice_reason;
        vendorname = vname;
        notes = dnotes;

        index_id = indx;
    }

    public int getMac_id() {
        return mac_id;
    }

    public String getService_refno() {
        return service_refno;
    }

    public String getService_date() {
        return service_date;
    }

    public String getMachine_no() {
        return machine_no;
    }

    public String getService_reason() {
        return service_reason;
    }


    public String getVendorname() {
        return vendorname;
    }

    public String getNotes() {
        return notes;
    }

    public int getIndex() {
        return index_id;
    }

}
