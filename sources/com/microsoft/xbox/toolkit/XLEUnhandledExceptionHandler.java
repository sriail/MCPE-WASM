package com.microsoft.xbox.toolkit;

import java.lang.Thread;
import java.util.Date;

public class XLEUnhandledExceptionHandler implements Thread.UncaughtExceptionHandler {
    public static XLEUnhandledExceptionHandler Instance = new XLEUnhandledExceptionHandler();
    private Thread.UncaughtExceptionHandler oldExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();

    public void uncaughtException(Thread thread, Throwable ex) {
        String th = ex.toString();
        if (ex.getCause() != null) {
            printStackTrace("CAUSE STACK TRACE", ex.getCause());
        }
        printStackTrace("MAIN THREAD STACK TRACE", ex);
        this.oldExceptionHandler.uncaughtException(thread, ex);
    }

    private void printStackTrace(String initialText, Throwable ex) {
        new Date();
        String text = "";
        for (StackTraceElement elem : ex.getStackTrace()) {
            text = text + String.format("\t%s\n", new Object[]{elem.toString()});
        }
    }
}
