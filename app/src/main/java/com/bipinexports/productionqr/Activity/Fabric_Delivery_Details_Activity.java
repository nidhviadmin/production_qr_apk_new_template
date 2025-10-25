package com.bipinexports.productionqr.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.bipinexports.productionqr.APIClient;
import com.bipinexports.productionqr.GetResult;
import com.bipinexports.productionqr.ModelClass;
import com.bipinexports.productionqr.R;
import com.bipinexports.productionqr.SessionManagement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.bipinexports.productionqr.APIClient.SpaceIamgepath_URL;
import static com.bipinexports.productionqr.Common.QR_URL;
import static java.lang.Boolean.TRUE;
import static java.lang.StrictMath.round;

public class Fabric_Delivery_Details_Activity extends BaseActivity implements View.OnClickListener, GetResult.MyListener {

    String Id, User;

    SessionManagement session;
    GridView gridView;
    ProgressBar progress;
    String processorid, dcid, joborderno, styleno, partname,vendorname, dcno, dcdate,type, sentrolls, shipcode;
    String userid, delivery, pendingcount;

    ImageView imageView;
    Button AddProg, AddReject, Go_Back;
    LinearLayout Fabric_Spec;

    TextView text_Job_Ref, text_Goods_from, text_DC_No, text_DC_Date, text_Style;
    TextView txtUser;
    TextView   text_Fabric_Type, text_Reference, text_Yarn_Details, text_Color, text_Gauge, text_Rolls_and_weight;

    int rollslength = 0;
    int FIRST_HALF_COUNT = 0;

    TextView txtPercentage, txtTagImgDesc;
    String imageFilePath;
    private static final int REQUEST_CAPTURE_IMAGE = 100;

    public static CustPrograssbar_new custPrograssbar_new;

    ImageView recordicon, playicon;
    String AudioSavePathInDevice = null;
    MediaRecorder mediaRecorder ;
    public static final int RequestPermissionCode = 1;
    MediaPlayer mediaPlayer ;
    Uri audioURI;
    Boolean recording = false;
    Boolean recordplay = false;
    TextView song_seekbartext;
    Boolean playaudio = false;
    Chronometer mycm;
    int elapsed = 0;
    Boolean playstop = false;

    private SeekBar seekBar;
    String audiofilepath;
    Boolean audiorecourd =false;
    String fabric_dc_imgpath1, fabric_dc_imgpath2, fabric_dc_imgpath3, fabric_dc_imgpath4;

    private ArrayList<Toast> msjsToast = new ArrayList<Toast>();
    String endtime;

    EditText Notes;

    ImageView[] letters = new ImageView[10];
    private ImageView imgTagPic1, imgTagPic2, imgTagPic3, imgTagPic4;

    int current_id = 1;
    List additionalimages = new ArrayList<String>();
    Boolean image_tak1 = false;
    Boolean image_tak2 = false;
    Boolean image_tak3 = false;

