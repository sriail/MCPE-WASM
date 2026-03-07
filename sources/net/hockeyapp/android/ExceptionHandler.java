package net.hockeyapp.android;

import android.os.Process;
import android.text.TextUtils;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread;
import java.util.Date;
import java.util.UUID;
import net.hockeyapp.android.objects.CrashDetails;
import net.hockeyapp.android.utils.HockeyLog;

public class ExceptionHandler implements Thread.UncaughtExceptionHandler {
    private CrashManagerListener mCrashManagerListener;
    private Thread.UncaughtExceptionHandler mDefaultExceptionHandler;
    private boolean mIgnoreDefaultHandler = false;

    public ExceptionHandler(Thread.UncaughtExceptionHandler defaultExceptionHandler, CrashManagerListener listener, boolean ignoreDefaultHandler) {
        this.mDefaultExceptionHandler = defaultExceptionHandler;
        this.mIgnoreDefaultHandler = ignoreDefaultHandler;
        this.mCrashManagerListener = listener;
    }

    public void setListener(CrashManagerListener listener) {
        this.mCrashManagerListener = listener;
    }

    @Deprecated
    public static void saveException(Throwable exception, CrashManagerListener listener) {
        saveException(exception, (Thread) null, listener);
    }

    public static void saveException(Throwable exception, Thread thread, CrashManagerListener listener) {
        Date now = new Date();
        Date startDate = new Date(CrashManager.getInitializeTimestamp());
        exception.printStackTrace(new PrintWriter(new StringWriter()));
        String filename = UUID.randomUUID().toString();
        CrashDetails crashDetails = new CrashDetails(filename, exception);
        crashDetails.setAppPackage(Constants.APP_PACKAGE);
        crashDetails.setAppVersionCode(Constants.APP_VERSION);
        crashDetails.setAppVersionName(Constants.APP_VERSION_NAME);
        crashDetails.setAppStartDate(startDate);
        crashDetails.setAppCrashDate(now);
        if (listener == null || listener.includeDeviceData()) {
            crashDetails.setOsVersion(Constants.ANDROID_VERSION);
            crashDetails.setOsBuild(Constants.ANDROID_BUILD);
            crashDetails.setDeviceManufacturer(Constants.PHONE_MANUFACTURER);
            crashDetails.setDeviceModel(Constants.PHONE_MODEL);
        }
        if (thread != null && (listener == null || listener.includeThreadDetails())) {
            crashDetails.setThreadName(thread.getName() + "-" + thread.getId());
        }
        if (Constants.CRASH_IDENTIFIER != null && (listener == null || listener.includeDeviceIdentifier())) {
            crashDetails.setReporterKey(Constants.CRASH_IDENTIFIER);
        }
        crashDetails.writeCrashReport();
        if (listener != null) {
            try {
                writeValueToFile(limitedString(listener.getUserID()), filename + ".user");
                writeValueToFile(limitedString(listener.getContact()), filename + ".contact");
                writeValueToFile(listener.getDescription(), filename + ".description");
            } catch (IOException e) {
                HockeyLog.error("Error saving crash meta data!", (Throwable) e);
            }
        }
    }

    public static void saveNativeException(Throwable exception, String managedExceptionString, Thread thread, CrashManagerListener listener) {
        String[] splits;
        if (!TextUtils.isEmpty(managedExceptionString) && (splits = managedExceptionString.split("--- End of managed exception stack trace ---", 2)) != null && splits.length > 0) {
            managedExceptionString = splits[0];
        }
        saveXamarinException(exception, thread, managedExceptionString, false, listener);
    }

    public static void saveManagedException(Throwable exception, Thread thread, CrashManagerListener listener) {
        saveXamarinException(exception, thread, (String) null, true, listener);
    }

    private static void saveXamarinException(Throwable exception, Thread thread, String additionalManagedException, Boolean isManagedException, CrashManagerListener listener) {
        Date startDate = new Date(CrashManager.getInitializeTimestamp());
        String filename = UUID.randomUUID().toString();
        Date now = new Date();
        PrintWriter printWriter = new PrintWriter(new StringWriter());
        if (exception != null) {
            exception.printStackTrace(printWriter);
        }
        CrashDetails crashDetails = new CrashDetails(filename, exception, additionalManagedException, isManagedException);
        crashDetails.setAppPackage(Constants.APP_PACKAGE);
        crashDetails.setAppVersionCode(Constants.APP_VERSION);
        crashDetails.setAppVersionName(Constants.APP_VERSION_NAME);
        crashDetails.setAppStartDate(startDate);
        crashDetails.setAppCrashDate(now);
        if (listener == null || listener.includeDeviceData()) {
            crashDetails.setOsVersion(Constants.ANDROID_VERSION);
            crashDetails.setOsBuild(Constants.ANDROID_BUILD);
            crashDetails.setDeviceManufacturer(Constants.PHONE_MANUFACTURER);
            crashDetails.setDeviceModel(Constants.PHONE_MODEL);
        }
        if (thread != null && (listener == null || listener.includeThreadDetails())) {
            crashDetails.setThreadName(thread.getName() + "-" + thread.getId());
        }
        if (Constants.CRASH_IDENTIFIER != null && (listener == null || listener.includeDeviceIdentifier())) {
            crashDetails.setReporterKey(Constants.CRASH_IDENTIFIER);
        }
        crashDetails.writeCrashReport();
        if (listener != null) {
            try {
                writeValueToFile(limitedString(listener.getUserID()), filename + ".user");
                writeValueToFile(limitedString(listener.getContact()), filename + ".contact");
                writeValueToFile(listener.getDescription(), filename + ".description");
            } catch (IOException e) {
                HockeyLog.error("Error saving crash meta data!", (Throwable) e);
            }
        }
    }

