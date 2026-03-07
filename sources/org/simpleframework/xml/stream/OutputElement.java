package org.simpleframework.xml.stream;

class OutputElement implements OutputNode {
    private String comment;
    private Mode mode = Mode.INHERIT;
    private String name;
    private OutputNode parent;
    private String reference;
    private NamespaceMap scope;
    private OutputNodeMap table = new OutputNodeMap(this);
    private String value;
    private NodeWriter writer;

    public OutputElement(OutputNode parent2, NodeWriter writer2, String name2) {
        this.scope = new PrefixResolver(parent2);
        this.writer = writer2;
        this.parent = parent2;
        this.name = name2;
    }

    public String getPrefix() {
        return getPrefix(true);
    }

    public String getPrefix(boolean inherit) {
        String prefix = this.scope.getPrefix(this.reference);
        if (!inherit || prefix != null) {
            return prefix;
        }
        return this.parent.getPrefix();
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

    public OutputNode getParent() {
        return this.parent;
    }

    public String getName() {
        return this.name;
    }

    public String getValue() {
        return this.value;
    }

    public String getComment() {
        return this.comment;
    }

    public boolean isRoot() {
        return this.writer.isRoot(this);
    }

    public Mode getMode() {
        return this.mode;
    }

    public void setMode(Mode mode2) {
        this.mode = mode2;
    }

    public OutputNodeMap getAttributes() {
        return this.table;
    }

    public void setComment(String comment2) {
        this.comment = comment2;
    }

    public void setValue(String value2) {
        this.value = value2;
    }

    public void setName(String name2) {
        this.name = name2;
    }

    public void setData(boolean data) {
        if (data) {
            this.mode = Mode.DATA;
        } else {
            this.mode = Mode.ESCAPE;
        }
    }

    public OutputNode setAttribute(String name2, String value2) {
        return this.table.put(name2, value2);
    }

    public OutputNode getChild(String name2) throws Exception {
        return this.writer.writeElement(this, name2);
    }

    public void remove() throws Exception {
        this.writer.remove(this);
    }

    public void commit() throws Exception {
        this.writer.commit(this);
    }

    public boolean isCommitted() {
        return this.writer.isCommitted(this);
    }

    public String toString() {
        return String.format("element %s", new Object[]{this.name});
    }
}
