package com.proclivis.smartknock.Activity.User;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.proclivis.smartknock.Adapter.WaitingAdapter;
import com.proclivis.smartknock.Common.Constant;
import com.proclivis.smartknock.Common.Util;
import com.proclivis.smartknock.DataBase.DatabaseHelper;
import com.proclivis.smartknock.DataBase.VisitorOfflineDb;
import com.proclivis.smartknock.FCM.MyFirebaseMessagingService;
import com.proclivis.smartknock.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class WaitingActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, WaitingAdapter.ClickListener {

    Toolbar toolbar;
    Context context;
    Activity activity;

    @SuppressLint("StaticFieldLeak")
    public static EditText edtSearch;
    @SuppressLint("StaticFieldLeak")
    public static LinearLayout llError;
    @SuppressLint("StaticFieldLeak")
    public static TextView txtResult;
    @SuppressLint("StaticFieldLeak")
    public static RecyclerView recycler;

    SwipeRefreshLayout swipeRefreshRecycler;
    RelativeLayout llMain, progress;

    DatabaseHelper db;
    WaitingAdapter adapter;
    ArrayList<VisitorOfflineDb> getVisitorVo = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.waiting_activity);
        context = this;
        activity = this;
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Visitor Summary");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Util.WriteSharePreferenceInt(context, Constant.DRAWER_COUNT, 0);
        edtSearch = findViewById(R.id.edtSearch);
        txtResult = findViewById(R.id.txtResult);
        swipeRefreshRecycler = findViewById(R.id.swipeRefreshRecycler);
        recycler = findViewById(R.id.recycler);
        llError = findViewById(R.id.llError);
        llMain = findViewById(R.id.llMain);
        progress = findViewById(R.id.progress);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recycler.setLayoutManager(mLayoutManager);
        recycler.setItemAnimator(new DefaultItemAnimator());
        adapter = new WaitingAdapter();
        adapter.setOnItemClickListener(this);

        edtSearch.setVisibility(View.GONE);
        txtResult.setVisibility(View.GONE);
        recycler.setVisibility(View.GONE);
        llError.setVisibility(View.GONE);

        swipeRefreshRecycler.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshRecycler.setRefreshing(true);
                getData();
            }
        });

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                adapter.getFilter().filter(charSequence.toString());

                if (edtSearch.getText().length() > 0) {
                    txtResult.setVisibility(View.VISIBLE);
                } else {
                    txtResult.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    public void onRefresh() {
        swipeRefreshRecycler.setRefreshing(true);

    }

    BroadcastReceiver tickReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null) {
                if (intent.getAction().compareTo(Intent.ACTION_TIME_TICK) == 0) {
                    adapter.notifyDataSetChanged();
                }
            }
        }
    };

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            getData();
        }
    };

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(context, WaitingDetailActivity.class);
        intent.putExtra("clicked", "true");
        intent.putExtra("position", position);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    private void getData() {

        getVisitorVo = new ArrayList<>();
        db = new DatabaseHelper(context);

        int n = 0;
        ArrayList<VisitorOfflineDb> getVisitorVo1 = db.getAllOfflineVisitor();
        for (int i = 0; i < getVisitorVo1.size(); i++) {
            if (getVisitorVo1.get(i).getDate_time_out().equals("")) {
                getVisitorVo.add(n, getVisitorVo1.get(i));
                n++;
            }
        }
        Collections.sort(getVisitorVo, new Comparator<VisitorOfflineDb>() {
            @Override
            public int compare(VisitorOfflineDb item, VisitorOfflineDb t1) {
                String s1 = item.getDate_time_in();
                String s2 = t1.getDate_time_in();
                return s1.compareToIgnoreCase(s2);
            }
        });

        Collections.reverse(getVisitorVo);
        Constant.waiting = getVisitorVo;
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        if (getVisitorVo.size() > 0) {

            edtSearch.setVisibility(View.VISIBLE);
            edtSearch.setHint("Search through all " + getVisitorVo.size() + " visits");
            edtSearch.setText("");
            txtResult.setVisibility(View.GONE);

            llError.setVisibility(View.GONE);
            recycler.setVisibility(View.VISIBLE);

            for (int i = 0; i < getVisitorVo.size(); i++) {
                String dateIn = Util.converter(getVisitorVo.get(i).getDate_time_in(), "yyyy-MM-dd HH:mm:ss", "dd-MMM-yyyy HH:mm");
                getVisitorVo.get(i).setDate_time_in(dateIn);
            }
            adapter = new WaitingAdapter(context, getVisitorVo);
            recycler.setAdapter(adapter);
        } else {
            llError.setVisibility(View.VISIBLE);
            recycler.setVisibility(View.GONE);
        }

        swipeRefreshRecycler.setRefreshing(false);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
        registerReceiver(broadcastReceiver, new IntentFilter(MyFirebaseMessagingService.BROADCAST_UPDATE_USER));
        registerReceiver(tickReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
        unregisterReceiver(tickReceiver);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(context , UserMainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }
}