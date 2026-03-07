package com.microsoft.xbox.xle.telemetry.helpers;

import com.microsoft.xbox.idp.telemetry.helpers.UTCPageAction;
import com.microsoft.xbox.idp.telemetry.helpers.UTCPageView;
import com.microsoft.xbox.idp.telemetry.utc.model.UTCAdditionalInfoModel;
import com.microsoft.xbox.idp.telemetry.utc.model.UTCCommonDataModel;
import com.microsoft.xbox.service.model.friendfinder.FriendFinderType;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.xle.telemetry.helpers.UTCEventTracker;

public class UTCFriendFinder {
    private static String currentUserXuid;

    /* access modifiers changed from: private */
    public static void setCurrentUserXuid(String userXuid) {
        currentUserXuid = userXuid;
        setUserIdForCommonData();
    }

    /* access modifiers changed from: private */
    public static void setUserIdForCommonData() {
        if (!JavaUtil.isNullOrEmpty(currentUserXuid)) {
            UTCCommonDataModel.setUserId(currentUserXuid);
        }
    }

    public static void trackFriendFinderView(final CharSequence activityTitle, final String currentUserXuid2) {
        UTCEventTracker.callTrackWrapper(new UTCEventTracker.UTCEventDelegate() {
            public void call() {
                UTCFriendFinder.setCurrentUserXuid(currentUserXuid2);
                UTCPageView.track("Friend Finder - View", activityTitle);
            }
        });
    }

    public static void trackFacebookOptInView(final CharSequence activityTitle) {
        UTCEventTracker.callTrackWrapper(new UTCEventTracker.UTCEventDelegate() {
            public void call() {
                UTCFriendFinder.setUserIdForCommonData();
                UTCPageView.track("Friend Finder Facebook -  Opt In View", activityTitle);
            }
        });
    }

    public static void trackFacebookLinkAccountView(final CharSequence activityTitle) {
        UTCEventTracker.callTrackWrapper(new UTCEventTracker.UTCEventDelegate() {
            public void call() {
                UTCFriendFinder.setUserIdForCommonData();
                UTCPageView.track("Friend Finder Facebook -  Link View", activityTitle);
            }
        });
    }

    public static void trackFacebookAddFriendView(final CharSequence activityTitle) {
        UTCEventTracker.callTrackWrapper(new UTCEventTracker.UTCEventDelegate() {
            public void call() {
                UTCFriendFinder.setUserIdForCommonData();
                UTCPageView.track("Friend Finder Facebook - Find Friends View", activityTitle);
            }
        });
    }

    public static void trackFacebookShareView(final CharSequence activityTitle) {
        UTCEventTracker.callTrackWrapper(new UTCEventTracker.UTCEventDelegate() {
            public void call() {
                UTCFriendFinder.setUserIdForCommonData();
                UTCPageView.track("Friend Finder Facebook - Share View", activityTitle);
            }
        });
    }

    public static void trackContactsFindFriendsView(final CharSequence activityTitle) {
        UTCEventTracker.callTrackWrapper(new UTCEventTracker.UTCEventDelegate() {
            public void call() {
                UTCFriendFinder.setUserIdForCommonData();
                UTCPageView.track("Friend Finder Contacts - Find Friends View", activityTitle);
            }
        });
    }

    public static void trackContactsVerifyPhoneView(final CharSequence activityTitle) {
        UTCEventTracker.callTrackWrapper(new UTCEventTracker.UTCEventDelegate() {
            public void call() {
                UTCFriendFinder.setUserIdForCommonData();
                UTCPageView.track("Friend Finder Contacts - Verify Phone View", activityTitle);
            }
        });
    }

    public static void trackContactsAddPhoneView(final CharSequence activityTitle) {
        UTCEventTracker.callTrackWrapper(new UTCEventTracker.UTCEventDelegate() {
            public void call() {
                UTCFriendFinder.setUserIdForCommonData();
                UTCPageView.track("Friend Finder Contacts - Add Phone View", activityTitle);
            }
        });
    }

    public static void trackContactsInviteFriendsView(final CharSequence activityTitle) {
        UTCEventTracker.callTrackWrapper(new UTCEventTracker.UTCEventDelegate() {
            public void call() {
                UTCFriendFinder.setUserIdForCommonData();
                UTCPageView.track("Friend Finder Contacts - Invite Friends View", activityTitle);
            }
        });
    }

    public static void trackContactsOptInView(final CharSequence activityTitle) {
        UTCEventTracker.callTrackWrapper(new UTCEventTracker.UTCEventDelegate() {
            public void call() {
                UTCFriendFinder.setUserIdForCommonData();
                UTCPageView.track("Friend Finder Contacts - Opt In View", activityTitle);
            }
        });
    }

    public static void trackFacebookSignup(final CharSequence activityTitle) {
        UTCEventTracker.callTrackWrapper(new UTCEventTracker.UTCEventDelegate() {
            public void call() {
                UTCFriendFinder.setUserIdForCommonData();
                UTCPageAction.track("Friend Finder - Facebook Signup", activityTitle);
            }
        });
    }

