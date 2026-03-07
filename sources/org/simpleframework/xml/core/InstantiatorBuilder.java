package org.simpleframework.xml.core;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class InstantiatorBuilder {
    private LabelMap attributes = new LabelMap();
    private Comparer comparer = new Comparer();
    private Detail detail;
    private LabelMap elements = new LabelMap();
    private Instantiator factory;
    private List<Creator> options = new ArrayList();
    private Scanner scanner;
    private LabelMap texts = new LabelMap();

    public InstantiatorBuilder(Scanner scanner2, Detail detail2) {
        this.scanner = scanner2;
        this.detail = detail2;
    }

    public Instantiator build() throws Exception {
        if (this.factory == null) {
            populate(this.detail);
            build(this.detail);
            validate(this.detail);
        }
        return this.factory;
    }

    private Instantiator build(Detail detail2) throws Exception {
        if (this.factory == null) {
            this.factory = create(detail2);
        }
        return this.factory;
    }

    private Instantiator create(Detail detail2) throws Exception {
        Signature primary = this.scanner.getSignature();
        ParameterMap registry = this.scanner.getParameters();
        Creator creator = null;
        if (primary != null) {
            creator = new SignatureCreator(primary);
        }
        return new ClassInstantiator(this.options, creator, registry, detail2);
    }

    private Creator create(Signature signature) {
        Creator creator = new SignatureCreator(signature);
        if (signature != null) {
            this.options.add(creator);
        }
        return creator;
    }

    private Parameter create(Parameter original) throws Exception {
        Label label = resolve(original);
        if (label != null) {
            return new CacheParameter(original, label);
        }
        return null;
    }

    private void populate(Detail detail2) throws Exception {
        for (Signature signature : this.scanner.getSignatures()) {
            populate(signature);
        }
    }

    private void populate(Signature signature) throws Exception {
        Signature substitute = new Signature(signature);
        Iterator i$ = signature.iterator();
        while (i$.hasNext()) {
            Parameter replace = create(i$.next());
            if (replace != null) {
                substitute.add(replace);
            }
        }
        create(substitute);
    }

    private void validate(Detail detail2) throws Exception {
        for (Parameter parameter : this.scanner.getParameters().getAll()) {
            Label label = resolve(parameter);
            String path = parameter.getPath();
            if (label == null) {
                throw new ConstructorException("Parameter '%s' does not have a match in %s", path, detail2);
            }
            validateParameter(label, parameter);
        }
        validateConstructors();
    }

    private void validateParameter(Label label, Parameter parameter) throws Exception {
        Contact contact = label.getContact();
        String name = parameter.getName();
        if (!Support.isAssignable(parameter.getType(), contact.getType())) {
            throw new ConstructorException("Type is not compatible with %s for '%s' in %s", label, name, parameter);
        }
        validateNames(label, parameter);
        validateAnnotations(label, parameter);
    }

    private void validateNames(Label label, Parameter parameter) throws Exception {
        String require;
        String[] options2 = label.getNames();
        String name = parameter.getName();
        if (!contains(options2, name) && name != (require = label.getName())) {
            if (name == null || require == null) {
                throw new ConstructorException("Annotation does not match %s for '%s' in %s", label, name, parameter);
            } else if (!name.equals(require)) {
                throw new ConstructorException("Annotation does not match %s for '%s' in %s", label, name, parameter);
            }
        }
    }

    private void validateAnnotations(Label label, Parameter parameter) throws Exception {
        Annotation field = label.getAnnotation();
        Annotation argument = parameter.getAnnotation();
        String name = parameter.getName();
        if (!this.comparer.equals(field, argument)) {
            Class expect = field.annotationType();
            Class actual = argument.annotationType();
            if (!expect.equals(actual)) {
                throw new ConstructorException("Annotation %s does not match %s for '%s' in %s", actual, expect, name, parameter);
            }
        }
    }

    private void validateConstructors() throws Exception {
        List<Creator> list = this.factory.getCreators();
        if (this.factory.isDefault()) {
            validateConstructors(this.elements);
            validateConstructors(this.attributes);
        }
        if (!list.isEmpty()) {
            validateConstructors(this.elements, list);
            validateConstructors(this.attributes, list);
        }
    }

    private void validateConstructors(LabelMap map) throws Exception {
        Iterator i$ = map.iterator();
        while (i$.hasNext()) {
            Label label = i$.next();
            if (label != null && label.getContact().isReadOnly()) {
                throw new ConstructorException("Default constructor can not accept read only %s in %s", label, this.detail);
            }
        }
    }

    private void validateConstructors(LabelMap map, List<Creator> list) throws Exception {
        Iterator i$ = map.iterator();
        while (i$.hasNext()) {
            Label label = i$.next();
            if (label != null) {
                validateConstructor(label, list);
            }
        }
        if (list.isEmpty()) {
            throw new ConstructorException("No constructor accepts all read only values in %s", this.detail);
        }
    }

    private void validateConstructor(Label label, List<Creator> list) throws Exception {
        Iterator<Creator> iterator = list.iterator();
        while (iterator.hasNext()) {
            Signature signature = iterator.next().getSignature();
            Contact contact = label.getContact();
            Object key = label.getKey();
            if (contact.isReadOnly() && signature.get(key) == null) {
                iterator.remove();
            }
        }
    }

    public void register(Label label) throws Exception {
        if (label.isAttribute()) {
            register(label, this.attributes);
        } else if (label.isText()) {
            register(label, this.texts);
        } else {
            register(label, this.elements);
        }
    }

    private void register(Label label, LabelMap map) throws Exception {
        String name = label.getName();
        String path = label.getPath();
        if (!map.containsKey(name)) {
            map.put(name, label);
        } else if (!((Label) map.get(name)).getPath().equals(name)) {
            map.remove(name);
        }
        map.put(path, label);
    }

    private Label resolve(Parameter parameter) throws Exception {
        if (parameter.isAttribute()) {
            return resolve(parameter, this.attributes);
        }
        if (parameter.isText()) {
            return resolve(parameter, this.texts);
        }
        return resolve(parameter, this.elements);
    }

    private Label resolve(Parameter parameter, LabelMap map) throws Exception {
        String name = parameter.getName();
        Label label = (Label) map.get(parameter.getPath());
        if (label == null) {
            return (Label) map.get(name);
        }
        return label;
    }

    private boolean contains(String[] list, String value) throws Exception {
        for (String entry : list) {
            if (entry == value || entry.equals(value)) {
                return true;
            }
        }
        return false;
    }
}
