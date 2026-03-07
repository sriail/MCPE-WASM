package org.simpleframework.xml.core;

import java.lang.annotation.Annotation;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.strategy.Type;
import org.simpleframework.xml.stream.Format;
import org.simpleframework.xml.stream.Style;

class ElementListLabel extends TemplateLabel {
    private Expression cache;
    private boolean data;
    private Decorator decorator;
    private Introspector detail;
    private String entry;
    private Format format;
    private boolean inline;
    private Class item;
    private ElementList label;
    private String name;
    private String override;
    private String path;
    private boolean required;
    private Class type;

    public ElementListLabel(Contact contact, ElementList label2, Format format2) {
        this.detail = new Introspector(contact, this, format2);
        this.decorator = new Qualifier(contact);
        this.required = label2.required();
        this.type = contact.getType();
        this.override = label2.name();
        this.inline = label2.inline();
        this.entry = label2.entry();
        this.data = label2.data();
        this.item = label2.type();
        this.format = format2;
        this.label = label2;
    }

    public Decorator getDecorator() throws Exception {
        return this.decorator;
    }

    public Converter getConverter(Context context) throws Exception {
        String entry2 = getEntry();
        if (!this.label.inline()) {
            return getConverter(context, entry2);
        }
        return getInlineConverter(context, entry2);
    }

    private Converter getConverter(Context context, String name2) throws Exception {
        Type item2 = getDependent();
        Type type2 = getContact();
        if (!context.isPrimitive(item2)) {
            return new CompositeList(context, type2, item2, name2);
        }
        return new PrimitiveList(context, type2, item2, name2);
    }

    private Converter getInlineConverter(Context context, String name2) throws Exception {
        Type item2 = getDependent();
        Type type2 = getContact();
        if (!context.isPrimitive(item2)) {
            return new CompositeInlineList(context, type2, item2, name2);
        }
        return new PrimitiveInlineList(context, type2, item2, name2);
    }

    public Object getEmpty(Context context) throws Exception {
        Factory factory = new CollectionFactory(context, new ClassType(this.type));
        if (!this.label.empty()) {
            return factory.getInstance();
        }
        return null;
    }

    public Type getDependent() throws Exception {
        Contact contact = getContact();
        if (this.item == Void.TYPE) {
            this.item = contact.getDependent();
        }
        if (this.item != null) {
            return new ClassType(this.item);
        }
        throw new ElementException("Unable to determine generic type for %s", contact);
    }

    public String getEntry() throws Exception {
        Style style = this.format.getStyle();
        if (this.detail.isEmpty(this.entry)) {
            this.entry = this.detail.getEntry();
        }
        return style.getElement(this.entry);
    }

    public String getName() throws Exception {
        if (this.name == null) {
            this.name = this.format.getStyle().getElement(this.detail.getName());
        }
        return this.name;
    }

    public String getPath() throws Exception {
        if (this.path == null) {
            this.path = getExpression().getElement(getName());
        }
        return this.path;
    }

    public Expression getExpression() throws Exception {
        if (this.cache == null) {
            this.cache = this.detail.getExpression();
        }
        return this.cache;
    }

    public Annotation getAnnotation() {
        return this.label;
    }

    public Class getType() {
        return this.type;
    }

    public Contact getContact() {
        return this.detail.getContact();
    }

    public String getOverride() {
        return this.override;
    }

    public boolean isData() {
        return this.data;
    }

    public boolean isCollection() {
        return true;
    }

    public boolean isRequired() {
        return this.required;
    }

    public boolean isInline() {
        return this.inline;
    }

    public String toString() {
        return this.detail.toString();
    }
}
