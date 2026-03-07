package org.simpleframework.xml.core;

import org.simpleframework.xml.strategy.Type;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;
import org.simpleframework.xml.stream.Style;

class PrimitiveKey implements Converter {
    private final Context context;
    private final Entry entry;
    private final PrimitiveFactory factory;
    private final Primitive root;
    private final Style style;
    private final Type type;

    public PrimitiveKey(Context context2, Entry entry2, Type type2) {
        this.factory = new PrimitiveFactory(context2, type2);
        this.root = new Primitive(context2, type2);
        this.style = context2.getStyle();
        this.context = context2;
        this.entry = entry2;
        this.type = type2;
    }

    public Object read(InputNode node) throws Exception {
        Class expect = this.type.getType();
        String name = this.entry.getKey();
        if (name == null) {
            name = this.context.getName(expect);
        }
        if (!this.entry.isAttribute()) {
            return readElement(node, name);
        }
        return readAttribute(node, name);
    }

    public Object read(InputNode node, Object value) throws Exception {
        Class expect = this.type.getType();
        if (value == null) {
            return read(node);
        }
        throw new PersistenceException("Can not read key of %s for %s", expect, this.entry);
    }

    private Object readAttribute(InputNode node, String key) throws Exception {
        InputNode child = node.getAttribute(this.style.getAttribute(key));
        if (child == null) {
            return null;
        }
        return this.root.read(child);
    }

    private Object readElement(InputNode node, String key) throws Exception {
        InputNode child = node.getNext(this.style.getElement(key));
        if (child == null) {
            return null;
        }
        return this.root.read(child);
    }

    public boolean validate(InputNode node) throws Exception {
        Class expect = this.type.getType();
        String name = this.entry.getKey();
        if (name == null) {
            name = this.context.getName(expect);
        }
        if (!this.entry.isAttribute()) {
            return validateElement(node, name);
        }
        return validateAttribute(node, name);
    }

    private boolean validateAttribute(InputNode node, String key) throws Exception {
        InputNode child = node.getAttribute(this.style.getElement(key));
        if (child == null) {
            return true;
        }
        return this.root.validate(child);
    }

    private boolean validateElement(InputNode node, String key) throws Exception {
        InputNode child = node.getNext(this.style.getElement(key));
        if (child == null) {
            return true;
        }
        return this.root.validate(child);
    }

    public void write(OutputNode node, Object item) throws Exception {
        if (!this.entry.isAttribute()) {
            writeElement(node, item);
        } else if (item != null) {
            writeAttribute(node, item);
        }
    }

    private void writeElement(OutputNode node, Object item) throws Exception {
        Class expect = this.type.getType();
        String key = this.entry.getKey();
        if (key == null) {
            key = this.context.getName(expect);
        }
        OutputNode child = node.getChild(this.style.getElement(key));
        if (item != null && !isOverridden(child, item)) {
            this.root.write(child, item);
        }
    }

    private void writeAttribute(OutputNode node, Object item) throws Exception {
        Class expect = this.type.getType();
        String text = this.factory.getText(item);
        String key = this.entry.getKey();
        if (key == null) {
            key = this.context.getName(expect);
        }
        String name = this.style.getAttribute(key);
        if (text != null) {
            node.setAttribute(name, text);
        }
    }

    private boolean isOverridden(OutputNode node, Object value) throws Exception {
        return this.factory.setOverride(this.type, value, node);
    }
}
