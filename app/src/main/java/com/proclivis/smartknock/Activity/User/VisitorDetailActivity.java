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
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
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
import com.proclivis.smartknock.Adapter.VisitorAutoCompleteAdapter;
import com.proclivis.smartknock.Common.ConnectivityStatus;
import com.proclivis.smartknock.Common.Constant;
import com.proclivis.smartknock.Common.Util;
import com.proclivis.smartknock.DataBase.DatabaseHelper;
import com.proclivis.smartknock.DataBase.VisitorDB;
import com.proclivis.smartknock.FCM.MyFirebaseMessagingService;
import com.proclivis.smartknock.Model.VisitorConfirm;
import com.proclivis.smartknock.R;
import com.proclivis.smartknock.Retrf.RestClient;


import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class VisitorDetailActivity extends AppCompatActivity implements VisitorAutoCompleteAdapter.ClickListener {

    Context context;
    LinearLayout toolbar, llBack, llHome;
    de.hdodenhof.circleimageview.CircleImageView imgProfile;
    EditText edtName, edtComingFrom, edtPurpose , edtMob;
    TextView mobileHint, nameHint, fromHint, purposeHint;
    AutoCompleteTextView edtMobile;
    Button btnNext;
    Boolean isImageTaken = false;
    RelativeLayout llMain, progress;
    ArrayList<VisitorDB> visitorDBs = new ArrayList<>();
    private DatabaseHelper db;
    int isImage = 0;

    Button btnPlus, btnMinus;
    EditText txtCount;
    VisitorAutoCompleteAdapter adapter;

    private FaceDetector detector;

    Boolean imageClick = false;
    Boolean snakeBar = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        switch (getResources().getConfiguration().orientation) {
            case Configuration.ORIENTATION_PORTRAIT:
                setContentView(R.layout.visitor_detail_activity_potrait);
                break;

            case Configuration.ORIENTATION_LANDSCAPE:
                setContentView(R.layout.visitor_detail_activity_landscape);
        }
        context = this;
        toolbar = findViewById(R.id.toolbar);
        llBack = findViewById(R.id.llBack);
        llHome = findViewById(R.id.llHome);

        imgProfile = findViewById(R.id.imgProfile);
        edtMobile = findViewById(R.id.edtMobile);
        edtName = findViewById(R.id.edtName);
        edtComingFrom = findViewById(R.id.edtComingFrom);
        edtPurpose = findViewById(R.id.edtPurpose);
        edtMob = findViewById(R.id.edtMob);
        btnNext = findViewById(R.id.btnNext);
        llMain = findViewById(R.id.llMain);
        progress = findViewById(R.id.progress);
        mobileHint = findViewById(R.id.mobileHint);
        nameHint = findViewById(R.id.nameHint);
        fromHint = findViewById(R.id.fromHint);
        purposeHint = findViewById(R.id.purposeHint);
        btnPlus = findViewById(R.id.btnPlus);
        btnMinus = findViewById(R.id.btnMinus);
        txtCount = findViewById(R.id.txtCount);


        if (Constant.USER_IMAGE_PATH != null) {
            Glide.with(this)
                    .load(Constant.USER_IMAGE_PATH)
                    .into(imgProfile);
            imgProfile.setImageURI(Constant.USER_IMAGE_PATH);
            isImageTaken = true;
            isImage = 1;
        }

        db = new DatabaseHelper(this);

        if (Util.ReadSharePreference(context, Constant.IS_VISITOR_FIRST_TIME).equals("yes") && Util.isOnline(context)) {
            getData();
        } else if (!Util.ReadSharePreference(context, Constant.VISITOR_API_CALL_DATE).equals(Util.getCurrentDateTime()) && Util.isOnline(context)) {
            getData();
        } else {
            visitorDBs = db.getAllVisitor();

            adapter = new VisitorAutoCompleteAdapter(context, visitorDBs);
            adapter.setOnItemClickListener(this);
            edtMobile.setAdapter(adapter);
        }


        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isImage = 0;
                openImageDialog();
            }
        });

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        edtMobile.setText(Constant.MOBILE_NO);
        edtMob.setText(Constant.MOBILE_NO);
        edtName.setText(Constant.NAME);
        edtComingFrom.setText(Constant.COMING_FROM);
        edtPurpose.setText(Constant.PURPOSE);
        txtCount.setText(String.valueOf(Constant.COUNT));

        detector = new FaceDetector.Builder(context)
                .setTrackingEnabled(false)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .setMode(FaceDetector.FAST_MODE)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();

        edtMobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (edtMobile.getText().toString().length() > 0) {
                    Constant.MOBILE_NO = edtMobile.getText().toString();
                    mobileHint.setVisibility(View.VISIBLE);
                    if (visitorDBs.size() > 0) {
                        for (int p = 0; p < visitorDBs.size(); p++) {
                            if (edtMobile.getText().toString().equals(visitorDBs.get(p).getMobileNo())) {
                                edtName.setText(visitorDBs.get(p).getName());
                                edtComingFrom.setText(visitorDBs.get(p).getComingFrom());
                                edtPurpose.setText(visitorDBs.get(p).getPurpose());
                                break;
                            }

                        }
                    }
                } else {
                    Constant.MOBILE_NO = "";
                    mobileHint.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        txtCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Util.hideSoftKeyboard(VisitorDetailActivity.this);
            }
        });
        edtMob.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (edtMob.getText().toString().length() > 0) {
                    Constant.MOBILE_NO = edtMob.getText().toString();
                    mobileHint.setVisibility(View.VISIBLE);
                    if (visitorDBs.size() > 0) {
                        for (int p = 0; p < visitorDBs.size(); p++) {
                            if (edtMob.getText().toString().equals(visitorDBs.get(p).getMobileNo())) {
                                edtName.setText(visitorDBs.get(p).getName());
                                edtComingFrom.setText(visitorDBs.get(p).getComingFrom());
                                edtPurpose.setText(visitorDBs.get(p).getPurpose());
                                break;
                            }
                            else
                            {
                                edtName.setText("");
                                edtComingFrom.setText("");
                                edtPurpose.setText("");

                            }
                        }
                    }
                } else {
                    Constant.MOBILE_NO = "";
                    mobileHint.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        edtName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (edtName.getText().toString().length() > 0) {
                    Constant.NAME = edtName.getText().toString();
                    nameHint.setVisibility(View.VISIBLE);
                } else {
                    Constant.NAME = "";
                    nameHint.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        edtComingFrom.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (edtComingFrom.getText().toString().length() > 0) {
                    Constant.COMING_FROM = edtComingFrom.getText().toString();
                    fromHint.setVisibility(View.VISIBLE);
                } else {
                    Constant.COMING_FROM = "";
                    fromHint.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        edtPurpose.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (edtPurpose.getText().toString().length() > 0) {
                    Constant.PURPOSE = edtPurpose.getText().toString();
                    purposeHint.setVisibility(View.VISIBLE);
                } else {
                    Constant.PURPOSE = "";
                    purposeHint.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edtMob.getText().toString().equals("")) {
                    edtMob.setError("Please enter mobile no");
                    edtMob.requestFocus();
                } else if (edtMob.getText().toString().length() != 10) {
                    edtMob.setError("Please enter valid mobile no");
                    edtMob.requestFocus();
                } else if (edtName.getText().toString().equals("")) {
                    edtName.setError("Please enter name");
                    edtName.requestFocus();
                } else if (edtComingFrom.getText().toString().equals("")) {
                    edtComingFrom.setError("Please enter coming from");
                    edtComingFrom.requestFocus();
                } else if (edtPurpose.getText().toString().equals("")) {
                    edtPurpose.setError("Please enter purpose");
                    edtPurpose.requestFocus();
                } else if (!isImageTaken) {
                    isImage = 1;
                    openImageDialog();
                } else {

                    if (Constant.faceDetect) {
                        homeClick = false;
                        Intent intent = new Intent(context, ToMeetActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                    } else {

                        snakeBar = true;
                        Snackbar snackbar = Snackbar
                                .make(llMain, "Sorry, let’s try that again. We must detect a face before you can proceed", Snackbar.LENGTH_LONG)
                                .setAction("ReCapture", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        homeClick = false;
                                        imageClick = true;
                                        Intent intent = new Intent(context , CameraActivity.class);
                                        startActivityForResult(intent, 2);
                                        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                                        /*Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                        startActivityForResult(intent, 0);*/
                                    }
                                });
                        snackbar.setActionTextColor(Color.RED);
                        snackbar.show();

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (!imageClick) {
                                    homeClick = false;
                                    Intent intent = new Intent(context , CameraActivity.class);
                                    startActivityForResult(intent, 2);
                                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                                    /*Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
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
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    edtName.requestFocus();
                    handled = true;
                }
                return handled;
            }
        });

        edtMob.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    edtName.requestFocus();
                    handled = true;
                }
                return handled;
            }
        });

        edtName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    handled = true;

                    int orientation = getResources().getConfiguration().orientation;
                    if (orientation == Configuration.ORIENTATION_PORTRAIT) {

                        edtComingFrom.requestFocus();
                    } else {
                        //code for landscape mode
                        txtCount.requestFocus();
                        Util.hideSoftKeyboard(VisitorDetailActivity.this);
                    }

                }
                return handled;
            }
        });

        edtPurpose.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    handled = true;
                    if (edtMob.getText().toString().equals("")) {
                        edtMob.setError("Please enter mobile no");
                        edtMob.requestFocus();
                    } else if (edtMob.getText().toString().length() != 10) {
                        edtMob.setError("Please enter valid mobile no");
                        edtMob.requestFocus();
                    } else if (edtName.getText().toString().equals("")) {
                        edtName.setError("Please enter name");
                        edtName.requestFocus();
                    } else if (edtComingFrom.getText().toString().equals("")) {
                        edtComingFrom.setError("Please enter coming from");
                        edtComingFrom.requestFocus();
                    } else if (edtPurpose.getText().toString().equals("")) {
                        edtPurpose.setError("Please enter purpose");
                        edtPurpose.requestFocus();
                    } else if (!isImageTaken) {
                        isImage = 1;
                        openImageDialog();
                    } else {
                        homeClick = false;
                        Intent intent = new Intent(context, ToMeetActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                    }
                }
                return handled;
            }
        });

        txtCount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (txtCount.getText().length()>0){
                    Constant.COUNT = Integer.parseInt(txtCount.getText().toString());
                } else {
                    txtCount.setText("0");
                    Constant.COUNT = 0;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constant.COUNT = Integer.parseInt(txtCount.getText().toString());
                Constant.COUNT = Constant.COUNT + 1;
                txtCount.setText(String.valueOf(Constant.COUNT));
            }
        });

        btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constant.COUNT = Integer.parseInt(txtCount.getText().toString());
                if (Constant.COUNT <= 0) {
                    return;
                }
                Constant.COUNT = Constant.COUNT - 1;
                txtCount.setText(String.valueOf(Constant.COUNT));
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

    private void getData() {

        progress.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        try {
            RestClient.getApi().get_visitors_customer_wise1(Util.ReadSharePreference(context, Constant.USER_ID),
                    new Callback<Response>() {
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

                                        db.insertVisitorsDB(confirm.getData());
                                        visitorDBs = db.getAllVisitor();

                                        Util.WriteSharePreference(context, Constant.IS_VISITOR_FIRST_TIME, "no");
                                        Util.WriteSharePreference(context, Constant.VISITOR_API_CALL_DATE, Util.getCurrentDateTime());

                                        adapter = new VisitorAutoCompleteAdapter(context, visitorDBs);
                                        adapter.setOnItemClickListener(VisitorDetailActivity.this);
                                        edtMobile.setAdapter(adapter);

                                        break;
                                    }
                                    default:

                                        progress.setVisibility(View.GONE);
                                        Util.logout(VisitorDetailActivity.this, llMain, confirm.getMessage());
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

    public void openImageDialog() {

        if (checkWriteExternalPermission()) {

            if (checkCameraPermission()) {
                if (snakeBar) {
                    imageClick = true;
                }
                homeClick = false;
                Intent intent = new Intent(context , CameraActivity.class);
                startActivityForResult(intent, 2);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                /*Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 0);*/
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

                    if (isImage == 1) {
                        if (Constant.faceDetect) {
                            homeClick = false;
                            Intent intent = new Intent(context, ToMeetActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                        } else {
                            snakeBar = true;
                            Snackbar snackbar = Snackbar
                                    .make(llMain, "Sorry, let’s try that again. We must detect a face before you can proceed", Snackbar.LENGTH_LONG)
                                    .setAction("ReCapture", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            homeClick = false;
                                            imageClick = true;
                                            Intent intent = new Intent(context , CameraActivity.class);
                                            startActivityForResult(intent, 2);
                                            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                                            /*Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                            startActivityForResult(intent, 0);*/
                                        }
                                    });
                            snackbar.setActionTextColor(Color.RED);
                            snackbar.show();

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (!imageClick) {
                                        homeClick = false;
                                        Intent intent = new Intent(context , CameraActivity.class);
                                        startActivityForResult(intent, 2);
                                        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                                        /*Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
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

                    if (isImage == 1) {
                        if (Constant.faceDetect) {
                            homeClick = false;
                            Intent intent = new Intent(context, ToMeetActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                        } else {
                            snakeBar = true;
                            Snackbar snackbar = Snackbar
                                    .make(llMain, "Sorry, let’s try that again. We must detect a face before you can proceed", Snackbar.LENGTH_LONG)
                                    .setAction("ReCapture", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            homeClick = false;
                                            imageClick = true;
                                            Intent intent = new Intent(context , CameraActivity.class);
                                            startActivityForResult(intent, 2);
                                            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                                            /*Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                            startActivityForResult(intent, 0);*/
                                        }
                                    });
                            snackbar.setActionTextColor(Color.RED);
                            snackbar.show();

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (!imageClick) {
                                        homeClick = false;
                                        Intent intent = new Intent(context , CameraActivity.class);
                                        startActivityForResult(intent, 2);
                                        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                                        /*Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
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
                    Intent intent = new Intent(context , CameraActivity.class);
                    startActivityForResult(intent, 2);
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                    /*Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, 0);*/
                }  else {
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
                        Intent intent = new Intent(context , CameraActivity.class);
                        startActivityForResult(intent, 2);
                        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                        /*Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, 0);*/

                    } else {
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
        imageClick = true;
        homeClick = false;
        Intent intent = new Intent(context, UserMainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }

    @Override
    public void onItemClick(VisitorDB searchedData) {
        Util.hideSoftKeyboard(VisitorDetailActivity.this);
        edtMobile.clearFocus();
        edtMobile.setText(searchedData.getMobileNo());
        edtMobile.setSelection(edtMobile.getText().length());
        edtName.setText(searchedData.getName());
        edtComingFrom.setText(searchedData.getComingFrom());
        edtPurpose.setText(searchedData.getPurpose());
    }

    private void processCameraPicture(Bitmap bitmap) {

        Frame frame = new Frame.Builder().setBitmap(bitmap).build();
        SparseArray<Face> faces = detector.detect(frame);
        Constant.faceDetect = faces.size() != 0;
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
                Util.uploadInVisitor(VisitorDetailActivity.this , llMain);
            }
        }
    };
}