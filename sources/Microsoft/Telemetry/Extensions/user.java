package Microsoft.Telemetry.Extensions;

import Microsoft.Telemetry.Extension;
import com.microsoft.bond.BondDataType;
import com.microsoft.bond.BondMirror;
import com.microsoft.bond.BondSerializable;
import com.microsoft.bond.FieldDef;
import com.microsoft.bond.Metadata;
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

public class user extends Extension {
    private String authId;
    private String id;
    private String localId;

    public BondSerializable clone() {
        return null;
    }

    public final String getId() {
        return this.id;
    }

    public final void setId(String value) {
        this.id = value;
    }

    public final String getLocalId() {
        return this.localId;
    }

    public final void setLocalId(String value) {
        this.localId = value;
    }

    public final String getAuthId() {
        return this.authId;
    }

    public final void setAuthId(String value) {
        this.authId = value;
    }

    public static class Schema {
        /* access modifiers changed from: private */
        public static final Metadata authId_metadata = new Metadata();
        /* access modifiers changed from: private */
        public static final Metadata id_metadata = new Metadata();
        /* access modifiers changed from: private */
        public static final Metadata localId_metadata = new Metadata();
        public static final Metadata metadata = new Metadata();
        public static final SchemaDef schemaDef = new SchemaDef();

        static {
            metadata.setName("user");
            metadata.setQualified_name("Microsoft.Telemetry.Extensions.user");
            metadata.getAttributes().put("Description", "Describes the User related fields. See https://osgwiki.com/wiki/CommonSchema/user_id");
            id_metadata.setName("id");
            id_metadata.getAttributes().put("Description", "Unique user Id. Clients aren't expected to set this; instead the service will decide the best ID to use here. Clients may set this if they believe they have the best user ID already. Format is <NamespaceIdentifier>:<Id> for example, x:12345678.");
            localId_metadata.setName("localId");
            localId_metadata.getAttributes().put("Description", "Local user identifier according to the client. Format is <NamespaceIdentifier>:<Id> for example, x:12345678.");
            authId_metadata.setName("authId");
            authId_metadata.getAttributes().put("Description", "This is the ID of the user associated with this event, deduced from a token such as an MSA ticket or Xbox xtoken.");
            authId_metadata.getAttributes().put("Name", "UserAuthId");
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
                    structDef.setBase_def(Extension.Schema.getTypeDef(schema));
                    FieldDef field = new FieldDef();
                    field.setId(10);
                    field.setMetadata(id_metadata);
                    field.getType().setId(BondDataType.BT_STRING);
                    structDef.getFields().add(field);
                    FieldDef field2 = new FieldDef();
                    field2.setId(20);
                    field2.setMetadata(localId_metadata);
                    field2.getType().setId(BondDataType.BT_STRING);
                    structDef.getFields().add(field2);
                    FieldDef field3 = new FieldDef();
                    field3.setId(30);
                    field3.setMetadata(authId_metadata);
                    field3.getType().setId(BondDataType.BT_STRING);
                    structDef.getFields().add(field3);
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
                return this.id;
            case 20:
                return this.localId;
            case 30:
                return this.authId;
            default:
                return null;
        }
    }

