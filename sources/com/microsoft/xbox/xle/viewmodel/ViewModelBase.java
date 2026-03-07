package com.microsoft.xbox.xle.viewmodel;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import com.microsoft.xbox.service.model.UpdateData;
import com.microsoft.xbox.service.model.UpdateType;
import com.microsoft.xbox.toolkit.AsyncResult;
import com.microsoft.xbox.toolkit.DialogManager;
import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.XLEObserver;
import com.microsoft.xbox.toolkit.anim.XLEAnimation;
import com.microsoft.xbox.toolkit.anim.XLEAnimationPackage;
import com.microsoft.xbox.toolkit.ui.ActivityParameters;
import com.microsoft.xbox.toolkit.ui.NavigationManager;
import com.microsoft.xbox.toolkit.ui.ScreenLayout;
import com.microsoft.xbox.xle.app.XLEUtil;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;

public abstract class ViewModelBase implements XLEObserver<UpdateData> {
    protected static int LAUNCH_TIME_OUT = 5000;
    public static final String TAG_PAGE_LOADING_TIME = "performance_measure_page_loadingtime";
    protected int LifetimeInMinutes;
    protected AdapterBase adapter;
    protected boolean isActive;
    protected boolean isForeground;
    protected boolean isLaunching;
    protected Runnable launchTimeoutHandler;
    protected int listIndex;
    private NavigationData nextScreenData;
    protected int offset;
    private boolean onlyProcessExceptionsAndShowToastsWhenActive;
    private ViewModelBase parent;
    private final ScreenLayout screen;
    private boolean shouldHideScreen;
    private boolean showNoNetworkPopup;
    private HashMap<UpdateType, XLEException> updateExceptions;
    private EnumSet<UpdateType> updateTypesToCheck;
    private boolean updating;

    private enum NavigationType {
        Push,
        PopReplace,
        PopAll
    }

    public abstract boolean isBusy();

    public abstract void load(boolean z);

    public abstract void onRehydrate();

    /* access modifiers changed from: protected */
    public abstract void onStartOverride();

    /* access modifiers changed from: protected */
    public abstract void onStopOverride();

    public View findViewById(int id) {
        if (this.screen != null) {
            return this.screen.xleFindViewId(id);
        }
        return null;
    }

    private class NavigationData {
        private NavigationType navigationType;
        private Class<? extends ScreenLayout> screenClass;

        protected NavigationData(Class<? extends ScreenLayout> screen, NavigationType type) {
            this.screenClass = screen;
            this.navigationType = type;
        }

        /* access modifiers changed from: protected */
        public Class<? extends ScreenLayout> getScreenClass() {
            return this.screenClass;
        }

        /* access modifiers changed from: protected */
        public NavigationType getNavigationType() {
            return this.navigationType;
        }
    }

    public ViewModelBase(ScreenLayout screen2) {
        this(screen2, true, false);
    }

    public ViewModelBase() {
        this((ScreenLayout) null, true, false);
    }

    public ViewModelBase(boolean showNoNetworkPopup2, boolean onlyProcessExceptionsAndShowToastsWhenActive2) {
        this((ScreenLayout) null, showNoNetworkPopup2, onlyProcessExceptionsAndShowToastsWhenActive2);
    }

    public ViewModelBase(ScreenLayout screen2, boolean showNoNetworkPopup2, boolean onlyProcessExceptionsAndShowToastsWhenActive2) {
        this.LifetimeInMinutes = 60;
        this.updateExceptions = new HashMap<>();
        this.showNoNetworkPopup = true;
        this.onlyProcessExceptionsAndShowToastsWhenActive = false;
        this.nextScreenData = null;
        this.updating = false;
        this.isLaunching = false;
        this.screen = screen2;
        this.showNoNetworkPopup = showNoNetworkPopup2;
        this.onlyProcessExceptionsAndShowToastsWhenActive = onlyProcessExceptionsAndShowToastsWhenActive2;
    }

    public ScreenLayout getScreen() {
        return this.screen;
    }

    /* access modifiers changed from: protected */
    public ViewModelBase getParent() {
        return this.parent;
    }

    /* access modifiers changed from: protected */
    public void setParent(ViewModelBase parent2) {
        this.parent = parent2;
    }

    public AdapterBase getAdapter() {
        return this.adapter;
    }

    /* access modifiers changed from: protected */
    public void onChildViewModelChanged(ViewModelBase child) {
    }

