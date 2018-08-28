package com.proclivis.smartknock.Activity.User;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.gson.Gson;
import com.proclivis.smartknock.Activity.CameraActivity;
import com.proclivis.smartknock.Activity.OtpExitActivity;
import com.proclivis.smartknock.Common.ConnectivityStatus;
import com.proclivis.smartknock.Common.Constant;
import com.proclivis.smartknock.Common.Util;
import com.proclivis.smartknock.DataBase.DatabaseHelper;
import com.proclivis.smartknock.DataBase.ExpressVisitorDB;
import com.proclivis.smartknock.DataBase.ExpressVisitorOfflineDb;
import com.proclivis.smartknock.FCM.MyFirebaseMessagingService;
import com.proclivis.smartknock.Model.ExpressVisitor;
import com.proclivis.smartknock.Model.VisitorConfirm;
import com.proclivis.smartknock.Model.VisitorConfirmVo;
import com.proclivis.smartknock.R;
import com.proclivis.smartknock.Retrf.RestClient;


import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class ExpressInActivity extends AppCompatActivity {

    Context context;
    LinearLayout toolbar, llBack, llHome;
    de.hdodenhof.circleimageview.CircleImageView imgProfile;
    TextView mobileHint;
    AutoCompleteTextView edtMobile;
    Button btnNext;
    Boolean isImageTaken = false;
    RelativeLayout llMain, progress;

    int flag = 0;
    boolean backed = false;

    private FaceDetector detector;
    boolean faceDetect = false;

    Boolean imageClick = false;
    Boolean snakeBar = false;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.express_in_activity);

        context = this;
        toolbar = findViewById(R.id.toolbar);
        llBack = findViewById(R.id.llBack);
        llHome = findViewById(R.id.llHome);

        imgProfile = findViewById(R.id.imgProfile);
        edtMobile = findViewById(R.id.edtMobile);
        btnNext = findViewById(R.id.btnNext);
        llMain = findViewById(R.id.llMain);
        progress = findViewById(R.id.progress);
        mobileHint = findViewById(R.id.mobileHint);

        if (Constant.USER_IMAGE_PATH != null) {
            Glide.with(this)
                    .load(Constant.USER_IMAGE_PATH)
                    .into(imgProfile);
            imgProfile.setImageURI(Constant.USER_IMAGE_PATH);
            isImageTaken = true;
            flag = 1;
        }

        db = new DatabaseHelper(this);
        if (Util.isOnline(context)) {
            if (Util.ReadSharePreference(context, Constant.IS_EXPRESS_FIRST_TIME).equals("0")) {
                setData();
            } else if (!Util.ReadSharePreference(context, Constant.EXPRESS_API_CALL_DATE).equals(Util.getCurrentDateTime())) {
                setData();
            }
        }
        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                flag = 1;
                openImageDialog();
            }
        });

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        Constant.MOBILE_NO = "";

        detector = new FaceDetector.Builder(context)
                .setTrackingEnabled(false)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .setMode(FaceDetector.FAST_MODE)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();

        if (!detector.isOperational()) {
            Snackbar snackbar = Snackbar
                    .make(llMain, "Your device is not support face detection yet , please update Google Play Services to use this feature", Snackbar.LENGTH_LONG);
            snackbar.show();
        }

        edtMobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (edtMobile.getText().toString().length() > 0) {
                    Constant.MOBILE_NO = edtMobile.getText().toString();
                    mobileHint.setVisibility(View.VISIBLE);

                } else {
                    Constant.MOBILE_NO = "";
                    mobileHint.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    Util.hideSoftKeyboard(ExpressInActivity.this);
                } catch (Exception e) {
                    Log.e("keyHide", e.toString());
                }

                if (edtMobile.getText().toString().equals("")) {
                    edtMobile.setError("Please enter mobile no");
                    edtMobile.requestFocus();
                } else if (edtMobile.getText().toString().length() != 10) {
                    edtMobile.setError("Please enter valid mobile no");
                    edtMobile.requestFocus();
                } else if (!isImageTaken) {
                    flag = 1;
                    openImageDialog();
                } else {

                    if (faceDetect) {

                        Confirm();
                    } else {
                        snakeBar = true;
                        Snackbar snackbar = Snackbar
                                .make(llMain, "Sorry, let’s try that again. We must detect a face before you can proceed", Snackbar.LENGTH_LONG)
                                .setAction("ReCapture", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        homeClick = false;
                                        Intent intent = new Intent(context, CameraActivity.class);
                                        startActivityForResult(intent, 2);
                                        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                                            /*Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                            intent.putExtra("android.intent.extras.CAMERA_FACING", 1);
                                            startActivityForResult(intent, 0);*/

                                    }
                                });
                        snackbar.setActionTextColor(Color.RED);
                        snackbar.show();

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (!imageClick && !backed) {
                                    homeClick = false;
                                    Intent intent = new Intent(context, CameraActivity.class);
                                    startActivityForResult(intent, 2);
                                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                                        /*Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                        intent.putExtra("android.intent.extras.CAMERA_FACING", 1);
                                        startActivityForResult(intent, 0);*/
                                }

                            }
                        }, 3000);
                    }
                }
            }
        });

        edtMobile.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;

                try {
                    Util.hideSoftKeyboard(ExpressInActivity.this);
                } catch (Exception e) {
                    Log.e("keyHide", e.toString());
                }

                if (actionId == EditorInfo.IME_ACTION_GO) {
                    handled = true;
                    if (edtMobile.getText().toString().equals("")) {
                        edtMobile.setError("Please enter mobile no");
                        edtMobile.requestFocus();
                    } else if (edtMobile.getText().toString().length() != 10) {
                        edtMobile.setError("Please enter valid mobile no");
                        edtMobile.requestFocus();
                    } else if (!isImageTaken) {
                        flag = 1;
                        openImageDialog();
                    } else {
                        Confirm();
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

    TypedFile imageUser = null;

    //Amit 19.06.18. Added Below function where insertion of offline record is handled
    public void insertOfflineExpressVistor(){
        ArrayList<ExpressVisitorDB> expressVisitorDBS = db.getAllExpressVisitorDB();
        Constant.expressVisitorOfflineDbs = new ExpressVisitorOfflineDb();

        if (expressVisitorDBS.size() > 0) {

            Boolean isExist = false;
            String time = Util.localeFormatedTime("yyyy-MM-dd HH:mm:ss");
            for (int i = 0; i < expressVisitorDBS.size(); i++) {
                if (edtMobile.getText().toString().equals(expressVisitorDBS.get(i).getMobileNo())) {
                    isExist = true;

                    File imageFile = new File(getRealPathFromURI(Constant.USER_IMAGE_PATH));
                    Bitmap realImage = BitmapFactory.decodeFile(imageFile.toString());
                    ByteArrayOutputStream b1 = new ByteArrayOutputStream();
                    realImage.compress(Bitmap.CompressFormat.JPEG, 100, b1);
                    byte[] b = b1.toByteArray();

                    String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);

                    Constant.expressVisitorOfflineDbs.setMobileNo(edtMobile.getText().toString());
                    Constant.expressVisitorOfflineDbs.setImage(encodedImage);
                    Constant.expressVisitorOfflineDbs.setDate_time_in(time);
                    Constant.expressVisitorOfflineDbs.setDate_time_out("");

                    Constant.expressVisitorOfflineDbs.setName(expressVisitorDBS.get(i).getName());
                    Constant.expressVisitorOfflineDbs.setComingFrom(expressVisitorDBS.get(i).getComing_from());
                    Constant.expressVisitorOfflineDbs.setPurpose(expressVisitorDBS.get(i).getPurpose());
                    Constant.expressVisitorOfflineDbs.setMember("");
                    Constant.expressVisitorOfflineDbs.setMemberName(expressVisitorDBS.get(i).getMember_name());
                    Constant.expressVisitorOfflineDbs.setCount("0");
                    Constant.expressVisitorOfflineDbs.setIs_sync("false");
                    Constant.expressVisitorOfflineDbs.setStatus("");
                    Constant.expressVisitorOfflineDbs.setReason("");

                    db.insertExpressVisitorOffline(Constant.expressVisitorOfflineDbs);

                }
            }

            if (isExist) {
                Snackbar snackbar = Snackbar
                        .make(llMain, "Visitor added successfully", Snackbar.LENGTH_LONG);
                snackbar.show();

                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        homeClick = false;
                        Intent intent = new Intent(context, ExpressConfirmActivity.class);
                        intent.putExtra("mode", "offline");
                        intent.putExtra("time", Util.getDateTime());
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                    }
                }, 1000);
            } else {
                Snackbar snackbar = Snackbar
                        .make(llMain, "Daily Visitor not found", Snackbar.LENGTH_LONG);
                snackbar.show();
            }

        } else {
            Snackbar snackbar = Snackbar
                    .make(llMain, "Daily Visitor not found", Snackbar.LENGTH_LONG);
            snackbar.show();
        }

    }
    public void Confirm() {
        if (!Util.isOnline(context)) {
            //Amit 19.06.18. Put the logic in a function
            insertOfflineExpressVistor();
            /*
            ArrayList<ExpressVisitorDB> expressVisitorDBS = db.getAllExpressVisitorDB();
            Constant.expressVisitorOfflineDbs = new ExpressVisitorOfflineDb();

            if (expressVisitorDBS.size() > 0) {

                Boolean isExist = false;
                String time = Util.localeFormatedTime("yyyy-MM-dd HH:mm:ss");
                for (int i = 0; i < expressVisitorDBS.size(); i++) {
                    if (edtMobile.getText().toString().equals(expressVisitorDBS.get(i).getMobileNo())) {
                        isExist = true;

                        File imageFile = new File(getRealPathFromURI(Constant.USER_IMAGE_PATH));
                        Bitmap realImage = BitmapFactory.decodeFile(imageFile.toString());
                        ByteArrayOutputStream b1 = new ByteArrayOutputStream();
                        realImage.compress(Bitmap.CompressFormat.JPEG, 100, b1);
                        byte[] b = b1.toByteArray();

                        String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);

                        Constant.expressVisitorOfflineDbs.setMobileNo(edtMobile.getText().toString());
                        Constant.expressVisitorOfflineDbs.setImage(encodedImage);
                        Constant.expressVisitorOfflineDbs.setDate_time_in(time);
                        Constant.expressVisitorOfflineDbs.setDate_time_out("");

                        Constant.expressVisitorOfflineDbs.setName(expressVisitorDBS.get(i).getName());
                        Constant.expressVisitorOfflineDbs.setComingFrom(expressVisitorDBS.get(i).getComing_from());
                        Constant.expressVisitorOfflineDbs.setPurpose(expressVisitorDBS.get(i).getPurpose());
                        Constant.expressVisitorOfflineDbs.setMember("");
                        Constant.expressVisitorOfflineDbs.setMemberName(expressVisitorDBS.get(i).getMember_name());
                        Constant.expressVisitorOfflineDbs.setCount("0");
                        Constant.expressVisitorOfflineDbs.setIs_sync("false");
                        Constant.expressVisitorOfflineDbs.setStatus("");
                        Constant.expressVisitorOfflineDbs.setReason("");

                        db.insertExpressVisitorOffline(Constant.expressVisitorOfflineDbs);

                    }
                }

                if (isExist) {
                    Snackbar snackbar = Snackbar
                            .make(llMain, "Visitor added successfully", Snackbar.LENGTH_LONG);
                    snackbar.show();

                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            homeClick = false;
                            Intent intent = new Intent(context, ExpressConfirmActivity.class);
                            intent.putExtra("mode", "offline");
                            intent.putExtra("time", Util.getDateTime());
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                        }
                    }, 1000);
                } else {
                    Snackbar snackbar = Snackbar
                            .make(llMain, "Daily Visitor not found", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }

            } else {
                Snackbar snackbar = Snackbar
                        .make(llMain, "Daily Visitor not found", Snackbar.LENGTH_LONG);
                snackbar.show();
            }*/

        } else {
            File image3 = new File(getRealPathFromURI(Constant.USER_IMAGE_PATH));
            imageUser = new TypedFile("image/*", image3);

            AddVisitor(new TypedString(edtMobile.getText().toString()), new TypedString(Util.ReadSharePreference(context, Constant.USER_INSTALLATION_ID)), imageUser);
        }
    }

    public void AddVisitor(TypedString mobile, TypedString installation_id, TypedFile visitor_image) {
        progress.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        try {
            //Amit 19.06.18. Passing cirrent time in online mode too
            TypedString time = new TypedString(Util.localeFormatedTime("yyyy-MM-dd HH:mm:ss"));

            RestClient.getApi().add_daily_visitor_while_IN_without_OUT_validation(mobile, installation_id, new TypedString(Util.ReadSharePreference(context, Constant.USER_MOBILE_NO)), time, visitor_image, new Callback<Response>() {
                @Override
                public void success(Response response, Response response2) {
                    try {

                        JSONObject jsonObject = new JSONObject(Util.getString(response.getBody().in()));

                        Gson gson = new Gson();
                        VisitorConfirm confirm = gson.fromJson(jsonObject.toString(), VisitorConfirm.class);

                        switch (confirm.getStatus()) {
                            case "true":

                                progress.setVisibility(View.GONE);

                                final ArrayList<VisitorConfirmVo> visitorConfirmVos = confirm.getData();

                                Snackbar snackbar = Snackbar
                                        .make(llMain, confirm.getMessage(), Snackbar.LENGTH_LONG);
                                snackbar.show();

                                ArrayList<ExpressVisitorDB> expressVisitorDBS = db.getAllExpressVisitorDB();
                                Constant.expressVisitorOfflineDbs = new ExpressVisitorOfflineDb();

                                if (expressVisitorDBS.size() > 0) {

                                    for (int i = 0; i < expressVisitorDBS.size(); i++) {
                                        if (edtMobile.getText().toString().equals(expressVisitorDBS.get(i).getMobileNo())) {

                                            File imageFile = new File(getRealPathFromURI(Constant.USER_IMAGE_PATH));
                                            Bitmap realImage = BitmapFactory.decodeFile(imageFile.toString());
                                            ByteArrayOutputStream b1 = new ByteArrayOutputStream();
                                            realImage.compress(Bitmap.CompressFormat.JPEG, 100, b1);
                                            byte[] b = b1.toByteArray();

                                            String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);

                                            Constant.expressVisitorOfflineDbs.setMobileNo(edtMobile.getText().toString());
                                            Constant.expressVisitorOfflineDbs.setImage(encodedImage);
                                            Constant.expressVisitorOfflineDbs.setDate_time_in(Util.localeFormatedTime("yyyy-MM-dd HH:mm:ss"));
                                            Constant.expressVisitorOfflineDbs.setDate_time_out("");

                                            Constant.expressVisitorOfflineDbs.setName(expressVisitorDBS.get(i).getName());
                                            Constant.expressVisitorOfflineDbs.setComingFrom(expressVisitorDBS.get(i).getComing_from());
                                            Constant.expressVisitorOfflineDbs.setPurpose(expressVisitorDBS.get(i).getPurpose());
                                            Constant.expressVisitorOfflineDbs.setMember("");
                                            Constant.expressVisitorOfflineDbs.setMemberName(expressVisitorDBS.get(i).getMember_name());
                                            Constant.expressVisitorOfflineDbs.setCount("0");
                                            Constant.expressVisitorOfflineDbs.setIs_sync("true");
                                            Constant.expressVisitorOfflineDbs.setStatus("");
                                            Constant.expressVisitorOfflineDbs.setReason("");

                                            db.insertExpressVisitorOffline(Constant.expressVisitorOfflineDbs);

                                        }
                                    }
                                }

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        homeClick = false;
                                        Intent intent = new Intent(context, ExpressConfirmActivity.class);
                                        intent.putParcelableArrayListExtra("data", visitorConfirmVos);
                                        intent.putExtra("mode", "online");
                                        intent.putExtra("time", Util.getDateTime());
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                                    }
                                }, 1000);

                                break;

                            case "false":
                                progress.setVisibility(View.GONE);
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                snackbar = Snackbar
                                        .make(llMain, "Sorry. You are not registered as an Express Visitor. Please Check-In using the IN button", Snackbar.LENGTH_LONG);
                                snackbar.show();
                                break;

                            default:
                                progress.setVisibility(View.GONE);
                                Util.logout(ExpressInActivity.this, llMain, confirm.getMessage());
                                break;
                        }
                    } catch (Exception e) {
                        progress.setVisibility(View.GONE);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                        e.printStackTrace();
                        //Amit:19.06.18 Commented the Message and called the load into offline database
                        //Snackbar snackbar = Snackbar
                          //      .make(llMain, "Something went wrong, please try again", Snackbar.LENGTH_LONG);
                        //snackbar.show();
                        insertOfflineExpressVistor();
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    progress.setVisibility(View.GONE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    //Amit:19.06.18 Commented the Message and called the load into offline database
                    //Snackbar snackbar = Snackbar
                    //      .make(llMain, "Something went wrong, please try again", Snackbar.LENGTH_LONG);
                    //snackbar.show();
                    insertOfflineExpressVistor();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openImageDialog() {
        if (checkWriteExternalPermission()) {

            if (checkCameraPermission()) {

                if (snakeBar) {
                    imageClick = true;
                }

                homeClick = false;

                Intent intent = new Intent(context, CameraActivity.class);
                startActivityForResult(intent, 2);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                /*Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 0 );*/
            } else {
                ActivityCompat.requestPermissions((Activity) context,
                        new String[]{Manifest.permission.CAMERA},
                        0);
            }

        } else {
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    2);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {

                    Bitmap photo = (Bitmap) imageReturnedIntent.getExtras().get("data");
                    Uri tempUri = getImageUri(getApplicationContext(), photo);
                    File imageFile = new File(getRealPathFromURI(tempUri));
                    Constant.USER_IMAGE_PATH = Uri.fromFile(imageFile);
                    isImageTaken = true;
                    imgProfile.setImageBitmap(photo);

                    try {
                        processCameraPicture(photo);
                    } catch (Exception e) {
                        Snackbar snackbar = Snackbar
                                .make(llMain, "Failed to load Image", Snackbar.LENGTH_LONG);

                        snackbar.show();
                    }

                    if (flag == 1) {
                        if (faceDetect) {
                            if (edtMobile.getText().toString().equals("")) {
                                edtMobile.setError("Please enter mobile no");
                                edtMobile.requestFocus();
                            } else if (edtMobile.getText().toString().length() != 10) {
                                edtMobile.setError("Please enter valid mobile no");
                                edtMobile.requestFocus();
                            } else {
                                Confirm();
                            }
                        } else {
                            snakeBar = true;
                            Snackbar snackbar = Snackbar
                                    .make(llMain, "Sorry, let’s try that again. We must detect a face before you can proceed", Snackbar.LENGTH_LONG)
                                    .setAction("ReCapture", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            homeClick = false;
                                            imageClick = true;

                                            Intent intent = new Intent(context, CameraActivity.class);
                                            startActivityForResult(intent, 2);
                                            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                                            /*Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                            intent.putExtra("android.intent.extras.CAMERA_FACING", 1);
                                            startActivityForResult(intent, 0);*/
                                        }
                                    });
                            snackbar.setActionTextColor(Color.RED);
                            snackbar.show();

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (!imageClick && !backed) {
                                        homeClick = false;
                                        Intent intent = new Intent(context, CameraActivity.class);
                                        startActivityForResult(intent, 2);
                                        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                                        /*Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                        intent.putExtra("android.intent.extras.CAMERA_FACING", 1);
                                        startActivityForResult(intent, 0);*/
                                    }
                                }
                            }, 3000);
                        }
                    }
                }
                break;

            case 2:
                if (resultCode == 2) {

                    String uri = imageReturnedIntent.getStringExtra("image_uri");
                    Uri tempUri = Uri.parse(uri);
                    File imageFile = new File(getRealPathFromURI(tempUri));
                    Bitmap photo = BitmapFactory.decodeFile(imageFile.toString());

                    Constant.USER_IMAGE_PATH = Uri.fromFile(imageFile);
                    isImageTaken = true;
                    imgProfile.setImageBitmap(photo);

                    try {
                        processCameraPicture(photo);
                    } catch (Exception e) {
                        Snackbar snackbar = Snackbar
                                .make(llMain, "Failed to load Image", Snackbar.LENGTH_LONG);

                        snackbar.show();
                    }

                    if (flag == 1) {
                        if (faceDetect) {
                            if (edtMobile.getText().toString().equals("")) {
                                edtMobile.setError("Please enter mobile no");
                                edtMobile.requestFocus();
                            } else if (edtMobile.getText().toString().length() != 10) {
                                edtMobile.setError("Please enter valid mobile no");
                                edtMobile.requestFocus();
                            } else {
                                Confirm();
                            }
                        } else {
                            snakeBar = true;
                            Snackbar snackbar = Snackbar
                                    .make(llMain, "Sorry, let’s try that again. We must detect a face before you can proceed", Snackbar.LENGTH_LONG)
                                    .setAction("ReCapture", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            homeClick = false;
                                            imageClick = true;
                                            Intent intent = new Intent(context, CameraActivity.class);
                                            startActivityForResult(intent, 2);
                                            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                                            /*Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                            intent.putExtra("android.intent.extras.CAMERA_FACING", 1);
                                            startActivityForResult(intent, 0);*/
                                        }
                                    });
                            snackbar.setActionTextColor(Color.RED);
                            snackbar.show();

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (!imageClick && !backed) {
                                        homeClick = false;
                                        Intent intent = new Intent(context, CameraActivity.class);
                                        startActivityForResult(intent, 2);
                                        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                                        /*Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                        intent.putExtra("android.intent.extras.CAMERA_FACING", 1);
                                        startActivityForResult(intent, 0);*/
                                    }
                                }
                            }, 3000);
                        }
                    }
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {

            case 0: {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (snakeBar) {
                        imageClick = true;
                    }
                    homeClick = false;
                    Intent intent = new Intent(context, CameraActivity.class);
                    startActivityForResult(intent, 2);
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                    /*Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra("android.intent.extras.CAMERA_FACING", 1);
                    startActivityForResult(intent, 0);*/
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

                    if (checkCameraPermission()) {
                        if (snakeBar) {
                            imageClick = true;
                        }
                        homeClick = false;
                        Intent intent = new Intent(context, CameraActivity.class);
                        startActivityForResult(intent, 2);
                        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                        /*Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, 0);*/

                    } else {
                        ActivityCompat.requestPermissions((Activity) context,
                                new String[]{Manifest.permission.CAMERA}, 0);
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

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
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

    @Override
    public void onBackPressed() {
        homeClick = false;
        backed = true;
        super.onBackPressed();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }

    private void processCameraPicture(Bitmap bitmap) {

        Frame frame = new Frame.Builder().setBitmap(bitmap).build();
        SparseArray<Face> faces = detector.detect(frame);
        faceDetect = faces.size() != 0;
    }

    private void setData() {

        progress.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        try {
            RestClient.getApi().get_daily_visitor_customer_ins_id(Util.ReadSharePreference(context, Constant.USER_INSTALLATION_ID),
                    new Callback<Response>() {
                        @Override
                        public void success(Response response, Response response2) {
                            try {

                                JSONObject jsonObject = new JSONObject(Util.getString(response.getBody().in()));

                                Gson gson = new Gson();
                                ExpressVisitor confirm = gson.fromJson(jsonObject.toString(), ExpressVisitor.class);
                                switch (confirm.getStatus()) {
                                    case "true": {

                                        progress.setVisibility(View.GONE);
                                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                        db.insertAllExpressVisitorDB(confirm.getData());


                                        Util.WriteSharePreference(context, Constant.IS_EXPRESS_FIRST_TIME, "1");
                                        Util.WriteSharePreference(context, Constant.EXPRESS_API_CALL_DATE, Util.getCurrentDateTime());


                                        break;
                                    }
                                    default:

                                        progress.setVisibility(View.GONE);
                                        Util.logout(ExpressInActivity.this, llMain, confirm.getMsg());
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
        } catch (
                Exception e)

        {
            progress.setVisibility(View.GONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            Log.e("errorGetMember==>", e.toString());
        }

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
                Util.uploadInVisitor(ExpressInActivity.this , llMain);
            }
        }
    };
}