package com.microsoft.onlineid.sts;

import android.content.Context;
import android.content.SharedPreferences;
import com.microsoft.onlineid.internal.configuration.AbstractSettings;
import com.microsoft.onlineid.internal.configuration.Environment;
import com.microsoft.onlineid.internal.configuration.ISetting;
import com.microsoft.onlineid.internal.configuration.Setting;
import com.microsoft.onlineid.internal.configuration.Settings;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Set;

public class ServerConfig extends AbstractSettings {
    public static Setting<Set<String>> AndroidSsoCertificates = new Setting<>("AndroidSsoCerts", Collections.singleton("sX6CAbEo4edMwCNRCrfqA6wn3eUNMtgQ6hV3dY8cwJg="));
    public static final String DefaultConfigVersion = "1";
    /* access modifiers changed from: private */
    public static String Domain = (Settings.isDebugBuild() ? "live-int.com" : "live.com");
    public static Setting<String> EnvironmentName = new Setting<>("environment", Settings.isDebugBuild() ? "int" : "production");
    public static Setting<Integer> NgcCloudPinLength = new Setting<>("cloud_pin_length", 4);
    static final String StorageName = "ServerConfig";
    public static Setting<String> Version = new Setting<>("ConfigVersion", "1");

    public enum KnownEnvironment {
        Production("production", "https://go.microsoft.com/fwlink/?LinkId=398559"),
        Int("int", "https://go.microsoft.com/fwlink/?LinkId=398560");
        
        private final Environment _environment;

        private KnownEnvironment(String name, String configUrl) {
            try {
                this._environment = new Environment(name, new URL(configUrl));
            } catch (MalformedURLException e) {
                throw new IllegalArgumentException("Invalid known environment URL: " + configUrl);
            }
        }

        public Environment getEnvironment() {
            return this._environment;
        }
    }

    public enum Int implements ISetting<Integer> {
        ConnectTimeout("ConnectTimeout", 10000),
        SendTimeout("SendTimeout", 30000),
        ReceiveTimeout("ReceiveTimeout", 30000),
        BackupSlaveCount("BackupSlaveCount", 3),
        MaxSecondsBetweenBackups("MaxSecondsBetweenBackups", 259200),
        MinSecondsBetweenConfigDownloads("MinSecondsBetweenConfigDownloads", 28800),
        MaxTriesForSsoRequestToSingleService("MaxTriesForSsoRequestToSingleService", 2),
        MaxTriesForSsoRequestWithFallback("MaxTriesForSsoRequestWithFallback", 4);
        
        private final Integer _defaultValue;
        private final String _settingName;

        private Int(String settingName, int defaultValue) {
            this._settingName = settingName;
            this._defaultValue = Integer.valueOf(defaultValue);
        }

        public String getSettingName() {
            return this._settingName;
        }

        public Integer getDefaultValue() {
            return this._defaultValue;
        }
    }

    public enum Endpoint implements ISetting<URL> {
        ;
        
        private final URL _defaultValue;
        private final String _settingName;

