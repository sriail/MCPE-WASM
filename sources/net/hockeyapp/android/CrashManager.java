package net.hockeyapp.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import com.facebook.internal.ServerProtocol;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.Thread;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.hockeyapp.android.objects.CrashDetails;
import net.hockeyapp.android.objects.CrashManagerUserInput;
import net.hockeyapp.android.objects.CrashMetaData;
import net.hockeyapp.android.utils.HockeyLog;
import net.hockeyapp.android.utils.HttpURLConnectionBuilder;
import net.hockeyapp.android.utils.Util;

public class CrashManager {
    private static final String ALWAYS_SEND_KEY = "always_send_crash_reports";
    private static final int STACK_TRACES_FOUND_CONFIRMED = 2;
    private static final int STACK_TRACES_FOUND_NEW = 1;
    private static final int STACK_TRACES_FOUND_NONE = 0;
    private static boolean didCrashInLastSession = false;
    private static String identifier = null;
    private static long initializeTimestamp;
    /* access modifiers changed from: private */
    public static boolean submitting = false;
    private static String urlString = null;

    public static void register(Context context) {
        String appIdentifier = Util.getAppIdentifier(context);
        if (TextUtils.isEmpty(appIdentifier)) {
            throw new IllegalArgumentException("HockeyApp app identifier was not configured correctly in manifest or build configuration.");
        }
        register(context, appIdentifier);
    }

    public static void register(Context context, String appIdentifier) {
        register(context, Constants.BASE_URL, appIdentifier, (CrashManagerListener) null);
    }

    public static void register(Context context, String appIdentifier, CrashManagerListener listener) {
        register(context, Constants.BASE_URL, appIdentifier, listener);
    }

    public static void register(Context context, String urlString2, String appIdentifier, CrashManagerListener listener) {
        initialize(context, urlString2, appIdentifier, listener, false);
        execute(context, listener);
    }

    public static void initialize(Context context, String appIdentifier, CrashManagerListener listener) {
        initialize(context, Constants.BASE_URL, appIdentifier, listener, true);
    }

    public static void initialize(Context context, String urlString2, String appIdentifier, CrashManagerListener listener) {
        initialize(context, urlString2, appIdentifier, listener, true);
    }

    public static void execute(Context context, CrashManagerListener listener) {
        boolean z;
        boolean z2 = true;
        if (listener == null || !listener.ignoreDefaultHandler()) {
            z = false;
        } else {
            z = true;
        }
        Boolean ignoreDefaultHandler = Boolean.valueOf(z);
        WeakReference<Context> weakContext = new WeakReference<>(context);
        int foundOrSend = hasStackTraces(weakContext);
        if (foundOrSend == 1) {
            didCrashInLastSession = true;
            if (context instanceof Activity) {
                z2 = false;
            }
            Boolean autoSend = Boolean.valueOf(Boolean.valueOf(z2).booleanValue() | PreferenceManager.getDefaultSharedPreferences(context).getBoolean(ALWAYS_SEND_KEY, false));
            if (listener != null) {
                autoSend = Boolean.valueOf(Boolean.valueOf(autoSend.booleanValue() | listener.shouldAutoUploadCrashes()).booleanValue() | listener.onCrashesFound());
                listener.onNewCrashesFound();
            }
            if (!autoSend.booleanValue()) {
                showDialog(weakContext, listener, ignoreDefaultHandler.booleanValue());
            } else {
                sendCrashes(weakContext, listener, ignoreDefaultHandler.booleanValue());
            }
        } else if (foundOrSend == 2) {
            if (listener != null) {
                listener.onConfirmedCrashesFound();
            }
            sendCrashes(weakContext, listener, ignoreDefaultHandler.booleanValue());
        } else {
            registerHandler(weakContext, listener, ignoreDefaultHandler.booleanValue());
        }
    }

    public static int hasStackTraces(WeakReference<Context> weakContext) {
        String[] filenames = searchForStackTraces();
        List<String> confirmedFilenames = null;
        if (filenames == null || filenames.length <= 0) {
            return 0;
        }
        try {
            confirmedFilenames = getConfirmedFilenames(weakContext);
        } catch (Exception e) {
        }
        if (confirmedFilenames == null) {
            return 1;
        }
        for (String filename : filenames) {
            if (!confirmedFilenames.contains(filename)) {
                return 1;
            }
        }
        return 2;
    }

    public static boolean didCrashInLastSession() {
        return didCrashInLastSession;
    }

