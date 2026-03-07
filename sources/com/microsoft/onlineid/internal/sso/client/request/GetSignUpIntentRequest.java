package com.microsoft.onlineid.internal.sso.client.request;

import android.app.PendingIntent;
import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import com.microsoft.onlineid.OnlineIdConfiguration;
import com.microsoft.onlineid.SignUpOptions;
import com.microsoft.onlineid.exception.AuthenticationException;
import com.microsoft.onlineid.internal.sso.BundleMarshaller;

public class GetSignUpIntentRequest extends SingleSsoRequest<PendingIntent> {
    private final OnlineIdConfiguration _onlineIdConfiguration;
    private final SignUpOptions _signUpOptions;

    public GetSignUpIntentRequest(Context applicationContext, Bundle state, SignUpOptions signUpOptions, OnlineIdConfiguration onlineIdConfiguration) {
        super(applicationContext, state);
        this._signUpOptions = signUpOptions;
        this._onlineIdConfiguration = onlineIdConfiguration;
    }

    public PendingIntent performRequestTask() throws RemoteException, AuthenticationException {
        Bundle params = getDefaultCallingParams();
        if (this._signUpOptions != null) {
            params.putAll(this._signUpOptions.asBundle());
        }
        if (this._onlineIdConfiguration != null) {
            params.putAll(BundleMarshaller.onlineIdConfigurationToBundle(this._onlineIdConfiguration));
        }
        Bundle bundle = this._msaSsoService.getSignUpIntent(params);
        SingleSsoRequest.checkForErrors(bundle);
        return BundleMarshaller.pendingIntentFromBundle(bundle);
    }
}
