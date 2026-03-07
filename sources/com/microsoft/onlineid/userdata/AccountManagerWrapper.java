package com.microsoft.onlineid.userdata;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;

public class AccountManagerWrapper {
    private final AccountManager _accountManager;

    public AccountManagerWrapper(Context applicationContext) {
        this._accountManager = AccountManager.get(applicationContext);
    }

    public Account[] getAccounts() {
        return this._accountManager.getAccounts();
    }

    public Account[] getAccountsByType(String type) {
        return this._accountManager.getAccountsByType(type);
    }
}
