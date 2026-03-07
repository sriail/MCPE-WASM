package org.simpleframework.xml.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.Map;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementArray;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.stream.Format;
import org.simpleframework.xml.stream.Verbosity;

class AnnotationFactory {
    private final Format format;
    private final boolean required;

    public AnnotationFactory(Detail detail, Support support) {
        this.required = detail.isRequired();
        this.format = support.getFormat();
    }

    public Annotation getInstance(Class type, Class[] dependents) throws Exception {
        ClassLoader loader = getClassLoader();
        if (Map.class.isAssignableFrom(type)) {
            if (!isPrimitiveKey(dependents) || !isAttribute()) {
                return getInstance(loader, ElementMap.class);
            }
            return getInstance(loader, ElementMap.class, true);
        } else if (Collection.class.isAssignableFrom(type)) {
            return getInstance(loader, ElementList.class);
        } else {
            return getInstance(type);
        }
    }

    private Annotation getInstance(Class type) throws Exception {
        ClassLoader loader = getClassLoader();
        Class entry = type.getComponentType();
        if (type.isArray()) {
            if (isPrimitive(entry)) {
                return getInstance(loader, Element.class);
            }
            return getInstance(loader, ElementArray.class);
        } else if (!isPrimitive(type) || !isAttribute()) {
            return getInstance(loader, Element.class);
        } else {
            return getInstance(loader, Attribute.class);
        }
    }

    private Annotation getInstance(ClassLoader loader, Class label) throws Exception {
        return getInstance(loader, label, false);
    }

    private Annotation getInstance(ClassLoader loader, Class label, boolean attribute) throws Exception {
        return (Annotation) Proxy.newProxyInstance(loader, new Class[]{label}, new AnnotationHandler(label, this.required, attribute));
    }

    private ClassLoader getClassLoader() throws Exception {
        return AnnotationFactory.class.getClassLoader();
    }

    private boolean isPrimitiveKey(Class[] dependents) {
        if (dependents == null || dependents.length <= 0) {
            return false;
        }
        Class parent = dependents[0].getSuperclass();
        Class type = dependents[0];
        if (parent == null || (!parent.isEnum() && !type.isEnum())) {
            return isPrimitive(type);
        }
        return true;
    }

    private boolean isPrimitive(Class type) {
        if (Number.class.isAssignableFrom(type) || type == Boolean.class || type == Character.class) {
            return true;
        }
        return type.isPrimitive();
    }

    private boolean isAttribute() {
        Verbosity verbosity = this.format.getVerbosity();
        if (verbosity == null || verbosity != Verbosity.LOW) {
            return false;
        }
        return true;
    }
}
