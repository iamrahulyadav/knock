package com.proclivis.smartknock.Activity.User;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.proclivis.smartknock.Activity.OtpActivity;
import com.proclivis.smartknock.Activity.OtpExitActivity;
import com.proclivis.smartknock.Common.ConnectivityStatus;
import com.proclivis.smartknock.Common.Constant;
import com.proclivis.smartknock.Common.Util;
import com.proclivis.smartknock.DataBase.DatabaseHelper;
import com.proclivis.smartknock.FCM.MyFirebaseMessagingService;
import com.proclivis.smartknock.Model.ToMeet;
import com.proclivis.smartknock.Model.VisitorConfirm;
import com.proclivis.smartknock.R;
import com.proclivis.smartknock.Retrf.RestClient;
import com.proclivis.smartknock.Service.UploadService;


import org.json.JSONObject;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class UserMainActivity extends AppCompatActivity {

    LinearLayout toolbarMain, llSetting , llSettingView ;
    //Amit 19.06.18. Updated ll Main as Public static to be used in the receiver
    public static LinearLayout llMain;
    @SuppressLint("StaticFieldLeak")
    public static Context context;
    @SuppressLint("StaticFieldLeak")
    public static TextView tatMessage, txtUserName;
    public static de.hdodenhof.circleimageview.CircleImageView imgProfile1, imgProfile2;
    @SuppressLint("StaticFieldLeak")
    public static ImageView img1, img2;
    Button btnIn, btnOut, btnIn1, btnOut1, btnExpress, btnExpress1;
    @SuppressLint("StaticFieldLeak")
    public static LinearLayout llWithImage, llWithoutImage;
    @SuppressLint("StaticFieldLeak")
    public static LinearLayout llDrawer;
    @SuppressLint("StaticFieldLeak")
    public static TextView txtDrawerCount;
    @SuppressLint("StaticFieldLeak")
    public static LinearLayout llNoImage1, llNoImage2, llImage1, llImage2;
    @SuppressLint("StaticFieldLeak")
    public static DatabaseHelper db;
    @SuppressLint("StaticFieldLeak")
    public Intent serviceIntent;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        switch (getResources().getConfiguration().orientation) {
            case Configuration.ORIENTATION_PORTRAIT:
                setContentView(R.layout.user_main_activity_potrait);

                break;

            case Configuration.ORIENTATION_LANDSCAPE:
                setContentView(R.layout.user_main_activity_landscape);
        }

        homeClick = true;
        context = this;
        toolbarMain = findViewById(R.id.toolbar);
        txtDrawerCount = findViewById(R.id.txtDrawerCount);
        llMain = findViewById(R.id.llMain);
        llDrawer = findViewById(R.id.llDrawer);
        llSetting = findViewById(R.id.llSetting);
        llSettingView = findViewById(R.id.llSettingView);
        llSetting.setVisibility(View.GONE);
        llDrawer.setVisibility(View.VISIBLE);

        txtUserName = findViewById(R.id.txtUserName);
        tatMessage = findViewById(R.id.txtMessege);
        imgProfile1 = findViewById(R.id.imgProfile1);
        imgProfile2 = findViewById(R.id.imgProfile2);
        img1 = findViewById(R.id.img1);
        img2 = findViewById(R.id.img2);
        btnIn = findViewById(R.id.btnIn);
        btnOut = findViewById(R.id.btnOut);
        btnIn1 = findViewById(R.id.btnIn1);
        btnOut1 = findViewById(R.id.btnOut1);
        btnExpress = findViewById(R.id.btnExpress);
        btnExpress1 = findViewById(R.id.btnExpress1);
        llNoImage1 = findViewById(R.id.llNoImage1);
        llNoImage2 = findViewById(R.id.llNoImage2);
        llImage1 = findViewById(R.id.llImage1);
        llImage2 = findViewById(R.id.llImage2);
        llWithImage = findViewById(R.id.llWithImage);
        llWithoutImage = findViewById(R.id.llWithoutImage);
        txtUserName.setText(Util.ReadSharePreference(context, Constant.USER_NAME) + "!!!");
        db = new DatabaseHelper(context);

        if (Util.ReadSharePreference(context, Constant.USER_SCROLLING_MESSEGE).length() > 0) {
            tatMessage.setVisibility(View.VISIBLE);
            tatMessage.setText(Util.ReadSharePreference(context, Constant.USER_SCROLLING_MESSEGE));
        } else {
            tatMessage.setVisibility(View.GONE);
        }

        if (Util.ReadSharePreference(context , Constant.USER_PRO).equalsIgnoreCase("yes")){
            txtDrawerCount.setVisibility(View.VISIBLE);
            llDrawer.setVisibility(View.VISIBLE);
        } else {
            txtDrawerCount.setVisibility(View.GONE);
            llDrawer.setVisibility(View.GONE);
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                tatMessage.setSelected(true);

                tatMessage.setFocusable(true);
                tatMessage.setFocusableInTouchMode(true);
                tatMessage.setHorizontallyScrolling(true);
                tatMessage.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                tatMessage.setMarqueeRepeatLimit(-1);
            }
        }, 3000);

        checkImage();

        llDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                homeClick = false;
                Intent intent = new Intent(context, WaitingActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        });

        llSettingView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Snackbar snackbar = Snackbar
                        .make(llMain, "Setting enable", Snackbar.LENGTH_LONG);
                snackbar.show();
                llSetting.setVisibility(View.VISIBLE);
                return true;
            }
        });

        if (!Util.ReadSharePreference(context, Constant.IMAGE_UPLOADED_1).equalsIgnoreCase("")) {

            llImage1.setVisibility(View.VISIBLE);
            llNoImage1.setVisibility(View.GONE);
            byte[] b1 = Base64.decode(Util.ReadSharePreference(context, Constant.IMAGE_UPLOADED_1), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(b1, 0, b1.length);
            img1.setImageBitmap(bitmap);
        }

        if (!Util.ReadSharePreference(context, Constant.IMAGE_UPLOADED_2).equalsIgnoreCase("")) {

            llImage2.setVisibility(View.VISIBLE);
            llNoImage2.setVisibility(View.GONE);

            byte[] b1 = Base64.decode(Util.ReadSharePreference(context, Constant.IMAGE_UPLOADED_2), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(b1, 0, b1.length);
            img2.setImageBitmap(bitmap);
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

        btnIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                inDialog();
            }
        });

        btnIn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inDialog();
            }
        });

        btnExpress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                homeClick = false;
                Intent intent = new Intent(context, ExpressInActivity.class);
                startActivity(intent);
                Constant.USER_IMAGE_PATH = null;
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        });

        btnExpress1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                homeClick = false;
                Intent intent = new Intent(context, ExpressInActivity.class);
                startActivity(intent);
                Constant.USER_IMAGE_PATH = null;
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        });

        btnOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                homeClick = false;
                Intent intent = new Intent(context, GetVisitorActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        });

        btnOut1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                homeClick = false;
                Intent intent = new Intent(context, GetVisitorActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        });

        llSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                homeClick = false;
                Intent intent = new Intent(context, OtpActivity.class);
                intent.putExtra("type", "main");
                intent.putExtra("mobile", Util.ReadSharePreference(context, Constant.USER_MOBILE_NO));
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        });


    }

    public void inDialog() {

        homeClick = false;
        Constant.MOBILE_NO = "";
        Constant.NAME = "";
        Constant.COMING_FROM = "";
        Constant.PURPOSE = "";
        Constant.COUNT = 0;
        Constant.selectedMember = new ArrayList<>();
        Constant.USER_IMAGE_PATH = null;

        Intent intent = new Intent(context, VisitorDetailActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    public static void checkImage() {
        if (Util.ReadSharePreference(context, Constant.IMAGE_UPLOADED_1).equalsIgnoreCase("")
                && Util.ReadSharePreference(context, Constant.IMAGE_UPLOADED_2).equalsIgnoreCase("")) {

            llWithImage.setVisibility(View.GONE);
            llWithoutImage.setVisibility(View.VISIBLE);
        } else {
            llWithImage.setVisibility(View.VISIBLE);
            llWithoutImage.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {

        if (homeClick) {
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

    public static boolean homeClick = true;
    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();

        if (homeClick) {
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

        //Amit 19.06.18. Added Intent filters to programmatically broadcast
        IntentFilter connectivityChange = new IntentFilter();
        connectivityChange.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        connectivityChange.addAction("com.vms.knockknock.UploadBroadcast");
        registerReceiver(receiver, connectivityChange);

        registerReceiver(Util.broadcastReceiver, new IntentFilter(MyFirebaseMessagingService.BROADCAST_UPDATE_USER));
        registerReceiver(broadcastReceiver, new IntentFilter(MyFirebaseMessagingService.BROADCAST_USER_PRO));

        //Amit 19.06.18 Check if Offline records exists. If yes start Service
        if(Util.offlineRecordsExists(this)){
            Log.d("UserMainActivity","Starting Service");
            serviceIntent = new Intent(context, UploadService.class);
            startService(serviceIntent);
        }

        if (Util.ReadSharePreference(context , Constant.USER_PRO).equalsIgnoreCase("yes")){
            llDrawer.setVisibility(View.VISIBLE);
            int count = Util.ReadSharePreferenceInt(context , Constant.DRAWER_COUNT);
            if (count!=0){
                txtDrawerCount.setVisibility(View.VISIBLE);
                if (count>9){
                    txtDrawerCount.setText(String.format(" %s ", String.valueOf(count)));
                } else {
                    txtDrawerCount.setText(String.format("  %s  ", String.valueOf(count)));
                }
            } else {
                txtDrawerCount.setVisibility(View.GONE);
            }
        } else {
            llDrawer.setVisibility(View.GONE);
        }

        homeClick = true;
        llSetting.setVisibility(View.GONE);
        //Amit 19.06.18. Commented since this is registered above too
        //context.registerReceiver(receiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceiver(receiver);

        unregisterReceiver(broadcastReceiver);
        unregisterReceiver(Util.broadcastReceiver);
        //Amit 19.06.18. Stopping the service since this should only run on main screen
        if(isMyServiceRunning(UploadService.class))
            stopService(serviceIntent);


    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        //Amit:19.06.18. Set Context to final since it is used in inner class
        public void onReceive(final Context context, Intent intent) {
            //Amit:19.06.18. Commented below since network check should nothappen onmain thread
            //if(ConnectivityStatus.isConnected(context)){
              //  homeClick = false;
                //Amit:19.06.18. Added AsyncTask to Upload Visitor records, get Visitor Detail Records and Get Member Records in case the Broadcast receiver is called
                new AsyncTask<Object, Void, String>(){
                    @Override
                    protected String doInBackground(Object... params) {
                        if (ConnectivityStatus.isConnected()) {
                            homeClick = false;

                            //new Handler().postDelayed(new Runnable() {
                            //     @Override
                            //    public void run() {
                            //Amit 19.06.18. Check id Uploading of Visitors already in progress
                            if(!Util.uploadInProgress) {
                                Util.uploadInVisitor(UserMainActivity.this, llMain);
                                //Amit 19.06.18.Added below code tostop service if no offline records exists
                                if (!Util.isUpload) {
                                    //Amit 19.06.18. Stopping the service since this should only run on main screen
                                    if(isMyServiceRunning(UploadService.class))
                                        stopService(serviceIntent);
                                }
                            }
                            if (Util.ReadSharePreference(context, Constant.IS_VISITOR_FIRST_TIME).equals("yes") && Util.isOnline(context)) {
                                getVisitorDetailData();
                            } else if (!Util.ReadSharePreference(context, Constant.VISITOR_API_CALL_DATE).equals(Util.getCurrentDateTime()) && Util.isOnline(context))
                                getVisitorDetailData();
                            if (Util.ReadSharePreference(context, Constant.IS_MEMBER_FIRST_TIME).equals("true") && Util.isOnline(context)) {
                                getMemberData();
                            } else if (!Util.ReadSharePreference(context, Constant.MEMBER_API_CALL_DATE).equals(Util.getCurrentDateTime()) && Util.isOnline(context)) {
                                getMemberData();
                            }
                            // }
                            //},60000);
                        }
                        return null;
                    }
                }.execute();
                //Amit:19.06.18. End of change

            ////Amit:19.06.18. Commented the below code since this handled in the background task
            //Util.uploadInVisitor(UserMainActivity.this , llMain);
            }
        //}
    };

    //Amit 19.06.18. Check if theservice is already running
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("isMyServiceRunning?", true+"");
                return true;
            }
        }
        Log.i ("isMyServiceRunning?", false+"");
        return false;
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Util.ReadSharePreference(context, Constant.USER_PRO).equalsIgnoreCase("yes")){
                llDrawer.setVisibility(View.VISIBLE);
            } else {
                llDrawer.setVisibility(View.GONE);
            }
        }
    };

    //Amit:19.06.18. Added below functions to get Visitor Detail Data in Background process from Broadcast Receiver
    private void getVisitorDetailData() {

        try {
            RestClient.getApi().get_visitors_customer_wise1(Util.ReadSharePreference(context, Constant.USER_ID),
                    new Callback<Response>() {
                        @Override
                        public void success(Response response, Response response2) {
                            try {

                                JSONObject jsonObject = new JSONObject(Util.getString(response.getBody().in()));

                                Gson gson = new Gson();
                                VisitorConfirm confirm = gson.fromJson(jsonObject.toString(), VisitorConfirm.class);
                                switch (confirm.getStatus()) {
                                    case "true": {

                                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                        db.insertVisitorsDB(confirm.getData());

                                        Util.WriteSharePreference(context, Constant.IS_VISITOR_FIRST_TIME, "no");
                                        Util.WriteSharePreference(context, Constant.VISITOR_API_CALL_DATE, Util.getCurrentDateTime());

                                        break;
                                    }
                                    default:

                                        Util.logout(UserMainActivity.this, llMain, confirm.getMessage());
                                        break;
                                }


                            } catch (Exception e) {
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                Log.e("errorGetMember==>", e.toString());
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            Log.e("errorGetMember==>", error.toString());
                        }
                    });
        } catch (
                Exception e)

        {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            Log.e("errorGetMember==>", e.toString());
        }

    }

    //Amit:19.06.18. Added below function to get Member Data in Background process from Broadcast Receiver
    private void getMemberData() {

        try {
            RestClient.getApi().get_members_list_by_customer_id1(Util.ReadSharePreference(context, Constant.USER_ID),
                    new Callback<Response>() {
                        @Override
                        public void success(Response response, Response response2) {
                            try {
                                JSONObject jsonObject = new JSONObject(Util.getString(response.getBody().in()));

                                Gson gson = new Gson();
                                ToMeet confirm = gson.fromJson(jsonObject.toString(), ToMeet.class);

                                switch (confirm.getStatus()) {
                                    case "true": {

                                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                        db.insertMemberDB(confirm.getData());

                                        Util.WriteSharePreference(context, Constant.IS_MEMBER_FIRST_TIME, "false");
                                        Util.WriteSharePreference(context, Constant.MEMBER_API_CALL_DATE, Util.getCurrentDateTime());

                                        break;
                                    }
                                    default:
                                        Util.logout(UserMainActivity.this, llMain, confirm.getMessage());
                                        break;
                                }


                            } catch (Exception e) {
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                Log.e("errorGetMember==>", e.toString());
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            Log.e("errorGetMember==>", error.toString());
                        }
                    });
        } catch (Exception e) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            Log.e("errorGetMember==>", e.toString());
        }

    }

}