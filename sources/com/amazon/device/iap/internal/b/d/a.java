package com.amazon.device.iap.internal.b.d;

import com.amazon.device.iap.internal.b.e;
import com.amazon.device.iap.internal.b.e.c;
import com.amazon.device.iap.internal.b.e.d;
import com.amazon.device.iap.internal.b.i;
import com.amazon.device.iap.internal.model.PurchaseUpdatesResponseBuilder;
import com.amazon.device.iap.model.PurchaseUpdatesResponse;
import com.amazon.device.iap.model.Receipt;
import com.amazon.device.iap.model.RequestId;
import java.util.HashSet;

/* compiled from: GetPurchaseUpdatesRequest */
public final class a extends e {
    public a(RequestId requestId, boolean z) {
        super(requestId);
        c cVar = new c(this);
        cVar.a((i) new c(this, z));
        d dVar = new d(this);
        dVar.a((i) new d(this));
        cVar.b((i) dVar);
        a((i) cVar);
    }

    public void a() {
        com.amazon.device.iap.internal.b.g.a aVar = null;
        PurchaseUpdatesResponse purchaseUpdatesResponse = (PurchaseUpdatesResponse) d().a();
        if (purchaseUpdatesResponse.getReceipts() != null && purchaseUpdatesResponse.getReceipts().size() > 0) {
            HashSet hashSet = new HashSet();
            for (Receipt next : purchaseUpdatesResponse.getReceipts()) {
                if (!com.amazon.device.iap.internal.util.d.a(next.getReceiptId())) {
                    hashSet.add(next.getReceiptId());
                }
            }
            aVar = new com.amazon.device.iap.internal.b.g.a(this, hashSet, com.amazon.device.iap.internal.model.a.DELIVERED.toString());
        }
        a(purchaseUpdatesResponse, aVar);
    }

    public void b() {
        PurchaseUpdatesResponse purchaseUpdatesResponse;
        Object a = d().a();
        if (a == null || !(a instanceof PurchaseUpdatesResponse)) {
            purchaseUpdatesResponse = new PurchaseUpdatesResponseBuilder().setRequestId(c()).setRequestStatus(PurchaseUpdatesResponse.RequestStatus.FAILED).build();
        } else {
            purchaseUpdatesResponse = (PurchaseUpdatesResponse) a;
        }
        a((Object) purchaseUpdatesResponse);
    }
}
