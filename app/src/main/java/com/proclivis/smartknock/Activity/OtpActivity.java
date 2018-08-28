package com.proclivis.smartknock.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.proclivis.smartknock.Activity.Member.MemberMainActivity;
import com.proclivis.smartknock.Activity.User.SettingUserActivity;
import com.proclivis.smartknock.Activity.User.UserMainActivity;
import com.proclivis.smartknock.Common.Constant;
import com.proclivis.smartknock.Common.Util;
import com.proclivis.smartknock.Model.LoginMember;
import com.proclivis.smartknock.Model.LoginUser;
import com.proclivis.smartknock.Model.OtpVo;
import com.proclivis.smartknock.R;
import com.proclivis.smartknock.Retrf.RestClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import cz.msebera.android.httpclient.Header;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class OtpActivity extends AppCompatActivity {

    Context context;
    TextView txtOtpDetail, txtOtpTimer, txtResend;
    com.chaos.view.PinView otpView;
    Button btnSubmit;
    ImageView imgHome;
    RelativeLayout llMain, progress;

    JSONObject jsonObject;
    String mobile = "";
    String type = "";
    String otpSessionId = "";
    String otpMobile = "";
    Boolean otpTimer = true;

    CountDownTimer countDownTimer;
    int time = 120000;

    boolean isIn = false;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.otp_activity);
        context = this;

        txtOtpDetail = findViewById(R.id.txtOtpDetail);
        txtOtpTimer = findViewById(R.id.txtOtpTimer);
        txtResend = findViewById(R.id.txtResend);
        otpView = findViewById(R.id.otpView);
        btnSubmit = findViewById(R.id.btnSubmit);
        imgHome = findViewById(R.id.imgHome);
        llMain = findViewById(R.id.llMain);
        progress = findViewById(R.id.progress);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            jsonObject = null;
        } else {

            type = extras.getString("type");
            mobile = extras.getString("mobile");
            otpMobile = extras.getString("mobile");
            if (type.equals("login")) {

                otpSessionId = extras.getString("otpSessionId");

                try {
                    jsonObject = new JSONObject(getIntent().getStringExtra("jsonObject"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        startTimer();

        if (type.equals("main") || type.equals("exit")) {
            time = 30000;

            resendOtp();
        }

        final StringBuilder res = new StringBuilder();
        if (mobile.length() > 0) {
            for (int i = 0; i < mobile.length(); i++) {
                if (i > 5) {
                    res.append(mobile.charAt(i));
                }
            }
        }

        txtOtpDetail.setText("An OTP is sent to your registered mobile Number XXXXX X" + res);
        //txtOtpTimer.setText("Please enter OTP in next 02:00 minutes");

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Util.hideSoftKeyboard(OtpActivity.this);

                if (otpTimer) {
                    if (otpView.getText().length() == 6) {
                        if (Util.isOnline(context)){
                            checkTimer();
                        } else {
                            Snackbar snackbar = Snackbar
                                    .make(llMain, "No internet connection", Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }
                    } else {
                        otpView.requestFocus();
                        Snackbar snackbar = Snackbar
                                .make(llMain, "Please Enter OTP", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                } else {
                    final Snackbar snackbar = Snackbar
                            .make(llMain, "Please Resend OTP", Snackbar.LENGTH_LONG)
                            .setAction("Resend", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    resendOtp();
                                }
                            });
                    snackbar.setActionTextColor(Color.RED);
                    snackbar.show();
                }
            }
        });

        txtResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                resendOtp();

            }
        });

        imgHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isIn=true;
                if (type.equals("login")) {

                    Intent intent = new Intent(context, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
                } else {

                    Intent intent = new Intent(context, UserMainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
                }
            }
        });

    }

    public void startTimer() {

        otpTimer = true;
        otpView.setText("");

        countDownTimer = new CountDownTimer(time, 1000) {

            @SuppressLint("SetTextI18n")
            public void onTick(long millisUntilFinished) {

                txtOtpTimer.setText("Please enter OTP in next " + millisUntilFinished / 1000 + " seconds");
            }

            @SuppressLint("SetTextI18n")
            public void onFinish() {

                if (type.equals("main") || type.equals("exit")) {
                    if (!isIn) {
                        onBackPressed();
                    }
                } else {
                    otpTimer = false;
                    otpView.setText("");
                    txtOtpTimer.setText("You have to resend OTP");
                }

            }

        }.start();
    }

    public void checkTimer() {
        if (otpTimer) {

            String otp = String.valueOf(otpView.getText());

            Log.e("otpVerify", "https://2factor.in/API/V1/c1f0d9c2-d13f-11e7-94da-0200cd936042/SMS/VERIFY/" + otpSessionId + "/" + otp);

            progress.setVisibility(View.VISIBLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);


            new GetMethodDemo().execute("https://2factor.in/API/V1/c1f0d9c2-d13f-11e7-94da-0200cd936042/SMS/VERIFY/" + otpSessionId + "/" + otp);

            //verifyOtp();

        } else {
            final Snackbar snackbar = Snackbar
                    .make(llMain, "Please Resend OTP", Snackbar.LENGTH_LONG)
                    .setAction("Resend", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            resendOtp();

                        }
                    });
            snackbar.setActionTextColor(Color.RED);
            snackbar.show();
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class GetMethodDemo extends AsyncTask<String, Void, String> {
        String server_response;

        @Override
        protected String doInBackground(String... strings) {

            URL url;
            HttpURLConnection urlConnection;

            try {
                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();

                int responseCode = urlConnection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    server_response = readStream(urlConnection.getInputStream());
                    Log.v("CatalogClient", server_response);
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
                Log.e("response", e.toString());
                progress.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                Snackbar snackbar = Snackbar
                        .make(llMain, "Something went wrong", Snackbar.LENGTH_LONG);

                snackbar.show();


            } catch (IOException e) {
                e.printStackTrace();

                Log.e("response", e.toString());
                progress.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                Snackbar snackbar = Snackbar
                        .make(llMain, "Something went wrong", Snackbar.LENGTH_LONG);

                snackbar.show();


            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);


            Log.e("Response", "" + server_response);
            if (!(server_response == null)) {
                progress.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                Gson gson = new Gson();
                OtpVo otpVo = gson.fromJson(server_response, OtpVo.class);

                if (otpVo.getStatus().equals("Success")) {

                    isIn = true;
                    switch (type) {
                        case "main":
                            Intent intent = new Intent(context, SettingUserActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                            break;
                        case "exit":
                            onBackPressed();
                            break;
                        default:
                            verifyUser();
                            break;
                    }

                } else {
                    progress.setVisibility(View.GONE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    Snackbar snackbar = Snackbar
                            .make(llMain, otpVo.getDetails(), Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            } else {

                progress.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                Snackbar snackbar = Snackbar
                        .make(llMain, "OTP entered was incorrect. We have resent a new OTP.", Snackbar.LENGTH_LONG);

                snackbar.show();

                resendOtp();
            }

        }
    }

    @NonNull
    private String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuilder response = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response.toString();
    }

    public void verifyUser() {
        Gson gson = new Gson();
        try {
            if (jsonObject.getString("type").equals("customer")) {
                LoginUser loginUser = gson.fromJson(jsonObject.toString(), LoginUser.class);

                if (loginUser.getStatus().equals("true")) {

                    Util.WriteSharePreference(context, Constant.USER_ID, loginUser.getData().getId());
                    Util.WriteSharePreference(context, Constant.USER_INSTALLATION_ID, loginUser.getData().getInstallationId());
                    Util.WriteSharePreference(context, Constant.USER_NAME, loginUser.getData().getName());
                    Util.WriteSharePreference(context, Constant.USER_MOBILE_NO, loginUser.getData().getMobileNo());
                    Util.WriteSharePreference(context, Constant.USER_ADDRESS1, loginUser.getData().getAddress1());
                    Util.WriteSharePreference(context, Constant.USER_ADDRESS2, loginUser.getData().getAddress2());
                    Util.WriteSharePreference(context, Constant.USER_ADDRESS3, loginUser.getData().getAddress3());
                    Util.WriteSharePreference(context, Constant.USER_CITY, loginUser.getData().getCity());
                    Util.WriteSharePreference(context, Constant.USER_STATE, loginUser.getData().getState());
                    Util.WriteSharePreference(context, Constant.USER_PINCODE, loginUser.getData().getPincode());
                    Util.WriteSharePreference(context, Constant.USER_ENABLED, loginUser.getData().getEnabled());
                    Util.WriteSharePreference(context, Constant.USER_CREATION_DATE, loginUser.getData().getCreationDate());
                    Util.WriteSharePreference(context, Constant.USER_LAST_UODATE_DATE, loginUser.getData().getLastUpdatedDate());
                    Util.WriteSharePreference(context, Constant.USER_PRO, loginUser.getData().getPro());
                    Util.WriteSharePreferenceInt(context, Constant.DRAWER_COUNT, 0);

                    if (loginUser.getData().getMessage().length() > 0) {
                        String msg = loginUser.getData().getMessage();
                        String newMsg = msg.replace("%27", "'");

                        Util.WriteSharePreference(context, Constant.USER_SCROLLING_MESSEGE, newMsg);
                    } else {
                        Util.WriteSharePreference(context, Constant.USER_SCROLLING_MESSEGE, "Please remember to record your visit in our digital register");
                    }

                    Util.WriteSharePreference(context, Constant.IS_MEMBER_FIRST_TIME, "true");
                    Util.WriteSharePreference(context, Constant.IS_VISITOR_FIRST_TIME, "yes");
                    Util.WriteSharePreference(context, Constant.IS_EXPRESS_FIRST_TIME, "0");

                    progress.setVisibility(View.GONE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    Snackbar snackbar = Snackbar
                            .make(llMain, "Successfully Login", Snackbar.LENGTH_LONG);

                    snackbar.show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isIn = true;
                            Intent intent = new Intent(context, UserMainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                        }
                    }, 1000);


                } else {
                    progress.setVisibility(View.GONE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    Snackbar snackbar = Snackbar
                            .make(llMain, loginUser.getMessage(), Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            } else {
                final LoginMember loginMember = gson.fromJson(jsonObject.toString(), LoginMember.class);

                if (loginMember.getStatus().equals("true")) {

                    setFcm(loginMember);

                } else {
                    progress.setVisibility(View.GONE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    Snackbar snackbar = Snackbar
                            .make(llMain, loginMember.getMessage(), Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setFcm(final LoginMember data) {

        progress.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        String fcmId;
        try {

            fcmId = FirebaseInstanceId.getInstance().getToken();
            Util.WriteSharePreference(context, Constant.FCM_ID, fcmId);
        } catch (Exception e) {
            Log.e("fcmIdError", e.toString());
            fcmId = "";
            Util.WriteSharePreference(context, Constant.FCM_ID, fcmId);
        }

        try {
            RestClient.getApi().update_fcm_id_and_apple_id1(data.getData().getMobileNo(), fcmId,
                    new Callback<Response>() {
                        @Override
                        public void success(Response response, Response response2) {
                            try {

                                JSONObject jsonObject = new JSONObject(Util.getString(response.getBody().in()));

                                switch (jsonObject.getString("status")) {
                                    case "true": {

                                        Util.WriteSharePreference(context, Constant.MEMBER_ID, data.getData().getId());
                                        Util.WriteSharePreference(context, Constant.MEMBER_CUSTOMER_ID, data.getData().getCustomerId());
                                        Util.WriteSharePreference(context, Constant.MEMBER_INSTALLATION_ID, data.getData().getInstallationId());
                                        Util.WriteSharePreference(context, Constant.MEMBER_NAME, data.getData().getName());
                                        Util.WriteSharePreference(context, Constant.MEMBER_MOBILE_NO, data.getData().getMobileNo());
                                        Util.WriteSharePreference(context, Constant.MEMBER_MOBILE_UNIQUE_ID, data.getData().getMobileUniqueId());
                                        Util.WriteSharePreference(context, Constant.MEMBER_EMAIL, data.getData().getEmail());
                                        Util.WriteSharePreference(context, Constant.MEMBER_EMAIL_FLAG, data.getData().getEmailFlag());
                                        Util.WriteSharePreference(context, Constant.MEMBER_FLAT_NO, data.getData().getFlatNo());
                                        Util.WriteSharePreference(context, Constant.MEMBER_ENABLED, data.getData().getEnabled());
                                        Util.WriteSharePreference(context, Constant.MEMBER_CREATION_DATE, data.getData().getCreationDate());
                                        Util.WriteSharePreference(context, Constant.MEMBER_LAST_UODATE_DATE, data.getData().getLastUpdatedDate());
                                        Util.WriteSharePreference(context, Constant.MEMBER_PROFILE_IMAGE, data.getData().getMemberProfileImage());
                                        Util.WriteSharePreference(context, Constant.MEMBER_HELP, "not_show");
                                        Util.WriteSharePreference(context, Constant.IS_MEMBER_DATABASE_FIRST_TIME, "");

                                        progress.setVisibility(View.GONE);
                                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                        Snackbar snackbar = Snackbar
                                                .make(llMain, "Successfully Login", Snackbar.LENGTH_LONG);

                                        snackbar.show();
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {

                                                isIn = true;
                                                Intent intent = new Intent(context, MemberMainActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                                finish();
                                                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                                            }
                                        }, 1000);
                                        break;
                                    }
                                    case "false": {
                                        progress.setVisibility(View.GONE);
                                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                        Snackbar snackbar = Snackbar
                                                .make(llMain, "Error in login", Snackbar.LENGTH_LONG);

                                        snackbar.show();
                                        break;
                                    }
                                    default:
                                        progress.setVisibility(View.GONE);
                                        Util.logout(OtpActivity.this, llMain, jsonObject.getString("message"));
                                        break;
                                }

                            } catch (Exception e) {
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
                            progress.setVisibility(View.GONE);
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                            Log.e("LoginRetrofit Error==>", error.toString());
                            Snackbar snackbar = Snackbar
                                    .make(llMain, "Something went wrong, please try again", Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void resendOtp() {

        if (!Util.isOnline(context)){
            Snackbar snackbar = Snackbar
                    .make(llMain, "No internet connection", Snackbar.LENGTH_LONG);
            snackbar.show();
        } else {
            progress.setVisibility(View.VISIBLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            AsyncHttpClient client = new AsyncHttpClient();

            client.post(getApplicationContext(), "https://2factor.in/API/V1/c1f0d9c2-d13f-11e7-94da-0200cd936042/SMS/+91" + otpMobile + "/AUTOGEN", null, new JsonHttpResponseHandler() {

                @Override
                public void onStart() {
                    super.onStart();
                }

                @Override
                public void onFinish() {
                    super.onFinish();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);

                    if (!(response == null)) {
                        progress.setVisibility(View.GONE);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                        Gson gson = new Gson();
                        OtpVo otpVo = gson.fromJson(response.toString(), OtpVo.class);

                        if (otpVo.getStatus().equals("Success")) {

                            countDownTimer.cancel();
                            startTimer();

                            otpSessionId = otpVo.getDetails();

                        } else {
                            progress.setVisibility(View.GONE);
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            Snackbar snackbar = Snackbar
                                    .make(llMain, otpVo.getDetails(), Snackbar.LENGTH_LONG);

                            snackbar.show();
                        }
                    } else {
                        progress.setVisibility(View.GONE);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        Snackbar snackbar = Snackbar
                                .make(llMain, "Something went wrong", Snackbar.LENGTH_LONG);

                        snackbar.show();

                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                }
            });
        }

    }

    @Override
    public void onBackPressed() {

        if (type.equals("main") || type.equals("exit")) {
            isIn = true;
            Intent intent = new Intent(context, UserMainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
        }
    }
}
