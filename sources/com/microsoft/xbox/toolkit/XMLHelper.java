package com.microsoft.xbox.toolkit;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.convert.AnnotationStrategy;
import org.simpleframework.xml.core.Persister;

public class XMLHelper {
    private static final int XML_WAIT_TIMEOUT_MS = 1000;
    private static XMLHelper instance = new XMLHelper();
    private Serializer serializer;

    public static XMLHelper instance() {
        return instance;
    }

    private XMLHelper() {
        this.serializer = null;
        this.serializer = new Persister(new AnnotationStrategy());
    }

    public <T> T load(InputStream input, Class<T> type) throws XLEException {
        ClassLoader clsLoader;
        if (ThreadManager.UIThread != Thread.currentThread()) {
            BackgroundThreadWaitor.getInstance().waitForReady(XML_WAIT_TIMEOUT_MS);
        }
        new TimeMonitor();
        try {
            clsLoader = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader(type.getClassLoader());
            T rv = this.serializer.read(type, input, false);
            Thread.currentThread().setContextClassLoader(clsLoader);
            return rv;
        } catch (Exception e) {
            throw new XLEException(9, e.toString());
        } catch (Throwable th) {
            Thread.currentThread().setContextClassLoader(clsLoader);
            throw th;
        }
    }

    public <T> String save(T output) throws XLEException {
        new TimeMonitor();
        StringWriter writer = new StringWriter();
        try {
            this.serializer.write((Object) output, (Writer) writer);
            return writer.toString();
        } catch (Exception e) {
            throw new XLEException(9, e.toString());
        }
    }

    public <T> void save(T output, OutputStream outStream) throws XLEException {
        new TimeMonitor();
        try {
            this.serializer.write((Object) output, outStream);
        } catch (Exception e) {
            throw new XLEException(9, e.toString());
        }
    }
}
