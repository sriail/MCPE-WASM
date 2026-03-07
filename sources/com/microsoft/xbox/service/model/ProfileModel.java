package com.microsoft.xbox.service.model;

import android.util.Log;
import com.microsoft.xbox.service.model.FollowersData;
import com.microsoft.xbox.service.model.friendfinder.FriendFinderState;
import com.microsoft.xbox.service.model.privacy.PrivacySettingsResult;
import com.microsoft.xbox.service.model.sls.AddShareIdentityRequest;
import com.microsoft.xbox.service.model.sls.FavoriteListRequest;
import com.microsoft.xbox.service.model.sls.FeedbackType;
import com.microsoft.xbox.service.model.sls.MutedListRequest;
import com.microsoft.xbox.service.model.sls.NeverListRequest;
import com.microsoft.xbox.service.model.sls.SubmitFeedbackRequest;
import com.microsoft.xbox.service.model.sls.UserProfileRequest;
import com.microsoft.xbox.service.model.sls.UserProfileSetting;
import com.microsoft.xbox.service.network.managers.AddFollowingUserResponseContainer;
import com.microsoft.xbox.service.network.managers.FamilySettings;
import com.microsoft.xbox.service.network.managers.FollowingSummaryResult;
import com.microsoft.xbox.service.network.managers.IFollowerPresenceResult;
import com.microsoft.xbox.service.network.managers.IPeopleHubResult;
import com.microsoft.xbox.service.network.managers.IUserProfileResult;
import com.microsoft.xbox.service.network.managers.MutedListResultContainer;
import com.microsoft.xbox.service.network.managers.NeverListResultContainer;
import com.microsoft.xbox.service.network.managers.ProfileSummaryResultContainer;
import com.microsoft.xbox.service.network.managers.ServiceManagerFactory;
import com.microsoft.xbox.service.network.managers.friendfinder.FacebookManager;
import com.microsoft.xbox.service.network.managers.xblshared.ISLSServiceManager;
import com.microsoft.xbox.toolkit.AsyncActionStatus;
import com.microsoft.xbox.toolkit.AsyncResult;
import com.microsoft.xbox.toolkit.DataLoadUtil;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.ProjectSpecificDataProvider;
import com.microsoft.xbox.toolkit.SingleEntryLoadingStatus;
import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.ThreadSafeFixedSizeHashtable;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEErrorCode;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.XLERValueHelper;
import com.microsoft.xbox.toolkit.network.IDataLoaderRunnable;
import com.microsoft.xbox.toolkit.network.XLEThreadPool;
import com.microsoft.xbox.toolkit.network.XboxLiveEnvironment;
import com.microsoft.xbox.xle.app.XLEUtil;
import com.microsoft.xbox.xle.viewmodel.ShareRealNameSettingFilter;
import com.microsoft.xboxtcui.XboxTcuiSdk;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;

public class ProfileModel extends ModelBase<ProfileData> {
    private static final int MAX_PROFILE_MODELS = 20;
    private static final long friendsDataLifetime = 180000;
    private static ProfileModel meProfileInstance = null;
    private static ThreadSafeFixedSizeHashtable<String, ProfileModel> profileModelCache = new ThreadSafeFixedSizeHashtable<>(20);
    private static final long profilePresenceDataLifetime = 180000;
    private AddFollowingUserResponseContainer.AddFollowingUserResponse addUserToFollowingResponse;
    private SingleEntryLoadingStatus addingUserToFavoriteListLoadingStatus = new SingleEntryLoadingStatus();
    private SingleEntryLoadingStatus addingUserToFollowingListLoadingStatus = new SingleEntryLoadingStatus();
    private SingleEntryLoadingStatus addingUserToMutedListLoadingStatus = new SingleEntryLoadingStatus();
    private SingleEntryLoadingStatus addingUserToNeverListLoadingStatus = new SingleEntryLoadingStatus();
    private SingleEntryLoadingStatus addingUserToShareIdentityListLoadingStatus = new SingleEntryLoadingStatus();
    private ArrayList<FollowersData> favorites;
    private String firstName;
    private ArrayList<FollowersData> following;
    private ArrayList<FollowingSummaryResult.People> followingSummaries;
    private String lastName;
    private Date lastRefreshMutedList;
    private Date lastRefreshNeverList;
    private Date lastRefreshPeopleHubRecommendations;
    private Date lastRefreshPresenceData;
    private Date lastRefreshProfileSummary;
    private MutedListResultContainer.MutedListResult mutedList;
    private SingleEntryLoadingStatus mutedListLoadingStatus = new SingleEntryLoadingStatus();
    private NeverListResultContainer.NeverListResult neverList;
    private SingleEntryLoadingStatus neverListLoadingStatus = new SingleEntryLoadingStatus();
    private IPeopleHubResult.PeopleHubPersonSummary peopleHubPersonSummary;
    private ArrayList<FollowersData> peopleHubRecommendations;
    private IPeopleHubResult.PeopleHubPeopleSummary peopleHubRecommendationsRaw;
    private IFollowerPresenceResult.UserPresence presenceData;
    private SingleEntryLoadingStatus presenceDataLoadingStatus;
    private String profileImageUrl;
    private ProfileSummaryResultContainer.ProfileSummaryResult profileSummary;
    private SingleEntryLoadingStatus profileSummaryLoadingStatus;
    private IUserProfileResult.ProfileUser profileUser;
    private SingleEntryLoadingStatus removingUserFromFavoriteListLoadingStatus = new SingleEntryLoadingStatus();
    private SingleEntryLoadingStatus removingUserFromFollowingListLoadingStatus = new SingleEntryLoadingStatus();
    private SingleEntryLoadingStatus removingUserFromMutedListLoadingStatus = new SingleEntryLoadingStatus();
    private SingleEntryLoadingStatus removingUserFromNeverListLoadingStatus = new SingleEntryLoadingStatus();
    private SingleEntryLoadingStatus removingUserFromShareIdentityListLoadingStatus = new SingleEntryLoadingStatus();
    private boolean shareRealName;
    private String shareRealNameStatus;
    private boolean sharingRealNameTransitively;
    private SingleEntryLoadingStatus submitFeedbackForUserLoadingStatus = new SingleEntryLoadingStatus();
    /* access modifiers changed from: private */
    public String xuid;

    private ProfileModel(String xuid2) {
        this.xuid = xuid2;
    }

    public static ProfileModel getMeProfileModel() {
        if (ProjectSpecificDataProvider.getInstance().getXuidString() == null) {
            return null;
        }
        if (meProfileInstance == null) {
            meProfileInstance = new ProfileModel(ProjectSpecificDataProvider.getInstance().getXuidString());
        }
        return meProfileInstance;
    }

    public static ProfileModel getProfileModel(String xuid2) {
        if (JavaUtil.isNullOrEmpty(xuid2)) {
            throw new IllegalArgumentException();
        } else if (JavaUtil.stringsEqualCaseInsensitive(xuid2, ProjectSpecificDataProvider.getInstance().getXuidString())) {
            if (meProfileInstance == null) {
                meProfileInstance = new ProfileModel(xuid2);
            }
            return meProfileInstance;
        } else {
            ProfileModel model = profileModelCache.get(xuid2);
            if (model != null) {
                return model;
            }
            ProfileModel model2 = new ProfileModel(xuid2);
            profileModelCache.put(xuid2, model2);
            return model2;
        }
    }

