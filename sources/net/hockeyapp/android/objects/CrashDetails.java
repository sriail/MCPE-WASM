package net.hockeyapp.android.objects;

import android.text.TextUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import net.hockeyapp.android.utils.HockeyLog;

public class CrashDetails {
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
    private static final String FIELD_APP_CRASH_DATE = "Date";
    private static final String FIELD_APP_PACKAGE = "Package";
    private static final String FIELD_APP_START_DATE = "Start Date";
    private static final String FIELD_APP_VERSION_CODE = "Version Code";
    private static final String FIELD_APP_VERSION_NAME = "Version Name";
    private static final String FIELD_CRASH_REPORTER_KEY = "CrashReporter Key";
    private static final String FIELD_DEVICE_MANUFACTURER = "Manufacturer";
    private static final String FIELD_DEVICE_MODEL = "Model";
    private static final String FIELD_FORMAT = "Format";
    private static final String FIELD_FORMAT_VALUE = "Xamarin";
    private static final String FIELD_OS_BUILD = "Android Build";
    private static final String FIELD_OS_VERSION = "Android";
    private static final String FIELD_THREAD_NAME = "Thread";
    private static final String FIELD_XAMARIN_CAUSED_BY = "Xamarin caused by: ";
    private Date appCrashDate;
    private String appPackage;
    private Date appStartDate;
    private String appVersionCode;
    private String appVersionName;
    private final String crashIdentifier;
    private String deviceManufacturer;
    private String deviceModel;
    private String format;
    private Boolean isXamarinException;
    private String osBuild;
    private String osVersion;
    private String reporterKey;
    private String threadName;
    private String throwableStackTrace;

    public CrashDetails(String crashIdentifier2) {
        this.crashIdentifier = crashIdentifier2;
    }

    public CrashDetails(String crashIdentifier2, Throwable throwable) {
        this(crashIdentifier2);
        this.isXamarinException = false;
        Writer stackTraceResult = new StringWriter();
        throwable.printStackTrace(new PrintWriter(stackTraceResult));
        this.throwableStackTrace = stackTraceResult.toString();
    }

