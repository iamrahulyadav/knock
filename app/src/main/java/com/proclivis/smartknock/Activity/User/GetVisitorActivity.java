package com.proclivis.smartknock.Activity.User;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.proclivis.smartknock.Activity.OtpExitActivity;
import com.proclivis.smartknock.Common.ConnectivityStatus;
import com.proclivis.smartknock.Common.Constant;
import com.proclivis.smartknock.Common.Util;
import com.proclivis.smartknock.DataBase.DatabaseHelper;
import com.proclivis.smartknock.DataBase.ExpressVisitorOfflineDb;
import com.proclivis.smartknock.DataBase.VisitorOfflineDb;
import com.proclivis.smartknock.FCM.MyFirebaseMessagingService;
import com.proclivis.smartknock.Model.GetVisitor;
import com.proclivis.smartknock.Model.GetVisitorVo;
import com.proclivis.smartknock.R;
import com.proclivis.smartknock.Retrf.RestClient;


import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class GetVisitorActivity extends AppCompatActivity {

    LinearLayout toolbar , llBack , llHome;
    Context context;
    EditText edtMobile;
    Button btnSearch;
    TextView txtUserName;
    RelativeLayout progress, llMain;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.get_visitor_activity);
        context = this;
        toolbar =  findViewById(R.id.toolbar);
        llBack =  findViewById(R.id.llBack);
        llBack.setVisibility(View.VISIBLE);

        edtMobile =  findViewById(R.id.edtMobile);
        btnSearch =  findViewById(R.id.btnSearch);
        txtUserName =  findViewById(R.id.txtUserName);
        progress =  findViewById(R.id.progress);
        llMain =  findViewById(R.id.llMain);
        llHome =  findViewById(R.id.llHome);

        db = new DatabaseHelper(context);
        txtUserName.setText(String.format("%s!!!", Util.ReadSharePreference(context, Constant.USER_NAME)));

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edtMobile.getText().toString().equals("")) {
                    edtMobile.setError("Please enter mobile no");
                    edtMobile.requestFocus();
                } else if (edtMobile.getText().toString().length() != 10) {
                    edtMobile.setError("Please enter valid mobile no");
                    edtMobile.requestFocus();
                } else {
                    Util.hideSoftKeyboard(GetVisitorActivity.this);
                    getVisitor();
                }
            }
        });

        edtMobile.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    handled = true;

                    if (edtMobile.getText().toString().equals("")) {
                        edtMobile.setError("Please enter mobile no");
                        edtMobile.requestFocus();
                    } else if (edtMobile.getText().toString().length() != 10) {
                        edtMobile.setError("Please enter valid mobile no");
                        edtMobile.requestFocus();
                    } else {
                        Util.hideSoftKeyboard(GetVisitorActivity.this);
                        getVisitor();
                    }
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

        llHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    public void getVisitorOffline(){
        ArrayList<ExpressVisitorOfflineDb> expressVisitorOfferingDabs = db.getAllOfflineExpressVisitorByMobile(edtMobile.getText().toString());
        if (expressVisitorOfferingDabs.size()>0){
            Constant.getVisitorVo = new ArrayList<>();

            int n = 0;
            for (int i=0 ; i<expressVisitorOfferingDabs.size(); i++){
                if (expressVisitorOfferingDabs.get(i).getDate_time_out().equals("")){

                    VisitorOfflineDb visitorOfflineDb = new VisitorOfflineDb();

                    visitorOfflineDb.setId(expressVisitorOfferingDabs.get(i).getId());
                    visitorOfflineDb.setName(expressVisitorOfferingDabs.get(i).getName());
                    visitorOfflineDb.setMember(expressVisitorOfferingDabs.get(i).getMember());
                    visitorOfflineDb.setMobileNo(expressVisitorOfferingDabs.get(i).getMobileNo());
                    visitorOfflineDb.setComingFrom(expressVisitorOfferingDabs.get(i).getComingFrom());
                    visitorOfflineDb.setPurpose(expressVisitorOfferingDabs.get(i).getPurpose());
                    visitorOfflineDb.setImage(expressVisitorOfferingDabs.get(i).getImage());
                    visitorOfflineDb.setCount(expressVisitorOfferingDabs.get(i).getCount());
                    visitorOfflineDb.setDate_time_in(expressVisitorOfferingDabs.get(i).getDate_time_in());
                    visitorOfflineDb.setDate_time_out(expressVisitorOfferingDabs.get(i).getDate_time_out());

                    Constant.getVisitorVo.add(n , visitorOfflineDb);
                    n++;
                }
            }

            if (Constant.getVisitorVo.size()>0){
                homeClick = false;
                Intent intent = new Intent(context, VisitorVerifyActivity.class);
                intent.putExtra("mode", "offline");
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            } else {

                Snackbar snackbar = Snackbar
                        .make(llMain, "No data found" , Snackbar.LENGTH_LONG);
                snackbar.show();
            }

        } else {
            Constant.getVisitorVo = new ArrayList<>();
            ArrayList<VisitorOfflineDb> getVisitorVo1 = db.getAllOfflineVisitorByMobile(edtMobile.getText().toString());
            int n = 0;
            for (int i=0 ; i<getVisitorVo1.size(); i++){
                if (getVisitorVo1.get(i).getDate_time_out().equals("")){
                    Constant.getVisitorVo.add(n , getVisitorVo1.get(i));
                    n++;
                }
            }

            if (Constant.getVisitorVo.size()>0){
                homeClick = false;
                Intent intent = new Intent(context, VisitorVerifyActivity.class);
                intent.putExtra("mode", "offline");
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            } else {

                Snackbar snackbar = Snackbar
                        .make(llMain, "No data found" , Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        }

    }
    public void getVisitor() {
        if (!Util.isOnline(context)) {
            //Amit 19.06.18. Put the logic in a function
            getVisitorOffline();
            /*ArrayList<ExpressVisitorOfflineDb> expressVisitorOfferingDabs = db.getAllOfflineExpressVisitorByMobile(edtMobile.getText().toString());
            if (expressVisitorOfferingDabs.size()>0){
                Constant.getVisitorVo = new ArrayList<>();

                int n = 0;
                for (int i=0 ; i<expressVisitorOfferingDabs.size(); i++){
                    if (expressVisitorOfferingDabs.get(i).getDate_time_out().equals("")){

                        VisitorOfflineDb visitorOfflineDb = new VisitorOfflineDb();

                        visitorOfflineDb.setId(expressVisitorOfferingDabs.get(i).getId());
                        visitorOfflineDb.setName(expressVisitorOfferingDabs.get(i).getName());
                        visitorOfflineDb.setMember(expressVisitorOfferingDabs.get(i).getMember());
                        visitorOfflineDb.setMobileNo(expressVisitorOfferingDabs.get(i).getMobileNo());
                        visitorOfflineDb.setComingFrom(expressVisitorOfferingDabs.get(i).getComingFrom());
                        visitorOfflineDb.setPurpose(expressVisitorOfferingDabs.get(i).getPurpose());
                        visitorOfflineDb.setImage(expressVisitorOfferingDabs.get(i).getImage());
                        visitorOfflineDb.setCount(expressVisitorOfferingDabs.get(i).getCount());
                        visitorOfflineDb.setDate_time_in(expressVisitorOfferingDabs.get(i).getDate_time_in());
                        visitorOfflineDb.setDate_time_out(expressVisitorOfferingDabs.get(i).getDate_time_out());

                        Constant.getVisitorVo.add(n , visitorOfflineDb);
                        n++;
                    }
                }

                if (Constant.getVisitorVo.size()>0){
                    homeClick = false;
                    Intent intent = new Intent(context, VisitorVerifyActivity.class);
                    intent.putExtra("mode", "offline");
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                } else {

                    Snackbar snackbar = Snackbar
                            .make(llMain, "No data found" , Snackbar.LENGTH_LONG);
                    snackbar.show();
                }

            } else {
                Constant.getVisitorVo = new ArrayList<>();
                ArrayList<VisitorOfflineDb> getVisitorVo1 = db.getAllOfflineVisitorByMobile(edtMobile.getText().toString());
                int n = 0;
                for (int i=0 ; i<getVisitorVo1.size(); i++){
                    if (getVisitorVo1.get(i).getDate_time_out().equals("")){
                        Constant.getVisitorVo.add(n , getVisitorVo1.get(i));
                        n++;
                    }
                }

                if (Constant.getVisitorVo.size()>0){
                    homeClick = false;
                    Intent intent = new Intent(context, VisitorVerifyActivity.class);
                    intent.putExtra("mode", "offline");
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                } else {

                    Snackbar snackbar = Snackbar
                            .make(llMain, "No data found" , Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }*/

        } else {
            edtMobile.clearFocus();
            progress.setVisibility(View.VISIBLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            try {
                RestClient.getApi().get_visitor_detail_by_mobileno(edtMobile.getText().toString(),Util.ReadSharePreference(context , Constant.USER_INSTALLATION_ID),
                        Util.ReadSharePreference(context , Constant.USER_MOBILE_NO) ,
                        new Callback<Response>() {
                            @Override
                            public void success(Response response, Response response2) {
                                try {

                                    JSONObject jsonObject = new JSONObject(Util.getString(response.getBody().in()));
                                    Gson gson = new Gson();
                                    GetVisitor getVisitor = gson.fromJson(jsonObject.toString(), GetVisitor.class);

                                    switch (getVisitor.getStatus()) {
                                        case "true":

                                            ArrayList<GetVisitorVo> getVisitorVo = getVisitor.getData();

                                            progress.setVisibility(View.GONE);
                                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                            Collections.reverse(getVisitorVo);
                                            homeClick = false;
                                            Intent intent = new Intent(context, VisitorVerifyActivity.class);
                                            intent.putParcelableArrayListExtra("data", getVisitorVo);
                                            intent.putExtra("mode", "online");
                                            startActivity(intent);
                                            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

                                            break;
                                        case "false":
                                            progress.setVisibility(View.GONE);
                                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                            Snackbar snackbar = Snackbar
                                                    .make(llMain, getVisitor.getMessage(), Snackbar.LENGTH_LONG);
                                            snackbar.show();
                                            break;
                                        default:
                                            progress.setVisibility(View.GONE);
                                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                            Util.logout(GetVisitorActivity.this, llMain, getVisitor.getMessage());
                                            break;
                                    }

                                } catch (Exception e) {
                                    progress.setVisibility(View.GONE);
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                    e.printStackTrace();
                                    Log.e("LoginRetrofit Error==>", e.toString());
                                    //Amit:19.06.18 Commented the Message and called the get from offline database
                                    //Snackbar snackbar = Snackbar
                                      //      .make(llMain, "Something went wrong, please try again", Snackbar.LENGTH_LONG);
                                    //snackbar.show();
                                    getVisitorOffline();
                                }
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                progress.setVisibility(View.GONE);
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                Log.e("LoginRetrofit Error==>", error.toString());
                                //Amit:19.06.18 Commented the Message and called the get from offline database
                                //Snackbar snackbar = Snackbar
                                //      .make(llMain, "Something went wrong, please try again", Snackbar.LENGTH_LONG);
                                //snackbar.show();
                                getVisitorOffline();                            }
                        });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        homeClick = false;
        super.onBackPressed();
        overridePendingTransition(R.anim.left_to_right , R.anim.right_to_left);
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

    //Amit:19.06.18. Need to delete this after testing
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(ConnectivityStatus.isConnected()){
                Util.uploadInVisitor(GetVisitorActivity.this , llMain);
            }
        }
    };
}