    public String getXuid() {
        return this.xuid;
    }

    public String getAccountTier() {
        return getProfileSettingValue(UserProfileSetting.AccountTier);
    }

    public String getAppDisplayName() {
        return getProfileSettingValue(UserProfileSetting.AppDisplayName);
    }

    public String getGamerScore() {
        return getProfileSettingValue(UserProfileSetting.Gamerscore);
    }

    public String getLocation() {
        return getProfileSettingValue(UserProfileSetting.Location);
    }

    public String getBio() {
        return getProfileSettingValue(UserProfileSetting.Bio);
    }

    public String getRealName() {
        if (this.shareRealName) {
            return getProfileSettingValue(UserProfileSetting.RealName);
        }
        return null;
    }

    private String getProfileImageUrl() {
        if (this.profileImageUrl != null) {
            return this.profileImageUrl;
        }
        this.profileImageUrl = getProfileSettingValue(UserProfileSetting.GameDisplayPicRaw);
        return this.profileImageUrl;
    }

    public ArrayList<FollowersData> getFollowingData() {
        return this.following;
    }

    public String getGamerPicImageUrl() {
        return getProfileImageUrl();
    }

    public int getNumberOfFollowing() {
        if (this.profileSummary != null) {
            return this.profileSummary.targetFollowingCount;
        }
        return 0;
    }

    public int getNumberOfFollowers() {
        if (this.profileSummary != null) {
            return this.profileSummary.targetFollowerCount;
        }
        return 0;
    }

    public int getPreferedColor() {
        if (this.profileUser == null || this.profileUser.colors == null) {
            return getDefaultColor();
        }
        return this.profileUser.colors.getPrimaryColor();
    }

    public ArrayList<URI> getWatermarkUris() {
        ArrayList<URI> uriArrayList = new ArrayList<>();
        String tenureLevel = getProfileSettingValue(UserProfileSetting.TenureLevel);
        if (!JavaUtil.isNullOrEmpty(tenureLevel) && !tenureLevel.equalsIgnoreCase("0")) {
            try {
                String tenureWatermarkUrlFormat = XboxLiveEnvironment.Instance().getTenureWatermarkUrlFormat();
                Object[] objArr = new Object[1];
                if (tenureLevel.length() == 1) {
                    tenureLevel = "0" + tenureLevel;
                }
                objArr[0] = tenureLevel;
                uriArrayList.add(new URI(String.format(tenureWatermarkUrlFormat, objArr)));
            } catch (URISyntaxException ex) {
                XLEAssert.fail("Failed to create URI for tenure watermark: " + ex.toString());
            }
        }
        String otherWatermarks = getProfileSettingValue(UserProfileSetting.Watermarks);
        if (!JavaUtil.isNullOrEmpty(otherWatermarks)) {
            for (String watermark : otherWatermarks.split("\\|")) {
                try {
                    uriArrayList.add(new URI(XboxLiveEnvironment.Instance().getWatermarkUrl(watermark)));
                } catch (URISyntaxException ex2) {
                    XLEAssert.fail("Failed to create URI for watermark " + watermark + " : " + ex2.toString());
                }
            }
        }
        return uriArrayList;
    }

    public boolean isMeProfile() {
        return isMeXuid(this.xuid);
    }

    public boolean isCallerFollowingTarget() {
        return this.profileSummary != null && this.profileSummary.isCallerFollowingTarget;
    }

    public boolean isTargetFollowingCaller() {
        return this.profileSummary != null && this.profileSummary.isTargetFollowingCaller;
    }

    public boolean hasCallerMarkedTargetAsFavorite() {
        return this.profileSummary != null && this.profileSummary.hasCallerMarkedTargetAsFavorite;
    }

    public boolean hasCallerMarkedTargetAsIdentityShared() {
        return this.profileSummary != null && this.profileSummary.hasCallerMarkedTargetAsIdentityShared;
    }

    public static boolean isMeXuid(String xuid2) {
        String myXuid = ProjectSpecificDataProvider.getInstance().getXuidString();
        return (myXuid == null || xuid2 == null || xuid2.compareToIgnoreCase(myXuid) != 0) ? false : true;
    }

    public static int getDefaultColor() {
        return XboxTcuiSdk.getResources().getColor(XLERValueHelper.getColorRValue("XboxOneGreen"));
    }

    public ProfileSummaryResultContainer.ProfileSummaryResult getProfileSummaryData() {
        return this.profileSummary;
    }

    public String getShareRealNameStatus() {
        return this.shareRealNameStatus;
    }

    public String getGamerTag() {
        return getProfileSettingValue(UserProfileSetting.Gamertag);
    }

    private String getProfileSettingValue(UserProfileSetting settingId) {
        if (!(this.profileUser == null || this.profileUser.settings == null)) {
            Iterator i$ = this.profileUser.settings.iterator();
            while (i$.hasNext()) {
                IUserProfileResult.Settings setting = i$.next();
                if (setting.id != null && setting.id.equals(settingId.toString())) {
                    return setting.value;
                }
            }
        }
        return null;
    }

    public NeverListResultContainer.NeverListResult getNeverListData() {
        return this.neverList;
    }

    public MutedListResultContainer.MutedListResult getMutedList() {
        return this.mutedList;
    }

    public IFollowerPresenceResult.UserPresence getPresenceData() {
        return this.presenceData;
    }

    public int getMaturityLevel() {
        if (this.profileUser != null) {
            return this.profileUser.getMaturityLevel();
        }
        return 0;
    }

    public void setFirstName(String firstName2) {
        this.firstName = firstName2;
    }

    public void setLastName(String lastName2) {
        this.lastName = lastName2;
    }

    public static boolean hasPrivilegeToAddFriend() {
        return hasPrivilege(XPrivilegeConstants.XPRIVILEGE_ADD_FRIEND);
    }

    public static boolean hasPrivilegeToSendMessage() {
        return hasPrivilege(XPrivilegeConstants.XPRIVILEGE_COMMUNICATIONS);
    }

    private static boolean hasPrivilege(String prv) {
        String privileges = ProjectSpecificDataProvider.getInstance().getPrivileges();
        return !JavaUtil.isNullOrEmpty(privileges) && privileges.contains(prv);
    }

    public ArrayList<FollowersData> getFavorites() {
        return this.favorites;
    }

    public IPeopleHubResult.PeopleHubPersonSummary getPeopleHubPersonSummary() {
        return this.peopleHubPersonSummary;
    }

    public AddFollowingUserResponseContainer.AddFollowingUserResponse getAddUserToFollowingResult() {
        return this.addUserToFollowingResponse;
    }

    public IPeopleHubResult.PeopleHubPeopleSummary getPeopleHubRecommendationsRawData() {
        return this.peopleHubRecommendationsRaw;
    }

    public static void reset() {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        Enumeration<ProfileModel> e = profileModelCache.elements();
        while (e.hasMoreElements()) {
            e.nextElement().clearObserver();
        }
        if (meProfileInstance != null) {
            meProfileInstance.clearObserver();
            meProfileInstance = null;
        }
        profileModelCache = new ThreadSafeFixedSizeHashtable<>(20);
    }

    public boolean shouldRefreshProfileSummary() {
        return XLEUtil.shouldRefresh(this.lastRefreshProfileSummary, this.lifetime);
    }

