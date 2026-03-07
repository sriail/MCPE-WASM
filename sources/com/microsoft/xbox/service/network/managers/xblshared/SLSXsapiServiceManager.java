package com.microsoft.xbox.service.network.managers.xblshared;

import android.util.Log;
import android.util.Pair;
import com.microsoft.xbox.idp.util.HttpCall;
import com.microsoft.xbox.idp.util.HttpUtil;
import com.microsoft.xbox.service.model.friendfinder.FriendFinderState;
import com.microsoft.xbox.service.model.friendfinder.LinkedAccountHelpers;
import com.microsoft.xbox.service.model.friendfinder.OptInStatus;
import com.microsoft.xbox.service.model.friendfinder.ShortCircuitProfileMessage;
import com.microsoft.xbox.service.model.friendfinder.UpdateThirdPartyTokenRequest;
import com.microsoft.xbox.service.model.privacy.PrivacySettings;
import com.microsoft.xbox.service.model.privacy.PrivacySettingsResult;
import com.microsoft.xbox.service.network.managers.AddFollowingUserResponseContainer;
import com.microsoft.xbox.service.network.managers.FamilySettings;
import com.microsoft.xbox.service.network.managers.IPeopleHubResult;
import com.microsoft.xbox.service.network.managers.IUserProfileResult;
import com.microsoft.xbox.service.network.managers.MutedListResultContainer;
import com.microsoft.xbox.service.network.managers.NeverListResultContainer;
import com.microsoft.xbox.service.network.managers.ProfilePreferredColor;
import com.microsoft.xbox.service.network.managers.ProfileSummaryResultContainer;
import com.microsoft.xbox.toolkit.GsonUtil;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.ProjectSpecificDataProvider;
import com.microsoft.xbox.toolkit.TcuiHttpUtil;
import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.network.XboxLiveEnvironment;
import com.microsoft.xbox.xle.app.FriendFinderSettings;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

public class SLSXsapiServiceManager implements ISLSServiceManager {
    private static final String TAG = SLSXsapiServiceManager.class.getSimpleName();

    public FamilySettings getFamilySettings(String xuid) throws XLEException {
        return null;
    }

    public boolean removeFriendFromShareIdentitySetting(String xuid, String postBody) throws XLEException {
        boolean z;
        Log.i(TAG, "removeFriendFromShareIdentitySetting");
        if (Thread.currentThread() != ThreadManager.UIThread) {
            z = true;
        } else {
            z = false;
        }
        XLEAssert.assertTrue(z);
        HttpCall httpCall = HttpUtil.appendCommonParameters(new HttpCall("POST", String.format(XboxLiveEnvironment.Instance().getRemoveUsersFromShareIdentityUrlFormat(), new Object[]{xuid}), ""), "4");
        httpCall.setRequestBody(postBody);
        boolean result = TcuiHttpUtil.getResponseSyncSucceeded(httpCall, Arrays.asList(new Integer[]{204}));
        TcuiHttpUtil.throwIfNullOrFalse(Boolean.valueOf(result));
        return result;
    }

    public boolean addFriendToShareIdentitySetting(String xuid, String postBody) throws XLEException {
        boolean z;
        Log.i(TAG, "addFriendToShareIdentitySetting");
        if (Thread.currentThread() != ThreadManager.UIThread) {
            z = true;
        } else {
            z = false;
        }
        XLEAssert.assertTrue(z);
        HttpCall httpCall = HttpUtil.appendCommonParameters(new HttpCall("POST", String.format(XboxLiveEnvironment.Instance().getAddFriendsToShareIdentityUrlFormat(), new Object[]{xuid}), ""), "4");
        httpCall.setRequestBody(postBody);
        boolean result = TcuiHttpUtil.getResponseSyncSucceeded(httpCall, Arrays.asList(new Integer[]{204}));
        TcuiHttpUtil.throwIfNullOrFalse(Boolean.valueOf(result));
        return result;
    }

    public boolean addUserToFavoriteList(String postBody) throws XLEException {
        boolean z;
        Log.i(TAG, "addUserToFavoriteList");
        if (Thread.currentThread() != ThreadManager.UIThread) {
            z = true;
        } else {
            z = false;
        }
        XLEAssert.assertTrue(z);
        HttpCall httpCall = HttpUtil.appendCommonParameters(new HttpCall("POST", String.format(XboxLiveEnvironment.Instance().getProfileFavoriteListUrl(), new Object[]{"add"}), ""), "1");
        httpCall.setRequestBody(postBody);
        boolean result = TcuiHttpUtil.getResponseSyncSucceeded(httpCall, Arrays.asList(new Integer[]{204}));
        TcuiHttpUtil.throwIfNullOrFalse(Boolean.valueOf(result));
        return result;
    }

