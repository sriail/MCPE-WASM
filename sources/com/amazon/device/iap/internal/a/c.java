package com.amazon.device.iap.internal.a;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import com.amazon.device.iap.PurchasingListener;
import com.amazon.device.iap.PurchasingService;
import com.amazon.device.iap.internal.d;
import com.amazon.device.iap.internal.model.ProductBuilder;
import com.amazon.device.iap.internal.model.ProductDataResponseBuilder;
import com.amazon.device.iap.internal.model.PurchaseResponseBuilder;
import com.amazon.device.iap.internal.model.PurchaseUpdatesResponseBuilder;
import com.amazon.device.iap.internal.model.ReceiptBuilder;
import com.amazon.device.iap.internal.model.UserDataBuilder;
import com.amazon.device.iap.internal.model.UserDataResponseBuilder;
import com.amazon.device.iap.internal.util.b;
import com.amazon.device.iap.internal.util.e;
import com.amazon.device.iap.model.FulfillmentResult;
import com.amazon.device.iap.model.Product;
import com.amazon.device.iap.model.ProductDataResponse;
import com.amazon.device.iap.model.ProductType;
import com.amazon.device.iap.model.PurchaseResponse;
import com.amazon.device.iap.model.PurchaseUpdatesResponse;
import com.amazon.device.iap.model.Receipt;
import com.amazon.device.iap.model.RequestId;
import com.amazon.device.iap.model.UserData;
import com.amazon.device.iap.model.UserDataResponse;
import com.facebook.share.internal.ShareConstants;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* compiled from: SandboxRequestHandler */
public final class c implements com.amazon.device.iap.internal.c {
    /* access modifiers changed from: private */
    public static final String a = c.class.getSimpleName();

    public void a(RequestId requestId) {
        e.a(a, "sendGetUserDataRequest");
        a(requestId.toString(), false, false);
    }

    private void a(String str, boolean z, boolean z2) {
        try {
            Context b = d.d().b();
            Bundle bundle = new Bundle();
            JSONObject jSONObject = new JSONObject();
            jSONObject.put("requestId", str);
            jSONObject.put("packageName", b.getPackageName());
            jSONObject.put("sdkVersion", PurchasingService.SDK_VERSION);
            jSONObject.put("isPurchaseUpdates", z);
            jSONObject.put("reset", z2);
            bundle.putString("userInput", jSONObject.toString());
            Intent a2 = a("com.amazon.testclient.iap.appUserId");
            a2.addFlags(268435456);
            a2.putExtras(bundle);
            b.startService(a2);
        } catch (JSONException e) {
            e.b(a, "Error in sendGetUserDataRequest.");
        }
    }

    public void a(RequestId requestId, String str) {
        e.a(a, "sendPurchaseRequest");
        try {
            Context b = d.d().b();
            Bundle bundle = new Bundle();
            JSONObject jSONObject = new JSONObject();
            jSONObject.put("sku", str);
            jSONObject.put("requestId", requestId.toString());
            jSONObject.put("packageName", b.getPackageName());
            jSONObject.put("sdkVersion", PurchasingService.SDK_VERSION);
            bundle.putString("purchaseInput", jSONObject.toString());
            Intent a2 = a("com.amazon.testclient.iap.purchase");
            a2.addFlags(268435456);
            a2.putExtras(bundle);
            b.startService(a2);
        } catch (JSONException e) {
            e.b(a, "Error in sendPurchaseRequest.");
        }
    }

    public void a(RequestId requestId, Set<String> set) {
        e.a(a, "sendItemDataRequest");
        try {
            Context b = d.d().b();
            Bundle bundle = new Bundle();
            JSONObject jSONObject = new JSONObject();
            JSONArray jSONArray = new JSONArray(set);
            jSONObject.put("requestId", requestId.toString());
            jSONObject.put("packageName", b.getPackageName());
            jSONObject.put("skus", jSONArray);
            jSONObject.put("sdkVersion", PurchasingService.SDK_VERSION);
            bundle.putString("itemDataInput", jSONObject.toString());
            Intent a2 = a("com.amazon.testclient.iap.itemData");
            a2.addFlags(268435456);
            a2.putExtras(bundle);
            b.startService(a2);
        } catch (JSONException e) {
            e.b(a, "Error in sendItemDataRequest.");
        }
    }

