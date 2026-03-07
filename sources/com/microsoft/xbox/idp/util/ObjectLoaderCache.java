package com.microsoft.xbox.idp.util;

import com.microsoft.xbox.idp.toolkit.ObjectLoader;
import java.util.HashMap;

public class ObjectLoaderCache implements ObjectLoader.Cache {
    private final HashMap<Object, ObjectLoader.Result<?>> map = new HashMap<>();

    public <T> ObjectLoader.Result<T> get(Object key) {
        return this.map.get(key);
    }

    public <T> ObjectLoader.Result<T> put(Object key, ObjectLoader.Result<T> value) {
        return this.map.put(key, value);
    }

    public <T> ObjectLoader.Result<T> remove(Object key) {
        return this.map.remove(key);
    }

    public void clear() {
        this.map.clear();
    }
}
