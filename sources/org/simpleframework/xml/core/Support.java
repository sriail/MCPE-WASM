package org.simpleframework.xml.core;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.simpleframework.xml.DefaultType;
import org.simpleframework.xml.filter.Filter;
import org.simpleframework.xml.filter.PlatformFilter;
import org.simpleframework.xml.strategy.Value;
import org.simpleframework.xml.stream.Format;
import org.simpleframework.xml.stream.Style;
import org.simpleframework.xml.transform.Matcher;
import org.simpleframework.xml.transform.Transform;
import org.simpleframework.xml.transform.Transformer;

class Support implements Filter {
    private final DetailExtractor defaults;
    private final DetailExtractor details;
    private final Filter filter;
    private final Format format;
    private final InstanceFactory instances;
    private final LabelExtractor labels;
    private final Matcher matcher;
    private final ScannerFactory scanners;
    private final Transformer transform;

    public Support() {
        this(new PlatformFilter());
    }

    public Support(Filter filter2) {
        this(filter2, new EmptyMatcher());
    }

    public Support(Filter filter2, Matcher matcher2) {
        this(filter2, matcher2, new Format());
    }

    public Support(Filter filter2, Matcher matcher2, Format format2) {
        this.defaults = new DetailExtractor(this, DefaultType.FIELD);
        this.transform = new Transformer(matcher2);
        this.scanners = new ScannerFactory(this);
        this.details = new DetailExtractor(this);
        this.labels = new LabelExtractor(format2);
        this.instances = new InstanceFactory();
        this.matcher = matcher2;
        this.filter = filter2;
        this.format = format2;
    }

    public String replace(String text) {
        return this.filter.replace(text);
    }

    public Style getStyle() {
        return this.format.getStyle();
    }

    public Format getFormat() {
        return this.format;
    }

    public Instance getInstance(Value value) {
        return this.instances.getInstance(value);
    }

    public Instance getInstance(Class type) {
        return this.instances.getInstance(type);
    }

    public Transform getTransform(Class type) throws Exception {
        return this.matcher.match(type);
    }

    public Label getLabel(Contact contact, Annotation label) throws Exception {
        return this.labels.getLabel(contact, label);
    }

    public List<Label> getLabels(Contact contact, Annotation label) throws Exception {
        return this.labels.getList(contact, label);
    }

    public Detail getDetail(Class type) {
        return getDetail(type, (DefaultType) null);
    }

    public Detail getDetail(Class type, DefaultType access) {
        if (access != null) {
            return this.defaults.getDetail(type);
        }
        return this.details.getDetail(type);
    }

    public ContactList getFields(Class type) throws Exception {
        return getFields(type, (DefaultType) null);
    }

    public ContactList getFields(Class type, DefaultType access) throws Exception {
        if (access != null) {
            return this.defaults.getFields(type);
        }
        return this.details.getFields(type);
    }

    public ContactList getMethods(Class type) throws Exception {
        return getMethods(type, (DefaultType) null);
    }

    public ContactList getMethods(Class type, DefaultType access) throws Exception {
        if (access != null) {
            return this.defaults.getMethods(type);
        }
        return this.details.getMethods(type);
    }

    public Scanner getScanner(Class type) throws Exception {
        return this.scanners.getInstance(type);
    }

    public Object read(String value, Class type) throws Exception {
        return this.transform.read(value, type);
    }

    public String write(Object value, Class type) throws Exception {
        return this.transform.write(value, type);
    }

    public boolean valid(Class type) throws Exception {
        return this.transform.valid(type);
    }

    public String getName(Class type) throws Exception {
        String name = getScanner(type).getName();
        return name != null ? name : getClassName(type);
    }

    private String getClassName(Class type) throws Exception {
        if (type.isArray()) {
            type = type.getComponentType();
        }
        String name = type.getSimpleName();
        return type.isPrimitive() ? name : Reflector.getName(name);
    }

    public boolean isPrimitive(Class type) throws Exception {
        if (type == String.class || type == Float.class || type == Double.class || type == Long.class || type == Integer.class || type == Boolean.class || type.isEnum() || type.isPrimitive()) {
            return true;
        }
        return this.transform.valid(type);
    }

    public boolean isContainer(Class type) {
        if (!Collection.class.isAssignableFrom(type) && !Map.class.isAssignableFrom(type)) {
            return type.isArray();
        }
        return true;
    }

    public static boolean isFloat(Class type) throws Exception {
        if (type == Double.class || type == Float.class || type == Float.TYPE || type == Double.TYPE) {
            return true;
        }
        return false;
    }

    public static boolean isAssignable(Class expect, Class actual) {
        if (expect.isPrimitive()) {
            expect = getPrimitive(expect);
        }
        if (actual.isPrimitive()) {
            actual = getPrimitive(actual);
        }
        return actual.isAssignableFrom(expect);
    }

    public static Class getPrimitive(Class type) {
        if (type == Double.TYPE) {
            return Double.class;
        }
        if (type == Float.TYPE) {
            return Float.class;
        }
        if (type == Integer.TYPE) {
            return Integer.class;
        }
        if (type == Long.TYPE) {
            return Long.class;
        }
        if (type == Boolean.TYPE) {
            return Boolean.class;
        }
        if (type == Character.TYPE) {
            return Character.class;
        }
        if (type == Short.TYPE) {
            return Short.class;
        }
        if (type == Byte.TYPE) {
            return Byte.class;
        }
        return type;
    }
}
