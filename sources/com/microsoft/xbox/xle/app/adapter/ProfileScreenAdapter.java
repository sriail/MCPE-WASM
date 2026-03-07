package com.microsoft.xbox.xle.app.adapter;

import android.view.View;
import android.widget.ScrollView;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.ui.CustomTypefaceTextView;
import com.microsoft.xbox.toolkit.ui.FastProgressBar;
import com.microsoft.xbox.toolkit.ui.XLERoundedUniversalImageView;
import com.microsoft.xbox.xle.app.ImageUtil;
import com.microsoft.xbox.xle.app.XLEUtil;
import com.microsoft.xbox.xle.app.activity.Profile.ProfileScreenViewModel;
import com.microsoft.xbox.xle.telemetry.helpers.UTCPeopleHub;
import com.microsoft.xbox.xle.ui.IconFontToggleButton;
import com.microsoft.xbox.xle.ui.XLERootView;
import com.microsoft.xbox.xle.viewmodel.AdapterBase;
import com.microsoft.xboxtcui.R;
import com.microsoft.xboxtcui.XboxAppDeepLinker;

public class ProfileScreenAdapter extends AdapterBase {
    /* access modifiers changed from: private */
    public IconFontToggleButton blockButton = ((IconFontToggleButton) findViewById(R.id.profile_block));
    private ScrollView contentScrollView = ((ScrollView) findViewById(R.id.profile_screen_content_list));
    private IconFontToggleButton followButton = ((IconFontToggleButton) findViewById(R.id.profile_follow));
    private XLERoundedUniversalImageView gamerPicImageView = ((XLERoundedUniversalImageView) findViewById(R.id.profile_gamerpic));
    private CustomTypefaceTextView gamerscoreIconTextView = ((CustomTypefaceTextView) findViewById(R.id.profile_gamerscore_icon));
    private CustomTypefaceTextView gamerscoreTextView = ((CustomTypefaceTextView) findViewById(R.id.profile_gamerscore));
    private CustomTypefaceTextView gamertagTextView = ((CustomTypefaceTextView) findViewById(R.id.profile_gamertag));
    private FastProgressBar loadingProgressBar = ((FastProgressBar) findViewById(R.id.profile_screen_loading));
    /* access modifiers changed from: private */
    public IconFontToggleButton muteButton = ((IconFontToggleButton) findViewById(R.id.profile_mute));
    private CustomTypefaceTextView realNameTextView = ((CustomTypefaceTextView) findViewById(R.id.profile_realname));
    private IconFontToggleButton reportButton = ((IconFontToggleButton) findViewById(R.id.profile_report));
    private XLERootView rootView = ((XLERootView) findViewById(R.id.profile_root));
    private IconFontToggleButton viewInXboxAppButton = ((IconFontToggleButton) findViewById(R.id.profile_view_in_xbox_app));
    private CustomTypefaceTextView viewInXboxAppSubTextView = ((CustomTypefaceTextView) findViewById(R.id.profile_view_in_xbox_app_subtext));
    /* access modifiers changed from: private */
    public ProfileScreenViewModel viewModel;

    public ProfileScreenAdapter(ProfileScreenViewModel viewModel2) {
        super(viewModel2);
        this.viewModel = viewModel2;
        this.viewInXboxAppButton.setVisibility(0);
        this.viewInXboxAppButton.setEnabled(true);
        this.viewInXboxAppButton.setChecked(true);
        if (this.viewModel.isMeProfile()) {
            this.followButton.setVisibility(8);
            this.muteButton.setVisibility(8);
            this.blockButton.setVisibility(8);
            this.reportButton.setVisibility(8);
            this.viewInXboxAppSubTextView.setText(R.string.Profile_ViewInXboxApp_Details_MeProfile);
            return;
        }
        this.followButton.setVisibility(0);
        this.followButton.setEnabled(true);
        this.muteButton.setVisibility(0);
        this.muteButton.setEnabled(true);
        this.muteButton.setChecked(false);
        this.blockButton.setVisibility(0);
        this.blockButton.setEnabled(false);
        this.reportButton.setVisibility(0);
        this.reportButton.setEnabled(true);
        this.reportButton.setChecked(false);
        this.viewInXboxAppSubTextView.setText(R.string.Profile_ViewInXboxApp_Details_YouProfile);
    }

