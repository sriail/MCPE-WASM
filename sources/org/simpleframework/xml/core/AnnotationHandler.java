package org.simpleframework.xml.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

class AnnotationHandler implements InvocationHandler {
    private static final String ATTRIBUTE = "attribute";
    private static final String CLASS = "annotationType";
    private static final String EQUAL = "equals";
    private static final String REQUIRED = "required";
    private static final String STRING = "toString";
    private final boolean attribute;
    private final Comparer comparer;
    private final boolean required;
    private final Class type;

    public AnnotationHandler(Class type2) {
        this(type2, true);
    }

    public AnnotationHandler(Class type2, boolean required2) {
        this(type2, required2, false);
    }

    public AnnotationHandler(Class type2, boolean required2, boolean attribute2) {
        this.comparer = new Comparer();
        this.attribute = attribute2;
        this.required = required2;
        this.type = type2;
    }

    public Object invoke(Object proxy, Method method, Object[] list) throws Throwable {
        String name = method.getName();
        if (name.equals(STRING)) {
            return toString();
        }
        if (name.equals(EQUAL)) {
            return Boolean.valueOf(equals(proxy, list));
        }
        if (name.equals(CLASS)) {
            return this.type;
        }
        if (name.equals(REQUIRED)) {
            return Boolean.valueOf(this.required);
        }
        if (name.equals(ATTRIBUTE)) {
            return Boolean.valueOf(this.attribute);
        }
        return method.getDefaultValue();
    }

    private boolean equals(Object proxy, Object[] list) throws Throwable {
        Annotation left = (Annotation) proxy;
        Annotation right = list[0];
        if (left.annotationType() == right.annotationType()) {
            return this.comparer.equals(left, right);
        }
        throw new PersistenceException("Annotation %s is not the same as %s", left, right);
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (this.type != null) {
            name(builder);
            attributes(builder);
        }
        return builder.toString();
    }

    private void name(StringBuilder builder) {
        String name = this.type.getName();
        if (name != null) {
            builder.append('@');
            builder.append(name);
            builder.append('(');
        }
    }

    private void attributes(StringBuilder builder) {
        Method[] list = this.type.getDeclaredMethods();
        for (int i = 0; i < list.length; i++) {
            String attribute2 = list[i].getName();
            Object value = value(list[i]);
            if (i > 0) {
                builder.append(',');
                builder.append(' ');
            }
            builder.append(attribute2);
            builder.append('=');
            builder.append(value);
        }
        builder.append(')');
    }

    private Object value(Method method) {
        String name = method.getName();
        if (name.equals(REQUIRED)) {
            return Boolean.valueOf(this.required);
        }
        if (name.equals(ATTRIBUTE)) {
            return Boolean.valueOf(this.attribute);
        }
        return method.getDefaultValue();
    }
}