    public void a(RequestId requestId, boolean z) {
        if (requestId == null) {
            requestId = new RequestId();
        }
        e.a(a, "sendPurchaseUpdatesRequest/sendGetUserData first:" + requestId);
        a(requestId.toString(), true, z);
    }

    public void a(RequestId requestId, String str, FulfillmentResult fulfillmentResult) {
        e.a(a, "sendNotifyPurchaseFulfilled");
        try {
            Context b = d.d().b();
            Bundle bundle = new Bundle();
            JSONObject jSONObject = new JSONObject();
            jSONObject.put("requestId", requestId.toString());
            jSONObject.put("packageName", b.getPackageName());
            jSONObject.put("receiptId", str);
            jSONObject.put("fulfillmentResult", fulfillmentResult);
            jSONObject.put("sdkVersion", PurchasingService.SDK_VERSION);
            bundle.putString("purchaseFulfilledInput", jSONObject.toString());
            Intent a2 = a("com.amazon.testclient.iap.purchaseFulfilled");
            a2.addFlags(268435456);
            a2.putExtras(bundle);
            b.startService(a2);
        } catch (JSONException e) {
            e.b(a, "Error in sendNotifyPurchaseFulfilled.");
        }
    }

    public void a(Context context, Intent intent) {
        e.a(a, "handleResponse");
        intent.setComponent(new ComponentName("com.amazon.sdktestclient", "com.amazon.sdktestclient.command.CommandBroker"));
        try {
            String string = intent.getExtras().getString("responseType");
            if (string.equalsIgnoreCase("com.amazon.testclient.iap.purchase")) {
                g(intent);
            } else if (string.equalsIgnoreCase("com.amazon.testclient.iap.appUserId")) {
                e(intent);
            } else if (string.equalsIgnoreCase("com.amazon.testclient.iap.itemData")) {
                c(intent);
            } else if (string.equalsIgnoreCase("com.amazon.testclient.iap.purchaseUpdates")) {
                a(intent);
            }
        } catch (Exception e) {
            Log.e(a, "Error handling response.", e);
        }
    }

    private Intent a(String str) {
        Intent intent = new Intent(str);
        intent.setComponent(new ComponentName("com.amazon.sdktestclient", "com.amazon.sdktestclient.command.CommandBroker"));
        return intent;
    }

    /* access modifiers changed from: protected */
    public void a(final Object obj) {
        com.amazon.device.iap.internal.util.d.a(obj, "response");
        Context b = d.d().b();
        final PurchasingListener a2 = d.d().a();
        if (b == null || a2 == null) {
            e.a(a, "PurchasingListener is not set. Dropping response: " + obj);
            return;
        }
        new Handler(b.getMainLooper()).post(new Runnable() {
            public void run() {
                try {
                    if (obj instanceof ProductDataResponse) {
                        a2.onProductDataResponse((ProductDataResponse) obj);
                    } else if (obj instanceof UserDataResponse) {
                        a2.onUserDataResponse((UserDataResponse) obj);
                    } else if (obj instanceof PurchaseUpdatesResponse) {
                        a2.onPurchaseUpdatesResponse((PurchaseUpdatesResponse) obj);
                    } else if (obj instanceof PurchaseResponse) {
                        a2.onPurchaseResponse((PurchaseResponse) obj);
                    } else {
                        e.b(c.a, "Unknown response type:" + obj.getClass().getName());
                    }
                } catch (Exception e) {
                    e.b(c.a, "Error in sendResponse: " + e);
                }
            }
        });
    }

    private void a(Intent intent) throws JSONException {
        PurchaseUpdatesResponse b = b(intent);
        if (b.getRequestStatus() == PurchaseUpdatesResponse.RequestStatus.SUCCESSFUL) {
            String optString = new JSONObject(intent.getStringExtra("purchaseUpdatesOutput")).optString("offset");
            Log.i(a, "Offset for PurchaseUpdatesResponse:" + optString);
            b.a(b.getUserData().getUserId(), optString);
        }
        a((Object) b);
    }

