package com.microsoft.onlineid.internal;

import android.app.PendingIntent;
import android.content.Intent;
import com.microsoft.onlineid.exception.InternalException;

public abstract class ActivityResultHandler {
    /* access modifiers changed from: protected */
    public abstract void onFailure(Exception exc);

    /* access modifiers changed from: protected */
    public abstract void onSuccess(ApiResult apiResult);

    /* access modifiers changed from: protected */
    public abstract void onUINeeded(PendingIntent pendingIntent);

    /* access modifiers changed from: protected */
    public abstract void onUserCancel();

    public void onActivityResult(int resultCode, Intent data) {
        ApiResult apiResult = new ApiResult(data != null ? data.getExtras() : null);
        switch (resultCode) {
            case -1:
                onSuccess(apiResult);
                return;
            case 0:
                onUserCancel();
                return;
            case 1:
                onFailure(apiResult.getException());
                return;
            case 2:
                onUINeeded(apiResult.getUINeededIntent());
                return;
            default:
                onUnknownResult(apiResult, resultCode);
                return;
        }
    }

    /* access modifiers changed from: protected */
    public void onUnknownResult(ApiResult result, int resultCode) {
        Assertion.check(false, "Unknown result code: " + resultCode);
        onFailure(new InternalException("Unknown result code: " + resultCode));
    }
}
