package org.simpleframework.xml.core;

import java.lang.annotation.Annotation;

class MethodContact implements Contact {
    private MethodPart get;
    private Class item;
    private Class[] items;
    private Annotation label;
    private String name;
    private Class owner;
    private MethodPart set;
    private Class type;

    public MethodContact(MethodPart get2) {
        this(get2, (MethodPart) null);
    }

    public MethodContact(MethodPart get2, MethodPart set2) {
        this.owner = get2.getDeclaringClass();
        this.label = get2.getAnnotation();
        this.items = get2.getDependents();
        this.item = get2.getDependent();
        this.type = get2.getType();
        this.name = get2.getName();
        this.set = set2;
        this.get = get2;
    }

    public boolean isReadOnly() {
        return this.set == null;
    }

    public MethodPart getRead() {
        return this.get;
    }

    public MethodPart getWrite() {
        return this.set;
    }

    public Annotation getAnnotation() {
        return this.label;
    }

    public <T extends Annotation> T getAnnotation(Class<T> type2) {
        T result = this.get.getAnnotation(type2);
        if (type2 == this.label.annotationType()) {
            return this.label;
        }
        if (result != null || this.set == null) {
            return result;
        }
        return this.set.getAnnotation(type2);
    }

    public Class getType() {
        return this.type;
    }

    public Class getDependent() {
        return this.item;
    }

    public Class[] getDependents() {
        return this.items;
    }

    public Class getDeclaringClass() {
        return this.owner;
    }

    public String getName() {
        return this.name;
    }

    public void set(Object source, Object value) throws Exception {
        Class type2 = this.get.getMethod().getDeclaringClass();
        if (this.set == null) {
            throw new MethodException("Property '%s' is read only in %s", this.name, type2);
        }
        this.set.getMethod().invoke(source, new Object[]{value});
    }

    public Object get(Object source) throws Exception {
        return this.get.getMethod().invoke(source, new Object[0]);
    }

    public String toString() {
        return String.format("method '%s'", new Object[]{this.name});
    }
}