    public void uncaughtException(Thread thread, Throwable exception) {
        if (Constants.FILES_PATH == null) {
            this.mDefaultExceptionHandler.uncaughtException(thread, exception);
            return;
        }
        saveException(exception, thread, this.mCrashManagerListener);
        if (!this.mIgnoreDefaultHandler) {
            this.mDefaultExceptionHandler.uncaughtException(thread, exception);
            return;
        }
        Process.killProcess(Process.myPid());
        System.exit(10);
    }

    /* JADX WARNING: Removed duplicated region for block: B:16:0x0047  */
    /* JADX WARNING: Removed duplicated region for block: B:19:0x004e  */
    /* JADX WARNING: Removed duplicated region for block: B:28:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static void writeValueToFile(java.lang.String r5, java.lang.String r6) throws java.io.IOException {
        /*
            boolean r3 = android.text.TextUtils.isEmpty(r5)
            if (r3 == 0) goto L_0x0007
        L_0x0006:
            return
        L_0x0007:
            r1 = 0
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x0044, all -> 0x004b }
            r3.<init>()     // Catch:{ IOException -> 0x0044, all -> 0x004b }
            java.lang.String r4 = net.hockeyapp.android.Constants.FILES_PATH     // Catch:{ IOException -> 0x0044, all -> 0x004b }
            java.lang.StringBuilder r3 = r3.append(r4)     // Catch:{ IOException -> 0x0044, all -> 0x004b }
            java.lang.String r4 = "/"
            java.lang.StringBuilder r3 = r3.append(r4)     // Catch:{ IOException -> 0x0044, all -> 0x004b }
            java.lang.StringBuilder r3 = r3.append(r6)     // Catch:{ IOException -> 0x0044, all -> 0x004b }
            java.lang.String r0 = r3.toString()     // Catch:{ IOException -> 0x0044, all -> 0x004b }
            boolean r3 = android.text.TextUtils.isEmpty(r5)     // Catch:{ IOException -> 0x0044, all -> 0x004b }
            if (r3 != 0) goto L_0x003e
            int r3 = android.text.TextUtils.getTrimmedLength(r5)     // Catch:{ IOException -> 0x0044, all -> 0x004b }
            if (r3 <= 0) goto L_0x003e
            java.io.BufferedWriter r2 = new java.io.BufferedWriter     // Catch:{ IOException -> 0x0044, all -> 0x004b }
            java.io.FileWriter r3 = new java.io.FileWriter     // Catch:{ IOException -> 0x0044, all -> 0x004b }
            r3.<init>(r0)     // Catch:{ IOException -> 0x0044, all -> 0x004b }
            r2.<init>(r3)     // Catch:{ IOException -> 0x0044, all -> 0x004b }
            r2.write(r5)     // Catch:{ IOException -> 0x0055, all -> 0x0052 }
            r2.flush()     // Catch:{ IOException -> 0x0055, all -> 0x0052 }
            r1 = r2
        L_0x003e:
            if (r1 == 0) goto L_0x0006
            r1.close()
            goto L_0x0006
        L_0x0044:
            r3 = move-exception
        L_0x0045:
            if (r1 == 0) goto L_0x0006
            r1.close()
            goto L_0x0006
        L_0x004b:
            r3 = move-exception
        L_0x004c:
            if (r1 == 0) goto L_0x0051
            r1.close()
        L_0x0051:
            throw r3
        L_0x0052:
            r3 = move-exception
            r1 = r2
            goto L_0x004c
        L_0x0055:
            r3 = move-exception
            r1 = r2
            goto L_0x0045
        */
        throw new UnsupportedOperationException("Method not decompiled: net.hockeyapp.android.ExceptionHandler.writeValueToFile(java.lang.String, java.lang.String):void");
    }

    private static String limitedString(String string) {
        if (TextUtils.isEmpty(string) || string.length() <= 255) {
            return string;
        }
        return string.substring(0, 255);
    }
}
