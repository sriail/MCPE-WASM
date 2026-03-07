package com.microsoft.cll.android;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import com.microsoft.onlineid.sts.request.AbstractStsRequest;
import java.util.Locale;

public class AndroidPartA extends PartA {
    private final String DeviceTypePC = "Android.PC";
    private final String DeviceTypePhone = "Android.Phone";
    protected final String TAG = "AndroidCll-AndroidPartA";
    protected Context appContext;

    public AndroidPartA(ILogger logger, String iKey, Context context, CorrelationVector correlationVector) {
        super(logger, iKey, correlationVector);
        this.appContext = context;
        PopulateConstantValues();
    }

    /* access modifiers changed from: protected */
    @SuppressLint({"MissingPermission"})
    public void setUserId() {
        if (this.appContext != null) {
            try {
                Account[] accounts = AccountManager.get(this.appContext).getAccountsByType("com.google");
                if (accounts.length > 0) {
                    this.userExt.setLocalId("g:" + HashStringSha256(accounts[0].name));
                    return;
                }
            } catch (SecurityException e) {
                this.logger.info("AndroidCll-AndroidPartA", "Get_Accounts permission was not provided. UserID will be blank");
            }
        }
        this.userExt.setLocalId("");
    }

    /* access modifiers changed from: protected */
    public void setOs() {
        this.osName = AbstractStsRequest.DeviceType;
    }

    /* access modifiers changed from: protected */
    @SuppressLint({"MissingPermission"})
    public void setDeviceInfo() {
        this.deviceExt.setLocalId("");
        try {
            if (this.appContext != null && this.uniqueId == null) {
                this.uniqueId = Settings.Secure.getString(this.appContext.getContentResolver(), "android_id");
                if (this.uniqueId == null) {
                    this.uniqueId = ((WifiManager) this.appContext.getSystemService("wifi")).getConnectionInfo().getMacAddress().replace(":", "");
                    this.deviceExt.setLocalId("m:" + this.uniqueId);
                } else {
                    this.deviceExt.setLocalId("a:" + this.uniqueId);
                }
            }
        } catch (SecurityException e) {
            this.logger.info("AndroidCll-AndroidPartA", "Access Wifi State permission was not Provided. DeviceID will be blank");
        }
        if (testRadioVersion()) {
            this.deviceExt.setDeviceClass("Android.Phone");
        } else {
            DisplayMetrics dm = new DisplayMetrics();
            ((WindowManager) this.appContext.getSystemService("window")).getDefaultDisplay().getMetrics(dm);
            if (getDeviceScreenSize(dm.heightPixels, dm.widthPixels, dm.densityDpi) >= 8.0d) {
                this.deviceExt.setDeviceClass("Android.PC");
            } else {
                this.deviceExt.setDeviceClass("Android.Phone");
            }
        }
        this.osVer = String.format("%s", new Object[]{Build.VERSION.RELEASE});
        this.osExt.setLocale(Locale.getDefault().toString().replaceAll("_", "-"));
    }

    @TargetApi(14)
    private boolean testRadioVersion() {
        if (Build.VERSION.SDK_INT < 14 || Build.getRadioVersion() == null) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: protected */
    public void setAppInfo() {
        try {
            PackageInfo info = this.appContext.getPackageManager().getPackageInfo(this.appContext.getPackageName(), 0);
            this.appVer = info.versionName;
            this.appId = "A:" + info.packageName;
        } catch (PackageManager.NameNotFoundException e) {
            this.logger.error("AndroidCll-AndroidPartA", "Could not get package name");
        }
    }

    /* access modifiers changed from: package-private */
    public double getDeviceScreenSize(int height, int width, int density) {
        return Math.sqrt(Math.pow(((double) width) / ((double) density), 2.0d) + Math.pow(((double) height) / ((double) density), 2.0d));
    }

    /* access modifiers changed from: protected */
    public void PopulateConstantValues() {
        setDeviceInfo();
        setUserId();
        setAppInfo();
        setOs();
    }
}
