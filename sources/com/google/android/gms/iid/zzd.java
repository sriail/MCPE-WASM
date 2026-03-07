package com.google.android.gms.iid;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import java.io.File;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class zzd {
    Context context;
    SharedPreferences zzaNt;

    public zzd(Context context2) {
        this(context2, "com.google.android.gms.appid");
    }

    public zzd(Context context2, String str) {
        this.context = context2;
        this.zzaNt = context2.getSharedPreferences(str, 4);
        zzeb(str + "-no-backup");
    }

    private void zzeb(String str) {
        File file = new File(new ContextCompat().getNoBackupFilesDir(this.context), str);
        if (!file.exists()) {
            try {
                if (file.createNewFile() && !isEmpty()) {
                    Log.i("InstanceID/Store", "App restored, clearing state");
                    InstanceIDListenerService.zza(this.context, this);
                }
            } catch (IOException e) {
                if (Log.isLoggable("InstanceID/Store", 3)) {
                    Log.d("InstanceID/Store", "Error creating file in no backup dir: " + e.getMessage());
                }
            }
        }
    }

    private String zzh(String str, String str2, String str3) {
        return str + "|T|" + str2 + "|" + str3;
    }

    /* access modifiers changed from: package-private */
    public synchronized String get(String key) {
        return this.zzaNt.getString(key, (String) null);
    }

    /* access modifiers changed from: package-private */
    public synchronized String get(String subtype, String key) {
        return this.zzaNt.getString(subtype + "|S|" + key, (String) null);
    }

    /* access modifiers changed from: package-private */
    public boolean isEmpty() {
        return this.zzaNt.getAll().isEmpty();
    }

    /* access modifiers changed from: package-private */
    public synchronized void zza(SharedPreferences.Editor editor, String str, String str2, String str3) {
        editor.putString(str + "|S|" + str2, str3);
    }

    public synchronized void zza(String str, String str2, String str3, String str4, String str5) {
        String zzh = zzh(str, str2, str3);
        SharedPreferences.Editor edit = this.zzaNt.edit();
        edit.putString(zzh, str4);
        edit.putString("appVersion", str5);
        edit.putString("lastToken", Long.toString(System.currentTimeMillis() / 1000));
        edit.commit();
    }

    /* access modifiers changed from: package-private */
    public synchronized KeyPair zzd(String str, long j) {
        KeyPair zzyy;
        zzyy = zza.zzyy();
        SharedPreferences.Editor edit = this.zzaNt.edit();
        zza(edit, str, "|P|", InstanceID.zzn(zzyy.getPublic().getEncoded()));
        zza(edit, str, "|K|", InstanceID.zzn(zzyy.getPrivate().getEncoded()));
        zza(edit, str, "cre", Long.toString(j));
        edit.commit();
        return zzyy;
    }

    public synchronized void zzec(String str) {
        SharedPreferences.Editor edit = this.zzaNt.edit();
        for (String next : this.zzaNt.getAll().keySet()) {
            if (next.startsWith(str)) {
                edit.remove(next);
            }
        }
        edit.commit();
    }

    public KeyPair zzed(String str) {
        return zzeg(str);
    }

    /* access modifiers changed from: package-private */
    public void zzee(String str) {
        zzec(str + "|");
    }

    public void zzef(String str) {
        zzec(str + "|T|");
    }

    /* access modifiers changed from: package-private */
    public KeyPair zzeg(String str) {
        String str2 = get(str, "|P|");
        String str3 = get(str, "|K|");
        if (str3 == null) {
            return null;
        }
        try {
            byte[] decode = Base64.decode(str2, 8);
            byte[] decode2 = Base64.decode(str3, 8);
            KeyFactory instance = KeyFactory.getInstance("RSA");
            return new KeyPair(instance.generatePublic(new X509EncodedKeySpec(decode)), instance.generatePrivate(new PKCS8EncodedKeySpec(decode2)));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            Log.w("InstanceID/Store", "Invalid key stored " + e);
            InstanceIDListenerService.zza(this.context, this);
            return null;
        }
    }

    public synchronized String zzi(String str, String str2, String str3) {
        return this.zzaNt.getString(zzh(str, str2, str3), (String) null);
    }

    public synchronized void zzj(String str, String str2, String str3) {
        String zzh = zzh(str, str2, str3);
        SharedPreferences.Editor edit = this.zzaNt.edit();
        edit.remove(zzh);
        edit.commit();
    }

    public synchronized void zzyG() {
        this.zzaNt.edit().clear().commit();
    }
}
