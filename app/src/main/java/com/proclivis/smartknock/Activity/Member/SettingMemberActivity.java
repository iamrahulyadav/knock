package com.proclivis.smartknock.Activity.Member;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.proclivis.smartknock.Activity.LoginActivity;
import com.proclivis.smartknock.Adapter.VisitorAdapter;
import com.proclivis.smartknock.Common.Constant;
import com.proclivis.smartknock.Common.ResizeImage;
import com.proclivis.smartknock.Common.Util;
import com.proclivis.smartknock.DataBase.DatabaseHelper;
import com.proclivis.smartknock.Model.GetMemberVisitor;
import com.proclivis.smartknock.Model.MemberDetail;
import com.proclivis.smartknock.Model.MemberProfile;
import com.proclivis.smartknock.Model.VisitorConfirm;
import com.proclivis.smartknock.R;
import com.proclivis.smartknock.Retrf.RestClient;


import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;

public class SettingMemberActivity extends AppCompatActivity {

    Context context;
    Toolbar toolbar;
    Activity activity;
    de.hdodenhof.circleimageview.CircleImageView imgProfile;
    TextView txtMobile;
    EditText txtName, txtEmail;
    CheckBox chkEmail;
    Button btnLogOut;
    RecyclerView recycler;
    VisitorAdapter adapter;
    @SuppressLint("StaticFieldLeak")
    public static RelativeLayout llMain , progress;
    ArrayList<MemberDetail> memberDetails = new ArrayList<>();
    private DatabaseHelper db;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_member_activity);
        context = this;
        activity = this;
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Profile");
        setSupportActionBar(toolbar);
        if (getSupportActionBar()!=null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        db = new DatabaseHelper(this);

        imgProfile = findViewById(R.id.imgProfile);
        txtMobile = findViewById(R.id.txtMobile);
        txtName = findViewById(R.id.txtName);
        txtEmail = findViewById(R.id.txtEmail);
        chkEmail = findViewById(R.id.chkEmail);
        btnLogOut = findViewById(R.id.btnLogOut);
        llMain = findViewById(R.id.llMain);
        progress = findViewById(R.id.progress);
        recycler = findViewById(R.id.recycler);
        imgProfile = findViewById(R.id.imgProfile);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recycler.setLayoutManager(mLayoutManager);
        recycler.setItemAnimator(new DefaultItemAnimator());
        adapter = new VisitorAdapter();

        txtMobile.setText("+91 " + Util.ReadSharePreference(context, Constant.MEMBER_MOBILE_NO));
        txtName.setText(Util.ReadSharePreference(context, Constant.MEMBER_NAME));
        txtEmail.setText(Util.ReadSharePreference(context, Constant.MEMBER_EMAIL));

        if (Util.isOnline(context)) {
            getVisitors();
            getMemberDetails();
        }
        else
            {
            Snackbar snackbar = Snackbar
                    .make(llMain, "No internet connection", Snackbar.LENGTH_LONG);
            snackbar.show();
        }

        if (Util.ReadSharePreference(context, Constant.MEMBER_EMAIL_FLAG).equals("yes")) {
            chkEmail.setChecked(true);
        } else {
            chkEmail.setChecked(false);
        }

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        if (!Util.ReadSharePreference(context, Constant.MEMBER_PROFILE_IMAGE).equalsIgnoreCase("")) {

            if (Util.ReadSharePreference(context, Constant.MEMBER_PROFILE_IMAGE).toLowerCase().contains("http://")) {
                Glide.with(context)
                        .load(Util.ReadSharePreference(context, Constant.MEMBER_PROFILE_IMAGE))
                        .into(imgProfile);
            } else {
                byte[] b1 = Base64.decode(Util.ReadSharePreference(context, Constant.MEMBER_PROFILE_IMAGE), Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(b1, 0, b1.length);
                imgProfile.setImageBitmap(bitmap);
            }
        }

        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, 0);
            }
        });

        chkEmail.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (Util.isOnline(context)) {
                    if (b) {
                        if(txtEmail.getText().length() > 0)
                            callEmailFlag("yes");
                        else{
                            txtEmail.requestFocus();
                            InputMethodManager imm=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.showSoftInput(txtEmail,InputMethodManager.SHOW_IMPLICIT);
                        }
                    } else {
                        callEmailFlag("no");
                        txtEmail.setError(null);
                    }
                } else {
                    Snackbar snackbar = Snackbar
                            .make(llMain, "No internet connection", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        });

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(context)
                        .setMessage("Are you sure you want to Logout?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (Util.isOnline(context)) {
                                    setFcm();
                                } else {
                                    Snackbar snackbar = Snackbar
                                            .make(llMain, "No internet connection", Snackbar.LENGTH_LONG);
                                    snackbar.show();
                                }
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });

        txtName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    handled = true;

                    if (txtName.getText().length() > 0) {

                        if (Util.isOnline(context)) {
                            Util.hideSoftKeyboard(SettingMemberActivity.this);
                            changeDetail("name", txtName.getText().toString(), Util.ReadSharePreference(context, Constant.MEMBER_EMAIL));

                        } else {
                            Snackbar snackbar = Snackbar
                                    .make(llMain, "No internet connection", Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }

                    } else {
                        txtName.setError("Name can not be blank");
                        txtName.requestFocus();
                    }
                }
                return handled;
            }
        });

        txtEmail.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    handled = true;

                    if (txtEmail.getText().length() > 0 || !chkEmail.isChecked()) {
                        Util.hideSoftKeyboard(SettingMemberActivity.this);

                        //Amit 22.06.18. Added the regex to validate the Email ID that is entered. If not matched a message will
                        //be displayed toenter correct email id.
                        final Pattern VALID_EMAIL_ADDRESS_REGEX =
                                Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
                        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(txtEmail.getText());
                        if(matcher.find() || txtEmail.getText().length()==0) {
                            if (Util.isOnline(context)) {

                                changeDetail("email", Util.ReadSharePreference(context, Constant.MEMBER_NAME), txtEmail.getText().toString());
                                //Amit 22.06.18. Added this logic to update the email Flag along with Email update
                                if(txtEmail.getText().length() > 0 && chkEmail.isChecked()) callEmailFlag("yes");

                            } else {
                                Snackbar snackbar = Snackbar
                                        .make(llMain, "No internet connection", Snackbar.LENGTH_LONG);
                                snackbar.show();
                            }
                        } else{
                            Snackbar snackbar = Snackbar
                                    .make(llMain, "Please enter correct Email Id", Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }

                    } else {
                        //Amit 22.06.18. Added condition to ensure Email is mandatory only if Check Email is selected
                        txtEmail.setError("Email can not be blank");
                        txtEmail.requestFocus();

                    }
                }
                return handled;
            }
        });
    }

    public void changeDetail(final String type, String name, String email) {
        progress.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        try {
            RestClient.getApi().update_member_name_email1(Util.ReadSharePreference(context, Constant.MEMBER_MOBILE_NO),
                    name, email,
                    new Callback<Response>() {
                        @Override
                        public void success(Response response, Response response2) {
                            try {

                                JSONObject jsonObject = new JSONObject(Util.getString(response.getBody().in()));

                                progress.setVisibility(View.GONE);
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                switch (jsonObject.getString("status")) {
                                    case "true":

                                        Util.WriteSharePreference(context, Constant.MEMBER_NAME, txtName.getText().toString());


                                        Util.WriteSharePreference(context, Constant.MEMBER_EMAIL, txtEmail.getText().toString());


                                        if (type.equals("name")) {
                                            Snackbar snackbar = Snackbar
                                                    .make(llMain, "Name updated successfully", Snackbar.LENGTH_LONG);
                                            snackbar.show();
                                        } else {
                                            Snackbar snackbar = Snackbar
                                                    .make(llMain, "Email updated successfully", Snackbar.LENGTH_LONG);
                                            snackbar.show();
                                        }
                                        break;
                                    case "false":

                                        Snackbar snackbar = Snackbar
                                                .make(llMain, jsonObject.getString("message"), Snackbar.LENGTH_LONG);
                                        snackbar.show();

                                        break;
                                    default:
                                        Util.logout(SettingMemberActivity.this, llMain, jsonObject.getString("message"));
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

    //API call for get Member details

    public void getMemberDetails() {
        progress.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        try {
            RestClient.getApi().get_member_name_email(Util.ReadSharePreference(context, Constant.MEMBER_MOBILE_NO),
                    Util.ReadSharePreference(context, Constant.MEMBER_MOBILE_NO),
                    new Callback<Response>() {
                        @Override
                        public void success(Response response, Response response2) {
                            try {

                                JSONObject jsonObject = new JSONObject(Util.getString(response.getBody().in()));
                                Gson gson = new Gson();
                                MemberProfile detail = gson.fromJson(jsonObject.toString(), MemberProfile.class);


                                progress.setVisibility(View.GONE);
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                switch (jsonObject.getString("status")) {
                                    case "true":

//                                        Util.WriteSharePreference(context, Constant.MEMBER_NAME, txtName.getText().toString());
//                                        Util.WriteSharePreference(context, Constant.MEMBER_EMAIL, txtEmail.getText().toString());

                                        txtName.setText(detail.getData().getName());
                                        txtEmail.setText(detail.getData().getEmail());
                                        txtMobile.setText("+91"+detail.getData().getMobileNo());
                                        Glide.with(context)
                                                .load(detail.getData().getMemberprofileimage())
                                                .into(imgProfile);

                                        if (detail.getData().getEmailflag().equalsIgnoreCase("yes")) {
                                            chkEmail.setChecked(true);
                                        } else {
                                            chkEmail.setChecked(false);
                                        }


                                        break;
                                    case "false":
                                        Snackbar snackbar = Snackbar
                                                .make(llMain, jsonObject.getString("message"), Snackbar.LENGTH_LONG);
                                        snackbar.show();

                                        break;
                                    default:
                                        Util.logout(SettingMemberActivity.this, llMain, jsonObject.getString("message"));
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









































///////////////////////////////
    public void callEmailFlag(final String value) {
        progress.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        try {
            RestClient.getApi().update_email_flag_of_member1(Util.ReadSharePreference(context, Constant.MEMBER_MOBILE_NO), value,
                    new Callback<Response>() {
                        @Override
                        public void success(Response response, Response response2) {
                            try {

                                JSONObject jsonObject = new JSONObject(Util.getString(response.getBody().in()));

                                progress.setVisibility(View.GONE);
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                switch (jsonObject.getString("status")) {
                                    case "true": {

                                        Util.WriteSharePreference(context, Constant.MEMBER_EMAIL_FLAG, value);
                                        Snackbar snackbar = Snackbar
                                                .make(llMain, jsonObject.getString("message"), Snackbar.LENGTH_LONG);
                                        snackbar.show();
                                        break;
                                    }
                                    case "false": {
                                        Snackbar snackbar = Snackbar
                                                .make(llMain, jsonObject.getString("message"), Snackbar.LENGTH_LONG);
                                        snackbar.show();
                                        break;
                                    }
                                    default:
                                        Util.logout(SettingMemberActivity.this, llMain, jsonObject.getString("message"));
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

    public void setFcm() {

        progress.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        try {
            RestClient.getApi().logout(Util.ReadSharePreference(context, Constant.MEMBER_MOBILE_NO), Util.ReadSharePreference(context, Constant.FCM_ID),
                    new Callback<Response>() {
                        @Override
                        public void success(Response response, Response response2) {
                            try {

                                JSONObject jsonObject = new JSONObject(Util.getString(response.getBody().in()));

                                if (jsonObject.getString("status").equals("true")) {

                                    progress.setVisibility(View.GONE);
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                    SharedPreferences preferences = getSharedPreferences(Constant.SHARE_PREF, Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = preferences.edit();
                                    editor.clear();
                                    editor.apply();

                                    Intent intent = new Intent(context, LoginActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();

                                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                                } else {
                                    progress.setVisibility(View.GONE);
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    Snackbar snackbar = Snackbar
                                            .make(llMain, "Error in Logout", Snackbar.LENGTH_LONG);

                                    snackbar.show();
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

    TypedFile imageUser = null;

    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {

            case 0:
                if (resultCode == RESULT_OK) {

                    Uri selectedImage = imageReturnedIntent.getData();
                    File inputImageFile = new File(getRealPathFromURI(selectedImage));

                    imageUser = new TypedFile("image/*", inputImageFile);

                    //Amit 4.7.18 Image Resize. Added below code to resize the image before sending to server
                    final File imageFile = new ResizeImage(this).compressImage(inputImageFile.getAbsolutePath());
                    imageUser = new TypedFile("image/*",imageFile);
                    //End Amit 4.7.18 Image Resize. Added below code to resize the image before sending to server

                    progress.setVisibility(View.VISIBLE);
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    try {
                        RestClient.getApi().update_member_profile_image1(new TypedString(Util.ReadSharePreference(context, Constant.MEMBER_MOBILE_NO)), imageUser, new Callback<Response>() {
                            @Override
                            public void success(Response response, Response response2) {
                                try {

                                    JSONObject jsonObject = new JSONObject(Util.getString(response.getBody().in()));

                                    Gson gson = new Gson();
                                    VisitorConfirm confirm = gson.fromJson(jsonObject.toString(), VisitorConfirm.class);

                                    switch (confirm.getStatus()) {
                                        case "true": {

                                            progress.setVisibility(View.GONE);
                                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                            Bitmap realImage = BitmapFactory.decodeFile(imageFile.toString());
                                            ByteArrayOutputStream baas = new ByteArrayOutputStream();
                                            realImage.compress(Bitmap.CompressFormat.JPEG, 100, baas);
                                            byte[] b = baas.toByteArray();

                                            String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);

                                            Log.e("image", encodedImage);

                                            Util.WriteSharePreference(context, Constant.MEMBER_PROFILE_IMAGE, encodedImage);
                                            if (!Util.ReadSharePreference(context, Constant.MEMBER_PROFILE_IMAGE).equalsIgnoreCase("")) {
                                                byte[] b1 = Base64.decode(Util.ReadSharePreference(context, Constant.MEMBER_PROFILE_IMAGE), Base64.DEFAULT);
                                                Bitmap bitmap = BitmapFactory.decodeByteArray(b1, 0, b1.length);
                                                imgProfile.setImageBitmap(bitmap);
                                            }

                                            Snackbar snackbar = Snackbar
                                                    .make(llMain, "Image Uploaded Successfully", Snackbar.LENGTH_LONG);
                                            snackbar.show();

                                            break;
                                        }
                                        case "false": {

                                            Snackbar snackbar = Snackbar
                                                    .make(llMain, jsonObject.getString("message"), Snackbar.LENGTH_LONG);
                                            snackbar.show();
                                            break;
                                        }
                                        default:
                                            progress.setVisibility(View.GONE);
                                            Constant.selectedMember = new ArrayList<>();
                                            Util.logout(SettingMemberActivity.this, llMain, confirm.getMessage());
                                            break;
                                    }
                                } catch (Exception e) {
                                    progress.setVisibility(View.GONE);
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                    e.printStackTrace();
                                    Snackbar snackbar = Snackbar
                                            .make(llMain, "Something went wrong, please try again", Snackbar.LENGTH_LONG);
                                    snackbar.show();
                                }
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                progress.setVisibility(View.GONE);
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                Snackbar snackbar = Snackbar
                                        .make(llMain, "Something went wrong, please try again", Snackbar.LENGTH_LONG);
                                snackbar.show();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    break;
                }


        }
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    public void getVisitors() {

        progress.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        String flagValue;

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
                                GetMemberVisitor getMemberVisitor = gson.fromJson(jsonObject.toString(), GetMemberVisitor.class);

                                switch (getMemberVisitor.getStatus()) {
                                    case "true":

                                        progress.setVisibility(View.GONE);
                                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                        if (getMemberVisitor.getMemberDetails().size() > 0) {
                                            memberDetails = getMemberVisitor.getMemberDetails();

                                            if (memberDetails.size() > 1) {
                                                for (int i = 0; i < memberDetails.size(); i++) {
                                                    if (i < memberDetails.size() - 1) {
                                                        if (memberDetails.get(i).getAddress1().equals(memberDetails.get(i + 1).getAddress1())) {
                                                            memberDetails.remove(i);
                                                        }
                                                    }
                                                }
                                            }

                                            adapter = new VisitorAdapter(activity , memberDetails);
                                            recycler.setAdapter(adapter);


                                        } else {

                                            Snackbar snackbar = Snackbar
                                                    .make(llMain, "No data found", Snackbar.LENGTH_LONG);
                                            snackbar.show();
                                        }
                                        break;
                                    case "false":

                                        progress.setVisibility(View.GONE);
                                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                        memberDetails = db.getMemberSoc();
                                        if (memberDetails.size() > 1) {
                                            for (int i = 0; i < memberDetails.size(); i++) {
                                                if (i < memberDetails.size() - 1) {
                                                    if (memberDetails.get(i).getAddress1().equals(memberDetails.get(i + 1).getAddress1())) {
                                                        memberDetails.remove(i);
                                                    }
                                                }
                                            }
                                        }
                                        adapter = new VisitorAdapter( activity , memberDetails);
                                        recycler.setAdapter(adapter);
                                        break;
                                    default:

                                        progress.setVisibility(View.GONE);
                                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                        Util.logout(SettingMemberActivity.this, llMain, getMemberVisitor.getMessage());
                                        break;
                                }

                            } catch (Exception e) {
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

