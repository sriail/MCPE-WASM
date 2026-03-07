package com.microsoft.telemetry;

import com.microsoft.telemetry.Domain;
import java.io.IOException;
import java.io.Writer;

public class Data<TDomain extends Domain> extends Base implements ITelemetryData {
    private TDomain baseData;

    public Data() {
        InitializeFields();
        SetupAttributes();
    }

    public TDomain getBaseData() {
        return this.baseData;
    }

    public void setBaseData(TDomain value) {
        this.baseData = value;
    }

    /* access modifiers changed from: protected */
    public String serializeContent(Writer writer) throws IOException {
        writer.write(super.serializeContent(writer) + "\"baseData\":");
        JsonHelper.writeJsonSerializable(writer, this.baseData);
        return ",";
    }

    public void SetupAttributes() {
        this.Attributes.put("Description", "Data struct to contain both B and C sections.");
    }

    /* access modifiers changed from: protected */
    public void InitializeFields() {
        this.QualifiedName = "com.microsoft.telemetry.Data";
    }
}
