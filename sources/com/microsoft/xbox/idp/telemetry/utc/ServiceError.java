package com.microsoft.xbox.idp.telemetry.utc;

import Microsoft.Telemetry.Data;
import android.support.v4.view.MotionEventCompat;
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

public class ServiceError extends Data<CommonData> {
    private String errorCode;
    private String errorName;
    private String errorText;
    private String pageName;

    public BondSerializable clone() {
        return null;
    }

    public final String getErrorName() {
        return this.errorName;
    }

    public final void setErrorName(String value) {
        this.errorName = value;
    }

    public final String getErrorText() {
        return this.errorText;
    }

    public final void setErrorText(String value) {
        this.errorText = value;
    }

    public final String getErrorCode() {
        return this.errorCode;
    }

    public final void setErrorCode(String value) {
        this.errorCode = value;
    }

    public final String getPageName() {
        return this.pageName;
    }

    public final void setPageName(String value) {
        this.pageName = value;
    }

    public static class Schema {
        /* access modifiers changed from: private */
        public static final Metadata errorCode_metadata = new Metadata();
        /* access modifiers changed from: private */
        public static final Metadata errorName_metadata = new Metadata();
        /* access modifiers changed from: private */
        public static final Metadata errorText_metadata = new Metadata();
        public static final Metadata metadata = new Metadata();
        /* access modifiers changed from: private */
        public static final Metadata pageName_metadata = new Metadata();
        public static final SchemaDef schemaDef = new SchemaDef();

        static {
            metadata.setName("ServiceError");
            metadata.setQualified_name("com.microsoft.xbox.idp.telemetry.utc.ServiceError");
            metadata.getAttributes().put("Description", "OnlineId Service Error event");
            errorName_metadata.setName("errorName");
            errorName_metadata.setModifier(Modifier.Required);
            errorName_metadata.getAttributes().put("Description", "the name of the error - Can be a specific name (such as Profile Load Error)");
            errorText_metadata.setName("errorText");
            errorText_metadata.getAttributes().put("Description", "The text of the http error, if applicable");
            errorCode_metadata.setName("errorCode");
            errorCode_metadata.getAttributes().put("Description", "The code we get back in http error, if applicable.");
            pageName_metadata.setName("pageName");
            pageName_metadata.getAttributes().put("Description", "Most recent page shown");
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
                    field.setMetadata(errorName_metadata);
                    field.getType().setId(BondDataType.BT_WSTRING);
                    structDef.getFields().add(field);
                    FieldDef field2 = new FieldDef();
                    field2.setId(20);
                    field2.setMetadata(errorText_metadata);
                    field2.getType().setId(BondDataType.BT_WSTRING);
                    structDef.getFields().add(field2);
                    FieldDef field3 = new FieldDef();
                    field3.setId(30);
                    field3.setMetadata(errorCode_metadata);
                    field3.getType().setId(BondDataType.BT_WSTRING);
                    structDef.getFields().add(field3);
                    FieldDef field4 = new FieldDef();
                    field4.setId(40);
                    field4.setMetadata(pageName_metadata);
                    field4.getType().setId(BondDataType.BT_WSTRING);
                    structDef.getFields().add(field4);
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
                return this.errorName;
            case 20:
                return this.errorText;
            case 30:
                return this.errorCode;
            case MotionEventCompat.AXIS_GENERIC_9:
                return this.pageName;
            default:
                return null;
        }
    }