    public boolean removeUserFromFavoriteList(String postBody) throws XLEException {
        boolean z;
        Log.i(TAG, "removeUserFromFavoriteList");
        if (Thread.currentThread() != ThreadManager.UIThread) {
            z = true;
        } else {
            z = false;
        }
        XLEAssert.assertTrue(z);
        HttpCall httpCall = HttpUtil.appendCommonParameters(new HttpCall("POST", String.format(XboxLiveEnvironment.Instance().getProfileFavoriteListUrl(), new Object[]{"remove"}), ""), "1");
        httpCall.setRequestBody(postBody);
        boolean result = TcuiHttpUtil.getResponseSyncSucceeded(httpCall, Arrays.asList(new Integer[]{204}));
        TcuiHttpUtil.throwIfNullOrFalse(Boolean.valueOf(result));
        return result;
    }

    public AddFollowingUserResponseContainer.AddFollowingUserResponse addUserToFollowingList(String postBody) throws XLEException {
        boolean z;
        Log.i(TAG, "addUserToFollowingList");
        if (Thread.currentThread() != ThreadManager.UIThread) {
            z = true;
        } else {
            z = false;
        }
        XLEAssert.assertTrue(z);
        HttpCall httpCall = HttpUtil.appendCommonParameters(new HttpCall("POST", String.format(XboxLiveEnvironment.Instance().updateProfileFollowingListUrl(), new Object[]{"add"}), ""), "1");
        httpCall.setRequestBody(postBody);
        final AddFollowingUserResponseContainer.AddFollowingUserResponse result = new AddFollowingUserResponseContainer.AddFollowingUserResponse();
        final AtomicReference<Pair<Boolean, AddFollowingUserResponseContainer.AddFollowingUserResponse>> notifier = new AtomicReference<>();
        notifier.set(new Pair(false, (Object) null));
        httpCall.getResponseAsync((HttpCall.Callback) new HttpCall.Callback() {
            public void processResponse(InputStream stream) throws Exception {
                synchronized (notifier) {
                    result.setAddFollowingRequestStatus(true);
                    notifier.set(new Pair(true, result));
                    notifier.notify();
                }
            }

            public void processHttpError(int errorCode, int httpStatus, String errorMessage) {
                synchronized (notifier) {
                    if (httpStatus == 204) {
                        result.setAddFollowingRequestStatus(true);
                        notifier.set(new Pair(true, result));
                    } else {
                        notifier.set(new Pair(true, (AddFollowingUserResponseContainer.AddFollowingUserResponse) GsonUtil.deserializeJson(errorMessage, AddFollowingUserResponseContainer.AddFollowingUserResponse.class)));
                    }
                    notifier.notify();
                }
            }
        });
        synchronized (notifier) {
            while (!((Boolean) notifier.get().first).booleanValue()) {
                try {
                    notifier.wait();
                } catch (InterruptedException e) {
                }
            }
        }
        TcuiHttpUtil.throwIfNullOrFalse(notifier.get().second);
        return (AddFollowingUserResponseContainer.AddFollowingUserResponse) notifier.get().second;
    }

    public ProfileSummaryResultContainer.ProfileSummaryResult getProfileSummaryInfo(String xuid) throws XLEException {
        boolean z;
        boolean z2;
        Log.i(TAG, "getProfileSummaryInfo");
        if (Thread.currentThread() != ThreadManager.UIThread) {
            z = true;
        } else {
            z = false;
        }
        XLEAssert.assertTrue(z);
        if (!JavaUtil.isNullOrEmpty(xuid)) {
            z2 = true;
        } else {
            z2 = false;
        }
        XLEAssert.assertTrue(z2);
        ProfileSummaryResultContainer.ProfileSummaryResult result = (ProfileSummaryResultContainer.ProfileSummaryResult) TcuiHttpUtil.getResponseSync(HttpUtil.appendCommonParameters(new HttpCall("GET", String.format(XboxLiveEnvironment.Instance().getProfileSummaryUrlFormat(), new Object[]{xuid}), ""), XboxLiveEnvironment.USER_PROFILE_CONTRACT_VERSION), ProfileSummaryResultContainer.ProfileSummaryResult.class);
        TcuiHttpUtil.throwIfNullOrFalse(result);
        return result;
    }

