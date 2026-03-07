package org.simpleframework.xml.stream;

class OutputDocument implements OutputNode {
    private String comment;
    private Mode mode = Mode.INHERIT;
    private String name;
    private String reference;
    private OutputStack stack;
    private OutputNodeMap table = new OutputNodeMap(this);
    private String value;
    private NodeWriter writer;

    public OutputDocument(NodeWriter writer2, OutputStack stack2) {
        this.writer = writer2;
        this.stack = stack2;
    }

    public String getPrefix() {
        return null;
    }

    public String getPrefix(boolean inherit) {
        return null;
    }

    public String getReference() {
        return this.reference;
    }

    public void setReference(String reference2) {
        this.reference = reference2;
    }

    public NamespaceMap getNamespaces() {
        return null;
    }

    public OutputNode getParent() {
        return null;
    }

    public String getName() {
        return null;
    }

    public String getValue() throws Exception {
        return this.value;
    }

    public String getComment() {
        return this.comment;
    }

    public boolean isRoot() {
        return true;
    }

    public Mode getMode() {
        return this.mode;
    }

    public void setMode(Mode mode2) {
        this.mode = mode2;
    }

    public OutputNode setAttribute(String name2, String value2) {
        return this.table.put(name2, value2);
    }

    public NodeMap<OutputNode> getAttributes() {
        return this.table;
    }

    public void setName(String name2) {
        this.name = name2;
    }

    public void setValue(String value2) {
        this.value = value2;
    }

    public void setComment(String comment2) {
        this.comment = comment2;
    }

    public void setData(boolean data) {
        if (data) {
            this.mode = Mode.DATA;
        } else {
            this.mode = Mode.ESCAPE;
        }
    }

    public OutputNode getChild(String name2) throws Exception {
        return this.writer.writeElement(this, name2);
    }

    public void remove() throws Exception {
        if (this.stack.isEmpty()) {
            throw new NodeException("No root node");
        }
        this.stack.bottom().remove();
    }

    public void commit() throws Exception {
        if (this.stack.isEmpty()) {
            throw new NodeException("No root node");
        }
        this.stack.bottom().commit();
    }

    public boolean isCommitted() {
        return this.stack.isEmpty();
    }
}