    public static void trackFacebookSuggestions(final CharSequence activityTitle) {
        UTCEventTracker.callTrackWrapper(new UTCEventTracker.UTCEventDelegate() {
            public void call() {
                UTCFriendFinder.setUserIdForCommonData();
                UTCPageAction.track("Friend Finder - Facebook Suggestions", activityTitle);
            }
        });
    }

    public static void trackContactsSignUp(final CharSequence activityTitle) {
        UTCEventTracker.callTrackWrapper(new UTCEventTracker.UTCEventDelegate() {
            public void call() {
                UTCFriendFinder.setUserIdForCommonData();
                UTCPageAction.track("Friend Finder Contacts - Sign Up", activityTitle);
            }
        });
    }

    public static void trackContactsSuggestions(final CharSequence activityTitle) {
        UTCEventTracker.callTrackWrapper(new UTCEventTracker.UTCEventDelegate() {
            public void call() {
                UTCFriendFinder.setUserIdForCommonData();
                UTCPageAction.track("Friend Finder Contacts - Suggestions", activityTitle);
            }
        });
    }

    public static void trackGamertagSearch(final CharSequence activityTitle) {
        UTCEventTracker.callTrackWrapper(new UTCEventTracker.UTCEventDelegate() {
            public void call() {
                UTCFriendFinder.setUserIdForCommonData();
                UTCPageAction.track("Friend Finder - Gamertag Search", activityTitle);
            }
        });
    }

    public static void trackGamertagSearchSubmit(final CharSequence activityTitle) {
        UTCEventTracker.callTrackWrapper(new UTCEventTracker.UTCEventDelegate() {
            public void call() {
                UTCFriendFinder.setUserIdForCommonData();
                UTCPageAction.track("Friend Finder - Gamertag Search Submit", activityTitle);
            }
        });
    }

    public static void trackGamertagSearchSuccess(final CharSequence activityTitle, final String xuid) {
        UTCEventTracker.callTrackWrapper(new UTCEventTracker.UTCEventDelegate() {
            public void call() {
                UTCFriendFinder.setUserIdForCommonData();
                UTCAdditionalInfoModel model = new UTCAdditionalInfoModel();
                model.addValue(UTCDeepLink.TARGET_XUID_KEY, "x:" + xuid);
                UTCPageAction.track("Friend Finder - Gamertag Search Success", activityTitle, model);
            }
        });
    }

    public static void trackDone(final CharSequence activityTitle) {
        UTCEventTracker.callTrackWrapper(new UTCEventTracker.UTCEventDelegate() {
            public void call() {
                UTCFriendFinder.setUserIdForCommonData();
                UTCPageAction.track("Friend Finder - Done", activityTitle);
            }
        });
    }

    public static void trackFacebookOptInNext(final CharSequence activityTitle) {
        UTCEventTracker.callTrackWrapper(new UTCEventTracker.UTCEventDelegate() {
            public void call() {
                UTCFriendFinder.setUserIdForCommonData();
                UTCPageAction.track("Friend Finder Facebook - Opt In Next", activityTitle);
            }
        });
    }

    public static void trackFacebookLoginCancel(final CharSequence activityTitle) {
        UTCEventTracker.callTrackWrapper(new UTCEventTracker.UTCEventDelegate() {
            public void call() {
                UTCFriendFinder.setUserIdForCommonData();
                UTCPageAction.track("Friend Finder Facebook - Login Cancel", activityTitle);
            }
        });
    }

    private static void trackFacebookFriendFinderBack(final CharSequence activityTitle) {
        UTCEventTracker.callTrackWrapper(new UTCEventTracker.UTCEventDelegate() {
            public void call() {
                UTCFriendFinder.setUserIdForCommonData();
                UTCPageAction.track("Friend Finder Facebook - Friend Finder Back", activityTitle);
                UTCPageView.removePage();
            }
        });
    }

    public static void trackBackButtonPressed(CharSequence activityTitle, FriendFinderType friendFinderType) {
        switch (friendFinderType) {
            case FACEBOOK:
                trackFacebookFriendFinderBack(activityTitle);
                return;
            case PHONE:
                trackPhoneContactsBack(activityTitle);
                return;
            default:
                return;
        }
    }

    public static void trackFacebookLoginSuccessful(final CharSequence activityTitle) {
        UTCEventTracker.callTrackWrapper(new UTCEventTracker.UTCEventDelegate() {
            public void call() {
                UTCFriendFinder.setUserIdForCommonData();
                UTCPageAction.track("Friend Finder Facebook - Login", activityTitle);
            }
        });
    }

    public static void trackAddFacebookFriend(final CharSequence activityTitle, final String[] targetXuids) {
        UTCEventTracker.callTrackWrapper(new UTCEventTracker.UTCEventDelegate() {
            public void call() {
                UTCFriendFinder.setUserIdForCommonData();
                UTCAdditionalInfoModel model = new UTCAdditionalInfoModel();
                model.addValue("selectedXUIDs", UTCFriendFinder.formatXuids(targetXuids));
                UTCPageAction.track("Friend Finder Facebook - Add Facebook Friend", activityTitle, model);
            }
        });
    }

