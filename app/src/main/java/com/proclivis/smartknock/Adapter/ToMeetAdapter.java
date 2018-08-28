package com.proclivis.smartknock.Adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.proclivis.smartknock.Activity.User.ToMeetActivity;
import com.proclivis.smartknock.Common.Constant;
import com.proclivis.smartknock.DataBase.MemberDB;
import com.proclivis.smartknock.R;

import java.util.ArrayList;

public class ToMeetAdapter extends  RecyclerView.Adapter<ToMeetAdapter.MyViewHolder> implements Filterable{


    private ArrayList<MemberDB> autoCompletesVos;
    private ArrayList<MemberDB> filtered_autoCompletesVos;

    public ToMeetAdapter() {

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView txtName ;
        public TextView txtAddress;
        public LinearLayout llMain;

        MyViewHolder(View view) {
            super(view);
            txtName = view.findViewById(R.id.txtName);
            txtAddress = view.findViewById(R.id.txtAddress);
            llMain = view.findViewById(R.id.llMain);
        }
    }

    public ToMeetAdapter( ArrayList<MemberDB> autoCompletesVos) {
        this.autoCompletesVos = autoCompletesVos;
        this.filtered_autoCompletesVos = autoCompletesVos;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.to_meet_recycler_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        final MemberDB data = filtered_autoCompletesVos.get(position);

        holder.txtName.setSelected(true);
        holder.txtAddress.setSelected(true);

        holder.txtName.setText(data.getName());

        if (data.getFlat_no().equals("")){
            holder.txtAddress.setVisibility(View.GONE);
        } else {
            holder.txtAddress.setVisibility(View.VISIBLE);
        }
        holder.txtAddress.setText(data.getFlat_no());
        if (Constant.selectedMember.size()!=0){
            for (int i=0;i< Constant.selectedMember.size();i++){
                if (data.getId().equals(Constant.selectedMember.get(i).getId())){
                    holder.llMain.setBackgroundResource(R.drawable.edittext_background_gray);
                    break;
                } else {
                    holder.llMain.setBackgroundResource(R.drawable.edittext_background);
                }
            }

        } else {
            holder.llMain.setBackgroundResource(R.drawable.edittext_background);
        }

        holder.llMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Boolean flag = false;
                if (Constant.selectedMember.size()!=0){
                    for (int i=0;i< Constant.selectedMember.size();i++){
                        if (data.getId().equals(Constant.selectedMember.get(i).getId())){
                            flag = true;
                            break;
                        }
                    }

                }
                clickListener.onItemClick(data , flag);

                notifyDataSetChanged();
            }
        });
    }

    private static ClickListener clickListener;

    public void setOnItemClickListener(ClickListener clickListener) {
        ToMeetAdapter.clickListener = clickListener;
    }
    public interface ClickListener {
        void onItemClick(MemberDB searchedData, Boolean selected);
    }

    @Override
    public int getItemCount() {
        return filtered_autoCompletesVos.size();
    }

    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String charString = charSequence.toString();

                if (charString.isEmpty()) {

                    filtered_autoCompletesVos = autoCompletesVos;
                } else {

                    ArrayList<MemberDB> filteredList = new ArrayList<>();

                    for (MemberDB androidVersion : autoCompletesVos) {

                        if (androidVersion.getName().toLowerCase().contains(charString.toLowerCase()) || androidVersion.getFlat_no().toLowerCase().contains(charString.toLowerCase())) {

                            filteredList.add(androidVersion);
                        }
                    }

                    filtered_autoCompletesVos = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filtered_autoCompletesVos;
                return filterResults;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filtered_autoCompletesVos = (ArrayList<MemberDB>) filterResults.values;

                if (filtered_autoCompletesVos != null && filtered_autoCompletesVos.size()==0){
                    ToMeetActivity.llError.setVisibility(View.VISIBLE);
                    ToMeetActivity.recycler.setVisibility(View.GONE);
                } else {
                    ToMeetActivity.llError.setVisibility(View.GONE);
                    ToMeetActivity.recycler.setVisibility(View.VISIBLE);
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