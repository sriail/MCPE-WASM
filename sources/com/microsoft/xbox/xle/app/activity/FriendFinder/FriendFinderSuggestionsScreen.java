package com.microsoft.xbox.xle.app.activity.FriendFinder;

import android.content.Context;
import android.util.AttributeSet;
import com.microsoft.xbox.toolkit.ui.NavigationManager;
import com.microsoft.xbox.xle.app.activity.ActivityBase;
import com.microsoft.xbox.xle.telemetry.helpers.UTCFriendFinder;
import com.microsoft.xboxtcui.R;

public class FriendFinderSuggestionsScreen extends ActivityBase {
    public FriendFinderSuggestionsScreen() {
    }

    public FriendFinderSuggestionsScreen(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void onCreate() {
        super.onCreate();
        onCreateContentView();
        this.viewModel = new FriendFinderSuggestionsScreenViewModel(this);
    }

    public void onStart() {
        super.onStart();
        switch (NavigationManager.getInstance().getActivityParameters().getFriendFinderType()) {
            case FACEBOOK:
                UTCFriendFinder.trackFacebookAddFriendView(getActivityName());
                return;
            case PHONE:
                UTCFriendFinder.trackContactsFindFriendsView(getActivityName());
                return;
            default:
                return;
        }
    }

    public void onCreateContentView() {
        setContentView(R.layout.friendfinder_suggestions_screen);
    }

    /* access modifiers changed from: protected */
    public String getActivityName() {
        return "Friend Finder Suggestions";
    }
}
