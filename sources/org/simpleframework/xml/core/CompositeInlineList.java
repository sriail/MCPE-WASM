package org.simpleframework.xml.core;

import java.util.Collection;
import org.simpleframework.xml.strategy.Type;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;

class CompositeInlineList implements Repeater {
    private final Type entry;
    private final CollectionFactory factory;
    private final String name;
    private final Traverser root;
    private final Type type;

    public CompositeInlineList(Context context, Type type2, Type entry2, String name2) {
        this.factory = new CollectionFactory(context, type2);
        this.root = new Traverser(context);
        this.entry = entry2;
        this.type = type2;
        this.name = name2;
    }

    public Object read(InputNode node) throws Exception {
        Collection list = (Collection) this.factory.getInstance();
        if (list != null) {
            return read(node, list);
        }
        return null;
    }

    public Object read(InputNode node, Object value) throws Exception {
        Collection list = (Collection) value;
        if (list != null) {
            return read(node, list);
        }
        return read(node);
    }

    private Object read(InputNode node, Collection list) throws Exception {
        InputNode from = node.getParent();
        String name2 = node.getName();
        while (node != null) {
            Object item = read(node, this.entry.getType());
            if (item != null) {
                list.add(item);
            }
            node = from.getNext(name2);
        }
        return list;
    }

    private Object read(InputNode node, Class expect) throws Exception {
        Object item = this.root.read(node, expect);
        Class result = item.getClass();
        if (this.entry.getType().isAssignableFrom(result)) {
            return item;
        }
        throw new PersistenceException("Entry %s does not match %s for %s", result, this.entry, this.type);
    }

    public boolean validate(InputNode node) throws Exception {
        InputNode from = node.getParent();
        Class type2 = this.entry.getType();
        String name2 = node.getName();
        while (node != null) {
            if (!this.root.validate(node, type2)) {
                return false;
            }
            node = from.getNext(name2);
        }
        return true;
    }

    public void write(OutputNode node, Object source) throws Exception {
        Collection list = (Collection) source;
        OutputNode parent = node.getParent();
        if (!node.isCommitted()) {
            node.remove();
        }
        write(parent, list);
    }

    public void write(OutputNode node, Collection list) throws Exception {
        for (Object item : list) {
            if (item != null) {
                Class expect = this.entry.getType();
                Class actual = item.getClass();
                if (!expect.isAssignableFrom(actual)) {
                    throw new PersistenceException("Entry %s does not match %s for %s", actual, expect, this.type);
                }
                this.root.write(node, item, expect, this.name);
            }
        }
    }
}
