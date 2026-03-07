package com.microsoft.xbox.xle.app.activity.FriendFinder;

import com.microsoft.xbox.service.model.ProfileModel;
import com.microsoft.xbox.service.model.friendfinder.FriendFinderSuggestionModel;
import com.microsoft.xbox.service.model.friendfinder.FriendFinderType;
import com.microsoft.xbox.service.model.sls.FavoriteListRequest;
import com.microsoft.xbox.service.network.managers.IPeopleHubResult;
import com.microsoft.xbox.service.network.managers.ServiceManagerFactory;
import com.microsoft.xbox.toolkit.AsyncActionStatus;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.NetworkAsyncTask;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.ui.ActivityParameters;
import com.microsoft.xbox.toolkit.ui.NavigationManager;
import com.microsoft.xbox.toolkit.ui.ScreenLayout;
import com.microsoft.xbox.xle.app.adapter.FriendFinderSuggestionsScreenAdapter;
import com.microsoft.xbox.xle.telemetry.helpers.UTCFriendFinder;
import com.microsoft.xbox.xle.viewmodel.ViewModelBase;
import com.microsoft.xboxtcui.R;
import com.microsoft.xboxtcui.XboxTcuiSdk;
import java.util.ArrayList;
import java.util.Iterator;

public class FriendFinderSuggestionsScreenViewModel extends ViewModelBase {
    private AddSuggestionsAsyncTask addSuggestionsAsyncTask;
    private ArrayList<IPeopleHubResult.PeopleHubPersonSummary> foundPeople = new ArrayList<>(0);
    private FriendFinderType friendFinderType;
    private GetPeopleHubRecommendationsAsyncTask getPeopleHubRecommendationsAsyncTask;
    /* access modifiers changed from: private */
    public boolean isAddingSuggestions;
    /* access modifiers changed from: private */
    public boolean isLoadingRecommendations;
    /* access modifiers changed from: private */
    public ProfileModel meProfileModel;

    public FriendFinderSuggestionsScreenViewModel(ScreenLayout screenLayout) {
        super(screenLayout);
        this.adapter = new FriendFinderSuggestionsScreenAdapter(this);
    }

    public String getTitle() {
        switch (this.foundPeople.size()) {
            case 0:
                return XboxTcuiSdk.getResources().getText(R.string.FriendFinder_Facebook_Upsell_Title_NoFriends).toString();
            case 1:
                return String.format(XboxTcuiSdk.getResources().getText(R.string.FriendFinder_Facebook_Upsell_Title_OneFriend_Android).toString(), new Object[]{getNameOrGamertagAtIndex(0)});
            case 2:
                return String.format(XboxTcuiSdk.getResources().getText(R.string.FriendFinder_Facebook_Upsell_Title_TwoFriends_Android).toString(), new Object[]{getNameOrGamertagAtIndex(0), getNameOrGamertagAtIndex(1)});
            case 3:
                return String.format(XboxTcuiSdk.getResources().getText(R.string.FriendFinder_Facebook_Upsell_Title_ThreeFriends_Android).toString(), new Object[]{getNameOrGamertagAtIndex(0), getNameOrGamertagAtIndex(1), getNameOrGamertagAtIndex(2)});
            default:
                return String.format(XboxTcuiSdk.getResources().getText(R.string.FriendFinder_Facebook_Upsell_Title_ManyFriends_Android).toString(), new Object[]{getNameOrGamertagAtIndex(0), getNameOrGamertagAtIndex(1), Integer.valueOf(this.foundPeople.size() - 2)});
        }
    }

    private String getNameOrGamertagAtIndex(int index) {
        String result = "";
        if (index >= this.foundPeople.size()) {
            return result;
        }
        IPeopleHubResult.PeopleHubPersonSummary person = this.foundPeople.get(index);
        if (!(person.recommendation == null || person.recommendation.Reasons == null || person.recommendation.Reasons.size() <= 0)) {
            result = person.recommendation.Reasons.get(0);
        }
        if (JavaUtil.isNullOrEmpty(result)) {
            return person.gamertag;
        }
        return result;
    }

    public String getSubtitle() {
        if (this.foundPeople.size() != 0) {
            return XboxTcuiSdk.getResources().getString(R.string.FriendFinder_Found_Subtitle);
        }
        String subtitle = XboxTcuiSdk.getResources().getString(R.string.FriendFinder_Facebook_Upsell_Description_NoFriends_LineOne);
        if (this.friendFinderType == FriendFinderType.FACEBOOK) {
            return subtitle + "\n\n" + XboxTcuiSdk.getResources().getString(R.string.FriendFinder_Facebook_Upsell_Description_Default_LineTwo);
        }
        return subtitle;
    }

