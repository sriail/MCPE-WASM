package com.microsoft.xbox.xle.app.adapter;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.ui.CustomTypefaceTextView;
import com.microsoft.xbox.toolkit.ui.XLEButton;
import com.microsoft.xbox.xle.app.activity.FriendFinder.FriendFinderHomeScreenViewModel;
import com.microsoft.xbox.xle.telemetry.helpers.UTCFriendFinder;
import com.microsoft.xbox.xle.ui.IconFontSubTextButton;
import com.microsoft.xbox.xle.ui.ImageTitleSubtitleButton;
import com.microsoft.xbox.xle.viewmodel.AdapterBase;
import com.microsoft.xboxtcui.R;
import java.util.ArrayList;
import java.util.Arrays;

public class FriendFinderHomeScreenAdapter extends AdapterBase implements View.OnClickListener {
    private XLEButton doneButton;
    private ImageTitleSubtitleButton inviteFacebookButton;
    private CustomTypefaceTextView inviteFriendsTextView;
    private IconFontSubTextButton invitePhoneButton;
    private ImageTitleSubtitleButton linkFacebookButton;
    private IconFontSubTextButton linkPhoneButton;
    private FrameLayout loadingFrameLayout;
    private IconFontSubTextButton searchButton;
    /* access modifiers changed from: private */
    public EditText searchEditText;
    private FrameLayout searchIconButton;
    /* access modifiers changed from: private */
    public final ArrayList<Integer> searchImeActions = new ArrayList<>(Arrays.asList(new Integer[]{6, 5, 3, 2, 4}));
    private RelativeLayout searchLayout;
    /* access modifiers changed from: private */
    public FriendFinderHomeScreenViewModel viewModel;

    public FriendFinderHomeScreenAdapter(FriendFinderHomeScreenViewModel viewModel2) {
        super(viewModel2);
        this.viewModel = viewModel2;
        this.linkFacebookButton = (ImageTitleSubtitleButton) findViewById(R.id.friendfinder_link_facebook);
        this.linkPhoneButton = (IconFontSubTextButton) findViewById(R.id.friendfinder_link_phone);
        this.searchButton = (IconFontSubTextButton) findViewById(R.id.friendfinder_link_search);
        this.searchIconButton = (FrameLayout) findViewById(R.id.friendfinder_search_icon);
        this.searchLayout = (RelativeLayout) findViewById(R.id.friendfinder_search_layout);
        this.searchEditText = (EditText) findViewById(R.id.friendfinder_search_edittext);
        this.inviteFacebookButton = (ImageTitleSubtitleButton) findViewById(R.id.friendfinder_home_invite_facebook);
        this.invitePhoneButton = (IconFontSubTextButton) findViewById(R.id.friendfinder_home_invite_phone);
        this.inviteFriendsTextView = (CustomTypefaceTextView) findViewById(R.id.friendfinder_home_invite_friends_text);
        this.loadingFrameLayout = (FrameLayout) findViewById(R.id.friendfinder_home_loading);
        this.doneButton = (XLEButton) findViewById(R.id.friendfinder_home_done);
        XLEAssert.assertNotNull(this.linkFacebookButton);
        XLEAssert.assertNotNull(this.linkPhoneButton);
        XLEAssert.assertNotNull(this.searchButton);
        XLEAssert.assertNotNull(this.searchIconButton);
        XLEAssert.assertNotNull(this.searchLayout);
        XLEAssert.assertNotNull(this.searchEditText);
        XLEAssert.assertNotNull(this.inviteFacebookButton);
        XLEAssert.assertNotNull(this.invitePhoneButton);
        XLEAssert.assertNotNull(this.inviteFriendsTextView);
        XLEAssert.assertNotNull(this.loadingFrameLayout);
        XLEAssert.assertNotNull(this.doneButton);
    }

