package com.microsoft.cll.android;

import com.microsoft.cll.android.SettingsStore;
import com.microsoft.telemetry.IJsonSerializable;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import org.apache.james.mime4j.util.CharsetUtil;

public class FileStorage implements IStorage {
    protected static final SynchronizedArrayList<String> fileLockList = new SynchronizedArrayList<>();
    private final String TAG;
    private int eventsWritten;
    private String filePathAndName;
    private long fileSize;
    private FileReader inputFile;
    private boolean isOpen;
    private boolean isWritable;
    private final ILogger logger;
    private FileWriter outputFile;
    private AbstractHandler parent;
    private BufferedReader reader;
    private final EventSerializer serializer;

    public FileStorage(String fileExtension, ILogger logger2, String filePath, AbstractHandler parent2) {
        this.TAG = "AndroidCll-FileStorage";
        this.eventsWritten = 0;
        this.fileSize = 0;
        this.filePathAndName = filePath + File.separator + UUID.randomUUID() + fileExtension;
        this.logger = logger2;
        this.serializer = new EventSerializer(logger2);
        this.parent = parent2;
        int tries = 1;
        while (!openFile()) {
            this.filePathAndName = filePath + "/" + UUID.randomUUID() + fileExtension;
            tries++;
            if (tries >= 5) {
                logger2.error("AndroidCll-FileStorage", "Could not create a file");
                return;
            }
        }
    }

    public FileStorage(ILogger logger2, String filePathAndName2, AbstractHandler parent2) throws Exception {
        this.TAG = "AndroidCll-FileStorage";
        this.logger = logger2;
        this.serializer = new EventSerializer(logger2);
        this.filePathAndName = filePathAndName2;
        this.parent = parent2;
        if (fileLockList.contains(filePathAndName2)) {
            throw new Exception("Could not get lock for file");
        }
    }

    public void add(IJsonSerializable event) throws FileFullException, IOException {
        add((Tuple<String, List<String>>) new Tuple(this.serializer.serialize(event), null));
    }

    public void add(Tuple<String, List<String>> serializedEvent) throws FileFullException, IOException {
        if (!this.isOpen || !this.isWritable) {
            this.logger.warn("AndroidCll-FileStorage", "This file is not open or not writable");
        } else if (!canAdd(serializedEvent)) {
            throw new FileFullException("The file is already full!");
        } else {
            if (serializedEvent.b != null) {
                for (String id : (List) serializedEvent.b) {
                    this.outputFile.write("x:" + id + CharsetUtil.CRLF);
                }
            }
            this.outputFile.write((String) serializedEvent.a);
            this.eventsWritten++;
            this.fileSize += (long) ((String) serializedEvent.a).length();
        }
    }

    public boolean canAdd(IJsonSerializable event) {
        return canAdd((Tuple<String, List<String>>) new Tuple(this.serializer.serialize(event), null));
    }

    public boolean canAdd(Tuple<String, List<String>> event) {
        if (!this.isOpen || !this.isWritable) {
            this.logger.warn("AndroidCll-FileStorage", "This file is not open or not writable");
            return false;
        }
        return this.eventsWritten < SettingsStore.getCllSettingsAsInt(SettingsStore.Settings.MAXEVENTSPERPOST) && ((long) ((String) event.a).length()) + this.fileSize < ((long) SettingsStore.getCllSettingsAsInt(SettingsStore.Settings.MAXEVENTSIZEINBYTES));
    }

