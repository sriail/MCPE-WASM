package Microsoft.Telemetry.Extensions;

import Microsoft.Telemetry.Extension;
import android.support.v4.view.MotionEventCompat;
import com.facebook.GraphRequest;
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

public class utc extends Extension {
    private String aId;
    private long cat;
    private long flags;
    private String op;
    private String raId;
    private String sqmId;
    private String stId;

    public BondSerializable clone() {
        return null;
    }

    public final String getStId() {
        return this.stId;
    }

    public final void setStId(String value) {
        this.stId = value;
    }

    public final String getAId() {
        return this.aId;
    }

    public final void setAId(String value) {
        this.aId = value;
    }

    public final String getRaId() {
        return this.raId;
    }

    public final void setRaId(String value) {
        this.raId = value;
    }

    public final String getOp() {
        return this.op;
    }

    public final void setOp(String value) {
        this.op = value;
    }

    public final long getCat() {
        return this.cat;
    }

    public final void setCat(long value) {
        this.cat = value;
    }

    public final long getFlags() {
        return this.flags;
    }

    public final void setFlags(long value) {
        this.flags = value;
    }

    public final String getSqmId() {
        return this.sqmId;
    }

    public final void setSqmId(String value) {
        this.sqmId = value;
    }

    public static class Schema {
        /* access modifiers changed from: private */
        public static final Metadata aId_metadata = new Metadata();
        /* access modifiers changed from: private */
        public static final Metadata cat_metadata = new Metadata();
        /* access modifiers changed from: private */
        public static final Metadata flags_metadata = new Metadata();
        public static final Metadata metadata = new Metadata();
        /* access modifiers changed from: private */
        public static final Metadata op_metadata = new Metadata();
        /* access modifiers changed from: private */
        public static final Metadata raId_metadata = new Metadata();
        public static final SchemaDef schemaDef = new SchemaDef();
        /* access modifiers changed from: private */
        public static final Metadata sqmId_metadata = new Metadata();
        /* access modifiers changed from: private */
        public static final Metadata stId_metadata = new Metadata();

        static {
            metadata.setName("utc");
            metadata.setQualified_name("Microsoft.Telemetry.Extensions.utc");
            metadata.getAttributes().put("Description", "Describes the properties that might be populated by a logging library on Windows.");
            stId_metadata.setName("stId");
            stId_metadata.getAttributes().put("Description", "Used for UTC scenarios.");
            aId_metadata.setName("aId");
            aId_metadata.getAttributes().put("Description", "Activity Id in ETW (event tracing for windows).");
            raId_metadata.setName("raId");
            raId_metadata.getAttributes().put("Description", "Related Activity Id in ETW.");
            op_metadata.setName("op");
            op_metadata.getAttributes().put("Description", "Op Code in ETW.");
            cat_metadata.setName("cat");
            cat_metadata.getAttributes().put("Description", "Categories.");
            cat_metadata.getDefault_value().setInt_value(0);
            flags_metadata.setName("flags");
            flags_metadata.getAttributes().put("Description", "This captures the characteristics of the traffic. Examples: isTest, isInternal.");
            flags_metadata.getDefault_value().setInt_value(0);
            sqmId_metadata.setName("sqmId");
            sqmId_metadata.getAttributes().put("Description", "The Windows SQM device ID.");
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
                    field.setMetadata(stId_metadata);
                    field.getType().setId(BondDataType.BT_STRING);
                    structDef.getFields().add(field);
                    FieldDef field2 = new FieldDef();
                    field2.setId(20);
                    field2.setMetadata(aId_metadata);
                    field2.getType().setId(BondDataType.BT_STRING);
                    structDef.getFields().add(field2);
                    FieldDef field3 = new FieldDef();
                    field3.setId(30);
                    field3.setMetadata(raId_metadata);
                    field3.getType().setId(BondDataType.BT_STRING);
                    structDef.getFields().add(field3);
                    FieldDef field4 = new FieldDef();
                    field4.setId(40);
                    field4.setMetadata(op_metadata);
                    field4.getType().setId(BondDataType.BT_STRING);
                    structDef.getFields().add(field4);
                    FieldDef field5 = new FieldDef();
                    field5.setId(50);
                    field5.setMetadata(cat_metadata);
                    field5.getType().setId(BondDataType.BT_INT64);
                    structDef.getFields().add(field5);
                    FieldDef field6 = new FieldDef();
                    field6.setId(60);
                    field6.setMetadata(flags_metadata);
                    field6.getType().setId(BondDataType.BT_INT64);
                    structDef.getFields().add(field6);
                    FieldDef field7 = new FieldDef();
                    field7.setId(70);
                    field7.setMetadata(sqmId_metadata);
                    field7.getType().setId(BondDataType.BT_STRING);
                    structDef.getFields().add(field7);
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
                return this.stId;
            case 20:
                return this.aId;
            case 30:
                return this.raId;
            case MotionEventCompat.AXIS_GENERIC_9 /*40*/:
                return this.op;
            case GraphRequest.MAXIMUM_BATCH_SIZE /*50*/:
                return Long.valueOf(this.cat);
            case 60:
                return Long.valueOf(this.flags);
            case 70:
                return this.sqmId;
            default:
                return null;
        }
    }

