package com.microsoft.xbox.idp.util;

import android.app.LoaderManager;
import com.microsoft.xbox.idp.toolkit.BitmapLoader;
import com.microsoft.xbox.idp.util.ErrorHelper;

public class BitmapLoaderInfo implements ErrorHelper.LoaderInfo {
    private final LoaderManager.LoaderCallbacks<?> callbacks;

    public BitmapLoaderInfo(LoaderManager.LoaderCallbacks<?> callbacks2) {
        this.callbacks = callbacks2;
    }

    public LoaderManager.LoaderCallbacks<?> getLoaderCallbacks() {
        return this.callbacks;
    }

    public void clearCache(Object key) {
        BitmapLoader.Cache cache = CacheUtil.getBitmapCache();
        synchronized (cache) {
            cache.remove(key);
        }
    }

    public boolean hasCachedData(Object key) {
        boolean z;
        BitmapLoader.Cache cache = CacheUtil.getBitmapCache();
        synchronized (cache) {
            z = cache.get(key) != null;
        }
        return z;
    }
}
