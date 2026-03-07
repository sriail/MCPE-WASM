package com.amazon.device.iap.internal.b.e;

import com.amazon.device.iap.internal.b.e;
import com.amazon.device.iap.internal.b.i;
import com.amazon.device.iap.internal.model.UserDataResponseBuilder;
import com.amazon.device.iap.model.RequestId;
import com.amazon.device.iap.model.UserDataResponse;

/* compiled from: GetUserDataRequest */
public final class a extends e {
    public a(RequestId requestId) {
        super(requestId);
        c cVar = new c(this);
        cVar.b((i) new d(this));
        a((i) cVar);
    }

    public void a() {
        a((Object) (UserDataResponse) d().a());
    }

    public void b() {
        UserDataResponse userDataResponse = (UserDataResponse) d().a();
        if (userDataResponse == null) {
            userDataResponse = new UserDataResponseBuilder().setRequestId(c()).setRequestStatus(UserDataResponse.RequestStatus.FAILED).build();
        }
        a((Object) userDataResponse);
    }
}