    public void onStart() {
        super.onStart();
        if (this.followButton != null) {
            this.followButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    ProfileScreenAdapter.this.viewModel.navigateToChangeRelationship();
                }
            });
        }
        if (this.muteButton != null) {
            this.muteButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    ProfileScreenAdapter.this.muteButton.toggle();
                    ProfileScreenAdapter.this.muteButton.setEnabled(false);
                    if (ProfileScreenAdapter.this.muteButton.isChecked()) {
                        UTCPeopleHub.trackMute(true);
                        ProfileScreenAdapter.this.viewModel.muteUser();
                        return;
                    }
                    UTCPeopleHub.trackMute(false);
                    ProfileScreenAdapter.this.viewModel.unmuteUser();
                }
            });
        }
        if (this.blockButton != null) {
            this.blockButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    ProfileScreenAdapter.this.blockButton.toggle();
                    ProfileScreenAdapter.this.blockButton.setEnabled(false);
                    if (ProfileScreenAdapter.this.blockButton.isChecked()) {
                        UTCPeopleHub.trackBlock();
                        ProfileScreenAdapter.this.viewModel.blockUser();
                        return;
                    }
                    UTCPeopleHub.trackUnblock();
                    ProfileScreenAdapter.this.viewModel.unblockUser();
                }
            });
        }
        if (this.reportButton != null) {
            this.reportButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    UTCPeopleHub.trackReport();
                    ProfileScreenAdapter.this.viewModel.showReportDialog();
                }
            });
        }
        if (this.viewInXboxAppButton == null) {
            return;
        }
        if (XboxAppDeepLinker.appDeeplinkingSupported()) {
            this.viewInXboxAppButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    UTCPeopleHub.trackViewInXboxApp();
                    ProfileScreenAdapter.this.viewModel.launchXboxApp();
                }
            });
            return;
        }
        this.viewInXboxAppButton.setVisibility(8);
        this.viewInXboxAppSubTextView.setVisibility(8);
    }

    /* access modifiers changed from: protected */
    public void updateViewOverride() {
        int i;
        boolean pendingBlockChange;
        boolean z;
        boolean z2;
        boolean z3 = true;
        if (this.rootView != null) {
            this.rootView.setBackgroundColor(this.viewModel.getPreferredColor());
        }
        this.loadingProgressBar.setVisibility(this.viewModel.isBusy() ? 0 : 8);
        ScrollView scrollView = this.contentScrollView;
        if (this.viewModel.isBusy()) {
            i = 8;
        } else {
            i = 0;
        }
        scrollView.setVisibility(i);
        if (this.gamerPicImageView != null) {
            this.gamerPicImageView.setImageURI2(ImageUtil.getMedium(this.viewModel.getGamerPicUrl()), R.drawable.gamerpic_missing, R.drawable.gamerpic_missing);
        }
        if (this.realNameTextView != null) {
            String realName = this.viewModel.getRealName();
            if (!JavaUtil.isNullOrEmpty(realName)) {
                this.realNameTextView.setText(realName);
                this.realNameTextView.setVisibility(0);
            } else {
                this.realNameTextView.setVisibility(8);
            }
        }
        if (!(this.gamerscoreTextView == null || this.gamerscoreIconTextView == null)) {
            String gamerScore = this.viewModel.getGamerScore();
            if (!JavaUtil.isNullOrEmpty(gamerScore)) {
                XLEUtil.updateTextAndVisibilityIfNotNull(this.gamerscoreTextView, gamerScore, 0);
                XLEUtil.updateVisibilityIfNotNull(this.gamerscoreIconTextView, 0);
            }
        }
        if (this.gamertagTextView != null) {
            String gamerTag = this.viewModel.getGamerTag();
            if (!JavaUtil.isNullOrEmpty(gamerTag)) {
                XLEUtil.updateTextAndVisibilityIfNotNull(this.gamertagTextView, gamerTag, 0);
            }
        }
        if (!this.viewModel.isMeProfile()) {
            if (this.viewModel.getIsAddingUserToBlockList() || this.viewModel.getIsRemovingUserFromBlockList()) {
                pendingBlockChange = true;
            } else {
                pendingBlockChange = false;
            }
            this.followButton.setChecked(this.viewModel.isCallerFollowingTarget());
            IconFontToggleButton iconFontToggleButton = this.followButton;
            if (pendingBlockChange || this.viewModel.getIsBlocked()) {
                z = false;
            } else {
                z = true;
            }
            iconFontToggleButton.setEnabled(z);
            this.muteButton.setChecked(this.viewModel.getIsMuted());
            IconFontToggleButton iconFontToggleButton2 = this.muteButton;
            if (this.viewModel.getIsAddingUserToMutedList() || this.viewModel.getIsRemovingUserFromMutedList()) {
                z2 = false;
            } else {
                z2 = true;
            }
            iconFontToggleButton2.setEnabled(z2);
            this.blockButton.setChecked(this.viewModel.getIsBlocked());
            IconFontToggleButton iconFontToggleButton3 = this.blockButton;
            if (pendingBlockChange) {
                z3 = false;
            }
            iconFontToggleButton3.setEnabled(z3);
        }
    }
}
