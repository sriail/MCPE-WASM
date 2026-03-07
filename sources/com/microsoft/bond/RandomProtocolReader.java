package com.microsoft.bond;

import android.support.v4.internal.view.SupportMenu;
import com.microsoft.bond.ProtocolReader;
import java.io.IOException;
import java.util.Random;

public class RandomProtocolReader extends ProtocolReader {
    private static final int DEFAULT_MAX_CONTAINER_SIZE = 10;
    private static final int DEFAULT_MAX_STRING_LENGTH = 20;
    private final int maxContainerSize;
    private final int maxStringLength;
    private final Random random;

    public RandomProtocolReader() {
        this.maxStringLength = 20;
        this.maxContainerSize = 10;
        this.random = new Random();
    }

    public RandomProtocolReader(long seed) {
        this(seed, 20, 10);
    }

    public RandomProtocolReader(long seed, int maxStringLength2, int maxContainerSize2) {
        this.maxStringLength = maxStringLength2;
        this.maxContainerSize = maxContainerSize2;
        this.random = new Random(seed);
    }

    public boolean hasCapability(ProtocolCapability capability) {
        return false;
    }

    public boolean isProtocolSame(ProtocolWriter writer) {
        return false;
    }

    public ProtocolReader.ListTag readContainerBegin() {
        return new ProtocolReader.ListTag(this.random.nextInt(this.maxContainerSize) + 1, BondDataType.BT_UNAVAILABLE);
    }

    public ProtocolReader.MapTag readMapContainerBegin() {
        return new ProtocolReader.MapTag(this.random.nextInt(this.maxContainerSize) + 1, BondDataType.BT_UNAVAILABLE, BondDataType.BT_UNAVAILABLE);
    }

    public void readContainerEnd() {
    }

    public BondBlob readBlob(int size) {
        return null;
    }

    public boolean readBool() {
        return this.random.nextBoolean();
    }

    public float readFloat() {
        return ((float) this.random.nextLong()) * this.random.nextFloat();
    }

    public double readDouble() {
        return ((double) this.random.nextLong()) * this.random.nextDouble();
    }

    public byte readUInt8() {
        return (byte) this.random.nextInt(255);
    }

    public short readUInt16() {
        return (short) (65535 & this.random.nextInt());
    }

    public int readUInt32() {
        return this.random.nextInt();
    }

    public long readUInt64() {
        return this.random.nextLong();
    }

    public byte readInt8() {
        return (byte) (this.random.nextInt(255) - 127);
    }

    public short readInt16() {
        return (short) (this.random.nextInt(SupportMenu.USER_MASK) - 32767);
    }

    public int readInt32() {
        return this.random.nextInt();
    }

    public long readInt64() {
        return this.random.nextLong();
    }

    public String readString() {
        int length = this.random.nextInt(this.maxStringLength) + 1;
        StringBuilder builder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            builder.append((char) (this.random.nextInt(94) + 32));
        }
        return builder.toString();
    }

    public String readWString() {
        return readString();
    }

    public void skip(BondDataType type) {
    }

    public ProtocolReader clone() {
        return null;
    }

    public int getPosition() throws IOException {
        throw new IOException();
    }

    public void setPosition(int position) throws IOException {
        throw new IOException();
    }
}
