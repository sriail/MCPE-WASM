package com.microsoft.cll.android;

import com.microsoft.cll.android.EventEnums;
import com.microsoft.cll.android.SettingsStore;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SnapshotScheduler extends ScheduledWorker {
    private final String TAG = "AndroidCll-SnapshotScheduler";
    private final ClientTelemetry clientTelemetry;
    private final ISingletonCll cll;
    private final ILogger logger;

    public SnapshotScheduler(ClientTelemetry clientTelemetry2, ILogger logger2, ISingletonCll cll2) {
        super(SettingsStore.getCllSettingsAsLong(SettingsStore.Settings.SNAPSHOTSCHEDULEINTERVAL));
        this.cll = cll2;
        this.clientTelemetry = clientTelemetry2;
        this.logger = logger2;
    }

    public void start(ScheduledExecutorService executor) {
        this.executor = executor;
        this.nextExecution = executor.scheduleAtFixedRate(this, this.interval, this.interval, TimeUnit.SECONDS);
    }

    public void resume(ScheduledExecutorService executor) {
        this.executor = executor;
        this.nextExecution = executor.scheduleAtFixedRate(this, this.interval, this.interval, TimeUnit.SECONDS);
        this.isPaused = false;
    }

    public void run() {
        this.logger.info("AndroidCll-SnapshotScheduler", "Uploading snapshot");
        if (this.interval != ((long) SettingsStore.getCllSettingsAsInt(SettingsStore.Settings.SNAPSHOTSCHEDULEINTERVAL))) {
            this.nextExecution.cancel(false);
            this.interval = (long) SettingsStore.getCllSettingsAsInt(SettingsStore.Settings.SNAPSHOTSCHEDULEINTERVAL);
            this.nextExecution = this.executor.scheduleAtFixedRate(this, this.interval, this.interval, TimeUnit.SECONDS);
        }
        recordStatistics();
    }

    private void recordStatistics() {
        this.cll.log(this.clientTelemetry.GetEvent(), EventEnums.Latency.LatencyUnspecified, EventEnums.Persistence.PersistenceUnspecified, EnumSet.of(EventEnums.Sensitivity.SensitivityUnspecified), -1.0d, (List<String>) null);
        this.clientTelemetry.Reset();
    }
}
