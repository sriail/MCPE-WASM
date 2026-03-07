package org.simpleframework.xml.core;

import java.lang.annotation.Annotation;
import org.simpleframework.xml.Text;
import org.simpleframework.xml.strategy.Type;
import org.simpleframework.xml.stream.Format;

class TextLabel extends TemplateLabel {
    private Contact contact;
    private boolean data;
    private Introspector detail;
    private String empty;
    private Text label;
    private Expression path;
    private boolean required;
    private Class type;

    public TextLabel(Contact contact2, Text label2, Format format) {
        this.detail = new Introspector(contact2, this, format);
        this.required = label2.required();
        this.type = contact2.getType();
        this.empty = label2.empty();
        this.data = label2.data();
        this.contact = contact2;
        this.label = label2;
    }

    public Decorator getDecorator() throws Exception {
        return null;
    }

    public Converter getConverter(Context context) throws Exception {
        String ignore = getEmpty(context);
        Type type2 = getContact();
        if (context.isPrimitive(type2)) {
            return new Primitive(context, type2, ignore);
        }
        throw new TextException("Cannot use %s to represent %s", type2, this.label);
    }

    public String getEmpty(Context context) {
        if (this.detail.isEmpty(this.empty)) {
            return null;
        }
        return this.empty;
    }

    public String getPath() throws Exception {
        return getExpression().getPath();
    }

    public Expression getExpression() throws Exception {
        if (this.path == null) {
            this.path = this.detail.getExpression();
        }
        return this.path;
    }

    public Annotation getAnnotation() {
        return this.label;
    }

    public Contact getContact() {
        return this.contact;
    }

    public String getName() {
        return "";
    }

    public String getOverride() {
        return this.contact.toString();
    }

    public Class getType() {
        return this.type;
    }

    public boolean isRequired() {
        return this.required;
    }

    public boolean isData() {
        return this.data;
    }

    public boolean isText() {
        return true;
    }

    public boolean isInline() {
        return true;
    }

    public String toString() {
        return this.detail.toString();
    }
}
