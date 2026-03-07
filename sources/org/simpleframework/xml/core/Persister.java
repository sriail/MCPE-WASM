package org.simpleframework.xml.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.filter.Filter;
import org.simpleframework.xml.filter.PlatformFilter;
import org.simpleframework.xml.strategy.Strategy;
import org.simpleframework.xml.strategy.TreeStrategy;
import org.simpleframework.xml.stream.Format;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.NodeBuilder;
import org.simpleframework.xml.stream.OutputNode;
import org.simpleframework.xml.transform.Matcher;

public class Persister implements Serializer {
    private final Format format;
    private final SessionManager manager;
    private final Strategy strategy;
    private final Support support;

    public Persister() {
        this((Map) new HashMap());
    }

    public Persister(Format format2) {
        this((Strategy) new TreeStrategy(), format2);
    }

    public Persister(Map filter) {
        this((Filter) new PlatformFilter(filter));
    }

    public Persister(Map filter, Format format2) {
        this((Filter) new PlatformFilter(filter));
    }

    public Persister(Filter filter) {
        this((Strategy) new TreeStrategy(), filter);
    }

    public Persister(Filter filter, Format format2) {
        this((Strategy) new TreeStrategy(), filter, format2);
    }

    public Persister(Matcher matcher) {
        this((Strategy) new TreeStrategy(), matcher);
    }

    public Persister(Matcher matcher, Format format2) {
        this((Strategy) new TreeStrategy(), matcher, format2);
    }

    public Persister(Strategy strategy2) {
        this(strategy2, (Map) new HashMap());
    }

    public Persister(Strategy strategy2, Format format2) {
        this(strategy2, (Map) new HashMap(), format2);
    }

    public Persister(Filter filter, Matcher matcher) {
        this((Strategy) new TreeStrategy(), filter, matcher);
    }

    public Persister(Filter filter, Matcher matcher, Format format2) {
        this(new TreeStrategy(), filter, matcher, format2);
    }

    public Persister(Strategy strategy2, Map data) {
        this(strategy2, (Filter) new PlatformFilter(data));
    }

    public Persister(Strategy strategy2, Map data, Format format2) {
        this(strategy2, (Filter) new PlatformFilter(data), format2);
    }

    public Persister(Strategy strategy2, Filter filter) {
        this(strategy2, filter, new Format());
    }

    public Persister(Strategy strategy2, Filter filter, Format format2) {
        this(strategy2, filter, new EmptyMatcher(), format2);
    }

    public Persister(Strategy strategy2, Matcher matcher) {
        this(strategy2, (Filter) new PlatformFilter(), matcher);
    }

    public Persister(Strategy strategy2, Matcher matcher, Format format2) {
        this(strategy2, new PlatformFilter(), matcher, format2);
    }

    public Persister(Strategy strategy2, Filter filter, Matcher matcher) {
        this(strategy2, filter, matcher, new Format());
    }

    public Persister(Strategy strategy2, Filter filter, Matcher matcher, Format format2) {
        this.support = new Support(filter, matcher, format2);
        this.manager = new SessionManager();
        this.strategy = strategy2;
        this.format = format2;
    }

    public <T> T read(Class<? extends T> type, String source) throws Exception {
        return read(type, source, true);
    }

    public <T> T read(Class<? extends T> type, File source) throws Exception {
        return read(type, source, true);
    }

    public <T> T read(Class<? extends T> type, InputStream source) throws Exception {
        return read(type, source, true);
    }

    public <T> T read(Class<? extends T> type, Reader source) throws Exception {
        return read(type, source, true);
    }

    public <T> T read(Class<? extends T> type, InputNode source) throws Exception {
        return read(type, source, true);
    }

    public <T> T read(Class<? extends T> type, String source, boolean strict) throws Exception {
        return read(type, (Reader) new StringReader(source), strict);
    }

    public <T> T read(Class<? extends T> type, File source, boolean strict) throws Exception {
        InputStream file = new FileInputStream(source);
        try {
            return read(type, file, strict);
        } finally {
            file.close();
        }
    }

    public <T> T read(Class<? extends T> type, InputStream source, boolean strict) throws Exception {
        return read(type, NodeBuilder.read(source), strict);
    }

    public <T> T read(Class<? extends T> type, Reader source, boolean strict) throws Exception {
        return read(type, NodeBuilder.read(source), strict);
    }

    public <T> T read(Class<? extends T> type, InputNode node, boolean strict) throws Exception {
        try {
            return read(type, node, this.manager.open(strict));
        } finally {
            this.manager.close();
        }
    }

