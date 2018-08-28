package com.proclivis.smartknock.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.proclivis.smartknock.Activity.Member.MemberMainActivity;
import com.proclivis.smartknock.Activity.Member.MemberPagerActivity;
import com.proclivis.smartknock.Common.Constant;
import com.proclivis.smartknock.Common.Util;
import com.proclivis.smartknock.DataBase.DatabaseHelper;
import com.proclivis.smartknock.Model.GetMemberVisitorVo;
import com.proclivis.smartknock.Model.MemberDetail;
import com.proclivis.smartknock.R;
import com.proclivis.smartknock.Retrf.RestClient;


import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MemberVisitorPagerAdapter extends PagerAdapter {

    private Context mContext;
    private Activity activity;
    private ArrayList<GetMemberVisitorVo> visitorVos;
    private ArrayList<MemberDetail> societyNames;
    private int societySize;

    private ArrayList<String> numbers = new ArrayList<>();

    private String reason = "";
    private DatabaseHelper db;

    public MemberVisitorPagerAdapter(Context mContext, Activity activity, ArrayList<GetMemberVisitorVo> visitorVos, ArrayList<MemberDetail> societyDetails) {
        this.mContext = mContext;
        this.activity = activity;
        this.visitorVos = visitorVos;
        this.societyNames = societyDetails;
        societySize = societyDetails.size();

        int n = visitorVos.size();
        for (int i = 0; i < visitorVos.size(); i++) {
            numbers.add(i, String.valueOf(n));
            n--;
        }
    }

    public MemberVisitorPagerAdapter() {

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

    @Override
    public int getItemPosition(@NonNull Object object) {
        return super.getItemPosition(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewGroup view = null;

        TimeZone tz = TimeZone.getDefault();
        Log.e("MEMBER ADAPTER==","TimeZone   "+tz.getDisplayName(false, TimeZone.SHORT)+" Timezon id :: " +tz.getID());

        Calendar cal = Calendar.getInstance();
        TimeZone tz1 = cal.getTimeZone();
        Log.e("MEMBER ADAPTER==","TimeZone   "+tz1);

        switch (mContext.getResources().getConfiguration().orientation) {
            case Configuration.ORIENTATION_PORTRAIT:
                view = (ViewGroup) inflater.inflate(R.layout.pending_visitor_item_potrait, container, false);
                break;

            case Configuration.ORIENTATION_LANDSCAPE:
                view = (ViewGroup) inflater.inflate(R.layout.pending_visitor_item_landscape, container, false);
        }

        final TextView txtId, txtName, txtComingFrom, txtPurpose, txtMobile, txtComment , txtRegisterOn , txtLeftOn, txtSocietyName, txtSocietyMessage;
        final LinearLayout llLeft, llBlock, llGuard, llConfirm, llResponse , llComment,llTimezone;
        ImageView imgDone, imgGivenResponse;
        ImageView imgProfile, imgBackGround;

        assert view != null;
        txtName = view.findViewById(R.id.txtName);
        txtMobile = view.findViewById(R.id.txtMobile);
        txtComingFrom = view.findViewById(R.id.txtComingFrom);
        txtPurpose = view.findViewById(R.id.txtPurpose);
        txtId = view.findViewById(R.id.txtId);
        txtRegisterOn = view.findViewById(R.id.txtRegisterOn);
        imgProfile = view.findViewById(R.id.imgProfile);
        txtSocietyName = view.findViewById(R.id.txtSocietyName);
        txtSocietyMessage = view.findViewById(R.id.txtSocietyMessage);
        llLeft = view.findViewById(R.id.llLeft);
        llBlock = view.findViewById(R.id.llBlock);
        llGuard = view.findViewById(R.id.llGuard);
        llConfirm = view.findViewById(R.id.llConfirm);
        imgDone = view.findViewById(R.id.imgDone);
        txtLeftOn = view.findViewById(R.id.txtLeftOn);
        imgBackGround = view.findViewById(R.id.imgBackGround);
        llResponse = view.findViewById(R.id.llResponse);
        llComment = view.findViewById(R.id.llComment);
        llTimezone=view.findViewById(R.id.ll_timezone);
        txtComment = view.findViewById(R.id.txtComment);
        imgGivenResponse = view.findViewById(R.id.imgGivenResponse);

        txtComingFrom.setSelected(true);
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

        txtId.setText(numbers.get(position));

        final GetMemberVisitorVo data = visitorVos.get(position);
        if (Integer.parseInt(data.getVisitorCount()) > 0) {
            txtName.setText(String.format("%s + %s", data.getVisitorName(), data.getVisitorCount()));
        } else {
            txtName.setText(data.getVisitorName());
        }

        //to check timezone is IST(India standard timezone)
        if (tz.getDisplayName(false, TimeZone.SHORT).equalsIgnoreCase("GMT+05:30") || tz.getID().equalsIgnoreCase("Asia/Calcutta") || tz.getID().equalsIgnoreCase("Asia/Kolkata"))
        {

            llTimezone.setVisibility(View.GONE);
        }
        else
        {
            llTimezone.setVisibility(View.VISIBLE);
        }


        txtMobile.setText(data.getVisitorMobileNo());
        txtComingFrom.setText(data.getVisitorComingFrom());
        txtPurpose.setText(data.getVisitorPurpose());

        if (data.getCustomerMessage().length() > 0) {
            txtSocietyMessage.setVisibility(View.VISIBLE);
            String msg = data.getCustomerMessage();
            String newMsg = msg.replace("%27", "'");
            txtSocietyMessage.setText(newMsg);
        } else {
            txtSocietyMessage.setVisibility(View.GONE);
        }

        txtRegisterOn.setText(data.getVisitorDateTimeIn());

        txtSocietyName.setText(String.format("Visitor for %s", data.getCustomerName()));

        if (data.getVisitorImage().length() > 0) {
            Glide.with(mContext)
                    .load(data.getVisitorImage())
                    .into(imgProfile);
        }

        for (int i = 0; i < societySize; i++) {
            if (societyNames.get(i).getName().equals(data.getCustomerName())) {

                if (i == 0) {
                    imgBackGround.setBackgroundResource(R.drawable.back_image_1);
                } else if (i == 1) {
                    imgBackGround.setBackgroundResource(R.drawable.back_image_2);
                } else if (i == 2) {
                    imgBackGround.setBackgroundResource(R.drawable.back_image_3);
                } else if (i == 3) {
                    imgBackGround.setBackgroundResource(R.drawable.back_image_4);
                } else if (i == 4) {
                    imgBackGround.setBackgroundResource(R.drawable.back_image_5);
                } else if (i == 5) {
                    imgBackGround.setBackgroundResource(R.drawable.back_image_6);
                } else {
                    imgBackGround.setBackgroundResource(R.drawable.back_image_7);
                }
            }
        }

        if (!data.getVisitorDateTimeOut().equals("0000-00-00 00:00:00")) {
            imgDone.setVisibility(View.VISIBLE);
            llLeft.setVisibility(View.VISIBLE);
            txtLeftOn.setText(Util.converter(data.getVisitorDateTimeOut(), "yyyy-MM-dd HH:mm:ss", "dd-MMM-yyyy HH:mm"));


        } else {
            imgDone.setVisibility(View.GONE);
            llLeft.setVisibility(View.GONE);
        }

        if (!data.getPro().equalsIgnoreCase("yes")) {
            llComment.setVisibility(View.GONE);
            llResponse.setVisibility(View.GONE);
            imgGivenResponse.setVisibility(View.GONE);
        } else {
            if (data.getVisitorType().equalsIgnoreCase("1")) {
                llComment.setVisibility(View.GONE);
                llResponse.setVisibility(View.GONE);
                imgGivenResponse.setVisibility(View.GONE);
            } else {
                if (!data.getVisitorDateTimeOut().equals("0000-00-00 00:00:00")) {
                    llResponse.setVisibility(View.GONE);
                    imgGivenResponse.setVisibility(View.VISIBLE);

                    if (!data.getStatus().equalsIgnoreCase("")) {
                        if (data.getStatus().equalsIgnoreCase(Constant.REJECT)) {
                            llComment.setVisibility(View.VISIBLE);
                            txtComment.setText(data.getReason());
                        } else {
                            llComment.setVisibility(View.GONE);
                        }

                        switch (data.getStatus()) {
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
                    } else {
                        llComment.setVisibility(View.GONE);
                        llResponse.setVisibility(View.GONE);
                        imgGivenResponse.setVisibility(View.GONE);
                    }
                } else {

                    if (!data.getStatus().equalsIgnoreCase("")) {

                        llResponse.setVisibility(View.GONE);
                        imgGivenResponse.setVisibility(View.VISIBLE);
                        switch (data.getStatus()) {
                            case Constant.REJECT:
                                imgGivenResponse.setBackground(mContext.getResources().getDrawable(R.drawable.block));
                                llComment.setVisibility(View.VISIBLE);
                                txtComment.setText(data.getReason());
                                break;
                            case Constant.ACCEPT:
                                imgGivenResponse.setBackground(mContext.getResources().getDrawable(R.drawable.confirm));
                                llComment.setVisibility(View.GONE);
                                break;
                            case Constant.GUARD:
                                imgGivenResponse.setBackground(mContext.getResources().getDrawable(R.drawable.guard));
                                llComment.setVisibility(View.GONE);
                                break;
                        }
                    } else {
                        llResponse.setVisibility(View.VISIBLE);
                        llComment.setVisibility(View.GONE);
                        imgGivenResponse.setVisibility(View.GONE);
                    }



                }
            }
        }

        llBlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (data.getStatus().equalsIgnoreCase("")) {
                    final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
                    LayoutInflater inflater = activity.getLayoutInflater();
                    @SuppressLint("InflateParams") final View dialogView = inflater.inflate(R.layout.block_dialoge, null);
                    dialogBuilder.setView(dialogView);
                    dialogBuilder.setTitle("Select Reason");
                    dialogBuilder.setCancelable(false);

                    final AlertDialog alertDialog = dialogBuilder.create();
                    alertDialog.show();

                    RadioGroup rdg = dialogView.findViewById(R.id.rdg);
                    RadioButton rbNotHome = dialogView.findViewById(R.id.rbNotHome);
                    final EditText edtReason = dialogView.findViewById(R.id.edtReason);
                    TextView txtCancel = dialogView.findViewById(R.id.txtCancel);
                    TextView txtAdd = dialogView.findViewById(R.id.txtAdd);

                    edtReason.setVisibility(View.GONE);
                    rbNotHome.setChecked(true);
                    reason = Constant.REASON1;

                    rdg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        public void onCheckedChanged(RadioGroup group, int checkedId) {
                            RadioButton checkedRadioButton = group.findViewById(checkedId);
                            boolean isChecked = checkedRadioButton.isChecked();
                            if (isChecked) {
                                switch (checkedRadioButton.getText().toString()) {
                                    case Constant.REASON1:
                                        edtReason.setVisibility(View.GONE);
                                        reason = Constant.REASON1;
                                        break;
                                    case Constant.REASON2:
                                        edtReason.setVisibility(View.GONE);
                                        reason = Constant.REASON2;
                                        break;
                                    case Constant.REASON3:
                                        edtReason.setVisibility(View.GONE);
                                        reason = Constant.REASON3;
                                        break;
                                    case Constant.REASON4:
                                        edtReason.setVisibility(View.VISIBLE);
                                        break;
                                }
                            }
                        }
                    });

                    txtCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.cancel();
                        }
                    });

                    txtAdd.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            if (edtReason.getVisibility() == View.VISIBLE && edtReason.getText().toString().equals("")) {
                                edtReason.setError("please enter reason");
                                edtReason.requestFocus();
                            } else if (edtReason.getVisibility() == View.VISIBLE && !edtReason.getText().toString().equals("")) {
                                reason = edtReason.getText().toString();
                                updateResponse(Constant.REJECT, reason, data.getVistorRecordId(), position, llBlock , llComment , txtComment);
                                alertDialog.cancel();
                            } else {
                                updateResponse(Constant.REJECT, reason, data.getVistorRecordId(), position, llBlock , llComment , txtComment);
                                alertDialog.cancel();
                            }
                        }
                    });
                }
            }
        });

        llConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (data.getStatus().equalsIgnoreCase("")) {
                    updateResponse(Constant.ACCEPT, "", data.getVistorRecordId(), position, llConfirm , llComment , txtComment);
                }
            }
        });

        llGuard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (data.getStatus().equalsIgnoreCase("")) {
                    updateResponse(Constant.GUARD, "", data.getVistorRecordId(), position, llGuard , llComment , txtComment);
                }
            }
        });

        container.addView(view);
        return view;
    }

    private void updateResponse(final String type, final String reason, final String id, final int position, final View view , final View llComment ,  final TextView txtComment) {

        if (!Util.isOnline(mContext)){
            Snackbar snackbar = Snackbar
                    .make(MemberPagerActivity.llMain, "No internet connection", Snackbar.LENGTH_LONG);
            snackbar.show();
        } else {
            MemberPagerActivity.progress.setVisibility(View.VISIBLE);
            activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            try {
                RestClient.getApi().member_accept_reject_visitor(Util.ReadSharePreference(mContext, Constant.MEMBER_ID), id, type, reason,
                        new Callback<Response>() {
                            @Override
                            public void success(Response response, Response response2) {
                                try {

                                    JSONObject jsonObject = new JSONObject(Util.getString(response.getBody().in()));

                                    switch (jsonObject.getString("status")) {
                                        case "true":

                                            MemberPagerActivity.progress.setVisibility(View.GONE);
                                            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                            db = new DatabaseHelper(mContext);

                                            db.updateMemberVisitor(id, reason, type);
                                            visitorVos.get(position).setReason(reason);
                                            visitorVos.get(position).setStatus(type);
                                            Constant.getMemberVisitorVo = visitorVos;

                                            MemberMainActivity.setOfflineData();
                                            view.setBackgroundColor(mContext.getResources().getColor(R.color.lightYellow));
                                            if (type.equalsIgnoreCase(Constant.REJECT)){
                                                llComment.setVisibility(View.VISIBLE);
                                                txtComment.setText(reason);
                                            } else {
                                                llComment.setVisibility(View.GONE);
                                            }
                                            Snackbar snackbar = Snackbar
                                                    .make(MemberPagerActivity.llMain, jsonObject.getString("message"), Snackbar.LENGTH_LONG);
                                            snackbar.show();
                                            break;
                                        case "false":

                                            MemberPagerActivity.progress.setVisibility(View.GONE);
                                            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                            Snackbar snack = Snackbar
                                                    .make(MemberPagerActivity.llMain, jsonObject.getString("message"), Snackbar.LENGTH_LONG);
                                            snack.show();
                                            break;
                                        default:
                                            MemberPagerActivity.progress.setVisibility(View.GONE);
                                            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                            Util.logout(activity, MemberPagerActivity.llMain, jsonObject.getString("message"));
                                            break;
                                    }

                                } catch (Exception e) {
                                    MemberPagerActivity.progress.setVisibility(View.GONE);
                                    activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    e.printStackTrace();
                                    Log.e("LoginRetrofit Error==>", e.toString());
                                    Snackbar snackbar = Snackbar
                                            .make(MemberPagerActivity.llMain, "Something went wrong, please try again", Snackbar.LENGTH_LONG);
                                    snackbar.show();
                                }
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                MemberPagerActivity.progress.setVisibility(View.GONE);
                                activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                Log.e("LoginRetrofit Error==>", error.toString());
                                Snackbar snackbar = Snackbar.make(MemberPagerActivity.llMain, "Something went wrong, please try again", Snackbar.LENGTH_LONG);
                                snackbar.show();
                            }
                        });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}