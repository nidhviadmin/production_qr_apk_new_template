package com.bipinexports.productionqr;


import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIClient {
    static Retrofit retrofit = null;
    public static final String Base_URL = "https://qr.nidhvitec.com/qrcode/";
    public static final String QR_URL = "https://qr.nidhvitec.com/qrcode/qrapkapi/";
    public static final String JOB_SUMMARY_URL = "https://qr.nidhvitec.com/api/jobsummaryapkapi/";
    public static final String Accessory_Receipts_URL = "https://qr.nidhvitec.com/api/accessoryreceiptsapi/";
    public static final String A_L_APPEND_URL = "https://edm.bipinexports.co.in/lna/apkapi/machine_qr_api/";
    public static final String A_L_Sharptool_URL = "https://edm.bipinexports.co.in/lna/apkapi/sharptools_apk_api/";
    public static final String Piecewise_QR_URL = "https://qr.nidhvitec.com/api/piecewise_qrapkapi/";
    public static final String HRMS_IMG_URL = "https://edm.bipinexports.co.in/hrms/";
    public static final String SpaceIamgepath_URL = "https://qr.nidhvitec.com/api/spacesapi/";

    public static final String Rollwise_URL = "https://ne.nidhvitec.com/api/fabricproductionsapi/";
    public static final String Fabric_Receipts_URL = "https://ne.nidhvitec.com/api/fabricreceiptsapi/";
    public static final String Employee_Budle_Mapp_URL = "https://qr.nidhvitec.com/api/employee_bundle_mapping_api/";

//    public static final String Base_URL = "http://192.168.0.219/gci3qrscan/qrcode/";
//    public static final String QR_URL = "http://192.168.0.219/gci3qrscan/qrcode/qrapkapi/";
//    public static final String JOB_SUMMARY_URL = "http://192.168.0.219/gci3qrscan/api/jobsummaryapkapi/";
//    public static final String Accessory_Receipts_URL = "http://192.168.0.219/gci3qrscan/api/accessoryreceiptsapi/";
//    public static final String A_L_APPEND_URL = "http://192.168.0.219/ci3assetsandlegals/apkapi/machine_qr_api/";
//    public static final String A_L_Sharptool_URL = "http://192.168.0.219/ci3assetsandlegals/apkapi/sharptools_apk_api/";
//    public static final String Piecewise_QR_URL = "http://192.168.0.219/gci3qrscan/api/piecewise_qrapkapi/";
//    public static String HRMS_IMG_URL = "http://192.168.0.219/gci3hrms/";
//    public static final String SpaceIamgepath_URL = "http://192.168.0.219/gci3qrscan/api/spacesapi/";
//    public static final String Employee_Budle_Mapp_URL = "http://192.168.0.219/gci3qrscan/api/employee_bundle_mapping_api/";




//    public static final String QR_URL = "http://192.168.0.164/gci3qrscan/qrcode/qrapkapi/";
//    public static final String BASE_URL = "http://192.168.0.164/gci3qrscan/qrcode/qrapkapi/";
//    public static final String JOB_SUMMARY_URL = "http://192.168.0.164/gci3qrscan/api/jobsummaryapkapi/";
//    public static final String Accessory_Receipts_URL = "http://192.168.0.164/gci3qrscan/api/accessoryreceiptsapi/";
//    public static final String A_L_APPEND_URL = "http://192.168.0.164/ci3assetsandlegals/apkapi/machine_qr_api/";
//    public static final String A_L_Sharptool_URL = "http://192.168.0.164/ci3assetsandlegals/apkapi/sharptools_apk_api/";
//    public static final String Piecewise_QR_URL = "http://192.168.0.164/gci3qrscan/api/piecewise_qrapkapi/";
//    public static String HRMS_IMG_URL = "http://192.168.0.164/gci3hrms/";
//    public static final String SpaceIamgepath_URL = "http://192.168.0.164/gci3qrscan/api/spacesapi/";
//    public static final String Employee_Budle_Mapp_URL = "http://192.168.0.164/gci3qrscan/api/employee_bundle_mapping_api/";
//    public static final String Rollwise_URL = "http://192.168.0.164/gci3qrscan/api/fabricproductionsapi/";
//    public static final String Fabric_Receipts_URL = "http://192.168.0.164/gci3qrscan/api/fabricreceiptsapi/";

    public static UserService getInterface() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();
        retrofit = new Retrofit.Builder()
                .baseUrl(QR_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        return retrofit.create(UserService.class);
    }
}
