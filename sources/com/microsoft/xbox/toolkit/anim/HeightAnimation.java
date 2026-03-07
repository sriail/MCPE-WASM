package com.microsoft.xbox.toolkit.anim;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class HeightAnimation extends Animation {
    private int fromValue;
    private int toValue;
    private View view;

    public HeightAnimation(int from, int to) {
        this.fromValue = from;
        this.toValue = to;
    }

    public void setTargetView(View targetView) {
        this.view = targetView;
        this.fromValue = targetView.getHeight();
    }

    /* access modifiers changed from: protected */
    public void applyTransformation(float interpolatedTime, Transformation t) {
        int newDelta = (int) (((float) (this.toValue - this.fromValue)) * interpolatedTime);
        this.view.getLayoutParams().height = this.fromValue + newDelta;
        this.view.requestLayout();
    }

    public boolean willChangeBounds() {
        return true;
    }
}
