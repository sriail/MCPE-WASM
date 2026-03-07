package com.microsoft.xbox.toolkit;

import android.app.Dialog;

public class DialogManager implements IProjectSpecificDialogManager {
    private static DialogManager instance = new DialogManager();
    private IProjectSpecificDialogManager manager;

    private DialogManager() {
    }

    public static DialogManager getInstance() {
        return instance;
    }

    public void setManager(IProjectSpecificDialogManager manager2) {
        this.manager = manager2;
    }

    public IProjectSpecificDialogManager getManager() {
        return this.manager;
    }

    public Dialog getVisibleDialog() {
        checkProvider();
        if (this.manager != null) {
            return this.manager.getVisibleDialog();
        }
        return null;
    }

    public boolean getIsBlocking() {
        checkProvider();
        if (this.manager != null) {
            return this.manager.getIsBlocking();
        }
        return false;
    }

    public void setEnabled(boolean value) {
        checkProvider();
        if (this.manager != null) {
            this.manager.setEnabled(value);
        }
    }

    public void showManagedDialog(IXLEManagedDialog dialog) {
        checkProvider();
        if (this.manager != null) {
            this.manager.showManagedDialog(dialog);
        }
    }

    public void dismissManagedDialog(IXLEManagedDialog dialog) {
        checkProvider();
        if (this.manager != null) {
            this.manager.dismissManagedDialog(dialog);
        }
    }

    public void onDialogStopped(IXLEManagedDialog dialog) {
        checkProvider();
        if (this.manager != null) {
            this.manager.onDialogStopped(dialog);
        }
    }

    public void showFatalAlertDialog(String title, String promptText, String okText, Runnable okHandler) {
        checkProvider();
        if (this.manager != null) {
            this.manager.showFatalAlertDialog(title, promptText, okText, okHandler);
        }
    }

    public void showNonFatalAlertDialog(String title, String promptText, String okText, Runnable okHandler) {
        checkProvider();
        if (this.manager != null) {
            this.manager.showNonFatalAlertDialog(title, promptText, okText, okHandler);
        }
    }

    public void showOkCancelDialog(String title, String promptText, String okText, Runnable okHandler, String cancelText, Runnable cancelHandler) {
        checkProvider();
        if (this.manager != null) {
            this.manager.showOkCancelDialog(title, promptText, okText, okHandler, cancelText, cancelHandler);
        }
    }

    public void showToast(int contentResId) {
        checkProvider();
        if (this.manager != null) {
            this.manager.showToast(contentResId);
        }
    }

    public void setBlocking(boolean visible, String statusText) {
        checkProvider();
        if (this.manager != null) {
            this.manager.setBlocking(visible, statusText);
        }
    }

    public void setCancelableBlocking(boolean visible, String statusText, Runnable cancelRunnable) {
        checkProvider();
        if (this.manager != null) {
            this.manager.setCancelableBlocking(visible, statusText, cancelRunnable);
        }
    }

    public void forceDismissAll() {
        checkProvider();
        if (this.manager != null) {
            this.manager.forceDismissAll();
        }
    }

    public void dismissToast() {
        checkProvider();
        if (this.manager != null) {
            this.manager.dismissToast();
        }
    }

    public void forceDismissAlerts() {
        checkProvider();
        if (this.manager != null) {
            this.manager.forceDismissAlerts();
        }
    }

    public void dismissTopNonFatalAlert() {
        checkProvider();
        if (this.manager != null) {
            this.manager.dismissTopNonFatalAlert();
        }
    }

    public void dismissBlocking() {
        checkProvider();
        if (this.manager != null) {
            this.manager.dismissBlocking();
        }
    }

    private void checkProvider() {
    }

    public void addManagedDialog(IXLEManagedDialog dialog) {
        checkProvider();
        if (this.manager != null) {
            this.manager.addManagedDialog(dialog);
        }
    }

    public void onApplicationPause() {
        if (this.manager != null) {
            this.manager.onApplicationPause();
        }
    }

    public void onApplicationResume() {
        if (this.manager != null) {
            this.manager.onApplicationResume();
        }
    }
}
