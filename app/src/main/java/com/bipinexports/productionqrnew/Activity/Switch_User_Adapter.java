package com.bipinexports.productionqrnew.Activity;

import android.graphics.Color;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bipinexports.productionqrnew.UserDataObject;

import com.bipinexports.productionqrnew.R;

import java.util.ArrayList;
import java.util.List;

public class Switch_User_Adapter extends RecyclerView.Adapter<Switch_User_Adapter.DataObjectHolder> {

    private ArrayList<UserDataObject> mDataset;
    private static Switch_User_Adapter.MyClickListener myClickListener;

    public static class DataObjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView username, newcode, section_name, txt_access;
        LinearLayout linear_cardview;
        CardView card_view;

        public DataObjectHolder(View itemView) {
            super(itemView);

            username = (TextView) itemView.findViewById(R.id.text_username);
            newcode = (TextView) itemView.findViewById(R.id.text_newcode);
            section_name = (TextView) itemView.findViewById(R.id.text_section_name);
            card_view =  itemView.findViewById(R.id.card_view);
            txt_access =  (TextView) itemView.findViewById(R.id.txt_access);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(getAdapterPosition(), v);
        }
    }

    public void setOnItemClickListener(Switch_User_Adapter.MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public Switch_User_Adapter(Switch_User_Activity switch_user_activity, ArrayList<UserDataObject> myDataset) {
        mDataset = myDataset;
    }

    @Override
    public Switch_User_Adapter.DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.switch_user_card_view, parent, false);
        Switch_User_Adapter.DataObjectHolder dataObjectHolder = new Switch_User_Adapter.DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(Switch_User_Adapter.DataObjectHolder holder, int position) {

        holder.username.setText(mDataset.get(position).getIndex() +". "+ mDataset.get(position).getUsername());
        holder.username.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        holder.username.setTextColor(Color.parseColor("#000000"));
        holder.username.setTypeface(null, Typeface.BOLD);

        holder.newcode.setText(mDataset.get(position).getNew_code().toString());
        holder.newcode.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        holder.newcode.setTextColor(Color.parseColor("#000000"));
        holder.newcode.setTypeface(null, Typeface.BOLD);

        holder.section_name.setText(mDataset.get(position).getSection_name().toString());
        holder.section_name.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        holder.section_name.setTextColor(Color.parseColor("#000000"));
        holder.section_name.setTypeface(null, Typeface.BOLD);

        if (mDataset.get(position).getAccessid().equals("null"))
        {
            holder.card_view.setBackgroundColor(Color.parseColor("#f5e7e7"));
            holder.txt_access.setText(mDataset.get(position).getStatus());
            holder.txt_access.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            holder.txt_access.setTextColor(Color.parseColor("#ff0000"));
            holder.txt_access.setTypeface(null, Typeface.BOLD);
        }
        else
        {
            holder.card_view.setBackgroundColor(Color.parseColor("#ebfee2"));
            holder.txt_access.setText(mDataset.get(position).getStatus());
            holder.txt_access.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            holder.txt_access.setTextColor(Color.parseColor("#31561f"));
            holder.txt_access.setTypeface(null, Typeface.BOLD);
        }
    }

    public void addItem(UserDataObject dataObj, int index) {
        mDataset.add(index, dataObj);
        notifyItemInserted(index);
    }

    public void deleteItem(int index) {
        mDataset.remove(index);
        notifyItemRemoved(index);
    }

    public String getId(String index) {
        return mDataset.get(Integer.parseInt(index)).getId();
    }

    public String getUsername(String index) {
        return mDataset.get(Integer.parseInt(index)).getUsername();
    }

    public String getNew_code(String index) {
        return mDataset.get(Integer.parseInt(index)).getNew_code();
    }

    public String getProcessorid(String index) {
        return mDataset.get(Integer.parseInt(index)).getProcessorid();
    }

    public String getSection_name(String index) {
        return mDataset.get(Integer.parseInt(index)).getSection_name();
    }

    public String getUnitid(String index) {
        return mDataset.get(Integer.parseInt(index)).getUnitid();
    }

    public String getStatus(String index) {
        return mDataset.get(Integer.parseInt(index)).getStatus();
    }

    public String getMessage(String index) {
        return mDataset.get(Integer.parseInt(index)).getMessage();
    }

    public String getAccessid(String index) {
        return mDataset.get(Integer.parseInt(index)).getAccessid();
    }

    public String getIsqc(String index) {
        return mDataset.get(Integer.parseInt(index)).getIsqc();
    }

    public void updateData(List<UserDataObject> newData) {
        mDataset.clear();                // Clear old data
        mDataset.addAll(newData);       // Add new list
        notifyDataSetChanged();         // Notify adapter
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }
}

