package org.simpleframework.xml.core;

import java.lang.annotation.Annotation;

class LabelKey {
    private final Class label;
    private final String name;
    private final Class owner;
    private final Class type;

    public LabelKey(Contact contact, Annotation label2) {
        this.owner = contact.getDeclaringClass();
        this.label = label2.annotationType();
        this.name = contact.getName();
        this.type = contact.getType();
    }

    public int hashCode() {
        return this.name.hashCode() ^ this.owner.hashCode();
    }

    public boolean equals(Object value) {
        if (value instanceof LabelKey) {
            return equals((LabelKey) value);
        }
        return false;
    }

    private boolean equals(LabelKey key) {
        if (key == this) {
            return true;
        }
        if (key.label == this.label && key.owner == this.owner && key.type == this.type) {
            return key.name.equals(this.name);
        }
        return false;
    }

    public String toString() {
        return String.format("key '%s' for %s", new Object[]{this.name, this.owner});
    }
}