    public boolean shouldRefreshPresenceData() {
        return XLEUtil.shouldRefresh(this.lastRefreshPresenceData, this.lifetime);
    }

    public void loadAsync(boolean forceRefresh) {
        loadInternal(forceRefresh, UpdateType.MeProfileData, new GetProfileRunner(this, this.xuid, false));
    }

    public AsyncResult<ProfileData> loadSync(boolean forceRefresh) {
        return loadSync(forceRefresh, false);
    }

    public AsyncResult<ProfileData> loadSync(boolean forceRefresh, boolean loadEssentialsOnly) {
        return super.loadData(forceRefresh, new GetProfileRunner(this, this.xuid, loadEssentialsOnly));
    }

    public AsyncResult<IFollowerPresenceResult.UserPresence> loadPresenceData(boolean forceRefresh) {
        if (this.presenceDataLoadingStatus == null) {
            this.presenceDataLoadingStatus = new SingleEntryLoadingStatus();
        }
        return DataLoadUtil.Load(forceRefresh, 180000, this.lastRefreshPresenceData, this.presenceDataLoadingStatus, new GetPresenceDataRunner(this, this.xuid));
    }

    public AsyncResult<IPeopleHubResult.PeopleHubPeopleSummary> loadPeopleHubRecommendations(boolean forceRefresh) {
        XLEAssert.assertIsNotUIThread();
        XLEAssert.assertNotNull(this.xuid);
        return DataLoadUtil.Load(forceRefresh, 180000, this.lastRefreshPeopleHubRecommendations, new SingleEntryLoadingStatus(), new GetPeopleHubRecommendationRunner(this, this.xuid));
    }

    public AsyncResult<Boolean> removeUserFromShareIdentity(boolean forceRefresh, ArrayList<String> users) {
        XLEAssert.assertIsNotUIThread();
        XLEAssert.assertNotNull(this.xuid);
        return DataLoadUtil.Load(forceRefresh, this.lifetime, (Date) null, this.removingUserFromShareIdentityListLoadingStatus, new RemoveUsersFromShareIdentityListRunner(this, users));
    }

    public AsyncResult<Boolean> addUserToShareIdentity(boolean forceRefresh, ArrayList<String> users) {
        XLEAssert.assertIsNotUIThread();
        XLEAssert.assertNotNull(this.xuid);
        return DataLoadUtil.Load(forceRefresh, this.lifetime, (Date) null, this.addingUserToShareIdentityListLoadingStatus, new AddUsersToShareIdentityListRunner(this, users));
    }

    public AsyncResult<Boolean> addUserToFavoriteList(boolean forceRefresh, String favoriteUserXuid) {
        XLEAssert.assertIsNotUIThread();
        XLEAssert.assertNotNull(this.xuid);
        XLEAssert.assertNotNull(favoriteUserXuid);
        return DataLoadUtil.Load(forceRefresh, this.lifetime, (Date) null, this.addingUserToFavoriteListLoadingStatus, new AddUserToFavoriteListRunner(this, favoriteUserXuid));
    }

    public AsyncResult<Boolean> removeUserFromFavoriteList(boolean forceRefresh, String favoriteUserXuid) {
        XLEAssert.assertIsNotUIThread();
        XLEAssert.assertNotNull(this.xuid);
        XLEAssert.assertNotNull(favoriteUserXuid);
        return DataLoadUtil.Load(forceRefresh, this.lifetime, (Date) null, this.removingUserFromFavoriteListLoadingStatus, new RemoveUserFromFavoriteListRunner(this, favoriteUserXuid));
    }

    public AsyncResult<AddFollowingUserResponseContainer.AddFollowingUserResponse> addUserToFollowingList(boolean forceRefresh, String followingUserXuid) {
        XLEAssert.assertIsNotUIThread();
        XLEAssert.assertNotNull(this.xuid);
        XLEAssert.assertNotNull(followingUserXuid);
        return DataLoadUtil.Load(forceRefresh, this.lifetime, (Date) null, this.addingUserToFollowingListLoadingStatus, new AddUserToFollowingListRunner(this, followingUserXuid));
    }

    public AsyncResult<ProfileSummaryResultContainer.ProfileSummaryResult> loadProfileSummary(boolean forceRefresh) {
        if (this.profileSummaryLoadingStatus == null) {
            this.profileSummaryLoadingStatus = new SingleEntryLoadingStatus();
        }
        return DataLoadUtil.Load(forceRefresh, this.lifetime, this.lastRefreshProfileSummary, this.profileSummaryLoadingStatus, new GetProfileSummaryRunner(this, this.xuid));
    }

    public AsyncResult<Boolean> removeUserFromFollowingList(boolean forceRefresh, String followingUserXuid) {
        XLEAssert.assertIsNotUIThread();
        XLEAssert.assertNotNull(this.xuid);
        XLEAssert.assertNotNull(followingUserXuid);
        return DataLoadUtil.Load(forceRefresh, this.lifetime, (Date) null, this.removingUserFromFollowingListLoadingStatus, new RemoveUserFromFollowingListRunner(this, followingUserXuid));
    }

    public AsyncResult<NeverListResultContainer.NeverListResult> loadUserNeverList(boolean forceRefresh) {
        return DataLoadUtil.Load(forceRefresh, this.lifetime, this.lastRefreshNeverList, this.neverListLoadingStatus, new GetNeverListRunner(this, this.xuid));
    }

    public AsyncResult<Boolean> addUserToNeverList(boolean forceRefresh, String blockUserXuid) {
        XLEAssert.assertIsNotUIThread();
        XLEAssert.assertNotNull(this.xuid);
        XLEAssert.assertNotNull(blockUserXuid);
        return DataLoadUtil.Load(forceRefresh, this.lifetime, (Date) null, this.addingUserToNeverListLoadingStatus, new PutUserToNeverListRunner(this, this.xuid, blockUserXuid));
    }

    public AsyncResult<Boolean> removeUserFromNeverList(boolean forceRefresh, String unblockUserXuid) {
        XLEAssert.assertIsNotUIThread();
        XLEAssert.assertNotNull(this.xuid);
        XLEAssert.assertNotNull(unblockUserXuid);
        return DataLoadUtil.Load(forceRefresh, this.lifetime, (Date) null, this.removingUserFromNeverListLoadingStatus, new RemoveUserFromNeverListRunner(this, this.xuid, unblockUserXuid));
    }

    public AsyncResult<MutedListResultContainer.MutedListResult> loadUserMutedList(boolean forceRefresh) {
        return DataLoadUtil.Load(forceRefresh, this.lifetime, this.lastRefreshMutedList, this.mutedListLoadingStatus, new GetMutedListRunner(this, this.xuid));
    }

    public AsyncResult<Boolean> addUserToMutedList(boolean forceRefresh, String mutedUserXuid) {
        XLEAssert.assertIsNotUIThread();
        XLEAssert.assertNotNull(this.xuid);
        XLEAssert.assertNotNull(mutedUserXuid);
        return DataLoadUtil.Load(forceRefresh, this.lifetime, (Date) null, this.addingUserToMutedListLoadingStatus, new PutUserToMutedListRunner(this, this.xuid, mutedUserXuid));
    }

