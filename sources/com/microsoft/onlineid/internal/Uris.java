package com.microsoft.onlineid.internal;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import com.microsoft.onlineid.internal.log.Logger;
import com.microsoft.onlineid.userdata.AccountManagerReader;
import com.microsoft.onlineid.userdata.TelephonyManagerReader;
import java.util.Set;

public class Uris {
    static final String EmailDelimiter = ",";
    static final String EmailParam = "email";
    static final String MktParam = "mkt";
    static final String PhoneParam = "phone";

    public static Uri appendMarketQueryString(Context applicationContext, Uri original) {
        if (!TextUtils.isEmpty(original.getQueryParameter(MktParam))) {
            Logger.warning("Given URL already has mkt parameter set.");
            return original;
        }
        String mkt = Resources.getString(applicationContext, "app_market");
        Uri.Builder buildUpon = original.buildUpon();
        if (TextUtils.isEmpty(mkt)) {
            mkt = "en";
        }
        return buildUpon.appendQueryParameter(MktParam, mkt).build();
    }

    public static Uri appendPhoneDigits(TelephonyManagerReader telephonyManagerReader, Uri original) {
        if (!TextUtils.isEmpty(original.getQueryParameter(PhoneParam))) {
            Logger.warning("Given URL already has phone parameter set.");
            return original;
        }
        String phoneNumber = telephonyManagerReader.getPhoneNumber();
        return original.buildUpon().appendQueryParameter(PhoneParam, TextUtils.isEmpty(phoneNumber) ? "" : phoneNumber.replaceAll("[^\\d]+", "")).build();
    }

    public static Uri appendEmails(AccountManagerReader accountManagerReader, Uri original) {
        if (!TextUtils.isEmpty(original.getQueryParameter("email"))) {
            Logger.warning("Given URL already has email parameter set.");
            return original;
        }
        Set<String> emails = accountManagerReader.getEmails();
        return original.buildUpon().appendQueryParameter("email", emails.isEmpty() ? "" : TextUtils.join(EmailDelimiter, emails)).build();
    }
}
