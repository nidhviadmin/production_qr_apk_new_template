package com.bipinexports.productionqr.Activity;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bipinexports.productionqr.R;
import com.bipinexports.productionqr.SlideImage_Data_object;
import java.util.List;

public class SlideImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<SlideImage_Data_object> itemList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(SlideImage_Data_object item, int position);
    }

    public SlideImageAdapter(Context context, List<SlideImage_Data_object> itemList, OnItemClickListener listener) {
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

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_slide_image, parent, false);
        return new ImageViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        SlideImage_Data_object item = itemList.get(position);

        if (holder instanceof TitleViewHolder) {
            TitleViewHolder titleHolder = (TitleViewHolder) holder;
            titleHolder.txtTitle.setText(item.getTitle());
        }
        else if (holder instanceof ImageViewHolder) {
            ImageViewHolder imgHolder = (ImageViewHolder) holder;

            Log.e("Bipin", "Slide image path => " + item.getImgpath());

            Glide.with(context)
                    .load(item.getImgpath())
                    .into(imgHolder.imageView);

            imgHolder.itemView.setOnClickListener(v -> listener.onItemClick(item, position));
        }
    }


    static class TitleViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle;
        TitleViewHolder(View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtTitle);
        }
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.slideImage);
        }
    }
}
