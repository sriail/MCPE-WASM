package com.microsoft.xbox.toolkit.system;

import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xboxtcui.XboxTcuiSdk;

public class SystemUtil {
    private static final int MAX_SD_SCREEN_PIXELS = 384000;

    public static int getSdkInt() {
        return Build.VERSION.SDK_INT;
    }

    public static int DIPtoPixels(float dip) {
        return (int) TypedValue.applyDimension(1, dip, XboxTcuiSdk.getResources().getDisplayMetrics());
    }

    public static int SPtoPixels(float sp) {
        return (int) TypedValue.applyDimension(2, sp, XboxTcuiSdk.getResources().getDisplayMetrics());
    }

    public static int getScreenWidth() {
        return XboxTcuiSdk.getResources().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return XboxTcuiSdk.getResources().getDisplayMetrics().heightPixels;
    }

    public static int getColorDepth() {
        PixelFormat.getPixelFormatInfo(1, (PixelFormat) null);
        return null.bitsPerPixel;
    }

    public static float getScreenWidthInches() {
        return ((float) getScreenWidth()) / XboxTcuiSdk.getResources().getDisplayMetrics().xdpi;
    }

    public static float getScreenHeightInches() {
        return ((float) getScreenHeight()) / XboxTcuiSdk.getResources().getDisplayMetrics().ydpi;
    }

    public static float getYDPI() {
        return XboxTcuiSdk.getResources().getDisplayMetrics().ydpi;
    }

    public static int getRotation() {
        return getDisplay().getRotation();
    }

    public static int getOrientation() {
        int rotation = getRotation();
        if (rotation == 0 || rotation == 2) {
            return 1;
        }
        return 2;
    }

    public static boolean isHDScreen() {
        return getScreenHeight() * getScreenWidth() > MAX_SD_SCREEN_PIXELS;
    }

    public static boolean isSlate() {
        return Math.sqrt(Math.pow((double) getScreenWidthInches(), 2.0d) + Math.pow((double) getScreenHeightInches(), 2.0d)) > 6.0d;
    }

    public static String getDeviceType() {
        XLEAssert.assertTrue(false);
        return "";
    }

    private static Display getDisplay() {
        return ((WindowManager) XboxTcuiSdk.getSystemService("window")).getDefaultDisplay();
    }

    public static boolean isSDCardAvailable() {
        return Environment.getExternalStorageState().equals("mounted");
    }

    public static float getScreenWidthHeightAspectRatio() {
        int screenWidth = getScreenWidth();
        int screenHeight = getScreenHeight();
        if (screenWidth <= 0 || screenHeight <= 0) {
            return 0.0f;
        }
        if (screenWidth > screenHeight) {
            return ((float) screenWidth) / ((float) screenHeight);
        }
        return ((float) screenHeight) / ((float) screenWidth);
    }

    public static String getDeviceId() {
        return Settings.Secure.getString(XboxTcuiSdk.getContentResolver(), "android_id");
    }

    public static String getDeviceModelName() {
        return Build.MODEL;
    }

    /* JADX WARNING: Removed duplicated region for block: B:4:0x0012 A[Catch:{ Exception -> 0x0062 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.String getMACAddress(java.lang.String r10) {
        /*
            java.util.Enumeration r6 = java.net.NetworkInterface.getNetworkInterfaces()     // Catch:{ Exception -> 0x0062 }
            java.util.ArrayList r3 = java.util.Collections.list(r6)     // Catch:{ Exception -> 0x0062 }
            java.util.Iterator r1 = r3.iterator()     // Catch:{ Exception -> 0x0062 }
        L_0x000c:
            boolean r6 = r1.hasNext()     // Catch:{ Exception -> 0x0062 }
            if (r6 == 0) goto L_0x0063
            java.lang.Object r4 = r1.next()     // Catch:{ Exception -> 0x0062 }
            java.net.NetworkInterface r4 = (java.net.NetworkInterface) r4     // Catch:{ Exception -> 0x0062 }
            if (r10 == 0) goto L_0x0024
            java.lang.String r6 = r4.getName()     // Catch:{ Exception -> 0x0062 }
            boolean r6 = r6.equalsIgnoreCase(r10)     // Catch:{ Exception -> 0x0062 }
            if (r6 == 0) goto L_0x000c
        L_0x0024:
            byte[] r5 = r4.getHardwareAddress()     // Catch:{ Exception -> 0x0062 }
            if (r5 != 0) goto L_0x002d
            java.lang.String r6 = ""
        L_0x002c:
            return r6
        L_0x002d:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0062 }
            r0.<init>()     // Catch:{ Exception -> 0x0062 }
            r2 = 0
        L_0x0033:
            int r6 = r5.length     // Catch:{ Exception -> 0x0062 }
            if (r2 >= r6) goto L_0x004e
            java.lang.String r6 = "%02X:"
            r7 = 1
            java.lang.Object[] r7 = new java.lang.Object[r7]     // Catch:{ Exception -> 0x0062 }
            r8 = 0
            byte r9 = r5[r2]     // Catch:{ Exception -> 0x0062 }
            java.lang.Byte r9 = java.lang.Byte.valueOf(r9)     // Catch:{ Exception -> 0x0062 }
            r7[r8] = r9     // Catch:{ Exception -> 0x0062 }
            java.lang.String r6 = java.lang.String.format(r6, r7)     // Catch:{ Exception -> 0x0062 }
            r0.append(r6)     // Catch:{ Exception -> 0x0062 }
            int r2 = r2 + 1
            goto L_0x0033
        L_0x004e:
            int r6 = r0.length()     // Catch:{ Exception -> 0x0062 }
            if (r6 <= 0) goto L_0x005d
            int r6 = r0.length()     // Catch:{ Exception -> 0x0062 }
            int r6 = r6 + -1
            r0.deleteCharAt(r6)     // Catch:{ Exception -> 0x0062 }
        L_0x005d:
            java.lang.String r6 = r0.toString()     // Catch:{ Exception -> 0x0062 }
            goto L_0x002c
        L_0x0062:
            r6 = move-exception
        L_0x0063:
            java.lang.String r6 = ""
            goto L_0x002c
        */
        throw new UnsupportedOperationException("Method not decompiled: com.microsoft.xbox.toolkit.system.SystemUtil.getMACAddress(java.lang.String):java.lang.String");
    }

    public static void TEST_randomSleep(int maxSeconds) {
        XLEAssert.assertTrue(false);
    }

    public static boolean TEST_randomFalseOutOf(int max) {
        XLEAssert.assertTrue(false);
        return true;
    }

    public static boolean isKindle() {
        String manufecturer = Build.MANUFACTURER;
        return manufecturer != null && "AMAZON".compareToIgnoreCase(manufecturer) == 0;
    }
}
