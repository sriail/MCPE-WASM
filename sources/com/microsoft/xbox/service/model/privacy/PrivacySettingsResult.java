package com.microsoft.xbox.service.model.privacy;

import com.microsoft.xbox.service.model.privacy.PrivacySettings;
import com.microsoft.xbox.toolkit.GsonUtil;
import java.util.ArrayList;
import java.util.Iterator;

public class PrivacySettingsResult {
    public ArrayList<PrivacySettings.PrivacySetting> settings;

    public PrivacySettingsResult() {
    }

    public PrivacySettingsResult(ArrayList<PrivacySettings.PrivacySetting> settings2) {
        this.settings = new ArrayList<>(settings2);
    }

    public static PrivacySettingsResult deserialize(String input) {
        return (PrivacySettingsResult) GsonUtil.deserializeJson(input, PrivacySettingsResult.class);
    }

    public String getShareRealNameStatus() {
        Iterator i$ = this.settings.iterator();
        while (i$.hasNext()) {
            PrivacySettings.PrivacySetting s = i$.next();
            if (s.getPrivacySettingId() == PrivacySettings.PrivacySettingId.ShareIdentity) {
                return s.value;
            }
        }
        return PrivacySettings.PrivacySettingValue.PeopleOnMyList.name();
    }

    public boolean getSharingRealNameTransitively() {
        Iterator i$ = this.settings.iterator();
        while (i$.hasNext()) {
            PrivacySettings.PrivacySetting s = i$.next();
            if (s.getPrivacySettingId() == PrivacySettings.PrivacySettingId.ShareIdentityTransitively) {
                return s.value.equalsIgnoreCase(PrivacySettings.PrivacySettingValue.Everyone.name());
            }
        }
        return false;
    }

    public static String getPrivacySettingRequestBody(PrivacySettingsResult privacySettingsResult) {
        try {
            return GsonUtil.toJsonString(privacySettingsResult);
        } catch (Exception e) {
            return null;
        }
    }
}
