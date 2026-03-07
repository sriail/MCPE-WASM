package com.amazon.device.iap.internal.b.c;

import com.amazon.device.iap.internal.b.e;
import com.amazon.device.iap.internal.b.i;
import com.amazon.device.iap.internal.model.ProductDataResponseBuilder;
import com.amazon.device.iap.model.ProductDataResponse;
import com.amazon.device.iap.model.RequestId;
import java.util.Set;

/* compiled from: GetProductDataRequest */
public final class d extends e {
    public d(RequestId requestId, Set<String> set) {
        super(requestId);
        a aVar = new a(this, set);
        aVar.b((i) new b(this, set));
        a((i) aVar);
    }

    public void a() {
        a((Object) (ProductDataResponse) d().a());
    }

    public void b() {
        ProductDataResponse productDataResponse = (ProductDataResponse) d().a();
        if (productDataResponse == null) {
            productDataResponse = new ProductDataResponseBuilder().setRequestId(c()).setRequestStatus(ProductDataResponse.RequestStatus.FAILED).build();
        }
        a((Object) productDataResponse);
    }
}
