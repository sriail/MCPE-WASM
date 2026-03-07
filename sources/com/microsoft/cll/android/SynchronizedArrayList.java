package com.microsoft.cll.android;

import java.util.ArrayList;

public class SynchronizedArrayList<T> extends ArrayList<T> {
    public synchronized boolean add(T t) {
        boolean add;
        if (contains(t)) {
            add = false;
        } else {
            add = super.add(t);
        }
        return add;
    }

    public synchronized boolean remove(Object o) {
        return super.remove(o);
    }
}
