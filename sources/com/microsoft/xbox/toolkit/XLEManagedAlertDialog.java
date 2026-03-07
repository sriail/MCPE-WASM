package com.microsoft.xbox.toolkit;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import com.microsoft.xbox.toolkit.IXLEManagedDialog;

public class XLEManagedAlertDialog extends AlertDialog implements IXLEManagedDialog {
    private IXLEManagedDialog.DialogType dialogType = IXLEManagedDialog.DialogType.NORMAL;

    protected XLEManagedAlertDialog(Context context) {
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

    public void safeDismiss() {
        DialogManager.getInstance().dismissManagedDialog(this);
    }

    public void quickDismiss() {
        super.dismiss();
    }

    public void onStop() {
        super.onStop();
        DialogManager.getInstance().onDialogStopped(this);
    }
}
