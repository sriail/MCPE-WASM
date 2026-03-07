package net.hockeyapp.android.metrics.model;

import java.io.IOException;
import java.io.Writer;
import net.hockeyapp.android.metrics.JsonHelper;

public class Extension implements IJsonSerializable {
    private String ver = "1.0";

    public Extension() {
        InitializeFields();
    }

    public String getVer() {
        return this.ver;
    }

    public void setVer(String value) {
        this.ver = value;
    }

    public void serialize(Writer writer) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("writer");
        }
        writer.write(123);
        serializeContent(writer);
        writer.write(125);
    }

    /* access modifiers changed from: protected */
    public String serializeContent(Writer writer) throws IOException {
        if (this.ver == null) {
            return "";
        }
        writer.write("" + "\"ver\":");
        writer.write(JsonHelper.convert(this.ver));
        return ",";
    }

    /* access modifiers changed from: protected */
    public void InitializeFields() {
    }
}
