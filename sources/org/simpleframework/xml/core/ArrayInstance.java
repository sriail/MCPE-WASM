package org.simpleframework.xml.core;

import java.lang.reflect.Array;
import org.simpleframework.xml.strategy.Value;

class ArrayInstance implements Instance {
    private final int length;
    private final Class type;
    private final Value value;

    public ArrayInstance(Value value2) {
        this.length = value2.getLength();
        this.type = value2.getType();
        this.value = value2;
    }

    public Object getInstance() throws Exception {
        if (this.value.isReference()) {
            return this.value.getValue();
        }
        Object array = Array.newInstance(this.type, this.length);
        if (this.value == null) {
            return array;
        }
        this.value.setValue(array);
        return array;
    }

    public Object setInstance(Object array) {
        if (this.value != null) {
            this.value.setValue(array);
        }
        return array;
    }

    public Class getType() {
        return this.type;
    }

    public boolean isReference() {
        return this.value.isReference();
    }
}
