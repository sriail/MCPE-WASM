package com.microsoft.xbox.xle.app.activity.FriendFinder;

import android.os.AsyncTask;
import com.microsoft.xbox.service.model.friendfinder.FriendFinderType;
import com.microsoft.xbox.service.model.friendfinder.LinkedAccountHelpers;
import com.microsoft.xbox.service.model.friendfinder.OptInStatus;
import com.microsoft.xbox.service.model.friendfinder.ShortCircuitProfileMessage;
import com.microsoft.xbox.service.network.managers.ServiceManagerFactory;
import com.microsoft.xbox.service.network.managers.friendfinder.PhoneContactInfo;
import com.microsoft.xbox.service.network.managers.friendfinder.UploadContactsAsyncTask;
import com.microsoft.xbox.toolkit.AsyncActionStatus;
import com.microsoft.xbox.toolkit.AsyncResult;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.NetworkAsyncTask;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.ui.ActivityParameters;
import com.microsoft.xbox.toolkit.ui.NavigationManager;
import com.microsoft.xbox.toolkit.ui.ScreenLayout;
import com.microsoft.xbox.xle.app.adapter.FriendFinderAddPhoneScreenAdapter;
import com.microsoft.xbox.xle.telemetry.helpers.UTCFriendFinder;
import com.microsoft.xbox.xle.viewmodel.ViewModelBase;
import com.microsoft.xboxtcui.R;

public class FriendFinderAddPhoneScreenViewModel extends ViewModelBase {
    private AddShortCircuitProfileAsyncTask addShortCircuitProfileAsyncTask;
    /* access modifiers changed from: private */
    public String currentCountryCode;
    /* access modifiers changed from: private */
    public boolean isAddingProfile;
    /* access modifiers changed from: private */
    public boolean isLoadingInfo;
    /* access modifiers changed from: private */
    public boolean isLoadingMyProfileTask;
    /* access modifiers changed from: private */
    public boolean isUploadingContactsAndOptingIn;
    private LoadInfoAsyncTask loadInfoAsyncTask;
    private LoadMyProfileAsyncTask loadMyProfileAsyncTask;
    /* access modifiers changed from: private */
    public ShortCircuitProfileMessage.PhoneState myPhoneState;
    /* access modifiers changed from: private */
    public ShortCircuitProfileMessage.ShortCircuitProfileResponse myProfile;
    private OptInAsyncTask optInAsyncTask;
    /* access modifiers changed from: private */
    public String simPhoneNumber;
    private UploadContactsAsyncTask uploadContactsAsyncTask;

    public FriendFinderAddPhoneScreenViewModel(ScreenLayout screenLayout) {
        super(screenLayout);
        this.adapter = new FriendFinderAddPhoneScreenAdapter(this);
    }

    public String getCurrentCountryCode() {
        return this.currentCountryCode;
    }

    public String getSimPhoneNumber() {
        return this.simPhoneNumber;
    }

    public void addPhoneNumber(String enteredPhoneNumber) {
        if (JavaUtil.isNullOrEmpty(enteredPhoneNumber)) {
            showError(R.string.FriendFinder_PhoneNumberHint);
            return;
        }
        PhoneContactInfo.getInstance().setUserEnteredNumber(enteredPhoneNumber);
        if (needToAddPhoneNumber(enteredPhoneNumber)) {
            if (!(!JavaUtil.isNullOrEmpty(PhoneContactInfo.normalizePhoneNumber(enteredPhoneNumber)))) {
                showError(R.string.FriendFinder_PhoneVerifyEnterRegionAndPhoneNubmer);
                return;
            }
            cancelActiveTasks();
            this.addShortCircuitProfileAsyncTask = new AddShortCircuitProfileAsyncTask();
            this.addShortCircuitProfileAsyncTask.load(true);
        } else {
            if (this.uploadContactsAsyncTask != null) {
                this.uploadContactsAsyncTask.cancel();
                this.uploadContactsAsyncTask = null;
            }
            this.uploadContactsAsyncTask = new UploadContactsAsyncTask((UploadContactsAsyncTask.UploadContactsCompleted) null);
            this.uploadContactsAsyncTask.load(true);
            if (this.optInAsyncTask != null) {
                this.optInAsyncTask.cancel();
                this.optInAsyncTask = null;
            }
            this.optInAsyncTask = new OptInAsyncTask();
            this.optInAsyncTask.load(true);
        }
        updateAdapter();
    }

