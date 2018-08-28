package com.proclivis.smartknock.Activity.User;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.proclivis.smartknock.Common.ConnectivityStatus;
import com.proclivis.smartknock.Common.Constant;
import com.proclivis.smartknock.Common.Util;
import com.proclivis.smartknock.FCM.MyFirebaseMessagingService;
import com.proclivis.smartknock.R;
import com.proclivis.smartknock.Retrf.RestClient;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SettingUserActivity extends AppCompatActivity {

    LinearLayout toolbar, llBack, imgLayoutLandScape , llSetting;
    Context context;
    TextView txtUserName;
    EditText edtName, edtTextMessage;
    ImageView imgProfile1, imgProfile2, imgUploaded1, imgUploaded2, editImg1, editImg2, imgCancel1, imgCancel2;

    RelativeLayout llMain, progress;

    final static int GALLERY_IMF_PROFILE_1 = 5;
    final static int GALLERY_IMF_PROFILE_2 = 6;
    final static int GALLERY_UPLOADED_1 = 7;
    final static int GALLERY_UPLOADED_2 = 8;

    LinearLayout llNoImage1, llNoImage2;
    FrameLayout imgFrame1, imgFrame2;

    info.hoang8f.android.segmented.SegmentedGroup rgp;
    RadioButton rbBasic , rbPro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        switch (getResources().getConfiguration().orientation) {
            case Configuration.ORIENTATION_PORTRAIT:
                setContentView(R.layout.setting_user_activity_potrait);
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                toolbar = findViewById(R.id.toolbar);
                break;

            case Configuration.ORIENTATION_LANDSCAPE:
                setContentView(R.layout.setting_user_activity_landscape);
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                imgLayoutLandScape = findViewById(R.id.imgLayout);
        }
        context = this;
        llBack = findViewById(R.id.llBack);
        llBack.setVisibility(View.VISIBLE);

        txtUserName = findViewById(R.id.txtUserName);
        edtName = findViewById(R.id.edtName);
        edtTextMessage = findViewById(R.id.edtTextMessege);
        imgProfile1 = findViewById(R.id.imgProfile1);
        imgProfile2 = findViewById(R.id.imgProfile2);
        imgUploaded1 = findViewById(R.id.img1);
        imgUploaded2 = findViewById(R.id.img2);
        editImg1 = findViewById(R.id.editImg1);
        editImg2 = findViewById(R.id.editImg2);
        imgCancel1 = findViewById(R.id.imgCancel1);
        imgCancel2 = findViewById(R.id.imgCancel2);
        rgp = findViewById(R.id.rgp);
        rbBasic = findViewById(R.id.rbBasic);
        rbPro = findViewById(R.id.rbPro);
        llSetting = findViewById(R.id.llSetting);

        llNoImage1 = findViewById(R.id.llNoImage1);
        llNoImage2 = findViewById(R.id.llNoImage2);
        imgFrame1 = findViewById(R.id.imgFrame1);
        imgFrame2 = findViewById(R.id.imgFrame2);

        llMain = findViewById(R.id.llMain);
        progress = findViewById(R.id.progress);

        editImg1.setVisibility(View.VISIBLE);
        editImg2.setVisibility(View.VISIBLE);

        llSetting.setVisibility(View.GONE);
        registerReceiver(broadcastReceiver, new IntentFilter(MyFirebaseMessagingService.BROADCAST_USER_PRO));

        StringBuilder res = new StringBuilder();
        for (int i = 0; i < Util.ReadSharePreference(context, Constant.USER_MOBILE_NO).length(); i++) {
            if (i == 5) {
                res.append("  ").append(String.valueOf(Util.ReadSharePreference(context, Constant.USER_MOBILE_NO).charAt(i)));
            } else {
                res.append(String.valueOf(Util.ReadSharePreference(context, Constant.USER_MOBILE_NO).charAt(i)));
            }
        }
        txtUserName.setText(res.toString());
        edtName.setText(Util.ReadSharePreference(context, Constant.USER_NAME));
        edtTextMessage.setText(Util.ReadSharePreference(context, Constant.USER_SCROLLING_MESSEGE));

        if (!Util.ReadSharePreference(context, Constant.IMAGE_UPLOADED_1).equalsIgnoreCase("")) {

            llNoImage1.setVisibility(View.GONE);
            imgFrame1.setVisibility(View.VISIBLE);
            byte[] b1 = Base64.decode(Util.ReadSharePreference(context, Constant.IMAGE_UPLOADED_1), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(b1, 0, b1.length);
            imgUploaded1.setImageBitmap(bitmap);
        } else {
            llNoImage1.setVisibility(View.VISIBLE);
            imgFrame1.setVisibility(View.GONE);
        }

        if (!Util.ReadSharePreference(context, Constant.IMAGE_UPLOADED_2).equalsIgnoreCase("")) {

            llNoImage2.setVisibility(View.GONE);
            imgFrame2.setVisibility(View.VISIBLE);

            byte[] b1 = Base64.decode(Util.ReadSharePreference(context, Constant.IMAGE_UPLOADED_2), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(b1, 0, b1.length);
            imgUploaded2.setImageBitmap(bitmap);
        } else {
            llNoImage2.setVisibility(View.VISIBLE);
            imgFrame2.setVisibility(View.GONE);
        }

        if (!Util.ReadSharePreference(context, Constant.IMAGE_PROFILE_1).equalsIgnoreCase("")) {
            byte[] b1 = Base64.decode(Util.ReadSharePreference(context, Constant.IMAGE_PROFILE_1), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(b1, 0, b1.length);
            imgProfile1.setImageBitmap(bitmap);
        }

        if (!Util.ReadSharePreference(context, Constant.IMAGE_PROFILE_2).equalsIgnoreCase("")) {
            byte[] b1 = Base64.decode(Util.ReadSharePreference(context, Constant.IMAGE_PROFILE_2), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(b1, 0, b1.length);
            imgProfile2.setImageBitmap(bitmap);
        }


        llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        edtTextMessage.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    handled = true;

                    Util.hideSoftKeyboard(SettingUserActivity.this);
                    if (Util.isOnline(context)) {

                        changeDetail("message", Util.ReadSharePreference(context, Constant.USER_NAME), edtTextMessage.getText().toString());

                    } else {
                        Snackbar snackbar = Snackbar
                                .make(llMain, "No internet connection", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }

                }
                return handled;
            }
        });

        edtName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    handled = true;

                    if (edtName.getText().length() > 0) {
                        Util.hideSoftKeyboard(SettingUserActivity.this);

                        if (Util.isOnline(context)) {

                            changeDetail("name", edtName.getText().toString(), Util.ReadSharePreference(context, Constant.USER_SCROLLING_MESSEGE));

                        } else {
                            Snackbar snackbar = Snackbar
                                    .make(llMain, "No internet connection", Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }

                    } else {
                        edtName.setError("Name can not be blank");
                        edtName.requestFocus();
                    }
                }
                return handled;
            }
        });

        imgUploaded1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, GALLERY_UPLOADED_1);
            }
        });


        imgUploaded2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, GALLERY_UPLOADED_2);
            }
        });

        imgProfile1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, GALLERY_IMF_PROFILE_1);
            }
        });

        imgProfile2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, GALLERY_IMF_PROFILE_2);


            }
        });

        imgCancel1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(context)
                        .setMessage("Are you sure you want to remove?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Util.WriteSharePreference(context, Constant.IMAGE_UPLOADED_1, "");

                                llNoImage1.setVisibility(View.VISIBLE);
                                imgFrame1.setVisibility(View.GONE);

                                UserMainActivity.llImage1.setVisibility(View.GONE);
                                UserMainActivity.llNoImage1.setVisibility(View.VISIBLE);

                                UserMainActivity.checkImage();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });

        imgCancel2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(context)
                        .setMessage("Are you sure you want to remove?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Util.WriteSharePreference(context, Constant.IMAGE_UPLOADED_2, "");

                                llNoImage2.setVisibility(View.VISIBLE);
                                imgFrame2.setVisibility(View.GONE);
                                UserMainActivity.llImage2.setVisibility(View.GONE);
                                UserMainActivity.llNoImage2.setVisibility(View.VISIBLE);
                                UserMainActivity.checkImage();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();

            }
        });


        llNoImage1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, GALLERY_UPLOADED_1);
            }
        });

        llNoImage2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, GALLERY_UPLOADED_2);

            }
        });

        if (Util.ReadSharePreference(context, Constant.USER_PRO).equalsIgnoreCase("yes")){
            rbPro.setChecked(true);
            rbBasic.setChecked(false);
        } else {
            rbBasic.setChecked(true);
            rbPro.setChecked(false);
        }

        rbPro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Util.ReadSharePreference(context , Constant.USER_PRO).equalsIgnoreCase("yes")){
                    rbPro.setChecked(true);
                    rbBasic.setChecked(false);
                } else {
                    rbPro.setChecked(false);
                    rbBasic.setChecked(true);
                    Snackbar snackbar = Snackbar
                            .make(llMain, "Please contact info@proclivistech.com to activate Pro Features", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        });

        rbBasic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Util.ReadSharePreference(context , Constant.USER_PRO).equalsIgnoreCase("yes")){
                    rbBasic.setChecked(false);
                    rbPro.setChecked(true);
                    Snackbar snackbar = Snackbar
                            .make(llMain, "Please contact info@proclivistech.com to deactivate Pro Features", Snackbar.LENGTH_LONG);
                    snackbar.show();
                } else {
                    rbBasic.setChecked(true);
                    rbPro.setChecked(false);
                }
            }
        });
    }

    public void changeDetail(final String type, String userName, String message) {
        progress.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        try {
            RestClient.getApi().update_customer_name_message1(Util.ReadSharePreference(context, Constant.USER_ID),
                    userName, message,
                    new Callback<Response>() {
                        @Override
                        public void success(Response response, Response response2) {
                            try {

                                JSONObject jsonObject = new JSONObject(Util.getString(response.getBody().in()));

                                progress.setVisibility(View.GONE);
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                switch (jsonObject.getString("status")) {
                                    case "true":

                                        if (type.equals("message")) {
                                            Util.WriteSharePreference(context, Constant.USER_SCROLLING_MESSEGE, edtTextMessage.getText().toString());
                                            UserMainActivity.tatMessage.setVisibility(View.VISIBLE);
                                            UserMainActivity.tatMessage.setText(edtTextMessage.getText().toString());

                                            Snackbar snackbar = Snackbar
                                                    .make(llMain, "Display Message updated successfully", Snackbar.LENGTH_LONG);
                                            snackbar.show();

                                            if (Util.ReadSharePreference(context, Constant.USER_SCROLLING_MESSEGE).length() <= 0) {
                                                UserMainActivity.tatMessage.setVisibility(View.GONE);
                                            }
                                        } else {

                                            Util.WriteSharePreference(context, Constant.USER_NAME, edtName.getText().toString());
                                            UserMainActivity.txtUserName.setText(String.format("%s!!!", Util.ReadSharePreference(context, Constant.USER_NAME)));

                                            Snackbar snackbar = Snackbar
                                                    .make(llMain, "Customer Name updated successfully", Snackbar.LENGTH_LONG);
                                            snackbar.show();
                                        }

                                        break;
                                    case "false":

                                        Snackbar snackbar = Snackbar
                                                .make(llMain, jsonObject.getString("message"), Snackbar.LENGTH_LONG);
                                        snackbar.show();

                                        break;
                                    default:
                                        Util.logout(SettingUserActivity.this, llMain, jsonObject.getString("message"));
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

    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {

            case GALLERY_IMF_PROFILE_1:
                if (resultCode == RESULT_OK) {


                    Uri selectedImage = imageReturnedIntent.getData();
                    File imageFile = new File(getRealPathFromURI(selectedImage));

                    if (imageSizeCheck(selectedImage)) {
                        Bitmap realImage = BitmapFactory.decodeFile(imageFile.toString());
                        ByteArrayOutputStream b2 = new ByteArrayOutputStream();
                        realImage.compress(Bitmap.CompressFormat.JPEG, 100, b2);
                        byte[] b = b2.toByteArray();

                        String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);

                        Log.e("image", encodedImage);

                        Util.WriteSharePreference(context, Constant.IMAGE_PROFILE_1, encodedImage);
                        if (!Util.ReadSharePreference(context, Constant.IMAGE_PROFILE_1).equalsIgnoreCase("")) {
                            byte[] b1 = Base64.decode(Util.ReadSharePreference(context, Constant.IMAGE_PROFILE_1), Base64.DEFAULT);
                            Bitmap bitmap = BitmapFactory.decodeByteArray(b1, 0, b1.length);
                            imgProfile1.setImageBitmap(bitmap);
                            UserMainActivity.imgProfile1.setImageBitmap(bitmap);
                        }
                    } else {
                        Snackbar snackbar = Snackbar
                                .make(llMain, "Image size must be less than 3.5 MB", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }

                    break;
                }

            case GALLERY_IMF_PROFILE_2:
                if (resultCode == RESULT_OK) {

                    Uri selectedImage = imageReturnedIntent.getData();
                    File imageFile = new File(getRealPathFromURI(selectedImage));

                    if (imageSizeCheck(selectedImage)) {

                        Bitmap realImage = BitmapFactory.decodeFile(imageFile.toString());
                        ByteArrayOutputStream b2 = new ByteArrayOutputStream();
                        realImage.compress(Bitmap.CompressFormat.JPEG, 100, b2);
                        byte[] b = b2.toByteArray();

                        String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);

                        Log.e("image", encodedImage);

                        Util.WriteSharePreference(context, Constant.IMAGE_PROFILE_2, encodedImage);
                        if (!Util.ReadSharePreference(context, Constant.IMAGE_PROFILE_2).equalsIgnoreCase("")) {
                            byte[] b1 = Base64.decode(Util.ReadSharePreference(context, Constant.IMAGE_PROFILE_2), Base64.DEFAULT);
                            Bitmap bitmap = BitmapFactory.decodeByteArray(b1, 0, b1.length);
                            imgProfile2.setImageBitmap(bitmap);
                            UserMainActivity.imgProfile2.setImageBitmap(bitmap);
                        }
                    } else {
                        Snackbar snackbar = Snackbar
                                .make(llMain, "Image size must be less than 3.5 MB", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }

                    break;
                }

            case GALLERY_UPLOADED_1:
                if (resultCode == RESULT_OK) {

                    Uri selectedImage = imageReturnedIntent.getData();
                    File imageFile = new File(getRealPathFromURI(selectedImage));

                    if (imageSizeCheck(selectedImage)) {

                        imgFrame1.setVisibility(View.VISIBLE);
                        llNoImage1.setVisibility(View.GONE);

                        Bitmap realImage = BitmapFactory.decodeFile(imageFile.toString());
                        ByteArrayOutputStream b2 = new ByteArrayOutputStream();
                        realImage.compress(Bitmap.CompressFormat.JPEG, 100, b2);
                        byte[] b = b2.toByteArray();

                        String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);

                        Log.e("image", encodedImage);

                        Util.WriteSharePreference(context, Constant.IMAGE_UPLOADED_1, encodedImage);
                        if (!Util.ReadSharePreference(context, Constant.IMAGE_UPLOADED_1).equalsIgnoreCase("")) {
                            byte[] b1 = Base64.decode(Util.ReadSharePreference(context, Constant.IMAGE_UPLOADED_1), Base64.DEFAULT);
                            Bitmap bitmap = BitmapFactory.decodeByteArray(b1, 0, b1.length);
                            imgUploaded1.setImageBitmap(bitmap);
                            UserMainActivity.img1.setImageBitmap(bitmap);
                            UserMainActivity.llNoImage1.setVisibility(View.GONE);
                            UserMainActivity.llImage1.setVisibility(View.VISIBLE);

                            UserMainActivity.checkImage();

                        }
                    } else {
                        Snackbar snackbar = Snackbar
                                .make(llMain, "Image size must be less than 3.5 MB", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }

                    break;
                }

            case GALLERY_UPLOADED_2:
                if (resultCode == RESULT_OK) {

                    Uri selectedImage = imageReturnedIntent.getData();
                    File imageFile = new File(getRealPathFromURI(selectedImage));

                    if (imageSizeCheck(selectedImage)) {
                        imgFrame2.setVisibility(View.VISIBLE);
                        llNoImage2.setVisibility(View.GONE);

                        Bitmap realImage = BitmapFactory.decodeFile(imageFile.toString());
                        ByteArrayOutputStream b2 = new ByteArrayOutputStream();
                        realImage.compress(Bitmap.CompressFormat.JPEG, 100, b2);
                        byte[] b = b2.toByteArray();

                        String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);

                        Log.e("image", encodedImage);

                        Util.WriteSharePreference(context, Constant.IMAGE_UPLOADED_2, encodedImage);
                        if (!Util.ReadSharePreference(context, Constant.IMAGE_UPLOADED_2).equalsIgnoreCase("")) {
                            byte[] b1 = Base64.decode(Util.ReadSharePreference(context, Constant.IMAGE_UPLOADED_2), Base64.DEFAULT);
                            Bitmap bitmap = BitmapFactory.decodeByteArray(b1, 0, b1.length);
                            imgUploaded2.setImageBitmap(bitmap);
                            UserMainActivity.img2.setImageBitmap(bitmap);
                            UserMainActivity.llNoImage2.setVisibility(View.GONE);
                            UserMainActivity.llImage2.setVisibility(View.VISIBLE);

                            UserMainActivity.checkImage();
                        }
                    } else {
                        Snackbar snackbar = Snackbar
                                .make(llMain, "Image size must be less than 3.5 MB", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }

                    break;
                }
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(context, UserMainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
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

    public Boolean imageSizeCheck(Uri uri) {

        int dataSize = 0;


        try {
            InputStream fileInputStream = getApplicationContext().getContentResolver().openInputStream(uri);
            assert fileInputStream != null;
            dataSize = fileInputStream.available();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("size in if", String.valueOf(dataSize));

        //3670016 = 3.5 MB
        return dataSize <= 3670016;

    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Util.ReadSharePreference(context, Constant.USER_PRO).equalsIgnoreCase("yes")){
                rbPro.setChecked(true);
                rbBasic.setChecked(false);
            } else {
                rbBasic.setChecked(true);
                rbPro.setChecked(false);
            }
        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        //context.registerReceiver(receiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        registerReceiver(Util.broadcastReceiver, new IntentFilter(MyFirebaseMessagingService.BROADCAST_UPDATE_USER));
    }

    @Override
    protected void onPause() {
        super.onPause();
        //context.unregisterReceiver(receiver);
        unregisterReceiver(Util.broadcastReceiver);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(ConnectivityStatus.isConnected()){
                Util.uploadInVisitor(SettingUserActivity.this , llMain);
            }
        }
    };
}
