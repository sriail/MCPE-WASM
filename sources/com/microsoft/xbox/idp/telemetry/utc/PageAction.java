package com.microsoft.xbox.idp.telemetry.utc;

import Microsoft.Telemetry.Data;
import com.microsoft.bond.BondDataType;
import com.microsoft.bond.BondMirror;
import com.microsoft.bond.BondSerializable;
import com.microsoft.bond.FieldDef;
import com.microsoft.bond.Metadata;
import com.microsoft.bond.Modifier;
import com.microsoft.bond.ProtocolCapability;
import com.microsoft.bond.ProtocolReader;
import com.microsoft.bond.ProtocolWriter;
import com.microsoft.bond.SchemaDef;
import com.microsoft.bond.StructDef;
import com.microsoft.bond.TypeDef;
import com.microsoft.bond.internal.Marshaler;
import com.microsoft.bond.internal.ReadHelper;
import java.io.IOException;
import java.io.InputStream;

public class PageAction extends Data<CommonData> {
    private String actionName;
    private String pageName;

    public BondSerializable clone() {
        return null;
    }

    public final String getActionName() {
        return this.actionName;
    }

    public final void setActionName(String value) {
        this.actionName = value;
    }

    public final String getPageName() {
        return this.pageName;
    }

    public final void setPageName(String value) {
        this.pageName = value;
    }

    public static class Schema {
        /* access modifiers changed from: private */
        public static final Metadata actionName_metadata = new Metadata();
        public static final Metadata metadata = new Metadata();
        /* access modifiers changed from: private */
        public static final Metadata pageName_metadata = new Metadata();
        public static final SchemaDef schemaDef = new SchemaDef();

        static {
            metadata.setName("PageAction");
            metadata.setQualified_name("com.microsoft.xbox.idp.telemetry.utc.PageAction");
            metadata.getAttributes().put("Description", "OnlineId PageAction event");
            actionName_metadata.setName("actionName");
            actionName_metadata.setModifier(Modifier.Required);
            actionName_metadata.getAttributes().put("Description", "The name of the action taking place");
            pageName_metadata.setName("pageName");
            pageName_metadata.setModifier(Modifier.Required);
            pageName_metadata.getAttributes().put("Description", "The name of the page the action is taking place upon");
            schemaDef.setRoot(getTypeDef(schemaDef));
        }

        public static TypeDef getTypeDef(SchemaDef schema) {
            TypeDef type = new TypeDef();
            type.setId(BondDataType.BT_STRUCT);
            type.setStruct_def(getStructDef(schema));
            return type;
        }

        private static short getStructDef(SchemaDef schema) {
            short pos = 0;
            while (true) {
                if (pos >= schema.getStructs().size()) {
                    StructDef structDef = new StructDef();
                    schema.getStructs().add(structDef);
                    structDef.setMetadata(metadata);
                    structDef.setBase_def(Data.Schema.getTypeDef(schema));
                    FieldDef field = new FieldDef();
                    field.setId(10);
                    field.setMetadata(actionName_metadata);
                    field.getType().setId(BondDataType.BT_WSTRING);
                    structDef.getFields().add(field);
                    FieldDef field2 = new FieldDef();
                    field2.setId(20);
                    field2.setMetadata(pageName_metadata);
                    field2.getType().setId(BondDataType.BT_WSTRING);
                    structDef.getFields().add(field2);
                    break;
                } else if (schema.getStructs().get(pos).getMetadata() == metadata) {
                    break;
                } else {
                    pos = (short) (pos + 1);
                }
            }
            return pos;
        }
    }

    public Object getField(FieldDef fieldDef) {
        switch (fieldDef.getId()) {
            case 10:
                return this.actionName;
            case 20:
                return this.pageName;
            default:
                return null;
        }
    }

    public void setField(FieldDef fieldDef, Object value) {
        switch (fieldDef.getId()) {
            case 10:
                this.actionName = (String) value;
                return;
            case 20:
                this.pageName = (String) value;
                return;
            default:
                return;
        }
    }

