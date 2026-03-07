package com.microsoft.xbox.xle.app.activity.FriendFinder;

import com.microsoft.xbox.service.model.friendfinder.FriendFinderModel;
import com.microsoft.xbox.service.model.friendfinder.FriendFinderType;
import com.microsoft.xbox.service.model.friendfinder.LinkedAccountHelpers;
import com.microsoft.xbox.service.model.friendfinder.OptInStatus;
import com.microsoft.xbox.service.model.privacy.PrivacySettings;
import com.microsoft.xbox.service.model.privacy.PrivacySettingsResult;
import com.microsoft.xbox.service.network.managers.ServiceManagerFactory;
import com.microsoft.xbox.service.network.managers.friendfinder.FacebookManager;
import com.microsoft.xbox.toolkit.AsyncActionStatus;
import com.microsoft.xbox.toolkit.NetworkAsyncTask;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.ui.ActivityParameters;
import com.microsoft.xbox.toolkit.ui.NavigationManager;
import com.microsoft.xbox.toolkit.ui.ScreenLayout;
import com.microsoft.xbox.xle.telemetry.helpers.UTCFriendFinder;
import com.microsoft.xbox.xle.viewmodel.ViewModelBase;
import com.microsoft.xboxtcui.R;
import java.util.ArrayList;

public class LinkFacebookAccountViewModel extends ViewModelBase {
    private LinkFacebookAccountAsyncTask linkAccountAsyncTask;

    public LinkFacebookAccountViewModel(ScreenLayout screenLayout) {
        super(screenLayout);
    }

    public void onStart() {
        super.onStart();
        XLEAssert.assertTrue(FacebookManager.getFacebookManagerReady().getIsReady());
    }

    /* access modifiers changed from: protected */
    public void onStartOverride() {
    }

    public void onRehydrate() {
    }

    /* access modifiers changed from: protected */
    public void onStopOverride() {
        cancelActiveTasks();
    }

    private void cancelActiveTasks() {
        if (this.linkAccountAsyncTask != null) {
            this.linkAccountAsyncTask.cancel();
        }
    }

    public boolean isBusy() {
        return true;
    }

    public void load(boolean forceRefresh) {
        cancelActiveTasks();
        this.linkAccountAsyncTask = new LinkFacebookAccountAsyncTask();
        this.linkAccountAsyncTask.load(true);
    }

    public boolean onBackButtonPressed() {
        UTCFriendFinder.trackBackButtonPressed(getScreen().getName(), FriendFinderType.FACEBOOK);
        return super.onBackButtonPressed();
    }

    /* access modifiers changed from: private */
    public void onLinkAccountAsyncTaskCompleted(AsyncActionStatus status) {
        switch (status) {
            case SUCCESS:
            case NO_CHANGE:
            case NO_OP_SUCCESS:
                FriendFinderModel.getInstance().loadAsync(true);
                try {
                    ActivityParameters parameters = new ActivityParameters();
                    parameters.putFriendFinderType(FriendFinderType.FACEBOOK);
                    UTCFriendFinder.trackFacebookLoginSuccessful(((FriendFinderLinkScreen) getScreen()).getActivityName());
                    NavigationManager.getInstance().PopScreensAndReplace(1, FriendFinderSuggestionsScreen.class, false, true, false, parameters);
                    return;
                } catch (XLEException e) {
                    return;
                }
            case FAIL:
            case NO_OP_FAIL:
                FacebookManager.getInstance().resetFacebookToken(true);
                showError(R.string.Service_ErrorText);
                NavigationManager.getInstance().OnBackButtonPressed();
                return;
            default:
                return;
        }
    }

    protected class LinkFacebookAccountAsyncTask extends NetworkAsyncTask<AsyncActionStatus> {
        protected LinkFacebookAccountAsyncTask() {
        }

        /* access modifiers changed from: protected */
        public boolean checkShouldExecute() {
            return false;
        }

        /* access modifiers changed from: protected */
        public void onNoAction() {
        }

        /* access modifiers changed from: protected */
        public AsyncActionStatus onError() {
            return null;
        }

        private boolean needUpdatePrivacy(PrivacySettings.PrivacySetting privacySetting) {
            PrivacySettings.PrivacySettingValue privacyValue = privacySetting.getPrivacySettingValue();
            return privacyValue == PrivacySettings.PrivacySettingValue.NotSet || privacyValue == PrivacySettings.PrivacySettingValue.Blocked;
        }

        /* access modifiers changed from: protected */
        public AsyncActionStatus loadDataInBackground() {
            try {
                PrivacySettings.PrivacySetting setting = ServiceManagerFactory.getInstance().getSLSServiceManager().getPrivacySetting(PrivacySettings.PrivacySettingId.ShareIdentity);
                if (setting != null && needUpdatePrivacy(setting)) {
                    ArrayList<PrivacySettings.PrivacySetting> newSettings = new ArrayList<>();
                    newSettings.add(new PrivacySettings.PrivacySetting(PrivacySettings.PrivacySettingId.ShareIdentity, PrivacySettings.PrivacySettingValue.FriendCategoryShareIdentity));
                    if (!ServiceManagerFactory.getInstance().getSLSServiceManager().setPrivacySettings(new PrivacySettingsResult(newSettings))) {
                        return AsyncActionStatus.FAIL;
                    }
                }
                if (!ServiceManagerFactory.getInstance().getSLSServiceManager().updateThirdPartyToken(LinkedAccountHelpers.LinkedAccountType.Facebook, FacebookManager.getInstance().getTokenString())) {
                    return AsyncActionStatus.FAIL;
                }
                if (!ServiceManagerFactory.getInstance().getSLSServiceManager().setFriendFinderOptInStatus(LinkedAccountHelpers.LinkedAccountType.Facebook, OptInStatus.OptedIn)) {
                    return AsyncActionStatus.FAIL;
                }
                return AsyncActionStatus.SUCCESS;
            } catch (XLEException e) {
                return AsyncActionStatus.FAIL;
            }
        }

        /* access modifiers changed from: protected */
        public void onPreExecute() {
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(AsyncActionStatus status) {
            LinkFacebookAccountViewModel.this.onLinkAccountAsyncTaskCompleted(status);
        }
    }
}
