package org.simpleframework.xml.core;

import java.util.Iterator;
import java.util.LinkedHashMap;

class Collector implements Criteria {
    private final Registry alias = new Registry();
    private final Registry registry = new Registry();

    public Variable get(Object key) {
        return (Variable) this.registry.get(key);
    }

    public Variable get(Label label) throws Exception {
        if (label == null) {
            return null;
        }
        return (Variable) this.registry.get(label.getKey());
    }

    public Variable resolve(String path) {
        return (Variable) this.alias.get(path);
    }

    public Variable remove(Object key) throws Exception {
        return (Variable) this.registry.remove(key);
    }

    public Iterator<Object> iterator() {
        return this.registry.iterator();
    }

    public void set(Label label, Object value) throws Exception {
        Variable variable = new Variable(label, value);
        if (label != null) {
            String[] paths = label.getPaths();
            Object key = label.getKey();
            for (String path : paths) {
                this.alias.put(path, variable);
            }
            this.registry.put(key, variable);
        }
    }

    public void commit(Object source) throws Exception {
        for (Variable entry : this.registry.values()) {
            entry.getContact().set(source, entry.getValue());
        }
    }

    private static class Registry extends LinkedHashMap<Object, Variable> {
        private Registry() {
        }

        public Iterator<Object> iterator() {
            return keySet().iterator();
        }
    }
}
