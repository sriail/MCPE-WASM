package com.microsoft.xbox.idp.util;

import android.util.LruCache;
import com.microsoft.xbox.idp.toolkit.BitmapLoader;

public class BitmapLoaderCache implements BitmapLoader.Cache {
    private final LruCache<Object, byte[]> cache;

    public BitmapLoaderCache(int numOfEntries) {
        this.cache = new LruCache<>(numOfEntries);
    }

    public byte[] get(Object key) {
        return this.cache.get(key);
    }

    public byte[] put(Object key, byte[] value) {
        return this.cache.put(key, value);
    }

    public byte[] remove(Object key) {
        return this.cache.remove(key);
    }

    public void clear() {
        this.cache.evictAll();
    }
}
