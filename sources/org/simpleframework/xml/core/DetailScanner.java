package org.simpleframework.xml.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;
import org.simpleframework.xml.Default;
import org.simpleframework.xml.DefaultType;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.NamespaceList;
import org.simpleframework.xml.Order;
import org.simpleframework.xml.Root;

class DetailScanner implements Detail {
    private DefaultType access;
    private NamespaceList declaration;
    private List<FieldDetail> fields;
    private Annotation[] labels;
    private List<MethodDetail> methods;
    private String name;
    private Namespace namespace;
    private Order order;
    private DefaultType override;
    private boolean required;
    private Root root;
    private boolean strict;
    private Class type;

    public DetailScanner(Class type2) {
        this(type2, (DefaultType) null);
    }

    public DetailScanner(Class type2, DefaultType override2) {
        this.methods = new LinkedList();
        this.fields = new LinkedList();
        this.labels = type2.getDeclaredAnnotations();
        this.override = override2;
        this.strict = true;
        this.type = type2;
        scan(type2);
    }

    public boolean isRequired() {
        return this.required;
    }

    public boolean isStrict() {
        return this.strict;
    }

    public boolean isPrimitive() {
        return this.type.isPrimitive();
    }

    public boolean isInstantiable() {
        if (!Modifier.isStatic(this.type.getModifiers()) && this.type.isMemberClass()) {
            return false;
        }
        return true;
    }

    public Root getRoot() {
        return this.root;
    }

    public String getName() {
        return this.name;
    }

    public Class getType() {
        return this.type;
    }

    public Order getOrder() {
        return this.order;
    }

    public DefaultType getOverride() {
        return this.override;
    }

    public DefaultType getAccess() {
        if (this.override != null) {
            return this.override;
        }
        return this.access;
    }

    public Namespace getNamespace() {
        return this.namespace;
    }

    public NamespaceList getNamespaceList() {
        return this.declaration;
    }

    public List<MethodDetail> getMethods() {
        return this.methods;
    }

    public List<FieldDetail> getFields() {
        return this.fields;
    }

    public Annotation[] getAnnotations() {
        return this.labels;
    }

    public Constructor[] getConstructors() {
        return this.type.getDeclaredConstructors();
    }

    public Class getSuper() {
        Class base = this.type.getSuperclass();
        if (base == Object.class) {
            return null;
        }
        return base;
    }

    private void scan(Class type2) {
        methods(type2);
        fields(type2);
        extract(type2);
    }

    private void extract(Class type2) {
        for (Annotation label : this.labels) {
            if (label instanceof Namespace) {
                namespace(label);
            }
            if (label instanceof NamespaceList) {
                scope(label);
            }
            if (label instanceof Root) {
                root(label);
            }
            if (label instanceof Order) {
                order(label);
            }
            if (label instanceof Default) {
                access(label);
            }
        }
    }

    private void methods(Class type2) {
        for (Method method : type2.getDeclaredMethods()) {
            this.methods.add(new MethodDetail(method));
        }
    }

    private void fields(Class type2) {
        for (Field field : type2.getDeclaredFields()) {
            this.fields.add(new FieldDetail(field));
        }
    }

    private void root(Annotation label) {
        if (label != null) {
            Root value = (Root) label;
            String real = this.type.getSimpleName();
            String str = real;
            if (value != null) {
                String text = value.name();
                if (isEmpty(text)) {
                    text = Reflector.getName(real);
                }
                this.strict = value.strict();
                this.root = value;
                this.name = text;
            }
        }
    }

    private boolean isEmpty(String value) {
        return value.length() == 0;
    }

    private void order(Annotation label) {
        if (label != null) {
            this.order = (Order) label;
        }
    }

    private void access(Annotation label) {
        if (label != null) {
            Default value = (Default) label;
            this.required = value.required();
            this.access = value.value();
        }
    }

    private void namespace(Annotation label) {
        if (label != null) {
            this.namespace = (Namespace) label;
        }
    }

    private void scope(Annotation label) {
        if (label != null) {
            this.declaration = (NamespaceList) label;
        }
    }

    public String toString() {
        return this.type.toString();
    }
}
