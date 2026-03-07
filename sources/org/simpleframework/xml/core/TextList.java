package org.simpleframework.xml.core;

import java.util.Collection;
import org.simpleframework.xml.strategy.Type;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;

class TextList implements Repeater {
    private final CollectionFactory factory;
    private final Primitive primitive;
    private final Type type = new ClassType(String.class);

    public TextList(Context context, Type list, Label label) {
        this.factory = new CollectionFactory(context, list);
        this.primitive = new Primitive(context, this.type);
    }

    public Object read(InputNode node) throws Exception {
        Instance value = this.factory.getInstance(node);
        Object data = value.getInstance();
        if (value.isReference()) {
            return value.getInstance();
        }
        return read(node, data);
    }

    public Object read(InputNode node, Object result) throws Exception {
        Collection list = (Collection) result;
        Object value = this.primitive.read(node);
        if (value != null) {
            list.add(value);
        }
        return result;
    }

    public boolean validate(InputNode node) throws Exception {
        return true;
    }

    public void write(OutputNode node, Object object) throws Exception {
        OutputNode parent = node.getParent();
        for (Object item : (Collection) object) {
            this.primitive.write(parent, item);
        }
    }
}
