package com.bipinexports.productionqr.Activity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bipinexports.productionqr.R;
import com.bipinexports.productionqr.service.NotificationHelper;
import com.squareup.picasso.Picasso;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private final Context context;
    private final List<NotificationModel> list;

    public NotificationAdapter(Context context, List<NotificationModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NotificationModel item = list.get(position);
        holder.txtTitle.setText(item.title);

        // Change card background based on read/unread
        int bgColor = item.isRead
                ? context.getResources().getColor(android.R.color.white)
                : context.getResources().getColor(android.R.color.darker_gray);
        holder.itemView.setBackgroundColor(bgColor);

        if (item.imageUrl != null && !item.imageUrl.isEmpty()) {
            holder.imgNotification.setVisibility(View.VISIBLE);
            Picasso.get().load(item.imageUrl).into(holder.imgNotification);
        } else {
            holder.imgNotification.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            item.isRead = true;
            NotificationHelper.markAsRead(context, position);
            notifyItemChanged(position);

            Intent intent = new Intent(context, NotificationDetailActivity.class);
            intent.putExtra("title", item.title);
            intent.putExtra("message", item.message);
            intent.putExtra("imageUrl", item.imageUrl);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle;
        ImageView imgNotification;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            imgNotification = itemView.findViewById(R.id.imgNotification);
        }
    }
}
