package com.microsoft.xbox.xle.app.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import com.microsoft.xbox.toolkit.BackgroundThreadWaitor;
import com.microsoft.xbox.toolkit.anim.MAAS;
import com.microsoft.xbox.toolkit.anim.MAASAnimation;
import com.microsoft.xbox.toolkit.anim.XLEAnimation;
import com.microsoft.xbox.toolkit.anim.XLEAnimationPackage;
import com.microsoft.xbox.toolkit.ui.ScreenLayout;
import com.microsoft.xbox.xle.anim.XLEMAASAnimationPackageNavigationManager;
import com.microsoft.xbox.xle.ui.XLERootView;
import com.microsoft.xbox.xle.viewmodel.ViewModelBase;
import com.microsoft.xboxtcui.XboxTcuiSdk;
import java.lang.ref.WeakReference;

public abstract class ActivityBase extends ScreenLayout {
    private boolean showRightPane;
    private boolean showUtilityBar;
    protected ViewModelBase viewModel;

    /* access modifiers changed from: protected */
    public abstract String getActivityName();

    public abstract void onCreateContentView();

    public ActivityBase() {
        this(0);
    }

    public ActivityBase(int orientation) {
        super(XboxTcuiSdk.getApplicationContext(), orientation);
        this.showUtilityBar = true;
        this.showRightPane = true;
    }

    public ActivityBase(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.showUtilityBar = true;
        this.showRightPane = true;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (this.viewModel != null) {
            this.viewModel.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void onStop() {
        if (getIsStarted()) {
            super.onStop();
            if (this.viewModel != null) {
                this.viewModel.onSetInactive();
            }
            if (this.viewModel != null) {
                this.viewModel.onStop();
            }
        }
    }

    public void forceRefresh() {
        if (this.viewModel != null) {
            this.viewModel.forceRefresh();
        }
    }

    public void onStart() {
        if (!getIsStarted()) {
            super.onStart();
            if (this.viewModel != null) {
                this.viewModel.onStart();
            }
            if (this.viewModel != null) {
                this.viewModel.load();
            }
        }
        if (!delayAppbarAnimation()) {
            adjustBottomMargin(computeBottomMargin());
        }
    }

    public void onAnimateInStarted() {
        if (this.viewModel != null) {
            this.viewModel.forceUpdateViewImmediately();
        }
    }

    public void onAnimateInCompleted() {
        if (this.viewModel != null) {
            final WeakReference<ViewModelBase> viewModelWeakPtr = new WeakReference<>(this.viewModel);
            BackgroundThreadWaitor.getInstance().postRunnableAfterReady(new Runnable() {
                public void run() {
                    ViewModelBase viewModelPtr = (ViewModelBase) viewModelWeakPtr.get();
                    if (viewModelPtr != null) {
                        viewModelPtr.forceUpdateViewImmediately();
                    }
                }
            });
        }
        if (this.viewModel != null) {
            this.viewModel.onAnimateInCompleted();
        }
    }

    public void forceUpdateViewImmediately() {
        if (this.viewModel != null) {
            this.viewModel.forceUpdateViewImmediately();
        }
    }

    /* access modifiers changed from: protected */
    public int computeBottomMargin() {
        return 0;
    }

    public XLEAnimationPackage getAnimateOut(boolean goingBack) {
        MAASAnimation animation;
        XLEAnimation screenBodyAnimation;
        View root = getChildAt(0);
        if (root == null || (animation = MAAS.getInstance().getAnimation("Screen")) == null || (screenBodyAnimation = ((XLEMAASAnimationPackageNavigationManager) animation).compile(MAAS.MAASAnimationType.ANIMATE_OUT, goingBack, root)) == null) {
            return null;
        }
        XLEAnimationPackage animationPackage = new XLEAnimationPackage();
        animationPackage.add(screenBodyAnimation);
        return animationPackage;
    }

    public XLEAnimationPackage getAnimateIn(boolean goingBack) {
        MAASAnimation animation;
        XLEAnimation screenBodyAnimation;
        View root = getChildAt(0);
        if (root == null || (animation = MAAS.getInstance().getAnimation("Screen")) == null || (screenBodyAnimation = ((XLEMAASAnimationPackageNavigationManager) animation).compile(MAAS.MAASAnimationType.ANIMATE_IN, goingBack, root)) == null) {
            return null;
        }
        XLEAnimationPackage animationPackage = new XLEAnimationPackage();
        animationPackage.add(screenBodyAnimation);
        return animationPackage;
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (this.viewModel != null) {
            this.viewModel.onRestoreInstanceState(savedInstanceState);
        }
    }

    public boolean onBackButtonPressed() {
        if (this.viewModel != null) {
            return this.viewModel.onBackButtonPressed();
        }
        return false;
    }

    public void onSetActive() {
        super.onSetActive();
        if (this.viewModel != null) {
            this.viewModel.onSetActive();
        }
    }

    public boolean getShouldShowAppbar() {
        return false;
    }

    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
        if (event.getEventType() != 8 || getXLERootView() == null || getXLERootView().getContentDescription() == null) {
            return super.dispatchPopulateAccessibilityEvent(event);
        }
        event.getText().clear();
        event.getText().add(getXLERootView().getContentDescription());
        return true;
    }

    public void onSetInactive() {
        super.onSetInactive();
        if (this.viewModel != null) {
            this.viewModel.onSetInactive();
        }
    }

    public void onPause() {
        super.onPause();
        if (this.viewModel != null) {
            this.viewModel.onPause();
        }
    }

    public void onApplicationPause() {
        super.onApplicationPause();
        if (this.viewModel != null) {
            this.viewModel.onApplicationPause();
        }
    }

    public void onApplicationResume() {
        super.onApplicationResume();
        if (this.viewModel != null) {
            this.viewModel.onApplicationResume();
        }
    }

    public void onResume() {
        super.onResume();
        if (this.viewModel != null) {
            this.viewModel.onResume();
        }
    }

    public void onDestroy() {
        if (this.viewModel != null) {
            this.viewModel.onDestroy();
        }
        this.viewModel = null;
        super.onDestroy();
    }

    public void onTombstone() {
        if (this.viewModel != null) {
            this.viewModel.onTombstone();
        }
        super.onTombstone();
    }

    public void onRehydrate() {
        super.onRehydrate();
        if (this.viewModel != null) {
            this.viewModel.onRehydrate();
        }
    }

    public void onRehydrateOverride() {
        onCreateContentView();
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (this.viewModel != null) {
            this.viewModel.onConfigurationChanged(newConfig);
        }
    }

    private XLERootView getXLERootView() {
        if (getChildAt(0) instanceof XLERootView) {
            return (XLERootView) getChildAt(0);
        }
        return null;
    }

    public void adjustBottomMargin(int bottomMargin) {
        if (getXLERootView() != null) {
            getXLERootView().setBottomMargin(bottomMargin);
        }
    }

    public void removeBottomMargin() {
        if (getXLERootView() != null) {
            getXLERootView().setBottomMargin(0);
        }
    }

    public void resetBottomMargin() {
        if (getXLERootView() != null) {
            adjustBottomMargin(computeBottomMargin());
        }
    }

    /* access modifiers changed from: protected */
    public boolean delayAppbarAnimation() {
        return false;
    }

    public void setHeaderName(String headerName) {
    }

    public String getName() {
        return getActivityName();
    }

    public String getRelativeId() {
        return null;
    }

    public void setScreenState(int state) {
        if (this.viewModel != null) {
            this.viewModel.setScreenState(state);
        }
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        clearDisappearingChildren();
    }
}
