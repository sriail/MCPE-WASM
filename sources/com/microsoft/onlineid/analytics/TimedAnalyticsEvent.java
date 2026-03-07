package com.microsoft.onlineid.analytics;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.microsoft.onlineid.internal.Assertion;
import com.microsoft.onlineid.internal.log.Logger;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class TimedAnalyticsEvent implements ITimedAnalyticsEvent {
    private static final long StartTimeNotSet = -1;
    private final HitBuilders.TimingBuilder _builder;
    private long _startTime = -1;
    private final Tracker _tracker;

    TimedAnalyticsEvent(Tracker tracker, String category, String name, String label) {
        Assertion.check((category == null || name == null) ? false : true);
        this._tracker = tracker;
        this._builder = new HitBuilders.TimingBuilder();
        this._builder.setCategory(category);
        this._builder.setVariable(name);
        if (label != null) {
            this._builder.setLabel(label);
        }
    }

    public TimedAnalyticsEvent setLabel(String label) {
        this._builder.setLabel(label);
        return this;
    }

    public TimedAnalyticsEvent start() {
        this._startTime = System.nanoTime();
        return this;
    }

    public void end() {
        if (this._startTime != -1) {
            this._builder.setValue(TimeUnit.MILLISECONDS.convert(System.nanoTime() - this._startTime, TimeUnit.NANOSECONDS));
            send(this._builder.build());
            return;
        }
        Logger.error("TimedAnalyticsEvent.end() called before start().");
    }

    /* access modifiers changed from: protected */
    public void send(Map<String, String> params) {
        this._tracker.send(params);
    }
}
