package com.amazon.device.iap.internal.b.a;

import com.amazon.device.iap.internal.b.e;
import com.amazon.device.iap.internal.b.f.b;
import com.amazon.device.iap.internal.b.f.c;
import com.amazon.device.iap.internal.b.i;
import com.amazon.device.iap.internal.model.PurchaseResponseBuilder;
import com.amazon.device.iap.model.ProductType;
import com.amazon.device.iap.model.PurchaseResponse;
import com.amazon.device.iap.model.Receipt;
import com.amazon.device.iap.model.RequestId;

/* compiled from: PurchaseResponseRequest */
public final class d extends e {
    public d(RequestId requestId) {
        super(requestId);
        a aVar = new a(this);
        aVar.b((i) new b(this));
        a((i) aVar);
    }

    public void a() {
        PurchaseResponse purchaseResponse = (PurchaseResponse) d().a();
        if (purchaseResponse != null) {
            Receipt receipt = purchaseResponse.getReceipt();
            boolean z = receipt != null;
            c cVar = new c(this, z);
            if (z && (ProductType.ENTITLED == receipt.getProductType() || ProductType.SUBSCRIPTION == receipt.getProductType())) {
                cVar.b((i) new b(this, c().toString()));
            }
            a(purchaseResponse, cVar);
        }
    }

    public void b() {
        PurchaseResponse purchaseResponse = (PurchaseResponse) d().a();
        if (purchaseResponse == null) {
            purchaseResponse = new PurchaseResponseBuilder().setRequestId(c()).setRequestStatus(PurchaseResponse.RequestStatus.FAILED).build();
        }
        a(purchaseResponse, new c(this, false));
    }
}
