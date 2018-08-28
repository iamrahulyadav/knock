package com.proclivis.smartknock.Activity.User;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;


import com.proclivis.smartknock.Activity.OtpExitActivity;
import com.proclivis.smartknock.Adapter.OfflineVisitorVerifyDoneAdapter;
import com.proclivis.smartknock.Adapter.VisitorVerifyDoneAdapter;
import com.proclivis.smartknock.Common.ConnectivityStatus;
import com.proclivis.smartknock.Common.Constant;
import com.proclivis.smartknock.Common.Util;
import com.proclivis.smartknock.FCM.MyFirebaseMessagingService;
import com.proclivis.smartknock.Model.VisitorVerifyDoneVo;
import com.proclivis.smartknock.R;

import java.util.ArrayList;

public class VisitorVerifyDoneActivity extends AppCompatActivity {

    LinearLayout toolbar, llBack ,llHome;
    Context context;
    Activity activity;

    LinearLayout llLeft;
    ViewPager viewPager;
    ArrayList<VisitorVerifyDoneVo> getVisitorVos = new ArrayList<>();
    RelativeLayout progress, llMain;
    String mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.visitor_verify_done_activity);

        context = this;
        activity = this;

        toolbar = findViewById(R.id.toolbar);
        llBack = findViewById(R.id.llBack);
        llHome = findViewById(R.id.llHome);
        viewPager = findViewById(R.id.viewPager);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            getVisitorVos = null;

        } else {
            mode = extras.getString("mode");
            assert mode != null;
            if (mode.equals("online")){
                getVisitorVos = extras.getParcelableArrayList("data");
                assert getVisitorVos != null;
                viewPager.setAdapter(new VisitorVerifyDoneAdapter(context, activity , getVisitorVos));
            } else {
                viewPager.setAdapter(new OfflineVisitorVerifyDoneAdapter(context, activity ,  Constant.getVisitorVo));
            }
        }

        llLeft = findViewById(R.id.llLeft);
        progress = findViewById(R.id.progress);
        llMain = findViewById(R.id.llMain);

        llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        llHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                homeClick=false;
                Constant.getVisitorVo = new ArrayList<>();
                Intent intent = new Intent(context , UserMainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
            }
        });
    }

    @Override
    public void onBackPressed() {
        homeClick=false;
        Constant.getVisitorVo = new ArrayList<>();
        Intent intent = new Intent(context, UserMainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    public static boolean homeClick = true;
    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();

        if (homeClick) {
            homeClick = false;
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

    //Amit:19.06.18. Need to delete this after testing
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(ConnectivityStatus.isConnected()){
                Util.uploadInVisitor(VisitorVerifyDoneActivity.this , llMain);
            }
        }
    };
}
