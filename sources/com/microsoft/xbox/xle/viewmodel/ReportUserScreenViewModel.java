package com.microsoft.xbox.xle.viewmodel;

import com.microsoft.xbox.idp.telemetry.helpers.UTCPageView;
import com.microsoft.xbox.service.model.ProfileModel;
import com.microsoft.xbox.service.model.sls.FeedbackType;
import com.microsoft.xbox.toolkit.AsyncActionStatus;
import com.microsoft.xbox.toolkit.DialogManager;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.NetworkAsyncTask;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.ui.NavigationManager;
import com.microsoft.xbox.toolkit.ui.ScreenLayout;
import com.microsoft.xbox.xle.app.adapter.ReportUserScreenAdapter;
import com.microsoft.xboxtcui.R;
import com.microsoft.xboxtcui.XboxTcuiSdk;
import java.util.ArrayList;

public class ReportUserScreenViewModel extends ViewModelBase {
    private FeedbackType[] feedbackReasons;
    /* access modifiers changed from: private */
    public boolean isSubmittingReport;
    private ProfileModel model;
    private FeedbackType selectedReason;
    private SubmitReportAsyncTask submitReportAsyncTask;

    public ReportUserScreenViewModel(ScreenLayout screenLayout) {
        super(screenLayout);
        boolean z;
        String profileXuid = NavigationManager.getInstance().getActivityParameters().getSelectedProfile();
        XLEAssert.assertTrue(!JavaUtil.isNullOrEmpty(profileXuid));
        if (JavaUtil.isNullOrEmpty(profileXuid)) {
            popScreenWithXuidError();
        }
        this.model = ProfileModel.getProfileModel(profileXuid);
        if (!JavaUtil.isNullOrEmpty(this.model.getGamerTag())) {
            z = true;
        } else {
            z = false;
        }
        XLEAssert.assertTrue(z);
        this.adapter = new ReportUserScreenAdapter(this);
        FeedbackType[] feedbackTypeArr = new FeedbackType[7];
        feedbackTypeArr[0] = FeedbackType.UserContentPersonalInfo;
        feedbackTypeArr[1] = FeedbackType.FairPlayCheater;
        feedbackTypeArr[2] = JavaUtil.isNullOrEmpty(this.model.getRealName()) ? FeedbackType.UserContentGamertag : FeedbackType.UserContentRealName;
        feedbackTypeArr[3] = FeedbackType.UserContentGamerpic;
        feedbackTypeArr[4] = FeedbackType.FairPlayQuitter;
        feedbackTypeArr[5] = FeedbackType.FairplayUnsporting;
        feedbackTypeArr[6] = FeedbackType.CommsAbusiveVoice;
        this.feedbackReasons = feedbackTypeArr;
    }

    public boolean onBackButtonPressed() {
        UTCPageView.removePage();
        try {
            NavigationManager.getInstance().PopScreensAndReplace(1, (Class<? extends ScreenLayout>) null, false, false, false, NavigationManager.getInstance().getActivityParameters());
            return true;
        } catch (XLEException e) {
            return false;
        }
    }

    /* access modifiers changed from: protected */
    public void onStartOverride() {
    }

    public void onRehydrate() {
    }

    /* access modifiers changed from: protected */
    public void onStopOverride() {
        if (this.submitReportAsyncTask != null) {
            this.submitReportAsyncTask.cancel();
        }
    }

    public boolean isBusy() {
        return this.isSubmittingReport;
    }

    public void load(boolean forceRefresh) {
    }

    private void popScreenWithXuidError() {
        try {
            showError(R.string.Service_ErrorText);
            NavigationManager.getInstance().PopScreen();
        } catch (XLEException e) {
        }
    }

    public int getPreferredColor() {
        return this.model.getPreferedColor();
    }

    public String getTitle() {
        return String.format(XboxTcuiSdk.getResources().getString(R.string.ProfileCard_Report_InfoString_Android), new Object[]{this.model.getGamerTag()});
    }

    public ArrayList<String> getReasonTitles() {
        ArrayList<String> titles = new ArrayList<>(this.feedbackReasons.length);
        titles.add(XboxTcuiSdk.getResources().getString(R.string.ProfileCard_Report_SelectReason));
        for (FeedbackType feedbackType : this.feedbackReasons) {
            titles.add(feedbackType.getTitle());
        }
        return titles;
    }

    public boolean validReasonSelected() {
        return this.selectedReason != null;
    }

    public void setReason(int position) {
        this.selectedReason = position != 0 && position + -1 < this.feedbackReasons.length ? this.feedbackReasons[position - 1] : null;
        updateAdapter();
    }

    public FeedbackType getReason() {
        return this.selectedReason;
    }

    public void submitReport(String textReason) {
        if (this.submitReportAsyncTask != null) {
            this.submitReportAsyncTask.cancel();
        }
        if (this.selectedReason != null) {
            this.submitReportAsyncTask = new SubmitReportAsyncTask(this.model, this.selectedReason, textReason);
            this.submitReportAsyncTask.load(true);
        }
    }

    /* access modifiers changed from: private */
    public void onSubmitReportCompleted(AsyncActionStatus status) {
        switch (status) {
            case SUCCESS:
            case NO_CHANGE:
            case NO_OP_SUCCESS:
                DialogManager.getInstance().showToast(R.string.ProfileCard_Report_SuccessSubtext);
                onBackButtonPressed();
                return;
            case FAIL:
            case NO_OP_FAIL:
                showError(R.string.ProfileCard_Report_Error);
                return;
            default:
                return;
        }
    }

    public String getXUID() {
        return this.model.getXuid();
    }

    private class SubmitReportAsyncTask extends NetworkAsyncTask<AsyncActionStatus> {
        private FeedbackType feedbackType;
        private ProfileModel model;
        private String textReason;

        private SubmitReportAsyncTask(ProfileModel model2, FeedbackType feedbackType2, String textReason2) {
            this.model = model2;
            this.feedbackType = feedbackType2;
            this.textReason = textReason2;
        }

        /* access modifiers changed from: protected */
        public boolean checkShouldExecute() {
            XLEAssert.assertIsUIThread();
            return true;
        }

        /* access modifiers changed from: protected */
        public void onNoAction() {
            XLEAssert.assertIsUIThread();
            ReportUserScreenViewModel.this.onSubmitReportCompleted(AsyncActionStatus.NO_CHANGE);
        }

        /* access modifiers changed from: protected */
        public void onPreExecute() {
            XLEAssert.assertIsUIThread();
            boolean unused = ReportUserScreenViewModel.this.isSubmittingReport = true;
            ReportUserScreenViewModel.this.updateAdapter();
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(AsyncActionStatus result) {
            ReportUserScreenViewModel.this.onSubmitReportCompleted(result);
        }

        /* access modifiers changed from: protected */
        public AsyncActionStatus onError() {
            return AsyncActionStatus.FAIL;
        }

        /* access modifiers changed from: protected */
        public AsyncActionStatus loadDataInBackground() {
            XLEAssert.assertNotNull(this.model);
            return this.model.submitFeedbackForUser(this.forceLoad, this.feedbackType, this.textReason).getStatus();
        }
    }
}
