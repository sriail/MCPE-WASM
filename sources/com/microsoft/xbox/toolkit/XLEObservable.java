package com.microsoft.xbox.toolkit;

import java.util.ArrayList;
import java.util.HashSet;

public abstract class XLEObservable<T> {
    private HashSet<XLEObserver<T>> data = new HashSet<>();

    public synchronized void addUniqueObserver(XLEObserver<T> observer) {
        if (!this.data.contains(observer)) {
            addObserver(observer);
        }
    }

    public synchronized void addObserver(XLEObserver<T> observer) {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        this.data.add(observer);
    }

    public synchronized void removeObserver(XLEObserver<T> observer) {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        this.data.remove(observer);
    }

    public synchronized void notifyObservers(AsyncResult<T> asyncResult) {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        for (XLEObserver<T> observer : new ArrayList<>(this.data)) {
            observer.update(asyncResult);
        }
    }

    /* access modifiers changed from: protected */
    public synchronized void clearObserver() {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        this.data.clear();
    }

    /* access modifiers changed from: protected */
    public synchronized ArrayList<XLEObserver<T>> getObservers() {
        return new ArrayList<>(this.data);
    }
}
