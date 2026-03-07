package com.microsoft.cll.android;

import com.microsoft.cll.android.SettingsStore;
import java.util.Iterator;
import org.json.JSONObject;

public class HostSettings extends AbstractSettings {
    private final String baseUrl = "https://settings.data.microsoft.com/settings/v2.0/telemetry/";

    public HostSettings(ClientTelemetry clientTelemetry, ILogger logger, String iKey, PartA partA) {
        super(clientTelemetry, logger, partA);
        this.TAG = "AndroidCll-HostSettings";
        this.ETagSettingName = SettingsStore.Settings.HOSTSETTINGSETAG;
        this.disableUploadOn404 = true;
        this.endpoint = "https://settings.data.microsoft.com/settings/v2.0/telemetry/" + iKey;
        this.queryParam = "?os=" + partA.osName + "&osVer=" + partA.osVer + "&deviceClass=" + partA.deviceExt.getDeviceClass() + "&deviceId=" + partA.deviceExt.getLocalId();
    }

    public void ParseSettings(JSONObject resultJson) {
        if (resultJson != null) {
            try {
                if (resultJson.has("settings")) {
                    JSONObject jsonSettings = (JSONObject) resultJson.get("settings");
                    Iterator<String> keys = jsonSettings.keys();
                    while (keys.hasNext()) {
                        String key = keys.next();
                        String value = jsonSettings.getString(key);
                        if (key.split(":").length != 4) {
                            this.logger.error(this.TAG, "Bad Settings Format");
                        }
                        SettingsStore.updateHostSetting(key.toUpperCase(), value.replaceAll(" ", "").replaceAll("_", "").toUpperCase());
                    }
                    return;
                }
            } catch (Exception e) {
                this.logger.error(this.TAG, "An exception occurred while parsing settings");
                return;
            }
        }
        this.logger.info(this.TAG, "Json result did not contain a \"settings\" field!");
    }
}
