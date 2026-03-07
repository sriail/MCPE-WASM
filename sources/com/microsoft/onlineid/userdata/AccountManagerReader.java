package com.microsoft.onlineid.userdata;

import android.accounts.Account;
import android.content.Context;
import android.util.Patterns;
import com.microsoft.onlineid.analytics.ClientAnalytics;
import java.util.HashSet;
import java.util.Set;
import org.json.JSONArray;

public class AccountManagerReader {
    private final AccountManagerWrapper _accountManager;

    public AccountManagerReader(Context applicationContext) {
        this(new AccountManagerWrapper(applicationContext));
    }

    AccountManagerReader(AccountManagerWrapper accountManagerWrapper) {
        this._accountManager = accountManagerWrapper;
    }

    public Set<String> getEmails() {
        Set<String> accountNames = new HashSet<>();
        for (Account account : this._accountManager.getAccounts()) {
            if (Patterns.EMAIL_ADDRESS.matcher(account.name).matches()) {
                accountNames.add(account.name);
            }
        }
        ClientAnalytics.get().logEvent(ClientAnalytics.UserDataCategory, ClientAnalytics.UniqueEmailCount, ClientAnalytics.ExistsInAccountManager, Long.valueOf((long) accountNames.size()));
        return accountNames;
    }

    public String getEmailsAsJsonArray() {
        Set<String> accountNames = getEmails();
        JSONArray json = new JSONArray();
        for (String accountName : accountNames) {
            json.put(accountName);
        }
        return json.toString();
    }

    public String getDeviceEmail() {
        Account[] googleAccounts = this._accountManager.getAccountsByType("com.google");
        ClientAnalytics.get().logEvent(ClientAnalytics.UserDataCategory, ClientAnalytics.GoogleEmail, googleAccounts.length == 0 ? ClientAnalytics.DoesntExistInAccountManager : ClientAnalytics.ExistsInAccountManager);
        if (googleAccounts.length == 0) {
            return null;
        }
        ClientAnalytics.get().logEvent(ClientAnalytics.UserDataCategory, ClientAnalytics.GoogleEmailCount, ClientAnalytics.ExistsInAccountManager, Long.valueOf((long) googleAccounts.length));
        return googleAccounts[0].name;
    }
}
