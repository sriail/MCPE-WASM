package com.microsoft.xbox.toolkit;

public class AsyncResult<T> {
    private final XLEException exception;
    private final T result;
    private final Object sender;
    private AsyncActionStatus status;

    /* JADX INFO: this call moved to the top of the method (can break code semantics) */
    public AsyncResult(T result2, Object sender2, XLEException exception2) {
        this(result2, sender2, exception2, exception2 == null ? AsyncActionStatus.SUCCESS : AsyncActionStatus.FAIL);
    }

    public AsyncResult(T result2, Object sender2, XLEException exception2, AsyncActionStatus status2) {
        this.sender = sender2;
        this.exception = exception2;
        this.result = result2;
        this.status = status2;
    }

    public Object getSender() {
        return this.sender;
    }

    public XLEException getException() {
        return this.exception;
    }

    public T getResult() {
        return this.result;
    }

    public AsyncActionStatus getStatus() {
        return this.status;
    }
}