        static {
            String externalForm;
            if (Settings.isDebugBuild()) {
                externalForm = KnownEnvironment.Int.getEnvironment().getConfigUrl().toExternalForm();
            } else {
                externalForm = KnownEnvironment.Production.getEnvironment().getConfigUrl().toExternalForm();
            }
            Configuration = new Endpoint("Configuration", 0, "ConfigUrl", externalForm);
            Sts = new Endpoint("Sts", 1, "WLIDSTS_WCF", "https://login." + ServerConfig.Domain + ":443/RST2.srf");
            DeviceProvision = new Endpoint("DeviceProvision", 2, "DeviceAddService", "https://login." + ServerConfig.Domain + "/ppsecure/deviceaddcredential.srf");
            ManageApprover = new Endpoint("ManageApprover", 3, "ManageApprover", "https://login." + ServerConfig.Domain + "/ManageApprover.srf");
            ManageLoginKeys = new Endpoint("ManageLoginKeys", 4, "ManageLoginKeys", "https://login." + ServerConfig.Domain + "/ManageLoginKeys.srf");
            ListSessions = new Endpoint("ListSessions", 5, "ListSessions", "https://login." + ServerConfig.Domain + "/ListSessions.srf");
            ApproveSession = new Endpoint("ApproveSession", 6, "ApproveSession", "https://login." + ServerConfig.Domain + "/ApproveSession.srf");
            ConnectMsa = new Endpoint("ConnectMsa", 7, "CPConnect", "https://login." + ServerConfig.Domain + "/ppsecure/InlineConnect.srf?id=80601");
            ConnectPartner = new Endpoint("ConnectPartner", 8, "CompleteAccountConnect", "https://login." + ServerConfig.Domain + "/ppsecure/InlineConnect.srf?id=80604");
            SignInMsa = new Endpoint("SignInMsa", 9, "CPSignInAuthUp", "https://login." + ServerConfig.Domain + "/ppsecure/InlineLogin.srf?id=80601");
            SignInPartner = new Endpoint("SignInPartner", 10, "CompleteAccountSignIn", "https://login." + ServerConfig.Domain + "/ppsecure/InlineLogin.srf?id=80604");
            SignupMsa = new Endpoint("SignupMsa", 11, "SignupMsa", "https://signup." + ServerConfig.Domain + "/signup?id=80601");
            SignupPartner = new Endpoint("SignupPartner", 12, "SignupPartner", "https://signup." + ServerConfig.Domain + "/signup?id=80604");
            SignupWReplyMsa = new Endpoint("SignupWReplyMsa", 13, "SignupWReplyMsa", "https://login." + ServerConfig.Domain + "/ppsecure/InlineLogin.srf?id=80601&actionid=7");
            SignupWReplyPartner = new Endpoint("SignupWReplyPartner", 14, "SignupWReplyPartner", "https://login." + ServerConfig.Domain + "/ppsecure/InlineLogin.srf?id=80604&actionid=7");
            Refresh = new Endpoint("Refresh", 15, "URL_AccountSettings", "https://account." + ServerConfig.Domain + "/");
            RemoteConnect = new Endpoint("RemoteConnect", 16, "RemoteConnect", "https://login." + ServerConfig.Domain + "/RemoteConnectClientAuth.srf");
            $VALUES = new Endpoint[]{Configuration, Sts, DeviceProvision, ManageApprover, ManageLoginKeys, ListSessions, ApproveSession, ConnectMsa, ConnectPartner, SignInMsa, SignInPartner, SignupMsa, SignupPartner, SignupWReplyMsa, SignupWReplyPartner, Refresh, RemoteConnect};
        }

        private Endpoint(String settingName, String defaultValue) {
            this._settingName = settingName;
            try {
                this._defaultValue = new URL(defaultValue);
            } catch (MalformedURLException e) {
                throw new IllegalArgumentException("Default value for ServerConfig.Url with name '" + settingName + "' is not a valid URL.");
            }
        }

        public String getSettingName() {
            return this._settingName;
        }

        public URL getDefaultValue() {
            return this._defaultValue;
        }
    }

    public ServerConfig(Context applicationContext) {
        super(applicationContext, StorageName);
    }

    public Editor edit() {
        return new Editor(this._preferences.edit());
    }

    public int getInt(ISetting<? extends Integer> setting) {
        return super.getInt(setting);
    }

    public String getString(ISetting<? extends String> setting) {
        return super.getString(setting);
    }

    public Set<String> getStringSet(ISetting<? extends Set<String>> setting) {
        return super.getStringSet(setting);
    }

    /* access modifiers changed from: protected */
    public boolean getBoolean(ISetting<? extends Boolean> setting) {
        return super.getBoolean(setting);
    }

    public URL getUrl(Endpoint setting) {
        try {
            String value = this._preferences.getString(setting.getSettingName(), (String) null);
            return value != null ? new URL(value) : setting.getDefaultValue();
        } catch (MalformedURLException ex) {
            throw new IllegalStateException("Stored URL for setting " + setting.getSettingName() + " is invalid.", ex);
        }
    }

    public Environment getEnvironment() {
        return new Environment(getString(EnvironmentName), getUrl(Endpoint.Configuration));
    }

    public Environment getDefaultEnvironment() {
        return new Environment(EnvironmentName.getDefaultValue(), Endpoint.Configuration.getDefaultValue());
    }

    public Integer getNgcCloudPinLength() {
        return Integer.valueOf(getInt(NgcCloudPinLength));
    }

    public static class Editor extends AbstractSettings.Editor {
        private Editor(SharedPreferences.Editor editor) {
            super(editor);
        }

        public Editor clear() {
            super.clear();
            return this;
        }

        public Editor setInt(ISetting<? extends Integer> setting, int value) {
            super.setInt(setting, value);
            return this;
        }

        public Editor setString(ISetting<? extends String> setting, String value) {
            super.setString(setting, value);
            return this;
        }

        public Editor setStringSet(ISetting<? extends Set<String>> setting, Set<String> value) {
            super.setStringSet(setting, value);
            return this;
        }

        public Editor setBoolean(ISetting<? extends Boolean> setting, boolean value) {
            super.setBoolean(setting, value);
            return this;
        }

        public Editor setUrl(Endpoint setting, URL value) {
            this._editor.putString(setting.getSettingName(), value.toExternalForm());
            return this;
        }
    }
}
