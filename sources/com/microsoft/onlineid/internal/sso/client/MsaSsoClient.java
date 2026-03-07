package com.microsoft.onlineid.internal.sso.client;

import android.app.PendingIntent;
import android.content.Context;
import android.os.Bundle;
import com.microsoft.onlineid.ISecurityScope;
import com.microsoft.onlineid.OnlineIdConfiguration;
import com.microsoft.onlineid.SignInOptions;
import com.microsoft.onlineid.SignUpOptions;
import com.microsoft.onlineid.Ticket;
import com.microsoft.onlineid.exception.AuthenticationException;
import com.microsoft.onlineid.internal.log.Logger;
import com.microsoft.onlineid.internal.sso.client.request.GetAccountByIdRequest;
import com.microsoft.onlineid.internal.sso.client.request.GetAccountPickerIntentRequest;
import com.microsoft.onlineid.internal.sso.client.request.GetAccountRequest;
import com.microsoft.onlineid.internal.sso.client.request.GetAllAccountsRequest;
import com.microsoft.onlineid.internal.sso.client.request.GetSignInIntentRequest;
import com.microsoft.onlineid.internal.sso.client.request.GetSignOutIntentRequest;
import com.microsoft.onlineid.internal.sso.client.request.GetSignUpIntentRequest;
import com.microsoft.onlineid.internal.sso.client.request.GetTicketRequest;
import com.microsoft.onlineid.internal.sso.client.request.SingleSsoRequest;
import com.microsoft.onlineid.sts.AuthenticatorUserAccount;
import com.microsoft.onlineid.sts.ConfigManager;
import com.microsoft.onlineid.sts.ServerConfig;
import java.util.ArrayList;
import java.util.Set;

public class MsaSsoClient {
    private final Context _applicationContext;
    private final ServerConfig _config;
    private final ConfigManager _configManager;
    private final MigrationManager _migrationManager;
    private final ServiceFinder _serviceFinder;

    public MsaSsoClient(Context applicationContext) {
        this._applicationContext = applicationContext;
        this._config = new ServerConfig(applicationContext);
        this._serviceFinder = new ServiceFinder(applicationContext);
        this._configManager = new ConfigManager(applicationContext);
        this._migrationManager = new MigrationManager(applicationContext);
    }

    public SsoResponse<AuthenticatorUserAccount> getAccount(OnlineIdConfiguration onlineIdConfiguration, Bundle state) throws AuthenticationException {
        return (SsoResponse) performRequestWithFallback(new GetAccountRequest(this._applicationContext, state, onlineIdConfiguration));
    }

    public AuthenticatorUserAccount getAccountById(String cid, Bundle state) throws AuthenticationException {
        return (AuthenticatorUserAccount) performRequestWithFallback(new GetAccountByIdRequest(this._applicationContext, state, cid));
    }

    public Set<AuthenticatorUserAccount> getAllAccounts(Bundle state) throws AuthenticationException {
        return (Set) performRequestWithFallback(new GetAllAccountsRequest(this._applicationContext, state));
    }

    public PendingIntent getSignInIntent(SignInOptions signInOptions, OnlineIdConfiguration onlineIdConfiguration, Bundle state) throws AuthenticationException {
        return (PendingIntent) performRequestWithFallback(new GetSignInIntentRequest(this._applicationContext, state, signInOptions, onlineIdConfiguration));
    }

    public PendingIntent getSignUpIntent(SignUpOptions signUpOptions, OnlineIdConfiguration onlineIdConfiguration, Bundle state) throws AuthenticationException {
        return (PendingIntent) performRequestWithFallback(new GetSignUpIntentRequest(this._applicationContext, state, signUpOptions, onlineIdConfiguration));
    }

    public PendingIntent getSignOutIntent(String cid, Bundle state) throws AuthenticationException {
        return (PendingIntent) performRequestWithFallback(new GetSignOutIntentRequest(this._applicationContext, state, cid));
    }

    public PendingIntent getAccountPickerIntent(ArrayList<String> cidExclusionList, OnlineIdConfiguration onlineIdConfiguration, Bundle state) throws AuthenticationException {
        return (PendingIntent) performRequestWithFallback(new GetAccountPickerIntentRequest(this._applicationContext, state, cidExclusionList, onlineIdConfiguration));
    }

