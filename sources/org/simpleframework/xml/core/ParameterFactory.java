package org.simpleframework.xml.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementArray;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.ElementListUnion;
import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.ElementMapUnion;
import org.simpleframework.xml.ElementUnion;
import org.simpleframework.xml.Text;
import org.simpleframework.xml.stream.Format;

class ParameterFactory {
    private final Format format;

    public ParameterFactory(Support support) {
        this.format = support.getFormat();
    }

    public Parameter getInstance(Constructor factory, Annotation label, int index) throws Exception {
        return getInstance(factory, label, (Annotation) null, index);
    }

    public Parameter getInstance(Constructor factory, Annotation label, Annotation entry, int index) throws Exception {
        Constructor builder = getConstructor(label);
        if (entry != null) {
            return (Parameter) builder.newInstance(new Object[]{factory, label, entry, this.format, Integer.valueOf(index)});
        }
        return (Parameter) builder.newInstance(new Object[]{factory, label, this.format, Integer.valueOf(index)});
    }

    private Constructor getConstructor(Annotation label) throws Exception {
        Constructor factory = getBuilder(label).getConstructor();
        if (!factory.isAccessible()) {
            factory.setAccessible(true);
        }
        return factory;
    }

    private ParameterBuilder getBuilder(Annotation label) throws Exception {
        if (label instanceof Element) {
            return new ParameterBuilder(ElementParameter.class, Element.class);
        }
        if (label instanceof ElementList) {
            return new ParameterBuilder(ElementListParameter.class, ElementList.class);
        }
        if (label instanceof ElementArray) {
            return new ParameterBuilder(ElementArrayParameter.class, ElementArray.class);
        }
        if (label instanceof ElementMapUnion) {
            return new ParameterBuilder(ElementMapUnionParameter.class, ElementMapUnion.class, ElementMap.class);
        }
        if (label instanceof ElementListUnion) {
            return new ParameterBuilder(ElementListUnionParameter.class, ElementListUnion.class, ElementList.class);
        }
        if (label instanceof ElementUnion) {
            return new ParameterBuilder(ElementUnionParameter.class, ElementUnion.class, Element.class);
        }
        if (label instanceof ElementMap) {
            return new ParameterBuilder(ElementMapParameter.class, ElementMap.class);
        }
        if (label instanceof Attribute) {
            return new ParameterBuilder(AttributeParameter.class, Attribute.class);
        }
        if (label instanceof Text) {
            return new ParameterBuilder(TextParameter.class, Text.class);
        }
        throw new PersistenceException("Annotation %s not supported", label);
    }

    private static class ParameterBuilder {
        private final Class entry;
        private final Class label;
        private final Class type;

        public ParameterBuilder(Class type2, Class label2) {
            this(type2, label2, (Class) null);
        }

        public ParameterBuilder(Class type2, Class label2, Class entry2) {
            this.label = label2;
            this.entry = entry2;
            this.type = type2;
        }

        public Constructor getConstructor() throws Exception {
            if (this.entry != null) {
                return getConstructor(this.label, this.entry);
            }
            return getConstructor(this.label);
        }

        public Constructor getConstructor(Class label2) throws Exception {
            return getConstructor(Constructor.class, label2, Format.class, Integer.TYPE);
        }

        public Constructor getConstructor(Class label2, Class entry2) throws Exception {
            return getConstructor(Constructor.class, label2, entry2, Format.class, Integer.TYPE);
        }

        private Constructor getConstructor(Class... types) throws Exception {
            return this.type.getConstructor(types);
        }
    }
}
