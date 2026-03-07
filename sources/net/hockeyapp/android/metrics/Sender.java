package net.hockeyapp.android.metrics;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.text.TextUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.GZIPOutputStream;
import net.hockeyapp.android.utils.AsyncTaskUtils;
import net.hockeyapp.android.utils.HockeyLog;
import net.hockeyapp.android.utils.HttpURLConnectionBuilder;

public class Sender {
    static final String DEFAULT_ENDPOINT_URL = "https://gate.hockeyapp.net/v2/track";
    static final int DEFAULT_SENDER_CONNECT_TIMEOUT = 15000;
    static final int DEFAULT_SENDER_READ_TIMEOUT = 10000;
    static final int MAX_REQUEST_COUNT = 10;
    private static final String TAG = "HockeyApp-Metrics";
    private String mCustomServerURL;
    private AtomicInteger mRequestCount = new AtomicInteger(0);
    protected WeakReference<Persistence> mWeakPersistence;

    protected Sender() {
    }

    /* access modifiers changed from: protected */
    public void triggerSending() {
        if (requestCount() < 10) {
            try {
                AsyncTaskUtils.execute(new AsyncTask<Void, Void, Void>() {
                    /* access modifiers changed from: protected */
                    public Void doInBackground(Void... params) {
                        Sender.this.sendAvailableFiles();
                        return null;
                    }
                });
            } catch (RejectedExecutionException e) {
                HockeyLog.error("Could not send events. Executor rejected async task.", (Throwable) e);
            }
        } else {
            HockeyLog.debug(TAG, "We have already 10 pending requests, not sending anything.");
        }
    }

    /* access modifiers changed from: protected */
    public void triggerSendingForTesting(final HttpURLConnection connection, final File file, final String persistedData) {
        if (requestCount() < 10) {
            this.mRequestCount.getAndIncrement();
            AsyncTaskUtils.execute(new AsyncTask<Void, Void, Void>() {
                /* access modifiers changed from: protected */
                public Void doInBackground(Void... params) {
                    Sender.this.send(connection, file, persistedData);
                    return null;
                }
            });
        }
    }

