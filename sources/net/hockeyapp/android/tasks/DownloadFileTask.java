package net.hockeyapp.android.tasks;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.UUID;
import net.hockeyapp.android.Constants;
import net.hockeyapp.android.R;
import net.hockeyapp.android.listeners.DownloadFileListener;

public class DownloadFileTask extends AsyncTask<Void, Integer, Long> {
    protected static final int MAX_REDIRECTS = 6;
    protected Context mContext;
    private String mDownloadErrorMessage;
    protected String mFilePath = (Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download");
    protected String mFilename = (UUID.randomUUID() + ".apk");
    protected DownloadFileListener mNotifier;
    protected ProgressDialog mProgressDialog;
    protected String mUrlString;

    public DownloadFileTask(Context context, String urlString, DownloadFileListener notifier) {
        this.mContext = context;
        this.mUrlString = urlString;
        this.mNotifier = notifier;
        this.mDownloadErrorMessage = null;
    }

    public void attach(Context context) {
        this.mContext = context;
    }

    public void detach() {
        this.mContext = null;
        this.mProgressDialog = null;
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x008e A[SYNTHETIC, Splitter:B:26:0x008e] */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x0093 A[Catch:{ IOException -> 0x0097 }] */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x0123 A[SYNTHETIC, Splitter:B:55:0x0123] */
    /* JADX WARNING: Removed duplicated region for block: B:58:0x0128 A[Catch:{ IOException -> 0x012c }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.Long doInBackground(java.lang.Void... r23) {
        /*
            r22 = this;
            r9 = 0
            r12 = 0
            java.net.URL r15 = new java.net.URL     // Catch:{ IOException -> 0x0082 }
            java.lang.String r18 = r22.getURLString()     // Catch:{ IOException -> 0x0082 }
            r0 = r18
            r15.<init>(r0)     // Catch:{ IOException -> 0x0082 }
            r18 = 6
            r0 = r22
            r1 = r18
            java.net.URLConnection r2 = r0.createConnection(r15, r1)     // Catch:{ IOException -> 0x0082 }
            r2.connect()     // Catch:{ IOException -> 0x0082 }
            int r11 = r2.getContentLength()     // Catch:{ IOException -> 0x0082 }
            java.lang.String r3 = r2.getContentType()     // Catch:{ IOException -> 0x0082 }
            if (r3 == 0) goto L_0x004c
            java.lang.String r18 = "text"
            r0 = r18
            boolean r18 = r3.contains(r0)     // Catch:{ IOException -> 0x0082 }
            if (r18 == 0) goto L_0x004c
            java.lang.String r18 = "The requested download does not appear to be a file."
            r0 = r18
            r1 = r22
            r1.mDownloadErrorMessage = r0     // Catch:{ IOException -> 0x0082 }
            r18 = 0
            java.lang.Long r18 = java.lang.Long.valueOf(r18)     // Catch:{ IOException -> 0x0082 }
            if (r12 == 0) goto L_0x0041
            r12.close()     // Catch:{ IOException -> 0x0047 }
        L_0x0041:
            if (r9 == 0) goto L_0x0046
            r9.close()     // Catch:{ IOException -> 0x0047 }
        L_0x0046:
            return r18
        L_0x0047:
            r7 = move-exception
            r7.printStackTrace()
            goto L_0x0046
        L_0x004c:
            java.io.File r6 = new java.io.File     // Catch:{ IOException -> 0x0082 }
            r0 = r22
            java.lang.String r0 = r0.mFilePath     // Catch:{ IOException -> 0x0082 }
            r18 = r0
            r0 = r18
            r6.<init>(r0)     // Catch:{ IOException -> 0x0082 }
            boolean r14 = r6.mkdirs()     // Catch:{ IOException -> 0x0082 }
            if (r14 != 0) goto L_0x009c
            boolean r18 = r6.exists()     // Catch:{ IOException -> 0x0082 }
            if (r18 != 0) goto L_0x009c
            java.io.IOException r18 = new java.io.IOException     // Catch:{ IOException -> 0x0082 }
            java.lang.StringBuilder r19 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x0082 }
            r19.<init>()     // Catch:{ IOException -> 0x0082 }
            java.lang.String r20 = "Could not create the dir(s):"
            java.lang.StringBuilder r19 = r19.append(r20)     // Catch:{ IOException -> 0x0082 }
            java.lang.String r20 = r6.getAbsolutePath()     // Catch:{ IOException -> 0x0082 }
            java.lang.StringBuilder r19 = r19.append(r20)     // Catch:{ IOException -> 0x0082 }
            java.lang.String r19 = r19.toString()     // Catch:{ IOException -> 0x0082 }
            r18.<init>(r19)     // Catch:{ IOException -> 0x0082 }
            throw r18     // Catch:{ IOException -> 0x0082 }
        L_0x0082:
            r7 = move-exception
        L_0x0083:
            r7.printStackTrace()     // Catch:{ all -> 0x0120 }
            r18 = 0
            java.lang.Long r18 = java.lang.Long.valueOf(r18)     // Catch:{ all -> 0x0120 }
            if (r12 == 0) goto L_0x0091
            r12.close()     // Catch:{ IOException -> 0x0097 }
        L_0x0091:
            if (r9 == 0) goto L_0x0046
            r9.close()     // Catch:{ IOException -> 0x0097 }
            goto L_0x0046
        L_0x0097:
            r7 = move-exception
            r7.printStackTrace()
            goto L_0x0046
        L_0x009c:
            java.io.File r8 = new java.io.File     // Catch:{ IOException -> 0x0082 }
            r0 = r22
            java.lang.String r0 = r0.mFilename     // Catch:{ IOException -> 0x0082 }
            r18 = r0
            r0 = r18
            r8.<init>(r6, r0)     // Catch:{ IOException -> 0x0082 }
            java.io.BufferedInputStream r10 = new java.io.BufferedInputStream     // Catch:{ IOException -> 0x0082 }
            java.io.InputStream r18 = r2.getInputStream()     // Catch:{ IOException -> 0x0082 }
            r0 = r18
            r10.<init>(r0)     // Catch:{ IOException -> 0x0082 }
            java.io.FileOutputStream r13 = new java.io.FileOutputStream     // Catch:{ IOException -> 0x0138, all -> 0x0131 }
            r13.<init>(r8)     // Catch:{ IOException -> 0x0138, all -> 0x0131 }
            r18 = 1024(0x400, float:1.435E-42)
            r0 = r18
            byte[] r5 = new byte[r0]     // Catch:{ IOException -> 0x0101, all -> 0x0134 }
            r16 = 0
        L_0x00c1:
            int r4 = r10.read(r5)     // Catch:{ IOException -> 0x0101, all -> 0x0134 }
            r18 = -1
            r0 = r18
            if (r4 == r0) goto L_0x0106
            long r0 = (long) r4     // Catch:{ IOException -> 0x0101, all -> 0x0134 }
            r18 = r0
            long r16 = r16 + r18
            r18 = 1
            r0 = r18
            java.lang.Integer[] r0 = new java.lang.Integer[r0]     // Catch:{ IOException -> 0x0101, all -> 0x0134 }
            r18 = r0
            r19 = 0
            r0 = r16
            float r0 = (float) r0     // Catch:{ IOException -> 0x0101, all -> 0x0134 }
            r20 = r0
            r21 = 1120403456(0x42c80000, float:100.0)
            float r20 = r20 * r21
            float r0 = (float) r11     // Catch:{ IOException -> 0x0101, all -> 0x0134 }
            r21 = r0
            float r20 = r20 / r21
            int r20 = java.lang.Math.round(r20)     // Catch:{ IOException -> 0x0101, all -> 0x0134 }
            java.lang.Integer r20 = java.lang.Integer.valueOf(r20)     // Catch:{ IOException -> 0x0101, all -> 0x0134 }
            r18[r19] = r20     // Catch:{ IOException -> 0x0101, all -> 0x0134 }
            r0 = r22
            r1 = r18
            r0.publishProgress(r1)     // Catch:{ IOException -> 0x0101, all -> 0x0134 }
            r18 = 0
            r0 = r18
            r13.write(r5, r0, r4)     // Catch:{ IOException -> 0x0101, all -> 0x0134 }
            goto L_0x00c1
        L_0x0101:
            r7 = move-exception
            r12 = r13
            r9 = r10
            goto L_0x0083
        L_0x0106:
            r13.flush()     // Catch:{ IOException -> 0x0101, all -> 0x0134 }
            java.lang.Long r18 = java.lang.Long.valueOf(r16)     // Catch:{ IOException -> 0x0101, all -> 0x0134 }
            if (r13 == 0) goto L_0x0112
            r13.close()     // Catch:{ IOException -> 0x011b }
        L_0x0112:
            if (r10 == 0) goto L_0x0117
            r10.close()     // Catch:{ IOException -> 0x011b }
        L_0x0117:
            r12 = r13
            r9 = r10
            goto L_0x0046
        L_0x011b:
            r7 = move-exception
            r7.printStackTrace()
            goto L_0x0117
        L_0x0120:
            r18 = move-exception
        L_0x0121:
            if (r12 == 0) goto L_0x0126
            r12.close()     // Catch:{ IOException -> 0x012c }
        L_0x0126:
            if (r9 == 0) goto L_0x012b
            r9.close()     // Catch:{ IOException -> 0x012c }
        L_0x012b:
            throw r18
        L_0x012c:
            r7 = move-exception
            r7.printStackTrace()
            goto L_0x012b
        L_0x0131:
            r18 = move-exception
            r9 = r10
            goto L_0x0121
        L_0x0134:
            r18 = move-exception
            r12 = r13
            r9 = r10
            goto L_0x0121
        L_0x0138:
            r7 = move-exception
            r9 = r10
            goto L_0x0083
        */
        throw new UnsupportedOperationException("Method not decompiled: net.hockeyapp.android.tasks.DownloadFileTask.doInBackground(java.lang.Void[]):java.lang.Long");
    }

    /* access modifiers changed from: protected */
    public void setConnectionProperties(HttpURLConnection connection) {
        connection.addRequestProperty("User-Agent", Constants.SDK_USER_AGENT);
        connection.setInstanceFollowRedirects(true);
        if (Build.VERSION.SDK_INT <= 9) {
            connection.setRequestProperty("connection", "close");
        }
    }

    /* access modifiers changed from: protected */
    public URLConnection createConnection(URL url, int remainingRedirects) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        setConnectionProperties(connection);
        int code = connection.getResponseCode();
        if ((code != 301 && code != 302 && code != 303) || remainingRedirects == 0) {
            return connection;
        }
        URL movedUrl = new URL(connection.getHeaderField("Location"));
        if (url.getProtocol().equals(movedUrl.getProtocol())) {
            return connection;
        }
        connection.disconnect();
        return createConnection(movedUrl, remainingRedirects - 1);
    }

