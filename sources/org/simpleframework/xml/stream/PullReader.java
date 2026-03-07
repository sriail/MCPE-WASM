package org.simpleframework.xml.stream;

import org.xmlpull.v1.XmlPullParser;

class PullReader implements EventReader {
    private XmlPullParser parser;
    private EventNode peek;

    public PullReader(XmlPullParser parser2) {
        this.parser = parser2;
    }

    public EventNode peek() throws Exception {
        if (this.peek == null) {
            this.peek = next();
        }
        return this.peek;
    }

    public EventNode next() throws Exception {
        EventNode next = this.peek;
        if (next == null) {
            return read();
        }
        this.peek = null;
        return next;
    }

    private EventNode read() throws Exception {
        int event = this.parser.next();
        if (event == 1) {
            return null;
        }
        if (event == 2) {
            return start();
        }
        if (event == 4) {
            return text();
        }
        if (event == 3) {
            return end();
        }
        return read();
    }

    private Text text() throws Exception {
        return new Text(this.parser);
    }

    private Start start() throws Exception {
        Start event = new Start(this.parser);
        if (event.isEmpty()) {
            return build(event);
        }
        return event;
    }

    private Start build(Start event) throws Exception {
        int count = this.parser.getAttributeCount();
        for (int i = 0; i < count; i++) {
            Entry entry = attribute(i);
            if (!entry.isReserved()) {
                event.add(entry);
            }
        }
        return event;
    }

    private Entry attribute(int index) throws Exception {
        return new Entry(this.parser, index);
    }

    private End end() throws Exception {
        return new End();
    }

    private static class Entry extends EventAttribute {
        private final String name;
        private final String prefix;
        private final String reference;
        private final XmlPullParser source;
        private final String value;

        public Entry(XmlPullParser source2, int index) {
            this.reference = source2.getAttributeNamespace(index);
            this.prefix = source2.getAttributePrefix(index);
            this.value = source2.getAttributeValue(index);
            this.name = source2.getAttributeName(index);
            this.source = source2;
        }

        public String getName() {
            return this.name;
        }

        public String getValue() {
            return this.value;
        }

        public boolean isReserved() {
            return false;
        }

        public String getReference() {
            return this.reference;
        }

        public String getPrefix() {
            return this.prefix;
        }

        public Object getSource() {
            return this.source;
        }
    }

    private static class Start extends EventElement {
        private final int line;
        private final String name;
        private final String prefix;
        private final String reference;
        private final XmlPullParser source;

        public Start(XmlPullParser source2) {
            this.reference = source2.getNamespace();
            this.line = source2.getLineNumber();
            this.prefix = source2.getPrefix();
            this.name = source2.getName();
            this.source = source2;
        }

        public int getLine() {
            return this.line;
        }

        public String getName() {
            return this.name;
        }

        public String getReference() {
            return this.reference;
        }

        public String getPrefix() {
            return this.prefix;
        }

        public Object getSource() {
            return this.source;
        }
    }

    private static class Text extends EventToken {
        private final XmlPullParser source;
        private final String text;

        public Text(XmlPullParser source2) {
            this.text = source2.getText();
            this.source = source2;
        }

        public boolean isText() {
            return true;
        }

        public String getValue() {
            return this.text;
        }

        public Object getSource() {
            return this.source;
        }
    }

    private static class End extends EventToken {
        private End() {
        }

        public boolean isEnd() {
            return true;
        }
    }
}