    /* access modifiers changed from: protected */
    public void updateAdapter() {
        updateAdapter(true);
    }

    /* access modifiers changed from: protected */
    public void updateAdapter(boolean notifyParent) {
        if (this.adapter != null) {
            this.adapter.updateView();
        }
        if (this.parent != null && notifyParent) {
            this.parent.onChildViewModelChanged(this);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

    public void onStart() {
        this.isForeground = true;
        onStartOverride();
        if (this.adapter != null) {
            this.adapter.onStart();
        }
    }

    public boolean getShouldHideScreen() {
        return this.shouldHideScreen;
    }

    public void setShouldHideScreen(boolean shouldHide) {
        this.shouldHideScreen = shouldHide;
    }

    public void setListPosition(int index, int offset2) {
        this.listIndex = index;
        this.offset = offset2;
    }

    public int getAndResetListPosition() {
        int value = this.listIndex;
        this.listIndex = 0;
        return value;
    }

    public int getAndResetListOffset() {
        int offset2 = this.offset;
        this.offset = 0;
        return offset2;
    }

    public void onStop() {
        this.isForeground = false;
        if (this.adapter != null) {
            this.adapter.onStop();
        }
        DialogManager.getInstance().dismissBlocking();
        if (shouldDismissTopNoFatalAlert()) {
            DialogManager.getInstance().dismissTopNonFatalAlert();
        }
        DialogManager.getInstance().dismissToast();
        onStopOverride();
    }

    /* access modifiers changed from: protected */
    public boolean shouldDismissTopNoFatalAlert() {
        return true;
    }

    public void onPause() {
        cancelLaunchTimeout();
        if (this.adapter != null) {
            this.adapter.onPause();
        }
    }

    public void onApplicationPause() {
        if (this.adapter != null) {
            this.adapter.onApplicationPause();
        }
    }

    public void onApplicationResume() {
        if (this.adapter != null) {
            this.adapter.onApplicationResume();
        }
    }

    public void onResume() {
        if (this.adapter != null) {
            this.adapter.onResume();
            this.adapter.updateView();
        }
    }

    public void onDestroy() {
        if (this.adapter != null) {
            this.adapter.onDestroy();
        }
        this.adapter = null;
    }

    public void onTombstone() {
        if (this.adapter != null) {
            this.adapter.onDestroy();
        }
        this.adapter = null;
    }

    public void forceUpdateViewImmediately() {
        if (this.adapter != null) {
            this.adapter.forceUpdateViewImmediately();
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
    }

    public void onSaveInstanceState(Bundle outState) {
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
    }

    public boolean onBackButtonPressed() {
        return false;
    }

    public boolean isBlockingBusy() {
        return false;
    }

    public String getBlockingStatusText() {
        return null;
    }

    public void load() {
        load(XLEGlobalData.getInstance().CheckDrainShouldRefresh(getClass()));
    }

    public void forceRefresh() {
        load(true);
        if (this.adapter != null) {
            this.adapter.updateView();
        }
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void update(com.microsoft.xbox.toolkit.AsyncResult<com.microsoft.xbox.service.model.UpdateData> r9) {
        /*
            r8 = this;
            r5 = 0
            r4 = 0
            r3 = 1
            r8.updating = r3
            com.microsoft.xbox.xle.viewmodel.ViewModelBase$NavigationData r2 = r8.nextScreenData
            if (r2 != 0) goto L_0x005f
            r2 = r3
        L_0x000a:
            com.microsoft.xbox.toolkit.XLEAssert.assertTrue(r2)
            r8.nextScreenData = r5
            com.microsoft.xbox.toolkit.XLEException r2 = r9.getException()
            if (r2 == 0) goto L_0x0034
            com.microsoft.xbox.toolkit.XLEException r2 = r9.getException()
            long r0 = r2.getErrorCode()
            com.microsoft.xbox.toolkit.XLEException r2 = r9.getException()
            boolean r2 = r2.getIsHandled()
            if (r2 != 0) goto L_0x0034
            r6 = 1005(0x3ed, double:4.965E-321)
            int r2 = (r0 > r6 ? 1 : (r0 == r6 ? 0 : -1))
            if (r2 != 0) goto L_0x0034
            com.microsoft.xbox.toolkit.XLEException r2 = r9.getException()
            r2.setIsHandled(r3)
        L_0x0034:
            com.microsoft.xbox.xle.viewmodel.ViewModelBase$NavigationData r2 = r8.nextScreenData
            if (r2 != 0) goto L_0x0045
            com.microsoft.xbox.xle.viewmodel.AdapterBase r2 = r8.adapter
            if (r2 != 0) goto L_0x0042
            boolean r2 = r8.updateWithoutAdapter()
            if (r2 == 0) goto L_0x0045
        L_0x0042:
            r8.updateOverride(r9)
        L_0x0045:
            r8.updating = r4
            com.microsoft.xbox.xle.viewmodel.ViewModelBase$NavigationData r2 = r8.nextScreenData
            if (r2 == 0) goto L_0x008f
            int[] r2 = com.microsoft.xbox.xle.viewmodel.ViewModelBase.AnonymousClass1.$SwitchMap$com$microsoft$xbox$xle$viewmodel$ViewModelBase$NavigationType     // Catch:{ XLEException -> 0x0070 }
            com.microsoft.xbox.xle.viewmodel.ViewModelBase$NavigationData r3 = r8.nextScreenData     // Catch:{ XLEException -> 0x0070 }
            com.microsoft.xbox.xle.viewmodel.ViewModelBase$NavigationType r3 = r3.getNavigationType()     // Catch:{ XLEException -> 0x0070 }
            int r3 = r3.ordinal()     // Catch:{ XLEException -> 0x0070 }
            r2 = r2[r3]     // Catch:{ XLEException -> 0x0070 }
            switch(r2) {
                case 1: goto L_0x0061;
                case 2: goto L_0x0072;
                case 3: goto L_0x0081;
                default: goto L_0x005c;
            }
        L_0x005c:
            r8.nextScreenData = r5
            return
        L_0x005f:
            r2 = r4
            goto L_0x000a
        L_0x0061:
            com.microsoft.xbox.toolkit.ui.NavigationManager r2 = com.microsoft.xbox.toolkit.ui.NavigationManager.getInstance()     // Catch:{ XLEException -> 0x0070 }
            com.microsoft.xbox.xle.viewmodel.ViewModelBase$NavigationData r3 = r8.nextScreenData     // Catch:{ XLEException -> 0x0070 }
            java.lang.Class r3 = r3.getScreenClass()     // Catch:{ XLEException -> 0x0070 }
            r4 = 1
            r2.NavigateTo(r3, r4)     // Catch:{ XLEException -> 0x0070 }
            goto L_0x005c
        L_0x0070:
            r2 = move-exception
            goto L_0x005c
        L_0x0072:
            com.microsoft.xbox.toolkit.ui.NavigationManager r2 = com.microsoft.xbox.toolkit.ui.NavigationManager.getInstance()     // Catch:{ XLEException -> 0x0070 }
            com.microsoft.xbox.xle.viewmodel.ViewModelBase$NavigationData r3 = r8.nextScreenData     // Catch:{ XLEException -> 0x0070 }
            java.lang.Class r3 = r3.getScreenClass()     // Catch:{ XLEException -> 0x0070 }
            r4 = 0
            r2.NavigateTo(r3, r4)     // Catch:{ XLEException -> 0x0070 }
            goto L_0x005c
        L_0x0081:
            com.microsoft.xbox.toolkit.ui.NavigationManager r2 = com.microsoft.xbox.toolkit.ui.NavigationManager.getInstance()     // Catch:{ XLEException -> 0x0070 }
            com.microsoft.xbox.xle.viewmodel.ViewModelBase$NavigationData r3 = r8.nextScreenData     // Catch:{ XLEException -> 0x0070 }
            java.lang.Class r3 = r3.getScreenClass()     // Catch:{ XLEException -> 0x0070 }
            r2.GotoScreenWithPop(r3)     // Catch:{ XLEException -> 0x0070 }
            goto L_0x005c
        L_0x008f:
            boolean r2 = r8.shouldProcessErrors()
            if (r2 == 0) goto L_0x005c
            com.microsoft.xbox.toolkit.XLEException r2 = r9.getException()
            if (r2 == 0) goto L_0x00ce
            com.microsoft.xbox.toolkit.XLEException r2 = r9.getException()
            boolean r2 = r2.getIsHandled()
            if (r2 != 0) goto L_0x00ce
            java.util.EnumSet<com.microsoft.xbox.service.model.UpdateType> r2 = r8.updateTypesToCheck
            if (r2 == 0) goto L_0x00ce
            java.util.EnumSet<com.microsoft.xbox.service.model.UpdateType> r3 = r8.updateTypesToCheck
            java.lang.Object r2 = r9.getResult()
            com.microsoft.xbox.service.model.UpdateData r2 = (com.microsoft.xbox.service.model.UpdateData) r2
            com.microsoft.xbox.service.model.UpdateType r2 = r2.getUpdateType()
            boolean r2 = r3.contains(r2)
            if (r2 == 0) goto L_0x00ce
            java.util.HashMap<com.microsoft.xbox.service.model.UpdateType, com.microsoft.xbox.toolkit.XLEException> r3 = r8.updateExceptions
            java.lang.Object r2 = r9.getResult()
            com.microsoft.xbox.service.model.UpdateData r2 = (com.microsoft.xbox.service.model.UpdateData) r2
            com.microsoft.xbox.service.model.UpdateType r2 = r2.getUpdateType()
            com.microsoft.xbox.toolkit.XLEException r4 = r9.getException()
            r3.put(r2, r4)
        L_0x00ce:
            java.lang.Object r2 = r9.getResult()
            com.microsoft.xbox.service.model.UpdateData r2 = (com.microsoft.xbox.service.model.UpdateData) r2
            boolean r2 = r2.getIsFinal()
            if (r2 == 0) goto L_0x005c
            java.util.EnumSet<com.microsoft.xbox.service.model.UpdateType> r2 = r8.updateTypesToCheck
            if (r2 == 0) goto L_0x00ed
            java.util.EnumSet<com.microsoft.xbox.service.model.UpdateType> r3 = r8.updateTypesToCheck
            java.lang.Object r2 = r9.getResult()
            com.microsoft.xbox.service.model.UpdateData r2 = (com.microsoft.xbox.service.model.UpdateData) r2
            com.microsoft.xbox.service.model.UpdateType r2 = r2.getUpdateType()
            r3.remove(r2)
        L_0x00ed:
            java.util.EnumSet<com.microsoft.xbox.service.model.UpdateType> r2 = r8.updateTypesToCheck
            if (r2 == 0) goto L_0x00f9
            java.util.EnumSet<com.microsoft.xbox.service.model.UpdateType> r2 = r8.updateTypesToCheck
            boolean r2 = r2.isEmpty()
            if (r2 == 0) goto L_0x005c
        L_0x00f9:
            r8.onUpdateFinished()
            r8.updateTypesToCheck = r5
            goto L_0x005c
        */
        throw new UnsupportedOperationException("Method not decompiled: com.microsoft.xbox.xle.viewmodel.ViewModelBase.update(com.microsoft.xbox.toolkit.AsyncResult):void");
    }

    /* access modifiers changed from: protected */
    public boolean updateWithoutAdapter() {
        return false;
    }

    /* access modifiers changed from: protected */
    public void updateOverride(AsyncResult<UpdateData> asyncResult) {
    }

    /* access modifiers changed from: protected */
    public void logOut(boolean clearEverything) {
    }

    /* access modifiers changed from: protected */
    public void setUpdateTypesToCheck(EnumSet<UpdateType> checkList) {
        this.updateTypesToCheck = checkList;
        this.updateExceptions.clear();
    }

    /* access modifiers changed from: protected */
    public boolean checkErrorCode(UpdateType updateType, long errorCode) {
        if (!this.updateExceptions.containsKey(updateType) || this.updateExceptions.get(updateType).getErrorCode() != errorCode) {
            return false;
        }
        if (this.updateExceptions.get(updateType).getIsHandled()) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: protected */
    public boolean updateTypesToCheckIsEmpty() {
        return this.updateTypesToCheck == null || this.updateTypesToCheck.isEmpty();
    }

    /* access modifiers changed from: protected */
    public boolean updateTypesToCheckHadAnyErrors() {
        return !this.updateExceptions.isEmpty();
    }

    /* access modifiers changed from: protected */
    public void onUpdateFinished() {
        this.updateTypesToCheck = null;
        this.updateExceptions.clear();
    }

    public XLEAnimationPackage getAnimateOut(boolean goingBack) {
        ArrayList<XLEAnimation> animations = this.adapter.getAnimateOut(goingBack);
        if (animations == null || animations.size() <= 0) {
            return null;
        }
        XLEAnimationPackage animationPackage = new XLEAnimationPackage();
        Iterator i$ = animations.iterator();
        while (i$.hasNext()) {
            animationPackage.add(i$.next());
        }
        return animationPackage;
    }

    public XLEAnimationPackage getAnimateIn(boolean goingBack) {
        ArrayList<XLEAnimation> animations = this.adapter.getAnimateIn(goingBack);
        if (animations == null || animations.size() <= 0) {
            return null;
        }
        XLEAnimationPackage animationPackage = new XLEAnimationPackage();
        Iterator i$ = animations.iterator();
        while (i$.hasNext()) {
            animationPackage.add(i$.next());
        }
        return animationPackage;
    }

    public void TEST_induceGoBack() {
    }

    public void onAnimateInCompleted() {
        if (this.adapter != null) {
            this.adapter.onAnimateInCompleted();
        }
    }

    /* access modifiers changed from: protected */
    public void NavigateTo(Class<? extends ScreenLayout> screenClass, ActivityParameters activityParameters) {
        NavigateTo(screenClass, true, activityParameters);
    }

    /* access modifiers changed from: protected */
    public void NavigateTo(Class<? extends ScreenLayout> screenClass) {
        NavigateTo(screenClass, (ActivityParameters) null);
    }

    /* access modifiers changed from: protected */
    public void NavigateTo(Class<? extends ScreenLayout> screenClass, boolean addToStack, ActivityParameters activityParameters) {
        cancelLaunchTimeout();
        XLEAssert.assertFalse("We shouldn't navigate to a new screen if the current screen is blocking", isBlockingBusy());
        if (this.updating) {
            this.nextScreenData = new NavigationData(screenClass, addToStack ? NavigationType.Push : NavigationType.PopReplace);
            return;
        }
        XLEAssert.assertFalse("We shouldn't navigate to a new screen if the current screen is blocking", isBlockingBusy());
        NavigationManager.getInstance().NavigateTo(screenClass, addToStack, activityParameters);
    }

    /* access modifiers changed from: protected */
    public void NavigateTo(Class<? extends ScreenLayout> screenClass, boolean addToStack) {
        NavigateTo(screenClass, addToStack, (ActivityParameters) null);
    }

    /* access modifiers changed from: protected */
    public void showMustActDialog(String title, String promptText, String okText, Runnable okHandler, boolean isFatal) {
    }

    /* access modifiers changed from: protected */
    public void showOkCancelDialog(String promptText, String okText, Runnable okHandler, String cancelText, Runnable cancelHandler) {
        showOkCancelDialog((String) null, promptText, okText, okHandler, cancelText, cancelHandler);
    }

    /* access modifiers changed from: protected */
    public void showOkCancelDialog(String title, String promptText, String okText, Runnable okHandler, String cancelText, Runnable cancelHandler) {
        if (shouldProcessErrors()) {
            XLEUtil.showOkCancelDialog(title, promptText, okText, okHandler, cancelText, cancelHandler);
        }
    }

    /* access modifiers changed from: protected */
    public void showError(int contentResId) {
        DialogManager.getInstance().showToast(contentResId);
    }

    public void onSetActive() {
        this.isActive = true;
        if (this.adapter != null) {
            this.adapter.onSetActive();
        }
    }

    public void onSetInactive() {
        DialogManager.getInstance().dismissToast();
        this.isActive = false;
        if (this.adapter != null) {
            this.adapter.onSetInactive();
        }
    }

    public boolean getIsActive() {
        return this.isActive;
    }

    public boolean getShowNoNetworkPopup() {
        return this.showNoNetworkPopup;
    }

    private boolean shouldProcessErrors() {
        if (this.onlyProcessExceptionsAndShowToastsWhenActive) {
            return this.isActive;
        }
        return true;
    }

    public void setAsPivotPane() {
        this.showNoNetworkPopup = true;
        this.onlyProcessExceptionsAndShowToastsWhenActive = true;
    }

    /* access modifiers changed from: protected */
    public void cancelLaunchTimeout() {
        this.isLaunching = false;
        if (this.launchTimeoutHandler != null) {
            ThreadManager.Handler.removeCallbacks(this.launchTimeoutHandler);
        }
    }

    public void cancelLaunch() {
        this.isLaunching = false;
    }

    /* access modifiers changed from: protected */
    public void adapterUpdateView() {
        if (this.adapter != null) {
            this.adapter.updateView();
        }
    }

    public void setScreenState(int state) {
        if (this.adapter != null) {
            this.adapter.setScreenState(state);
        }
    }

    public void leaveViewModel(Runnable leaveHandler) {
        leaveHandler.run();
    }

    public boolean shouldRefreshAsPivotHeader() {
        return false;
    }
}
