package com.microsoft.xbox.xle.app.activity.Profile;

import android.content.Context;
import android.util.AttributeSet;
import com.microsoft.xbox.xle.app.activity.ActivityBase;
import com.microsoft.xbox.xle.telemetry.helpers.UTCPeopleHub;
import com.microsoft.xboxtcui.R;

public class ProfileScreen extends ActivityBase {
    public ProfileScreen() {
    }

    public ProfileScreen(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void onCreate() {
        super.onCreate();
        onCreateContentView();
        ProfileScreenViewModel psVM = new ProfileScreenViewModel(this);
        this.viewModel = psVM;
        UTCPeopleHub.trackPeopleHubView(getActivityName(), psVM.getXuid(), psVM.isMeProfile());
    }

    public void onCreateContentView() {
        setContentView(R.layout.profile_screen);
    }

    /* access modifiers changed from: protected */
    public String getActivityName() {
        return "PeopleHub Info";
    }
}