    JSONArray additional_images = new JSONArray();


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_fabric_delivery_details);
        setContentView(R.layout.activity_base);
        setupDrawer();

        View content = getLayoutInflater().inflate(
                R.layout.activity_fabric_delivery_details,
                findViewById(R.id.content_frame),
                true
        );

        if ( Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission( getApplicationContext(), Manifest.permission.CAMERA ) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 212);
        }

        if ( Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission( getApplicationContext(), WRITE_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE}, 212);
        }

        if ( Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission( getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 212);
        }

        imageView = (ImageView) content.findViewById(R.id.imgd);
        gridView = (GridView) this.findViewById(R.id.grid);
        progress = (ProgressBar) content.findViewById(R.id.progress);
        progress.setVisibility(View.GONE);

        custPrograssbar_new = new CustPrograssbar_new();
        Fabric_Delivery_Details_Activity.custPrograssbar_new.prograssCreate(this);



        delivery = getIntent().getStringExtra("delivery");
        pendingcount = getIntent().getStringExtra("pendingcount");

        txtUser = (TextView) content.findViewById(R.id.txtUser);

        session = new SessionManagement(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        String name = user.get(SessionManagement.KEY_USER);
        this.Id = user.get(SessionManagement.KEY_PROCESSOR_ID);
        processorid = user.get(SessionManagement.KEY_PROCESSOR_ID);
        userid = user.get(SessionManagement.KEY_USER_ID);
        this.User = user.get(SessionManagement.KEY_USER);

        processorid = getIntent().getStringExtra("processorid");
        dcid = getIntent().getStringExtra("dcid");
        dcno = getIntent().getStringExtra("dcno");
        dcdate = getIntent().getStringExtra("dcdate");

        vendorname = getIntent().getStringExtra("vendorname");
        joborderno = getIntent().getStringExtra("joborderno");
        shipcode = getIntent().getStringExtra("shipcode");
        styleno = getIntent().getStringExtra("styleno");
        partname = getIntent().getStringExtra("partname");
        sentrolls = getIntent().getStringExtra("sentrolls");
        type = getIntent().getStringExtra("status");

        imageView.setOnClickListener(this);

        Fabric_Spec = content.findViewById(R.id.Fabric_Spec);

        text_DC_No = content.findViewById(R.id.text_DC_No);
        text_DC_Date = content.findViewById(R.id.text_DC_Date);
        text_Job_Ref = content.findViewById(R.id.text_Job_Ref);
        text_Goods_from = content.findViewById(R.id.text_Goods_from);
        Notes = content.findViewById(R.id.Notes);
        text_Style = content.findViewById(R.id.text_Style);

        imgTagPic1 = content.findViewById(R.id.imgTagPic1);
        imgTagPic2 = content.findViewById(R.id.imgTagPic2);
        imgTagPic3 = content.findViewById(R.id.imgTagPic3);
        imgTagPic4 = content.findViewById(R.id.imgTagPic4);

        txtPercentage = content.findViewById(R.id.txtPercentage);
        txtTagImgDesc = content.findViewById(R.id.txtTagImgDesc);

        for (int i = 1; i <= 4; i++) { //You might have to change that slightly depending on where you want to start/end counting
            int res = getResources().getIdentifier("imgTagPic"+i, "id", getPackageName()); //This line is necessary to "convert" a string (e.g. "i1", "i2" etc.) to a resource ID
            letters[i] = (ImageView) findViewById(res);
            letters[i].setVisibility(View.VISIBLE);
            letters[i].setOnClickListener(this);
            letters[i].setEnabled(true);
            //setOnclicklistener for letters[i] and whatever you would like to do.
        }
        imgTagPic1.setEnabled(true);


        imageView.setOnClickListener(this);
        recordicon = content.findViewById(R.id.recordicon);
        playicon = content.findViewById(R.id.playicon);
        playicon.setEnabled(false);
        playicon.setVisibility(View.INVISIBLE);

        song_seekbartext = content.findViewById(R.id.song_seekbartext);

        seekBar = (SeekBar) content.findViewById(R.id.song_seekbar);
        seekBar.setVisibility(View.INVISIBLE);
        song_seekbartext.setVisibility(View.INVISIBLE);

        mycm = (Chronometer) content.findViewById(R.id.simpleChronometer);
        mycm.setVisibility(View.INVISIBLE);

        text_DC_No.setText(dcno);
        text_DC_Date.setText(dcdate);
        text_Job_Ref.setText(joborderno+" | " + shipcode);
        text_Style.setText(styleno);
        text_Goods_from.setText(vendorname);


        text_Fabric_Type = content.findViewById(R.id.text_Fabric_Type);
        text_Reference = content.findViewById(R.id.text_Reference);
        text_Yarn_Details = content.findViewById(R.id.text_Yarn_Details);
        text_Color = content.findViewById(R.id.text_Color);
        text_Gauge = content.findViewById(R.id.text_Gauge);
        text_Rolls_and_weight = content.findViewById(R.id.text_Rolls_and_weight);

        getvalue();
        get_deliverydetails();

        AddProg = content.findViewById(R.id.AddProg);
        AddProg.setOnClickListener(this);

        AddReject = content.findViewById(R.id.AddReject);
        AddReject.setOnClickListener(this);

        Go_Back = content.findViewById(R.id.Go_Back);
        Go_Back.setOnClickListener(this);
        recordicon.setOnClickListener(this);
        playicon.setOnClickListener(this);

    }

    public void onClick(View v) {
        if (isOnline()) {
            switch (v.getId()) {
                case R.id.imgd:
                    toggleDrawer();
                    break;
                case R.id.imgTagPic1:
                    current_id = 1;
                    openCameraIntent();
                    break;
                case R.id.imgTagPic2:
                    if(image_tak1)
                    {
                        current_id = 2;
                        openCameraIntent();
                    }
                    else
                    {
                        new AlertDialog.Builder(Fabric_Delivery_Details_Activity.this)
                                .setMessage("Please Upload Delivery Image1 Copy")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface d, int arg0) {
                                        d.dismiss();
                                    }
                                }).show();
                    }

                    break;
                case R.id.imgTagPic3:

                    if(image_tak1 && image_tak2)
                    {
                        current_id = 3;
                        openCameraIntent();
                    }
                    else if(image_tak1  == false)
                    {
                        new AlertDialog.Builder(Fabric_Delivery_Details_Activity.this)
                                .setMessage("Please Upload Delivery Image1 Copy")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface d, int arg0) {
                                        d.dismiss();
                                    }
                                }).show();
                    }
                    else
                    {
                        new AlertDialog.Builder(Fabric_Delivery_Details_Activity.this)
                                .setMessage("Please Upload Delivery Image2 Copy")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface d, int arg0) {
                                        d.dismiss();
                                    }
                                }).show();
                    }
                    break;
                case R.id.imgTagPic4:
                    if(image_tak1 && image_tak2 && image_tak3)
                    {
                        current_id = 4;
                        openCameraIntent();
                    }
                    else if(image_tak1  == false)
                    {
                        new AlertDialog.Builder(Fabric_Delivery_Details_Activity.this)
                                .setMessage("Please Upload Delivery Image1 Copy")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface d, int arg0) {
                                        d.dismiss();
                                    }
                                }).show();
                    }
                    else if(image_tak1  == true && image_tak2  == false)
                    {
                        new AlertDialog.Builder(Fabric_Delivery_Details_Activity.this)
                                .setMessage("Please Upload Delivery Image2 Copy")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface d, int arg0) {
                                        d.dismiss();
                                    }
                                }).show();
                    }
                    else
                    {
                        new AlertDialog.Builder(Fabric_Delivery_Details_Activity.this)
                                .setMessage("Please Upload Delivery Image3 Copy")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface d, int arg0) {
                                        d.dismiss();
                                    }
                                }).show();
                    }
                    break;
                case R.id.AddProg:

                    if(fabric_dc_imgpath1 == null || fabric_dc_imgpath1.equals("") || fabric_dc_imgpath1.equals(null) || fabric_dc_imgpath1.equals("null")) {
                        new AlertDialog.Builder(Fabric_Delivery_Details_Activity.this)
                                .setMessage("Please Upload Delivery Image Copy")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface d, int arg0) {
                                        d.dismiss();
                                    }
                                }).show();
                    }
                    else
                    {
                        Accept_delivery_data();
                    }

                    break;
                case R.id.AddReject:
                    if(Notes.getText().length() == 0) {
                        new AlertDialog.Builder(Fabric_Delivery_Details_Activity.this)
                                .setMessage("Please Enter Reject Notes")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface d, int arg0) {
                                        d.dismiss();
                                    }
                                }).show();
                    }
                    else
                    {
                        rejectdelivery();
                    }
                    break;
                case R.id.Go_Back:
                    onBackPressed();
                    break;
                case R.id.recordicon:

                    if(recording ==false && playaudio == false)
                    {
                        if(checkPermission()) {
                            recording =true;
                            audiorecourd =true;
                            AddProg.setVisibility(View.GONE);
                            AddReject.setVisibility(View.GONE);
                            Go_Back.setVisibility(View.GONE);
                            playicon.setEnabled(false);
                            playicon.setVisibility(View.INVISIBLE);
                            seekBar.setVisibility(View.INVISIBLE);
                            song_seekbartext.setVisibility(View.INVISIBLE);
                            File audioFile = null;
                            try {
                                audioFile = createAudioFile();
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                            if (audioFile != null) {
                                audioURI = FileProvider.getUriForFile(this,  "com.bipinexports.productionqr.fileprovider", audioFile);
                            }
                            MediaRecorderReady();
                            try {
                                mediaRecorder.prepare();
                                mediaRecorder.start();

                                mycm.setBase(SystemClock.elapsedRealtime());
                                mycm.stop();
                                elapsed = 0;
                                mycm.setVisibility(View.VISIBLE);
                                mycm.setBase(SystemClock.elapsedRealtime() - elapsed);
                                mycm.start();
                            } catch (IllegalStateException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }

                            recordicon.setImageResource(R.drawable.ic_record_red);

                            Toast t = Toast.makeText(Fabric_Delivery_Details_Activity.this, "Recording started", Toast.LENGTH_LONG);
                            t.show();
                            msjsToast.add(t);

//                            Toast.makeText(ViewAndUpdateBundleActivity.this, "Recording started", Toast.LENGTH_LONG).show();
                        }
                        else {
                            requestPermission();
                        }
                    }
                    else if(recording && playaudio == false)
                    {
                        mediaRecorder.stop();
                        recordicon.setImageResource(R.drawable.ic_baseline_mic_green);
                        AddProg.setVisibility(View.VISIBLE);
                        AddReject.setVisibility(View.VISIBLE);
                        Go_Back.setVisibility(View.VISIBLE);
                        mycm.stop();
                        elapsed = (int) (SystemClock.elapsedRealtime() -  mycm.getBase());

                        final long minutes=(elapsed/1000)/60;
                        final int seconds= (int) ((elapsed/1000)%60);

                        if(minutes<10 && seconds<10)
                        {
                            endtime = "0"+minutes+ ":"+"0"+seconds;
                        }
                        else if(minutes<10 && seconds>10)
                        {
                            endtime = "0"+minutes+ ":"+seconds;
                        }
                        else if(minutes>10 &&  seconds<10)
                        {
                            endtime = minutes+ ":"+"0"+seconds;
                        }
                        else
                        {
                            endtime = minutes+ ":"+seconds;
                        }

                        mycm.setVisibility(View.INVISIBLE);
                        song_seekbartext.setText("00:00/"+endtime);

                        seekBar.setVisibility(View.VISIBLE);
                        song_seekbartext.setVisibility(View.VISIBLE);
                        playicon.setEnabled(true);
                        playicon.setVisibility(View.VISIBLE);

                        mediaPlayer = new MediaPlayer();
                        try {
                            mediaPlayer.setDataSource(AudioSavePathInDevice);
                            mediaPlayer.prepare();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        recording = false;
                        seekBar.setMax(mediaPlayer.getDuration());

                        Toast t = Toast.makeText(Fabric_Delivery_Details_Activity.this, "Recording Completed", Toast.LENGTH_LONG);
                        t.show();
                        msjsToast.add(t);
//                        Toast.makeText(ViewAndUpdateBundleActivity.this, "Recording Completed", Toast.LENGTH_LONG).show();
                    }

                    break;
                case R.id.playicon:
                    if(recordplay == false )
                    {
                        playstop = false;
                        seekBar.setVisibility(View.VISIBLE);
                        song_seekbartext.setVisibility(View.VISIBLE);
                        playicon.setImageResource(R.drawable.ic_stop_circle_red);

                        playaudio = true;
                        mediaPlayer = new MediaPlayer();
                        try {
                            mediaPlayer.setDataSource(AudioSavePathInDevice);
                            mediaPlayer.prepare();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        recordplay = true;
                        mediaPlayer.start();
                        updateSeekBar();
                        Toast t = Toast.makeText(Fabric_Delivery_Details_Activity.this, "Recording Playing", Toast.LENGTH_LONG);
                        t.show();
                        msjsToast.add(t);

                        //Toast.makeText(ViewAndUpdateBundleActivity.this, "Recording Playing", Toast.LENGTH_LONG).show();
                    }
                    else if(recordplay)
                    {
                        playaudio = false;
                        playstop = true;

                        seekBar.setVisibility(View.INVISIBLE);
                        song_seekbartext.setVisibility(View.INVISIBLE);
                        recordicon.setImageResource(R.drawable.ic_baseline_mic_green);
                        playicon.setImageResource(R.drawable.ic_play_circle_blue);

                        recordicon.setEnabled(true);
                        recordicon.setVisibility(View.VISIBLE);

                        if(mediaPlayer != null){
                            mediaPlayer.stop();
                            mediaPlayer.release();
                            MediaRecorderReady();
                        }
                        recordplay = false;
                    }
                    break;
            }
        }
        else {
            AlertDialog alertDialog = new AlertDialog.Builder(Fabric_Delivery_Details_Activity.this)
                    .setMessage("Please Check Your Internet Connection")
                    .setCancelable(false)
                    .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg1, int arg0) {
                            arg1.dismiss();
                            finish();
                        }
                    }).show();
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile( imageFileName, ".jpg", storageDir);
        imageFilePath = image.getAbsolutePath();
        return image;
    }

    private void openCameraIntent() {
        Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(pictureIntent.resolveActivity(getPackageManager()) != null){
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,  "com.bipinexports.productionqr.fileprovider", photoFile);
                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                pictureIntent.putExtra("android.intent.extras.CAMERA_FACING", 0);
                startActivityForResult(pictureIntent, REQUEST_CAPTURE_IMAGE);
            }
        }
    }

    private File createAudioFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String audioFileName = timeStamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        File audio = File.createTempFile( audioFileName, ".3gp", storageDir);
        AudioSavePathInDevice = audio.getAbsolutePath();
        return audio;
    }

    public void MediaRecorderReady(){
        mediaRecorder=new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(AudioSavePathInDevice);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(Fabric_Delivery_Details_Activity.this, new
                String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length> 0) {
                    boolean StoragePermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission) {
                        Toast t = Toast.makeText(Fabric_Delivery_Details_Activity.this, "Permission Grante", Toast.LENGTH_LONG);
                        t.show();
                        msjsToast.add(t);
//                        Toast.makeText(ViewAndUpdateBundleActivity.this, "Permission Granted", Toast.LENGTH_LONG).show();
                    } else {

                        Toast t = Toast.makeText(Fabric_Delivery_Details_Activity.this, "Permission Denied", Toast.LENGTH_LONG);
                        t.show();
                        msjsToast.add(t);

//                        Toast.makeText(ViewAndUpdateBundleActivity.this,"Permission Denied",Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
        }

        @Override
        protected String doInBackground(Void... params) {
            HttpURLConnection conn;
            DataOutputStream dos;
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";
            int bytesRead = 0;

            byte[] buffer;
            File sourceFile = new File(AudioSavePathInDevice);
            String response = "";
            if (sourceFile.isFile()) {

                if (isOnline()) {
                    try {
                        String upLoadServerUri = SpaceIamgepath_URL + "upload_audio_fabric_delivery_file";

                        FileInputStream fileInputStream = new FileInputStream(sourceFile);
                        URL url = new URL(upLoadServerUri);

                        conn = (HttpURLConnection) url.openConnection();
                        conn.setDoInput(true);
                        conn.setDoOutput(true);
                        conn.setUseCaches(false);
                        conn.setRequestMethod("POST");
                        conn.setRequestProperty("Connection", "Keep-Alive");
                        conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                        conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                        conn.setRequestProperty("audiofile", AudioSavePathInDevice);

                        dos = new DataOutputStream(conn.getOutputStream());

                        dos.writeBytes(twoHyphens + boundary + lineEnd);
                        dos.writeBytes("Content-Disposition: form-data; name=\"audiofile\";filename=\"" + AudioSavePathInDevice + "\"" + lineEnd);
                        dos.writeBytes(lineEnd);


                        buffer = new byte[1024];
                        // int progress = 0;

                        BufferedInputStream bufInput = new BufferedInputStream(fileInputStream);
                        while ((bytesRead = bufInput.read(buffer)) != -1) {
                            if (isOnline()) {
                                dos.write(buffer, 0, bytesRead);
                                dos.flush();
                            }
                            else
                            {
                                AlertDialog alertDialog = new AlertDialog.Builder(Fabric_Delivery_Details_Activity.this)
                                        .setMessage("Please Check Your Internet Connection")
                                        .setCancelable(false)
                                        .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface arg1, int arg0) {

                                                Fabric_Delivery_Details_Activity.custPrograssbar_new.closePrograssBar();
                                                arg1.dismiss();
                                                onBackPressed();

                                            }
                                        }).show();

                            }
                        }

                        dos.writeBytes(lineEnd);
                        dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                        fileInputStream.close();
                        dos.flush();
                        dos.close();

                        int serverResponseCode = conn.getResponseCode();
                        BufferedReader br;
                        String line;
                        if (serverResponseCode == HttpURLConnection.HTTP_OK) {
                            br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                            while ((line = br.readLine()) != null) {
                                response += line;
                            }
                        } else {
                            br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                            while ((line = br.readLine()) != null) {
                                response += line;
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else
                {
                    AlertDialog alertDialog = new AlertDialog.Builder(Fabric_Delivery_Details_Activity.this)
                            .setMessage("Please Check Your Internet Connection")
                            .setCancelable(false)
                            .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg1, int arg0) {
                                    arg1.dismiss();

                                }
                            }).show();
                }
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                JSONObject json = new JSONObject(result);
                String status = json.optString("status");
                String mess = json.optString("message");
                if (status.equals("success")) {
                    JSONObject jsonObj = json.getJSONObject("data");
                    String audioname = jsonObj.optString("audioname");
                    audiofilepath = jsonObj.optString("audiofilepath");
                   fabric_acceptdelivery();
                }
                else
                {
                    new AlertDialog.Builder(Fabric_Delivery_Details_Activity.this)
                            .setMessage(mess)
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface d, int arg0) {
                                    d.dismiss();
                                }
                            }).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("BipinExports", "JSONException: " + e);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CAPTURE_IMAGE) {
            if (resultCode == RESULT_OK) {
                Fabric_Delivery_Details_Activity.custPrograssbar_new.prograssCreate(this);
                // Load the original bitmap
                Bitmap originalBitmap = BitmapFactory.decodeFile(imageFilePath);
                int originalSize = originalBitmap.getByteCount();

                // Compress the bitmap
                Bitmap compressedBitmap = compressImage(imageFilePath);

                // After compression
                int compressedSize = compressedBitmap.getByteCount();

                if(current_id ==1) {
                    imgTagPic1.setImageBitmap(compressedBitmap);
                }
                else  if(current_id ==2) {
                    imgTagPic2.setImageBitmap(compressedBitmap);
                }
                else  if(current_id ==3) {
                    imgTagPic3.setImageBitmap(compressedBitmap);
                }
                else  if(current_id ==4) {
                    imgTagPic4.setImageBitmap(compressedBitmap);
                }

                File compressedFile = saveBitmapToFile(compressedBitmap);
                imageFilePath = compressedFile.getAbsolutePath(); // Update the image file p

                // Start AsyncTask to upload the compressed image
                new UploadFileToServer1(Fabric_Delivery_Details_Activity.this).execute();

            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Picture was not taken", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Picture was not taken", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private class UploadFileToServer1 extends AsyncTask<Void, Integer, String> {
        private Context context;
        // private int maxlen = 0;
        public UploadFileToServer1(Context context) {
            this.context = context;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Fabric_Delivery_Details_Activity.custPrograssbar_new.prograssCreate(context);
            txtPercentage.setText("Uploading Fabric Delivery Order Copy! Please Wait...");
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
        }

        @Override
        protected String doInBackground(Void... params) {
            HttpURLConnection conn;
            DataOutputStream dos;
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";
            int bytesRead = 0;
            byte[] buffer;
            String response = "";

            File sourceFile = new File(imageFilePath);
            if (sourceFile.isFile())
            {
                if (isOnline()) {
                    try {
                        String upLoadServerUri = SpaceIamgepath_URL + "upload_fabric_delivery_file";

                        FileInputStream fileInputStream = new FileInputStream(sourceFile);
                        URL url = new URL(upLoadServerUri);

                        conn = (HttpURLConnection) url.openConnection();
                        conn.setDoInput(true);
                        conn.setDoOutput(true);
                        conn.setUseCaches(false);
                        conn.setRequestMethod("POST");
                        conn.setRequestProperty("Connection", "Keep-Alive");
                        conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                        conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                        if (current_id == 1) {
                            conn.setRequestProperty("fabric_dc_img1", imageFilePath);
                        } else if (current_id == 2) {
                            conn.setRequestProperty("fabric_dc_img2", imageFilePath);
                        } else if (current_id == 3) {
                            conn.setRequestProperty("fabric_dc_img3", imageFilePath);
                        } else if (current_id == 4) {
                            conn.setRequestProperty("fabric_dc_img4", imageFilePath);
                        }

                        dos = new DataOutputStream(conn.getOutputStream());

                        dos.writeBytes(twoHyphens + boundary + lineEnd);
                        if (current_id == 1) {
                            dos.writeBytes("Content-Disposition: form-data; name=\"fabric_dc_img1\";filename=\"" + imageFilePath + "\"" + lineEnd);
                        } else if (current_id == 2) {
                            dos.writeBytes("Content-Disposition: form-data; name=\"fabric_dc_img2\";filename=\"" + imageFilePath + "\"" + lineEnd);
                        } else if (current_id == 3) {
                            dos.writeBytes("Content-Disposition: form-data; name=\"fabric_dc_img3\";filename=\"" + imageFilePath + "\"" + lineEnd);
                        } else if (current_id == 4) {
                            dos.writeBytes("Content-Disposition: form-data; name=\"fabric_dc_img4\";filename=\"" + imageFilePath + "\"" + lineEnd);
                        }
                        dos.writeBytes(lineEnd);

                        buffer = new byte[1024];
                        BufferedInputStream bufInput = new BufferedInputStream(fileInputStream);
                        while ((bytesRead = bufInput.read(buffer)) != -1) {
                            if (isOnline()) {
                                dos.write(buffer, 0, bytesRead);
                                dos.flush();
                            } else {
                                AlertDialog alertDialog = new AlertDialog.Builder(Fabric_Delivery_Details_Activity.this)
                                        .setMessage("Please Check Your Internet Connection")
                                        .setCancelable(false)
                                        .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface arg1, int arg0) {

                                                Fabric_Delivery_Details_Activity.custPrograssbar_new.closePrograssBar();
                                                arg1.dismiss();
                                                onBackPressed();

                                            }
                                        }).show();
//                                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED);
                            }
                        }

                        dos.writeBytes(lineEnd);
                        dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                        fileInputStream.close();
                        dos.flush();
                        dos.close();

                        int serverResponseCode = conn.getResponseCode();
                        BufferedReader br;
                        String line;
                        if (serverResponseCode == HttpURLConnection.HTTP_OK) {
                            br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                            while ((line = br.readLine()) != null) {
                                response += line;
                            }
                        } else {
                            br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                            while ((line = br.readLine()) != null) {
                                response += line;
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else {
                    AlertDialog alertDialog = new AlertDialog.Builder(Fabric_Delivery_Details_Activity.this)
                            .setMessage("Please Check Your Internet Connection")
                            .setCancelable(false)
                            .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg1, int arg0) {

                                    Fabric_Delivery_Details_Activity.custPrograssbar_new.closePrograssBar();
                                    arg1.dismiss();
                                    onBackPressed();
                                }
                            }).show();
//                    alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED);
                }
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (isOnline()) {
                txtPercentage.setText("");
                txtTagImgDesc.setText("Fabric Delivery File Uploaded!");
                try {
                    JSONObject json = new JSONObject(result);
                    String status = json.optString("status");
                    String mess = json.optString("message");

                    if (status.equals("success")) {
                        JSONObject jsonObj = json.getJSONObject("data");
                        String imgname = jsonObj.optString("imgname");

                        if (imgname == null || imgname.isEmpty()) {
                            new AlertDialog.Builder(Fabric_Delivery_Details_Activity.this)
                                    .setMessage("Cannot upload Report Master File to server. Please retry! If problem persists, contact Admin.")
                                    .setCancelable(false)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface d, int arg0) {
                                            d.dismiss();
                                        }
                                    }).show();
                        }
                        else {

                            if(current_id ==1)
                            {
                                fabric_dc_imgpath1 = jsonObj.optString("fabric_dc_imgpath1");
                                imgTagPic2.setEnabled(true);
                                image_tak1 = true;
                            }
                            else  if(current_id ==2)
                            {
                                fabric_dc_imgpath2 = jsonObj.optString("fabric_dc_imgpath2");
                                imgTagPic3.setEnabled(true);
                                additionalimages.add(fabric_dc_imgpath2);
                                image_tak2 = true;
                            }
                            else  if(current_id ==3)
                            {
                                fabric_dc_imgpath3 = jsonObj.optString("fabric_dc_imgpath3");
                                imgTagPic4.setEnabled(true);
                                additionalimages.add(fabric_dc_imgpath3);
                                image_tak3 = true;
                            }
                            else  if(current_id ==4)
                            {
                                fabric_dc_imgpath4 = jsonObj.optString("fabric_dc_imgpath4");
                                additionalimages.add(fabric_dc_imgpath4);
                            }

                        }
                        Fabric_Delivery_Details_Activity.custPrograssbar_new.closePrograssBar();
                    }
                    else {
                        new AlertDialog.Builder(Fabric_Delivery_Details_Activity.this)
                                .setMessage(mess)
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface d, int arg0) {
                                        d.dismiss();
                                    }
                                }).show();
                        Fabric_Delivery_Details_Activity.custPrograssbar_new.closePrograssBar();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else {
                AlertDialog alertDialog = new AlertDialog.Builder(Fabric_Delivery_Details_Activity.this)
                        .setMessage("Please Check Your Internet Connection")
                        .setCancelable(false)
                        .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg1, int arg0) {
                                Fabric_Delivery_Details_Activity.custPrograssbar_new.closePrograssBar();
                                onBackPressed();

                            }
                        }).show();
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED);
                Fabric_Delivery_Details_Activity.custPrograssbar_new.closePrograssBar();
            }
        }
    }


    private Bitmap compressImage(String imagePath) {
        final int MAX_SIZE_KB = 500;
        final int MAX_QUALITY = 80;

        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);

        // Handle EXIF orientation
        try {
            ExifInterface exif = new ExifInterface(imagePath);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
            bitmap = rotateBitmap(bitmap, orientation);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        int quality = MAX_QUALITY;

        // Compress the bitmap with decreasing quality until the size is less than 500KB
        do {
            outputStream.reset(); // Reset the stream for each compression attempt
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            quality -= 10; // Decrease quality by 10 each time
        } while (outputStream.size() / 1024 > MAX_SIZE_KB && quality > 0);

        // Decode the compressed bitmap from the byte array
        byte[] compressedData = outputStream.toByteArray();
        return BitmapFactory.decodeByteArray(compressedData, 0, compressedData.length);
    }

    private Bitmap rotateBitmap(Bitmap bitmap, int orientation) {
        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.postRotate(90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.postRotate(180);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.postRotate(270);
                break;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.postScale(1, -1);
                break;
            default:
                return bitmap;
        }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    private File saveBitmapToFile(Bitmap bitmap) {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_compressed.jpg";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        Log.e("Bipin","storageDir : " +storageDir);

        File compressedFile = new File(storageDir, imageFileName);

        try {
            FileOutputStream outputStream = new FileOutputStream(compressedFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream); // Compress with maximum quality
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return compressedFile;
    }

    private void Accept_delivery_data()
    {
        if(audiorecourd)
        {
            Fabric_Delivery_Details_Activity.custPrograssbar_new.prograssCreate(this);
            new UploadFileToServer().execute();
        }
        else
        {
            fabric_acceptdelivery();
        }
    }


    private void fabric_acceptdelivery() {

        if (isOnline()) {
            session = new SessionManagement(getApplicationContext());
            HashMap<String, String> user = session.getUserDetails();
            processorid = user.get(SessionManagement.KEY_PROCESSOR_ID);
            userid = user.get(SessionManagement.KEY_USER_ID);
            Fabric_Delivery_Details_Activity.custPrograssbar_new.prograssCreate(this);

            JSONObject jsonObject = new JSONObject();
            try {

                jsonObject.put("userid", userid);
                jsonObject.put("contractorid", processorid);

                if (additionalimages != null && additionalimages.size() > 0)
                {
                    for (int i = 0; i < additionalimages.size(); i++) {
                        additional_images.put(additionalimages.get(i));
                    }
                }

                jsonObject.put("dcid", dcid);
                jsonObject.put("verifiedby", userid);
                jsonObject.put("imgpath", fabric_dc_imgpath1);
                jsonObject.put("addlimages", additional_images);

                if(audiofilepath == null || audiofilepath.equals("") || audiofilepath.equals(null) || audiofilepath.equals("null"))
                {
                    audiofilepath = "";
                }
                jsonObject.put("audiopath", audiofilepath);
                jsonObject.put("reason", Notes.getText());

                JsonParser jsonParser = new JsonParser();
                Call<JsonObject> call = APIClient.getInterface().fabric_acceptdelivery((JsonObject) jsonParser.parse(jsonObject.toString()));
                GetResult getResult = new GetResult();
                getResult.setMyListener(this);
                getResult.callForLogin(call, "acceptdelivery");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else {
            AlertDialog alertDialog = new AlertDialog.Builder(Fabric_Delivery_Details_Activity.this)
                    .setMessage("Please Check Your Internet Connection")
                    .setCancelable(false)
                    .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg1, int arg0) {
                            Fabric_Delivery_Details_Activity.custPrograssbar_new.closePrograssBar();
                            onBackPressed();

                        }
                    }).show();
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED);
            Fabric_Delivery_Details_Activity.custPrograssbar_new.closePrograssBar();
        }
    }

    private void rejectdelivery() {

        if (isOnline()) {
            session = new SessionManagement(getApplicationContext());
            HashMap<String, String> user = session.getUserDetails();
            processorid = user.get(SessionManagement.KEY_PROCESSOR_ID);
            userid = user.get(SessionManagement.KEY_USER_ID);
            Fabric_Delivery_Details_Activity.custPrograssbar_new.prograssCreate(this);

            JSONObject jsonObject = new JSONObject();
            try {

                jsonObject.put("userid", userid);
                jsonObject.put("contractorid", processorid);

                if (additionalimages != null && additionalimages.size() > 0)
                {
                    for (int i = 0; i < additionalimages.size(); i++) {
                        additional_images.put(additionalimages.get(i));
                    }
                }

                jsonObject.put("dcid", dcid);
                jsonObject.put("verifiedby", userid);
                jsonObject.put("imgpath", fabric_dc_imgpath1);
                jsonObject.put("addlimages", additional_images);

                if(audiofilepath == null || audiofilepath.equals("") || audiofilepath.equals(null) || audiofilepath.equals("null"))
                {
                    audiofilepath = "";
                }
                jsonObject.put("audiopath", audiofilepath);
                jsonObject.put("reason", Notes.getText());

                JsonParser jsonParser = new JsonParser();
                Call<JsonObject> call = APIClient.getInterface().fabric_rejectdelivery((JsonObject) jsonParser.parse(jsonObject.toString()));
                GetResult getResult = new GetResult();
                getResult.setMyListener(this);
                getResult.callForLogin(call, "acceptdelivery");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else {
            AlertDialog alertDialog = new AlertDialog.Builder(Fabric_Delivery_Details_Activity.this)
                    .setMessage("Please Check Your Internet Connection")
                    .setCancelable(false)
                    .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg1, int arg0) {
                            Fabric_Delivery_Details_Activity.custPrograssbar_new.closePrograssBar();
                            onBackPressed();

                        }
                    }).show();
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED);
            Fabric_Delivery_Details_Activity.custPrograssbar_new.closePrograssBar();
        }
    }

    private void get_deliverydetails() {
        session = new SessionManagement(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        processorid = user.get(SessionManagement.KEY_PROCESSOR_ID);
        userid = user.get(SessionManagement.KEY_USER_ID);
        Fabric_Delivery_Details_Activity.custPrograssbar_new.prograssCreate(this);

        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("userid", userid);
            jsonObject.put("contractorid", processorid);
            jsonObject.put("dcid", dcid);

            JsonParser jsonParser = new JsonParser();
            Call<JsonObject> call = APIClient.getInterface().fabric_deliverydetails((JsonObject) jsonParser.parse(jsonObject.toString()));
            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.callForLogin(call, "deliverydetails");
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onBackPressed() {
        session = new SessionManagement(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        Intent intent = new Intent(Fabric_Delivery_Details_Activity.this, Fabric_Receipt_Activity.class);
        intent.putExtra("name", user.get(SessionManagement.KEY_USER));
        intent.putExtra("userid", user.get(SessionManagement.KEY_USER_ID));
        intent.putExtra("processorid", user.get(SessionManagement.KEY_PROCESSOR_ID));
        intent.putExtra("delivery", delivery.toString());
        intent.putExtra("pendingcount", pendingcount);
        intent.putExtra("processorid", processorid);

        startActivity(intent);
        finish();
    }

    public void getvalue() {
        txtUser.setText("Hello " + this.User);
        ModelClass modelClass = new ModelClass();
        modelClass.setmID(userid);
        Fabric_Delivery_Details_Activity.custPrograssbar_new.closePrograssBar();
    }


    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    @Override
    public void callback(JsonObject result, String callNo) {
        try {
            Fabric_Delivery_Details_Activity.custPrograssbar_new.closePrograssBar();

            if (callNo.equalsIgnoreCase("deliverydetails")) {
                JSONObject jsonObject = new JSONObject(result.toString());
                String mStatus = jsonObject.optString("status");
                String message = jsonObject.optString("message");
                if (mStatus.equals("success")) {

                    JSONObject jsonObj = jsonObject.getJSONObject("data");
                    JSONObject dcdetailsJsonObj = jsonObj.getJSONObject("dcdetails");
                    JSONObject vendorJsonObj = dcdetailsJsonObj.getJSONObject("vendor");
                    JSONObject detailsJsonObj = dcdetailsJsonObj.getJSONObject("detail");
                    JSONObject rollsJsonObj = dcdetailsJsonObj.getJSONObject("rolls");

                    String dcid = dcdetailsJsonObj.optString("dcid");
                    String dcno = dcdetailsJsonObj.optString("dcno");
                    String dcdate = dcdetailsJsonObj.optString("dcdate");
                    String progno = dcdetailsJsonObj.optString("progno");
                    String progdate = dcdetailsJsonObj.optString("progdate");

                    String fromvendor = dcdetailsJsonObj.optString("fromvendor");
                    String jobref = dcdetailsJsonObj.optString("joborderno");
                    String styleno = dcdetailsJsonObj.optString("styleno");
                    String shipcode = dcdetailsJsonObj.optString("shipcode");
                    String partname = dcdetailsJsonObj.optString("partname");
                    String sentrolls = dcdetailsJsonObj.optString("sentrolls");
                    String sentweight = dcdetailsJsonObj.optString("sentweight");
                    String status = dcdetailsJsonObj.optString("status");

                    String outwardid =  detailsJsonObj.getString("outwardid");
                    String detailid =  detailsJsonObj.getString("detailid");
                    String fabric =  detailsJsonObj.getString("fabric");
                    String color = detailsJsonObj.getString("color");
                    String dia = detailsJsonObj.getString("dia");
                    String gsm = detailsJsonObj.getString("gsm");
                    String gauge = detailsJsonObj.getString("gauge");
                    String rolls = detailsJsonObj.getString("rolls");
                    String weight = detailsJsonObj.getString("weight");
                    rollslength = rollsJsonObj.length();

                    text_Fabric_Type.setText(fabric);
                    text_Reference.setText(progno);
                    text_Yarn_Details.setText("");
                    text_Color.setText(color);
                    text_Gauge.setText(dia + " | " + gsm + " | " + gauge);
                    text_Rolls_and_weight.setText(rolls +" | " + weight);
                    Fabric_Spec.setVisibility(View.VISIBLE);

                    int divlength = (int) Math.ceil(rollslength / 2.0);
                    FIRST_HALF_COUNT =  divlength;

                    try {
                        TableLayout programtbl = findViewById(R.id.addprogramdata);
                        Drawable cellBorder = ContextCompat.getDrawable(this, R.drawable.table_cell_border);
                        List<JSONObject> firstHalf = new ArrayList<>();
                        List<JSONObject> secondHalf = new ArrayList<>();

                        Iterator<String> prog = rollsJsonObj.keys();
                        int index = 0;
                        while (prog.hasNext()) {
                            String key = prog.next();
                            if (rollsJsonObj.get(key) instanceof JSONObject) {
                                JSONObject roll = (JSONObject) rollsJsonObj.get(key);
                                if (index < FIRST_HALF_COUNT) {
                                    firstHalf.add(roll);
                                } else {
                                    secondHalf.add(roll);
                                }
                                index++;
                            }
                        }
                        // Ensure both lists have the same length by adding empty JSONObjects if necessary
                        while (firstHalf.size() < secondHalf.size()) {
                            firstHalf.add(new JSONObject());
                        }
                        while (secondHalf.size() < firstHalf.size()) {
                            secondHalf.add(new JSONObject());
                        }

                        // Iterate over the lists and create table rows
                        for (int i = 0; i < firstHalf.size(); i++) {
                            TableRow row = new TableRow(this);
                            row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                            row.setPadding(0, 0, 0, 0);


                            addCellToRow(row, firstHalf.get(i), cellBorder);

                            if (i < secondHalf.size()) {
                                addCellToRow(row, secondHalf.get(i), cellBorder);
                            } else {
                                addEmptyCellToRow(row, cellBorder);  // Add empty cell if secondHalf has less items
                            }

                            programtbl.addView(row, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("NidhviTec", "JSONException: " + e);
                    }
                }
                else if (mStatus.equals("logout"))
                {
                    new AlertDialog.Builder(Fabric_Delivery_Details_Activity.this)
                            .setMessage(message)
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg1, int arg0) {
                                    arg1.dismiss();
                                    session.logoutUser();
                                    finishAffinity();
                                    finish();
                                }
                            }).show();
                }
                else {
                    new AlertDialog.Builder(Fabric_Delivery_Details_Activity.this)
                            .setMessage(message)
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg1, int arg0) {
                                    arg1.dismiss();
                                    onBackPressed();
                                }
                            }).show();
                }

            }
            else  if (callNo.equalsIgnoreCase("acceptdelivery")) {


                new AlertDialog.Builder(Fabric_Delivery_Details_Activity.this)
                        .setMessage("Fabric Verification updated successfully but Return null to do")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg1, int arg0) {
                                arg1.dismiss();
                                onBackPressed();
                            }
                        }).show();
