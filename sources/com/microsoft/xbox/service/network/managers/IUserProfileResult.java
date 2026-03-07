package com.microsoft.xbox.service.network.managers;

import com.microsoft.xbox.service.model.sls.UserProfileSetting;
import com.microsoft.xbox.toolkit.GsonUtil;
import java.util.ArrayList;
import java.util.Iterator;

public interface IUserProfileResult {

    public static class Settings {
        public String id;
        public String value;
    }

    public static class ProfileUser {
        private static final long FORCE_MATURITY_LEVEL_UPDATE_TIME = 10800000;
        public boolean canViewTVAdultContent;
        public ProfilePreferredColor colors;
        public String id;
        private int maturityLevel;
        private int[] privileges;
        public ArrayList<Settings> settings;
        private long updateMaturityLevelTimer = -1;

        private void fetchMaturityLevel() {
            try {
                FamilySettings familySettings = ServiceManagerFactory.getInstance().getSLSServiceManager().getFamilySettings(this.id);
                if (familySettings != null && familySettings.familyUsers != null) {
                    int i = 0;
                    while (true) {
                        if (i >= familySettings.familyUsers.size()) {
                            break;
                        } else if (familySettings.familyUsers.get(i).xuid.equalsIgnoreCase(this.id)) {
                            this.canViewTVAdultContent = familySettings.familyUsers.get(i).canViewTVAdultContent;
                            this.maturityLevel = familySettings.familyUsers.get(i).maturityLevel;
                            break;
                        } else {
                            i++;
                        }
                    }
                }
            } catch (Throwable th) {
            }
            this.updateMaturityLevelTimer = System.currentTimeMillis();
        }

        public int getMaturityLevel() {
            if (this.updateMaturityLevelTimer < 0 || System.currentTimeMillis() - this.updateMaturityLevelTimer > FORCE_MATURITY_LEVEL_UPDATE_TIME) {
                fetchMaturityLevel();
            }
            return this.maturityLevel;
        }

        public void setmaturityLevel(int maturityLevel2) {
            this.maturityLevel = maturityLevel2;
            this.updateMaturityLevelTimer = System.currentTimeMillis();
        }

        public int[] getPrivileges() {
            return this.privileges;
        }

        public void setPrivilieges(int[] privileges2) {
            this.privileges = privileges2;
        }

        public String getSettingValue(UserProfileSetting settingId) {
            if (this.settings != null) {
                Iterator i$ = this.settings.iterator();
                while (i$.hasNext()) {
                    Settings setting = i$.next();
                    if (setting.id != null && setting.id.equals(settingId.toString())) {
                        return setting.value;
                    }
                }
            }
            return null;
        }
    }

    public static class UserProfileResult {
        public ArrayList<ProfileUser> profileUsers;

        public static UserProfileResult deserialize(String input) {
            return (UserProfileResult) GsonUtil.deserializeJson(input, UserProfileResult.class);
        }
    }
}
