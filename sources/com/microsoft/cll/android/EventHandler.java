package com.microsoft.cll.android;

import com.microsoft.cll.android.EventEnums;
import com.microsoft.cll.android.FileStorage;
import com.microsoft.cll.android.SettingsStore;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

public class EventHandler extends ScheduledWorker {
    static final /* synthetic */ boolean $assertionsDisabled = (!EventHandler.class.desiredAssertionStatus());
    private final String TAG = "AndroidCll-EventHandler";
    private final ClientTelemetry clientTelemetry;
    private final List<ICllEvents> cllEvents;
    final AbstractHandler criticalHandler;
    private URL endpoint;
    private final ILogger logger;
    final AbstractHandler normalHandler;
    private double sampleId;
    private EventSender sender;
    private ITicketCallback ticketCallback;

    protected EventHandler(ClientTelemetry clientTelemetry2, List<ICllEvents> cllEvents2, ILogger logger2, AbstractHandler normalEventHandler, AbstractHandler criticalEventAbstractHandler) {
        super((long) SettingsStore.getCllSettingsAsInt(SettingsStore.Settings.QUEUEDRAININTERVAL));
        this.clientTelemetry = clientTelemetry2;
        this.cllEvents = cllEvents2;
        this.logger = logger2;
        this.normalHandler = normalEventHandler;
        this.criticalHandler = criticalEventAbstractHandler;
        this.sampleId = -1.0d;
    }

    public EventHandler(ClientTelemetry clientTelemetry2, List<ICllEvents> cllEvents2, ILogger logger2, String filePath) {
        super((long) SettingsStore.getCllSettingsAsInt(SettingsStore.Settings.QUEUEDRAININTERVAL));
        this.clientTelemetry = clientTelemetry2;
        this.cllEvents = cllEvents2;
        this.logger = logger2;
        this.criticalHandler = new CriticalEventHandler(logger2, filePath, clientTelemetry2);
        this.normalHandler = new NormalEventHandler(logger2, filePath, clientTelemetry2);
        this.sampleId = -1.0d;
    }

    public void run() {
        if (this.interval != ((long) SettingsStore.getCllSettingsAsInt(SettingsStore.Settings.QUEUEDRAININTERVAL))) {
            this.nextExecution.cancel(false);
            this.interval = (long) SettingsStore.getCllSettingsAsInt(SettingsStore.Settings.QUEUEDRAININTERVAL);
            this.nextExecution = this.executor.scheduleAtFixedRate(this, this.interval, this.interval, TimeUnit.SECONDS);
        }
        if (EventQueueWriter.future != null) {
            this.logger.info("AndroidCll-EventHandler", "Retry logic in progress, skipping normal send");
        } else {
            send();
        }
    }

    public void stop() {
        super.stop();
        this.normalHandler.close();
        this.criticalHandler.close();
    }

    /* access modifiers changed from: protected */
    public boolean log(SerializedEvent event, List<String> ids) {
        boolean isSenderBusy = false;
        if (Filter(event)) {
            return false;
        }
        if (EventQueueWriter.getRunningThreadCount() >= SettingsStore.getCllSettingsAsInt(SettingsStore.Settings.MAXEVENTSIZEINBYTES)) {
            isSenderBusy = true;
        }
        if (event.getLatency() == EventEnums.Latency.LatencyRealtime && !this.isPaused && !isSenderBusy) {
            if (startEventQueueWriter(new EventQueueWriter(this.endpoint, event, ids, this.clientTelemetry, this.cllEvents, this.logger, this.executor, this, this.ticketCallback))) {
                return true;
            }
        }
        return addToStorage(event, ids);
    }

    /* access modifiers changed from: protected */
    public boolean addToStorage(SerializedEvent event, List<String> ids) {
        switch (event.getPersistence()) {
            case PersistenceNormal:
                break;
            case PersistenceCritical:
                try {
                    this.criticalHandler.add(event.getSerializedData(), ids);
                    break;
                } catch (IOException e) {
                    this.logger.error("AndroidCll-EventHandler", "Could not add event to normal storage");
                    return false;
                } catch (FileStorage.FileFullException e2) {
                    this.logger.warn("AndroidCll-EventHandler", "No space on disk to store events");
                    return false;
                }
            default:
                this.logger.error("AndroidCll-EventHandler", "Unknown persistence");
                if (!$assertionsDisabled) {
                    throw new AssertionError();
                }
                break;
        }
        try {
            this.normalHandler.add(event.getSerializedData(), ids);
            return true;
        } catch (IOException e3) {
            this.logger.error("AndroidCll-EventHandler", "Could not add event to normal storage");
            return false;
        } catch (FileStorage.FileFullException e4) {
            this.logger.warn("AndroidCll-EventHandler", "No space on disk to store events");
            return false;
        }
    }