    public void onStart() {
        super.onStart();
        this.linkFacebookButton.setOnClickListener(this);
        this.linkPhoneButton.setOnClickListener(this);
        this.searchButton.setOnClickListener(this);
        this.inviteFacebookButton.setOnClickListener(this);
        this.invitePhoneButton.setOnClickListener(this);
        this.doneButton.setOnClickListener(this);
        this.linkFacebookButton.setImageUri("");
        this.inviteFacebookButton.setImageUri("");
        this.searchLayout.setVisibility(8);
        final String activityName = this.viewModel.getScreen().getName();
        this.searchIconButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FriendFinderHomeScreenAdapter.this.viewModel.searchGamertag(FriendFinderHomeScreenAdapter.this.searchEditText.getText().toString());
            }
        });
        this.searchEditText.addTextChangedListener(new TextWatcher() {
            private boolean isEnterKey;

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                this.isEnterKey = s.length() > 0 && s.subSequence(s.length() + -1, s.length()).toString().equalsIgnoreCase("\n");
                if (this.isEnterKey) {
                    UTCFriendFinder.trackGamertagSearchSubmit(activityName);
                    FriendFinderHomeScreenAdapter.this.viewModel.searchGamertag(FriendFinderHomeScreenAdapter.this.searchEditText.getText().toString());
                }
            }

            public void afterTextChanged(Editable s) {
                if (this.isEnterKey) {
                    s.delete(s.length() - 1, s.length());
                }
            }
        });
        this.searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (!FriendFinderHomeScreenAdapter.this.searchImeActions.contains(Integer.valueOf(actionId))) {
                    return false;
                }
                UTCFriendFinder.trackGamertagSearchSubmit(activityName);
                FriendFinderHomeScreenAdapter.this.viewModel.searchGamertag(FriendFinderHomeScreenAdapter.this.searchEditText.getText().toString());
                return true;
            }
        });
        this.searchEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                InputMethodManager inputMethodManager = (InputMethodManager) FriendFinderHomeScreenAdapter.this.searchEditText.getContext().getSystemService("input_method");
                if (hasFocus) {
                    inputMethodManager.toggleSoftInput(2, 0);
                } else {
                    inputMethodManager.toggleSoftInput(1, 0);
                }
            }
        });
    }

    /* access modifiers changed from: protected */
    public void updateViewOverride() {
        setViewVisibilities();
        String facebookIcon = this.viewModel.getFacebookIconUri();
        if (!JavaUtil.isNullOrEmpty(facebookIcon)) {
            this.linkFacebookButton.setImageUri(facebookIcon);
            this.inviteFacebookButton.setImageUri(facebookIcon);
        }
    }

    private void setViewVisibilities() {
        int i;
        int i2;
        int i3;
        int i4;
        int i5;
        int i6 = 8;
        this.linkFacebookButton.setVisibility(this.viewModel.facebookLinked() ? 8 : 0);
        IconFontSubTextButton iconFontSubTextButton = this.linkPhoneButton;
        if (this.viewModel.phoneLinked()) {
            i = 8;
        } else {
            i = 0;
        }
        iconFontSubTextButton.setVisibility(i);
        ImageTitleSubtitleButton imageTitleSubtitleButton = this.inviteFacebookButton;
        if (this.viewModel.facebookLinked()) {
            i2 = 0;
        } else {
            i2 = 8;
        }
        imageTitleSubtitleButton.setVisibility(i2);
        IconFontSubTextButton iconFontSubTextButton2 = this.invitePhoneButton;
        if (this.viewModel.phoneLinked()) {
            i3 = 0;
        } else {
            i3 = 8;
        }
        iconFontSubTextButton2.setVisibility(i3);
        FrameLayout frameLayout = this.loadingFrameLayout;
        if (this.viewModel.isBusy()) {
            i4 = 0;
        } else {
            i4 = 8;
        }
        frameLayout.setVisibility(i4);
        XLEButton xLEButton = this.doneButton;
        if (this.viewModel.shouldShowDone()) {
            i5 = 0;
        } else {
            i5 = 8;
        }
        xLEButton.setVisibility(i5);
        CustomTypefaceTextView customTypefaceTextView = this.inviteFriendsTextView;
        if (this.viewModel.facebookLinked() || this.viewModel.phoneLinked()) {
            i6 = 0;
        }
        customTypefaceTextView.setVisibility(i6);
    }

    public void onClick(View v) {
        int id = v.getId();
        CharSequence activityName = this.viewModel.getScreen().getName();
        if (id == R.id.friendfinder_link_facebook) {
            UTCFriendFinder.trackFacebookSignup(activityName);
            this.viewModel.navigateToLinkFacebook();
        } else if (id == R.id.friendfinder_link_phone) {
            UTCFriendFinder.trackContactsSignUp(activityName);
            this.viewModel.navigateToLinkPhone();
        } else if (id == R.id.friendfinder_link_search) {
            UTCFriendFinder.trackGamertagSearch(activityName);
            this.searchLayout.setVisibility(0);
            this.searchButton.setVisibility(8);
            this.searchEditText.requestFocus();
        } else if (id == R.id.friendfinder_home_invite_facebook) {
            UTCFriendFinder.trackFacebookSuggestions(activityName);
            this.viewModel.navigateToFacebookSuggestions();
        } else if (id == R.id.friendfinder_home_invite_phone) {
            UTCFriendFinder.trackContactsSuggestions(activityName);
            this.viewModel.navigateToPhoneSuggestions();
        } else if (id == R.id.friendfinder_home_done) {
            UTCFriendFinder.trackDone(activityName);
            this.viewModel.finishFriendFinder();
        }
    }
}
