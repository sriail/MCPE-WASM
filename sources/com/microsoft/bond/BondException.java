package com.microsoft.bond;

import java.io.IOException;

public class BondException extends RuntimeException {
    private static final long serialVersionUID = 1;

    public BondException(String msg) {
        super(msg);
    }

    public BondException(IOException innerException) {
        super(innerException);
    }
}
