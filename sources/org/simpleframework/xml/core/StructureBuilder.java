package org.simpleframework.xml.core;

import java.lang.annotation.Annotation;
import java.util.Iterator;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementArray;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.ElementListUnion;
import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.ElementMapUnion;
import org.simpleframework.xml.ElementUnion;
import org.simpleframework.xml.Order;
import org.simpleframework.xml.Text;
import org.simpleframework.xml.Version;

class StructureBuilder {
    private ModelAssembler assembler;
    private LabelMap attributes;
    private ExpressionBuilder builder;
    private LabelMap elements;
    private Instantiator factory;
    private boolean primitive;
    private InstantiatorBuilder resolver;
    private Model root;
    private Scanner scanner;
    private Support support;
    private Label text;
    private LabelMap texts;
    private Label version;

    public StructureBuilder(Scanner scanner2, Detail detail, Support support2) throws Exception {
        this.builder = new ExpressionBuilder(detail, support2);
        this.assembler = new ModelAssembler(this.builder, detail, support2);
        this.resolver = new InstantiatorBuilder(scanner2, detail);
        this.root = new TreeModel(scanner2, detail);
        this.attributes = new LabelMap(scanner2);
        this.elements = new LabelMap(scanner2);
        this.texts = new LabelMap(scanner2);
        this.scanner = scanner2;
        this.support = support2;
    }

    public void assemble(Class type) throws Exception {
        Order order = this.scanner.getOrder();
        if (order != null) {
            this.assembler.assemble(this.root, order);
        }
    }

    public void process(Contact field, Annotation label) throws Exception {
        if (label instanceof Attribute) {
            process(field, label, this.attributes);
        }
        if (label instanceof ElementUnion) {
            union(field, label, this.elements);
        }
        if (label instanceof ElementListUnion) {
            union(field, label, this.elements);
        }
        if (label instanceof ElementMapUnion) {
            union(field, label, this.elements);
        }
        if (label instanceof ElementList) {
            process(field, label, this.elements);
        }
        if (label instanceof ElementArray) {
            process(field, label, this.elements);
        }
        if (label instanceof ElementMap) {
            process(field, label, this.elements);
        }
        if (label instanceof Element) {
            process(field, label, this.elements);
        }
        if (label instanceof Version) {
            version(field, label);
        }
        if (label instanceof Text) {
            text(field, label);
        }
    }

    private void union(Contact field, Annotation type, LabelMap map) throws Exception {
        for (Label label : this.support.getLabels(field, type)) {
            String path = label.getPath();
            String name = label.getName();
            if (map.get(path) != null) {
                throw new PersistenceException("Duplicate annotation of name '%s' on %s", name, label);
            }
            process(field, label, map);
        }
    }

    private void process(Contact field, Annotation type, LabelMap map) throws Exception {
        Label label = this.support.getLabel(field, type);
        String path = label.getPath();
        String name = label.getName();
        if (map.get(path) != null) {
            throw new PersistenceException("Duplicate annotation of name '%s' on %s", name, field);
        } else {
            process(field, label, map);
        }
    }

    private void process(Contact field, Label label, LabelMap map) throws Exception {
        Expression expression = label.getExpression();
        String path = label.getPath();
        Model model = this.root;
        if (!expression.isEmpty()) {
            model = register(expression);
        }
        this.resolver.register(label);
        model.register(label);
        map.put(path, label);
    }

    private void text(Contact field, Annotation type) throws Exception {
        Label label = this.support.getLabel(field, type);
        Expression expression = label.getExpression();
        String path = label.getPath();
        Model model = this.root;
        if (!expression.isEmpty()) {
            model = register(expression);
        }
        if (this.texts.get(path) != null) {
            throw new TextException("Multiple text annotations in %s", type);
        }
        this.resolver.register(label);
        model.register(label);
        this.texts.put(path, label);
    }

    private void version(Contact field, Annotation type) throws Exception {
        Label label = this.support.getLabel(field, type);
        if (this.version != null) {
            throw new AttributeException("Multiple version annotations in %s", type);
        } else {
            this.version = label;
        }
    }

    public Structure build(Class type) throws Exception {
        return new Structure(this.factory, this.root, this.version, this.text, this.primitive);
    }

