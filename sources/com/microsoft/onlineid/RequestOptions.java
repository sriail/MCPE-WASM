package com.microsoft.onlineid;

import android.os.Bundle;
import com.microsoft.onlineid.RequestOptions;
import com.microsoft.onlineid.internal.Objects;
import com.microsoft.onlineid.internal.sso.BundleMarshaller;

public abstract class RequestOptions<B extends RequestOptions<B>> {
    protected final Bundle _values;

    protected RequestOptions() {
        this(new Bundle());
    }

    protected RequestOptions(Bundle bundle) {
        Objects.verifyArgumentNotNull(bundle, "bundle");
        this._values = bundle;
    }

    public Bundle asBundle() {
        return this._values;
    }

    public String getPrefillUsername() {
        return this._values.getString(BundleMarshaller.PrefillUsernameKey);
    }

    public B setPrefillUsername(String username) {
        this._values.putString(BundleMarshaller.PrefillUsernameKey, username);
        return this;
    }

    public B setUnauthenticatedSessionId(String uaid) {
        this._values.putString(BundleMarshaller.UnauthenticatedSessionIdKey, uaid);
        return this;
    }

    public String getUnauthenticatedSessionId() {
        return this._values.getString(BundleMarshaller.UnauthenticatedSessionIdKey);
    }

    public B setFlightConfiguration(String flightConfiguration) {
        this._values.putString(BundleMarshaller.ClientFlightsKey, flightConfiguration);
        return this;
    }

    public String getFlightConfiguration() {
        return this._values.getString(BundleMarshaller.ClientFlightsKey);
    }

    public B setWasPrecachingEnabled(boolean precachingEnabled) {
        this._values.putBoolean(BundleMarshaller.WebFlowTelemetryPrecachingEnabledKey, precachingEnabled);
        return this;
    }

    public boolean getWasPrecachingEnabled() {
        return this._values.getBoolean(BundleMarshaller.WebFlowTelemetryPrecachingEnabledKey, false);
    }
}
