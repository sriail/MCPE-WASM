package net.hockeyapp.android.tasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import com.facebook.internal.ServerProtocol;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Locale;
import net.hockeyapp.android.Constants;
import net.hockeyapp.android.Tracking;
import net.hockeyapp.android.UpdateActivity;
import net.hockeyapp.android.UpdateManagerListener;
import net.hockeyapp.android.utils.HockeyLog;
import net.hockeyapp.android.utils.HttpURLConnectionBuilder;
import net.hockeyapp.android.utils.VersionCache;
import net.hockeyapp.android.utils.VersionHelper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CheckUpdateTask extends AsyncTask<Void, String, JSONArray> {
    protected static final String APK = "apk";
    private static final int MAX_NUMBER_OF_VERSIONS = 25;
    protected String appIdentifier;
    private Context context;
    protected UpdateManagerListener listener;
    protected Boolean mandatory;
    protected String urlString;
    private long usageTime;

    public CheckUpdateTask(WeakReference<? extends Context> weakContext, String urlString2) {
        this(weakContext, urlString2, (String) null);
    }

    public CheckUpdateTask(WeakReference<? extends Context> weakContext, String urlString2, String appIdentifier2) {
        this(weakContext, urlString2, appIdentifier2, (UpdateManagerListener) null);
    }

    public CheckUpdateTask(WeakReference<? extends Context> weakContext, String urlString2, String appIdentifier2, UpdateManagerListener listener2) {
        this.urlString = null;
        this.appIdentifier = null;
        this.context = null;
        this.mandatory = false;
        this.usageTime = 0;
        this.appIdentifier = appIdentifier2;
        this.urlString = urlString2;
        this.listener = listener2;
        Context ctx = weakContext != null ? (Context) weakContext.get() : null;
        if (ctx != null) {
            this.context = ctx.getApplicationContext();
            this.usageTime = Tracking.getUsageTime(ctx);
            Constants.loadFromContext(ctx);
        }
    }

    public void attach(WeakReference<? extends Context> weakContext) {
        Context ctx = null;
        if (weakContext != null) {
            ctx = (Context) weakContext.get();
        }
        if (ctx != null) {
            this.context = ctx.getApplicationContext();
            Constants.loadFromContext(ctx);
        }
    }

    public void detach() {
        this.context = null;
    }

    /* access modifiers changed from: protected */
    public int getVersionCode() {
        return Integer.parseInt(Constants.APP_VERSION);
    }

    /* access modifiers changed from: protected */
    public JSONArray doInBackground(Void... args) {
        try {
            int versionCode = getVersionCode();
            JSONArray json = new JSONArray(VersionCache.getVersionInfo(this.context));
            if (!getCachingEnabled() || !findNewVersion(json, versionCode)) {
                URLConnection connection = createConnection(new URL(getURLString(UpdateActivity.EXTRA_JSON)));
                connection.connect();
                InputStream inputStream = new BufferedInputStream(connection.getInputStream());
                String jsonString = convertStreamToString(inputStream);
                inputStream.close();
                JSONArray json2 = new JSONArray(jsonString);
                if (findNewVersion(json2, versionCode)) {
                    return limitResponseSize(json2);
                }
                return null;
            }
            HockeyLog.verbose("HockeyUpdate", "Returning cached JSON");
            return json;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    /* access modifiers changed from: protected */
    public URLConnection createConnection(URL url) throws IOException {
        URLConnection connection = url.openConnection();
        connection.addRequestProperty("User-Agent", Constants.SDK_USER_AGENT);
        if (Build.VERSION.SDK_INT <= 9) {
            connection.setRequestProperty("connection", "close");
        }
        return connection;
    }

    private boolean findNewVersion(JSONArray json, int versionCode) {
        boolean largerVersionCode;
        boolean newerApkFile;
        boolean minRequirementsMet;
        boolean newerVersionFound = false;
        int index = 0;
        while (index < json.length()) {
            try {
                JSONObject entry = json.getJSONObject(index);
                if (entry.getInt(ServerProtocol.FALLBACK_DIALOG_PARAM_VERSION) > versionCode) {
                    largerVersionCode = true;
                } else {
                    largerVersionCode = false;
                }
                if (entry.getInt(ServerProtocol.FALLBACK_DIALOG_PARAM_VERSION) != versionCode || !VersionHelper.isNewerThanLastUpdateTime(this.context, entry.getLong("timestamp"))) {
                    newerApkFile = false;
                } else {
                    newerApkFile = true;
                }
                if (VersionHelper.compareVersionStrings(entry.getString("minimum_os_version"), VersionHelper.mapGoogleVersion(Build.VERSION.RELEASE)) <= 0) {
                    minRequirementsMet = true;
                } else {
                    minRequirementsMet = false;
                }
                if ((largerVersionCode || newerApkFile) && minRequirementsMet) {
                    if (entry.has("mandatory")) {
                        this.mandatory = Boolean.valueOf(this.mandatory.booleanValue() | entry.getBoolean("mandatory"));
                    }
                    newerVersionFound = true;
                }
                index++;
            } catch (JSONException e) {
                return false;
            }
        }
        return newerVersionFound;
    }

    private JSONArray limitResponseSize(JSONArray json) {
        JSONArray result = new JSONArray();
        for (int index = 0; index < Math.min(json.length(), 25); index++) {
            try {
                result.put(json.get(index));
            } catch (JSONException e) {
            }
        }
        return result;
    }

    /* access modifiers changed from: protected */
    public void onPostExecute(JSONArray updateInfo) {
        if (updateInfo != null) {
            HockeyLog.verbose("HockeyUpdate", "Received Update Info");
            if (this.listener != null) {
                this.listener.onUpdateAvailable(updateInfo, getURLString(APK));
                return;
            }
            return;
        }
        HockeyLog.verbose("HockeyUpdate", "No Update Info available");
        if (this.listener != null) {
            this.listener.onNoUpdateAvailable();
        }
    }

    /* access modifiers changed from: protected */
    public void cleanUp() {
        this.urlString = null;
        this.appIdentifier = null;
    }

    /* access modifiers changed from: protected */
    public String getURLString(String format) {
        StringBuilder builder = new StringBuilder();
        builder.append(this.urlString);
        builder.append("api/2/apps/");
        builder.append(this.appIdentifier != null ? this.appIdentifier : this.context.getPackageName());
        builder.append("?format=" + format);
        if (!TextUtils.isEmpty(Settings.Secure.getString(this.context.getContentResolver(), "android_id"))) {
            builder.append("&udid=" + encodeParam(Settings.Secure.getString(this.context.getContentResolver(), "android_id")));
        }
        SharedPreferences prefs = this.context.getSharedPreferences("net.hockeyapp.android.login", 0);
        String auid = prefs.getString("auid", (String) null);
        if (!TextUtils.isEmpty(auid)) {
            builder.append("&auid=" + encodeParam(auid));
        }
        String iuid = prefs.getString("iuid", (String) null);
        if (!TextUtils.isEmpty(iuid)) {
            builder.append("&iuid=" + encodeParam(iuid));
        }
        builder.append("&os=Android");
        builder.append("&os_version=" + encodeParam(Constants.ANDROID_VERSION));
        builder.append("&device=" + encodeParam(Constants.PHONE_MODEL));
        builder.append("&oem=" + encodeParam(Constants.PHONE_MANUFACTURER));
        builder.append("&app_version=" + encodeParam(Constants.APP_VERSION));
        builder.append("&sdk=" + encodeParam(Constants.SDK_NAME));
        builder.append("&sdk_version=" + encodeParam("4.1.1"));
        builder.append("&lang=" + encodeParam(Locale.getDefault().getLanguage()));
        builder.append("&usage_time=" + this.usageTime);
        return builder.toString();
    }

    private String encodeParam(String param) {
        try {
            return URLEncoder.encode(param, HttpURLConnectionBuilder.DEFAULT_CHARSET);
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    /* access modifiers changed from: protected */
    public boolean getCachingEnabled() {
        return true;
    }

    private static String convertStreamToString(InputStream inputStream) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream), 1024);
        StringBuilder stringBuilder = new StringBuilder();
        while (true) {
            try {
                String line = reader.readLine();
                if (line != null) {
                    stringBuilder.append(line + "\n");
                } else {
                    try {
                        break;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e2) {
                e2.printStackTrace();
                try {
                    inputStream.close();
                } catch (IOException e3) {
                    e3.printStackTrace();
                }
            } catch (Throwable th) {
                try {
                    inputStream.close();
                } catch (IOException e4) {
                    e4.printStackTrace();
                }
                throw th;
            }
        }
        inputStream.close();
        return stringBuilder.toString();
    }
}
