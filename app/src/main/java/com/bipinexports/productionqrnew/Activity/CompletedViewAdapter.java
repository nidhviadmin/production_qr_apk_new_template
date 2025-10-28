package com.bipinexports.productionqrnew.Activity;

import android.graphics.Color;
import android.graphics.Typeface;
import androidx.recyclerview.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bipinexports.productionqrnew.CompletedDataObject;
import com.bipinexports.productionqrnew.R;

import java.util.ArrayList;

public class CompletedViewAdapter extends RecyclerView.Adapter<CompletedViewAdapter.DataObjectHolder> {
    private ArrayList<CompletedDataObject> mDataset;
    private static MyClickListener myClickListener;

    public static class DataObjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView weekyear;
        TextView BundleCnt, BundleQty, BundlePrice;

        public DataObjectHolder(View itemView) {
            super(itemView);
            weekyear = (TextView) itemView.findViewById(R.id.txtWeekYear);
            BundleCnt = (TextView) itemView.findViewById(R.id.textBundlecnt);
            BundleQty = (TextView) itemView.findViewById(R.id.textBundleQty);
            BundlePrice = (TextView) itemView.findViewById(R.id.textBundlePrice);
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

    public CompletedViewAdapter(ArrayList<CompletedDataObject> myDataset) {
        mDataset = myDataset;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_row, parent, false);
        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {

        if(mDataset.get(position).getWeekyear().equals("Grand Total"))
        {
            holder.weekyear.setText("Grand Total");
            holder.weekyear.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            holder.weekyear.setTextColor(Color.parseColor("#AB47BC"));
            holder.weekyear.setTypeface(null, Typeface.BOLD);

            holder.BundleCnt.setText(mDataset.get(position).getCompletedbundlecnt().toString());
            holder.BundleCnt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            holder.BundleCnt.setTextColor(Color.parseColor("#AB47BC"));
            holder.BundleCnt.setTypeface(null, Typeface.BOLD);

            holder.BundleQty.setText(mDataset.get(position).getCompletedbundleqty().toString());
            holder.BundleQty.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            holder.BundleQty.setTextColor(Color.parseColor("#AB47BC"));
            holder.BundleQty.setTypeface(null, Typeface.BOLD);

            holder.BundlePrice.setText(mDataset.get(position).getCompletedprice().toString());
            holder.BundlePrice.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            holder.BundlePrice.setTextColor(Color.parseColor("#AB47BC"));
            holder.BundlePrice.setTypeface(null, Typeface.BOLD);
        }
        else
        {
            holder.weekyear.setText(mDataset.get(position).getWeekyear());
            holder.weekyear.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            holder.weekyear.setTextColor(Color.parseColor("#178ab4"));
            holder.weekyear.setTypeface(null, Typeface.BOLD);

            holder.BundleCnt.setText(mDataset.get(position).getCompletedbundlecnt().toString());
            holder.BundleCnt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            holder.BundleCnt.setTextColor(Color.parseColor("#000000"));
            holder.BundleCnt.setTypeface(null, Typeface.BOLD);

            holder.BundleQty.setText(mDataset.get(position).getCompletedbundleqty().toString());
            holder.BundleQty.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            holder.BundleQty.setTextColor(Color.parseColor("#000000"));
            holder.BundleQty.setTypeface(null, Typeface.BOLD);

            holder.BundlePrice.setText(mDataset.get(position).getCompletedprice().toString());
            holder.BundlePrice.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            holder.BundlePrice.setTextColor(Color.parseColor("#000000"));
            holder.BundlePrice.setTypeface(null, Typeface.BOLD);
        }
    }

    public void addItem(CompletedDataObject dataObj, int index) {
        mDataset.add(index, dataObj);
        notifyItemInserted(index);
    }

    public void deleteItem(int index) {
        mDataset.remove(index);
        notifyItemRemoved(index);
    }

    public int getweek(int index) {
        return mDataset.get(index).getWeek();
    }
    public int getYear(int index) {
        return mDataset.get(index).getYear();
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }
}