    public boolean removeUserFromFollowingList(String postBody) throws XLEException {
        boolean z;
        Log.i(TAG, "removeUserFromFollowingList");
        if (Thread.currentThread() != ThreadManager.UIThread) {
            z = true;
        } else {
            z = false;
        }
        XLEAssert.assertTrue(z);
        HttpCall httpCall = HttpUtil.appendCommonParameters(new HttpCall("POST", String.format(XboxLiveEnvironment.Instance().updateProfileFollowingListUrl(), new Object[]{"remove"}), ""), "1");
        httpCall.setRequestBody(postBody);
        boolean result = TcuiHttpUtil.getResponseSyncSucceeded(httpCall, Arrays.asList(new Integer[]{204}));
        TcuiHttpUtil.throwIfNullOrFalse(Boolean.valueOf(result));
        return result;
    }

    public IUserProfileResult.UserProfileResult getUserProfileInfo(String postBody) throws XLEException {
        Log.i(TAG, "getUserProfileInfo");
        XLEAssert.assertTrue(Thread.currentThread() != ThreadManager.UIThread);
        HttpCall httpCall = HttpUtil.appendCommonParameters(new HttpCall("POST", XboxLiveEnvironment.Instance().getUserProfileInfoUrl(), ""), XboxLiveEnvironment.USER_PROFILE_CONTRACT_VERSION);
        httpCall.setRequestBody(postBody);
        IUserProfileResult.UserProfileResult result = (IUserProfileResult.UserProfileResult) TcuiHttpUtil.getResponseSync(httpCall, IUserProfileResult.UserProfileResult.class);
        TcuiHttpUtil.throwIfNullOrFalse(result);
        return result;
    }

    public int[] getXTokenPrivileges() throws XLEException {
        return new int[0];
    }

    public ProfilePreferredColor getProfilePreferredColor(String url) throws XLEException {
        Log.i(TAG, "getProfilePreferredColor");
        XLEAssert.assertTrue(Thread.currentThread() != ThreadManager.UIThread);
        ProfilePreferredColor result = (ProfilePreferredColor) TcuiHttpUtil.getResponseSync(HttpUtil.appendCommonParameters(new HttpCall("GET", url, ""), ""), ProfilePreferredColor.class);
        TcuiHttpUtil.throwIfNullOrFalse(result);
        return result;
    }

    public PrivacySettingsResult getUserProfilePrivacySettings() throws XLEException {
        Log.i(TAG, "getUserProfilePrivacySettings");
        XLEAssert.assertTrue(Thread.currentThread() != ThreadManager.UIThread);
        PrivacySettingsResult result = (PrivacySettingsResult) TcuiHttpUtil.getResponseSync(HttpUtil.appendCommonParameters(new HttpCall("GET", XboxLiveEnvironment.Instance().getUserProfileSettingUrlFormat(), ""), "4"), PrivacySettingsResult.class);
        TcuiHttpUtil.throwIfNullOrFalse(result);
        return result;
    }

    public NeverListResultContainer.NeverListResult getNeverListInfo(String xuid) throws XLEException {
        boolean z;
        boolean z2;
        Log.i(TAG, "getNeverListInfo");
        if (Thread.currentThread() != ThreadManager.UIThread) {
            z = true;
        } else {
            z = false;
        }
        XLEAssert.assertTrue(z);
        if (!JavaUtil.isNullOrEmpty(xuid)) {
            z2 = true;
        } else {
            z2 = false;
        }
        XLEAssert.assertTrue(z2);
        NeverListResultContainer.NeverListResult result = (NeverListResultContainer.NeverListResult) TcuiHttpUtil.getResponseSync(HttpUtil.appendCommonParameters(new HttpCall("GET", String.format(XboxLiveEnvironment.Instance().getProfileNeverListUrlFormat(), new Object[]{xuid}), ""), "1"), NeverListResultContainer.NeverListResult.class);
        TcuiHttpUtil.throwIfNullOrFalse(result);
        return result;
    }

