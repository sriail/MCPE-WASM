package org.simpleframework.xml.stream;

class InputAttribute implements InputNode {
    private String name;
    private InputNode parent;
    private String prefix;
    private String reference;
    private Object source;
    private String value;

    public InputAttribute(InputNode parent2, String name2, String value2) {
        this.parent = parent2;
        this.value = value2;
        this.name = name2;
    }

    public InputAttribute(InputNode parent2, Attribute attribute) {
        this.reference = attribute.getReference();
        this.prefix = attribute.getPrefix();
        this.source = attribute.getSource();
        this.value = attribute.getValue();
        this.name = attribute.getName();
        this.parent = parent2;
    }

    public Object getSource() {
        return this.source;
    }

    public InputNode getParent() {
        return this.parent;
    }

    public Position getPosition() {
        return this.parent.getPosition();
    }

    public String getName() {
        return this.name;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public String getReference() {
        return this.reference;
    }

    public String getValue() {
        return this.value;
    }

    public boolean isRoot() {
        return false;
    }

    public boolean isElement() {
        return false;
    }

    public InputNode getAttribute(String name2) {
        return null;
    }

    public NodeMap<InputNode> getAttributes() {
        return new InputNodeMap(this);
    }

    public InputNode getNext() {
        return null;
    }

    public InputNode getNext(String name2) {
        return null;
    }

    public void skip() {
    }

    public boolean isEmpty() {
        return false;
    }

    public String toString() {
        return String.format("attribute %s='%s'", new Object[]{this.name, this.value});
    }
}
