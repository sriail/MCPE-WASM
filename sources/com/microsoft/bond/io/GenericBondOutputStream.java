package com.microsoft.bond.io;

import java.io.IOException;
import java.io.OutputStream;

public class GenericBondOutputStream extends BondOutputStream {
    private final OutputStream stream;

    public GenericBondOutputStream(OutputStream stream2) {
        this.stream = stream2;
    }

    public void close() throws IOException {
        this.stream.close();
    }

    public void write(byte value) throws IOException {
        this.stream.write(value);
    }

    public void write(byte[] buffer, int offset, int length) throws IOException {
        this.stream.write(buffer, offset, length);
    }

    public void write(byte[] buffer) throws IOException {
        write(buffer, 0, buffer.length);
    }

    public boolean isSeekable() {
        return false;
    }

    public int getPosition() throws IOException {
        throw new UnsupportedOperationException();
    }

    public int setPosition(int position) throws IOException {
        throw new UnsupportedOperationException();
    }

    public int setPositionRelative(int offset) throws IOException {
        throw new UnsupportedOperationException();
    }
}
