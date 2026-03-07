package com.microsoft.onlineid.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import com.microsoft.onlineid.RequestOptions;
import com.microsoft.onlineid.SignInOptions;
import com.microsoft.onlineid.SignUpOptions;
import com.microsoft.onlineid.analytics.ClientAnalytics;
import com.microsoft.onlineid.exception.InternalException;
import com.microsoft.onlineid.exception.NetworkException;
import com.microsoft.onlineid.internal.ActivityResultSender;
import com.microsoft.onlineid.internal.ApiRequest;
import com.microsoft.onlineid.internal.ApiResult;
import com.microsoft.onlineid.internal.Applications;
import com.microsoft.onlineid.internal.Assertion;
import com.microsoft.onlineid.internal.Intents;
import com.microsoft.onlineid.internal.NetworkConnectivity;
import com.microsoft.onlineid.internal.PackageInfoHelper;
import com.microsoft.onlineid.internal.Resources;
import com.microsoft.onlineid.internal.Uris;
import com.microsoft.onlineid.internal.log.Logger;
import com.microsoft.onlineid.internal.sso.BundleMarshaller;
import com.microsoft.onlineid.internal.sso.client.BackupService;
import com.microsoft.onlineid.internal.storage.TypedStorage;
import com.microsoft.onlineid.internal.ui.WebFlowActivity;
import com.microsoft.onlineid.internal.ui.WebFlowTelemetryData;
import com.microsoft.onlineid.sts.AuthenticatorUserAccount;
import com.microsoft.onlineid.sts.ServerConfig;

