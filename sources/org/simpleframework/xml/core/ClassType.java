package org.simpleframework.xml.core;

import java.lang.annotation.Annotation;
import org.simpleframework.xml.strategy.Type;

class ClassType implements Type {
    private final Class type;

    public ClassType(Class type2) {
        this.type = type2;
    }

    public Class getType() {
        return this.type;
    }

    public <T extends Annotation> T getAnnotation(Class<T> cls) {
        return null;
    }

    public String toString() {
        return this.type.toString();
    }
}
