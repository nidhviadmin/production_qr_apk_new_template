package com.bipinexports.productionqrnew.Activity;

import android.graphics.Color;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bipinexports.productionqrnew.Machine_Service_Verification_Data_Object;
import com.bipinexports.productionqrnew.R;

import java.util.ArrayList;

public class Machine_Service_View_Adapter extends RecyclerView.Adapter<Machine_Service_View_Adapter.DataObjectHolder> {
    private ArrayList<Machine_Service_Verification_Data_Object> mDataset;
    private static MyClickListener myClickListener;

    public static class DataObjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView Service_Ref_No, Service_Date, Service_Reason, MachineNo, Vendor, Notes, Quantity, Service_Ref_No_Text ;

        public DataObjectHolder(View itemView) {
            super(itemView);
            Service_Ref_No = (TextView) itemView.findViewById(R.id.text_Service_Ref_No);
            Service_Date = (TextView) itemView.findViewById(R.id.text_Service_Date);
            Service_Reason = (TextView) itemView.findViewById(R.id.text_Service_Reason);
            MachineNo = (TextView) itemView.findViewById(R.id.text_MachineNo);
            Vendor = (TextView) itemView.findViewById(R.id.text_Vendor);
            Notes = (TextView) itemView.findViewById(R.id.text_Notes);
            Quantity = (TextView) itemView.findViewById(R.id.text_Quantity);
            Service_Ref_No_Text = (TextView) itemView.findViewById(R.id.Service_Ref_No);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(getAdapterPosition(), v);
        }
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public Machine_Service_View_Adapter(ArrayList<Machine_Service_Verification_Data_Object> myDataset) {
        mDataset = myDataset;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.machine_service_card_view, parent, false);
        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {


        Spannable word = new SpannableString(new String(String.valueOf(mDataset.get(position).getIndex())));
        word.setSpan(new ForegroundColorSpan(Color.BLUE), 0, word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.Service_Ref_No_Text.setText(word);
        Spannable wordTwo = new SpannableString(".  Service Ref No");
        wordTwo.setSpan(new ForegroundColorSpan(Color.BLACK), 0, wordTwo.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.Service_Ref_No_Text.append(wordTwo);


        holder.Service_Ref_No.setText(mDataset.get(position).getService_refno().toString());
        holder.Service_Ref_No.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        holder.Service_Ref_No.setTextColor(Color.parseColor("#000000"));
       // holder.Service_Ref_No.setTypeface(null, Typeface.BOLD);

        holder.Service_Date.setText(mDataset.get(position).getService_date().toString());
        holder.Service_Date.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        holder.Service_Date.setTextColor(Color.parseColor("#000000"));
        //holder.Service_Date.setTypeface(null, Typeface.BOLD);

        holder.Service_Reason.setText(mDataset.get(position).getService_reason().toString());
        holder.Service_Reason.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        holder.Service_Reason.setTextColor(Color.parseColor("#000000"));
//        holder.Service_Reason.setTypeface(null, Typeface.BOLD);

        holder.MachineNo.setText(mDataset.get(position).getMachine_no().toString());
        holder.MachineNo.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        holder.MachineNo.setTextColor(Color.parseColor("#000000"));
        //holder.MachineNo.setTypeface(null, Typeface.BOLD);

        holder.Vendor.setText(mDataset.get(position).getVendorname().toString());
        holder.Vendor.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        holder.Vendor.setTextColor(Color.parseColor("#000000"));
        //holder.Vendor.setTypeface(null, Typeface.BOLD);

        holder.Notes.setText(mDataset.get(position).getNotes().toString());
        holder.Notes.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        holder.Notes.setTextColor(Color.parseColor("#000000"));
        //holder.Notes.setTypeface(null, Typeface.BOLD);
    }

    public void addItem(Machine_Service_Verification_Data_Object dataObj, int index) {
        mDataset.add(index, dataObj);
        notifyItemInserted(index);
    }

    public void deleteItem(int index) {
        mDataset.remove(index);
        notifyItemRemoved(index);
    }

    public int getMac_id(int index) {
        return mDataset.get(index).getMac_id();
    }


    public String getService_refno(String index) {
        return mDataset.get(Integer.parseInt(index)).getService_refno();
    }

    public String getService_date(String index) {
        return mDataset.get(Integer.parseInt(index)).getService_date();
    }

    public String getVendorname(String index) {
        return mDataset.get(Integer.parseInt(index)).getVendorname();
    }

    public String getMachine_no(String index) {
        return mDataset.get(Integer.parseInt(index)).getMachine_no();
    }
    public String getService_reason(String index) {
        return mDataset.get(Integer.parseInt(index)).getService_reason();
    }
    public String getNotes(String index) {
        return mDataset.get(Integer.parseInt(index)).getNotes();
    }

    public int getIndex(int index) {
        return mDataset.get(index).getIndex();
    }


    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }
}
