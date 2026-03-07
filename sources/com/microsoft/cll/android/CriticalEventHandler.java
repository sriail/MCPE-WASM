package com.microsoft.cll.android;

import com.microsoft.cll.android.EventEnums;
import com.microsoft.cll.android.FileStorage;
import java.io.IOException;
import java.util.List;

public class CriticalEventHandler extends AbstractHandler {
    private final String TAG = "AndroidCll-CriticalEventHandler";

    public CriticalEventHandler(ILogger logger, String filePath, ClientTelemetry clientTelemetry) {
        super(logger, filePath, clientTelemetry);
        this.fileStorage = new FileStorage(".crit.cllevent", logger, filePath, this);
    }

    public synchronized void add(String event, List<String> ids) throws IOException, FileStorage.FileFullException {
        Tuple<String, List<String>> tuple = new Tuple<>(event, ids);
        if (!ensureCanAdd(tuple, EventEnums.Persistence.PersistenceCritical)) {
            this.clientTelemetry.IncrementEventsDroppedDueToQuota();
            this.logger.warn("AndroidCll-CriticalEventHandler", "Out of storage space for critical events. Logged event was dropped.");
        }
        if (!this.fileStorage.canAdd(tuple)) {
            this.logger.info("AndroidCll-CriticalEventHandler", "Closing full file and opening a new one");
            this.fileStorage.close();
            this.fileStorage = new FileStorage(".crit.cllevent", this.logger, this.filePath, this);
        }
        this.fileStorage.add(tuple);
        totalStorageUsed.getAndAdd((long) event.length());
        this.fileStorage.flush();
    }

    public synchronized List<IStorage> getFilesForDraining() {
        List<IStorage> storageList;
        if (this.fileStorage.size() > 0) {
            this.fileStorage.close();
            storageList = getFilesByExtensionForDraining(".crit.cllevent");
            this.fileStorage = new FileStorage(".crit.cllevent", this.logger, this.filePath, this);
        } else {
            storageList = getFilesByExtensionForDraining(".crit.cllevent");
        }
        return storageList;
    }

    public void close() {
        this.logger.info("AndroidCll-CriticalEventHandler", "Closing critical file");
        this.fileStorage.close();
    }

    public void dispose(IStorage storage) {
        totalStorageUsed.getAndAdd(-1 * storage.size());
    }
}
