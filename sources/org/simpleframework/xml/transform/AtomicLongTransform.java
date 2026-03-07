package org.simpleframework.xml.transform;

import java.util.concurrent.atomic.AtomicLong;

class AtomicLongTransform implements Transform<AtomicLong> {
    AtomicLongTransform() {
    }

    public AtomicLong read(String value) {
        return new AtomicLong(Long.valueOf(value).longValue());
    }

    public String write(AtomicLong value) {
        return value.toString();
    }
}