    public AsyncResult<Boolean> removeUserFromMutedList(boolean forceRefresh, String mutedUserXuid) {
        XLEAssert.assertIsNotUIThread();
        XLEAssert.assertNotNull(this.xuid);
        XLEAssert.assertNotNull(mutedUserXuid);
        return DataLoadUtil.Load(forceRefresh, this.lifetime, (Date) null, this.removingUserFromMutedListLoadingStatus, new RemoveUserFromMutedListRunner(this, this.xuid, mutedUserXuid));
    }

    public AsyncResult<Boolean> submitFeedbackForUser(boolean forceRefresh, FeedbackType feedbackType, String textReason) {
        XLEAssert.assertIsNotUIThread();
        XLEAssert.assertNotNull(this.xuid);
        return DataLoadUtil.Load(forceRefresh, this.lifetime, (Date) null, this.submitFeedbackForUserLoadingStatus, new SubmitFeedbackForUserRunner(this, this.xuid, feedbackType, textReason));
    }

    private void onGetPeopleHubPersonDataCompleted(AsyncResult<IPeopleHubResult.PeopleHubPersonSummary> result) {
        if (result.getStatus() == AsyncActionStatus.SUCCESS) {
            this.peopleHubPersonSummary = result.getResult();
        }
    }

    public ArrayList<FollowingSummaryResult.People> getProfileFollowingSummaryData() {
        return this.followingSummaries;
    }

    public void setProfileFollowingSummaryData(ArrayList<FollowingSummaryResult.People> people) {
        this.followingSummaries = people;
    }

    /* access modifiers changed from: private */
    public void onRemoveUserFromShareIdentityCompleted(AsyncResult<Boolean> result, ArrayList<String> xuids) {
        if (result.getStatus() == AsyncActionStatus.SUCCESS && result.getResult().booleanValue()) {
            Iterator i$ = xuids.iterator();
            while (i$.hasNext()) {
                ProfileSummaryResultContainer.ProfileSummaryResult p = getProfileModel(i$.next()).getProfileSummaryData();
                if (p != null) {
                    p.hasCallerMarkedTargetAsIdentityShared = false;
                }
            }
            ProfileModel meModel = getMeProfileModel();
            ArrayList<FollowingSummaryResult.People> followingSummaries2 = meModel.getProfileFollowingSummaryData();
            if (!XLEUtil.isNullOrEmpty(followingSummaries2)) {
                Iterator i$2 = xuids.iterator();
                while (i$2.hasNext()) {
                    String xuid2 = i$2.next();
                    Iterator i$3 = followingSummaries2.iterator();
                    while (true) {
                        if (!i$3.hasNext()) {
                            break;
                        }
                        FollowingSummaryResult.People person = i$3.next();
                        if (person.xuid.equalsIgnoreCase(xuid2)) {
                            person.isIdentityShared = false;
                            break;
                        }
                    }
                }
                meModel.setProfileFollowingSummaryData(followingSummaries2);
            }
        }
    }

    /* access modifiers changed from: private */
    public void onAddUserToShareIdentityCompleted(AsyncResult<Boolean> result, ArrayList<String> xuids) {
        if (result.getStatus() == AsyncActionStatus.SUCCESS && result.getResult().booleanValue()) {
            Iterator i$ = xuids.iterator();
            while (i$.hasNext()) {
                ProfileSummaryResultContainer.ProfileSummaryResult p = getProfileModel(i$.next()).getProfileSummaryData();
                if (p != null) {
                    p.hasCallerMarkedTargetAsIdentityShared = true;
                }
            }
            ProfileModel meModel = getMeProfileModel();
            ArrayList<FollowingSummaryResult.People> followingSummaries2 = meModel.getProfileFollowingSummaryData();
            if (!XLEUtil.isNullOrEmpty(followingSummaries2)) {
                Iterator i$2 = xuids.iterator();
                while (i$2.hasNext()) {
                    String xuid2 = i$2.next();
                    Iterator i$3 = followingSummaries2.iterator();
                    while (true) {
                        if (!i$3.hasNext()) {
                            break;
                        }
                        FollowingSummaryResult.People person = i$3.next();
                        if (person.xuid.equalsIgnoreCase(xuid2)) {
                            person.isIdentityShared = true;
                            break;
                        }
                    }
                }
                meModel.setProfileFollowingSummaryData(followingSummaries2);
            }
        }
    }

    /* access modifiers changed from: private */
    public void onAddUserToFavoriteListCompleted(AsyncResult<Boolean> result, String xuid2) {
        if (result.getStatus() == AsyncActionStatus.SUCCESS && result.getResult().booleanValue() && this.following != null) {
            ArrayList<FollowersData> newFavoritesData = new ArrayList<>();
            Iterator i$ = this.following.iterator();
            while (i$.hasNext()) {
                FollowersData fdata = i$.next();
                if (fdata.xuid.equals(xuid2)) {
                    fdata.isFavorite = true;
                }
                if (fdata.isFavorite) {
                    newFavoritesData.add(fdata);
                }
            }
            Collections.sort(newFavoritesData, new FollowingAndFavoritesComparator());
            this.favorites = newFavoritesData;
            notifyObservers(new AsyncResult(new UpdateData(UpdateType.UpdateFriend, true), this, (XLEException) null));
        }
    }

    /* access modifiers changed from: private */
    public void onRemoveUserFromFavoriteListCompleted(AsyncResult<Boolean> result, String xuid2) {
        if (result.getStatus() == AsyncActionStatus.SUCCESS && result.getResult().booleanValue() && this.following != null) {
            ArrayList<FollowersData> newFavoritesData = new ArrayList<>();
            Iterator i$ = this.following.iterator();
            while (i$.hasNext()) {
                FollowersData fdata = i$.next();
                if (fdata.xuid.equals(xuid2)) {
                    fdata.isFavorite = false;
                }
                if (fdata.isFavorite) {
                    newFavoritesData.add(fdata);
                }
            }
            this.favorites = newFavoritesData;
            notifyObservers(new AsyncResult(new UpdateData(UpdateType.UpdateFriend, true), this, (XLEException) null));
        }
    }

    /* access modifiers changed from: private */
    public void onAddUserToFollowingListCompleted(AsyncResult<AddFollowingUserResponseContainer.AddFollowingUserResponse> result, String xuid2) {
        ProfileModel newUserProfileModel = getProfileModel(xuid2);
        XLEAssert.assertNotNull(newUserProfileModel);
        this.addUserToFollowingResponse = result.getResult();
        if (result.getStatus() == AsyncActionStatus.SUCCESS && this.addUserToFollowingResponse != null && this.addUserToFollowingResponse.getAddFollowingRequestStatus()) {
            boolean isAlreadyFollowing = false;
            ArrayList<FollowersData> newFollowersData = new ArrayList<>();
            if (this.following != null) {
                Iterator i$ = this.following.iterator();
                while (i$.hasNext()) {
                    FollowersData fdata = i$.next();
                    newFollowersData.add(fdata);
                    if (fdata.xuid.equals(xuid2)) {
                        isAlreadyFollowing = true;
                    }
                }
            }
            if (!isAlreadyFollowing) {
                FollowersData newFollowingUser = new FollowersData();
                newFollowingUser.xuid = xuid2;
                newFollowingUser.isFavorite = false;
                newFollowingUser.status = UserStatus.Offline;
                newFollowingUser.userProfileData = new UserProfileData();
                newFollowingUser.userProfileData.accountTier = newUserProfileModel.getAccountTier();
                newFollowingUser.userProfileData.appDisplayName = newUserProfileModel.getAppDisplayName();
                newFollowingUser.userProfileData.gamerScore = newUserProfileModel.getGamerScore();
                newFollowingUser.userProfileData.gamerTag = newUserProfileModel.getGamerTag();
                newFollowingUser.userProfileData.profileImageUrl = newUserProfileModel.getProfileImageUrl();
                newFollowersData.add(newFollowingUser);
                Collections.sort(newFollowersData, new FollowingAndFavoritesComparator());
            }
            this.following = newFollowersData;
            notifyObservers(new AsyncResult(new UpdateData(UpdateType.UpdateFriend, true), this, (XLEException) null));
        } else if (result.getStatus() != AsyncActionStatus.SUCCESS || (this.addUserToFollowingResponse.code != 1028 && !this.addUserToFollowingResponse.getAddFollowingRequestStatus())) {
            this.addUserToFollowingResponse = null;
        }
    }

