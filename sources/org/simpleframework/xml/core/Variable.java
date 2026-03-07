package org.simpleframework.xml.core;

import java.lang.annotation.Annotation;
import org.simpleframework.xml.strategy.Type;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;
import org.simpleframework.xml.stream.Position;

class Variable implements Label {
    private final Label label;
    private final Object value;

    public Variable(Label label2, Object value2) {
        this.label = label2;
        this.value = value2;
    }

    public Label getLabel(Class type) {
        return this;
    }

    public Type getType(Class type) throws Exception {
        return this.label.getType(type);
    }

    public String[] getNames() throws Exception {
        return this.label.getNames();
    }

    public String[] getPaths() throws Exception {
        return this.label.getPaths();
    }

    public Object getValue() {
        return this.value;
    }

    public Decorator getDecorator() throws Exception {
        return this.label.getDecorator();
    }

    public Converter getConverter(Context context) throws Exception {
        Converter reader = this.label.getConverter(context);
        return reader instanceof Adapter ? reader : new Adapter(reader, this.label, this.value);
    }

    public Object getEmpty(Context context) throws Exception {
        return this.label.getEmpty(context);
    }

    public Contact getContact() {
        return this.label.getContact();
    }

    public Type getDependent() throws Exception {
        return this.label.getDependent();
    }

    public Object getKey() throws Exception {
        return this.label.getKey();
    }

    public String getEntry() throws Exception {
        return this.label.getEntry();
    }

    public String getName() throws Exception {
        return this.label.getName();
    }

    public Annotation getAnnotation() {
        return this.label.getAnnotation();
    }

    public String getPath() throws Exception {
        return this.label.getPath();
    }

    public Expression getExpression() throws Exception {
        return this.label.getExpression();
    }

    public String getOverride() {
        return this.label.getOverride();
    }

    public Class getType() {
        return this.label.getType();
    }

    public boolean isData() {
        return this.label.isData();
    }

    public boolean isInline() {
        return this.label.isInline();
    }

    public boolean isAttribute() {
        return this.label.isAttribute();
    }

    public boolean isCollection() {
        return this.label.isCollection();
    }

    public boolean isRequired() {
        return this.label.isRequired();
    }

    public boolean isText() {
        return this.label.isText();
    }

    public boolean isTextList() {
        return this.label.isTextList();
    }

    public boolean isUnion() {
        return this.label.isUnion();
    }

    public String toString() {
        return this.label.toString();
    }

    private static class Adapter implements Repeater {
        private final Label label;
        private final Converter reader;
        private final Object value;

        public Adapter(Converter reader2, Label label2, Object value2) {
            this.reader = reader2;
            this.value = value2;
            this.label = label2;
        }

        public Object read(InputNode node) throws Exception {
            return read(node, this.value);
        }

        public Object read(InputNode node, Object value2) throws Exception {
            Position line = node.getPosition();
            String name = node.getName();
            if (this.reader instanceof Repeater) {
                return ((Repeater) this.reader).read(node, value2);
            }
            throw new PersistenceException("Element '%s' is already used with %s at %s", name, this.label, line);
        }

        public boolean validate(InputNode node) throws Exception {
            Position line = node.getPosition();
            String name = node.getName();
            if (this.reader instanceof Repeater) {
                return ((Repeater) this.reader).validate(node);
            }
            throw new PersistenceException("Element '%s' declared twice at %s", name, line);
        }

        public void write(OutputNode node, Object value2) throws Exception {
            write(node, value2);
        }
    }
}