    private boolean isElement(String path) throws Exception {
        Expression target = this.builder.build(path);
        Model model = lookup(target);
        if (model != null) {
            String name = target.getLast();
            int index = target.getIndex();
            if (model.isElement(name)) {
                return true;
            }
            if (model.isModel(name)) {
                if (model.lookup(name, index).isEmpty()) {
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    private boolean isAttribute(String path) throws Exception {
        Expression target = this.builder.build(path);
        Model model = lookup(target);
        if (model == null) {
            return false;
        }
        String name = target.getLast();
        if (!target.isPath()) {
            return model.isAttribute(path);
        }
        return model.isAttribute(name);
    }

    private Model lookup(Expression path) throws Exception {
        Expression target = path.getPath(0, 1);
        if (path.isPath()) {
            return this.root.lookup(target);
        }
        return this.root;
    }

    private Model register(Expression path) throws Exception {
        Model model = this.root.lookup(path);
        return model != null ? model : create(path);
    }

    private Model create(Expression path) throws Exception {
        Model model = this.root;
        while (model != null) {
            String prefix = path.getPrefix();
            String name = path.getFirst();
            int index = path.getIndex();
            if (name != null) {
                model = model.register(name, prefix, index);
            }
            if (!path.isPath()) {
                break;
            }
            path = path.getPath(1);
        }
        return model;
    }

    public void commit(Class type) throws Exception {
        if (this.factory == null) {
            this.factory = this.resolver.build();
        }
    }

    public void validate(Class type) throws Exception {
        Order order = this.scanner.getOrder();
        validateUnions(type);
        validateElements(type, order);
        validateAttributes(type, order);
        validateModel(type);
        validateText(type);
        validateTextList(type);
    }

    private void validateModel(Class type) throws Exception {
        if (!this.root.isEmpty()) {
            this.root.validate(type);
        }
    }

    private void validateText(Class type) throws Exception {
        Label label = this.root.getText();
        if (label != null) {
            if (label.isTextList()) {
                return;
            }
            if (!this.elements.isEmpty()) {
                throw new TextException("Elements used with %s in %s", label, type);
            } else if (this.root.isComposite()) {
                throw new TextException("Paths used with %s in %s", label, type);
            }
        } else if (this.scanner.isEmpty()) {
            this.primitive = isEmpty();
        }
    }

    private void validateTextList(Class type) throws Exception {
        Label label = this.root.getText();
        if (label != null && label.isTextList()) {
            Object key = label.getKey();
            Iterator i$ = this.elements.iterator();
            while (i$.hasNext()) {
                Label element = i$.next();
                if (!element.getKey().equals(key)) {
                    throw new TextException("Elements used with %s in %s", label, type);
                }
                Class actual = element.getDependent().getType();
                if (actual == String.class) {
                    throw new TextException("Illegal entry of %s with text annotations on %s in %s", actual, label, type);
                }
            }
            if (this.root.isComposite()) {
                throw new TextException("Paths used with %s in %s", label, type);
            }
        }
    }

    private void validateUnions(Class type) throws Exception {
        Iterator<Label> it = this.elements.iterator();
        while (it.hasNext()) {
            Label label = it.next();
            String[] options = label.getPaths();
            Contact contact = label.getContact();
            String[] arr$ = options;
            int len$ = arr$.length;
            int i$ = 0;
            while (true) {
                if (i$ < len$) {
                    String option = arr$[i$];
                    Annotation union = contact.getAnnotation();
                    Label other = (Label) this.elements.get(option);
                    if (label.isInline() != other.isInline()) {
                        throw new UnionException("Inline must be consistent in %s for %s", union, contact);
                    } else if (label.isRequired() != other.isRequired()) {
                        throw new UnionException("Required must be consistent in %s for %s", union, contact);
                    } else {
                        i$++;
                    }
                }
            }
        }
    }

    private void validateElements(Class type, Order order) throws Exception {
        if (order != null) {
            for (String name : order.elements()) {
                if (!isElement(name)) {
                    throw new ElementException("Ordered element '%s' missing for %s", name, type);
                }
            }
        }
    }

    private void validateAttributes(Class type, Order order) throws Exception {
        if (order != null) {
            for (String name : order.attributes()) {
                if (!isAttribute(name)) {
                    throw new AttributeException("Ordered attribute '%s' missing in %s", name, type);
                }
            }
        }
    }

    private boolean isEmpty() {
        if (this.text != null) {
            return false;
        }
        return this.root.isEmpty();
    }
}
