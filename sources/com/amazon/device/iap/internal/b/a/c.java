package com.amazon.device.iap.internal.b.a;

import com.amazon.device.iap.internal.b.e;
import com.amazon.device.iap.internal.b.i;
import com.amazon.device.iap.internal.model.PurchaseResponseBuilder;
import com.amazon.device.iap.internal.model.UserDataBuilder;
import com.amazon.device.iap.model.PurchaseResponse;
import com.amazon.device.iap.model.Receipt;

/* compiled from: PurchaseResponseCommandBase */
abstract class c extends i {
    c(e eVar, String str) {
        super(eVar, "purchase_response", str);
    }

    /* access modifiers changed from: protected */
    public void a(String str, String str2, String str3, PurchaseResponse.RequestStatus requestStatus) {
        e b = b();
        b.d().a((Object) new PurchaseResponseBuilder().setRequestId(b.c()).setRequestStatus(requestStatus).setUserData(new UserDataBuilder().setUserId(str).setMarketplace(str2).build()).setReceipt((Receipt) null).build());
    }
}
