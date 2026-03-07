package com.microsoft.onlineid.internal.log;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Debug;
import android.os.Environment;
import android.os.Process;
import android.support.v4.media.session.PlaybackStateCompat;
import com.facebook.internal.ServerProtocol;
import com.microsoft.onlineid.internal.configuration.Settings;
import com.microsoft.onlineid.sts.AuthenticatorAccountManager;
import com.microsoft.onlineid.sts.AuthenticatorUserAccount;
import java.io.BufferedReader;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.ref.WeakReference;
import java.nio.CharBuffer;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import org.apache.james.mime4j.field.ContentTypeField;

public class ErrorReportManager {
    private static final String ConfirmationTitle = "Report a problem?";
    private static final String CrashReportEmailTitleFormat = "MSA Android Application Crash Report - %s";
    private static final String CrashReportExtension = ".stacktrace";
    private static WeakReference<Context> CurrentActivityContext = null;
    /* access modifiers changed from: private */
    public static Context CurrentAppContext = null;
    private static final String DontAskAgainMessage = "No, don't ask again";
    private static final DateFormat EmailTitleDateFormat = DateFormat.getDateTimeInstance(2, 2, Locale.getDefault());
    private static final String IgnoreCrashReportingStorageKeyName = "isIgnoreCrashReporting";
    private static final int LogCatNumberLines = 5000;
    private static final String ScreenshotFileName = "com.microsoft.msa.authenticator.screenshot.jpg";
    private static final String SendCrashReportConfirmation = "A problem occurred last time you ran this application. Would you like to report it?";
    private static final String SendEmailTo = "WS-MSACLIENT-AFB@microsoft.com";
    private File _contextFilePath;
    private boolean _sendLogs = true;
    private boolean _sendScreenshot = false;

    public ErrorReportManager(Context applicationContext) {
        if (applicationContext != null) {
            init(applicationContext);
        }
    }

    public ErrorReportManager() {
        CurrentAppContext = null;
    }

    public void init(Context applicationContext) {
        try {
            CurrentAppContext = applicationContext;
            if (this._contextFilePath == null && CurrentAppContext != null) {
                this._contextFilePath = CurrentAppContext.getFilesDir();
            }
        } catch (Exception ex) {
            Logger.warning("Error in init: ", ex);
        }
    }

    /* access modifiers changed from: protected */
    public boolean isIgnoreCrashReportingFlagSet() {
        return Settings.getInstance(CurrentAppContext).isSettingEnabled(IgnoreCrashReportingStorageKeyName);
    }

    public void setSendScreenshot(boolean sendScreenshotNewValue) {
        this._sendScreenshot = sendScreenshotNewValue;
    }

    public void setSendLogs(boolean sendLogsNewValue) {
        this._sendLogs = sendLogsNewValue;
    }

    public void generateAndSaveCrashReport(Throwable e) {
        try {
            if (!isIgnoreCrashReportingFlagSet()) {
                if (this._sendScreenshot) {
                    saveScreenshot(CurrentActivityContext);
                }
                PrintWriter printWriter = new PrintWriter(CurrentAppContext.openFileOutput("stack-" + System.currentTimeMillis() + CrashReportExtension, 0));
                constructReport(e, true, (String) null, printWriter);
                printWriter.close();
            }
        } catch (Exception ex) {
            Logger.warning("Error in generateAndSaveCrashReport: ", ex);
        }
    }

    public void generateAndSendReportWithUserPermission(Context activityContext) {
        generateAndSendReportWithUserPermission(activityContext, (String) null);
    }

    public void generateAndSendReportWithUserPermission(Context activityContext, String userFeedback) {
        try {
            CurrentActivityContext = new WeakReference<>(activityContext);
            if (this._sendScreenshot) {
                saveScreenshot(CurrentActivityContext);
            }
            emailLogs(userFeedback);
        } catch (Exception ex) {
            Logger.error("!Error generateAndSendReportWithUserPermission:", ex);
        }
    }

