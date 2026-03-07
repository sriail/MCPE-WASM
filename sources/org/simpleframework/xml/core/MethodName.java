package org.simpleframework.xml.core;

import java.lang.reflect.Method;

class MethodName {
    private Method method;
    private String name;
    private MethodType type;

    public MethodName(Method method2, MethodType type2, String name2) {
        this.method = method2;
        this.type = type2;
        this.name = name2;
    }

    public String getName() {
        return this.name;
    }

    public MethodType getType() {
        return this.type;
    }

    public Method getMethod() {
        return this.method;
    }
}
