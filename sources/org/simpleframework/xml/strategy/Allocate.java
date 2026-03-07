package org.simpleframework.xml.strategy;

import java.util.Map;

class Allocate implements Value {
    private String key;
    private Map map;
    private Value value;

    public Allocate(Value value2, Map map2, String key2) {
        this.value = value2;
        this.map = map2;
        this.key = key2;
    }

    public Object getValue() {
        return this.map.get(this.key);
    }

    public void setValue(Object object) {
        if (this.key != null) {
            this.map.put(this.key, object);
        }
        this.value.setValue(object);
    }

    public Class getType() {
        return this.value.getType();
    }

    public int getLength() {
        return this.value.getLength();
    }

    public boolean isReference() {
        return false;
    }
}
