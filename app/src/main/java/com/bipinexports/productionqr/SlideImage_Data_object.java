package com.bipinexports.productionqr;

public class SlideImage_Data_object {

    public static final int TYPE_IMAGE = 0;
    public static final int TYPE_TITLE = 1;

    private int type;
    private String imgpath;
    private String title;

    public SlideImage_Data_object(int type, String imgpath, String title) {
        this.type = type;
        this.imgpath = imgpath;
        this.title = title;
    }

    public int getType() {
        return type;
    }

    public String getImgpath() {
        return imgpath;
    }

    public String getTitle() {
        return title;
    }
}
