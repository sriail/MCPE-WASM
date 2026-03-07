package com.microsoft.xbox.xle.telemetry.helpers;

import com.microsoft.xbox.idp.telemetry.helpers.UTCPageAction;
import com.microsoft.xbox.idp.telemetry.helpers.UTCPageView;
import com.microsoft.xbox.idp.telemetry.utc.model.UTCAdditionalInfoModel;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.xle.telemetry.helpers.UTCEventTracker;

public class UTCChangeRelationship {
    /* access modifiers changed from: private */
    public static CharSequence currentActivityTitle = "";
    /* access modifiers changed from: private */
    public static String currentXUID = "";

    public enum Relationship {
        UNKNOWN(0),
        ADDFRIEND(1),
        REMOVEFRIEND(2),
        EXISTINGFRIEND(3),
        NOTCHANGED(4);
        
        private int value;

        public int getValue() {
            return this.value;
        }

        private Relationship(int val) {
            this.value = val;
        }
    }

    public enum FavoriteStatus {
        UNKNOWN(0),
        FAVORITED(1),
        UNFAVORITED(2),
        NOTFAVORITED(3),
        EXISTINGFAVORITE(4),
        EXISTINGNOTFAVORITED(5);
        
        private int value;

        public int getValue() {
            return this.value;
        }

        private FavoriteStatus(int val) {
            this.value = val;
        }
    }

    public enum RealNameStatus {
        UNKNOWN(0),
        SHARINGON(1),
        SHARINGOFF(2),
        EXISTINGSHARED(3),
        EXISTINGNOTSHARED(4);
        
        private int value;

        public int getValue() {
            return this.value;
        }

        private RealNameStatus(int val) {
            this.value = val;
        }
    }

    public enum GamerType {
        UNKNOWN(0),
        NORMAL(1),
        FACEBOOK(2),
        SUGGESTED(3);
        
        private int value;

        private GamerType(int val) {
            this.value = val;
        }

        public int getValue() {
            return this.value;
        }
    }

    private static void verifyTrackedDefaults() {
        XLEAssert.assertFalse("Called trackPeopleHubView without set currentXUID", currentXUID.equals(""));
        XLEAssert.assertFalse("Called trackPeopleHubView without set activityTitle", currentActivityTitle.toString().equals(""));
    }

    /* access modifiers changed from: private */
    public static UTCAdditionalInfoModel getBaseInfoModel(String targetXUID) {
        UTCAdditionalInfoModel additionalInfoModel = new UTCAdditionalInfoModel();
        additionalInfoModel.addValue(UTCDeepLink.TARGET_XUID_KEY, "x:" + targetXUID);
        return additionalInfoModel;
    }

    public static void trackChangeRelationshipView(final CharSequence activityTitle, final String targetXUID) {
        UTCEventTracker.callTrackWrapper(new UTCEventTracker.UTCEventDelegate() {
            public void call() {
                CharSequence unused = UTCChangeRelationship.currentActivityTitle = activityTitle;
                String unused2 = UTCChangeRelationship.currentXUID = targetXUID;
                UTCPageView.track("Change Relationship - Change Relationship View", UTCChangeRelationship.currentActivityTitle, UTCChangeRelationship.getBaseInfoModel(UTCChangeRelationship.currentXUID));
            }
        });
    }

    public static void trackChangeRelationshipAction(boolean isFollowing, boolean isFromFacebook) {
        verifyTrackedDefaults();
        trackChangeRelationshipAction(currentActivityTitle, currentXUID, isFollowing, isFromFacebook);
    }

    public static void trackChangeRelationshipAction(final CharSequence activityTitle, final String targetXUID, final boolean isFollowing, final boolean isFromFacebook) {
        UTCEventTracker.callTrackWrapper(new UTCEventTracker.UTCEventDelegate() {
            public void call() {
                UTCAdditionalInfoModel model = UTCChangeRelationship.getBaseInfoModel(targetXUID);
                model.addValue("relationship", Integer.valueOf(isFollowing ? Relationship.EXISTINGFRIEND.getValue() : Relationship.ADDFRIEND.getValue()));
                UTCPageAction.track("Change Relationship - Action", activityTitle, model);
                if (isFromFacebook) {
                    UTCChangeRelationship.trackChangeRelationshipDone(activityTitle, targetXUID, Relationship.ADDFRIEND, RealNameStatus.SHARINGON, FavoriteStatus.NOTFAVORITED, GamerType.FACEBOOK);
                }
            }
        });
    }

    public static void trackChangeRelationshipRemoveFriend() {
        verifyTrackedDefaults();
        trackChangeRelationshipRemoveFriend(currentActivityTitle, currentXUID);
    }

    public static void trackChangeRelationshipRemoveFriend(final CharSequence activityTitle, final String targetXUID) {
        UTCEventTracker.callTrackWrapper(new UTCEventTracker.UTCEventDelegate() {
            public void call() {
                UTCAdditionalInfoModel additionalInfo = UTCChangeRelationship.getBaseInfoModel(targetXUID);
                additionalInfo.addValue("relationship", Relationship.REMOVEFRIEND);
                UTCPageAction.track("Change Relationship - Action", activityTitle, additionalInfo);
            }
        });
    }

    public static void trackChangeRelationshipDone(Relationship relationship, RealNameStatus realNameStatus, FavoriteStatus favoriteStatus, GamerType gamerType) {
        verifyTrackedDefaults();
        trackChangeRelationshipDone(currentActivityTitle, currentXUID, relationship, realNameStatus, favoriteStatus, gamerType);
    }

    public static void trackChangeRelationshipDone(CharSequence activityTitle, String targetXUID, Relationship relationship, RealNameStatus realNameStatus, FavoriteStatus favoriteStatus, GamerType gamerType) {
        final String str = targetXUID;
        final Relationship relationship2 = relationship;
        final FavoriteStatus favoriteStatus2 = favoriteStatus;
        final RealNameStatus realNameStatus2 = realNameStatus;
        final GamerType gamerType2 = gamerType;
        final CharSequence charSequence = activityTitle;
        UTCEventTracker.callTrackWrapper(new UTCEventTracker.UTCEventDelegate() {
            public void call() {
                UTCAdditionalInfoModel model = UTCChangeRelationship.getBaseInfoModel(str);
                model.addValue("relationship", Integer.valueOf(relationship2.getValue()));
                model.addValue("favorite", Integer.valueOf(favoriteStatus2.getValue()));
                model.addValue("realname", Integer.valueOf(realNameStatus2.getValue()));
                model.addValue("gamertype", Integer.valueOf(gamerType2.getValue()));
                UTCPageAction.track("Change Relationship - Done", charSequence, model);
            }
        });
    }
}
