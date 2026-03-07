package org.simpleframework.xml.convert;

import org.simpleframework.xml.strategy.Value;

class Reference implements Value {
    private Class actual;
    private Object data;
    private Value value;

    public Reference(Value value2, Object data2, Class actual2) {
        this.actual = actual2;
        this.value = value2;
        this.data = data2;
    }

    public int getLength() {
        return 0;
    }

    public Class getType() {
        if (this.data != null) {
            return this.data.getClass();
        }
        return this.actual;
    }

    public Object getValue() {
        return this.data;
    }

    public boolean isReference() {
        return true;
    }

    public void setValue(Object data2) {
        if (this.value != null) {
            this.value.setValue(data2);
        }
        this.data = data2;
    }
}
