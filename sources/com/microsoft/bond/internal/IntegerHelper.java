package com.microsoft.bond.internal;

import android.support.v4.media.TransportMediator;
import com.microsoft.bond.io.BondInputStream;
import java.io.IOException;

public final class IntegerHelper {
    public static final int MAX_BYTES_VARINT16 = 3;
    public static final int MAX_BYTES_VARINT32 = 5;
    public static final int MAX_BYTES_VARINT64 = 10;
    public static final int MAX_VARINT_SIZE_BYTES = 10;
    public static final int SIZEOF_BYTE = 1;
    public static final int SIZEOF_INT = 4;
    public static final int SIZEOF_LONG = 8;
    public static final int SIZEOF_SHORT = 2;

    private IntegerHelper() {
    }

    public static int encodeVarUInt16(byte[] dst, short src) {
        return encodeVarUInt16(src, dst, 0);
    }

    public static int encodeVarUInt16(short src, byte[] dst, int offset) {
        if ((65408 & src) != 0) {
            int offset2 = offset + 1;
            dst[offset] = (byte) ((src & 127) | 128);
            src = (short) (src >>> 7);
            if ((65408 & src) != 0) {
                offset = offset2 + 1;
                dst[offset2] = (byte) ((src & 127) | 128);
                src = (short) (src >>> 7);
            } else {
                offset = offset2;
            }
        }
        int offset3 = offset + 1;
        dst[offset] = (byte) (src & 127);
        return offset3;
    }

    public static int encodeVarUInt32(int src, byte[] dst) {
        return encodeVarUInt32(src, dst, 0);
    }

    public static int encodeVarUInt32(int src, byte[] dst, int offset) {
        if ((src & -128) != 0) {
            int offset2 = offset + 1;
            dst[offset] = (byte) ((src & TransportMediator.KEYCODE_MEDIA_PAUSE) | 128);
            src >>>= 7;
            if ((src & -128) != 0) {
                offset = offset2 + 1;
                dst[offset2] = (byte) ((src & TransportMediator.KEYCODE_MEDIA_PAUSE) | 128);
                src >>>= 7;
                if ((src & -128) != 0) {
                    offset2 = offset + 1;
                    dst[offset] = (byte) ((src & TransportMediator.KEYCODE_MEDIA_PAUSE) | 128);
                    src >>>= 7;
                    if ((src & -128) != 0) {
                        offset = offset2 + 1;
                        dst[offset2] = (byte) ((src & TransportMediator.KEYCODE_MEDIA_PAUSE) | 128);
                        src >>>= 7;
                    }
                }
            }
            offset = offset2;
        }
        int offset3 = offset + 1;
        dst[offset] = (byte) (src & TransportMediator.KEYCODE_MEDIA_PAUSE);
        return offset3;
    }

    public static int encodeVarUInt64(long src, byte[] dst) {
        return encodeVarUInt64(src, dst, 0);
    }

