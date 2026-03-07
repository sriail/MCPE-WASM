package com.microsoft.bond;

public enum ProtocolVersion {
    ONE(1),
    TWO(2);
    
    private short value;

    private ProtocolVersion(int value2) {
        this.value = (short) value2;
    }

    public short getValue() {
        return this.value;
    }

    public static ProtocolVersion fromValue(short value2) {
        switch (value2) {
            case 1:
                return ONE;
            case 2:
                return TWO;
            default:
                return null;
        }
    }
}