    public boolean addUserToNeverList(String xuid, String postBody) throws XLEException {
        boolean z;
        Log.i(TAG, "addUserToNeverList");
        if (Thread.currentThread() != ThreadManager.UIThread) {
            z = true;
        } else {
            z = false;
        }
        XLEAssert.assertTrue(z);
        HttpCall httpCall = HttpUtil.appendCommonParameters(new HttpCall("PUT", String.format(XboxLiveEnvironment.Instance().getProfileNeverListUrlFormat(), new Object[]{xuid}), ""), "1");
        httpCall.setRequestBody(postBody);
        boolean result = TcuiHttpUtil.getResponseSyncSucceeded(httpCall, new ArrayList(0));
        TcuiHttpUtil.throwIfNullOrFalse(Boolean.valueOf(result));
        return result;
    }

    public boolean removeUserFromNeverList(String xuid, String postBody) throws XLEException {
        boolean z;
        Log.i(TAG, "removeUserFromNeverList");
        if (Thread.currentThread() != ThreadManager.UIThread) {
            z = true;
        } else {
            z = false;
        }
        XLEAssert.assertTrue(z);
        HttpCall httpCall = HttpUtil.appendCommonParameters(new HttpCall("DELETE", String.format(XboxLiveEnvironment.Instance().getProfileNeverListUrlFormat(), new Object[]{xuid}), ""), "1");
        httpCall.setRequestBody(postBody);
        boolean result = TcuiHttpUtil.getResponseSyncSucceeded(httpCall, new ArrayList(0));
        TcuiHttpUtil.throwIfNullOrFalse(Boolean.valueOf(result));
        return result;
    }

    public MutedListResultContainer.MutedListResult getMutedListInfo(String xuid) throws XLEException {
        boolean z;
        boolean z2;
        Log.i(TAG, "getMutedListInfo");
        if (Thread.currentThread() != ThreadManager.UIThread) {
            z = true;
        } else {
            z = false;
        }
        XLEAssert.assertTrue(z);
        if (!JavaUtil.isNullOrEmpty(xuid)) {
            z2 = true;
        } else {
            z2 = false;
        }
        XLEAssert.assertTrue(z2);
        MutedListResultContainer.MutedListResult result = (MutedListResultContainer.MutedListResult) TcuiHttpUtil.getResponseSync(HttpUtil.appendCommonParameters(new HttpCall("GET", String.format(XboxLiveEnvironment.Instance().getMutedServiceUrlFormat(), new Object[]{xuid}), ""), "1"), MutedListResultContainer.MutedListResult.class);
        TcuiHttpUtil.throwIfNullOrFalse(result);
        return result;
    }

    public boolean addUserToMutedList(String xuid, String postBody) throws XLEException {
        boolean z;
        Log.i(TAG, "addUserToMutedList");
        if (Thread.currentThread() != ThreadManager.UIThread) {
            z = true;
        } else {
            z = false;
        }
        XLEAssert.assertTrue(z);
        HttpCall httpCall = HttpUtil.appendCommonParameters(new HttpCall("PUT", String.format(XboxLiveEnvironment.Instance().getMutedServiceUrlFormat(), new Object[]{xuid}), ""), "1");
        httpCall.setRequestBody(postBody);
        boolean result = TcuiHttpUtil.getResponseSyncSucceeded(httpCall, new ArrayList(0));
        TcuiHttpUtil.throwIfNullOrFalse(Boolean.valueOf(result));
        return result;
    }

    public boolean removeUserFromMutedList(String xuid, String postBody) throws XLEException {
        boolean z;
        Log.i(TAG, "removeUserFromMutedList");
        if (Thread.currentThread() != ThreadManager.UIThread) {
            z = true;
        } else {
            z = false;
        }
        XLEAssert.assertTrue(z);
        HttpCall httpCall = HttpUtil.appendCommonParameters(new HttpCall("DELETE", String.format(XboxLiveEnvironment.Instance().getMutedServiceUrlFormat(), new Object[]{xuid}), ""), "1");
        httpCall.setRequestBody(postBody);
        boolean result = TcuiHttpUtil.getResponseSyncSucceeded(httpCall, new ArrayList(0));
        TcuiHttpUtil.throwIfNullOrFalse(Boolean.valueOf(result));
        return result;
    }

