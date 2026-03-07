package com.microsoft.bond.io;

import com.microsoft.bond.BondBlob;
import com.microsoft.bond.BondException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FileBondInputStream extends BondInputStream {
    private final File file;
    private int fileLength;
    private int position;
    private FileInputStream stream;

    public FileBondInputStream(File file2) throws FileNotFoundException {
        this.file = file2;
        resetStream();
    }

    public void close() throws IOException {
        this.stream.close();
    }

    public boolean isSeekable() {
        return true;
    }

    public int getPosition() {
        return this.position;
    }

    public int setPosition(int newPosition) throws IOException {
        int skipBytes;
        if (newPosition < 0 || newPosition > this.fileLength) {
            throw new BondException("Invalid position: " + newPosition);
        }
        if (newPosition >= this.position) {
            skipBytes = newPosition - this.position;
        } else {
            skipBytes = newPosition;
            resetStream();
        }
        this.position = newPosition;
        this.stream.skip((long) skipBytes);
        return this.position;
    }

    private void resetStream() throws FileNotFoundException {
        this.fileLength = (int) this.file.length();
        this.position = 0;
        this.stream = new FileInputStream(this.file);
    }

    public int setPositionRelative(int offset) throws IOException {
        setPosition(this.position + offset);
        return this.position;
    }

    public byte read() throws IOException {
        this.position++;
        return (byte) this.stream.read();
    }

    public int read(byte[] buffer, int offset, int length) throws IOException {
        int readBytes = 0;
        while (readBytes < length) {
            try {
                readBytes += this.stream.read(buffer, offset + readBytes, length - readBytes);
            } catch (Throwable th) {
                this.position += readBytes;
                throw th;
            }
        }
        this.position += readBytes;
        return readBytes;
    }

    public BondBlob readBlob(int size) throws IOException {
        return new BondBlob(this, size);
    }

    public BondInputStream clone(boolean asReadonlyStream) throws IOException {
        FileBondInputStream newStream = new FileBondInputStream(this.file);
        newStream.setPosition(this.position);
        return newStream;
    }

    public boolean isCloneable() {
        return true;
    }
}
