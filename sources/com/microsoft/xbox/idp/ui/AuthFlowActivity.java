package com.microsoft.xbox.idp.ui;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import com.microsoft.onlineid.Ticket;
import com.microsoft.xbox.idp.R;
import com.microsoft.xbox.idp.interop.Interop;
import com.microsoft.xbox.idp.interop.XsapiUser;
import com.microsoft.xbox.idp.telemetry.helpers.UTCPageView;
import com.microsoft.xbox.idp.telemetry.helpers.UTCSignin;
import com.microsoft.xbox.idp.telemetry.helpers.UTCUser;
import com.microsoft.xbox.idp.telemetry.utc.model.UTCCommonDataModel;
import com.microsoft.xbox.idp.ui.AccountProvisioningFragment;
import com.microsoft.xbox.idp.ui.EventInitializationFragment;
import com.microsoft.xbox.idp.ui.FinishSignInFragment;
import com.microsoft.xbox.idp.ui.HeaderFragment;
import com.microsoft.xbox.idp.ui.IntroducingFragment;
import com.microsoft.xbox.idp.ui.MSAFragment;
import com.microsoft.xbox.idp.ui.SignUpFragment;
import com.microsoft.xbox.idp.ui.StartSignInFragment;
import com.microsoft.xbox.idp.ui.WelcomeFragment;
import com.microsoft.xbox.idp.ui.XBLoginFragment;
import com.microsoft.xbox.idp.ui.XBLogoutFragment;
import com.microsoft.xbox.idp.util.AuthFlowResult;
import com.microsoft.xbox.idp.util.CacheUtil;

public class AuthFlowActivity extends AuthActivity implements HeaderFragment.Callbacks, StartSignInFragment.Callbacks, MSAFragment.Callbacks, XBLoginFragment.Callbacks, AccountProvisioningFragment.Callbacks, SignUpFragment.Callbacks, EventInitializationFragment.Callbacks, WelcomeFragment.Callbacks, IntroducingFragment.Callbacks, FinishSignInFragment.Callbacks, XBLogoutFragment.Callbacks {
    public static final String ARG_ALT_BUTTON_TEXT = "ARG_ALT_BUTTON_TEXT";
    public static final String ARG_SECURITY_POLICY = "ARG_SECURITY_POLICY";
    public static final String ARG_SECURITY_SCOPE = "ARG_SECURITY_SCOPE";
    public static final String ARG_USER_PTR = "ARG_USER_PTR";
    public static final String EXTRA_CID = "EXTRA_CID";
    private static final String KEY_STATE = "KEY_STATE";
    public static final int RESULT_PROVIDER_ERROR = 2;
    /* access modifiers changed from: private */
    public static final String TAG = AuthFlowActivity.class.getSimpleName();
    private static StaticCallbacks staticCallbacks;
    private final Handler handler = new Handler();
    /* access modifiers changed from: private */
    public State state;
    /* access modifiers changed from: private */
    public boolean stateSaved;
    /* access modifiers changed from: private */
    public Interop.AuthFlowScreenStatus status = Interop.AuthFlowScreenStatus.NO_ERROR;

    public interface StaticCallbacks {
        void onAuthFlowFinished(long j, Interop.AuthFlowScreenStatus authFlowScreenStatus, String str);
    }

