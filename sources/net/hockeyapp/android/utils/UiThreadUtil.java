package net.hockeyapp.android.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import java.lang.ref.WeakReference;

public class UiThreadUtil {
    private UiThreadUtil() {
    }

    private static class WbUtilHolder {
        public static final UiThreadUtil INSTANCE = new UiThreadUtil();

        private WbUtilHolder() {
        }
    }

    public static UiThreadUtil getInstance() {
        return WbUtilHolder.INSTANCE;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:1:0x0002, code lost:
        r0 = (android.app.Activity) r3.get();
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void dismissLoadingDialogAndDisplayError(java.lang.ref.WeakReference<android.app.Activity> r3, final android.app.ProgressDialog r4, final int r5) {
        /*
            r2 = this;
            if (r3 == 0) goto L_0x0012
            java.lang.Object r0 = r3.get()
            android.app.Activity r0 = (android.app.Activity) r0
            if (r0 == 0) goto L_0x0012
            net.hockeyapp.android.utils.UiThreadUtil$1 r1 = new net.hockeyapp.android.utils.UiThreadUtil$1
            r1.<init>(r4, r0, r5)
            r0.runOnUiThread(r1)
        L_0x0012:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: net.hockeyapp.android.utils.UiThreadUtil.dismissLoadingDialogAndDisplayError(java.lang.ref.WeakReference, android.app.ProgressDialog, int):void");
    }

    public void dismissLoading(WeakReference<Activity> weakActivity, final ProgressDialog progressDialog) {
        Activity activity;
        if (weakActivity != null && (activity = (Activity) weakActivity.get()) != null) {
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                }
            });
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:1:0x0002, code lost:
        r0 = (android.app.Activity) r3.get();
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void displayToastMessage(java.lang.ref.WeakReference<android.app.Activity> r3, final java.lang.String r4, final int r5) {
        /*
            r2 = this;
            if (r3 == 0) goto L_0x0012
            java.lang.Object r0 = r3.get()
            android.app.Activity r0 = (android.app.Activity) r0
            if (r0 == 0) goto L_0x0012
            net.hockeyapp.android.utils.UiThreadUtil$3 r1 = new net.hockeyapp.android.utils.UiThreadUtil$3
            r1.<init>(r0, r4, r5)
            r0.runOnUiThread(r1)
        L_0x0012:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: net.hockeyapp.android.utils.UiThreadUtil.displayToastMessage(java.lang.ref.WeakReference, java.lang.String, int):void");
    }
}