    public SsoResponse<Ticket> getTicket(String cid, ISecurityScope securityScope, OnlineIdConfiguration onlineIdConfiguration, Bundle state) throws AuthenticationException {
        return (SsoResponse) performRequestWithFallback(new GetTicketRequest(this._applicationContext, state, cid, securityScope, onlineIdConfiguration));
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x00b6, code lost:
        r11 = r11 + 1;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public <T> T performRequestWithFallback(com.microsoft.onlineid.internal.sso.client.request.SingleSsoRequest<T> r18) throws com.microsoft.onlineid.exception.AuthenticationException {
        /*
            r17 = this;
            r0 = r17
            com.microsoft.onlineid.sts.ConfigManager r12 = r0._configManager
            r12.updateIfFirstDownloadNeeded()
            r0 = r17
            com.microsoft.onlineid.internal.sso.client.MigrationManager r12 = r0._migrationManager
            r12.migrateAndUpgradeStorageIfNeeded()
            r0 = r17
            com.microsoft.onlineid.sts.ServerConfig r12 = r0._config
            com.microsoft.onlineid.sts.ServerConfig$Int r13 = com.microsoft.onlineid.sts.ServerConfig.Int.MaxTriesForSsoRequestWithFallback
            int r7 = r12.getInt(r13)
            r12 = 1
            if (r7 >= r12) goto L_0x003d
            java.lang.StringBuilder r12 = new java.lang.StringBuilder
            r12.<init>()
            java.lang.String r13 = "Invalid MaxTriesForSsoRequestWithFallback: "
            java.lang.StringBuilder r12 = r12.append(r13)
            java.lang.StringBuilder r12 = r12.append(r7)
            java.lang.String r2 = r12.toString()
            com.microsoft.onlineid.internal.log.Logger.error((java.lang.String) r2)
            com.microsoft.onlineid.analytics.IClientAnalytics r12 = com.microsoft.onlineid.analytics.ClientAnalytics.get()
            java.lang.String r13 = "SDK"
            java.lang.String r14 = "SSO fallback"
            r12.logEvent(r13, r14, r2)
            r7 = 1
        L_0x003d:
            r11 = 0
            r5 = 0
            r0 = r17
            com.microsoft.onlineid.internal.sso.client.ServiceFinder r12 = r0._serviceFinder
            java.util.List r12 = r12.getOrderedSsoServices()
            java.util.Iterator r6 = r12.iterator()
            boolean r12 = r6.hasNext()
            if (r12 == 0) goto L_0x0063
            java.lang.Object r12 = r6.next()
            com.microsoft.onlineid.internal.sso.SsoService r12 = (com.microsoft.onlineid.internal.sso.SsoService) r12
            r10 = r12
        L_0x0058:
            if (r11 >= r7) goto L_0x011e
            if (r10 == 0) goto L_0x011e
            r0 = r18
            java.lang.Object r12 = r0.performRequest(r10)     // Catch:{ MasterRedirectException -> 0x0065, ServiceBindingException -> 0x00bb, ClientNotAuthorizedException -> 0x00cc, UnsupportedClientVersionException -> 0x00d2, ClientConfigUpdateNeededException -> 0x00d8 }
        L_0x0062:
            return r12
        L_0x0063:
            r10 = 0
            goto L_0x0058
        L_0x0065:
            r1 = move-exception
            java.lang.String r9 = r1.getRedirectRequestTo()
            java.lang.StringBuilder r12 = new java.lang.StringBuilder
            r12.<init>()
            java.lang.String r13 = "Redirect to: "
            java.lang.StringBuilder r12 = r12.append(r13)
            java.lang.StringBuilder r12 = r12.append(r9)
            java.lang.String r8 = r12.toString()
            com.microsoft.onlineid.internal.log.Logger.info(r8, r1)
            com.microsoft.onlineid.analytics.IClientAnalytics r12 = com.microsoft.onlineid.analytics.ClientAnalytics.get()
            java.lang.String r13 = "SDK"
            java.lang.String r14 = "SSO fallback"
            r12.logEvent(r13, r14, r8)
            r0 = r17
            com.microsoft.onlineid.internal.sso.client.ServiceFinder r12 = r0._serviceFinder
            com.microsoft.onlineid.internal.sso.SsoService r10 = r12.getSsoService(r9)
            if (r10 != 0) goto L_0x00b6
            java.lang.String r3 = "Cannot find redirected master"
            java.lang.String r12 = "Cannot find redirected master"
            com.microsoft.onlineid.internal.log.Logger.error(r12, r1)
            com.microsoft.onlineid.analytics.IClientAnalytics r12 = com.microsoft.onlineid.analytics.ClientAnalytics.get()
            java.lang.String r13 = "SDK"
            java.lang.String r14 = "SSO fallback"
            java.lang.String r15 = "Cannot find redirected master"
            r12.logEvent(r13, r14, r15)
            boolean r12 = r6.hasNext()
            if (r12 == 0) goto L_0x00b9
            java.lang.Object r12 = r6.next()
            com.microsoft.onlineid.internal.sso.SsoService r12 = (com.microsoft.onlineid.internal.sso.SsoService) r12
            r10 = r12
        L_0x00b6:
            int r11 = r11 + 1
            goto L_0x0058
        L_0x00b9:
            r10 = 0
            goto L_0x00b6
        L_0x00bb:
            r1 = move-exception
            boolean r12 = r6.hasNext()
            if (r12 == 0) goto L_0x00ca
            java.lang.Object r12 = r6.next()
            com.microsoft.onlineid.internal.sso.SsoService r12 = (com.microsoft.onlineid.internal.sso.SsoService) r12
            r10 = r12
        L_0x00c9:
            goto L_0x00b6
        L_0x00ca:
            r10 = 0
            goto L_0x00c9
        L_0x00cc:
            r1 = move-exception
            java.lang.Object r12 = r17.performRequestWithSelf(r18)
            goto L_0x0062
        L_0x00d2:
            r1 = move-exception
            java.lang.Object r12 = r17.performRequestWithSelf(r18)
            goto L_0x0062
        L_0x00d8:
            r1 = move-exception
            java.lang.StringBuilder r12 = new java.lang.StringBuilder
            r12.<init>()
            java.lang.String r13 = "Client needs config update: "
            java.lang.StringBuilder r12 = r12.append(r13)
            java.lang.String r13 = r1.getMessage()
            java.lang.StringBuilder r12 = r12.append(r13)
            java.lang.String r12 = r12.toString()
            com.microsoft.onlineid.internal.log.Logger.info((java.lang.String) r12)
            r0 = r17
            com.microsoft.onlineid.sts.ConfigManager r12 = r0._configManager
            boolean r12 = r12.update()
            if (r12 == 0) goto L_0x00b6
            r0 = r17
            com.microsoft.onlineid.internal.sso.client.ServiceFinder r12 = r0._serviceFinder
            java.util.List r12 = r12.getOrderedSsoServices()
            java.util.Iterator r6 = r12.iterator()
            boolean r12 = r6.hasNext()
            if (r12 == 0) goto L_0x011c
            java.lang.Object r12 = r6.next()
            com.microsoft.onlineid.internal.sso.SsoService r12 = (com.microsoft.onlineid.internal.sso.SsoService) r12
            r10 = r12
        L_0x0116:
            if (r5 != 0) goto L_0x011a
            int r11 = r11 + -1
        L_0x011a:
            r5 = 1
            goto L_0x00b6
        L_0x011c:
            r10 = 0
            goto L_0x0116
        L_0x011e:
            java.util.Locale r12 = java.util.Locale.US
            java.lang.String r13 = "SSO request failed after %d tries"
            r14 = 1
            java.lang.Object[] r14 = new java.lang.Object[r14]
            r15 = 0
            java.lang.Integer r16 = java.lang.Integer.valueOf(r11)
            r14[r15] = r16
            java.lang.String r4 = java.lang.String.format(r12, r13, r14)
            com.microsoft.onlineid.internal.log.Logger.error((java.lang.String) r4)
            com.microsoft.onlineid.analytics.IClientAnalytics r12 = com.microsoft.onlineid.analytics.ClientAnalytics.get()
            java.lang.String r13 = "SDK"
            java.lang.String r14 = "SSO fallback"
            r12.logEvent(r13, r14, r4)
            java.lang.Object r12 = r17.performRequestWithSelf(r18)
            goto L_0x0062
        */
        throw new UnsupportedOperationException("Method not decompiled: com.microsoft.onlineid.internal.sso.client.MsaSsoClient.performRequestWithFallback(com.microsoft.onlineid.internal.sso.client.request.SingleSsoRequest):java.lang.Object");
    }

    private <T> T performRequestWithSelf(SingleSsoRequest<T> request) throws AuthenticationException {
        Logger.info("Attempting to self-service request.");
        return request.performRequest(this._serviceFinder.getSelfSsoService());
    }
}
