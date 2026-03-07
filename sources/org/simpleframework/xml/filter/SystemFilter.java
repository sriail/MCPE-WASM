package org.simpleframework.xml.filter;

public class SystemFilter implements Filter {
    private Filter filter;

    public SystemFilter() {
        this((Filter) null);
    }

    public SystemFilter(Filter filter2) {
        this.filter = filter2;
    }

    public String replace(String text) {
        String value = System.getProperty(text);
        if (value != null) {
            return value;
        }
        if (this.filter != null) {
            return this.filter.replace(text);
        }
        return null;
    }
}
