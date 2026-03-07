package com.microsoft.onlineid.internal.sso.client.request;

import android.app.PendingIntent;
import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import com.microsoft.onlineid.OnlineIdConfiguration;
import com.microsoft.onlineid.SignInOptions;
import com.microsoft.onlineid.exception.AuthenticationException;
import com.microsoft.onlineid.internal.sso.BundleMarshaller;

public class GetSignInIntentRequest extends SingleSsoRequest<PendingIntent> {
    private final OnlineIdConfiguration _onlineIdConfiguration;
    private final SignInOptions _signInOptions;

    public GetSignInIntentRequest(Context applicationContext, Bundle state, SignInOptions signInOptions, OnlineIdConfiguration onlineIdConfiguration) {
        super(applicationContext, state);
        this._signInOptions = signInOptions;
        this._onlineIdConfiguration = onlineIdConfiguration;
    }

    public PendingIntent performRequestTask() throws RemoteException, AuthenticationException {
        Bundle params = getDefaultCallingParams();
        if (this._signInOptions != null) {
            params.putAll(this._signInOptions.asBundle());
        }
        if (this._onlineIdConfiguration != null) {
            params.putAll(BundleMarshaller.onlineIdConfigurationToBundle(this._onlineIdConfiguration));
        }
        Bundle bundle = this._msaSsoService.getSignInIntent(params);
        SingleSsoRequest.checkForErrors(bundle);
        return BundleMarshaller.pendingIntentFromBundle(bundle);
    }
}
