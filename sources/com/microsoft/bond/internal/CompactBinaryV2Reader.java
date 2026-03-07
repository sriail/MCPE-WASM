package com.microsoft.bond.internal;

import com.microsoft.bond.BondDataType;
import com.microsoft.bond.CompactBinaryReader;
import com.microsoft.bond.ProtocolReader;
import com.microsoft.bond.ProtocolVersion;
import com.microsoft.bond.io.BondInputStream;
import java.io.IOException;

public class CompactBinaryV2Reader extends CompactBinaryReader {
    public CompactBinaryV2Reader(BondInputStream stream) {
        super(ProtocolVersion.TWO, stream);
    }

    public void readStructBegin(boolean isBase) throws IOException {
        if (!isBase) {
            readUInt32();
        }
    }

    public ProtocolReader.ListTag readContainerBegin() throws IOException {
        byte rawValue = readUInt8();
        BondDataType elementType = BondDataType.fromValue(rawValue & 31);
        if ((rawValue & 224) != 0) {
            return new ProtocolReader.ListTag((((byte) (rawValue >> 5)) & 7) - 1, elementType);
        }
        return new ProtocolReader.ListTag(readUInt32(), elementType);
    }

    public void skip(BondDataType type) throws IOException {
        switch (type) {
            case BT_STRUCT:
                this.stream.setPositionRelative(readUInt32());
                return;
            default:
                super.skip(type);
                return;
        }
    }
}
