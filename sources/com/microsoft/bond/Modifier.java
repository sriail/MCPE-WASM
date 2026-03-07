package com.microsoft.bond;

public enum Modifier {
    Optional(0),
    Required(1),
    RequiredOptional(2),
    __INVALID_ENUM_VALUE(3);
    
    private final int value;

    private Modifier(int value2) {
        this.value = value2;
    }

    public int getValue() {
        return this.value;
    }

    public static Modifier fromValue(int value2) {
        switch (value2) {
            case 0:
                return Optional;
            case 1:
                return Required;
            case 2:
                return RequiredOptional;
            default:
                return __INVALID_ENUM_VALUE;
        }
    }
}
