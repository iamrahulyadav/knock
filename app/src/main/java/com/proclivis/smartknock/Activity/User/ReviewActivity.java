package com.proclivis.smartknock.Activity.User;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.proclivis.smartknock.Activity.OtpExitActivity;
import com.proclivis.smartknock.Common.ConnectivityStatus;
import com.proclivis.smartknock.Common.Constant;
import com.proclivis.smartknock.Common.Util;
import com.proclivis.smartknock.DataBase.DatabaseHelper;
import com.proclivis.smartknock.DataBase.VisitorDB;
import com.proclivis.smartknock.DataBase.VisitorOfflineDb;
import com.proclivis.smartknock.FCM.MyFirebaseMessagingService;
import com.proclivis.smartknock.Model.InSuccess;
import com.proclivis.smartknock.R;
import com.proclivis.smartknock.Retrf.RestClient;


import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;

public class ReviewActivity extends AppCompatActivity {

    LinearLayout toolbar, llBack, llHome, llVisitorDetail, llToMeet , llVisitedOn;
    public Context context;
    TextView txtUserName;
    de.hdodenhof.circleimageview.CircleImageView imgProfile, imgProfile1, imgProfile2;
    TextView edtMobile, edtName, edtComingFrom, edtPurpose, edtToMeet , edtVisitedOn;
    Button btnConfirm;
    RelativeLayout progress, llMain;
    private DatabaseHelper db;
    Boolean doneClick = false;
    String time;

    //Amit 02.07.18 Timer: Added this variable for Count Down Timer
    CountDownTimer timer;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        switch (getResources().getConfiguration().orientation) {
            case Configuration.ORIENTATION_PORTRAIT:
                setContentView(R.layout.review_activity_potrait);

                break;

            case Configuration.ORIENTATION_LANDSCAPE:
                setContentView(R.layout.review_activity_landscape);
        }
        context = this;
        toolbar = findViewById(R.id.toolbar);
        llBack = findViewById(R.id.llBack);
        llBack.setVisibility(View.VISIBLE);

        db = new DatabaseHelper(this);

        imgProfile = findViewById(R.id.imgProfile);
        imgProfile1 = findViewById(R.id.imgProfile1);
        imgProfile2 = findViewById(R.id.imgProfile2);
        edtMobile = findViewById(R.id.edtMobile);
        edtName = findViewById(R.id.edtName);
        edtComingFrom = findViewById(R.id.edtComingFrom);
        edtPurpose = findViewById(R.id.edtPurpose);
        edtToMeet = findViewById(R.id.edtToMeet);
        edtVisitedOn = findViewById(R.id.edtVisitedOn);
        btnConfirm = findViewById(R.id.btnConfirm);
        txtUserName = findViewById(R.id.txtUserName);
        progress = findViewById(R.id.progress);
        llMain = findViewById(R.id.llMain);
        llHome = findViewById(R.id.llHome);
        llVisitorDetail = findViewById(R.id.llVisitorDetail);
        llToMeet = findViewById(R.id.llToMeet);
        llVisitedOn = findViewById(R.id.llVisitedOn);

        txtUserName.setText(Util.ReadSharePreference(context, Constant.USER_NAME) + "!!!");
        edtMobile.setText(Constant.MOBILE_NO);


        edtName.setText(Util.allCapitalize(Constant.NAME) + " + " + String.valueOf(Constant.COUNT));

        edtComingFrom.setText(Constant.COMING_FROM);
        edtPurpose.setText(Constant.PURPOSE);

        StringBuilder members = new StringBuilder();
        for (int i = 0; i < Constant.selectedMember.size(); i++) {

            if (i == Constant.selectedMember.size() - 1) {
                members.append(Constant.selectedMember.get(i).getName());
            } else {
                members.append(Constant.selectedMember.get(i).getName()).append(" ; ");
            }
        }
        edtToMeet.setText(members.toString());
        edtToMeet.setSelected(true);

        if (Constant.USER_IMAGE_PATH != null) {
            Glide.with(this)
                    .load(Constant.USER_IMAGE_PATH)
                    .into(imgProfile);
            imgProfile.setImageURI(Constant.USER_IMAGE_PATH);
        }

