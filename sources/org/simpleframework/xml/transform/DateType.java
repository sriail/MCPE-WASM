package org.simpleframework.xml.transform;

import java.text.SimpleDateFormat;
import java.util.Date;

enum DateType {
    FULL("yyyy-MM-dd HH:mm:ss.S z"),
    LONG("yyyy-MM-dd HH:mm:ss z"),
    NORMAL("yyyy-MM-dd z"),
    SHORT("yyyy-MM-dd");
    
    private DateFormat format;

    private DateType(String format2) {
        this.format = new DateFormat(format2);
    }

    private DateFormat getFormat() {
        return this.format;
    }

    public static String getText(Date date) throws Exception {
        return FULL.getFormat().getText(date);
    }

    public static Date getDate(String text) throws Exception {
        return getType(text).getFormat().getDate(text);
    }

    public static DateType getType(String text) {
        int length = text.length();
        if (length > 23) {
            return FULL;
        }
        if (length > 20) {
            return LONG;
        }
        if (length > 11) {
            return NORMAL;
        }
        return SHORT;
    }

    private static class DateFormat {
        private SimpleDateFormat format;

        public DateFormat(String format2) {
            this.format = new SimpleDateFormat(format2);
        }

        public synchronized String getText(Date date) throws Exception {
            return this.format.format(date);
        }

        public synchronized Date getDate(String text) throws Exception {
            return this.format.parse(text);
        }
    }
}
