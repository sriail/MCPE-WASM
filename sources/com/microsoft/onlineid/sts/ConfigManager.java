package com.microsoft.onlineid.sts;

import android.content.Context;
import android.text.TextUtils;
import android.util.Xml;
import com.microsoft.onlineid.analytics.ClientAnalytics;
import com.microsoft.onlineid.exception.NetworkException;
import com.microsoft.onlineid.internal.configuration.Environment;
import com.microsoft.onlineid.internal.configuration.ISetting;
import com.microsoft.onlineid.internal.log.Logger;
import com.microsoft.onlineid.internal.storage.TypedStorage;
import com.microsoft.onlineid.internal.transport.Transport;
import com.microsoft.onlineid.internal.transport.TransportFactory;
import com.microsoft.onlineid.sts.ServerConfig;
import com.microsoft.onlineid.sts.exception.StsParseException;
import com.microsoft.onlineid.sts.response.parsers.ConfigParser;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class ConfigManager {
    private final Context _applicationContext;
    private ServerConfig _config;
    private TypedStorage _storage;

    public ConfigManager(Context applicationContext) {
        this._applicationContext = applicationContext;
    }

    /* access modifiers changed from: protected */
    public ServerConfig getConfig() {
        if (this._config == null) {
            this._config = new ServerConfig(this._applicationContext);
        }
        return this._config;
    }

    /* access modifiers changed from: protected */
    public TypedStorage getStorage() {
        if (this._storage == null) {
            this._storage = new TypedStorage(this._applicationContext);
        }
        return this._storage;
    }

    /* access modifiers changed from: protected */
    public TransportFactory getTransportFactory() {
        return new TransportFactory(this._applicationContext);
    }

    public boolean switchEnvironment(Environment newEnvironment) {
        if (newEnvironment.equals(getConfig().getEnvironment())) {
            return true;
        }
        return downloadConfiguration(newEnvironment);
    }

    public boolean isClientConfigVersionOlder(String clientConfigVersion) {
        try {
            return compareVersions(clientConfigVersion, getCurrentConfigVersion()) < 0;
        } catch (NumberFormatException ex) {
            Logger.warning("Invalid client version: " + clientConfigVersion, ex);
            return false;
        }
    }

    public boolean hasConfigBeenUpdatedRecently(long configLastDownloadedTime) {
        return (System.currentTimeMillis() - configLastDownloadedTime) / 1000 < ((long) getConfig().getInt(ServerConfig.Int.MinSecondsBetweenConfigDownloads));
    }

    public String getCurrentConfigVersion() {
        return getConfig().getString(ServerConfig.Version);
    }

    public boolean update() {
        return downloadConfiguration(getConfig().getEnvironment());
    }

    public boolean updateIfFirstDownloadNeeded() {
        if (compareVersions(getCurrentConfigVersion(), "1") == 0) {
            return update();
        }
        return true;
    }

    public boolean updateIfNeeded(String desiredVersion) {
        if (hasConfigBeenUpdatedRecently(getStorage().readConfigLastDownloadedTime())) {
            return true;
        }
        String currentVersion = getCurrentConfigVersion();
        Logger.info(String.format(Locale.US, "Checking for PPCRL config update from version \"%s\" to version \"%s\"", new Object[]{currentVersion, desiredVersion}));
        try {
            if (compareVersions(desiredVersion, currentVersion) > 0) {
                return downloadConfiguration(getConfig().getEnvironment());
            }
            return true;
        } catch (NumberFormatException ex) {
            Logger.warning("Invalid server configuration requested: " + desiredVersion, ex);
            return false;
        }
    }

    private boolean downloadConfiguration(Environment environment) {
        boolean result = false;
        Logger.info("Downloading new PPCRL config file (" + environment + ").");
        Transport transport = getTransportFactory().createTransport();
        try {
            transport.openGetRequest(environment.getConfigUrl());
            int responseCode = transport.getResponseCode();
            if (responseCode == 200) {
                result = parseConfig(transport.getResponseStream(), environment);
            } else {
                Logger.error("Failed to update ppcrlconfig due to HTTP response code " + responseCode);
            }
        } catch (NetworkException ex) {
            Logger.error("Failed to update ppcrlconfig.", ex);
            ClientAnalytics.get().logException(ex);
        } catch (IOException ex2) {
            Logger.error("Failed to update ppcrlconfig.", ex2);
            ClientAnalytics.get().logException(ex2);
        } catch (XmlPullParserException ex3) {
            Logger.error("Failed to update ppcrlconfig.", ex3);
            ClientAnalytics.get().logException(ex3);
        } catch (StsParseException ex4) {
            Logger.error("Failed to update ppcrlconfig.", ex4);
            ClientAnalytics.get().logException(ex4);
        } finally {
            transport.closeConnection();
        }
        if (result) {
            Logger.info("Successfully updated ppcrlconfig to version: " + getCurrentConfigVersion());
            getStorage().writeConfigLastDownloadedTime();
        } else {
            Logger.error("Failed to update ppcrlconfig (parseConfig() returned false).");
        }
        return result;
    }

    static long compareVersions(String left, String right) {
        int diff = 0;
        String[] leftTokens = TextUtils.isEmpty(left) ? new String[0] : left.split("\\.");
        String[] rightTokens = TextUtils.isEmpty(right) ? new String[0] : right.split("\\.");
        int index = 0;
        while (true) {
            if (index >= leftTokens.length && index >= rightTokens.length) {
                break;
            }
            int leftValue = 0;
            int rightValue = 0;
            if (index < leftTokens.length) {
                leftValue = Integer.parseInt(leftTokens[index]);
            }
            if (index < rightTokens.length) {
                rightValue = Integer.parseInt(rightTokens[index]);
            }
            diff = leftValue - rightValue;
            if (diff != 0) {
                break;
            }
            index++;
        }
        return (long) diff;
    }

    /* access modifiers changed from: protected */
    public boolean parseConfig(InputStream stream, Environment environment) throws IOException, XmlPullParserException, StsParseException {
        try {
            XmlPullParser rawParser = Xml.newPullParser();
            rawParser.setInput(stream, (String) null);
            Integer cloudPinLength = getConfig().getNgcCloudPinLength();
            ServerConfig.Editor editor = getConfig().edit();
            editor.clear();
            editor.setString((ISetting) ServerConfig.EnvironmentName, environment.getEnvironmentName());
            editor.setUrl(ServerConfig.Endpoint.Configuration, environment.getConfigUrl());
            editor.setInt((ISetting) ServerConfig.NgcCloudPinLength, cloudPinLength.intValue());
            new ConfigParser(rawParser, editor).parse();
            return editor.commit();
        } finally {
            stream.close();
        }
    }
}
