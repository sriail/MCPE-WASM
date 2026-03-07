package org.simpleframework.xml.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.ElementListUnion;
import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.ElementMapUnion;
import org.simpleframework.xml.ElementUnion;
import org.simpleframework.xml.stream.Format;

class ExtractorFactory {
    private final Contact contact;
    private final Format format;
    private final Annotation label;

    public ExtractorFactory(Contact contact2, Annotation label2, Format format2) {
        this.contact = contact2;
        this.format = format2;
        this.label = label2;
    }

    public Extractor getInstance() throws Exception {
        return (Extractor) getInstance(this.label);
    }

    private Object getInstance(Annotation label2) throws Exception {
        Constructor factory = getBuilder(label2).getConstructor();
        if (!factory.isAccessible()) {
            factory.setAccessible(true);
        }
        return factory.newInstance(new Object[]{this.contact, label2, this.format});
    }

    private ExtractorBuilder getBuilder(Annotation label2) throws Exception {
        if (label2 instanceof ElementUnion) {
            return new ExtractorBuilder(ElementUnion.class, ElementExtractor.class);
        }
        if (label2 instanceof ElementListUnion) {
            return new ExtractorBuilder(ElementListUnion.class, ElementListExtractor.class);
        }
        if (label2 instanceof ElementMapUnion) {
            return new ExtractorBuilder(ElementMapUnion.class, ElementMapExtractor.class);
        }
        throw new PersistenceException("Annotation %s is not a union", label2);
    }

    private static class ExtractorBuilder {
        private final Class label;
        private final Class type;

        public ExtractorBuilder(Class label2, Class type2) {
            this.label = label2;
            this.type = type2;
        }

        /* access modifiers changed from: private */
        public Constructor getConstructor() throws Exception {
            return this.type.getConstructor(new Class[]{Contact.class, this.label, Format.class});
        }
    }

    private static class ElementExtractor implements Extractor<Element> {
        private final Contact contact;
        private final Format format;
        private final ElementUnion union;

        public ElementExtractor(Contact contact2, ElementUnion union2, Format format2) throws Exception {
            this.contact = contact2;
            this.format = format2;
            this.union = union2;
        }

        public Element[] getAnnotations() {
            return this.union.value();
        }

        public Label getLabel(Element element) {
            return new ElementLabel(this.contact, element, this.format);
        }

        public Class getType(Element element) {
            Class type = element.type();
            if (type == Void.TYPE) {
                return this.contact.getType();
            }
            return type;
        }
    }

    private static class ElementListExtractor implements Extractor<ElementList> {
        private final Contact contact;
        private final Format format;
        private final ElementListUnion union;

        public ElementListExtractor(Contact contact2, ElementListUnion union2, Format format2) throws Exception {
            this.contact = contact2;
            this.format = format2;
            this.union = union2;
        }

        public ElementList[] getAnnotations() {
            return this.union.value();
        }

        public Label getLabel(ElementList element) {
            return new ElementListLabel(this.contact, element, this.format);
        }

        public Class getType(ElementList element) {
            return element.type();
        }
    }

    private static class ElementMapExtractor implements Extractor<ElementMap> {
        private final Contact contact;
        private final Format format;
        private final ElementMapUnion union;

        public ElementMapExtractor(Contact contact2, ElementMapUnion union2, Format format2) throws Exception {
            this.contact = contact2;
            this.format = format2;
            this.union = union2;
        }

        public ElementMap[] getAnnotations() {
            return this.union.value();
        }

        public Label getLabel(ElementMap element) {
            return new ElementMapLabel(this.contact, element, this.format);
        }

        public Class getType(ElementMap element) {
            return element.valueType();
        }
    }
}
