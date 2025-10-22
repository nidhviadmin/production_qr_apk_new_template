package com.bipinexports.productionqr.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bipinexports.productionqr.APIClient;
import com.bipinexports.productionqr.In_Employee_Bundle_Data_Object;
import com.bipinexports.productionqr.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class In_Employee_Bundle_Scan_View_Adapter extends RecyclerView.Adapter<In_Employee_Bundle_Scan_View_Adapter.DataObjectHolder> {
    private ArrayList<In_Employee_Bundle_Data_Object> mDataset;
    private static MyClickListener myClickListener;
    private Context mContext;

    public static class DataObjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView empcode, empname, designation, process, operation, txt_index, mapp_cnt, txt_mobile ;
        ImageView Emp_Imge;

        public DataObjectHolder(View itemView) {
            super(itemView);
            empcode = (TextView) itemView.findViewById(R.id.txt_Code);
            empname = (TextView) itemView.findViewById(R.id.txt_Name);
            designation = (TextView) itemView.findViewById(R.id.txt_Designation);
            process = (TextView) itemView.findViewById(R.id.txt_Process);
            operation = (TextView) itemView.findViewById(R.id.txt_Operation);
            Emp_Imge = (ImageView) itemView.findViewById(R.id.Emp_Imge);
            txt_index = (TextView) itemView.findViewById(R.id.txt_index);
            mapp_cnt = itemView.findViewById(R.id.mapp_cnt);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.txt_mobile) {
                // Get the phone number
                String phoneNumber = txt_mobile.getText().toString();

                // Initiate a phone call
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + phoneNumber));
                v.getContext().startActivity(intent);
            } else {
                // Handle other click events if any
                myClickListener.onItemClick(getAdapterPosition(), v);
            }
        }
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public In_Employee_Bundle_Scan_View_Adapter(Context context, ArrayList<In_Employee_Bundle_Data_Object> myDataset) {
        mDataset = myDataset;
        mContext = context;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.in_emp_bundle_mapp_card_view, parent, false);
        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {

        holder.txt_index.setText(String.valueOf(position + 1) +". ");
        holder.txt_index.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);

        holder.empcode.setText(mDataset.get(position).getEmpcode().toString());
        holder.empcode.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        holder.empcode.setTextColor(Color.parseColor("#000000"));

        holder.empname.setText(mDataset.get(position).getEmpname().toString());
        holder.empname.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        holder.empname.setTextColor(Color.parseColor("#000000"));

        holder.designation.setText(mDataset.get(position).getDesignation().toString());
        holder.designation.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        holder.designation.setTextColor(Color.parseColor("#000000"));

        holder.process.setText(mDataset.get(position).getProcess().toString());
        holder.process.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        holder.process.setTextColor(Color.parseColor("#000000"));

        holder.operation.setText(mDataset.get(position).getOperation().toString());
        holder.operation.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        holder.operation.setTextColor(Color.parseColor("#000000"));

        String imagePath = mDataset.get(position).getImgpath();
        String[] parts = imagePath.split("/");
        String directory = parts[0];
        if(directory.equals("assets"))
        {
            Picasso.with(mContext).load(APIClient.HRMS_IMG_URL + "/" +mDataset.get(position).getImgpath()).into(holder.Emp_Imge);
        }
        else
        {
            Picasso.with(mContext).load(mDataset.get(position).getImgpath()).into(holder.Emp_Imge);
        }
        String mapped_cnt = mDataset.get(position).getmapped_cnt().toString();

        if(Integer.parseInt(mapped_cnt) > 0)
        {
            holder.mapp_cnt.setText(mapped_cnt);
            holder.mapp_cnt.getBackground().setColorFilter(Color.parseColor("#008000"), PorterDuff.Mode.SRC_IN);
        }
       else
        {
            holder.mapp_cnt.setText(mapped_cnt);
            holder.mapp_cnt.getBackground().setColorFilter(Color.parseColor("#ff0000"), PorterDuff.Mode.SRC_IN);
        }
    }

    public void addItem(In_Employee_Bundle_Data_Object dataObj, int index) {
        mDataset.add(index, dataObj);
        notifyItemInserted(index);
    }

    public void deleteItem(int index) {
        mDataset.remove(index);
        notifyItemRemoved(index);
    }

    public String getEmpcode(String index) {
        return mDataset.get(Integer.parseInt(index)).getEmpcode();
    }

    public String getEmpname(String index) {
        return mDataset.get(Integer.parseInt(index)).getEmpname();
    }

    public String getImgpath(String index) {
        return mDataset.get(Integer.parseInt(index)).getImgpath();
    }

    public String getHrmsrecid(String index) {
        return mDataset.get(Integer.parseInt(index)).getHrmsrecid();
    }
    public String getOperationid(String index) {
        return mDataset.get(Integer.parseInt(index)).getOperationid();
    }
    public String getHrmssecid(String index) {
        return mDataset.get(Integer.parseInt(index)).getHrmssecid();
    }

    public String getDesignation(String index) {
        return mDataset.get(Integer.parseInt(index)).getDesignation();
    }

    public String getOperation(String index) {
        return mDataset.get(Integer.parseInt(index)).getOperation();
    }

    public String getProcess(String index) {
        return mDataset.get(Integer.parseInt(index)).getProcess();
    }

    public String getSection_name(String index) {
        return mDataset.get(Integer.parseInt(index)).getSection_name();
    }

    public String getQrcontractorid(String index) {
        return mDataset.get(Integer.parseInt(index)).getQrcontractorid();
    }


    public String getmapped_cnt(String index) {
        return mDataset.get(Integer.parseInt(index)).getmapped_cnt();
    }


    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }
}