    private <T> T read(Class<? extends T> type, InputNode node, Session session) throws Exception {
        return read(type, node, (Context) new Source(this.strategy, this.support, session));
    }

    private <T> T read(Class<? extends T> type, InputNode node, Context context) throws Exception {
        return new Traverser(context).read(node, (Class) type);
    }

    public <T> T read(T value, String source) throws Exception {
        return read(value, source, true);
    }

    public <T> T read(T value, File source) throws Exception {
        return read(value, source, true);
    }

    public <T> T read(T value, InputStream source) throws Exception {
        return read(value, source, true);
    }

    public <T> T read(T value, Reader source) throws Exception {
        return read(value, source, true);
    }

    public <T> T read(T value, InputNode source) throws Exception {
        return read(value, source, true);
    }

    public <T> T read(T value, String source, boolean strict) throws Exception {
        return read(value, (Reader) new StringReader(source), strict);
    }

    public <T> T read(T value, File source, boolean strict) throws Exception {
        InputStream file = new FileInputStream(source);
        try {
            return read(value, file, strict);
        } finally {
            file.close();
        }
    }

    public <T> T read(T value, InputStream source, boolean strict) throws Exception {
        return read(value, NodeBuilder.read(source), strict);
    }

    public <T> T read(T value, Reader source, boolean strict) throws Exception {
        return read(value, NodeBuilder.read(source), strict);
    }

    public <T> T read(T value, InputNode node, boolean strict) throws Exception {
        try {
            return read(value, node, this.manager.open(strict));
        } finally {
            this.manager.close();
        }
    }

    private <T> T read(T value, InputNode node, Session session) throws Exception {
        return read(value, node, (Context) new Source(this.strategy, this.support, session));
    }

    private <T> T read(T value, InputNode node, Context context) throws Exception {
        return new Traverser(context).read(node, (Object) value);
    }

    public boolean validate(Class type, String source) throws Exception {
        return validate(type, source, true);
    }

    public boolean validate(Class type, File source) throws Exception {
        return validate(type, source, true);
    }

    public boolean validate(Class type, InputStream source) throws Exception {
        return validate(type, source, true);
    }

    public boolean validate(Class type, Reader source) throws Exception {
        return validate(type, source, true);
    }

    public boolean validate(Class type, InputNode source) throws Exception {
        return validate(type, source, true);
    }

    public boolean validate(Class type, String source, boolean strict) throws Exception {
        return validate(type, (Reader) new StringReader(source), strict);
    }

    public boolean validate(Class type, File source, boolean strict) throws Exception {
        InputStream file = new FileInputStream(source);
        try {
            return validate(type, file, strict);
        } finally {
            file.close();
        }
    }

    public boolean validate(Class type, InputStream source, boolean strict) throws Exception {
        return validate(type, NodeBuilder.read(source), strict);
    }

    public boolean validate(Class type, Reader source, boolean strict) throws Exception {
        return validate(type, NodeBuilder.read(source), strict);
    }

    public boolean validate(Class type, InputNode node, boolean strict) throws Exception {
        try {
            return validate(type, node, this.manager.open(strict));
        } finally {
            this.manager.close();
        }
    }

    private boolean validate(Class type, InputNode node, Session session) throws Exception {
        return validate(type, node, (Context) new Source(this.strategy, this.support, session));
    }

    private boolean validate(Class type, InputNode node, Context context) throws Exception {
        return new Traverser(context).validate(node, type);
    }

    public void write(Object source, OutputNode root) throws Exception {
        try {
            write(source, root, this.manager.open());
        } finally {
            this.manager.close();
        }
    }

    private void write(Object source, OutputNode root, Session session) throws Exception {
        write(source, root, (Context) new Source(this.strategy, this.support, session));
    }

    private void write(Object source, OutputNode node, Context context) throws Exception {
        new Traverser(context).write(node, source);
    }

    public void write(Object source, File out) throws Exception {
        OutputStream file = new FileOutputStream(out);
        try {
            write(source, file);
        } finally {
            file.close();
        }
    }

    public void write(Object source, OutputStream out) throws Exception {
        write(source, out, "utf-8");
    }

    public void write(Object source, OutputStream out, String charset) throws Exception {
        write(source, (Writer) new OutputStreamWriter(out, charset));
    }

    public void write(Object source, Writer out) throws Exception {
        write(source, NodeBuilder.write(out, this.format));
    }
}
