package com.bipinexports.productionqr.Activity;

import android.content.Context;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.util.Consumer;
import androidx.recyclerview.widget.RecyclerView;

import com.bipinexports.productionqr.MainImage_Data_Object;
import com.bipinexports.productionqr.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MainImage_Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<MainImage_Data_Object> itemList;
    private OnItemClickListener listener;
    private boolean isHoneywellScannerAvailable = false;

    public interface OnItemClickListener {
        void onItemClick(MainImage_Data_Object item, int position);
    }

    public MainImage_Adapter(Context context, List<MainImage_Data_Object> itemList, OnItemClickListener listener) {
        this.context = context;
        this.itemList = itemList;
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        return itemList.get(position).getType();
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == MainImage_Data_Object.TYPE_LOGO) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_logo, parent, false);
            return new LogoViewHolder(view);
        }
        else if (viewType == MainImage_Data_Object.TYPE_TITLE) {
            View view = LayoutInflater.from(context).inflate(R.layout.main_image_title, parent, false);
            return new TitleViewHolder(view);
        }
        else {
            View view = LayoutInflater.from(context).inflate(R.layout.main_image_item, parent, false);
            return new ImageViewHolder(view);
        }
    }

    public String getTitle_name(String index) {
        return itemList.get(Integer.parseInt(index)).getTitle_name();
    }

    public String getimgpath(String index) {
        return itemList.get(Integer.parseInt(index)).getimgpath();
    }

    public String getimage_name(String index) {
        return itemList.get(Integer.parseInt(index)).getimage_name();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MainImage_Data_Object item = itemList.get(position);
//        if (holder instanceof TitleViewHolder)
//        {
        if (holder instanceof TitleViewHolder)
        {
            TitleViewHolder titleHolder = (TitleViewHolder) holder;
            titleHolder.titleText.setText(item.getTitle_name());
            titleHolder.titleText.setPaintFlags(titleHolder.titleText.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        }
//        else if (holder instanceof ImageViewHolder)
//        {
//            Log.e("Bipin"," getimgpath " +item.getimgpath());
//            ((ImageViewHolder) holder).txtTitle.setText(item.getimage_name());
//            Picasso.get()
//                    .load(item.getimgpath())
//                    .into(((ImageViewHolder) holder).imageView);
//
//            holder.itemView.setOnClickListener(v -> listener.onItemClick(item, position));
//            if (item.getCount() != null && !item.getCount().equals("0"))
//            {
//                ((ImageViewHolder) holder).txt_count.setText(item.getCount());
//                ((ImageViewHolder) holder).txt_count.setVisibility(View.VISIBLE);
        else if (holder instanceof ImageViewHolder) {
            ImageViewHolder imageHolder = (ImageViewHolder) holder;
            imageHolder.txtTitle.setText(item.getimage_name());
            Picasso.get().load(item.getimgpath()).into(imageHolder.imageView);
            imageHolder.itemView.setOnClickListener(v -> listener.onItemClick(item, position));

            if (item.getCount() != null && !item.getCount().equals("0")) {
                imageHolder.txt_count.setText(item.getCount());
                imageHolder.txt_count.setVisibility(View.VISIBLE);
            } else {
                imageHolder.txt_count.setVisibility(View.GONE);
            }
//            else {
//                ((ImageViewHolder) holder).txt_count.setVisibility(View.GONE);
            }
        }

    public void checkHoneywellScanner(Context context, Consumer<Boolean> callback) {
        boolean available = false;

        try {
            String manufacturer = android.os.Build.MANUFACTURER.toLowerCase();
            String brand = android.os.Build.BRAND.toLowerCase();
            String model = android.os.Build.MODEL.toLowerCase();

            Log.e("ScannerCheck", "brand : " + brand);
            Log.e("ScannerCheck", "model : " + model);

            // ✅ Detect Honeywell devices
            if (manufacturer.contains("honeywell") || brand.contains("honeywell") || model.contains("honeywell")) {
                available = true;
            } else {
                available = false;
            }
        } catch (Exception e) {
            available = false;
            Log.e("ScannerCheck", "Device check failed: " + e.getMessage());
        }
        Log.e("ScannerCheck", "Honeywell Device Detected? : " + available);

        if (callback != null) {
            callback.accept(available);
        }
    }

    public void setHoneywellAvailable(boolean available) {
        this.isHoneywellScannerAvailable = available;
    }

    static class TitleViewHolder extends RecyclerView.ViewHolder {
        TextView titleText;

        TitleViewHolder(View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.titleText);
        }
    }
    static class LogoViewHolder extends RecyclerView.ViewHolder {
        ImageView logo;
        LogoViewHolder(View view) {
            super(view);
            logo = view.findViewById(R.id.logoImage);
        }
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView txtTitle, txt_count;

        ImageViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            txtTitle = itemView.findViewById(R.id.txt_title);
            txt_count = itemView.findViewById(R.id.txt_count);
        }
    }
}

