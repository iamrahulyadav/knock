package com.proclivis.smartknock.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.proclivis.smartknock.Activity.User.WaitingActivity;
import com.proclivis.smartknock.Activity.User.WaitingDetailActivity;
import com.proclivis.smartknock.Common.Constant;
import com.proclivis.smartknock.Common.Util;
import com.proclivis.smartknock.DataBase.DatabaseHelper;
import com.proclivis.smartknock.DataBase.VisitorOfflineDb;
import com.proclivis.smartknock.R;
import com.proclivis.smartknock.Retrf.RestClient;

import org.json.JSONObject;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class WaitingPagerAdapter extends PagerAdapter {

    private Context mContext;
    private ArrayList<VisitorOfflineDb> visitorVos;
    private Activity activity;
    private ArrayList<String> numbers = new ArrayList<>();
    private DatabaseHelper db;
    public WaitingPagerAdapter(Context mContext, Activity activity, ArrayList<VisitorOfflineDb> visitorVos) {
        this.mContext = mContext;
        this.activity = activity;
        this.visitorVos = visitorVos;

        int n = visitorVos.size();
        for (int i = 0; i < visitorVos.size(); i++) {
            numbers.add(i, String.valueOf(n));
            n--;
        }
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return visitorVos.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewGroup view = null;

        switch (mContext.getResources().getConfiguration().orientation) {
            case Configuration.ORIENTATION_PORTRAIT:
                view = (ViewGroup) inflater.inflate(R.layout.waiting_detail_item_potrait, container, false);
                break;

            case Configuration.ORIENTATION_LANDSCAPE:
                view = (ViewGroup) inflater.inflate(R.layout.waiting_detail_item_landscape, container, false);
        }

        final TextView  txtName, txtComingFrom, txtPurpose, txtToMeet , txtRegisterOn, txtSocietyName, txtSocietyMessage, txtComment;
        LinearLayout llComment;
        ImageView imgProfile, imgOut , imgGivenResponse;

        assert view != null;
        txtName = view.findViewById(R.id.txtName);

        txtComingFrom = view.findViewById(R.id.txtComingFrom);
        txtPurpose = view.findViewById(R.id.txtPurpose);
        txtRegisterOn = view.findViewById(R.id.txtRegisterOn);
        imgProfile = view.findViewById(R.id.imgProfile);
        txtSocietyName = view.findViewById(R.id.txtSocietyName);
        txtSocietyMessage = view.findViewById(R.id.txtSocietyMessage);
        imgOut = view.findViewById(R.id.imgOut);
        txtComment = view.findViewById(R.id.txtComment);
        llComment = view.findViewById(R.id.llComment);
        imgGivenResponse = view.findViewById(R.id.imgGivenResponse);
        txtToMeet = view.findViewById(R.id.txtToMeet);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                txtSocietyMessage.setSelected(true);

                txtSocietyMessage.setFocusable(true);
                txtSocietyMessage.setFocusableInTouchMode(true);
                txtSocietyMessage.setHorizontallyScrolling(true);
                txtSocietyMessage.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                txtSocietyMessage.setMarqueeRepeatLimit(-1);
            }
        }, 3000);

        if (Integer.parseInt(visitorVos.get(position).getCount()) > 0) {
            txtName.setText(String.format("%s + %s", visitorVos.get(position).getName(), visitorVos.get(position).getCount()));
        } else {
            txtName.setText(visitorVos.get(position).getName());
        }

        txtComingFrom.setText(visitorVos.get(position).getComingFrom());
        txtPurpose.setText(visitorVos.get(position).getPurpose());

        txtRegisterOn.setText(visitorVos.get(position).getDate_time_in());

        txtSocietyName.setText(Util.ReadSharePreference(mContext, Constant.USER_ADDRESS1));
        txtSocietyMessage.setText(Util.ReadSharePreference(mContext, Constant.USER_SCROLLING_MESSEGE));

        byte[] b1 = Base64.decode(visitorVos.get(position).getImage(), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(b1, 0, b1.length);
        imgProfile.setImageBitmap(bitmap);

        if (Util.ReadSharePreference(mContext, Constant.USER_PRO).equalsIgnoreCase("yes")) {
            if (visitorVos.get(position).getStatus().equals(Constant.REJECT)) {
                imgOut.setVisibility(View.VISIBLE);
                llComment.setVisibility(View.VISIBLE);
                txtComment.setText(visitorVos.get(position).getReason());
            } else {
                imgOut.setVisibility(View.VISIBLE);
                llComment.setVisibility(View.GONE);
            }
        } else {
            imgOut.setVisibility(View.GONE);
            llComment.setVisibility(View.GONE);
        }

        txtToMeet.setText(visitorVos.get(position).getMember_name());
        if (!visitorVos.get(position).getStatus().equalsIgnoreCase("")){
            imgGivenResponse.setVisibility(View.VISIBLE);
            switch (visitorVos.get(position).getStatus()){
                case Constant.REJECT:
                    imgGivenResponse.setBackground(mContext.getResources().getDrawable(R.drawable.block));
                    break;
                case Constant.ACCEPT:
                    imgGivenResponse.setBackground(mContext.getResources().getDrawable(R.drawable.confirm));
                    break;
                case Constant.GUARD:
                    imgGivenResponse.setBackground(mContext.getResources().getDrawable(R.drawable.guard));
                    break;
            }
        } else{
            imgGivenResponse.setVisibility(View.INVISIBLE);
        }
        imgOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Util.isOnline(mContext)){
                    OutVisitor(visitorVos.get(position).getMobileNo() , Util.ReadSharePreference(mContext , Constant.USER_INSTALLATION_ID) , visitorVos.get(position).getId() );
                } else {
                    db = new DatabaseHelper(mContext);
                    ArrayList<VisitorOfflineDb> getVisitorVo1 = db.getAllOfflineVisitorByMobile(visitorVos.get(position).getMobileNo());
                    ArrayList<VisitorOfflineDb> getVisitorVo = new ArrayList<>();
                    int n = 0;
                    for (int i=0 ; i<getVisitorVo1.size(); i++){
                        if (getVisitorVo1.get(i).getDate_time_out().equals("")){
                            getVisitorVo.add(n , getVisitorVo1.get(i));
                            n++;
                        }
                    }
                    for (int i=0; i<getVisitorVo.size(); i++){
                        db.updateVisitorOffline(getVisitorVo.get(i).getId() , getVisitorVo.get(i).getMobileNo(), Util.localeFormatedTime("yyyy-MM-dd HH:mm:ss"));
                    }

                    Intent intent = new Intent(mContext, WaitingActivity.class);
                    mContext.startActivity(intent);
                    activity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                }
            }
        });
        container.addView(view);
        return view;
    }

    private void OutVisitor(final String mobile, String installation_id , final int id) {
        WaitingDetailActivity.progress.setVisibility(View.VISIBLE);
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        try {
            RestClient.getApi().update_visitor_while_OUT_by_id(mobile, installation_id, Util.ReadSharePreference(mContext, Constant.USER_MOBILE_NO), "Confirmation Screen", new Callback<Response>() {
                @Override
                public void success(Response response, Response response2) {
                    try {

                        JSONObject jsonObject = new JSONObject(Util.getString(response.getBody().in()));

                        switch (jsonObject.getString("status")) {
                            case "true":

                                WaitingDetailActivity.progress.setVisibility(View.GONE);
                                activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                db = new DatabaseHelper(mContext);
                                VisitorOfflineDb visitorOfflineDbs = new VisitorOfflineDb();
                                visitorOfflineDbs.setId(id);
                                visitorOfflineDbs.setMobileNo(mobile);
                                try {
                                    db.deleteOfflineVisitorByMobile(visitorOfflineDbs);
                                }catch (Exception e){
                                    Log.e("deleteOfflineVisByMo" , e.toString());
                                }

                                Intent intent = new Intent(mContext, WaitingActivity.class);
                                mContext.startActivity(intent);
                                activity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

                                break;
                            case "false":
                                WaitingDetailActivity.progress.setVisibility(View.GONE);
                                activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                Snackbar snackbar = Snackbar
                                        .make(WaitingDetailActivity.llMain, jsonObject.getString("message"), Snackbar.LENGTH_LONG);
                                snackbar.show();
                                break;
                            default:
                                WaitingDetailActivity.progress.setVisibility(View.GONE);
                                Util.logout(activity, WaitingDetailActivity.llMain, jsonObject.getString("message"));
                                break;
                        }
                    } catch (Exception e) {
                        WaitingDetailActivity.progress.setVisibility(View.GONE);
                        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                        e.printStackTrace();
                        Snackbar snackbar = Snackbar
                                .make(WaitingDetailActivity.llMain, "Something went wrong, please try again", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    WaitingDetailActivity.progress.setVisibility(View.GONE);
                    activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    Snackbar snackbar = Snackbar
                            .make(WaitingDetailActivity.llMain, "Something went wrong, please try again", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}