    /* access modifiers changed from: protected */
    public void onProgressUpdate(Integer... args) {
        try {
            if (this.mProgressDialog == null) {
                this.mProgressDialog = new ProgressDialog(this.mContext);
                this.mProgressDialog.setProgressStyle(1);
                this.mProgressDialog.setMessage("Loading...");
                this.mProgressDialog.setCancelable(false);
                this.mProgressDialog.show();
            }
            this.mProgressDialog.setProgress(args[0].intValue());
        } catch (Exception e) {
        }
    }

    /* access modifiers changed from: protected */
    public void onPostExecute(Long result) {
        String message;
        if (this.mProgressDialog != null) {
            try {
                this.mProgressDialog.dismiss();
            } catch (Exception e) {
            }
        }
        if (result.longValue() > 0) {
            this.mNotifier.downloadSuccessful(this);
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.setDataAndType(Uri.fromFile(new File(this.mFilePath, this.mFilename)), "application/vnd.android.package-archive");
            intent.setFlags(268435456);
            StrictMode.VmPolicy oldVmPolicy = null;
            if (Build.VERSION.SDK_INT >= 24) {
                oldVmPolicy = StrictMode.getVmPolicy();
                StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().penaltyLog().build());
            }
            this.mContext.startActivity(intent);
            if (oldVmPolicy != null) {
                StrictMode.setVmPolicy(oldVmPolicy);
                return;
            }
            return;
        }
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this.mContext);
            builder.setTitle(R.string.hockeyapp_download_failed_dialog_title);
            if (this.mDownloadErrorMessage == null) {
                message = this.mContext.getString(R.string.hockeyapp_download_failed_dialog_message);
            } else {
                message = this.mDownloadErrorMessage;
            }
            builder.setMessage(message);
            builder.setNegativeButton(R.string.hockeyapp_download_failed_dialog_negative_button, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    DownloadFileTask.this.mNotifier.downloadFailed(DownloadFileTask.this, false);
                }
            });
            builder.setPositiveButton(R.string.hockeyapp_download_failed_dialog_positive_button, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    DownloadFileTask.this.mNotifier.downloadFailed(DownloadFileTask.this, true);
                }
            });
            builder.create().show();
        } catch (Exception e2) {
        }
    }

    /* access modifiers changed from: protected */
    public String getURLString() {
        return this.mUrlString + "&type=apk";
    }
}
