package com.microsoft.telemetry.extensions;

import com.microsoft.telemetry.Extension;
import com.microsoft.telemetry.IJsonSerializable;
import com.microsoft.telemetry.JsonHelper;
import java.io.IOException;
import java.io.Writer;

public class os extends Extension implements IJsonSerializable {
    private String expId;
    private String locale;

    public os() {
        InitializeFields();
    }

    public String getLocale() {
        return this.locale;
    }

    public void setLocale(String value) {
        this.locale = value;
    }

    public String getExpId() {
        return this.expId;
    }

    public void setExpId(String value) {
        this.expId = value;
    }

    /* access modifiers changed from: protected */
    public String serializeContent(Writer writer) throws IOException {
        String prefix = super.serializeContent(writer);
        if (this.locale != null) {
            writer.write(prefix + "\"locale\":");
            writer.write(JsonHelper.convert(this.locale));
            prefix = ",";
        }
        if (this.expId == null) {
            return prefix;
        }
        writer.write(prefix + "\"expId\":");
        writer.write(JsonHelper.convert(this.expId));
        return ",";
    }

    /* access modifiers changed from: protected */
    public void InitializeFields() {
    }
}
