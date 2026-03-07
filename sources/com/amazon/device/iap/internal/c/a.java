package com.amazon.device.iap.internal.c;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import com.amazon.device.iap.internal.util.d;
import com.amazon.device.iap.internal.util.e;
import com.amazon.device.iap.model.Receipt;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.json.JSONException;
import org.json.JSONObject;

/* compiled from: PendingReceiptsManager */
public class a {
    /* access modifiers changed from: private */
    public static final String a = a.class.getSimpleName();
    /* access modifiers changed from: private */
    public static final String b = (a.class.getName() + "_PREFS");
    private static final String c = (a.class.getName() + "_CLEANER_PREFS");
    /* access modifiers changed from: private */
    public static int d = 604800000;
    private static final a e = new a();

    public void a(String str, String str2, String str3, String str4) {
        e.a(a, "enter saveReceipt for receipt [" + str4 + "]");
        try {
            d.a(str2, "userId");
            d.a(str3, "receiptId");
            d.a(str4, "receiptString");
            Context b2 = com.amazon.device.iap.internal.d.d().b();
            d.a((Object) b2, "context");
            d dVar = new d(str2, str4, str, System.currentTimeMillis());
            SharedPreferences.Editor edit = b2.getSharedPreferences(b, 0).edit();
            edit.putString(str3, dVar.d());
            edit.commit();
        } catch (Throwable th) {
            e.a(a, "error in saving pending receipt:" + str + "/" + str4 + ":" + th.getMessage());
        }
        e.a(a, "leaving saveReceipt for receipt id [" + str3 + "]");
    }

    private void e() {
        e.a(a, "enter old receipts cleanup! ");
        final Context b2 = com.amazon.device.iap.internal.d.d().b();
        d.a((Object) b2, "context");
        a(System.currentTimeMillis());
        new Handler().post(new Runnable() {
            public void run() {
                String next;
                try {
                    e.a(a.a, "perform house keeping! ");
                    SharedPreferences sharedPreferences = b2.getSharedPreferences(a.b, 0);
                    Iterator<String> it = sharedPreferences.getAll().keySet().iterator();
                    while (it.hasNext()) {
                        next = it.next();
                        if (System.currentTimeMillis() - d.a(sharedPreferences.getString(next, (String) null)).c() > ((long) a.d)) {
                            e.a(a.a, "house keeping - try remove Receipt:" + next + " since it's too old");
                            a.this.a(next);
                        }
                    }
                } catch (e e) {
                    e.a(a.a, "house keeping - try remove Receipt:" + next + " since it's invalid ");
                    a.this.a(next);
                } catch (Throwable th) {
                    e.a(a.a, "Error in running cleaning job:" + th);
                }
            }
        });
    }

    public void a(String str) {
        e.a(a, "enter removeReceipt for receipt[" + str + "]");
        Context b2 = com.amazon.device.iap.internal.d.d().b();
        d.a((Object) b2, "context");
        SharedPreferences.Editor edit = b2.getSharedPreferences(b, 0).edit();
        edit.remove(str);
        edit.commit();
        e.a(a, "leave removeReceipt for receipt[" + str + "]");
    }

    private long f() {
        Context b2 = com.amazon.device.iap.internal.d.d().b();
        d.a((Object) b2, "context");
        long currentTimeMillis = System.currentTimeMillis();
        long j = b2.getSharedPreferences(c, 0).getLong("LAST_CLEANING_TIME", 0);
        if (j != 0) {
            return j;
        }
        a(currentTimeMillis);
        return currentTimeMillis;
    }

    private void a(long j) {
        Context b2 = com.amazon.device.iap.internal.d.d().b();
        d.a((Object) b2, "context");
        SharedPreferences.Editor edit = b2.getSharedPreferences(c, 0).edit();
        edit.putLong("LAST_CLEANING_TIME", j);
        edit.commit();
    }

    public Set<Receipt> b(String str) {
        Context b2 = com.amazon.device.iap.internal.d.d().b();
        d.a((Object) b2, "context");
        e.a(a, "enter getLocalReceipts for user[" + str + "]");
        HashSet hashSet = new HashSet();
        if (d.a(str)) {
            e.b(a, "empty UserId: " + str);
            throw new RuntimeException("Invalid UserId:" + str);
        }
        Map<String, ?> all = b2.getSharedPreferences(b, 0).getAll();
        for (String next : all.keySet()) {
            String str2 = (String) all.get(next);
            try {
                d a2 = d.a(str2);
                hashSet.add(com.amazon.device.iap.internal.util.a.a(new JSONObject(a2.b()), str, a2.a()));
            } catch (com.amazon.device.iap.internal.b.d e2) {
                a(next);
                e.b(a, "failed to verify signature:[" + str2 + "]");
            } catch (JSONException e3) {
                a(next);
                e.b(a, "failed to convert string to JSON object:[" + str2 + "]");
            } catch (Throwable th) {
                e.b(a, "failed to load the receipt from SharedPreference:[" + str2 + "]");
            }
        }
        e.a(a, "leaving getLocalReceipts for user[" + str + "], " + hashSet.size() + " local receipts found.");
        if (System.currentTimeMillis() - f() > ((long) d)) {
            e();
        }
        return hashSet;
    }

    public static a a() {
        return e;
    }

    public String c(String str) {
        Context b2 = com.amazon.device.iap.internal.d.d().b();
        d.a((Object) b2, "context");
        if (d.a(str)) {
            e.b(a, "empty receiptId: " + str);
            throw new RuntimeException("Invalid ReceiptId:" + str);
        }
        String string = b2.getSharedPreferences(b, 0).getString(str, (String) null);
        if (string == null) {
            return null;
        }
        try {
            return d.a(string).a();
        } catch (e e2) {
            return null;
        }
    }
}
