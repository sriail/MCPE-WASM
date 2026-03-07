package com.microsoft.xbox.toolkit;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.view.View;
import com.microsoft.xbox.toolkit.IXLEManagedDialog;
import com.microsoft.xbox.toolkit.anim.MAAS;
import com.microsoft.xbox.toolkit.anim.XLEAnimation;
import com.microsoft.xbox.toolkit.anim.XLEAnimationPackage;
import com.microsoft.xbox.toolkit.system.SystemUtil;
import com.microsoft.xbox.toolkit.ui.NavigationManager;
import com.microsoft.xbox.xle.anim.XLEMAASAnimationPackageNavigationManager;

public class XLEManagedDialog extends Dialog implements IXLEManagedDialog {
    protected static final String BODY_ANIMATION_NAME = "Dialog";
    protected String bodyAnimationName = BODY_ANIMATION_NAME;
    final Runnable callAfterAnimationIn = new Runnable() {
        public void run() {
            XLEManagedDialog.this.OnAnimationInEnd();
        }
    };
    final Runnable callAfterAnimationOut = new Runnable() {
        public void run() {
            XLEManagedDialog.this.OnAnimationOutEnd();
        }
    };
    protected View dialogBody = null;
    private IXLEManagedDialog.DialogType dialogType = IXLEManagedDialog.DialogType.NORMAL;
    protected Runnable onAnimateOutCompletedRunable = null;

    public void setBodyAnimationName(String string) {
        this.bodyAnimationName = string;
    }

    public String getBodyAnimationName() {
        return this.bodyAnimationName;
    }

    protected XLEManagedDialog(Context context, boolean cancelable, DialogInterface.OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public XLEManagedDialog(Context context, int theme) {
        super(context, theme);
    }

    public XLEManagedDialog(Context context) {
        super(context);
    }

    public void setDialogType(IXLEManagedDialog.DialogType type) {
        this.dialogType = type;
    }

    public IXLEManagedDialog.DialogType getDialogType() {
        return this.dialogType;
    }

    public Dialog getDialog() {
        return this;
    }

    public void makeFullScreen() {
        getWindow().setLayout(-1, -2);
    }

    public void safeDismiss() {
        DialogManager.getInstance().dismissManagedDialog(this);
    }

    public void quickDismiss() {
        super.dismiss();
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        if (!hasFocus) {
            safeDismiss();
        }
    }

    public XLEAnimationPackage getAnimateOut() {
        XLEAnimation screenBodyAnimation = getBodyAnimation(MAAS.MAASAnimationType.ANIMATE_OUT, true);
        if (screenBodyAnimation == null) {
            return null;
        }
        XLEAnimationPackage animationPackage = new XLEAnimationPackage();
        animationPackage.add(screenBodyAnimation);
        return animationPackage;
    }

    public XLEAnimationPackage getAnimateIn() {
        XLEAnimation screenBodyAnimation = getBodyAnimation(MAAS.MAASAnimationType.ANIMATE_IN, false);
        if (screenBodyAnimation == null) {
            return null;
        }
        XLEAnimationPackage animationPackage = new XLEAnimationPackage();
        animationPackage.add(screenBodyAnimation);
        return animationPackage;
    }

    public void OnAnimationInEnd() {
        NavigationManager.getInstance().setAnimationBlocking(false);
    }

    public void OnAnimationOutEnd() {
        NavigationManager.getInstance().setAnimationBlocking(false);
        super.dismiss();
        if (this.onAnimateOutCompletedRunable != null) {
            try {
                this.onAnimateOutCompletedRunable.run();
            } catch (Exception e) {
            }
        }
    }

    public void setAnimateOutRunnable(Runnable postAnimateOutRunnable) {
        this.onAnimateOutCompletedRunable = postAnimateOutRunnable;
    }

    public View getDialogBody() {
        return this.dialogBody;
    }

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        XLEAnimationPackage anim = getAnimateIn();
        if (getDialogBody() != null && anim != null) {
            NavigationManager.getInstance().setAnimationBlocking(true);
            anim.setOnAnimationEndRunnable(this.callAfterAnimationIn);
            anim.startAnimation();
        }
    }

    public void dismiss() {
        if (!isShowing()) {
            super.dismiss();
            return;
        }
        XLEAnimationPackage anim = getAnimateOut();
        if (getDialogBody() == null || anim == null) {
            if (this.onAnimateOutCompletedRunable != null) {
                this.onAnimateOutCompletedRunable.run();
            }
            super.dismiss();
            return;
        }
        NavigationManager.getInstance().setAnimationBlocking(true);
        anim.setOnAnimationEndRunnable(this.callAfterAnimationOut);
        anim.startAnimation();
    }

    /* access modifiers changed from: protected */
    public XLEAnimation getBodyAnimation(MAAS.MAASAnimationType animationType, boolean goingBack) {
        if (getDialogBody() != null) {
            return ((XLEMAASAnimationPackageNavigationManager) MAAS.getInstance().getAnimation(this.bodyAnimationName)).compile(animationType, goingBack, getDialogBody());
        }
        return null;
    }

    /* access modifiers changed from: protected */
    public void forceKindleRespectDimOptions() {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                XLEManagedDialog.this.getWindow().addFlags(2);
            }
        }, 100);
    }

    protected static boolean isKindle() {
        return SystemUtil.isKindle();
    }
}