    private PurchaseUpdatesResponse b(Intent intent) {
        Exception exc;
        UserData userData;
        RequestId requestId;
        ArrayList arrayList;
        PurchaseUpdatesResponse.RequestStatus requestStatus;
        boolean z;
        PurchaseUpdatesResponse.RequestStatus requestStatus2 = PurchaseUpdatesResponse.RequestStatus.FAILED;
        try {
            JSONObject jSONObject = new JSONObject(intent.getStringExtra("purchaseUpdatesOutput"));
            RequestId fromString = RequestId.fromString(jSONObject.optString("requestId"));
            try {
                requestStatus2 = PurchaseUpdatesResponse.RequestStatus.valueOf(jSONObject.optString("status"));
                boolean optBoolean = jSONObject.optBoolean("isMore");
                try {
                    UserData build = new UserDataBuilder().setUserId(jSONObject.optString("userId")).setMarketplace(jSONObject.optString("marketplace")).build();
                    try {
                        if (requestStatus2 == PurchaseUpdatesResponse.RequestStatus.SUCCESSFUL) {
                            arrayList = new ArrayList();
                            try {
                                JSONArray optJSONArray = jSONObject.optJSONArray("receipts");
                                if (optJSONArray != null) {
                                    for (int i = 0; i < optJSONArray.length(); i++) {
                                        JSONObject optJSONObject = optJSONArray.optJSONObject(i);
                                        try {
                                            arrayList.add(a(optJSONObject));
                                        } catch (Exception e) {
                                            Log.e(a, "Failed to parse receipt from json:" + optJSONObject);
                                        }
                                    }
                                }
                            } catch (Exception e2) {
                                Exception exc2 = e2;
                                requestStatus = requestStatus2;
                                z = optBoolean;
                                userData = build;
                                requestId = fromString;
                                exc = exc2;
                            }
                        } else {
                            arrayList = null;
                        }
                        requestStatus = requestStatus2;
                        z = optBoolean;
                        userData = build;
                        requestId = fromString;
                    } catch (Exception e3) {
                        Exception exc3 = e3;
                        arrayList = null;
                        requestStatus = requestStatus2;
                        z = optBoolean;
                        userData = build;
                        requestId = fromString;
                        exc = exc3;
                        Log.e(a, "Error parsing purchase updates output", exc);
                        return new PurchaseUpdatesResponseBuilder().setRequestId(requestId).setRequestStatus(requestStatus).setUserData(userData).setReceipts(arrayList).setHasMore(z).build();
                    }
                } catch (Exception e4) {
                    requestId = fromString;
                    exc = e4;
                    arrayList = null;
                    boolean z2 = optBoolean;
                    userData = null;
                    requestStatus = requestStatus2;
                    z = z2;
                }
            } catch (Exception e5) {
                userData = null;
                requestId = fromString;
                exc = e5;
                arrayList = null;
                requestStatus = requestStatus2;
                z = false;
                Log.e(a, "Error parsing purchase updates output", exc);
                return new PurchaseUpdatesResponseBuilder().setRequestId(requestId).setRequestStatus(requestStatus).setUserData(userData).setReceipts(arrayList).setHasMore(z).build();
            }
        } catch (Exception e6) {
            exc = e6;
            userData = null;
            requestId = null;
            arrayList = null;
            requestStatus = requestStatus2;
            z = false;
            Log.e(a, "Error parsing purchase updates output", exc);
            return new PurchaseUpdatesResponseBuilder().setRequestId(requestId).setRequestStatus(requestStatus).setUserData(userData).setReceipts(arrayList).setHasMore(z).build();
        }
        return new PurchaseUpdatesResponseBuilder().setRequestId(requestId).setRequestStatus(requestStatus).setUserData(userData).setReceipts(arrayList).setHasMore(z).build();
    }

    private void c(Intent intent) {
        a((Object) d(intent));
    }

