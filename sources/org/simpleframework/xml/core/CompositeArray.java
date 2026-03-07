package org.simpleframework.xml.core;

import java.lang.reflect.Array;
import org.simpleframework.xml.strategy.Type;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;
import org.simpleframework.xml.stream.Position;

class CompositeArray implements Converter {
    private final Type entry;
    private final ArrayFactory factory;
    private final String parent;
    private final Traverser root;
    private final Type type;

    public CompositeArray(Context context, Type type2, Type entry2, String parent2) {
        this.factory = new ArrayFactory(context, type2);
        this.root = new Traverser(context);
        this.parent = parent2;
        this.entry = entry2;
        this.type = type2;
    }

    public Object read(InputNode node) throws Exception {
        Instance type2 = this.factory.getInstance(node);
        Object list = type2.getInstance();
        if (!type2.isReference()) {
            return read(node, list);
        }
        return list;
    }

    public Object read(InputNode node, Object list) throws Exception {
        int length = Array.getLength(list);
        int pos = 0;
        while (true) {
            Position line = node.getPosition();
            InputNode next = node.getNext();
            if (next == null) {
                return list;
            }
            if (pos >= length) {
                throw new ElementException("Array length missing or incorrect for %s at %s", this.type, line);
            }
            read(next, list, pos);
            pos++;
        }
    }

    private void read(InputNode node, Object list, int index) throws Exception {
        Class type2 = this.entry.getType();
        Object value = null;
        if (!node.isEmpty()) {
            value = this.root.read(node, type2);
        }
        Array.set(list, index, value);
    }

    public boolean validate(InputNode node) throws Exception {
        Instance value = this.factory.getInstance(node);
        if (value.isReference()) {
            return true;
        }
        Object instance = value.setInstance((Object) null);
        return validate(node, value.getType());
    }

    private boolean validate(InputNode node, Class type2) throws Exception {
        while (true) {
            InputNode next = node.getNext();
            if (next == null) {
                return true;
            }
            if (!next.isEmpty()) {
                this.root.validate(next, type2);
            }
        }
    }

    public void write(OutputNode node, Object source) throws Exception {
        int size = Array.getLength(source);
        for (int i = 0; i < size; i++) {
            this.root.write(node, Array.get(source, i), this.entry.getType(), this.parent);
        }
        node.commit();
    }
}
