package com.microsoft.onlineid;

public class OnlineIdConfiguration {
    private String _cobrandingId;
    private final PreferredSignUpMemberNameType _preferredSignUpMemberNameType;
    private boolean _requestWebTelemetry;

    public enum PreferredSignUpMemberNameType {
        None((String) null),
        Email("easi2"),
        Outlook("wld2"),
        Telephone("phone2"),
        TelephoneOnly("phone"),
        TelephoneEvenIfBlank("phone3");
        
        private final String _qsValue;

        private PreferredSignUpMemberNameType(String qsValue) {
            this._qsValue = qsValue;
        }

        public String toString() {
            return this._qsValue;
        }
    }

    public OnlineIdConfiguration() {
        this(PreferredSignUpMemberNameType.None);
    }

    public OnlineIdConfiguration(PreferredSignUpMemberNameType preferredSignUpMemberNameType) {
        this._preferredSignUpMemberNameType = preferredSignUpMemberNameType;
        this._requestWebTelemetry = false;
    }

    public PreferredSignUpMemberNameType getPreferredSignUpMemberNameType() {
        return this._preferredSignUpMemberNameType;
    }

    public String getCobrandingId() {
        return this._cobrandingId;
    }

    public OnlineIdConfiguration setCobrandingId(String cobrandingId) {
        this._cobrandingId = cobrandingId;
        return this;
    }

    public OnlineIdConfiguration setShouldGatherWebTelemetry(boolean requestWebTelemetry) {
        this._requestWebTelemetry = requestWebTelemetry;
        return this;
    }

    public boolean getShouldGatherWebTelemetry() {
        return this._requestWebTelemetry;
    }
}
