package com.microsoft.xboxtcui;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.microsoft.xbox.service.model.ProfileModel;
import com.microsoft.xbox.service.network.managers.friendfinder.FacebookManager;
import com.microsoft.xbox.xle.app.ImageUtil;
import java.net.URI;

public class FbShareShimActivity extends FbShimActivity {
    private final String SHARE_TO_FACEBOOK_LINK = "http://go.microsoft.com/fwlink/?LinkId=698852";
    private ShareDialog shareDialog;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            this.shareDialog = new ShareDialog((Activity) this);
            FacebookManager.getInstance().registerShareCallback(this.shareDialog);
            URI imageUrl = ImageUtil.getMedium(ProfileModel.getMeProfileModel().getGamerPicImageUrl());
            if (ShareDialog.canShow(ShareLinkContent.class)) {
                this.shareDialog.show(((ShareLinkContent.Builder) new ShareLinkContent.Builder().setImageUrl(Uri.parse(imageUrl.toString())).setContentTitle(XboxTcuiSdk.getResources().getString(R.string.FriendFinder_Facebook_Share_Title)).setContentDescription(XboxTcuiSdk.getResources().getString(R.string.FriendFinder_Facebook_Share_Description)).setContentUrl(Uri.parse("http://go.microsoft.com/fwlink/?LinkId=698852"))).build());
            }
        }
    }
}
