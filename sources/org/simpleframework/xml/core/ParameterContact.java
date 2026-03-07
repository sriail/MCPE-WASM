package org.simpleframework.xml.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;

abstract class ParameterContact<T extends Annotation> implements Contact {
    protected final Constructor factory;
    protected final int index;
    protected final T label;
    protected final Annotation[] labels;
    protected final Class owner;

    public abstract String getName();

    public ParameterContact(T label2, Constructor factory2, int index2) {
        this.labels = factory2.getParameterAnnotations()[index2];
        this.owner = factory2.getDeclaringClass();
        this.factory = factory2;
        this.index = index2;
        this.label = label2;
    }

    public Annotation getAnnotation() {
        return this.label;
    }

    public Class getType() {
        return this.factory.getParameterTypes()[this.index];
    }

    public Class getDependent() {
        return Reflector.getParameterDependent(this.factory, this.index);
    }

    public Class[] getDependents() {
        return Reflector.getParameterDependents(this.factory, this.index);
    }

    public Class getDeclaringClass() {
        return this.owner;
    }

    public Object get(Object source) {
        return null;
    }

    public void set(Object source, Object value) {
    }

    public <A extends Annotation> A getAnnotation(Class<A> type) {
        for (Annotation label2 : this.labels) {
            if (label2.annotationType().equals(type)) {
                return label2;
            }
        }
        return null;
    }

    public boolean isReadOnly() {
        return false;
    }

    public String toString() {
        return String.format("parameter %s of constructor %s", new Object[]{Integer.valueOf(this.index), this.factory});
    }
}
