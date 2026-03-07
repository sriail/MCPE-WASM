package org.simpleframework.xml.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.DefaultType;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementArray;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.ElementListUnion;
import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.ElementMapUnion;
import org.simpleframework.xml.ElementUnion;
import org.simpleframework.xml.Text;
import org.simpleframework.xml.Transient;
import org.simpleframework.xml.Version;

class MethodScanner extends ContactList {
    private final Detail detail;
    private final MethodPartFactory factory;
    private final PartMap read = new PartMap();
    private final Support support;
    private final PartMap write = new PartMap();

    public MethodScanner(Detail detail2, Support support2) throws Exception {
        this.factory = new MethodPartFactory(detail2, support2);
        this.support = support2;
        this.detail = detail2;
        scan(detail2);
    }

    private void scan(Detail detail2) throws Exception {
        DefaultType override = detail2.getOverride();
        DefaultType access = detail2.getAccess();
        Class base = detail2.getSuper();
        if (base != null) {
            extend(base, override);
        }
        extract(detail2, access);
        extract(detail2);
        build();
        validate();
    }

    private void extend(Class base, DefaultType access) throws Exception {
        Iterator i$ = this.support.getMethods(base, access).iterator();
        while (i$.hasNext()) {
            process((MethodContact) ((Contact) i$.next()));
        }
    }

    private void extract(Detail detail2) throws Exception {
        for (MethodDetail entry : detail2.getMethods()) {
            Annotation[] list = entry.getAnnotations();
            Method method = entry.getMethod();
            for (Annotation label : list) {
                scan(method, label, list);
            }
        }
    }

    private void extract(Detail detail2, DefaultType access) throws Exception {
        List<MethodDetail> methods = detail2.getMethods();
        if (access == DefaultType.PROPERTY) {
            for (MethodDetail entry : methods) {
                Annotation[] list = entry.getAnnotations();
                Method method = entry.getMethod();
                if (this.factory.getType(method) != null) {
                    process(method, list);
                }
            }
        }
    }

    private void scan(Method method, Annotation label, Annotation[] list) throws Exception {
        if (label instanceof Attribute) {
            process(method, label, list);
        }
        if (label instanceof ElementUnion) {
            process(method, label, list);
        }
        if (label instanceof ElementListUnion) {
            process(method, label, list);
        }
        if (label instanceof ElementMapUnion) {
            process(method, label, list);
        }
        if (label instanceof ElementList) {
            process(method, label, list);
        }
        if (label instanceof ElementArray) {
            process(method, label, list);
        }
        if (label instanceof ElementMap) {
            process(method, label, list);
        }
        if (label instanceof Element) {
            process(method, label, list);
        }
        if (label instanceof Version) {
            process(method, label, list);
        }
        if (label instanceof Text) {
            process(method, label, list);
        }
        if (label instanceof Transient) {
            remove(method, label, list);
        }
    }

    private void process(Method method, Annotation label, Annotation[] list) throws Exception {
        MethodPart part = this.factory.getInstance(method, label, list);
        MethodType type = part.getMethodType();
        if (type == MethodType.GET) {
            process(part, this.read);
        }
        if (type == MethodType.IS) {
            process(part, this.read);
        }
        if (type == MethodType.SET) {
            process(part, this.write);
        }
    }

    private void process(Method method, Annotation[] list) throws Exception {
        MethodPart part = this.factory.getInstance(method, list);
        MethodType type = part.getMethodType();
        if (type == MethodType.GET) {
            process(part, this.read);
        }
        if (type == MethodType.IS) {
            process(part, this.read);
        }
        if (type == MethodType.SET) {
            process(part, this.write);
        }
    }

    private void process(MethodPart method, PartMap map) {
        String name = method.getName();
        if (name != null) {
            map.put(name, method);
        }
    }

    private void process(MethodContact contact) {
        MethodPart get = contact.getRead();
        MethodPart set = contact.getWrite();
        if (set != null) {
            insert(set, this.write);
        }
        insert(get, this.read);
    }

    private void insert(MethodPart method, PartMap map) {
        String name = method.getName();
        MethodPart existing = (MethodPart) map.remove(name);
        if (existing != null && isText(method)) {
            method = existing;
        }
        map.put(name, method);
    }

    private boolean isText(MethodPart method) {
        if (method.getAnnotation() instanceof Text) {
            return true;
        }
        return false;
    }

    private void remove(Method method, Annotation label, Annotation[] list) throws Exception {
        MethodPart part = this.factory.getInstance(method, label, list);
        MethodType type = part.getMethodType();
        if (type == MethodType.GET) {
            remove(part, this.read);
        }
        if (type == MethodType.IS) {
            remove(part, this.read);
        }
        if (type == MethodType.SET) {
            remove(part, this.write);
        }
    }

    private void remove(MethodPart part, PartMap map) throws Exception {
        String name = part.getName();
        if (name != null) {
            map.remove(name);
        }
    }

    private void build() throws Exception {
        Iterator i$ = this.read.iterator();
        while (i$.hasNext()) {
            String name = i$.next();
            MethodPart part = (MethodPart) this.read.get(name);
            if (part != null) {
                build(part, name);
            }
        }
    }

    private void build(MethodPart read2, String name) throws Exception {
        MethodPart match = this.write.take(name);
        if (match != null) {
            build(read2, match);
        } else {
            build(read2);
        }
    }

    private void build(MethodPart read2) throws Exception {
        add(new MethodContact(read2));
    }

    private void build(MethodPart read2, MethodPart write2) throws Exception {
        Annotation label = read2.getAnnotation();
        String name = read2.getName();
        if (!write2.getAnnotation().equals(label)) {
            throw new MethodException("Annotations do not match for '%s' in %s", name, this.detail);
        }
        Class type = read2.getType();
        if (type != write2.getType()) {
            throw new MethodException("Method types do not match for %s in %s", name, type);
        } else {
            add(new MethodContact(read2, write2));
        }
    }

    private void validate() throws Exception {
        Iterator i$ = this.write.iterator();
        while (i$.hasNext()) {
            String name = i$.next();
            MethodPart part = (MethodPart) this.write.get(name);
            if (part != null) {
                validate(part, name);
            }
        }
    }

    private void validate(MethodPart write2, String name) throws Exception {
        MethodPart match = this.read.take(name);
        Method method = write2.getMethod();
        if (match == null) {
            throw new MethodException("No matching get method for %s in %s", method, this.detail);
        }
    }

    private static class PartMap extends LinkedHashMap<String, MethodPart> implements Iterable<String> {
        private PartMap() {
        }

        public Iterator<String> iterator() {
            return keySet().iterator();
        }

        public MethodPart take(String name) {
            return (MethodPart) remove(name);
        }
    }
}
