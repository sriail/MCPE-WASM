package com.microsoft.xbox.idp.toolkit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import com.microsoft.xbox.idp.toolkit.WorkerLoader;
import com.microsoft.xbox.idp.util.HttpCall;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class BitmapLoader extends WorkerLoader<Result> {
    /* access modifiers changed from: private */
    public static final String TAG = BitmapLoader.class.getSimpleName();

    public interface Cache {
        void clear();

        byte[] get(Object obj);

        byte[] put(Object obj, byte[] bArr);

        byte[] remove(Object obj);
    }

    public BitmapLoader(Context context, HttpCall httpCall) {
        this(context, (Cache) null, (Object) null, httpCall);
    }

    public BitmapLoader(Context context, Cache cache, Object resultKey, HttpCall httpCall) {
        super(context, new MyWorker(cache, resultKey, httpCall));
    }

    /* access modifiers changed from: protected */
    public boolean isDataReleased(Result result) {
        return result.isReleased();
    }

    /* access modifiers changed from: protected */
    public void releaseData(Result result) {
        result.release();
    }

    public static class Result extends LoaderResult<Bitmap> {
        protected Result(Bitmap data) {
            super(data, (HttpError) null);
        }

        protected Result(HttpError error) {
            super(null, error);
        }

        public boolean isReleased() {
            return hasData() && ((Bitmap) getData()).isRecycled();
        }

        public void release() {
            if (hasData()) {
                ((Bitmap) getData()).recycle();
            }
        }
    }

    private static class MyWorker implements WorkerLoader.Worker<Result> {
        static final /* synthetic */ boolean $assertionsDisabled = (!BitmapLoader.class.desiredAssertionStatus());
        /* access modifiers changed from: private */
        public final Cache cache;
        private final HttpCall httpCall;
        /* access modifiers changed from: private */
        public final Object resultKey;

        private MyWorker(Cache cache2, Object resultKey2, HttpCall httpCall2) {
            if ($assertionsDisabled || httpCall2 != null) {
                this.cache = cache2;
                this.resultKey = resultKey2;
                this.httpCall = httpCall2;
                return;
            }
            throw new AssertionError();
        }

        /* access modifiers changed from: private */
        public boolean hasCache() {
            return (this.cache == null || this.resultKey == null) ? false : true;
        }

        public void start(final WorkerLoader.ResultListener<Result> listener) {
            final byte[] data;
            if (hasCache()) {
                synchronized (this.cache) {
                    data = this.cache.get(this.resultKey);
                }
                if (data != null) {
                    new Thread(new Runnable() {
                        public void run() {
                            listener.onResult(new Result(BitmapFactory.decodeByteArray(data, 0, data.length)));
                        }
                    }).start();
                    return;
                }
            }
            this.httpCall.getResponseAsync((HttpCall.Callback) new HttpCall.Callback() {
                public void processResponse(InputStream stream) throws Exception {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    try {
                        BufferedInputStream bis = new BufferedInputStream(stream);
                        try {
                            BitmapLoader.readStream(bis, baos);
                            byte[] data = baos.toByteArray();
                            if (MyWorker.this.hasCache()) {
                                synchronized (MyWorker.this.cache) {
                                    MyWorker.this.cache.put(MyWorker.this.resultKey, data);
                                }
                            }
                            listener.onResult(new Result(BitmapFactory.decodeByteArray(data, 0, data.length)));
                        } finally {
                            bis.close();
                        }
                    } finally {
                        baos.close();
                    }
                }

                public void processHttpError(int errorCode, int httpStatus, String errorMessage) {
                    Log.e(BitmapLoader.TAG, "errorCode: " + errorCode + ", httpStatus: " + httpStatus + ", errorMessage: " + errorMessage);
                    listener.onResult(new Result(new HttpError(errorCode, httpStatus, errorMessage)));
                }
            });
        }

        public void cancel() {
        }
    }

    /* access modifiers changed from: private */
    public static void readStream(InputStream from, OutputStream to) throws IOException {
        while (true) {
            int oneByte = from.read();
            if (oneByte != -1) {
                to.write(oneByte);
            } else {
                return;
            }
        }
    }
}