//                JSONObject jsonObject = new JSONObject(result.toString());

//                String mStatus = jsonObject.optString("status");
//                String message = jsonObject.optString("message");
//                Fabric_Delivery_Details_Activity.custPrograssbar_new.closePrograssBar();
//                if (mStatus.equals("success")) {
//                    new AlertDialog.Builder(Fabric_Delivery_Details_Activity.this)
//                            .setMessage(message)
//                            .setCancelable(false)
//                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface arg1, int arg0) {
//                                    arg1.dismiss();
//                                    onBackPressed();
//                                }
//                            }).show();
//                }
//                else {
//                    new AlertDialog.Builder(Fabric_Delivery_Details_Activity.this)
//                            .setMessage(message)
//                            .setCancelable(false)
//                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface arg1, int arg0) {
//                                    arg1.dismiss();
//                                }
//                            }).show();
//                }
            }

        }
        catch (Exception e) {
        }
    }


    private void addEmptyCellToRow(TableRow row, Drawable cellBorder) {
        TextView cell = new TextView(this);
        cell.setText("");
        cell.setBackground(cellBorder);  // Apply the border
        cell.setPadding(8, 8, 8, 8);     // Add padding to the cell
        row.addView(cell);
    }

    private void addCellToRow(TableRow row, JSONObject roll, Drawable cellBorder) {
        try {
            String rollno = roll.optString("rollno", "");
            String weights = roll.optString("weight", "");

            TextView txtsizename = new TextView(this);
            txtsizename.setText(rollno);
            txtsizename.setTextColor(Color.BLACK);
            txtsizename.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            txtsizename.setBackground(cellBorder);  // Apply the border
            txtsizename.setGravity(Gravity.CENTER);
            txtsizename.setPadding(8, 8, 8, 8);
            row.addView(txtsizename);

            TextView txt_quantity = new TextView(this);
            txt_quantity.setText(weights);
            txt_quantity.setTextColor(Color.BLACK);
            txt_quantity.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            txt_quantity.setBackground(cellBorder);  // Apply the border
            txt_quantity.setGravity(Gravity.CENTER);
            txt_quantity.setPadding(8, 8, 8, 8);
            row.addView(txt_quantity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateSeekBar() {
        getDurationTimer();
        getSeekBarStatus();
    }

    //Creating duration time method
    public void getDurationTimer(){
        final long minutes=(mediaPlayer.getCurrentPosition()/1000)/60;
        final int seconds= (int) ((mediaPlayer.getCurrentPosition()/1000)%60);

        if(minutes<10 && seconds<10)
        {
            song_seekbartext.setText("0"+minutes+ ":"+"0"+seconds+"/" +endtime);
        }
        else if(minutes<10 && seconds>10)
        {
            song_seekbartext.setText("0"+minutes+ ":"+seconds+"/" +endtime);
        }
        else if(minutes>10 &&  seconds<10)
        {
            song_seekbartext.setText(minutes+ ":"+"0"+seconds+"/" +endtime);
        }
        else
        {
            song_seekbartext.setText(minutes+ ":"+seconds+"/" +endtime);
        }
    }
    public void readyfunc()
    {
        if(recordplay)
        {
            recordicon.setImageResource(R.drawable.ic_baseline_mic_green);
            playicon.setImageResource(R.drawable.ic_play_circle_blue);

            if(mediaPlayer != null){
                mediaPlayer.stop();
                mediaPlayer.release();
                MediaRecorderReady();
            }
            recordplay = false;
            playaudio =false;
        }
    }

    //creating a method for seekBar progress
    public void getSeekBarStatus(){

        if(playstop ==false)
        {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // mp is your MediaPlayer
                    // progress is your ProgressBar

                    int currentPosition = 0;
                    int total = mediaPlayer.getDuration();
                    seekBar.setMax(total);
                    if(currentPosition >= total || mediaPlayer ==null)
                    {
                        readyfunc();
                    }
                    while (mediaPlayer != null && currentPosition < total && playstop ==false)
                    {
                        try {
                            Thread.sleep(1000);
                            if(playstop ==false)
                            {
                                currentPosition = mediaPlayer.getCurrentPosition();
                            }
                            if(currentPosition >= total)
                            {
                                if(playstop ==false)
                                {
                                    readyfunc();
                                }
                            }
                        }
                        catch (InterruptedException e) {
                            return;
                        }
                        if(playstop ==false) {
                            seekBar.setProgress(currentPosition);
                        }
                    }
                }
            }).start();
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(final SeekBar seekBar, int ProgressValue, boolean fromUser) {
                    if (fromUser) {
                        mediaPlayer.seekTo(ProgressValue);//if user drags the seekbar, it gets the position and updates in textView.
                    }
                    final long mMinutes=(ProgressValue/1000)/60;//converting into minutes
                    final int mSeconds=((ProgressValue/1000)%60);//converting into seconds
                    Log.e("seconds","seconds :" +mSeconds);

                    if(mMinutes<10 && mSeconds<10)
                    {
                        song_seekbartext.setText("0"+mMinutes+ ":"+"0"+mSeconds+"/" +endtime);
                    }
                    else if(mMinutes<10 && mSeconds>10)
                    {
                        song_seekbartext.setText("0"+mMinutes+ ":"+mSeconds+"/" +endtime);
                    }
                    else if(mMinutes>10 &&  mSeconds<10)
                    {
                        song_seekbartext.setText(mMinutes+ ":"+"0"+mSeconds+"/" +endtime);
                    }
                    else
                    {
                        song_seekbartext.setText(mMinutes+ ":"+mSeconds+"/" +endtime);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
        }
    }

}