public class AddAccountActivity extends Activity {
    protected static final int AccountAddedRequest = 2;
    public static final String ActionAddAccount = "com.microsoft.onlineid.internal.ADD_ACCOUNT";
    public static final String ActionSignUpAccount = "com.microsoft.onlineid.internal.SIGN_UP_ACCOUNT";
    protected static final int AddPendingRequest = 1;
    private static final String AppIdLabel = "client_id";
    private static final String ClientFlightLabel = "client_flight";
    public static final String CobrandingIdLabel = "cobrandid";
    protected static final int NoRequest = -1;
    public static final String PlatformLabel = "platform";
    public static final String PlatformName = "android";
    private static final String PrefillUsernameLabel = "username";
    public static final String SignInOptionsLabel = (AddAccountActivity.class.getName() + ".SignInOptions");
    protected static final int SignInWebFlowRequest = 0;
    public static final String SignUpFlowLabel = "fl";
    public static final String SignUpOptionsLabel = (AddAccountActivity.class.getName() + ".SignUpOptions");
    private static final String UnauthenticatedSessionIdLabel = "uaid";
    private static final String WReplyLabel = "wreply";
    protected String _accountPuid;
    protected Handler _handler;
    protected int _pendingChildRequest = -1;
    private ActivityResultSender _resultSender;
    protected TypedStorage _typedStorage;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        Bundle options;
        Uri startUri;
        super.onCreate(savedInstanceState);
        ServerConfig serverConfig = new ServerConfig(getApplicationContext());
        String clientPackageName = getIntent().getStringExtra(BundleMarshaller.ClientPackageNameKey);
        boolean isCallerMsa = PackageInfoHelper.isAuthenticatorApp(clientPackageName);
        boolean precachingEnabled = false;
        this._resultSender = new ActivityResultSender(this, ActivityResultSender.ResultType.Account);
        String action = getIntent().getAction();
        if (ActionSignUpAccount.equals(action)) {
            Bundle options2 = getIntent().getBundleExtra(SignUpOptionsLabel);
            if (options2 != null) {
                precachingEnabled = new SignUpOptions(options2).getWasPrecachingEnabled();
            }
        } else if (ActionAddAccount.equals(action) && (options = getIntent().getBundleExtra(SignInOptionsLabel)) != null) {
            precachingEnabled = new SignInOptions(options).getWasPrecachingEnabled();
        }
        boolean webTelemetryRequested = getIntent().getBooleanExtra(BundleMarshaller.WebFlowTelemetryRequestedKey, false);
        if (ActionSignUpAccount.equals(action)) {
            startUri = getSignupUri(serverConfig, isCallerMsa);
        } else {
            startUri = getLoginUri(serverConfig, isCallerMsa, false);
        }
        Intent intent = WebFlowActivity.getFlowRequest(getApplicationContext(), startUri, ActionSignUpAccount.equals(action) ? WebFlowActivity.ActionSignUp : WebFlowActivity.ActionSignIn, isCallerMsa, new WebFlowTelemetryData().setIsWebTelemetryRequested(webTelemetryRequested).setCallingAppPackageName(clientPackageName).setCallingAppVersionName(PackageInfoHelper.getAppVersionName(getApplicationContext(), clientPackageName)).setWasPrecachingEnabled(precachingEnabled)).asIntent();
        intent.addFlags(65536);
        this._pendingChildRequest = 0;
        if (!NetworkConnectivity.hasInternetConnectivity(getApplicationContext())) {
            ClientAnalytics.get().logEvent(ClientAnalytics.PerformanceCategory, ClientAnalytics.NoNetworkConnectivity, ClientAnalytics.AtStartOfWebFlow);
            sendFailureResult((Exception) new NetworkException());
            return;
        }
        startActivityForResult(intent, 0);
        this._handler = new Handler();
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == this._pendingChildRequest) {
            this._pendingChildRequest = -1;
        }
        switch (requestCode) {
            case 0:
                addTelemetryToResult(data);
                switch (resultCode) {
                    case -1:
                        if (data == null || data.getExtras() == null) {
                            sendFailureResult("Sign in flow finished successfully with no extras set.");
                            return;
                        } else {
                            onSetupSuccessful(new ApiResult(data.getExtras()).getAccountPuid());
                            return;
                        }
                    case 0:
                        sendCancelledResult();
                        return;
                    case 1:
                        sendFailureResult(new ApiResult(data.getExtras()).getException());
                        return;
                    default:
                        sendFailureResult("Sign in activity finished with unexpected result code: " + resultCode);
                        return;
                }
            case 1:
                return;
            case 2:
                switch (resultCode) {
                    case -1:
                    case 0:
                        sendSuccessResult(this._accountPuid);
                        return;
                    default:
                        sendFailureResult("Account added activity finished with unexpected result code: " + resultCode);
                        return;
                }
            default:
                Logger.error("Received activity result for unknown request code: " + requestCode);
                sendFailureResult("Received activity result for unknown request code: " + requestCode);
                return;
        }
    }

    /* access modifiers changed from: protected */
    public Uri getLoginUri(ServerConfig serverConfig, boolean isCallerMsa, boolean isWreply) {
        ServerConfig.Endpoint endpoint;
        if (isCallerMsa) {
            endpoint = isWreply ? ServerConfig.Endpoint.SignupWReplyMsa : ServerConfig.Endpoint.ConnectMsa;
        } else {
            endpoint = isWreply ? ServerConfig.Endpoint.SignupWReplyPartner : ServerConfig.Endpoint.ConnectPartner;
        }
        Uri.Builder uriBuilder = Uri.parse(serverConfig.getUrl(endpoint).toExternalForm()).buildUpon();
        addCommonQueryStringParams(uriBuilder);
        Bundle options = getIntent().getBundleExtra(SignInOptionsLabel);
        if (options != null) {
            appendOptions(new SignInOptions(options), uriBuilder);
        }
        if (isWreply) {
            return Uris.appendMarketQueryString(getApplicationContext(), uriBuilder.build());
        }
        return uriBuilder.build();
    }

    /* access modifiers changed from: protected */
    public Uri getSignupUri(ServerConfig serverConfig, boolean isCallerMsa) {
        Uri.Builder uriBuilder = Uri.parse(serverConfig.getUrl(isCallerMsa ? ServerConfig.Endpoint.SignupMsa : ServerConfig.Endpoint.SignupPartner).toExternalForm()).buildUpon();
        addCommonQueryStringParams(uriBuilder);
        Bundle options = getIntent().getBundleExtra(SignUpOptionsLabel);
        if (options != null) {
            appendOptions(new SignUpOptions(options), uriBuilder);
        }
        String signUpFlow = getIntent().getStringExtra(SignUpFlowLabel);
        if (signUpFlow != null) {
            uriBuilder.appendQueryParameter(SignUpFlowLabel, signUpFlow);
        }
        uriBuilder.appendQueryParameter(WReplyLabel, getLoginUri(serverConfig, isCallerMsa, true).toString());
        return uriBuilder.build();
    }

    /* access modifiers changed from: protected */
    public void addCommonQueryStringParams(Uri.Builder uriBuilder) {
        uriBuilder.appendQueryParameter(PlatformLabel, PlatformName + Resources.getSdkVersion(getApplicationContext()));
        uriBuilder.appendQueryParameter("client_id", Applications.buildClientAppUri(getApplicationContext(), getIntent().getStringExtra(BundleMarshaller.ClientPackageNameKey)));
        String cobrandingId = getIntent().getStringExtra(BundleMarshaller.CobrandingIdKey);
        if (cobrandingId != null) {
            uriBuilder.appendQueryParameter(CobrandingIdLabel, cobrandingId);
        }
    }

    /* access modifiers changed from: protected */
    public void addTelemetryToResult(Intent data) {
        if (data != null && data.getExtras() != null) {
            ApiResult result = new ApiResult(data.getExtras());
            if (result.hasWebFlowTelemetryEvents()) {
                this._resultSender.putWebFlowTelemetryFields(result).set();
            }
        }
    }

    /* access modifiers changed from: protected */
    public void sendSuccessResult(String accountPuid) {
        Assertion.check(accountPuid != null);
        ApiRequest request = new ApiRequest(getApplicationContext(), getIntent());
        if (request.hasResultReceiver()) {
            request.sendSuccess(new ApiResult().setAccountPuid(accountPuid));
        } else {
            AuthenticatorUserAccount account = new TypedStorage(getApplicationContext()).readAccount(accountPuid);
            if (account == null) {
                sendFailureResult((Exception) new InternalException("AddAccountActivity could not acquire newly added account."));
                return;
            }
            this._resultSender.putLimitedUserAccount(account).set();
        }
        finish();
    }

    /* access modifiers changed from: protected */
    public void sendFailureResult(Exception exception) {
        Assertion.check(exception != null);
        Logger.error("Failed to add account.", exception);
        ClientAnalytics.get().logException(exception);
        ApiRequest request = new ApiRequest(getApplicationContext(), getIntent());
        if (request.hasResultReceiver()) {
            request.sendFailure(exception);
        } else {
            this._resultSender.putException(exception).set();
        }
        finish();
    }

    /* access modifiers changed from: protected */
    public void sendFailureResult(String message) {
        sendFailureResult((Exception) new InternalException(message));
    }

    /* access modifiers changed from: protected */
    public void sendCancelledResult() {
        ApiRequest request = new ApiRequest(getApplicationContext(), getIntent());
        if (request.hasResultReceiver()) {
            request.sendUserCanceled();
        }
        finish();
    }

    public void finish() {
        if (this._pendingChildRequest != -1) {
            finishActivity(this._pendingChildRequest);
            this._pendingChildRequest = -1;
        }
        super.finish();
    }

    /* access modifiers changed from: protected */
    public void onSetupSuccessful(String accountPuid) {
        BackupService.pushBackup(getApplicationContext());
        if (!isFinishing()) {
            finishActivity(1);
            sendSuccessResult(accountPuid);
        }
    }

    /* access modifiers changed from: protected */
    public void onSetupFailure(Exception exception) {
        sendFailureResult(exception);
    }

    private void appendOptions(RequestOptions options, Uri.Builder uriBuilder) {
        String username = options.getPrefillUsername();
        if (username != null) {
            uriBuilder.appendQueryParameter(PrefillUsernameLabel, username);
        }
        String uaid = options.getUnauthenticatedSessionId();
        if (uaid != null) {
            uriBuilder.appendQueryParameter(UnauthenticatedSessionIdLabel, uaid);
        }
        String flightConfiguration = options.getFlightConfiguration();
        if (flightConfiguration != null) {
            uriBuilder.appendQueryParameter(ClientFlightLabel, flightConfiguration);
        }
    }

    public static Intent getSignUpIntent(Context applicationContext, SignUpOptions options, String membernameType, String cobrandingId, boolean webTelemetryRequested, String clientPackageName, Bundle clientState) {
        Intent intent = new Intent(applicationContext, AddAccountActivity.class).setAction(ActionSignUpAccount).putExtra(SignUpFlowLabel, membernameType).putExtra(BundleMarshaller.CobrandingIdKey, cobrandingId).putExtra(BundleMarshaller.WebFlowTelemetryRequestedKey, webTelemetryRequested).putExtra(BundleMarshaller.ClientPackageNameKey, clientPackageName).putExtra(BundleMarshaller.ClientStateBundleKey, clientState).setData(new Intents.DataBuilder().add((RequestOptions) options).add(membernameType).add(cobrandingId).add(clientPackageName).build());
        if (options != null) {
            intent.putExtra(SignUpOptionsLabel, options.asBundle());
        }
        return intent;
    }

    public static Intent getSignInIntent(Context applicationContext, SignInOptions options, String membernameType, String cobrandingId, boolean webTelemetryRequested, String clientPackageName, Bundle clientState) {
        Intent intent = new Intent(applicationContext, AddAccountActivity.class).setAction(ActionAddAccount).putExtra(SignUpFlowLabel, membernameType).putExtra(BundleMarshaller.CobrandingIdKey, cobrandingId).putExtra(BundleMarshaller.WebFlowTelemetryRequestedKey, webTelemetryRequested).putExtra(BundleMarshaller.ClientPackageNameKey, clientPackageName).putExtra(BundleMarshaller.ClientStateBundleKey, clientState).setData(new Intents.DataBuilder().add((RequestOptions) options).add(membernameType).add(cobrandingId).add(clientPackageName).build());
        if (options != null) {
            intent.putExtra(SignInOptionsLabel, options.asBundle());
        }
        return intent;
    }
}
