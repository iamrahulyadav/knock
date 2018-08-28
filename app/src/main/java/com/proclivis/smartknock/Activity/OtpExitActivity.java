package com.proclivis.smartknock.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.proclivis.smartknock.Activity.User.UserMainActivity;
import com.proclivis.smartknock.Common.Constant;
import com.proclivis.smartknock.Common.Util;
import com.proclivis.smartknock.Model.OtpVo;
import com.proclivis.smartknock.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import cz.msebera.android.httpclient.Header;

public class OtpExitActivity extends AppCompatActivity {

    Context context;
    TextView txtOtpDetail, txtOtpTimer, txtResend;
    com.chaos.view.PinView otpView;
    Button btnSubmit;
    ImageView imgHome;
    RelativeLayout llMain, progress;

    String mobile = "";

    String otpSessionId = "";
    Boolean otpTimer = true;

    CountDownTimer countDownTimer;
    int time = 30000;

    boolean isExit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.otp_exit_activity);
        context = this;

        txtOtpDetail = findViewById(R.id.txtOtpDetail);
        txtOtpTimer = findViewById(R.id.txtOtpTimer);
        txtResend = findViewById(R.id.txtResend);
        otpView = findViewById(R.id.otpView);
        btnSubmit = findViewById(R.id.btnSubmit);
        imgHome = findViewById(R.id.imgHome);
        llMain = findViewById(R.id.llMain);
        progress = findViewById(R.id.progress);

        if (Util.isOnline(context)){
            startTimer();
            resendOtp();
        } else {
            Snackbar snackbar = Snackbar
                    .make(llMain, "No internet connection", Snackbar.LENGTH_LONG);
            snackbar.show();
        }


        mobile = Util.ReadSharePreference(context, Constant.USER_MOBILE_NO);
        final StringBuilder res = new StringBuilder();
        if (mobile.length() > 0) {
            for (int i = 0; i < mobile.length(); i++) {
                if (i > 5) {
                    res.append(mobile.charAt(i));
                }
            }
        }

        txtOtpDetail.setText(String.format("An OTP is sent to your registered mobile Number XXXXX X%s", res));
        //txtOtpTimer.setText("Please enter OTP in next 02:00 minutes");


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Util.hideSoftKeyboard(OtpExitActivity.this);

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
                isExit=true;
                Intent intent = new Intent(context, UserMainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);

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

            public void onFinish() {

                if (!isExit) {
                    onBackPressed();
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

                    isExit = true;
                    Intent intent1 = new Intent(context , SplashActivity.class);
                    startActivity(intent1);
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK );
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);

                } else {
                    progress.setVisibility(View.GONE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    Snackbar snackbar = Snackbar
                            .make(llMain, otpVo.getDetails(), Snackbar.LENGTH_LONG);
                    snackbar.show();
                    resendOtp();
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

            client.post(getApplicationContext(), "https://2factor.in/API/V1/c1f0d9c2-d13f-11e7-94da-0200cd936042/SMS/+91" + Util.ReadSharePreference(context, Constant.USER_MOBILE_NO) + "/AUTOGEN", null, new JsonHttpResponseHandler() {

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
        isExit=true;
        Intent intent = new Intent(context, UserMainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }

    @Override
    protected void onPause() {
        super.onPause();
        isExit = true;
    }
}
