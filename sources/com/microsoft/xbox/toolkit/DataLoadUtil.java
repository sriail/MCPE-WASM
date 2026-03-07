package com.microsoft.xbox.toolkit;

import com.microsoft.xbox.toolkit.SingleEntryLoadingStatus;
import com.microsoft.xbox.toolkit.network.IDataLoaderRunnable;
import com.microsoft.xbox.xle.app.XLEUtil;
import java.util.Date;

public class DataLoadUtil {
    public static <T> NetworkAsyncTask StartLoadFromUI(boolean forceLoad, long lifetime, Date lastRefreshedTime, SingleEntryLoadingStatus loadingStatus, IDataLoaderRunnable<T> runner) {
        final long j = lifetime;
        final Date date = lastRefreshedTime;
        final SingleEntryLoadingStatus singleEntryLoadingStatus = loadingStatus;
        final IDataLoaderRunnable<T> iDataLoaderRunnable = runner;
        NetworkAsyncTask<T> task = new NetworkAsyncTask<T>() {
            /* access modifiers changed from: protected */
            public boolean checkShouldExecute() {
                return this.forceLoad;
            }

            /* access modifiers changed from: protected */
            public void onNoAction() {
            }

            /* access modifiers changed from: protected */
            public void onPreExecute() {
            }

            /* access modifiers changed from: protected */
            public void onPostExecute(T t) {
            }

            /* access modifiers changed from: protected */
            public T onError() {
                return null;
            }

            /* access modifiers changed from: protected */
            public T loadDataInBackground() {
                return DataLoadUtil.Load(this.forceLoad, j, date, singleEntryLoadingStatus, iDataLoaderRunnable).getResult();
            }
        };
        task.execute();
        return task;
    }

    public static <T> AsyncResult<T> Load(boolean forceLoad, long lifetime, Date lastRefreshedTime, SingleEntryLoadingStatus loadingStatus, IDataLoaderRunnable<T> runner) {
        XLEAssert.assertNotNull(loadingStatus);
        XLEAssert.assertNotNull(runner);
        XLEAssert.assertIsNotUIThread();
        SingleEntryLoadingStatus.WaitResult waitResult = loadingStatus.waitForNotLoading();
        if (waitResult.waited) {
            XLEException exception = waitResult.error;
            if (exception == null) {
                return safeReturnResult((Object) null, runner, (XLEException) null, AsyncActionStatus.NO_OP_SUCCESS);
            }
            return safeReturnResult((Object) null, runner, exception, AsyncActionStatus.NO_OP_FAIL);
        } else if (XLEUtil.shouldRefresh(lastRefreshedTime, lifetime) || forceLoad) {
            final IDataLoaderRunnable<T> iDataLoaderRunnable = runner;
            ThreadManager.UIThreadSend(new Runnable() {
                public void run() {
                    iDataLoaderRunnable.onPreExecute();
                }
            });
            XLEException error = null;
            int retryCount = runner.getShouldRetryCountOnTokenError();
            int i = 0;
            while (true) {
                if (i > retryCount) {
                    break;
                }
                try {
                    T result = runner.buildData();
                    postExecute(result, runner, (XLEException) null, AsyncActionStatus.SUCCESS);
                    loadingStatus.setSuccess();
                    return new AsyncResult<>(result, runner, (XLEException) null, AsyncActionStatus.SUCCESS);
                } catch (XLEException xex) {
                    error = xex;
                    if (xex.getErrorCode() == XLEErrorCode.NOT_AUTHORIZED) {
                        i++;
                    } else if (xex.getErrorCode() == XLEErrorCode.INVALID_ACCESS_TOKEN) {
                    }
                } catch (Exception ex) {
                    error = new XLEException(runner.getDefaultErrorCode(), (Throwable) ex);
                }
            }
            loadingStatus.setFailed(error);
            return safeReturnResult((Object) null, runner, error, AsyncActionStatus.FAIL);
        } else {
            loadingStatus.setSuccess();
            return safeReturnResult((Object) null, runner, (XLEException) null, AsyncActionStatus.NO_CHANGE);
        }
    }

    private static <T> AsyncResult<T> safeReturnResult(T result, IDataLoaderRunnable<T> runner, XLEException error, AsyncActionStatus status) {
        postExecute(result, runner, error, status);
        return new AsyncResult<>(result, runner, error, status);
    }

    private static <T> void postExecute(final T result, final IDataLoaderRunnable<T> runner, final XLEException error, final AsyncActionStatus status) {
        ThreadManager.UIThreadSend(new Runnable() {
            public void run() {
                runner.onPostExcute(new AsyncResult(result, runner, error, status));
            }
        });
    }
}
