package com.proclivis.smartknock.Activity.User;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.proclivis.smartknock.Activity.OtpExitActivity;
import com.proclivis.smartknock.Adapter.SelectedMemberAdapter;
import com.proclivis.smartknock.Adapter.ToMeetAdapter;
import com.proclivis.smartknock.Common.ConnectivityStatus;
import com.proclivis.smartknock.Common.Constant;
import com.proclivis.smartknock.Common.Util;
import com.proclivis.smartknock.DataBase.DatabaseHelper;
import com.proclivis.smartknock.DataBase.MemberDB;
import com.proclivis.smartknock.FCM.MyFirebaseMessagingService;
import com.proclivis.smartknock.Model.ToMeet;
import com.proclivis.smartknock.R;
import com.proclivis.smartknock.Retrf.RestClient;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ToMeetActivity extends AppCompatActivity implements ToMeetAdapter.ClickListener, SelectedMemberAdapter.ClickListener {

    LinearLayout toolbar, llBack, llHome, llVisitorDetail;
    RelativeLayout llMain;
    Context context;
    @SuppressLint("StaticFieldLeak")
    public static RecyclerView recycler;
    @SuppressLint("StaticFieldLeak")
    public static RecyclerView recyclerMember;
    Button btnReview;
    RelativeLayout progress;
    EditText edtSearch;
    @SuppressLint("StaticFieldLeak")
    public static ToMeetAdapter adapter;
    @SuppressLint("StaticFieldLeak")
    public static SelectedMemberAdapter adapterMember;
    private DatabaseHelper db;

    ScrollView scrollView;
    @SuppressLint("StaticFieldLeak")
    public static LinearLayout llError;

    ArrayList<MemberDB> memberDBs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.to_meet_activity);
        context = this;
        toolbar = findViewById(R.id.toolbar);
        llBack = findViewById(R.id.llBack);
        llMain = findViewById(R.id.llMain);

        db = new DatabaseHelper(this);

        btnReview = findViewById(R.id.btnReview);
        progress = findViewById(R.id.progress);
        recycler = findViewById(R.id.recycler);
        recyclerMember = findViewById(R.id.recyclerMember);
        edtSearch = findViewById(R.id.edtSearch);
        scrollView = findViewById(R.id.scrollView);
        llError = findViewById(R.id.llError);
        llHome = findViewById(R.id.llHome);
        llVisitorDetail = findViewById(R.id.llVisitorDetail);
        progress.setVisibility(View.GONE);
        btnReview.setVisibility(View.GONE);

        switch (getResources().getConfiguration().orientation) {
            case Configuration.ORIENTATION_PORTRAIT:

                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                recycler.setLayoutManager(mLayoutManager);

                recyclerMember.setLayoutManager(new GridLayoutManager(this, 2));
                Constant.MemberNameLength = 12;
                break;

            case Configuration.ORIENTATION_LANDSCAPE:

                int numberOfColumns = 3;
                recycler.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
                recyclerMember.setLayoutManager(new GridLayoutManager(this, 4));
                Constant.MemberNameLength = 10;
        }

        recycler.setItemAnimator(new DefaultItemAnimator());
        adapter = new ToMeetAdapter();
        adapter.setOnItemClickListener(this);

        recyclerMember.setItemAnimator(new DefaultItemAnimator());
        adapterMember = new SelectedMemberAdapter();
        adapterMember.setOnItemClickListener(this);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
        btnReview.setVisibility(View.GONE);
        recyclerMember.setVisibility(View.GONE);


    if (Util.ReadSharePreference(context, Constant.IS_MEMBER_FIRST_TIME).equals("true") && Util.isOnline(context)) {
        getData();
    } else if (!Util.ReadSharePreference(context, Constant.MEMBER_API_CALL_DATE).equals(Util.getCurrentDateTime()) && Util.isOnline(context)) {
        getData();
    } else {
        memberDBs = db.getAllMember();

        ArrayList<MemberDB> mFlat = new ArrayList<>();
        ArrayList<MemberDB> mName = new ArrayList<>();

        for (int i = 0; i < memberDBs.size(); i++) {
            if (!memberDBs.get(i).getFlat_no().equals("")) {
                mFlat.add(memberDBs.get(i));
            }
        }

        for (int i = 0; i < memberDBs.size(); i++) {
            if (memberDBs.get(i).getFlat_no().equals("")) {
                mName.add(memberDBs.get(i));
            }
        }

        Collections.sort(mName, new Comparator<MemberDB>() {
            @Override
            public int compare(MemberDB item, MemberDB t1) {
                String s1 = item.getName();
                String s2 = t1.getName();
                return s1.compareToIgnoreCase(s2);
            }

        });

        if (mFlat.size() == 0) {
            edtSearch.setHint("Search by Name");
        } else {
            edtSearch.setHint("Search by Name or Number");
        }
        memberDBs = new ArrayList<>();

        memberDBs.addAll(mFlat);
        memberDBs.addAll(mName);

        adapter = new ToMeetAdapter(memberDBs);
        recycler.setAdapter(adapter);
        }

        if (Constant.selectedMember.size() > 0) {

            recyclerMember.setVisibility(View.VISIBLE);
            btnReview.setVisibility(View.VISIBLE);

            adapterMember = new SelectedMemberAdapter(context, Constant.selectedMember);
            recyclerMember.setAdapter(adapterMember);
            adapterMember.notifyDataSetChanged();

            adapter = new ToMeetAdapter(memberDBs);
            recycler.setAdapter(adapter);
            adapter.notifyDataSetChanged();

        } else {
            recyclerMember.setVisibility(View.GONE);
            btnReview.setVisibility(View.GONE);
        }

        btnReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                homeClick = false;
                Intent intent = new Intent(context, ReviewActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        });

        //Added code for Top Train Panel to disappear when soft keyboard is visible.
        final LinearLayout llStep = findViewById(R.id.llStep);
        edtSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                llStep.setVisibility(View.GONE);
            }
        });
        //End, Added code for Top Train Panel to disappear when soft keyboard is visible.

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                adapter.getFilter().filter(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    handled = true;
                    Util.hideSoftKeyboard(ToMeetActivity.this);
                    final LinearLayout llStep = findViewById(R.id.llStep);
                    llStep.setVisibility(View.VISIBLE);
                }
                return handled;
            }
        });

        llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        llVisitorDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        llHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                homeClick = false;
                Intent intent = new Intent(context, UserMainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
            }
        });
    }

    private void getData() {

        progress.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
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

                                        progress.setVisibility(View.GONE);
                                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                        db.insertMemberDB(confirm.getData());
                                        memberDBs = db.getAllMember();

                                        ArrayList<MemberDB> mFlat = new ArrayList<>();
                                        ArrayList<MemberDB> mName = new ArrayList<>();

                                        for (int i=0 ; i<memberDBs.size(); i++){
                                            if (!memberDBs.get(i).getFlat_no().equals("")){
                                                mFlat.add(memberDBs.get(i));
                                            }
                                        }

                                        for (int i=0 ; i<memberDBs.size(); i++){
                                            if (memberDBs.get(i).getFlat_no().equals("")){
                                                mName.add(memberDBs.get(i));
                                            }
                                        }

                                        Collections.sort(mName, new Comparator<MemberDB>() {
                                            @Override
                                            public int compare(MemberDB item, MemberDB t1) {
                                                String s1 = item.getName();
                                                String s2 = t1.getName();
                                                return s1.compareToIgnoreCase(s2);
                                            }

                                        });

                                        if (mFlat.size()==0){
                                            edtSearch.setHint("Search by Name");
                                        } else {
                                            edtSearch.setHint("Search by Name or Number");
                                        }
                                        memberDBs = new ArrayList<>();

                                        memberDBs.addAll(mFlat);
                                        memberDBs.addAll(mName);

                                        Util.WriteSharePreference(context, Constant.IS_MEMBER_FIRST_TIME, "false");
                                        Util.WriteSharePreference(context, Constant.MEMBER_API_CALL_DATE, Util.getCurrentDateTime());

                                        adapter = new ToMeetAdapter(memberDBs);
                                        recycler.setAdapter(adapter);

                                        break;
                                    }
                                    default:
                                        progress.setVisibility(View.GONE);
                                        Util.logout(ToMeetActivity.this, llMain, confirm.getMessage());
                                        break;
                                }


                            } catch (Exception e) {
                                progress.setVisibility(View.GONE);
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                Log.e("errorGetMember==>", e.toString());
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            progress.setVisibility(View.GONE);
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            Log.e("errorGetMember==>", error.toString());
                        }
                    });
        } catch (Exception e) {
            progress.setVisibility(View.GONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            Log.e("errorGetMember==>", e.toString());
        }

    }

    @Override
    public void onBackPressed() {
        homeClick = false;
        super.onBackPressed();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }

    @Override
    public void onItemClick(MemberDB searchedData, Boolean selected) {

        //edtSearch.setText(searchedData.getName());
        btnReview.setVisibility(View.VISIBLE);
        edtSearch.setText("");

        scrollView.fullScroll(ScrollView.FOCUS_UP);
        if (selected) {
            if (Constant.selectedMember.size() != 0) {
                for (int i = 0; i < Constant.selectedMember.size(); i++) {
                    if (searchedData.getId().equals(Constant.selectedMember.get(i).getId())) {
                        Constant.selectedMember.remove(i);
                        adapterMember = new SelectedMemberAdapter(context, Constant.selectedMember);
                        recyclerMember.setAdapter(adapterMember);
                        adapterMember.notifyDataSetChanged();

                    }
                }
            }
        } else {
            Constant.selectedMember.add(searchedData);
            adapterMember = new SelectedMemberAdapter(context, Constant.selectedMember);
            recyclerMember.setAdapter(adapterMember);
            adapterMember.notifyDataSetChanged();
        }

        if (Constant.selectedMember.size() == 0) {
            recyclerMember.setVisibility(View.GONE);
            btnReview.setVisibility(View.GONE);
        } else {
            recyclerMember.setVisibility(View.VISIBLE);
            btnReview.setVisibility(View.VISIBLE);
        }
        //Added code for Top Train Panel to appear when soft keyboard is NOT visible.
        final LinearLayout llStep = findViewById(R.id.llStep);
        llStep.setVisibility(View.VISIBLE);
        Util.hideSoftKeyboard(ToMeetActivity.this);

    }

    @Override
    public void onItemClick(MemberDB searchedData, int position) {
        if (Constant.selectedMember.size() > 0) {
            for (int i = 0; i < Constant.selectedMember.size(); i++) {
                if (Constant.selectedMember.get(i).getId().equals(searchedData.getId())) {
                    Constant.selectedMember.remove(i);
                    break;
                }
            }
        }

        if (Constant.selectedMember.size() == 0) {
            btnReview.setVisibility(View.GONE);
        }
        adapterMember = new SelectedMemberAdapter(context, Constant.selectedMember);
        recyclerMember.setAdapter(adapterMember);
        adapterMember.notifyDataSetChanged();
        adapter.notifyDataSetChanged();
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
            if (ConnectivityStatus.isConnected()) {
                Util.uploadInVisitor(ToMeetActivity.this, llMain);
            }
        }
    };
}