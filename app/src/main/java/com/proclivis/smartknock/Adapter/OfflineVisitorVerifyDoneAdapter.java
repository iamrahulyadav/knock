package com.proclivis.smartknock.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.proclivis.smartknock.Activity.User.UserMainActivity;
import com.proclivis.smartknock.Activity.User.VisitorVerifyDoneActivity;
import com.proclivis.smartknock.Common.Constant;
import com.proclivis.smartknock.Common.Util;
import com.proclivis.smartknock.DataBase.VisitorOfflineDb;
import com.proclivis.smartknock.R;

import java.util.ArrayList;

public class OfflineVisitorVerifyDoneAdapter extends PagerAdapter {

    private Context mContext;
    private ArrayList<VisitorOfflineDb> getVisitorVos ;
    private ArrayList<String> numbers = new ArrayList<>();
    private Boolean doneClick = false;
    private Boolean intentDone = false;
    private Activity activity;

    public OfflineVisitorVerifyDoneAdapter(Context mContext, Activity activity ,ArrayList<VisitorOfflineDb> getVisitorVos) {
        this.mContext = mContext;
        this.getVisitorVos=getVisitorVos;
        this.activity=activity;

        int n = getVisitorVos.size();
        for (int i=0;i<getVisitorVos.size();i++){
            numbers.add(i , String.valueOf(n));
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

    @SuppressLint("SetTextI18n")
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

        TextView txtId, txtName, txtComingFrom, txtPurpose, txtMobile, txtRegisterOn , tatMessage , txtLeftOn;
        ImageView imgDone;
        LinearLayout llLeft;
        Button btnConfirm;
        de.hdodenhof.circleimageview.CircleImageView imgProfile;

        assert view != null;
        txtId = view.findViewById(R.id.txtId);

        txtName = view.findViewById(R.id.txtName);
        txtComingFrom = view.findViewById(R.id.txtComingFrom);
        txtPurpose = view.findViewById(R.id.txtPurpose);
        txtMobile = view.findViewById(R.id.txtMobile);
        txtRegisterOn = view.findViewById(R.id.txtRegisterOn);
        tatMessage = view.findViewById(R.id.txtMessege);
        txtLeftOn = view.findViewById(R.id.txtLeftOn);

        btnConfirm = view.findViewById(R.id.btnConfirm);
        imgProfile = view.findViewById(R.id.imgProfile);
        imgDone = view.findViewById(R.id.imgDone);
        llLeft = view.findViewById(R.id.llLeft);

        txtComingFrom.setSelected(true);
        txtPurpose.setSelected(true);
        txtName.setSelected(true);

        btnConfirm.setText("Done");
        imgDone.setVisibility(View.VISIBLE);
        llLeft.setVisibility(View.VISIBLE);
        tatMessage.setText(mContext.getResources().getString(R.string.thanku));

        txtId.setText(numbers.get(position));

        txtName.setText(getVisitorVos.get(position).getName() + " + " + getVisitorVos.get(position).getCount());
        txtComingFrom.setText(getVisitorVos.get(position).getComingFrom());
        txtPurpose.setText(getVisitorVos.get(position).getPurpose());
        txtMobile.setText(getVisitorVos.get(position).getMobileNo());

        txtRegisterOn.setText(Util.converter(getVisitorVos.get(position).getDate_time_in(), "yyyy-MM-dd HH:mm:ss", "dd-MMM-yyyy HH:mm"));
        txtLeftOn.setText(Util.converter(getVisitorVos.get(position).getDate_time_out(), "yyyy-MM-dd HH:mm:ss", "dd-MMM-yyyy HH:mm"));

        byte[] b1 = Base64.decode(getVisitorVos.get(position).getImage(), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(b1, 0, b1.length);
        imgProfile.setImageBitmap(bitmap);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doneClick =true;
                Constant.getVisitorVo = new ArrayList<>();
                VisitorVerifyDoneActivity.homeClick=false;
                Intent intent = new Intent(mContext , UserMainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
                activity.finish();
                activity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (!doneClick && !intentDone){
                    VisitorVerifyDoneActivity.homeClick=false;
                    intentDone =true;
                    Constant.getVisitorVo = new ArrayList<>();
                    Intent intent = new Intent(mContext , UserMainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                    activity.finish();
                    activity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                }

            }
        }, 10000);

        container.addView(view);
        return view;
    }

}