    public CrashDetails(String crashIdentifier2, Throwable throwable, String managedExceptionString, Boolean isManagedException) {
        this(crashIdentifier2);
        Writer stackTraceResult = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stackTraceResult);
        this.isXamarinException = true;
        setFormat(FIELD_FORMAT_VALUE);
        if (isManagedException.booleanValue()) {
            printWriter.print(FIELD_XAMARIN_CAUSED_BY);
            throwable.printStackTrace(printWriter);
        } else if (!TextUtils.isEmpty(managedExceptionString)) {
            throwable.printStackTrace(printWriter);
            printWriter.print(FIELD_XAMARIN_CAUSED_BY);
            printWriter.print(managedExceptionString);
        } else {
            throwable.printStackTrace(printWriter);
        }
        this.throwableStackTrace = stackTraceResult.toString();
    }

    public static CrashDetails fromFile(File file) throws IOException {
        return fromReader(file.getName().substring(0, file.getName().indexOf(".stacktrace")), new FileReader(file));
    }

    public static CrashDetails fromReader(String crashIdentifier2, Reader in) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(in);
        CrashDetails result = new CrashDetails(crashIdentifier2);
        boolean headersProcessed = false;
        StringBuilder stackTraceBuilder = new StringBuilder();
        while (true) {
            String readLine = bufferedReader.readLine();
            if (readLine == null) {
                result.setThrowableStackTrace(stackTraceBuilder.toString());
                return result;
            } else if (headersProcessed) {
                stackTraceBuilder.append(readLine).append("\n");
            } else if (readLine.isEmpty()) {
                headersProcessed = true;
            } else {
                int colonIndex = readLine.indexOf(":");
                if (colonIndex < 0) {
                    HockeyLog.error("Malformed header line when parsing crash details: \"" + readLine + "\"");
                }
                String headerName = readLine.substring(0, colonIndex).trim();
                String headerValue = readLine.substring(colonIndex + 1, readLine.length()).trim();
                if (headerName.equals(FIELD_CRASH_REPORTER_KEY)) {
                    result.setReporterKey(headerValue);
                } else if (headerName.equals(FIELD_APP_START_DATE)) {
                    try {
                        result.setAppStartDate(DATE_FORMAT.parse(headerValue));
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                } else if (headerName.equals("Date")) {
                    try {
                        result.setAppCrashDate(DATE_FORMAT.parse(headerValue));
                    } catch (ParseException e2) {
                        throw new RuntimeException(e2);
                    }
                } else if (headerName.equals("Android")) {
                    result.setOsVersion(headerValue);
                } else if (headerName.equals(FIELD_OS_BUILD)) {
                    result.setOsBuild(headerValue);
                } else if (headerName.equals(FIELD_DEVICE_MANUFACTURER)) {
                    result.setDeviceManufacturer(headerValue);
                } else if (headerName.equals(FIELD_DEVICE_MODEL)) {
                    result.setDeviceModel(headerValue);
                } else if (headerName.equals(FIELD_APP_PACKAGE)) {
                    result.setAppPackage(headerValue);
                } else if (headerName.equals(FIELD_APP_VERSION_NAME)) {
                    result.setAppVersionName(headerValue);
                } else if (headerName.equals(FIELD_APP_VERSION_CODE)) {
                    result.setAppVersionCode(headerValue);
                } else if (headerName.equals(FIELD_THREAD_NAME)) {
                    result.setThreadName(headerValue);
                } else if (headerName.equals(FIELD_FORMAT)) {
                    result.setFormat(headerValue);
                }
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:20:0x00ce A[SYNTHETIC, Splitter:B:20:0x00ce] */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x00dc A[SYNTHETIC, Splitter:B:26:0x00dc] */
    /* JADX WARNING: Removed duplicated region for block: B:37:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void writeCrashReport() {
        /*
            r8 = this;
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = net.hockeyapp.android.Constants.FILES_PATH
            java.lang.StringBuilder r5 = r5.append(r6)
            java.lang.String r6 = "/"
            java.lang.StringBuilder r5 = r5.append(r6)
            java.lang.String r6 = r8.crashIdentifier
            java.lang.StringBuilder r5 = r5.append(r6)
            java.lang.String r6 = ".stacktrace"
            java.lang.StringBuilder r5 = r5.append(r6)
            java.lang.String r2 = r5.toString()
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "Writing unhandled exception to: "
            java.lang.StringBuilder r5 = r5.append(r6)
            java.lang.StringBuilder r5 = r5.append(r2)
            java.lang.String r5 = r5.toString()
            net.hockeyapp.android.utils.HockeyLog.debug(r5)
            r3 = 0
            java.io.BufferedWriter r4 = new java.io.BufferedWriter     // Catch:{ IOException -> 0x00c6 }
            java.io.FileWriter r5 = new java.io.FileWriter     // Catch:{ IOException -> 0x00c6 }
            r5.<init>(r2)     // Catch:{ IOException -> 0x00c6 }
            r4.<init>(r5)     // Catch:{ IOException -> 0x00c6 }
            java.lang.String r5 = "Package"
            java.lang.String r6 = r8.appPackage     // Catch:{ IOException -> 0x00ea, all -> 0x00e7 }
            r8.writeHeader(r4, r5, r6)     // Catch:{ IOException -> 0x00ea, all -> 0x00e7 }
            java.lang.String r5 = "Version Code"
            java.lang.String r6 = r8.appVersionCode     // Catch:{ IOException -> 0x00ea, all -> 0x00e7 }
            r8.writeHeader(r4, r5, r6)     // Catch:{ IOException -> 0x00ea, all -> 0x00e7 }
            java.lang.String r5 = "Version Name"
            java.lang.String r6 = r8.appVersionName     // Catch:{ IOException -> 0x00ea, all -> 0x00e7 }
            r8.writeHeader(r4, r5, r6)     // Catch:{ IOException -> 0x00ea, all -> 0x00e7 }
            java.lang.String r5 = "Android"
            java.lang.String r6 = r8.osVersion     // Catch:{ IOException -> 0x00ea, all -> 0x00e7 }
            r8.writeHeader(r4, r5, r6)     // Catch:{ IOException -> 0x00ea, all -> 0x00e7 }
            java.lang.String r5 = "Android Build"
            java.lang.String r6 = r8.osBuild     // Catch:{ IOException -> 0x00ea, all -> 0x00e7 }
            r8.writeHeader(r4, r5, r6)     // Catch:{ IOException -> 0x00ea, all -> 0x00e7 }
            java.lang.String r5 = "Manufacturer"
            java.lang.String r6 = r8.deviceManufacturer     // Catch:{ IOException -> 0x00ea, all -> 0x00e7 }
            r8.writeHeader(r4, r5, r6)     // Catch:{ IOException -> 0x00ea, all -> 0x00e7 }
            java.lang.String r5 = "Model"
            java.lang.String r6 = r8.deviceModel     // Catch:{ IOException -> 0x00ea, all -> 0x00e7 }
            r8.writeHeader(r4, r5, r6)     // Catch:{ IOException -> 0x00ea, all -> 0x00e7 }
            java.lang.String r5 = "Thread"
            java.lang.String r6 = r8.threadName     // Catch:{ IOException -> 0x00ea, all -> 0x00e7 }
            r8.writeHeader(r4, r5, r6)     // Catch:{ IOException -> 0x00ea, all -> 0x00e7 }
            java.lang.String r5 = "CrashReporter Key"
            java.lang.String r6 = r8.reporterKey     // Catch:{ IOException -> 0x00ea, all -> 0x00e7 }
            r8.writeHeader(r4, r5, r6)     // Catch:{ IOException -> 0x00ea, all -> 0x00e7 }
            java.lang.String r5 = "Start Date"
            java.text.SimpleDateFormat r6 = DATE_FORMAT     // Catch:{ IOException -> 0x00ea, all -> 0x00e7 }
            java.util.Date r7 = r8.appStartDate     // Catch:{ IOException -> 0x00ea, all -> 0x00e7 }
            java.lang.String r6 = r6.format(r7)     // Catch:{ IOException -> 0x00ea, all -> 0x00e7 }
            r8.writeHeader(r4, r5, r6)     // Catch:{ IOException -> 0x00ea, all -> 0x00e7 }
            java.lang.String r5 = "Date"
            java.text.SimpleDateFormat r6 = DATE_FORMAT     // Catch:{ IOException -> 0x00ea, all -> 0x00e7 }
            java.util.Date r7 = r8.appCrashDate     // Catch:{ IOException -> 0x00ea, all -> 0x00e7 }
            java.lang.String r6 = r6.format(r7)     // Catch:{ IOException -> 0x00ea, all -> 0x00e7 }
            r8.writeHeader(r4, r5, r6)     // Catch:{ IOException -> 0x00ea, all -> 0x00e7 }
            java.lang.Boolean r5 = r8.isXamarinException     // Catch:{ IOException -> 0x00ea, all -> 0x00e7 }
            boolean r5 = r5.booleanValue()     // Catch:{ IOException -> 0x00ea, all -> 0x00e7 }
            if (r5 == 0) goto L_0x00aa
            java.lang.String r5 = "Format"
            java.lang.String r6 = "Xamarin"
            r8.writeHeader(r4, r5, r6)     // Catch:{ IOException -> 0x00ea, all -> 0x00e7 }
        L_0x00aa:
            java.lang.String r5 = "\n"
            r4.write(r5)     // Catch:{ IOException -> 0x00ea, all -> 0x00e7 }
            java.lang.String r5 = r8.throwableStackTrace     // Catch:{ IOException -> 0x00ea, all -> 0x00e7 }
            r4.write(r5)     // Catch:{ IOException -> 0x00ea, all -> 0x00e7 }
            r4.flush()     // Catch:{ IOException -> 0x00ea, all -> 0x00e7 }
            if (r4 == 0) goto L_0x00bc
            r4.close()     // Catch:{ IOException -> 0x00be }
        L_0x00bc:
            r3 = r4
        L_0x00bd:
            return
        L_0x00be:
            r1 = move-exception
            java.lang.String r5 = "Error saving crash report!"
            net.hockeyapp.android.utils.HockeyLog.error((java.lang.String) r5, (java.lang.Throwable) r1)
            r3 = r4
            goto L_0x00bd
        L_0x00c6:
            r0 = move-exception
        L_0x00c7:
            java.lang.String r5 = "Error saving crash report!"
            net.hockeyapp.android.utils.HockeyLog.error((java.lang.String) r5, (java.lang.Throwable) r0)     // Catch:{ all -> 0x00d9 }
            if (r3 == 0) goto L_0x00bd
            r3.close()     // Catch:{ IOException -> 0x00d2 }
            goto L_0x00bd
        L_0x00d2:
            r1 = move-exception
            java.lang.String r5 = "Error saving crash report!"
            net.hockeyapp.android.utils.HockeyLog.error((java.lang.String) r5, (java.lang.Throwable) r1)
            goto L_0x00bd
        L_0x00d9:
            r5 = move-exception
        L_0x00da:
            if (r3 == 0) goto L_0x00df
            r3.close()     // Catch:{ IOException -> 0x00e0 }
        L_0x00df:
            throw r5
        L_0x00e0:
            r1 = move-exception
            java.lang.String r6 = "Error saving crash report!"
            net.hockeyapp.android.utils.HockeyLog.error((java.lang.String) r6, (java.lang.Throwable) r1)
            goto L_0x00df
        L_0x00e7:
            r5 = move-exception
            r3 = r4
            goto L_0x00da
        L_0x00ea:
            r0 = move-exception
            r3 = r4
            goto L_0x00c7
        */
        throw new UnsupportedOperationException("Method not decompiled: net.hockeyapp.android.objects.CrashDetails.writeCrashReport():void");
    }

    private void writeHeader(Writer writer, String name, String value) throws IOException {
        writer.write(name + ": " + value + "\n");
    }

    public String getCrashIdentifier() {
        return this.crashIdentifier;
    }

    public String getReporterKey() {
        return this.reporterKey;
    }

    public void setReporterKey(String reporterKey2) {
        this.reporterKey = reporterKey2;
    }

    public Date getAppStartDate() {
        return this.appStartDate;
    }

    public void setAppStartDate(Date appStartDate2) {
        this.appStartDate = appStartDate2;
    }

    public Date getAppCrashDate() {
        return this.appCrashDate;
    }

    public void setAppCrashDate(Date appCrashDate2) {
        this.appCrashDate = appCrashDate2;
    }

    public String getOsVersion() {
        return this.osVersion;
    }

    public void setOsVersion(String osVersion2) {
        this.osVersion = osVersion2;
    }

    public String getOsBuild() {
        return this.osBuild;
    }

    public void setOsBuild(String osBuild2) {
        this.osBuild = osBuild2;
    }

    public String getDeviceManufacturer() {
        return this.deviceManufacturer;
    }

    public void setDeviceManufacturer(String deviceManufacturer2) {
        this.deviceManufacturer = deviceManufacturer2;
    }

    public String getDeviceModel() {
        return this.deviceModel;
    }

    public void setDeviceModel(String deviceModel2) {
        this.deviceModel = deviceModel2;
    }

    public String getAppPackage() {
        return this.appPackage;
    }

    public void setAppPackage(String appPackage2) {
        this.appPackage = appPackage2;
    }

    public String getAppVersionName() {
        return this.appVersionName;
    }

    public void setAppVersionName(String appVersionName2) {
        this.appVersionName = appVersionName2;
    }

    public String getAppVersionCode() {
        return this.appVersionCode;
    }

    public void setAppVersionCode(String appVersionCode2) {
        this.appVersionCode = appVersionCode2;
    }

    public String getThreadName() {
        return this.threadName;
    }

    public void setThreadName(String threadName2) {
        this.threadName = threadName2;
    }

    public String getThrowableStackTrace() {
        return this.throwableStackTrace;
    }

    public void setThrowableStackTrace(String throwableStackTrace2) {
        this.throwableStackTrace = throwableStackTrace2;
    }

    public Boolean getIsXamarinException() {
        return this.isXamarinException;
    }

    public void setIsXamarinException(Boolean isXamarinException2) {
        this.isXamarinException = isXamarinException2;
    }

    public String getFormat() {
        return this.format;
    }

    public void setFormat(String format2) {
        this.format = format2;
    }
}
