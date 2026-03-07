package com.microsoft.onlineid.internal.sso.client.request;

import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import com.microsoft.onlineid.ISecurityScope;
import com.microsoft.onlineid.OnlineIdConfiguration;
import com.microsoft.onlineid.Ticket;
import com.microsoft.onlineid.exception.AuthenticationException;
import com.microsoft.onlineid.internal.sso.BundleMarshaller;
import com.microsoft.onlineid.internal.sso.client.SsoResponse;

public class GetTicketRequest extends SingleSsoRequest<SsoResponse<Ticket>> {
    private final String _cid;
    private final OnlineIdConfiguration _onlineIdConfiguration;
    private final ISecurityScope _securityScope;

    public GetTicketRequest(Context applicationContext, Bundle state, String cid, ISecurityScope securityScope, OnlineIdConfiguration onlineIdConfiguration) {
        super(applicationContext, state);
        this._cid = cid;
        this._securityScope = securityScope;
        this._onlineIdConfiguration = onlineIdConfiguration;
    }

    public SsoResponse<Ticket> performRequestTask() throws RemoteException, AuthenticationException {
        Bundle params = getDefaultCallingParams();
        params.putString(BundleMarshaller.UserCidKey, this._cid);
        params.putAll(BundleMarshaller.scopeToBundle(this._securityScope));
        if (this._onlineIdConfiguration != null) {
            params.putAll(BundleMarshaller.onlineIdConfigurationToBundle(this._onlineIdConfiguration));
        }
        Bundle bundle = this._msaSsoService.getTicket(params);
        SingleSsoRequest.checkForErrors(bundle);
        if (BundleMarshaller.hasPendingIntent(bundle)) {
            return new SsoResponse().setPendingIntent(BundleMarshaller.pendingIntentFromBundle(bundle));
        }
        return new SsoResponse().setData(BundleMarshaller.ticketFromBundle(bundle));
    }
}