    /* access modifiers changed from: private */
    public void onGetProfileSummaryCompleted(AsyncResult<ProfileSummaryResultContainer.ProfileSummaryResult> result) {
        if (result.getStatus() == AsyncActionStatus.SUCCESS) {
            this.lastRefreshProfileSummary = new Date();
            this.profileSummary = result.getResult();
            notifyObservers(new AsyncResult(new UpdateData(UpdateType.ActivityAlertsSummary, true), this, (XLEException) null));
        }
    }

    /* access modifiers changed from: private */
    public void onRemoveUserFromFollowingListCompleted(AsyncResult<Boolean> result, String xuid2) {
        if (result.getStatus() == AsyncActionStatus.SUCCESS && result.getResult().booleanValue() && this.following != null) {
            ArrayList<FollowersData> newFollowersData = new ArrayList<>();
            ArrayList<FollowersData> newFavoritesData = new ArrayList<>();
            Iterator i$ = this.following.iterator();
            while (i$.hasNext()) {
                FollowersData fData = i$.next();
                if (!fData.xuid.equals(xuid2)) {
                    newFollowersData.add(fData);
                    if (fData.isFavorite) {
                        newFavoritesData.add(fData);
                    }
                }
            }
            this.following = newFollowersData;
            this.favorites = newFavoritesData;
            notifyObservers(new AsyncResult(new UpdateData(UpdateType.UpdateFriend, true), this, (XLEException) null));
        }
    }

    /* access modifiers changed from: private */
    public void onGetPresenceDataCompleted(AsyncResult<IFollowerPresenceResult.UserPresence> result) {
        if (result.getStatus() == AsyncActionStatus.SUCCESS) {
            this.lastRefreshPresenceData = new Date();
            this.presenceData = result.getResult();
        }
    }

    /* access modifiers changed from: private */
    public void onGetNeverListCompleted(AsyncResult<NeverListResultContainer.NeverListResult> result) {
        if (result.getStatus() == AsyncActionStatus.SUCCESS) {
            NeverListResultContainer.NeverListResult data = result.getResult();
            this.lastRefreshNeverList = new Date();
            if (data != null) {
                this.neverList = data;
            } else {
                this.neverList = new NeverListResultContainer.NeverListResult();
            }
        }
    }

    /* access modifiers changed from: private */
    public void onPutUserToNeverListCompleted(AsyncResult<Boolean> result, String xuid2) {
        if (result.getStatus() == AsyncActionStatus.SUCCESS && result.getResult().booleanValue()) {
            if (this.neverList == null) {
                this.neverList = new NeverListResultContainer.NeverListResult();
            }
            if (!this.neverList.contains(xuid2)) {
                this.neverList.add(xuid2);
            }
        }
    }

    /* access modifiers changed from: private */
    public void onRemoveUserFromNeverListCompleted(AsyncResult<Boolean> result, String xuid2) {
        if (result.getStatus() == AsyncActionStatus.SUCCESS && result.getResult().booleanValue() && this.neverList != null && this.neverList.contains(xuid2)) {
            this.neverList.remove(xuid2);
        }
    }

    /* access modifiers changed from: private */
    public void onGetMutedListCompleted(AsyncResult<MutedListResultContainer.MutedListResult> result) {
        if (result.getStatus() == AsyncActionStatus.SUCCESS) {
            MutedListResultContainer.MutedListResult data = result.getResult();
            this.lastRefreshMutedList = new Date();
            if (data != null) {
                this.mutedList = data;
            } else {
                this.mutedList = new MutedListResultContainer.MutedListResult();
            }
        }
    }

    /* access modifiers changed from: private */
    public void onPutUserToMutedListCompleted(AsyncResult<Boolean> result, String xuid2) {
        if (result.getStatus() == AsyncActionStatus.SUCCESS && result.getResult().booleanValue()) {
            if (this.mutedList == null) {
                this.mutedList = new MutedListResultContainer.MutedListResult();
            }
            if (!this.mutedList.contains(xuid2)) {
                this.mutedList.add(xuid2);
            }
        }
    }

    /* access modifiers changed from: private */
    public void onRemoveUserFromMutedListCompleted(AsyncResult<Boolean> result, String xuid2) {
        if (result.getStatus() == AsyncActionStatus.SUCCESS && result.getResult().booleanValue() && this.mutedList != null && this.mutedList.contains(xuid2)) {
            this.mutedList.remove(xuid2);
        }
    }

    /* access modifiers changed from: private */
    public void onSubmitFeedbackForUserCompleted(AsyncResult<Boolean> asyncResult) {
    }

    /* access modifiers changed from: private */
    public void onGetPeopleHubRecommendationsCompleted(AsyncResult<IPeopleHubResult.PeopleHubPeopleSummary> result) {
        if (result.getStatus() == AsyncActionStatus.SUCCESS) {
            IPeopleHubResult.PeopleHubPeopleSummary data = result.getResult();
            if (data == null) {
                this.peopleHubRecommendationsRaw = null;
                this.peopleHubRecommendations = null;
                return;
            }
            this.peopleHubRecommendationsRaw = data;
            FriendFinderState.FriendsFinderStateResult friendFinderState = FacebookManager.getInstance().getFacebookFriendFinderState();
            buildRecommendationsList(friendFinderState != null && friendFinderState.getLinkedAccountOptInStatus() == FriendFinderState.LinkedAccountOptInStatus.ShowPrompt);
            this.lastRefreshPeopleHubRecommendations = new Date();
        }
    }

    private void buildRecommendationsList(boolean showLinkToFacebbokButton) {
        this.peopleHubRecommendations = new ArrayList<>();
        if (showLinkToFacebbokButton) {
            this.peopleHubRecommendations.add(0, new RecommendationsPeopleData(true, FollowersData.DummyType.DUMMY_LINK_TO_FACEBOOK));
        }
        if (this.peopleHubRecommendationsRaw != null && !XLEUtil.isNullOrEmpty(this.peopleHubRecommendationsRaw.people)) {
            Iterator i$ = this.peopleHubRecommendationsRaw.people.iterator();
            while (i$.hasNext()) {
                this.peopleHubRecommendations.add(new RecommendationsPeopleData(i$.next()));
            }
        }
    }