    public boolean submitFeedback(String xuid, String postBody) throws XLEException {
        boolean z;
        Log.i(TAG, "submitFeedback");
        if (Thread.currentThread() != ThreadManager.UIThread) {
            z = true;
        } else {
            z = false;
        }
        XLEAssert.assertTrue(z);
        HttpCall httpCall = HttpUtil.appendCommonParameters(new HttpCall("POST", String.format(XboxLiveEnvironment.Instance().getSubmitFeedbackUrlFormat(), new Object[]{xuid}), ""), "101");
        httpCall.setRequestBody(postBody);
        boolean result = TcuiHttpUtil.getResponseSyncSucceeded(httpCall, new ArrayList(202));
        TcuiHttpUtil.throwIfNullOrFalse(Boolean.valueOf(result));
        return result;
    }

    public FriendFinderSettings getFriendFinderSettings() throws XLEException {
        boolean z;
        Log.i(TAG, "getFriendFinderSettings");
        if (Thread.currentThread() != ThreadManager.UIThread) {
            z = true;
        } else {
            z = false;
        }
        XLEAssert.assertTrue(z);
        FriendFinderSettings result = (FriendFinderSettings) TcuiHttpUtil.getResponseSync(HttpUtil.appendCommonParameters(new HttpCall("GET", XboxLiveEnvironment.Instance().getFriendFinderSettingsUrl(), "", false), "1"), FriendFinderSettings.class);
        TcuiHttpUtil.throwIfNullOrFalse(result);
        return result;
    }

    public FriendFinderState.FriendsFinderStateResult getPeopleHubFriendFinderState() throws XLEException {
        Log.i(TAG, "getPeopleHubFriendFinderState");
        XLEAssert.assertTrue(Thread.currentThread() != ThreadManager.UIThread);
        HttpCall httpCall = HttpUtil.appendCommonParameters(new HttpCall("GET", XboxLiveEnvironment.Instance().getPeopleHubFriendFinderStateUrlFormat(), ""), "1");
        httpCall.setCustomHeader("Accept-Language", ProjectSpecificDataProvider.getInstance().getLegalLocale());
        httpCall.setCustomHeader("X-XBL-Contract-Version", "1");
        httpCall.setCustomHeader("X-XBL-Market", ProjectSpecificDataProvider.getInstance().getRegion());
        FriendFinderState.FriendsFinderStateResult result = (FriendFinderState.FriendsFinderStateResult) TcuiHttpUtil.getResponseSync(httpCall, FriendFinderState.FriendsFinderStateResult.class);
        TcuiHttpUtil.throwIfNullOrFalse(result);
        return result;
    }

    public PrivacySettings.PrivacySetting getPrivacySetting(PrivacySettings.PrivacySettingId settingId) throws XLEException {
        boolean z;
        Log.i(TAG, "getPrivacySetting");
        if (Thread.currentThread() != ThreadManager.UIThread) {
            z = true;
        } else {
            z = false;
        }
        XLEAssert.assertTrue(z);
        PrivacySettings.PrivacySetting result = (PrivacySettings.PrivacySetting) TcuiHttpUtil.getResponseSync(HttpUtil.appendCommonParameters(new HttpCall("GET", String.format(XboxLiveEnvironment.Instance().getProfileSettingUrlFormat(), new Object[]{settingId.name()}), ""), "4"), PrivacySettings.PrivacySetting.class);
        TcuiHttpUtil.throwIfNullOrFalse(result);
        return result;
    }

    public boolean setPrivacySettings(PrivacySettingsResult settings) throws XLEException {
        boolean z;
        Log.i(TAG, "setPrivacySettings");
        if (Thread.currentThread() != ThreadManager.UIThread) {
            z = true;
        } else {
            z = false;
        }
        XLEAssert.assertTrue(z);
        HttpCall httpCall = HttpUtil.appendCommonParameters(new HttpCall("PUT", XboxLiveEnvironment.Instance().getUserProfileSettingUrlFormat(), ""), "4");
        httpCall.setRequestBody(PrivacySettingsResult.getPrivacySettingRequestBody(settings));
        boolean result = TcuiHttpUtil.getResponseSyncSucceeded(httpCall, Arrays.asList(new Integer[]{201}));
        TcuiHttpUtil.throwIfNullOrFalse(Boolean.valueOf(result));
        return result;
    }

