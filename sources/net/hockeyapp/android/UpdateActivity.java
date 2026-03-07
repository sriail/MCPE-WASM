package net.hockeyapp.android;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import java.util.Locale;
import net.hockeyapp.android.listeners.DownloadFileListener;
import net.hockeyapp.android.objects.ErrorObject;
import net.hockeyapp.android.tasks.DownloadFileTask;
import net.hockeyapp.android.tasks.GetFileSizeTask;
import net.hockeyapp.android.utils.AsyncTaskUtils;
import net.hockeyapp.android.utils.HockeyLog;
import net.hockeyapp.android.utils.Util;
import net.hockeyapp.android.utils.VersionHelper;

public class UpdateActivity extends Activity implements UpdateActivityInterface, UpdateInfoListener, View.OnClickListener {
    private static final int DIALOG_ERROR_ID = 0;
    public static final String EXTRA_JSON = "json";
    public static final String EXTRA_URL = "url";
    private Context mContext;
    protected DownloadFileTask mDownloadTask;
    /* access modifiers changed from: private */
    public ErrorObject mError;
    protected VersionHelper mVersionHelper;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("App Update");
        setContentView(getLayoutView());
        this.mContext = this;
        this.mVersionHelper = new VersionHelper(this, getIntent().getStringExtra(EXTRA_JSON), this);
        configureView();
        this.mDownloadTask = (DownloadFileTask) getLastNonConfigurationInstance();
        if (this.mDownloadTask != null) {
            this.mDownloadTask.attach(this);
        }
    }

    public Object onRetainNonConfigurationInstance() {
        if (this.mDownloadTask != null) {
            this.mDownloadTask.detach();
        }
        return this.mDownloadTask;
    }

    /* access modifiers changed from: protected */
    public Dialog onCreateDialog(int id) {
        return onCreateDialog(id, (Bundle) null);
    }

    /* access modifiers changed from: protected */
    public Dialog onCreateDialog(int id, Bundle args) {
        switch (id) {
            case 0:
                return new AlertDialog.Builder(this).setMessage("An error has occured").setCancelable(false).setTitle("Error").setIcon(17301543).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ErrorObject unused = UpdateActivity.this.mError = null;
                        dialog.cancel();
                    }
                }).create();
            default:
                return null;
        }
    }

    /* access modifiers changed from: protected */
    public void onPrepareDialog(int id, Dialog dialog) {
        switch (id) {
            case 0:
                AlertDialog messageDialogError = (AlertDialog) dialog;
                if (this.mError != null) {
                    messageDialogError.setMessage(this.mError.getMessage());
                    return;
                } else {
                    messageDialogError.setMessage("An unknown error has occured.");
                    return;
                }
            default:
                return;
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        enableUpdateButton();
        if (permissions.length != 0 && grantResults.length != 0 && requestCode == 1) {
            if (grantResults[0] == 0) {
                prepareDownload();
                return;
            }
            HockeyLog.warn("User denied write permission, can't continue with updater task.");
            UpdateManagerListener listener = UpdateManager.getLastListener();
            if (listener != null) {
                listener.onUpdatePermissionsNotGranted();
                return;
            }
            new AlertDialog.Builder(this.mContext).setTitle(getString(R.string.hockeyapp_permission_update_title)).setMessage(getString(R.string.hockeyapp_permission_update_message)).setNegativeButton(getString(R.string.hockeyapp_permission_dialog_negative_button), (DialogInterface.OnClickListener) null).setPositiveButton(getString(R.string.hockeyapp_permission_dialog_positive_button), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    this.prepareDownload();
                }
            }).create().show();
        }
    }

    public int getCurrentVersionCode() {
        try {
            return getPackageManager().getPackageInfo(getPackageName(), 128).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            return -1;
        }
    }

    @SuppressLint({"InflateParams"})
    public View getLayoutView() {
        return getLayoutInflater().inflate(R.layout.hockeyapp_activity_update, (ViewGroup) null);
    }

    public void onClick(View v) {
        prepareDownload();
        v.setEnabled(false);
    }

    /* access modifiers changed from: protected */
    public void configureView() {
        ((TextView) findViewById(R.id.label_title)).setText(getAppName());
        final TextView versionLabel = (TextView) findViewById(R.id.label_version);
        String versionString = "Version " + this.mVersionHelper.getVersionString();
        final String fileDate = this.mVersionHelper.getFileDateString();
        String appSizeString = "Unknown size";
        long appSize = this.mVersionHelper.getFileSizeBytes();
        if (appSize >= 0) {
            appSizeString = String.format(Locale.US, "%.2f", new Object[]{Float.valueOf(((float) appSize) / 1048576.0f)}) + " MB";
        } else {
            final String str = versionString;
            AsyncTaskUtils.execute(new GetFileSizeTask(this, getIntent().getStringExtra("url"), new DownloadFileListener() {
                public void downloadSuccessful(DownloadFileTask task) {
                    if (task instanceof GetFileSizeTask) {
                        long appSize = ((GetFileSizeTask) task).getSize();
                        versionLabel.setText(UpdateActivity.this.getString(R.string.hockeyapp_update_version_details_label, new Object[]{str, fileDate, String.format(Locale.US, "%.2f", new Object[]{Float.valueOf(((float) appSize) / 1048576.0f)}) + " MB"}));
                    }
                }
            }));
        }
        versionLabel.setText(getString(R.string.hockeyapp_update_version_details_label, new Object[]{versionString, fileDate, appSizeString}));
        ((Button) findViewById(R.id.button_update)).setOnClickListener(this);
        WebView webView = (WebView) findViewById(R.id.web_update_details);
        webView.clearCache(true);
        webView.destroyDrawingCache();
        webView.loadDataWithBaseURL(Constants.BASE_URL, getReleaseNotes(), "text/html", "utf-8", (String) null);
    }

    /* access modifiers changed from: protected */
    public String getReleaseNotes() {
        return this.mVersionHelper.getReleaseNotes(false);
    }

    /* access modifiers changed from: protected */
    public void startDownloadTask() {
        startDownloadTask(getIntent().getStringExtra("url"));
    }

    /* access modifiers changed from: protected */
    public void startDownloadTask(String url) {
        createDownloadTask(url, new DownloadFileListener() {
            public void downloadFailed(DownloadFileTask task, Boolean userWantsRetry) {
                if (userWantsRetry.booleanValue()) {
                    UpdateActivity.this.startDownloadTask();
                } else {
                    UpdateActivity.this.enableUpdateButton();
                }
            }

            public void downloadSuccessful(DownloadFileTask task) {
                UpdateActivity.this.enableUpdateButton();
            }
        });
        AsyncTaskUtils.execute(this.mDownloadTask);
    }

    /* access modifiers changed from: protected */
    public void createDownloadTask(String url, DownloadFileListener listener) {
        this.mDownloadTask = new DownloadFileTask(this, url, listener);
    }

    public void enableUpdateButton() {
        findViewById(R.id.button_update).setEnabled(true);
    }

    public String getAppName() {
        try {
            PackageManager pm = getPackageManager();
            return pm.getApplicationLabel(pm.getApplicationInfo(getPackageName(), 0)).toString();
        } catch (PackageManager.NameNotFoundException e) {
            return "";
        }
    }

    private boolean isWriteExternalStorageSet(Context context) {
        return context.checkCallingOrSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == 0;
    }

    @SuppressLint({"InlinedApi"})
    private boolean isUnknownSourcesChecked() {
        try {
            if (Build.VERSION.SDK_INT < 17 || Build.VERSION.SDK_INT >= 21) {
                if (Settings.Secure.getInt(getContentResolver(), "install_non_market_apps") != 1) {
                    return false;
                }
                return true;
            } else if (Settings.Global.getInt(getContentResolver(), "install_non_market_apps") == 1) {
                return true;
            } else {
                return false;
            }
        } catch (Settings.SettingNotFoundException e) {
            return true;
        }
    }

    /* access modifiers changed from: protected */
    public void prepareDownload() {
        if (!Util.isConnectedToNetwork(this.mContext)) {
            this.mError = new ErrorObject();
            this.mError.setMessage(getString(R.string.hockeyapp_error_no_network_message));
            runOnUiThread(new Runnable() {
                public void run() {
                    UpdateActivity.this.showDialog(0);
                }
            });
        } else if (!isWriteExternalStorageSet(this.mContext)) {
            if (Build.VERSION.SDK_INT >= 23) {
                requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 1);
                return;
            }
            this.mError = new ErrorObject();
            this.mError.setMessage("The permission to access the external storage permission is not set. Please contact the developer.");
            runOnUiThread(new Runnable() {
                public void run() {
                    UpdateActivity.this.showDialog(0);
                }
            });
        } else if (!isUnknownSourcesChecked()) {
            this.mError = new ErrorObject();
            this.mError.setMessage("The installation from unknown sources is not enabled. Please check the device settings.");
            runOnUiThread(new Runnable() {
                public void run() {
                    UpdateActivity.this.showDialog(0);
                }
            });
        } else {
            startDownloadTask();
        }
    }
}
