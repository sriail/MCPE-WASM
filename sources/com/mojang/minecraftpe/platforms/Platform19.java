package com.mojang.minecraftpe.platforms;

import android.annotation.TargetApi;
import android.os.Handler;
import android.view.View;

@TargetApi(19)
public class Platform19 extends Platform9 {
    /* access modifiers changed from: private */
    public Runnable decorViewSettings;
    /* access modifiers changed from: private */
    public View decoreView;
    /* access modifiers changed from: private */
    public Handler eventHandler;

    public Platform19(boolean initEventHandler) {
        if (initEventHandler) {
            this.eventHandler = new Handler();
        }
    }

    public void onVolumePressed() {
    }

    public void onAppStart(View view) {
        if (this.eventHandler != null) {
            this.decoreView = view;
            this.decoreView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                public void onSystemUiVisibilityChange(int visibility) {
                    Platform19.this.eventHandler.postDelayed(Platform19.this.decorViewSettings, 500);
                }
            });
            this.decorViewSettings = new Runnable() {
                public void run() {
                    Platform19.this.decoreView.setSystemUiVisibility(5894);
                }
            };
            this.eventHandler.post(this.decorViewSettings);
        }
    }

    public void onViewFocusChanged(boolean hasFocus) {
        if (this.eventHandler != null && hasFocus) {
            this.eventHandler.postDelayed(this.decorViewSettings, 500);
        }
    }
}