    public static int encodeVarUInt64(long src, byte[] dst, int offset) {
        if ((-128 & src) != 0) {
            int offset2 = offset + 1;
            dst[offset] = (byte) ((int) ((127 & src) | 128));
            src >>>= 7;
            if ((-128 & src) != 0) {
                offset = offset2 + 1;
                dst[offset2] = (byte) ((int) ((127 & src) | 128));
                src >>>= 7;
                if ((-128 & src) != 0) {
                    offset2 = offset + 1;
                    dst[offset] = (byte) ((int) ((127 & src) | 128));
                    src >>>= 7;
                    if ((-128 & src) != 0) {
                        offset = offset2 + 1;
                        dst[offset2] = (byte) ((int) ((127 & src) | 128));
                        src >>>= 7;
                        if ((-128 & src) != 0) {
                            offset2 = offset + 1;
                            dst[offset] = (byte) ((int) ((127 & src) | 128));
                            src >>>= 7;
                            if ((-128 & src) != 0) {
                                offset = offset2 + 1;
                                dst[offset2] = (byte) ((int) ((127 & src) | 128));
                                src >>>= 7;
                                if ((-128 & src) != 0) {
                                    offset2 = offset + 1;
                                    dst[offset] = (byte) ((int) ((127 & src) | 128));
                                    src >>>= 7;
                                    if ((-128 & src) != 0) {
                                        offset = offset2 + 1;
                                        dst[offset2] = (byte) ((int) ((127 & src) | 128));
                                        src >>>= 7;
                                        if ((-128 & src) != 0) {
                                            dst[offset] = (byte) ((int) ((127 & src) | 128));
                                            src >>>= 7;
                                            offset++;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            offset = offset2;
        }
        int offset3 = offset + 1;
        dst[offset] = (byte) ((int) (127 & src));
        return offset3;
    }

    public static short decodeVarInt16(BondInputStream stream) throws IOException {
        return (short) ((int) decodeVarInt64(stream));
    }

    public static int decodeVarInt32(BondInputStream stream) throws IOException {
        return (int) decodeVarInt64(stream);
    }

    public static long decodeVarInt64(BondInputStream stream) throws IOException {
        long result = 0;
        for (int shift = 0; shift < 64; shift += 7) {
            byte raw = stream.read();
            result |= ((long) (raw & Byte.MAX_VALUE)) << shift;
            if ((raw & 128) == 0) {
                break;
            }
        }
        return result;
    }

    public static short encodeZigzag16(short value) {
        return (short) ((value << 1) ^ (value >> 15));
    }

    public static int encodeZigzag32(int value) {
        return (value << 1) ^ (value >> 31);
    }

    public static long encodeZigzag64(long value) {
        return (value << 1) ^ (value >> 63);
    }

    public static short decodeZigzag16(short value) {
        return (short) (((65535 & value) >>> 1) ^ (-(value & 1)));
    }

    public static int decodeZigzag32(int value) {
        return (value >>> 1) ^ (-(value & 1));
    }

    public static long decodeZigzag64(long value) {
        return (value >>> 1) ^ (-(1 & value));
    }

    public static int getVarUInt16Size(short value) {
        if ((65408 & value) == 0) {
            return 1;
        }
        if ((49152 & value) != 0) {
            return 3;
        }
        return 2;
    }

    public static int getVarUInt32Size(int value) {
        if ((-2097152 & value) != 0) {
            if ((-268435456 & value) != 0) {
                return 5;
            }
            return 4;
        } else if ((value & -128) == 0) {
            return 1;
        } else {
            if ((value & -16384) != 0) {
                return 3;
            }
            return 2;
        }
    }

    public static int getVarUInt64Size(long value) {
        if (0 != (-34359738368L & value)) {
            if (0 != (-562949953421312L & value)) {
                if (0 == (-72057594037927936L & value)) {
                    return 8;
                }
                if (0 != (Long.MIN_VALUE & value)) {
                    return 10;
                }
                return 9;
            } else if (0 != (-4398046511104L & value)) {
                return 7;
            } else {
                return 6;
            }
        } else if (0 != (-2097152 & value)) {
            if (0 != (-268435456 & value)) {
                return 5;
            }
            return 4;
        } else if (0 == (-128 & value)) {
            return 1;
        } else {
            if (0 != (-16384 & value)) {
                return 3;
            }
            return 2;
        }
    }

    public static void writeBigEndianInt32(int value, byte[] writeBuffer) {
        writeBuffer[3] = (byte) value;
        writeBuffer[2] = (byte) (value >> 8);
        writeBuffer[1] = (byte) (value >> 16);
        writeBuffer[0] = (byte) (value >> 24);
    }

    public static int readBigEndianInt32(byte[] buffer) {
        return (buffer[3] & 255) | ((buffer[2] & 255) << 8) | ((buffer[1] & 255) << 16) | ((buffer[0] & 255) << 24);
    }
}
