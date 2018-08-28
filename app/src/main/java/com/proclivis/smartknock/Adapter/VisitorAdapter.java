package com.proclivis.smartknock.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;


import com.proclivis.smartknock.Activity.Member.SettingMemberActivity;
import com.proclivis.smartknock.Common.Constant;
import com.proclivis.smartknock.Common.Util;
import com.proclivis.smartknock.Model.MemberDetail;
import com.proclivis.smartknock.R;
import com.proclivis.smartknock.Retrf.RestClient;

import org.json.JSONObject;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class VisitorAdapter extends RecyclerView.Adapter<VisitorAdapter.MyViewHolder> {


    private ArrayList<MemberDetail> memberDetails;
    private Activity activity;
    public VisitorAdapter() {

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView txtDetail , txtReason;
        public LinearLayout llMain, llResponse;
        public RadioGroup rdg;
        public RadioButton rbAlwaysAsk, rbAlwaysAccept, rbAlwaysReject;

        MyViewHolder(View view) {
            super(view);
            txtDetail = view.findViewById(R.id.txtDetail);

            llMain = view.findViewById(R.id.llMain);
            llResponse = view.findViewById(R.id.llResponse);
            rdg = view.findViewById(R.id.rdg);
            rbAlwaysAsk = view.findViewById(R.id.rbAlwaysAsk);
            rbAlwaysAccept = view.findViewById(R.id.rbAlwaysAccept);
            rbAlwaysReject = view.findViewById(R.id.rbAlwaysReject);
            txtReason = view.findViewById(R.id.txtReason);
        }
    }

    public VisitorAdapter( Activity activity, ArrayList<MemberDetail> memberDetails) {
        this.memberDetails = memberDetails;
        this.activity = activity;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.visitors_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        final MemberDetail data = memberDetails.get(position);

        if (data.getFlatNo().equals("")) {
            holder.txtDetail.setText(data.getAddress1());
        } else {
            holder.txtDetail.setText(String.format("%s, %s", data.getFlatNo(), data.getAddress1()));
        }


        if (position % 2 == 0) {
            holder.llMain.setBackgroundResource(R.color.lightGray);
        } else {
            holder.llMain.setBackgroundResource(R.color.white);
        }

        if (data.getPro().equalsIgnoreCase("yes")) {
            holder.llResponse.setVisibility(View.VISIBLE);
        } else {
            holder.llResponse.setVisibility(View.GONE);
        }

        if (data.getPro().equalsIgnoreCase("yes")){
            switch (data.getStatus()) {
                case Constant.ALWAYS_ASK:
                    holder.rbAlwaysAsk.setChecked(true);
                    holder.txtReason.setVisibility(View.GONE);
                    break;
                case Constant.ALWAYS_ACCEPT:
                    holder.rbAlwaysAccept.setChecked(true);
                    holder.txtReason.setVisibility(View.GONE);
                    break;
                case Constant.ALWAYS_REJECT:
                    holder.rbAlwaysReject.setChecked(true);
                    holder.txtReason.setVisibility(View.VISIBLE);
                    holder.txtReason.setText(String.format("Reason: %s", data.getReason()));
                    break;
                default:
                    holder.rbAlwaysAsk.setChecked(true);
                    holder.txtReason.setVisibility(View.GONE);
                    break;
            }
        }


        holder.rdg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton checkedRadioButton = group.findViewById(checkedId);
                boolean isChecked = checkedRadioButton.isChecked();
                if (isChecked) {
                    switch (checkedRadioButton.getText().toString()) {
                        case Constant.ALWAYS_ASK:
                            holder.txtReason.setVisibility(View.GONE);
                            changeDefaultResponse(data.getMId() , Constant.ALWAYS_ASK,"" ,  data.getInstallationId() , holder.txtReason);
                            holder.rbAlwaysAsk.setChecked(true);
                            break;
                        case Constant.ALWAYS_ACCEPT:
                            holder.txtReason.setVisibility(View.GONE);
                            changeDefaultResponse(data.getMId() , Constant.ALWAYS_ACCEPT, "" , data.getInstallationId() , holder.txtReason);
                            holder.rbAlwaysAccept.setChecked(true);
                            break;
                        case Constant.ALWAYS_REJECT:
                            setAlwaysBlock(data.getInstallationId() , data.getMId() , holder.txtReason);
                            //holder.rbAlwaysReject.setChecked(true);
                            break;
                        default:
                            holder.txtReason.setVisibility(View.GONE);
                            changeDefaultResponse(data.getMId() , Constant.ALWAYS_ASK, "" ,  data.getInstallationId() , holder.txtReason);
                            holder.rbAlwaysAsk.setChecked(true);
                            break;
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return memberDetails.size();
    }

    private void changeDefaultResponse(final String member_id , final String value , final String reason, final String installation_id , final TextView txtReason) {
        SettingMemberActivity.progress.setVisibility(View.VISIBLE);
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        try {
            RestClient.getApi().set_status_member(member_id, value, reason ,  installation_id,
                    new Callback<Response>() {
                        @Override
                        public void success(Response response, Response response2) {
                            try {

                                JSONObject jsonObject = new JSONObject(Util.getString(response.getBody().in()));

                                SettingMemberActivity.progress.setVisibility(View.GONE);
                                activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                switch (jsonObject.getString("status")) {
                                    case "true": {
                                        Snackbar snackbar = Snackbar
                                                .make(SettingMemberActivity.llMain, jsonObject.getString("message"), Snackbar.LENGTH_LONG);
                                        snackbar.show();

                                        if (value.equalsIgnoreCase(Constant.ALWAYS_REJECT)){
                                            txtReason.setVisibility(View.VISIBLE);
                                            txtReason.setText(String.format("Reason: %s", reason));
                                        } else {
                                            txtReason.setVisibility(View.GONE);
                                        }
                                        break;
                                    }
                                    case "false":

                                        Snackbar snackbar = Snackbar
                                                .make(SettingMemberActivity.llMain, jsonObject.getString("message"), Snackbar.LENGTH_LONG);
                                        snackbar.show();
                                        break;
                                    default:
                                        Util.logout(activity, SettingMemberActivity.llMain, jsonObject.getString("message"));
                                        break;
                                }

                            } catch (Exception e) {
                                SettingMemberActivity.progress.setVisibility(View.GONE);
                                activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                e.printStackTrace();
                                Log.e("LoginRetrofit Error==>", e.toString());
                                Snackbar snackbar = Snackbar
                                        .make(SettingMemberActivity.llMain, "Something went wrong, please try again", Snackbar.LENGTH_LONG);
                                snackbar.show();
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            SettingMemberActivity.progress.setVisibility(View.GONE);
                            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            Log.e("LoginRetrofit Error==>", error.toString());
                            Snackbar snackbar = Snackbar
                                    .make(SettingMemberActivity.llMain, "Something went wrong, please try again", Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private  String reason;
    private void setAlwaysBlock(final String installation_id , final String id , final TextView txtReason){

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
                    changeDefaultResponse(id , Constant.ALWAYS_REJECT, reason ,  installation_id , txtReason);
                    alertDialog.cancel();
                } else {
                    changeDefaultResponse(id , Constant.ALWAYS_REJECT, reason ,  installation_id , txtReason);
                    alertDialog.cancel();
                }
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}