    public BondMirror createInstance(StructDef structDef) {
        return null;
    }

    public SchemaDef getSchema() {
        return getRuntimeSchema();
    }

    public static SchemaDef getRuntimeSchema() {
        return Schema.schemaDef;
    }

    public void reset() {
        reset("PageAction", "com.microsoft.xbox.idp.telemetry.utc.PageAction");
    }

    /* access modifiers changed from: protected */
    public void reset(String name, String qualifiedName) {
        super.reset(name, qualifiedName);
        this.actionName = "";
        this.pageName = "";
    }

    public void unmarshal(InputStream input) throws IOException {
        Marshaler.unmarshal(input, this);
    }

    public void unmarshal(InputStream input, BondSerializable schema) throws IOException {
        Marshaler.unmarshal(input, (SchemaDef) schema, this);
    }

    public void read(ProtocolReader reader) throws IOException {
        reader.readBegin();
        readNested(reader);
        reader.readEnd();
    }

    public void readNested(ProtocolReader reader) throws IOException {
        if (!reader.hasCapability(ProtocolCapability.TAGGED)) {
            readUntagged(reader, false);
        } else if (readTagged(reader, false)) {
            ReadHelper.skipPartialStruct(reader);
        }
    }

    public void read(ProtocolReader reader, BondSerializable schema) throws IOException {
    }

    /* access modifiers changed from: protected */
    public void readUntagged(ProtocolReader reader, boolean isBase) throws IOException {
        boolean canOmitFields = reader.hasCapability(ProtocolCapability.CAN_OMIT_FIELDS);
        reader.readStructBegin(isBase);
        super.readUntagged(reader, true);
        if (!canOmitFields || !reader.readFieldOmitted()) {
            this.actionName = reader.readWString();
        }
        if (!canOmitFields || !reader.readFieldOmitted()) {
            this.pageName = reader.readWString();
        }
        reader.readStructEnd();
    }

    /* access modifiers changed from: protected */
    public boolean readTagged(ProtocolReader reader, boolean isBase) throws IOException {
        ProtocolReader.FieldTag fieldTag;
        boolean isPartial = true;
        reader.readStructBegin(isBase);
        if (!super.readTagged(reader, true)) {
            return false;
        }
        while (true) {
            fieldTag = reader.readFieldBegin();
            if (fieldTag.type != BondDataType.BT_STOP && fieldTag.type != BondDataType.BT_STOP_BASE) {
                switch (fieldTag.id) {
                    case 10:
                        this.actionName = ReadHelper.readWString(reader, fieldTag.type);
                        break;
                    case 20:
                        this.pageName = ReadHelper.readWString(reader, fieldTag.type);
                        break;
                    default:
                        reader.skip(fieldTag.type);
                        break;
                }
                reader.readFieldEnd();
            }
        }
        if (fieldTag.type != BondDataType.BT_STOP_BASE) {
            isPartial = false;
        }
        reader.readStructEnd();
        return isPartial;
    }

    public void marshal(ProtocolWriter writer) throws IOException {
        Marshaler.marshal(this, writer);
    }

    public void write(ProtocolWriter writer) throws IOException {
        writer.writeBegin();
        ProtocolWriter firstPassWriter = writer.getFirstPassWriter();
        if (firstPassWriter != null) {
            writeNested(firstPassWriter, false);
            writeNested(writer, false);
        } else {
            writeNested(writer, false);
        }
        writer.writeEnd();
    }

    public void writeNested(ProtocolWriter writer, boolean isBase) throws IOException {
        boolean hasCapability = writer.hasCapability(ProtocolCapability.CAN_OMIT_FIELDS);
        writer.writeStructBegin(Schema.metadata, isBase);
        super.writeNested(writer, true);
        writer.writeFieldBegin(BondDataType.BT_WSTRING, 10, Schema.actionName_metadata);
        writer.writeWString(this.actionName);
        writer.writeFieldEnd();
        writer.writeFieldBegin(BondDataType.BT_WSTRING, 20, Schema.pageName_metadata);
        writer.writeWString(this.pageName);
        writer.writeFieldEnd();
        writer.writeStructEnd(isBase);
    }

