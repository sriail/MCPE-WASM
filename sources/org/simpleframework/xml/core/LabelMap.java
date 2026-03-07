package org.simpleframework.xml.core;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

class LabelMap extends LinkedHashMap<String, Label> implements Iterable<Label> {
    private final Policy policy;

    public LabelMap() {
        this((Policy) null);
    }

    public LabelMap(Policy policy2) {
        this.policy = policy2;
    }

    public Iterator<Label> iterator() {
        return values().iterator();
    }

    public Label getLabel(String name) {
        return (Label) remove(name);
    }

    public String[] getKeys() throws Exception {
        Set<String> list = new HashSet<>();
        Iterator i$ = iterator();
        while (i$.hasNext()) {
            Label label = i$.next();
            if (label != null) {
                String path = label.getPath();
                String name = label.getName();
                list.add(path);
                list.add(name);
            }
        }
        return getArray(list);
    }

    public String[] getPaths() throws Exception {
        Set<String> list = new HashSet<>();
        Iterator i$ = iterator();
        while (i$.hasNext()) {
            Label label = i$.next();
            if (label != null) {
                list.add(label.getPath());
            }
        }
        return getArray(list);
    }

    public LabelMap getLabels() throws Exception {
        LabelMap map = new LabelMap(this.policy);
        Iterator i$ = iterator();
        while (i$.hasNext()) {
            Label label = i$.next();
            if (label != null) {
                map.put(label.getPath(), label);
            }
        }
        return map;
    }

    private String[] getArray(Set<String> list) {
        return (String[]) list.toArray(new String[0]);
    }

    public boolean isStrict(Context context) {
        if (this.policy == null) {
            return context.isStrict();
        }
        return context.isStrict() && this.policy.isStrict();
    }
}