    private ProductDataResponse d(Intent intent) {
        Exception exc;
        LinkedHashSet linkedHashSet;
        ProductDataResponse.RequestStatus requestStatus;
        RequestId requestId;
        LinkedHashSet linkedHashSet2;
        HashMap hashMap;
        HashMap hashMap2 = null;
        ProductDataResponse.RequestStatus requestStatus2 = ProductDataResponse.RequestStatus.FAILED;
        try {
            JSONObject jSONObject = new JSONObject(intent.getStringExtra("itemDataOutput"));
            RequestId fromString = RequestId.fromString(jSONObject.optString("requestId"));
            try {
                ProductDataResponse.RequestStatus valueOf = ProductDataResponse.RequestStatus.valueOf(jSONObject.optString("status"));
                if (valueOf != ProductDataResponse.RequestStatus.FAILED) {
                    linkedHashSet = new LinkedHashSet();
                    try {
                        hashMap = new HashMap();
                    } catch (Exception e) {
                        Exception exc2 = e;
                        requestStatus = valueOf;
                        requestId = fromString;
                        exc = exc2;
                        Log.e(a, "Error parsing item data output", exc);
                        return new ProductDataResponseBuilder().setRequestId(requestId).setRequestStatus(requestStatus).setProductData(hashMap2).setUnavailableSkus(linkedHashSet).build();
                    }
                    try {
                        JSONArray optJSONArray = jSONObject.optJSONArray("unavailableSkus");
                        if (optJSONArray != null) {
                            for (int i = 0; i < optJSONArray.length(); i++) {
                                linkedHashSet.add(optJSONArray.getString(i));
                            }
                        }
                        JSONObject optJSONObject = jSONObject.optJSONObject("items");
                        if (optJSONObject != null) {
                            Iterator<String> keys = optJSONObject.keys();
                            while (keys.hasNext()) {
                                String next = keys.next();
                                hashMap.put(next, a(next, optJSONObject.optJSONObject(next)));
                            }
                        }
                        hashMap2 = hashMap;
                        linkedHashSet2 = linkedHashSet;
                    } catch (Exception e2) {
                        Exception exc3 = e2;
                        hashMap2 = hashMap;
                        requestStatus = valueOf;
                        requestId = fromString;
                        exc = exc3;
                        Log.e(a, "Error parsing item data output", exc);
                        return new ProductDataResponseBuilder().setRequestId(requestId).setRequestStatus(requestStatus).setProductData(hashMap2).setUnavailableSkus(linkedHashSet).build();
                    }
                } else {
                    linkedHashSet2 = null;
                }
                linkedHashSet = linkedHashSet2;
                requestStatus = valueOf;
                requestId = fromString;
            } catch (Exception e3) {
                linkedHashSet = null;
                ProductDataResponse.RequestStatus requestStatus3 = requestStatus2;
                requestId = fromString;
                exc = e3;
                requestStatus = requestStatus3;
                Log.e(a, "Error parsing item data output", exc);
                return new ProductDataResponseBuilder().setRequestId(requestId).setRequestStatus(requestStatus).setProductData(hashMap2).setUnavailableSkus(linkedHashSet).build();
            }
        } catch (Exception e4) {
            exc = e4;
            linkedHashSet = null;
            requestStatus = requestStatus2;
            requestId = null;
            Log.e(a, "Error parsing item data output", exc);
            return new ProductDataResponseBuilder().setRequestId(requestId).setRequestStatus(requestStatus).setProductData(hashMap2).setUnavailableSkus(linkedHashSet).build();
        }
        return new ProductDataResponseBuilder().setRequestId(requestId).setRequestStatus(requestStatus).setProductData(hashMap2).setUnavailableSkus(linkedHashSet).build();
    }

