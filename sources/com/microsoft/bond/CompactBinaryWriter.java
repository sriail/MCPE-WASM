package com.microsoft.bond;

import com.microsoft.bond.internal.CompactBinaryV2Writer;
import com.microsoft.bond.internal.DecimalHelper;
import com.microsoft.bond.internal.IntegerHelper;
import com.microsoft.bond.internal.StringHelper;
import com.microsoft.bond.io.BondOutputStream;
import java.io.IOException;

public class CompactBinaryWriter extends ProtocolWriter {
    public static final short MAGIC = ((short) ProtocolType.COMPACT_PROTOCOL.getValue());
    private final BondOutputStream stream;
    private final ProtocolVersion version;
    private final byte[] writeBuffer = new byte[10];

    public static CompactBinaryWriter createV1(BondOutputStream stream2) {
        return new CompactBinaryWriter(ProtocolVersion.ONE, stream2);
    }

    public static CompactBinaryWriter createV2(BondOutputStream stream2) {
        return new CompactBinaryV2Writer(stream2);
    }

    protected CompactBinaryWriter(ProtocolVersion version2, BondOutputStream stream2) {
        this.version = version2;
        this.stream = stream2;
    }

    public void writeVersion() throws IOException {
        writeUInt16(MAGIC);
        writeUInt16(this.version.getValue());
    }

    public void writeStructEnd(boolean isBase) throws IOException {
        writeUInt8((byte) (isBase ? BondDataType.BT_STOP_BASE.getValue() : BondDataType.BT_STOP.getValue()));
    }

    public void writeFieldBegin(BondDataType type, int id, BondSerializable metadata) throws IOException {
        byte fieldType = (byte) type.getValue();
        if (id <= 5) {
            this.stream.write((byte) ((id << 5) | fieldType));
        } else if (id <= 255) {
            this.stream.write((byte) (fieldType | 192));
            this.stream.write((byte) id);
        } else {
            this.stream.write((byte) (fieldType | 224));
            this.stream.write((byte) id);
            this.stream.write((byte) (id >>> 8));
        }
    }

    public void writeContainerBegin(int size, BondDataType elementType) throws IOException {
        writeUInt8((byte) elementType.getValue());
        writeUInt32(size);
    }

    public void writeContainerBegin(int size, BondDataType keyType, BondDataType valueType) throws IOException {
        writeUInt8((byte) keyType.getValue());
        writeUInt8((byte) valueType.getValue());
        writeUInt32(size);
    }

    public void writeContainerEnd() {
    }

    public void writeBool(boolean value) throws IOException {
        writeUInt8((byte) (value ? 1 : 0));
    }

    public void writeString(String value) throws IOException {
        if (value.isEmpty()) {
            writeUInt32(0);
            return;
        }
        byte[] buffer = StringHelper.encodeToUtf8(value);
        writeUInt32(buffer.length);
        this.stream.write(buffer);
    }

    public void writeWString(String value) throws IOException {
        if (value.isEmpty()) {
            writeUInt32(0);
            return;
        }
        writeUInt32(value.length());
        byte[] buffer = StringHelper.encodeToUtf16(value);
        this.stream.write(buffer, 0, buffer.length);
    }

    public void writeFloat(float value) throws IOException {
        DecimalHelper.encodeFloat(value, this.writeBuffer);
        this.stream.write(this.writeBuffer, 0, 4);
    }

    public void writeDouble(double value) throws IOException {
        DecimalHelper.encodeDouble(value, this.writeBuffer);
        this.stream.write(this.writeBuffer, 0, 8);
    }

    public void writeBlob(BondBlob value) throws IOException {
        this.stream.write(value.getBuffer(), value.getOffset(), value.size());
    }

    public void writeUInt8(byte value) throws IOException {
        this.stream.write(value);
    }

    public void writeUInt16(short value) throws IOException {
        this.stream.write(this.writeBuffer, 0, IntegerHelper.encodeVarUInt16(value, this.writeBuffer, 0));
    }

    public void writeUInt32(int value) throws IOException {
        this.stream.write(this.writeBuffer, 0, IntegerHelper.encodeVarUInt32(value, this.writeBuffer, 0));
    }

    public void writeUInt64(long value) throws IOException {
        this.stream.write(this.writeBuffer, 0, IntegerHelper.encodeVarUInt64(value, this.writeBuffer, 0));
    }

    public void writeInt8(byte value) throws IOException {
        this.stream.write(value);
    }

    public void writeInt16(short value) throws IOException {
        this.stream.write(this.writeBuffer, 0, IntegerHelper.encodeVarUInt16(IntegerHelper.encodeZigzag16(value), this.writeBuffer, 0));
    }

    public void writeInt32(int value) throws IOException {
        this.stream.write(this.writeBuffer, 0, IntegerHelper.encodeVarUInt32(IntegerHelper.encodeZigzag32(value), this.writeBuffer, 0));
    }

    public void writeInt64(long value) throws IOException {
        this.stream.write(this.writeBuffer, 0, IntegerHelper.encodeVarUInt64(IntegerHelper.encodeZigzag64(value), this.writeBuffer, 0));
    }

    public boolean hasCapability(ProtocolCapability capability) {
        switch (capability) {
            case CAN_OMIT_FIELDS:
            case TAGGED:
                return true;
            default:
                return super.hasCapability(capability);
        }
    }

    public String toString() {
        return String.format("[%s version=%d]", new Object[]{getClass().getName(), Short.valueOf(this.version.getValue())});
    }

    public ProtocolVersion getVersion() {
        return this.version;
    }
}
