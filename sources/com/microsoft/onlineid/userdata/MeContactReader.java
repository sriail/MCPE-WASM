package com.microsoft.onlineid.userdata;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.TextUtils;
import com.facebook.share.internal.ShareConstants;
import com.microsoft.onlineid.analytics.ClientAnalytics;
import com.microsoft.onlineid.internal.Assertion;
import com.microsoft.onlineid.internal.log.Logger;

public class MeContactReader implements IPhoneNumberReader {
    private final Context _applicationContext;

    public MeContactReader(Context applicationContext) {
        this._applicationContext = applicationContext;
    }

    public class FullName {
        private final String _firstName;
        private final String _lastName;

        public FullName(String firstName, String lastName) {
            this._firstName = firstName;
            this._lastName = lastName;
        }

        public String getFirstName() {
            return this._firstName;
        }

        public String getLastName() {
            return this._lastName;
        }
    }

    public FullName getFullName() {
        FullName fullName;
        boolean z = true;
        Cursor cursor = createCursorForProfile(new String[]{"data2", "data3"}, "vnd.android.cursor.item/name");
        if (cursor == null || !cursor.moveToFirst()) {
            fullName = new FullName("", "");
        } else {
            fullName = new FullName(cursor.getString(cursor.getColumnIndex("data2")), cursor.getString(cursor.getColumnIndex("data3")));
        }
        if (fullName == null) {
            z = false;
        }
        Assertion.check(z);
        if (cursor != null) {
            cursor.close();
        }
        ClientAnalytics.get().logEvent(ClientAnalytics.UserDataCategory, ClientAnalytics.FirstName, TextUtils.isEmpty(fullName.getFirstName()) ? ClientAnalytics.DoesntExistInMeContact : ClientAnalytics.ExistsInMeContact);
        ClientAnalytics.get().logEvent(ClientAnalytics.UserDataCategory, ClientAnalytics.LastName, TextUtils.isEmpty(fullName.getLastName()) ? ClientAnalytics.DoesntExistInMeContact : ClientAnalytics.ExistsInMeContact);
        return fullName;
    }

    public String getPhoneNumber() {
        Cursor cursor = createCursorForProfile(new String[]{"data2", "data1"}, "vnd.android.cursor.item/phone_v2");
        String mobile = null;
        String home = null;
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int phoneType = cursor.getInt(cursor.getColumnIndex("data2"));
                if (phoneType == 2) {
                    if (TextUtils.isEmpty(mobile)) {
                        mobile = cursor.getString(cursor.getColumnIndex("data1"));
                    }
                } else if (phoneType == 1 && TextUtils.isEmpty(home)) {
                    home = cursor.getString(cursor.getColumnIndex("data1"));
                }
            }
            cursor.close();
        }
        ClientAnalytics.get().logEvent(ClientAnalytics.UserDataCategory, ClientAnalytics.MobilePhoneNumber, TextUtils.isEmpty(mobile) ? ClientAnalytics.DoesntExistInMeContact : ClientAnalytics.ExistsInMeContact);
        ClientAnalytics.get().logEvent(ClientAnalytics.UserDataCategory, ClientAnalytics.HomePhoneNumber, TextUtils.isEmpty(home) ? ClientAnalytics.DoesntExistInMeContact : ClientAnalytics.ExistsInMeContact);
        if (TextUtils.isEmpty(mobile)) {
            return home;
        }
        return mobile;
    }

    /* access modifiers changed from: protected */
    public Cursor createCursorForProfile(String[] projection, String singleSelection) {
        try {
            return this._applicationContext.getContentResolver().query(Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI, ShareConstants.WEB_DIALOG_PARAM_DATA), projection, "mimetype = ?", new String[]{singleSelection}, (String) null);
        } catch (Exception e) {
            Logger.warning("Failed to retrieve user profile from device", e);
            return null;
        }
    }
}
