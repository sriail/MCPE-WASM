package org.simpleframework.xml.filter;

public class EnvironmentFilter implements Filter {
    private Filter filter;

    public EnvironmentFilter() {
        this((Filter) null);
    }

    public EnvironmentFilter(Filter filter2) {
        this.filter = filter2;
    }

    public String replace(String text) {
        String value = System.getenv(text);
        if (value != null) {
            return value;
        }
        if (this.filter != null) {
            return this.filter.replace(text);
        }
        return null;
    }
}
