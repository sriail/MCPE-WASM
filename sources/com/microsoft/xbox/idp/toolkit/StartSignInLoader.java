package com.microsoft.xbox.idp.toolkit;

import android.content.Context;
import com.microsoft.xbox.idp.interop.XsapiUser;
import com.microsoft.xbox.idp.toolkit.WorkerLoader;
import com.microsoft.xbox.idp.util.ResultCache;

public class StartSignInLoader extends WorkerLoader<Result> {

    public static class Data {
    }

    public StartSignInLoader(Context context, ResultCache<Result> cache, Object resultKey) {
        super(context, new MyWorker(cache, resultKey));
    }

    /* access modifiers changed from: protected */
    public boolean isDataReleased(Result data) {
        return data.isReleased();
    }

    /* access modifiers changed from: protected */
    public void releaseData(Result data) {
        data.release();
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

        private MyWorker(ResultCache<Result> cache2, Object resultKey2) {
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
            XsapiUser.getInstance().startSignIn(new XsapiUser.StartSignInCallback() {
                public void onSuccess() {
                    Result result = new Result(new Data(), (HttpError) null);
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
