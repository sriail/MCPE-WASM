package org.simpleframework.xml.core;

import org.simpleframework.xml.DefaultType;
import org.simpleframework.xml.util.Cache;
import org.simpleframework.xml.util.ConcurrentCache;

class DetailExtractor {
    private final Cache<Detail> details;
    private final Cache<ContactList> fields;
    private final Cache<ContactList> methods;
    private final DefaultType override;
    private final Support support;

    public DetailExtractor(Support support2) {
        this(support2, (DefaultType) null);
    }

    public DetailExtractor(Support support2, DefaultType override2) {
        this.methods = new ConcurrentCache();
        this.fields = new ConcurrentCache();
        this.details = new ConcurrentCache();
        this.override = override2;
        this.support = support2;
    }

    public Detail getDetail(Class type) {
        Detail detail = this.details.fetch(type);
        if (detail != null) {
            return detail;
        }
        Detail detail2 = new DetailScanner(type, this.override);
        this.details.cache(type, detail2);
        return detail2;
    }

    public ContactList getFields(Class type) throws Exception {
        Detail detail;
        ContactList list = this.fields.fetch(type);
        if (list != null || (detail = getDetail(type)) == null) {
            return list;
        }
        return getFields(type, detail);
    }

    private ContactList getFields(Class type, Detail detail) throws Exception {
        ContactList list = new FieldScanner(detail, this.support);
        if (detail != null) {
            this.fields.cache(type, list);
        }
        return list;
    }

    public ContactList getMethods(Class type) throws Exception {
        Detail detail;
        ContactList list = this.methods.fetch(type);
        if (list != null || (detail = getDetail(type)) == null) {
            return list;
        }
        return getMethods(type, detail);
    }

    private ContactList getMethods(Class type, Detail detail) throws Exception {
        ContactList list = new MethodScanner(detail, this.support);
        if (detail != null) {
            this.methods.cache(type, list);
        }
        return list;
    }
}
