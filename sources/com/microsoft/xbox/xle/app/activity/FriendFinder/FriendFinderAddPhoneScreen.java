package com.microsoft.xbox.xle.app.activity.FriendFinder;

import com.microsoft.xbox.xle.app.activity.ActivityBase;
import com.microsoft.xbox.xle.telemetry.helpers.UTCFriendFinder;
import com.microsoft.xboxtcui.R;

public class FriendFinderAddPhoneScreen extends ActivityBase {
    public void onCreate() {
        super.onCreate();
        onCreateContentView();
        this.viewModel = new FriendFinderAddPhoneScreenViewModel(this);
    }

    public void onStart() {
        super.onStart();
        UTCFriendFinder.trackContactsAddPhoneView(getName());
    }

    public void onCreateContentView() {
        setContentView(R.layout.friendfinder_add_phone_screen);
    }

    /* access modifiers changed from: protected */
    public String getActivityName() {
        return "Friend Finder Add Phone";
    }
}
