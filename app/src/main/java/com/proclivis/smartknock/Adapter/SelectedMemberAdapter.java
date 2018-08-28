package com.proclivis.smartknock.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.proclivis.smartknock.Common.Constant;
import com.proclivis.smartknock.DataBase.MemberDB;
import com.proclivis.smartknock.R;

import java.util.ArrayList;

public class SelectedMemberAdapter extends RecyclerView.Adapter<SelectedMemberAdapter.MyViewHolder> {

    public Context context;
    private ArrayList<MemberDB> selectedMember;

    public SelectedMemberAdapter() {

    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txtName;
        ImageView imgCancel;

        MyViewHolder(View view) {
            super(view);

            txtName = view.findViewById(R.id.txtName);
            imgCancel = view.findViewById(R.id.imgCancel);

        }
    }

    public SelectedMemberAdapter(Context context, ArrayList<MemberDB> visitorVos) {
        this.context = context;
        this.selectedMember= visitorVos;

    }

    @NonNull
    @Override
    public SelectedMemberAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.selected_member_item, parent, false);
        return new SelectedMemberAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectedMemberAdapter.MyViewHolder holder, final int position) {
        final MemberDB data = selectedMember.get(position);

        if (data.getName().length()> Constant.MemberNameLength){
            String newValue = data.getName().substring(0, Math.min(data.getName().length(), Constant.MemberNameLength));
            holder.txtName.setText(newValue + "..");
        } else {
            holder.txtName.setText(data.getName());
        }

        holder.imgCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickListener.onItemClick(data, position);
                notifyDataSetChanged();
            }
        });


    }

    private static ClickListener clickListener;

    public void setOnItemClickListener(ClickListener clickListener) {
        SelectedMemberAdapter.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(MemberDB searchedData, int position);
    }

    @Override
    public int getItemCount() {
        return selectedMember.size();
    }

}
