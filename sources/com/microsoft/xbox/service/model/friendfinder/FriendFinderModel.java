package com.microsoft.xbox.service.model.friendfinder;

import com.microsoft.xbox.service.model.ModelBase;
import com.microsoft.xbox.service.model.UpdateData;
import com.microsoft.xbox.service.model.UpdateType;
import com.microsoft.xbox.service.model.friendfinder.FriendFinderState;
import com.microsoft.xbox.service.network.managers.ServiceManagerFactory;
import com.microsoft.xbox.service.network.managers.friendfinder.FacebookManager;
import com.microsoft.xbox.toolkit.AsyncActionStatus;
import com.microsoft.xbox.toolkit.AsyncResult;
import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.network.IDataLoaderRunnable;

public class FriendFinderModel extends ModelBase<FriendFinderState.FriendsFinderStateResult> {
    private static FriendFinderModel instance = new FriendFinderModel();
    private LoadFailedCallback callback;
    private FriendFinderState.FriendsFinderStateResult result;

    public interface LoadFailedCallback {
        void onFriendFinderLoadFailed();
    }

    public static FriendFinderModel getInstance() {
        return instance;
    }

    public FriendFinderState.FriendsFinderStateResult getResult() {
        return this.result;
    }

    public boolean shouldRefresh() {
        return shouldRefresh(this.lastRefreshTime);
    }

    public void updateWithNewData(AsyncResult<FriendFinderState.FriendsFinderStateResult> result2) {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        super.updateWithNewData(result2);
        if (result2.getStatus() == AsyncActionStatus.SUCCESS && result2.getResult() != null) {
            this.result = result2.getResult();
            FacebookManager.getInstance().setFacebookFriendFinderState(this.result);
            notifyObservers(new AsyncResult(new UpdateData(UpdateType.FriendFinderFacebook, true), this, result2.getException()));
        } else if (this.callback != null) {
            this.callback.onFriendFinderLoadFailed();
            this.callback = null;
        }
    }

    public boolean isLoading() {
        return this.isLoading;
    }

    public void loadAsync(boolean forceRefresh, LoadFailedCallback callback2) {
        this.callback = callback2;
        loadAsync(forceRefresh);
    }

    public void loadAsync(boolean forceRefresh) {
        loadInternal(forceRefresh, UpdateType.FriendFinderFacebook, new GetPeopleHubFriendFinderStateResultRunner());
    }

    private class GetPeopleHubFriendFinderStateResultRunner extends IDataLoaderRunnable<FriendFinderState.FriendsFinderStateResult> {
        public GetPeopleHubFriendFinderStateResultRunner() {
        }

        public FriendFinderState.FriendsFinderStateResult buildData() throws XLEException {
            return ServiceManagerFactory.getInstance().getSLSServiceManager().getPeopleHubFriendFinderState();
        }

        public void onPreExecute() {
            boolean unused = FriendFinderModel.this.isLoading = true;
        }

        public void onPostExcute(AsyncResult<FriendFinderState.FriendsFinderStateResult> result) {
            FriendFinderModel.this.updateWithNewData(result);
        }

        public long getDefaultErrorCode() {
            return 11;
        }
    }
}