    public boolean memberwiseCompare(Object obj) {
        if (obj == null) {
            return false;
        }
        PageAction that = (PageAction) obj;
        if (!memberwiseCompareQuick(that) || !memberwiseCompareDeep(that)) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:17:0x0021  */
    /* JADX WARNING: Removed duplicated region for block: B:19:0x0024  */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x0037  */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x004f  */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x0065  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean memberwiseCompareQuick(com.microsoft.xbox.idp.telemetry.utc.PageAction r6) {
        /*
            r5 = this;
            r2 = 1
            r3 = 0
            r0 = 1
            if (r0 == 0) goto L_0x0039
            boolean r1 = super.memberwiseCompareQuick(r6)
            if (r1 == 0) goto L_0x0039
            r0 = r2
        L_0x000c:
            if (r0 == 0) goto L_0x003f
            java.lang.String r1 = r5.actionName
            if (r1 != 0) goto L_0x003b
            r1 = r2
        L_0x0013:
            java.lang.String r4 = r6.actionName
            if (r4 != 0) goto L_0x003d
            r4 = r2
        L_0x0018:
            if (r1 != r4) goto L_0x003f
            r0 = r2
        L_0x001b:
            if (r0 == 0) goto L_0x004f
            java.lang.String r1 = r5.actionName
            if (r1 != 0) goto L_0x0041
        L_0x0021:
            r0 = r2
        L_0x0022:
            if (r0 == 0) goto L_0x0055
            java.lang.String r1 = r5.pageName
            if (r1 != 0) goto L_0x0051
            r1 = r2
        L_0x0029:
            java.lang.String r4 = r6.pageName
            if (r4 != 0) goto L_0x0053
            r4 = r2
        L_0x002e:
            if (r1 != r4) goto L_0x0055
            r0 = r2
        L_0x0031:
            if (r0 == 0) goto L_0x0065
            java.lang.String r1 = r5.pageName
            if (r1 != 0) goto L_0x0057
        L_0x0037:
            r0 = r2
        L_0x0038:
            return r0
        L_0x0039:
            r0 = r3
            goto L_0x000c
        L_0x003b:
            r1 = r3
            goto L_0x0013
        L_0x003d:
            r4 = r3
            goto L_0x0018
        L_0x003f:
            r0 = r3
            goto L_0x001b
        L_0x0041:
            java.lang.String r1 = r5.actionName
            int r1 = r1.length()
            java.lang.String r4 = r6.actionName
            int r4 = r4.length()
            if (r1 == r4) goto L_0x0021
        L_0x004f:
            r0 = r3
            goto L_0x0022
        L_0x0051:
            r1 = r3
            goto L_0x0029
        L_0x0053:
            r4 = r3
            goto L_0x002e
        L_0x0055:
            r0 = r3
            goto L_0x0031
        L_0x0057:
            java.lang.String r1 = r5.pageName
            int r1 = r1.length()
            java.lang.String r4 = r6.pageName
            int r4 = r4.length()
            if (r1 == r4) goto L_0x0037
        L_0x0065:
            r0 = r3
            goto L_0x0038
        */
        throw new UnsupportedOperationException("Method not decompiled: com.microsoft.xbox.idp.telemetry.utc.PageAction.memberwiseCompareQuick(com.microsoft.xbox.idp.telemetry.utc.PageAction):boolean");
    }

    /* access modifiers changed from: protected */
    public boolean memberwiseCompareDeep(PageAction that) {
        boolean equals;
        if (1 == 0 || !super.memberwiseCompareDeep(that)) {
            equals = false;
        } else {
            equals = true;
        }
        return (equals && (this.actionName == null || this.actionName.equals(that.actionName))) && (this.pageName == null || this.pageName.equals(that.pageName));
    }
}
