package com.microsoft.bond;

import com.microsoft.bond.io.BondInputStream;
import java.io.IOException;

public class BondBlob {
    private final byte[] buffer;
    private final int length;
    private final int offset;

    public BondBlob() {
        this.buffer = null;
        this.length = 0;
        this.offset = 0;
    }

    public BondBlob(BondInputStream stream, int length2) throws IOException {
        this.buffer = new byte[length2];
        this.length = length2;
        this.offset = 0;
        stream.read(this.buffer, this.offset, this.length);
    }

    public BondBlob(byte[] buffer2, int offset2, int length2) {
        this.buffer = buffer2;
        this.offset = offset2;
        this.length = length2;
    }

    public BondBlob(BondBlob that) {
        this.buffer = that.buffer;
        this.offset = that.offset;
        this.length = that.length;
    }

    public byte[] getBuffer() {
        return this.buffer;
    }

    public int getOffset() {
        return this.offset;
    }

    public int size() {
        return this.length;
    }

    public boolean equals(BondBlob that) {
        if (this.length != that.length) {
            return false;
        }
        for (int i = 0; i < this.length; i++) {
            if (this.buffer[this.offset + i] != that.buffer[that.offset + i]) {
                return false;
            }
        }
        return true;
    }
}
