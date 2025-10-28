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

import com.bipinexports.productionqrnew.Fabric_Receipt_Data_Object;
import com.bipinexports.productionqrnew.R;

import java.util.ArrayList;

public class Fabric_Receipt_View_Adapter extends RecyclerView.Adapter<Fabric_Receipt_View_Adapter.DataObjectHolder> {
    private ArrayList<Fabric_Receipt_Data_Object> mDataset;
    private static MyClickListener myClickListener;

    public static class DataObjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView joborderno, styleno, Vendor_Name, DC_No, DC_Date, text_Shipcode, text_sentweight, text_DC_No ;

        public DataObjectHolder(View itemView) {
            super(itemView);

            DC_No = (TextView) itemView.findViewById(R.id.DC_No);
            text_DC_No = (TextView) itemView.findViewById(R.id.text_DC_No);
            DC_Date = (TextView) itemView.findViewById(R.id.text_DC_Date);
            Vendor_Name = (TextView) itemView.findViewById(R.id.text_Vendor_Name);
            joborderno = (TextView) itemView.findViewById(R.id.text_joborderno);
            styleno = (TextView) itemView.findViewById(R.id.text_styleno);
            text_Shipcode = (TextView) itemView.findViewById(R.id.text_Shipcode);
            text_sentweight = (TextView) itemView.findViewById(R.id.text_sentweight);
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

    public Fabric_Receipt_View_Adapter(ArrayList<Fabric_Receipt_Data_Object> myDataset) {
        mDataset = myDataset;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fabric_receipt_card_view_row, parent, false);
        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {

        Spannable word = new SpannableString(new String(String.valueOf(mDataset.get(position).getIndex())));
        word.setSpan(new ForegroundColorSpan(Color.BLUE), 0, word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.DC_No.setText(word);
        Spannable wordTwo = new SpannableString(". DC No");
        wordTwo.setSpan(new ForegroundColorSpan(Color.BLACK), 0, wordTwo.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.DC_No.append(wordTwo);


        holder.text_DC_No.setText(mDataset.get(position).getDcno().toString());
        holder.text_DC_No.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        holder.text_DC_No.setTextColor(Color.parseColor("#000000"));
        // holder.text_DC_No.setTypeface(null, Typeface.BOLD);

        holder.DC_Date.setText(mDataset.get(position).getDcdate().toString());
        holder.DC_Date.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        holder.DC_Date.setTextColor(Color.parseColor("#000000"));

        holder.joborderno.setText(mDataset.get(position).getJoborderno().toString());
        holder.joborderno.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        holder.joborderno.setTextColor(Color.parseColor("#000000"));
       // holder.PO_No.setTypeface(null, Typeface.BOLD);

        holder.styleno.setText(mDataset.get(position).getStyleno().toString());
        holder.styleno.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        holder.styleno.setTextColor(Color.parseColor("#000000"));

        holder.Vendor_Name.setText(mDataset.get(position).getFromvendor().toString());
        holder.Vendor_Name.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        holder.Vendor_Name.setTextColor(Color.parseColor("#000000"));

        String type = "";
        if(mDataset.get(position).getStatus().equals("S"))
        {
            type = "Size wise";
        }
        else  if(mDataset.get(position).getStatus().equals("O"))
        {
            type = "All Sizes";
        }
        holder.text_Shipcode.setText(mDataset.get(position).getShipcode().toString());
        holder.text_Shipcode.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        holder.text_Shipcode.setTextColor(Color.parseColor("#000000"));

        holder.text_sentweight.setText(mDataset.get(position).getSentweight().toString());
        holder.text_sentweight.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        holder.text_sentweight.setTextColor(Color.parseColor("#000000"));
    }

    public void addItem(Fabric_Receipt_Data_Object dataObj, int index) {
        mDataset.add(index, dataObj);
        notifyItemInserted(index);
    }

    public void deleteItem(int index) {
        mDataset.remove(index);
        notifyItemRemoved(index);
    }

    public int getDcid(int index) {
        return mDataset.get(index).getDcid();
    }
    public String getDcno(String index) {
        return mDataset.get(Integer.parseInt(index)).getDcno();
    }
    public String getDcdate(String index) {
        return mDataset.get(Integer.parseInt(index)).getDcdate();
    }

    public String getJoborderno(String index) {
        return mDataset.get(Integer.parseInt(index)).getJoborderno();
    }

    public String getStyleno(String index) {
        return mDataset.get(Integer.parseInt(index)).getStyleno();
    }

    public String getShipcode(String index) {
        return mDataset.get(Integer.parseInt(index)).getShipcode();
    }

    public String getVendorname(String index) {
        return mDataset.get(Integer.parseInt(index)).getFromvendor();
    }

    public String getPartname(String index) {
        return mDataset.get(Integer.parseInt(index)).getPartname();
    }

    public String getSentrolls(String index) {
        return mDataset.get(Integer.parseInt(index)).getSentrolls();
    }

    public String getStatus(String index) {
        return mDataset.get(Integer.parseInt(index)).getStatus();
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }
}
