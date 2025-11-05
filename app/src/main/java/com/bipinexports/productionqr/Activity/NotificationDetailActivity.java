package com.bipinexports.productionqr.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bipinexports.productionqr.R;
import com.squareup.picasso.Picasso;

public class NotificationDetailActivity extends AppCompatActivity {

    TextView txtTitle, txtMessage;
    ImageView imgNotification, backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_detail);

        txtTitle = findViewById(R.id.txtTitle);
        txtMessage = findViewById(R.id.txtMessage);
        imgNotification = findViewById(R.id.imgNotification);
        backBtn = findViewById(R.id.imgd);

        String title = getIntent().getStringExtra("title");
        String message = getIntent().getStringExtra("message");
        String imageUrl = getIntent().getStringExtra("imageUrl");

        txtTitle.setText(title != null ? title : "Notification");
        txtMessage.setText(message != null ? message : "");

        if (imageUrl != null && !imageUrl.isEmpty()) {
            Picasso.get().load(imageUrl).into(imgNotification);
        }

        backBtn.setOnClickListener(v -> {
            Intent intent = new Intent(NotificationDetailActivity.this, NotificationViewActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }
}
