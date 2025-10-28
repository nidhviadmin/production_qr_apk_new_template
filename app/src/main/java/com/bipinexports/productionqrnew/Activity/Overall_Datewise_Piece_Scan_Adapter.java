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

import com.bipinexports.productionqrnew.Overall_Datewise_Piece_Scan_Data_Object;
import com.bipinexports.productionqrnew.R;

import java.util.ArrayList;

public class Overall_Datewise_Piece_Scan_Adapter extends RecyclerView.Adapter<Overall_Datewise_Piece_Scan_Adapter.DataObjectHolder> {
    private ArrayList<Overall_Datewise_Piece_Scan_Data_Object> mDataset;
    private static MyClickListener myClickListener;

    public static class DataObjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView Job_Ref, text_Job_Ref,text_Shipcode, text_Section, text_Bundle_Qty, text_Scanned_Pcs, text_Diff, Bundle_Qty, Scanned_Pcs, diff_qty;
        LinearLayout layout_Shipcode,layout_Section;

        public DataObjectHolder(View itemView) {
            super(itemView);
            Job_Ref = (TextView) itemView.findViewById(R.id.Job_Ref);
            text_Job_Ref = (TextView) itemView.findViewById(R.id.text_Job_Ref);
            text_Shipcode = (TextView) itemView.findViewById(R.id.text_Shipcode);
            text_Section = (TextView) itemView.findViewById(R.id.text_Section);
            text_Bundle_Qty = (TextView) itemView.findViewById(R.id.text_Bundle_Qty);
            text_Scanned_Pcs = (TextView) itemView.findViewById(R.id.text_Scanned_Pcs);
            text_Diff = (TextView) itemView.findViewById(R.id.text_Diff);

            Bundle_Qty = (TextView) itemView.findViewById(R.id.Bundle_Qty);
            Scanned_Pcs = (TextView) itemView.findViewById(R.id.Scanned_Pcs);
            diff_qty = (TextView) itemView.findViewById(R.id.diff_qty);

            layout_Shipcode = (LinearLayout ) itemView.findViewById(R.id.layout_Shipcode);
            layout_Section = (LinearLayout ) itemView.findViewById(R.id.layout_Section);

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

    public Overall_Datewise_Piece_Scan_Adapter(ArrayList<Overall_Datewise_Piece_Scan_Data_Object> myDataset) {
        mDataset = myDataset;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.overall_piece_scan_card_view, parent, false);
        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {

        if(mDataset.get(position).getSectionname().equals("Grand Total"))
        {
            holder.Job_Ref.setText("  Grand Total");
            holder.Job_Ref.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            holder.Job_Ref.setTextColor(Color.parseColor("#AB47BC"));
            holder.Job_Ref.setTypeface(null, Typeface.BOLD);

            holder.text_Bundle_Qty.setText(mDataset.get(position).getScanned_pcno().toString());
            holder.text_Bundle_Qty.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            holder.text_Bundle_Qty.setTextColor(Color.parseColor("#AB47BC"));
            holder.text_Bundle_Qty.setTypeface(null, Typeface.BOLD);

            holder.text_Scanned_Pcs.setText(mDataset.get(position).getBundle_qty().toString());
            holder.text_Scanned_Pcs.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            holder.text_Scanned_Pcs.setTextColor(Color.parseColor("#AB47BC"));
            holder.text_Scanned_Pcs.setTypeface(null, Typeface.BOLD);

            holder.text_Diff.setText(mDataset.get(position).getDiff_qty().toString());
            holder.text_Diff.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            holder.text_Diff.setTextColor(Color.parseColor("#AB47BC"));
            holder.text_Diff.setTypeface(null, Typeface.BOLD);

            holder.layout_Shipcode.setVisibility(View.GONE);
            holder.layout_Section.setVisibility(View.GONE);

            holder.Bundle_Qty.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
            holder.Scanned_Pcs.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
            holder.diff_qty.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        }
        else
        {
            Spannable word = new SpannableString(new String(String.valueOf(mDataset.get(position).getIndex())));
            word.setSpan(new ForegroundColorSpan(Color.RED), 0, word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.Job_Ref.setText(word);
            Spannable wordTwo = new SpannableString(".Job Ref");
            wordTwo.setSpan(new ForegroundColorSpan(Color.BLACK), 0, wordTwo.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.Job_Ref.append(wordTwo);

            holder.text_Job_Ref.setText(mDataset.get(position).getJob_ref().toString());
            holder.text_Job_Ref.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            holder.text_Job_Ref.setTextColor(Color.parseColor("#000000"));

            holder.text_Shipcode.setText(mDataset.get(position).getShipcode().toString());
            holder.text_Shipcode.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            holder.text_Shipcode.setTextColor(Color.parseColor("#000000"));

            holder.text_Section.setText(mDataset.get(position).getSectionname().toString());
            holder.text_Section.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            holder.text_Section.setTextColor(Color.parseColor("#000000"));
            // holder.Name.setTypeface(null, Typeface.BOLD);

            holder.text_Bundle_Qty.setText(mDataset.get(position).getBundle_qty().toString());
            holder.text_Bundle_Qty.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            holder.text_Bundle_Qty.setTextColor(Color.parseColor("#000000"));

            holder.text_Scanned_Pcs.setText(mDataset.get(position).getScanned_pcno().toString());
            holder.text_Scanned_Pcs.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            holder.text_Scanned_Pcs.setTextColor(Color.parseColor("#000000"));

            holder.text_Diff.setText(mDataset.get(position).getDiff_qty().toString());
            holder.text_Diff.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            holder.text_Diff.setTextColor(Color.parseColor("#000000"));
            //holder.Expected_Salary.setTypeface(null, Typeface.BOLD);
        }
    }

    public void addItem(Overall_Datewise_Piece_Scan_Data_Object dataObj, int index) {
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

    public String getShipcode(String index) {
        return mDataset.get(Integer.parseInt(index)).getShipcode();
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
