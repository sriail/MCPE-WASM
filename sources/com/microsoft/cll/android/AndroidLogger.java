package com.microsoft.cll.android;

import android.util.Log;

public class AndroidLogger implements ILogger {
    private static AndroidLogger INSTANCE;
    private static Object InstanceLock = new Object();
    private Verbosity verbosity;

    public static ILogger getInstance() {
        if (INSTANCE == null) {
            synchronized (InstanceLock) {
                if (INSTANCE == null) {
                    INSTANCE = new AndroidLogger();
                }
            }
        }
        return INSTANCE;
    }

    private AndroidLogger() {
        setVerbosity(Verbosity.NONE);
    }

    public void setVerbosity(Verbosity verbosity2) {
        this.verbosity = verbosity2;
    }

    public Verbosity getVerbosity() {
        return this.verbosity;
    }

    public void info(String TAG, String message) {
        if (this.verbosity == Verbosity.INFO) {
            Log.i(TAG, message);
        }
    }

    public void warn(String TAG, String message) {
        if (this.verbosity == Verbosity.WARN || this.verbosity == Verbosity.INFO) {
            Log.d(TAG, message);
        }
    }

    public void error(String TAG, String message) {
        if (this.verbosity == Verbosity.ERROR || this.verbosity == Verbosity.WARN || this.verbosity == Verbosity.INFO) {
            Log.e(TAG, message);
        }
    }
}
