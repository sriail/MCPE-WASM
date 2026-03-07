package Microsoft.Android.LoggingLibrary;

import Ms.Telemetry.CllHeartBeat;
import com.microsoft.telemetry.Data;
import com.microsoft.telemetry.IJsonSerializable;
import java.io.IOException;
import java.io.Writer;

public class Snapshot extends Data<CllHeartBeat> implements IJsonSerializable {
    public Snapshot() {
        InitializeFields();
        SetupAttributes();
    }

    /* access modifiers changed from: protected */
    public String serializeContent(Writer writer) throws IOException {
        return super.serializeContent(writer);
    }

    public void SetupAttributes() {
        this.Attributes.put("Description", "Android's Client Telemetry Snapshot");
    }

    /* access modifiers changed from: protected */
    public void InitializeFields() {
        this.QualifiedName = "Microsoft.Android.LoggingLibrary.Snapshot";
    }
}
