package com.microsoft.xbox.xle.viewmodel;

import android.view.View;
import com.microsoft.xbox.toolkit.DialogManager;
import com.microsoft.xbox.toolkit.XLEAllocationTracker;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.anim.XLEAnimation;
import com.microsoft.xbox.toolkit.ui.NavigationManager;
import com.microsoft.xbox.xle.app.XLEUtil;
import com.microsoft.xbox.xle.app.module.ScreenModuleLayout;
import com.microsoft.xboxtcui.XboxTcuiSdk;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public abstract class AdapterBase {
    public static String ALLOCATION_TAG = "ADAPTERBASE";
    private static HashMap<String, Integer> adapterCounter = new HashMap<>();
    protected boolean isActive;
    private boolean isStarted;
    private ArrayList<ScreenModuleLayout> screenModules;
    private final ViewModelBase viewModel;

    /* access modifiers changed from: protected */
    public abstract void updateViewOverride();

    /* access modifiers changed from: protected */
    public boolean getIsStarted() {
        return this.isStarted;
    }

    public AdapterBase() {
        this((ViewModelBase) null);
    }

    public AdapterBase(ViewModelBase viewModel2) {
        this.isActive = false;
        this.isStarted = false;
        this.screenModules = new ArrayList<>();
        this.viewModel = viewModel2;
        XLEAllocationTracker.getInstance().debugIncrement(ALLOCATION_TAG, getClass().getSimpleName());
        XLEAllocationTracker.getInstance().debugPrintOverallocated(ALLOCATION_TAG);
    }

    public void finalize() {
        XLEAllocationTracker.getInstance().debugDecrement(ALLOCATION_TAG, getClass().getSimpleName());
        XLEAllocationTracker.getInstance().debugPrintOverallocated(ALLOCATION_TAG);
    }

    public void updateView() {
        if (!NavigationManager.getInstance().isAnimating()) {
            updateViewOverride();
            Iterator i$ = this.screenModules.iterator();
            while (i$.hasNext()) {
                i$.next().updateView();
            }
        }
    }

    public void invalidateView() {
        if (!NavigationManager.getInstance().isAnimating()) {
            invalidateViewOverride();
            Iterator i$ = this.screenModules.iterator();
            while (i$.hasNext()) {
                i$.next().invalidateView();
            }
        }
    }

    /* access modifiers changed from: protected */
    public void invalidateViewOverride() {
    }

    public void forceUpdateViewImmediately() {
        XLEAssert.assertIsUIThread();
        updateViewOverride();
        Iterator i$ = this.screenModules.iterator();
        while (i$.hasNext()) {
            i$.next().updateView();
        }
    }

    public ArrayList<XLEAnimation> getAnimateIn(boolean goingBack) {
        return null;
    }

    public ArrayList<XLEAnimation> getAnimateOut(boolean goingBack) {
        return null;
    }

    public void onPause() {
        Iterator i$ = this.screenModules.iterator();
        while (i$.hasNext()) {
            i$.next().onPause();
        }
    }

    public void onApplicationPause() {
        Iterator i$ = this.screenModules.iterator();
        while (i$.hasNext()) {
            i$.next().onApplicationPause();
        }
    }

    public void onApplicationResume() {
        Iterator i$ = this.screenModules.iterator();
        while (i$.hasNext()) {
            i$.next().onApplicationResume();
        }
    }

    public void onResume() {
        Iterator i$ = this.screenModules.iterator();
        while (i$.hasNext()) {
            i$.next().onResume();
        }
    }

    public void onDestroy() {
        Iterator i$ = this.screenModules.iterator();
        while (i$.hasNext()) {
            i$.next().onDestroy();
        }
        this.screenModules.clear();
    }

    public void onStart() {
        this.isStarted = true;
        Iterator i$ = this.screenModules.iterator();
        while (i$.hasNext()) {
            i$.next().onStart();
        }
    }

    public void onStop() {
        this.isStarted = false;
        Iterator i$ = this.screenModules.iterator();
        while (i$.hasNext()) {
            i$.next().onStop();
        }
    }

    /* access modifiers changed from: protected */
    @Deprecated
    public void onAppBarUpdated() {
    }

    /* access modifiers changed from: protected */
    public void onAppBarButtonsAdded() {
    }

    /* access modifiers changed from: protected */
    public void showKeyboard(View view, int delayMS) {
        XLEUtil.showKeyboard(view, delayMS);
    }

    public void onSetActive() {
        this.isActive = true;
        if (XboxTcuiSdk.getActivity() != null && this.isStarted) {
            updateView();
        }
    }

    public void onSetInactive() {
        this.isActive = false;
    }

    public void onAnimateInCompleted() {
    }

    public View findViewById(int id) {
        View view = null;
        if (this.viewModel != null) {
            view = this.viewModel.findViewById(id);
        }
        return view != null ? view : XboxTcuiSdk.getActivity().findViewById(id);
    }

    /* access modifiers changed from: protected */
    public void findAndInitializeModuleById(int id, ViewModelBase vm) {
        View view = findViewById(id);
        if (view != null && (view instanceof ScreenModuleLayout)) {
            ScreenModuleLayout module = (ScreenModuleLayout) findViewById(id);
            module.setViewModel(vm);
            this.screenModules.add(module);
        }
    }

    /* access modifiers changed from: protected */
    public void setBlocking(boolean visible, String blockingText) {
        DialogManager.getInstance().setBlocking(visible, blockingText);
    }

    /* access modifiers changed from: protected */
    public void setCancelableBlocking(boolean visible, String blockingText, Runnable cancelRunnable) {
        DialogManager.getInstance().setCancelableBlocking(visible, blockingText, cancelRunnable);
    }

    public void setScreenState(int state) {
    }
}
