package org.simpleframework.xml.filter;

import java.util.Map;

public class MapFilter implements Filter {
    private Filter filter;
    private Map map;

    public MapFilter(Map map2) {
        this(map2, (Filter) null);
    }

    public MapFilter(Map map2, Filter filter2) {
        this.filter = filter2;
        this.map = map2;
    }

    public String replace(String text) {
        Object value = null;
        if (this.map != null) {
            value = this.map.get(text);
        }
        if (value != null) {
            return value.toString();
        }
        if (this.filter != null) {
            return this.filter.replace(text);
        }
        return null;
    }
}
