package com.microsoft.bond.internal;

import com.microsoft.bond.BondBlob;
import com.microsoft.bond.BondDataType;
import com.microsoft.bond.BondSerializable;
import com.microsoft.bond.ProtocolCapability;
import com.microsoft.bond.ProtocolWriter;
import java.io.IOException;

public class CompactBinaryByteCounterWriter extends ProtocolWriter {
    private IntArrayStack byteLengths = new IntArrayStack(32);
    private IntArrayStack byteLengthsIndexes = new IntArrayStack(8);
    private int positionBytes;

    public int getByteLength(int index) {
        return this.byteLengths.get(index);
    }

    public void reset() {
        this.positionBytes = 0;
        this.byteLengths.clear();
        this.byteLengthsIndexes.clear();
    }

    public boolean hasCapability(ProtocolCapability capability) {
        switch (capability) {
            case TAGGED:
            case PASS_THROUGH:
            case CAN_OMIT_FIELDS:
                return true;
            default:
                return super.hasCapability(capability);
        }
    }

    public void writeVersion() throws IOException {
        this.positionBytes += 4;
    }

    public void writeStructBegin(BondSerializable metadata, boolean isBase) throws IOException {
        if (!isBase) {
            this.byteLengthsIndexes.push(this.byteLengths.getSize());
            this.byteLengths.push(this.positionBytes);
        }
    }

    public void writeStructEnd(boolean isBase) throws IOException {
        this.positionBytes++;
        if (!isBase) {
            int lengthIndex = this.byteLengthsIndexes.pop();
            int byteSize = this.positionBytes - this.byteLengths.get(lengthIndex);
            this.byteLengths.set(lengthIndex, byteSize);
            this.positionBytes += IntegerHelper.getVarUInt32Size(byteSize);
        }
    }

    public void writeFieldBegin(BondDataType type, int id, BondSerializable metadata) throws IOException {
        if (id <= 5) {
            this.positionBytes++;
        } else if (id <= 255) {
            this.positionBytes += 2;
        } else {
            this.positionBytes += 3;
        }
    }

    public void writeFieldEnd() throws IOException {
    }

    public void writeFieldOmitted(BondDataType type, int id, BondSerializable metadata) throws IOException {
    }

    public void writeContainerBegin(int size, BondDataType elementType) throws IOException {
        this.positionBytes = (size < 7 ? 0 : IntegerHelper.getVarUInt32Size(size)) + 1 + this.positionBytes;
    }

    public void writeContainerBegin(int size, BondDataType keyType, BondDataType valueType) throws IOException {
        this.positionBytes += IntegerHelper.getVarUInt32Size(size) + 2;
    }

    public void writeContainerEnd() throws IOException {
    }

    public void writeBool(boolean value) throws IOException {
        this.positionBytes++;
    }

    public void writeString(String value) throws IOException {
        if (value == null || value.isEmpty()) {
            this.positionBytes++;
            return;
        }
        int encodedStringByteSize = StringHelper.encodeToUtf8(value).length;
        this.positionBytes += IntegerHelper.getVarUInt32Size(encodedStringByteSize) + encodedStringByteSize;
    }

    public void writeWString(String value) throws IOException {
        if (value.isEmpty()) {
            this.positionBytes++;
            return;
        }
        this.positionBytes += IntegerHelper.getVarUInt32Size(value.length()) + StringHelper.encodeToUtf16(value).length;
    }

    public void writeFloat(float value) throws IOException {
        this.positionBytes += 4;
    }

    public void writeDouble(double value) throws IOException {
        this.positionBytes += 8;
    }

    public void writeBlob(BondBlob value) throws IOException {
        this.positionBytes += value.size();
    }

    public void writeUInt8(byte value) throws IOException {
        this.positionBytes++;
    }

    public void writeUInt16(short value) throws IOException {
        this.positionBytes += IntegerHelper.getVarUInt16Size(value);
    }

    public void writeUInt32(int value) throws IOException {
        this.positionBytes += IntegerHelper.getVarUInt32Size(value);
    }

    public void writeUInt64(long value) throws IOException {
        this.positionBytes += IntegerHelper.getVarUInt64Size(value);
    }

    public void writeInt8(byte value) throws IOException {
        this.positionBytes++;
    }

    public void writeInt16(short value) throws IOException {
        writeUInt16(IntegerHelper.encodeZigzag16(value));
    }

    public void writeInt32(int value) throws IOException {
        writeUInt32(IntegerHelper.encodeZigzag32(value));
    }

    public void writeInt64(long value) throws IOException {
        writeUInt64(IntegerHelper.encodeZigzag64(value));
    }
}
