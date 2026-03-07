package com.microsoft.xbox.xle.viewmodel;

import com.microsoft.xbox.service.model.FollowersData;
import com.microsoft.xbox.service.model.ProfileModel;
import com.microsoft.xbox.service.network.managers.AddFollowingUserResponseContainer;
import com.microsoft.xbox.toolkit.AsyncActionStatus;
import com.microsoft.xbox.toolkit.DialogManager;
import com.microsoft.xbox.toolkit.NetworkAsyncTask;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.network.ListState;
import com.microsoft.xbox.xle.app.SGProjectSpecificDialogManager;
import com.microsoft.xbox.xle.app.activity.Profile.ProfileScreenViewModel;
import com.microsoft.xbox.xle.telemetry.helpers.UTCChangeRelationship;
import com.microsoft.xboxtcui.R;
import com.microsoft.xboxtcui.XboxTcuiSdk;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class ChangeFriendshipDialogViewModel {
    private static final String TAG = ChangeFriendshipDialogViewModel.class.getSimpleName();
    private AddUserToFavoriteListAsyncTask addUserToFavoriteListAsyncTask;
    private AddUserToFollowingListAsyncTask addUserToFollowingListAsyncTask;
    private AddUserToShareIdentityListAsyncTask addUserToShareIdentityListAsyncTask;
    private HashSet<ProfileScreenViewModel.ChangeFriendshipFormOptions> changeFriendshipForm = new HashSet<>();
    /* access modifiers changed from: private */
    public boolean isAddingUserToFavoriteList;
    /* access modifiers changed from: private */
    public boolean isAddingUserToFollowingList;
    /* access modifiers changed from: private */
    public boolean isAddingUserToShareIdentityList;
    private boolean isFavorite = false;
    private boolean isFollowing = false;
    /* access modifiers changed from: private */
    public boolean isLoadingUserProfile;
    /* access modifiers changed from: private */
    public boolean isRemovingUserFromFavoriteList;
    /* access modifiers changed from: private */
    public boolean isRemovingUserFromFollowingList;
    /* access modifiers changed from: private */
    public boolean isRemovingUserFromShareIdentityList;
    private boolean isSharingRealNameEnd;
    private boolean isSharingRealNameStart;
    private LoadPersonDataAsyncTask loadProfileAsyncTask;
    /* access modifiers changed from: private */
    public ProfileModel model;
    private RemoveUserFromFavoriteListAsyncTask removeUserFromFavoriteListAsyncTask;
    private RemoveUserFromFollowingListAsyncTask removeUserFromFollowingListAsyncTask;
    private RemoveUserFromShareIdentityListAsyncTask removeUserFromShareIdentityListAsyncTask;
    private ListState viewModelState = ListState.LoadingState;

    public ChangeFriendshipDialogViewModel(ProfileModel model2) {
        boolean z = false;
        XLEAssert.assertTrue(!ProfileModel.isMeXuid(model2.getXuid()) ? true : z);
        this.model = model2;
    }

    public ListState getViewModelState() {
        return this.viewModelState;
    }

    public String getGamerTag() {
        return this.model.getGamerTag();
    }

    public String getGamerPicUrl() {
        return this.model.getGamerPicImageUrl();
    }

    public String getRealName() {
        return this.model.getRealName();
    }

    public String getGamerScore() {
        return this.model.getGamerScore();
    }

    public int getPreferredColor() {
        return this.model.getPreferedColor();
    }

    public boolean getIsFollowing() {
        return this.model.isCallerFollowingTarget();
    }

    public boolean getIsFavorite() {
        return this.model.hasCallerMarkedTargetAsFavorite();
    }

    public String getXuid() {
        return this.model.getXuid();
    }

    public boolean getCallerMarkedTargetAsIdentityShared() {
        return this.model.hasCallerMarkedTargetAsIdentityShared();
    }

    public String getCallerShareRealNameStatus() {
        ProfileModel meProfile = ProfileModel.getMeProfileModel();
        if (meProfile != null) {
            return meProfile.getShareRealNameStatus();
        }
        return "";
    }

    public String getCallerGamerTag() {
        ProfileModel meProfile = ProfileModel.getMeProfileModel();
        if (meProfile != null) {
            return meProfile.getGamerTag();
        }
        return "";
    }

    public void setShouldAddUserToFriendList(boolean shouldAddUserToFriendList) {
        if (shouldAddUserToFriendList) {
            this.changeFriendshipForm.add(ProfileScreenViewModel.ChangeFriendshipFormOptions.ShouldAddUserToFriendList);
        } else {
            this.changeFriendshipForm.remove(ProfileScreenViewModel.ChangeFriendshipFormOptions.ShouldAddUserToFriendList);
        }
    }

    public void setShouldAddUserToFavoriteList(boolean shouldAddUserToFavoriteList) {
        if (shouldAddUserToFavoriteList) {
            this.changeFriendshipForm.add(ProfileScreenViewModel.ChangeFriendshipFormOptions.ShouldAddUserToFavoriteList);
        } else {
            this.changeFriendshipForm.remove(ProfileScreenViewModel.ChangeFriendshipFormOptions.ShouldAddUserToFavoriteList);
        }
    }

    public void setShouldRemoveUserFromFavoriteList(boolean shouldRemoveUserFromFavoriteList) {
        if (shouldRemoveUserFromFavoriteList) {
            this.changeFriendshipForm.add(ProfileScreenViewModel.ChangeFriendshipFormOptions.ShouldRemoveUserFromFavoriteList);
        } else {
            this.changeFriendshipForm.remove(ProfileScreenViewModel.ChangeFriendshipFormOptions.ShouldRemoveUserFromFavoriteList);
        }
    }

    public void setShouldAddUserToShareIdentityList(boolean shouldAddUserToShareIdentityList) {
        if (shouldAddUserToShareIdentityList) {
            this.changeFriendshipForm.add(ProfileScreenViewModel.ChangeFriendshipFormOptions.ShouldAddUserToShareIdentityList);
        } else {
            this.changeFriendshipForm.remove(ProfileScreenViewModel.ChangeFriendshipFormOptions.ShouldAddUserToShareIdentityList);
        }
    }

    public void setShouldRemoveUserFroShareIdentityList(boolean shouldRemoveUserFroShareIdentityList) {
        if (shouldRemoveUserFroShareIdentityList) {
            this.changeFriendshipForm.add(ProfileScreenViewModel.ChangeFriendshipFormOptions.ShouldRemoveUserFromShareIdentityList);
        } else {
            this.changeFriendshipForm.remove(ProfileScreenViewModel.ChangeFriendshipFormOptions.ShouldRemoveUserFromShareIdentityList);
        }
    }

    public void onChangeRelationshipCompleted() {
        boolean willPerformAsyncAction = false;
        UTCChangeRelationship.Relationship relationship = this.model.isCallerFollowingTarget() ? UTCChangeRelationship.Relationship.EXISTINGFRIEND : UTCChangeRelationship.Relationship.NOTCHANGED;
        UTCChangeRelationship.FavoriteStatus favoriteStatus = this.model.hasCallerMarkedTargetAsFavorite() ? UTCChangeRelationship.FavoriteStatus.EXISTINGFAVORITE : UTCChangeRelationship.FavoriteStatus.EXISTINGNOTFAVORITED;
        UTCChangeRelationship.RealNameStatus realNameStatus = this.model.hasCallerMarkedTargetAsIdentityShared() ? UTCChangeRelationship.RealNameStatus.EXISTINGSHARED : UTCChangeRelationship.RealNameStatus.EXISTINGNOTSHARED;
        UTCChangeRelationship.GamerType gamerType = UTCChangeRelationship.GamerType.NORMAL;
        if (this.changeFriendshipForm.contains(ProfileScreenViewModel.ChangeFriendshipFormOptions.ShouldAddUserToFriendList)) {
            relationship = UTCChangeRelationship.Relationship.ADDFRIEND;
            addFollowingUser();
            willPerformAsyncAction = true;
        }
        if (this.changeFriendshipForm.contains(ProfileScreenViewModel.ChangeFriendshipFormOptions.ShouldRemoveUserFromFriendList)) {
            relationship = UTCChangeRelationship.Relationship.REMOVEFRIEND;
            removeFollowingUser();
            willPerformAsyncAction = true;
        }
        if (this.changeFriendshipForm.contains(ProfileScreenViewModel.ChangeFriendshipFormOptions.ShouldAddUserToFavoriteList)) {
            favoriteStatus = UTCChangeRelationship.FavoriteStatus.FAVORITED;
            addFavoriteUser();
            willPerformAsyncAction = true;
        }
        if (this.changeFriendshipForm.contains(ProfileScreenViewModel.ChangeFriendshipFormOptions.ShouldRemoveUserFromFavoriteList)) {
            favoriteStatus = UTCChangeRelationship.FavoriteStatus.UNFAVORITED;
            removeFavoriteUser();
            willPerformAsyncAction = true;
        }
        if (this.changeFriendshipForm.contains(ProfileScreenViewModel.ChangeFriendshipFormOptions.ShouldAddUserToShareIdentityList)) {
            realNameStatus = UTCChangeRelationship.RealNameStatus.SHARINGON;
            addUserToShareIdentityList();
            willPerformAsyncAction = true;
        }
        if (this.changeFriendshipForm.contains(ProfileScreenViewModel.ChangeFriendshipFormOptions.ShouldRemoveUserFromShareIdentityList)) {
            realNameStatus = UTCChangeRelationship.RealNameStatus.SHARINGOFF;
            removeUserFromShareIdentityList();
            willPerformAsyncAction = true;
        }
        if (!willPerformAsyncAction) {
            notifyDialogAsyncTaskCompleted();
        } else {
            UTCChangeRelationship.trackChangeRelationshipDone(relationship, realNameStatus, favoriteStatus, gamerType);
        }
    }

    public void clearChangeFriendshipForm() {
        this.changeFriendshipForm.clear();
    }

    public void setInitialRealNameSharingState(boolean state) {
        this.isSharingRealNameStart = state;
        this.isSharingRealNameEnd = state;
    }

    public void setIsSharingRealNameEnd(boolean state) {
        this.isSharingRealNameEnd = state;
    }

    public boolean getIsSharingRealNameStart() {
        return this.isSharingRealNameStart;
    }

    public boolean getIsSharingRealNameEnd() {
        return this.isSharingRealNameEnd;
    }

    private void showError(int contentResId) {
        DialogManager.getInstance().showToast(contentResId);
    }

    public String getDialogButtonText() {
        if (this.isFollowing) {
            return XboxTcuiSdk.getResources().getString(R.string.TextInput_Confirm);
        }
        return XboxTcuiSdk.getResources().getString(R.string.OK_Text);
    }

    public boolean isBusy() {
        return this.isLoadingUserProfile || this.isAddingUserToFavoriteList || this.isRemovingUserFromFavoriteList || this.isAddingUserToFollowingList || this.isRemovingUserFromFollowingList || this.isAddingUserToShareIdentityList || this.isRemovingUserFromShareIdentityList;
    }

    public void load() {
        if (this.loadProfileAsyncTask != null) {
            this.loadProfileAsyncTask.cancel();
        }
        this.loadProfileAsyncTask = new LoadPersonDataAsyncTask();
        this.loadProfileAsyncTask.load(true);
    }

    public void addFavoriteUser() {
        if (this.addUserToFavoriteListAsyncTask != null) {
            this.addUserToFavoriteListAsyncTask.cancel();
        }
        this.addUserToFavoriteListAsyncTask = new AddUserToFavoriteListAsyncTask(this.model.getXuid());
        this.addUserToFavoriteListAsyncTask.load(true);
    }

    public void removeFavoriteUser() {
        if (this.removeUserFromFavoriteListAsyncTask != null) {
            this.removeUserFromFavoriteListAsyncTask.cancel();
        }
        this.removeUserFromFavoriteListAsyncTask = new RemoveUserFromFavoriteListAsyncTask(this.model.getXuid());
        this.removeUserFromFavoriteListAsyncTask.load(true);
    }

    public void addUserToShareIdentityList() {
        if (this.addUserToShareIdentityListAsyncTask != null) {
            this.addUserToShareIdentityListAsyncTask.cancel();
        }
        ArrayList<String> users = new ArrayList<>();
        users.add(this.model.getXuid());
        this.addUserToShareIdentityListAsyncTask = new AddUserToShareIdentityListAsyncTask(users);
        this.addUserToShareIdentityListAsyncTask.load(true);
    }

    public void removeUserFromShareIdentityList() {
        if (this.removeUserFromFollowingListAsyncTask != null) {
            this.removeUserFromFavoriteListAsyncTask.cancel();
        }
        ArrayList<String> users = new ArrayList<>();
        users.add(this.model.getXuid());
        this.removeUserFromShareIdentityListAsyncTask = new RemoveUserFromShareIdentityListAsyncTask(users);
        this.removeUserFromShareIdentityListAsyncTask.load(true);
    }

    private void notifyDialogUpdateView() {
        ((SGProjectSpecificDialogManager) DialogManager.getInstance().getManager()).notifyChangeFriendshipDialogUpdateView();
    }

    private void notifyDialogAsyncTaskCompleted() {
        ((SGProjectSpecificDialogManager) DialogManager.getInstance().getManager()).notifyChangeFriendshipDialogAsyncTaskCompleted();
    }

    private void notifyDialogAsyncTaskFailed(String errorMessage) {
        ((SGProjectSpecificDialogManager) DialogManager.getInstance().getManager()).notifyChangeFriendshipDialogAsyncTaskFailed(errorMessage);
    }

    public void addFollowingUser() {
        if (ProfileModel.hasPrivilegeToAddFriend()) {
            if (this.addUserToFollowingListAsyncTask != null) {
                this.addUserToFollowingListAsyncTask.cancel();
            }
            this.addUserToFollowingListAsyncTask = new AddUserToFollowingListAsyncTask(this.model.getXuid());
            this.addUserToFollowingListAsyncTask.load(true);
            return;
        }
        showError(R.string.Global_MissingPrivilegeError_DialogBody);
    }

    public void removeFollowingUser() {
        if (this.removeUserFromFollowingListAsyncTask != null) {
            this.removeUserFromFollowingListAsyncTask.cancel();
        }
        this.removeUserFromFollowingListAsyncTask = new RemoveUserFromFollowingListAsyncTask(this.model.getXuid());
        this.removeUserFromFollowingListAsyncTask.load(true);
    }

    /* access modifiers changed from: private */
    public void onLoadPersonDataCompleted(AsyncActionStatus status) {
        this.isLoadingUserProfile = false;
        switch (status) {
            case SUCCESS:
            case NO_CHANGE:
            case NO_OP_SUCCESS:
                if (this.model.getProfileSummaryData() == null) {
                    this.viewModelState = ListState.ErrorState;
                    break;
                } else {
                    this.viewModelState = ListState.ValidContentState;
                    break;
                }
            case FAIL:
            case NO_OP_FAIL:
                this.viewModelState = ListState.ErrorState;
                break;
        }
        notifyDialogUpdateView();
    }

    /* access modifiers changed from: private */
    public void onAddUseToShareIdentityListCompleted(AsyncActionStatus status) {
        this.isAddingUserToShareIdentityList = false;
        switch (status) {
            case SUCCESS:
            case NO_CHANGE:
            case NO_OP_SUCCESS:
                notifyDialogAsyncTaskCompleted();
                return;
            case FAIL:
            case NO_OP_FAIL:
                notifyDialogAsyncTaskFailed(XboxTcuiSdk.getResources().getString(R.string.RealNameSharing_ErrorChangeRemove));
                return;
            default:
                return;
        }
    }

    /* access modifiers changed from: private */
    public void onRemoveUserFromShareIdentityListCompleted(AsyncActionStatus status) {
        this.isRemovingUserFromShareIdentityList = false;
        switch (status) {
            case SUCCESS:
            case NO_CHANGE:
            case NO_OP_SUCCESS:
                notifyDialogAsyncTaskCompleted();
                return;
            case FAIL:
            case NO_OP_FAIL:
                notifyDialogAsyncTaskFailed(XboxTcuiSdk.getResources().getString(R.string.RealNameSharing_ErrorChangeRemove));
                return;
            default:
                return;
        }
    }

    /* access modifiers changed from: private */
    public void onAddUserToFavoriteListCompleted(AsyncActionStatus status, boolean isFavorite2) {
        this.isAddingUserToFavoriteList = false;
        switch (status) {
            case SUCCESS:
            case NO_CHANGE:
            case NO_OP_SUCCESS:
                this.isFavorite = isFavorite2;
                notifyDialogAsyncTaskCompleted();
                return;
            case FAIL:
            case NO_OP_FAIL:
                notifyDialogAsyncTaskFailed(XboxTcuiSdk.getResources().getString(R.string.RealNameSharing_ErrorChangeRemove));
                return;
            default:
                return;
        }
    }

    /* access modifiers changed from: private */
    public void onRemoveUserFromFavoriteListCompleted(AsyncActionStatus status, boolean isFavorite2) {
        this.isRemovingUserFromFavoriteList = false;
        switch (status) {
            case SUCCESS:
            case NO_CHANGE:
            case NO_OP_SUCCESS:
                this.isFavorite = isFavorite2;
                notifyDialogAsyncTaskCompleted();
                return;
            case FAIL:
            case NO_OP_FAIL:
                notifyDialogAsyncTaskFailed(XboxTcuiSdk.getResources().getString(R.string.RealNameSharing_ErrorChangeRemove));
                return;
            default:
                return;
        }
    }

    /* access modifiers changed from: private */
    public void onAddUserToFollowingListCompleted(AsyncActionStatus status, boolean isFollowing2) {
        this.isAddingUserToFollowingList = false;
        switch (status) {
            case SUCCESS:
            case NO_CHANGE:
            case NO_OP_SUCCESS:
                this.isFollowing = isFollowing2;
                notifyDialogAsyncTaskCompleted();
                return;
            case FAIL:
            case NO_OP_FAIL:
                AddFollowingUserResponseContainer.AddFollowingUserResponse result = null;
                ProfileModel meProfile = ProfileModel.getMeProfileModel();
                if (meProfile != null) {
                    result = meProfile.getAddUserToFollowingResult();
                }
                if (result == null || result.getAddFollowingRequestStatus() || result.code != 1028) {
                    notifyDialogAsyncTaskFailed(XboxTcuiSdk.getResources().getString(R.string.RealNameSharing_ErrorAddingFriend));
                    return;
                } else {
                    notifyDialogAsyncTaskFailed(result.description);
                    return;
                }
            default:
                return;
        }
    }

    /* access modifiers changed from: private */
    public void onRemoveUserFromFollowingListCompleted(AsyncActionStatus status, boolean isFollowing2) {
        this.isRemovingUserFromFollowingList = false;
        switch (status) {
            case SUCCESS:
            case NO_CHANGE:
            case NO_OP_SUCCESS:
                this.isFollowing = isFollowing2;
                if (this.isFavorite && !this.isFollowing) {
                    this.isFavorite = false;
                }
                notifyDialogAsyncTaskCompleted();
                return;
            case FAIL:
            case NO_OP_FAIL:
                notifyDialogAsyncTaskFailed(XboxTcuiSdk.getResources().getString(R.string.RealNameSharing_ErrorChangeRemove));
                return;
            default:
                return;
        }
    }

    private class LoadPersonDataAsyncTask extends NetworkAsyncTask<AsyncActionStatus> {
        private LoadPersonDataAsyncTask() {
        }

        /* access modifiers changed from: protected */
        public boolean checkShouldExecute() {
            XLEAssert.assertIsUIThread();
            return false;
        }

        /* access modifiers changed from: protected */
        public void onNoAction() {
            XLEAssert.assertIsUIThread();
            ChangeFriendshipDialogViewModel.this.onLoadPersonDataCompleted(AsyncActionStatus.NO_CHANGE);
        }

        /* access modifiers changed from: protected */
        public void onPreExecute() {
            XLEAssert.assertIsUIThread();
            boolean unused = ChangeFriendshipDialogViewModel.this.isLoadingUserProfile = true;
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(AsyncActionStatus result) {
            ChangeFriendshipDialogViewModel.this.onLoadPersonDataCompleted(result);
        }

        /* access modifiers changed from: protected */
        public AsyncActionStatus onError() {
            return AsyncActionStatus.FAIL;
        }

        /* access modifiers changed from: protected */
        public AsyncActionStatus loadDataInBackground() {
            XLEAssert.assertNotNull(ChangeFriendshipDialogViewModel.this.model);
            return ChangeFriendshipDialogViewModel.this.model.loadProfileSummary(this.forceLoad).getStatus();
        }
    }

    private class RemoveUserFromShareIdentityListAsyncTask extends NetworkAsyncTask<AsyncActionStatus> {
        private ArrayList<String> usersToAdd;

        public RemoveUserFromShareIdentityListAsyncTask(ArrayList<String> users) {
            this.usersToAdd = users;
        }

        /* access modifiers changed from: protected */
        public boolean checkShouldExecute() {
            return true;
        }

        /* access modifiers changed from: protected */
        public void onNoAction() {
        }

        /* access modifiers changed from: protected */
        public void onPreExecute() {
            XLEAssert.assertIsUIThread();
            boolean unused = ChangeFriendshipDialogViewModel.this.isRemovingUserFromShareIdentityList = true;
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(AsyncActionStatus asyncActionStatus) {
            ChangeFriendshipDialogViewModel.this.onRemoveUserFromShareIdentityListCompleted(asyncActionStatus);
        }

        /* access modifiers changed from: protected */
        public AsyncActionStatus onError() {
            return AsyncActionStatus.FAIL;
        }

        /* access modifiers changed from: protected */
        public AsyncActionStatus loadDataInBackground() {
            ProfileModel meProfile = ProfileModel.getMeProfileModel();
            if (meProfile != null) {
                return meProfile.removeUserFromShareIdentity(this.forceLoad, this.usersToAdd).getStatus();
            }
            return AsyncActionStatus.FAIL;
        }
    }

    private class AddUserToShareIdentityListAsyncTask extends NetworkAsyncTask<AsyncActionStatus> {
        private ArrayList<String> usersToAdd;

        public AddUserToShareIdentityListAsyncTask(ArrayList<String> users) {
            this.usersToAdd = users;
        }

        /* access modifiers changed from: protected */
        public boolean checkShouldExecute() {
            return true;
        }

        /* access modifiers changed from: protected */
        public void onNoAction() {
        }

        /* access modifiers changed from: protected */
        public void onPreExecute() {
            XLEAssert.assertIsUIThread();
            boolean unused = ChangeFriendshipDialogViewModel.this.isAddingUserToShareIdentityList = true;
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(AsyncActionStatus asyncActionStatus) {
            ChangeFriendshipDialogViewModel.this.onAddUseToShareIdentityListCompleted(asyncActionStatus);
        }

        /* access modifiers changed from: protected */
        public AsyncActionStatus onError() {
            return AsyncActionStatus.FAIL;
        }

        /* access modifiers changed from: protected */
        public AsyncActionStatus loadDataInBackground() {
            ProfileModel meProfile = ProfileModel.getMeProfileModel();
            if (meProfile != null) {
                return meProfile.addUserToShareIdentity(this.forceLoad, this.usersToAdd).getStatus();
            }
            return AsyncActionStatus.FAIL;
        }
    }

    private class AddUserToFavoriteListAsyncTask extends NetworkAsyncTask<AsyncActionStatus> {
        private boolean favoriteUser = false;
        private String favoriteUserXuid;

        public AddUserToFavoriteListAsyncTask(String favoriteUserXuid2) {
            this.favoriteUserXuid = favoriteUserXuid2;
        }

        /* access modifiers changed from: protected */
        public boolean checkShouldExecute() {
            XLEAssert.assertIsUIThread();
            return true;
        }

        /* access modifiers changed from: protected */
        public void onNoAction() {
            XLEAssert.assertIsUIThread();
            ChangeFriendshipDialogViewModel.this.onAddUserToFavoriteListCompleted(AsyncActionStatus.NO_CHANGE, this.favoriteUser);
        }

        /* access modifiers changed from: protected */
        public void onPreExecute() {
            XLEAssert.assertIsUIThread();
            boolean unused = ChangeFriendshipDialogViewModel.this.isAddingUserToFavoriteList = true;
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(AsyncActionStatus result) {
            ChangeFriendshipDialogViewModel.this.onAddUserToFavoriteListCompleted(result, this.favoriteUser);
        }

        /* access modifiers changed from: protected */
        public AsyncActionStatus onError() {
            return AsyncActionStatus.FAIL;
        }

        /* access modifiers changed from: protected */
        public AsyncActionStatus loadDataInBackground() {
            ArrayList<FollowersData> favoriteList;
            ProfileModel meProfile = ProfileModel.getMeProfileModel();
            if (meProfile == null) {
                return AsyncActionStatus.FAIL;
            }
            AsyncActionStatus status = meProfile.addUserToFavoriteList(this.forceLoad, this.favoriteUserXuid).getStatus();
            if ((status != AsyncActionStatus.SUCCESS && status != AsyncActionStatus.NO_CHANGE && status != AsyncActionStatus.NO_OP_SUCCESS) || (favoriteList = meProfile.getFavorites()) == null) {
                return status;
            }
            Iterator i$ = favoriteList.iterator();
            while (i$.hasNext()) {
                FollowersData fData = i$.next();
                if (fData.xuid.equals(this.favoriteUserXuid)) {
                    this.favoriteUser = fData.isFavorite;
                    return status;
                }
            }
            return status;
        }
    }

    private class RemoveUserFromFavoriteListAsyncTask extends NetworkAsyncTask<AsyncActionStatus> {
        private boolean favoriteUser = false;
        private String favoriteUserXuid;

        public RemoveUserFromFavoriteListAsyncTask(String favoriteUserXuid2) {
            this.favoriteUserXuid = favoriteUserXuid2;
        }

        /* access modifiers changed from: protected */
        public boolean checkShouldExecute() {
            XLEAssert.assertIsUIThread();
            return true;
        }

        /* access modifiers changed from: protected */
        public void onNoAction() {
            XLEAssert.assertIsUIThread();
            ChangeFriendshipDialogViewModel.this.onRemoveUserFromFavoriteListCompleted(AsyncActionStatus.NO_CHANGE, this.favoriteUser);
        }

        /* access modifiers changed from: protected */
        public void onPreExecute() {
            XLEAssert.assertIsUIThread();
            boolean unused = ChangeFriendshipDialogViewModel.this.isRemovingUserFromFavoriteList = true;
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(AsyncActionStatus result) {
            ChangeFriendshipDialogViewModel.this.onRemoveUserFromFavoriteListCompleted(result, this.favoriteUser);
        }

        /* access modifiers changed from: protected */
        public AsyncActionStatus onError() {
            return AsyncActionStatus.FAIL;
        }

        /* access modifiers changed from: protected */
        public AsyncActionStatus loadDataInBackground() {
            ArrayList<FollowersData> favoriteList;
            ProfileModel meProfile = ProfileModel.getMeProfileModel();
            if (meProfile == null) {
                return AsyncActionStatus.FAIL;
            }
            AsyncActionStatus status = meProfile.removeUserFromFavoriteList(this.forceLoad, this.favoriteUserXuid).getStatus();
            if ((status != AsyncActionStatus.SUCCESS && status != AsyncActionStatus.NO_CHANGE && status != AsyncActionStatus.NO_OP_SUCCESS) || (favoriteList = meProfile.getFavorites()) == null) {
                return status;
            }
            Iterator i$ = favoriteList.iterator();
            while (i$.hasNext()) {
                FollowersData fData = i$.next();
                if (fData.xuid.equals(this.favoriteUserXuid)) {
                    this.favoriteUser = fData.isFavorite;
                    return status;
                }
            }
            return status;
        }
    }

    private class AddUserToFollowingListAsyncTask extends NetworkAsyncTask<AsyncActionStatus> {
        private String followingUserXuid;
        private boolean isFollowingUser = false;

        public AddUserToFollowingListAsyncTask(String followingUserXuid2) {
            this.followingUserXuid = followingUserXuid2;
        }

        /* access modifiers changed from: protected */
        public boolean checkShouldExecute() {
            XLEAssert.assertIsUIThread();
            return true;
        }

        /* access modifiers changed from: protected */
        public void onNoAction() {
            XLEAssert.assertIsUIThread();
            ChangeFriendshipDialogViewModel.this.onAddUserToFollowingListCompleted(AsyncActionStatus.NO_CHANGE, this.isFollowingUser);
        }

        /* access modifiers changed from: protected */
        public void onPreExecute() {
            XLEAssert.assertIsUIThread();
            boolean unused = ChangeFriendshipDialogViewModel.this.isAddingUserToFollowingList = true;
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(AsyncActionStatus result) {
            ChangeFriendshipDialogViewModel.this.onAddUserToFollowingListCompleted(result, this.isFollowingUser);
        }

        /* access modifiers changed from: protected */
        public AsyncActionStatus onError() {
            return AsyncActionStatus.FAIL;
        }

        /* access modifiers changed from: protected */
        public AsyncActionStatus loadDataInBackground() {
            ProfileModel meProfile = ProfileModel.getMeProfileModel();
            if (meProfile == null) {
                return AsyncActionStatus.FAIL;
            }
            AsyncActionStatus status = meProfile.addUserToFollowingList(this.forceLoad, this.followingUserXuid).getStatus();
            if (AsyncActionStatus.getIsFail(status)) {
                return status;
            }
            AddFollowingUserResponseContainer.AddFollowingUserResponse response = meProfile.getAddUserToFollowingResult();
            if (response != null && !response.getAddFollowingRequestStatus() && response.code == 1028) {
                return AsyncActionStatus.FAIL;
            }
            ChangeFriendshipDialogViewModel.this.model.loadProfileSummary(true);
            meProfile.loadProfileSummary(true);
            ArrayList<FollowersData> followersList = meProfile.getFollowingData();
            if (followersList == null) {
                return status;
            }
            Iterator i$ = followersList.iterator();
            while (i$.hasNext()) {
                if (i$.next().xuid.equals(this.followingUserXuid)) {
                    this.isFollowingUser = true;
                    return status;
                }
            }
            return status;
        }
    }

    private class RemoveUserFromFollowingListAsyncTask extends NetworkAsyncTask<AsyncActionStatus> {
        private String followingUserXuid;
        private boolean isFollowingUser = true;

        public RemoveUserFromFollowingListAsyncTask(String followingUserXuid2) {
            this.followingUserXuid = followingUserXuid2;
        }

        /* access modifiers changed from: protected */
        public boolean checkShouldExecute() {
            XLEAssert.assertIsUIThread();
            return true;
        }

        /* access modifiers changed from: protected */
        public void onNoAction() {
            XLEAssert.assertIsUIThread();
            ChangeFriendshipDialogViewModel.this.onRemoveUserFromFollowingListCompleted(AsyncActionStatus.NO_CHANGE, this.isFollowingUser);
        }

        /* access modifiers changed from: protected */
        public void onPreExecute() {
            XLEAssert.assertIsUIThread();
            boolean unused = ChangeFriendshipDialogViewModel.this.isRemovingUserFromFollowingList = true;
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(AsyncActionStatus result) {
            ChangeFriendshipDialogViewModel.this.onRemoveUserFromFollowingListCompleted(result, this.isFollowingUser);
        }

        /* access modifiers changed from: protected */
        public AsyncActionStatus onError() {
            return AsyncActionStatus.FAIL;
        }

        /* access modifiers changed from: protected */
        public AsyncActionStatus loadDataInBackground() {
            ProfileModel meProfile = ProfileModel.getMeProfileModel();
            if (meProfile == null) {
                return AsyncActionStatus.FAIL;
            }
            AsyncActionStatus status = meProfile.removeUserFromFollowingList(this.forceLoad, this.followingUserXuid).getStatus();
            if (AsyncActionStatus.getIsFail(status)) {
                return status;
            }
            ChangeFriendshipDialogViewModel.this.model.loadProfileSummary(true);
            meProfile.loadProfileSummary(true);
            this.isFollowingUser = false;
            return status;
        }
    }
}