    public static CrashDetails getLastCrashDetails() {
        if (Constants.FILES_PATH == null || !didCrashInLastSession()) {
            return null;
        }
        long lastModification = 0;
        File lastModifiedFile = null;
        for (File file : new File(Constants.FILES_PATH + "/").listFiles(new FilenameFilter() {
            public boolean accept(File dir, String filename) {
                return filename.endsWith(".stacktrace");
            }
        })) {
            if (file.lastModified() > lastModification) {
                lastModification = file.lastModified();
                lastModifiedFile = file;
            }
        }
        if (lastModifiedFile == null || !lastModifiedFile.exists()) {
            return null;
        }
        try {
            return CrashDetails.fromFile(lastModifiedFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void submitStackTraces(WeakReference<Context> weakContext, CrashManagerListener listener) {
        submitStackTraces(weakContext, listener, (CrashMetaData) null);
    }

    public static void submitStackTraces(WeakReference<Context> weakContext, CrashManagerListener listener, CrashMetaData crashMetaData) {
        String[] list = searchForStackTraces();
        Boolean successful = false;
        if (list != null && list.length > 0) {
            HockeyLog.debug("Found " + list.length + " stacktrace(s).");
            for (int index = 0; index < list.length; index++) {
                HttpURLConnection urlConnection = null;
                try {
                    String filename = list[index];
                    String stacktrace = contentsOfFile(weakContext, filename);
                    if (stacktrace.length() > 0) {
                        HockeyLog.debug("Transmitting crash data: \n" + stacktrace);
                        String userID = contentsOfFile(weakContext, filename.replace(".stacktrace", ".user"));
                        String contact = contentsOfFile(weakContext, filename.replace(".stacktrace", ".contact"));
                        if (crashMetaData != null) {
                            String crashMetaDataUserID = crashMetaData.getUserID();
                            if (!TextUtils.isEmpty(crashMetaDataUserID)) {
                                userID = crashMetaDataUserID;
                            }
                            String crashMetaDataContact = crashMetaData.getUserEmail();
                            if (!TextUtils.isEmpty(crashMetaDataContact)) {
                                contact = crashMetaDataContact;
                            }
                        }
                        String applicationLog = contentsOfFile(weakContext, filename.replace(".stacktrace", ".description"));
                        String description = crashMetaData != null ? crashMetaData.getUserDescription() : "";
                        if (!TextUtils.isEmpty(applicationLog)) {
                            if (!TextUtils.isEmpty(description)) {
                                description = String.format("%s\n\nLog:\n%s", new Object[]{description, applicationLog});
                            } else {
                                description = String.format("Log:\n%s", new Object[]{applicationLog});
                            }
                        }
                        Map<String, String> parameters = new HashMap<>();
                        parameters.put("raw", stacktrace);
                        parameters.put("userID", userID);
                        parameters.put("contact", contact);
                        parameters.put("description", description);
                        parameters.put(ServerProtocol.DIALOG_PARAM_SDK_VERSION, Constants.SDK_NAME);
                        parameters.put("sdk_version", "4.1.1");
                        urlConnection = new HttpURLConnectionBuilder(getURLString()).setRequestMethod("POST").writeFormFields(parameters).build();
                        int responseCode = urlConnection.getResponseCode();
                        successful = Boolean.valueOf(responseCode == 202 || responseCode == 201);
                    }
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                    if (successful.booleanValue()) {
                        HockeyLog.debug("Transmission succeeded");
                        deleteStackTrace(weakContext, list[index]);
                        if (listener != null) {
                            listener.onCrashesSent();
                            deleteRetryCounter(weakContext, list[index], listener.getMaxRetryAttempts());
                        }
                    } else {
                        HockeyLog.debug("Transmission failed, will retry on next register() call");
                        if (listener != null) {
                            listener.onCrashesNotSent();
                            updateRetryCounter(weakContext, list[index], listener.getMaxRetryAttempts());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                    if (successful.booleanValue()) {
                        HockeyLog.debug("Transmission succeeded");
                        deleteStackTrace(weakContext, list[index]);
                        if (listener != null) {
                            listener.onCrashesSent();
                            deleteRetryCounter(weakContext, list[index], listener.getMaxRetryAttempts());
                        }
                    } else {
                        HockeyLog.debug("Transmission failed, will retry on next register() call");
                        if (listener != null) {
                            listener.onCrashesNotSent();
                            updateRetryCounter(weakContext, list[index], listener.getMaxRetryAttempts());
                        }
                    }
                } catch (Throwable th) {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                    if (successful.booleanValue()) {
                        HockeyLog.debug("Transmission succeeded");
                        deleteStackTrace(weakContext, list[index]);
                        if (listener != null) {
                            listener.onCrashesSent();
                            deleteRetryCounter(weakContext, list[index], listener.getMaxRetryAttempts());
                        }
                    } else {
                        HockeyLog.debug("Transmission failed, will retry on next register() call");
                        if (listener != null) {
                            listener.onCrashesNotSent();
                            updateRetryCounter(weakContext, list[index], listener.getMaxRetryAttempts());
                        }
                    }
                    throw th;
                }
            }
        }
    }

    public static void deleteStackTraces(WeakReference<Context> weakContext) {
        String[] list = searchForStackTraces();
        if (list != null && list.length > 0) {
            HockeyLog.debug("Found " + list.length + " stacktrace(s).");
            for (int index = 0; index < list.length; index++) {
                if (weakContext != null) {
                    try {
                        HockeyLog.debug("Delete stacktrace " + list[index] + ".");
                        deleteStackTrace(weakContext, list[index]);
                        Context context = (Context) weakContext.get();
                        if (context != null) {
                            context.deleteFile(list[index]);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static boolean handleUserInput(CrashManagerUserInput userInput, CrashMetaData userProvidedMetaData, CrashManagerListener listener, WeakReference<Context> weakContext, boolean ignoreDefaultHandler) {
        switch (userInput) {
            case CrashManagerUserInputDontSend:
                if (listener != null) {
                    listener.onUserDeniedCrashes();
                }
                deleteStackTraces(weakContext);
                registerHandler(weakContext, listener, ignoreDefaultHandler);
                return true;
            case CrashManagerUserInputAlwaysSend:
                Context context = null;
                if (weakContext != null) {
                    context = (Context) weakContext.get();
                }
                if (context == null) {
                    return false;
                }
                PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(ALWAYS_SEND_KEY, true).apply();
                sendCrashes(weakContext, listener, ignoreDefaultHandler, userProvidedMetaData);
                return true;
            case CrashManagerUserInputSend:
                sendCrashes(weakContext, listener, ignoreDefaultHandler, userProvidedMetaData);
                return true;
            default:
                return false;
        }
    }

    public static void resetAlwaysSend(WeakReference<Context> weakContext) {
        Context context;
        if (weakContext != null && (context = (Context) weakContext.get()) != null) {
            PreferenceManager.getDefaultSharedPreferences(context).edit().remove(ALWAYS_SEND_KEY).apply();
        }
    }

    private static void initialize(Context context, String urlString2, String appIdentifier, CrashManagerListener listener, boolean registerHandler) {
        boolean z = false;
        if (context != null) {
            if (initializeTimestamp == 0) {
                initializeTimestamp = System.currentTimeMillis();
            }
            urlString = urlString2;
            identifier = Util.sanitizeAppIdentifier(appIdentifier);
            didCrashInLastSession = false;
            Constants.loadFromContext(context);
            if (identifier == null) {
                identifier = Constants.APP_PACKAGE;
            }
            if (registerHandler) {
                if (listener != null && listener.ignoreDefaultHandler()) {
                    z = true;
                }
                registerHandler(new WeakReference<>(context), listener, Boolean.valueOf(z).booleanValue());
            }
        }
    }

    private static void showDialog(final WeakReference<Context> weakContext, final CrashManagerListener listener, final boolean ignoreDefaultHandler) {
        Context context = null;
        if (weakContext != null) {
            context = (Context) weakContext.get();
        }
        if (context != null) {
            if (listener == null || !listener.onHandleAlertView()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(getAlertTitle(context));
                builder.setMessage(R.string.hockeyapp_crash_dialog_message);
                builder.setNegativeButton(R.string.hockeyapp_crash_dialog_negative_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        CrashManager.handleUserInput(CrashManagerUserInput.CrashManagerUserInputDontSend, (CrashMetaData) null, listener, weakContext, ignoreDefaultHandler);
                    }
                });
                builder.setNeutralButton(R.string.hockeyapp_crash_dialog_neutral_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        CrashManager.handleUserInput(CrashManagerUserInput.CrashManagerUserInputAlwaysSend, (CrashMetaData) null, listener, weakContext, ignoreDefaultHandler);
                    }
                });
                builder.setPositiveButton(R.string.hockeyapp_crash_dialog_positive_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        CrashManager.handleUserInput(CrashManagerUserInput.CrashManagerUserInputSend, (CrashMetaData) null, listener, weakContext, ignoreDefaultHandler);
                    }
                });
                builder.create().show();
            }
        }
    }

    private static String getAlertTitle(Context context) {
        String appTitle = Util.getAppName(context);
        return String.format(context.getString(R.string.hockeyapp_crash_dialog_title), new Object[]{appTitle});
    }

    private static void sendCrashes(WeakReference<Context> weakContext, CrashManagerListener listener, boolean ignoreDefaultHandler) {
        sendCrashes(weakContext, listener, ignoreDefaultHandler, (CrashMetaData) null);
    }

    private static void sendCrashes(final WeakReference<Context> weakContext, final CrashManagerListener listener, boolean ignoreDefaultHandler, final CrashMetaData crashMetaData) {
        saveConfirmedStackTraces(weakContext);
        registerHandler(weakContext, listener, ignoreDefaultHandler);
        Context ctx = (Context) weakContext.get();
        if ((ctx == null || Util.isConnectedToNetwork(ctx)) && !submitting) {
            submitting = true;
            new Thread() {
                public void run() {
                    CrashManager.submitStackTraces(weakContext, listener, crashMetaData);
                    boolean unused = CrashManager.submitting = false;
                }
            }.start();
        }
    }

    private static void registerHandler(WeakReference<Context> weakReference, CrashManagerListener listener, boolean ignoreDefaultHandler) {
        if (TextUtils.isEmpty(Constants.APP_VERSION) || TextUtils.isEmpty(Constants.APP_PACKAGE)) {
            HockeyLog.debug("Exception handler not set because version or package is null.");
            return;
        }
        Thread.UncaughtExceptionHandler currentHandler = Thread.getDefaultUncaughtExceptionHandler();
        if (currentHandler != null) {
            HockeyLog.debug("Current handler class = " + currentHandler.getClass().getName());
        }
        if (currentHandler instanceof ExceptionHandler) {
            ((ExceptionHandler) currentHandler).setListener(listener);
        } else {
            Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(currentHandler, listener, ignoreDefaultHandler));
        }
    }

    private static String getURLString() {
        return urlString + "api/2/apps/" + identifier + "/crashes/";
    }

    private static void updateRetryCounter(WeakReference<Context> weakContext, String filename, int maxRetryAttempts) {
        Context context;
        if (maxRetryAttempts != -1 && weakContext != null && (context = (Context) weakContext.get()) != null) {
            SharedPreferences preferences = context.getSharedPreferences(Constants.SDK_NAME, 0);
            SharedPreferences.Editor editor = preferences.edit();
            int retryCounter = preferences.getInt("RETRY_COUNT: " + filename, 0);
            if (retryCounter >= maxRetryAttempts) {
                deleteStackTrace(weakContext, filename);
                deleteRetryCounter(weakContext, filename, maxRetryAttempts);
                return;
            }
            editor.putInt("RETRY_COUNT: " + filename, retryCounter + 1);
            editor.apply();
        }
    }

    private static void deleteRetryCounter(WeakReference<Context> weakContext, String filename, int maxRetryAttempts) {
        Context context;
        if (weakContext != null && (context = (Context) weakContext.get()) != null) {
            SharedPreferences.Editor editor = context.getSharedPreferences(Constants.SDK_NAME, 0).edit();
            editor.remove("RETRY_COUNT: " + filename);
            editor.apply();
        }
    }

    private static void deleteStackTrace(WeakReference<Context> weakContext, String filename) {
        Context context;
        if (weakContext != null && (context = (Context) weakContext.get()) != null) {
            context.deleteFile(filename);
            context.deleteFile(filename.replace(".stacktrace", ".user"));
            context.deleteFile(filename.replace(".stacktrace", ".contact"));
            context.deleteFile(filename.replace(".stacktrace", ".description"));
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:15:0x0037 A[SYNTHETIC, Splitter:B:15:0x0037] */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x004f A[SYNTHETIC, Splitter:B:28:0x004f] */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x0058 A[SYNTHETIC, Splitter:B:33:0x0058] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static java.lang.String contentsOfFile(java.lang.ref.WeakReference<android.content.Context> r8, java.lang.String r9) {
        /*
            r1 = 0
            if (r8 == 0) goto L_0x005c
            java.lang.Object r1 = r8.get()
            android.content.Context r1 = (android.content.Context) r1
            if (r1 == 0) goto L_0x005c
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r4 = 0
            java.io.BufferedReader r5 = new java.io.BufferedReader     // Catch:{ FileNotFoundException -> 0x0068, IOException -> 0x0049 }
            java.io.InputStreamReader r6 = new java.io.InputStreamReader     // Catch:{ FileNotFoundException -> 0x0068, IOException -> 0x0049 }
            java.io.FileInputStream r7 = r1.openFileInput(r9)     // Catch:{ FileNotFoundException -> 0x0068, IOException -> 0x0049 }
            r6.<init>(r7)     // Catch:{ FileNotFoundException -> 0x0068, IOException -> 0x0049 }
            r5.<init>(r6)     // Catch:{ FileNotFoundException -> 0x0068, IOException -> 0x0049 }
            r3 = 0
        L_0x0020:
            java.lang.String r3 = r5.readLine()     // Catch:{ FileNotFoundException -> 0x0033, IOException -> 0x0065, all -> 0x0062 }
            if (r3 == 0) goto L_0x003f
            r0.append(r3)     // Catch:{ FileNotFoundException -> 0x0033, IOException -> 0x0065, all -> 0x0062 }
            java.lang.String r6 = "line.separator"
            java.lang.String r6 = java.lang.System.getProperty(r6)     // Catch:{ FileNotFoundException -> 0x0033, IOException -> 0x0065, all -> 0x0062 }
            r0.append(r6)     // Catch:{ FileNotFoundException -> 0x0033, IOException -> 0x0065, all -> 0x0062 }
            goto L_0x0020
        L_0x0033:
            r6 = move-exception
            r4 = r5
        L_0x0035:
            if (r4 == 0) goto L_0x003a
            r4.close()     // Catch:{ IOException -> 0x005e }
        L_0x003a:
            java.lang.String r6 = r0.toString()
        L_0x003e:
            return r6
        L_0x003f:
            if (r5 == 0) goto L_0x006a
            r5.close()     // Catch:{ IOException -> 0x0046 }
            r4 = r5
            goto L_0x003a
        L_0x0046:
            r6 = move-exception
            r4 = r5
            goto L_0x003a
        L_0x0049:
            r2 = move-exception
        L_0x004a:
            r2.printStackTrace()     // Catch:{ all -> 0x0055 }
            if (r4 == 0) goto L_0x003a
            r4.close()     // Catch:{ IOException -> 0x0053 }
            goto L_0x003a
        L_0x0053:
            r6 = move-exception
            goto L_0x003a
        L_0x0055:
            r6 = move-exception
        L_0x0056:
            if (r4 == 0) goto L_0x005b
            r4.close()     // Catch:{ IOException -> 0x0060 }
        L_0x005b:
            throw r6
        L_0x005c:
            r6 = 0
            goto L_0x003e
        L_0x005e:
            r6 = move-exception
            goto L_0x003a
        L_0x0060:
            r7 = move-exception
            goto L_0x005b
        L_0x0062:
            r6 = move-exception
            r4 = r5
            goto L_0x0056
        L_0x0065:
            r2 = move-exception
            r4 = r5
            goto L_0x004a
        L_0x0068:
            r6 = move-exception
            goto L_0x0035
        L_0x006a:
            r4 = r5
            goto L_0x003a
        */
        throw new UnsupportedOperationException("Method not decompiled: net.hockeyapp.android.CrashManager.contentsOfFile(java.lang.ref.WeakReference, java.lang.String):java.lang.String");
    }

    private static void saveConfirmedStackTraces(WeakReference<Context> weakContext) {
        Context context;
        if (weakContext != null && (context = (Context) weakContext.get()) != null) {
            try {
                String[] filenames = searchForStackTraces();
                SharedPreferences.Editor editor = context.getSharedPreferences(Constants.SDK_NAME, 0).edit();
                editor.putString("ConfirmedFilenames", joinArray(filenames, "|"));
                editor.apply();
            } catch (Exception e) {
            }
        }
    }

    private static String joinArray(String[] array, String delimiter) {
        StringBuffer buffer = new StringBuffer();
        for (int index = 0; index < array.length; index++) {
            buffer.append(array[index]);
            if (index < array.length - 1) {
                buffer.append(delimiter);
            }
        }
        return buffer.toString();
    }

    private static String[] searchForStackTraces() {
        if (Constants.FILES_PATH != null) {
            HockeyLog.debug("Looking for exceptions in: " + Constants.FILES_PATH);
            File dir = new File(Constants.FILES_PATH + "/");
            if (dir.mkdir() || dir.exists()) {
                return dir.list(new FilenameFilter() {
                    public boolean accept(File dir, String name) {
                        return name.endsWith(".stacktrace");
                    }
                });
            }
            return new String[0];
        }
        HockeyLog.debug("Can't search for exception as file path is null.");
        return null;
    }

    private static List<String> getConfirmedFilenames(WeakReference<Context> weakContext) {
        Context context;
        if (weakContext == null || (context = (Context) weakContext.get()) == null) {
            return null;
        }
        return Arrays.asList(context.getSharedPreferences(Constants.SDK_NAME, 0).getString("ConfirmedFilenames", "").split("\\|"));
    }

    public static long getInitializeTimestamp() {
        return initializeTimestamp;
    }
}
