package com.microsoft.xbox.service.network.managers.friendfinder;

import android.content.Intent;
import android.util.Log;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.Sharer;
import com.facebook.share.widget.ShareDialog;
import com.microsoft.xbox.service.model.friendfinder.FriendFinderModel;
import com.microsoft.xbox.service.model.friendfinder.FriendFinderState;
import com.microsoft.xbox.service.model.friendfinder.FriendFinderType;
import com.microsoft.xbox.service.network.managers.xblshared.ProtectedRunnable;
import com.microsoft.xbox.toolkit.Ready;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.ui.ActivityParameters;
import com.microsoft.xbox.toolkit.ui.NavigationManager;
import com.microsoft.xbox.xle.app.activity.FriendFinder.FriendFinderLinkScreen;
import com.microsoft.xbox.xle.telemetry.helpers.UTCFriendFinder;
import com.microsoft.xboxtcui.FbLoginShimActivity;
import com.microsoft.xboxtcui.FbShareShimActivity;
import com.microsoft.xboxtcui.XboxTcuiSdk;
import java.util.Arrays;
import java.util.List;

public class FacebookManager {
    private static Ready facebookManagerReady = new Ready();
    private static FacebookManager instance;
    /* access modifiers changed from: private */
    public CallbackManager callbackManager;
    /* access modifiers changed from: private */
    public List<String> facebookPermission;
    /* access modifiers changed from: private */
    public boolean firstLoginWithReadOnly = false;
    private FriendFinderState.FriendsFinderStateResult friendsFinderStateResult;
    private LoginBehavior loginBehavior;
    /* access modifiers changed from: private */
    public FacebookCallback<LoginResult> loginResult = new FacebookCallback<LoginResult>() {
        public void onSuccess(LoginResult loginResult) {
            AccessToken unused = FacebookManager.this.token = loginResult.getAccessToken();
            if (FacebookManager.this.token == null) {
                return;
            }
            if (FacebookManager.this.firstLoginWithReadOnly) {
                boolean unused2 = FacebookManager.this.firstLoginWithReadOnly = false;
                String unused3 = FacebookManager.this.tokenString = FacebookManager.this.token.getToken();
                ActivityParameters parameters = new ActivityParameters();
                parameters.putFriendFinderType(FriendFinderType.FACEBOOK);
                try {
                    NavigationManager.getInstance().PushScreen(FriendFinderLinkScreen.class, parameters);
                } catch (XLEException e) {
                }
            } else {
                FacebookManager.this.showShareDialog();
            }
        }

        public void onCancel() {
            UTCFriendFinder.trackFacebookLoginCancel((CharSequence) null);
            FacebookManager.this.loadPeopleHubFriendFinderState();
            FacebookManager.this.resetFacebookToken(true);
        }

        public void onError(FacebookException e) {
            FacebookManager.this.loadPeopleHubFriendFinderState();
            FacebookManager.this.resetFacebookToken(true);
        }
    };
    private FacebookCallback<Sharer.Result> shareResult = new FacebookCallback<Sharer.Result>() {
        public void onSuccess(Sharer.Result result) {
        }

        public void onCancel() {
        }

        public void onError(FacebookException error) {
        }
    };
    /* access modifiers changed from: private */
    public AccessToken token;
    /* access modifiers changed from: private */
    public String tokenString;

    private FacebookManager() {
        facebookManagerReady.reset();
        new ProtectedRunnable(new Runnable() {
            public void run() {
                try {
                    List unused = FacebookManager.this.facebookPermission = Arrays.asList(new String[]{"public_profile", "user_friends"});
                    FacebookSdk.sdkInitialize(XboxTcuiSdk.getApplicationContext());
                    CallbackManager unused2 = FacebookManager.this.callbackManager = CallbackManager.Factory.create();
                    LoginManager.getInstance().registerCallback(FacebookManager.this.callbackManager, FacebookManager.this.loginResult);
                } catch (Exception e) {
                    Log.i("h", e.getMessage());
                }
            }
        }).run();
        this.loginBehavior = LoginBehavior.WEB_ONLY;
        facebookManagerReady.setReady();
    }

    public static Ready getFacebookManagerReady() {
        return facebookManagerReady;
    }

    public static synchronized FacebookManager getInstance() {
        FacebookManager facebookManager;
        synchronized (FacebookManager.class) {
            if (instance == null) {
                instance = new FacebookManager();
            }
            facebookManager = instance;
        }
        return facebookManager;
    }

    public void registerShareCallback(ShareDialog shareDialog) {
        shareDialog.registerCallback(this.callbackManager, this.shareResult);
    }

    public LoginBehavior getLoginBehavior() {
        return this.loginBehavior;
    }

    public List<String> getFacebookPermission() {
        return this.facebookPermission;
    }

    public String getTokenString() {
        return this.tokenString;
    }

    public void login() {
        UTCFriendFinder.trackFacebookLinkAccountView((CharSequence) null);
        this.firstLoginWithReadOnly = true;
        XboxTcuiSdk.getActivity().startActivity(getReadShimIntent());
    }

    public void shareToFacebook() {
        this.firstLoginWithReadOnly = false;
        XboxTcuiSdk.getActivity().startActivity(getPublishShimIntent());
    }

    private Intent getReadShimIntent() {
        Intent shimIntent = new Intent(XboxTcuiSdk.getActivity(), FbLoginShimActivity.class);
        shimIntent.putExtra(FbLoginShimActivity.LOGIN_TYPE_KEY, FbLoginShimActivity.LoginType.READ);
        return shimIntent;
    }

    private Intent getPublishShimIntent() {
        Intent shimIntent = new Intent(XboxTcuiSdk.getActivity(), FbLoginShimActivity.class);
        shimIntent.putExtra(FbLoginShimActivity.LOGIN_TYPE_KEY, FbLoginShimActivity.LoginType.PUBLISH);
        return shimIntent;
    }

    public void resetFacebookToken(boolean forceResetLoginToken) {
        this.token = null;
        this.tokenString = null;
        if (forceResetLoginToken) {
            LoginManager.getInstance().logOut();
        }
    }

    public void setFacebookFriendFinderState(FriendFinderState.FriendsFinderStateResult result) {
        this.friendsFinderStateResult = result;
    }

    public FriendFinderState.FriendsFinderStateResult getFacebookFriendFinderState() {
        return this.friendsFinderStateResult;
    }

    public boolean isFacebookFriendFinderOptedIn() {
        return this.friendsFinderStateResult != null && this.friendsFinderStateResult.getLinkedAccountOptInStatus() == FriendFinderState.LinkedAccountOptInStatus.OptedIn && this.friendsFinderStateResult.getLinkedAccountTokenStatus() == FriendFinderState.LinkedAccountTokenStatus.OK;
    }

    public void onShimActivityResult(int requestCode, int resultCode, Intent data) {
        this.callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    /* access modifiers changed from: private */
    public void showShareDialog() {
        XboxTcuiSdk.getActivity().startActivity(new Intent(XboxTcuiSdk.getActivity(), FbShareShimActivity.class));
    }

    public void loadPeopleHubFriendFinderState() {
        FriendFinderModel.getInstance().loadAsync(true);
    }
}