    private Product a(String str, JSONObject jSONObject) throws JSONException {
        ProductType valueOf = ProductType.valueOf(jSONObject.optString("itemType"));
        JSONObject jSONObject2 = jSONObject.getJSONObject("priceJson");
        Currency instance = Currency.getInstance(jSONObject2.optString("currency"));
        String str2 = instance.getSymbol() + new BigDecimal(jSONObject2.optString("value"));
        return new ProductBuilder().setSku(str).setProductType(valueOf).setDescription(jSONObject.optString("description")).setPrice(str2).setSmallIconUrl(jSONObject.optString("smallIconUrl")).setTitle(jSONObject.optString(ShareConstants.WEB_DIALOG_PARAM_TITLE)).build();
    }

    private void e(Intent intent) {
        JSONObject jSONObject;
        UserDataResponse f = f(intent);
        RequestId requestId = f.getRequestId();
        String stringExtra = intent.getStringExtra("userInput");
        try {
            jSONObject = new JSONObject(stringExtra);
        } catch (JSONException e) {
            Log.e(a, "Unable to parse request data: " + stringExtra, e);
            jSONObject = null;
        }
        if (requestId == null || jSONObject == null) {
            a((Object) f);
        } else if (!jSONObject.optBoolean("isPurchaseUpdates", false)) {
            a((Object) f);
        } else if (f.getUserData() == null || com.amazon.device.iap.internal.util.d.a(f.getUserData().getUserId())) {
            Log.e(a, "No Userid found in userDataResponse" + f);
            a((Object) new PurchaseUpdatesResponseBuilder().setRequestId(requestId).setRequestStatus(PurchaseUpdatesResponse.RequestStatus.FAILED).setUserData(f.getUserData()).setReceipts(new ArrayList()).setHasMore(false).build());
        } else {
            Log.i(a, "sendGetPurchaseUpdates with user id" + f.getUserData().getUserId());
            a(requestId.toString(), f.getUserData().getUserId(), jSONObject.optBoolean("reset", true));
        }
    }

    private void a(String str, String str2, boolean z) {
        try {
            Context b = d.d().b();
            String a2 = b.a(str2);
            Log.i(a, "send PurchaseUpdates with user id:" + str2 + ";reset flag:" + z + ", local cursor:" + a2 + ", parsed from old requestId:" + str);
            Bundle bundle = new Bundle();
            JSONObject jSONObject = new JSONObject();
            jSONObject.put("requestId", str.toString());
            if (z) {
                a2 = null;
            }
            jSONObject.put("offset", a2);
            jSONObject.put("sdkVersion", PurchasingService.SDK_VERSION);
            jSONObject.put("packageName", b.getPackageName());
            bundle.putString("purchaseUpdatesInput", jSONObject.toString());
            Intent a3 = a("com.amazon.testclient.iap.purchaseUpdates");
            a3.addFlags(268435456);
            a3.putExtras(bundle);
            b.startService(a3);
        } catch (JSONException e) {
            e.b(a, "Error in sendPurchaseUpdatesRequest.");
        }
    }

    private UserDataResponse f(Intent intent) {
        Exception e;
        RequestId requestId;
        UserDataResponse.RequestStatus requestStatus;
        UserData userData;
        UserDataResponse.RequestStatus requestStatus2;
        UserData userData2 = null;
        UserDataResponse.RequestStatus requestStatus3 = UserDataResponse.RequestStatus.FAILED;
        try {
            JSONObject jSONObject = new JSONObject(intent.getStringExtra("userOutput"));
            requestId = RequestId.fromString(jSONObject.optString("requestId"));
            try {
                requestStatus = UserDataResponse.RequestStatus.valueOf(jSONObject.optString("status"));
            } catch (Exception e2) {
                Exception exc = e2;
                requestStatus = requestStatus3;
                e = exc;
            }
            try {
                if (requestStatus == UserDataResponse.RequestStatus.SUCCESSFUL) {
                    userData2 = new UserDataBuilder().setUserId(jSONObject.optString("userId")).setMarketplace(jSONObject.optString("marketplace")).build();
                }
                UserDataResponse.RequestStatus requestStatus4 = requestStatus;
                userData = userData2;
                requestStatus2 = requestStatus4;
            } catch (Exception e3) {
                e = e3;
                Log.e(a, "Error parsing userid output", e);
                UserDataResponse.RequestStatus requestStatus5 = requestStatus;
                userData = null;
                requestStatus2 = requestStatus5;
                return new UserDataResponseBuilder().setRequestId(requestId).setRequestStatus(requestStatus2).setUserData(userData).build();
            }
        } catch (Exception e4) {
            requestId = null;
            UserDataResponse.RequestStatus requestStatus6 = requestStatus3;
            e = e4;
            requestStatus = requestStatus6;
            Log.e(a, "Error parsing userid output", e);
            UserDataResponse.RequestStatus requestStatus52 = requestStatus;
            userData = null;
            requestStatus2 = requestStatus52;
            return new UserDataResponseBuilder().setRequestId(requestId).setRequestStatus(requestStatus2).setUserData(userData).build();
        }
        return new UserDataResponseBuilder().setRequestId(requestId).setRequestStatus(requestStatus2).setUserData(userData).build();
    }

