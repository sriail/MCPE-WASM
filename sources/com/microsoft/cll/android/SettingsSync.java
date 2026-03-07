package com.microsoft.cll.android;

import com.microsoft.cll.android.SettingsStore;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;

public class SettingsSync extends ScheduledWorker {
    private final String TAG = "AndroidCll-SettingsSync";
    private final ClientTelemetry clientTelemetry;
    private final ILogger logger;
    private final List<AbstractSettings> settingsList;

    public SettingsSync(ClientTelemetry clientTelemetry2, ILogger logger2, String iKey, PartA partA) {
        super(SettingsStore.getCllSettingsAsLong(SettingsStore.Settings.SYNCREFRESHINTERVAL));
        this.clientTelemetry = clientTelemetry2;
        this.logger = logger2;
        this.settingsList = new ArrayList();
        this.settingsList.add(new CllSettings(clientTelemetry2, logger2, this, partA));
        if (!iKey.equals("")) {
            this.settingsList.add(new HostSettings(clientTelemetry2, logger2, iKey, partA));
        }
    }

    public void run() {
        this.logger.info("AndroidCll-SettingsSync", "Cloud sync!");
        GetCloudSettings();
    }

    private void GetCloudSettings() {
        for (AbstractSettings abstractSettings : this.settingsList) {
            JSONObject json = abstractSettings.getSettings();
            if (json == null) {
                this.logger.error("AndroidCll-SettingsSync", "Could not get or parse settings");
            } else {
                abstractSettings.ParseSettings(json);
            }
        }
    }
}
