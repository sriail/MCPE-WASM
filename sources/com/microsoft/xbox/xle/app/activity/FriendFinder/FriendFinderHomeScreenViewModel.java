package com.microsoft.xbox.xle.app.activity.FriendFinder;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import com.microsoft.onlineid.Ticket;
import com.microsoft.onlineid.UserAccount;
import com.microsoft.xbox.idp.interop.LocalConfig;
import com.microsoft.xbox.idp.jobs.JobSilentSignIn;
import com.microsoft.xbox.idp.jobs.MSAJob;
import com.microsoft.xbox.service.model.ProfileModel;
import com.microsoft.xbox.service.model.UpdateData;
import com.microsoft.xbox.service.model.UpdateType;
import com.microsoft.xbox.service.model.friendfinder.FriendFinderModel;
import com.microsoft.xbox.service.model.friendfinder.FriendFinderState;
import com.microsoft.xbox.service.model.friendfinder.FriendFinderType;
import com.microsoft.xbox.service.network.managers.IPeopleHubResult;
import com.microsoft.xbox.service.network.managers.IUserProfileResult;
import com.microsoft.xbox.service.network.managers.ServiceManagerFactory;
import com.microsoft.xbox.service.network.managers.friendfinder.FacebookManager;
import com.microsoft.xbox.service.network.managers.friendfinder.UploadContactsAsyncTask;
import com.microsoft.xbox.toolkit.AsyncActionStatus;
import com.microsoft.xbox.toolkit.AsyncResult;
import com.microsoft.xbox.toolkit.DataLoadUtil;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.NetworkAsyncTask;
import com.microsoft.xbox.toolkit.ProjectSpecificDataProvider;
import com.microsoft.xbox.toolkit.SingleEntryLoadingStatus;
import com.microsoft.xbox.toolkit.XLEErrorCode;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.network.IDataLoaderRunnable;
import com.microsoft.xbox.toolkit.ui.ActivityParameters;
import com.microsoft.xbox.toolkit.ui.NavigationManager;
import com.microsoft.xbox.toolkit.ui.ScreenLayout;
import com.microsoft.xbox.xle.app.FriendFinderSettings;
import com.microsoft.xbox.xle.app.activity.Profile.ProfileScreen;
import com.microsoft.xbox.xle.app.adapter.FriendFinderHomeScreenAdapter;
import com.microsoft.xbox.xle.telemetry.helpers.UTCFriendFinder;
import com.microsoft.xbox.xle.viewmodel.ViewModelBase;
import com.microsoft.xboxtcui.R;
import com.microsoft.xboxtcui.XboxTcuiSdk;
import java.util.Date;

public class FriendFinderHomeScreenViewModel extends ViewModelBase implements MSAJob.Callbacks, FriendFinderModel.LoadFailedCallback {
    private static final int MAX_SEARCH_LENGTH = 256;
    private static final String MSA_TAG = "FriendFinder.MSA";
    private static final String POLICY = "mbi_ssl";
    private static final String SCOPE = "ssl.live.com";
    private FBSettingsAsyncTask fbSettingsAsyncTask;
    private FBManagerAndModelInitTask initFBandModelTask;
    /* access modifiers changed from: private */
    public boolean isLoadingFriendFinderState;
    private boolean isSearchGamertagTaskRunning = false;
    /* access modifiers changed from: private */
    public boolean isUploadingContacts;
    private SingleEntryLoadingStatus searchGamertagLoadingStatus;
    private NetworkAsyncTask searchGamertagTask;
    private Boolean shouldShowDone;
    private UploadContactsAsyncTask uploadContactsAsyncTask;

    public FriendFinderHomeScreenViewModel(ScreenLayout screenLayout) {
        super(screenLayout);
        this.adapter = new FriendFinderHomeScreenAdapter(this);
        this.searchGamertagLoadingStatus = new SingleEntryLoadingStatus();
    }

    public boolean facebookLinked() {
        if (FacebookManager.getFacebookManagerReady().getIsReady()) {
            return FacebookManager.getInstance().isFacebookFriendFinderOptedIn();
        }
        return false;
    }

    public boolean phoneLinked() {
        FriendFinderState.FriendsFinderStateResult stateResult = FriendFinderModel.getInstance().getResult();
        return stateResult != null && stateResult.getPhoneAccountOptInStatus() == FriendFinderState.LinkedAccountOptInStatus.OptedIn;
    }

