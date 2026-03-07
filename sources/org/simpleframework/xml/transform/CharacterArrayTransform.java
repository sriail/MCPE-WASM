package org.simpleframework.xml.transform;

import java.lang.reflect.Array;

class CharacterArrayTransform implements Transform {
    private final Class entry;

    public CharacterArrayTransform(Class entry2) {
        this.entry = entry2;
    }

    public Object read(String value) throws Exception {
        char[] list = value.toCharArray();
        return this.entry == Character.TYPE ? list : read(list, list.length);
    }

    private Object read(char[] list, int length) throws Exception {
        Object array = Array.newInstance(this.entry, length);
        for (int i = 0; i < length; i++) {
            Array.set(array, i, Character.valueOf(list[i]));
        }
        return array;
    }

    public String write(Object value) throws Exception {
        int length = Array.getLength(value);
        if (this.entry == Character.TYPE) {
            return new String((char[]) value);
        }
        return write(value, length);
    }

    private String write(Object value, int length) throws Exception {
        StringBuilder text = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            Object entry2 = Array.get(value, i);
            if (entry2 != null) {
                text.append(entry2);
            }
        }
        return text.toString();
    }
}
