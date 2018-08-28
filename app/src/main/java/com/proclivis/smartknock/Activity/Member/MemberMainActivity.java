package com.proclivis.smartknock.Activity.Member;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.proclivis.smartknock.Adapter.MemberVisitorRecyclerAdapter;
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

public class MemberMainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, MemberVisitorRecyclerAdapter.ClickListener {

    @SuppressLint("StaticFieldLeak")
    public static Context context;
    @SuppressLint("StaticFieldLeak")
    public static RecyclerView recycler;
    @SuppressLint("StaticFieldLeak")
    public static MemberVisitorRecyclerAdapter adapter;
    @SuppressLint("StaticFieldLeak")
    public static RelativeLayout llMain, llWarning, llRecycler, progress;
    @SuppressLint("StaticFieldLeak")
    public static SwipeRefreshLayout swipeRefreshRecycler;
    public static ArrayList<GetMemberVisitorVo> getVisitorVo;
    public static ArrayList<MemberDetail> memberDetails;
    @SuppressLint("StaticFieldLeak")
    public static EditText edtSearch;
    @SuppressLint("StaticFieldLeak")
    public static LinearLayout llError;
    @SuppressLint("StaticFieldLeak")
    public static TextView txtResult;
    public static DatabaseHelper db;
    Toolbar toolbar;
    TextView txtSkip1, txtSkip2, txtSkip3, txtSkip4;
    TextView txtNext1, txtNext2, txtNext3, txtNext4;
    RelativeLayout llHelp, llHelp1, llHelp2, llHelp3, llHelp4;
    String clicked = "no";
    Activity activity;
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
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            getData();
            getVisitors();

        }
    };

    public static void setOfflineData() {
        memberDetails = db.getMemberSoc();
        Constant.memberDetails = memberDetails;
        getVisitorVo = db.getMemberVisitor();
        Constant.getMemberVisitorVo = getVisitorVo;

        if (getVisitorVo.size() > 0) {
            swipeRefreshRecycler.setRefreshing(false);
            ArrayList<GetMemberVisitorVo> newData = new ArrayList<>();
            for (int i = 0; i < getVisitorVo.size(); i++) {
                if (getVisitorVo.get(i).getVistorRecordId() != null) {
                    newData.add(getVisitorVo.get(i));
                }
            }
            getVisitorVo = newData;
            Constant.getMemberVisitorVo = getVisitorVo;
            if (getVisitorVo.size() > 0) {
                edtSearch.setHint("Search through all " + getVisitorVo.size() + " visits");
                edtSearch.setText("");
                txtResult.setVisibility(View.GONE);

                llWarning.setVisibility(View.GONE);
                llRecycler.setVisibility(View.VISIBLE);

                for (int i = 0; i < getVisitorVo.size(); i++) {
                    String dateIn = Util.converter(getVisitorVo.get(i).getVisitorDateTimeIn(), "yyyy-MM-dd HH:mm:ss", "dd-MMM-yyyy HH:mm");
                    getVisitorVo.get(i).setVisitorDateTimeIn(dateIn);
                }
                adapter = new MemberVisitorRecyclerAdapter(context, getVisitorVo, memberDetails);
                recycler.setAdapter(adapter);
            } else {
                llWarning.setVisibility(View.VISIBLE);
                llRecycler.setVisibility(View.GONE);
                swipeRefreshRecycler.setRefreshing(false);
                Snackbar snackbar = Snackbar
                        .make(llMain, "No data found", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        switch (getResources().getConfiguration().orientation) {
            case Configuration.ORIENTATION_PORTRAIT:
                setContentView(R.layout.mamber_main_activity_potrait);
                break;
            case Configuration.ORIENTATION_LANDSCAPE:
                setContentView(R.layout.mamber_main_activity_landscape);
        }
        context = this;
        activity = this;
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        registerReceiver(broadcastReceiver, new IntentFilter(MyFirebaseMessagingService.BROADCAST_UPDATE_MEMBER_MAIN));
        registerReceiver(tickReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));

        db = new DatabaseHelper(this);

        llMain = findViewById(R.id.llMain);
        llWarning = findViewById(R.id.llWarning);
        llRecycler = findViewById(R.id.llRecycler);
        progress = findViewById(R.id.progress);
        edtSearch = findViewById(R.id.edtSearch);
        llError = findViewById(R.id.llError);
        txtResult = findViewById(R.id.txtResult);
        txtResult.setVisibility(View.GONE);

        llHelp = findViewById(R.id.llHelp);
        llHelp1 = findViewById(R.id.llHelp1);
        llHelp2 = findViewById(R.id.llHelp2);
        llHelp3 = findViewById(R.id.llHelp3);
        llHelp4 = findViewById(R.id.llHelp4);
        txtSkip1 = findViewById(R.id.txtSkip1);
        txtSkip2 = findViewById(R.id.txtSkip2);
        txtSkip3 = findViewById(R.id.txtSkip3);
        txtSkip4 = findViewById(R.id.txtSkip4);
        txtNext1 = findViewById(R.id.txtNext1);
        txtNext2 = findViewById(R.id.txtNext2);
        txtNext3 = findViewById(R.id.txtNext3);
        txtNext4 = findViewById(R.id.txtNext4);

        swipeRefreshRecycler = findViewById(R.id.swipeRefreshRecycler);
        recycler = findViewById(R.id.recycler);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            clicked = extras.getString("clicked");
        } else {
            clicked = "no";
        }

        assert clicked != null;
        if (clicked.equals("fromNotification")){
            Constant.NOTIFICATION_COUNT = 0;
            NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (nMgr != null) {
                nMgr.cancelAll();
            }
        }

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recycler.setLayoutManager(mLayoutManager);
        recycler.setItemAnimator(new DefaultItemAnimator());
        adapter = new MemberVisitorRecyclerAdapter();
        adapter.setOnItemClickListener(this);

        llWarning.setVisibility(View.GONE);
        llRecycler.setVisibility(View.GONE);

        txtNext1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                llHelp.setVisibility(View.VISIBLE);
                llHelp1.setVisibility(View.GONE);
                llHelp2.setVisibility(View.VISIBLE);
                llHelp3.setVisibility(View.GONE);
                llHelp4.setVisibility(View.GONE);
                Constant.help_count++;
            }
        });

        txtNext2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                llHelp.setVisibility(View.VISIBLE);
                llHelp1.setVisibility(View.GONE);
                llHelp2.setVisibility(View.GONE);
                llHelp3.setVisibility(View.VISIBLE);
                llHelp4.setVisibility(View.GONE);
                Constant.help_count++;
            }
        });

        txtNext3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                llHelp.setVisibility(View.VISIBLE);
                llHelp1.setVisibility(View.GONE);
                llHelp2.setVisibility(View.GONE);
                llHelp3.setVisibility(View.GONE);
                llHelp4.setVisibility(View.VISIBLE);
                Constant.help_count++;
            }
        });

        txtNext4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                skipHelp();
            }
        });

        txtSkip1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                skipHelp();
            }
        });

        txtSkip2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                skipHelp();
            }
        });

        txtSkip3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                skipHelp();
            }
        });

        txtSkip4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                skipHelp();
            }
        });

        if (Util.ReadSharePreference(context, Constant.IS_MEMBER_DATABASE_FIRST_TIME).equals("")) {
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
        } else {

            if (Util.isOnline(context)) {
                getVisitors();
            } else {

                setOfflineData();

            }


            /**/
        }


        swipeRefreshRecycler.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshRecycler.setRefreshing(true);
                if (Util.isOnline(context)) {
                    getVisitors();
                } else {

                    setOfflineData();

                }
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
        if (Util.isOnline(context)) {
            getVisitors();
        } else {

            setOfflineData();

        }
    }

    public void getData() {

        if (Util.isOnline(context)) {
            getVisitors();
        } else {

            setOfflineData();

        }
    }

    public void getVisitors() {

        if (!swipeRefreshRecycler.isRefreshing()) {
            progress.setVisibility(View.VISIBLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }

        final String flagValue;

        if (Util.ReadSharePreference(context, Constant.IS_MEMBER_DATABASE_FIRST_TIME).equals("")) {
            flagValue = "";
        } else {
            //flagValue = "flag";
            flagValue = "";

        }
        try {
            RestClient.getApi().get_visitors_records_by_member_number1(Util.ReadSharePreference(context, Constant.MEMBER_MOBILE_NO), flagValue,
                    new Callback<Response>() {
                        @Override
                        public void success(Response response, Response response2) {
                            try {

                                JSONObject jsonObject = new JSONObject(Util.getString(response.getBody().in()));
                                Gson gson = new Gson();
                                GetMemberVisitor getMemberVisitor = gson.fromJson(jsonObject.toString(), GetMemberVisitor.class);

                                switch (getMemberVisitor.getStatus()) {
                                    case "true":

                                        progress.setVisibility(View.GONE);
                                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                        db.addAllMemberSoc(getMemberVisitor.getMemberDetails());

                                        Util.WriteSharePreference(context, Constant.IS_MEMBER_DATABASE_FIRST_TIME, "flag");
                                        if (Util.ReadSharePreference(context, Constant.MEMBER_HELP).equals("not_show")) {
                                            llHelp.setVisibility(View.VISIBLE);
                                            llHelp1.setVisibility(View.VISIBLE);
                                            llHelp2.setVisibility(View.GONE);
                                            llHelp3.setVisibility(View.GONE);
                                            llHelp4.setVisibility(View.GONE);
                                        } else {
                                            llHelp.setVisibility(View.GONE);
                                        }

                                        if (getMemberVisitor.getMemberDetails().size() > 0) {
                                            memberDetails = db.getMemberSoc();
                                            Constant.memberDetails = memberDetails;
                                        }
                                        if (getMemberVisitor.getData().size() > 0) {
                                            swipeRefreshRecycler.setRefreshing(false);
                                            getVisitorVo = getMemberVisitor.getData();
                                            Constant.getMemberVisitorVo = getVisitorVo;
                                            ArrayList<GetMemberVisitorVo> newData = new ArrayList<>();
                                            for (int i = 0; i < getVisitorVo.size(); i++) {
                                                if (getVisitorVo.get(i).getVistorRecordId() != null) {
                                                    newData.add(getVisitorVo.get(i));
                                                }
                                            }
                                            getVisitorVo = newData;

                                            if (flagValue.equals("")) {
                                                db.addAllMemberVisitor(getVisitorVo);
                                            } else {

                                                ArrayList<GetMemberVisitorVo> data = db.getMemberVisitor();
                                                db.addNewMemberVisitor(getVisitorVo, data);

                                                getVisitorVo = db.getMemberVisitor();
                                            }

                                            Constant.getMemberVisitorVo = getVisitorVo;

                                            if (getVisitorVo.size() > 0) {
                                                edtSearch.setHint("Search through all " + getVisitorVo.size() + " visits");
                                                edtSearch.setText("");
                                                txtResult.setVisibility(View.GONE);

                                                llWarning.setVisibility(View.GONE);
                                                llRecycler.setVisibility(View.VISIBLE);

                                                for (int i = 0; i < getVisitorVo.size(); i++) {
                                                    String dateIn = Util.converter(getVisitorVo.get(i).getVisitorDateTimeIn(), "yyyy-MM-dd HH:mm:ss", "dd-MMM-yyyy HH:mm");
                                                    getVisitorVo.get(i).setVisitorDateTimeIn(dateIn);
                                                }
                                                Constant.getMemberVisitorVo = getVisitorVo;
                                                adapter = new MemberVisitorRecyclerAdapter(context, getVisitorVo, memberDetails);
                                                recycler.setAdapter(adapter);
                                            } else {
                                                llWarning.setVisibility(View.VISIBLE);
                                                llRecycler.setVisibility(View.GONE);
                                                swipeRefreshRecycler.setRefreshing(false);
                                                Snackbar snackbar = Snackbar
                                                        .make(llMain, "No data found", Snackbar.LENGTH_LONG);
                                                snackbar.show();
                                            }
                                        } else {
                                            llWarning.setVisibility(View.VISIBLE);
                                            llRecycler.setVisibility(View.GONE);
                                            swipeRefreshRecycler.setRefreshing(false);
                                            Snackbar snackbar = Snackbar
                                                    .make(llMain, "No data found", Snackbar.LENGTH_LONG);
                                            snackbar.show();
                                        }
                                        break;
                                    case "false":
                                        swipeRefreshRecycler.setRefreshing(false);
                                        progress.setVisibility(View.GONE);
                                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                        llWarning.setVisibility(View.GONE);
                                        llRecycler.setVisibility(View.VISIBLE);
                                        getVisitorVo = db.getMemberVisitor();
                                        Constant.getMemberVisitorVo = getVisitorVo;
                                        memberDetails = db.getMemberSoc();
                                        if (getVisitorVo.size() > 0) {
                                            edtSearch.setHint("Search through all " + getVisitorVo.size() + " visits");
                                            edtSearch.setText("");
                                            txtResult.setVisibility(View.GONE);

                                            llWarning.setVisibility(View.GONE);
                                            llRecycler.setVisibility(View.VISIBLE);

                                            for (int i = 0; i < getVisitorVo.size(); i++) {
                                                String dateIn = Util.converter(getVisitorVo.get(i).getVisitorDateTimeIn(), "yyyy-MM-dd HH:mm:ss", "dd-MMM-yyyy HH:mm");
                                                getVisitorVo.get(i).setVisitorDateTimeIn(dateIn);
                                            }
                                            Constant.getMemberVisitorVo = getVisitorVo;
                                            adapter = new MemberVisitorRecyclerAdapter(context, getVisitorVo, memberDetails);
                                            recycler.setAdapter(adapter);
                                        } else {
                                            llWarning.setVisibility(View.VISIBLE);
                                            llRecycler.setVisibility(View.GONE);
                                            swipeRefreshRecycler.setRefreshing(false);
                                            Snackbar snackbar = Snackbar
                                                    .make(llMain, "No data found", Snackbar.LENGTH_LONG);
                                            snackbar.show();
                                        }

                                        break;
                                    default:
                                        llWarning.setVisibility(View.VISIBLE);
                                        llRecycler.setVisibility(View.GONE);
                                        swipeRefreshRecycler.setRefreshing(false);
                                        progress.setVisibility(View.GONE);
                                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                        Util.logout(MemberMainActivity.this, llMain, getMemberVisitor.getMessage());
                                        break;
                                }

                            } catch (Exception e) {
                                llWarning.setVisibility(View.VISIBLE);
                                llRecycler.setVisibility(View.GONE);
                                swipeRefreshRecycler.setRefreshing(false);
                                progress.setVisibility(View.GONE);
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                e.printStackTrace();
                                Log.e("MemberRetrofit Error==>", e.toString());
                                Snackbar snackbar = Snackbar
                                        .make(llMain, "Something went wrong, please try again", Snackbar.LENGTH_LONG);
                                snackbar.show();
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            llWarning.setVisibility(View.VISIBLE);
                            llRecycler.setVisibility(View.GONE);
                            swipeRefreshRecycler.setRefreshing(false);
                            progress.setVisibility(View.GONE);
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            Log.e("MemberFail Error==>", error.toString());
                            Snackbar snackbar = Snackbar
                                    .make(llMain, "Something went wrong, please try again", Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void skipHelp() {

        Constant.help_count = 1;
        llHelp.setVisibility(View.GONE);
        llHelp1.setVisibility(View.GONE);
        llHelp2.setVisibility(View.GONE);
        llHelp3.setVisibility(View.GONE);
        llHelp4.setVisibility(View.GONE);
        Util.WriteSharePreference(context, Constant.MEMBER_HELP, "show");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.editProfile:
                Intent intent1 = new Intent(context, SettingMemberActivity.class);
                startActivity(intent1);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }

    @Override
    public void onItemClick(GetMemberVisitorVo searchedData, int position) {

        Intent intent = new Intent(context, MemberPagerActivity.class);
        intent.putExtra("selectedData", searchedData);
        intent.putExtra("clicked", "true");
        intent.putExtra("position", position);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }
}