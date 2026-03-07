package org.simpleframework.xml.core;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.simpleframework.xml.Order;
import org.simpleframework.xml.Version;

class PrimitiveScanner implements Scanner {
    private final Detail detail;
    private final Section section = new EmptySection(this);

    public PrimitiveScanner(Detail detail2) {
        this.detail = detail2;
    }

    public Signature getSignature() {
        return null;
    }

    public List<Signature> getSignatures() {
        return new LinkedList();
    }

    public ParameterMap getParameters() {
        return new ParameterMap();
    }

    public Instantiator getInstantiator() {
        return null;
    }

    public Class getType() {
        return this.detail.getType();
    }

    public Decorator getDecorator() {
        return null;
    }

    public Caller getCaller(Context context) {
        return new Caller(this, context);
    }

    public Section getSection() {
        return this.section;
    }

    public Version getRevision() {
        return null;
    }

    public Order getOrder() {
        return null;
    }

    public Label getVersion() {
        return null;
    }

    public Label getText() {
        return null;
    }

    public String getName() {
        return null;
    }

    public Function getCommit() {
        return null;
    }

    public Function getValidate() {
        return null;
    }

    public Function getPersist() {
        return null;
    }

    public Function getComplete() {
        return null;
    }

    public Function getReplace() {
        return null;
    }

    public Function getResolve() {
        return null;
    }

    public boolean isPrimitive() {
        return true;
    }

    public boolean isEmpty() {
        return true;
    }

    public boolean isStrict() {
        return true;
    }

    private static class EmptySection implements Section {
        private final List<String> list = new LinkedList();
        private final Scanner scanner;

        public EmptySection(Scanner scanner2) {
            this.scanner = scanner2;
        }

        public Iterator<String> iterator() {
            return this.list.iterator();
        }

        public String getName() {
            return null;
        }

        public String getPrefix() {
            return null;
        }

        public Label getText() {
            return null;
        }

        public LabelMap getElements() {
            return new LabelMap(this.scanner);
        }

        public LabelMap getAttributes() {
            return new LabelMap(this.scanner);
        }

        public Label getElement(String name) {
            return null;
        }

        public Section getSection(String name) {
            return null;
        }

        public String getPath(String name) {
            return null;
        }

        public String getAttribute(String name) {
            return null;
        }

        public boolean isSection(String name) {
            return false;
        }
    }
}
