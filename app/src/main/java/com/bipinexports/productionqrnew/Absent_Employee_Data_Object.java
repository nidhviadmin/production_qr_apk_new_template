package com.bipinexports.productionqrnew;

public class Absent_Employee_Data_Object {

    private String empcode;
    private String empname;
    private String imgpath;
    private String hrmsrecid;
    private String  operationid;
    private String  hrmssecid;

    private String designation;
    private String  operation;
    private String  process;
    private String  section_name;
    private String  qrcontractorid;
    private String  currentstatus;
    private String  current_inoutimte;
    private String  noofconsecutiveabsentdays;
    private String  mobileno;
    private String  selecteddate;

    public Absent_Employee_Data_Object(String emp_code, String emp_name, String emp_imgpath, String hrms_recid, String operation_id, String hrms_secid, String hrms_des, String proc, String oper, String sec, String qrrec, String cur_stat, String innouttime, String noofabs, String sel_date, String mobil)
    {
        empcode = emp_code;
        empname = emp_name;
        hrmsrecid = hrms_recid;
        imgpath = emp_imgpath;
        operationid = operation_id;
        hrmssecid = hrms_secid;
        designation = hrms_des;
        operation = proc;
        process = oper;
        section_name= sec;
        qrcontractorid = qrrec;
        currentstatus = cur_stat;
        current_inoutimte = innouttime;
        noofconsecutiveabsentdays= noofabs;
        mobileno = mobil;
        selecteddate= sel_date;
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
        return hrmsrecid;
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

    public String getCurrentstatus() {
        return currentstatus;
    }

    public String getCurrent_inoutimte() {
        return current_inoutimte;
    }
    public String getNoofconsecutiveabsentdays() {
        return noofconsecutiveabsentdays;
    }

    public String getMobileno() {
        return mobileno;
    }
    public String getSelecteddate() {
        return selecteddate;
    }

}
