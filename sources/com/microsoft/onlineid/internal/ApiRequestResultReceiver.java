package com.microsoft.onlineid.internal;

import android.app.PendingIntent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import com.microsoft.onlineid.exception.InternalException;

public abstract class ApiRequestResultReceiver extends ResultReceiver {
    /* access modifiers changed from: protected */
    public abstract void onFailure(Exception exc);

    /* access modifiers changed from: protected */
    public abstract void onSuccess(ApiResult apiResult);

    /* access modifiers changed from: protected */
    public abstract void onUINeeded(PendingIntent pendingIntent);

    /* access modifiers changed from: protected */
    public abstract void onUserCancel();

    public ApiRequestResultReceiver(Handler handler) {
        super(handler);
    }

    /* access modifiers changed from: protected */
    public void onReceiveResult(int resultCode, Bundle resultData) {
        ApiResult request = new ApiResult(resultData);
        switch (resultCode) {
            case -1:
                onSuccess(request);
                return;
            case 0:
                onUserCancel();
                return;
            case 1:
                onFailure(request.getException());
                return;
            case 2:
                onUINeeded(request.getUINeededIntent());
                return;
            default:
                onUnknownResult(request, resultCode);
                return;
        }
    }

    /* access modifiers changed from: protected */
    public void onUnknownResult(ApiResult result, int resultCode) {
        Assertion.check(false, "Unknown result code: " + resultCode);
        onFailure(new InternalException("Unknown result code: " + resultCode));
    }
}
