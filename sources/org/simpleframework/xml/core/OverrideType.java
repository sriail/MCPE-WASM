package org.simpleframework.xml.core;

import java.lang.annotation.Annotation;
import org.simpleframework.xml.strategy.Type;

class OverrideType implements Type {
    private final Class override;
    private final Type type;

    public OverrideType(Type type2, Class override2) {
        this.override = override2;
        this.type = type2;
    }

    public <T extends Annotation> T getAnnotation(Class<T> label) {
        return this.type.getAnnotation(label);
    }

    public Class getType() {
        return this.override;
    }

    public String toString() {
        return this.type.toString();
    }
}