    public void setField(FieldDef fieldDef, Object value) {
        switch (fieldDef.getId()) {
            case 10:
                this.id = (String) value;
                return;
            case 20:
                this.localId = (String) value;
                return;
            case 30:
                this.authId = (String) value;
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
        reset("user", "Microsoft.Telemetry.Extensions.user");
    }

    /* access modifiers changed from: protected */
    public void reset(String name, String qualifiedName) {
        super.reset(name, qualifiedName);
        this.id = "";
        this.localId = "";
        this.authId = "";
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
            this.id = reader.readString();
        }
        if (!canOmitFields || !reader.readFieldOmitted()) {
            this.localId = reader.readString();
        }
        if (!canOmitFields || !reader.readFieldOmitted()) {
            this.authId = reader.readString();
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
                        this.id = ReadHelper.readString(reader, fieldTag.type);
                        break;
                    case 20:
                        this.localId = ReadHelper.readString(reader, fieldTag.type);
                        break;
                    case 30:
                        this.authId = ReadHelper.readString(reader, fieldTag.type);
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
        if (!canOmitFields || this.id != Schema.id_metadata.getDefault_value().getString_value()) {
            writer.writeFieldBegin(BondDataType.BT_STRING, 10, Schema.id_metadata);
            writer.writeString(this.id);
            writer.writeFieldEnd();
        } else {
            writer.writeFieldOmitted(BondDataType.BT_STRING, 10, Schema.id_metadata);
        }
        if (!canOmitFields || this.localId != Schema.localId_metadata.getDefault_value().getString_value()) {
            writer.writeFieldBegin(BondDataType.BT_STRING, 20, Schema.localId_metadata);
            writer.writeString(this.localId);
            writer.writeFieldEnd();
        } else {
            writer.writeFieldOmitted(BondDataType.BT_STRING, 20, Schema.localId_metadata);
        }
        if (!canOmitFields || this.authId != Schema.authId_metadata.getDefault_value().getString_value()) {
            writer.writeFieldBegin(BondDataType.BT_STRING, 30, Schema.authId_metadata);
            writer.writeString(this.authId);
            writer.writeFieldEnd();
        } else {
            writer.writeFieldOmitted(BondDataType.BT_STRING, 30, Schema.authId_metadata);
        }
        writer.writeStructEnd(isBase);
    }

    public boolean memberwiseCompare(Object obj) {
        if (obj == null) {
            return false;
        }
        user that = (user) obj;
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
    /* JADX WARNING: Removed duplicated region for block: B:50:0x0065  */
    /* JADX WARNING: Removed duplicated region for block: B:56:0x007b  */
    /* JADX WARNING: Removed duplicated region for block: B:62:0x0091  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean memberwiseCompareQuick(Microsoft.Telemetry.Extensions.user r6) {
        /*
            r5 = this;
            r2 = 1
            r3 = 0
            r0 = 1
            if (r0 == 0) goto L_0x004f
            boolean r1 = super.memberwiseCompareQuick(r6)
            if (r1 == 0) goto L_0x004f
            r0 = r2
        L_0x000c:
            if (r0 == 0) goto L_0x0055
            java.lang.String r1 = r5.id
            if (r1 != 0) goto L_0x0051
            r1 = r2
        L_0x0013:
            java.lang.String r4 = r6.id
            if (r4 != 0) goto L_0x0053
            r4 = r2
        L_0x0018:
            if (r1 != r4) goto L_0x0055
            r0 = r2
        L_0x001b:
            if (r0 == 0) goto L_0x0065
            java.lang.String r1 = r5.id
            if (r1 != 0) goto L_0x0057
        L_0x0021:
            r0 = r2
        L_0x0022:
            if (r0 == 0) goto L_0x006b
            java.lang.String r1 = r5.localId
            if (r1 != 0) goto L_0x0067
            r1 = r2
        L_0x0029:
            java.lang.String r4 = r6.localId
            if (r4 != 0) goto L_0x0069
            r4 = r2
        L_0x002e:
            if (r1 != r4) goto L_0x006b
            r0 = r2
        L_0x0031:
            if (r0 == 0) goto L_0x007b
            java.lang.String r1 = r5.localId
            if (r1 != 0) goto L_0x006d
        L_0x0037:
            r0 = r2
        L_0x0038:
            if (r0 == 0) goto L_0x0081
            java.lang.String r1 = r5.authId
            if (r1 != 0) goto L_0x007d
            r1 = r2
        L_0x003f:
            java.lang.String r4 = r6.authId
            if (r4 != 0) goto L_0x007f
            r4 = r2
        L_0x0044:
            if (r1 != r4) goto L_0x0081
            r0 = r2
        L_0x0047:
            if (r0 == 0) goto L_0x0091
            java.lang.String r1 = r5.authId
            if (r1 != 0) goto L_0x0083
        L_0x004d:
            r0 = r2
        L_0x004e:
            return r0
        L_0x004f:
            r0 = r3
            goto L_0x000c
        L_0x0051:
            r1 = r3
            goto L_0x0013
        L_0x0053:
            r4 = r3
            goto L_0x0018
        L_0x0055:
            r0 = r3
            goto L_0x001b
        L_0x0057:
            java.lang.String r1 = r5.id
            int r1 = r1.length()
            java.lang.String r4 = r6.id
            int r4 = r4.length()
            if (r1 == r4) goto L_0x0021
        L_0x0065:
            r0 = r3
            goto L_0x0022
        L_0x0067:
            r1 = r3
            goto L_0x0029
        L_0x0069:
            r4 = r3
            goto L_0x002e
        L_0x006b:
            r0 = r3
            goto L_0x0031
        L_0x006d:
            java.lang.String r1 = r5.localId
            int r1 = r1.length()
            java.lang.String r4 = r6.localId
            int r4 = r4.length()
            if (r1 == r4) goto L_0x0037
        L_0x007b:
            r0 = r3
            goto L_0x0038
        L_0x007d:
            r1 = r3
            goto L_0x003f
        L_0x007f:
            r4 = r3
            goto L_0x0044
        L_0x0081:
            r0 = r3
            goto L_0x0047
        L_0x0083:
            java.lang.String r1 = r5.authId
            int r1 = r1.length()
            java.lang.String r4 = r6.authId
            int r4 = r4.length()
            if (r1 == r4) goto L_0x004d
        L_0x0091:
            r0 = r3
            goto L_0x004e
        */
        throw new UnsupportedOperationException("Method not decompiled: Microsoft.Telemetry.Extensions.user.memberwiseCompareQuick(Microsoft.Telemetry.Extensions.user):boolean");
    }

    /* access modifiers changed from: protected */
    public boolean memberwiseCompareDeep(user that) {
        boolean equals;
        if (1 == 0 || !super.memberwiseCompareDeep(that)) {
            equals = false;
        } else {
            equals = true;
        }
        return ((equals && (this.id == null || this.id.equals(that.id))) && (this.localId == null || this.localId.equals(that.localId))) && (this.authId == null || this.authId.equals(that.authId));
    }
}
