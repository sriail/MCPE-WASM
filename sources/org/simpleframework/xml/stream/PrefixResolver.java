package org.simpleframework.xml.stream;

import java.util.Iterator;
import java.util.LinkedHashMap;

class PrefixResolver extends LinkedHashMap<String, String> implements NamespaceMap {
    private final OutputNode source;

    public PrefixResolver(OutputNode source2) {
        this.source = source2;
    }

    public String getPrefix() {
        return this.source.getPrefix();
    }

    public String setReference(String reference) {
        return setReference(reference, "");
    }

    public String setReference(String reference, String prefix) {
        if (resolvePrefix(reference) != null) {
            return null;
        }
        return (String) put(reference, prefix);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0006, code lost:
        r0 = (java.lang.String) get(r3);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.String getPrefix(java.lang.String r3) {
        /*
            r2 = this;
            int r1 = r2.size()
            if (r1 <= 0) goto L_0x000f
            java.lang.Object r0 = r2.get(r3)
            java.lang.String r0 = (java.lang.String) r0
            if (r0 == 0) goto L_0x000f
        L_0x000e:
            return r0
        L_0x000f:
            java.lang.String r0 = r2.resolvePrefix(r3)
            goto L_0x000e
        */
        throw new UnsupportedOperationException("Method not decompiled: org.simpleframework.xml.stream.PrefixResolver.getPrefix(java.lang.String):java.lang.String");
    }

    public String getReference(String prefix) {
        if (containsValue(prefix)) {
            Iterator i$ = iterator();
            while (i$.hasNext()) {
                String reference = i$.next();
                String value = (String) get(reference);
                if (value != null && value.equals(prefix)) {
                    return reference;
                }
            }
        }
        return resolveReference(prefix);
    }

    private String resolveReference(String prefix) {
        NamespaceMap parent = this.source.getNamespaces();
        if (parent != null) {
            return parent.getReference(prefix);
        }
        return null;
    }

    private String resolvePrefix(String reference) {
        NamespaceMap parent = this.source.getNamespaces();
        if (parent != null) {
            String prefix = parent.getPrefix(reference);
            if (!containsValue(prefix)) {
                return prefix;
            }
        }
        return null;
    }

    public Iterator<String> iterator() {
        return keySet().iterator();
    }
}
