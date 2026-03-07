package com.microsoft.onlineid.internal.sso.service.operation;

import android.content.Context;
import android.os.Bundle;
import com.microsoft.onlineid.internal.sso.BundleMarshaller;
import com.microsoft.onlineid.internal.sts.TicketManager;
import com.microsoft.onlineid.internal.ui.AccountPickerActivity;
import com.microsoft.onlineid.sts.AuthenticatorAccountManager;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class GetAccountPickerOperation extends ServiceOperation {
    public GetAccountPickerOperation(Context applicationContext, Bundle params, AuthenticatorAccountManager accountManager, TicketManager ticketManager) {
        super(applicationContext, params, accountManager, ticketManager);
    }

    public Bundle call() {
        ArrayList<String> excludedCids = getParameters().getStringArrayList(BundleMarshaller.CidExclusionListKey);
        String membernameType = getParameters().getString(BundleMarshaller.PreferredMembernameTypeKey);
        String cobrandingId = getParameters().getString(BundleMarshaller.CobrandingIdKey);
        Set<String> set = new HashSet<>();
        if (excludedCids != null) {
            set.addAll(excludedCids);
        }
        if (!getAccountManager().getFilteredAccounts(set).isEmpty()) {
            return BundleMarshaller.pendingIntentToBundle(getPendingIntentBuilder(AccountPickerActivity.getAccountPickerIntent(getContext(), excludedCids, membernameType, cobrandingId, getCallingPackage(), getCallerStateBundle())).setContext(getContext()).buildActivity());
        }
        return new GetSignInIntentOperation(getContext(), getParameters(), getAccountManager(), getTicketManager()).call();
    }
}
