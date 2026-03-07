package net.hockeyapp.android.metrics;

import android.content.Context;
import android.text.TextUtils;
import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import net.hockeyapp.android.utils.HockeyLog;

class Persistence {
    private static final String BIT_TELEMETRY_DIRECTORY = "/net.hockeyapp.android/telemetry/";
    private static final Object LOCK = new Object();
    private static final Integer MAX_FILE_COUNT = 50;
    private static final String TAG = "HA-MetricsPersistence";
    protected ArrayList<File> mServedFiles;
    protected final File mTelemetryDirectory;
    private final WeakReference<Context> mWeakContext;
    protected WeakReference<Sender> mWeakSender;

    protected Persistence(Context context, File telemetryDirectory, Sender sender) {
        this.mWeakContext = new WeakReference<>(context);
        this.mServedFiles = new ArrayList<>(51);
        this.mTelemetryDirectory = telemetryDirectory;
        this.mWeakSender = new WeakReference<>(sender);
        createDirectoriesIfNecessary();
    }

    protected Persistence(Context context, Sender sender) {
        this(context, new File(context.getFilesDir().getAbsolutePath() + BIT_TELEMETRY_DIRECTORY), (Sender) null);
        setSender(sender);
    }

    /* access modifiers changed from: protected */
    public void persist(String[] data) {
        if (!isFreeSpaceAvailable()) {
            HockeyLog.warn(TAG, "Failed to persist file: Too many files on disk.");
            getSender().triggerSending();
            return;
        }
        StringBuilder buffer = new StringBuilder();
        for (String aData : data) {
            if (buffer.length() > 0) {
                buffer.append(10);
            }
            buffer.append(aData);
        }
        if (writeToDisk(buffer.toString())) {
            getSender().triggerSending();
        }
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:11:?, code lost:
        r2 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x005e, code lost:
        if (r4 == null) goto L_0x00af;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:?, code lost:
        r4.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0063, code lost:
        r3 = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x0094, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x0095, code lost:
        r0.printStackTrace();
        r3 = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:?, code lost:
        r3.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x00a1, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x00a2, code lost:
        r0.printStackTrace();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x00a6, code lost:
        r6 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:42:0x00a7, code lost:
        r3 = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:43:0x00a9, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:44:0x00aa, code lost:
        r3 = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:47:0x00af, code lost:
        r3 = r4;
     */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x009d A[SYNTHETIC, Splitter:B:36:0x009d] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean writeToDisk(java.lang.String r11) {
        /*
            r10 = this;
            java.util.UUID r6 = java.util.UUID.randomUUID()
            java.lang.String r5 = r6.toString()
            r6 = 0
            java.lang.Boolean r2 = java.lang.Boolean.valueOf(r6)
            r3 = 0
            java.lang.Object r7 = LOCK     // Catch:{ Exception -> 0x006c }
            monitor-enter(r7)     // Catch:{ Exception -> 0x006c }
            java.io.File r1 = new java.io.File     // Catch:{ all -> 0x0069 }
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x0069 }
            r6.<init>()     // Catch:{ all -> 0x0069 }
            java.io.File r8 = r10.mTelemetryDirectory     // Catch:{ all -> 0x0069 }
            java.lang.StringBuilder r6 = r6.append(r8)     // Catch:{ all -> 0x0069 }
            java.lang.String r8 = "/"
            java.lang.StringBuilder r6 = r6.append(r8)     // Catch:{ all -> 0x0069 }
            java.lang.StringBuilder r6 = r6.append(r5)     // Catch:{ all -> 0x0069 }
            java.lang.String r6 = r6.toString()     // Catch:{ all -> 0x0069 }
            r1.<init>(r6)     // Catch:{ all -> 0x0069 }
            java.io.FileOutputStream r4 = new java.io.FileOutputStream     // Catch:{ all -> 0x0069 }
            r6 = 1
            r4.<init>(r1, r6)     // Catch:{ all -> 0x0069 }
            byte[] r6 = r11.getBytes()     // Catch:{ all -> 0x00ac }
            r4.write(r6)     // Catch:{ all -> 0x00ac }
            java.lang.String r6 = "HA-MetricsPersistence"
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ all -> 0x00ac }
            r8.<init>()     // Catch:{ all -> 0x00ac }
            java.lang.String r9 = "Saving data to: "
            java.lang.StringBuilder r8 = r8.append(r9)     // Catch:{ all -> 0x00ac }
            java.lang.String r9 = r1.toString()     // Catch:{ all -> 0x00ac }
            java.lang.StringBuilder r8 = r8.append(r9)     // Catch:{ all -> 0x00ac }
            java.lang.String r8 = r8.toString()     // Catch:{ all -> 0x00ac }
            net.hockeyapp.android.utils.HockeyLog.warn((java.lang.String) r6, (java.lang.String) r8)     // Catch:{ all -> 0x00ac }
            monitor-exit(r7)     // Catch:{ all -> 0x00ac }
            r6 = 1
            java.lang.Boolean r2 = java.lang.Boolean.valueOf(r6)     // Catch:{ Exception -> 0x00a9, all -> 0x00a6 }
            if (r4 == 0) goto L_0x00af
            r4.close()     // Catch:{ IOException -> 0x0094 }
            r3 = r4
        L_0x0064:
            boolean r6 = r2.booleanValue()
            return r6
        L_0x0069:
            r6 = move-exception
        L_0x006a:
            monitor-exit(r7)     // Catch:{ all -> 0x0069 }
            throw r6     // Catch:{ Exception -> 0x006c }
        L_0x006c:
            r0 = move-exception
        L_0x006d:
            java.lang.String r6 = "HA-MetricsPersistence"
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ all -> 0x009a }
            r7.<init>()     // Catch:{ all -> 0x009a }
            java.lang.String r8 = "Failed to save data with exception: "
            java.lang.StringBuilder r7 = r7.append(r8)     // Catch:{ all -> 0x009a }
            java.lang.String r8 = r0.toString()     // Catch:{ all -> 0x009a }
            java.lang.StringBuilder r7 = r7.append(r8)     // Catch:{ all -> 0x009a }
            java.lang.String r7 = r7.toString()     // Catch:{ all -> 0x009a }
            net.hockeyapp.android.utils.HockeyLog.warn((java.lang.String) r6, (java.lang.String) r7)     // Catch:{ all -> 0x009a }
            if (r3 == 0) goto L_0x0064
            r3.close()     // Catch:{ IOException -> 0x008f }
            goto L_0x0064
        L_0x008f:
            r0 = move-exception
            r0.printStackTrace()
            goto L_0x0064
        L_0x0094:
            r0 = move-exception
            r0.printStackTrace()
            r3 = r4
            goto L_0x0064
        L_0x009a:
            r6 = move-exception
        L_0x009b:
            if (r3 == 0) goto L_0x00a0
            r3.close()     // Catch:{ IOException -> 0x00a1 }
        L_0x00a0:
            throw r6
        L_0x00a1:
            r0 = move-exception
            r0.printStackTrace()
            goto L_0x00a0
        L_0x00a6:
            r6 = move-exception
            r3 = r4
            goto L_0x009b
        L_0x00a9:
            r0 = move-exception
            r3 = r4
            goto L_0x006d
        L_0x00ac:
            r6 = move-exception
            r3 = r4
            goto L_0x006a
        L_0x00af:
            r3 = r4
            goto L_0x0064
        */
        throw new UnsupportedOperationException("Method not decompiled: net.hockeyapp.android.metrics.Persistence.writeToDisk(java.lang.String):boolean");
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x0052, code lost:
        if (r5 == null) goto L_0x004c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:?, code lost:
        r5.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x0058, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x0059, code lost:
        net.hockeyapp.android.utils.HockeyLog.warn(TAG, "Error closing stream." + r2.getMessage());
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.String load(java.io.File r12) {
        /*
            r11 = this;
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            if (r12 == 0) goto L_0x004c
            r4 = 0
            java.lang.Object r8 = LOCK     // Catch:{ Exception -> 0x002a }
            monitor-enter(r8)     // Catch:{ Exception -> 0x002a }
            java.io.FileInputStream r3 = new java.io.FileInputStream     // Catch:{ all -> 0x00b9 }
            r3.<init>(r12)     // Catch:{ all -> 0x00b9 }
            java.io.InputStreamReader r6 = new java.io.InputStreamReader     // Catch:{ all -> 0x00b9 }
            r6.<init>(r3)     // Catch:{ all -> 0x00b9 }
            java.io.BufferedReader r5 = new java.io.BufferedReader     // Catch:{ all -> 0x00b9 }
            r5.<init>(r6)     // Catch:{ all -> 0x00b9 }
        L_0x001a:
            int r1 = r5.read()     // Catch:{ all -> 0x0026 }
            r7 = -1
            if (r1 == r7) goto L_0x0051
            char r7 = (char) r1     // Catch:{ all -> 0x0026 }
            r0.append(r7)     // Catch:{ all -> 0x0026 }
            goto L_0x001a
        L_0x0026:
            r7 = move-exception
            r4 = r5
        L_0x0028:
            monitor-exit(r8)     // Catch:{ all -> 0x00b9 }
            throw r7     // Catch:{ Exception -> 0x002a }
        L_0x002a:
            r2 = move-exception
            java.lang.String r7 = "HA-MetricsPersistence"
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ all -> 0x0094 }
            r8.<init>()     // Catch:{ all -> 0x0094 }
            java.lang.String r9 = "Error reading telemetry data from file with exception message "
            java.lang.StringBuilder r8 = r8.append(r9)     // Catch:{ all -> 0x0094 }
            java.lang.String r9 = r2.getMessage()     // Catch:{ all -> 0x0094 }
            java.lang.StringBuilder r8 = r8.append(r9)     // Catch:{ all -> 0x0094 }
            java.lang.String r8 = r8.toString()     // Catch:{ all -> 0x0094 }
            net.hockeyapp.android.utils.HockeyLog.warn((java.lang.String) r7, (java.lang.String) r8)     // Catch:{ all -> 0x0094 }
            if (r4 == 0) goto L_0x004c
            r4.close()     // Catch:{ IOException -> 0x0076 }
        L_0x004c:
            java.lang.String r7 = r0.toString()
            return r7
        L_0x0051:
            monitor-exit(r8)     // Catch:{ all -> 0x0026 }
            if (r5 == 0) goto L_0x004c
            r5.close()     // Catch:{ IOException -> 0x0058 }
            goto L_0x004c
        L_0x0058:
            r2 = move-exception
            java.lang.String r7 = "HA-MetricsPersistence"
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            java.lang.String r9 = "Error closing stream."
            java.lang.StringBuilder r8 = r8.append(r9)
            java.lang.String r9 = r2.getMessage()
            java.lang.StringBuilder r8 = r8.append(r9)
            java.lang.String r8 = r8.toString()
            net.hockeyapp.android.utils.HockeyLog.warn((java.lang.String) r7, (java.lang.String) r8)
            goto L_0x004c
        L_0x0076:
            r2 = move-exception
            java.lang.String r7 = "HA-MetricsPersistence"
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            java.lang.String r9 = "Error closing stream."
            java.lang.StringBuilder r8 = r8.append(r9)
            java.lang.String r9 = r2.getMessage()
            java.lang.StringBuilder r8 = r8.append(r9)
            java.lang.String r8 = r8.toString()
            net.hockeyapp.android.utils.HockeyLog.warn((java.lang.String) r7, (java.lang.String) r8)
            goto L_0x004c
        L_0x0094:
            r7 = move-exception
            if (r4 == 0) goto L_0x009a
            r4.close()     // Catch:{ IOException -> 0x009b }
        L_0x009a:
            throw r7
        L_0x009b:
            r2 = move-exception
            java.lang.String r8 = "HA-MetricsPersistence"
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r10 = "Error closing stream."
            java.lang.StringBuilder r9 = r9.append(r10)
            java.lang.String r10 = r2.getMessage()
            java.lang.StringBuilder r9 = r9.append(r10)
            java.lang.String r9 = r9.toString()
            net.hockeyapp.android.utils.HockeyLog.warn((java.lang.String) r8, (java.lang.String) r9)
            goto L_0x009a
        L_0x00b9:
            r7 = move-exception
            goto L_0x0028
        */
        throw new UnsupportedOperationException("Method not decompiled: net.hockeyapp.android.metrics.Persistence.load(java.io.File):java.lang.String");
    }

    /* access modifiers changed from: protected */
    public boolean hasFilesAvailable() {
        return nextAvailableFileInDirectory() != null;
    }

    /* access modifiers changed from: protected */
    public File nextAvailableFileInDirectory() {
        File file;
        File[] files;
        synchronized (LOCK) {
            if (this.mTelemetryDirectory != null && (files = this.mTelemetryDirectory.listFiles()) != null && files.length > 0) {
                int i = 0;
                while (true) {
                    if (i > files.length - 1) {
                        break;
                    }
                    file = files[i];
                    if (!this.mServedFiles.contains(file)) {
                        HockeyLog.info(TAG, "The directory " + file.toString() + " (ADDING TO SERVED AND RETURN)");
                        this.mServedFiles.add(file);
                        break;
                    }
                    HockeyLog.info(TAG, "The directory " + file.toString() + " (WAS ALREADY SERVED)");
                    i++;
                }
            }
            if (this.mTelemetryDirectory != null) {
                HockeyLog.info(TAG, "The directory " + this.mTelemetryDirectory.toString() + " did not contain any " + "unserved files");
            }
            file = null;
        }
        return file;
    }

    /* access modifiers changed from: protected */
    public void deleteFile(File file) {
        if (file != null) {
            synchronized (LOCK) {
                if (!file.delete()) {
                    HockeyLog.warn(TAG, "Error deleting telemetry file " + file.toString());
                } else {
                    HockeyLog.warn(TAG, "Successfully deleted telemetry file at: " + file.toString());
                    this.mServedFiles.remove(file);
                }
            }
            return;
        }
        HockeyLog.warn(TAG, "Couldn't delete file, the reference to the file was null");
    }

    /* access modifiers changed from: protected */
    public void makeAvailable(File file) {
        synchronized (LOCK) {
            if (file != null) {
                this.mServedFiles.remove(file);
            }
        }
    }

    /* access modifiers changed from: protected */
    public boolean isFreeSpaceAvailable() {
        boolean z = false;
        synchronized (LOCK) {
            Context context = getContext();
            if (context.getFilesDir() != null) {
                String path = context.getFilesDir().getAbsolutePath() + BIT_TELEMETRY_DIRECTORY;
                if (!TextUtils.isEmpty(path)) {
                    if (new File(path).listFiles().length < MAX_FILE_COUNT.intValue()) {
                        z = true;
                    }
                }
            }
        }
        return z;
    }

    /* access modifiers changed from: protected */
    public void createDirectoriesIfNecessary() {
        if (this.mTelemetryDirectory != null && !this.mTelemetryDirectory.exists()) {
            if (this.mTelemetryDirectory.mkdirs()) {
                HockeyLog.info(TAG, "Successfully created directory");
            } else {
                HockeyLog.info(TAG, "Error creating directory");
            }
        }
    }

    private Context getContext() {
        if (this.mWeakContext != null) {
            return (Context) this.mWeakContext.get();
        }
        return null;
    }

    /* access modifiers changed from: protected */
    public Sender getSender() {
        if (this.mWeakSender != null) {
            return (Sender) this.mWeakSender.get();
        }
        return null;
    }

    /* access modifiers changed from: protected */
    public void setSender(Sender sender) {
        this.mWeakSender = new WeakReference<>(sender);
    }
}
