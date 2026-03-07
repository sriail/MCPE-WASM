package com.microsoft.xbox.idp.telemetry.utc.model;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.os.Build;
import android.view.accessibility.AccessibilityManager;
import com.microsoft.xbox.idp.interop.Interop;
import com.microsoft.xbox.idp.telemetry.helpers.UTCLog;
import com.microsoft.xbox.idp.telemetry.utc.CommonData;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class UTCCommonDataModel {
    static final String DEFAULTSERVICES = "none";
    static final String EVENTVERSION = "1.1";
    static final String UNKNOWNAPP = "UNKNOWN";
    static final String UNKNOWNUSER = "UNKNOWN";
    static UTCAccessibilityInfoModel accessibilityInfo = null;
    static String appName = "UNKNOWN";
    static UUID applicationSession = null;
    static String deviceModel = null;
    static NetworkType netType = NetworkType.UNKNOWN;
    static String osLocale = null;
    static String userId = "UNKNOWN";

    private enum NetworkType {
        UNKNOWN(0),
        WIFI(1),
        CELLULAR(2),
        WIRED(3);
        
        private int value;

        public int getValue() {
            return this.value;
        }

        public void setValue(int value2) {
            this.value = value2;
        }

        private NetworkType(int val) {
            this.value = 0;
            setValue(val);
        }
    }

    public static CommonData getCommonData(int partCVersion) {
        return getCommonData(partCVersion, new UTCAdditionalInfoModel());
    }

    public static CommonData getCommonData(int partCVersion, UTCAdditionalInfoModel additionalInfo) {
        CommonData common = new CommonData();
        common.setEventVersion(String.format("%s.%s", new Object[]{EVENTVERSION, Integer.valueOf(partCVersion)}));
        common.setDeviceModel(getDeviceModel());
        common.setXsapiVersion("1.0");
        common.setAppName(getAppName());
        common.setClientLanguage(getDeviceLocale());
        common.setNetwork(getNetworkConnection().getValue());
        common.setSandboxId(getSandboxId());
        common.setAppSessionId(getAppSessionId());
        common.setUserId(getUserId());
        UTCAdditionalInfoModel info = additionalInfo;
        if (info == null) {
            info = new UTCAdditionalInfoModel();
        }
        common.setAdditionalInfo(info.toJson());
        common.setAccessibilityInfo(getAccessibilityInfo().toJson());
        common.setTitleDeviceId(Interop.getTitleDeviceId());
        common.setTitleSessionId(Interop.getTitleSessionId());
        return common;
    }

    public static String getUserId() {
        if (userId == null) {
            return "UNKNOWN";
        }
        return userId;
    }

    public static void setUserId(String userId2) {
        if (userId2 != null) {
            userId = "x:" + userId2;
        }
    }

    private static String getAppName() {
        try {
            Context ctx = Interop.getApplicationContext();
            if (appName == "UNKNOWN" && ctx != null) {
                appName = ctx.getApplicationInfo().packageName;
            }
        } catch (Exception ex) {
            UTCLog.log(ex.getMessage(), new Object[0]);
            appName = "UNKNOWN";
        }
        return appName;
    }

    private static String getDeviceModel() {
        if (deviceModel == null) {
            String model = Build.MODEL;
            deviceModel = "UNKNOWN";
            if (model != null && !model.isEmpty()) {
                deviceModel = removePipes(model);
            }
        }
        return deviceModel;
    }

    private static String getDeviceLocale() {
        if (osLocale == null) {
            try {
                Locale deviceLocale = Locale.getDefault();
                osLocale = String.format("%s-%s", new Object[]{deviceLocale.getLanguage(), deviceLocale.getCountry()});
            } catch (Exception ex) {
                UTCLog.log(ex.getMessage(), new Object[0]);
            }
        }
        return osLocale;
    }

    private static String getSandboxId() {
        return "";
    }

    private static String getAppSessionId() {
        if (applicationSession == null) {
            applicationSession = UUID.randomUUID();
        }
        return applicationSession.toString();
    }

    private static String removePipes(String parameter) {
        if (parameter != null) {
            return parameter.replace("|", "");
        }
        return parameter;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0030, code lost:
        netType = com.microsoft.xbox.idp.telemetry.utc.model.UTCCommonDataModel.NetworkType.UNKNOWN;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x003b, code lost:
        netType = com.microsoft.xbox.idp.telemetry.utc.model.UTCCommonDataModel.NetworkType.WIFI;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x003f, code lost:
        netType = com.microsoft.xbox.idp.telemetry.utc.model.UTCCommonDataModel.NetworkType.WIRED;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static com.microsoft.xbox.idp.telemetry.utc.model.UTCCommonDataModel.NetworkType getNetworkConnection() {
        /*
            com.microsoft.xbox.idp.telemetry.utc.model.UTCCommonDataModel$NetworkType r4 = netType
            com.microsoft.xbox.idp.telemetry.utc.model.UTCCommonDataModel$NetworkType r5 = com.microsoft.xbox.idp.telemetry.utc.model.UTCCommonDataModel.NetworkType.UNKNOWN
            if (r4 != r5) goto L_0x0034
            android.content.Context r4 = com.microsoft.xbox.idp.interop.Interop.getApplicationContext()
            if (r4 == 0) goto L_0x0034
            android.content.Context r4 = com.microsoft.xbox.idp.interop.Interop.getApplicationContext()     // Catch:{ Exception -> 0x0044 }
            java.lang.String r5 = "connectivity"
            java.lang.Object r0 = r4.getSystemService(r5)     // Catch:{ Exception -> 0x0044 }
            android.net.ConnectivityManager r0 = (android.net.ConnectivityManager) r0     // Catch:{ Exception -> 0x0044 }
            android.net.NetworkInfo r1 = r0.getActiveNetworkInfo()     // Catch:{ Exception -> 0x0044 }
            if (r1 != 0) goto L_0x0021
            com.microsoft.xbox.idp.telemetry.utc.model.UTCCommonDataModel$NetworkType r4 = netType     // Catch:{ Exception -> 0x0044 }
        L_0x0020:
            return r4
        L_0x0021:
            android.net.NetworkInfo$State r3 = r1.getState()     // Catch:{ Exception -> 0x0044 }
            android.net.NetworkInfo$State r4 = android.net.NetworkInfo.State.CONNECTED     // Catch:{ Exception -> 0x0044 }
            if (r3 != r4) goto L_0x0034
            int r4 = r1.getType()     // Catch:{ Exception -> 0x0044 }
            switch(r4) {
                case 0: goto L_0x0037;
                case 1: goto L_0x003b;
                case 6: goto L_0x0037;
                case 9: goto L_0x003f;
                default: goto L_0x0030;
            }     // Catch:{ Exception -> 0x0044 }
        L_0x0030:
            com.microsoft.xbox.idp.telemetry.utc.model.UTCCommonDataModel$NetworkType r4 = com.microsoft.xbox.idp.telemetry.utc.model.UTCCommonDataModel.NetworkType.UNKNOWN     // Catch:{ Exception -> 0x0044 }
            netType = r4     // Catch:{ Exception -> 0x0044 }
        L_0x0034:
            com.microsoft.xbox.idp.telemetry.utc.model.UTCCommonDataModel$NetworkType r4 = netType
            goto L_0x0020
        L_0x0037:
            com.microsoft.xbox.idp.telemetry.utc.model.UTCCommonDataModel$NetworkType r4 = com.microsoft.xbox.idp.telemetry.utc.model.UTCCommonDataModel.NetworkType.CELLULAR     // Catch:{ Exception -> 0x0044 }
            netType = r4     // Catch:{ Exception -> 0x0044 }
        L_0x003b:
            com.microsoft.xbox.idp.telemetry.utc.model.UTCCommonDataModel$NetworkType r4 = com.microsoft.xbox.idp.telemetry.utc.model.UTCCommonDataModel.NetworkType.WIFI     // Catch:{ Exception -> 0x0044 }
            netType = r4     // Catch:{ Exception -> 0x0044 }
        L_0x003f:
            com.microsoft.xbox.idp.telemetry.utc.model.UTCCommonDataModel$NetworkType r4 = com.microsoft.xbox.idp.telemetry.utc.model.UTCCommonDataModel.NetworkType.WIRED     // Catch:{ Exception -> 0x0044 }
            netType = r4     // Catch:{ Exception -> 0x0044 }
            goto L_0x0030
        L_0x0044:
            r2 = move-exception
            java.lang.String r4 = r2.getMessage()
            r5 = 0
            java.lang.Object[] r5 = new java.lang.Object[r5]
            com.microsoft.xbox.idp.telemetry.helpers.UTCLog.log(r4, r5)
            com.microsoft.xbox.idp.telemetry.utc.model.UTCCommonDataModel$NetworkType r4 = com.microsoft.xbox.idp.telemetry.utc.model.UTCCommonDataModel.NetworkType.UNKNOWN
            netType = r4
            goto L_0x0034
        */
        throw new UnsupportedOperationException("Method not decompiled: com.microsoft.xbox.idp.telemetry.utc.model.UTCCommonDataModel.getNetworkConnection():com.microsoft.xbox.idp.telemetry.utc.model.UTCCommonDataModel$NetworkType");
    }

    private static UTCAccessibilityInfoModel getAccessibilityInfo() {
        if (accessibilityInfo != null) {
            return accessibilityInfo;
        }
        accessibilityInfo = new UTCAccessibilityInfoModel();
        try {
            Context ctx = Interop.getApplicationContext();
            if (ctx != null) {
                AccessibilityManager manager = (AccessibilityManager) ctx.getSystemService("accessibility");
                accessibilityInfo.addValue("isenabled", Boolean.valueOf(manager.isEnabled()));
                List<AccessibilityServiceInfo> serviceInfoList = manager.getEnabledAccessibilityServiceList(-1);
                String services = DEFAULTSERVICES;
                for (AccessibilityServiceInfo info : serviceInfoList) {
                    if (services.equals(DEFAULTSERVICES)) {
                        services = info.getId();
                    } else {
                        services = services + String.format(";%s", new Object[]{info.getId()});
                    }
                }
                accessibilityInfo.addValue("enabledservices", services);
            }
        } catch (Exception e) {
            UTCLog.log(e.getMessage(), new Object[0]);
        }
        return accessibilityInfo;
    }
}