    public void checkAndSendCrashReportWithUserPermission(Context activityContext) {
        try {
            if (!isIgnoreCrashReportingFlagSet()) {
                CurrentActivityContext = new WeakReference<>(activityContext);
                File[] reportFilesList = getCrashErrorFileList();
                if (reportFilesList != null && reportFilesList.length > 0) {
                    askUserPermissionToEmailCrashReport();
                }
            }
        } catch (Exception ex) {
            Logger.error("!Error checkAndSendCrashReportWithUserPermission:", ex);
        }
    }

    /* access modifiers changed from: protected */
    public void constructReport(Throwable e, boolean shouldFilterByPID, String userFeedback, PrintWriter printWriter) {
        if (userFeedback != null) {
            try {
                if (!userFeedback.isEmpty()) {
                    printWriter.append(userFeedback);
                    printWriter.append("\n\n");
                }
            } catch (Exception ex) {
                Logger.error("Exception in constructReport:", ex);
                return;
            }
        }
        AuthenticatorAccountManager accountManager = new AuthenticatorAccountManager(CurrentAppContext);
        if (accountManager.hasAccounts()) {
            for (AuthenticatorUserAccount account : accountManager.getAccounts()) {
                appendValue(printWriter, "PUID", account.getPuid(), false);
                appendValue(printWriter, "Username", account.getUsername(), false);
                appendValue(printWriter, "GcmRegistrationID", account.getGcmRegistrationID(), false);
                printWriter.append("\n");
            }
        }
        printWriter.append(new Date().toString());
        printWriter.append("\n\n");
        getDeviceInfo(printWriter);
        if (e != null) {
            printWriter.append("Stack : \n");
            printWriter.append("-------------------- \n");
            e.printStackTrace(printWriter);
            int depthCounter = 0;
            Throwable cause = e.getCause();
            while (cause != null && depthCounter < 5) {
                printWriter.append("Cause :");
                printWriter.append(String.valueOf(depthCounter));
                printWriter.append("-------------------- \n");
                cause.printStackTrace(printWriter);
                cause = cause.getCause();
                depthCounter++;
            }
        }
        if (this._sendLogs) {
            printWriter.append("-------------------- \n");
            printWriter.append("\nLogcat:\n\n");
            collectLogCatLogs(printWriter, true);
            printWriter.append("\n");
            printWriter.append("-------------------- \n");
        }
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:14:0x0055 A[SYNTHETIC, Splitter:B:14:0x0055] */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x0063 A[SYNTHETIC, Splitter:B:20:0x0063] */
    /* JADX WARNING: Removed duplicated region for block: B:31:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void saveScreenshot(java.lang.ref.WeakReference<android.content.Context> r9) {
        /*
            r8 = this;
            r8.deleteScreenshot()
            r3 = 0
            java.lang.Object r6 = r9.get()     // Catch:{ Exception -> 0x004d }
            android.app.Activity r6 = (android.app.Activity) r6     // Catch:{ Exception -> 0x004d }
            android.view.Window r6 = r6.getWindow()     // Catch:{ Exception -> 0x004d }
            android.view.View r5 = r6.getDecorView()     // Catch:{ Exception -> 0x004d }
            r6 = 1
            r5.setDrawingCacheEnabled(r6)     // Catch:{ Exception -> 0x004d }
            r5.buildDrawingCache()     // Catch:{ Exception -> 0x004d }
            android.graphics.Bitmap r0 = r5.getDrawingCache()     // Catch:{ Exception -> 0x004d }
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x004d }
            r6.<init>()     // Catch:{ Exception -> 0x004d }
            java.io.File r7 = android.os.Environment.getExternalStorageDirectory()     // Catch:{ Exception -> 0x004d }
            java.lang.StringBuilder r6 = r6.append(r7)     // Catch:{ Exception -> 0x004d }
            java.lang.String r7 = java.io.File.separator     // Catch:{ Exception -> 0x004d }
            java.lang.StringBuilder r6 = r6.append(r7)     // Catch:{ Exception -> 0x004d }
            java.lang.String r7 = "com.microsoft.msa.authenticator.screenshot.jpg"
            java.lang.StringBuilder r6 = r6.append(r7)     // Catch:{ Exception -> 0x004d }
            java.lang.String r2 = r6.toString()     // Catch:{ Exception -> 0x004d }
            java.io.FileOutputStream r4 = new java.io.FileOutputStream     // Catch:{ Exception -> 0x004d }
            r4.<init>(r2)     // Catch:{ Exception -> 0x004d }
            android.graphics.Bitmap$CompressFormat r6 = android.graphics.Bitmap.CompressFormat.JPEG     // Catch:{ Exception -> 0x006d, all -> 0x006a }
            r7 = 10
            r0.compress(r6, r7, r4)     // Catch:{ Exception -> 0x006d, all -> 0x006a }
            if (r4 == 0) goto L_0x0070
            r4.close()     // Catch:{ Exception -> 0x0067 }
            r3 = r4
        L_0x004c:
            return
        L_0x004d:
            r1 = move-exception
        L_0x004e:
            java.lang.String r6 = "Exception in saveScreenshot:"
            com.microsoft.onlineid.internal.log.Logger.warning(r6, r1)     // Catch:{ all -> 0x0060 }
            if (r3 == 0) goto L_0x004c
            r3.close()     // Catch:{ Exception -> 0x0059 }
            goto L_0x004c
        L_0x0059:
            r1 = move-exception
        L_0x005a:
            java.lang.String r6 = "Exception in saveScreenshot:"
            com.microsoft.onlineid.internal.log.Logger.warning(r6, r1)
            goto L_0x004c
        L_0x0060:
            r6 = move-exception
        L_0x0061:
            if (r3 == 0) goto L_0x0066
            r3.close()     // Catch:{ Exception -> 0x0059 }
        L_0x0066:
            throw r6     // Catch:{ Exception -> 0x0059 }
        L_0x0067:
            r1 = move-exception
            r3 = r4
            goto L_0x005a
        L_0x006a:
            r6 = move-exception
            r3 = r4
            goto L_0x0061
        L_0x006d:
            r1 = move-exception
            r3 = r4
            goto L_0x004e
        L_0x0070:
            r3 = r4
            goto L_0x004c
        */
        throw new UnsupportedOperationException("Method not decompiled: com.microsoft.onlineid.internal.log.ErrorReportManager.saveScreenshot(java.lang.ref.WeakReference):void");
    }