    public void updateWithNewData(AsyncResult<ProfileData> asyncResult) {
        ProfileData profileData;
        boolean z;
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        super.updateWithNewData(asyncResult);
        if (asyncResult.getStatus() == AsyncActionStatus.SUCCESS && (profileData = asyncResult.getResult()) != null) {
            if (isMeProfile()) {
                z = profileData.getShareRealName();
            } else {
                z = true;
            }
            this.shareRealName = z;
            this.shareRealNameStatus = profileData.getShareRealNameStatus();
            Log.i("ProfileModel", "shareRealNameStatus: " + this.shareRealNameStatus);
            this.sharingRealNameTransitively = profileData.getSharingRealNameTransitively();
            IUserProfileResult.UserProfileResult userProfileResult = profileData.getProfileResult();
            if (!(userProfileResult == null || userProfileResult.profileUsers == null)) {
                this.profileUser = userProfileResult.profileUsers.get(0);
                this.profileImageUrl = null;
            }
        }
        notifyObservers(new AsyncResult(new UpdateData(UpdateType.ProfileData, true), this, asyncResult.getException()));
    }

    /* access modifiers changed from: private */
    public void updateWithProfileData(AsyncResult<ProfileData> asyncResult, boolean attemptedToLoadEssentialDataOnly) {
        updateWithNewData(asyncResult);
        if (attemptedToLoadEssentialDataOnly) {
            invalidateData();
        }
    }

    private class GetProfileRunner extends IDataLoaderRunnable<ProfileData> {
        private ProfileModel caller;
        private boolean loadEssentialsOnly;
        /* access modifiers changed from: private */
        public String xuid;

        public GetProfileRunner(ProfileModel caller2, String xuid2, boolean loadEssentialsOnly2) {
            this.caller = caller2;
            this.xuid = xuid2;
            this.loadEssentialsOnly = loadEssentialsOnly2;
        }

        public ProfileData buildData() throws XLEException {
            final ISLSServiceManager serviceManager = ServiceManagerFactory.getInstance().getSLSServiceManager();
            ArrayList<String> xuids = new ArrayList<>();
            xuids.add(this.xuid);
            IUserProfileResult.UserProfileResult response = serviceManager.getUserProfileInfo(UserProfileRequest.getUserProfileRequestBody(new UserProfileRequest(xuids, this.loadEssentialsOnly)));
            if (ProjectSpecificDataProvider.getInstance().getXuidString().equalsIgnoreCase(this.xuid)) {
                if (!(response == null || response.profileUsers == null || response.profileUsers.size() <= 0)) {
                    final IUserProfileResult.ProfileUser profileUser = response.profileUsers.get(0);
                    profileUser.setPrivilieges(serviceManager.getXTokenPrivileges());
                    try {
                        String url = profileUser.getSettingValue(UserProfileSetting.PreferredColor);
                        if (url != null && url.length() > 0) {
                            profileUser.colors = serviceManager.getProfilePreferredColor(url);
                        }
                    } catch (Throwable th) {
                    }
                    XLEThreadPool.networkOperationsThreadPool.run(new Runnable() {
                        public void run() {
                            try {
                                FamilySettings familySettings = serviceManager.getFamilySettings(GetProfileRunner.this.xuid);
                                if (familySettings != null && familySettings.familyUsers != null) {
                                    for (int i = 0; i < familySettings.familyUsers.size(); i++) {
                                        if (familySettings.familyUsers.get(i).xuid.equalsIgnoreCase(GetProfileRunner.this.xuid)) {
                                            profileUser.canViewTVAdultContent = familySettings.familyUsers.get(i).canViewTVAdultContent;
                                            profileUser.setmaturityLevel(familySettings.familyUsers.get(i).maturityLevel);
                                            return;
                                        }
                                    }
                                }
                            } catch (Throwable th) {
                            }
                        }
                    });
                }
            } else if (!(response == null || response.profileUsers == null || response.profileUsers.size() <= 0)) {
                IUserProfileResult.ProfileUser profileUser2 = response.profileUsers.get(0);
                try {
                    String url2 = profileUser2.getSettingValue(UserProfileSetting.PreferredColor);
                    if (url2 != null && url2.length() > 0) {
                        profileUser2.colors = serviceManager.getProfilePreferredColor(url2);
                    }
                } catch (Throwable th2) {
                }
            }
            boolean shareRealName = false;
            String shareRealNameStatus = null;
            boolean sharingRealNameTransitively = false;
            if (this.xuid != null && this.xuid.compareToIgnoreCase(ProjectSpecificDataProvider.getInstance().getXuidString()) == 0) {
                try {
                    PrivacySettingsResult privacyResult = serviceManager.getUserProfilePrivacySettings();
                    shareRealNameStatus = privacyResult.getShareRealNameStatus();
                    if (ShareRealNameSettingFilter.Blocked.toString().compareTo(shareRealNameStatus) != 0) {
                        shareRealName = true;
                    } else {
                        shareRealName = false;
                    }
                    sharingRealNameTransitively = privacyResult.getSharingRealNameTransitively();
                } catch (Exception e) {
                }
            }
            return new ProfileData(response, shareRealName, shareRealNameStatus, sharingRealNameTransitively);
        }

        public void onPreExecute() {
        }

        public void onPostExcute(AsyncResult<ProfileData> result) {
            this.caller.updateWithProfileData(result, this.loadEssentialsOnly);
        }

        public long getDefaultErrorCode() {
            return XLEErrorCode.FAILED_TO_GET_USER_PROFILE_INFO;
        }
    }

    private class FollowingAndFavoritesComparator implements Comparator<FollowersData> {
        private FollowingAndFavoritesComparator() {
        }

        public int compare(FollowersData object1, FollowersData object2) {
            return object1.userProfileData.appDisplayName.compareToIgnoreCase(object2.userProfileData.appDisplayName);
        }
    }

    private class RemoveUsersFromShareIdentityListRunner extends IDataLoaderRunnable<Boolean> {
        private ProfileModel caller;
        private ArrayList<String> userIds;

        public RemoveUsersFromShareIdentityListRunner(ProfileModel caller2, ArrayList<String> users) {
            this.caller = caller2;
            this.userIds = users;
        }

        public Boolean buildData() throws XLEException {
            return Boolean.valueOf(ServiceManagerFactory.getInstance().getSLSServiceManager().removeFriendFromShareIdentitySetting(this.caller.xuid, AddShareIdentityRequest.getAddShareIdentityRequestBody(new AddShareIdentityRequest(this.userIds))));
        }

        public void onPreExecute() {
        }

        public long getDefaultErrorCode() {
            return XLEErrorCode.FAILED_TO_REMOVE_FROM_SHARE_IDENTIY;
        }

        public void onPostExcute(AsyncResult<Boolean> result) {
            this.caller.onRemoveUserFromShareIdentityCompleted(result, this.userIds);
        }
    }

    private class AddUsersToShareIdentityListRunner extends IDataLoaderRunnable<Boolean> {
        private ProfileModel caller;
        private ArrayList<String> userIds;

        public AddUsersToShareIdentityListRunner(ProfileModel caller2, ArrayList<String> users) {
            this.caller = caller2;
            this.userIds = users;
        }

        public Boolean buildData() throws XLEException {
            return Boolean.valueOf(ServiceManagerFactory.getInstance().getSLSServiceManager().addFriendToShareIdentitySetting(this.caller.xuid, AddShareIdentityRequest.getAddShareIdentityRequestBody(new AddShareIdentityRequest(this.userIds))));
        }

        public void onPreExecute() {
        }

