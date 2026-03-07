package org.simpleframework.xml.transform;

import java.lang.reflect.Array;

class ArrayTransform implements Transform {
    private final Transform delegate;
    private final Class entry;
    private final StringArrayTransform split = new StringArrayTransform();

    public ArrayTransform(Transform delegate2, Class entry2) {
        this.delegate = delegate2;
        this.entry = entry2;
    }

    public Object read(String value) throws Exception {
        String[] list = this.split.read(value);
        return read(list, list.length);
    }

    private Object read(String[] list, int length) throws Exception {
        Object array = Array.newInstance(this.entry, length);
        for (int i = 0; i < length; i++) {
            Object item = this.delegate.read(list[i]);
            if (item != null) {
                Array.set(array, i, item);
            }
        }
        return array;
    }

    public String write(Object value) throws Exception {
        return write(value, Array.getLength(value));
    }

    private String write(Object value, int length) throws Exception {
        String[] list = new String[length];
        for (int i = 0; i < length; i++) {
            Object entry2 = Array.get(value, i);
            if (entry2 != null) {
                list[i] = this.delegate.write(entry2);
            }
        }
        return this.split.write(list);
    }
}