    public void setField(FieldDef fieldDef, Object value) {
        switch (fieldDef.getId()) {
            case 10:
                this.stId = (String) value;
                return;
            case 20:
                this.aId = (String) value;
                return;
            case 30:
                this.raId = (String) value;
                return;
            case MotionEventCompat.AXIS_GENERIC_9 /*40*/:
                this.op = (String) value;
                return;
            case GraphRequest.MAXIMUM_BATCH_SIZE /*50*/:
                this.cat = ((Long) value).longValue();
                return;
            case 60:
                this.flags = ((Long) value).longValue();
                return;
            case 70:
                this.sqmId = (String) value;
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
        reset("utc", "Microsoft.Telemetry.Extensions.utc");
    }

    /* access modifiers changed from: protected */
    public void reset(String name, String qualifiedName) {
        super.reset(name, qualifiedName);
        this.stId = "";
        this.aId = "";
        this.raId = "";
        this.op = "";
        this.cat = 0;
        this.flags = 0;
        this.sqmId = "";
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
            this.stId = reader.readString();
        }
        if (!canOmitFields || !reader.readFieldOmitted()) {
            this.aId = reader.readString();
        }
        if (!canOmitFields || !reader.readFieldOmitted()) {
            this.raId = reader.readString();
        }
        if (!canOmitFields || !reader.readFieldOmitted()) {
            this.op = reader.readString();
        }
        if (!canOmitFields || !reader.readFieldOmitted()) {
            this.cat = reader.readInt64();
        }
        if (!canOmitFields || !reader.readFieldOmitted()) {
            this.flags = reader.readInt64();
        }
        if (!canOmitFields || !reader.readFieldOmitted()) {
            this.sqmId = reader.readString();
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
                        this.stId = ReadHelper.readString(reader, fieldTag.type);
                        break;
                    case 20:
                        this.aId = ReadHelper.readString(reader, fieldTag.type);
                        break;
                    case 30:
                        this.raId = ReadHelper.readString(reader, fieldTag.type);
                        break;
                    case MotionEventCompat.AXIS_GENERIC_9 /*40*/:
                        this.op = ReadHelper.readString(reader, fieldTag.type);
                        break;
                    case GraphRequest.MAXIMUM_BATCH_SIZE /*50*/:
                        this.cat = ReadHelper.readInt64(reader, fieldTag.type);
                        break;
                    case 60:
                        this.flags = ReadHelper.readInt64(reader, fieldTag.type);
                        break;
                    case 70:
                        this.sqmId = ReadHelper.readString(reader, fieldTag.type);
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
        if (!canOmitFields || this.stId != Schema.stId_metadata.getDefault_value().getString_value()) {
            writer.writeFieldBegin(BondDataType.BT_STRING, 10, Schema.stId_metadata);
            writer.writeString(this.stId);
            writer.writeFieldEnd();
        } else {
            writer.writeFieldOmitted(BondDataType.BT_STRING, 10, Schema.stId_metadata);
        }
        if (!canOmitFields || this.aId != Schema.aId_metadata.getDefault_value().getString_value()) {
            writer.writeFieldBegin(BondDataType.BT_STRING, 20, Schema.aId_metadata);
            writer.writeString(this.aId);
            writer.writeFieldEnd();
        } else {
            writer.writeFieldOmitted(BondDataType.BT_STRING, 20, Schema.aId_metadata);
        }
        if (!canOmitFields || this.raId != Schema.raId_metadata.getDefault_value().getString_value()) {
            writer.writeFieldBegin(BondDataType.BT_STRING, 30, Schema.raId_metadata);
            writer.writeString(this.raId);
            writer.writeFieldEnd();
        } else {
            writer.writeFieldOmitted(BondDataType.BT_STRING, 30, Schema.raId_metadata);
        }
        if (!canOmitFields || this.op != Schema.op_metadata.getDefault_value().getString_value()) {
            writer.writeFieldBegin(BondDataType.BT_STRING, 40, Schema.op_metadata);
            writer.writeString(this.op);
            writer.writeFieldEnd();
        } else {
            writer.writeFieldOmitted(BondDataType.BT_STRING, 40, Schema.op_metadata);
        }
        if (!canOmitFields || this.cat != Schema.cat_metadata.getDefault_value().getInt_value()) {
            writer.writeFieldBegin(BondDataType.BT_INT64, 50, Schema.cat_metadata);
            writer.writeInt64(this.cat);
            writer.writeFieldEnd();
        } else {
            writer.writeFieldOmitted(BondDataType.BT_INT64, 50, Schema.cat_metadata);
        }
        if (!canOmitFields || this.flags != Schema.flags_metadata.getDefault_value().getInt_value()) {
            writer.writeFieldBegin(BondDataType.BT_INT64, 60, Schema.flags_metadata);
            writer.writeInt64(this.flags);
            writer.writeFieldEnd();
        } else {
            writer.writeFieldOmitted(BondDataType.BT_INT64, 60, Schema.flags_metadata);
        }
        if (!canOmitFields || this.sqmId != Schema.sqmId_metadata.getDefault_value().getString_value()) {
            writer.writeFieldBegin(BondDataType.BT_STRING, 70, Schema.sqmId_metadata);
            writer.writeString(this.sqmId);
            writer.writeFieldEnd();
        } else {
            writer.writeFieldOmitted(BondDataType.BT_STRING, 70, Schema.sqmId_metadata);
        }
        writer.writeStructEnd(isBase);
    }

    public boolean memberwiseCompare(Object obj) {
        if (obj == null) {
            return false;
        }
        utc that = (utc) obj;
        if (!memberwiseCompareQuick(that) || !memberwiseCompareDeep(that)) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:102:0x00f7  */
    /* JADX WARNING: Removed duplicated region for block: B:103:0x00fa  */
    /* JADX WARNING: Removed duplicated region for block: B:104:0x00fd  */
    /* JADX WARNING: Removed duplicated region for block: B:110:0x0114  */
    /* JADX WARNING: Removed duplicated region for block: B:17:0x0021  */
    /* JADX WARNING: Removed duplicated region for block: B:19:0x0024  */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x0037  */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x003a  */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x004d  */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x0050  */
    /* JADX WARNING: Removed duplicated region for block: B:56:0x0063  */
    /* JADX WARNING: Removed duplicated region for block: B:60:0x006e  */
    /* JADX WARNING: Removed duplicated region for block: B:64:0x0079  */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x007c  */
    /* JADX WARNING: Removed duplicated region for block: B:77:0x008f  */
    /* JADX WARNING: Removed duplicated region for block: B:84:0x00a9  */
    /* JADX WARNING: Removed duplicated region for block: B:90:0x00c3  */
    /* JADX WARNING: Removed duplicated region for block: B:96:0x00dd  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean memberwiseCompareQuick(Microsoft.Telemetry.Extensions.utc r9) {
        /*
            r8 = this;
            r2 = 1
            r3 = 0
            r0 = 1
            if (r0 == 0) goto L_0x0091
            boolean r1 = super.memberwiseCompareQuick(r9)
            if (r1 == 0) goto L_0x0091
            r0 = r2
        L_0x000c:
            if (r0 == 0) goto L_0x0099
            java.lang.String r1 = r8.stId
            if (r1 != 0) goto L_0x0094
            r1 = r2
        L_0x0013:
            java.lang.String r4 = r9.stId
            if (r4 != 0) goto L_0x0097
            r4 = r2
        L_0x0018:
            if (r1 != r4) goto L_0x0099
            r0 = r2
        L_0x001b:
            if (r0 == 0) goto L_0x00a9
            java.lang.String r1 = r8.stId
            if (r1 != 0) goto L_0x009b
        L_0x0021:
            r0 = r2
        L_0x0022:
            if (r0 == 0) goto L_0x00b2
            java.lang.String r1 = r8.aId
            if (r1 != 0) goto L_0x00ac
            r1 = r2
        L_0x0029:
            java.lang.String r4 = r9.aId
            if (r4 != 0) goto L_0x00af
            r4 = r2
        L_0x002e:
            if (r1 != r4) goto L_0x00b2
            r0 = r2
        L_0x0031:
            if (r0 == 0) goto L_0x00c3
            java.lang.String r1 = r8.aId
            if (r1 != 0) goto L_0x00b5
        L_0x0037:
            r0 = r2
        L_0x0038:
            if (r0 == 0) goto L_0x00cc
            java.lang.String r1 = r8.raId
            if (r1 != 0) goto L_0x00c6
            r1 = r2
        L_0x003f:
            java.lang.String r4 = r9.raId
            if (r4 != 0) goto L_0x00c9
            r4 = r2
        L_0x0044:
            if (r1 != r4) goto L_0x00cc
            r0 = r2
        L_0x0047:
            if (r0 == 0) goto L_0x00dd
            java.lang.String r1 = r8.raId
            if (r1 != 0) goto L_0x00cf
        L_0x004d:
            r0 = r2
        L_0x004e:
            if (r0 == 0) goto L_0x00e6
            java.lang.String r1 = r8.op
            if (r1 != 0) goto L_0x00e0
            r1 = r2
        L_0x0055:
            java.lang.String r4 = r9.op
            if (r4 != 0) goto L_0x00e3
            r4 = r2
        L_0x005a:
            if (r1 != r4) goto L_0x00e6
            r0 = r2
        L_0x005d:
            if (r0 == 0) goto L_0x00f7
            java.lang.String r1 = r8.op
            if (r1 != 0) goto L_0x00e9
        L_0x0063:
            r0 = r2
        L_0x0064:
            if (r0 == 0) goto L_0x00fa
            long r4 = r8.cat
            long r6 = r9.cat
            int r1 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r1 != 0) goto L_0x00fa
            r0 = r2
        L_0x006f:
            if (r0 == 0) goto L_0x00fd
            long r4 = r8.flags
            long r6 = r9.flags
            int r1 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r1 != 0) goto L_0x00fd
            r0 = r2
        L_0x007a:
            if (r0 == 0) goto L_0x0104
            java.lang.String r1 = r8.sqmId
            if (r1 != 0) goto L_0x0100
            r1 = r2
        L_0x0081:
            java.lang.String r4 = r9.sqmId
            if (r4 != 0) goto L_0x0102
            r4 = r2
        L_0x0086:
            if (r1 != r4) goto L_0x0104
            r0 = r2
        L_0x0089:
            if (r0 == 0) goto L_0x0114
            java.lang.String r1 = r8.sqmId
            if (r1 != 0) goto L_0x0106
        L_0x008f:
            r0 = r2
        L_0x0090:
            return r0
        L_0x0091:
            r0 = r3
            goto L_0x000c
        L_0x0094:
            r1 = r3
            goto L_0x0013
        L_0x0097:
            r4 = r3
            goto L_0x0018
        L_0x0099:
            r0 = r3
            goto L_0x001b
        L_0x009b:
            java.lang.String r1 = r8.stId
            int r1 = r1.length()
            java.lang.String r4 = r9.stId
            int r4 = r4.length()
            if (r1 == r4) goto L_0x0021
        L_0x00a9:
            r0 = r3
            goto L_0x0022
        L_0x00ac:
            r1 = r3
            goto L_0x0029
        L_0x00af:
            r4 = r3
            goto L_0x002e
        L_0x00b2:
            r0 = r3
            goto L_0x0031
        L_0x00b5:
            java.lang.String r1 = r8.aId
            int r1 = r1.length()
            java.lang.String r4 = r9.aId
            int r4 = r4.length()
            if (r1 == r4) goto L_0x0037
        L_0x00c3:
            r0 = r3
            goto L_0x0038
        L_0x00c6:
            r1 = r3
            goto L_0x003f
        L_0x00c9:
            r4 = r3
            goto L_0x0044
        L_0x00cc:
            r0 = r3
            goto L_0x0047
        L_0x00cf:
            java.lang.String r1 = r8.raId
            int r1 = r1.length()
            java.lang.String r4 = r9.raId
            int r4 = r4.length()
            if (r1 == r4) goto L_0x004d
        L_0x00dd:
            r0 = r3
            goto L_0x004e
        L_0x00e0:
            r1 = r3
            goto L_0x0055
        L_0x00e3:
            r4 = r3
            goto L_0x005a
        L_0x00e6:
            r0 = r3
            goto L_0x005d
        L_0x00e9:
            java.lang.String r1 = r8.op
            int r1 = r1.length()
            java.lang.String r4 = r9.op
            int r4 = r4.length()
            if (r1 == r4) goto L_0x0063
        L_0x00f7:
            r0 = r3
            goto L_0x0064
        L_0x00fa:
            r0 = r3
            goto L_0x006f
        L_0x00fd:
            r0 = r3
            goto L_0x007a
        L_0x0100:
            r1 = r3
            goto L_0x0081
        L_0x0102:
            r4 = r3
            goto L_0x0086
        L_0x0104:
            r0 = r3
            goto L_0x0089
        L_0x0106:
            java.lang.String r1 = r8.sqmId
            int r1 = r1.length()
            java.lang.String r4 = r9.sqmId
            int r4 = r4.length()
            if (r1 == r4) goto L_0x008f
        L_0x0114:
            r0 = r3
            goto L_0x0090
        */
        throw new UnsupportedOperationException("Method not decompiled: Microsoft.Telemetry.Extensions.utc.memberwiseCompareQuick(Microsoft.Telemetry.Extensions.utc):boolean");
    }

    /* access modifiers changed from: protected */
    public boolean memberwiseCompareDeep(utc that) {
        return (((((1 != 0 && super.memberwiseCompareDeep(that)) && (this.stId == null || this.stId.equals(that.stId))) && (this.aId == null || this.aId.equals(that.aId))) && (this.raId == null || this.raId.equals(that.raId))) && (this.op == null || this.op.equals(that.op))) && (this.sqmId == null || this.sqmId.equals(that.sqmId));
    }
}
