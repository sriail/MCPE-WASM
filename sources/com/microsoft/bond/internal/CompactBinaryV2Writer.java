package com.microsoft.bond.internal;

import com.microsoft.bond.BondDataType;
import com.microsoft.bond.BondSerializable;
import com.microsoft.bond.CompactBinaryWriter;
import com.microsoft.bond.ProtocolVersion;
import com.microsoft.bond.ProtocolWriter;
import com.microsoft.bond.io.BondOutputStream;
import java.io.IOException;

public class CompactBinaryV2Writer extends CompactBinaryWriter {
    private final CompactBinaryByteCounterWriter byteCounterWriter = new CompactBinaryByteCounterWriter();
    private int currentIndex = 0;

    public CompactBinaryV2Writer(BondOutputStream stream) {
        super(ProtocolVersion.TWO, stream);
    }

    public ProtocolWriter getFirstPassWriter() {
        if (this.currentIndex == 0) {
            return this.byteCounterWriter;
        }
        return null;
    }

    public void writeStructBegin(BondSerializable metadata, boolean isBase) throws IOException {
        if (!isBase) {
            writeUInt32(this.byteCounterWriter.getByteLength(this.currentIndex));
            this.currentIndex++;
        }
    }

    public void writeContainerBegin(int size, BondDataType elementType) throws IOException {
        if (size < 7) {
            writeUInt8((byte) (elementType.getValue() | ((size + 1) << 5)));
        } else {
            super.writeContainerBegin(size, elementType);
        }
    }

    public void writeEnd() {
        this.currentIndex = 0;
        this.byteCounterWriter.reset();
    }
}
