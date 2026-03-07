package org.simpleframework.xml.core;

import org.simpleframework.xml.strategy.Type;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;

class CompositeUnion implements Converter {
    private final Context context;
    private final LabelMap elements;
    private final Group group;
    private final Expression path;
    private final Type type;

    public CompositeUnion(Context context2, Group group2, Expression path2, Type type2) throws Exception {
        this.elements = group2.getElements();
        this.context = context2;
        this.group = group2;
        this.type = type2;
        this.path = path2;
    }

    public Object read(InputNode node) throws Exception {
        return ((Label) this.elements.get(this.path.getElement(node.getName()))).getConverter(this.context).read(node);
    }

    public Object read(InputNode node, Object value) throws Exception {
        return ((Label) this.elements.get(this.path.getElement(node.getName()))).getConverter(this.context).read(node, value);
    }

    public boolean validate(InputNode node) throws Exception {
        return ((Label) this.elements.get(this.path.getElement(node.getName()))).getConverter(this.context).validate(node);
    }

    public void write(OutputNode node, Object object) throws Exception {
        Class real = object.getClass();
        Label label = this.group.getLabel(real);
        if (label == null) {
            throw new UnionException("Value of %s not declared in %s with annotation %s", real, this.type, this.group);
        } else {
            write(node, object, label);
        }
    }

    private void write(OutputNode node, Object object, Label label) throws Exception {
        label.getConverter(this.context).write(node, object);
    }
}
