package org.simpleframework.xml.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.simpleframework.xml.strategy.Type;
import org.simpleframework.xml.stream.Format;
import org.simpleframework.xml.stream.Style;
import org.simpleframework.xml.util.Cache;
import org.simpleframework.xml.util.ConcurrentCache;

class PathParser implements Expression {
    protected boolean attribute;
    protected Cache<String> attributes = new ConcurrentCache();
    protected StringBuilder builder = new StringBuilder();
    protected String cache;
    protected int count;
    protected char[] data;
    protected Cache<String> elements = new ConcurrentCache();
    protected List<Integer> indexes = new ArrayList();
    protected String location;
    protected List<String> names = new ArrayList();
    protected int off;
    protected String path;
    protected List<String> prefixes = new ArrayList();
    protected int start;
    protected Style style;
    protected Type type;

    public PathParser(String path2, Type type2, Format format) throws Exception {
        this.style = format.getStyle();
        this.type = type2;
        this.path = path2;
        parse(path2);
    }

    public boolean isEmpty() {
        return isEmpty(this.location);
    }

    public boolean isPath() {
        return this.names.size() > 1;
    }

    public boolean isAttribute() {
        return this.attribute;
    }

    public int getIndex() {
        return this.indexes.get(0).intValue();
    }

    public String getPrefix() {
        return this.prefixes.get(0);
    }

    public String getFirst() {
        return this.names.get(0);
    }

    public String getLast() {
        return this.names.get(this.names.size() - 1);
    }

    public String getPath() {
        return this.location;
    }

    public String getElement(String name) {
        if (isEmpty(this.location)) {
            return this.style.getElement(name);
        }
        String path2 = this.elements.fetch(name);
        if (path2 != null) {
            return path2;
        }
        String path3 = getElementPath(this.location, name);
        if (path3 == null) {
            return path3;
        }
        this.elements.cache(name, path3);
        return path3;
    }

    /* access modifiers changed from: protected */
    public String getElementPath(String path2, String name) {
        String element = this.style.getElement(name);
        if (isEmpty(element)) {
            return path2;
        }
        if (isEmpty(path2)) {
            return element;
        }
        return path2 + "/" + element + "[1]";
    }

    public String getAttribute(String name) {
        if (isEmpty(this.location)) {
            return this.style.getAttribute(name);
        }
        String path2 = this.attributes.fetch(name);
        if (path2 != null) {
            return path2;
        }
        String path3 = getAttributePath(this.location, name);
        if (path3 == null) {
            return path3;
        }
        this.attributes.cache(name, path3);
        return path3;
    }

    /* access modifiers changed from: protected */
    public String getAttributePath(String path2, String name) {
        String attribute2 = this.style.getAttribute(name);
        return isEmpty(path2) ? attribute2 : path2 + "/@" + attribute2;
    }

    public Iterator<String> iterator() {
        return this.names.iterator();
    }

    public Expression getPath(int from) {
        return getPath(from, 0);
    }

    public Expression getPath(int from, int trim) {
        int last = this.names.size() - 1;
        if (last - trim >= from) {
            return new PathSection(from, last - trim);
        }
        return new PathSection(from, from);
    }

    private void parse(String path2) throws Exception {
        if (path2 != null) {
            this.count = path2.length();
            this.data = new char[this.count];
            path2.getChars(0, this.count, this.data, 0);
        }
        path();
    }

    private void path() throws Exception {
        if (this.data[this.off] == '/') {
            throw new PathException("Path '%s' in %s references document root", this.path, this.type);
        }
        if (this.data[this.off] == '.') {
            skip();
        }
        while (this.off < this.count) {
            if (this.attribute) {
                throw new PathException("Path '%s' in %s references an invalid attribute", this.path, this.type);
            }
            segment();
        }
        truncate();
        build();
    }

