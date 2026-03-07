package com.microsoft.xbox.idp.util;

import android.os.Parcel;
import android.os.Parcelable;

public final class AuthFlowResult implements Parcelable {
    public static final Parcelable.Creator<AuthFlowResult> CREATOR = new Parcelable.Creator<AuthFlowResult>() {
        public AuthFlowResult createFromParcel(Parcel in) {
            return new AuthFlowResult(in);
        }

        public AuthFlowResult[] newArray(int size) {
            return new AuthFlowResult[size];
        }
    };
    private final boolean deleteOnFinalize;
    private final long id;

    private static native void delete(long j);

    private static native String getAgeGroup(long j);

    private static native String getGamerTag(long j);

    private static native String getPrivileges(long j);

    private static native String getRpsTicket(long j);

    private static native String getUserId(long j);

    public AuthFlowResult(long id2) {
        this(id2, false);
    }

    public AuthFlowResult(long id2, boolean deleteOnFinalize2) {
        this.id = id2;
        this.deleteOnFinalize = deleteOnFinalize2;
    }

    protected AuthFlowResult(Parcel in) {
        this.id = in.readLong();
        this.deleteOnFinalize = in.readByte() != 0;
    }

    public String getRpsTicket() {
        return getRpsTicket(this.id);
    }

    public String getUserId() {
        return getUserId(this.id);
    }

    public String getGamerTag() {
        return getGamerTag(this.id);
    }

    public String getAgeGroup() {
        return getAgeGroup(this.id);
    }

    public String getPrivileges() {
        return getPrivileges(this.id);
    }

    /* access modifiers changed from: protected */
    public void finalize() throws Throwable {
        if (this.deleteOnFinalize) {
            delete(this.id);
        }
        super.finalize();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeByte((byte) (this.deleteOnFinalize ? 1 : 0));
    }
}
