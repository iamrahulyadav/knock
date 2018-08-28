package com.proclivis.smartknock.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.proclivis.smartknock.Activity.User.WaitingActivity;
import com.proclivis.smartknock.Common.Constant;
import com.proclivis.smartknock.Common.Util;
import com.proclivis.smartknock.DataBase.VisitorOfflineDb;
import com.proclivis.smartknock.R;

import java.util.ArrayList;

public class WaitingAdapter extends RecyclerView.Adapter<WaitingAdapter.MyViewHolder> implements Filterable {

    public Context context;
    private ArrayList<VisitorOfflineDb> waitingVos;
    private ArrayList<VisitorOfflineDb> filterVisitor;

    public WaitingAdapter() {

    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        LinearLayout llMain;
        de.hdodenhof.circleimageview.CircleImageView imgBackGround;
        TextView txtName , txtInTime , txtTime , txtToMeet ,txtReason;
        ImageView imgDone;
        RelativeLayout llImg;

        MyViewHolder(View view) {
            super(view);
            llMain = view.findViewById(R.id.llMain);
            imgBackGround = view.findViewById(R.id.imgBackGround);
            txtName = view.findViewById(R.id.txtName);
            txtInTime = view.findViewById(R.id.txtInTime);
            imgDone = view.findViewById(R.id.imgDone);
            txtToMeet = view.findViewById(R.id.txtToMeet);
            txtReason = view.findViewById(R.id.txtReason);
            llImg = view.findViewById(R.id.llImg);
            txtTime = view.findViewById(R.id.txtTime);
        }
    }

    public WaitingAdapter(Context context, ArrayList<VisitorOfflineDb> waitingVos ) {
        this.context=context;
        this.waitingVos = waitingVos;
        this.filterVisitor = waitingVos;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = null;
        switch (context.getResources().getConfiguration().orientation) {
            case Configuration.ORIENTATION_PORTRAIT:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.waiting_recycler_item_portrait, parent, false);
                break;

            case Configuration.ORIENTATION_LANDSCAPE:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.waiting_recycler_item_landscape, parent, false);
        }

        return new MyViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final VisitorOfflineDb data = filterVisitor.get(position);


        if (Integer.parseInt(data.getCount()) > 0){
            holder.txtName.setText(data.getName() + " + " + data.getCount());
        } else {
            holder.txtName.setText(data.getName());
        }

        holder.txtInTime.setText("Visited on  " + data.getDate_time_in() );
        holder.txtToMeet.setText("To Meet:  " +data.getMember_name());
        if (Util.ReadSharePreference(context , Constant.USER_PRO).equalsIgnoreCase("yes")){
            switch (data.getStatus()) {
                case Constant.ACCEPT:
                    holder.txtReason.setVisibility(View.GONE);
                    holder.imgDone.setVisibility(View.VISIBLE);
                    holder.txtTime.setVisibility(View.GONE);
                    holder.imgDone.setBackground(context.getResources().getDrawable(R.drawable.confirm));
                    break;
                case Constant.REJECT:
                    holder.txtReason.setVisibility(View.VISIBLE);
                    holder.imgDone.setVisibility(View.VISIBLE);
                    holder.txtTime.setVisibility(View.GONE);
                    holder.imgDone.setBackground(context.getResources().getDrawable(R.drawable.block));
                    holder.txtReason.setText( data.getReason() );
                    break;
                case Constant.GUARD:
                    holder.txtReason.setVisibility(View.GONE);
                    holder.imgDone.setVisibility(View.VISIBLE);
                    holder.txtTime.setVisibility(View.GONE);
                    holder.imgDone.setBackground(context.getResources().getDrawable(R.drawable.guard));
                    break;
                default:
                    holder.txtReason.setVisibility(View.GONE);
                    holder.imgDone.setVisibility(View.GONE);
                    holder.txtTime.setVisibility(View.VISIBLE);
                    holder.txtTime.setText(Util.timeCalculation(data.getDate_time_in(), "dd-MMM-yyyy HH:mm"));
                    break;
            }
        } else {
            holder.imgDone.setVisibility(View.GONE);
            holder.txtTime.setVisibility(View.GONE);
            holder.txtReason.setVisibility(View.GONE);
        }


        holder.txtReason.setSelected(true);
        holder.txtInTime.setSelected(true);
        holder.txtToMeet.setSelected(true);

        holder.llMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickListener.onItemClick(position);
                notifyDataSetChanged();
            }
        });

        byte[] b1 = Base64.decode(data.getImage(), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(b1, 0, b1.length);
        holder.imgBackGround.setImageBitmap(bitmap);

    }

    private static ClickListener clickListener;

    public void setOnItemClickListener(ClickListener clickListener) {
        WaitingAdapter.clickListener = clickListener;
    }
    public interface ClickListener {
        void onItemClick(int position);
    }

    @Override
    public int getItemCount() {
        return filterVisitor.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String charString = charSequence.toString();

                if (charString.isEmpty()) {

                    filterVisitor = waitingVos;
                } else {

                    ArrayList<VisitorOfflineDb> filteredList = new ArrayList<>();

                    for (VisitorOfflineDb androidVersion : waitingVos) {

                        if (androidVersion.getName().toLowerCase().contains(charString.toLowerCase()) || androidVersion.getMobileNo().toLowerCase().contains(charString.toLowerCase()) ||
                                androidVersion.getDate_time_in().toLowerCase().contains(charString.toLowerCase()) ||
                                androidVersion.getComingFrom().toLowerCase().contains(charString.toLowerCase()) ||
                                Util.converter(androidVersion.getDate_time_out().toLowerCase() , "yyyy-MM-dd HH:mm:ss", "dd-MM-yyyy HH:mm").contains(charString.toLowerCase()) ||
                                androidVersion.getPurpose().toLowerCase().contains(charString.toLowerCase())) {

                            filteredList.add(androidVersion);
                        }
                    }

                    filterVisitor = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filterVisitor;
                return filterResults;
            }

            @SuppressLint("SetTextI18n")
            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filterVisitor = (ArrayList<VisitorOfflineDb>) filterResults.values;
                if (filterVisitor != null){
                    WaitingActivity.txtResult.setText("Found " + filterVisitor.size() + " Visits");
                }

                if (filterVisitor != null && filterVisitor.size()==0){
                    WaitingActivity.recycler.setVisibility(View.GONE);
                    WaitingActivity.llError.setVisibility(View.VISIBLE);
                } else {
                    WaitingActivity.recycler.setVisibility(View.VISIBLE);
                    WaitingActivity.llError.setVisibility(View.GONE);
                }
                notifyDataSetChanged();
            }
        };
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