    private void build() {
        int count2 = this.names.size();
        int last = count2 - 1;
        for (int i = 0; i < count2; i++) {
            String prefix = this.prefixes.get(i);
            String segment = this.names.get(i);
            int index = this.indexes.get(i).intValue();
            if (i > 0) {
                this.builder.append('/');
            }
            if (!this.attribute || i != last) {
                if (prefix != null) {
                    this.builder.append(prefix);
                    this.builder.append(':');
                }
                this.builder.append(segment);
                this.builder.append('[');
                this.builder.append(index);
                this.builder.append(']');
            } else {
                this.builder.append('@');
                this.builder.append(segment);
            }
        }
        this.location = this.builder.toString();
    }

    private void skip() throws Exception {
        if (this.data.length > 1) {
            if (this.data[this.off + 1] != '/') {
                throw new PathException("Path '%s' in %s has an illegal syntax", this.path, this.type);
            }
            this.off++;
        }
        int i = this.off + 1;
        this.off = i;
        this.start = i;
    }

    private void segment() throws Exception {
        char first = this.data[this.off];
        if (first == '/') {
            throw new PathException("Invalid path expression '%s' in %s", this.path, this.type);
        }
        if (first == '@') {
            attribute();
        } else {
            element();
        }
        align();
    }

    private void element() throws Exception {
        int mark = this.off;
        int size = 0;
        while (true) {
            if (this.off >= this.count) {
                break;
            }
            char[] cArr = this.data;
            int i = this.off;
            this.off = i + 1;
            char value = cArr[i];
            if (isValid(value)) {
                size++;
            } else if (value == '@') {
                this.off--;
            } else if (value == '[') {
                index();
            } else if (value != '/') {
                throw new PathException("Illegal character '%s' in element for '%s' in %s", Character.valueOf(value), this.path, this.type);
            }
        }
        element(mark, size);
    }

    private void attribute() throws Exception {
        int mark = this.off + 1;
        this.off = mark;
        while (this.off < this.count) {
            char[] cArr = this.data;
            int i = this.off;
            this.off = i + 1;
            char value = cArr[i];
            if (!isValid(value)) {
                throw new PathException("Illegal character '%s' in attribute for '%s' in %s", Character.valueOf(value), this.path, this.type);
            }
        }
        if (this.off <= mark) {
            throw new PathException("Attribute reference in '%s' for %s is empty", this.path, this.type);
        }
        this.attribute = true;
        attribute(mark, this.off - mark);
    }

    private void index() throws Exception {
        int value = 0;
        if (this.data[this.off - 1] == '[') {
            while (this.off < this.count) {
                char[] cArr = this.data;
                int i = this.off;
                this.off = i + 1;
                char digit = cArr[i];
                if (!isDigit(digit)) {
                    break;
                }
                value = ((value * 10) + digit) - 48;
            }
        }
        char[] cArr2 = this.data;
        int i2 = this.off;
        this.off = i2 + 1;
        if (cArr2[i2 - 1] != ']') {
            throw new PathException("Invalid index for path '%s' in %s", this.path, this.type);
        } else {
            this.indexes.add(Integer.valueOf(value));
        }
    }

    private void truncate() throws Exception {
        if (this.off - 1 >= this.data.length) {
            this.off--;
        } else if (this.data[this.off - 1] == '/') {
            this.off--;
        }
    }

    private void align() throws Exception {
        if (this.names.size() > this.indexes.size()) {
            this.indexes.add(1);
        }
    }

    private boolean isEmpty(String text) {
        return text == null || text.length() == 0;
    }

    private boolean isDigit(char value) {
        return Character.isDigit(value);
    }

    private boolean isValid(char value) {
        return isLetter(value) || isSpecial(value);
    }

    private boolean isSpecial(char value) {
        return value == '_' || value == '-' || value == ':';
    }

    private boolean isLetter(char value) {
        return Character.isLetterOrDigit(value);
    }

