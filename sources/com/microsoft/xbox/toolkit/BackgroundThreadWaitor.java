package com.microsoft.xbox.toolkit;

import android.os.SystemClock;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;

public class BackgroundThreadWaitor {
    private static BackgroundThreadWaitor instance = new BackgroundThreadWaitor();
    private BackgroundThreadWaitorChangedCallback blockingChangedCallback = null;
    private Hashtable<WaitType, WaitObject> blockingTable = new Hashtable<>();
    private Ready waitReady = new Ready();
    private ArrayList<Runnable> waitingRunnables = new ArrayList<>();

    public interface BackgroundThreadWaitorChangedCallback {
        void run(EnumSet<WaitType> enumSet, boolean z);
    }

    public enum WaitType {
        Navigation,
        ApplicationBar,
        ListScroll,
        ListLayout,
        PivotScroll
    }

    public static BackgroundThreadWaitor getInstance() {
        if (instance == null) {
            instance = new BackgroundThreadWaitor();
        }
        return instance;
    }

    public void waitForReady(int timeoutMs) {
        XLEAssert.assertTrue(ThreadManager.UIThread != Thread.currentThread());
        ThreadManager.UIThreadPost(new Runnable() {
            public void run() {
                BackgroundThreadWaitor.this.updateWaitReady();
            }
        });
        this.waitReady.waitForReady(timeoutMs);
    }

    public void setBlocking(WaitType type, int expireMs) {
        XLEAssert.assertTrue(ThreadManager.UIThread == Thread.currentThread());
        this.blockingTable.put(type, new WaitObject(type, (long) expireMs));
        updateWaitReady();
    }

    public void clearBlocking(WaitType type) {
        XLEAssert.assertTrue(ThreadManager.UIThread == Thread.currentThread());
        this.blockingTable.remove(type);
        updateWaitReady();
    }

    public void setChangedCallback(BackgroundThreadWaitorChangedCallback callback) {
        this.blockingChangedCallback = callback;
    }

    public boolean isBlocking() {
        return !this.waitReady.getIsReady();
    }

    /* access modifiers changed from: private */
    public void updateWaitReady() {
        boolean blocking;
        XLEAssert.assertTrue(ThreadManager.UIThread == Thread.currentThread());
        HashSet<WaitType> waitTypesToRemove = new HashSet<>();
        EnumSet<WaitType> blockingTypes = EnumSet.noneOf(WaitType.class);
        Enumeration<WaitObject> e = this.blockingTable.elements();
        while (e.hasMoreElements()) {
            WaitObject waitObject = e.nextElement();
            if (waitObject.isExpired()) {
                waitTypesToRemove.add(waitObject.type);
            } else {
                blockingTypes.add(waitObject.type);
            }
        }
        Iterator i$ = waitTypesToRemove.iterator();
        while (i$.hasNext()) {
            this.blockingTable.remove(i$.next());
        }
        if (this.blockingTable.size() == 0) {
            this.waitReady.setReady();
            drainWaitingRunnables();
            blocking = false;
        } else {
            this.waitReady.reset();
            blocking = true;
        }
        if (this.blockingChangedCallback != null) {
            this.blockingChangedCallback.run(blockingTypes, blocking);
        }
    }

    public void postRunnableAfterReady(Runnable r) {
        XLEAssert.assertTrue(ThreadManager.UIThread == Thread.currentThread());
        if (r != null) {
            if (!isBlocking()) {
                r.run();
            } else {
                this.waitingRunnables.add(r);
            }
        }
    }

    private void drainWaitingRunnables() {
        XLEAssert.assertTrue(ThreadManager.UIThread == Thread.currentThread());
        Iterator i$ = this.waitingRunnables.iterator();
        while (i$.hasNext()) {
            i$.next().run();
        }
        this.waitingRunnables.clear();
    }

    private class WaitObject {
        private long expires;
        /* access modifiers changed from: private */
        public WaitType type;

        public WaitObject(WaitType type2, long expireMs) {
            this.type = type2;
            this.expires = SystemClock.uptimeMillis() + expireMs;
        }

        public boolean isExpired() {
            return this.expires < SystemClock.uptimeMillis();
        }
    }
}
