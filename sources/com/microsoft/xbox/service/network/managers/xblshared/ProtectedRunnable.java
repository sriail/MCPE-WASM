package com.microsoft.xbox.service.network.managers.xblshared;

public class ProtectedRunnable implements Runnable {
    private static final String TAG = ProtectedRunnable.class.getSimpleName();
    private final Runnable runnable;

    public ProtectedRunnable(Runnable runnable2) {
        this.runnable = runnable2;
    }

    public void run() {
        boolean success = false;
        int i = 0;
        while (!success && i < 10) {
            try {
                this.runnable.run();
                success = true;
            } catch (LinkageError e) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e2) {
                }
            }
            i++;
        }
        if (!success) {
        }
    }
}
