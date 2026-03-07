package org.simpleframework.xml.core;

import org.simpleframework.xml.strategy.Value;

class OverrideValue implements Value {
    private final Class type;
    private final Value value;

    public OverrideValue(Value value2, Class type2) {
        this.value = value2;
        this.type = type2;
    }

    public Object getValue() {
        return this.value.getValue();
    }

    public void setValue(Object instance) {
        this.value.setValue(instance);
    }

    public Class getType() {
        return this.type;
    }

    public int getLength() {
        return this.value.getLength();
    }

    public boolean isReference() {
        return this.value.isReference();
    }
}
