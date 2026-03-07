package com.microsoft.xboxtcui;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.ui.ActivityParameters;
import com.microsoft.xbox.toolkit.ui.NavigationManager;
import com.microsoft.xbox.toolkit.ui.ScreenLayout;
import com.microsoft.xbox.toolkit.ui.XLEButton;

public class XboxTcuiWindowDialog extends Dialog {
    private DetachedCallback detachedCallback;
    private final XboxTcuiWindow xboxTcuiWindow;

    public interface DetachedCallback {
        void onDetachedFromWindow();
    }

    public XboxTcuiWindowDialog(Activity activity, Class<? extends ScreenLayout> screenClass, ActivityParameters params) {
        super(activity, R.style.TcuiDialog);
        this.xboxTcuiWindow = new XboxTcuiWindow(activity, screenClass, params);
    }

    public void setDetachedCallback(DetachedCallback callback) {
        this.detachedCallback = callback;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        getWindow().setLayout(-1, -1);
        getWindow().setFlags(1024, 1024);
        this.xboxTcuiWindow.onCreate(savedInstanceState);
        setContentView(this.xboxTcuiWindow);
        addCloseButton();
        NavigationManager.getInstance().setOnNavigatedListener(new NavigationManager.OnNavigatedListener() {
            public void onPageNavigated(ScreenLayout from, ScreenLayout to) {
                if (to == null) {
                    XboxTcuiWindowDialog.this.dismiss();
                }
            }

            public void onPageRestarted(ScreenLayout screen) {
            }
        });
    }

    private void addCloseButton() {
        FrameLayout frameLayout = new FrameLayout(getContext());
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-2, -2);
        layoutParams.gravity = 5;
        XLEButton closeButton = new XLEButton(getContext());
        closeButton.setPadding(60, 0, 0, 0);
        closeButton.setBackgroundResource(R.drawable.common_button_background);
        closeButton.setText(R.string.ic_Close);
        closeButton.setTextColor(-1);
        closeButton.setTextSize(2, 14.0f);
        closeButton.setTypeFace("fonts/SegXboxSymbol.ttf");
        closeButton.setContentDescription(getContext().getResources().getString(R.string.TextInput_Confirm));
        closeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    NavigationManager.getInstance().PopAllScreens();
                } catch (XLEException e) {
                }
            }
        });
        closeButton.setOnKeyListener(NavigationManager.getInstance());
        frameLayout.addView(closeButton);
        addContentView(frameLayout, layoutParams);
    }

    public void onStart() {
        this.xboxTcuiWindow.onStart();
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        this.xboxTcuiWindow.onStop();
    }

    public void onDetachedFromWindow() {
        if (this.detachedCallback != null) {
            this.detachedCallback.onDetachedFromWindow();
        }
        super.onDetachedFromWindow();
    }
}
