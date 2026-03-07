package com.amazon.device.iap.internal.c;

import android.content.Context;
import android.content.SharedPreferences;
import com.amazon.device.iap.internal.util.d;
import com.amazon.device.iap.internal.util.e;

/* compiled from: EntitlementTracker */
public class c {
    private static c a = new c();
    private static final String b = c.class.getSimpleName();
    private static final String c = (c.class.getName() + "_PREFS_");

    public static c a() {
        return a;
    }

    public void a(String str, String str2, String str3) {
        e.a(b, "enter saveEntitlementRecord for v1 Entitlement [" + str2 + "/" + str3 + "], user [" + str + "]");
        try {
            d.a(str, "userId");
            d.a(str2, "receiptId");
            d.a(str3, "sku");
            Context b2 = com.amazon.device.iap.internal.d.d().b();
            d.a((Object) b2, "context");
            SharedPreferences.Editor edit = b2.getSharedPreferences(c + str, 0).edit();
            edit.putString(str3, str2);
            edit.commit();
        } catch (Throwable th) {
            e.a(b, "error in saving v1 Entitlement:" + str2 + "/" + str3 + ":" + th.getMessage());
        }
        e.a(b, "leaving saveEntitlementRecord for v1 Entitlement [" + str2 + "/" + str3 + "], user [" + str + "]");
    }

    public String a(String str, String str2) {
        String str3 = null;
        e.a(b, "enter getReceiptIdFromSku for sku [" + str2 + "], user [" + str + "]");
        try {
            d.a(str, "userId");
            d.a(str2, "sku");
            Context b2 = com.amazon.device.iap.internal.d.d().b();
            d.a((Object) b2, "context");
            str3 = b2.getSharedPreferences(c + str, 0).getString(str2, (String) null);
        } catch (Throwable th) {
            e.a(b, "error in saving v1 Entitlement:" + str2 + ":" + th.getMessage());
        }
        e.a(b, "leaving saveEntitlementRecord for sku [" + str2 + "], user [" + str + "]");
        return str3;
    }
}
