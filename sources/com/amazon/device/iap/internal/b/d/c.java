package com.amazon.device.iap.internal.b.d;

import com.amazon.device.iap.internal.b.d;
import com.amazon.device.iap.internal.b.e;
import com.amazon.device.iap.internal.model.PurchaseUpdatesResponseBuilder;
import com.amazon.device.iap.internal.model.UserDataBuilder;
import com.amazon.device.iap.internal.util.a;
import com.amazon.device.iap.model.PurchaseUpdatesResponse;
import com.amazon.device.iap.model.Receipt;
import com.amazon.venezia.command.SuccessResult;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;

/* compiled from: PurchaseUpdatesCommandV2 */
public final class c extends b {
    private static final String b = c.class.getSimpleName();

    public c(e eVar, boolean z) {
        super(eVar, "2.0", z);
    }

    /* access modifiers changed from: protected */
    public boolean a(SuccessResult successResult) throws Exception {
        Map data = successResult.getData();
        com.amazon.device.iap.internal.util.e.a(b, "data: " + data);
        String str = (String) data.get("userId");
        List<Receipt> a = a(str, (String) data.get("receipts"), (String) data.get("requestId"));
        boolean booleanValue = Boolean.valueOf((String) data.get("hasMore")).booleanValue();
        e b2 = b();
        PurchaseUpdatesResponse build = new PurchaseUpdatesResponseBuilder().setRequestId(b2.c()).setRequestStatus(PurchaseUpdatesResponse.RequestStatus.SUCCESSFUL).setUserData(new UserDataBuilder().setUserId(str).setMarketplace((String) data.get("marketplace")).build()).setReceipts(a).setHasMore(booleanValue).build();
        b2.d().a("newCursor", (String) data.get("cursor"));
        b2.d().a((Object) build);
        return true;
    }

    private List<Receipt> a(String str, String str2, String str3) throws JSONException {
        ArrayList arrayList = new ArrayList();
        JSONArray jSONArray = new JSONArray(str2);
        for (int i = 0; i < jSONArray.length(); i++) {
            try {
                arrayList.add(a.a(jSONArray.getJSONObject(i), str, str3));
            } catch (com.amazon.device.iap.internal.b.a e) {
                com.amazon.device.iap.internal.util.e.b(b, "fail to parse receipt, requestId:" + e.a());
            } catch (d e2) {
                com.amazon.device.iap.internal.util.e.b(b, "fail to verify receipt, requestId:" + e2.a());
            } catch (Throwable th) {
                com.amazon.device.iap.internal.util.e.b(b, "fail to verify receipt, requestId:" + th.getMessage());
            }
        }
        return arrayList;
    }
}
