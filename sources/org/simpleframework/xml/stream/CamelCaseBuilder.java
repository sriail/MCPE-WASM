package org.simpleframework.xml.stream;

class CamelCaseBuilder implements Style {
    protected final boolean attribute;
    protected final boolean element;

    public CamelCaseBuilder(boolean element2, boolean attribute2) {
        this.attribute = attribute2;
        this.element = element2;
    }

    public String getAttribute(String name) {
        if (name != null) {
            return new Attribute(name).process();
        }
        return null;
    }

    public String getElement(String name) {
        if (name != null) {
            return new Element(name).process();
        }
        return null;
    }

    private class Attribute extends Splitter {
        private boolean capital;

        private Attribute(String source) {
            super(source);
        }

        /* access modifiers changed from: protected */
        public void parse(char[] text, int off, int len) {
            if (CamelCaseBuilder.this.attribute || this.capital) {
                text[off] = toUpper(text[off]);
            }
            this.capital = true;
        }

        /* access modifiers changed from: protected */
        public void commit(char[] text, int off, int len) {
            this.builder.append(text, off, len);
        }
    }

    private class Element extends Attribute {
        private boolean capital;

        private Element(String source) {
            super(source);
        }

        /* access modifiers changed from: protected */
        public void parse(char[] text, int off, int len) {
            if (CamelCaseBuilder.this.element || this.capital) {
                text[off] = toUpper(text[off]);
            }
            this.capital = true;
        }
    }
}
