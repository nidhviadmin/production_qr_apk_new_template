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

import com.bipinexports.productionqrnew.Accessory_Receipt_Jobwise_Data_Object;
import com.bipinexports.productionqrnew.R;

import java.util.ArrayList;

public class Accessory_Receipt_Jobwise_View_Adapter extends RecyclerView.Adapter<Accessory_Receipt_Jobwise_View_Adapter.DataObjectHolder> {
    private ArrayList<Accessory_Receipt_Jobwise_Data_Object> mDataset;
    private static MyClickListener myClickListener;

    public static class DataObjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView joborderno, text_joborderno, Quantity, text_stylename, text_Del_count ;

        public DataObjectHolder(View itemView) {
            super(itemView);

            text_joborderno = (TextView) itemView.findViewById(R.id.text_joborderno);
            text_stylename = (TextView) itemView.findViewById(R.id.text_stylename);
            Quantity = (TextView) itemView.findViewById(R.id.text_Quantity);
            joborderno = (TextView) itemView.findViewById(R.id.joborderno);
            text_Del_count =  (TextView) itemView.findViewById(R.id.text_Del_count);
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

    public Accessory_Receipt_Jobwise_View_Adapter(ArrayList<Accessory_Receipt_Jobwise_Data_Object> myDataset) {
        mDataset = myDataset;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.accessory_receipt_card_view_jobwise, parent, false);
        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {

        Spannable word = new SpannableString(new String(String.valueOf(mDataset.get(position).getIndex())));
        word.setSpan(new ForegroundColorSpan(Color.RED), 0, word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.joborderno.setText(word);
        Spannable wordTwo = new SpannableString(". Job Order No");
        wordTwo.setSpan(new ForegroundColorSpan(Color.BLACK), 0, wordTwo.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.joborderno.append(wordTwo);

        holder.text_joborderno.setText(mDataset.get(position).getJoborderno().toString());
        holder.text_joborderno.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        holder.text_joborderno.setTextColor(Color.parseColor("#000000"));
//        holder.text_joborderno.setTypeface(null, Typeface.BOLD);

        holder.text_stylename.setText(mDataset.get(position).getStylename().toString());
        holder.text_stylename.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        holder.text_stylename.setTextColor(Color.parseColor("#000000"));
//        holder.text_joborderno.setTypeface(null, Typeface.BOLD);

        holder.Quantity.setText(mDataset.get(position).getQuantity().toString());
        holder.Quantity.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        holder.Quantity.setTextColor(Color.parseColor("#000000"));

        holder.text_Del_count.setText(mDataset.get(position).getCount().toString());
        holder.text_Del_count.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        holder.text_Del_count.setTextColor(Color.parseColor("#000000"));
        //holder.Quantity.setTypeface(null, Typeface.BOLD);
    }

    public void addItem(Accessory_Receipt_Jobwise_Data_Object dataObj, int index) {
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

    public String getJoborderno(String index) {
        return mDataset.get(Integer.parseInt(index)).getJoborderno();
    }

    public String getStylename(String index) {
        return mDataset.get(Integer.parseInt(index)).getStylename();
    }

    public String getQuantity(String index) {
        return mDataset.get(Integer.parseInt(index)).getQuantity();
    }

    public String getCount(String index) {
        return mDataset.get(Integer.parseInt(index)).getCount();
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }
}
