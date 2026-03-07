package org.simpleframework.xml.transform;

import org.simpleframework.xml.util.Cache;
import org.simpleframework.xml.util.ConcurrentCache;

public class RegistryMatcher implements Matcher {
    private final Cache<Transform> transforms = new ConcurrentCache();
    private final Cache<Class> types = new ConcurrentCache();

    public void bind(Class type, Class transform) {
        this.types.cache(type, transform);
    }

    public void bind(Class type, Transform transform) {
        this.transforms.cache(type, transform);
    }

    public Transform match(Class type) throws Exception {
        Transform transform = this.transforms.fetch(type);
        if (transform == null) {
            return create(type);
        }
        return transform;
    }

    private Transform create(Class type) throws Exception {
        Class factory = this.types.fetch(type);
        if (factory != null) {
            return create(type, factory);
        }
        return null;
    }

    private Transform create(Class type, Class factory) throws Exception {
        Transform transform = (Transform) factory.newInstance();
        if (transform != null) {
            this.transforms.cache(type, transform);
        }
        return transform;
    }
}
