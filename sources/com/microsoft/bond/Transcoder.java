package com.microsoft.bond;

import java.io.IOException;

public class Transcoder {
    public static void transcode(ProtocolWriter dst, ProtocolReader src) throws IOException {
        if (src.hasCapability(ProtocolCapability.CAN_SEEK) && src.isProtocolSame(dst)) {
            int start = src.getPosition();
            src.skip(BondDataType.BT_STRUCT);
            src.setPosition(start);
            dst.writeBlob(src.readBlob(src.getPosition() - start));
        }
    }
}
