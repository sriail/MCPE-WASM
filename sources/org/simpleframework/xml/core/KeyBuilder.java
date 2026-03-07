package org.simpleframework.xml.core;

import java.util.Arrays;

class KeyBuilder {
    private final Label label;

    private enum KeyType {
        TEXT,
        ATTRIBUTE,
        ELEMENT
    }

    public KeyBuilder(Label label2) {
        this.label = label2;
    }

    public Object getKey() throws Exception {
        if (this.label.isAttribute()) {
            return getKey(KeyType.ATTRIBUTE);
        }
        return getKey(KeyType.ELEMENT);
    }

    private Object getKey(KeyType type) throws Exception {
        String text = getKey(this.label.getPaths());
        return type == null ? text : new Key(type, text);
    }

    private String getKey(String[] list) throws Exception {
        StringBuilder builder = new StringBuilder();
        if (list.length > 0) {
            Arrays.sort(list);
            for (String path : list) {
                builder.append(path);
                builder.append('>');
            }
        }
        return builder.toString();
    }

    private static class Key {
        private final KeyType type;
        private final String value;

        public Key(KeyType type2, String value2) throws Exception {
            this.value = value2;
            this.type = type2;
        }

        public boolean equals(Object value2) {
            if (value2 instanceof Key) {
                return equals((Key) value2);
            }
            return false;
        }

        public boolean equals(Key key) {
            if (this.type == key.type) {
                return key.value.equals(this.value);
            }
            return false;
        }

        public int hashCode() {
            return this.value.hashCode();
        }

        public String toString() {
            return this.value;
        }
    }
}
