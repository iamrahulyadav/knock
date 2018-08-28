package com.proclivis.smartknock.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.proclivis.smartknock.Common.Constant;
import com.proclivis.smartknock.Common.Util;
import com.proclivis.smartknock.Model.OtpVo;
import com.proclivis.smartknock.R;
import com.proclivis.smartknock.Retrf.RestClient;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class LoginActivity extends AppCompatActivity {

    private RelativeLayout llMain;
    private EditText edtUserMobile;
    Button btnLogin;
    TextView txtTerms , txtClickHere;
    private Context context;
    private RelativeLayout progress;

    String deviceId;
    String deviceName;
    JSONObject jsonObject;

    @SuppressLint("HardwareIds")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        switch (getResources().getConfiguration().orientation) {
            case Configuration.ORIENTATION_PORTRAIT:
                setContentView(R.layout.login_activity_potrait);

                break;

            case Configuration.ORIENTATION_LANDSCAPE:
                setContentView(R.layout.login_activity_landscape);


        }

        context = this;

        llMain = findViewById(R.id.llMain);
        edtUserMobile = findViewById(R.id.edtUserMobile);
        btnLogin = findViewById(R.id.btnLogin);
        txtTerms = findViewById(R.id.txtTerms);
        txtClickHere = findViewById(R.id.txtClickhere);
        progress = findViewById(R.id.progress);

        deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        deviceName = Build.MANUFACTURER
                + " " + Build.MODEL + " " + Build.VERSION.RELEASE
                + " " + Build.VERSION_CODES.class.getFields()[Build.VERSION.SDK_INT].getName();

        Util.WriteSharePreference(context, Constant.DEVICE_ID, deviceId);
        Util.WriteSharePreference(context, Constant.DEVICE_NAME, deviceName);

        progress.setVisibility(View.GONE);


        if (!checkWriteExternalPermission()) {
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    2);
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Util.hideSoftKeyboard(LoginActivity.this);
                } catch (Exception e) {
                    Log.e("LoginHideKey", e.toString());
                }

                if (edtUserMobile.getText().toString().equals("")) {
                    edtUserMobile.setError("Please enter mobile number");
                    edtUserMobile.requestFocus();
                } else if (edtUserMobile.getText().toString().length() != 10) {
                    edtUserMobile.setError("Please enter valid mobile number");
                    edtUserMobile.requestFocus();
                } else {
                    Login();
                }
            }
        });

        txtTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uriUrl = Uri.parse("http://proclivistech.com/smart-knock/termsandconditions/");
                Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                startActivity(launchBrowser);
            }
        });

        txtClickHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uriUrl = Uri.parse("http://proclivistech.com/smart-knock/#contact");
                Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                startActivity(launchBrowser);
            }
        });
    }

    private boolean checkWriteExternalPermission() {
        String permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        int res = context.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    private boolean checkCameraPermission() {
        String permission = Manifest.permission.CAMERA;
        int res = context.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    public void Login() {

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        if (Util.isOnline(context)) {
            LoginApiCall(edtUserMobile.getText().toString());
        } else {

            final Snackbar snackbar = Snackbar
                    .make(llMain, "No internet connection", Snackbar.LENGTH_LONG)
                    .setAction("Retry", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Login();
                        }
                    });
            snackbar.setActionTextColor(Color.RED);
            snackbar.show();
        }
    }

    public void LoginApiCall(String userMobile) {
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
            RestClient.getApi().login_customer_or_member1(userMobile, deviceId, deviceName,fcmId,
                    new Callback<Response>() {
                        @Override
                        public void success(Response response, Response response2) {
                            try {

                                jsonObject = new JSONObject(Util.getString(response.getBody().in()));

                                switch (jsonObject.getString("status")) {
                                    case "true":

                                        AsyncHttpClient client = new AsyncHttpClient();
                                        String mobile = edtUserMobile.getText().toString();

                                        client.post(getApplicationContext(), "https://2factor.in/API/V1/c1f0d9c2-d13f-11e7-94da-0200cd936042/SMS/+91" + mobile + "/AUTOGEN", null, new JsonHttpResponseHandler() {

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

                                                        Intent intent = new Intent(context, OtpActivity.class);
                                                        intent.putExtra("jsonObject", jsonObject.toString());
                                                        intent.putExtra("mobile", edtUserMobile.getText().toString());
                                                        intent.putExtra("type", "login");
                                                        intent.putExtra("otpSessionId", otpVo.getDetails());
                                                        intent.putExtra("mobile", edtUserMobile.getText().toString());
                                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        startActivity(intent);
                                                        finish();
                                                        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

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

                                        break;
                                    case "false": {
                                        progress.setVisibility(View.GONE);
                                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                        Snackbar snackbar = Snackbar
                                                .make(llMain, jsonObject.getString("message"), Snackbar.LENGTH_LONG);

                                        snackbar.show();
                                        break;
                                    }
                                    default: {
                                        progress.setVisibility(View.GONE);
                                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                        Snackbar snackbar = Snackbar
                                                .make(llMain, jsonObject.getString("message"), Snackbar.LENGTH_LONG);

                                        snackbar.show();
                                        break;
                                    }
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

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {

            case 0: {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    return;
                } else {
                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                    alertDialogBuilder.setMessage("Kindly grant all permission, we respect your privacy and data!");
                    alertDialogBuilder.setPositiveButton("YES",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    recheckPermission(0);
                                }
                            });

                    alertDialogBuilder.setNegativeButton("DISMISS", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }

            }

            case 2: {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (!checkCameraPermission()) {
                        ActivityCompat.requestPermissions((Activity) context,
                                new String[]{Manifest.permission.CAMERA},
                                0);
                    }
                } else {
                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                    alertDialogBuilder.setMessage("Kindly grant all permission, we respect your privacy and data!");
                    alertDialogBuilder.setPositiveButton("YES",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    recheckPermission(2);
                                }
                            });

                    alertDialogBuilder.setNegativeButton("DISMISS", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            }
        }
    }

    public void recheckPermission(int p) {
        if (p == 0) {
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.CAMERA},
                    p);
        } else {
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    p);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }
}