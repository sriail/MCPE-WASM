package com.microsoft.bond;

import java.io.Closeable;
import java.io.IOException;

public abstract class ProtocolReader implements Closeable {
    public abstract int getPosition() throws IOException;

    public abstract boolean isProtocolSame(ProtocolWriter protocolWriter);

    public abstract BondBlob readBlob(int i) throws IOException;

    public abstract boolean readBool() throws IOException;

    public abstract ListTag readContainerBegin() throws IOException;

    public abstract void readContainerEnd() throws IOException;

    public abstract double readDouble() throws IOException;

    public abstract float readFloat() throws IOException;

    public abstract short readInt16() throws IOException;

    public abstract int readInt32() throws IOException;

    public abstract long readInt64() throws IOException;

    public abstract byte readInt8() throws IOException;

    public abstract MapTag readMapContainerBegin() throws IOException;

    public abstract String readString() throws IOException;

    public abstract short readUInt16() throws IOException;

    public abstract int readUInt32() throws IOException;

    public abstract long readUInt64() throws IOException;

    public abstract byte readUInt8() throws IOException;

    public abstract String readWString() throws IOException;

    public abstract void setPosition(int i) throws IOException;

    public abstract void skip(BondDataType bondDataType) throws IOException;

    public void close() throws IOException {
    }

    public void readBegin() {
    }

    public void readEnd() {
    }

    public void readStructBegin(boolean isBase) throws IOException {
    }

    public void readStructEnd() throws IOException {
    }

    public FieldTag readFieldBegin() throws IOException {
        return new FieldTag(BondDataType.BT_UNAVAILABLE, 32767);
    }

    public void readFieldEnd() throws IOException {
    }

    public boolean readFieldOmitted() throws IOException {
        return false;
    }

    public boolean hasCapability(ProtocolCapability capability) {
        return false;
    }

    public ProtocolReader cloneReader() throws IOException {
        return null;
    }

    public static class FieldTag {
        public final int id;
        public final BondDataType type;

        FieldTag(BondDataType type2, int id2) {
            this.type = type2;
            this.id = id2;
        }
    }

    public static class ListTag {
        public final int size;
        public final BondDataType type;

        public ListTag(int size2, BondDataType type2) {
            this.size = size2;
            this.type = type2;
        }
    }

    public static class MapTag {
        public final BondDataType keyType;
        public final int size;
        public final BondDataType valueType;

        public MapTag(int size2, BondDataType keyType2, BondDataType valueType2) {
            this.size = size2;
            this.keyType = keyType2;
            this.valueType = valueType2;
        }
    }
}
