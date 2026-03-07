package com.microsoft.cll.android;

import com.microsoft.cll.android.SettingsStore;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import net.hockeyapp.android.utils.HttpURLConnectionBuilder;

public class EventQueueWriter implements Runnable {
    private static int backoffSeconds = 0;
    protected static ScheduledFuture future;
    protected static AtomicBoolean running = new AtomicBoolean(false);
    private static AtomicInteger s_threadCount = new AtomicInteger(0);
    private final String TAG = "AndroidCll-EventQueueWriter";
    private final EventBatcher batcher;
    private final ClientTelemetry clientTelemetry;
    private final List<ICllEvents> cllEvents;
    private EventCompressor compressor;
    private URL endpoint;
    private final SerializedEvent event;
    private final ScheduledExecutorService executorService;
    private EventHandler handler;
    private final List<String> ids;
    private final ILogger logger;
    private final Random random = new Random();
    private List<IStorage> removedStorages;
    private EventSender sender;
    private final List<IStorage> storages;
    private final ITicketCallback ticketCallback;
    private final TicketManager ticketManager;

    enum SendResult {
        SUCCESS,
        ERROR
    }

    public EventQueueWriter(URL endpoint2, List<IStorage> storages2, ClientTelemetry clientTelemetry2, List<ICllEvents> cllEvents2, ILogger logger2, ScheduledExecutorService executorService2, ITicketCallback ticketCallback2) {
        this.cllEvents = cllEvents2;
        this.storages = storages2;
        this.logger = logger2;
        this.ticketCallback = ticketCallback2;
        this.batcher = new EventBatcher();
        this.sender = new EventSender(endpoint2, clientTelemetry2, logger2);
        this.compressor = new EventCompressor(logger2);
        this.event = null;
        this.ids = null;
        this.executorService = executorService2;
        this.clientTelemetry = clientTelemetry2;
        this.endpoint = endpoint2;
        this.removedStorages = new ArrayList();
        this.ticketManager = new TicketManager(ticketCallback2, logger2);
    }

    public EventQueueWriter(URL endpoint2, SerializedEvent event2, List<String> ids2, ClientTelemetry clientTelemetry2, List<ICllEvents> cllEvents2, ILogger logger2, ScheduledExecutorService executorService2, EventHandler handler2, ITicketCallback ticketCallback2) {
        this.cllEvents = cllEvents2;
        this.event = event2;
        this.ids = ids2;
        this.logger = logger2;
        this.ticketCallback = ticketCallback2;
        this.sender = new EventSender(endpoint2, clientTelemetry2, logger2);
        this.batcher = null;
        this.storages = null;
        this.executorService = executorService2;
        this.clientTelemetry = clientTelemetry2;
        this.handler = handler2;
        this.endpoint = endpoint2;
        this.ticketManager = new TicketManager(ticketCallback2, logger2);
        clientTelemetry2.IncrementEventsQueuedForUpload();
    }

    /* access modifiers changed from: package-private */
    public void setSender(EventSender sender2) {
        this.sender = sender2;
    }

    public void run() {
        try {
            s_threadCount.getAndAdd(1);
            this.logger.info("AndroidCll-EventQueueWriter", "Starting upload");
            if (this.storages == null) {
                sendRealTimeEvent(this.event);
            } else if (!running.compareAndSet(false, true)) {
                this.logger.info("AndroidCll-EventQueueWriter", "Skipping send, event sending is already in progress on different thread.");
                s_threadCount.getAndAdd(-1);
            } else {
                send();
                running.set(false);
                s_threadCount.getAndAdd(-1);
            }
        } finally {
            s_threadCount.getAndAdd(-1);
        }
    }

    /* access modifiers changed from: protected */
    public void sendRealTimeEvent(SerializedEvent singleEvent) {
        String eventString = singleEvent.getSerializedData();
        if (eventString.length() <= SettingsStore.getCllSettingsAsInt(SettingsStore.Settings.MAXEVENTSIZEINBYTES)) {
            boolean sendCompleted = false;
            try {
                this.ticketManager.clean();
                this.ticketManager.addTickets(this.ids);
                TicketHeaders ticketHeaders = this.ticketManager.getHeaders(false);
                byte[] eventData = getEventData(eventString);
                int sendErrorCode = this.sender.sendEvent(eventData, false, ticketHeaders);
                if (sendErrorCode == 401) {
                    sendErrorCode = this.sender.sendEvent(eventData, false, this.ticketManager.getHeaders(true));
                }
                if (sendErrorCode == 200 || sendErrorCode == 400) {
                    sendCompleted = true;
                }
            } catch (IOException e) {
                this.logger.error("AndroidCll-EventQueueWriter", "Cannot send event");
            }
            if (sendCompleted) {
                cancelBackoff();
                for (ICllEvents event2 : this.cllEvents) {
                    event2.sendComplete();
                }
                return;
            }
            this.handler.addToStorage(singleEvent, this.ids);
        }
    }

    /* access modifiers changed from: protected */
    public void send() {
        if (sendInternal() == SendResult.SUCCESS) {
            cancelBackoff();
            return;
        }
        int interval = generateBackoffInterval();
        this.storages.removeAll(this.removedStorages);
        EventQueueWriter eventQueueWriter = new EventQueueWriter(this.endpoint, this.storages, this.clientTelemetry, this.cllEvents, this.logger, this.executorService, this.ticketCallback);
        eventQueueWriter.setSender(this.sender);
        future = this.executorService.schedule(eventQueueWriter, (long) interval, TimeUnit.SECONDS);
    }