        public long getDefaultErrorCode() {
            return XLEErrorCode.FAILED_TO_ADD_TO_SHARE_IDENTIY;
        }

        public void onPostExcute(AsyncResult<Boolean> result) {
            this.caller.onAddUserToShareIdentityCompleted(result, this.userIds);
        }
    }

    private class AddUserToFavoriteListRunner extends IDataLoaderRunnable<Boolean> {
        private ProfileModel caller;
        private String favoriteUserXuid;

        public AddUserToFavoriteListRunner(ProfileModel caller2, String favoriteUserXuid2) {
            this.caller = caller2;
            this.favoriteUserXuid = favoriteUserXuid2;
        }

        public Boolean buildData() throws XLEException {
            ArrayList<String> xuids = new ArrayList<>();
            xuids.add(this.favoriteUserXuid);
            return Boolean.valueOf(ServiceManagerFactory.getInstance().getSLSServiceManager().addUserToFavoriteList(FavoriteListRequest.getFavoriteListRequestBody(new FavoriteListRequest(xuids))));
        }

        public void onPreExecute() {
        }

        public void onPostExcute(AsyncResult<Boolean> result) {
            this.caller.onAddUserToFavoriteListCompleted(result, this.favoriteUserXuid);
        }

        public long getDefaultErrorCode() {
            return XLEErrorCode.FAILED_TO_ADD_USER_TO_FAVORITELIST;
        }
    }

    private class RemoveUserFromFavoriteListRunner extends IDataLoaderRunnable<Boolean> {
        private ProfileModel caller;
        private String favoriteUserXuid;

        public RemoveUserFromFavoriteListRunner(ProfileModel caller2, String favoriteUserXuid2) {
            this.caller = caller2;
            this.favoriteUserXuid = favoriteUserXuid2;
        }

        public Boolean buildData() throws XLEException {
            ArrayList<String> xuids = new ArrayList<>();
            xuids.add(this.favoriteUserXuid);
            return Boolean.valueOf(ServiceManagerFactory.getInstance().getSLSServiceManager().removeUserFromFavoriteList(FavoriteListRequest.getFavoriteListRequestBody(new FavoriteListRequest(xuids))));
        }

        public void onPreExecute() {
        }

        public void onPostExcute(AsyncResult<Boolean> result) {
            this.caller.onRemoveUserFromFavoriteListCompleted(result, this.favoriteUserXuid);
        }

        public long getDefaultErrorCode() {
            return XLEErrorCode.FAILED_TO_REMOVE_USER_FROM_FAVORITELIST;
        }
    }

    private class AddUserToFollowingListRunner extends IDataLoaderRunnable<AddFollowingUserResponseContainer.AddFollowingUserResponse> {
        private ProfileModel caller;
        private String followingUserXuid;

        public AddUserToFollowingListRunner(ProfileModel caller2, String followingUserXuid2) {
            this.caller = caller2;
            this.followingUserXuid = followingUserXuid2;
        }

        public AddFollowingUserResponseContainer.AddFollowingUserResponse buildData() throws XLEException {
            ArrayList<String> xuids = new ArrayList<>();
            xuids.add(this.followingUserXuid);
            return ServiceManagerFactory.getInstance().getSLSServiceManager().addUserToFollowingList(FavoriteListRequest.getFavoriteListRequestBody(new FavoriteListRequest(xuids)));
        }

        public void onPreExecute() {
        }

        public void onPostExcute(AsyncResult<AddFollowingUserResponseContainer.AddFollowingUserResponse> result) {
            this.caller.onAddUserToFollowingListCompleted(result, this.followingUserXuid);
        }

        public long getDefaultErrorCode() {
            return XLEErrorCode.FAILED_TO_ADD_FRIEND;
        }
    }

    private class GetProfileSummaryRunner extends IDataLoaderRunnable<ProfileSummaryResultContainer.ProfileSummaryResult> {
        private ProfileModel caller;
        private String xuid;

        public GetProfileSummaryRunner(ProfileModel caller2, String xuid2) {
            this.caller = caller2;
            this.xuid = xuid2;
        }

        public ProfileSummaryResultContainer.ProfileSummaryResult buildData() throws XLEException {
            return ServiceManagerFactory.getInstance().getSLSServiceManager().getProfileSummaryInfo(this.xuid);
        }

        public void onPreExecute() {
        }

        public void onPostExcute(AsyncResult<ProfileSummaryResultContainer.ProfileSummaryResult> result) {
            this.caller.onGetProfileSummaryCompleted(result);
        }

        public long getDefaultErrorCode() {
            return XLEErrorCode.FAILED_TO_GET_USER_PROFILE_INFO;
        }
    }

    private class RemoveUserFromFollowingListRunner extends IDataLoaderRunnable<Boolean> {
        private ProfileModel caller;
        private String followingUserXuid;

        public RemoveUserFromFollowingListRunner(ProfileModel caller2, String followingUserXuid2) {
            this.caller = caller2;
            this.followingUserXuid = followingUserXuid2;
        }

        public Boolean buildData() throws XLEException {
            ArrayList<String> xuids = new ArrayList<>();
            xuids.add(this.followingUserXuid);
            return Boolean.valueOf(ServiceManagerFactory.getInstance().getSLSServiceManager().removeUserFromFollowingList(FavoriteListRequest.getFavoriteListRequestBody(new FavoriteListRequest(xuids))));
        }

        public void onPreExecute() {
        }

        public void onPostExcute(AsyncResult<Boolean> result) {
            this.caller.onRemoveUserFromFollowingListCompleted(result, this.followingUserXuid);
        }

        public long getDefaultErrorCode() {
            return XLEErrorCode.FAILED_TO_REMOVE_FRIEND;
        }
    }

    private class GetPresenceDataRunner extends IDataLoaderRunnable<IFollowerPresenceResult.UserPresence> {
        private ProfileModel caller;
        private String xuid;

        public GetPresenceDataRunner(ProfileModel caller2, String xuid2) {
            this.caller = caller2;
            this.xuid = xuid2;
        }

        public IFollowerPresenceResult.UserPresence buildData() throws XLEException {
            return null;
        }

        public void onPreExecute() {
        }

        public void onPostExcute(AsyncResult<IFollowerPresenceResult.UserPresence> result) {
            this.caller.onGetPresenceDataCompleted(result);
        }

        public long getDefaultErrorCode() {
            return XLEErrorCode.FAILED_TO_GET_PROFILE_PRESENCE_DATA;
        }
    }

    private class GetNeverListRunner extends IDataLoaderRunnable<NeverListResultContainer.NeverListResult> {
        private ProfileModel caller;
        private String xuid;

        public GetNeverListRunner(ProfileModel caller2, String xuid2) {
            this.caller = caller2;
            this.xuid = xuid2;
        }

        public NeverListResultContainer.NeverListResult buildData() throws XLEException {
            return ServiceManagerFactory.getInstance().getSLSServiceManager().getNeverListInfo(this.xuid);
        }

        public void onPreExecute() {
        }

        public void onPostExcute(AsyncResult<NeverListResultContainer.NeverListResult> result) {
            this.caller.onGetNeverListCompleted(result);
        }

        public long getDefaultErrorCode() {
            return XLEErrorCode.FAILED_TO_GET_NEVERLIST_DATA;
        }
    }

    private class PutUserToNeverListRunner extends IDataLoaderRunnable<Boolean> {
        private String blockUserXuid;
        private ProfileModel caller;
        private String xuid;

