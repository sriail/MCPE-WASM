package com.microsoft.cll.android;

import Microsoft.Android.LoggingLibrary.Snapshot;
import Ms.Telemetry.CllHeartBeat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.TimeZone;

public class ClientTelemetry {
    private ArrayList<Integer> settingsCallLatencies = new ArrayList<>();
    protected CllHeartBeat snapshot = new CllHeartBeat();
    private ArrayList<Integer> vortexCallLatencies = new ArrayList<>();

    public ClientTelemetry() {
        Reset();
    }

    /* access modifiers changed from: protected */
    public Snapshot GetEvent() {
        Snapshot snapshotEvent = new Snapshot();
        snapshotEvent.setBaseData(this.snapshot);
        return snapshotEvent;
    }

    /* access modifiers changed from: protected */
    public void Reset() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS'Z'", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        this.snapshot.setLastHeartBeat(dateFormat.format(new Date()).toString());
        this.snapshot.setEventsQueued(0);
        this.snapshot.setLogFailures(0);
        this.snapshot.setQuotaDropCount(0);
        this.snapshot.setRejectDropCount(0);
        this.snapshot.setVortexHttpAttempts(0);
        this.snapshot.setVortexHttpFailures(0);
        this.snapshot.setCacheUsagePercent(EventEnums.SampleRate_0_percent);
        this.snapshot.setAvgVortexLatencyMs(0);
        this.snapshot.setMaxVortexLatencyMs(0);
        this.snapshot.setSettingsHttpAttempts(0);
        this.snapshot.setSettingsHttpFailures(0);
        this.snapshot.setAvgSettingsLatencyMs(0);
        this.snapshot.setMaxSettingsLatencyMs(0);
        this.snapshot.setVortexFailures4xx(0);
        this.snapshot.setVortexFailures5xx(0);
        this.snapshot.setVortexFailuresTimeout(0);
        this.snapshot.setSettingsFailures4xx(0);
        this.snapshot.setSettingsFailures5xx(0);
        this.snapshot.setSettingsFailuresTimeout(0);
        this.settingsCallLatencies.clear();
        this.vortexCallLatencies.clear();
    }

    /* access modifiers changed from: protected */
    public void IncrementEventsQueuedForUpload() {
        IncrementEventsQueuedForUpload(1);
    }

    /* access modifiers changed from: protected */
    public void IncrementEventsQueuedForUpload(int count) {
        this.snapshot.setEventsQueued(this.snapshot.getEventsQueued() + count);
    }

    /* access modifiers changed from: protected */
    public void IncrementLogFailures() {
        this.snapshot.setLogFailures(this.snapshot.getLogFailures() + 1);
    }

    /* access modifiers changed from: protected */
    public void IncrementEventsDroppedDueToQuota() {
        this.snapshot.setQuotaDropCount(this.snapshot.getQuotaDropCount() + 1);
    }

    /* access modifiers changed from: protected */
    public void IncrementSettingsHttpAttempts() {
        this.snapshot.setSettingsHttpAttempts(this.snapshot.getSettingsHttpAttempts() + 1);
    }

    /* access modifiers changed from: protected */
    public void IncrementVortexHttpAttempts() {
        this.snapshot.setVortexHttpAttempts(this.snapshot.getVortexHttpAttempts() + 1);
    }

    /* access modifiers changed from: protected */
    public void IncrementVortexHttpFailures(int errorCode) {
        this.snapshot.setVortexHttpFailures(this.snapshot.getVortexHttpFailures() + 1);
        if (errorCode >= 400 && errorCode < 500) {
            this.snapshot.setVortexFailures4xx(this.snapshot.getVortexFailures4xx() + 1);
        }
        if (errorCode >= 500 && errorCode < 600) {
            this.snapshot.setVortexFailures5xx(this.snapshot.getVortexFailures5xx() + 1);
        }
        if (errorCode == -1) {
            this.snapshot.setVortexFailuresTimeout(this.snapshot.getVortexFailuresTimeout() + 1);
        }
    }

    /* access modifiers changed from: protected */
    public void IncrementSettingsHttpFailures(int errorCode) {
        this.snapshot.setSettingsHttpFailures(this.snapshot.getSettingsHttpFailures() + 1);
        if (errorCode >= 400 && errorCode < 500) {
            this.snapshot.setSettingsFailures4xx(this.snapshot.getSettingsFailures4xx() + 1);
        }
        if (errorCode >= 500 && errorCode < 600) {
            this.snapshot.setSettingsFailures5xx(this.snapshot.getSettingsFailures5xx() + 1);
        }
        if (errorCode == -1) {
            this.snapshot.setSettingsFailuresTimeout(this.snapshot.getSettingsFailuresTimeout() + 1);
        }
    }

    /* access modifiers changed from: protected */
    public void SetCacheUsagePercent(double percent) {
        this.snapshot.setCacheUsagePercent(percent);
    }

    /* access modifiers changed from: protected */
    public void SetAvgSettingsLatencyMs(int time) {
        this.settingsCallLatencies.add(Integer.valueOf(time));
        int total = 0;
        Iterator<Integer> it = this.settingsCallLatencies.iterator();
        while (it.hasNext()) {
            total += it.next().intValue();
        }
        this.snapshot.setAvgSettingsLatencyMs(total / this.settingsCallLatencies.size());
    }

    /* access modifiers changed from: protected */
    public void SetMaxSettingsLatencyMs(int time) {
        if (this.snapshot.getMaxSettingsLatencyMs() < time) {
            this.snapshot.setMaxSettingsLatencyMs(time);
        }
    }

    /* access modifiers changed from: protected */
    public void SetAvgVortexLatencyMs(int time) {
        this.vortexCallLatencies.add(Integer.valueOf(time));
        int total = 0;
        Iterator<Integer> it = this.vortexCallLatencies.iterator();
        while (it.hasNext()) {
            total += it.next().intValue();
        }
        this.snapshot.setAvgVortexLatencyMs(total / this.vortexCallLatencies.size());
    }

    /* access modifiers changed from: protected */
    public void SetMaxVortexLatencyMs(int time) {
        if (this.snapshot.getMaxVortexLatencyMs() < time) {
            this.snapshot.setMaxVortexLatencyMs(time);
        }
    }

    /* access modifiers changed from: protected */
    public void IncrementRejectDropCount(int numberOfEventsDropped) {
        this.snapshot.setRejectDropCount(this.snapshot.getRejectDropCount() + numberOfEventsDropped);
    }
}
