package com.microsoft.cll.android;

import com.microsoft.cll.android.EventEnums;

public class SerializedEvent {
    private String deviceId;
    private EventEnums.Latency latency;
    private EventEnums.Persistence persistence;
    private double sampleRate;
    private String serializedData;

    public String getSerializedData() {
        return this.serializedData;
    }

    public void setSerializedData(String serializedData2) {
        this.serializedData = serializedData2;
    }

    public EventEnums.Latency getLatency() {
        return this.latency;
    }

    public void setLatency(EventEnums.Latency latency2) {
        this.latency = latency2;
    }

    public EventEnums.Persistence getPersistence() {
        return this.persistence;
    }

    public void setPersistence(EventEnums.Persistence persistence2) {
        this.persistence = persistence2;
    }

    public double getSampleRate() {
        return this.sampleRate;
    }

    public void setSampleRate(double sampleRate2) {
        this.sampleRate = sampleRate2;
    }

    public String getDeviceId() {
        return this.deviceId;
    }

    public void setDeviceId(String deviceId2) {
        this.deviceId = deviceId2;
    }
}