        public PutUserToNeverListRunner(ProfileModel caller2, String xuid2, String blockUserXuid2) {
            this.caller = caller2;
            this.xuid = xuid2;
            this.blockUserXuid = blockUserXuid2;
        }

        public Boolean buildData() throws XLEException {
            return Boolean.valueOf(ServiceManagerFactory.getInstance().getSLSServiceManager().addUserToNeverList(this.xuid, NeverListRequest.getNeverListRequestBody(new NeverListRequest(Long.parseLong(this.blockUserXuid)))));
        }

        public void onPreExecute() {
        }

        public void onPostExcute(AsyncResult<Boolean> result) {
            this.caller.onPutUserToNeverListCompleted(result, this.blockUserXuid);
        }

        public long getDefaultErrorCode() {
            return XLEErrorCode.FAILED_TO_BLOCK_USER;
        }
    }

    private class RemoveUserFromNeverListRunner extends IDataLoaderRunnable<Boolean> {
        private ProfileModel caller;
        private String unblockUserXuid;
        private String xuid;

        public RemoveUserFromNeverListRunner(ProfileModel caller2, String xuid2, String unblockUserXuid2) {
            this.caller = caller2;
            this.xuid = xuid2;
            this.unblockUserXuid = unblockUserXuid2;
        }

        public Boolean buildData() throws XLEException {
            return Boolean.valueOf(ServiceManagerFactory.getInstance().getSLSServiceManager().removeUserFromNeverList(this.xuid, NeverListRequest.getNeverListRequestBody(new NeverListRequest(Long.parseLong(this.unblockUserXuid)))));
        }

        public void onPreExecute() {
        }

        public void onPostExcute(AsyncResult<Boolean> result) {
            this.caller.onRemoveUserFromNeverListCompleted(result, this.unblockUserXuid);
        }

        public long getDefaultErrorCode() {
            return XLEErrorCode.FAILED_TO_REMOVE_USER_FROM_NEVERLIST;
        }
    }

    private class GetMutedListRunner extends IDataLoaderRunnable<MutedListResultContainer.MutedListResult> {
        private ProfileModel caller;
        private String xuid;

        public GetMutedListRunner(ProfileModel caller2, String xuid2) {
            this.caller = caller2;
            this.xuid = xuid2;
        }

        public MutedListResultContainer.MutedListResult buildData() throws XLEException {
            return ServiceManagerFactory.getInstance().getSLSServiceManager().getMutedListInfo(this.xuid);
        }

        public void onPreExecute() {
        }

        public void onPostExcute(AsyncResult<MutedListResultContainer.MutedListResult> result) {
            this.caller.onGetMutedListCompleted(result);
        }

        public long getDefaultErrorCode() {
            return XLEErrorCode.FAILED_TO_GET_MUTED_LIST;
        }
    }

    private class PutUserToMutedListRunner extends IDataLoaderRunnable<Boolean> {
        private ProfileModel caller;
        private String mutedUserXuid;
        private String xuid;

        public PutUserToMutedListRunner(ProfileModel caller2, String xuid2, String mutedUserXuid2) {
            this.caller = caller2;
            this.xuid = xuid2;
            this.mutedUserXuid = mutedUserXuid2;
        }

        public Boolean buildData() throws XLEException {
            return Boolean.valueOf(ServiceManagerFactory.getInstance().getSLSServiceManager().addUserToMutedList(this.xuid, MutedListRequest.getNeverListRequestBody(new MutedListRequest(Long.parseLong(this.mutedUserXuid)))));
        }

        public void onPreExecute() {
        }

        public void onPostExcute(AsyncResult<Boolean> result) {
            this.caller.onPutUserToMutedListCompleted(result, this.mutedUserXuid);
        }

        public long getDefaultErrorCode() {
            return XLEErrorCode.FAILED_TO_MUTE_USER;
        }
    }

    private class RemoveUserFromMutedListRunner extends IDataLoaderRunnable<Boolean> {
        private ProfileModel caller;
        private String unmutedUserXuid;
        private String xuid;

        public RemoveUserFromMutedListRunner(ProfileModel caller2, String xuid2, String unmutedUserXuid2) {
            this.caller = caller2;
            this.xuid = xuid2;
            this.unmutedUserXuid = unmutedUserXuid2;
        }

        public Boolean buildData() throws XLEException {
            return Boolean.valueOf(ServiceManagerFactory.getInstance().getSLSServiceManager().removeUserFromMutedList(this.xuid, MutedListRequest.getNeverListRequestBody(new MutedListRequest(Long.parseLong(this.unmutedUserXuid)))));
        }

        public void onPreExecute() {
        }

        public void onPostExcute(AsyncResult<Boolean> result) {
            this.caller.onRemoveUserFromMutedListCompleted(result, this.unmutedUserXuid);
        }

        public long getDefaultErrorCode() {
            return XLEErrorCode.FAILED_TO_UNMUTE_USER;
        }
    }

    private class SubmitFeedbackForUserRunner extends IDataLoaderRunnable<Boolean> {
        private ProfileModel caller;
        private FeedbackType feedbackType;
        private String textReason;
        private String xuid;

        public SubmitFeedbackForUserRunner(ProfileModel caller2, String xuid2, FeedbackType feedbackType2, String textReason2) {
            this.caller = caller2;
            this.xuid = xuid2;
            this.feedbackType = feedbackType2;
            this.textReason = textReason2;
        }

        public Boolean buildData() throws XLEException {
            return Boolean.valueOf(ServiceManagerFactory.getInstance().getSLSServiceManager().submitFeedback(this.xuid, SubmitFeedbackRequest.getSubmitFeedbackRequestBody(new SubmitFeedbackRequest(Long.parseLong(this.xuid), (String) null, this.feedbackType, this.textReason, (String) null, (String) null))));
        }

        public void onPreExecute() {
        }

        public void onPostExcute(AsyncResult<Boolean> result) {
            this.caller.onSubmitFeedbackForUserCompleted(result);
        }

        public long getDefaultErrorCode() {
            return XLEErrorCode.FAILED_TO_SUBMIT_FEEDBACK;
        }
    }

    private class GetPeopleHubRecommendationRunner extends IDataLoaderRunnable<IPeopleHubResult.PeopleHubPeopleSummary> {
        private ProfileModel caller;
        private String xuid;

        public GetPeopleHubRecommendationRunner(ProfileModel caller2, String xuid2) {
            this.caller = caller2;
            this.xuid = xuid2;
        }

        public IPeopleHubResult.PeopleHubPeopleSummary buildData() throws XLEException {
            IPeopleHubResult.PeopleHubPeopleSummary result = new IPeopleHubResult.PeopleHubPeopleSummary();
            if (JavaUtil.isNullOrEmpty(this.xuid) || !this.xuid.equalsIgnoreCase(ProjectSpecificDataProvider.getInstance().getXuidString())) {
                return result;
            }
            return ServiceManagerFactory.getInstance().getSLSServiceManager().getPeopleHubRecommendations();
        }

        public void onPreExecute() {
        }

        public void onPostExcute(AsyncResult<IPeopleHubResult.PeopleHubPeopleSummary> result) {
            this.caller.onGetPeopleHubRecommendationsCompleted(result);
        }

        public long getDefaultErrorCode() {
            return 11;
        }
    }
}
