package com.microsoft.xbox.xle.app.adapter;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.ui.XLEButton;
import com.microsoft.xbox.xle.app.activity.FriendFinder.FriendFinderVerifyCodeScreenViewModel;
import com.microsoft.xbox.xle.telemetry.helpers.UTCFriendFinder;
import com.microsoft.xbox.xle.ui.IconFontToggleButton;
import com.microsoft.xbox.xle.viewmodel.AdapterBase;
import com.microsoft.xboxtcui.R;

public class FriendFinderVerifyCodeScreenAdapter extends AdapterBase {
    private IconFontToggleButton callMeButton = ((IconFontToggleButton) findViewById(R.id.friendfinder_verify_call_me));
    /* access modifiers changed from: private */
    public EditText codeEditText = ((EditText) findViewById(R.id.friendfinder_verify_code_edit_text));
    private FrameLayout loadingLayout = ((FrameLayout) findViewById(R.id.friendfinder_verify_loading));
    private IconFontToggleButton resendCodeButton = ((IconFontToggleButton) findViewById(R.id.friendfinder_verify_resend));
    /* access modifiers changed from: private */
    public XLEButton verifyButton = ((XLEButton) findViewById(R.id.friendfinder_verify_verify_code));
    /* access modifiers changed from: private */
    public FriendFinderVerifyCodeScreenViewModel viewModel;

    public FriendFinderVerifyCodeScreenAdapter(FriendFinderVerifyCodeScreenViewModel viewModel2) {
        super(viewModel2);
        this.viewModel = viewModel2;
        XLEAssert.assertNotNull(this.codeEditText);
        XLEAssert.assertNotNull(this.resendCodeButton);
        XLEAssert.assertNotNull(this.callMeButton);
        XLEAssert.assertNotNull(this.verifyButton);
        XLEAssert.assertNotNull(this.loadingLayout);
        this.resendCodeButton.setChecked(false);
        this.resendCodeButton.setEnabled(true);
        this.callMeButton.setChecked(false);
        this.callMeButton.setEnabled(true);
        this.verifyButton.setEnabled(false);
    }

    public void onStart() {
        super.onStart();
        this.codeEditText.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                FriendFinderVerifyCodeScreenAdapter.this.verifyButton.setEnabled(s.length() > 0);
            }
        });
        this.resendCodeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                UTCFriendFinder.trackPhoneContactsResendCode(FriendFinderVerifyCodeScreenAdapter.this.viewModel.getScreen().getName());
                FriendFinderVerifyCodeScreenAdapter.this.viewModel.resendCode();
            }
        });
        this.callMeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                UTCFriendFinder.trackPhoneContactsCallMe(FriendFinderVerifyCodeScreenAdapter.this.viewModel.getScreen().getName());
                FriendFinderVerifyCodeScreenAdapter.this.viewModel.callMe();
            }
        });
        this.verifyButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                UTCFriendFinder.trackPhoneContactsNext(FriendFinderVerifyCodeScreenAdapter.this.viewModel.getScreen().getName());
                FriendFinderVerifyCodeScreenAdapter.this.viewModel.verifyCode(FriendFinderVerifyCodeScreenAdapter.this.codeEditText.getText().toString());
            }
        });
    }

    /* access modifiers changed from: protected */
    public void updateViewOverride() {
        boolean z;
        boolean z2 = true;
        this.loadingLayout.setVisibility(this.viewModel.isBusy() ? 0 : 8);
        IconFontToggleButton iconFontToggleButton = this.resendCodeButton;
        if (!this.viewModel.isSendingCode()) {
            z = true;
        } else {
            z = false;
        }
        iconFontToggleButton.setEnabled(z);
        IconFontToggleButton iconFontToggleButton2 = this.callMeButton;
        if (this.viewModel.isSendingCode()) {
            z2 = false;
        }
        iconFontToggleButton2.setEnabled(z2);
    }
}
