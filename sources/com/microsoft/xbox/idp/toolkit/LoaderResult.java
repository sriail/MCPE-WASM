package com.microsoft.xbox.idp.toolkit;

public abstract class LoaderResult<T> {
    private final T data;
    private final HttpError error;

    public abstract boolean isReleased();

    public abstract void release();

    protected LoaderResult(T data2, HttpError error2) {
        this.data = data2;
        this.error = error2;
    }

    public T getData() {
        return this.data;
    }

    public HttpError getError() {
        return this.error;
    }

    public boolean hasData() {
        return this.data != null;
    }

    public boolean hasError() {
        return this.error != null;
    }
}
