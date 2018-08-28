package com.proclivis.smartknock.Activity.User;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.proclivis.smartknock.Activity.OtpExitActivity;
import com.proclivis.smartknock.Common.ConnectivityStatus;
import com.proclivis.smartknock.Common.Constant;
import com.proclivis.smartknock.Common.Util;
import com.proclivis.smartknock.DataBase.ExpressVisitorOfflineDb;
import com.proclivis.smartknock.FCM.MyFirebaseMessagingService;
import com.proclivis.smartknock.Model.VisitorConfirmVo;
import com.proclivis.smartknock.R;


import java.util.ArrayList;

public class ExpressConfirmActivity extends AppCompatActivity {

    Context context;
    LinearLayout llHome ;
    de.hdodenhof.circleimageview.CircleImageView imgProfile;
    TextView txtName , txtComingFrom , txtPurpose ,txtMobile ,txtRegisterOn;
    Button btnConfirm;
    String time;
    String mode;
    private Boolean doneClick = false;
    ArrayList<VisitorConfirmVo> visitorConfirmVos;
    RelativeLayout llMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        switch (getResources().getConfiguration().orientation) {
            case Configuration.ORIENTATION_PORTRAIT:
                setContentView(R.layout.express_confirm_activity_potrait);

                break;

            case Configuration.ORIENTATION_LANDSCAPE:
                setContentView(R.layout.express_confirm_activity_landscape);
        }

        context = this;
        llHome =  findViewById(R.id.llHome);
        llMain =  findViewById(R.id.llMain);
        imgProfile =  findViewById(R.id.imgProfile);
        txtName =  findViewById(R.id.txtName);
        txtComingFrom =  findViewById(R.id.txtComingFrom);
        txtPurpose =  findViewById(R.id.txtPurpose);
        txtMobile =  findViewById(R.id.txtMobile);
        txtRegisterOn =  findViewById(R.id.txtRegisterOn);
        btnConfirm =  findViewById(R.id.btnConfirm);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            visitorConfirmVos = null;
            time = "";
        } else {
            mode =  extras.getString("mode");
            time = extras.getString("time");
            if (mode.equals("online")){
                visitorConfirmVos = extras.getParcelableArrayList("data");

                assert visitorConfirmVos != null;
                txtName.setText(visitorConfirmVos.get(0).getName());
                txtComingFrom.setText(visitorConfirmVos.get(0).getComingFrom());
                txtPurpose.setText(visitorConfirmVos.get(0).getPurpose());
                txtMobile.setText(visitorConfirmVos.get(0).getMobileNo());
                txtRegisterOn.setText(time);
                Glide.with(this)
                        .load(Constant.USER_IMAGE_PATH)
                        .into(imgProfile);
                imgProfile.setImageURI(Constant.USER_IMAGE_PATH);

            } else {


                txtName.setText(Constant.expressVisitorOfflineDbs.getName());
                txtComingFrom.setText(Constant.expressVisitorOfflineDbs.getComingFrom());
                txtPurpose.setText(Constant.expressVisitorOfflineDbs.getPurpose());
                txtMobile.setText(Constant.expressVisitorOfflineDbs.getMobileNo());
                txtRegisterOn.setText(time);

                byte[] b1 = Base64.decode(Constant.expressVisitorOfflineDbs.getImage(), Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(b1, 0, b1.length);
                imgProfile.setImageBitmap(bitmap);

            }
        }






        llHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (!doneClick){
                    doneClick = true;
                    homeClick = false;
                    Constant.USER_IMAGE_PATH = null;
                    Constant.faceDetect = false;
                    Constant.expressVisitorOfflineDbs = new ExpressVisitorOfflineDb();

                    Intent intent = new Intent(context, UserMainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                }

            }
        }, 3000); //Amit:19.06.18. Updated the time from 10 sec to 3 sec
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        doneClick=true;
        homeClick = false;
        Constant.USER_IMAGE_PATH = null;
        Constant.faceDetect = false;
        Constant.expressVisitorOfflineDbs = new ExpressVisitorOfflineDb();

        Intent intent = new Intent(context, UserMainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    boolean homeClick = true;
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

                System.out.println("Sending contentIntent failed");
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        homeClick = true;
        //Amit:19.06.18. Commented the regitration/unregister of the Broadcast receiver since this will be handled in the main activity in background thread
        //context.registerReceiver(receiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        registerReceiver(Util.broadcastReceiver, new IntentFilter(MyFirebaseMessagingService.BROADCAST_UPDATE_USER));
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Amit:19.06.18. Commented the regitration/unregister of the Broadcast receiver since this will be handled in the main activity in background thread
        //context.unregisterReceiver(receiver);
        unregisterReceiver(Util.broadcastReceiver);
    }


    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(ConnectivityStatus.isConnected()){
                Util.uploadInVisitor(ExpressConfirmActivity.this , llMain);
            }
        }
    };
}