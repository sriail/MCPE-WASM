package com.microsoft.onlineid.internal.transport;

import android.content.Context;
import com.microsoft.onlineid.sts.ServerConfig;

public class TransportFactory {
    private final Context _applicationContext;

    public TransportFactory(Context applicationContext) {
        this._applicationContext = applicationContext;
    }

    public Transport createTransport() {
        Transport transport = new Transport();
        configureTransport(transport);
        return transport;
    }

    /* access modifiers changed from: protected */
    public void configureTransport(Transport transport) {
        ServerConfig config = getServerConfig();
        transport.setConnectionTimeoutMilliseconds(config.getInt(ServerConfig.Int.ConnectTimeout));
        transport.setReadTimeoutMilliseconds(config.getInt(ServerConfig.Int.ReceiveTimeout));
        transport.appendCustomUserAgentString(Transport.buildUserAgentString(this._applicationContext));
    }

    /* access modifiers changed from: protected */
    public ServerConfig getServerConfig() {
        return new ServerConfig(this._applicationContext);
    }
}
