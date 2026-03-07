package com.microsoft.xbox.authenticate;

import android.app.PendingIntent;
import android.os.Parcel;
import android.os.Parcelable;

public class DelegateRPSTicketResult implements Parcelable {
    public static final Parcelable.Creator<DelegateRPSTicketResult> CREATOR = new Parcelable.Creator<DelegateRPSTicketResult>() {
        public DelegateRPSTicketResult createFromParcel(Parcel in) {
            return new DelegateRPSTicketResult(in);
        }

        public DelegateRPSTicketResult[] newArray(int size) {
            return new DelegateRPSTicketResult[size];
        }
    };
    public static int RESULT_NOCID = 1;
    public static int RESULT_SUCCESS = 0;
    public static int RESULT_UNEXPECTED = 2;
    private int errorCode;
    private PendingIntent pendingIntent;
    private String ticket;

    private DelegateRPSTicketResult(Parcel in) {
        readFromParcel(in);
    }

    public String getTicket() {
        return this.ticket;
    }

    public int getErrorCode() {
        return this.errorCode;
    }

    public PendingIntent getPendingIntent() {
        return this.pendingIntent;
    }

    public void readFromParcel(Parcel in) {
        this.errorCode = in.readInt();
        this.ticket = in.readString();
        this.pendingIntent = PendingIntent.readPendingIntentOrNullFromParcel(in);
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.errorCode);
        dest.writeString(this.ticket);
        PendingIntent.writePendingIntentOrNullToParcel(this.pendingIntent, dest);
    }
}
