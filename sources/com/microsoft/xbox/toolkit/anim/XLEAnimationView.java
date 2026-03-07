package com.microsoft.xbox.toolkit.anim;

import android.graphics.Paint;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.Interpolator;
import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.XLEAssert;

public class XLEAnimationView extends XLEAnimation {
    private Animation anim;
    /* access modifiers changed from: private */
    public View animtarget;

    public XLEAnimationView(Animation anim2) {
        this.anim = anim2;
        this.anim.setFillAfter(true);
        this.anim.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
                XLEAnimationView.this.onViewAnimationStart();
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                XLEAnimationView.this.onViewAnimationEnd();
                if (XLEAnimationView.this.endRunnable != null) {
                    XLEAnimationView.this.endRunnable.run();
                }
            }
        });
    }

    public void start() {
        this.animtarget.startAnimation(this.anim);
    }

    public void clear() {
        this.anim.setAnimationListener((Animation.AnimationListener) null);
        this.animtarget.clearAnimation();
    }

    public void setTargetView(View targetView) {
        XLEAssert.assertNotNull(targetView);
        this.animtarget = targetView;
        if (this.anim instanceof AnimationSet) {
            for (Animation animation : ((AnimationSet) this.anim).getAnimations()) {
                if (animation instanceof HeightAnimation) {
                    ((HeightAnimation) animation).setTargetView(targetView);
                }
            }
        }
    }

    public void setInterpolator(Interpolator interpolator) {
        this.anim.setInterpolator(interpolator);
    }

    public void setFillAfter(boolean fillAfter) {
        this.anim.setFillAfter(fillAfter);
    }

    /* access modifiers changed from: private */
    public void onViewAnimationStart() {
        this.animtarget.setLayerType(2, (Paint) null);
    }

    /* access modifiers changed from: private */
    public void onViewAnimationEnd() {
        ThreadManager.UIThreadPost(new Runnable() {
            public void run() {
                XLEAnimationView.this.animtarget.setLayerType(0, (Paint) null);
            }
        });
    }
}
