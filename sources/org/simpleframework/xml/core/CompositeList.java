package org.simpleframework.xml.core;

import java.util.Collection;
import org.simpleframework.xml.strategy.Type;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;

class CompositeList implements Converter {
    private final Type entry;
    private final CollectionFactory factory;
    private final String name;
    private final Traverser root;
    private final Type type;

    public CompositeList(Context context, Type type2, Type entry2, String name2) {
        this.factory = new CollectionFactory(context, type2);
        this.root = new Traverser(context);
        this.entry = entry2;
        this.type = type2;
        this.name = name2;
    }

    public Object read(InputNode node) throws Exception {
        Instance type2 = this.factory.getInstance(node);
        Object list = type2.getInstance();
        if (!type2.isReference()) {
            return populate(node, list);
        }
        return list;
    }

    public Object read(InputNode node, Object result) throws Exception {
        Instance type2 = this.factory.getInstance(node);
        if (type2.isReference()) {
            return type2.getInstance();
        }
        type2.setInstance(result);
        if (result != null) {
            return populate(node, result);
        }
        return result;
    }

    private Object populate(InputNode node, Object result) throws Exception {
        Collection list = (Collection) result;
        while (true) {
            InputNode next = node.getNext();
            Class expect = this.entry.getType();
            if (next == null) {
                return list;
            }
            list.add(this.root.read(next, expect));
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
            Class expect = this.entry.getType();
            if (next == null) {
                return true;
            }
            this.root.validate(next, expect);
        }
    }

    public void write(OutputNode node, Object source) throws Exception {
        for (Object item : (Collection) source) {
            if (item != null) {
                Class expect = this.entry.getType();
                Class actual = item.getClass();
                if (!expect.isAssignableFrom(actual)) {
                    throw new PersistenceException("Entry %s does not match %s for %s", actual, this.entry, this.type);
                }
                this.root.write(node, item, expect, this.name);
            }
        }
    }
}
