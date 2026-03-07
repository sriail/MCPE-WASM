package com.microsoft.xbox.idp.util;

import android.app.LoaderManager;
import com.microsoft.xbox.idp.util.ErrorHelper;

public class ResultLoaderInfo<R> implements ErrorHelper.LoaderInfo {
    private final LoaderManager.LoaderCallbacks<?> callbacks;
    private final Class<R> cls;

    public ResultLoaderInfo(Class<R> cls2, LoaderManager.LoaderCallbacks<?> callbacks2) {
        this.cls = cls2;
        this.callbacks = callbacks2;
    }

    public LoaderManager.LoaderCallbacks<?> getLoaderCallbacks() {
        return this.callbacks;
    }

    public void clearCache(Object key) {
        ResultCache<R> cache = CacheUtil.getResultCache(this.cls);
        synchronized (cache) {
            cache.remove(key);
        }
    }

    public boolean hasCachedData(Object key) {
        boolean z;
        ResultCache<R> cache = CacheUtil.getResultCache(this.cls);
        synchronized (cache) {
            z = cache.get(key) != null;
        }
        return z;
    }
}
