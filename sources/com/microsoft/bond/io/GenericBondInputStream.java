package com.microsoft.bond.io;

import com.microsoft.bond.BondBlob;
import com.microsoft.bond.BondException;
import java.io.IOException;
import java.io.InputStream;

public class GenericBondInputStream extends BondInputStream {
    private final InputStream stream;

    public GenericBondInputStream(InputStream stream2) {
        this.stream = stream2;
    }

    public void close() throws IOException {
        this.stream.close();
    }

    public boolean isSeekable() {
        return false;
    }

    public int getPosition() {
        throw new UnsupportedOperationException();
    }

    public int setPosition(int position) {
        throw new UnsupportedOperationException();
    }

    public int setPositionRelative(int offset) {
        throw new UnsupportedOperationException();
    }

    public byte read() {
        try {
            return (byte) this.stream.read();
        } catch (IOException e) {
            throw new BondException(e);
        }
    }

    public int read(byte[] buffer, int offset, int length) {
        try {
            return this.stream.read(buffer, offset, length);
        } catch (IOException e) {
            throw new BondException(e);
        }
    }

    public BondBlob readBlob(int size) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public BondInputStream clone(boolean asReadonlyStream) {
        throw new UnsupportedOperationException();
    }

    public boolean isCloneable() {
        return false;
    }
}
