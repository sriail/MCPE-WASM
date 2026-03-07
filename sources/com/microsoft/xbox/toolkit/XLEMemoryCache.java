package com.microsoft.xbox.toolkit;

import android.util.LruCache;

public class XLEMemoryCache<K, V> {
    private int itemCount = 0;
    private final LruCache<K, XLEMemoryCacheEntry<V>> lruCache;
    private final int maxFileSizeBytes;

    static /* synthetic */ int access$006(XLEMemoryCache x0) {
        int i = x0.itemCount - 1;
        x0.itemCount = i;
        return i;
    }

    public XLEMemoryCache(int sizeInBytes, int maxFileSizeInBytes) {
        AnonymousClass1 r0;
        if (sizeInBytes < 0) {
            throw new IllegalArgumentException("sizeInBytes");
        } else if (maxFileSizeInBytes < 0) {
            throw new IllegalArgumentException("maxFileSizeInBytes");
        } else {
            this.maxFileSizeBytes = maxFileSizeInBytes;
            if (sizeInBytes == 0) {
                r0 = null;
            } else {
                r0 = new LruCache<K, XLEMemoryCacheEntry<V>>(sizeInBytes) {
                    /* access modifiers changed from: protected */
                    public int sizeOf(K k, XLEMemoryCacheEntry<V> value) {
                        return value.getByteCount();
                    }

                    /* access modifiers changed from: protected */
                    public void entryRemoved(boolean evicted, K k, XLEMemoryCacheEntry<V> xLEMemoryCacheEntry, XLEMemoryCacheEntry<V> xLEMemoryCacheEntry2) {
                        XLEMemoryCache.access$006(XLEMemoryCache.this);
                    }
                };
            }
            this.lruCache = r0;
        }
    }

    public int getBytesCurrent() {
        if (this.lruCache == null) {
            return 0;
        }
        return this.lruCache.size();
    }

    public int getItemsInCache() {
        return this.itemCount;
    }

    public int getBytesFree() {
        if (this.lruCache == null) {
            return 0;
        }
        return this.lruCache.maxSize() - this.lruCache.size();
    }

    public boolean add(K filename, V data, int fileByteCount) {
        if (fileByteCount > this.maxFileSizeBytes || this.lruCache == null) {
            return false;
        }
        XLEMemoryCacheEntry<V> entry = new XLEMemoryCacheEntry<>(data, fileByteCount);
        this.itemCount++;
        this.lruCache.put(filename, entry);
        return true;
    }

    public V get(K filename) {
        XLEMemoryCacheEntry<V> entry;
        if (this.lruCache == null || (entry = this.lruCache.get(filename)) == null) {
            return null;
        }
        return entry.getValue();
    }
}