    private SendResult sendInternal() {
        for (IStorage storage : this.storages) {
            if (this.executorService.isShutdown()) {
                return SendResult.SUCCESS;
            }
            this.ticketManager.clean();
            for (Tuple<String, List<String>> event2 : storage.drain()) {
                this.ticketManager.addTickets((List) event2.b);
                this.clientTelemetry.IncrementEventsQueuedForUpload();
                if (((String) event2.a).length() > SettingsStore.getCllSettingsAsInt(SettingsStore.Settings.MAXEVENTSIZEINBYTES)) {
                    this.logger.warn("AndroidCll-EventQueueWriter", "Dropping event because it is too large.");
                    for (ICllEvents cllEvent : this.cllEvents) {
                        cllEvent.eventDropped((String) event2.a);
                    }
                } else if (!this.batcher.tryAddingEventToBatch((String) event2.a)) {
                    this.logger.info("AndroidCll-EventQueueWriter", "Got a full batch, preparing to send");
                    String batchedEvents = this.batcher.getBatchedEvents();
                    if (!this.batcher.tryAddingEventToBatch((String) event2.a)) {
                        this.logger.error("AndroidCll-EventQueueWriter", "Could not add events to an empty batch");
                    }
                    SendResult sendResult = sendBatch(batchedEvents, storage);
                    if (sendResult == SendResult.ERROR) {
                        storage.close();
                        return sendResult;
                    }
                } else {
                    continue;
                }
            }
            this.logger.info("AndroidCll-EventQueueWriter", "Preparing to send");
            SendResult sendResult2 = sendBatch(this.batcher.getBatchedEvents(), storage);
            storage.close();
            if (sendResult2 == SendResult.ERROR) {
                return sendResult2;
            }
            storage.discard();
        }
        this.logger.info("AndroidCll-EventQueueWriter", "Sent " + this.clientTelemetry.snapshot.getEventsQueued() + " events.");
        for (ICllEvents event3 : this.cllEvents) {
            event3.sendComplete();
        }
        return SendResult.SUCCESS;
    }

    private void cancelBackoff() {
        future = null;
        backoffSeconds = 0;
    }

    private SendResult sendBatch(String batchedEvents, IStorage storage) {
        this.logger.info("AndroidCll-EventQueueWriter", "Sending Batch of events");
        if (batchedEvents.equals("")) {
            this.removedStorages.add(storage);
            return SendResult.SUCCESS;
        }
        this.logger.info("AndroidCll-EventQueueWriter", "Compressing events");
        boolean isCompressed = true;
        byte[] eventsData = this.compressor.compress(batchedEvents);
        if (eventsData == null) {
            eventsData = getEventData(batchedEvents);
            isCompressed = false;
        }
        boolean sendCompleted = false;
        try {
            int sendErrorCode = this.sender.sendEvent(eventsData, isCompressed, this.ticketManager.getHeaders(false));
            if (sendErrorCode == 401) {
                this.logger.info("AndroidCll-EventQueueWriter", "We got a 401 while sending the events, refreshing the tokens and trying again");
                sendErrorCode = this.sender.sendEvent(eventsData, isCompressed, this.ticketManager.getHeaders(true));
                if (sendErrorCode == 401) {
                    this.logger.info("AndroidCll-EventQueueWriter", "After refreshing the tokens we still got a 401. Most likely we couldn't get new tokens so we will keep these events on disk and try to get new tokens later");
                }
            }
            if (sendErrorCode == 200 || sendErrorCode == 400) {
                sendCompleted = true;
            }
        } catch (IOException e) {
            this.logger.error("AndroidCll-EventQueueWriter", "Cannot send event: " + e.getMessage());
        }
        if (sendCompleted) {
            return SendResult.SUCCESS;
        }
        return SendResult.ERROR;
    }

    /* access modifiers changed from: package-private */
    public int generateBackoffInterval() {
        int startInterval = SettingsStore.getCllSettingsAsInt(SettingsStore.Settings.CONSTANTFORRETRYPERIOD);
        int maxInterval = SettingsStore.getCllSettingsAsInt(SettingsStore.Settings.MAXRETRYPERIOD);
        int exponentBase = SettingsStore.getCllSettingsAsInt(SettingsStore.Settings.BASERETRYPERIOD);
        if (backoffSeconds == 0) {
            backoffSeconds = Math.max(0, startInterval);
        }
        if (this.logger.getVerbosity() == Verbosity.INFO) {
            this.logger.info("AndroidCll-EventQueueWriter", "Generating new backoff interval using \"Random.nextInt(" + (backoffSeconds + 1) + ") seconds\" formula.");
        }
        int interval = this.random.nextInt(backoffSeconds + 1);
        backoffSeconds = Math.min(backoffSeconds * exponentBase, maxInterval);
        if (this.logger.getVerbosity() == Verbosity.INFO) {
            this.logger.info("AndroidCll-EventQueueWriter", "The generated backoff interval is " + interval + ".");
        }
        return interval;
    }

    private byte[] getEventData(String body) {
        return body.getBytes(Charset.forName(HttpURLConnectionBuilder.DEFAULT_CHARSET));
    }

    public static int getRunningThreadCount() {
        return s_threadCount.get();
    }
}
