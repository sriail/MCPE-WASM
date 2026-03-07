package com.microsoft.xbox.toolkit;

public class XLEException extends Exception {
    private long errorCode;
    private boolean isHandled;
    private Object userObject;

    public XLEException(long errorCode2) {
        this(errorCode2, (String) null, (Throwable) null, (Object) null);
    }

    public XLEException(long errorCode2, String message) {
        this(errorCode2, message, (Throwable) null, (Object) null);
    }

    public XLEException(long errorCode2, Throwable innerException) {
        this(errorCode2, (String) null, innerException, (Object) null);
    }

    public XLEException(long errorCode2, String message, Throwable innerException) {
        this(errorCode2, (String) null, innerException, (Object) null);
    }

    public XLEException(long errorCode2, String message, Throwable innerException, Object userObject2) {
        super(message, innerException);
        this.errorCode = errorCode2;
        this.userObject = userObject2;
        this.isHandled = false;
    }

    public long getErrorCode() {
        return this.errorCode;
    }

    public Object getUserObject() {
        return this.userObject;
    }

    public void setIsHandled(boolean isHandled2) {
        this.isHandled = isHandled2;
    }

    public boolean getIsHandled() {
        return this.isHandled;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("XLEException ErrorCode: %d; ErrorMessage: %s \n\n", new Object[]{Long.valueOf(this.errorCode), getMessage()}));
        if (getCause() != null) {
            builder.append(String.format("\t Cause ErrorMessage: %s, StackTrace: ", new Object[]{getCause().toString()}));
            StackTraceElement[] arr$ = getCause().getStackTrace();
            int len$ = arr$.length;
            for (int i$ = 0; i$ < len$; i$++) {
                builder.append("\n\n \t " + arr$[i$].toString());
            }
        }
        return builder.toString();
    }
}