    private void element(int start2, int count2) {
        String segment = new String(this.data, start2, count2);
        if (count2 > 0) {
            element(segment);
        }
    }

    private void attribute(int start2, int count2) {
        String segment = new String(this.data, start2, count2);
        if (count2 > 0) {
            attribute(segment);
        }
    }

    private void element(String segment) {
        int index = segment.indexOf(58);
        String prefix = null;
        if (index > 0) {
            prefix = segment.substring(0, index);
            segment = segment.substring(index + 1);
        }
        String element = this.style.getElement(segment);
        this.prefixes.add(prefix);
        this.names.add(element);
    }

    private void attribute(String segment) {
        String attribute2 = this.style.getAttribute(segment);
        this.prefixes.add((Object) null);
        this.names.add(attribute2);
    }

    public String toString() {
        int size = this.off - this.start;
        if (this.cache == null) {
            this.cache = new String(this.data, this.start, size);
        }
        return this.cache;
    }

    private class PathSection implements Expression {
        private int begin;
        private List<String> cache = new ArrayList();
        private int end;
        private String path;
        private String section;

        public PathSection(int index, int end2) {
            this.begin = index;
            this.end = end2;
        }

        public boolean isEmpty() {
            return this.begin == this.end;
        }

        public boolean isPath() {
            return this.end - this.begin >= 1;
        }

        public boolean isAttribute() {
            if (!PathParser.this.attribute || this.end < PathParser.this.names.size() - 1) {
                return false;
            }
            return true;
        }

        public String getPath() {
            if (this.section == null) {
                this.section = getCanonicalPath();
            }
            return this.section;
        }

        public String getElement(String name) {
            String path2 = getPath();
            if (path2 != null) {
                return PathParser.this.getElementPath(path2, name);
            }
            return name;
        }

        public String getAttribute(String name) {
            String path2 = getPath();
            if (path2 != null) {
                return PathParser.this.getAttributePath(path2, name);
            }
            return name;
        }

        public int getIndex() {
            return PathParser.this.indexes.get(this.begin).intValue();
        }

        public String getPrefix() {
            return PathParser.this.prefixes.get(this.begin);
        }

        public String getFirst() {
            return PathParser.this.names.get(this.begin);
        }

        public String getLast() {
            return PathParser.this.names.get(this.end);
        }

        public Expression getPath(int from) {
            return getPath(from, 0);
        }

        public Expression getPath(int from, int trim) {
            return new PathSection(this.begin + from, this.end - trim);
        }

        public Iterator<String> iterator() {
            if (this.cache.isEmpty()) {
                for (int i = this.begin; i <= this.end; i++) {
                    String segment = PathParser.this.names.get(i);
                    if (segment != null) {
                        this.cache.add(segment);
                    }
                }
            }
            return this.cache.iterator();
        }

        private String getCanonicalPath() {
            int start = 0;
            int pos = 0;
            while (pos < this.begin) {
                start = PathParser.this.location.indexOf(47, start + 1);
                pos++;
            }
            int last = start;
            while (pos <= this.end) {
                last = PathParser.this.location.indexOf(47, last + 1);
                if (last == -1) {
                    last = PathParser.this.location.length();
                }
                pos++;
            }
            return PathParser.this.location.substring(start + 1, last);
        }

        private String getFragment() {
            int last = PathParser.this.start;
            int pos = 0;
            int i = 0;
            while (true) {
                if (i > this.end) {
                    break;
                } else if (last >= PathParser.this.count) {
                    last++;
                    break;
                } else {
                    int last2 = last + 1;
                    if (PathParser.this.data[last] == '/' && (i = i + 1) == this.begin) {
                        pos = last2;
                        last = last2;
                    } else {
                        last = last2;
                    }
                }
            }
            return new String(PathParser.this.data, pos, (last - 1) - pos);
        }

        public String toString() {
            if (this.path == null) {
                this.path = getFragment();
            }
            return this.path;
        }
    }
}
