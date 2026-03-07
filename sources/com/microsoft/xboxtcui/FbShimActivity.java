package com.microsoft.xboxtcui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.facebook.FacebookSdk;
import com.microsoft.xbox.service.model.ProfileModel;
import com.microsoft.xbox.service.network.managers.friendfinder.FacebookManager;
import com.microsoft.xbox.toolkit.XLEAssert;

public class FbShimActivity extends Activity {
    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        XLEAssert.assertTrue(FacebookManager.getFacebookManagerReady().getIsReady());
        XLEAssert.assertTrue(FacebookSdk.isInitialized());
        XLEAssert.assertNotNull(ProfileModel.getMeProfileModel());
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FacebookManager.getInstance().onShimActivityResult(requestCode, resultCode, data);
        finish();
    }
}
