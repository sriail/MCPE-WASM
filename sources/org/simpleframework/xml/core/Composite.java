package org.simpleframework.xml.core;

import java.util.Iterator;
import org.simpleframework.xml.Version;
import org.simpleframework.xml.strategy.Type;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.NamespaceMap;
import org.simpleframework.xml.stream.NodeMap;
import org.simpleframework.xml.stream.OutputNode;
import org.simpleframework.xml.stream.Position;

class Composite implements Converter {
    private final Context context;
    private final Criteria criteria;
    private final ObjectFactory factory;
    private final Primitive primitive;
    private final Revision revision;
    private final Type type;

    public Composite(Context context2, Type type2) {
        this(context2, type2, (Class) null);
    }

    public Composite(Context context2, Type type2, Class override) {
        this.factory = new ObjectFactory(context2, type2, override);
        this.primitive = new Primitive(context2, type2);
        this.criteria = new Collector();
        this.revision = new Revision();
        this.context = context2;
        this.type = type2;
    }

    public Object read(InputNode node) throws Exception {
        Instance value = this.factory.getInstance(node);
        Class type2 = value.getType();
        if (value.isReference()) {
            return value.getInstance();
        }
        if (this.context.isPrimitive(type2)) {
            return readPrimitive(node, value);
        }
        return read(node, value, type2);
    }

    public Object read(InputNode node, Object source) throws Exception {
        Schema schema = this.context.getSchema(source.getClass());
        Caller caller = schema.getCaller();
        read(node, source, schema);
        this.criteria.commit(source);
        caller.validate(source);
        caller.commit(source);
        return readResolve(node, source, caller);
    }

    private Object read(InputNode node, Instance value, Class real) throws Exception {
        Schema schema = this.context.getSchema(real);
        Caller caller = schema.getCaller();
        Object source = read(schema, value).read(node);
        caller.validate(source);
        caller.commit(source);
        value.setInstance(source);
        return readResolve(node, source, caller);
    }

    private Builder read(Schema schema, Instance value) throws Exception {
        if (schema.getInstantiator().isDefault()) {
            return new Builder(this, this.criteria, schema, value);
        }
        return new Injector(this, this.criteria, schema, value);
    }

    private Object readPrimitive(InputNode node, Instance value) throws Exception {
        Class type2 = value.getType();
        Object result = this.primitive.read(node, type2);
        if (type2 != null) {
            value.setInstance(result);
        }
        return result;
    }

    private Object readResolve(InputNode node, Object source, Caller caller) throws Exception {
        if (source == null) {
            return source;
        }
        Position line = node.getPosition();
        Object value = caller.resolve(source);
        Class expect = this.type.getType();
        Class real = value.getClass();
        if (expect.isAssignableFrom(real)) {
            return value;
        }
        throw new ElementException("Type %s does not match %s at %s", real, expect, line);
    }

    private void read(InputNode node, Object source, Schema schema) throws Exception {
        Section section = schema.getSection();
        readVersion(node, source, schema);
        readSection(node, source, section);
    }

    private void readSection(InputNode node, Object source, Section section) throws Exception {
        readText(node, source, section);
        readAttributes(node, source, section);
        readElements(node, source, section);
    }

    /* access modifiers changed from: private */
    public void readVersion(InputNode node, Object source, Schema schema) throws Exception {
        Label label = schema.getVersion();
        Class expect = this.type.getType();
        if (label != null) {
            InputNode value = node.getAttributes().remove(label.getName());
            if (value != null) {
                readVersion(value, source, label);
                return;
            }
            Version version = this.context.getVersion(expect);
            Double start = Double.valueOf(this.revision.getDefault());
            Double expected = Double.valueOf(version.revision());
            this.criteria.set(label, start);
            this.revision.compare(expected, start);
        }
    }

    private void readVersion(InputNode node, Object source, Label label) throws Exception {
        Object value = readInstance(node, source, label);
        Class expect = this.type.getType();
        if (value != null) {
            Double actual = Double.valueOf(this.context.getVersion(expect).revision());
            if (!value.equals(this.revision)) {
                this.revision.compare(actual, value);
            }
        }
    }

    /* access modifiers changed from: private */
    public void readAttributes(InputNode node, Object source, Section section) throws Exception {
        NodeMap<InputNode> list = node.getAttributes();
        LabelMap map = section.getAttributes();
        Iterator i$ = list.iterator();
        while (i$.hasNext()) {
            InputNode value = node.getAttribute(i$.next());
            if (value != null) {
                readAttribute(value, source, section, map);
            }
        }
        validate(node, map, source);
    }

