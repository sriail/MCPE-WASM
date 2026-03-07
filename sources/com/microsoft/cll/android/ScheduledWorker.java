package com.microsoft.cll.android;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public abstract class ScheduledWorker implements Runnable {
    protected ScheduledExecutorService executor;
    protected long interval;
    protected boolean isPaused = false;
    protected ScheduledFuture nextExecution;

    public abstract void run();

    public ScheduledWorker(long interval2) {
        this.interval = interval2;
    }

    /* access modifiers changed from: protected */
    public void start(ScheduledExecutorService executor2) {
        setupExecutor(executor2);
    }

    /* access modifiers changed from: protected */
    public void stop() {
        this.nextExecution.cancel(true);
    }

    /* access modifiers changed from: protected */
    public void pause() {
        this.nextExecution.cancel(false);
        this.isPaused = true;
    }

    /* access modifiers changed from: protected */
    public void resume(ScheduledExecutorService executor2) {
        setupExecutor(executor2);
        this.isPaused = false;
    }

    private void setupExecutor(ScheduledExecutorService executor2) {
        this.executor = executor2;
        this.nextExecution = executor2.scheduleAtFixedRate(this, 0, this.interval, TimeUnit.SECONDS);
    }
}
