package com.bipinexports.productionqr.Activity;

public class NotificationModel {
    public String title;
    public String message;
    public String imageUrl;
    public boolean isRead;

    public NotificationModel(String title, String message, String imageUrl) {
        this(title, message, imageUrl, false);
    }

    public NotificationModel(String title, String message, String imageUrl, boolean isRead) {
        this.title = title;
        this.message = message;
        this.imageUrl = imageUrl;
        this.isRead = isRead;
    }
}
