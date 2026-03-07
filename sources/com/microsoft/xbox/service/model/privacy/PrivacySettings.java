package com.microsoft.xbox.service.model.privacy;

public class PrivacySettings {

    public enum PrivacySettingId {
        None,
        ShareFriendList,
        ShareGameHistory,
        CommunicateUsingTextAndVoice,
        SharePresence,
        ShareProfile,
        ShareVideoAndMusicStatus,
        CommunicateUsingVideo,
        CollectVoiceData,
        ShareXboxMusicActivity,
        ShareExerciseInfo,
        ShareIdentity,
        ShareRecordedGameSessions,
        ShareIdentityTransitively,
        CanShareIdentity;

        public static PrivacySettingId getPrivacySettingId(String id) {
            for (PrivacySettingId status : values()) {
                if (status.name().equalsIgnoreCase(id)) {
                    return status;
                }
            }
            return None;
        }
    }

    public enum PrivacySettingValue {
        NotSet,
        Everyone,
        PeopleOnMyList,
        FriendCategoryShareIdentity,
        Blocked;

        public static PrivacySettingValue getPrivacySettingValue(String value) {
            for (PrivacySettingValue status : values()) {
                if (status.name().equalsIgnoreCase(value)) {
                    return status;
                }
            }
            return NotSet;
        }
    }

    public static class PrivacySetting {
        public String setting;
        private PrivacySettingId settingId;
        private PrivacySettingValue settingValue;
        public String value;

        public PrivacySetting() {
        }

        public PrivacySetting(PrivacySettingId settingId2, PrivacySettingValue value2) {
            this.setting = settingId2.name();
            this.value = value2.name();
        }

        public void setPrivacySettingId(PrivacySettingId settingId2) {
            this.setting = settingId2.name();
            this.settingId = settingId2;
        }

        public PrivacySettingId getPrivacySettingId() {
            this.settingId = PrivacySettingId.getPrivacySettingId(this.setting);
            return this.settingId;
        }

        public PrivacySettingValue getPrivacySettingValue() {
            this.settingValue = PrivacySettingValue.getPrivacySettingValue(this.value);
            return this.settingValue;
        }
    }
}
