package com.proclivis.smartknock.Activity.User;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;


import com.proclivis.smartknock.Adapter.WaitingPagerAdapter;
import com.proclivis.smartknock.Common.Constant;
import com.proclivis.smartknock.DataBase.VisitorOfflineDb;
import com.proclivis.smartknock.R;

import java.util.ArrayList;

public class WaitingDetailActivity extends AppCompatActivity {

    Toolbar toolbar;
    Context context;
    Activity activity;
    ViewPager viewPager;
    RelativeLayout llWarning, llPager;
    @SuppressLint("StaticFieldLeak")
    public static RelativeLayout llMain , progress;


    ArrayList<VisitorOfflineDb> getVisitorVos = new ArrayList<>();

    String clicked = "no";
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.waiting_detail_activity);
        context = this;
        activity = this;
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Visitor Details");
        setSupportActionBar(toolbar);
        if (getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        llMain = findViewById(R.id.llMain);
        llWarning = findViewById(R.id.llWarning);
        llPager = findViewById(R.id.llPager);
        progress = findViewById(R.id.progress);

        viewPager = findViewById(R.id.viewPager);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            clicked = extras.getString("clicked");
        }

        Bundle extras1 = getIntent().getExtras();
        if (extras1 == null) {
            getVisitorVos = null;
            position = 0;
        } else {

            getVisitorVos = Constant.waiting;
            position = extras1.getInt("position");

            llWarning.setVisibility(View.GONE);
            llPager.setVisibility(View.VISIBLE);
            viewPager.setAdapter(new WaitingPagerAdapter(context , activity, getVisitorVos));

            viewPager.setCurrentItem(position);

        }
    }

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