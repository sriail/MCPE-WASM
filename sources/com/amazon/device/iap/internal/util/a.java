package com.amazon.device.iap.internal.util;

import com.amazon.android.Kiwi;
import com.amazon.device.iap.PurchasingService;
import com.amazon.device.iap.internal.b.d;
import com.amazon.device.iap.internal.model.ReceiptBuilder;
import com.amazon.device.iap.model.ProductType;
import com.amazon.device.iap.model.Receipt;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.json.JSONException;
import org.json.JSONObject;

/* compiled from: ReceiptHelper */
public class a {
    private static final String a = a.class.getSimpleName();

    public static Receipt a(JSONObject jSONObject, String str, String str2) throws com.amazon.device.iap.internal.b.a, d, IllegalArgumentException {
        switch (b(jSONObject)) {
            case V1:
                return c(jSONObject, str, str2);
            case LEGACY:
                return b(jSONObject, str, str2);
            default:
                return d(jSONObject, str, str2);
        }
    }

    private static Receipt b(JSONObject jSONObject, String str, String str2) throws com.amazon.device.iap.internal.b.a, d {
        String optString = jSONObject.optString("signature");
        if (d.a(optString)) {
            e.b(a, "a signature was not found in the receipt for request ID " + str2);
            MetricsHelper.submitReceiptVerificationFailureMetrics(str2, "NO Signature found", optString);
            throw new d(str2, (String) null, optString);
        }
        try {
            Receipt a2 = a(jSONObject);
            String str3 = str + "-" + a2.getReceiptId();
            boolean isSignedByKiwi = Kiwi.isSignedByKiwi(str3, optString);
            e.a(a, "stringToVerify/legacy:\n" + str3 + "\nsignature:\n" + optString);
            if (isSignedByKiwi) {
                return a2;
            }
            MetricsHelper.submitReceiptVerificationFailureMetrics(str2, str3, optString);
            throw new d(str2, str3, optString);
        } catch (JSONException e) {
            throw new com.amazon.device.iap.internal.b.a(str2, jSONObject.toString(), e);
        }
    }

    private static Receipt a(JSONObject jSONObject) throws JSONException {
        Date date = null;
        String optString = jSONObject.optString("token");
        String string = jSONObject.getString("sku");
        ProductType valueOf = ProductType.valueOf(jSONObject.getString("itemType").toUpperCase());
        String optString2 = jSONObject.optString("startDate");
        Date b = a(optString2) ? null : b(optString2);
        String optString3 = jSONObject.optString("endDate");
        if (!a(optString3)) {
            date = b(optString3);
        }
        return new ReceiptBuilder().setReceiptId(optString).setSku(string).setProductType(valueOf).setPurchaseDate(b).setCancelDate(date).build();
    }

    private static Receipt c(JSONObject jSONObject, String str, String str2) throws com.amazon.device.iap.internal.b.a, d {
        Date date;
        Date date2 = null;
        String optString = jSONObject.optString("DeviceId");
        String optString2 = jSONObject.optString("signature");
        if (d.a(optString2)) {
            e.b(a, "a signature was not found in the receipt for request ID " + str2);
            MetricsHelper.submitReceiptVerificationFailureMetrics(str2, "NO Signature found", optString2);
            throw new d(str2, (String) null, optString2);
        }
        try {
            Receipt a2 = a(jSONObject);
            Object[] objArr = new Object[9];
            objArr[0] = PurchasingService.SDK_VERSION;
            objArr[1] = str;
            objArr[2] = optString;
            objArr[3] = a2.getProductType();
            objArr[4] = a2.getSku();
            objArr[5] = a2.getReceiptId();
            objArr[6] = str2;
            if (ProductType.SUBSCRIPTION == a2.getProductType()) {
                date = a2.getPurchaseDate();
            } else {
                date = null;
            }
            objArr[7] = date;
            if (ProductType.SUBSCRIPTION == a2.getProductType()) {
                date2 = a2.getCancelDate();
            }
            objArr[8] = date2;
            String format = String.format("%s|%s|%s|%s|%s|%s|%s|%tQ|%tQ", objArr);
            e.a(a, "stringToVerify/v1:\n" + format + "\nsignature:\n" + optString2);
            if (Kiwi.isSignedByKiwi(format, optString2)) {
                return a2;
            }
            MetricsHelper.submitReceiptVerificationFailureMetrics(str2, format, optString2);
            throw new d(str2, format, optString2);
        } catch (JSONException e) {
            throw new com.amazon.device.iap.internal.b.a(str2, jSONObject.toString(), e);
        }
    }

    private static Receipt d(JSONObject jSONObject, String str, String str2) throws com.amazon.device.iap.internal.b.a, d {
        Date date = null;
        String optString = jSONObject.optString("DeviceId");
        String optString2 = jSONObject.optString("signature");
        if (d.a(optString2)) {
            e.b(a, "a signature was not found in the receipt for request ID " + str2);
            MetricsHelper.submitReceiptVerificationFailureMetrics(str2, "NO Signature found", optString2);
            throw new d(str2, (String) null, optString2);
        }
        try {
            String string = jSONObject.getString("receiptId");
            String string2 = jSONObject.getString("sku");
            ProductType valueOf = ProductType.valueOf(jSONObject.getString("itemType").toUpperCase());
            String optString3 = jSONObject.optString("purchaseDate");
            Date b = a(optString3) ? null : b(optString3);
            String optString4 = jSONObject.optString("cancelDate");
            if (!a(optString4)) {
                date = b(optString4);
            }
            Receipt build = new ReceiptBuilder().setReceiptId(string).setSku(string2).setProductType(valueOf).setPurchaseDate(b).setCancelDate(date).build();
            String format = String.format("%s|%s|%s|%s|%s|%tQ|%tQ", new Object[]{str, optString, build.getProductType(), build.getSku(), build.getReceiptId(), build.getPurchaseDate(), build.getCancelDate()});
            e.a(a, "stringToVerify/v2:\n" + format + "\nsignature:\n" + optString2);
            if (Kiwi.isSignedByKiwi(format, optString2)) {
                return build;
            }
            MetricsHelper.submitReceiptVerificationFailureMetrics(str2, format, optString2);
            throw new d(str2, format, optString2);
        } catch (JSONException e) {
            throw new com.amazon.device.iap.internal.b.a(str2, jSONObject.toString(), e);
        }
    }

    protected static boolean a(String str) {
        return str == null || str.trim().length() == 0;
    }

    protected static Date b(String str) throws JSONException {
        try {
            Date parse = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").parse(str);
            if (0 == parse.getTime()) {
                return null;
            }
            return parse;
        } catch (ParseException e) {
            throw new JSONException(e.getMessage());
        }
    }

    private static c b(JSONObject jSONObject) {
        String optString = jSONObject.optString("DeviceId");
        if (!d.a(jSONObject.optString("receiptId"))) {
            return c.V2;
        }
        if (d.a(optString)) {
            return c.LEGACY;
        }
        return c.V1;
    }
}
