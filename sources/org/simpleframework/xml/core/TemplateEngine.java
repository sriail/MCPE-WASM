package org.simpleframework.xml.core;

import org.simpleframework.xml.filter.Filter;

class TemplateEngine {
    private Filter filter;
    private Template name = new Template();
    private int off;
    private Template source = new Template();
    private Template text = new Template();

    public TemplateEngine(Filter filter2) {
        this.filter = filter2;
    }

    public String process(String value) {
        if (value.indexOf(36) >= 0) {
            try {
                this.source.append(value);
                parse();
                value = this.text.toString();
            } finally {
                clear();
            }
        }
        return value;
    }

    private void parse() {
        while (this.off < this.source.count) {
            char[] cArr = this.source.buf;
            int i = this.off;
            this.off = i + 1;
            char next = cArr[i];
            if (next == '$' && this.off < this.source.count) {
                char[] cArr2 = this.source.buf;
                int i2 = this.off;
                this.off = i2 + 1;
                if (cArr2[i2] == '{') {
                    name();
                } else {
                    this.off--;
                }
            }
            this.text.append(next);
        }
    }

    private void name() {
        while (true) {
            if (this.off >= this.source.count) {
                break;
            }
            char[] cArr = this.source.buf;
            int i = this.off;
            this.off = i + 1;
            char next = cArr[i];
            if (next == '}') {
                replace();
                break;
            }
            this.name.append(next);
        }
        if (this.name.length() > 0) {
            this.text.append("${");
            this.text.append(this.name);
        }
    }

    private void replace() {
        if (this.name.length() > 0) {
            replace(this.name);
        }
        this.name.clear();
    }

    private void replace(Template name2) {
        replace(name2.toString());
    }

    private void replace(String name2) {
        String value = this.filter.replace(name2);
        if (value == null) {
            this.text.append("${");
            this.text.append(name2);
            this.text.append("}");
            return;
        }
        this.text.append(value);
    }

    public void clear() {
        this.name.clear();
        this.text.clear();
        this.source.clear();
        this.off = 0;
    }
}
