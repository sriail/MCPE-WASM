package com.google.android.gms.iid;

import android.annotation.TargetApi;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import com.google.android.gms.iid.zzb;

public class MessengerCompat implements Parcelable {
    public static final Parcelable.Creator<MessengerCompat> CREATOR = new Parcelable.Creator<MessengerCompat>() {
        /* renamed from: zzeO */
        public MessengerCompat createFromParcel(Parcel parcel) {
            IBinder readStrongBinder = parcel.readStrongBinder();
            if (readStrongBinder != null) {
                return new MessengerCompat(readStrongBinder);
            }
            return null;
        }

        /* renamed from: zzhm */
        public MessengerCompat[] newArray(int i) {
            return new MessengerCompat[i];
        }
    };
    Messenger zzaNd;
    zzb zzaNe;

    private final class zza extends zzb.zza {
        Handler handler;

        zza(Handler handler2) {
            this.handler = handler2;
        }

        public void send(Message msg) throws RemoteException {
            msg.arg2 = Binder.getCallingUid();
            this.handler.dispatchMessage(msg);
        }
    }

    public MessengerCompat(Handler handler) {
        if (Build.VERSION.SDK_INT >= 21) {
            this.zzaNd = new Messenger(handler);
        } else {
            this.zzaNe = new zza(handler);
        }
    }

    public MessengerCompat(IBinder target) {
        if (Build.VERSION.SDK_INT >= 21) {
            this.zzaNd = new Messenger(target);
        } else {
            this.zzaNe = zzb.zza.zzcd(target);
        }
    }

    public static int zzc(Message message) {
        return Build.VERSION.SDK_INT >= 21 ? zzd(message) : message.arg2;
    }

    @TargetApi(21)
    private static int zzd(Message message) {
        return message.sendingUid;
    }

    public int describeContents() {
        return 0;
    }

    public boolean equals(Object otherObj) {
        if (otherObj == null) {
            return false;
        }
        try {
            return getBinder().equals(((MessengerCompat) otherObj).getBinder());
        } catch (ClassCastException e) {
            return false;
        }
    }

    public IBinder getBinder() {
        return this.zzaNd != null ? this.zzaNd.getBinder() : this.zzaNe.asBinder();
    }

    public int hashCode() {
        return getBinder().hashCode();
    }

    public void send(Message message) throws RemoteException {
        if (this.zzaNd != null) {
            this.zzaNd.send(message);
        } else {
            this.zzaNe.send(message);
        }
    }

    public void writeToParcel(Parcel out, int flags) {
        if (this.zzaNd != null) {
            out.writeStrongBinder(this.zzaNd.getBinder());
        } else {
            out.writeStrongBinder(this.zzaNe.asBinder());
        }
    }
}