        if (!Util.ReadSharePreference(context, Constant.IMAGE_PROFILE_1).equalsIgnoreCase("")) {
            byte[] b1 = Base64.decode(Util.ReadSharePreference(context, Constant.IMAGE_PROFILE_1), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(b1, 0, b1.length);
            imgProfile1.setImageBitmap(bitmap);
        }

        if (!Util.ReadSharePreference(context, Constant.IMAGE_PROFILE_2).equalsIgnoreCase("")) {
            byte[] b1 = Base64.decode(Util.ReadSharePreference(context, Constant.IMAGE_PROFILE_2), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(b1, 0, b1.length);
            imgProfile2.setImageBitmap(bitmap);
        }
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!btnConfirm.getText().toString().toLowerCase().equals("done")){
                    Confirm();
                } else {
                    homeClick = false;
                    doneClick = true;
                    Intent intent = new Intent(context, UserMainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    finish();
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                }
            }
        });

        llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();
            }
        });

        llHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                homeClick = false;
                if (btnConfirm.getText().toString().toLowerCase().equals("done")){
                    doneClick = true;
                }
                Intent intent = new Intent(context, UserMainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
            }
        });

        llToMeet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        llVisitorDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!btnConfirm.getText().toString().toLowerCase().equals("done")){
                    homeClick = false;
                    Intent intent = new Intent(context, VisitorDetailActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
                }
            }
        });
    }

    TypedFile imageUser = null;

    @SuppressLint("SetTextI18n")
    public void Confirm() {
        time = Util.localeFormatedTime("yyyy-MM-dd HH:mm:ss");
        time = Util.localeFormatedTime("yyyy-MM-dd HH:mm:ss");
        if (!Util.isOnline(context)) {

            setData();

        } else {
            timer();
            File image3 = new File(getRealPathFromURI(Constant.USER_IMAGE_PATH));
            imageUser = new TypedFile("image/*", image3);

            AddVisitor(new TypedString(Util.ReadSharePreference(context, Constant.DEVICE_ID)),
                    new TypedString(Util.comaString()),
                    new TypedString(Constant.NAME),
                    new TypedString(Constant.MOBILE_NO),
                    new TypedString(Constant.COMING_FROM),
                    new TypedString(Constant.PURPOSE),
                    new TypedString(String.valueOf(Constant.COUNT)),
                    new TypedString(time),
                    imageUser);
        }
    }

    @SuppressLint("SetTextI18n")
    public void setData(){
        File imageFile = new File(getRealPathFromURI(Constant.USER_IMAGE_PATH));
        Bitmap realImage = BitmapFactory.decodeFile(imageFile.toString());
        ByteArrayOutputStream b1 = new ByteArrayOutputStream();
        realImage.compress(Bitmap.CompressFormat.JPEG, 100, b1);
        byte[] b = b1.toByteArray();

        String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
        //Amit: 19.06.18. Start Debugging
        Log.d("Util","Debug Upload Offline DB-> Selected Members:"+Constant.selectedMember.size() + " Mobile No:"+Constant.MOBILE_NO + " date:"+time);
        //Amit: 19.06.18. End Debugging

        for (int i=0 ; i<Constant.selectedMember.size(); i++){

            VisitorOfflineDb visitorOfflineDb = new VisitorOfflineDb();
            visitorOfflineDb.setVisitor_record_id("");
            visitorOfflineDb.setName(Constant.NAME);
            visitorOfflineDb.setMember(Constant.selectedMember.get(i).getId());
            visitorOfflineDb.setMember_name(Constant.selectedMember.get(i).getName());
            visitorOfflineDb.setMobileNo(Constant.MOBILE_NO);
            visitorOfflineDb.setComingFrom(Constant.COMING_FROM);
            visitorOfflineDb.setPurpose(Constant.PURPOSE);
            visitorOfflineDb.setImage(encodedImage);
            visitorOfflineDb.setCount(String.valueOf(Constant.COUNT));
            visitorOfflineDb.setDate_time_in(time);
            visitorOfflineDb.setDate_time_out("");
            visitorOfflineDb.setIs_sync("false");
            visitorOfflineDb.setStatus("");
            visitorOfflineDb.setReason("");

            db.insertVisitorOffline(visitorOfflineDb);
        }

        Snackbar snackbar = Snackbar
                .make(llMain, context.getResources().getString(R.string.offline_success), 8000);
        snackbar.show();

        ArrayList<VisitorDB> visitorDBs = db.getAllVisitor();
        Boolean flag = false;
        for (int i = 0; i < visitorDBs.size(); i++) {
            if (Constant.MOBILE_NO.equals(visitorDBs.get(i).getMobileNo())) {
                flag = true;
                break;
            }
        }
        if (flag) {
            db.updateVisitor(Constant.NAME, Constant.MOBILE_NO, Constant.COMING_FROM, Constant.PURPOSE);
        } else {
            db.insertVisitor(Constant.NAME, Constant.MOBILE_NO, Constant.COMING_FROM, Constant.PURPOSE);
        }

        Constant.selectedMember = new ArrayList<>();
        Constant.MOBILE_NO = "";
        Constant.NAME = "";
        Constant.COMING_FROM = "";
        Constant.PURPOSE = "";
        Constant.COUNT = 0;
        Constant.USER_IMAGE_PATH = null;
        Constant.faceDetect = false;

        btnConfirm.setText("Done");
        llVisitedOn.setVisibility(View.VISIBLE);
        edtVisitedOn.setText(Util.localeFormatedTime("dd-MMM-yyyy HH:mm"));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!doneClick){
                    homeClick = false;
                    doneClick = true;
                    Intent intent = new Intent(context, UserMainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    finish();
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                }
            }
        }, 3000);//Amit:19.06.18. Changed the Done screen wait from 10 to 3 secs
    }
    //Amit 12.06.18 Marked Mobile No, Member ID, tt as final for Debugging
    public void AddVisitor(TypedString deviceId, final TypedString member_id, final TypedString name, final TypedString mobile_no, TypedString coming_from, TypedString purpose , TypedString visitor_count, final TypedString tt, TypedFile visitor_image ) {
        progress.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        try {
            //Amit 19.06.18 Changed the order. Put the Visitor Image to the end.
            RestClient.getApi().add_visitor_while_IN_without_OUT_validation_date(deviceId, member_id, name, mobile_no, coming_from, purpose , visitor_count , tt , new TypedString(Util.ReadSharePreference(context , Constant.USER_MOBILE_NO)), visitor_image, new Callback<Response>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void success(Response response, Response response2) {
                    try {

                        JSONObject jsonObject = new JSONObject(Util.getString(response.getBody().in()));

                        Gson gson = new Gson();
                        InSuccess confirm = gson.fromJson(jsonObject.toString(), InSuccess.class);

                        switch (confirm.getStatus()) {
                            case "true": {

                                progress.setVisibility(View.GONE);
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                Snackbar snackbar = Snackbar
                                        .make(llMain, confirm.getMessage(), Snackbar.LENGTH_LONG);
                                snackbar.show();

                                ArrayList<VisitorDB> visitorDBs = db.getAllVisitor();
                                Boolean flag = false;
                                for (int i = 0; i < visitorDBs.size(); i++) {
                                    if (Constant.MOBILE_NO.equals(visitorDBs.get(i).getMobileNo())) {
                                        flag = true;
                                        break;
                                    }
                                }

                                if (flag) {
                                    db.updateVisitor(Constant.NAME, Constant.MOBILE_NO, Constant.COMING_FROM, Constant.PURPOSE);
                                } else {
                                    db.insertVisitor(Constant.NAME, Constant.MOBILE_NO, Constant.COMING_FROM, Constant.PURPOSE);
                                }

                                File imageFile = new File(getRealPathFromURI(Constant.USER_IMAGE_PATH));
                                Bitmap realImage = BitmapFactory.decodeFile(imageFile.toString());
                                ByteArrayOutputStream b1 = new ByteArrayOutputStream();
                                realImage.compress(Bitmap.CompressFormat.JPEG, 100, b1);
                                byte[] b = b1.toByteArray();

                                String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);

                                for (int i=0 ; i<Constant.selectedMember.size(); i++){

                                    for (int j=0 ; j<confirm.getData().size() ; j++){
                                        if (Constant.selectedMember.get(i).getId().equalsIgnoreCase(confirm.getData().get(j).getMember_id())){
                                            VisitorOfflineDb visitorOfflineDb = new VisitorOfflineDb();
                                            visitorOfflineDb.setVisitor_record_id("");
                                            visitorOfflineDb.setName(Constant.NAME);
                                            visitorOfflineDb.setMember(Constant.selectedMember.get(i).getId());
                                            visitorOfflineDb.setMember_name(Constant.selectedMember.get(i).getName());
                                            visitorOfflineDb.setMobileNo(Constant.MOBILE_NO);
                                            visitorOfflineDb.setComingFrom(Constant.COMING_FROM);
                                            visitorOfflineDb.setPurpose(Constant.PURPOSE);
                                            visitorOfflineDb.setImage(encodedImage);
                                            visitorOfflineDb.setCount(String.valueOf(Constant.COUNT));
                                            visitorOfflineDb.setDate_time_in(time);
                                            visitorOfflineDb.setDate_time_out("");
                                            visitorOfflineDb.setIs_sync("true");
                                            visitorOfflineDb.setStatus(confirm.getData().get(j).getStatus());
                                            visitorOfflineDb.setReason(confirm.getData().get(j).getReason());

                                            db.insertVisitorOffline(visitorOfflineDb);
                                            //Amit: 19.06.18. Start Debugging
                                            Log.d("Util","Debug Upload ReviewActivity Success-> Mobile:"+Constant.MOBILE_NO +" Member ID:" + Constant.selectedMember.get(i).getId() + " Date:"+time);
                                            //Amit: 19.06.18. End Debugging

                                            break;
                                        }
                                    }

                                }

                                Constant.selectedMember = new ArrayList<>();
                                Constant.MOBILE_NO = "";
                                Constant.NAME = "";
                                Constant.COMING_FROM = "";
                                Constant.PURPOSE = "";
                                Constant.COUNT = 0;
                                Constant.USER_IMAGE_PATH = null;
                                Constant.faceDetect = false;

                                Calendar c = Calendar.getInstance();
                                @SuppressLint("SimpleDateFormat") SimpleDateFormat df1 = new SimpleDateFormat("dd-MMM-yyyy HH:mm");
                                String formattedDate1 = df1.format(c.getTime());

                                btnConfirm.setText("Done");
                                llVisitedOn.setVisibility(View.VISIBLE);
                                edtVisitedOn.setText(formattedDate1);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (!doneClick){
                                            homeClick = false;
                                            doneClick = true;
                                            Intent intent = new Intent(context, UserMainActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            finish();
                                            startActivity(intent);
                                            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                                        }
                                    }
                                }, 3000);//Amit:19.06.18. Changed the Done screen wait from 10 to 3 secs

                                break;
                            }
                            //Amit:19.06.18 16:17 Added below code to handle the duplicate scenario while adding visitors on server
                            //Moving to the Done screen
                            case "duplicate":
                                //Amit: 19.06.18. Start Debugging
                                Log.d("Util","Debug Upload ReviewActivity Duplicate-> Mobile:"+mobile_no +" Member ID:" + member_id + " Date:"+tt);
                                //Amit: 19.06.18. End Debugging

                                progress.setVisibility(View.GONE);
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                Snackbar snackbar = Snackbar
                                        .make(llMain, confirm.getMessage(), Snackbar.LENGTH_LONG);
                                snackbar.show();
                                Calendar c = Calendar.getInstance();
                                @SuppressLint("SimpleDateFormat") SimpleDateFormat df1 = new SimpleDateFormat("dd-MMM-yyyy HH:mm");
                                String formattedDate1 = df1.format(c.getTime());

                                btnConfirm.setText("Done");
                                llVisitedOn.setVisibility(View.VISIBLE);
                                edtVisitedOn.setText(formattedDate1);

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (!doneClick) {
                                            homeClick = false;
                                            doneClick = true;
                                            Intent intent = new Intent(context, UserMainActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            finish();
                                            startActivity(intent);
                                            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                                        }
                                    }
                                }, 3000); //Amit:19.06.18. Changed the Done screen wait from 10 to 3 secs

                                break;
                            //Amit:19.06.18 16:17 End of Addition

                            case "false": {
                                //Amit: 19.06.18. Start Debugging
                                Log.d("Util","Debug Upload ReviewActivity False-> Mobile:"+mobile_no +" Member ID:" + member_id + " Date:"+tt);
                                //Amit: 19.06.18. End Debugging

                                progress.setVisibility(View.GONE);
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                //Amit:19.06.18. Added the below line to get the current time before populating the offline database
                                time = Util.localeFormatedTime("yyyy-MM-dd HH:mm:ss");

                                setData();
                                break;
                            }
                            default:
                                progress.setVisibility(View.GONE);
                                Constant.selectedMember = new ArrayList<>();
                                Util.logout(ReviewActivity.this, llMain, confirm.getMessage());
                                break;
                        }
                    } catch (Exception e) {
                        progress.setVisibility(View.GONE);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                        e.printStackTrace();

                        //Amit:19.06.18 Commented the Message and called the load into offline database
                        //Snackbar snackbar = Snackbar
                        //      .make(llMain, "Something went wrong, please try again", Snackbar.LENGTH_LONG);
                        //snackbar.show();
                        //Amit: 19.06.18. Start Debugging
                        Log.d("Util","Debug Upload ReviewActivity Exception-> Mobile:"+mobile_no +" Member ID:" + member_id + " Date:"+tt);
                        //Amit: 19.06.18. End Debugging

                        setData();
                        //Amit:19.06.18 End of Change
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    progress.setVisibility(View.GONE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                    //Amit:19.06.18 Commented the Message and called the load into offline database
                    //Snackbar snackbar = Snackbar
                    //      .make(llMain, "Something went wrong, please try again", Snackbar.LENGTH_LONG);
                    //snackbar.show();
                    //Amit: 19.06.18. Start Debugging
                    Log.d("Util","Debug Upload ReviewActivity failure-> Mobile:"+mobile_no +" Member ID:" + member_id + " Date:"+tt);
                    //Amit: 19.06.18. End Debugging

                    setData();
                    //Amit:19.06.18 End of Change

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if (!btnConfirm.getText().toString().toLowerCase().equals("done")){
            homeClick = false;
            ToMeetActivity.adapter.notifyDataSetChanged();
            super.onBackPressed();
            overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
        } else {
            homeClick = false;
            doneClick = true;
            Intent intent = new Intent(context, UserMainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            finish();
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        }
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    boolean homeClick = true;
    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();


        if (homeClick) {
            doneClick = true;
            Intent intent = new Intent(context, OtpExitActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            try {
                pendingIntent.send(context, 0, intent);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

            } catch (PendingIntent.CanceledException e) {

                System.out.println("Sending contentIntent failed: ");
            }
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        homeClick = true;
        //Amit:19.06.18. Commented register/unregister of receiver. Will be handled in the main screen in Background thread
        //context.registerReceiver(receiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        registerReceiver(Util.broadcastReceiver, new IntentFilter(MyFirebaseMessagingService.BROADCAST_UPDATE_USER));
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Amit:19.06.18. Commented register/unregister of receiver. Will be handled in the main screen in Background thread
        //context.unregisterReceiver(receiver);
        unregisterReceiver(Util.broadcastReceiver);
    }

    //Amit:19.06.18. Need to delete this after testing
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(ConnectivityStatus.isConnected()){
                Util.uploadInVisitor(ReviewActivity.this , llMain);
            }
        }
    };

    //Amit 22.06.18. Added the timer for Confirm Button
    private void timer()
    {
        timer = new CountDownTimer(20000, 10000) {

            public void onTick(long millisUntilFinished) {
                //mTextField.setText("seconds remaining: " + millisUntilFinished / 1000);
                if(millisUntilFinished == 10000) {
                    Snackbar snackbar = Snackbar
                            .make(llMain, "Things are going slow today", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }

            }

            public void onFinish() {
                // mTextField.setText("done!");

                setData();
            }
        }.start();

    }

}
