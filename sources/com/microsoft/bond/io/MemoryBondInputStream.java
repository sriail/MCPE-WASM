package com.microsoft.bond.io;

import com.microsoft.bond.BondBlob;
import com.microsoft.bond.BondException;
import java.io.EOFException;
import java.io.IOException;

public class MemoryBondInputStream extends BondInputStream {
    private final byte[] buffer;
    private final int bufferLength;
    private final int bufferOffset;
    private int readPosition;

    public MemoryBondInputStream(byte[] buffer2) {
        this(buffer2, 0, buffer2.length);
    }

    public MemoryBondInputStream(byte[] buffer2, int offset, int length) {
        this.buffer = buffer2;
        this.bufferOffset = offset;
        this.bufferLength = length;
        this.readPosition = 0;
    }

    public void close() throws IOException {
    }

    public boolean isSeekable() {
        return true;
    }

    public int getPosition() {
        return this.readPosition;
    }

    public int setPosition(int position) {
        validateNewPosition(position);
        this.readPosition = position;
        return this.readPosition;
    }

    private void validateNewPosition(int newPosition) {
        if (newPosition < 0) {
            throw new BondException(String.format("Invalid stream position [%s].", new Object[]{Integer.valueOf(newPosition)}));
        } else if (newPosition > this.bufferLength) {
            throw new BondException(String.format("Position [%s] is past the end of the buffer.", new Object[]{Integer.valueOf(newPosition)}));
        }
    }

    public int setPositionRelative(int offset) {
        int newPosition = this.readPosition + offset;
        validateNewPosition(newPosition);
        this.readPosition = newPosition;
        return this.readPosition;
    }

    public byte read() throws EOFException {
        validateRead(1);
        this.readPosition++;
        return this.buffer[(this.bufferOffset + this.readPosition) - 1];
    }

    private void validateRead(int bytesToBeRead) throws EOFException {
        if (this.readPosition + bytesToBeRead > this.bufferLength) {
            throw new EOFException(String.format("EOF reached. Trying to read [%d] bytes", new Object[]{Integer.valueOf(bytesToBeRead)}));
        }
    }

    public int read(byte[] buffer2, int offset, int length) throws EOFException {
        validateRead(length);
        System.arraycopy(this.buffer, this.bufferOffset + this.readPosition, buffer2, offset, length);
        this.readPosition += length;
        return length;
    }

    public BondBlob readBlob(int size) throws IOException {
        return new BondBlob(this, size);
    }

    public BondInputStream clone(boolean asReadonlyStream) {
        MemoryBondInputStream newStream = new MemoryBondInputStream(this.buffer, this.bufferOffset, this.bufferLength);
        newStream.readPosition = this.readPosition;
        return newStream;
    }

    public boolean isCloneable() {
        return true;
    }
}
