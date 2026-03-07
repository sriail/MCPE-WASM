package com.amazon.device.iap.internal.b.e;

import android.os.RemoteException;
import com.amazon.android.framework.exception.KiwiException;
import com.amazon.device.iap.internal.b.e;
import com.amazon.device.iap.internal.model.UserDataBuilder;
import com.amazon.device.iap.internal.model.UserDataResponseBuilder;
import com.amazon.device.iap.internal.util.d;
import com.amazon.device.iap.model.UserData;
import com.amazon.device.iap.model.UserDataResponse;
import com.amazon.venezia.command.SuccessResult;
import java.util.Map;

/* compiled from: GetUserIdCommandV2 */
public final class c extends b {
    private static final String b = c.class.getSimpleName();

    public c(e eVar) {
        super(eVar, "2.0");
    }

    /* access modifiers changed from: protected */
    public boolean a(SuccessResult successResult) throws RemoteException, KiwiException {
        com.amazon.device.iap.internal.util.e.a(b, "onResult: result = " + successResult);
        Map data = successResult.getData();
        com.amazon.device.iap.internal.util.e.a(b, "data: " + data);
        String str = (String) data.get("userId");
        String str2 = (String) data.get("marketplace");
        e b2 = b();
        if (d.a(str) || d.a(str2)) {
            b2.d().a((Object) new UserDataResponseBuilder().setRequestId(b2.c()).setRequestStatus(UserDataResponse.RequestStatus.FAILED).build());
            return false;
        }
        UserData build = new UserDataBuilder().setUserId(str).setMarketplace(str2).build();
        UserDataResponse build2 = new UserDataResponseBuilder().setRequestId(b2.c()).setRequestStatus(UserDataResponse.RequestStatus.SUCCESSFUL).setUserData(build).build();
        b2.d().a("userId", build.getUserId());
        b2.d().a((Object) build2);
        return true;
    }
}
