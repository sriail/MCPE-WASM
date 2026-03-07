package com.microsoft.xbox.idp.toolkit;

import android.content.Context;
import com.microsoft.xbox.idp.interop.Interop;
import com.microsoft.xbox.idp.toolkit.WorkerLoader;

public class XBLogoutLoader extends WorkerLoader<Result> {
    public XBLogoutLoader(Context context, long userPtr) {
        super(context, new MyWorker(userPtr));
    }

    /* access modifiers changed from: protected */
    public boolean isDataReleased(Result data) {
        return data.isReleased();
    }

    /* access modifiers changed from: protected */
    public void releaseData(Result data) {
        data.release();
    }

    public static class Result extends LoaderResult<Void> {
        protected Result() {
            super(null, (HttpError) null);
        }

        public boolean isReleased() {
            return true;
        }

        public void release() {
        }
    }

    private static class MyWorker implements WorkerLoader.Worker<Result> {
        private final long userPtr;

        private MyWorker(long userPtr2) {
            this.userPtr = userPtr2;
        }

        public void start(final WorkerLoader.ResultListener<Result> listener) {
            Interop.InvokeXBLogout(this.userPtr, new Interop.XBLogoutCallback() {
                public void onLoggedOut() {
                    listener.onResult(new Result());
                }
            });
        }

        public void cancel() {
        }
    }
}
