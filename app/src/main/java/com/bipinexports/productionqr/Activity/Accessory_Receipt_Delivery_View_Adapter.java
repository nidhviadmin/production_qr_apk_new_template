package com.bipinexports.productionqr.Activity;

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

import com.bipinexports.productionqr.Accessory_Receipt_Delivery_Data_Object;
import com.bipinexports.productionqr.R;

import java.util.ArrayList;

public class Accessory_Receipt_Delivery_View_Adapter extends RecyclerView.Adapter<Accessory_Receipt_Delivery_View_Adapter.DataObjectHolder> {
    private ArrayList<Accessory_Receipt_Delivery_Data_Object> mDataset;
    private static MyClickListener myClickListener;

    public static class DataObjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView PO_No, PO_Date, DC_No, DC_Date, Delivery_Type, Quantity, PO_No_Text ;

        public DataObjectHolder(View itemView) {
            super(itemView);
            PO_No = (TextView) itemView.findViewById(R.id.text_PO_No);
            PO_Date = (TextView) itemView.findViewById(R.id.text_PO_Date);
            DC_No = (TextView) itemView.findViewById(R.id.text_DC_No);
            DC_Date = (TextView) itemView.findViewById(R.id.text_DC_Date);
            Delivery_Type = (TextView) itemView.findViewById(R.id.text_Delivery_Type);
            Quantity = (TextView) itemView.findViewById(R.id.text_Quantity);
            PO_No_Text = (TextView) itemView.findViewById(R.id.PO_No);
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

    public Accessory_Receipt_Delivery_View_Adapter(ArrayList<Accessory_Receipt_Delivery_Data_Object> myDataset) {
        mDataset = myDataset;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.accessory_receipt_delivery_card_view, parent, false);
        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {


        Spannable word = new SpannableString(new String(String.valueOf(mDataset.get(position).getIndex())));
        word.setSpan(new ForegroundColorSpan(Color.BLUE), 0, word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.PO_No_Text.setText(word);
        Spannable wordTwo = new SpannableString(". PO No");
        wordTwo.setSpan(new ForegroundColorSpan(Color.BLACK), 0, wordTwo.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.PO_No_Text.append(wordTwo);


        holder.PO_No.setText(mDataset.get(position).getPono().toString());
        holder.PO_No.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        holder.PO_No.setTextColor(Color.parseColor("#000000"));
       // holder.PO_No.setTypeface(null, Typeface.BOLD);

        holder.PO_Date.setText(mDataset.get(position).getPodate().toString());
        holder.PO_Date.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        holder.PO_Date.setTextColor(Color.parseColor("#000000"));
        //holder.PO_Date.setTypeface(null, Typeface.BOLD);

        holder.DC_No.setText(mDataset.get(position).getDcno().toString());
        holder.DC_No.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        holder.DC_No.setTextColor(Color.parseColor("#000000"));
        //holder.DC_No.setTypeface(null, Typeface.BOLD);

        holder.DC_Date.setText(mDataset.get(position).getDcdate().toString());
        holder.DC_Date.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        holder.DC_Date.setTextColor(Color.parseColor("#000000"));
        //holder.DC_Date.setTypeface(null, Typeface.BOLD);

        String type = "";
        if(mDataset.get(position).getType().equals("S"))
        {
            type = "Size wise";
        }
        else  if(mDataset.get(position).getType().equals("O"))
        {
            type = "All Sizes";
        }
        holder.Delivery_Type.setText(type);
        holder.Delivery_Type.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        holder.Delivery_Type.setTextColor(Color.parseColor("#000000"));
        //holder.Delivery_Type.setTypeface(null, Typeface.BOLD);

        holder.Quantity.setText(mDataset.get(position).getQuantity().toString());
        holder.Quantity.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        holder.Quantity.setTextColor(Color.parseColor("#000000"));
        //holder.Quantity.setTypeface(null, Typeface.BOLD);
    }

    public void addItem(Accessory_Receipt_Delivery_Data_Object dataObj, int index) {
        mDataset.add(index, dataObj);
        notifyItemInserted(index);
    }

    public void deleteItem(int index) {
        mDataset.remove(index);
        notifyItemRemoved(index);
    }

    public int getPoid(int index) {
        return mDataset.get(index).getPoid();
    }
    public int getDcid(int index) {
        return mDataset.get(index).getDcid();
    }

    public String getPono(String index) {
        return mDataset.get(Integer.parseInt(index)).getPono();
    }

    public String getPodate(String index) {
        return mDataset.get(Integer.parseInt(index)).getPodate();
    }

    public String getVendorname(String index) {
        return mDataset.get(Integer.parseInt(index)).getVendorname();
    }

    public String getDcno(String index) {
        return mDataset.get(Integer.parseInt(index)).getDcno();
    }
    public String getDcdate(String index) {
        return mDataset.get(Integer.parseInt(index)).getDcdate();
    }
    public String getType(String index) {
        return mDataset.get(Integer.parseInt(index)).getType();
    }


    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }
}