    private boolean Filter(SerializedEvent event) {
        if (event.getSerializedData().length() > SettingsStore.getCllSettingsAsInt(SettingsStore.Settings.MAXEVENTSIZEINBYTES)) {
            this.logger.info("AndroidCll-EventHandler", "Event is too large");
            return true;
        } else if (IsUploadEnabled() && IsInSample(event)) {
            return false;
        } else {
            this.logger.info("AndroidCll-EventHandler", "Filtered event");
            return true;
        }
    }

    private boolean IsInSample(SerializedEvent event) {
        if (this.sampleId < -1.0E-5d) {
            this.sampleId = EventEnums.SampleRate_0_percent;
            String deviceId = event.getDeviceId();
            if (deviceId != null && deviceId.length() > 7) {
                try {
                    this.sampleId = ((double) (Long.parseLong(deviceId.substring(deviceId.length() - 7), 16) % 10000)) / 100.0d;
                } catch (NumberFormatException e) {
                }
            }
            this.logger.info("AndroidCll-EventHandler", "Sample Id is " + String.valueOf(this.sampleId) + " based on deviceId of " + deviceId);
        }
        if (this.sampleId < event.getSampleRate() + 1.0E-5d) {
            return true;
        }
        return false;
    }

    private boolean IsUploadEnabled() {
        if (!SettingsStore.getCllSettingsAsBoolean(SettingsStore.Settings.UPLOADENABLED)) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: protected */
    public boolean send() {
        return send((EventEnums.Persistence) null);
    }

    /* access modifiers changed from: protected */
    public boolean send(EventEnums.Persistence persistence) {
        if (this.isPaused) {
            return false;
        }
        List<IStorage> storages = null;
        if (persistence != null) {
            switch (persistence) {
                case PersistenceNormal:
                    this.logger.info("AndroidCll-EventHandler", "Draining normal events");
                    storages = this.normalHandler.getFilesForDraining();
                    break;
                case PersistenceCritical:
                    this.logger.info("AndroidCll-EventHandler", "Draining Critical events");
                    storages = this.criticalHandler.getFilesForDraining();
                    break;
                default:
                    this.logger.error("AndroidCll-EventHandler", "Unknown persistence");
                    if (!$assertionsDisabled) {
                        throw new AssertionError();
                    }
                    break;
            }
        } else {
            this.logger.info("AndroidCll-EventHandler", "Draining All events");
            storages = this.normalHandler.getFilesForDraining();
            storages.addAll(this.criticalHandler.getFilesForDraining());
        }
        if (storages == null || storages.size() == 0) {
            return true;
        }
        return startEventQueueWriter(new EventQueueWriter(this.endpoint, storages, this.clientTelemetry, this.cllEvents, this.logger, this.executor, this.ticketCallback));
    }

    /* access modifiers changed from: protected */
    public void setEndpointUrl(String endpointUrl) {
        try {
            this.endpoint = new URL(endpointUrl);
        } catch (MalformedURLException e) {
            this.logger.error("AndroidCll-EventHandler", "Bad Endpoint URL Form");
        }
    }

    /* access modifiers changed from: package-private */
    public void setSender(EventSender sender2) {
        this.sender = sender2;
    }

    /* access modifiers changed from: package-private */
    public void synchronize() {
        ((NormalEventHandler) this.normalHandler).writeQueueToDisk();
    }

    /* access modifiers changed from: package-private */
    public void setXuidCallback(ITicketCallback callback) {
        this.ticketCallback = callback;
    }

    private boolean startEventQueueWriter(Runnable r) {
        if (this.endpoint == null) {
            this.logger.warn("AndroidCll-EventHandler", "No endpoint set");
            return false;
        }
        EventQueueWriter eqw = (EventQueueWriter) r;
        if (this.sender != null) {
            eqw.setSender(this.sender);
        }
        try {
            this.executor.execute(r);
        } catch (RejectedExecutionException e) {
            this.logger.warn("AndroidCll-EventHandler", "Could not start new thread for EventQueueWriter");
            return false;
        } catch (NullPointerException e2) {
            this.logger.error("AndroidCll-EventHandler", "Executor is null. Is the cll paused or stopped?");
        }
        return true;
    }
}
