package com.microsoft.xbox.idp.toolkit;

public class HttpError {
    private final int errorCode;
    private final String errorMessage;
    private final int httpStatus;

    public HttpError(int errorCode2, int httpStatus2, String errorMessage2) {
        this.errorCode = errorCode2;
        this.httpStatus = httpStatus2;
        this.errorMessage = errorMessage2;
    }

    public int getErrorCode() {
        return this.errorCode;
    }

    public int getHttpStatus() {
        return this.httpStatus;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("errorCode: ").append(this.errorCode).append(", httpStatus: ").append(this.httpStatus).append(", errorMessage: ").append(this.errorMessage);
        return sb.toString();
    }
}
