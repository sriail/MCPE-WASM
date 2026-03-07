package com.microsoft.xbox.idp.ui;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.microsoft.xbox.idp.R;
import com.microsoft.xbox.idp.compat.BaseFragment;
import com.microsoft.xbox.idp.toolkit.SignOutLoader;
import com.microsoft.xbox.idp.ui.ErrorActivity;
import com.microsoft.xbox.idp.util.CacheUtil;
import com.microsoft.xbox.idp.util.ErrorHelper;
import com.microsoft.xbox.idp.util.FragmentLoaderKey;
import com.microsoft.xbox.idp.util.ResultLoaderInfo;

public class SignOutFragment extends BaseFragment implements ErrorHelper.ActivityContext {
    static final /* synthetic */ boolean $assertionsDisabled = (!SignOutFragment.class.desiredAssertionStatus());
    private static final String KEY_STATE = "KEY_STATE";
    private static final int LOADER_SIGN_OUT = 1;
    private static final Callbacks NO_OP_CALLBACKS = new Callbacks() {
        public void onComplete(Status status) {
        }
    };
    /* access modifiers changed from: private */
    public static final String TAG = SignOutFragment.class.getSimpleName();
    /* access modifiers changed from: private */
    public Callbacks callbacks = NO_OP_CALLBACKS;
    private final SparseArray<ErrorHelper.LoaderInfo> loaderMap = new SparseArray<>();
    private final LoaderManager.LoaderCallbacks<SignOutLoader.Result> signOutCallbacks = new LoaderManager.LoaderCallbacks<SignOutLoader.Result>() {
        public Loader<SignOutLoader.Result> onCreateLoader(int id, Bundle args) {
            Log.d(SignOutFragment.TAG, "Creating LOADER_SIGN_OUT");
            return new SignOutLoader(SignOutFragment.this.getActivity(), CacheUtil.getResultCache(SignOutLoader.Result.class), args.get(ErrorHelper.KEY_RESULT_KEY));
        }

        public void onLoadFinished(Loader<SignOutLoader.Result> loader, SignOutLoader.Result result) {
            Log.d(SignOutFragment.TAG, "LOADER_SIGN_OUT finished");
            if (result.hasError()) {
                Log.d(SignOutFragment.TAG, "LOADER_SIGN_OUT: " + result.getError());
                SignOutFragment.this.state.errorHelper.startErrorActivity(ErrorActivity.ErrorScreen.CATCHALL);
                return;
            }
            SignOutFragment.this.callbacks.onComplete(Status.SUCCESS);
        }

        public void onLoaderReset(Loader<SignOutLoader.Result> loader) {
            Log.d(SignOutFragment.TAG, "LOADER_SIGN_OUT reset");
        }
    };
    /* access modifiers changed from: private */
    public State state;

    public interface Callbacks {
        void onComplete(Status status);
    }

    public enum Status {
        SUCCESS,
        ERROR,
        PROVIDER_ERROR
    }

    public SignOutFragment() {
        this.loaderMap.put(1, new ResultLoaderInfo(SignOutLoader.Result.class, this.signOutCallbacks));
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if ($assertionsDisabled || (activity instanceof Callbacks)) {
            this.callbacks = (Callbacks) activity;
            return;
        }
        throw new AssertionError();
    }

    public void onDetach() {
        super.onDetach();
        this.callbacks = NO_OP_CALLBACKS;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            CacheUtil.getResultCache(SignOutLoader.Result.class).clear();
            this.state = new State();
        } else {
            this.state = (State) savedInstanceState.getParcelable(KEY_STATE);
        }
        this.state.errorHelper.setActivityContext(this);
    }

    public void onResume() {
        super.onResume();
        Bundle args = getArguments();
        Log.d(TAG, "Initializing LOADER_SIGN_OUT");
        Bundle bundle = new Bundle(args);
        bundle.putParcelable(ErrorHelper.KEY_RESULT_KEY, new FragmentLoaderKey(SignOutFragment.class, 1));
        if (this.state.errorHelper != null) {
            this.state.errorHelper.initLoader(1, bundle);
        }
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_STATE, this.state);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ErrorHelper.ActivityResult result = this.state.errorHelper.getActivityResult(requestCode, resultCode, data);
        if (result == null) {
            return;
        }
        if (result.isTryAgain()) {
            Log.d(TAG, "Trying again");
            this.state.errorHelper.deleteLoader();
            return;
        }
        this.state.errorHelper = null;
        this.callbacks.onComplete(Status.PROVIDER_ERROR);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.xbid_fragment_busy, container, false);
    }

    public ErrorHelper.LoaderInfo getLoaderInfo(int loaderId) {
        return this.loaderMap.get(loaderId);
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
        public ErrorHelper errorHelper;

        public State() {
            this.errorHelper = new ErrorHelper();
        }

        protected State(Parcel in) {
            this.errorHelper = (ErrorHelper) in.readParcelable(ErrorHelper.class.getClassLoader());
        }

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel dest, int flags) {
            dest.writeParcelable(this.errorHelper, flags);
        }
    }
}
