package org.simpleframework.xml.strategy;

class ObjectValue implements Value {
    private Class type;
    private Object value;

    public ObjectValue(Class type2) {
        this.type = type2;
    }

    public Object getValue() {
        return this.value;
    }

    public void setValue(Object value2) {
        this.value = value2;
    }

    public Class getType() {
        return this.type;
    }

    public int getLength() {
        return 0;
    }

    public boolean isReference() {
        return false;
    }
}
