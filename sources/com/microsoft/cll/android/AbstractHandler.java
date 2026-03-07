package com.microsoft.cll.android;

import com.microsoft.cll.android.EventEnums;
import com.microsoft.cll.android.FileStorage;
import com.microsoft.cll.android.SettingsStore;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public abstract class AbstractHandler {
    protected static final String criticalEventFileExtension = ".crit.cllevent";
    protected static final String normalEventFileExtension = ".norm.cllevent";
    protected static AtomicLong totalStorageUsed = new AtomicLong(0);
    private final String TAG = "AndroidCll-AbstractHandler";
    protected final ClientTelemetry clientTelemetry;
    protected String filePath;
    protected FileStorage fileStorage;
    protected final ILogger logger;

    public abstract void add(String str, List<String> list) throws IOException, FileStorage.FileFullException;

    public abstract void close();

    public abstract void dispose(IStorage iStorage);

    public abstract List<IStorage> getFilesForDraining();

    public AbstractHandler(ILogger logger2, String filePath2, ClientTelemetry clientTelemetry2) {
        this.filePath = filePath2;
        this.logger = logger2;
        this.clientTelemetry = clientTelemetry2;
        setFileStorageUsed();
    }

    public boolean canAdd(Tuple serializedEvent) {
        if (((long) ((String) serializedEvent.a).length()) + totalStorageUsed.get() <= ((long) SettingsStore.getCllSettingsAsInt(SettingsStore.Settings.MAXFILESSPACE))) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public List<IStorage> getFilesByExtensionForDraining(String fileExtension) {
        List<IStorage> fullFiles = new ArrayList<>();
        for (File file : findExistingFiles(fileExtension)) {
            try {
                IStorage storage = new FileStorage(this.logger, file.getAbsolutePath(), this);
                fullFiles.add(storage);
                storage.close();
            } catch (Exception e) {
                this.logger.info("AndroidCll-AbstractHandler", "File " + file.getName() + " is in use still");
            }
        }
        return fullFiles;
    }

    /* access modifiers changed from: protected */
    public File[] findExistingFiles(final String fileExtension) {
        File[] files = new File(this.filePath).listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                if (name.toLowerCase().endsWith(fileExtension)) {
                    return true;
                }
                return false;
            }
        });
        if (files == null) {
            return new File[0];
        }
        return files;
    }

    private void setFileStorageUsed() {
        totalStorageUsed.set(0);
        for (File file : findExistingFiles(criticalEventFileExtension)) {
            totalStorageUsed.getAndAdd(file.length());
        }
        for (File file2 : findExistingFiles(normalEventFileExtension)) {
            totalStorageUsed.getAndAdd(file2.length());
        }
    }

    /* access modifiers changed from: protected */
    public boolean ensureCanAdd(Tuple<String, List<String>> tuple, EventEnums.Persistence persistence) {
        int maxAttempts = SettingsStore.getCllSettingsAsInt(SettingsStore.Settings.MAXCRITICALCANADDATTEMPTS);
        boolean considerCritical = persistence == EventEnums.Persistence.PersistenceCritical;
        int attempts = 0;
        boolean dropFileResult = true;
        boolean canAddResult = canAdd(tuple);
        while (!canAddResult && attempts < maxAttempts && dropFileResult) {
            this.logger.warn("AndroidCll-AbstractHandler", "Out of storage space. Attempting to drop one oldest file.");
            dropFileResult = dropOldestFile(considerCritical);
            canAddResult = canAdd(tuple);
            attempts++;
        }
        return canAddResult;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v4, resolved type: boolean} */
    /* JADX WARNING: type inference failed for: r7v0 */
    /* JADX WARNING: type inference failed for: r7v1, types: [int] */
    /* JADX WARNING: type inference failed for: r7v5 */
    /* JADX WARNING: type inference failed for: r7v6 */
    /* JADX WARNING: type inference failed for: r7v7 */
    /* access modifiers changed from: protected */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean dropOldestFile(boolean r13) {
        /*
            r12 = this;
            r9 = 1
            r7 = 0
            java.lang.String r8 = ".norm.cllevent"
            java.io.File[] r1 = r12.findExistingFiles(r8)
            int r8 = r1.length
            if (r8 > r9) goto L_0x0013
            if (r13 == 0) goto L_0x0013
            java.lang.String r8 = ".crit.cllevent"
            java.io.File[] r1 = r12.findExistingFiles(r8)
        L_0x0013:
            int r8 = r1.length
            if (r8 > r9) goto L_0x0020
            com.microsoft.cll.android.ILogger r8 = r12.logger
            java.lang.String r9 = "AndroidCll-AbstractHandler"
            java.lang.String r10 = "There are no files to delete"
            r8.info(r9, r10)
        L_0x001f:
            return r7
        L_0x0020:
            r8 = r1[r7]
            long r4 = r8.lastModified()
            r6 = r1[r7]
            int r8 = r1.length
        L_0x0029:
            if (r7 >= r8) goto L_0x003d
            r0 = r1[r7]
            long r10 = r0.lastModified()
            int r9 = (r10 > r4 ? 1 : (r10 == r4 ? 0 : -1))
            if (r9 >= 0) goto L_0x003a
            long r4 = r0.lastModified()
            r6 = r0
        L_0x003a:
            int r7 = r7 + 1
            goto L_0x0029
        L_0x003d:
            long r2 = r6.length()
            boolean r7 = r12.deleteFile(r6)
            if (r7 == 0) goto L_0x001f
            java.util.concurrent.atomic.AtomicLong r8 = totalStorageUsed
            long r10 = -r2
            r8.getAndAdd(r10)
            goto L_0x001f
        */
        throw new UnsupportedOperationException("Method not decompiled: com.microsoft.cll.android.AbstractHandler.dropOldestFile(boolean):boolean");
    }

    private boolean deleteFile(File file) {
        try {
            return file.delete();
        } catch (Exception e) {
            this.logger.info("AndroidCll-AbstractHandler", "Exception while deleting the file: " + e.toString());
            return false;
        }
    }
}
