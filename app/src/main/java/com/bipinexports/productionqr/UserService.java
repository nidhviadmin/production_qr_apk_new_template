package com.bipinexports.productionqr;

import com.google.gson.JsonObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserService {

    @POST(APIClient.QR_URL + "login")
    Call<JsonObject> login(@Body JsonObject object);

    @POST(APIClient.Accessory_Receipts_URL + "pendingdeliverycount")
    Call<JsonObject> pendingdeliverycount(@Body JsonObject object);

    @POST(APIClient.Accessory_Receipts_URL + "pendingdeliveries")
    Call<JsonObject> fetch_accessory_receipt_Details(@Body JsonObject object);

    @POST(APIClient.A_L_APPEND_URL + "machine_service_verification_count") 
    Call<JsonObject> machine_service_verification_count(@Body JsonObject object);

    @POST(APIClient.A_L_APPEND_URL + "machine_service_verification")
    Call<JsonObject> machine_service_verification(@Body JsonObject object);
    
    @POST(APIClient.A_L_APPEND_URL + "get_machine_details")
    Call<JsonObject> get_machine_details(@Body JsonObject object);

    @POST(APIClient.A_L_APPEND_URL + "update_machine_details")
    Call<JsonObject> update_machine_details(@Body JsonObject object);

    @POST(APIClient.A_L_APPEND_URL + "machine_service_details")
    Call<JsonObject> machine_service_details(@Body JsonObject object);


    @POST(APIClient.A_L_APPEND_URL + "update_multiple_machine_details")
    Call<JsonObject> update_multiple_machine_details(@Body JsonObject object);

    @POST(APIClient.QR_URL + "getqrdata")
    Call<JsonObject> getqrdata(@Body JsonObject object);

    @POST(APIClient.QR_URL + "updatescanstatus")
    Call<JsonObject> updatescanstatus(@Body JsonObject object);

    @POST(APIClient.QR_URL + "getqcqrdata")
    Call<JsonObject> getqcqrdata(@Body JsonObject object);

    @POST(APIClient.QR_URL + "updateqcscanstatus")
    Call<JsonObject> updateqcscanstatus(@Body JsonObject object);

    ///////////

    @POST(APIClient.QR_URL + "getqrdata_queue")
    Call<JsonObject> getqrdata_queue(@Body JsonObject object);

    @POST(APIClient.QR_URL + "updatescanstatus_queue")
    Call<JsonObject> updatescanstatus_queue(@Body JsonObject object);

    ////////

    @POST(APIClient.Accessory_Receipts_URL + "pendingdeliveries")
    Call<JsonObject> pendingdeliveries(@Body JsonObject object);

    @POST(APIClient.QR_URL + "changepassword")
    Call<JsonObject> changepassword(@Body JsonObject object);

    @POST(APIClient.JOB_SUMMARY_URL + "fetchdailyrpoductionoutputdetails")
    Call<JsonObject> fetchdailyrpoductionoutputdetails(@Body JsonObject object);

    @POST(APIClient.JOB_SUMMARY_URL + "fetchdatewiseoutputdetails")
    Call<JsonObject> fetchdatewiseoutputdetails(@Body JsonObject object);

    @POST(APIClient.Accessory_Receipts_URL + "deliverydetails")
    Call<JsonObject> deliverydetails(@Body JsonObject object);

    @POST(APIClient.Accessory_Receipts_URL + "receivedelivery")
    Call<JsonObject> receivedelivery(@Body JsonObject object);

    @POST(APIClient.A_L_APPEND_URL + "machine_service_verification_details")
    Call<JsonObject> machine_service_verification_details(@Body JsonObject object);

    @POST(APIClient.A_L_APPEND_URL + "reject_mac_service_verification")
    Call<JsonObject> reject_mac_service_verification(@Body JsonObject object);

    @POST(APIClient.A_L_APPEND_URL + "approve_mac_service_verification")
    Call<JsonObject> approve_mac_service_verification(@Body JsonObject object);

    @POST(APIClient.JOB_SUMMARY_URL + "fetchproductionoutputsummary")
    Call<JsonObject> fetchproductionoutputsummary(@Body JsonObject object);

    @POST(APIClient.JOB_SUMMARY_URL + "getjobsummary")
    Call<JsonObject> getjobsummary(@Body JsonObject object);

    @POST(APIClient.JOB_SUMMARY_URL + "fetchassignjobdetails")
    Call<JsonObject> fetchassignjobdetails(@Body JsonObject object);

    @POST(APIClient.JOB_SUMMARY_URL + "fetchpendingjobdetails")
    Call<JsonObject> fetchpendingjobdetails(@Body JsonObject object);

    @POST(APIClient.JOB_SUMMARY_URL + "fetchinprocessjobdetails")
    Call<JsonObject> fetchinprocessjobdetails(@Body JsonObject object);

    @POST(APIClient.JOB_SUMMARY_URL + "fetchcompletedjobdetails")
    Call<JsonObject> fetchcompletedjobdetails(@Body JsonObject object);


    @POST(APIClient.QR_URL + "fetch_version_details")
    Call<JsonObject> fetch_version_details(@Body JsonObject object);


    @POST(APIClient.A_L_APPEND_URL + "get_scan_machine")
    Call<JsonObject>  get_scan_machine(@Body JsonObject object);

    @POST(APIClient.A_L_Sharptool_URL + "get_scan_sharptools")
    Call<JsonObject>  get_scan_sharptools(@Body JsonObject object);


    //////////////////////////// Piecewise

    @POST(APIClient.Piecewise_QR_URL + "get_piecewise_qrdata")
    Call<JsonObject> get_piecewise_qrdata(@Body JsonObject object);

    @POST(APIClient.Piecewise_QR_URL + "piecewise_updatescanstatus")
    Call<JsonObject> piecewise_updatescanstatus(@Body JsonObject object);

    @POST(APIClient.Piecewise_QR_URL + "piecewise_scan_and_update")
    Call<JsonObject> piecewise_scan_and_update(@Body JsonObject object);

    @POST(APIClient.Piecewise_QR_URL + "fetch_datewise_piece_scan_details")
    Call<JsonObject> fetch_datewise_piece_scan_details(@Body JsonObject object);

    @POST(APIClient.Piecewise_QR_URL + "fetch_datewise_bundle_piece_scan_details")
    Call<JsonObject> fetch_datewise_bundle_piece_scan_details(@Body JsonObject object);

    @POST(APIClient.Piecewise_QR_URL + "fetch_piece_scan_details")
    Call<JsonObject> fetch_piece_scan_details(@Body JsonObject object);

    @POST(APIClient.Piecewise_QR_URL + "fetch_overall_scanned_details")
    Call<JsonObject> fetch_overall_scanned_details(@Body JsonObject object);


    @POST(APIClient.QR_URL + "fetch_employee_details")
    Call<JsonObject> fetch_employee_details(@Body JsonObject object);

    @POST(APIClient.QR_URL + "get_style_operation_data")
    Call<JsonObject> get_style_operation_data(@Body JsonObject object);

    @POST(APIClient.QR_URL + "update_emp_style_operation_mapping")
    Call<JsonObject> update_emp_style_operation_mapping(@Body JsonObject object);

    @POST(APIClient.QR_URL + "get_qrdata")
    Call<JsonObject> get_qrdata(@Body JsonObject object);

    @POST(APIClient.QR_URL + "employee_bundle_mapping")
    Call<JsonObject>  update_bundle_data(@Body JsonObject object);


    // multiple bundle QR Scan
    @POST(APIClient.QR_URL + "get_new_qr_data")
    Call<JsonObject> get_new_qr_data(@Body JsonObject object);

    @POST(APIClient.QR_URL + "update_qr_scan_queue_bundwisewise")
    Call<JsonObject> update_qr_scan_queue_bundwisewise(@Body JsonObject object);

    @POST(APIClient.Accessory_Receipts_URL + "pendingdeliveries_new")
    Call<JsonObject> pendingdeliveries_new(@Body JsonObject object);

    @POST(APIClient.QR_URL + "get_laywise_qr_data")
    Call<JsonObject> get_laywise_qr_data(@Body JsonObject object);

    @POST(APIClient.QR_URL + "get_in_out_emp_count")
    Call<JsonObject> get_in_out_count(@Body JsonObject object);


    @POST(APIClient.QR_URL + "fetch_employee_in_details")
    Call<JsonObject> fetch_employee_in_details(@Body JsonObject object);

    @POST(APIClient.QR_URL + "fetch_employee_out_details")
    Call<JsonObject> fetch_employee_out_details(@Body JsonObject object);

    @POST(APIClient.QR_URL + "fetch_employee_absent_details")
    Call<JsonObject> fetch_employee_absent_details(@Body JsonObject object);

    @POST(APIClient.Employee_Budle_Mapp_URL + "get_bundle_qr_mapped_data")
    Call<JsonObject> get_bundle_qr_mapped_data(@Body JsonObject object);

    // Rollwise  Details

//    @POST(APIClient.Rollwise_URL + "getjobdetails")
    Call<JsonObject> getjobdetails(@Body JsonObject object);

    @POST(APIClient.Rollwise_URL + "updateproductionrolls")
    Call<JsonObject>  updateproductionrolls(@Body JsonObject object);

    @POST(APIClient.Fabric_Receipts_URL + "pendingdeliverycount")
    Call<JsonObject> pending_deliverycount(@Body JsonObject object);

    @POST(APIClient.Fabric_Receipts_URL + "pendingdeliveries")
    Call<JsonObject> fabric_pendingdeliveries(@Body JsonObject object);

    @POST(APIClient.Fabric_Receipts_URL + "deliverydetails")
    Call<JsonObject> fabric_deliverydetails(@Body JsonObject object);

    @POST(APIClient.Fabric_Receipts_URL + "acceptdelivery")
    Call<JsonObject> fabric_acceptdelivery(@Body JsonObject object);

    @POST(APIClient.Fabric_Receipts_URL + "rejectdelivery")
    Call<JsonObject> fabric_rejectdelivery(@Body JsonObject object);

    //  Inward Roll Details
    @POST(APIClient.Rollwise_URL + "getinwardrolldetails")
    Call<JsonObject> getinwardrolldetails(@Body JsonObject object);


    @POST(APIClient.Rollwise_URL + "updaterelaxedroll")
    Call<JsonObject> updaterelaxedroll(@Body JsonObject object);

    //  Inward Roll Details
    @POST(APIClient.Rollwise_URL + "getproductionrolldetails")
    Call<JsonObject> getproductionrolldetails(@Body JsonObject object);


    @POST(APIClient.Rollwise_URL + "updateproductionroll")
    Call<JsonObject> updateproductionroll(@Body JsonObject object);

    // fetch Image Details //

    @POST(APIClient.QR_URL + "fetch_mainimage_details")
    Call<JsonObject> fetch_mainimage_details(@Body JsonObject object);

    @POST(APIClient.QR_URL + "fetch_slideimage_details")
    Call<JsonObject> fetch_slideimage_details(@Body JsonObject object);

    // Get User Details //
    @POST(APIClient.QR_URL + "get_user_data")
    Call<JsonObject> get_user_data(@Body JsonObject object);

    // save Device Details //
    @POST(APIClient.QR_URL + "update_device_details")
    Call<JsonObject> update_device_details(@Body JsonObject object);

    // save Device Details //
    @POST(APIClient.QR_URL + "update_user_lastin")
    Call<JsonObject> update_user_lastin(@Body JsonObject object);

    @POST(APIClient.API_URL + "UpdateDeviceToken")
    Call<ResponseBody> updateDeviceToken(@Body JsonObject object);



}

