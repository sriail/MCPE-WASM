package Microsoft.Telemetry.Extensions;

import Microsoft.Telemetry.Extension;
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

public class sll extends Extension {
    private TracingEventLevel level;
    private String libVer;

    public BondSerializable clone() {
        return null;
    }

    public final String getLibVer() {
        return this.libVer;
    }

    public final void setLibVer(String value) {
        this.libVer = value;
    }

    public final TracingEventLevel getLevel() {
        return this.level;
    }

    public final void setLevel(TracingEventLevel value) {
        this.level = value;
    }

    public static class Schema {
        /* access modifiers changed from: private */
        public static final Metadata level_metadata = new Metadata();
        /* access modifiers changed from: private */
        public static final Metadata libVer_metadata = new Metadata();
        public static final Metadata metadata = new Metadata();
        public static final SchemaDef schemaDef = new SchemaDef();

        static {
            metadata.setName("sll");
            metadata.setQualified_name("Microsoft.Telemetry.Extensions.sll");
            metadata.getAttributes().put("Description", "Describes the fields related to a service logging library implementation.");
            libVer_metadata.setName("libVer");
            libVer_metadata.getAttributes().put("Description", "Service Logging Library version");
            level_metadata.setName("level");
            level_metadata.setModifier(Modifier.Required);
            level_metadata.getAttributes().put("Description", "Severity level for service event");
            level_metadata.getDefault_value().setInt_value((long) TracingEventLevel.None.getValue());
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
                    field.setMetadata(libVer_metadata);
                    field.getType().setId(BondDataType.BT_STRING);
                    structDef.getFields().add(field);
                    FieldDef field2 = new FieldDef();
                    field2.setId(20);
                    field2.setMetadata(level_metadata);
                    field2.getType().setId(BondDataType.BT_INT32);
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
                return this.libVer;
            case 20:
                return this.level;
            default:
                return null;
        }
    }

    public void setField(FieldDef fieldDef, Object value) {
        switch (fieldDef.getId()) {
            case 10:
                this.libVer = (String) value;
                return;
            case 20:
                this.level = (TracingEventLevel) value;
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
        reset("sll", "Microsoft.Telemetry.Extensions.sll");
    }

    /* access modifiers changed from: protected */
    public void reset(String name, String qualifiedName) {
        super.reset(name, qualifiedName);
        this.libVer = "";
        this.level = TracingEventLevel.None;
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
            this.libVer = reader.readString();
        }
        if (!canOmitFields || !reader.readFieldOmitted()) {
            this.level = TracingEventLevel.fromValue(reader.readInt32());
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
                        this.libVer = ReadHelper.readString(reader, fieldTag.type);
                        break;
                    case 20:
                        this.level = TracingEventLevel.fromValue(ReadHelper.readInt32(reader, fieldTag.type));
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
        if (!canOmitFields || this.libVer != Schema.libVer_metadata.getDefault_value().getString_value()) {
            writer.writeFieldBegin(BondDataType.BT_STRING, 10, Schema.libVer_metadata);
            writer.writeString(this.libVer);
            writer.writeFieldEnd();
        } else {
            writer.writeFieldOmitted(BondDataType.BT_STRING, 10, Schema.libVer_metadata);
        }
        writer.writeFieldBegin(BondDataType.BT_INT32, 20, Schema.level_metadata);
        writer.writeInt32(this.level.getValue());
        writer.writeFieldEnd();
        writer.writeStructEnd(isBase);
    }

    public boolean memberwiseCompare(Object obj) {
        if (obj == null) {
            return false;
        }
        sll that = (sll) obj;
        if (!memberwiseCompareQuick(that) || !memberwiseCompareDeep(that)) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:17:0x0021  */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x002a  */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x0042  */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x0044  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean memberwiseCompareQuick(Microsoft.Telemetry.Extensions.sll r6) {
        /*
            r5 = this;
            r2 = 1
            r3 = 0
            r0 = 1
            if (r0 == 0) goto L_0x002c
            boolean r1 = super.memberwiseCompareQuick(r6)
            if (r1 == 0) goto L_0x002c
            r0 = r2
        L_0x000c:
            if (r0 == 0) goto L_0x0032
            java.lang.String r1 = r5.libVer
            if (r1 != 0) goto L_0x002e
            r1 = r2
        L_0x0013:
            java.lang.String r4 = r6.libVer
            if (r4 != 0) goto L_0x0030
            r4 = r2
        L_0x0018:
            if (r1 != r4) goto L_0x0032
            r0 = r2
        L_0x001b:
            if (r0 == 0) goto L_0x0042
            java.lang.String r1 = r5.libVer
            if (r1 != 0) goto L_0x0034
        L_0x0021:
            r0 = r2
        L_0x0022:
            if (r0 == 0) goto L_0x0044
            Microsoft.Telemetry.Extensions.TracingEventLevel r1 = r5.level
            Microsoft.Telemetry.Extensions.TracingEventLevel r4 = r6.level
            if (r1 != r4) goto L_0x0044
            r0 = r2
        L_0x002b:
            return r0
        L_0x002c:
            r0 = r3
            goto L_0x000c
        L_0x002e:
            r1 = r3
            goto L_0x0013
        L_0x0030:
            r4 = r3
            goto L_0x0018
        L_0x0032:
            r0 = r3
            goto L_0x001b
        L_0x0034:
            java.lang.String r1 = r5.libVer
            int r1 = r1.length()
            java.lang.String r4 = r6.libVer
            int r4 = r4.length()
            if (r1 == r4) goto L_0x0021
        L_0x0042:
            r0 = r3
            goto L_0x0022
        L_0x0044:
            r0 = r3
            goto L_0x002b
        */
        throw new UnsupportedOperationException("Method not decompiled: Microsoft.Telemetry.Extensions.sll.memberwiseCompareQuick(Microsoft.Telemetry.Extensions.sll):boolean");
    }

    /* access modifiers changed from: protected */
    public boolean memberwiseCompareDeep(sll that) {
        boolean equals;
        if (1 == 0 || !super.memberwiseCompareDeep(that)) {
            equals = false;
        } else {
            equals = true;
        }
        return equals && (this.libVer == null || this.libVer.equals(that.libVer));
    }
}
