package com.microsoft.xbox.toolkit.ui;

import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import com.microsoft.xbox.toolkit.ui.XLEBitmap;

public class ButtonStateHandler {
    protected boolean disabled = false;
    private XLEBitmap.XLEBitmapDrawable disabledImage = null;
    private int disabledImageHandle = -1;
    private XLEBitmap.XLEBitmapDrawable enabledImage = null;
    private int enabledImageHandle = -1;
    protected boolean pressed = false;
    private XLEBitmap.XLEBitmapDrawable pressedImage = null;
    private int pressedImageHandle = -1;
    private ButtonStateHandlerRunnable pressedStateRunnable = null;

    public interface ButtonStateHandlerRunnable {
        void onPressStateChanged(boolean z);
    }

    public void setDisabled(boolean disabled2) {
        this.disabled = disabled2;
    }

    public void setDisabledImageHandle(int imageHandle) {
        this.disabledImageHandle = imageHandle;
    }

    public void setEnabledImageHandle(int imageHandle) {
        this.enabledImageHandle = imageHandle;
    }

    public void setPressedImageHandle(int imageHandle) {
        this.pressedImageHandle = imageHandle;
    }

    public boolean getDisabled() {
        return this.disabled;
    }

    public void setEnabled(boolean enabled) {
        this.disabled = !enabled;
    }

    public boolean onTouch(MotionEvent event) {
        boolean oldpressed = this.pressed;
        if (event.getAction() == 0) {
            this.pressed = true;
        } else if (event.getAction() == 1) {
            this.pressed = false;
        } else if (event.getAction() == 3) {
            this.pressed = false;
        }
        if (!(this.pressedStateRunnable == null || oldpressed == this.pressed)) {
            this.pressedStateRunnable.onPressStateChanged(this.pressed);
        }
        return false;
    }

    public boolean onSizeChanged(int width, int height) {
        boolean loadedNewImage = false;
        if (this.disabledImage == null && this.disabledImageHandle != -1) {
            loadedNewImage = true;
            this.disabledImage = TextureManager.Instance().loadScaledResourceDrawable(this.disabledImageHandle);
        }
        if (this.enabledImage == null && this.enabledImageHandle != -1) {
            loadedNewImage = true;
            this.enabledImage = TextureManager.Instance().loadScaledResourceDrawable(this.enabledImageHandle);
        }
        if (this.pressedImage != null || this.pressedImageHandle == -1) {
            return loadedNewImage;
        }
        this.pressedImage = TextureManager.Instance().loadScaledResourceDrawable(this.pressedImageHandle);
        return true;
    }

    public Drawable getImageDrawable() {
        if (!this.pressed || this.pressedImageHandle == -1) {
            if (!this.disabled || this.disabledImageHandle == -1) {
                if (this.enabledImageHandle == -1 || this.enabledImage == null) {
                    return null;
                }
                return this.enabledImage.getDrawable();
            } else if (this.disabledImage != null) {
                return this.disabledImage.getDrawable();
            } else {
                return null;
            }
        } else if (this.pressedImage == null) {
            return null;
        } else {
            return this.pressedImage.getDrawable();
        }
    }

    public void setPressedStateRunnable(ButtonStateHandlerRunnable runnable) {
        this.pressedStateRunnable = runnable;
    }
}
