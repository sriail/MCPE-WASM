package org.simpleframework.xml.core;

import java.lang.reflect.Modifier;
import org.simpleframework.xml.strategy.Type;
import org.simpleframework.xml.strategy.Value;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;
import org.simpleframework.xml.stream.Position;

abstract class Factory {
    protected Context context;
    protected Class override;
    protected Support support;
    protected Type type;

    protected Factory(Context context2, Type type2) {
        this(context2, type2, (Class) null);
    }

    protected Factory(Context context2, Type type2, Class override2) {
        this.support = context2.getSupport();
        this.override = override2;
        this.context = context2;
        this.type = type2;
    }

    public Class getType() {
        if (this.override != null) {
            return this.override;
        }
        return this.type.getType();
    }

    public Object getInstance() throws Exception {
        Class type2 = getType();
        if (isInstantiable(type2)) {
            return type2.newInstance();
        }
        throw new InstantiationException("Type %s can not be instantiated", type2);
    }

    /* access modifiers changed from: protected */
    public Value getOverride(InputNode node) throws Exception {
        Value value = getConversion(node);
        if (value != null) {
            Position line = node.getPosition();
            Class proposed = value.getType();
            if (!isCompatible(getType(), proposed)) {
                throw new InstantiationException("Incompatible %s for %s at %s", proposed, this.type, line);
            }
        }
        return value;
    }

    public boolean setOverride(Type type2, Object value, OutputNode node) throws Exception {
        Class expect = type2.getType();
        if (expect.isPrimitive()) {
            type2 = getPrimitive(type2, expect);
        }
        return this.context.setOverride(type2, value, node);
    }

    private Type getPrimitive(Type type2, Class expect) throws Exception {
        Support support2 = this.support;
        Class convert = Support.getPrimitive(expect);
        if (convert != expect) {
            return new OverrideType(type2, convert);
        }
        return type2;
    }

    public Value getConversion(InputNode node) throws Exception {
        Value value = this.context.getOverride(this.type, node);
        if (value == null || this.override == null) {
            return value;
        }
        if (!isCompatible(this.override, value.getType())) {
            return new OverrideValue(value, this.override);
        }
        return value;
    }

    public static boolean isCompatible(Class expect, Class type2) {
        if (expect.isArray()) {
            expect = expect.getComponentType();
        }
        return expect.isAssignableFrom(type2);
    }

    public static boolean isInstantiable(Class type2) {
        int modifiers = type2.getModifiers();
        if (!Modifier.isAbstract(modifiers) && !Modifier.isInterface(modifiers)) {
            return true;
        }
        return false;
    }
}