    public void setField(FieldDef fieldDef, Object value) {
        switch (fieldDef.getId()) {
            case 10:
                this.errorName = (String) value;
                return;
            case 20:
                this.errorText = (String) value;
                return;
            case 30:
                this.errorCode = (String) value;
                return;
            case MotionEventCompat.AXIS_GENERIC_9:
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
        reset("ServiceError", "com.microsoft.xbox.idp.telemetry.utc.ServiceError");
    }

    /* access modifiers changed from: protected */
    public void reset(String name, String qualifiedName) {
        super.reset(name, qualifiedName);
        this.errorName = "";
        this.errorText = "";
        this.errorCode = "";
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
            this.errorName = reader.readWString();
        }
        if (!canOmitFields || !reader.readFieldOmitted()) {
            this.errorText = reader.readWString();
        }
        if (!canOmitFields || !reader.readFieldOmitted()) {
            this.errorCode = reader.readWString();
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
                        this.errorName = ReadHelper.readWString(reader, fieldTag.type);
                        break;
                    case 20:
                        this.errorText = ReadHelper.readWString(reader, fieldTag.type);
                        break;
                    case 30:
                        this.errorCode = ReadHelper.readWString(reader, fieldTag.type);
                        break;
                    case MotionEventCompat.AXIS_GENERIC_9:
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
        boolean canOmitFields = writer.hasCapability(ProtocolCapability.CAN_OMIT_FIELDS);
        writer.writeStructBegin(Schema.metadata, isBase);
        super.writeNested(writer, true);
        writer.writeFieldBegin(BondDataType.BT_WSTRING, 10, Schema.errorName_metadata);
        writer.writeWString(this.errorName);
        writer.writeFieldEnd();
        if (!canOmitFields || this.errorText != Schema.errorText_metadata.getDefault_value().getWstring_value()) {
            writer.writeFieldBegin(BondDataType.BT_WSTRING, 20, Schema.errorText_metadata);
            writer.writeWString(this.errorText);
            writer.writeFieldEnd();
        } else {
            writer.writeFieldOmitted(BondDataType.BT_WSTRING, 20, Schema.errorText_metadata);
        }
        if (!canOmitFields || this.errorCode != Schema.errorCode_metadata.getDefault_value().getWstring_value()) {
            writer.writeFieldBegin(BondDataType.BT_WSTRING, 30, Schema.errorCode_metadata);
            writer.writeWString(this.errorCode);
            writer.writeFieldEnd();
        } else {
            writer.writeFieldOmitted(BondDataType.BT_WSTRING, 30, Schema.errorCode_metadata);
        }
        if (!canOmitFields || this.pageName != Schema.pageName_metadata.getDefault_value().getWstring_value()) {
            writer.writeFieldBegin(BondDataType.BT_WSTRING, 40, Schema.pageName_metadata);
            writer.writeWString(this.pageName);
            writer.writeFieldEnd();
        } else {
            writer.writeFieldOmitted(BondDataType.BT_WSTRING, 40, Schema.pageName_metadata);
        }
        writer.writeStructEnd(isBase);
    }

    public boolean memberwiseCompare(Object obj) {
        if (obj == null) {
            return false;
        }
        ServiceError that = (ServiceError) obj;
        if (!memberwiseCompareQuick(that) || !memberwiseCompareDeep(that)) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:17:0x0021  */
    /* JADX WARNING: Removed duplicated region for block: B:19:0x0024  */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x0037  */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x003a  */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x004d  */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x0050  */
    /* JADX WARNING: Removed duplicated region for block: B:56:0x0063  */
    /* JADX WARNING: Removed duplicated region for block: B:63:0x007b  */
    /* JADX WARNING: Removed duplicated region for block: B:69:0x0091  */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x00a7  */
    /* JADX WARNING: Removed duplicated region for block: B:81:0x00bd  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean memberwiseCompareQuick(com.microsoft.xbox.idp.telemetry.utc.ServiceError r6) {
        /*
            r5 = this;
            r2 = 1
            r3 = 0
            r0 = 1
            if (r0 == 0) goto L_0x0065
            boolean r1 = super.memberwiseCompareQuick(r6)
            if (r1 == 0) goto L_0x0065
            r0 = r2
        L_0x000c:
            if (r0 == 0) goto L_0x006b
            java.lang.String r1 = r5.errorName
            if (r1 != 0) goto L_0x0067
            r1 = r2
        L_0x0013:
            java.lang.String r4 = r6.errorName
            if (r4 != 0) goto L_0x0069
            r4 = r2
        L_0x0018:
            if (r1 != r4) goto L_0x006b
            r0 = r2
        L_0x001b:
            if (r0 == 0) goto L_0x007b
            java.lang.String r1 = r5.errorName
            if (r1 != 0) goto L_0x006d
        L_0x0021:
            r0 = r2
        L_0x0022:
            if (r0 == 0) goto L_0x0081
            java.lang.String r1 = r5.errorText
            if (r1 != 0) goto L_0x007d
            r1 = r2
        L_0x0029:
            java.lang.String r4 = r6.errorText
            if (r4 != 0) goto L_0x007f
            r4 = r2
        L_0x002e:
            if (r1 != r4) goto L_0x0081
            r0 = r2
        L_0x0031:
            if (r0 == 0) goto L_0x0091
            java.lang.String r1 = r5.errorText
            if (r1 != 0) goto L_0x0083
        L_0x0037:
            r0 = r2
        L_0x0038:
            if (r0 == 0) goto L_0x0097
            java.lang.String r1 = r5.errorCode
            if (r1 != 0) goto L_0x0093
            r1 = r2
        L_0x003f:
            java.lang.String r4 = r6.errorCode
            if (r4 != 0) goto L_0x0095
            r4 = r2
        L_0x0044:
            if (r1 != r4) goto L_0x0097
            r0 = r2
        L_0x0047:
            if (r0 == 0) goto L_0x00a7
            java.lang.String r1 = r5.errorCode
            if (r1 != 0) goto L_0x0099
        L_0x004d:
            r0 = r2
        L_0x004e:
            if (r0 == 0) goto L_0x00ad
            java.lang.String r1 = r5.pageName
            if (r1 != 0) goto L_0x00a9
            r1 = r2
        L_0x0055:
            java.lang.String r4 = r6.pageName
            if (r4 != 0) goto L_0x00ab
            r4 = r2
        L_0x005a:
            if (r1 != r4) goto L_0x00ad
            r0 = r2
        L_0x005d:
            if (r0 == 0) goto L_0x00bd
            java.lang.String r1 = r5.pageName
            if (r1 != 0) goto L_0x00af
        L_0x0063:
            r0 = r2
        L_0x0064:
            return r0
        L_0x0065:
            r0 = r3
            goto L_0x000c
        L_0x0067:
            r1 = r3
            goto L_0x0013
        L_0x0069:
            r4 = r3
            goto L_0x0018
        L_0x006b:
            r0 = r3
            goto L_0x001b
        L_0x006d:
            java.lang.String r1 = r5.errorName
            int r1 = r1.length()
            java.lang.String r4 = r6.errorName
            int r4 = r4.length()
            if (r1 == r4) goto L_0x0021
        L_0x007b:
            r0 = r3
            goto L_0x0022
        L_0x007d:
            r1 = r3
            goto L_0x0029
        L_0x007f:
            r4 = r3
            goto L_0x002e
        L_0x0081:
            r0 = r3
            goto L_0x0031
        L_0x0083:
            java.lang.String r1 = r5.errorText
            int r1 = r1.length()
            java.lang.String r4 = r6.errorText
            int r4 = r4.length()
            if (r1 == r4) goto L_0x0037
        L_0x0091:
            r0 = r3
            goto L_0x0038
        L_0x0093:
            r1 = r3
            goto L_0x003f
        L_0x0095:
            r4 = r3
            goto L_0x0044
        L_0x0097:
            r0 = r3
            goto L_0x0047
        L_0x0099:
            java.lang.String r1 = r5.errorCode
            int r1 = r1.length()
            java.lang.String r4 = r6.errorCode
            int r4 = r4.length()
            if (r1 == r4) goto L_0x004d
        L_0x00a7:
            r0 = r3
            goto L_0x004e
        L_0x00a9:
            r1 = r3
            goto L_0x0055
        L_0x00ab:
            r4 = r3
            goto L_0x005a
        L_0x00ad:
            r0 = r3
            goto L_0x005d
        L_0x00af:
            java.lang.String r1 = r5.pageName
            int r1 = r1.length()
            java.lang.String r4 = r6.pageName
            int r4 = r4.length()
            if (r1 == r4) goto L_0x0063
        L_0x00bd:
            r0 = r3
            goto L_0x0064
        */
        throw new UnsupportedOperationException("Method not decompiled: com.microsoft.xbox.idp.telemetry.utc.ServiceError.memberwiseCompareQuick(com.microsoft.xbox.idp.telemetry.utc.ServiceError):boolean");
    }

    /* access modifiers changed from: protected */
    public boolean memberwiseCompareDeep(ServiceError that) {
        return ((((1 != 0 && super.memberwiseCompareDeep(that)) && (this.errorName == null || this.errorName.equals(that.errorName))) && (this.errorText == null || this.errorText.equals(that.errorText))) && (this.errorCode == null || this.errorCode.equals(that.errorCode))) && (this.pageName == null || this.pageName.equals(that.pageName));
    }
}
