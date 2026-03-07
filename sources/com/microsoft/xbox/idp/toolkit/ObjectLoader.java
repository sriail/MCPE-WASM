package com.microsoft.xbox.idp.toolkit;

import android.content.Context;
import android.util.Log;
import com.google.gson.Gson;
import com.microsoft.xbox.idp.toolkit.WorkerLoader;
import com.microsoft.xbox.idp.util.HttpCall;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;

public class ObjectLoader<T> extends WorkerLoader<Result<T>> {
    /* access modifiers changed from: private */
    public static final String TAG = ObjectLoader.class.getSimpleName();

    public interface Cache {
        void clear();

        <T> Result<T> get(Object obj);

        <T> Result<T> put(Object obj, Result<T> result);

        <T> Result<T> remove(Object obj);
    }

    public ObjectLoader(Context context, Class<T> cls, Gson gson, HttpCall httpCall) {
        this(context, (Cache) null, (Object) null, cls, gson, httpCall);
    }

    public ObjectLoader(Context context, Cache cache, Object resultKey, Class<T> cls, Gson gson, HttpCall httpCall) {
        super(context, new MyWorker(cache, resultKey, cls, gson, httpCall));
    }

    /* access modifiers changed from: protected */
    public boolean isDataReleased(Result<T> result) {
        return result.isReleased();
    }

    /* access modifiers changed from: protected */
    public void releaseData(Result<T> result) {
        result.release();
    }

    public static class Result<T> extends LoaderResult<T> {
        protected Result(T data) {
            super(data, (HttpError) null);
        }

        protected Result(HttpError error) {
            super(null, error);
        }

        public boolean isReleased() {
            return true;
        }

        public void release() {
        }
    }

    private static class MyWorker<T> implements WorkerLoader.Worker<Result<T>> {
        /* access modifiers changed from: private */
        public final Cache cache;
        /* access modifiers changed from: private */
        public final Class<T> cls;
        /* access modifiers changed from: private */
        public final Gson gson;
        private final HttpCall httpCall;
        /* access modifiers changed from: private */
        public final Object resultKey;

        private MyWorker(Cache cache2, Object resultKey2, Class<T> cls2, Gson gson2, HttpCall httpCall2) {
            this.cache = cache2;
            this.resultKey = resultKey2;
            this.cls = cls2;
            this.gson = gson2;
            this.httpCall = httpCall2;
        }

        /* access modifiers changed from: private */
        public boolean hasCache() {
            return (this.cache == null || this.resultKey == null) ? false : true;
        }

        public void start(final WorkerLoader.ResultListener<Result<T>> listener) {
            Result<T> r;
            if (hasCache()) {
                synchronized (this.cache) {
                    r = this.cache.get(this.resultKey);
                }
                if (r != null) {
                    listener.onResult(r);
                    return;
                }
            }
            this.httpCall.getResponseAsync((HttpCall.Callback) new HttpCall.Callback() {
                public void processResponse(InputStream stream) throws Exception {
                    if (MyWorker.this.cls == Void.class) {
                        listener.onResult(new Result(null));
                        return;
                    }
                    StringWriter sw = new StringWriter();
                    try {
                        InputStreamReader r = new InputStreamReader(new BufferedInputStream(stream));
                        try {
                            Result<T> result = new Result<>(MyWorker.this.gson.fromJson((Reader) r, MyWorker.this.cls));
                            if (MyWorker.this.hasCache()) {
                                synchronized (MyWorker.this.cache) {
                                    MyWorker.this.cache.put(MyWorker.this.resultKey, result);
                                }
                            }
                            listener.onResult(result);
                        } finally {
                            r.close();
                        }
                    } finally {
                        sw.close();
                    }
                }

                public void processHttpError(int errorCode, int httpStatus, String errorMessage) {
                    Log.e(ObjectLoader.TAG, "errorCode: " + errorCode + ", httpStatus: " + httpStatus + ", errorMessage: " + errorMessage);
                    Result<T> result = new Result<>(new HttpError(errorCode, httpStatus, errorMessage));
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
