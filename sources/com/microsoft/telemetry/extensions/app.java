package com.microsoft.telemetry.extensions;

import com.microsoft.telemetry.Extension;
import com.microsoft.telemetry.IJsonSerializable;
import com.microsoft.telemetry.JsonHelper;
import java.io.IOException;
import java.io.Writer;

public class app extends Extension implements IJsonSerializable {
    private String expId;
    private String userId;

    public app() {
        InitializeFields();
    }

    public String getExpId() {
        return this.expId;
    }

    public void setExpId(String value) {
        this.expId = value;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String value) {
        this.userId = value;
    }

    /* access modifiers changed from: protected */
    public String serializeContent(Writer writer) throws IOException {
        String prefix = super.serializeContent(writer);
        if (this.expId != null) {
            writer.write(prefix + "\"expId\":");
            writer.write(JsonHelper.convert(this.expId));
            prefix = ",";
        }
        if (this.userId == null) {
            return prefix;
        }
        writer.write(prefix + "\"userId\":");
        writer.write(JsonHelper.convert(this.userId));
        return ",";
    }

    /* access modifiers changed from: protected */
    public void InitializeFields() {
    }
}
