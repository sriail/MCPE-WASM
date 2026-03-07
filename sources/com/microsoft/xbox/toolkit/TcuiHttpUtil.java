package com.microsoft.xbox.toolkit;

import android.util.Pair;
import com.microsoft.xbox.idp.util.HttpCall;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import net.hockeyapp.android.utils.HttpURLConnectionBuilder;

public class TcuiHttpUtil {
    public static <T> T getResponseSync(HttpCall httpCall, final Class<T> returnClass) throws XLEException {
        final AtomicReference<Pair<Boolean, T>> notifier = new AtomicReference<>();
        notifier.set(new Pair(false, (Object) null));
        httpCall.getResponseAsync((HttpCall.Callback) new HttpCall.Callback() {
            public void processResponse(InputStream stream) throws Exception {
                T result = GsonUtil.deserializeJson(stream, returnClass);
                synchronized (notifier) {
                    notifier.set(new Pair(true, result));
                    notifier.notify();
                }
            }

            public void processHttpError(int errorCode, int httpStatus, String errorMessage) {
                synchronized (notifier) {
                    notifier.set(new Pair(true, (Object) null));
                    notifier.notify();
                }
            }
        });
        synchronized (notifier) {
            while (!((Boolean) notifier.get().first).booleanValue()) {
                try {
                    notifier.wait();
                } catch (InterruptedException e) {
                }
            }
        }
        return notifier.get().second;
    }

    public static boolean getResponseSyncSucceeded(HttpCall httpCall, final List<Integer> acceptableStatusCodes) {
        final AtomicReference<Boolean> notifier = new AtomicReference<>();
        httpCall.getResponseAsync((HttpCall.Callback) new HttpCall.Callback() {
            public void processResponse(InputStream stream) throws Exception {
                synchronized (notifier) {
                    notifier.set(true);
                    notifier.notify();
                }
            }

            public void processHttpError(int errorCode, int httpStatus, String errorMessage) {
                boolean result = acceptableStatusCodes.contains(Integer.valueOf(httpStatus));
                synchronized (notifier) {
                    notifier.set(Boolean.valueOf(result));
                    notifier.notify();
                }
            }
        });
        synchronized (notifier) {
            while (notifier.get() == null) {
                try {
                    notifier.wait();
                } catch (InterruptedException e) {
                }
            }
        }
        return notifier.get().booleanValue();
    }

    public static String getResponseBodySync(HttpCall httpCall) throws XLEException {
        final AtomicReference<Pair<Boolean, String>> notifier = new AtomicReference<>();
        notifier.set(new Pair(false, (Object) null));
        httpCall.getResponseAsync((HttpCall.Callback) new HttpCall.Callback() {
            public void processResponse(InputStream stream) throws Exception {
                String responseBody = null;
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(stream, HttpURLConnectionBuilder.DEFAULT_CHARSET), 4096);
                    StringBuilder sb = new StringBuilder();
                    while (true) {
                        String line = reader.readLine();
                        if (line == null) {
                            break;
                        }
                        sb.append(line + "\n");
                    }
                    responseBody = sb.toString();
                } catch (IOException ioe) {
                    XLEAssert.assertTrue("Failed to read ShortCircuitProfileMessage string - " + ioe.getMessage(), false);
                }
                synchronized (notifier) {
                    notifier.set(new Pair(true, responseBody));
                    notifier.notify();
                }
            }

            public void processHttpError(int errorCode, int httpStatus, String errorMessage) {
                synchronized (notifier) {
                    notifier.set(new Pair(true, (Object) null));
                    notifier.notify();
                }
            }
        });
        synchronized (notifier) {
            while (!((Boolean) notifier.get().first).booleanValue()) {
                try {
                    notifier.wait();
                } catch (InterruptedException e) {
                }
            }
        }
        return (String) notifier.get().second;
    }

    public static <T> void throwIfNullOrFalse(T result) throws XLEException {
        if (result == null && !Boolean.getBoolean(result.toString())) {
            throw new XLEException(2);
        }
    }
}
