package com.proclivis.smartknock.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.proclivis.smartknock.DataBase.VisitorDB;
import com.proclivis.smartknock.R;

import java.util.ArrayList;

public class VisitorAutoCompleteAdapter extends ArrayAdapter<VisitorDB> {

    private ArrayList<VisitorDB> items;
    private ArrayList<VisitorDB> itemsAll;
    private ArrayList<VisitorDB> suggestions;

    @SuppressWarnings("unchecked")
    public VisitorAutoCompleteAdapter(Context context , ArrayList<VisitorDB> items) {
        super(context, R.layout.autocomplete_item, items);
        this.items = items;
        this.itemsAll =  (ArrayList<VisitorDB>) items.clone();
        this.suggestions = new ArrayList<>();
    }


    private static ClickListener clickListener;
    public void setOnItemClickListener(ClickListener clickListener) {
        VisitorAutoCompleteAdapter.clickListener = clickListener;
    }
    public interface ClickListener {
        void onItemClick(VisitorDB searchedData);
    }

    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            v = LayoutInflater.from(getContext()).inflate(R.layout.autocomplete_item, parent, false);
        }
        final VisitorDB data = items.get(position);
        if (data != null) {
            LinearLayout llMain =  v.findViewById(R.id.llMain);
            TextView txtName =  v.findViewById(R.id.txtName);
            TextView txtMobile =  v.findViewById(R.id.txtMobile);
            TextView txtAddress =  v.findViewById(R.id.txtAddress);

            txtName.setSelected(true);
            txtMobile.setSelected(true);
            txtAddress.setSelected(true);

            txtName.setText(String.format("Name: %s", data.getName()));
            txtMobile.setText(String.format("Mobile: %s", data.getMobileNo()));
            txtAddress.setText(String.format("From: %s", data.getComingFrom()));

            if (position%2==0){
                llMain.setBackgroundResource(R.color.white);
            }else {
                llMain.setBackgroundResource(R.color.lightGray);
            }

            llMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListener.onItemClick(data);
                }
            });

        }
        return v;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return nameFilter;
    }

    private Filter nameFilter = new Filter() {
        @Override
        public String convertResultToString(Object resultValue) {
            return ((VisitorDB)(resultValue)).getMobileNo();
        }
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if(constraint != null) {
                suggestions.clear();
                for (VisitorDB customer : itemsAll) {
                    if(customer.getMobileNo().toLowerCase().startsWith(constraint.toString().toLowerCase())){
                        suggestions.add(customer);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            ArrayList<VisitorDB> filteredList = (ArrayList<VisitorDB>) results.values;
            if(results.count > 0) {
                clear();
                for (VisitorDB c : filteredList) {
                    add(c);
                }
                notifyDataSetChanged();
            }
        }
    };
}