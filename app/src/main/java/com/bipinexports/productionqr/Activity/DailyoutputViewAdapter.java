package com.bipinexports.productionqr.Activity;

import android.graphics.Color;
import android.graphics.Typeface;
import androidx.recyclerview.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bipinexports.productionqr.DailyOutputDataObject;
import com.bipinexports.productionqr.R;

import java.util.ArrayList;

public class DailyoutputViewAdapter extends RecyclerView.Adapter<DailyoutputViewAdapter.DataObjectHolder> {
    private ArrayList<DailyOutputDataObject> mDataset;
    private static MyClickListener myClickListener;

    public static class DataObjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView jobno,  BundleCnt, BundleQty, BundlePrice;
        int indexpos = 0;

        public DataObjectHolder(View itemView) {
            super(itemView);
            jobno = (TextView) itemView.findViewById(R.id.txtJobNo);
            BundleCnt = (TextView) itemView.findViewById(R.id.textBundlecnt);
            BundleQty = (TextView) itemView.findViewById(R.id.textBundleQty);
            BundlePrice = (TextView) itemView.findViewById(R.id.textTotalPrice);
            itemView.setOnClickListener(this);
            indexpos =0;
        }

        @Override
        public void onClick(View v) {
//            myClickListener.onItemClick(getAdapterPosition(), v);
        }
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public DailyoutputViewAdapter(ArrayList<DailyOutputDataObject> myDataset) {
        mDataset = myDataset;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.daily_output_card_view_row, parent, false);
        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {

        if(mDataset.get(position).getDay().equals("Grand Total"))
        {
            holder.jobno.setText("Grand Total");
            holder.jobno.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            holder.jobno.setTextColor(Color.parseColor("#AB47BC"));
            holder.jobno.setTypeface(null, Typeface.BOLD);

            holder.BundleCnt.setText(mDataset.get(position).getDatewisebundlecnt().toString());
            holder.BundleCnt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            holder.BundleCnt.setTextColor(Color.parseColor("#AB47BC"));
            holder.BundleCnt.setTypeface(null, Typeface.BOLD);

            holder.BundleQty.setText(mDataset.get(position).getDatewisebundleqty().toString());
            holder.BundleQty.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            holder.BundleQty.setTextColor(Color.parseColor("#AB47BC"));
            holder.BundleQty.setTypeface(null, Typeface.BOLD);

            holder.BundlePrice.setText(mDataset.get(position).getDatewiseprice().toString());
            holder.BundlePrice.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            holder.BundlePrice.setTextColor(Color.parseColor("#AB47BC"));
            holder.BundlePrice.setTypeface(null, Typeface.BOLD);
        }
        else
        {
            holder.jobno.setText(mDataset.get(position).getDay());
            holder.jobno.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            holder.jobno.setTextColor(Color.parseColor("#000000"));
            holder.jobno.setTypeface(null, Typeface.BOLD);

            holder.BundleCnt.setText(mDataset.get(position).getDatewisebundlecnt().toString());
            holder.BundleCnt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            holder.BundleCnt.setTextColor(Color.parseColor("#000000"));
            holder.BundleCnt.setTypeface(null, Typeface.BOLD);

            holder.BundleQty.setText(mDataset.get(position).getDatewisebundleqty().toString());
            holder.BundleQty.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            holder.BundleQty.setTextColor(Color.parseColor("#000000"));
            holder.BundleQty.setTypeface(null, Typeface.BOLD);

            holder.BundlePrice.setText(mDataset.get(position).getDatewiseprice().toString());
            holder.BundlePrice.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            holder.BundlePrice.setTextColor(Color.parseColor("#000000"));
            holder.BundlePrice.setTypeface(null, Typeface.BOLD);
        }
    }

    public void addItem(DailyOutputDataObject dataObj, int index) {
        mDataset.add(index, dataObj);
        notifyItemInserted(index);
    }

    public void deleteItem(int index) {
        mDataset.remove(index);
        notifyItemRemoved(index);
    }


    public int getCount() {
        if (mDataset.size()<20) {
            return mDataset.size();
        }
        else return 20;

    }

    public String getDay(int index) {
        return mDataset.get(index).getDay();
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }
}
