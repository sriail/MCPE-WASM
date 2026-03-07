package org.simpleframework.xml.stream;

class OutputAttribute implements OutputNode {
    private String name;
    private String reference;
    private NamespaceMap scope;
    private OutputNode source;
    private String value;

    public OutputAttribute(OutputNode source2, String name2, String value2) {
        this.scope = source2.getNamespaces();
        this.source = source2;
        this.value = value2;
        this.name = name2;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value2) {
        this.value = value2;
    }

    public void setName(String name2) {
        this.name = name2;
    }

    public String getName() {
        return this.name;
    }

    public OutputNode getParent() {
        return this.source;
    }

    public NodeMap<OutputNode> getAttributes() {
        return new OutputNodeMap(this);
    }

    public OutputNode getChild(String name2) {
        return null;
    }

    public String getComment() {
        return null;
    }

    public void setComment(String comment) {
    }

    public Mode getMode() {
        return Mode.INHERIT;
    }

    public void setMode(Mode mode) {
    }

    public void setData(boolean data) {
    }

    public String getPrefix() {
        return this.scope.getPrefix(this.reference);
    }

    public String getPrefix(boolean inherit) {
        return this.scope.getPrefix(this.reference);
    }

    public String getReference() {
        return this.reference;
    }

    public void setReference(String reference2) {
        this.reference = reference2;
    }

    public NamespaceMap getNamespaces() {
        return this.scope;
    }

    public OutputNode setAttribute(String name2, String value2) {
        return null;
    }

    public void remove() {
    }

    public void commit() {
    }

    public boolean isRoot() {
        return false;
    }

    public boolean isCommitted() {
        return true;
    }

    public String toString() {
        return String.format("attribute %s='%s'", new Object[]{this.name, this.value});
    }
}
