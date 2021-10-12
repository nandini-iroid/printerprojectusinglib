package com.example.demodata;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.ViewHolder> {
    private ArrayList<ItemsItem> listdata = new ArrayList<ItemsItem>();

    // RecyclerView recyclerView;
    public MyListAdapter(ArrayList<ItemsItem> listdata) {
        this.listdata = listdata;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.row_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.itemName.setText(listdata.get(position).getName()+"  ");
        holder.MRP.setText(listdata.get(position).getMRP());
        holder.QTY.setText(listdata.get(position).getQTY());
        holder.tvAmount.setText(listdata.get(position).getTvAmount());
    }


    @Override
    public int getItemCount() {
        return listdata.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView itemName;
        public TextView MRP;
        public TextView QTY;
        public TextView tvAmount;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemName = itemView.findViewById(R.id.itemName);
            this.MRP = itemView.findViewById(R.id.MRP);
            this.QTY = itemView.findViewById(R.id.QTY);
            this.tvAmount = itemView.findViewById(R.id.tvAmount);
        }
    }
}  
