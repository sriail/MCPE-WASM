package org.simpleframework.xml.core;

import java.util.Collection;
import java.util.Collections;
import org.simpleframework.xml.strategy.Type;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;
import org.simpleframework.xml.stream.Style;

class CompositeListUnion implements Repeater {
    private final Context context;
    private final LabelMap elements;
    private final Group group;
    private final Expression path;
    private final Style style;
    private final Type type;

    public CompositeListUnion(Context context2, Group group2, Expression path2, Type type2) throws Exception {
        this.elements = group2.getElements();
        this.style = context2.getStyle();
        this.context = context2;
        this.group = group2;
        this.type = type2;
        this.path = path2;
    }

    public Object read(InputNode node) throws Exception {
        if (this.group.getText() == null) {
            return readElement(node);
        }
        return readText(node);
    }

    private Object readElement(InputNode node) throws Exception {
        return ((Label) this.elements.get(this.path.getElement(node.getName()))).getConverter(this.context).read(node);
    }

    private Object readText(InputNode node) throws Exception {
        return this.group.getText().getConverter(this.context).read(node);
    }

    public Object read(InputNode node, Object value) throws Exception {
        Object result = readElement(node, value);
        if (this.group.getText() != null) {
            return readText(node, value);
        }
        return result;
    }

    private Object readElement(InputNode node, Object value) throws Exception {
        return ((Label) this.elements.get(this.path.getElement(node.getName()))).getConverter(this.context).read(node, value);
    }

    private Object readText(InputNode node, Object value) throws Exception {
        return this.group.getText().getConverter(this.context).read(node.getParent(), value);
    }

    public boolean validate(InputNode node) throws Exception {
        return ((Label) this.elements.get(this.path.getElement(node.getName()))).getConverter(this.context).validate(node);
    }

    public void write(OutputNode node, Object source) throws Exception {
        Collection list = (Collection) source;
        if (!this.group.isInline()) {
            write(node, list);
        } else if (!list.isEmpty()) {
            write(node, list);
        } else if (!node.isCommitted()) {
            node.remove();
        }
    }

    private void write(OutputNode node, Collection list) throws Exception {
        for (Object item : list) {
            if (item != null) {
                Class real = item.getClass();
                Label label = this.group.getLabel(real);
                if (label == null) {
                    throw new UnionException("Entry of %s not declared in %s with annotation %s", real, this.type, this.group);
                }
                write(node, item, label);
            }
        }
    }

    private void write(OutputNode node, Object item, Label label) throws Exception {
        Converter converter = label.getConverter(this.context);
        Collection list = Collections.singleton(item);
        if (!label.isInline()) {
            String root = this.style.getElement(label.getName());
            if (!node.isCommitted()) {
                node.setName(root);
            }
        }
        converter.write(node, list);
    }
}
