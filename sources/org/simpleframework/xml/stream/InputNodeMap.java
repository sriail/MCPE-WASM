package org.simpleframework.xml.stream;

import java.util.Iterator;
import java.util.LinkedHashMap;

class InputNodeMap extends LinkedHashMap<String, InputNode> implements NodeMap<InputNode> {
    private final InputNode source;

    protected InputNodeMap(InputNode source2) {
        this.source = source2;
    }

    public InputNodeMap(InputNode source2, EventNode element) {
        this.source = source2;
        build(element);
    }

    private void build(EventNode element) {
        Iterator i$ = element.iterator();
        while (i$.hasNext()) {
            Attribute entry = (Attribute) i$.next();
            InputAttribute value = new InputAttribute(this.source, entry);
            if (!entry.isReserved()) {
                put(value.getName(), value);
            }
        }
    }

    public InputNode getNode() {
        return this.source;
    }

    public String getName() {
        return this.source.getName();
    }

    public InputNode put(String name, String value) {
        InputNode node = new InputAttribute(this.source, name, value);
        if (name != null) {
            put(name, node);
        }
        return node;
    }

    public InputNode remove(String name) {
        return (InputNode) super.remove(name);
    }

    public InputNode get(String name) {
        return (InputNode) super.get(name);
    }

    public Iterator<String> iterator() {
        return keySet().iterator();
    }
}
