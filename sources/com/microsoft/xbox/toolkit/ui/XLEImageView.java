package com.microsoft.xbox.toolkit.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

public class XLEImageView extends ImageView {
    public static final int IMAGE_ERROR = 2;
    public static final int IMAGE_FINAL = 0;
    public static final int IMAGE_LOADING = 1;
    public String TEST_loadingOrLoadedImageUrl;
    protected boolean isFinal;
    protected boolean shouldAnimate;

    public XLEImageView(Context context) {
        this(context, (AttributeSet) null, 0);
    }

    public void setShouldAnimate(boolean value) {
        this.shouldAnimate = value;
    }

    public boolean getShouldAnimate() {
        return this.shouldAnimate && !this.isFinal;
    }

    public XLEImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XLEImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.shouldAnimate = true;
        this.isFinal = false;
        setSoundEffectsEnabled(false);
    }

    public void setImageBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            super.setImageBitmap(bitmap);
        }
    }

    public void setImageSource(Bitmap bitmap, int source) {
        if (bitmap != null) {
            super.setImageBitmap(bitmap);
        }
    }

    public void setFinal(boolean value) {
        this.isFinal = value;
    }

    public void setOnClickListener(View.OnClickListener listener) {
        super.setOnClickListener(TouchUtil.createOnClickListener(listener));
    }
}
