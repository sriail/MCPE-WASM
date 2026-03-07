package com.microsoft.onlineid.internal.sso.client;

import android.content.Context;
import android.os.Bundle;
import com.microsoft.onlineid.analytics.ClientAnalytics;
import com.microsoft.onlineid.internal.Resources;
import com.microsoft.onlineid.internal.log.Logger;
import com.microsoft.onlineid.internal.sso.SsoService;
import com.microsoft.onlineid.internal.sso.client.request.RetrieveBackupRequest;
import com.microsoft.onlineid.internal.storage.TypedStorage;
import java.util.Iterator;
import java.util.List;

public class MigrationManager {
    public static final String InitialSdkVersion = "0";
    private final String _appSdkVersion;
    private final Context _applicationContext;
    private final ServiceFinder _serviceFinder;
    private List<SsoService> _ssoServices;
    private final TypedStorage _typedStorage;

    public MigrationManager(Context applicationContext) {
        this._applicationContext = applicationContext;
        this._typedStorage = new TypedStorage(applicationContext);
        this._serviceFinder = new ServiceFinder(applicationContext);
        this._appSdkVersion = Resources.getSdkVersion(applicationContext);
    }

    public void migrateAndUpgradeStorageIfNeeded() {
        String storageSdkVersion = this._typedStorage.readSdkVersion();
        if (storageSdkVersion == null) {
            this._typedStorage.writeSdkVersion("0");
            this._ssoServices = this._serviceFinder.getOrderedSsoServices();
            if (!this._ssoServices.isEmpty()) {
                migrateStorage();
            }
        }
        if (storageSdkVersion == null || !storageSdkVersion.equals(this._appSdkVersion)) {
            upgradeStorage(storageSdkVersion, this._appSdkVersion);
            this._typedStorage.writeSdkVersion(this._appSdkVersion);
        }
    }

    /* access modifiers changed from: protected */
    public void migrateStorage() {
        String thisAppPackageName = this._applicationContext.getPackageName();
        int migrationAttempts = 0;
        Iterator<SsoService> it = this._ssoServices.iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            SsoService ssoService = it.next();
            String ssoServicePackageName = ssoService.getPackageName();
            if (!ssoServicePackageName.equals(thisAppPackageName)) {
                migrationAttempts++;
                try {
                    Bundle backup = (Bundle) createRetrieveBackupRequest(this._applicationContext).performRequest(ssoService);
                    if (!backup.isEmpty()) {
                        this._typedStorage.storeBackup(backup);
                        Logger.info(thisAppPackageName + " migrated backup data from " + ssoServicePackageName);
                        break;
                    }
                } catch (Exception e) {
                    Logger.error("Encountered an error attempting to migrate storage from " + ssoServicePackageName, e);
                    ClientAnalytics.get().logException(e);
                }
            }
        }
        ClientAnalytics.get().logEvent(ClientAnalytics.MigrationCategory, ClientAnalytics.MigrationAttempts, String.valueOf(migrationAttempts));
    }

    /* access modifiers changed from: protected */
    public void upgradeStorage(String oldVersion, String newVersion) {
    }

    /* access modifiers changed from: protected */
    public RetrieveBackupRequest createRetrieveBackupRequest(Context applicationContext) {
        return new RetrieveBackupRequest(applicationContext);
    }
}
