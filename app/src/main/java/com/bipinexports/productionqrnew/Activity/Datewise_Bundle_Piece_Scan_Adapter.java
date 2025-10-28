package com.bipinexports.productionqrnew.Activity;

import android.graphics.Color;
import android.graphics.Typeface;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bipinexports.productionqrnew.Datewise_Bundle_Piece_Data_Object;
import com.bipinexports.productionqrnew.R;

import java.util.ArrayList;

public class Datewise_Bundle_Piece_Scan_Adapter extends RecyclerView.Adapter<Datewise_Bundle_Piece_Scan_Adapter.DataObjectHolder> {
    private ArrayList<Datewise_Bundle_Piece_Data_Object> mDataset;
    private static MyClickListener myClickListener;

    public static class DataObjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView Bundle_No, text_Bundle_No, text_Bundle_Qty, text_Scanned_Pcs_Qty, text_Diff_Qty, Bundle_Qty1, Scanned_Pcs_Qty, Diff_Qty;

        public DataObjectHolder(View itemView) {
            super(itemView);
            Bundle_No = (TextView) itemView.findViewById(R.id.Bundle_No);
            text_Bundle_No = (TextView) itemView.findViewById(R.id.text_Bundle_No);
            text_Bundle_Qty = (TextView) itemView.findViewById(R.id.text_Bundle_Qty);
            text_Scanned_Pcs_Qty = (TextView) itemView.findViewById(R.id.text_Scanned_Pcs_Qty);
            text_Diff_Qty = (TextView) itemView.findViewById(R.id.text_Diff_Qty);

            Bundle_Qty1 = (TextView) itemView.findViewById(R.id.Bundle_Qty1);
            Scanned_Pcs_Qty = (TextView) itemView.findViewById(R.id.Scanned_Pcs_Qty);
            Diff_Qty = (TextView) itemView.findViewById(R.id.Diff_Qty);

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

    public Datewise_Bundle_Piece_Scan_Adapter(ArrayList<Datewise_Bundle_Piece_Data_Object> myDataset) {
        mDataset = myDataset;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bundle_piece_card_view_row, parent, false);
        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {

        if(mDataset.get(position).getPartname().equals("Grand Total"))
        {
            holder.Bundle_No.setText("  Grand Total");
            holder.Bundle_No.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            holder.Bundle_No.setTextColor(Color.parseColor("#AB47BC"));
            holder.Bundle_No.setTypeface(null, Typeface.BOLD);

            holder.text_Bundle_Qty.setText(mDataset.get(position).getBundle_qty().toString());
            holder.text_Bundle_Qty.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            holder.text_Bundle_Qty.setTextColor(Color.parseColor("#AB47BC"));
            holder.text_Bundle_Qty.setTypeface(null, Typeface.BOLD);

            holder.text_Scanned_Pcs_Qty.setText(mDataset.get(position).getScanned_pcno().toString());
            holder.text_Scanned_Pcs_Qty.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            holder.text_Scanned_Pcs_Qty.setTextColor(Color.parseColor("#AB47BC"));
            holder.text_Scanned_Pcs_Qty.setTypeface(null, Typeface.BOLD);

            holder.text_Diff_Qty.setText(mDataset.get(position).getDiff_qty().toString());
            holder.text_Diff_Qty.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            holder.text_Diff_Qty.setTextColor(Color.parseColor("#AB47BC"));
            holder.text_Diff_Qty.setTypeface(null, Typeface.BOLD);


            holder.Bundle_Qty1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
            holder.Scanned_Pcs_Qty.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
            holder.Diff_Qty.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        }
        else {
            Spannable word = new SpannableString(new String(String.valueOf(mDataset.get(position).getIndex())));
            word.setSpan(new ForegroundColorSpan(Color.RED), 0, word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.Bundle_No.setText(word);
            Spannable wordTwo = new SpannableString(".Bundle No");
            wordTwo.setSpan(new ForegroundColorSpan(Color.BLACK), 0, wordTwo.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.Bundle_No.append(wordTwo);

            holder.text_Bundle_No.setText(mDataset.get(position).getBundleno().toString());
            holder.text_Bundle_No.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            holder.text_Bundle_No.setTextColor(Color.parseColor("#000000"));

            holder.text_Bundle_Qty.setText(mDataset.get(position).getBundle_qty().toString());
            holder.text_Bundle_Qty.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            holder.text_Bundle_Qty.setTextColor(Color.parseColor("#000000"));

            holder.text_Scanned_Pcs_Qty.setText(mDataset.get(position).getScanned_pcno().toString());
            holder.text_Scanned_Pcs_Qty.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            holder.text_Scanned_Pcs_Qty.setTextColor(Color.parseColor("#000000"));
            // holder.Name.setTypeface(null, Typeface.BOLD);

            holder.text_Diff_Qty.setText(mDataset.get(position).getDiff_qty().toString());
            holder.text_Diff_Qty.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            holder.text_Diff_Qty.setTextColor(Color.parseColor("#000000"));
        }
    }

    public void addItem(Datewise_Bundle_Piece_Data_Object dataObj, int index) {
        mDataset.add(index, dataObj);
        notifyItemInserted(index);
    }

    public void deleteItem(int index) {
        mDataset.remove(index);
        notifyItemRemoved(index);
    }

    public int getOrderid(int index) {
        return mDataset.get(index).getOrderid();
    }
    public int getSectionid(int index) {
        return mDataset.get(index).getSectionid();
    }
    public int getSizeid(int index) {
        return mDataset.get(index).getSizeid();
    }
    public int getPart_id(int index) {
        return mDataset.get(index).getPart_id();
    }

    public String getBundleno(String index) {
        return mDataset.get(Integer.parseInt(index)).getBundleno();
    }
    public String getBundle_qty(String index) {
        return mDataset.get(Integer.parseInt(index)).getBundle_qty();
    }


    public String getShipcode(String index) {
        return mDataset.get(Integer.parseInt(index)).getShipcode();
    }

    public String getPartname(String index) {
        return mDataset.get(Integer.parseInt(index)).getPartname();
    }
    public String getSizename(String index) {
        return mDataset.get(Integer.parseInt(index)).getSizename();
    }
    public String getSectionname(String index) {
        return mDataset.get(Integer.parseInt(index)).getSectionname();
    }

    public String getJob_ref(String index) {
        return mDataset.get(Integer.parseInt(index)).getJob_ref();
    }
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }
}
