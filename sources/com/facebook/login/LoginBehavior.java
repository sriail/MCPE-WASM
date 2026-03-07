package com.facebook.login;

public enum LoginBehavior {
    NATIVE_WITH_FALLBACK(true, true, false),
    NATIVE_ONLY(true, false, false),
    WEB_ONLY(false, true, false),
    DEVICE_AUTH(false, false, true);
    
    private final boolean allowsDeviceAuth;
    private final boolean allowsKatanaAuth;
    private final boolean allowsWebViewAuth;

    private LoginBehavior(boolean allowsKatanaAuth2, boolean allowsWebViewAuth2, boolean allowsDeviceAuth2) {
        this.allowsKatanaAuth = allowsKatanaAuth2;
        this.allowsWebViewAuth = allowsWebViewAuth2;
        this.allowsDeviceAuth = allowsDeviceAuth2;
    }

    /* access modifiers changed from: package-private */
    public boolean allowsKatanaAuth() {
        return this.allowsKatanaAuth;
    }

    /* access modifiers changed from: package-private */
    public boolean allowsWebViewAuth() {
        return this.allowsWebViewAuth;
    }

    /* access modifiers changed from: package-private */
    public boolean allowsDeviceAuth() {
        return this.allowsDeviceAuth;
    }
}
