package com.amazon.device.iap.internal.b;

import android.os.RemoteException;
import com.amazon.android.Kiwi;
import com.amazon.android.framework.exception.KiwiException;
import com.amazon.android.framework.prompt.PromptContent;
import com.amazon.android.framework.task.command.AbstractCommandTask;
import com.amazon.android.licensing.LicenseFailurePromptContentMapper;
import com.amazon.device.iap.PurchasingService;
import com.amazon.device.iap.internal.util.d;
import com.amazon.device.iap.internal.util.e;
import com.amazon.venezia.command.FailureResult;
import com.amazon.venezia.command.SuccessResult;
import java.util.HashMap;
import java.util.Map;

/* compiled from: KiwiCommand */
public abstract class i extends AbstractCommandTask {
    private static final String a = i.class.getSimpleName();
    private final e b;
    private final String c;
    private final String d;
    private final String e;
    private final Map<String, Object> f;
    private final LicenseFailurePromptContentMapper g = new LicenseFailurePromptContentMapper();
    private boolean h;
    private i i;
    private i j;
    private boolean k = false;

    /* access modifiers changed from: protected */
    public abstract boolean a(SuccessResult successResult) throws Exception;

    public i(e eVar, String str, String str2) {
        this.b = eVar;
        this.c = eVar.c().toString();
        this.d = str;
        this.e = str2;
        this.f = new HashMap();
        this.f.put("requestId", this.c);
        this.f.put("sdkVersion", PurchasingService.SDK_VERSION);
        this.h = true;
        this.i = null;
        this.j = null;
    }

    public i a(boolean z) {
        this.k = z;
        return this;
    }

    public void a(i iVar) {
        this.i = iVar;
    }

    public void b(i iVar) {
        this.j = iVar;
    }

    /* access modifiers changed from: protected */
    public void a(String str, Object obj) {
        this.f.put(str, obj);
    }

    /* access modifiers changed from: protected */
    public e b() {
        return this.b;
    }

    /* access modifiers changed from: protected */
    public String c() {
        return this.c;
    }

    /* access modifiers changed from: protected */
    public boolean isExecutionNeeded() {
        return true;
    }

    /* access modifiers changed from: protected */
    public String getCommandName() {
        return this.d;
    }

    /* access modifiers changed from: protected */
    public String getCommandVersion() {
        return this.e;
    }

    /* access modifiers changed from: protected */
    public Map<String, Object> getCommandData() {
        return this.f;
    }

    /* access modifiers changed from: protected */
    public void b(boolean z) {
        this.h = z;
    }

    private void a(PromptContent promptContent) {
        if (promptContent != null) {
            Kiwi.getPromptManager().present(new b(promptContent));
        }
    }

    /* access modifiers changed from: protected */
    public final void onSuccess(SuccessResult successResult) throws RemoteException {
        String str = (String) successResult.getData().get("errorMessage");
        e.a(a, "onSuccess: result = " + successResult + ", errorMessage: " + str);
        if (d.a(str)) {
            boolean z = false;
            try {
                z = a(successResult);
            } catch (Exception e2) {
                e.b(a, "Error calling onResult: " + e2);
            }
            if (z && this.i != null) {
                this.i.a_();
            } else if (this.k) {
            } else {
                if (z) {
                    this.b.a();
                } else {
                    this.b.b();
                }
            }
        } else if (!this.k) {
            this.b.b();
        }
    }

    /* access modifiers changed from: protected */
    public final void onFailure(FailureResult failureResult) throws RemoteException, KiwiException {
        boolean z;
        String str;
        e.a(a, "onFailure: result = " + failureResult);
        if (failureResult == null || (str = (String) failureResult.getExtensionData().get("maxVersion")) == null || !str.equalsIgnoreCase("1.0")) {
            z = false;
        } else {
            z = true;
        }
        if (!z || this.j == null) {
            if (this.h) {
                a(new PromptContent(failureResult.getDisplayableName(), failureResult.getDisplayableMessage(), failureResult.getButtonLabel(), failureResult.show()));
            }
            if (!this.k) {
                this.b.b();
                return;
            }
            return;
        }
        this.j.a(this.k);
        this.j.a_();
    }

    /* access modifiers changed from: protected */
    public final void onException(KiwiException kiwiException) {
        e.a(a, "onException: exception = " + kiwiException.getMessage());
        if (!"UNHANDLED_EXCEPTION".equals(kiwiException.getType()) || !"2.0".equals(this.e) || this.j == null) {
            if (this.h) {
                a(this.g.map(kiwiException));
            }
            if (!this.k) {
                this.b.b();
                return;
            }
            return;
        }
        this.j.a(this.k);
        this.j.a_();
    }

    public void a_() {
        Kiwi.addCommandToCommandTaskPipeline(this);
    }
}
