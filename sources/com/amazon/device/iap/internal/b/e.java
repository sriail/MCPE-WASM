package com.amazon.device.iap.internal.b;

import android.content.Context;
import android.os.Handler;
import com.amazon.device.iap.PurchasingListener;
import com.amazon.device.iap.internal.util.b;
import com.amazon.device.iap.internal.util.d;
import com.amazon.device.iap.model.ProductDataResponse;
import com.amazon.device.iap.model.PurchaseResponse;
import com.amazon.device.iap.model.PurchaseUpdatesResponse;
import com.amazon.device.iap.model.RequestId;
import com.amazon.device.iap.model.UserDataResponse;

/* compiled from: KiwiRequest */
public class e {
    /* access modifiers changed from: private */
    public static final String a = e.class.getSimpleName();
    private final RequestId b;
    private final h c = new h();
    private i d = null;

    public e(RequestId requestId) {
        this.b = requestId;
    }

    /* access modifiers changed from: protected */
    public void a(i iVar) {
        this.d = iVar;
    }

    /* access modifiers changed from: protected */
    public void a(Object obj) {
        a(obj, (i) null);
    }

    /* access modifiers changed from: protected */
    public void a(final Object obj, final i iVar) {
        d.a(obj, "response");
        Context b2 = com.amazon.device.iap.internal.d.d().b();
        final PurchasingListener a2 = com.amazon.device.iap.internal.d.d().a();
        if (b2 == null || a2 == null) {
            com.amazon.device.iap.internal.util.e.a(a, "PurchasingListener is not set. Dropping response: " + obj);
            return;
        }
        new Handler(b2.getMainLooper()).post(new Runnable() {
            public void run() {
                e.this.d().a("notifyListenerResult", Boolean.FALSE);
                try {
                    if (obj instanceof ProductDataResponse) {
                        a2.onProductDataResponse((ProductDataResponse) obj);
                    } else if (obj instanceof UserDataResponse) {
                        a2.onUserDataResponse((UserDataResponse) obj);
                    } else if (obj instanceof PurchaseUpdatesResponse) {
                        PurchaseUpdatesResponse purchaseUpdatesResponse = (PurchaseUpdatesResponse) obj;
                        a2.onPurchaseUpdatesResponse(purchaseUpdatesResponse);
                        Object a2 = e.this.d().a("newCursor");
                        if (a2 != null && (a2 instanceof String)) {
                            b.a(purchaseUpdatesResponse.getUserData().getUserId(), a2.toString());
                        }
                    } else if (obj instanceof PurchaseResponse) {
                        a2.onPurchaseResponse((PurchaseResponse) obj);
                    } else {
                        com.amazon.device.iap.internal.util.e.b(e.a, "Unknown response type:" + obj.getClass().getName());
                    }
                    e.this.d().a("notifyListenerResult", Boolean.TRUE);
                } catch (Throwable th) {
                    com.amazon.device.iap.internal.util.e.b(e.a, "Error in sendResponse: " + th);
                }
                if (iVar != null) {
                    iVar.a(true);
                    iVar.a_();
                }
            }
        });
    }

    public RequestId c() {
        return this.b;
    }

    public h d() {
        return this.c;
    }

    public void e() {
        if (this.d != null) {
            this.d.a_();
        } else {
            a();
        }
    }

    public void a() {
    }

    public void b() {
    }
}
