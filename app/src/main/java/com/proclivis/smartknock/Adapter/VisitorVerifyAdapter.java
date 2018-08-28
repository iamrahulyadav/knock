package com.proclivis.smartknock.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.proclivis.smartknock.Activity.User.VisitorVerifyActivity;
import com.proclivis.smartknock.Activity.User.VisitorVerifyDoneActivity;
import com.proclivis.smartknock.Common.Constant;
import com.proclivis.smartknock.Common.Util;
import com.proclivis.smartknock.DataBase.DatabaseHelper;
import com.proclivis.smartknock.DataBase.ExpressVisitorOfflineDb;
import com.proclivis.smartknock.DataBase.VisitorOfflineDb;
import com.proclivis.smartknock.Model.GetVisitorVo;
import com.proclivis.smartknock.Model.VisitorVerifyDone;
import com.proclivis.smartknock.R;
import com.proclivis.smartknock.Retrf.RestClient;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class VisitorVerifyAdapter extends PagerAdapter {

    private Context mContext;
    private ArrayList<GetVisitorVo> getVisitorVos;
    private ArrayList<String> numbers = new ArrayList<>();
    private DatabaseHelper db;
    private ArrayList<String> ids = new ArrayList<>();

    public VisitorVerifyAdapter(Context mContext, ArrayList<GetVisitorVo> getVisitorVos) {
        this.mContext = mContext;
        this.getVisitorVos = getVisitorVos;

        int n = getVisitorVos.size();
        for (int i = 0; i < getVisitorVos.size(); i++) {
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
        return getVisitorVos.size();
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
                view = (ViewGroup) inflater.inflate(R.layout.visitor_verify_item_potrait, container, false);
                break;

            case Configuration.ORIENTATION_LANDSCAPE:
                view = (ViewGroup) inflater.inflate(R.layout.visitor_verify_item_landscape, container, false);
        }

        TextView txtId, txtName, txtComingFrom, txtPurpose, txtMobile, txtRegisterOn;

        Button btnConfirm;
        de.hdodenhof.circleimageview.CircleImageView imgProfile;

        assert view != null;
        txtId = view.findViewById(R.id.txtId);

        txtName = view.findViewById(R.id.txtName);
        txtComingFrom = view.findViewById(R.id.txtComingFrom);
        txtPurpose = view.findViewById(R.id.txtPurpose);
        txtMobile = view.findViewById(R.id.txtMobile);
        txtRegisterOn = view.findViewById(R.id.txtRegisterOn);

        btnConfirm = view.findViewById(R.id.btnConfirm);
        imgProfile = view.findViewById(R.id.imgProfile);

        txtComingFrom.setSelected(true);
        txtPurpose.setSelected(true);
        txtName.setSelected(true);

        txtId.setText(numbers.get(position));

        txtName.setText(String.format("%s + %s", getVisitorVos.get(position).getName(), getVisitorVos.get(position).getVisitorCount()));
        txtComingFrom.setText(getVisitorVos.get(position).getComingFrom());
        txtPurpose.setText(getVisitorVos.get(position).getPurpose());
        txtMobile.setText(getVisitorVos.get(position).getMobileNo());

        txtRegisterOn.setText(Util.converter(getVisitorVos.get(position).getDateTimeIn(), "yyyy-MM-dd HH:mm:ss", "dd-MMM-yyyy HH:mm"));

        if (getVisitorVos.get(position).getVisitorImage().length() > 0) {
            Glide.with(mContext)
                    .load(getVisitorVos.get(position).getVisitorImage())
                    .into(imgProfile);
        }

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!Util.isOnline(mContext)) {
                    db = new DatabaseHelper(mContext);

                    ArrayList<ExpressVisitorOfflineDb> expressVisitorOfflineDbs = db.getAllOfflineExpressVisitorByMobile(getVisitorVos.get(position).getMobileNo());

                    if (expressVisitorOfflineDbs.size() > 0) {

                        for (int i = 0; i< expressVisitorOfflineDbs.size(); i++){
                            if (expressVisitorOfflineDbs.get(i).getDate_time_out().equals("")){
                                ids.add(String.valueOf(expressVisitorOfflineDbs.get(i).getId()));
                                db.updateExpressVisitorOffline(expressVisitorOfflineDbs.get(i).getId() , getVisitorVos.get(position).getMobileNo(), Util.localeFormatedTime("yyyy-MM-dd HH:mm:ss"));
                            }
                        }
                        expressVisitorOfflineDbs = db.getAllOfflineExpressVisitorByMobile(getVisitorVos.get(position).getMobileNo());
                        ArrayList<ExpressVisitorOfflineDb> newData = new ArrayList<>();

                        for (int i = 0; i< expressVisitorOfflineDbs.size() ; i++){
                            for (int j=0 ; j<ids.size();j++){
                                if (ids.get(j).equals(String.valueOf(expressVisitorOfflineDbs.get(i).getId()))){
                                    newData.add(expressVisitorOfflineDbs.get(i));
                                }
                            }
                        }
                        expressVisitorOfflineDbs = newData;

                        Constant.getVisitorVo = new ArrayList<>();

                        int n = 0;
                        for (int i = 0; i < expressVisitorOfflineDbs.size(); i++) {

                            VisitorOfflineDb visitorOfflineDb = new VisitorOfflineDb();
                            visitorOfflineDb.setId(expressVisitorOfflineDbs.get(i).getId());
                            visitorOfflineDb.setName(expressVisitorOfflineDbs.get(i).getName());
                            visitorOfflineDb.setMember(expressVisitorOfflineDbs.get(i).getMember());
                            visitorOfflineDb.setMobileNo(expressVisitorOfflineDbs.get(i).getMobileNo());
                            visitorOfflineDb.setComingFrom(expressVisitorOfflineDbs.get(i).getComingFrom());
                            visitorOfflineDb.setPurpose(expressVisitorOfflineDbs.get(i).getPurpose());
                            visitorOfflineDb.setImage(expressVisitorOfflineDbs.get(i).getImage());
                            visitorOfflineDb.setCount(expressVisitorOfflineDbs.get(i).getCount());
                            visitorOfflineDb.setDate_time_in(expressVisitorOfflineDbs.get(i).getDate_time_in());
                            visitorOfflineDb.setDate_time_out(expressVisitorOfflineDbs.get(i).getDate_time_out());

                            Constant.getVisitorVo.add(n, visitorOfflineDb);
                            n++;

                        }

                    } else {

                        ArrayList<VisitorOfflineDb> visitorOfflineDbs = db.getAllOfflineVisitorByMobile(getVisitorVos.get(position).getMobileNo());

                        for (int i= 0; i<visitorOfflineDbs.size() ; i++){
                            if (visitorOfflineDbs.get(i).getDate_time_out().equals("")){
                                ids.add(String.valueOf(visitorOfflineDbs.get(i).getId()));
                                db.updateVisitorOffline(visitorOfflineDbs.get(position).getId() , visitorOfflineDbs.get(position).getMobileNo(), Util.localeFormatedTime("yyyy-MM-dd HH:mm:ss"));
                                db.updateIsSyncVisitorOffline(visitorOfflineDbs.get(position).getId() );
                            }
                        }

                        ArrayList<VisitorOfflineDb> newData = new ArrayList<>();
                        visitorOfflineDbs = db.getAllOfflineVisitorByMobile(getVisitorVos.get(position).getMobileNo());
                        for (int i=0 ; i<visitorOfflineDbs.size() ; i++){
                            for (int j=0 ; j<ids.size();j++){
                                if (ids.get(j).equals(String.valueOf(visitorOfflineDbs.get(i).getId()))){
                                    newData.add(visitorOfflineDbs.get(i));
                                }
                            }
                        }

                        Constant.getVisitorVo = newData;
                    }

                    VisitorVerifyActivity.homeClick = false;
                    Intent intent = new Intent(mContext, VisitorVerifyDoneActivity.class);
                    intent.putExtra("mode", "offline");
                    mContext.startActivity(intent);
                    VisitorVerifyActivity.activity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

                } else {
                    AddVisitor(getVisitorVos.get(position).getMobileNo() , getVisitorVos.get(position).getInstallationId() );
                }
            }
        });

        container.addView(view);
        return view;
    }

    private void AddVisitor(final String mobile, String installation_id ) {
        VisitorVerifyActivity.progress.setVisibility(View.VISIBLE);
        VisitorVerifyActivity.activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        try {
            //Log.e("VISITOR","API FOR OUT==>"+RestClient.getApi().update_visitor_while_OUT_by_id(mobile, installation_id , Util.ReadSharePreference(mContext , Constant.USER_MOBILE_NO) )
            RestClient.getApi().update_visitor_while_OUT_by_id(mobile, installation_id , Util.ReadSharePreference(mContext , Constant.USER_MOBILE_NO) , "", new Callback<Response>() {
                @Override
                public void success(Response response, Response response2) {
                    try {

                        JSONObject jsonObject = new JSONObject(Util.getString(response.getBody().in()));

                        Gson gson = new Gson();
                        VisitorVerifyDone confirm = gson.fromJson(jsonObject.toString(), VisitorVerifyDone.class);

                        switch (confirm.getStatus()) {
                            case "true":

                                db = new DatabaseHelper(mContext);
                                ExpressVisitorOfflineDb expressVisitorOfflineDb = new ExpressVisitorOfflineDb();
                                expressVisitorOfflineDb.setMobileNo(mobile);

                                try {
                                    db.deleteOfflineExpressVisitor(expressVisitorOfflineDb);
                                }catch (Exception e){
                                    Log.e("deleteOfflineExpVisit" , e.toString());
                                }

                                VisitorOfflineDb visitorOfflineDbs = new VisitorOfflineDb();
                                visitorOfflineDbs.setMobileNo(mobile);
                                try {
                                    db.deleteOfflineVisitorByMobile(visitorOfflineDbs);
                                }catch (Exception e){
                                    Log.e("deleteOfflineVisByMo" , e.toString());
                                }


                                VisitorVerifyActivity.progress.setVisibility(View.GONE);
                                VisitorVerifyActivity.activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                Collections.reverse(confirm.getData());
                                VisitorVerifyActivity.homeClick = false;
                                Intent intent = new Intent(mContext, VisitorVerifyDoneActivity.class);
                                intent.putParcelableArrayListExtra("data", confirm.getData());
                                intent.putExtra("mode", "online");
                                mContext.startActivity(intent);
                                VisitorVerifyActivity.activity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

                                break;
                            case "false":
                                VisitorVerifyActivity.progress.setVisibility(View.GONE);
                                VisitorVerifyActivity.activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                Snackbar snackbar = Snackbar
                                        .make(VisitorVerifyActivity.llMain, confirm.getMessage(), Snackbar.LENGTH_LONG);
                                snackbar.show();
                                break;
                            default:
                                VisitorVerifyActivity.progress.setVisibility(View.GONE);
                                Util.logout(VisitorVerifyActivity.activity, VisitorVerifyActivity.llMain, confirm.getMessage());
                                break;
                        }
                    } catch (Exception e) {
                        VisitorVerifyActivity.progress.setVisibility(View.GONE);
                        VisitorVerifyActivity.activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                        e.printStackTrace();
                        Snackbar snackbar = Snackbar
                                .make(VisitorVerifyActivity.llMain, "Something went wrong, please try again", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    VisitorVerifyActivity.progress.setVisibility(View.GONE);
                    VisitorVerifyActivity.activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    Snackbar snackbar = Snackbar
                            .make(VisitorVerifyActivity.llMain, "Something went wrong, please try again", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
