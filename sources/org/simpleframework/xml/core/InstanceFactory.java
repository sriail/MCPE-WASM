package org.simpleframework.xml.core;

import java.lang.reflect.Constructor;
import org.simpleframework.xml.strategy.Value;
import org.simpleframework.xml.util.Cache;
import org.simpleframework.xml.util.ConcurrentCache;

class InstanceFactory {
    private final Cache<Constructor> cache = new ConcurrentCache();

    public Instance getInstance(Value value) {
        return new ValueInstance(value);
    }

    public Instance getInstance(Class type) {
        return new ClassInstance(type);
    }

    /* access modifiers changed from: protected */
    public Object getObject(Class type) throws Exception {
        Constructor method = this.cache.fetch(type);
        if (method == null) {
            method = type.getDeclaredConstructor(new Class[0]);
            if (!method.isAccessible()) {
                method.setAccessible(true);
            }
            this.cache.cache(type, method);
        }
        return method.newInstance(new Object[0]);
    }

    private class ValueInstance implements Instance {
        private final Class type;
        private final Value value;

        public ValueInstance(Value value2) {
            this.type = value2.getType();
            this.value = value2;
        }

        public Object getInstance() throws Exception {
            if (this.value.isReference()) {
                return this.value.getValue();
            }
            Object object = InstanceFactory.this.getObject(this.type);
            if (this.value == null) {
                return object;
            }
            this.value.setValue(object);
            return object;
        }

        public Object setInstance(Object object) {
            if (this.value != null) {
                this.value.setValue(object);
            }
            return object;
        }

        public boolean isReference() {
            return this.value.isReference();
        }

        public Class getType() {
            return this.type;
        }
    }

    private class ClassInstance implements Instance {
        private Class type;
        private Object value;

        public ClassInstance(Class type2) {
            this.type = type2;
        }

        public Object getInstance() throws Exception {
            if (this.value == null) {
                this.value = InstanceFactory.this.getObject(this.type);
            }
            return this.value;
        }

        public Object setInstance(Object value2) throws Exception {
            this.value = value2;
            return value2;
        }

        public Class getType() {
            return this.type;
        }

        public boolean isReference() {
            return false;
        }
    }
}
