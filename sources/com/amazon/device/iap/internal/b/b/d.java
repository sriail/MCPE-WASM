package com.amazon.device.iap.internal.b.b;

import com.amazon.device.iap.internal.b.e;
import com.amazon.device.iap.internal.b.i;
import com.amazon.device.iap.internal.model.PurchaseResponseBuilder;
import com.amazon.device.iap.model.PurchaseResponse;
import com.amazon.device.iap.model.RequestId;

/* compiled from: PurchaseRequest */
public final class d extends e {
    public d(RequestId requestId, String str) {
        super(requestId);
        c cVar = new c(this, str);
        cVar.b((i) new b(this, str));
        a((i) cVar);
    }

    public void a() {
    }

    public void b() {
        PurchaseResponse purchaseResponse = (PurchaseResponse) d().a();
        if (purchaseResponse == null) {
            purchaseResponse = new PurchaseResponseBuilder().setRequestId(c()).setRequestStatus(PurchaseResponse.RequestStatus.FAILED).build();
        }
        a((Object) purchaseResponse);
    }
}
