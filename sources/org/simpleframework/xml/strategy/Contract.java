package org.simpleframework.xml.strategy;

class Contract {
    private String label;
    private String length;
    private String mark;
    private String refer;

    public Contract(String mark2, String refer2, String label2, String length2) {
        this.length = length2;
        this.label = label2;
        this.refer = refer2;
        this.mark = mark2;
    }

    public String getLabel() {
        return this.label;
    }

    public String getReference() {
        return this.refer;
    }

    public String getIdentity() {
        return this.mark;
    }

    public String getLength() {
        return this.length;
    }
}