    private boolean needToAddPhoneNumber(String phoneNumber) {
        if (this.myProfile == null) {
            return true;
        }
        this.myPhoneState = this.myProfile.isVerified(phoneNumber);
        if (this.myPhoneState == null || !this.myPhoneState.isVerified || !this.myPhoneState.hasXboxApplication) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public void onStartOverride() {
    }

    public void onRehydrate() {
        this.adapter = new FriendFinderAddPhoneScreenAdapter(this);
    }

    /* access modifiers changed from: protected */
    public void onStopOverride() {
        cancelActiveTasks();
    }

    private void cancelActiveTasks() {
        if (this.addShortCircuitProfileAsyncTask != null) {
            this.addShortCircuitProfileAsyncTask.cancel();
            this.addShortCircuitProfileAsyncTask = null;
        }
        if (this.loadInfoAsyncTask != null) {
            this.loadInfoAsyncTask.cancel(true);
            this.loadInfoAsyncTask = null;
        }
        if (this.loadMyProfileAsyncTask != null) {
            this.loadMyProfileAsyncTask.cancel();
            this.loadMyProfileAsyncTask = null;
        }
        if (this.uploadContactsAsyncTask != null) {
            this.uploadContactsAsyncTask.cancel();
            this.uploadContactsAsyncTask = null;
        }
        if (this.optInAsyncTask != null) {
            this.optInAsyncTask.cancel();
            this.optInAsyncTask = null;
        }
    }

    public boolean isBusy() {
        return this.isAddingProfile || this.isLoadingInfo || this.isLoadingMyProfileTask || this.isUploadingContactsAndOptingIn;
    }

    public void load(boolean forceRefresh) {
        cancelActiveTasks();
        this.loadInfoAsyncTask = new LoadInfoAsyncTask();
        this.loadInfoAsyncTask.execute(new Void[0]);
        this.loadMyProfileAsyncTask = new LoadMyProfileAsyncTask();
        this.loadMyProfileAsyncTask.load(true);
    }

    public boolean onBackButtonPressed() {
        UTCFriendFinder.trackBackButtonPressed(getScreen().getName(), FriendFinderType.PHONE);
        return super.onBackButtonPressed();
    }

    /* access modifiers changed from: private */
    public void onAddShortCircuitProfileCompleted(AsyncActionStatus status) {
        this.isAddingProfile = false;
        switch (status) {
            case SUCCESS:
            case NO_CHANGE:
            case NO_OP_SUCCESS:
                try {
                    NavigationManager.getInstance().PushScreen(FriendFinderVerifyCodeScreen.class);
                    return;
                } catch (XLEException e) {
                    return;
                }
            case FAIL:
            case NO_OP_FAIL:
                showError(R.string.Service_ErrorText);
                updateAdapter();
                return;
            default:
                return;
        }
    }

    /* access modifiers changed from: private */
    public void onLoadMyProfileCompleted(AsyncActionStatus status, ShortCircuitProfileMessage.ShortCircuitProfileResponse profile) {
        this.isLoadingMyProfileTask = false;
        switch (status) {
            case SUCCESS:
            case NO_CHANGE:
            case NO_OP_SUCCESS:
                this.myProfile = profile;
                break;
        }
        updateAdapter();
    }

    /* access modifiers changed from: private */
    public void onOptInCompleted(AsyncActionStatus status) {
        this.isUploadingContactsAndOptingIn = false;
        switch (status) {
            case SUCCESS:
            case NO_CHANGE:
            case NO_OP_SUCCESS:
                try {
                    ActivityParameters parameters = new ActivityParameters();
                    parameters.putFriendFinderType(FriendFinderType.PHONE);
                    NavigationManager.getInstance().PushScreen(FriendFinderSuggestionsScreen.class, parameters);
                    return;
                } catch (XLEException e) {
                    return;
                }
            case FAIL:
            case NO_OP_FAIL:
                showError(R.string.Service_ErrorText);
                updateAdapter();
                return;
            default:
                return;
        }
    }

    private class LoadInfoAsyncTask extends AsyncTask<Void, Void, Void> {
        private LoadInfoAsyncTask() {
        }

        /* access modifiers changed from: protected */
        public void onPreExecute() {
            boolean unused = FriendFinderAddPhoneScreenViewModel.this.isLoadingInfo = true;
        }

        /* access modifiers changed from: protected */
        public Void doInBackground(Void... params) {
            String countryNameFromRegion = PhoneContactInfo.getInstance().getCountryNameFromRegion(PhoneContactInfo.getInstance().getRegion());
            String regionCode = PhoneContactInfo.getInstance().getCountryCode();
            if (!JavaUtil.isNullOrEmpty(regionCode)) {
                String unused = FriendFinderAddPhoneScreenViewModel.this.currentCountryCode = regionCode;
            }
            String unused2 = FriendFinderAddPhoneScreenViewModel.this.simPhoneNumber = PhoneContactInfo.getInstance().getPhoneNumberFromSim();
            return null;
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(Void aVoid) {
            boolean unused = FriendFinderAddPhoneScreenViewModel.this.isLoadingInfo = false;
            FriendFinderAddPhoneScreenViewModel.this.updateAdapter();
        }
    }

    private class LoadMyProfileAsyncTask extends NetworkAsyncTask<AsyncResult<ShortCircuitProfileMessage.ShortCircuitProfileResponse>> {
        private LoadMyProfileAsyncTask() {
        }

        /* access modifiers changed from: protected */
        public boolean checkShouldExecute() {
            return FriendFinderAddPhoneScreenViewModel.this.myProfile == null;
        }

        /* access modifiers changed from: protected */
        public void onNoAction() {
            FriendFinderAddPhoneScreenViewModel.this.onLoadMyProfileCompleted(AsyncActionStatus.SUCCESS, FriendFinderAddPhoneScreenViewModel.this.myProfile);
        }

        /* access modifiers changed from: protected */
        public AsyncResult<ShortCircuitProfileMessage.ShortCircuitProfileResponse> onError() {
            return null;
        }

        /* access modifiers changed from: protected */
        public AsyncResult<ShortCircuitProfileMessage.ShortCircuitProfileResponse> loadDataInBackground() {
            ShortCircuitProfileMessage.ShortCircuitProfileResponse profile = null;
            try {
                profile = ServiceManagerFactory.getInstance().getSLSServiceManager().getMyShortCircuitProfile();
            } catch (XLEException e) {
            }
            return new AsyncResult<>(profile, this, (XLEException) null);
        }

        /* access modifiers changed from: protected */
        public void onPreExecute() {
            boolean unused = FriendFinderAddPhoneScreenViewModel.this.isLoadingMyProfileTask = true;
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(AsyncResult<ShortCircuitProfileMessage.ShortCircuitProfileResponse> result) {
            if (result != null) {
                FriendFinderAddPhoneScreenViewModel.this.onLoadMyProfileCompleted(result.getStatus(), result.getResult());
            } else {
                FriendFinderAddPhoneScreenViewModel.this.onLoadMyProfileCompleted(AsyncActionStatus.FAIL, (ShortCircuitProfileMessage.ShortCircuitProfileResponse) null);
            }
        }
    }

    private class AddShortCircuitProfileAsyncTask extends NetworkAsyncTask<AsyncActionStatus> {
        private AddShortCircuitProfileAsyncTask() {
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
            try {
                String country = PhoneContactInfo.getInstance().getRegionWithCode();
                String userEnteredNumber = PhoneContactInfo.getInstance().getUserEnteredNumber();
                if (!JavaUtil.isNullOrEmpty(country)) {
                    userEnteredNumber = userEnteredNumber.replace("+", "");
                } else if (!userEnteredNumber.startsWith("+")) {
                    userEnteredNumber = "+" + userEnteredNumber;
                }
                ShortCircuitProfileMessage.ShortCircuitProfileResponse addResponse = ServiceManagerFactory.getInstance().getSLSServiceManager().sendShortCircuitProfile(new ShortCircuitProfileMessage.ShortCircuitProfileRequest(getAddType(), userEnteredNumber, country));
                if (addResponse == null || addResponse.error == null) {
                    return AsyncActionStatus.SUCCESS;
                }
                if (JavaUtil.isNullOrEmpty(country)) {
                    int digitCount = 0;
                    for (int i = 0; i < userEnteredNumber.length(); i++) {
                        if (Character.isDigit(userEnteredNumber.charAt(i))) {
                            digitCount++;
                        }
                    }
                    if (digitCount == 10) {
                        ShortCircuitProfileMessage.ShortCircuitProfileResponse addResponse2 = ServiceManagerFactory.getInstance().getSLSServiceManager().sendShortCircuitProfile(new ShortCircuitProfileMessage.ShortCircuitProfileRequest(getAddType(), "+1" + userEnteredNumber.replace("+", ""), country));
                        if (addResponse2 == null || addResponse2.error == null) {
                            return AsyncActionStatus.SUCCESS;
                        }
                        return AsyncActionStatus.FAIL;
                    }
                }
                if (addResponse.error.code == null || !addResponse.error.code.equalsIgnoreCase("PhoneAlreadyVerified")) {
                    return AsyncActionStatus.FAIL;
                }
                return AsyncActionStatus.SUCCESS;
            } catch (XLEException e) {
                return AsyncActionStatus.FAIL;
            }
        }

        private ShortCircuitProfileMessage.MsgType getAddType() {
            if (FriendFinderAddPhoneScreenViewModel.this.myPhoneState == null) {
                return ShortCircuitProfileMessage.MsgType.Add;
            }
            XLEAssert.assertFalse("Check for these before invoking this task", FriendFinderAddPhoneScreenViewModel.this.myPhoneState.isVerified && FriendFinderAddPhoneScreenViewModel.this.myPhoneState.hasXboxApplication);
            return FriendFinderAddPhoneScreenViewModel.this.myPhoneState.isVerified ? ShortCircuitProfileMessage.MsgType.AddXbox : ShortCircuitProfileMessage.MsgType.Edit;
        }

        /* access modifiers changed from: protected */
        public void onPreExecute() {
            boolean unused = FriendFinderAddPhoneScreenViewModel.this.isAddingProfile = true;
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(AsyncActionStatus status) {
            FriendFinderAddPhoneScreenViewModel.this.onAddShortCircuitProfileCompleted(status);
        }
    }

    private class OptInAsyncTask extends NetworkAsyncTask<AsyncActionStatus> {
        private OptInAsyncTask() {
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
            try {
                ServiceManagerFactory.getInstance().getSLSServiceManager().setFriendFinderOptInStatus(LinkedAccountHelpers.LinkedAccountType.Phone, OptInStatus.OptedIn);
                return AsyncActionStatus.SUCCESS;
            } catch (XLEException e) {
                return AsyncActionStatus.FAIL;
            }
        }

        /* access modifiers changed from: protected */
        public void onPreExecute() {
            boolean unused = FriendFinderAddPhoneScreenViewModel.this.isUploadingContactsAndOptingIn = true;
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(AsyncActionStatus status) {
            FriendFinderAddPhoneScreenViewModel.this.onOptInCompleted(status);
        }
    }
}
