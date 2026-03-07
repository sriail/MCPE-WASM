package org.simpleframework.xml.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.stream.Format;

class AttributeParameter extends TemplateParameter {
    private final Contact contact;
    private final Expression expression = this.label.getExpression();
    private final int index;
    private final Object key = this.label.getKey();
    private final Label label;
    private final String name = this.label.getName();
    private final String path = this.label.getPath();
    private final Class type = this.label.getType();

    public AttributeParameter(Constructor factory, Attribute value, Format format, int index2) throws Exception {
        this.contact = new Contact(value, factory, index2);
        this.label = new AttributeLabel(this.contact, value, format);
        this.index = index2;
    }

    public Object getKey() {
        return this.key;
    }

    public String getPath() {
        return this.path;
    }

    public String getName() {
        return this.name;
    }

    public Expression getExpression() {
        return this.expression;
    }

    public Class getType() {
        return this.type;
    }

    public Annotation getAnnotation() {
        return this.contact.getAnnotation();
    }

    public int getIndex() {
        return this.index;
    }

    public boolean isRequired() {
        return this.label.isRequired();
    }

    public boolean isPrimitive() {
        return this.type.isPrimitive();
    }

    public boolean isAttribute() {
        return true;
    }

    public String toString() {
        return this.contact.toString();
    }

    private static class Contact extends ParameterContact<Attribute> {
        public Contact(Attribute label, Constructor factory, int index) {
            super(label, factory, index);
        }

        public String getName() {
            return ((Attribute) this.label).name();
        }
    }
}
