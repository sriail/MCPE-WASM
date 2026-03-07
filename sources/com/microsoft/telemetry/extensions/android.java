package com.microsoft.telemetry.extensions;

import com.microsoft.telemetry.Extension;
import com.microsoft.telemetry.IJsonSerializable;
import com.microsoft.telemetry.JsonHelper;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class android extends Extension implements IJsonSerializable {
    private String libVer;
    private List<String> tickets;

    public android() {
        InitializeFields();
    }

    public String getLibVer() {
        return this.libVer;
    }

    public void setLibVer(String value) {
        this.libVer = value;
    }

    public List<String> getTickets() {
        if (this.tickets == null) {
            this.tickets = new ArrayList();
        }
        return this.tickets;
    }

    public void setTickets(List<String> value) {
        this.tickets = value;
    }

    /* access modifiers changed from: protected */
    public String serializeContent(Writer writer) throws IOException {
        String prefix = super.serializeContent(writer);
        if (this.libVer != null) {
            writer.write(prefix + "\"libVer\":");
            writer.write(JsonHelper.convert(this.libVer));
            prefix = ",";
        }
        if (this.tickets == null) {
            return prefix;
        }
        writer.write(prefix + "\"tickets\":");
        JsonHelper.writeListString(writer, this.tickets);
        return ",";
    }

    /* access modifiers changed from: protected */
    public void InitializeFields() {
    }
}