    public boolean setFriendFinderOptInStatus(LinkedAccountHelpers.LinkedAccountType type, OptInStatus optInStatus) throws XLEException {
        boolean z;
        Log.i(TAG, "setFriendFinderOptInStatus");
        if (Thread.currentThread() != ThreadManager.UIThread) {
            z = true;
        } else {
            z = false;
        }
        XLEAssert.assertTrue(z);
        HttpCall httpCall = HttpUtil.appendCommonParameters(new HttpCall("PUT", String.format(XboxLiveEnvironment.Instance().getSetFriendFinderOptInStatusUrlFormat(), new Object[]{type.name()}), optInStatus == OptInStatus.OptedIn ? "?status=OptedIn&waitForUpdate=true" : "?status=OptedOut"), "1");
        httpCall.setCustomHeader("Content-Length", "0");
        boolean result = TcuiHttpUtil.getResponseSyncSucceeded(httpCall, Arrays.asList(new Integer[]{204}));
        TcuiHttpUtil.throwIfNullOrFalse(Boolean.valueOf(result));
        return result;
    }

    public boolean updateThirdPartyToken(LinkedAccountHelpers.LinkedAccountType type, String token) throws XLEException {
        boolean z;
        Log.i(TAG, "updateThirdPartyToken");
        if (Thread.currentThread() != ThreadManager.UIThread) {
            z = true;
        } else {
            z = false;
        }
        XLEAssert.assertTrue(z);
        HttpCall httpCall = HttpUtil.appendCommonParameters(new HttpCall("PUT", String.format(XboxLiveEnvironment.Instance().getUpdateThirdPartyTokenUrlFormat(), new Object[]{type.name()}), ""), "1");
        httpCall.setRequestBody(UpdateThirdPartyTokenRequest.getUpdateThirdPartyTokenRequestBody(new UpdateThirdPartyTokenRequest(token)));
        boolean result = TcuiHttpUtil.getResponseSyncSucceeded(httpCall, Arrays.asList(new Integer[]{204}));
        TcuiHttpUtil.throwIfNullOrFalse(Boolean.valueOf(result));
        return result;
    }

    public IPeopleHubResult.PeopleHubPeopleSummary getPeopleHubRecommendations() throws XLEException {
        Log.i(TAG, "getPeopleHubRecommendations");
        XLEAssert.assertTrue(Thread.currentThread() != ThreadManager.UIThread);
        HttpCall httpCall = HttpUtil.appendCommonParameters(new HttpCall("GET", XboxLiveEnvironment.Instance().getPeopleHubRecommendationsUrlFormat(), ""), "1");
        httpCall.setCustomHeader("Accept-Language", ProjectSpecificDataProvider.getInstance().getLegalLocale());
        httpCall.setCustomHeader("X-XBL-Contract-Version", "1");
        httpCall.setCustomHeader("X-XBL-Market", ProjectSpecificDataProvider.getInstance().getRegion());
        IPeopleHubResult.PeopleHubPeopleSummary result = (IPeopleHubResult.PeopleHubPeopleSummary) TcuiHttpUtil.getResponseSync(httpCall, IPeopleHubResult.PeopleHubPeopleSummary.class);
        TcuiHttpUtil.throwIfNullOrFalse(result);
        return result;
    }

    public ShortCircuitProfileMessage.ShortCircuitProfileResponse getMyShortCircuitProfile() throws XLEException {
        Log.i(TAG, "getMyShortCircuitProfile");
        XLEAssert.assertTrue(Thread.currentThread() != ThreadManager.UIThread);
        String rpsTicket = ProjectSpecificDataProvider.getInstance().getSCDRpsTicket();
        XLEAssert.assertFalse("Expected to have acquired a ticket already", JavaUtil.isNullOrEmpty(rpsTicket));
        if (JavaUtil.isNullOrEmpty(rpsTicket)) {
            throw new XLEException(2);
        }
        HttpCall httpCall = new HttpCall("GET", XboxLiveEnvironment.Instance().getShortCircuitProfileUrlFormat(), "");
        httpCall.setCustomHeader("PS-MSAAuthTicket", rpsTicket);
        httpCall.setCustomHeader("PS-ApplicationId", "44445A65-4A71-4083-8C90-041A22856E69");
        httpCall.setCustomHeader("PS-Scenario", "Minecraft TCUI Friend Finder");
        httpCall.setCustomHeader("Content-Type", "application/x-www-form-urlencoded");
        String result = TcuiHttpUtil.getResponseBodySync(httpCall);
        if (!JavaUtil.isNullOrEmpty(result)) {
            return ShortCircuitProfileMessage.ShortCircuitProfileResponse.parseJson(result);
        }
        throw new XLEException(2);
    }

