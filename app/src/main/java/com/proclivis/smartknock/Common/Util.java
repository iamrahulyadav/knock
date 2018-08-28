package com.proclivis.smartknock.Common;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.gson.Gson;
import com.proclivis.smartknock.Activity.LoginActivity;
import com.proclivis.smartknock.Activity.User.UserMainActivity;
import com.proclivis.smartknock.DataBase.DatabaseHelper;
import com.proclivis.smartknock.DataBase.ExpressVisitorOfflineDb;
import com.proclivis.smartknock.DataBase.VisitorOfflineDb;
import com.proclivis.smartknock.Model.InSuccess;
import com.proclivis.smartknock.Model.VisitorConfirm;
import com.proclivis.smartknock.Model.VisitorVerifyDone;
import com.proclivis.smartknock.R;
import com.proclivis.smartknock.Retrf.RestClient;


import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;

@SuppressLint("SimpleDateFormat")
public class Util {

    private static ProgressDialog dialog;
    //Amit 19.06.18. Changing the below variable to public. If this is false. Broadcast receiver will stop the service
    public static boolean isUpload = false;
    //Amit 19.06.18. Setting static variables to indicate start of upload
    public static boolean uploadInProgress = false;
    public static boolean visitorRecordsExists = false;

    public static String getCurrentDateTime() {
        Calendar c = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");

        return df.format(c.getTime());
    }

    public static String getDateTime() {
        Calendar c = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy HH:mm");

        return df.format(c.getTime());
    }

    private static String getCurrentTime() {
        DateFormat df = new SimpleDateFormat("h:mm a");

        return df.format(Calendar.getInstance().getTime());
    }

    public static String converter(String inputDate, String formate1, String formate2) {

        SimpleDateFormat dateFormat = new SimpleDateFormat(formate1);
        SimpleDateFormat dateFormat2 = new SimpleDateFormat(formate2);

        String output;
        try {
            Date date = dateFormat.parse(inputDate);

            output = dateFormat2.format(date);

        } catch (ParseException e) {
            output = getCurrentTime();
        }

        return output;

    }

    public static String timeCalculation(String inputDate, String formate1) {

        Calendar c = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df1 = new SimpleDateFormat(formate1);

        SimpleDateFormat dateFormat = new SimpleDateFormat(formate1);

        String output;
        try {
            Date date1 = dateFormat.parse(inputDate);
            Date date2 = df1.parse(df1.format(c.getTime()));

            long diff = date2.getTime() - date1.getTime();
            long seconds = diff / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;

            minutes = minutes - (hours * 60);

            if (hours > 0) {
                String m = new DecimalFormat("00").format(minutes);
                output = String.valueOf(hours + ":" + m + "\n Hours ");
            } else {
                if (minutes > 1) {
                    output = String.valueOf(minutes + "\nMins");
                } else {
                    output = String.valueOf(minutes + "\nMin");
                }

            }


        } catch (ParseException e) {
            output = getCurrentTime();
        }

        return output;

    }

    public static String localeFormatedTime(String formate) {
        Calendar c = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df1 = new SimpleDateFormat(formate);
        return df1.format(c.getTime());
    }

