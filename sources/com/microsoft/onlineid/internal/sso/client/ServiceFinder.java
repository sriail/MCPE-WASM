package com.microsoft.onlineid.internal.sso.client;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import com.microsoft.onlineid.analytics.ClientAnalytics;
import com.microsoft.onlineid.internal.PackageInfoHelper;
import com.microsoft.onlineid.internal.log.Logger;
import com.microsoft.onlineid.internal.sso.SignatureVerifier;
import com.microsoft.onlineid.internal.sso.SsoService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ServiceFinder {
    public static final Comparator<SsoService> MasterPrecedenceComparator = new Comparator<SsoService>() {
        public int compare(SsoService service, SsoService otherService) {
            if (service.getPackageName().equals(PackageInfoHelper.AuthenticatorPackageName)) {
                return -1;
            }
            if (otherService.getPackageName().equals(PackageInfoHelper.AuthenticatorPackageName)) {
                return 1;
            }
            int diff = otherService.getSsoVersion() - service.getSsoVersion();
            if (diff == 0) {
                return (int) (service.getFirstInstallTime() - otherService.getFirstInstallTime());
            }
            return diff;
        }
    };
    public static final String SdkVersionMetaDataName = "com.microsoft.msa.service.sdk_version";
    public static final String SsoVersionMetaDataName = "com.microsoft.msa.service.sso_version";
    private final Context _applicationContext;
    private final SignatureVerifier _signatureVerifier;

    public ServiceFinder(Context applicationContext) {
        this._applicationContext = applicationContext;
        this._signatureVerifier = new SignatureVerifier(applicationContext);
    }

    /* access modifiers changed from: protected */
    public List<SsoService> getTrustedSsoServices() {
        List<ResolveInfo> services = this._applicationContext.getPackageManager().queryIntentServices(new Intent(SsoService.SsoServiceIntent), 128);
        List<SsoService> ssoServices = new ArrayList<>();
        for (ResolveInfo info : services) {
            String packageName = info.serviceInfo.applicationInfo.packageName;
            Bundle metadata = info.serviceInfo.metaData;
            int ssoVersion = metadata.getInt(SsoVersionMetaDataName);
            if (!this._signatureVerifier.isTrusted(packageName)) {
                Logger.warning("Disallowing SSO with " + packageName + " because it is not trusted.");
            } else if (ssoVersion <= 1) {
                Logger.warning("Disallowing  SSO with " + packageName + " because its SSO version is " + ssoVersion + ".");
            } else {
                try {
                    ssoServices.add(new SsoService(packageName, ssoVersion, metadata.getString(SdkVersionMetaDataName), getFirstInstallTime(packageName)));
                } catch (PackageManager.NameNotFoundException e) {
                    Logger.error("Could not find package when querying for first install time: " + packageName, e);
                }
            }
        }
        ClientAnalytics.get().logEvent(ClientAnalytics.SdkCategory, ClientAnalytics.TotalTrustedSsoServices, String.valueOf(ssoServices.size()));
        return ssoServices;
    }

    public List<SsoService> getOrderedSsoServices() {
        List<SsoService> orderedSsoServices = getTrustedSsoServices();
        Collections.sort(orderedSsoServices, MasterPrecedenceComparator);
        Logger.info("Available trusted/ordered SSO services: " + Arrays.toString(orderedSsoServices.toArray()));
        return orderedSsoServices;
    }

    /* access modifiers changed from: protected */
    public SsoService getSelfSsoService() {
        return new SsoService(this._applicationContext.getPackageName(), 0, "", 0);
    }

    public SsoService getSsoService(String packageName) {
        if (packageName != null) {
            for (SsoService ssoService : getOrderedSsoServices()) {
                if (ssoService.getPackageName().equalsIgnoreCase(packageName)) {
                    return ssoService;
                }
            }
        }
        return null;
    }

    /* access modifiers changed from: protected */
    public long getFirstInstallTime(String packageName) throws PackageManager.NameNotFoundException {
        return this._applicationContext.getPackageManager().getPackageInfo(packageName, 0).firstInstallTime;
    }
}
