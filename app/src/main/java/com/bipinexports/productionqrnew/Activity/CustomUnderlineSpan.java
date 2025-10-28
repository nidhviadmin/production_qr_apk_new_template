package com.bipinexports.productionqrnew.Activity;
import android.text.TextPaint;
import android.text.style.UnderlineSpan;

public class CustomUnderlineSpan extends UnderlineSpan {
    private final int underlineThickness;

    public CustomUnderlineSpan(int underlineThickness) {
        this.underlineThickness = underlineThickness;
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        ds.setUnderlineText(true);
        ds.setStrokeWidth(underlineThickness);
    }
}
