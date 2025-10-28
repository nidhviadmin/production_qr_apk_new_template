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
import android.widget.LinearLayout;
import android.widget.TextView;


import com.bipinexports.productionqrnew.Datewise_Piece_Scan_List_Data_Object;
import com.bipinexports.productionqrnew.R;

import java.util.ArrayList;

public class Datewise_Piece_scan_List_View_Adapter extends RecyclerView.Adapter<Datewise_Piece_scan_List_View_Adapter.DataObjectHolder> {
    private ArrayList<Datewise_Piece_Scan_List_Data_Object> mDataset;
    private static MyClickListener myClickListener;

    public static class DataObjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView Part_Name, text_Part, text_Size, text_Size_Qty, text_Bundle_Cnt, text_Bundle_qty, text_Scanned_Pcs_Cnt, text_Diff, Bundle_qty1, Scanned_Pcs_Cnt, diff_qty;

        LinearLayout layout_Size,layout_Size_qty,layout_Bundle_Cnt ;

        public DataObjectHolder(View itemView) {
            super(itemView);
            Part_Name = (TextView) itemView.findViewById(R.id.Part_Name);
            text_Part = (TextView) itemView.findViewById(R.id.text_Part);
            text_Size = (TextView) itemView.findViewById(R.id.text_Size);
            text_Size_Qty = (TextView) itemView.findViewById(R.id.text_Size_Qty);
            text_Bundle_Cnt = (TextView) itemView.findViewById(R.id.text_Bundle_Cnt);
            text_Bundle_qty = (TextView) itemView.findViewById(R.id.text_Bundle_qty);
            text_Scanned_Pcs_Cnt  = (TextView) itemView.findViewById(R.id.text_Scanned_Pcs_Cnt);
            text_Diff = (TextView) itemView.findViewById(R.id.text_Diff);

            Bundle_qty1 = (TextView) itemView.findViewById(R.id.Bundle_qty1);
            Scanned_Pcs_Cnt = (TextView) itemView.findViewById(R.id.Scanned_Pcs_Cnt);
            diff_qty = (TextView) itemView.findViewById(R.id.diff_qty);

            layout_Size = (LinearLayout ) itemView.findViewById(R.id.layout_Size);
            layout_Size_qty = (LinearLayout ) itemView.findViewById(R.id.layout_Size_qty);
            layout_Bundle_Cnt = (LinearLayout ) itemView.findViewById(R.id.layout_Bundle_Cnt);

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

    public Datewise_Piece_scan_List_View_Adapter(ArrayList<Datewise_Piece_Scan_List_Data_Object> myDataset) {
        mDataset = myDataset;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.piece_scan_card_view_row, parent, false);
        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {

        if(mDataset.get(position).getPartname().equals("Grand Total"))
        {
            holder.Part_Name.setText("  Grand Total");
            holder.Part_Name.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            holder.Part_Name.setTextColor(Color.parseColor("#AB47BC"));
            holder.Part_Name.setTypeface(null, Typeface.BOLD);

            holder.text_Bundle_qty.setText(mDataset.get(position).getScanned_pcno().toString());
            holder.text_Bundle_qty.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            holder.text_Bundle_qty.setTextColor(Color.parseColor("#AB47BC"));
            holder.text_Bundle_qty.setTypeface(null, Typeface.BOLD);

            holder.text_Scanned_Pcs_Cnt.setText(mDataset.get(position).getBundle_qty().toString());
            holder.text_Scanned_Pcs_Cnt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            holder.text_Scanned_Pcs_Cnt.setTextColor(Color.parseColor("#AB47BC"));
            holder.text_Scanned_Pcs_Cnt.setTypeface(null, Typeface.BOLD);

            holder.text_Diff.setText(mDataset.get(position).getDiff_qty().toString());
            holder.text_Diff.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            holder.text_Diff.setTextColor(Color.parseColor("#AB47BC"));
            holder.text_Diff.setTypeface(null, Typeface.BOLD);

            holder.layout_Size.setVisibility(View.GONE);
            holder.layout_Size_qty.setVisibility(View.GONE);
            holder.layout_Bundle_Cnt.setVisibility(View.GONE);

            holder.Bundle_qty1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
            holder.Scanned_Pcs_Cnt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
            holder.diff_qty.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        }
        else {
            Spannable word = new SpannableString(new String(String.valueOf(mDataset.get(position).getIndex())));
            word.setSpan(new ForegroundColorSpan(Color.RED), 0, word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.Part_Name.setText(word);
            Spannable wordTwo = new SpannableString(". Part Name");
            wordTwo.setSpan(new ForegroundColorSpan(Color.BLACK), 0, wordTwo.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.Part_Name.append(wordTwo);

            holder.text_Part.setText(mDataset.get(position).getPartname().toString());
            holder.text_Part.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            holder.text_Part.setTextColor(Color.parseColor("#000000"));

            holder.text_Size.setText(mDataset.get(position).getSizename().toString());
            holder.text_Size.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            holder.text_Size.setTextColor(Color.parseColor("#000000"));

            holder.text_Size_Qty.setText(mDataset.get(position).getSizewiseqty().toString());
            holder.text_Size_Qty.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            holder.text_Size_Qty.setTextColor(Color.parseColor("#000000"));

            holder.text_Bundle_Cnt.setText(mDataset.get(position).getBundlecnt().toString());
            holder.text_Bundle_Cnt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            holder.text_Bundle_Cnt.setTextColor(Color.parseColor("#000000"));
            //holder.Expected_Salary.setTypeface(null, Typeface.BOLD);

            holder.text_Bundle_qty.setText(mDataset.get(position).getBundle_qty().toString());
            holder.text_Bundle_qty.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            holder.text_Bundle_qty.setTextColor(Color.parseColor("#000000"));
            // holder.Name.setTypeface(null, Typeface.BOLD);

            holder.text_Scanned_Pcs_Cnt.setText(mDataset.get(position).getScanned_pcno().toString());
            holder.text_Scanned_Pcs_Cnt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            holder.text_Scanned_Pcs_Cnt.setTextColor(Color.parseColor("#000000"));

            holder.text_Diff.setText(mDataset.get(position).getDiff_qty().toString());
            holder.text_Diff.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            holder.text_Diff.setTextColor(Color.parseColor("#000000"));
        }
    }

    public void addItem(Datewise_Piece_Scan_List_Data_Object dataObj, int index) {
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

    public String getBundlecnt(String index) {
        return mDataset.get(Integer.parseInt(index)).getBundlecnt();
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
