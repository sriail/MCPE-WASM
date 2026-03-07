package com.microsoft.xbox.idp.toolkit;

import android.content.Context;
import com.microsoft.xbox.idp.interop.Interop;
import com.microsoft.xbox.idp.toolkit.WorkerLoader;
import com.microsoft.xbox.idp.util.ResultCache;

public class EventInitializationLoader extends WorkerLoader<Result> {
    public EventInitializationLoader(Context context, long userPtr, String rpsTicket, ResultCache<Result> cache, Object resultKey) {
        super(context, new MyWorker(userPtr, rpsTicket, cache, resultKey));
    }

    /* access modifiers changed from: protected */
    public boolean isDataReleased(Result data) {
        return true;
    }

    /* access modifiers changed from: protected */
    public void releaseData(Result data) {
    }

    public static class Result extends LoaderResult<Void> {
        protected Result(HttpError error) {
            super(null, error);
        }

        public boolean isReleased() {
            return true;
        }

        public void release() {
        }
    }

    private static class MyWorker implements WorkerLoader.Worker<Result> {
        /* access modifiers changed from: private */
        public final ResultCache<Result> cache;
        /* access modifiers changed from: private */
        public final Object resultKey;
        private final String rpsTicket;
        private final long userPtr;

        private MyWorker(long userPtr2, String rpsTicket2, ResultCache<Result> cache2, Object resultKey2) {
            this.userPtr = userPtr2;
            this.rpsTicket = rpsTicket2;
            this.cache = cache2;
            this.resultKey = resultKey2;
        }

        /* access modifiers changed from: private */
        public boolean hasCache() {
            return (this.cache == null || this.resultKey == null) ? false : true;
        }

        public void start(final WorkerLoader.ResultListener<Result> listener) {
            Result r;
            if (hasCache()) {
                synchronized (this.cache) {
                    r = this.cache.get(this.resultKey);
                }
                if (r != null) {
                    listener.onResult(r);
                    return;
                }
            }
            Interop.InvokeEventInitialization(this.userPtr, this.rpsTicket, new Interop.EventInitializationCallback() {
                public void onSuccess() {
                    Result result = new Result((HttpError) null);
                    if (MyWorker.this.hasCache()) {
                        synchronized (MyWorker.this.cache) {
                            MyWorker.this.cache.put(MyWorker.this.resultKey, result);
                        }
                    }
                    listener.onResult(result);
                }

                public void onError(int httpStatusCode, int errorCode, String errorMessage) {
                    Result result = new Result(new HttpError(errorCode, httpStatusCode, errorMessage));
                    if (MyWorker.this.hasCache()) {
                        synchronized (MyWorker.this.cache) {
                            MyWorker.this.cache.put(MyWorker.this.resultKey, result);
                        }
                    }
                    listener.onResult(result);
                }
            });
        }

        public void cancel() {
        }
    }
}
