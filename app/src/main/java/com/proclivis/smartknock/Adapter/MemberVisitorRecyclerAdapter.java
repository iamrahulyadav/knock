package com.proclivis.smartknock.Adapter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.proclivis.smartknock.Activity.Member.MemberMainActivity;
import com.proclivis.smartknock.Common.Constant;
import com.proclivis.smartknock.Common.Util;
import com.proclivis.smartknock.Model.GetMemberVisitorVo;
import com.proclivis.smartknock.Model.MemberDetail;
import com.proclivis.smartknock.R;

import java.util.ArrayList;

public class MemberVisitorRecyclerAdapter extends RecyclerView.Adapter<MemberVisitorRecyclerAdapter.MyViewHolder> implements Filterable {

    public Context context;
    private ArrayList<GetMemberVisitorVo> visitorVos;
    private ArrayList<GetMemberVisitorVo> filterVisitor;
    private ArrayList<MemberDetail> societyNames = new ArrayList<>();
    private ArrayList<String> numbers = new ArrayList<>();


    public MemberVisitorRecyclerAdapter() {

    }


    class MyViewHolder extends RecyclerView.ViewHolder {

        LinearLayout llMain;
        de.hdodenhof.circleimageview.CircleImageView imgBackGround;
        TextView txtId, txtName, txtInTime, txtTime;
        ImageView imgDone;
        RelativeLayout llImg;

        MyViewHolder(View view) {
            super(view);
            llMain = view.findViewById(R.id.llMain);
            imgBackGround = view.findViewById(R.id.imgBackGround);
            txtId = view.findViewById(R.id.txtId);
            txtName = view.findViewById(R.id.txtName);
            txtInTime = view.findViewById(R.id.txtInTime);
            imgDone = view.findViewById(R.id.imgDone);
            llImg = view.findViewById(R.id.llImg);
            txtTime = view.findViewById(R.id.txtTime);
        }
    }

    public MemberVisitorRecyclerAdapter(Context context, ArrayList<GetMemberVisitorVo> visitorVos, ArrayList<MemberDetail> societyDetails) {
        this.context = context;
        this.visitorVos = visitorVos;
        this.societyNames = societyDetails;
        this.filterVisitor = visitorVos;

        int n = visitorVos.size();
        for (int i = 0; i < visitorVos.size(); i++) {
            numbers.add(i, String.valueOf(n));
            n--;
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.member_visitor_recycler_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final GetMemberVisitorVo data = filterVisitor.get(position);

        holder.txtId.setText(numbers.get(position));

        if (Integer.parseInt(data.getVisitorCount()) > 0) {
            holder.txtName.setText(data.getVisitorName() + " + " + data.getVisitorCount());
        } else {
            holder.txtName.setText(data.getVisitorName());
        }

        holder.txtInTime.setText("Visited on  " + data.getVisitorDateTimeIn());

        if (!data.getVisitorDateTimeOut().equals("0000-00-00 00:00:00")) {
            holder.imgDone.setVisibility(View.VISIBLE);
            holder.imgDone.setBackground(context.getResources().getDrawable(R.drawable.done));
            holder.txtTime.setVisibility(View.GONE);
        } else {
            holder.imgDone.setVisibility(View.GONE);

            if (data.getVisitorType().equalsIgnoreCase("1")){
                holder.txtTime.setVisibility(View.GONE);
            } else {
                if (data.getPro().equalsIgnoreCase("yes")) {
                    if (!data.getStatus().equalsIgnoreCase("")){
                        holder.imgDone.setVisibility(View.VISIBLE);
                        switch (data.getStatus()) {
                            case Constant.REJECT:
                                holder.imgDone.setBackground(context.getResources().getDrawable(R.drawable.block));
                                break;
                            case Constant.ACCEPT:
                                holder.imgDone.setBackground(context.getResources().getDrawable(R.drawable.confirm));
                                break;
                            case Constant.GUARD:
                                holder.imgDone.setBackground(context.getResources().getDrawable(R.drawable.guard));
                                break;
                        }
                    } else {
                        holder.txtTime.setVisibility(View.VISIBLE);
                        holder.txtTime.setText(Util.timeCalculation(data.getVisitorDateTimeIn(), "dd-MMM-yyyy HH:mm"));
                    }
                }
            }
        }

        holder.llMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notifyDataSetChanged();
                clickListener.onItemClick(data, position);

            }
        });

        for (int i = 0; i < societyNames.size(); i++) {
            if (societyNames.get(i).getName().equals(data.getCustomerName())) {

                if (i == 0) {
                    holder.imgBackGround.setBackgroundResource(R.drawable.back_image_1);
                } else if (i == 1) {
                    holder.imgBackGround.setBackgroundResource(R.drawable.back_image_2);
                } else if (i == 2) {
                    holder.imgBackGround.setBackgroundResource(R.drawable.back_image_3);
                } else if (i == 3) {
                    holder.imgBackGround.setBackgroundResource(R.drawable.back_image_4);
                } else if (i == 4) {
                    holder.imgBackGround.setBackgroundResource(R.drawable.back_image_5);
                } else if (i == 5) {
                    holder.imgBackGround.setBackgroundResource(R.drawable.back_image_6);
                } else {
                    holder.imgBackGround.setBackgroundResource(R.drawable.back_image_7);
                }
            }
        }
    }

    private static ClickListener clickListener;

    public void setOnItemClickListener(ClickListener clickListener) {
        MemberVisitorRecyclerAdapter.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(GetMemberVisitorVo searchedData, int position);
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

                    filterVisitor = visitorVos;
                } else {

                    ArrayList<GetMemberVisitorVo> filteredList = new ArrayList<>();

                    for (GetMemberVisitorVo androidVersion : visitorVos) {

                        if (androidVersion.getVisitorName().toLowerCase().contains(charString.toLowerCase()) || androidVersion.getVisitorMobileNo().toLowerCase().contains(charString.toLowerCase()) ||
                                androidVersion.getVisitorDateTimeIn().toLowerCase().contains(charString.toLowerCase()) ||
                                androidVersion.getCustomerName().toLowerCase().contains(charString.toLowerCase()) || androidVersion.getVisitorComingFrom().toLowerCase().contains(charString.toLowerCase()) ||
                                Util.converter(androidVersion.getVisitorDateTimeOut().toLowerCase(), "yyyy-MM-dd HH:mm:ss", "dd-MM-yyyy HH:mm").contains(charString.toLowerCase()) ||
                                androidVersion.getVisitorPurpose().toLowerCase().contains(charString.toLowerCase())) {

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
                filterVisitor = (ArrayList<GetMemberVisitorVo>) filterResults.values;
                if (filterVisitor != null) {
                    MemberMainActivity.txtResult.setText("Found " + filterVisitor.size() + " Visits");
                }

                if (filterVisitor!= null && filterVisitor.size()>0){
                    int n = filterVisitor.size();
                    for (int i = 0; i < filterVisitor.size(); i++) {
                        numbers.add(i, String.valueOf(n));
                        n--;
                    }
                    Constant.getMemberVisitorVo = filterVisitor;
                }

                if (filterVisitor != null && filterVisitor.size() == 0) {
                    MemberMainActivity.swipeRefreshRecycler.setVisibility(View.GONE);
                    MemberMainActivity.llError.setVisibility(View.VISIBLE);
                } else {
                    MemberMainActivity.swipeRefreshRecycler.setVisibility(View.VISIBLE);
                    MemberMainActivity.llError.setVisibility(View.GONE);
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