    public ArrayList<FriendFinderSuggestionModel> getSuggestions() {
        ArrayList<FriendFinderSuggestionModel> suggestions = new ArrayList<>(this.foundPeople.size());
        Iterator i$ = this.foundPeople.iterator();
        while (i$.hasNext()) {
            suggestions.add(FriendFinderSuggestionModel.fromPeopleHubSummary(i$.next()));
        }
        return suggestions;
    }

    public void addSuggestions(ArrayList<Integer> suggestionsToAdd) {
        cancelActiveTasks();
        ArrayList<String> xuids = new ArrayList<>(suggestionsToAdd.size());
        Iterator i$ = suggestionsToAdd.iterator();
        while (i$.hasNext()) {
            Integer i = i$.next();
            XLEAssert.assertTrue(i.intValue() < this.foundPeople.size());
            if (i.intValue() < this.foundPeople.size()) {
                xuids.add(this.foundPeople.get(i.intValue()).xuid);
            }
        }
        switch (this.friendFinderType) {
            case PHONE:
                UTCFriendFinder.trackPhoneContactsAddFriends(getScreen().getName(), (String[]) xuids.toArray(new String[xuids.size()]));
                break;
            case FACEBOOK:
                UTCFriendFinder.trackAddFacebookFriend(getScreen().getName(), (String[]) xuids.toArray(new String[xuids.size()]));
                break;
        }
        this.addSuggestionsAsyncTask = new AddSuggestionsAsyncTask(xuids);
        this.addSuggestionsAsyncTask.load(true);
    }

    public void navigateToSkip() {
        switch (this.friendFinderType) {
            case PHONE:
                UTCFriendFinder.trackPhoneContactsSkipAddFriends(getScreen().getName());
                break;
            case FACEBOOK:
                UTCFriendFinder.trackAddFacebookFriendCancel(getScreen().getName());
                break;
        }
        navigateToInvite();
    }

    private void navigateToInvite() {
        if (this.friendFinderType == FriendFinderType.FACEBOOK) {
            navigateToFacebookInvite();
        } else {
            navigateToPhoneInvite();
        }
    }

    private void navigateToFacebookInvite() {
        ActivityParameters params = new ActivityParameters();
        params.putFriendFinderType(FriendFinderType.FACEBOOK);
        try {
            NavigationManager.getInstance().PushScreen(FriendFinderInviteScreen.class, params);
        } catch (XLEException e) {
        }
    }

    private void navigateToPhoneInvite() {
        ActivityParameters params = new ActivityParameters();
        params.putFriendFinderDone(true);
        try {
            NavigationManager.getInstance().PushScreen(FriendFinderHomeScreen.class, params);
        } catch (XLEException e) {
        }
    }

    /* access modifiers changed from: protected */
    public void onStartOverride() {
        this.friendFinderType = NavigationManager.getInstance().getActivityParameters().getFriendFinderType();
        XLEAssert.assertTrue(this.friendFinderType != FriendFinderType.UNKNOWN);
        this.meProfileModel = ProfileModel.getMeProfileModel();
        XLEAssert.assertNotNull(this.meProfileModel);
    }

    public void onRehydrate() {
        this.adapter = new FriendFinderSuggestionsScreenAdapter(this);
    }

    /* access modifiers changed from: protected */
    public void onStopOverride() {
        cancelActiveTasks();
    }

    private void cancelActiveTasks() {
        if (this.getPeopleHubRecommendationsAsyncTask != null) {
            this.getPeopleHubRecommendationsAsyncTask.cancel();
        }
        if (this.addSuggestionsAsyncTask != null) {
            this.addSuggestionsAsyncTask.cancel();
        }
    }

    public boolean isBusy() {
        return this.isLoadingRecommendations || this.isAddingSuggestions;
    }

    public void load(boolean forceRefresh) {
        cancelActiveTasks();
        this.getPeopleHubRecommendationsAsyncTask = new GetPeopleHubRecommendationsAsyncTask();
        this.getPeopleHubRecommendationsAsyncTask.load(true);
    }

    public boolean onBackButtonPressed() {
        UTCFriendFinder.trackBackButtonPressed(getScreen().getName(), this.friendFinderType);
        return super.onBackButtonPressed();
    }