    /* access modifiers changed from: private */
    public void readElements(InputNode node, Object source, Section section) throws Exception {
        LabelMap map = section.getElements();
        InputNode child = node.getNext();
        while (child != null) {
            Section block = section.getSection(child.getName());
            if (block != null) {
                readSection(child, source, block);
            } else {
                readElement(child, source, section, map);
            }
            child = node.getNext();
        }
        validate(node, map, source);
    }

    /* access modifiers changed from: private */
    public void readText(InputNode node, Object source, Section section) throws Exception {
        Label label = section.getText();
        if (label != null) {
            readInstance(node, source, label);
        }
    }

    private void readAttribute(InputNode node, Object source, Section section, LabelMap map) throws Exception {
        String path = section.getAttribute(node.getName());
        Label label = map.getLabel(path);
        if (label == null) {
            Position line = node.getPosition();
            Class expect = this.context.getType(this.type, source);
            if (map.isStrict(this.context) && this.revision.isEqual()) {
                throw new AttributeException("Attribute '%s' does not have a match in %s at %s", path, expect, line);
            }
            return;
        }
        readInstance(node, source, label);
    }

    private void readElement(InputNode node, Object source, Section section, LabelMap map) throws Exception {
        String path = section.getPath(node.getName());
        Label label = map.getLabel(path);
        if (label == null) {
            label = this.criteria.resolve(path);
        }
        if (label == null) {
            Position line = node.getPosition();
            Class expect = this.context.getType(this.type, source);
            if (!map.isStrict(this.context) || !this.revision.isEqual()) {
                node.skip();
            } else {
                throw new ElementException("Element '%s' does not have a match in %s at %s", path, expect, line);
            }
        } else {
            readUnion(node, source, map, label);
        }
    }

    private void readUnion(InputNode node, Object source, LabelMap map, Label label) throws Exception {
        Object value = readInstance(node, source, label);
        for (String key : label.getPaths()) {
            map.getLabel(key);
        }
        if (label.isInline()) {
            this.criteria.set(label, value);
        }
    }

    private Object readInstance(InputNode node, Object source, Label label) throws Exception {
        Object object = readVariable(node, source, label);
        if (object == null) {
            Position line = node.getPosition();
            Class expect = this.context.getType(this.type, source);
            if (label.isRequired() && this.revision.isEqual()) {
                throw new ValueRequiredException("Empty value for %s in %s at %s", label, expect, line);
            }
        } else if (object != label.getEmpty(this.context)) {
            this.criteria.set(label, object);
        }
        return object;
    }

    private Object readVariable(InputNode node, Object source, Label label) throws Exception {
        Object value;
        Converter reader = label.getConverter(this.context);
        if (label.isCollection()) {
            Variable variable = this.criteria.get(label);
            Contact contact = label.getContact();
            if (variable != null) {
                return reader.read(node, variable.getValue());
            }
            if (!(source == null || (value = contact.get(source)) == null)) {
                return reader.read(node, value);
            }
        }
        return reader.read(node);
    }

    private void validate(InputNode node, LabelMap map, Object source) throws Exception {
        Class expect = this.context.getType(this.type, source);
        Position line = node.getPosition();
        Iterator i$ = map.iterator();
        while (i$.hasNext()) {
            Label label = i$.next();
            if (!label.isRequired() || !this.revision.isEqual()) {
                Object value = label.getEmpty(this.context);
                if (value != null) {
                    this.criteria.set(label, value);
                }
            } else {
                throw new ValueRequiredException("Unable to satisfy %s for %s at %s", label, expect, line);
            }
        }
    }

    public boolean validate(InputNode node) throws Exception {
        Instance value = this.factory.getInstance(node);
        if (value.isReference()) {
            return true;
        }
        Object instance = value.setInstance((Object) null);
        return validate(node, value.getType());
    }

    private boolean validate(InputNode node, Class type2) throws Exception {
        Schema schema = this.context.getSchema(type2);
        Section section = schema.getSection();
        validateText(node, schema);
        validateSection(node, section);
        return node.isElement();
    }

    private void validateSection(InputNode node, Section section) throws Exception {
        validateAttributes(node, section);
        validateElements(node, section);
    }

    private void validateAttributes(InputNode node, Section section) throws Exception {
        NodeMap<InputNode> list = node.getAttributes();
        LabelMap map = section.getAttributes();
        Iterator i$ = list.iterator();
        while (i$.hasNext()) {
            InputNode value = node.getAttribute(i$.next());
            if (value != null) {
                validateAttribute(value, section, map);
            }
        }
        validate(node, map);
    }

