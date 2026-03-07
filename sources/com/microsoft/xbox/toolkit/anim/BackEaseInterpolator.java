package com.microsoft.xbox.toolkit.anim;

import com.microsoft.cll.android.EventEnums;

public class BackEaseInterpolator extends XLEInterpolator {
    private float amplitude;

    public BackEaseInterpolator(float amplitude2, EasingMode easingMode) {
        super(easingMode);
        this.amplitude = amplitude2;
    }

    /* access modifiers changed from: protected */
    public float getInterpolationCore(float normalizedTime) {
        float normalizedTime2 = (float) Math.max((double) normalizedTime, EventEnums.SampleRate_0_percent);
        return (float) (((double) ((normalizedTime2 * normalizedTime2) * normalizedTime2)) - (((double) (this.amplitude * normalizedTime2)) * Math.sin(((double) normalizedTime2) * 3.141592653589793d)));
    }
}
