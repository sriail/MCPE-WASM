package net.hockeyapp.android.metrics.model;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import net.hockeyapp.android.metrics.JsonHelper;

public class SessionStateData extends TelemetryData {
    private SessionState state = SessionState.START;
    private int ver = 2;

    public SessionStateData() {
        InitializeFields();
        SetupAttributes();
    }

    public String getEnvelopeName() {
        return "Microsoft.ApplicationInsights.SessionState";
    }

    public String getBaseType() {
        return "SessionStateData";
    }

    public int getVer() {
        return this.ver;
    }

    public void setVer(int value) {
        this.ver = value;
    }

    public SessionState getState() {
        return this.state;
    }

    public void setState(SessionState value) {
        this.state = value;
    }

    public Map<String, String> getProperties() {
        return null;
    }

    public void setProperties(Map<String, String> map) {
    }

    /* access modifiers changed from: protected */
    public String serializeContent(Writer writer) throws IOException {
        writer.write(super.serializeContent(writer) + "\"ver\":");
        writer.write(JsonHelper.convert(Integer.valueOf(this.ver)));
        writer.write("," + "\"state\":");
        writer.write(JsonHelper.convert(Integer.valueOf(this.state.getValue())));
        return ",";
    }

    public void SetupAttributes() {
    }

    /* access modifiers changed from: protected */
    public void InitializeFields() {
        this.QualifiedName = "com.microsoft.applicationinsights.contracts.SessionStateData";
    }
}
