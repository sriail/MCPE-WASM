package com.microsoft.xbox.toolkit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class XLEFileCache {
    private static final String TAG = XLEFileCache.class.getSimpleName();
    private boolean enabled;
    private final long expiredTimer;
    final int maxFileNumber;
    private int readAccessCnt;
    private int readSuccessfulCnt;
    int size;
    private int writeAccessCnt;

    XLEFileCache() {
        this.size = 0;
        this.enabled = true;
        this.readAccessCnt = 0;
        this.writeAccessCnt = 0;
        this.readSuccessfulCnt = 0;
        this.expiredTimer = Long.MAX_VALUE;
        this.maxFileNumber = 0;
        this.enabled = false;
    }

    XLEFileCache(String dir, int maxFileNumber2) {
        this(dir, maxFileNumber2, Long.MAX_VALUE);
    }

    XLEFileCache(String dir, int maxFileNumber2, long expiredDurationInSeconds) {
        this.size = 0;
        this.enabled = true;
        this.readAccessCnt = 0;
        this.writeAccessCnt = 0;
        this.readSuccessfulCnt = 0;
        this.maxFileNumber = maxFileNumber2;
        this.expiredTimer = expiredDurationInSeconds;
    }

    public int getItemsInCache() {
        return this.size;
    }

    public synchronized boolean contains(XLEFileCacheItemKey cachedItem) {
        boolean exists;
        if (!this.enabled) {
            exists = false;
        } else {
            exists = new File(XLEFileCacheManager.getCacheRootDir(this), getCachedItemFileName(cachedItem)).exists();
        }
        return exists;
    }

    public synchronized OutputStream getOuputStreamForSave(XLEFileCacheItemKey cachedItem) throws IOException {
        OutputStream cachedFileOutputStreamItem;
        if (!this.enabled) {
            cachedFileOutputStreamItem = new OutputStream() {
                public void write(int oneByte) throws IOException {
                }
            };
        } else {
            XLEAssert.assertTrue(Thread.currentThread() != ThreadManager.UIThread);
            this.writeAccessCnt++;
            checkAndEnsureCapacity();
            File outputFile = new File(XLEFileCacheManager.getCacheRootDir(this), getCachedItemFileName(cachedItem));
            if (outputFile.exists()) {
                outputFile.delete();
                this.size--;
            }
            if (outputFile.createNewFile()) {
                this.size++;
            }
            cachedFileOutputStreamItem = new CachedFileOutputStreamItem(cachedItem, outputFile);
        }
        return cachedFileOutputStreamItem;
    }

    /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void save(com.microsoft.xbox.toolkit.XLEFileCacheItemKey r3, java.io.InputStream r4) {
        /*
            r2 = this;
            monitor-enter(r2)
            java.io.OutputStream r0 = r2.getOuputStreamForSave(r3)     // Catch:{ IOException -> 0x0010, all -> 0x000d }
            com.microsoft.xbox.toolkit.StreamUtil.CopyStream(r0, r4)     // Catch:{ IOException -> 0x0010, all -> 0x000d }
            r0.close()     // Catch:{ IOException -> 0x0010, all -> 0x000d }
        L_0x000b:
            monitor-exit(r2)
            return
        L_0x000d:
            r1 = move-exception
            monitor-exit(r2)
            throw r1
        L_0x0010:
            r1 = move-exception
            goto L_0x000b
        */
        throw new UnsupportedOperationException("Method not decompiled: com.microsoft.xbox.toolkit.XLEFileCache.save(com.microsoft.xbox.toolkit.XLEFileCacheItemKey, java.io.InputStream):void");
    }

    public synchronized InputStream getInputStreamForRead(XLEFileCacheItemKey cachedItem) {
        InputStream inputStream;
        if (!this.enabled) {
            inputStream = null;
        } else {
            XLEAssert.assertTrue(Thread.currentThread() != ThreadManager.UIThread);
            this.readAccessCnt++;
            File cacheFile = new File(XLEFileCacheManager.getCacheRootDir(this), getCachedItemFileName(cachedItem));
            if (cacheFile.exists()) {
                if (cacheFile.lastModified() < System.currentTimeMillis() - this.expiredTimer) {
                    cacheFile.delete();
                    this.size--;
                    inputStream = null;
                } else {
                    try {
                        inputStream = new CachedFileInputStreamItem(cachedItem, cacheFile).getContentInputStream();
                        this.readSuccessfulCnt++;
                    } catch (IOException e) {
                    }
                }
            }
            inputStream = null;
        }
        return inputStream;
    }

    public String toString() {
        return "Size=" + this.size + "\tRootDir=" + XLEFileCacheManager.getCacheRootDir(this) + "\tMaxFileNumber=" + this.maxFileNumber + "\tExpiredTimerInSeconds=" + this.expiredTimer + "\tWriteAccessCnt=" + this.writeAccessCnt + "\tReadAccessCnt=" + this.readAccessCnt + "\tReadSuccessfulCnt=" + this.readSuccessfulCnt;
    }

    private void checkAndEnsureCapacity() {
        if (this.size >= this.maxFileNumber && this.enabled) {
            File[] files = XLEFileCacheManager.getCacheRootDir(this).listFiles();
            files[new Random().nextInt(files.length)].delete();
            this.size = files.length - 1;
        }
    }

    private String getCachedItemFileName(XLEFileCacheItemKey fileItem) {
        return String.valueOf(fileItem.getKeyString().hashCode());
    }

    private class CachedFileInputStreamItem {
        private byte[] computedMd5;
        private InputStream contentInputStream;
        private MessageDigest mDigest = null;
        private byte[] savedMd5;

        public CachedFileInputStreamItem(XLEFileCacheItemKey key, File file) throws IOException {
            FileInputStream wrappedFileInputStream = new FileInputStream(file);
            try {
                this.mDigest = MessageDigest.getInstance("MD5");
                this.savedMd5 = new byte[this.mDigest.getDigestLength()];
                if (wrappedFileInputStream.read(this.savedMd5) != this.mDigest.getDigestLength()) {
                    wrappedFileInputStream.close();
                    throw new IOException("Ddigest lengh check failed!");
                }
                int keyLength = XLEFileCache.readInt(wrappedFileInputStream);
                byte[] cacheItemKey = new byte[keyLength];
                if (keyLength != wrappedFileInputStream.read(cacheItemKey) || !key.getKeyString().equals(new String(cacheItemKey))) {
                    file.delete();
                    throw new IOException("File key check failed because keyLength != readKeyLength or !key.getKeyString().equals(new String(urlOrSomething))");
                }
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                StreamUtil.CopyStream(baos, wrappedFileInputStream);
                wrappedFileInputStream.close();
                byte[] content = baos.toByteArray();
                this.mDigest.update(content);
                this.computedMd5 = this.mDigest.digest();
                if (isMd5Error()) {
                    file.delete();
                    throw new IOException(wrappedFileInputStream.getFD() + "the saved md5 is not equal computed md5." + "ComputedMd5:" + this.computedMd5 + "     SavedMd5:" + this.savedMd5);
                } else {
                    this.contentInputStream = new ByteArrayInputStream(content);
                }
            } catch (NoSuchAlgorithmException e) {
                wrappedFileInputStream.close();
                throw new IOException("File digest failed! " + e.getMessage());
            } catch (OutOfMemoryError e2) {
                wrappedFileInputStream.close();
                throw new IOException("File digest failed! Out of memory: " + e2.getMessage());
            }
        }

        public InputStream getContentInputStream() {
            return this.contentInputStream;
        }

        private boolean isMd5Error() {
            for (int i = 0; i < this.mDigest.getDigestLength(); i++) {
                if (this.savedMd5[i] != this.computedMd5[i]) {
                    return true;
                }
            }
            return false;
        }
    }

    private class CachedFileOutputStreamItem extends FileOutputStream {
        private File destFile;
        private MessageDigest mDigest = null;
        private boolean startDigest = false;
        private boolean writeMd5Finished = false;

        public CachedFileOutputStreamItem(XLEFileCacheItemKey key, File file) throws IOException {
            super(file);
            this.destFile = file;
            try {
                this.mDigest = MessageDigest.getInstance("MD5");
                write(new byte[this.mDigest.getDigestLength()]);
                byte[] urlOrSomething = key.getKeyString().getBytes();
                writeInt(urlOrSomething.length);
                write(urlOrSomething);
                this.startDigest = true;
            } catch (NoSuchAlgorithmException e) {
                throw new IOException("File digest failed!" + e.getMessage());
            }
        }

        public void close() throws IOException {
            super.close();
            if (!this.writeMd5Finished) {
                this.writeMd5Finished = true;
                RandomAccessFile raf = new RandomAccessFile(this.destFile, "rw");
                byte[] md5Hash = this.mDigest.digest();
                raf.seek(0);
                raf.write(md5Hash);
                raf.close();
            }
        }

        public void write(byte[] buffer, int offset, int byteCount) throws IOException {
            super.write(buffer, offset, byteCount);
            if (this.startDigest) {
                this.mDigest.update(buffer, offset, byteCount);
            }
        }

        private final void writeInt(int v) throws IOException {
            write((v >>> 24) & 255);
            write((v >>> 16) & 255);
            write((v >>> 8) & 255);
            write((v >>> 0) & 255);
        }
    }

    /* access modifiers changed from: private */
    public static int readInt(InputStream is) throws IOException {
        int ch1 = is.read();
        int ch2 = is.read();
        int ch3 = is.read();
        int ch4 = is.read();
        if ((ch1 | ch2 | ch3 | ch4) >= 0) {
            return (ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0);
        }
        throw new EOFException();
    }
}