    public static void trackShareFacebookLinkToFeed(final CharSequence activityTitle) {
        UTCEventTracker.callTrackWrapper(new UTCEventTracker.UTCEventDelegate() {
            public void call() {
                UTCFriendFinder.setUserIdForCommonData();
                UTCPageAction.track("Friend Finder Facebook - Upsell Success", activityTitle);
            }
        });
    }

    public static void trackSkipFacebookSharing(final CharSequence activityTitle) {
        UTCEventTracker.callTrackWrapper(new UTCEventTracker.UTCEventDelegate() {
            public void call() {
                UTCFriendFinder.setUserIdForCommonData();
                UTCPageAction.track("Friend Finder Facebook - Upsell Cancel", activityTitle);
            }
        });
    }

    public static void trackAddFacebookFriendCancel(final CharSequence activityTitle) {
        UTCEventTracker.callTrackWrapper(new UTCEventTracker.UTCEventDelegate() {
            public void call() {
                UTCFriendFinder.setUserIdForCommonData();
                UTCPageAction.track("Friend Finder Facebook - Add Facebook Friend Cancel", activityTitle);
            }
        });
    }

    public static void trackPhoneContactsNext(final CharSequence activityTitle) {
        UTCEventTracker.callTrackWrapper(new UTCEventTracker.UTCEventDelegate() {
            public void call() {
                UTCFriendFinder.setUserIdForCommonData();
                UTCPageAction.track("Friend Finder Contacts - Next", activityTitle);
            }
        });
    }

    private static void trackPhoneContactsBack(final CharSequence activityTitle) {
        UTCEventTracker.callTrackWrapper(new UTCEventTracker.UTCEventDelegate() {
            public void call() {
                UTCFriendFinder.setUserIdForCommonData();
                UTCPageAction.track("Friend Finder Contacts - Close", activityTitle);
                UTCPageView.removePage();
            }
        });
    }

    public static void trackPhoneContactsSkipAddFriends(final CharSequence activityTitle) {
        UTCEventTracker.callTrackWrapper(new UTCEventTracker.UTCEventDelegate() {
            public void call() {
                UTCFriendFinder.setUserIdForCommonData();
                UTCPageAction.track("Friend Finder Contacts - Skip", activityTitle);
            }
        });
    }

    public static void trackPhoneContactsChangeRegion(final CharSequence activityTitle) {
        UTCEventTracker.callTrackWrapper(new UTCEventTracker.UTCEventDelegate() {
            public void call() {
                UTCFriendFinder.setUserIdForCommonData();
                UTCPageAction.track("Friend Finder Contacts - Change Region", activityTitle);
            }
        });
    }

    public static void trackPhoneContactsResendCode(final CharSequence activityTitle) {
        UTCEventTracker.callTrackWrapper(new UTCEventTracker.UTCEventDelegate() {
            public void call() {
                UTCFriendFinder.setUserIdForCommonData();
                UTCPageAction.track("Friend Finder Contacts - Resend Code", activityTitle);
            }
        });
    }

    public static void trackPhoneContactsCallMe(final CharSequence activityTitle) {
        UTCEventTracker.callTrackWrapper(new UTCEventTracker.UTCEventDelegate() {
            public void call() {
                UTCFriendFinder.setUserIdForCommonData();
                UTCPageAction.track("Friend Finder Contacts - Call Me", activityTitle);
            }
        });
    }

    public static void trackPhoneContactsSendInvite(final CharSequence activityTitle) {
        UTCEventTracker.callTrackWrapper(new UTCEventTracker.UTCEventDelegate() {
            public void call() {
                UTCFriendFinder.setUserIdForCommonData();
                UTCPageAction.track("Friend Finder Contacts - Send Invite", activityTitle);
            }
        });
    }

    public static void trackPhoneContactsAddFriends(final CharSequence activityTitle, final String[] targetXuids) {
        UTCEventTracker.callTrackWrapper(new UTCEventTracker.UTCEventDelegate() {
            public void call() {
                UTCFriendFinder.setUserIdForCommonData();
                UTCAdditionalInfoModel model = new UTCAdditionalInfoModel();
                model.addValue("selectedXUIDs", UTCFriendFinder.formatXuids(targetXuids));
                UTCPageAction.track("Friend Finder Contacts - Add Friends", activityTitle, model);
            }
        });
    }

    public static void trackPhoneContactsLinkSuccess(final CharSequence activityTitle) {
        UTCEventTracker.callTrackWrapper(new UTCEventTracker.UTCEventDelegate() {
            public void call() {
                UTCFriendFinder.setUserIdForCommonData();
                UTCPageAction.track("Friend Finder Contacts - Link Success", activityTitle);
            }
        });
    }

    /* access modifiers changed from: private */
    public static String[] formatXuids(String[] xuids) {
        for (int index = 0; index < xuids.length; index++) {
            String currentXuid = xuids[index];
            if (!currentXuid.startsWith("x:")) {
                xuids[index] = "x:" + currentXuid;
            }
        }
        return xuids;
    }
}
