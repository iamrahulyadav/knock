package com.proclivis.smartknock.Activity.Member;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.proclivis.smartknock.Adapter.MemberVisitorPagerAdapter;
import com.proclivis.smartknock.Common.Constant;
import com.proclivis.smartknock.Common.Util;
import com.proclivis.smartknock.DataBase.DatabaseHelper;
import com.proclivis.smartknock.FCM.MyFirebaseMessagingService;
import com.proclivis.smartknock.Model.GetMemberVisitor;
import com.proclivis.smartknock.Model.GetMemberVisitorVo;
import com.proclivis.smartknock.Model.MemberDetail;
import com.proclivis.smartknock.R;
import com.proclivis.smartknock.Retrf.RestClient;

import org.json.JSONObject;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MemberPagerActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    Toolbar toolbar;
    Context context;
    Activity activity;
    ViewPager viewPager;
    RelativeLayout  llWarning, llPager;
    @SuppressLint("StaticFieldLeak")
    public static MemberVisitorPagerAdapter adapter;
    @SuppressLint("StaticFieldLeak")
    public static RelativeLayout progress , llMain;
    @SuppressLint("StaticFieldLeak")
    public static ArrayList<GetMemberVisitorVo> getVisitorVos = new ArrayList<>();
    ArrayList<MemberDetail> memberDetails = new ArrayList<>();
    GetMemberVisitorVo selectedData = new GetMemberVisitorVo();
    String clicked = "no";
    int position = 0;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.member_pager_activity);
        context = this;
        activity = this;
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Visitor Details");
        setSupportActionBar(toolbar);
        if (getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        registerReceiver(broadcastReceiver, new IntentFilter(MyFirebaseMessagingService.BROADCAST_UPDATE_MEMBER_MAIN));

        llMain = findViewById(R.id.llMain);
        llWarning = findViewById(R.id.llWarning);
        llPager = findViewById(R.id.llPager);
        progress = findViewById(R.id.progress);

        viewPager = findViewById(R.id.viewPager);
        db = new DatabaseHelper(this);

        adapter = new MemberVisitorPagerAdapter();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            clicked = extras.getString("clicked");
        }

        if (clicked != null && clicked.equals("no")) {
            NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            nMgr.cancelAll();
            Constant.VISITOR_POS = getIntent().getStringExtra("message");
            getData();
        }

        Bundle extras1 = getIntent().getExtras();
        if (extras1 == null) {
            getVisitorVos = null;
            position = 0;
        } else {
            getVisitorVos = Constant.getMemberVisitorVo;
            memberDetails = Constant.memberDetails;

            assert extras != null;
            position = extras.getInt("position");
            selectedData = extras.getParcelable("selectedData");

            llWarning.setVisibility(View.GONE);
            llPager.setVisibility(View.VISIBLE);
            adapter = new MemberVisitorPagerAdapter(context , activity , getVisitorVos, memberDetails);
            viewPager.setAdapter(adapter);

            viewPager.setCurrentItem(position);

        }
    }

    public void getData() {

        if (Util.isOnline(context)) {
            getVisitors();
        } else {
            final Snackbar snackbar = Snackbar
                    .make(llMain, "No internet connection", Snackbar.LENGTH_LONG)
                    .setAction("Retry", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            getData();
                        }
                    });
            snackbar.setActionTextColor(Color.RED);
            snackbar.show();
        }
    }

    public void getVisitors() {

        progress.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        final String flagValue;

        if (Util.ReadSharePreference(context, Constant.IS_MEMBER_DATABASE_FIRST_TIME).equals("")) {
            flagValue = "";
        } else {
            flagValue = "flag";
        }
        try {
            RestClient.getApi().get_visitors_records_by_member_number1(Util.ReadSharePreference(context, Constant.MEMBER_MOBILE_NO), flagValue,
                    new Callback<Response>() {
                        @Override
                        public void success(Response response, Response response2) {
                            try {

                                JSONObject jsonObject = new JSONObject(Util.getString(response.getBody().in()));
                                Gson gson = new Gson();
                                GetMemberVisitor getVisitor = gson.fromJson(jsonObject.toString(), GetMemberVisitor.class);

                                switch (getVisitor.getStatus()) {
                                    case "true":


                                        db.addAllMemberSoc(getVisitor.getMemberDetails());

                                        Util.WriteSharePreference(context, Constant.IS_MEMBER_DATABASE_FIRST_TIME, "flag");

                                        if (getVisitor.getData().size() > 0) {
                                            llWarning.setVisibility(View.GONE);
                                            llPager.setVisibility(View.VISIBLE);
                                        } else {
                                            llWarning.setVisibility(View.VISIBLE);
                                            llPager.setVisibility(View.GONE);
                                        }
                                        if (getVisitor.getMemberDetails().size() > 0) {
                                            //memberDetails = getVisitor.getMemberDetails();
                                            memberDetails = db.getMemberSoc();
                                            Constant.memberDetails = memberDetails;
                                        }
                                        progress.setVisibility(View.GONE);
                                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                        //getVisitorVos = getVisitor.getData();
                                        getVisitorVos = db.getMemberVisitor();
                                        ArrayList<GetMemberVisitorVo> newData;
                                        newData = new ArrayList<>();
                                        for (int i = 0; i < getVisitorVos.size(); i++) {

                                            if (getVisitorVos.get(i).getVistorRecordId() != null) {
                                                newData.add(getVisitorVos.get(i));
                                            }
                                        }

                                        getVisitorVos = newData;
                                        if (flagValue.equals("")) {
                                            db.addAllMemberVisitor(getVisitor.getData());
                                        } else {
                                            getVisitorVos = db.getMemberVisitor();
                                            db.addNewMemberVisitor(getVisitor.getData(), getVisitorVos);

                                            getVisitorVos = db.getMemberVisitor();
                                        }
                                        Constant.getMemberVisitorVo = getVisitorVos;
                                        for (int i = 0; i < getVisitorVos.size(); i++) {
                                            String dateIn = Util.converter(getVisitorVos.get(i).getVisitorDateTimeIn(), "yyyy-MM-dd HH:mm:ss", "dd-MMM-yyyy HH:mm");
                                            getVisitorVos.get(i).setVisitorDateTimeIn(dateIn);
                                        }
                                        viewPager.setAdapter(new MemberVisitorPagerAdapter(context , activity , getVisitorVos, memberDetails));
                                        if (Constant.VISITOR_POS != null) {
                                            for (int i = 0; i < getVisitorVos.size(); i++) {
                                                if (Constant.VISITOR_POS.equals(getVisitorVos.get(i).getVistorRecordId())) {
                                                    viewPager.setCurrentItem(i);
                                                    break;
                                                }
                                            }
                                        }
                                        break;
                                    case "false":
                                        llWarning.setVisibility(View.VISIBLE);
                                        llPager.setVisibility(View.GONE);
                                        progress.setVisibility(View.GONE);
                                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                        Snackbar snackbar = Snackbar
                                                .make(llMain, getVisitor.getMessage(), Snackbar.LENGTH_LONG);
                                        snackbar.show();
                                        break;
                                    default:
                                        llWarning.setVisibility(View.VISIBLE);
                                        llPager.setVisibility(View.GONE);
                                        progress.setVisibility(View.GONE);
                                        Util.logout(MemberPagerActivity.this, llMain, getVisitor.getMessage());
                                        break;
                                }

                            } catch (Exception e) {
                                llWarning.setVisibility(View.VISIBLE);
                                llPager.setVisibility(View.GONE);
                                progress.setVisibility(View.GONE);
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                e.printStackTrace();
                                Log.e("LoginRetrofit Error==>", e.toString());
                                Snackbar snackbar = Snackbar
                                        .make(llMain, "Something went wrong, please try again", Snackbar.LENGTH_LONG);
                                snackbar.show();
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            llWarning.setVisibility(View.VISIBLE);
                            llPager.setVisibility(View.GONE);
                            progress.setVisibility(View.GONE);
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            Log.e("LoginRetrofit Error==>", error.toString());
                            Snackbar snackbar = Snackbar.make(llMain, "Something went wrong, please try again", Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onRefresh() {
        if (Util.isOnline(context)) {
            getVisitors();
        } else {
            final Snackbar snackbar = Snackbar
                    .make(llMain, "No internet connection", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Retry", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            getData();
                        }
                    });
            snackbar.setActionTextColor(Color.RED);
            snackbar.show();
        }
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            getData();
        }
    };

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }
}