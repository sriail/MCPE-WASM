package org.simpleframework.xml.core;

import java.util.Map;
import org.simpleframework.xml.strategy.Type;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;
import org.simpleframework.xml.stream.Style;

class CompositeMap implements Converter {
    private final Entry entry;
    private final MapFactory factory;
    private final Converter key;
    private final Style style;
    private final Converter value;

    public CompositeMap(Context context, Entry entry2, Type type) throws Exception {
        this.factory = new MapFactory(context, type);
        this.value = entry2.getValue(context);
        this.key = entry2.getKey(context);
        this.style = context.getStyle();
        this.entry = entry2;
    }

    public Object read(InputNode node) throws Exception {
        Instance type = this.factory.getInstance(node);
        Object map = type.getInstance();
        if (!type.isReference()) {
            return populate(node, map);
        }
        return map;
    }

    public Object read(InputNode node, Object result) throws Exception {
        Instance type = this.factory.getInstance(node);
        if (type.isReference()) {
            return type.getInstance();
        }
        type.setInstance(result);
        if (result != null) {
            return populate(node, result);
        }
        return result;
    }

    private Object populate(InputNode node, Object result) throws Exception {
        Map map = (Map) result;
        while (true) {
            InputNode next = node.getNext();
            if (next == null) {
                return map;
            }
            map.put(this.key.read(next), this.value.read(next));
        }
    }

    public boolean validate(InputNode node) throws Exception {
        Instance value2 = this.factory.getInstance(node);
        if (value2.isReference()) {
            return true;
        }
        Object instance = value2.setInstance((Object) null);
        return validate(node, value2.getType());
    }

    /* JADX WARNING: Removed duplicated region for block: B:4:0x0009  */
    /* JADX WARNING: Removed duplicated region for block: B:8:0x0007 A[SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean validate(org.simpleframework.xml.stream.InputNode r4, java.lang.Class r5) throws java.lang.Exception {
        /*
            r3 = this;
            r1 = 0
        L_0x0001:
            org.simpleframework.xml.stream.InputNode r0 = r4.getNext()
            if (r0 != 0) goto L_0x0009
            r1 = 1
        L_0x0008:
            return r1
        L_0x0009:
            org.simpleframework.xml.core.Converter r2 = r3.key
            boolean r2 = r2.validate(r0)
            if (r2 == 0) goto L_0x0008
            org.simpleframework.xml.core.Converter r2 = r3.value
            boolean r2 = r2.validate(r0)
            if (r2 != 0) goto L_0x0001
            goto L_0x0008
        */
        throw new UnsupportedOperationException("Method not decompiled: org.simpleframework.xml.core.CompositeMap.validate(org.simpleframework.xml.stream.InputNode, java.lang.Class):boolean");
    }

    public void write(OutputNode node, Object source) throws Exception {
        Map map = (Map) source;
        for (Object index : map.keySet()) {
            OutputNode next = node.getChild(this.style.getElement(this.entry.getEntry()));
            Object item = map.get(index);
            this.key.write(next, index);
            this.value.write(next, item);
        }
    }
}
