package com.bipinexports.productionqrnew;
public class MainImage_Data_Object {
    public static final int TYPE_LOGO = 0;
    public static final int TYPE_TITLE = 1;
    public static final int TYPE_IMAGE = 2;

    public int type;
    private String title_name;
    private String imgpath;
    private String image_name;
    private String activityname;
    private String count = "0";

    public MainImage_Data_Object(int type, String title_name, String imgpath, String image_name, String activityname) {
        this.type = type;
        this.title_name = title_name;
        this.imgpath = imgpath;
        this.image_name = image_name;
        this.activityname = activityname;
    }

    public int getType() {
        return type;
    }

    public String getTitle_name() {
        return title_name;
    }

    public String getimgpath() {
        return imgpath;
    }

    public String getimage_name() {
        return image_name;
    }

    public String getActivityname() {
        return activityname;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }
}