    private enum Task {
        START_SIGN_IN,
        MSA,
        XB_LOGIN,
        ACCOUNT_PROVISIONING,
        SIGN_UP,
        EVENT_INITIALIZATION,
        INTRODUCING,
        WELCOME,
        FINISH_SIGN_IN,
        XB_LOGOUT
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.xbid_activity_auth_flow);
        if (savedInstanceState == null) {
            this.state = new State();
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                Log.e(TAG, "Intent has no extras");
                this.status = Interop.AuthFlowScreenStatus.ERROR_USER_CANCEL;
                finishWithResult();
                return;
            }
            Bundle args = new Bundle(extras);
            if (!args.containsKey("ARG_USER_PTR")) {
                Log.e(TAG, "No user pointer, non-native activity mode");
                this.state.nativeActivity = false;
                CacheUtil.clearCaches();
                showBodyFragment(Task.START_SIGN_IN, new StartSignInFragment(), args, false);
                return;
            }
            Log.e(TAG, "User pointer present, native activity mode");
            this.state.nativeActivity = true;
            this.state.userImpl = new XsapiUser.UserImpl(args.getLong("ARG_USER_PTR"));
            showBodyFragment(Task.MSA, new MSAFragment(), args, false);
            return;
        }
        this.state = (State) savedInstanceState.getParcelable(KEY_STATE);
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        UTCPageView.removePage();
        super.onDestroy();
        if (isFinishing() && this.state.nativeActivity && staticCallbacks != null) {
            staticCallbacks.onAuthFlowFinished(this.state.userImpl.getUserImplPtr(), this.status, this.state.cid);
        }
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        this.stateSaved = false;
    }

    /* access modifiers changed from: protected */
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_STATE, this.state);
        this.stateSaved = true;
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (this.state.currentTask == Task.MSA) {
            getFragmentManager().findFragmentById(R.id.xbid_body_fragment).onActivityResult(requestCode, resultCode, data);
        }
    }

    public void onBackPressed() {
        Log.d(TAG, "onBackPressed");
        UTCUser.trackCancel(getTitle());
        this.status = Interop.AuthFlowScreenStatus.ERROR_USER_CANCEL;
        finishWithResult();
    }

    public void onClickCloseHeader() {
        Log.d(TAG, "onClickCloseHeader");
        switch (this.state.currentTask) {
            case SIGN_UP:
            case INTRODUCING:
            case WELCOME:
                UTCUser.trackCancel(getTitle());
                this.status = Interop.AuthFlowScreenStatus.NO_ERROR;
                finishWithResult();
                return;
            case XB_LOGOUT:
            case FINISH_SIGN_IN:
                return;
            default:
                UTCUser.trackCancel(getTitle());
                this.status = Interop.AuthFlowScreenStatus.ERROR_USER_CANCEL;
                finishWithResult();
                return;
        }
    }

    public void onComplete(StartSignInFragment.Status status2) {
        Log.d(TAG, "onComplete: StartSignInFragment.Status." + status2);
        switch (status2) {
            case SUCCESS:
                this.handler.post(new Runnable() {
                    public void run() {
                        Log.d(AuthFlowActivity.TAG, "stateSaved: " + AuthFlowActivity.this.stateSaved);
                        if (!AuthFlowActivity.this.stateSaved) {
                            AuthFlowActivity.this.state.userImpl = XsapiUser.getInstance().getUserImpl();
                            Bundle args = new Bundle(AuthFlowActivity.this.getIntent().getExtras());
                            args.putLong("ARG_USER_PTR", AuthFlowActivity.this.state.userImpl.getUserImplPtr());
                            AuthFlowActivity.this.showBodyFragment(Task.MSA, new MSAFragment(), args, false);
                        }
                    }
                });
                return;
            case ERROR:
                this.status = Interop.AuthFlowScreenStatus.ERROR_USER_CANCEL;
                finishWithResult();
                return;
            case PROVIDER_ERROR:
                this.status = Interop.AuthFlowScreenStatus.PROVIDER_ERROR;
                finishWithResult();
                return;
            default:
                return;
        }
    }

    public void onComplete(MSAFragment.Status status2, String cid, Ticket ticket) {
        Log.d(TAG, "onComplete: MSAFragment.Status." + status2);
        switch (status2) {
            case SUCCESS:
                this.state.cid = cid;
                this.state.ticket = ticket.getValue();
                Bundle args = new Bundle();
                args.putString("ARG_RPS_TICKET", this.state.ticket);
                args.putLong("ARG_USER_PTR", this.state.userImpl.getUserImplPtr());
                UTCSignin.trackXBLSigninStart(cid, getTitle());
                showBodyFragment(Task.XB_LOGIN, new XBLoginFragment(), args, false);
                return;
            case ERROR:
                this.status = Interop.AuthFlowScreenStatus.ERROR_USER_CANCEL;
                finishWithResult();
                return;
            case PROVIDER_ERROR:
                this.status = Interop.AuthFlowScreenStatus.PROVIDER_ERROR;
                finishWithResult();
                return;
            default:
                return;
        }
    }

    public void onComplete(XBLoginFragment.Status status2, AuthFlowResult authFlowResult, final boolean createAccount) {
        Log.d(TAG, "onComplete: XBLoginFragment.Status." + status2);
        switch (status2) {
            case SUCCESS:
                this.state.createAccount = createAccount;
                this.handler.post(new Runnable() {
                    public void run() {
                        Log.d(AuthFlowActivity.TAG, "stateSaved: " + AuthFlowActivity.this.stateSaved);
                        if (!AuthFlowActivity.this.stateSaved) {
                            Bundle args = new Bundle();
                            args.putLong("ARG_USER_PTR", AuthFlowActivity.this.state.userImpl.getUserImplPtr());
                            if (createAccount) {
                                AuthFlowActivity.this.showBodyFragment(Task.ACCOUNT_PROVISIONING, new AccountProvisioningFragment(), args, false);
                                return;
                            }
                            args.putString("ARG_RPS_TICKET", AuthFlowActivity.this.state.ticket);
                            AuthFlowActivity.this.showBodyFragment(Task.EVENT_INITIALIZATION, new EventInitializationFragment(), args, false);
                        }
                    }
                });
                return;
            case ERROR:
                this.status = Interop.AuthFlowScreenStatus.ERROR_USER_CANCEL;
                finishWithResult();
                return;
            case PROVIDER_ERROR:
                this.status = Interop.AuthFlowScreenStatus.PROVIDER_ERROR;
                finishWithResult();
                return;
            default:
                return;
        }
    }

    public void onCloseWithStatus(AccountProvisioningFragment.Status status2, final AccountProvisioningResult result) {
        Log.d(TAG, "onComplete: AccountProvisioningFragment.Status." + status2);
        switch (status2) {
            case NO_ERROR:
                this.handler.post(new Runnable() {
                    public void run() {
                        Log.d(AuthFlowActivity.TAG, "stateSaved: " + AuthFlowActivity.this.stateSaved);
                        if (!AuthFlowActivity.this.stateSaved) {
                            AuthFlowActivity.this.state.accountProvisioningResult = result;
                            Bundle args = new Bundle();
                            args.putLong("ARG_USER_PTR", AuthFlowActivity.this.state.userImpl.getUserImplPtr());
                            args.putString("ARG_RPS_TICKET", AuthFlowActivity.this.state.ticket);
                            AuthFlowActivity.this.showBodyFragment(Task.EVENT_INITIALIZATION, new EventInitializationFragment(), args, false);
                        }
                    }
                });
                return;
            case ERROR_USER_CANCEL:
                this.status = Interop.AuthFlowScreenStatus.ERROR_USER_CANCEL;
                finishWithResult();
                return;
            case PROVIDER_ERROR:
                this.status = Interop.AuthFlowScreenStatus.PROVIDER_ERROR;
                finishWithResult();
                return;
            default:
                return;
        }
    }

    public void onComplete(EventInitializationFragment.Status status2) {
        switch (status2) {
            case SUCCESS:
                final CharSequence title = getTitle();
                this.handler.post(new Runnable() {
                    public void run() {
                        Log.d(AuthFlowActivity.TAG, "stateSaved: " + AuthFlowActivity.this.stateSaved);
                        if (!(AuthFlowActivity.this.state == null || AuthFlowActivity.this.state.accountProvisioningResult == null)) {
                            UTCCommonDataModel.setUserId(AuthFlowActivity.this.state.accountProvisioningResult.getXuid());
                        }
                        UTCSignin.trackXBLSigninSuccess(AuthFlowActivity.this.state.cid, title, AuthFlowActivity.this.state.createAccount);
                        if (!AuthFlowActivity.this.stateSaved) {
                            Bundle args = new Bundle();
                            args.putLong("ARG_USER_PTR", AuthFlowActivity.this.state.userImpl.getUserImplPtr());
                            if (AuthFlowActivity.this.state.createAccount) {
                                args.putParcelable(SignUpFragment.ARG_ACCOUNT_PROVISIONING_RESULT, AuthFlowActivity.this.state.accountProvisioningResult);
                                AuthFlowActivity.this.showBodyFragment(Task.SIGN_UP, new SignUpFragment(), args, true);
                                return;
                            }
                            Bundle intentArgs = AuthFlowActivity.this.getIntent().getExtras();
                            if (intentArgs != null && intentArgs.containsKey("ARG_ALT_BUTTON_TEXT")) {
                                args.putString("ARG_ALT_BUTTON_TEXT", intentArgs.getString("ARG_ALT_BUTTON_TEXT"));
                            }
                            AuthFlowActivity.this.showBodyFragment(Task.WELCOME, new WelcomeFragment(), args, true);
                        }
                    }
                });
                return;
            case ERROR:
                this.status = Interop.AuthFlowScreenStatus.ERROR_USER_CANCEL;
                finishWithResult();
                return;
            case PROVIDER_ERROR:
                this.status = Interop.AuthFlowScreenStatus.PROVIDER_ERROR;
                finishWithResult();
                return;
            default:
                return;
        }
    }

    public void onCloseWithStatus(SignUpFragment.Status status2) {
        Log.d(TAG, "onCloseWithStatus: SignUpFragment.Status." + status2);
        switch (status2) {
            case NO_ERROR:
                this.handler.post(new Runnable() {
                    public void run() {
                        Log.d(AuthFlowActivity.TAG, "stateSaved: " + AuthFlowActivity.this.stateSaved);
                        if (!AuthFlowActivity.this.stateSaved) {
                            Bundle args = new Bundle();
                            args.putLong("ARG_USER_PTR", AuthFlowActivity.this.state.userImpl.getUserImplPtr());
                            Bundle intentArgs = AuthFlowActivity.this.getIntent().getExtras();
                            if (intentArgs != null && intentArgs.containsKey("ARG_ALT_BUTTON_TEXT")) {
                                args.putString("ARG_ALT_BUTTON_TEXT", intentArgs.getString("ARG_ALT_BUTTON_TEXT"));
                            }
                            AuthFlowActivity.this.showBodyFragment(Task.INTRODUCING, new IntroducingFragment(), args, true);
                        }
                    }
                });
                return;
            case ERROR_USER_CANCEL:
                this.status = Interop.AuthFlowScreenStatus.ERROR_USER_CANCEL;
                finishWithResult();
                return;
            case PROVIDER_ERROR:
                this.status = Interop.AuthFlowScreenStatus.PROVIDER_ERROR;
                finishWithResult();
                return;
            case ERROR_SWITCH_USER:
                this.handler.post(new Runnable() {
                    public void run() {
                        Log.d(AuthFlowActivity.TAG, "stateSaved: " + AuthFlowActivity.this.stateSaved);
                        if (!AuthFlowActivity.this.stateSaved) {
                            Bundle args = new Bundle();
                            args.putLong("ARG_USER_PTR", AuthFlowActivity.this.state.userImpl.getUserImplPtr());
                            AuthFlowActivity.this.showBodyFragment(Task.XB_LOGOUT, new XBLogoutFragment(), args, true);
                        }
                    }
                });
                return;
            default:
                return;
        }
    }

    public void onCloseWithStatus(IntroducingFragment.Status status2) {
        Log.d(TAG, "onCloseWithStatus: IntroducingFragment.Status." + status2);
        switch (status2) {
            case NO_ERROR:
                this.status = Interop.AuthFlowScreenStatus.NO_ERROR;
                finishWithResult();
                return;
            case ERROR_USER_CANCEL:
                this.status = Interop.AuthFlowScreenStatus.ERROR_USER_CANCEL;
                finishWithResult();
                return;
            case PROVIDER_ERROR:
                this.status = Interop.AuthFlowScreenStatus.PROVIDER_ERROR;
                finishWithResult();
                return;
            default:
                return;
        }
    }

    public void onCloseWithStatus(WelcomeFragment.Status status2) {
        Log.d(TAG, "onCloseWithStatus: WelcomeFragment.Status." + status2);
        switch (status2) {
            case NO_ERROR:
                this.status = Interop.AuthFlowScreenStatus.NO_ERROR;
                finishWithResult();
                return;
            case ERROR_USER_CANCEL:
                this.status = Interop.AuthFlowScreenStatus.ERROR_USER_CANCEL;
                finishWithResult();
                return;
            case PROVIDER_ERROR:
                this.status = Interop.AuthFlowScreenStatus.PROVIDER_ERROR;
                finishWithResult();
                return;
            case ERROR_SWITCH_USER:
                this.handler.post(new Runnable() {
                    public void run() {
                        Log.d(AuthFlowActivity.TAG, "stateSaved: " + AuthFlowActivity.this.stateSaved);
                        if (!AuthFlowActivity.this.stateSaved) {
                            Bundle args = new Bundle();
                            args.putLong("ARG_USER_PTR", AuthFlowActivity.this.state.userImpl.getUserImplPtr());
                            AuthFlowActivity.this.showBodyFragment(Task.XB_LOGOUT, new XBLogoutFragment(), args, true);
                        }
                    }
                });
                return;
            default:
                return;
        }
    }

    public void onComplete(FinishSignInFragment.Status status2) {
        Log.d(TAG, "onComplete: FinishSignInFragment.Status." + status2);
        this.status = this.state.lastStatus;
        finishWithResult();
    }

    public void onComplete(XBLogoutFragment.Status status2) {
        Log.d(TAG, "onComplete: XBLogoutFragment.Status." + status2);
        switch (status2) {
            case SUCCESS:
                this.handler.post(new Runnable() {
                    public void run() {
                        Log.d(AuthFlowActivity.TAG, "stateSaved: " + AuthFlowActivity.this.stateSaved);
                        if (!AuthFlowActivity.this.stateSaved) {
                            CacheUtil.clearCaches();
                            AuthFlowActivity.this.state.createAccount = false;
                            AuthFlowActivity.this.showBodyFragment(Task.MSA, new MSAFragment(), AuthFlowActivity.this.getIntent().getExtras(), false);
                        }
                    }
                });
                return;
            case ERROR:
                Log.e(TAG, "Should not be here! Cancelling auth flow.");
                this.status = Interop.AuthFlowScreenStatus.ERROR_USER_CANCEL;
                finishWithResult();
                return;
            default:
                return;
        }
    }

    public static void setStaticCallbacks(StaticCallbacks staticCallbacks2) {
        staticCallbacks = staticCallbacks2;
    }

    private void finishWithResult() {
        if (this.state.nativeActivity || this.state.currentTask == Task.FINISH_SIGN_IN) {
            Intent intent = new Intent();
            intent.putExtra(EXTRA_CID, this.state.cid);
            setResult(toActivityResult(this.status), intent);
            finishCompat();
            return;
        }
        this.state.lastStatus = this.status;
        this.handler.post(new Runnable() {
            public void run() {
                Log.d(AuthFlowActivity.TAG, "stateSaved: " + AuthFlowActivity.this.stateSaved);
                if (!AuthFlowActivity.this.stateSaved) {
                    Bundle args = new Bundle();
                    args.putString(FinishSignInFragment.ARG_AUTH_STATUS, AuthFlowActivity.this.status.toString());
                    args.putString(FinishSignInFragment.ARG_CID, AuthFlowActivity.this.state.cid);
                    AuthFlowActivity.this.showBodyFragment(Task.FINISH_SIGN_IN, new FinishSignInFragment(), args, true);
                }
            }
        });
    }

    /* access modifiers changed from: private */
    public void showBodyFragment(Task task, Fragment bodyFragment, Bundle args, boolean showHeader) {
        this.state.currentTask = task;
        showBodyFragment(bodyFragment, args, showHeader);
    }

    private static class State implements Parcelable {
        public static final Parcelable.Creator<State> CREATOR = new Parcelable.Creator<State>() {
            public State createFromParcel(Parcel in) {
                return new State(in);
            }

            public State[] newArray(int size) {
                return new State[size];
            }
        };
        public AccountProvisioningResult accountProvisioningResult;
        public String cid;
        public boolean createAccount;
        public Task currentTask;
        public Interop.AuthFlowScreenStatus lastStatus;
        public boolean nativeActivity;
        public String ticket;
        public XsapiUser.UserImpl userImpl;

        public State() {
        }

        protected State(Parcel in) {
            boolean z;
            boolean z2 = true;
            this.userImpl = (XsapiUser.UserImpl) in.readParcelable(XsapiUser.UserImpl.class.getClassLoader());
            int taskId = in.readInt();
            if (taskId != -1) {
                this.currentTask = Task.values()[taskId];
            }
            this.cid = in.readString();
            this.ticket = in.readString();
            if (in.readInt() != 0) {
                z = true;
            } else {
                z = false;
            }
            this.createAccount = z;
            this.nativeActivity = in.readInt() == 0 ? false : z2;
            int lastStatusId = in.readInt();
            if (lastStatusId != -1) {
                this.lastStatus = Interop.AuthFlowScreenStatus.values()[lastStatusId];
            }
            this.accountProvisioningResult = (AccountProvisioningResult) in.readParcelable(AccountProvisioningResult.class.getClassLoader());
        }

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel dest, int flags) {
            int i;
            int i2 = 1;
            int i3 = -1;
            dest.writeParcelable(this.userImpl, flags);
            dest.writeInt(this.currentTask == null ? -1 : this.currentTask.ordinal());
            dest.writeString(this.cid);
            dest.writeString(this.ticket);
            if (this.createAccount) {
                i = 1;
            } else {
                i = 0;
            }
            dest.writeInt(i);
            if (!this.nativeActivity) {
                i2 = 0;
            }
            dest.writeInt(i2);
            if (this.lastStatus != null) {
                i3 = this.lastStatus.ordinal();
            }
            dest.writeInt(i3);
            dest.writeParcelable(this.accountProvisioningResult, flags);
        }
    }
}
