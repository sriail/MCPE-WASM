package com.microsoft.xbox.toolkit;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.PriorityQueue;

public class ThreadSafeFixedSizeHashtable<K, V> {
    private int count = 0;
    private PriorityQueue<ThreadSafeFixedSizeHashtable<K, V>.KeyTuple> fifo = new PriorityQueue<>();
    private Hashtable<K, V> hashtable = new Hashtable<>();
    private final int maxSize;
    private Object syncObject = new Object();

    private class KeyTuple implements Comparable<ThreadSafeFixedSizeHashtable<K, V>.KeyTuple> {
        private int index = 0;
        /* access modifiers changed from: private */
        public K key;

        public KeyTuple(K key2, int index2) {
            this.key = key2;
            this.index = index2;
        }

        public int compareTo(ThreadSafeFixedSizeHashtable<K, V>.KeyTuple rhs) {
            return this.index - rhs.index;
        }

        public K getKey() {
            return this.key;
        }
    }

    public ThreadSafeFixedSizeHashtable(int maxSize2) {
        this.maxSize = maxSize2;
        if (maxSize2 <= 0) {
            throw new IllegalArgumentException();
        }
    }

    public void put(K key, V value) {
        if (key != null && value != null) {
            synchronized (this.syncObject) {
                if (!this.hashtable.containsKey(key)) {
                    this.count++;
                    this.fifo.add(new KeyTuple(key, this.count));
                    this.hashtable.put(key, value);
                    cleanupIfNecessary();
                }
            }
        }
    }

    public V get(K key) {
        V v;
        if (key == null) {
            return null;
        }
        synchronized (this.syncObject) {
            v = this.hashtable.get(key);
        }
        return v;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:25:?, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void remove(K r6) {
        /*
            r5 = this;
            if (r6 != 0) goto L_0x0003
        L_0x0002:
            return
        L_0x0003:
            java.lang.Object r4 = r5.syncObject
            monitor-enter(r4)
            java.util.Hashtable<K, V> r3 = r5.hashtable     // Catch:{ all -> 0x0010 }
            boolean r3 = r3.containsKey(r6)     // Catch:{ all -> 0x0010 }
            if (r3 != 0) goto L_0x0013
            monitor-exit(r4)     // Catch:{ all -> 0x0010 }
            goto L_0x0002
        L_0x0010:
            r3 = move-exception
            monitor-exit(r4)     // Catch:{ all -> 0x0010 }
            throw r3
        L_0x0013:
            java.util.Hashtable<K, V> r3 = r5.hashtable     // Catch:{ all -> 0x0010 }
            r3.remove(r6)     // Catch:{ all -> 0x0010 }
            r0 = 0
            r1 = 0
            java.util.PriorityQueue<com.microsoft.xbox.toolkit.ThreadSafeFixedSizeHashtable<K, V>$KeyTuple> r3 = r5.fifo     // Catch:{ all -> 0x0010 }
            java.util.Iterator r2 = r3.iterator()     // Catch:{ all -> 0x0010 }
        L_0x0020:
            boolean r3 = r2.hasNext()     // Catch:{ all -> 0x0010 }
            if (r3 == 0) goto L_0x0037
            java.lang.Object r0 = r2.next()     // Catch:{ all -> 0x0010 }
            com.microsoft.xbox.toolkit.ThreadSafeFixedSizeHashtable$KeyTuple r0 = (com.microsoft.xbox.toolkit.ThreadSafeFixedSizeHashtable.KeyTuple) r0     // Catch:{ all -> 0x0010 }
            java.lang.Object r3 = r0.key     // Catch:{ all -> 0x0010 }
            boolean r3 = r3.equals(r6)     // Catch:{ all -> 0x0010 }
            if (r3 == 0) goto L_0x0020
            r1 = r0
        L_0x0037:
            if (r1 == 0) goto L_0x003e
            java.util.PriorityQueue<com.microsoft.xbox.toolkit.ThreadSafeFixedSizeHashtable<K, V>$KeyTuple> r3 = r5.fifo     // Catch:{ all -> 0x0010 }
            r3.remove(r1)     // Catch:{ all -> 0x0010 }
        L_0x003e:
            monitor-exit(r4)     // Catch:{ all -> 0x0010 }
            goto L_0x0002
        */
        throw new UnsupportedOperationException("Method not decompiled: com.microsoft.xbox.toolkit.ThreadSafeFixedSizeHashtable.remove(java.lang.Object):void");
    }

    public Enumeration<V> elements() {
        return this.hashtable.elements();
    }

    public Enumeration<K> keys() {
        return this.hashtable.keys();
    }

    private void cleanupIfNecessary() {
        boolean z;
        boolean z2;
        if (this.hashtable.size() == this.fifo.size()) {
            z = true;
        } else {
            z = false;
        }
        XLEAssert.assertTrue(z);
        while (this.hashtable.size() > this.maxSize) {
            this.hashtable.remove(((KeyTuple) this.fifo.remove()).getKey());
            if (this.hashtable.size() == this.fifo.size()) {
                z2 = true;
            } else {
                z2 = false;
            }
            XLEAssert.assertTrue(z2);
        }
    }
}
