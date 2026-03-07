package com.microsoft.xbox.xle.app.activity.FriendFinder;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import com.microsoft.xbox.service.model.ProfileModel;
import com.microsoft.xbox.service.model.friendfinder.FriendFinderType;
import com.microsoft.xbox.service.model.privacy.PrivacySettings;
import com.microsoft.xbox.service.network.managers.friendfinder.FacebookManager;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.ui.ActivityParameters;
import com.microsoft.xbox.toolkit.ui.CustomTypefaceTextView;
import com.microsoft.xbox.toolkit.ui.NavigationManager;
import com.microsoft.xbox.toolkit.ui.XLEButton;
import com.microsoft.xbox.xle.app.activity.ActivityBase;
import com.microsoft.xbox.xle.telemetry.helpers.UTCFriendFinder;
import com.microsoft.xboxtcui.R;
import com.microsoft.xboxtcui.XboxTcuiSdk;

public class FriendFinderInfoScreen extends ActivityBase {
    private FriendFinderType infoType = FriendFinderType.UNKNOWN;
    private XLEButton nextButton;
    private CustomTypefaceTextView subtitleTextView;
    private CustomTypefaceTextView titleTextView;

    public FriendFinderInfoScreen() {
    }

    public FriendFinderInfoScreen(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void onCreate() {
        super.onCreate();
        onCreateContentView();
        ActivityParameters params = NavigationManager.getInstance().getActivityParameters();
        XLEAssert.assertNotNull(params);
        if (params != null) {
            this.infoType = params.getFriendFinderType();
            XLEAssert.assertFalse("Expected info type", this.infoType == FriendFinderType.UNKNOWN);
        }
    }

    public void onCreateContentView() {
        setContentView(R.layout.friend_finder_info_screen);
        this.titleTextView = (CustomTypefaceTextView) findViewById(R.id.friendfinder_info_title);
        this.subtitleTextView = (CustomTypefaceTextView) findViewById(R.id.friendfinder_info_subtitle);
        this.nextButton = (XLEButton) findViewById(R.id.friendfinder_info_next);
        XLEAssert.assertNotNull(this.titleTextView);
        XLEAssert.assertNotNull(this.subtitleTextView);
        XLEAssert.assertNotNull(this.nextButton);
    }

    public void onStart() {
        super.onStart();
        switch (this.infoType) {
            case FACEBOOK:
                this.titleTextView.setText(R.string.FriendFinder_LinkFacebook_Dialog_Title);
                this.subtitleTextView.setText(getFacebookText());
                this.nextButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        UTCFriendFinder.trackFacebookOptInNext(FriendFinderInfoScreen.this.getName());
                        XLEAssert.assertTrue(FacebookManager.getFacebookManagerReady().getIsReady());
                        FacebookManager.getInstance().login();
                    }
                });
                UTCFriendFinder.trackFacebookOptInView(getName());
                return;
            case PHONE:
                this.titleTextView.setText(R.string.FriendFinder_LinkPhone_Dialog_Title);
                this.subtitleTextView.setText(getPhoneText());
                this.nextButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        UTCFriendFinder.trackPhoneContactsNext(FriendFinderInfoScreen.this.getName());
                        try {
                            NavigationManager.getInstance().PushScreen(FriendFinderAddPhoneScreen.class);
                        } catch (XLEException e) {
                        }
                    }
                });
                UTCFriendFinder.trackContactsOptInView(getName());
                return;
            default:
                return;
        }
    }

    private String getFacebookText() {
        switch (PrivacySettings.PrivacySettingValue.getPrivacySettingValue(ProfileModel.getMeProfileModel().getShareRealNameStatus())) {
            case NotSet:
                return multiLineText(R.string.FriendFinder_LinkFacebook_Dialog_Text_Default, R.string.FriendFinder_LinkFacebook_Dialog_Text_NotSet_LineTwo, R.string.FriendFinder_LinkFacebook_Dialog_Text_NotSet_LineThree);
            case Blocked:
                return multiLineText(R.string.FriendFinder_LinkFacebook_Dialog_Text_Default, R.string.FriendFinder_LinkFacebook_Dialog_Text_Blocked_LineTwo, R.string.FriendFinder_LinkFacebook_Dialog_Text_Blocked_LineThree);
            case FriendCategoryShareIdentity:
                return multiLineText(R.string.FriendFinder_LinkFacebook_Dialog_Text_Default, R.string.FriendFinder_LinkFacebook_Dialog_Text_PeopleIChoose_LineTwo);
            default:
                return XboxTcuiSdk.getResources().getString(R.string.FriendFinder_LinkFacebook_Dialog_Text_Default);
        }
    }

    private String getPhoneText() {
        switch (PrivacySettings.PrivacySettingValue.getPrivacySettingValue(ProfileModel.getMeProfileModel().getShareRealNameStatus())) {
            case NotSet:
            case Blocked:
                return multiLineText(R.string.FriendFinder_LinkPhone_Dialog_Text_LineOne, R.string.FriendFinder_LinkPhone_Dialog_Text_LineTwo, R.string.FriendFinder_LinkPhone_Dialog_Text_RealNameSharedWithContacts, R.string.FriendFinder_LinkPhone_Dialog_Text_LineThree);
            case FriendCategoryShareIdentity:
            case Everyone:
                return multiLineText(R.string.FriendFinder_LinkPhone_Dialog_Text_LineOne, R.string.FriendFinder_LinkPhone_Dialog_Text_LineTwo, R.string.FriendFinder_LinkPhone_Dialog_Text_LineThree);
            case PeopleOnMyList:
                return multiLineText(R.string.FriendFinder_LinkPhone_Dialog_Text_LineOne, R.string.FriendFinder_LinkPhone_Dialog_Text_LineTwo, R.string.FriendFinder_LinkPhone_Dialog_Text_RealNameSharedWithContacts, R.string.FriendFinder_LinkPhone_Dialog_Text_ACoupleNotes, R.string.FriendFinder_LinkPhone_Dialog_Text_LineThree);
            default:
                return multiLineText(R.string.FriendFinder_LinkPhone_Dialog_Text_LineOne, R.string.FriendFinder_LinkPhone_Dialog_Text_LineTwo);
        }
    }

    private String multiLineText(int... textIds) {
        if (textIds.length == 0) {
            return "";
        }
        String text = XboxTcuiSdk.getResources().getString(textIds[0]);
        for (int i = 1; i < textIds.length; i++) {
            text = text + "\n\n" + XboxTcuiSdk.getResources().getString(textIds[i]);
        }
        return text;
    }

    /* access modifiers changed from: protected */
    public String getActivityName() {
        switch (this.infoType) {
            case FACEBOOK:
                return "Friend finder facebook info";
            case PHONE:
                return "Friend finder phone info";
            default:
                return "Friend finder info";
        }
    }

    public boolean onBackButtonPressed() {
        UTCFriendFinder.trackBackButtonPressed(getName(), this.infoType);
        return super.onBackButtonPressed();
    }
}
