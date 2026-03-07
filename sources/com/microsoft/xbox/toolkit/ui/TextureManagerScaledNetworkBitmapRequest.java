package com.microsoft.xbox.toolkit.ui;

import com.microsoft.xbox.toolkit.XLEFileCacheItemKey;

public class TextureManagerScaledNetworkBitmapRequest implements XLEFileCacheItemKey {
    public final TextureBindingOption bindingOption;
    public final String url;

    public TextureManagerScaledNetworkBitmapRequest(String url2) {
        this(url2, new TextureBindingOption());
    }

    public TextureManagerScaledNetworkBitmapRequest(String url2, TextureBindingOption option) {
        this.url = url2;
        this.bindingOption = option;
    }

    public boolean equals(Object rhsuntyped) {
        if (this == rhsuntyped) {
            return true;
        }
        if (!(rhsuntyped instanceof TextureManagerScaledNetworkBitmapRequest)) {
            return false;
        }
        TextureManagerScaledNetworkBitmapRequest rhs = (TextureManagerScaledNetworkBitmapRequest) rhsuntyped;
        if (!this.url.equals(rhs.url) || !this.bindingOption.equals(rhs.bindingOption)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        if (this.url == null) {
            return 0;
        }
        return this.url.hashCode();
    }

    public String getKeyString() {
        return this.url;
    }
}
