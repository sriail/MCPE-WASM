package com.microsoft.xbox.service.model;

import android.os.Bundle;

public final class UpdateData {
    private final Bundle extra;
    private final boolean isFinal;
    private final UpdateType updateType;

    public UpdateData(UpdateType updateType2, boolean isFinal2) {
        this.updateType = updateType2;
        this.isFinal = isFinal2;
        this.extra = null;
    }

    public UpdateData(UpdateType updateType2, boolean isFinal2, Bundle extra2) {
        this.updateType = updateType2;
        this.isFinal = isFinal2;
        this.extra = extra2;
    }

    public UpdateType getUpdateType() {
        return this.updateType;
    }

    public boolean getIsFinal() {
        return this.isFinal;
    }

    public Bundle getExtra() {
        return this.extra;
    }
}
