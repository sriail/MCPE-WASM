package com.microsoft.xbox.idp.toolkit;

import android.content.Context;
import com.microsoft.xbox.idp.interop.Interop;
import com.microsoft.xbox.idp.toolkit.WorkerLoader;
import com.microsoft.xbox.idp.util.AuthFlowResult;
import com.microsoft.xbox.idp.util.ResultCache;

public class XTokenLoader extends WorkerLoader<Result> {
    public XTokenLoader(Context context, long userPtr) {
        this(context, userPtr, (ResultCache<Result>) null, (Object) null);
    }

    public XTokenLoader(Context context, long userPtr, ResultCache<Result> cache, Object resultKey) {
        super(context, new MyWorker(userPtr, cache, resultKey));
    }

    /* access modifiers changed from: protected */
    public boolean isDataReleased(Result data) {
        return data.isReleased();
    }

    /* access modifiers changed from: protected */
    public void releaseData(Result data) {
        data.release();
    }

    public static class Data {
        private final AuthFlowResult authFlowResult;

        public Data(AuthFlowResult authFlowResult2) {
            this.authFlowResult = authFlowResult2;
        }

        public AuthFlowResult getAuthFlowResult() {
            return this.authFlowResult;
        }
    }

    public static class Result extends LoaderResult<Data> {
        protected Result(Data data, HttpError error) {
            super(data, error);
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
        private final long userPtr;

        public MyWorker(long userPtr2, ResultCache<Result> cache2, Object resultKey2) {
            this.userPtr = userPtr2;
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
            Interop.InvokeXTokenCallback(this.userPtr, new Interop.Callback() {
                public void onXTokenAcquired(long authFlowResultPtr) {
                    Result result = new Result(new Data(new AuthFlowResult(authFlowResultPtr)), (HttpError) null);
                    if (MyWorker.this.hasCache()) {
                        synchronized (MyWorker.this.cache) {
                            MyWorker.this.cache.put(MyWorker.this.resultKey, result);
                        }
                    }
                    listener.onResult(result);
                }

                public void onError(int httpStatusCode, int errorCode, String errorMessage) {
                    Result result = new Result((Data) null, new HttpError(errorCode, httpStatusCode, errorMessage));
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
