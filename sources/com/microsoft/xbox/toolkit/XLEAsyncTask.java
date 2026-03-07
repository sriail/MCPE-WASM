package com.microsoft.xbox.toolkit;

import com.microsoft.xbox.toolkit.network.XLEThreadPool;

public abstract class XLEAsyncTask<Result> {
    protected boolean cancelled = false;
    /* access modifiers changed from: private */
    public XLEAsyncTask chainedTask = null;
    private Runnable doBackgroundAndPostExecuteRunnable = null;
    protected boolean isBusy = false;
    private XLEThreadPool threadPool = null;

    /* access modifiers changed from: protected */
    public abstract Result doInBackground();

    /* access modifiers changed from: protected */
    public abstract void onPostExecute(Result result);

    /* access modifiers changed from: protected */
    public abstract void onPreExecute();

    public XLEAsyncTask(XLEThreadPool threadPool2) {
        this.threadPool = threadPool2;
        this.doBackgroundAndPostExecuteRunnable = new Runnable() {
            public void run() {
                final Result r;
                if (!XLEAsyncTask.this.cancelled) {
                    r = XLEAsyncTask.this.doInBackground();
                } else {
                    r = null;
                }
                ThreadManager.UIThreadPost(new Runnable() {
                    public void run() {
                        XLEAsyncTask.this.isBusy = false;
                        if (!XLEAsyncTask.this.cancelled) {
                            XLEAsyncTask.this.onPostExecute(r);
                            if (XLEAsyncTask.this.chainedTask != null) {
                                XLEAsyncTask.this.chainedTask.execute();
                            }
                        }
                    }
                });
            }
        };
    }

    public void cancel() {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        this.cancelled = true;
    }

    public void execute() {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        this.cancelled = false;
        this.isBusy = true;
        onPreExecute();
        executeBackground();
    }

    public boolean getIsBusy() {
        return this.isBusy && !this.cancelled;
    }

    public static void executeAll(XLEAsyncTask... tasks) {
        if (tasks.length > 0) {
            for (int i = 0; i < tasks.length - 1; i++) {
                tasks[i].chainedTask = tasks[i + 1];
            }
            tasks[0].execute();
        }
    }

    /* access modifiers changed from: protected */
    public void executeBackground() {
        this.cancelled = false;
        this.threadPool.run(this.doBackgroundAndPostExecuteRunnable);
    }
}
