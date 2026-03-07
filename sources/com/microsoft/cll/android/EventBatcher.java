package com.microsoft.cll.android;

import com.microsoft.cll.android.SettingsStore;
import org.apache.james.mime4j.util.CharsetUtil;

public class EventBatcher {
    private StringBuilder eventString;
    private final String newLine;
    private int numberOfEvents;
    private int size;

    public EventBatcher(int size2) {
        this.newLine = CharsetUtil.CRLF;
        this.size = size2;
        this.eventString = new StringBuilder(size2);
        this.numberOfEvents = 0;
    }

    public EventBatcher() {
        this.newLine = CharsetUtil.CRLF;
        this.size = SettingsStore.getCllSettingsAsInt(SettingsStore.Settings.MAXEVENTSIZEINBYTES);
        this.eventString = new StringBuilder(this.size);
        this.numberOfEvents = 0;
    }

    /* access modifiers changed from: protected */
    public boolean canAddToBatch(String serializedEvent) {
        if (this.eventString.length() + CharsetUtil.CRLF.length() + serializedEvent.length() > this.size || this.numberOfEvents >= SettingsStore.getCllSettingsAsInt(SettingsStore.Settings.MAXEVENTSPERPOST)) {
            return false;
        }
        return true;
    }

    public boolean tryAddingEventToBatch(String serializedEvent) {
        if (!canAddToBatch(serializedEvent)) {
            return false;
        }
        this.eventString.append(serializedEvent).append(CharsetUtil.CRLF);
        this.numberOfEvents++;
        return true;
    }

    public String getBatchedEvents() {
        String batchedEvents = this.eventString.toString();
        this.eventString.setLength(0);
        this.numberOfEvents = 0;
        return batchedEvents;
    }
}