    /* JADX WARNING: Code restructure failed: missing block: B:4:0x000d, code lost:
        if (openFile() == false) goto L_0x000f;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.util.List<com.microsoft.cll.android.Tuple<java.lang.String, java.util.List<java.lang.String>>> drain() {
        /*
            r8 = this;
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            boolean r4 = r8.isOpen
            if (r4 != 0) goto L_0x001b
            boolean r4 = r8.openFile()     // Catch:{ Exception -> 0x0010 }
            if (r4 != 0) goto L_0x001b
        L_0x000f:
            return r0
        L_0x0010:
            r1 = move-exception
            com.microsoft.cll.android.ILogger r4 = r8.logger
            java.lang.String r5 = "AndroidCll-FileStorage"
            java.lang.String r6 = "Error opening file"
            r4.error(r5, r6)
            goto L_0x000f
        L_0x001b:
            java.io.BufferedReader r4 = r8.reader     // Catch:{ Exception -> 0x005d }
            java.lang.String r3 = r4.readLine()     // Catch:{ Exception -> 0x005d }
            java.util.ArrayList r2 = new java.util.ArrayList     // Catch:{ Exception -> 0x005d }
            r2.<init>()     // Catch:{ Exception -> 0x005d }
        L_0x0026:
            if (r3 == 0) goto L_0x0067
            java.lang.String r4 = "x:"
            boolean r4 = r3.startsWith(r4)     // Catch:{ Exception -> 0x005d }
            if (r4 == 0) goto L_0x003f
            r4 = 2
            java.lang.String r4 = r3.substring(r4)     // Catch:{ Exception -> 0x005d }
            r2.add(r4)     // Catch:{ Exception -> 0x005d }
        L_0x0038:
            java.io.BufferedReader r4 = r8.reader     // Catch:{ Exception -> 0x005d }
            java.lang.String r3 = r4.readLine()     // Catch:{ Exception -> 0x005d }
            goto L_0x0026
        L_0x003f:
            int r4 = r2.size()     // Catch:{ Exception -> 0x005d }
            if (r4 <= 0) goto L_0x0053
            com.microsoft.cll.android.Tuple r4 = new com.microsoft.cll.android.Tuple     // Catch:{ Exception -> 0x005d }
            r4.<init>(r3, r2)     // Catch:{ Exception -> 0x005d }
            r0.add(r4)     // Catch:{ Exception -> 0x005d }
            java.util.ArrayList r2 = new java.util.ArrayList     // Catch:{ Exception -> 0x005d }
            r2.<init>()     // Catch:{ Exception -> 0x005d }
            goto L_0x0038
        L_0x0053:
            com.microsoft.cll.android.Tuple r4 = new com.microsoft.cll.android.Tuple     // Catch:{ Exception -> 0x005d }
            r5 = 0
            r4.<init>(r3, r5)     // Catch:{ Exception -> 0x005d }
            r0.add(r4)     // Catch:{ Exception -> 0x005d }
            goto L_0x0038
        L_0x005d:
            r1 = move-exception
            com.microsoft.cll.android.ILogger r4 = r8.logger
            java.lang.String r5 = "AndroidCll-FileStorage"
            java.lang.String r6 = "Error reading from input file"
            r4.error(r5, r6)
        L_0x0067:
            com.microsoft.cll.android.ILogger r4 = r8.logger
            java.lang.String r5 = "AndroidCll-FileStorage"
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r7 = "Read "
            java.lang.StringBuilder r6 = r6.append(r7)
            int r7 = r0.size()
            java.lang.StringBuilder r6 = r6.append(r7)
            java.lang.String r7 = " events from file"
            java.lang.StringBuilder r6 = r6.append(r7)
            java.lang.String r6 = r6.toString()
            r4.info(r5, r6)
            goto L_0x000f
        */
        throw new UnsupportedOperationException("Method not decompiled: com.microsoft.cll.android.FileStorage.drain():java.util.List");
    }

    public long size() {
        if (!this.isOpen) {
            return new File(this.filePathAndName).length();
        }
        return this.fileSize;
    }

    public void discard() {
        this.logger.info("AndroidCll-FileStorage", "Discarding file");
        close();
        this.parent.dispose(this);
        new File(this.filePathAndName).delete();
    }

    public void flush() {
        if (this.isOpen && this.isWritable) {
            try {
                this.outputFile.flush();
            } catch (Exception e) {
                this.logger.error("AndroidCll-FileStorage", "Could not flush file");
            }
        }
    }

    public void close() {
        if (this.isOpen) {
            flush();
            fileLockList.remove(this.filePathAndName);
            try {
                if (this.isWritable) {
                    this.outputFile.close();
                } else {
                    this.inputFile.close();
                    this.reader.close();
                }
                this.isOpen = false;
            } catch (Exception e) {
                this.logger.error("AndroidCll-FileStorage", "Error when closing file");
            }
        }
    }

    private boolean openFile() {
        if (!getLock()) {
            this.logger.info("AndroidCll-FileStorage", "Could not get lock for file");
            return false;
        }
        File f = new File(this.filePathAndName);
        if (f.exists()) {
            this.isWritable = false;
            try {
                this.inputFile = new FileReader(this.filePathAndName);
                this.reader = new BufferedReader(this.inputFile);
                this.fileSize = f.length();
            } catch (IOException e) {
                this.logger.error("AndroidCll-FileStorage", "Event file was not found");
                return false;
            }
        } else {
            this.isWritable = true;
            this.logger.info("AndroidCll-FileStorage", "Creating new file");
            try {
                this.outputFile = new FileWriter(this.filePathAndName);
            } catch (IOException e2) {
                this.logger.error("AndroidCll-FileStorage", "Error opening file");
                return false;
            }
        }
        this.isOpen = true;
        return true;
    }

    private boolean getLock() {
        return fileLockList.add(this.filePathAndName);
    }

    class FileFullException extends Exception {
        public FileFullException(String message) {
            super(message);
        }
    }
}
