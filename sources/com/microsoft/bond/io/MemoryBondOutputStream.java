package com.microsoft.bond.io;

import com.microsoft.bond.BondBlob;
import java.io.IOException;

public class MemoryBondOutputStream extends BondOutputStream {
    private static final int DEFAULT_CAPACITY_BYTES = 1024;
    private byte[] buffer;
    private int position;

    public MemoryBondOutputStream() {
        this(1024);
    }

    public MemoryBondOutputStream(int capacityBytes) {
        this.buffer = new byte[capacityBytes];
        this.position = 0;
    }

    public void close() throws IOException {
        this.buffer = null;
        this.position = -1;
    }

    public void write(byte value) {
        ensureBufferSizeForExtraBytes(1);
        this.buffer[this.position] = value;
        this.position++;
    }

    public void write(byte[] buffer2, int offset, int length) {
        ensureBufferSizeForExtraBytes(length);
        System.arraycopy(buffer2, offset, this.buffer, this.position, length);
        this.position += length;
    }

    private void ensureBufferSizeForExtraBytes(int extraBytes) {
        if (this.buffer.length < this.position + extraBytes) {
            int newSize = this.buffer.length + (this.buffer.length >> 1);
            if (newSize < this.position + extraBytes) {
                newSize = this.position + extraBytes;
            }
            byte[] newBuffer = new byte[newSize];
            System.arraycopy(this.buffer, 0, newBuffer, 0, this.position);
            this.buffer = newBuffer;
        }
    }

    public void write(byte[] buffer2) {
        write(buffer2, 0, buffer2.length);
    }

    public byte[] toByteArray() {
        byte[] bufferToReturn = new byte[this.position];
        System.arraycopy(this.buffer, 0, bufferToReturn, 0, bufferToReturn.length);
        return bufferToReturn;
    }

    public BondBlob toBondBlod() {
        return new BondBlob(this.buffer, 0, this.position);
    }

    public boolean isSeekable() {
        return true;
    }

    public int getPosition() throws IOException {
        return this.position;
    }

    public int setPosition(int position2) throws IOException {
        if (position2 < 0 || position2 >= this.buffer.length) {
            throw new IllegalArgumentException(String.format("Cannot jump to position [%d]. Valid positions are from [%d] to [%d] inclusive.", new Object[]{Integer.valueOf(position2), 0, Integer.valueOf(this.buffer.length - 1)}));
        }
        this.position = position2;
        return this.position;
    }

    public int setPositionRelative(int offset) throws IOException {
        return setPosition(this.position + offset);
    }
}
