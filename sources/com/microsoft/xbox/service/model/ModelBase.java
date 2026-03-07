package com.microsoft.xbox.service.model;

import com.microsoft.xbox.toolkit.AsyncActionStatus;
import com.microsoft.xbox.toolkit.AsyncResult;
import com.microsoft.xbox.toolkit.DataLoadUtil;
import com.microsoft.xbox.toolkit.ModelData;
import com.microsoft.xbox.toolkit.SingleEntryLoadingStatus;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.XLEObservable;
import com.microsoft.xbox.toolkit.network.IDataLoaderRunnable;
import com.microsoft.xbox.xle.app.XLEUtil;
import java.util.Date;

public abstract class ModelBase<T> extends XLEObservable<UpdateData> implements ModelData<T> {
    protected static final long MilliSecondsInADay = 86400000;
    protected static final long MilliSecondsInAnHour = 3600000;
    protected static final long MilliSecondsInHalfHour = 1800000;
    /* access modifiers changed from: protected */
    public boolean isLoading = false;
    protected long lastInvalidatedTick = 0;
    protected Date lastRefreshTime;
    protected long lifetime = MilliSecondsInADay;
    protected IDataLoaderRunnable<T> loaderRunnable;
    private SingleEntryLoadingStatus loadingStatus = new SingleEntryLoadingStatus();

    public boolean shouldRefresh() {
        return shouldRefresh(this.lastRefreshTime);
    }

    public boolean hasValidData() {
        return this.lastRefreshTime != null;
    }

    /* access modifiers changed from: protected */
    public boolean shouldRefresh(Date lastRefreshTime2) {
        return XLEUtil.shouldRefresh(lastRefreshTime2, this.lifetime);
    }

    /* access modifiers changed from: protected */
    public boolean isLoaded() {
        return this.lastRefreshTime != null;
    }

    public void updateWithNewData(AsyncResult<T> result) {
        this.isLoading = false;
        if (result.getException() == null && result.getStatus() == AsyncActionStatus.SUCCESS) {
            this.lastRefreshTime = new Date();
        }
    }

    public boolean getIsLoading() {
        return this.loadingStatus.getIsLoading();
    }

    public void invalidateData() {
        this.lastRefreshTime = null;
    }

    /* access modifiers changed from: protected */
    public AsyncResult<T> loadData(boolean forceRefresh, IDataLoaderRunnable<T> runnable) {
        XLEAssert.assertIsNotUIThread();
        return DataLoadUtil.Load(forceRefresh, this.lifetime, this.lastRefreshTime, this.loadingStatus, runnable);
    }

    /* access modifiers changed from: protected */
    public void loadInternal(boolean forceRefresh, UpdateType updateType, IDataLoaderRunnable<T> runnable) {
        loadInternal(forceRefresh, updateType, runnable, this.lastRefreshTime);
    }

    /* access modifiers changed from: protected */
    public void loadInternal(boolean forceRefresh, UpdateType updateType, IDataLoaderRunnable<T> runnable, Date lastRefreshTime2) {
        boolean z = false;
        XLEAssert.assertIsUIThread();
        if (getIsLoading() || (!forceRefresh && !shouldRefresh(lastRefreshTime2))) {
            if (!getIsLoading()) {
                z = true;
            }
            notifyObservers(new AsyncResult(new UpdateData(updateType, z), this, (XLEException) null));
            return;
        }
        DataLoadUtil.StartLoadFromUI(forceRefresh, this.lifetime, this.lastRefreshTime, this.loadingStatus, runnable);
        notifyObservers(new AsyncResult(new UpdateData(updateType, false), this, (XLEException) null));
    }
}
