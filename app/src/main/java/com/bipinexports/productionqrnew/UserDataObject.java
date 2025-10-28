package com.bipinexports.productionqrnew;

public class UserDataObject {
    private String username;
    private String new_code;
    private String processorid;
    private String section_name;
    private String unitid;
    private String isqc;
    private String id;
    private String index;
    private String accessid;
    private String status;
    private String message;

    public UserDataObject(String idx, String d, String uname, String ncode, String procesid, String sec, String unt, String qc, String acc_id, String stat, String mes){

        index = idx;
        processorid = procesid;
        id = d;
        username = uname;
        new_code = ncode;
        section_name = sec;
        unitid = unt;
        isqc = qc;
        accessid = acc_id;
        status = stat;
        message = mes;
    }

    public String getIndex() {
        return index;
    }

    public String getProcessorid() {
        return processorid;
    }
    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }
    public String getNew_code() {
        return new_code;
    }

    public String getSection_name() {
        return section_name;
    }

    public String getUnitid() {
        return unitid;
    }

    public String getIsqc() {
        return isqc;
    }
    public String getAccessid() {
        return accessid;
    }
    public String getStatus() {
        return status;
    }
    public String getMessage() {
        return message;
    }

}
