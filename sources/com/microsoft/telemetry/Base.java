package com.microsoft.telemetry;

import java.io.IOException;
import java.io.Writer;
import java.util.LinkedHashMap;

public class Base implements IJsonSerializable {
    public LinkedHashMap<String, String> Attributes = new LinkedHashMap<>();
    public String QualifiedName;
    private String baseType;

    public Base() {
        InitializeFields();
    }

    public String getBaseType() {
        return this.baseType;
    }

    public void setBaseType(String value) {
        this.baseType = value;
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
        if (this.baseType == null) {
            return "";
        }
        writer.write("" + "\"baseType\":");
        writer.write(JsonHelper.convert(this.baseType));
        return ",";
    }

    /* access modifiers changed from: protected */
    public void InitializeFields() {
    }
}
