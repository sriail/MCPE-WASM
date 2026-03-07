package org.simpleframework.xml.core;

import org.simpleframework.xml.strategy.Type;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;
import org.simpleframework.xml.stream.Style;

class CompositeValue implements Converter {
    private final Context context;
    private final Entry entry;
    private final Traverser root;
    private final Style style;
    private final Type type;

    public CompositeValue(Context context2, Entry entry2, Type type2) throws Exception {
        this.root = new Traverser(context2);
        this.style = context2.getStyle();
        this.context = context2;
        this.entry = entry2;
        this.type = type2;
    }

    public Object read(InputNode node) throws Exception {
        InputNode next = node.getNext();
        Class expect = this.type.getType();
        if (next != null && !next.isEmpty()) {
            return this.root.read(next, expect);
        }
        return null;
    }

    public Object read(InputNode node, Object value) throws Exception {
        Class expect = this.type.getType();
        if (value == null) {
            return read(node);
        }
        throw new PersistenceException("Can not read value of %s for %s", expect, this.entry);
    }

    public boolean validate(InputNode node) throws Exception {
        Class expect = this.type.getType();
        String name = this.entry.getValue();
        if (name == null) {
            name = this.context.getName(expect);
        }
        return validate(node, name);
    }

    private boolean validate(InputNode node, String key) throws Exception {
        InputNode next = node.getNext(this.style.getElement(key));
        Class expect = this.type.getType();
        if (next != null && !next.isEmpty()) {
            return this.root.validate(next, expect);
        }
        return true;
    }

    public void write(OutputNode node, Object item) throws Exception {
        Class expect = this.type.getType();
        String key = this.entry.getValue();
        if (key == null) {
            key = this.context.getName(expect);
        }
        this.root.write(node, item, expect, this.style.getElement(key));
    }
}
