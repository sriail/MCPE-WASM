package org.simpleframework.xml.core;

import java.lang.reflect.Array;
import org.simpleframework.xml.strategy.Type;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;
import org.simpleframework.xml.stream.Position;

class PrimitiveArray implements Converter {
    private final Type entry;
    private final ArrayFactory factory;
    private final String parent;
    private final Primitive root;
    private final Type type;

    public PrimitiveArray(Context context, Type type2, Type entry2, String parent2) {
        this.factory = new ArrayFactory(context, type2);
        this.root = new Primitive(context, entry2);
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
            Array.set(list, pos, this.root.read(next));
            pos++;
        }
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
            this.root.validate(next);
        }
    }

    public void write(OutputNode node, Object source) throws Exception {
        int size = Array.getLength(source);
        int i = 0;
        while (i < size) {
            OutputNode child = node.getChild(this.parent);
            if (child != null) {
                write(child, source, i);
                i++;
            } else {
                return;
            }
        }
    }

    private void write(OutputNode node, Object source, int index) throws Exception {
        Object item = Array.get(source, index);
        if (item != null && !isOverridden(node, item)) {
            this.root.write(node, item);
        }
    }

    private boolean isOverridden(OutputNode node, Object value) throws Exception {
        return this.factory.setOverride(this.entry, value, node);
    }
}
