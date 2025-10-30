package com.bipinexports.productionqr;

public class In_Employee_Bundle_Data_Object {

    private String empcode;
    private String empname;
    private String imgpath;
    private String rec_code;
    private String  operationid;
    private String  hrmssecid;

    private String designation;
    private String  operation;
    private String  process;
    private String  section_name;

    private String  qrcontractorid;
    private String  mapped_cnt;

    public In_Employee_Bundle_Data_Object(String emp_code, String emp_name, String emp_imgpath, String hrms_recid, String operation_id, String hrms_secid, String hrms_des, String proc, String oper, String sec, String qrrec, String mappednt)
    {
        empcode = emp_code;
        empname = emp_name;
        rec_code = hrms_recid;
        imgpath = emp_imgpath;
        operationid = operation_id;
        hrmssecid = hrms_secid;
        designation = hrms_des;
        operation = proc;
        process = oper;
        section_name= sec;
        qrcontractorid = qrrec;
        mapped_cnt = mappednt;
    }

    public String getEmpcode() {
        return empcode;
    }

    public String getEmpname() {
        return empname;
    }

    public String getImgpath() {
        return imgpath;
    }

    public String getHrmsrecid() {
        return rec_code;
    }

    public String getOperationid() {
        return operationid;
    }

    public String getHrmssecid() {
        return hrmssecid;
    }

    public String getDesignation() {
        return designation;
    }

    public String getOperation() {
        return operation;
    }

    public String getProcess() {
        return process;
    }

    public String getSection_name() {
        return section_name;
    }

    public String getQrcontractorid() {
        return qrcontractorid;
    }

    public String getmapped_cnt() {
        return mapped_cnt;
    }

}
