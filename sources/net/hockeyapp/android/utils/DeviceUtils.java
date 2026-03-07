package net.hockeyapp.android.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import net.hockeyapp.android.Constants;

public class DeviceUtils {
    private DeviceUtils() {
    }

    private static class DeviceUtilsHolder {
        public static final DeviceUtils INSTANCE = new DeviceUtils();

        private DeviceUtilsHolder() {
        }
    }

    public static DeviceUtils getInstance() {
        return DeviceUtilsHolder.INSTANCE;
    }

    public int getCurrentVersionCode(Context context) {
        return Integer.parseInt(Constants.APP_VERSION);
    }

    public String getAppName(Context context) {
        if (context == null) {
            return "";
        }
        try {
            PackageManager pm = context.getPackageManager();
            if (pm == null) {
                return "";
            }
            return pm.getApplicationLabel(pm.getApplicationInfo(context.getPackageName(), 0)).toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }
}
