package com.microsoft.xbox.xle.telemetry.helpers;

import com.microsoft.xbox.idp.telemetry.helpers.UTCPageAction;
import com.microsoft.xbox.idp.telemetry.helpers.UTCPageView;
import com.microsoft.xbox.idp.telemetry.utc.model.UTCAdditionalInfoModel;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.xle.telemetry.helpers.UTCEventTracker;

public class UTCReportUser {
    /* access modifiers changed from: private */
    public static CharSequence currentActivityTitle = "";
    /* access modifiers changed from: private */
    public static String currentXUID = "";

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

    public static void trackReportView(final CharSequence activityTitle, final String targetXUID) {
        UTCEventTracker.callTrackWrapper(new UTCEventTracker.UTCEventDelegate() {
            public void call() {
                CharSequence unused = UTCReportUser.currentActivityTitle = activityTitle;
                String unused2 = UTCReportUser.currentXUID = targetXUID;
                UTCPageView.track("People Hub Report view", UTCReportUser.currentActivityTitle, UTCReportUser.getBaseInfoModel(targetXUID));
            }
        });
    }

    public static void trackReportDialogOK(String reason) {
        verifyTrackedDefaults();
        trackReportDialogOK(currentActivityTitle, currentXUID, reason);
    }

    public static void trackReportDialogOK(final CharSequence activityTitle, final String targetXUID, final String reason) {
        UTCEventTracker.callTrackWrapper(new UTCEventTracker.UTCEventDelegate() {
            public void call() {
                UTCAdditionalInfoModel additionalInfo = UTCReportUser.getBaseInfoModel(targetXUID);
                additionalInfo.addValue("reason", reason);
                UTCPageAction.track("People Hub - Report OK", activityTitle, additionalInfo);
            }
        });
    }
}
