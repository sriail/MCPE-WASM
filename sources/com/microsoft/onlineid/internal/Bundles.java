package com.microsoft.onlineid.internal;

import android.os.Bundle;
import com.microsoft.onlineid.internal.log.Logger;

public class Bundles {
    public static void log(String prefix, Bundle bundle) {
        if (bundle != null) {
            Logger.info(prefix);
            for (String key : bundle.keySet()) {
                Object value = bundle.get(key);
                if (value != null) {
                    Logger.info(String.format("%s: %s (%s)", new Object[]{key, value.toString(), value.getClass().getName()}));
                } else {
                    Logger.info(String.format("%s: null", new Object[]{key}));
                }
            }
            return;
        }
        Logger.info(prefix + " (bundle was null)");
    }
}
