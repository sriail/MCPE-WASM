package com.microsoft.xbox.toolkit.ui;

import java.net.URI;

public class XLEURIArg {
    private final int errorResourceId;
    private final int loadingResourceId;
    private final URI uri;

    public XLEURIArg(URI uri2, int loadingResourceId2, int errorResourceId2) {
        this.uri = uri2;
        this.loadingResourceId = loadingResourceId2;
        this.errorResourceId = errorResourceId2;
    }

    public XLEURIArg(URI uri2) {
        this(uri2, -1, -1);
    }

    public URI getUri() {
        return this.uri;
    }

    public int getLoadingResourceId() {
        return this.loadingResourceId;
    }

    public int getErrorResourceId() {
        return this.errorResourceId;
    }

    public TextureBindingOption getTextureBindingOption() {
        return new TextureBindingOption(-1, -1, this.loadingResourceId, this.errorResourceId, false);
    }

    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (!(o instanceof XLEURIArg)) {
            return false;
        }
        XLEURIArg other = (XLEURIArg) o;
        if (this.loadingResourceId != other.loadingResourceId || this.errorResourceId != other.errorResourceId) {
            return false;
        }
        if (this.uri == other.uri || (this.uri != null && this.uri.equals(other.uri))) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        int hash = ((this.loadingResourceId + 13) * 17) + this.errorResourceId;
        if (this.uri != null) {
            return (hash * 23) + this.uri.hashCode();
        }
        return hash;
    }
}
