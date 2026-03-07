package org.simpleframework.xml.core;

import java.lang.annotation.Annotation;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.strategy.Type;
import org.simpleframework.xml.stream.Format;

class Introspector {
    private final Contact contact;
    private final Format format;
    private final Label label;
    private final Annotation marker;

    public Introspector(Contact contact2, Label label2, Format format2) {
        this.marker = contact2.getAnnotation();
        this.contact = contact2;
        this.format = format2;
        this.label = label2;
    }

    public Contact getContact() {
        return this.contact;
    }

    public Type getDependent() throws Exception {
        return this.label.getDependent();
    }

    public String getEntry() throws Exception {
        Class type = getDependent().getType();
        if (type.isArray()) {
            type = type.getComponentType();
        }
        return getName(type);
    }

    private String getName(Class type) throws Exception {
        String name = getRoot(type);
        if (name != null) {
            return name;
        }
        return Reflector.getName(type.getSimpleName());
    }

    private String getRoot(Class type) {
        Class real = type;
        while (type != null) {
            String name = getRoot(real, type);
            if (name != null) {
                return name;
            }
            type = type.getSuperclass();
        }
        return null;
    }

    private String getRoot(Class<?> cls, Class<?> type) {
        String name = type.getSimpleName();
        Root root = (Root) type.getAnnotation(Root.class);
        if (root == null) {
            return null;
        }
        String text = root.name();
        if (!isEmpty(text)) {
            return text;
        }
        return Reflector.getName(name);
    }

    public String getName() throws Exception {
        String entry = this.label.getEntry();
        if (!this.label.isInline()) {
            return getDefault();
        }
        return entry;
    }

    private String getDefault() throws Exception {
        String name = this.label.getOverride();
        return !isEmpty(name) ? name : this.contact.getName();
    }

    public Expression getExpression() throws Exception {
        String path = getPath();
        if (path != null) {
            return new PathParser(path, this.contact, this.format);
        }
        return new EmptyExpression(this.format);
    }

    public String getPath() throws Exception {
        Path path = (Path) this.contact.getAnnotation(Path.class);
        if (path == null) {
            return null;
        }
        return path.value();
    }

    public boolean isEmpty(String value) {
        if (value == null || value.length() == 0) {
            return true;
        }
        return false;
    }

    public String toString() {
        return String.format("%s on %s", new Object[]{this.marker, this.contact});
    }
}