    public static void hideSoftKeyboard(Activity activity) {
        try {
            InputMethodManager inputMethodManager =
                    (InputMethodManager) activity.getSystemService(
                            Activity.INPUT_METHOD_SERVICE);
            assert inputMethodManager != null;
            inputMethodManager.hideSoftInputFromWindow(
                    activity.getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String allCapitalize(String str) {

        String[] strArray = str.split(" ");
        StringBuilder builder = new StringBuilder();
        for (String s : strArray) {
            String cap = s.substring(0, 1).toUpperCase() + s.substring(1);
            builder.append(cap).append(" ");
        }

        return builder.toString();

    }

    public static boolean isOnline(Context ctx) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);

        assert connectivityManager != null;
        if (connectivityManager.getActiveNetworkInfo() != null
                && connectivityManager.getActiveNetworkInfo().isAvailable()
                && connectivityManager.getActiveNetworkInfo().isConnected()) {
            Log.e("netCheck", "Internet is working");
            // txt_status.setText("Internet is working");
            return true;
        } else {
            // txt_status.setText("Internet Connection Not Present");
            Log.e("netCheck", "Internet Connection Not Present");
            return false;
        }

        /*Runtime runtime = Runtime.getRuntime();
        try {

            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;*/
    }

    public static BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            ArrayList<VisitorOfflineDb> getVisitorVo = new ArrayList<>();
            UserMainActivity.db = new DatabaseHelper(context);

            ArrayList<VisitorOfflineDb> getVisitorVo1 = UserMainActivity.db.getAllOfflineVisitor();
            int n  = 0;
            for (int i = 0; i < getVisitorVo1.size(); i++) {
                if (getVisitorVo1.get(i).getDate_time_out().equals("")) {
                    getVisitorVo.add(n, getVisitorVo1.get(i));
                    n++;
                }
            }
            for (int i=0 ; i<getVisitorVo.size(); i++){
                if (getVisitorVo.get(i).getMember_name().equalsIgnoreCase(Constant.DRAWER_m_name) &&
                        getVisitorVo.get(i).getMobileNo().equalsIgnoreCase(Constant.DRAWER_mobile_no) &&
                        getVisitorVo.get(i).getComingFrom().equalsIgnoreCase(Constant.DRAWER_coming_from) &&
                        getVisitorVo.get(i).getPurpose().equalsIgnoreCase(Constant.DRAWER_purpose) &&
                        getVisitorVo.get(i).getDate_time_in().equalsIgnoreCase(Constant.DRAWER_date_time_in)) {

                    UserMainActivity.db.updateVisitorOfflineStatus(getVisitorVo.get(i).getId() , Constant.DRAWER_status , Constant.DRAWER_reason);
                    if (Util.ReadSharePreference(context , Constant.USER_PRO).equalsIgnoreCase("yes")){
                        UserMainActivity.llDrawer.setVisibility(View.VISIBLE);
                        int count = Util.ReadSharePreferenceInt(context , Constant.DRAWER_COUNT);
                        count = count + 1;
                        Util.WriteSharePreferenceInt(context , Constant.DRAWER_COUNT , count);
                        UserMainActivity.txtDrawerCount.setVisibility(View.VISIBLE);
                        if (count>9){
                            UserMainActivity.txtDrawerCount.setText(String.format(" %s ", String.valueOf(count)));
                        } else {
                            UserMainActivity.txtDrawerCount.setText(String.format("  %s  ", String.valueOf(count)));
                        }
                    }
                    break;
                }
            }
        }
    };
    public static String ReadSharePreference(Context context, String key) {
        @SuppressWarnings("static-access")
        SharedPreferences read_data = null;
        try {

            read_data = context.getSharedPreferences(
                    Constant.SHARE_PREF, Context.MODE_PRIVATE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        assert read_data != null;
        return read_data.getString(key, "");
    }

    public static void WriteSharePreference(Context context, String key, String values) {
        @SuppressWarnings("static-access")
        SharedPreferences write_Data = context.getSharedPreferences(
                Constant.SHARE_PREF, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = write_Data.edit();
        editor.putString(key, values);
        editor.apply();
    }

    public static void WriteSharePreferenceInt(Context context, String key, int values) {
        @SuppressWarnings("static-access")
        SharedPreferences write_Data = context.getSharedPreferences(
                Constant.SHARE_PREF, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = write_Data.edit();
        editor.putInt(key, values);
        editor.apply();
    }

    public static int ReadSharePreferenceInt(Context context, String key) {
        @SuppressWarnings("static-access")
        SharedPreferences read_data = null;
        try {

            read_data = context.getSharedPreferences(
                    Constant.SHARE_PREF, Context.MODE_PRIVATE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        assert read_data != null;
        return read_data.getInt(key, 0);
    }

    public static String getString(InputStream in) {
        InputStreamReader is = new InputStreamReader(in);
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(is);
        try {
            String read = br.readLine();
            while (read != null) {
                sb.append(read);
                read = br.readLine();
            }
        } catch (Exception e) {
            Log.e("JsonConverter", "ERROR WHILE PARSING RESPONSE TO STRING :: " + e.getMessage());
        } finally {
            try {
                is.close();
                br.close();
            } catch (Exception ignored) {
            }
        }
        return sb.toString();
    }

    public static String comaString() {

        StringBuilder result = new StringBuilder();
        if (Constant.selectedMember.size() != 0) {
            for (int i = 0; i < Constant.selectedMember.size(); i++) {

                if (i == 0) {
                    result = new StringBuilder(Constant.selectedMember.get(i).getId());
                } else {
                    result.append(",").append(Constant.selectedMember.get(i).getId());
                }

            }
        } else {
            result = new StringBuilder();
        }
        return result.toString();
    }


    public static void logout(final Activity activity, final View view, String s) {

        Snackbar snackbar = Snackbar
                .make(view, s, Snackbar.LENGTH_LONG);
        snackbar.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                SharedPreferences preferences = activity.getSharedPreferences(Constant.SHARE_PREF, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.apply();

                Intent intent = new Intent(activity, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                activity.startActivity(intent);
                activity.finish();
                activity.overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);

            }
        }, 1500);
    }

    //Amit 19.06.18. Added below function to get total Visitor records not inSync
    public static boolean offlineRecordsExists(Activity activity){
        final DatabaseHelper db = new DatabaseHelper(activity);
        if(db.getVisitorOfflineCount() > 0) return true;
        if(db.getExpressVisitorOfflineCount() > 0) return true;
        return false;
    }

    public static void uploadInVisitor(final Activity activity, final View view) {
        //Amit 19.06.18. Setting static variable to indicate start of upload
        uploadInProgress =true;

        isUpload = false;
        //Amit: 19.06.18. Start Debugging
        Log.d("Util","Debug Upload uploadINVisitor Called");
        //Amit: 19.06.18. End Debugging

        final DatabaseHelper db = new DatabaseHelper(activity);
        ArrayList<VisitorOfflineDb> visitorOfflineDbs;
        visitorOfflineDbs = db.getAllOfflineVisitor();

        ArrayList<VisitorOfflineDb> newData = new ArrayList<>();
        for (int i = 0; i < visitorOfflineDbs.size(); i++) {
            if (visitorOfflineDbs.get(i).getIs_sync().equals("false")) {
                newData.add(visitorOfflineDbs.get(i));
            }
        }

        visitorOfflineDbs = newData;

        if (visitorOfflineDbs.size() > 0) {
            isUpload = true;
            //Amit:19.06.18. Commented the below code because this will be executed in background thread
            //dialog = new ProgressDialog(activity);
            //dialog.setMessage("Uploading Visitors");
            //dialog.show();
            //Amit: 19.06.18. Start Debugging
            Log.d("Util","Debug Upload uploadINVisitor visitorOfflineDbs.size():"+ visitorOfflineDbs.size());
            //Amit: 19.06.18. End Debugging

            for (int i = 0; i < visitorOfflineDbs.size(); i++) {
                try {
                    //Amit: 19.06.18. Start Debugging
                    Log.d("Util","Debug Upload uploadINVisitor Counter i:"+ i);
                    //Amit: 19.06.18. End Debugging
                    TypedString deviceId = new TypedString(Util.ReadSharePreference(activity, Constant.DEVICE_ID));
                    //Amit 19.06.18. Added final for debugging
                    final TypedString member_id = new TypedString(visitorOfflineDbs.get(i).getMember());
                    TypedString name = new TypedString(visitorOfflineDbs.get(i).getName());
                    //Amit 19.06.18. Added final for debugging
                    final TypedString mobile_no = new TypedString(visitorOfflineDbs.get(i).getMobileNo());
                    TypedString coming_from = new TypedString(visitorOfflineDbs.get(i).getComingFrom());
                    TypedString purpose = new TypedString(visitorOfflineDbs.get(i).getPurpose());
                    TypedString vistor_count = new TypedString(visitorOfflineDbs.get(i).getCount());
                    //Amit 19.06.18. Added final for debugging
                    final TypedString date = new TypedString(visitorOfflineDbs.get(i).getDate_time_in());
                    TypedFile visitor_image;

                    //Amit: 19.06.18. Start Debugging
                    Log.d("Util","Debug Upload uploadINVisitor-> Mobile:"+mobile_no +" Member ID:" + member_id + " Date:"+date);
                    //Amit: 19.06.18. End Debugging

                    File f = new File(activity.getCacheDir(), String.valueOf(System.currentTimeMillis()) + ".jpg");
                    f.createNewFile();

                    byte[] b1 = Base64.decode(visitorOfflineDbs.get(i).getImage(), Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(b1, 0, b1.length);

                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
                    byte[] bitmapdata = bos.toByteArray();

                    FileOutputStream fos = new FileOutputStream(f);
                    fos.write(bitmapdata);
                    fos.flush();
                    fos.close();

                    visitor_image = new TypedFile("image/*", f);

                    final ArrayList<VisitorOfflineDb> finalVisitorOfflineDbs = visitorOfflineDbs;
                    final int finalI = i;
                    //Amit 19.06.18 Changed the order. Put the Visitor Image to the end.
                    RestClient.getApi().add_visitor_while_IN_without_OUT_validation_date(deviceId, member_id, name, mobile_no,
                            coming_from, purpose, vistor_count , date,
                            new TypedString(Util.ReadSharePreference(activity, Constant.USER_MOBILE_NO)), visitor_image, new Callback<Response>() {
                                @Override
                                public void success(Response response, Response response2) {
                                    try {

                                        JSONObject jsonObject = new JSONObject(Util.getString(response.getBody().in()));

                                        Gson gson = new Gson();
                                        InSuccess confirm = gson.fromJson(jsonObject.toString(), InSuccess.class);

                                        switch (confirm.getStatus()) {
                                            case "true": {

                                                Constant.VISITOR_UPLOADED = Constant.VISITOR_UPLOADED + 1;

                                                db.updateIsSyncVisitorOffline(finalVisitorOfflineDbs.get(finalI).getId());
                                                db.updateStatusReasonVisitorOffline(finalVisitorOfflineDbs.get(finalI).getId(), confirm.getData().get(0).getStatus(), confirm.getData().get(0).getReason());
                                                //Amit: 19.06.18. Start Debugging
                                                Log.d("Util","Debug Upload uploadINVisitor Success Done-> Mobile:"+mobile_no +" Member ID:" + member_id + " Date:"+date);
                                                //Amit: 19.06.18. End Debugging


                                                if (Constant.VISITOR_UPLOADED == finalVisitorOfflineDbs.size()) {
                                                    Constant.VISITOR_UPLOADED = 0;

                                                    //Amit:19.06.18. Commented the below code because this will be executed in background thread
                                                    //dialog.cancel();
                                                    uploadExpressInVisitor(activity, view);
                                                }

                                                break;
                                            }
                                            //Amit:19.06.18. Added the below code to handle duplicate status
                                            //Do the same steps as true and mark the record as processed
                                            case "duplicate":{
                                                //Amit: 19.06.18. Start Debugging
                                                Log.d("Util","Debug Upload uploadINVisitor Duplicate-> Mobile:"+mobile_no +" Member ID:" + member_id + " Date:"+date);
                                                //Amit: 19.06.18. End Debugging


                                                Constant.VISITOR_UPLOADED = Constant.VISITOR_UPLOADED + 1;

                                                db.updateIsSyncVisitorOffline(finalVisitorOfflineDbs.get(finalI).getId());
                                                db.updateStatusReasonVisitorOffline(finalVisitorOfflineDbs.get(finalI).getId(), confirm.getData().get(0).getStatus(), confirm.getData().get(0).getReason());
                                                if (Constant.VISITOR_UPLOADED == finalVisitorOfflineDbs.size()) {
                                                    Constant.VISITOR_UPLOADED = 0;
                                                    //Amit:19.06.18. Commented the below code because this will be executed in background thread
                                                    //dialog.cancel();
                                                    uploadExpressInVisitor(activity, view);
                                                }

                                                break;

                                            }
                                            //Amit:19.06.18. End of Change

                                            case "false": {
                                                //Amit: 19.06.18. Start Debugging
                                                Log.d("Util","Debug Upload uploadINVisitor False-> Mobile:"+mobile_no +" Member ID:" + member_id + " Date:"+date);
                                                //Amit: 19.06.18. End Debugging

                                                //Amit:19.06.18. Commented the below code because this will be executed in background thread
                                                //dialog.dismiss();
                                                Toast.makeText(activity, confirm.getMessage(), Toast.LENGTH_SHORT).show();
                                                break;
                                            }
                                            default:
                                                //Amit:19.06.18. Commented the below code because this will be executed in background thread
                                                //dialog.dismiss();
                                                Toast.makeText(activity, confirm.getMessage(), Toast.LENGTH_SHORT).show();
                                                Util.logout(activity, view, confirm.getMessage());
                                                break;
                                        }
                                    } catch (Exception e) {
                                        //Amit:19.06.18. Commented the below code because this will be executed in background thread
                                        //dialog.dismiss();
                                        //Amit: 19.06.18. Start Debugging
                                        Log.d("Util","Debug Upload uploadINVisitor Error-> Mobile:"+mobile_no +" Member ID:" + member_id + " Date:"+date);
                                        //Amit: 19.06.18. End Debugging
                                        Toast.makeText(activity, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void failure(RetrofitError error) {
                                    //Amit:19.06.18. Commented the below code because this will be executed in background thread
                                    //dialog.dismiss();
                                    //Amit: 19.06.18. Start Debugging
                                    Log.d("Util","Debug Upload uploadINVisitor Failure-> Mobile:"+mobile_no +" Member ID:" + member_id + " Date:"+date);
                                    //Amit: 19.06.18. End Debugging
                                    Toast.makeText(activity, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
                                    error.printStackTrace();
                                }
                            });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            uploadExpressInVisitor(activity, view);
        }
    }

    private static void uploadExpressInVisitor(final Activity activity, final View view) {

        final DatabaseHelper db = new DatabaseHelper(activity);
        ArrayList<ExpressVisitorOfflineDb> expressVisitorOfflineDbs;

        expressVisitorOfflineDbs = db.getAllOfflineExpressVisitor();

        ArrayList<ExpressVisitorOfflineDb> newExpress = new ArrayList<>();
        for (int i = 0; i < expressVisitorOfflineDbs.size(); i++) {
            if (expressVisitorOfflineDbs.get(i).getIs_sync().equals("false")) {
                newExpress.add(expressVisitorOfflineDbs.get(i));
            }
        }

        ArrayList<ExpressVisitorOfflineDb> newData = new ArrayList<>();
        for (int i = 0; i < newExpress.size(); i++) {
            if (i == 0) {
                newData.add(newExpress.get(i));
            } else {
                for (int j = 0; j < newData.size(); j++) {
                    if (!(newData.get(j).getMobileNo().equals(newExpress.get(i).getMobileNo()))) {
                        newData.add(newExpress.get(i));
                    }
                }
            }
        }

        expressVisitorOfflineDbs = newData;

        if (expressVisitorOfflineDbs.size() > 0) {
            isUpload = true;
            //Amit:19.06.18. Commented the below code because this will be executed in background thread
            //dialog = new ProgressDialog(activity);
            //dialog.setMessage("Uploading Express Visitors");
            //dialog.show();

            for (int i = 0; i < expressVisitorOfflineDbs.size(); i++) {
                try {


                    TypedString mobile_no = new TypedString(expressVisitorOfflineDbs.get(i).getMobileNo());
                    TypedString date = new TypedString(expressVisitorOfflineDbs.get(i).getDate_time_in());
                    TypedFile visitor_image;

                    File f = new File(activity.getCacheDir(), String.valueOf(System.currentTimeMillis()) + ".jpg");
                    f.createNewFile();

                    byte[] b1 = Base64.decode(expressVisitorOfflineDbs.get(i).getImage(), Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(b1, 0, b1.length);

                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
                    byte[] bitmapdata = bos.toByteArray();


                    FileOutputStream fos = new FileOutputStream(f);
                    fos.write(bitmapdata);
                    fos.flush();
                    fos.close();

                    visitor_image = new TypedFile("image/*", f);

                    final ArrayList<ExpressVisitorOfflineDb> finalVisitorOfflineDbs = expressVisitorOfflineDbs;
                    final int finalI = i;
                    RestClient.getApi().add_daily_visitor_while_IN_without_OUT_validation_date(mobile_no,
                            new TypedString(Util.ReadSharePreference(activity, Constant.USER_INSTALLATION_ID)),
                            new TypedString(Util.ReadSharePreference(activity, Constant.USER_MOBILE_NO)), date, visitor_image, new Callback<Response>() {
                                @Override
                                public void success(Response response, Response response2) {
                                    try {

                                        JSONObject jsonObject = new JSONObject(Util.getString(response.getBody().in()));

                                        Gson gson = new Gson();
                                        VisitorConfirm confirm = gson.fromJson(jsonObject.toString(), VisitorConfirm.class);

                                        switch (confirm.getStatus()) {
                                            case "true": {

                                                Constant.VISITOR_UPLOADED = Constant.VISITOR_UPLOADED + 1;

                                                db.updateIsSyncExpressVisitorOffline(finalVisitorOfflineDbs.get(finalI).getMobileNo());
                                                if (Constant.VISITOR_UPLOADED == finalVisitorOfflineDbs.size()) {
                                                    Constant.VISITOR_UPLOADED = 0;
                                                    //Amit:19.06.18. Commented the below code because this will be executed in background thread
                                                    //dialog.cancel();
                                                    uploadOutVisitor(activity, view);
                                                }

                                                break;
                                            }
                                            case "false": {
                                                //Amit:19.06.18. Commented the below code because this will be executed in background thread
                                                //dialog.dismiss();
                                                Toast.makeText(activity, confirm.getMessage(), Toast.LENGTH_SHORT).show();
                                                break;
                                            }
                                            default:
                                                //Amit:19.06.18. Commented the below code because this will be executed in background thread
                                                //dialog.dismiss();
                                                Toast.makeText(activity, confirm.getMessage(), Toast.LENGTH_SHORT).show();
                                                Util.logout(activity, view, confirm.getMessage());
                                                break;
                                        }
                                    } catch (Exception e) {
                                        //Amit:19.06.18. Commented the below code because this will be executed in background thread
                                        //dialog.dismiss();
                                        Toast.makeText(activity, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void failure(RetrofitError error) {
                                    //Amit:19.06.18. Commented the below code because this will be executed in background thread
                                    //dialog.dismiss();
                                    Toast.makeText(activity, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
                                    error.printStackTrace();
                                }
                            });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            uploadOutVisitor(activity, view);
        }
    }

    private static void uploadOutVisitor(final Activity activity, final View view) {

        final DatabaseHelper db = new DatabaseHelper(activity);
        ArrayList<VisitorOfflineDb> visitorOfflineDbs;
        visitorOfflineDbs = db.getAllOfflineVisitor();

        ArrayList<VisitorOfflineDb> newData = new ArrayList<>();
        for (int i = 0; i < visitorOfflineDbs.size(); i++) {
            if (!visitorOfflineDbs.get(i).getDate_time_out().equals("")) {
                newData.add(visitorOfflineDbs.get(i));
            }
        }

        visitorOfflineDbs = newData;

        if (visitorOfflineDbs.size() > 0) {
            isUpload = true;
            //Amit:19.06.18. Commented the below code because this will be executed in background thread
            //dialog = new ProgressDialog(activity);
            //dialog.setMessage("Uploading Out Visitors");
            //dialog.show();

            for (int i = 0; i < visitorOfflineDbs.size(); i++) {
                try {

                    final ArrayList<VisitorOfflineDb> finalVisitorOfflineDbs = visitorOfflineDbs;
                    final int finalI = i;
                    RestClient.getApi().update_visitor_while_OUT_by_id_date(visitorOfflineDbs.get(i).getMobileNo(),
                            Util.ReadSharePreference(activity, Constant.USER_INSTALLATION_ID),
                            Util.ReadSharePreference(activity, Constant.USER_MOBILE_NO), visitorOfflineDbs.get(i).getDate_time_out(), new Callback<Response>() {
                                @Override
                                public void success(Response response, Response response2) {
                                    try {

                                        JSONObject jsonObject = new JSONObject(Util.getString(response.getBody().in()));

                                        Gson gson = new Gson();
                                        VisitorVerifyDone confirm = gson.fromJson(jsonObject.toString(), VisitorVerifyDone.class);

                                        switch (confirm.getStatus()) {
                                            case "true": {

                                                Constant.VISITOR_UPLOADED = Constant.VISITOR_UPLOADED + 1;

                                                db.deleteOfflineVisitor(finalVisitorOfflineDbs.get(finalI));
                                                if (Constant.VISITOR_UPLOADED == finalVisitorOfflineDbs.size()) {
                                                    Constant.VISITOR_UPLOADED = 0;
                                                    //Amit:19.06.18. Commented the below code because this will be executed in background thread
                                                    //dialog.cancel();
                                                    uploadExpressOutVisitor(activity, view);
                                                }

                                                break;
                                            }
                                            case "false": {
                                                //Amit:19.06.18. Commented the below code because this will be executed in background thread
                                                //dialog.dismiss();
                                                Toast.makeText(activity, confirm.getMessage(), Toast.LENGTH_SHORT).show();
                                                break;
                                            }
                                            default:
                                                //Amit:19.06.18. Commented the below code because this will be executed in background thread
                                                //dialog.dismiss();
                                                Toast.makeText(activity, confirm.getMessage(), Toast.LENGTH_SHORT).show();
                                                Util.logout(activity, view, confirm.getMessage());
                                                break;
                                        }
                                    } catch (Exception e) {
                                        //Amit:19.06.18. Commented the below code because this will be executed in background thread
                                        //dialog.dismiss();
                                        Toast.makeText(activity, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void failure(RetrofitError error) {
                                    //Amit:19.06.18. Commented the below code because this will be executed in background thread
                                    //dialog.dismiss();
                                    Toast.makeText(activity, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
                                    error.printStackTrace();
                                }
                            });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            uploadExpressOutVisitor(activity, view);
        }
    }

    private static void uploadExpressOutVisitor(final Activity activity, final View view) {

        final DatabaseHelper db = new DatabaseHelper(activity);
        ArrayList<ExpressVisitorOfflineDb> expressVisitorOfflineDbs;

        expressVisitorOfflineDbs = db.getAllOfflineExpressVisitor();

        ArrayList<ExpressVisitorOfflineDb> newExpress = new ArrayList<>();
        for (int i = 0; i < expressVisitorOfflineDbs.size(); i++) {
            if (!expressVisitorOfflineDbs.get(i).getDate_time_out().equals("")) {
                newExpress.add(expressVisitorOfflineDbs.get(i));
            }
        }

        ArrayList<ExpressVisitorOfflineDb> newData = new ArrayList<>();
        for (int i = 0; i < newExpress.size(); i++) {
            if (i == 0) {
                newData.add(newExpress.get(i));
            } else {
                for (int j = 0; j < newData.size(); j++) {
                    if (!(newData.get(j).getMobileNo().equals(newExpress.get(i).getMobileNo()) && newData.get(j).getDate_time_out().equals(newExpress.get(i).getDate_time_out()))) {
                        newData.add(newExpress.get(i));
                    }
                }
            }
        }

        expressVisitorOfflineDbs = newData;
        //Amit:19.06.18. Commented the below code because this will be executed in background thread
        //dialog = new ProgressDialog(activity);
        if (expressVisitorOfflineDbs.size() > 0) {
            isUpload = true;
            //Amit:19.06.18. Commented the below code because this will be executed in background thread
            //dialog.setMessage("Uploading Out Visitors");
            //dialog.show();

            for (int i = 0; i < expressVisitorOfflineDbs.size(); i++) {
                try {

                    final ArrayList<ExpressVisitorOfflineDb> finalVisitorOfflineDbs = expressVisitorOfflineDbs;
                    final int finalI = i;
                    RestClient.getApi().update_visitor_while_OUT_by_id_date(expressVisitorOfflineDbs.get(i).getMobileNo(),
                            Util.ReadSharePreference(activity, Constant.USER_INSTALLATION_ID),
                            Util.ReadSharePreference(activity, Constant.USER_MOBILE_NO), expressVisitorOfflineDbs.get(i).getDate_time_out(), new Callback<Response>() {
                                @Override
                                public void success(Response response, Response response2) {
                                    try {

                                        JSONObject jsonObject = new JSONObject(Util.getString(response.getBody().in()));

                                        Gson gson = new Gson();
                                        VisitorVerifyDone confirm = gson.fromJson(jsonObject.toString(), VisitorVerifyDone.class);

                                        switch (confirm.getStatus()) {
                                            case "true": {

                                                Constant.VISITOR_UPLOADED = Constant.VISITOR_UPLOADED + 1;

                                                db.deleteOfflineExpressVisitor(finalVisitorOfflineDbs.get(finalI));
                                                if (Constant.VISITOR_UPLOADED == finalVisitorOfflineDbs.size()) {
                                                    Constant.VISITOR_UPLOADED = 0;
                                                    //Amit:19.06.18. Commented the below code because this will be executed in background thread
                                                    //dialog.dismiss();
                                                    UserMainActivity.homeClick = true;
                                                    Toast.makeText(activity, "Upload Completed", Toast.LENGTH_SHORT).show();
                                                    isUpload=false;
                                                }

                                                break;
                                            }
                                            case "false": {
                                                //Amit:19.06.18. Commented the below code because this will be executed in background thread
                                                //dialog.dismiss();
                                                Toast.makeText(activity, confirm.getMessage(), Toast.LENGTH_SHORT).show();
                                                break;
                                            }
                                            default:
                                                //Amit:19.06.18. Commented the below code because this will be executed in background thread
                                                //dialog.dismiss();
                                                Toast.makeText(activity, confirm.getMessage(), Toast.LENGTH_SHORT).show();
                                                Util.logout(activity, view, confirm.getMessage());
                                                break;
                                        }
                                    } catch (Exception e) {
                                        //Amit:19.06.18. Commented the below code because this will be executed in background thread
                                        //dialog.dismiss();
                                        Toast.makeText(activity, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void failure(RetrofitError error) {
                                    //Amit:19.06.18. Commented the below code because this will be executed in background thread
                                    //dialog.dismiss();
                                    Toast.makeText(activity, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
                                    error.printStackTrace();
                                }
                            });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            uploadInProgress = false;
        } else {
            //Amit:19.06.18. Commented the below code because this will be executed in background thread
            //dialog.dismiss();
            UserMainActivity.homeClick = true;
            if (isUpload) {
                Toast.makeText(activity, "Upload Completed", Toast.LENGTH_SHORT).show();
                isUpload=false;
            }
            uploadInProgress = false;
        }
    }
}