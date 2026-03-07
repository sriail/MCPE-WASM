package com.microsoft.xbox.toolkit.ui;

import android.graphics.Typeface;
import com.microsoft.xbox.toolkit.system.SystemUtil;

public class XLETextArg {
    private final Params params;
    private final String text;

    public XLETextArg(String text2, Params params2) {
        this.text = text2;
        this.params = params2;
    }

    public XLETextArg(Params params2) {
        this((String) null, params2);
    }

    public String getText() {
        return this.text;
    }

    public boolean hasText() {
        return this.text != null;
    }

    public Params getParams() {
        return this.params;
    }

    public static class Params {
        private final boolean adjustForImageSize;
        private final int color;
        private final int eraseColor;
        private final Float textAspectRatio;
        private final float textSize;
        private final Typeface typeface;

        public Params() {
            this((float) SystemUtil.SPtoPixels(8.0f), -1, Typeface.DEFAULT, 0, false, (Float) null);
        }

        public Params(float textSize2, int color2, Typeface typeface2, int eraseColor2, boolean adjustForImageSize2, Float textAspectRatio2) {
            this.textSize = textSize2;
            this.color = color2;
            this.typeface = typeface2;
            this.eraseColor = eraseColor2;
            this.adjustForImageSize = adjustForImageSize2;
            this.textAspectRatio = textAspectRatio2;
        }

        public float getTextSize() {
            return this.textSize;
        }

        public int getColor() {
            return this.color;
        }

        public Typeface getTypeface() {
            return this.typeface;
        }

        public boolean hasEraseColor() {
            return this.eraseColor != 0;
        }

        public int getEraseColor() {
            return this.eraseColor;
        }

        public boolean isAdjustForImageSize() {
            return this.adjustForImageSize;
        }

        public Float getTextAspectRatio() {
            return this.textAspectRatio;
        }

        public boolean hasTextAspectRatio() {
            return this.textAspectRatio != null;
        }
    }
}
