package org.simpleframework.xml.strategy;

class ArrayValue implements Value {
    private int size;
    private Class type;
    private Object value;

    public ArrayValue(Class type2, int size2) {
        this.type = type2;
        this.size = size2;
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
        return this.size;
    }

    public boolean isReference() {
        return false;
    }
}