    public String getFacebookIconUri() {
        return FriendFinderSettings.getIconBySize(IPeopleHubResult.RecommendationType.FacebookFriend.name(), FriendFinderSettings.IconImageSize.MEDIUM);
    }

    public boolean shouldShowDone() {
        if (this.shouldShowDone == null) {
            ActivityParameters parameters = NavigationManager.getInstance().getActivityParameters();
            this.shouldShowDone = Boolean.valueOf(parameters != null && parameters.getFriendFinderDone());
        }
        return this.shouldShowDone.booleanValue();
    }

    public void navigateToLinkFacebook() {
        navigateToInfo(FriendFinderType.FACEBOOK);
    }

    public void navigateToLinkPhone() {
        if (hasReadContactsPermission()) {
            navigateToInfo(FriendFinderType.PHONE);
        } else {
            showContactsPermissionDialog();
        }
    }

    private void navigateToInfo(FriendFinderType infoType) {
        ActivityParameters params = new ActivityParameters();
        params.putFriendFinderType(infoType);
        try {
            NavigationManager.getInstance().PushScreen(FriendFinderInfoScreen.class, params);
        } catch (XLEException e) {
        }
    }

    public void finishFriendFinder() {
        try {
            NavigationManager.getInstance().PopAllScreens();
        } catch (XLEException e) {
        }
    }

    public void navigateToFacebookSuggestions() {
        navigateToSuggestions(FriendFinderType.FACEBOOK);
    }

    public void navigateToPhoneSuggestions() {
        if (hasReadContactsPermission()) {
            if (this.uploadContactsAsyncTask != null) {
                this.uploadContactsAsyncTask.cancel();
                this.uploadContactsAsyncTask = null;
            }
            this.uploadContactsAsyncTask = new UploadContactsAsyncTask(new UploadContactsAsyncTask.UploadContactsCompleted() {
                public void onResult(AsyncActionStatus status) {
                    boolean unused = FriendFinderHomeScreenViewModel.this.isUploadingContacts = false;
                    FriendFinderHomeScreenViewModel.this.navigateToSuggestions(FriendFinderType.PHONE);
                }
            });
            this.isUploadingContacts = true;
            updateAdapter();
            this.uploadContactsAsyncTask.load(true);
            return;
        }
        showContactsPermissionDialog();
    }

    /* access modifiers changed from: private */
    public void navigateToSuggestions(FriendFinderType type) {
        try {
            ActivityParameters parameters = new ActivityParameters();
            parameters.putFriendFinderType(type);
            NavigationManager.getInstance().PushScreen(FriendFinderSuggestionsScreen.class, parameters);
        } catch (XLEException e) {
        }
    }

    public void searchGamertag(String gamertag) {
        if (!this.isSearchGamertagTaskRunning) {
            if (this.searchGamertagTask != null) {
                this.searchGamertagTask.cancel();
                this.searchGamertagTask = null;
            }
            if (validSearchGamertag(gamertag)) {
                this.isSearchGamertagTaskRunning = true;
                this.searchGamertagTask = DataLoadUtil.StartLoadFromUI(true, new Date().getTime(), (Date) null, this.searchGamertagLoadingStatus, new SearchGamertagRunner(gamertag.trim()));
                return;
            }
            showError(R.string.FriendsHub_CouldNotFindGamer);
        }
    }

    private boolean validSearchGamertag(String gamertag) {
        return !JavaUtil.isNullOrEmpty(gamertag) && gamertag.length() <= 256 && JavaUtil.urlEncode(gamertag) != null;
    }

    private boolean hasReadContactsPermission() {
        return ContextCompat.checkSelfPermission(XboxTcuiSdk.getActivity(), "android.permission.READ_CONTACTS") == 0;
    }

