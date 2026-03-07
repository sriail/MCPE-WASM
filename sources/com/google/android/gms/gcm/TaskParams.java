package com.google.android.gms.gcm;

import android.os.Bundle;

public class TaskParams {
    private final Bundle extras;
    private final String tag;

    public TaskParams(String tag2) {
        this(tag2, (Bundle) null);
    }

    public TaskParams(String tag2, Bundle extras2) {
        this.tag = tag2;
        this.extras = extras2;
    }

    public Bundle getExtras() {
        return this.extras;
    }

    public String getTag() {
        return this.tag;
    }
}