    /* access modifiers changed from: protected */
    public void sendAvailableFiles() {
        if (getPersistence() != null) {
            File fileToSend = getPersistence().nextAvailableFileInDirectory();
            String persistedData = loadData(fileToSend);
            HttpURLConnection connection = createConnection();
            if (persistedData != null && connection != null) {
                send(connection, fileToSend, persistedData);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void send(HttpURLConnection connection, File file, String persistedData) {
        logRequest(connection, persistedData);
        if (connection != null && file != null && persistedData != null) {
            this.mRequestCount.getAndIncrement();
            try {
                connection.connect();
                onResponse(connection, connection.getResponseCode(), persistedData, file);
            } catch (IOException e) {
                HockeyLog.debug(TAG, "Couldn't send data with IOException: " + e.toString());
                this.mRequestCount.getAndDecrement();
                if (getPersistence() != null) {
                    HockeyLog.debug(TAG, "Persisting because of IOException: We're probably offline.");
                    getPersistence().makeAvailable(file);
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public String loadData(File file) {
        String persistedData = null;
        if (!(getPersistence() == null || file == null || (persistedData = getPersistence().load(file)) == null || !persistedData.isEmpty())) {
            getPersistence().deleteFile(file);
        }
        return persistedData;
    }

    /* access modifiers changed from: protected */
    public HttpURLConnection createConnection() {
        URL url;
        try {
            if (getCustomServerURL() == null) {
                url = new URL(DEFAULT_ENDPOINT_URL);
            } else {
                url = new URL(this.mCustomServerURL);
                if (url == null) {
                    url = new URL(DEFAULT_ENDPOINT_URL);
                }
            }
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(DEFAULT_SENDER_READ_TIMEOUT);
            connection.setConnectTimeout(DEFAULT_SENDER_CONNECT_TIMEOUT);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-json-stream");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            return connection;
        } catch (IOException e) {
            HockeyLog.error(TAG, "Could not open connection for provided URL with exception: ", e);
            return null;
        }
    }

    /* access modifiers changed from: protected */
    public void onResponse(HttpURLConnection connection, int responseCode, String payload, File fileToSend) {
        this.mRequestCount.getAndDecrement();
        HockeyLog.debug(TAG, "response code " + Integer.toString(responseCode));
        if (isRecoverableError(responseCode)) {
            HockeyLog.debug(TAG, "Recoverable error (probably a server error), persisting data:\n" + payload);
            if (getPersistence() != null) {
                getPersistence().makeAvailable(fileToSend);
                return;
            }
            return;
        }
        if (getPersistence() != null) {
            getPersistence().deleteFile(fileToSend);
        }
        StringBuilder builder = new StringBuilder();
        if (isExpected(responseCode)) {
            triggerSending();
        } else {
            onUnexpected(connection, responseCode, builder);
        }
    }

    /* access modifiers changed from: protected */
    public boolean isRecoverableError(int responseCode) {
        return Arrays.asList(new Integer[]{408, 429, 500, 503, 511}).contains(Integer.valueOf(responseCode));
    }

    /* access modifiers changed from: protected */
    public boolean isExpected(int responseCode) {
        return 200 <= responseCode && responseCode <= 203;
    }

    /* access modifiers changed from: protected */
    public void onUnexpected(HttpURLConnection connection, int responseCode, StringBuilder builder) {
        String message = String.format(Locale.ROOT, "Unexpected response code: %d", new Object[]{Integer.valueOf(responseCode)});
        builder.append(message);
        builder.append("\n");
        HockeyLog.error(TAG, message);
        readResponse(connection, builder);
    }

    private void logRequest(HttpURLConnection connection, String payload) {
        Writer writer = null;
        if (!(connection == null || payload == null)) {
            try {
                HockeyLog.debug(TAG, "Sending payload:\n" + payload);
                HockeyLog.debug(TAG, "Using URL:" + connection.getURL().toString());
                writer = getWriter(connection);
                writer.write(payload);
                writer.flush();
            } catch (IOException e) {
                HockeyLog.debug(TAG, "Couldn't log data with: " + e.toString());
                if (writer != null) {
                    try {
                        writer.close();
                        return;
                    } catch (IOException e2) {
                        HockeyLog.error(TAG, "Couldn't close writer with: " + e2.toString());
                        return;
                    }
                } else {
                    return;
                }
            } catch (Throwable th) {
                if (writer != null) {
                    try {
                        writer.close();
                    } catch (IOException e3) {
                        HockeyLog.error(TAG, "Couldn't close writer with: " + e3.toString());
                    }
                }
                throw th;
            }
        }
        if (writer != null) {
            try {
                writer.close();
            } catch (IOException e4) {
                HockeyLog.error(TAG, "Couldn't close writer with: " + e4.toString());
            }
        }
    }

    /* access modifiers changed from: protected */
    public void readResponse(HttpURLConnection connection, StringBuilder builder) {
        String result;
        StringBuffer buffer = new StringBuffer();
        InputStream inputStream = null;
        try {
            inputStream = connection.getErrorStream();
            if (inputStream == null) {
                inputStream = connection.getInputStream();
            }
            if (inputStream != null) {
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, HttpURLConnectionBuilder.DEFAULT_CHARSET));
                while (true) {
                    String inputLine = br.readLine();
                    if (inputLine == null) {
                        break;
                    }
                    buffer.append(inputLine);
                }
                result = buffer.toString();
            } else {
                result = connection.getResponseMessage();
            }
            if (!TextUtils.isEmpty(result)) {
                HockeyLog.verbose(result);
            } else {
                HockeyLog.verbose(TAG, "Couldn't log response, result is null or empty string");
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    HockeyLog.error(TAG, e.toString());
                }
            }
        } catch (IOException e2) {
            HockeyLog.error(TAG, e2.toString());
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e3) {
                    HockeyLog.error(TAG, e3.toString());
                }
            }
        } catch (Throwable th) {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e4) {
                    HockeyLog.error(TAG, e4.toString());
                }
            }
            throw th;
        }
    }

    /* access modifiers changed from: protected */
    @TargetApi(19)
    public Writer getWriter(HttpURLConnection connection) throws IOException {
        if (Build.VERSION.SDK_INT < 19) {
            return new OutputStreamWriter(connection.getOutputStream(), HttpURLConnectionBuilder.DEFAULT_CHARSET);
        }
        connection.addRequestProperty("Content-Encoding", "gzip");
        connection.setRequestProperty("Content-Type", "application/x-json-stream");
        return new OutputStreamWriter(new GZIPOutputStream(connection.getOutputStream(), true), HttpURLConnectionBuilder.DEFAULT_CHARSET);
    }

    /* access modifiers changed from: protected */
    public Persistence getPersistence() {
        if (this.mWeakPersistence != null) {
            return (Persistence) this.mWeakPersistence.get();
        }
        return null;
    }

    /* access modifiers changed from: protected */
    public void setPersistence(Persistence persistence) {
        this.mWeakPersistence = new WeakReference<>(persistence);
    }

    /* access modifiers changed from: protected */
    public int requestCount() {
        return this.mRequestCount.get();
    }

    /* access modifiers changed from: protected */
    public String getCustomServerURL() {
        return this.mCustomServerURL;
    }

    /* access modifiers changed from: protected */
    public void setCustomServerURL(String customServerURL) {
        this.mCustomServerURL = customServerURL;
    }
}