    /* access modifiers changed from: private */
    public void onGetPeopleHubRecommendationsAsyncTaskCompleted(AsyncActionStatus status) {
        this.isLoadingRecommendations = false;
        switch (status) {
            case SUCCESS:
            case NO_CHANGE:
            case NO_OP_SUCCESS:
                updateFoundPeople();
                break;
            case FAIL:
            case NO_OP_FAIL:
                showError(R.string.Service_ErrorText);
                break;
        }
        updateAdapter();
    }

    private void updateFoundPeople() {
        IPeopleHubResult.RecommendationType recommendationType = this.friendFinderType == FriendFinderType.FACEBOOK ? IPeopleHubResult.RecommendationType.FacebookFriend : IPeopleHubResult.RecommendationType.PhoneContact;
        IPeopleHubResult.PeopleHubPeopleSummary recommendations = this.meProfileModel.getPeopleHubRecommendationsRawData();
        this.foundPeople = new ArrayList<>();
        if (recommendations != null) {
            Iterator i$ = recommendations.people.iterator();
            while (i$.hasNext()) {
                IPeopleHubResult.PeopleHubPersonSummary person = i$.next();
                if (person.recommendation.getRecommendationType() == recommendationType) {
                    this.foundPeople.add(person);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void onAddSuggestionsCompleted(AsyncActionStatus status) {
        switch (status) {
            case SUCCESS:
            case NO_CHANGE:
            case NO_OP_SUCCESS:
                navigateToInvite();
                return;
            case FAIL:
            case NO_OP_FAIL:
                showError(R.string.Service_ErrorText);
                return;
            default:
                return;
        }
    }

    private class GetPeopleHubRecommendationsAsyncTask extends NetworkAsyncTask<AsyncActionStatus> {
        private GetPeopleHubRecommendationsAsyncTask() {
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

        /* access modifiers changed from: protected */
        public AsyncActionStatus loadDataInBackground() {
            FriendFinderSuggestionsScreenViewModel.this.meProfileModel.loadSync(true);
            return FriendFinderSuggestionsScreenViewModel.this.meProfileModel.loadPeopleHubRecommendations(true).getStatus();
        }

        /* access modifiers changed from: protected */
        public void onPreExecute() {
            boolean unused = FriendFinderSuggestionsScreenViewModel.this.isLoadingRecommendations = true;
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(AsyncActionStatus status) {
            FriendFinderSuggestionsScreenViewModel.this.onGetPeopleHubRecommendationsAsyncTaskCompleted(status);
        }
    }

    private class AddSuggestionsAsyncTask extends NetworkAsyncTask<AsyncActionStatus> {
        private ArrayList<String> xuids;

        public AddSuggestionsAsyncTask(ArrayList<String> xuids2) {
            this.xuids = xuids2;
        }

        /* access modifiers changed from: protected */
        public boolean checkShouldExecute() {
            XLEAssert.assertIsUIThread();
            return this.xuids.size() > 0;
        }

        /* access modifiers changed from: protected */
        public void onNoAction() {
            XLEAssert.assertIsUIThread();
            FriendFinderSuggestionsScreenViewModel.this.onAddSuggestionsCompleted(AsyncActionStatus.NO_CHANGE);
        }

        /* access modifiers changed from: protected */
        public void onPreExecute() {
            XLEAssert.assertIsUIThread();
            boolean unused = FriendFinderSuggestionsScreenViewModel.this.isAddingSuggestions = true;
            FriendFinderSuggestionsScreenViewModel.this.updateAdapter();
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(AsyncActionStatus result) {
            boolean unused = FriendFinderSuggestionsScreenViewModel.this.isAddingSuggestions = false;
            FriendFinderSuggestionsScreenViewModel.this.onAddSuggestionsCompleted(result);
        }

        /* access modifiers changed from: protected */
        public AsyncActionStatus onError() {
            return AsyncActionStatus.FAIL;
        }

        /* access modifiers changed from: protected */
        public AsyncActionStatus loadDataInBackground() {
            try {
                return ServiceManagerFactory.getInstance().getSLSServiceManager().addUserToFollowingList(FavoriteListRequest.getFavoriteListRequestBody(new FavoriteListRequest(this.xuids))).getAddFollowingRequestStatus() ? AsyncActionStatus.SUCCESS : AsyncActionStatus.FAIL;
            } catch (XLEException e) {
                return AsyncActionStatus.FAIL;
            }
        }
    }
}
