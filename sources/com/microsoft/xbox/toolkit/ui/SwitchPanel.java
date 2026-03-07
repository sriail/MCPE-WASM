package com.microsoft.xbox.toolkit.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.microsoft.xbox.toolkit.BackgroundThreadWaitor;
import com.microsoft.xbox.toolkit.XLERValueHelper;

public class SwitchPanel extends LinearLayout {
    private static final int LAYOUT_BLOCK_TIMEOUT_MS = 150;
    private AnimatorListenerAdapter AnimateInListener = new AnimatorListenerAdapter() {
        public void onAnimationCancel(Animator animation) {
            SwitchPanel.this.onAnimateInEnd();
        }

        public void onAnimationEnd(Animator animation) {
            SwitchPanel.this.onAnimateInEnd();
        }

        public void onAnimationStart(Animator animation) {
            SwitchPanel.this.onAnimateInStart();
        }
    };
    private AnimatorListenerAdapter AnimateOutListener = new AnimatorListenerAdapter() {
        public void onAnimationCancel(Animator animation) {
            SwitchPanel.this.onAnimateOutEnd();
        }

        public void onAnimationEnd(Animator animation) {
            SwitchPanel.this.onAnimateOutEnd();
        }

        public void onAnimationStart(Animator animation) {
            SwitchPanel.this.onAnimateOutStart();
        }
    };
    private final int INVALID_STATE_ID = -1;
    private final int VALID_CONTENT_STATE = 0;
    private boolean active = false;
    private boolean blocking = false;
    private View newView = null;
    private View oldView = null;
    private int selectedState;
    private boolean shouldAnimate = true;

    public interface SwitchPanelChild {
        int getState();
    }

    public SwitchPanel(Context context) {
        super(context);
        throw new UnsupportedOperationException();
    }

    public SwitchPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, XLERValueHelper.getStyleableRValueArray("SwitchPanel"));
        this.selectedState = a.getInteger(XLERValueHelper.getStyleableRValue("SwitchPanel_selectedState"), -1);
        a.recycle();
        if (this.selectedState < 0) {
            throw new IllegalArgumentException("You must specify the selectedState attribute in the xml, and the value must be positive.");
        }
        setLayoutParams(new RelativeLayout.LayoutParams(-1, -1));
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        updateVisibility(-1, this.selectedState);
    }

    public void setActive(boolean active2) {
        this.active = active2;
    }

    public void setShouldAnimate(boolean value) {
        this.shouldAnimate = value;
    }

    public void setState(int newState) {
        if (newState < 0) {
            throw new IllegalArgumentException("New state must be a positive value.");
        } else if (this.selectedState != newState) {
            int oldState = this.selectedState;
            this.selectedState = newState;
            updateVisibility(oldState, newState);
        }
    }

    public int getState() {
        return this.selectedState;
    }

    private void updateVisibility(int oldState, int newState) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View v = getChildAt(i);
            if (!(v instanceof SwitchPanelChild)) {
                throw new UnsupportedOperationException("All children of SwitchPanel must implement the SwitchPanelChild interface. All other types are not supported and should be removed.");
            }
            int switchPanelState = ((SwitchPanelChild) v).getState();
            if (switchPanelState == oldState) {
                this.oldView = v;
            } else if (switchPanelState == newState) {
                this.newView = v;
            } else {
                v.setVisibility(8);
            }
        }
        if (!this.shouldAnimate || newState != 0 || this.newView == null) {
            if (this.oldView != null) {
                this.oldView.setVisibility(8);
            }
            if (this.newView != null) {
                this.newView.setAlpha(1.0f);
                this.newView.setVisibility(0);
            }
            requestLayout();
            return;
        }
        this.newView.setAlpha(0.0f);
        this.newView.setVisibility(0);
        requestLayout();
        if (this.oldView != null) {
            this.oldView.animate().alpha(0.0f).setDuration(150).setListener(this.AnimateOutListener);
        }
        this.newView.animate().alpha(1.0f).setDuration(150).setListener(this.AnimateInListener);
    }

    public void setBlocking(boolean value) {
        if (this.blocking != value) {
            this.blocking = value;
            if (this.blocking) {
                BackgroundThreadWaitor.getInstance().setBlocking(BackgroundThreadWaitor.WaitType.ListLayout, LAYOUT_BLOCK_TIMEOUT_MS);
            } else {
                BackgroundThreadWaitor.getInstance().clearBlocking(BackgroundThreadWaitor.WaitType.ListLayout);
            }
        }
    }

    /* access modifiers changed from: private */
    public void onAnimateInStart() {
        if (this.newView != null) {
            this.newView.setLayerType(2, (Paint) null);
            setBlocking(true);
        }
    }

    /* access modifiers changed from: private */
    public void onAnimateInEnd() {
        setBlocking(false);
        if (this.newView != null) {
            this.newView.setLayerType(0, (Paint) null);
        }
    }

    /* access modifiers changed from: private */
    public void onAnimateOutStart() {
        if (this.oldView != null) {
            this.oldView.setLayerType(2, (Paint) null);
            setBlocking(true);
        }
    }

    /* access modifiers changed from: private */
    public void onAnimateOutEnd() {
        if (this.oldView != null) {
            this.oldView.setVisibility(8);
            this.oldView.setLayerType(0, (Paint) null);
        }
    }
}
