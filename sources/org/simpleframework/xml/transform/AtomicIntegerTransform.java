package org.simpleframework.xml.transform;

import java.util.concurrent.atomic.AtomicInteger;

class AtomicIntegerTransform implements Transform<AtomicInteger> {
    AtomicIntegerTransform() {
    }

    public AtomicInteger read(String value) {
        return new AtomicInteger(Integer.valueOf(value).intValue());
    }

    public String write(AtomicInteger value) {
        return value.toString();
    }
}