    public ShortCircuitProfileMessage.ShortCircuitProfileResponse sendShortCircuitProfile(ShortCircuitProfileMessage.ShortCircuitProfileRequest request) throws XLEException {
        Log.i(TAG, "sendShortCircuitProfile");
        XLEAssert.assertTrue(Thread.currentThread() != ThreadManager.UIThread);
        String rpsTicket = ProjectSpecificDataProvider.getInstance().getSCDRpsTicket();
        XLEAssert.assertFalse("Expected to have acquired a ticket already", JavaUtil.isNullOrEmpty(rpsTicket));
        if (JavaUtil.isNullOrEmpty(rpsTicket)) {
            throw new XLEException(2);
        }
        HttpCall httpCall = new HttpCall("POST", XboxLiveEnvironment.Instance().getShortCircuitProfileUrlFormat(), "");
        httpCall.setCustomHeader("PS-MSAAuthTicket", rpsTicket);
        httpCall.setCustomHeader("PS-ApplicationId", "44445A65-4A71-4083-8C90-041A22856E69");
        httpCall.setCustomHeader("PS-Scenario", "Minecraft TCUI Friend Finder");
        httpCall.setCustomHeader("Content-Type", "application/x-www-form-urlencoded");
        httpCall.setRequestBody(request.toString());
        String result = TcuiHttpUtil.getResponseBodySync(httpCall);
        if (!JavaUtil.isNullOrEmpty(result)) {
            return ShortCircuitProfileMessage.ShortCircuitProfileResponse.parseJson(result);
        }
        throw new XLEException(2);
    }

    public ShortCircuitProfileMessage.UploadPhoneContactsResponse updatePhoneContacts(ShortCircuitProfileMessage.UploadPhoneContactsRequest request) throws XLEException {
        Log.i(TAG, "updatePhoneContacts");
        XLEAssert.assertTrue(Thread.currentThread() != ThreadManager.UIThread);
        String rpsTicket = ProjectSpecificDataProvider.getInstance().getSCDRpsTicket();
        XLEAssert.assertFalse("Expected to have acquired a ticket already", JavaUtil.isNullOrEmpty(rpsTicket));
        if (JavaUtil.isNullOrEmpty(rpsTicket)) {
            throw new XLEException(2);
        }
        HttpCall httpCall = new HttpCall("POST", XboxLiveEnvironment.Instance().getUploadingPhoneContactsUrlFormat(), "");
        httpCall.setCustomHeader("X-TicketToken", rpsTicket);
        httpCall.setCustomHeader("X-AppId", "44445A65-4A71-4083-8C90-041A22856E69");
        httpCall.setCustomHeader("X-Scenario", "Minecraft TCUI Friend Finder");
        httpCall.setCustomHeader("Content-Type", "application/x-www-form-urlencoded");
        httpCall.setRequestBody(request.toString());
        String result = TcuiHttpUtil.getResponseBodySync(httpCall);
        if (!JavaUtil.isNullOrEmpty(result)) {
            return ShortCircuitProfileMessage.UploadPhoneContactsResponse.parseJson(result);
        }
        throw new XLEException(2);
    }

    public IUserProfileResult.UserProfileResult SearchGamertag(String gamertag) throws XLEException {
        boolean z = true;
        Log.i(TAG, "SearchGamertag");
        if (Thread.currentThread() == ThreadManager.UIThread) {
            z = false;
        }
        XLEAssert.assertTrue(z);
        try {
            IUserProfileResult.UserProfileResult result = (IUserProfileResult.UserProfileResult) TcuiHttpUtil.getResponseSync(HttpUtil.appendCommonParameters(new HttpCall("GET", String.format(XboxLiveEnvironment.Instance().getGamertagSearchUrlFormat(), new Object[]{URLEncoder.encode(gamertag.toLowerCase(), "utf-8")}), ""), XboxLiveEnvironment.USER_PROFILE_CONTRACT_VERSION), IUserProfileResult.UserProfileResult.class);
            TcuiHttpUtil.throwIfNullOrFalse(result);
            return result;
        } catch (UnsupportedEncodingException e) {
            throw new XLEException(15, (Throwable) e);
        }
    }
}