    private void validateElements(InputNode node, Section section) throws Exception {
        LabelMap map = section.getElements();
        InputNode next = node.getNext();
        while (next != null) {
            Section child = section.getSection(next.getName());
            if (child != null) {
                validateSection(next, child);
            } else {
                validateElement(next, section, map);
            }
            next = node.getNext();
        }
        validate(node, map);
    }

    private void validateText(InputNode node, Schema schema) throws Exception {
        Label label = schema.getText();
        if (label != null) {
            validate(node, label);
        }
    }

    private void validateAttribute(InputNode node, Section section, LabelMap map) throws Exception {
        Position line = node.getPosition();
        String path = section.getAttribute(node.getName());
        Label label = map.getLabel(path);
        if (label == null) {
            Class expect = this.type.getType();
            if (map.isStrict(this.context) && this.revision.isEqual()) {
                throw new AttributeException("Attribute '%s' does not exist for %s at %s", path, expect, line);
            }
            return;
        }
        validate(node, label);
    }

    private void validateElement(InputNode node, Section section, LabelMap map) throws Exception {
        String path = section.getPath(node.getName());
        Label label = map.getLabel(path);
        if (label == null) {
            label = this.criteria.resolve(path);
        }
        if (label == null) {
            Position line = node.getPosition();
            Class expect = this.type.getType();
            if (!map.isStrict(this.context) || !this.revision.isEqual()) {
                node.skip();
            } else {
                throw new ElementException("Element '%s' does not exist for %s at %s", path, expect, line);
            }
        } else {
            validateUnion(node, map, label);
        }
    }

    private void validateUnion(InputNode node, LabelMap map, Label label) throws Exception {
        for (String key : label.getPaths()) {
            map.getLabel(key);
        }
        if (label.isInline()) {
            this.criteria.set(label, (Object) null);
        }
        validate(node, label);
    }

    private void validate(InputNode node, Label label) throws Exception {
        Converter reader = label.getConverter(this.context);
        Position line = node.getPosition();
        Class expect = this.type.getType();
        if (!reader.validate(node)) {
            throw new PersistenceException("Invalid value for %s in %s at %s", label, expect, line);
        } else {
            this.criteria.set(label, (Object) null);
        }
    }

    private void validate(InputNode node, LabelMap map) throws Exception {
        Position line = node.getPosition();
        Iterator i$ = map.iterator();
        while (i$.hasNext()) {
            Label label = i$.next();
            Class expect = this.type.getType();
            if (label.isRequired() && this.revision.isEqual()) {
                throw new ValueRequiredException("Unable to satisfy %s for %s at %s", label, expect, line);
            }
        }
    }

    public void write(OutputNode node, Object source) throws Exception {
        Schema schema = this.context.getSchema(source.getClass());
        Caller caller = schema.getCaller();
        try {
            if (schema.isPrimitive()) {
                this.primitive.write(node, source);
            } else {
                caller.persist(source);
                write(node, source, schema);
            }
        } finally {
            caller.complete(source);
        }
    }

    private void write(OutputNode node, Object source, Schema schema) throws Exception {
        Section section = schema.getSection();
        writeVersion(node, source, schema);
        writeSection(node, source, section);
    }

    private void writeSection(OutputNode node, Object source, Section section) throws Exception {
        NamespaceMap scope = node.getNamespaces();
        String prefix = section.getPrefix();
        if (prefix != null) {
            String reference = scope.getReference(prefix);
            if (reference == null) {
                throw new ElementException("Namespace prefix '%s' in %s is not in scope", prefix, this.type);
            }
            node.setReference(reference);
        }
        writeAttributes(node, source, section);
        writeElements(node, source, section);
        writeText(node, source, section);
    }

    private void writeVersion(OutputNode node, Object source, Schema schema) throws Exception {
        Version version = schema.getRevision();
        Label label = schema.getVersion();
        if (version != null) {
            Double start = Double.valueOf(this.revision.getDefault());
            Double value = Double.valueOf(version.revision());
            if (!this.revision.compare(value, start)) {
                writeAttribute(node, value, label);
            } else if (label.isRequired()) {
                writeAttribute(node, value, label);
            }
        }
    }

    private void writeAttributes(OutputNode node, Object source, Section section) throws Exception {
        Iterator i$ = section.getAttributes().iterator();
        while (i$.hasNext()) {
            Label label = i$.next();
            Object value = label.getContact().get(source);
            Class expect = this.context.getType(this.type, source);
            if (value == null) {
                value = label.getEmpty(this.context);
            }
            if (value != null || !label.isRequired()) {
                writeAttribute(node, value, label);
            } else {
                throw new AttributeException("Value for %s is null in %s", label, expect);
            }
        }
    }

