package org.simpleframework.xml.stream;

class IdentityStyle implements Style {
    IdentityStyle() {
    }

    public String getAttribute(String name) {
        return name;
    }

    public String getElement(String name) {
        return name;
    }
}