    private void g(Intent intent) {
        a((Object) h(intent));
    }

    private PurchaseResponse h(Intent intent) {
        Exception e;
        RequestId requestId;
        UserData userData;
        PurchaseResponse.RequestStatus requestStatus;
        Receipt receipt = null;
        PurchaseResponse.RequestStatus requestStatus2 = PurchaseResponse.RequestStatus.FAILED;
        try {
            JSONObject jSONObject = new JSONObject(intent.getStringExtra("purchaseOutput"));
            requestId = RequestId.fromString(jSONObject.optString("requestId"));
            try {
                userData = new UserDataBuilder().setUserId(jSONObject.optString("userId")).setMarketplace(jSONObject.optString("marketplace")).build();
                try {
                    requestStatus = PurchaseResponse.RequestStatus.safeValueOf(jSONObject.optString("purchaseStatus"));
                } catch (Exception e2) {
                    Exception exc = e2;
                    requestStatus = requestStatus2;
                    e = exc;
                    Log.e(a, "Error parsing purchase output", e);
                    return new PurchaseResponseBuilder().setRequestId(requestId).setRequestStatus(requestStatus).setUserData(userData).setReceipt(receipt).build();
                }
            } catch (Exception e3) {
                userData = null;
                PurchaseResponse.RequestStatus requestStatus3 = requestStatus2;
                e = e3;
                requestStatus = requestStatus3;
                Log.e(a, "Error parsing purchase output", e);
                return new PurchaseResponseBuilder().setRequestId(requestId).setRequestStatus(requestStatus).setUserData(userData).setReceipt(receipt).build();
            }
            try {
                JSONObject optJSONObject = jSONObject.optJSONObject("receipt");
                if (optJSONObject != null) {
                    receipt = a(optJSONObject);
                }
            } catch (Exception e4) {
                e = e4;
                Log.e(a, "Error parsing purchase output", e);
                return new PurchaseResponseBuilder().setRequestId(requestId).setRequestStatus(requestStatus).setUserData(userData).setReceipt(receipt).build();
            }
        } catch (Exception e5) {
            userData = null;
            requestId = null;
            Exception exc2 = e5;
            requestStatus = requestStatus2;
            e = exc2;
            Log.e(a, "Error parsing purchase output", e);
            return new PurchaseResponseBuilder().setRequestId(requestId).setRequestStatus(requestStatus).setUserData(userData).setReceipt(receipt).build();
        }
        return new PurchaseResponseBuilder().setRequestId(requestId).setRequestStatus(requestStatus).setUserData(userData).setReceipt(receipt).build();
    }

    private Receipt a(JSONObject jSONObject) throws ParseException {
        String optString = jSONObject.optString("receiptId");
        String optString2 = jSONObject.optString("sku");
        ProductType valueOf = ProductType.valueOf(jSONObject.optString("itemType"));
        Date parse = b.a.parse(jSONObject.optString("purchaseDate"));
        String optString3 = jSONObject.optString("cancelDate");
        return new ReceiptBuilder().setReceiptId(optString).setSku(optString2).setProductType(valueOf).setPurchaseDate(parse).setCancelDate((optString3 == null || optString3.length() == 0) ? null : b.a.parse(optString3)).build();
    }
}
