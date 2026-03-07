package org.simpleframework.xml.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import org.simpleframework.xml.util.Cache;
import org.simpleframework.xml.util.ConcurrentCache;

class FieldContact implements Contact {
    private final Cache<Annotation> cache = new ConcurrentCache();
    private final Field field;
    private final Annotation label;
    private final Annotation[] list;
    private final int modifier;
    private final String name;

    public FieldContact(Field field2, Annotation label2, Annotation[] list2) {
        this.modifier = field2.getModifiers();
        this.name = field2.getName();
        this.label = label2;
        this.field = field2;
        this.list = list2;
    }

    public boolean isReadOnly() {
        return !isStatic() && isFinal();
    }

    public boolean isStatic() {
        return Modifier.isStatic(this.modifier);
    }

    public boolean isFinal() {
        return Modifier.isFinal(this.modifier);
    }

    public Class getType() {
        return this.field.getType();
    }

    public Class getDependent() {
        return Reflector.getDependent(this.field);
    }

    public Class[] getDependents() {
        return Reflector.getDependents(this.field);
    }

    public Class getDeclaringClass() {
        return this.field.getDeclaringClass();
    }

    public String getName() {
        return this.name;
    }

    public Annotation getAnnotation() {
        return this.label;
    }

    public <T extends Annotation> T getAnnotation(Class<T> type) {
        if (type == this.label.annotationType()) {
            return this.label;
        }
        return getCache(type);
    }

    private <T extends Annotation> T getCache(Class<T> type) {
        if (this.cache.isEmpty()) {
            for (Annotation entry : this.list) {
                this.cache.cache(entry.annotationType(), entry);
            }
        }
        return (Annotation) this.cache.fetch(type);
    }

    public void set(Object source, Object value) throws Exception {
        if (!isFinal()) {
            this.field.set(source, value);
        }
    }

    public Object get(Object source) throws Exception {
        return this.field.get(source);
    }

    public String toString() {
        return String.format("field '%s' %s", new Object[]{getName(), this.field.toString()});
    }
}
