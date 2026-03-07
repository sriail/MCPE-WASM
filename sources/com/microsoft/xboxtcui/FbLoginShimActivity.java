package com.microsoft.xboxtcui;

import android.app.Activity;
import android.os.Bundle;
import com.facebook.login.LoginManager;
import com.microsoft.xbox.service.network.managers.friendfinder.FacebookManager;
import java.util.Arrays;
import java.util.Collection;

public class FbLoginShimActivity extends FbShimActivity {
    public static final String LOGIN_TYPE_KEY = "LoginType";

    public enum LoginType {
        READ,
        PUBLISH
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            LoginType loginType = (LoginType) getIntent().getExtras().getSerializable(LOGIN_TYPE_KEY);
            if (loginType == LoginType.READ) {
                LoginManager.getInstance().setLoginBehavior(FacebookManager.getInstance().getLoginBehavior()).logInWithReadPermissions((Activity) this, (Collection<String>) FacebookManager.getInstance().getFacebookPermission());
            } else if (loginType == LoginType.PUBLISH) {
                LoginManager.getInstance().setLoginBehavior(FacebookManager.getInstance().getLoginBehavior()).logInWithPublishPermissions((Activity) this, (Collection<String>) Arrays.asList(new String[]{"publish_actions"}));
            }
        }
    }
}
