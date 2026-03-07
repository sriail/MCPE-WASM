package com.microsoft.cll.android;

import Microsoft.Telemetry.Base;
import android.content.Context;
import android.content.SharedPreferences;
import com.microsoft.cll.android.EventEnums;
import com.microsoft.cll.android.SettingsStore;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

public class AndroidCll implements ICll, SettingsStore.UpdateListener {
    private final String TAG;
    protected ISingletonCll cll;
    private final SharedPreferences cllPreferences;
    private final SharedPreferences hostPreferences;
    private final ILogger logger;
    private final String sharedCllPreferencesName;
    private final String sharedHostPreferencesName;

    public AndroidCll(String iKey, Context context) {
        this.TAG = "AndroidCll-AndroidCll";
        this.logger = AndroidLogger.getInstance();
        this.sharedCllPreferencesName = "AndroidCllSettingsSharedPreferences";
        this.sharedHostPreferencesName = "AndroidHostSettingsSharedPreferences";
        CorrelationVector correlationVector = new CorrelationVector();
        this.cll = SingletonCll.getInstance(iKey, AndroidLogger.getInstance(), context.getFilesDir().getPath(), new AndroidPartA(AndroidLogger.getInstance(), iKey, context, correlationVector), correlationVector);
        this.cllPreferences = context.getSharedPreferences("AndroidCllSettingsSharedPreferences", 0);
        this.hostPreferences = context.getSharedPreferences("AndroidHostSettingsSharedPreferences", 0);
        SettingsStore.setUpdateListener(this);
        setSettingsStoreValues();
    }

    protected AndroidCll() {
        this.TAG = "AndroidCll-AndroidCll";
        this.logger = AndroidLogger.getInstance();
        this.sharedCllPreferencesName = "AndroidCllSettingsSharedPreferences";
        this.sharedHostPreferencesName = "AndroidHostSettingsSharedPreferences";
        this.cllPreferences = null;
        this.hostPreferences = null;
    }

    public void start() {
        this.cll.start();
    }

    public void stop() {
        this.cll.stop();
    }

    public void pause() {
        this.cll.pause();
    }

    public void resume() {
        this.cll.resume();
    }

    public void log(Base event) {
        log(event, (List<String>) null);
    }

    public void log(Base event, List<String> ids) {
        log(event, EventEnums.Latency.LatencyUnspecified, EventEnums.Persistence.PersistenceUnspecified, EnumSet.of(EventEnums.Sensitivity.SensitivityUnspecified), -1.0d, ids);
    }

    public void log(Base event, EventEnums.Latency latency, EventEnums.Persistence persistence, EnumSet<EventEnums.Sensitivity> sensitivity, double sampleRate, List<String> ids) {
        this.cll.log(PreSerializedEvent.createFromStaticEvent(this.logger, event), latency, persistence, sensitivity, sampleRate, ids);
    }

    public void log(String eventName, String eventData, EventEnums.Latency latency, EventEnums.Persistence persistence, EnumSet<EventEnums.Sensitivity> sensitivity, double sampleRate, List<String> ids) {
        if (!eventName.contains(".")) {
            this.logger.error("AndroidCll-AndroidCll", "Event Name does not follow a valid format. Your event must have at least one . between two words. E.g. Microsoft.MyEvent");
            return;
        }
        this.cll.log(PreSerializedEvent.createFromDynamicEvent(eventName, eventData), latency, persistence, sensitivity, sampleRate, ids);
    }

    public void logInternal(com.microsoft.telemetry.Base testEvent) {
        this.cll.log(testEvent, (EventEnums.Latency) null, (EventEnums.Persistence) null, (EnumSet<EventEnums.Sensitivity>) null, -1.0d, (List<String>) null);
    }

    public void setDebugVerbosity(Verbosity verbosity) {
        this.cll.setDebugVerbosity(verbosity);
    }

    public void send() {
        this.cll.send();
    }

    public void setEndpointUrl(String url) {
        this.cll.setEndpointUrl(url);
    }

    public void useLegacyCS(boolean value) {
        this.cll.useLegacyCS(value);
    }

    public void setExperimentId(String id) {
        this.cll.setExperimentId(id);
    }

    public void synchronize() {
        this.cll.synchronize();
    }

    public void SubscribeCllEvents(ICllEvents cllEvents) {
        this.cll.SubscribeCllEvents(cllEvents);
    }

    public void setAppUserId(String userId) {
        this.cll.setAppUserId(userId);
    }

    public String getAppUserId() {
        return this.cll.getAppUserId();
    }

    public CorrelationVector getCorrelationVector() {
        return ((SingletonCll) this.cll).correlationVector;
    }

    public void setXuidCallback(ITicketCallback callback) {
        this.cll.setXuidCallback(callback);
    }

    public void OnHostSettingUpdate(String settingName, String settingValue) {
        SharedPreferences.Editor editor = this.hostPreferences.edit();
        editor.putString(settingName, settingValue);
        editor.apply();
    }

    public void OnCllSettingUpdate(String settingName, String settingValue) {
        SharedPreferences.Editor editor = this.cllPreferences.edit();
        editor.putString(settingName, settingValue);
        editor.apply();
    }

    private void setSettingsStoreValues() {
        for (Map.Entry<String, String> setting : this.cllPreferences.getAll().entrySet()) {
            try {
                SettingsStore.updateCllSetting(SettingsStore.Settings.valueOf(setting.getKey()), setting.getValue());
            } catch (Exception e) {
                SharedPreferences.Editor editor = this.cllPreferences.edit();
                editor.remove(setting.getKey());
                editor.apply();
            }
        }
        for (Map.Entry<String, String> setting2 : this.hostPreferences.getAll().entrySet()) {
            SettingsStore.updateHostSetting(setting2.getKey(), setting2.getValue());
        }
    }
}
