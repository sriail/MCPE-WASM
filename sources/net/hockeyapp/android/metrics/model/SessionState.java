package net.hockeyapp.android.metrics.model;

public enum SessionState {
    START(0),
    END(1);
    
    private final int value;

    private SessionState(int value2) {
        this.value = value2;
    }

    public int getValue() {
        return this.value;
    }
}
