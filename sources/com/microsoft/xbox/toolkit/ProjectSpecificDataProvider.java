package com.microsoft.xbox.toolkit;

public class ProjectSpecificDataProvider implements IProjectSpecificDataProvider {
    private static ProjectSpecificDataProvider instance = new ProjectSpecificDataProvider();
    private IProjectSpecificDataProvider provider;

    public static ProjectSpecificDataProvider getInstance() {
        return instance;
    }

    public void setProvider(IProjectSpecificDataProvider provider2) {
        this.provider = provider2;
    }

    public String getLegalLocale() {
        checkProvider();
        if (this.provider != null) {
            return this.provider.getLegalLocale();
        }
        return null;
    }

    public String getCombinedContentRating() {
        checkProvider();
        if (this.provider != null) {
            return this.provider.getCombinedContentRating();
        }
        return null;
    }

    public String getMembershipLevel() {
        checkProvider();
        if (this.provider != null) {
            return this.provider.getMembershipLevel();
        }
        return null;
    }

    public String getXuidString() {
        checkProvider();
        if (this.provider != null) {
            return this.provider.getXuidString();
        }
        return null;
    }

    public void setXuidString(String xuid) {
        checkProvider();
        if (this.provider != null) {
            this.provider.setXuidString(xuid);
        }
    }

    public String getSCDRpsTicket() {
        checkProvider();
        if (this.provider != null) {
            return this.provider.getSCDRpsTicket();
        }
        return null;
    }

    public void setSCDRpsTicket(String rpsTicket) {
        checkProvider();
        if (this.provider != null) {
            this.provider.setSCDRpsTicket(rpsTicket);
        }
    }

    public String getPrivileges() {
        checkProvider();
        if (this.provider != null) {
            return this.provider.getPrivileges();
        }
        return "";
    }

    public void setPrivileges(String privileges) {
        checkProvider();
        if (this.provider != null) {
            this.provider.setPrivileges(privileges);
        }
    }

    public boolean getAllowExplicitContent() {
        checkProvider();
        if (this.provider != null) {
            return this.provider.getAllowExplicitContent();
        }
        return false;
    }

    public String getAutoSuggestdDataSource() {
        checkProvider();
        if (this.provider != null) {
            return this.provider.getAutoSuggestdDataSource();
        }
        return null;
    }

    public boolean getInitializeComplete() {
        checkProvider();
        if (this.provider != null) {
            return this.provider.getInitializeComplete();
        }
        return false;
    }

    public boolean getIsFreeAccount() {
        checkProvider();
        if (this.provider != null) {
            return this.provider.getIsFreeAccount();
        }
        return true;
    }

    private void checkProvider() {
    }

    public boolean getIsXboxMusicSupported() {
        checkProvider();
        if (this.provider != null) {
            return this.provider.getIsXboxMusicSupported();
        }
        return false;
    }

    public String getWindowsLiveClientId() {
        checkProvider();
        if (this.provider != null) {
            return this.provider.getWindowsLiveClientId();
        }
        return null;
    }

    public String getVersionCheckUrl() {
        checkProvider();
        if (this.provider != null) {
            return this.provider.getVersionCheckUrl();
        }
        return null;
    }

    public void resetModels(boolean clearEverything) {
        checkProvider();
        if (this.provider != null) {
            this.provider.resetModels(clearEverything);
        }
    }

    public boolean getIsForXboxOne() {
        checkProvider();
        if (this.provider != null) {
            return this.provider.getIsForXboxOne();
        }
        return false;
    }

    public String getCurrentSandboxID() {
        checkProvider();
        if (this.provider != null) {
            return this.provider.getCurrentSandboxID();
        }
        return null;
    }

    public boolean isDeviceLocaleKnown() {
        checkProvider();
        if (this.provider != null) {
            return this.provider.isDeviceLocaleKnown();
        }
        return true;
    }

    public String getConnectedLocale() {
        checkProvider();
        if (this.provider != null) {
            return this.provider.getConnectedLocale();
        }
        return null;
    }

    public String getConnectedLocale(boolean fromEdsCall) {
        checkProvider();
        if (this.provider != null) {
            return this.provider.getConnectedLocale(fromEdsCall);
        }
        return null;
    }

    public int getVersionCode() {
        checkProvider();
        if (this.provider != null) {
            return this.provider.getVersionCode();
        }
        return 0;
    }

    public String getContentRestrictions() {
        checkProvider();
        if (this.provider != null) {
            return this.provider.getContentRestrictions();
        }
        return null;
    }

    public String getRegion() {
        checkProvider();
        if (this.provider != null) {
            return this.provider.getRegion();
        }
        return null;
    }
}