    private void writeElements(OutputNode node, Object source, Section section) throws Exception {
        Iterator i$ = section.iterator();
        while (i$.hasNext()) {
            String name = (String) i$.next();
            Section child = section.getSection(name);
            if (child != null) {
                writeSection(node.getChild(name), source, child);
            } else {
                Label label = section.getElement(section.getPath(name));
                Class expect = this.context.getType(this.type, source);
                if (this.criteria.get(label) != null) {
                    continue;
                } else if (label == null) {
                    throw new ElementException("Element '%s' not defined in %s", name, expect);
                } else {
                    writeUnion(node, source, section, label);
                }
            }
        }
    }

    private void writeUnion(OutputNode node, Object source, Section section, Label label) throws Exception {
        Object value = label.getContact().get(source);
        Class expect = this.context.getType(this.type, source);
        if (value != null || !label.isRequired()) {
            Object replace = writeReplace(value);
            if (replace != null) {
                writeElement(node, replace, label);
            }
            this.criteria.set(label, replace);
            return;
        }
        throw new ElementException("Value for %s is null in %s", label, expect);
    }

    private Object writeReplace(Object source) throws Exception {
        if (source == null) {
            return source;
        }
        return this.context.getCaller(source.getClass()).replace(source);
    }

    private void writeText(OutputNode node, Object source, Section section) throws Exception {
        Label label = section.getText();
        if (label != null) {
            Object value = label.getContact().get(source);
            Class expect = this.context.getType(this.type, source);
            if (value == null) {
                value = label.getEmpty(this.context);
            }
            if (value != null || !label.isRequired()) {
                writeText(node, value, label);
            } else {
                throw new TextException("Value for %s is null in %s", label, expect);
            }
        }
    }

    private void writeAttribute(OutputNode node, Object value, Label label) throws Exception {
        if (value != null) {
            label.getDecorator().decorate(node.setAttribute(label.getName(), this.factory.getText(value)));
        }
    }

    private void writeElement(OutputNode node, Object value, Label label) throws Exception {
        if (value != null) {
            Class real = value.getClass();
            Label match = label.getLabel(real);
            String name = match.getName();
            Type type2 = label.getType(real);
            OutputNode next = node.getChild(name);
            if (!match.isInline()) {
                writeNamespaces(next, type2, match);
            }
            if (match.isInline() || !isOverridden(next, value, type2)) {
                Converter convert = match.getConverter(this.context);
                next.setData(match.isData());
                writeElement(next, value, convert);
            }
        }
    }

    private void writeElement(OutputNode node, Object value, Converter convert) throws Exception {
        convert.write(node, value);
    }

    private void writeNamespaces(OutputNode node, Type type2, Label label) throws Exception {
        label.getDecorator().decorate(node, this.context.getDecorator(type2.getType()));
    }

    private void writeText(OutputNode node, Object value, Label label) throws Exception {
        if (value != null && !label.isTextList()) {
            String text = this.factory.getText(value);
            node.setData(label.isData());
            node.setValue(text);
        }
    }

    private boolean isOverridden(OutputNode node, Object value, Type type2) throws Exception {
        return this.factory.setOverride(type2, value, node);
    }

    private static class Builder {
        protected final Composite composite;
        protected final Criteria criteria;
        protected final Schema schema;
        protected final Instance value;

        public Builder(Composite composite2, Criteria criteria2, Schema schema2, Instance value2) {
            this.composite = composite2;
            this.criteria = criteria2;
            this.schema = schema2;
            this.value = value2;
        }

        public Object read(InputNode node) throws Exception {
            Object source = this.value.getInstance();
            Section section = this.schema.getSection();
            this.value.setInstance(source);
            this.composite.readVersion(node, source, this.schema);
            this.composite.readText(node, source, section);
            this.composite.readAttributes(node, source, section);
            this.composite.readElements(node, source, section);
            this.criteria.commit(source);
            return source;
        }
    }

    private class Injector extends Builder {
        private Injector(Composite composite, Criteria criteria, Schema schema, Instance value) {
            super(composite, criteria, schema, value);
        }

        public Object read(InputNode node) throws Exception {
            Section section = this.schema.getSection();
            this.composite.readVersion(node, (Object) null, this.schema);
            this.composite.readText(node, (Object) null, section);
            this.composite.readAttributes(node, (Object) null, section);
            this.composite.readElements(node, (Object) null, section);
            return readInject(node);
        }

        private Object readInject(InputNode node) throws Exception {
            Object source = this.schema.getInstantiator().getInstance(this.criteria);
            this.value.setInstance(source);
            this.criteria.commit(source);
            return source;
        }
    }
}
