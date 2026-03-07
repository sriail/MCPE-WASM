package com.microsoft.bond.internal;

public final class DecimalHelper {
    public static final int SIZEOF_DOUBLE = 8;
    public static final int SIZEOF_FLOAT = 4;

    private DecimalHelper() {
    }

    public static float decodeFloat(byte[] buffer) {
        return Float.intBitsToFloat((buffer[0] & 255) | ((buffer[1] & 255) << 8) | ((buffer[2] & 255) << 16) | ((buffer[3] & 255) << 24));
    }

    public static double decodeDouble(byte[] buffer) {
        return Double.longBitsToDouble((((long) buffer[0]) & 255) | ((((long) buffer[1]) & 255) << 8) | ((((long) buffer[2]) & 255) << 16) | ((((long) buffer[3]) & 255) << 24) | ((((long) buffer[4]) & 255) << 32) | ((((long) buffer[5]) & 255) << 40) | ((((long) buffer[6]) & 255) << 48) | ((((long) buffer[7]) & 255) << 56));
    }

    public static void encodeFloat(float value, byte[] writeBuffer) {
        int valueBits = Float.floatToRawIntBits(value);
        writeBuffer[0] = (byte) valueBits;
        writeBuffer[1] = (byte) (valueBits >> 8);
        writeBuffer[2] = (byte) (valueBits >> 16);
        writeBuffer[3] = (byte) (valueBits >> 24);
    }

    public static void encodeDouble(double value, byte[] writeBuffer) {
        long valueBits = Double.doubleToRawLongBits(value);
        writeBuffer[0] = (byte) ((int) valueBits);
        writeBuffer[1] = (byte) ((int) (valueBits >> 8));
        writeBuffer[2] = (byte) ((int) (valueBits >> 16));
        writeBuffer[3] = (byte) ((int) (valueBits >> 24));
        writeBuffer[4] = (byte) ((int) (valueBits >> 32));
        writeBuffer[5] = (byte) ((int) (valueBits >> 40));
        writeBuffer[6] = (byte) ((int) (valueBits >> 48));
        writeBuffer[7] = (byte) ((int) (valueBits >> 56));
    }
}
