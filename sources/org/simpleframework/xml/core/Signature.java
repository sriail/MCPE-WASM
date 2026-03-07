package org.simpleframework.xml.core;

import java.lang.reflect.Constructor;
import java.util.Iterator;
import java.util.List;

class Signature implements Iterable<Parameter> {
    private final Constructor factory;
    private final ParameterMap parameters;
    private final Class type;

    public Signature(Signature signature) {
        this(signature.factory, signature.type);
    }

    public Signature(Constructor factory2) {
        this(factory2, factory2.getDeclaringClass());
    }

    public Signature(Constructor factory2, Class type2) {
        this.parameters = new ParameterMap();
        this.factory = factory2;
        this.type = type2;
    }

    public int size() {
        return this.parameters.size();
    }

    public boolean isEmpty() {
        return this.parameters.isEmpty();
    }

    public boolean contains(Object key) {
        return this.parameters.containsKey(key);
    }

    public Iterator<Parameter> iterator() {
        return this.parameters.iterator();
    }

    public Parameter remove(Object key) {
        return (Parameter) this.parameters.remove(key);
    }

    public Parameter get(int ordinal) {
        return this.parameters.get(ordinal);
    }

    public Parameter get(Object key) {
        return (Parameter) this.parameters.get(key);
    }

    public List<Parameter> getAll() {
        return this.parameters.getAll();
    }

    public void add(Parameter parameter) {
        Object key = parameter.getKey();
        if (key != null) {
            this.parameters.put(key, parameter);
        }
    }

    public void set(Object key, Parameter parameter) {
        this.parameters.put(key, parameter);
    }

    public Object create() throws Exception {
        if (!this.factory.isAccessible()) {
            this.factory.setAccessible(true);
        }
        return this.factory.newInstance(new Object[0]);
    }

    public Object create(Object[] list) throws Exception {
        if (!this.factory.isAccessible()) {
            this.factory.setAccessible(true);
        }
        return this.factory.newInstance(list);
    }

    public Signature copy() throws Exception {
        Signature signature = new Signature(this);
        Iterator i$ = iterator();
        while (i$.hasNext()) {
            signature.add(i$.next());
        }
        return signature;
    }

    public Class getType() {
        return this.type;
    }

    public String toString() {
        return this.factory.toString();
    }
}