    private void showContactsPermissionDialog() {
        Context context = XboxTcuiSdk.getActivity();
        String appName = context.getString(context.getApplicationInfo().labelRes);
        String message = String.format(context.getString(R.string.Contacts_Permission_Denied_Android), new Object[]{appName, appName});
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.Contacts_Permission_Denied_Header);
        builder.setMessage(message);
        builder.create().show();
    }

    /* access modifiers changed from: protected */
    public void onStartOverride() {
        FriendFinderModel.getInstance().addUniqueObserver(this);
    }

    public void onRehydrate() {
        this.adapter = new FriendFinderHomeScreenAdapter(this);
    }

    /* access modifiers changed from: protected */
    public void onStopOverride() {
        cancelActiveTasks();
        FriendFinderModel.getInstance().removeObserver(this);
    }

    private void cancelActiveTasks() {
        if (this.initFBandModelTask != null) {
            this.initFBandModelTask.cancel();
            this.initFBandModelTask = null;
        }
        if (this.fbSettingsAsyncTask != null) {
            this.fbSettingsAsyncTask.cancel();
            this.fbSettingsAsyncTask = null;
        }
        if (this.searchGamertagTask != null) {
            this.searchGamertagTask.cancel();
            this.searchGamertagTask = null;
        }
        if (this.uploadContactsAsyncTask != null) {
            this.uploadContactsAsyncTask.cancel();
            this.uploadContactsAsyncTask = null;
        }
    }

    public boolean isBusy() {
        return this.isLoadingFriendFinderState || this.isUploadingContacts;
    }

    public void load(boolean forceRefresh) {
        cancelActiveTasks();
        if (JavaUtil.isNullOrEmpty(ProjectSpecificDataProvider.getInstance().getSCDRpsTicket())) {
            new JobSilentSignIn(XboxTcuiSdk.getActivity(), "FriendFinderHome", this, SCOPE, POLICY, new LocalConfig().getCid()).start();
        }
        if (FacebookManager.getFacebookManagerReady().getIsReady()) {
            FriendFinderModel.getInstance().loadAsync(true, this);
        } else {
            this.initFBandModelTask = new FBManagerAndModelInitTask();
            this.initFBandModelTask.load(forceRefresh);
        }
        this.fbSettingsAsyncTask = new FBSettingsAsyncTask();
        this.fbSettingsAsyncTask.load(forceRefresh);
    }

    /* access modifiers changed from: protected */
    public void onFacebookInitCompleted(AsyncActionStatus status) {
        switch (status) {
            case SUCCESS:
            case NO_CHANGE:
            case NO_OP_SUCCESS:
                FriendFinderModel.getInstance().loadAsync(true, this);
                return;
            case FAIL:
            case NO_OP_FAIL:
                showError(R.string.Service_ErrorText);
                return;
            default:
                return;
        }
    }

    /* access modifiers changed from: protected */
    public void onFacebookSettingsCompleted(AsyncActionStatus status) {
        updateAdapter();
    }

    /* access modifiers changed from: private */
    public void onSearchGamertagCompleted(AsyncActionStatus status, String gamertagXuid) {
        this.isSearchGamertagTaskRunning = false;
        switch (status) {
            case SUCCESS:
            case NO_CHANGE:
            case NO_OP_SUCCESS:
                try {
                    if (!JavaUtil.isNullOrEmpty(gamertagXuid)) {
                        ActivityParameters parameters = new ActivityParameters();
                        parameters.putSelectedProfile(gamertagXuid);
                        UTCFriendFinder.trackGamertagSearchSuccess(getScreen().getName(), gamertagXuid);
                        NavigationManager.getInstance().PushScreen(ProfileScreen.class, parameters);
                        break;
                    }
                } catch (XLEException e) {
                    break;
                }
                break;
            case FAIL:
            case NO_OP_FAIL:
                showError(R.string.FriendsHub_CouldNotFindGamer);
                break;
        }
        updateAdapter();
    }

    /* access modifiers changed from: protected */
    public void updateOverride(AsyncResult<UpdateData> asyncResult) {
        if (asyncResult.getResult().getUpdateType() == UpdateType.FriendFinderFacebook && asyncResult.getResult().getIsFinal()) {
            FriendFinderState.FriendsFinderStateResult newResult = FriendFinderModel.getInstance().getResult();
            this.isLoadingFriendFinderState = false;
            if (FacebookManager.getInstance().getFacebookFriendFinderState() == null || (newResult != null && FacebookManager.getInstance().getFacebookFriendFinderState().isFacebookStateChanged(newResult))) {
                FacebookManager.getInstance().setFacebookFriendFinderState(newResult);
            }
            updateAdapter();
        }
    }

    public void onUiNeeded(MSAJob job) {
        Log.i(MSA_TAG, "onUiNeeded - ignoring and will fail phone finder if invoked.");
    }

    public void onFailure(MSAJob job, Exception e) {
        Log.i(MSA_TAG, "onFailure - ignoring and will fail phone finder if invoked. " + Log.getStackTraceString(e));
    }

    public void onUserCancel(MSAJob job) {
    }

    public void onSignedOut(MSAJob job) {
    }

    public void onAccountAcquired(MSAJob job, UserAccount userAccount) {
    }

    public void onTicketAcquired(MSAJob job, Ticket ticket) {
        Log.i(MSA_TAG, "onTicketAcquired - " + ticket.getValue());
        ProjectSpecificDataProvider.getInstance().setSCDRpsTicket(ticket.getValue());
    }

    public void onFriendFinderLoadFailed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(XboxTcuiSdk.getActivity());
        builder.setMessage(R.string.Service_ErrorText);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.OK_Text, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                try {
                    NavigationManager.getInstance().PopAllScreens();
                } catch (XLEException e) {
                }
            }
        });
        builder.create().show();
    }

    protected class FBManagerAndModelInitTask extends NetworkAsyncTask<AsyncActionStatus> {
        protected FBManagerAndModelInitTask() {
        }

        /* access modifiers changed from: protected */
        public boolean checkShouldExecute() {
            return !FacebookManager.getFacebookManagerReady().getIsReady() || FriendFinderModel.getInstance().shouldRefresh();
        }

        /* access modifiers changed from: protected */
        public void onNoAction() {
            if (FacebookManager.getFacebookManagerReady().getIsReady()) {
                FriendFinderHomeScreenViewModel.this.onFacebookInitCompleted(AsyncActionStatus.SUCCESS);
            }
        }

        /* access modifiers changed from: protected */
        public AsyncActionStatus onError() {
            return AsyncActionStatus.FAIL;
        }

        /* access modifiers changed from: protected */
        public AsyncActionStatus loadDataInBackground() {
            FacebookManager.getInstance();
            FacebookManager.getFacebookManagerReady().waitForReady();
            ProfileModel.getMeProfileModel().loadSync(true);
            return FacebookManager.getFacebookManagerReady().getIsReady() ? AsyncActionStatus.SUCCESS : AsyncActionStatus.FAIL;
        }

        /* access modifiers changed from: protected */
        public void onPreExecute() {
            boolean unused = FriendFinderHomeScreenViewModel.this.isLoadingFriendFinderState = true;
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(AsyncActionStatus status) {
            FriendFinderHomeScreenViewModel.this.onFacebookInitCompleted(status);
        }
    }

    protected class FBSettingsAsyncTask extends NetworkAsyncTask<AsyncActionStatus> {
        protected FBSettingsAsyncTask() {
        }

        /* access modifiers changed from: protected */
        public boolean checkShouldExecute() {
            return true;
        }

        /* access modifiers changed from: protected */
        public void onNoAction() {
        }

        /* access modifiers changed from: protected */
        public AsyncActionStatus onError() {
            return AsyncActionStatus.FAIL;
        }

        /* access modifiers changed from: protected */
        public AsyncActionStatus loadDataInBackground() {
            FriendFinderSettings friendFinderSettings = null;
            try {
                friendFinderSettings = ServiceManagerFactory.getInstance().getSLSServiceManager().getFriendFinderSettings();
                friendFinderSettings.getIconsFromJson(friendFinderSettings.ICONS);
            } catch (XLEException e) {
            }
            return friendFinderSettings != null ? AsyncActionStatus.SUCCESS : AsyncActionStatus.FAIL;
        }

        /* access modifiers changed from: protected */
        public void onPreExecute() {
            boolean unused = FriendFinderHomeScreenViewModel.this.isLoadingFriendFinderState = true;
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(AsyncActionStatus status) {
            FriendFinderHomeScreenViewModel.this.onFacebookSettingsCompleted(status);
        }
    }

    private class SearchGamertagRunner extends IDataLoaderRunnable<IUserProfileResult.UserProfileResult> {
        private String gamerXuid;
        private String gamertag;

        public SearchGamertagRunner(String gamertag2) {
            this.gamertag = gamertag2;
        }

        public IUserProfileResult.UserProfileResult buildData() throws XLEException {
            IUserProfileResult.UserProfileResult userProfileResult = ServiceManagerFactory.getInstance().getSLSServiceManager().SearchGamertag(this.gamertag);
            this.gamerXuid = userProfileResult.profileUsers.get(0).id;
            if (!JavaUtil.isNullOrEmpty(this.gamerXuid)) {
                return userProfileResult;
            }
            throw new XLEException(getDefaultErrorCode(), "Invalid gamertag returned from search service");
        }

        public void onPreExecute() {
        }

        public void onPostExcute(AsyncResult<IUserProfileResult.UserProfileResult> result) {
            FriendFinderHomeScreenViewModel.this.onSearchGamertagCompleted(result.getStatus(), this.gamerXuid);
        }

        public long getDefaultErrorCode() {
            return XLEErrorCode.FAILED_TO_GET_USER_PROFILE_INFO;
        }
    }
}
