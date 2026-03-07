package com.microsoft.xbox.toolkit;

import com.microsoft.xbox.toolkit.network.XLEThreadPool;

public abstract class NetworkAsyncTask<T> extends XLEAsyncTask<T> {
    protected boolean forceLoad = true;
    private boolean shouldExecute = true;

    /* access modifiers changed from: protected */
    public abstract boolean checkShouldExecute();

    /* access modifiers changed from: protected */
    public abstract T loadDataInBackground();

    /* access modifiers changed from: protected */
    public abstract T onError();

    /* access modifiers changed from: protected */
    public abstract void onNoAction();

    public NetworkAsyncTask() {
        super(XLEThreadPool.networkOperationsThreadPool);
    }

    public NetworkAsyncTask(XLEThreadPool threadPool) {
        super(XLEThreadPool.networkOperationsThreadPool);
    }

    public void load(boolean forceLoad2) {
        this.forceLoad = forceLoad2;
        execute();
    }

    /* access modifiers changed from: protected */
    public final T doInBackground() {
        try {
            return loadDataInBackground();
        } catch (Exception e) {
            return onError();
        }
    }

    public void execute() {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        if (this.cancelled) {
        }
        this.shouldExecute = checkShouldExecute();
        if (this.shouldExecute || this.forceLoad) {
            this.isBusy = true;
            onPreExecute();
            super.executeBackground();
            return;
        }
        onNoAction();
        this.isBusy = false;
    }
}