    /* access modifiers changed from: protected */
    public File getScreenshotFile() {
        File screenshotFile = null;
        try {
            if (this._sendScreenshot) {
                File screenshotFile2 = new File(Environment.getExternalStorageDirectory() + File.separator + ScreenshotFileName);
                try {
                    if (screenshotFile2.exists()) {
                        File file = screenshotFile2;
                        return screenshotFile2;
                    }
                    File file2 = screenshotFile2;
                    return null;
                } catch (Exception e) {
                    e = e;
                    screenshotFile = screenshotFile2;
                }
            }
        } catch (Exception e2) {
            e = e2;
            Logger.warning("Exception in getScreenshotFile:", e);
            return screenshotFile;
        }
        return screenshotFile;
    }

    /* access modifiers changed from: protected */
    public void deleteScreenshot() {
        try {
            File reportFile = getScreenshotFile();
            if (reportFile != null) {
                deleteFileNoThrow(reportFile);
            }
        } catch (Exception e) {
            Logger.warning("Exception in deleteScreenshot", e);
        }
    }

    protected static void collectLogCatLogs(PrintWriter printWriter, boolean shouldFilterByPID) {
        String pidFilter = null;
        if (shouldFilterByPID) {
            try {
                int pid = Process.myPid();
                if (pid > 0) {
                    pidFilter = Integer.toString(pid) + "):";
                }
            } catch (Exception e) {
                Logger.error("Exception in collectLogCat", e);
                return;
            }
        }
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec(new String[]{"logcat", "-t", Integer.toString(LogCatNumberLines), "-v", "time", Logger.getLogTag() + ":*", "*:S"}).getInputStream()));
        while (true) {
            String line = bufferedReader.readLine();
            if (line != null) {
                if (pidFilter == null || line.contains(pidFilter)) {
                    printWriter.append(line);
                    printWriter.append("\n");
                }
            } else {
                return;
            }
        }
    }

    /* access modifiers changed from: protected */
    public void getDeviceInfo(PrintWriter printWriter) {
        try {
            appendValue(printWriter, "Package", CurrentAppContext.getPackageName(), false);
            appendValue(printWriter, "FilePath", this._contextFilePath.getAbsolutePath(), false);
            try {
                appendValue(printWriter, "Version", CurrentAppContext.getPackageManager().getPackageInfo(CurrentAppContext.getPackageName(), 0).versionName, false);
            } catch (Exception e) {
            }
            printWriter.append("\nPackage Data\n");
            appendValue(printWriter, "OS version", Build.VERSION.RELEASE);
            appendValue(printWriter, "SDK level", String.valueOf(Build.VERSION.SDK_INT));
            appendValue(printWriter, "Board", Build.BOARD);
            appendValue(printWriter, "Brand", Build.BRAND);
            appendValue(printWriter, "Phone model", Build.MODEL);
            appendValue(printWriter, "Device", Build.DEVICE);
            appendValue(printWriter, "Display", Build.DISPLAY);
            appendValue(printWriter, "Fingerprint", Build.FINGERPRINT);
            appendValue(printWriter, "Host", Build.HOST);
            appendValue(printWriter, "ID", Build.ID);
            appendValue(printWriter, "Model", Build.MODEL);
            appendValue(printWriter, "Product", Build.PRODUCT);
            appendValue(printWriter, "Tags", Build.TAGS);
            appendValue(printWriter, "Type", String.valueOf(Build.TYPE));
            appendValue(printWriter, "User", String.valueOf(Build.USER));
            appendValue(printWriter, "Locale", Locale.getDefault().toString());
            appendValue(printWriter, "Screen density", String.valueOf(CurrentAppContext.getResources().getDisplayMetrics().density));
            appendValue(printWriter, "Screen size", getScreenSize());
            appendValue(printWriter, "Screen orientation", getOrientation());
            printWriter.append("Internal Memory\n");
            appendValue(printWriter, "Total", String.valueOf(Environment.getDataDirectory().getTotalSpace() / PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID) + "KB");
            appendValue(printWriter, "Available", String.valueOf(Environment.getDataDirectory().getUsableSpace() / PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID) + "KB");
            printWriter.append("Native Memory\n");
            appendValue(printWriter, "Allocated heap size", String.valueOf(Debug.getNativeHeapAllocatedSize() / PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID) + "KB");
            appendValue(printWriter, "Free size", String.valueOf(Debug.getNativeHeapFreeSize() / PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID) + "KB");
            appendValue(printWriter, "Heap size", String.valueOf(Debug.getNativeHeapSize() / PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID) + "KB");
        } catch (Exception e2) {
            Logger.warning("Error in getDeviceInfo: ", e2);
        }
        printWriter.append("\n");
    }

    private void appendValue(PrintWriter writer, String name, String value, boolean indent) {
        String format = "%s : %s\n";
        if (indent) {
            format = "      " + format;
        }
        writer.append(String.format(Locale.US, format, new Object[]{name, value}));
    }

    private void appendValue(PrintWriter writer, String name, String value) {
        appendValue(writer, name, value, true);
    }

    private static String getScreenSize() {
        switch (CurrentAppContext.getResources().getConfiguration().screenLayout & 15) {
            case 1:
                return "Small";
            case 2:
                return "Normal";
            case 3:
                return "Large";
            case 4:
                return "Xlarge";
            default:
                return "Undefined";
        }
    }

    private static String getOrientation() {
        return CurrentAppContext.getResources().getConfiguration().orientation == 1 ? "Portrait" : "Landscape";
    }

    /* access modifiers changed from: protected */
    public File[] getCrashErrorFileList() {
        File[] fileList = new File[0];
        try {
            if (this._contextFilePath == null) {
                return fileList;
            }
            return this._contextFilePath.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String fileName) {
                    return fileName.endsWith(ErrorReportManager.CrashReportExtension);
                }
            });
        } catch (Exception e) {
            Logger.warning("Exception in getCrashErrorFileList", e);
            return fileList;
        }
    }

    /* access modifiers changed from: protected */
    public void notifyUserOfNoMailApp() {
        DialogInterface.OnClickListener closeDialogListener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        };
        AlertDialog.Builder noMailDialogBuilder = new AlertDialog.Builder((Context) CurrentActivityContext.get());
        noMailDialogBuilder.setTitle(getStringResourceIdAtRuntime("send_feedback_no_email_app_header"));
        noMailDialogBuilder.setMessage(getStringResourceIdAtRuntime("send_feedback_no_email_app_body"));
        noMailDialogBuilder.setPositiveButton(getStringResourceIdAtRuntime("popup_button_close"), closeDialogListener);
        noMailDialogBuilder.show();
    }

    private static int getStringResourceIdAtRuntime(String identifier) {
        return CurrentAppContext.getResources().getIdentifier(identifier, "string", CurrentAppContext.getPackageName());
    }

    /* access modifiers changed from: protected */
    public void emailLogs(String userFeedback) {
        Writer result = null;
        PrintWriter printWriter = null;
        String report = "";
        try {
            Writer result2 = new CharArrayWriter();
            try {
                PrintWriter printWriter2 = new PrintWriter(result2);
                try {
                    constructReport((Throwable) null, false, userFeedback, printWriter2);
                    printWriter2.close();
                    report = result2.toString();
                    try {
                        printWriter2.close();
                        result2.close();
                        PrintWriter printWriter3 = printWriter2;
                        Writer writer = result2;
                    } catch (Exception e) {
                        e = e;
                        PrintWriter printWriter4 = printWriter2;
                        Writer writer2 = result2;
                        Logger.warning("Exception in emailLogs", e);
                        return;
                    }
                } catch (Exception e2) {
                    e = e2;
                    printWriter = printWriter2;
                    result = result2;
                } catch (Throwable th) {
                    th = th;
                    printWriter = printWriter2;
                    result = result2;
                    printWriter.close();
                    result.close();
                    throw th;
                }
            } catch (Exception e3) {
                e = e3;
                result = result2;
                try {
                    Logger.warning("Exception in emailLogs", e);
                    printWriter.close();
                    result.close();
                    String subjectTag = CurrentAppContext.getResources().getString(getStringResourceIdAtRuntime("send_feedback_subject_tag"));
                    String subject = EmailTitleDateFormat.format(new Date());
                    subject = subject + " : " + userFeedback.substring(0, Math.min(userFeedback.length(), 50));
                    sendEmail(CurrentActivityContext, report, SendEmailTo, String.format(Locale.US, "[%s] %s", new Object[]{subjectTag, subject}));
                } catch (Throwable th2) {
                    th = th2;
                    printWriter.close();
                    result.close();
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                result = result2;
                printWriter.close();
                result.close();
                throw th;
            }
        } catch (Exception e4) {
            e = e4;
            Logger.warning("Exception in emailLogs", e);
            printWriter.close();
            result.close();
            String subjectTag2 = CurrentAppContext.getResources().getString(getStringResourceIdAtRuntime("send_feedback_subject_tag"));
            String subject2 = EmailTitleDateFormat.format(new Date());
            subject2 = subject2 + " : " + userFeedback.substring(0, Math.min(userFeedback.length(), 50));
            sendEmail(CurrentActivityContext, report, SendEmailTo, String.format(Locale.US, "[%s] %s", new Object[]{subjectTag2, subject2}));
        }
        try {
            String subjectTag22 = CurrentAppContext.getResources().getString(getStringResourceIdAtRuntime("send_feedback_subject_tag"));
            String subject22 = EmailTitleDateFormat.format(new Date());
            if (userFeedback != null && !userFeedback.isEmpty()) {
                subject22 = subject22 + " : " + userFeedback.substring(0, Math.min(userFeedback.length(), 50));
            }
            sendEmail(CurrentActivityContext, report, SendEmailTo, String.format(Locale.US, "[%s] %s", new Object[]{subjectTag22, subject22}));
        } catch (Exception e5) {
            e = e5;
        }
    }

    /* access modifiers changed from: protected */
    public void askUserPermissionToEmailCrashReport() {
        DialogInterface.OnClickListener permissionListener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                ErrorReportManager.this.sendMailAndDeleteFiles(id == -1);
                if (id == -3) {
                    Settings.getInstance(ErrorReportManager.CurrentAppContext).setSetting(ErrorReportManager.IgnoreCrashReportingStorageKeyName, ServerProtocol.DIALOG_RETURN_SCOPES_TRUE);
                }
            }
        };
        AlertDialog.Builder permissionDialogBuilder = new AlertDialog.Builder((Context) CurrentActivityContext.get());
        permissionDialogBuilder.setTitle(ConfirmationTitle);
        permissionDialogBuilder.setMessage(SendCrashReportConfirmation);
        permissionDialogBuilder.setPositiveButton(17039379, permissionListener);
        permissionDialogBuilder.setNegativeButton(17039369, permissionListener);
        permissionDialogBuilder.setNeutralButton(DontAskAgainMessage, permissionListener);
        permissionDialogBuilder.show();
    }

    /* access modifiers changed from: protected */
    public void sendMailAndDeleteFiles(boolean sendMail) {
        File[] reportFilesList = getCrashErrorFileList();
        CharBuffer buffer = null;
        try {
            Arrays.sort(reportFilesList);
            int bufferCapacity = 0;
            for (File file : reportFilesList) {
                if (sendMail) {
                    bufferCapacity = (int) (((long) bufferCapacity) + file.length());
                } else {
                    deleteFileNoThrow(file);
                }
            }
            if (bufferCapacity > 0) {
                buffer = CharBuffer.allocate(bufferCapacity);
                for (File file2 : reportFilesList) {
                    FileReader input = null;
                    try {
                        FileReader input2 = new FileReader(file2.getAbsoluteFile());
                        do {
                            try {
                            } catch (Exception e) {
                                e = e;
                                input = input2;
                                Logger.warning("Error reading the report file", e);
                                input.close();
                                deleteFileNoThrow(file2);
                            }
                        } while (input2.read(buffer) > 0);
                        buffer.flip();
                        input = input2;
                    } catch (Exception e2) {
                        e = e2;
                        Logger.warning("Error reading the report file", e);
                        input.close();
                        deleteFileNoThrow(file2);
                    }
                    try {
                        input.close();
                        deleteFileNoThrow(file2);
                    } catch (Exception e3) {
                        Logger.error("Error closing the report file", e3);
                        deleteFileNoThrow(file2);
                    } catch (Throwable th) {
                        deleteFileNoThrow(file2);
                        throw th;
                    }
                }
            }
            if (sendMail && buffer != null) {
                sendEmail(CurrentActivityContext, buffer.toString(), SendEmailTo, String.format(Locale.US, CrashReportEmailTitleFormat, new Object[]{EmailTitleDateFormat.format(new Date())}));
            }
        } catch (Exception e4) {
            Logger.warning("Error in sendMailAndDeleteFiles: ", e4);
        }
    }

    /* access modifiers changed from: protected */
    public void deleteFileNoThrow(File file) {
        if (file != null) {
            try {
                file.delete();
            } catch (Exception e) {
                Logger.error("deleteFileNoThrow failed", e);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void sendEmail(WeakReference<Context> context, String errorContent, String mailTo, String emailSubject) {
        try {
            File file = getScreenshotFile();
            Intent sendIntent = new Intent("android.intent.action.SEND");
            sendIntent.putExtra("android.intent.extra.EMAIL", new String[]{mailTo});
            sendIntent.putExtra("android.intent.extra.SUBJECT", emailSubject);
            sendIntent.putExtra("android.intent.extra.TEXT", errorContent + "\n");
            sendIntent.setType(ContentTypeField.TYPE_MESSAGE_RFC822);
            if (file != null) {
                sendIntent.putExtra("android.intent.extra.STREAM", Uri.fromFile(file));
            }
            ((Context) context.get()).startActivity(sendIntent);
        } catch (ActivityNotFoundException e) {
            notifyUserOfNoMailApp();
            Logger.warning("ActivityNotFoundException in sendEmail.", e);
        } catch (Exception ex) {
            Logger.warning("Exception in sendEmail.", ex);
        }
    }
}
