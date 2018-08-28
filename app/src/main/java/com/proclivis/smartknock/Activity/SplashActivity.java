package com.proclivis.smartknock.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.proclivis.smartknock.Activity.Member.MemberMainActivity;
import com.proclivis.smartknock.Activity.User.UserMainActivity;
import com.proclivis.smartknock.Common.Constant;
import com.proclivis.smartknock.Common.Util;
import com.proclivis.smartknock.R;



public class SplashActivity extends AppCompatActivity {

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        switch (getResources().getConfiguration().orientation) {
            case Configuration.ORIENTATION_PORTRAIT:
                setContentView(R.layout.splash_activity_potrait);

                break;

            case Configuration.ORIENTATION_LANDSCAPE:
                setContentView(R.layout.splash_activity_landscape);



        }

        context=this;
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        new Handler().postDelayed(new Runnable()
        {

            @Override
            public void run() {
                if (!Util.ReadSharePreference(context, Constant.USER_ID).equals("")) {

                    Intent in = new Intent(context, UserMainActivity.class);
                    in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(in);
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                    finish();
                } else if (!Util.ReadSharePreference(context, Constant.MEMBER_ID).equals("")) {

                    Intent in = new Intent(context, MemberMainActivity.class);
                    in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(in);
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                    finish();
                }else {
                    Intent in = new Intent(context, LoginActivity.class);
                    in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(in);
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                    finish();
                }
            }
        }, 2000);
    }
}
