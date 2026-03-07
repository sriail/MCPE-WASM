package com.microsoft.onlineid.internal.log;

import android.util.Log;

public class LogInstance {
    public static final String LogTag = "MSA";
    public static final int MaxLogLength = 4000;
    private boolean _isLoggingEnabled = false;
    private boolean _isRedactionEnabled = true;
    private boolean _isStackTraceLoggingEnabled = true;

    protected LogInstance(boolean isRedactionEnabled, boolean isLoggingEnabled, boolean isStackTraceLoggingEnabled) {
        this._isRedactionEnabled = isRedactionEnabled;
        this._isLoggingEnabled = isLoggingEnabled;
        this._isStackTraceLoggingEnabled = isStackTraceLoggingEnabled;
    }

    protected LogInstance() {
    }

    /* access modifiers changed from: protected */
    public void setIsRedactionEnable(boolean isRedactionEnabled) {
        this._isRedactionEnabled = isRedactionEnabled;
    }

    /* access modifiers changed from: protected */
    public void setIsLoggingEnabled(boolean isLoggingEnabled) {
        this._isLoggingEnabled = isLoggingEnabled;
    }

    /* access modifiers changed from: protected */
    public void setIsStackTraceLoggingEnabled(boolean isStackTraceLoggingEnabled) {
        this._isStackTraceLoggingEnabled = isStackTraceLoggingEnabled;
    }

    /* access modifiers changed from: protected */
    public void logInfo(String message) {
        Log.i(LogTag, message);
    }

    /* access modifiers changed from: protected */
    public void logInfo(String message, Throwable throwable) {
        Log.i(LogTag, message, throwable);
    }

    /* access modifiers changed from: protected */
    public void logWarning(String message) {
        Log.w(LogTag, message);
    }

    /* access modifiers changed from: protected */
    public void logWarning(String message, Throwable throwable) {
        Log.w(LogTag, message, throwable);
    }

    /* access modifiers changed from: protected */
    public void logError(String message) {
        Log.e(LogTag, message);
    }

    /* access modifiers changed from: protected */
    public void logError(String message, Throwable throwable) {
        Log.e(LogTag, message, throwable);
    }

    /* access modifiers changed from: protected */
    public boolean shouldRedact() {
        return this._isRedactionEnabled && this._isLoggingEnabled;
    }

    /* access modifiers changed from: protected */
    public String getStackTraceInfo(String message, int stackTraceDepth) {
        if (!this._isStackTraceLoggingEnabled) {
            return message;
        }
        StringBuilder returnValue = new StringBuilder();
        try {
            returnValue.append(message);
            StackTraceElement[] stackTraceInfo = Thread.currentThread().getStackTrace();
            int index = 0;
            int length = stackTraceInfo.length;
            int i = 0;
            while (true) {
                if (i >= length) {
                    break;
                }
                index++;
                if (stackTraceInfo[i].getMethodName().contains("getStackTrace")) {
                    int index2 = index + stackTraceDepth;
                    returnValue.append(" ");
                    returnValue.append(stackTraceInfo[index2].getMethodName());
                    returnValue.append("()@");
                    returnValue.append(stackTraceInfo[index2].getFileName());
                    returnValue.append("_");
                    returnValue.append(stackTraceInfo[index2].getLineNumber());
                    break;
                }
                i++;
            }
        } catch (Exception e) {
            logWarning("Error in getStackTraceInfo", e);
        }
        return returnValue.toString();
    }

    /* access modifiers changed from: protected */
    public void logMessage(String message, int loggingLevel, Throwable throwable) {
        logMessage(message, loggingLevel, throwable, 4);
    }

    /* access modifiers changed from: protected */
    public void logMessage(String message, int loggingLevel, Throwable throwable, int stackTraceDepth) {
        if (message != null && this._isLoggingEnabled && Log.isLoggable(LogTag, loggingLevel)) {
            int len = message.length();
            int start = 0;
            while (start < len) {
                int end = Math.min(len, start + MaxLogLength);
                logMessageLevel(getStackTraceInfo(message.substring(start, end), stackTraceDepth), loggingLevel, throwable);
                start = end;
            }
        }
    }

    private void logMessageLevel(String message, int loggingLevel, Throwable throwable) {
        switch (loggingLevel) {
            case 5:
                if (throwable == null) {
                    logWarning(message);
                    return;
                } else {
                    logWarning(message, throwable);
                    return;
                }
            case 6:
                if (throwable == null) {
                    logError(message);
                    return;
                } else {
                    logError(message, throwable);
                    return;
                }
            default:
                if (throwable == null) {
                    logInfo(message);
                    return;
                } else {
                    logInfo(message, throwable);
                    return;
                }
        }
    }

    /* access modifiers changed from: protected */
    public void logRedactedMessage(IRedactable redactableMessage, int loggingLevel) {
        if (redactableMessage == null) {
            return;
        }
        if (this._isRedactionEnabled) {
            logMessage(redactableMessage.getRedactedString(), loggingLevel, (Throwable) null, 4);
        } else {
            logMessage(redactableMessage.getUnredactedString(), loggingLevel, (Throwable) null, 4);
        }
    }
}
