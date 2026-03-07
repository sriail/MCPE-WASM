package org.simpleframework.xml.core;

import org.simpleframework.xml.strategy.Value;

class ObjectInstance implements Instance {
    private final Context context;
    private final Class type;
    private final Value value;

    public ObjectInstance(Context context2, Value value2) {
        this.type = value2.getType();
        this.context = context2;
        this.value = value2;
    }

    public Object getInstance() throws Exception {
        if (this.value.isReference()) {
            return this.value.getValue();
        }
        Object object = getInstance(this.type);
        if (this.value == null) {
            return object;
        }
        this.value.setValue(object);
        return object;
    }

    public Object getInstance(Class type2) throws Exception {
        return this.context.getInstance(type2).getInstance();
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
