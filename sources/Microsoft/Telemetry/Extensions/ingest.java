package Microsoft.Telemetry.Extensions;

import Microsoft.Telemetry.Extension;
import android.support.v4.view.MotionEventCompat;
import com.facebook.GraphRequest;
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

public class ingest extends Extension {
    private long auth;
    private String clientIp;
    private long quality;
    private String time;
    private String uploadTime;
    private String userAgent;

    public BondSerializable clone() {
        return null;
    }

    public final String getTime() {
        return this.time;
    }

    public final void setTime(String value) {
        this.time = value;
    }

    public final String getClientIp() {
        return this.clientIp;
    }

    public final void setClientIp(String value) {
        this.clientIp = value;
    }

    public final long getAuth() {
        return this.auth;
    }

    public final void setAuth(long value) {
        this.auth = value;
    }

    public final long getQuality() {
        return this.quality;
    }

    public final void setQuality(long value) {
        this.quality = value;
    }

    public final String getUploadTime() {
        return this.uploadTime;
    }

    public final void setUploadTime(String value) {
        this.uploadTime = value;
    }

    public final String getUserAgent() {
        return this.userAgent;
    }

    public final void setUserAgent(String value) {
        this.userAgent = value;
    }

    public static class Schema {
        /* access modifiers changed from: private */
        public static final Metadata auth_metadata = new Metadata();
        /* access modifiers changed from: private */
        public static final Metadata clientIp_metadata = new Metadata();
        public static final Metadata metadata = new Metadata();
        /* access modifiers changed from: private */
        public static final Metadata quality_metadata = new Metadata();
        public static final SchemaDef schemaDef = new SchemaDef();
        /* access modifiers changed from: private */
        public static final Metadata time_metadata = new Metadata();
        /* access modifiers changed from: private */
        public static final Metadata uploadTime_metadata = new Metadata();
        /* access modifiers changed from: private */
        public static final Metadata userAgent_metadata = new Metadata();

        static {
            metadata.setName("ingest");
            metadata.setQualified_name("Microsoft.Telemetry.Extensions.ingest");
            metadata.getAttributes().put("Description", "Describes the fields added dynamically by the service. Clients should NOT use this section since it is adding dynamically by the service.");
            time_metadata.setName("time");
            time_metadata.setModifier(Modifier.Required);
            time_metadata.getAttributes().put("Name", "IngestDateTime");
            clientIp_metadata.setName("clientIp");
            clientIp_metadata.setModifier(Modifier.Required);
            clientIp_metadata.getAttributes().put("Name", "ClientIp");
            auth_metadata.setName("auth");
            auth_metadata.getAttributes().put("Name", "DataAuthorization");
            auth_metadata.getDefault_value().setInt_value(0);
            quality_metadata.setName("quality");
            quality_metadata.getAttributes().put("Name", "DataQuality");
            quality_metadata.getDefault_value().setInt_value(0);
            uploadTime_metadata.setName("uploadTime");
            uploadTime_metadata.getAttributes().put("Name", "UploadDateTime");
            userAgent_metadata.setName("userAgent");
            userAgent_metadata.getAttributes().put("Name", "UserAgent");
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
                    field.setMetadata(time_metadata);
                    field.getType().setId(BondDataType.BT_STRING);
                    structDef.getFields().add(field);
                    FieldDef field2 = new FieldDef();
                    field2.setId(20);
                    field2.setMetadata(clientIp_metadata);
                    field2.getType().setId(BondDataType.BT_STRING);
                    structDef.getFields().add(field2);
                    FieldDef field3 = new FieldDef();
                    field3.setId(30);
                    field3.setMetadata(auth_metadata);
                    field3.getType().setId(BondDataType.BT_INT64);
                    structDef.getFields().add(field3);
                    FieldDef field4 = new FieldDef();
                    field4.setId(40);
                    field4.setMetadata(quality_metadata);
                    field4.getType().setId(BondDataType.BT_INT64);
                    structDef.getFields().add(field4);
                    FieldDef field5 = new FieldDef();
                    field5.setId(50);
                    field5.setMetadata(uploadTime_metadata);
                    field5.getType().setId(BondDataType.BT_STRING);
                    structDef.getFields().add(field5);
                    FieldDef field6 = new FieldDef();
                    field6.setId(60);
                    field6.setMetadata(userAgent_metadata);
                    field6.getType().setId(BondDataType.BT_STRING);
                    structDef.getFields().add(field6);
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
                return this.time;
            case 20:
                return this.clientIp;
            case 30:
                return Long.valueOf(this.auth);
            case MotionEventCompat.AXIS_GENERIC_9 /*40*/:
                return Long.valueOf(this.quality);
            case GraphRequest.MAXIMUM_BATCH_SIZE /*50*/:
                return this.uploadTime;
            case 60:
                return this.userAgent;
            default:
                return null;
        }
    }

    public void setField(FieldDef fieldDef, Object value) {
        switch (fieldDef.getId()) {
            case 10:
                this.time = (String) value;
                return;
            case 20:
                this.clientIp = (String) value;
                return;
            case 30:
                this.auth = ((Long) value).longValue();
                return;
            case MotionEventCompat.AXIS_GENERIC_9 /*40*/:
                this.quality = ((Long) value).longValue();
                return;
            case GraphRequest.MAXIMUM_BATCH_SIZE /*50*/:
                this.uploadTime = (String) value;
                return;
            case 60:
                this.userAgent = (String) value;
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
        reset("ingest", "Microsoft.Telemetry.Extensions.ingest");
    }

    /* access modifiers changed from: protected */
    public void reset(String name, String qualifiedName) {
        super.reset(name, qualifiedName);
        this.time = "";
        this.clientIp = "";
        this.auth = 0;
        this.quality = 0;
        this.uploadTime = "";
        this.userAgent = "";
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
            this.time = reader.readString();
        }
        if (!canOmitFields || !reader.readFieldOmitted()) {
            this.clientIp = reader.readString();
        }
        if (!canOmitFields || !reader.readFieldOmitted()) {
            this.auth = reader.readInt64();
        }
        if (!canOmitFields || !reader.readFieldOmitted()) {
            this.quality = reader.readInt64();
        }
        if (!canOmitFields || !reader.readFieldOmitted()) {
            this.uploadTime = reader.readString();
        }
        if (!canOmitFields || !reader.readFieldOmitted()) {
            this.userAgent = reader.readString();
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
                        this.time = ReadHelper.readString(reader, fieldTag.type);
                        break;
                    case 20:
                        this.clientIp = ReadHelper.readString(reader, fieldTag.type);
                        break;
                    case 30:
                        this.auth = ReadHelper.readInt64(reader, fieldTag.type);
                        break;
                    case MotionEventCompat.AXIS_GENERIC_9 /*40*/:
                        this.quality = ReadHelper.readInt64(reader, fieldTag.type);
                        break;
                    case GraphRequest.MAXIMUM_BATCH_SIZE /*50*/:
                        this.uploadTime = ReadHelper.readString(reader, fieldTag.type);
                        break;
                    case 60:
                        this.userAgent = ReadHelper.readString(reader, fieldTag.type);
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
        writer.writeFieldBegin(BondDataType.BT_STRING, 10, Schema.time_metadata);
        writer.writeString(this.time);
        writer.writeFieldEnd();
        writer.writeFieldBegin(BondDataType.BT_STRING, 20, Schema.clientIp_metadata);
        writer.writeString(this.clientIp);
        writer.writeFieldEnd();
        if (!canOmitFields || this.auth != Schema.auth_metadata.getDefault_value().getInt_value()) {
            writer.writeFieldBegin(BondDataType.BT_INT64, 30, Schema.auth_metadata);
            writer.writeInt64(this.auth);
            writer.writeFieldEnd();
        } else {
            writer.writeFieldOmitted(BondDataType.BT_INT64, 30, Schema.auth_metadata);
        }
        if (!canOmitFields || this.quality != Schema.quality_metadata.getDefault_value().getInt_value()) {
            writer.writeFieldBegin(BondDataType.BT_INT64, 40, Schema.quality_metadata);
            writer.writeInt64(this.quality);
            writer.writeFieldEnd();
        } else {
            writer.writeFieldOmitted(BondDataType.BT_INT64, 40, Schema.quality_metadata);
        }
        if (!canOmitFields || this.uploadTime != Schema.uploadTime_metadata.getDefault_value().getString_value()) {
            writer.writeFieldBegin(BondDataType.BT_STRING, 50, Schema.uploadTime_metadata);
            writer.writeString(this.uploadTime);
            writer.writeFieldEnd();
        } else {
            writer.writeFieldOmitted(BondDataType.BT_STRING, 50, Schema.uploadTime_metadata);
        }
        if (!canOmitFields || this.userAgent != Schema.userAgent_metadata.getDefault_value().getString_value()) {
            writer.writeFieldBegin(BondDataType.BT_STRING, 60, Schema.userAgent_metadata);
            writer.writeString(this.userAgent);
            writer.writeFieldEnd();
        } else {
            writer.writeFieldOmitted(BondDataType.BT_STRING, 60, Schema.userAgent_metadata);
        }
        writer.writeStructEnd(isBase);
    }

    public boolean memberwiseCompare(Object obj) {
        if (obj == null) {
            return false;
        }
        ingest that = (ingest) obj;
        if (!memberwiseCompareQuick(that) || !memberwiseCompareDeep(that)) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:17:0x0021  */
    /* JADX WARNING: Removed duplicated region for block: B:19:0x0024  */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x0037  */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x0042  */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x004d  */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x0050  */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x0063  */
    /* JADX WARNING: Removed duplicated region for block: B:53:0x0066  */
    /* JADX WARNING: Removed duplicated region for block: B:64:0x0079  */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x0091  */
    /* JADX WARNING: Removed duplicated region for block: B:77:0x00a7  */
    /* JADX WARNING: Removed duplicated region for block: B:78:0x00a9  */
    /* JADX WARNING: Removed duplicated region for block: B:79:0x00ab  */
    /* JADX WARNING: Removed duplicated region for block: B:85:0x00c1  */
    /* JADX WARNING: Removed duplicated region for block: B:91:0x00d7  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean memberwiseCompareQuick(Microsoft.Telemetry.Extensions.ingest r9) {
        /*
            r8 = this;
            r2 = 1
            r3 = 0
            r0 = 1
            if (r0 == 0) goto L_0x007b
            boolean r1 = super.memberwiseCompareQuick(r9)
            if (r1 == 0) goto L_0x007b
            r0 = r2
        L_0x000c:
            if (r0 == 0) goto L_0x0081
            java.lang.String r1 = r8.time
            if (r1 != 0) goto L_0x007d
            r1 = r2
        L_0x0013:
            java.lang.String r4 = r9.time
            if (r4 != 0) goto L_0x007f
            r4 = r2
        L_0x0018:
            if (r1 != r4) goto L_0x0081
            r0 = r2
        L_0x001b:
            if (r0 == 0) goto L_0x0091
            java.lang.String r1 = r8.time
            if (r1 != 0) goto L_0x0083
        L_0x0021:
            r0 = r2
        L_0x0022:
            if (r0 == 0) goto L_0x0097
            java.lang.String r1 = r8.clientIp
            if (r1 != 0) goto L_0x0093
            r1 = r2
        L_0x0029:
            java.lang.String r4 = r9.clientIp
            if (r4 != 0) goto L_0x0095
            r4 = r2
        L_0x002e:
            if (r1 != r4) goto L_0x0097
            r0 = r2
        L_0x0031:
            if (r0 == 0) goto L_0x00a7
            java.lang.String r1 = r8.clientIp
            if (r1 != 0) goto L_0x0099
        L_0x0037:
            r0 = r2
        L_0x0038:
            if (r0 == 0) goto L_0x00a9
            long r4 = r8.auth
            long r6 = r9.auth
            int r1 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r1 != 0) goto L_0x00a9
            r0 = r2
        L_0x0043:
            if (r0 == 0) goto L_0x00ab
            long r4 = r8.quality
            long r6 = r9.quality
            int r1 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r1 != 0) goto L_0x00ab
            r0 = r2
        L_0x004e:
            if (r0 == 0) goto L_0x00b1
            java.lang.String r1 = r8.uploadTime
            if (r1 != 0) goto L_0x00ad
            r1 = r2
        L_0x0055:
            java.lang.String r4 = r9.uploadTime
            if (r4 != 0) goto L_0x00af
            r4 = r2
        L_0x005a:
            if (r1 != r4) goto L_0x00b1
            r0 = r2
        L_0x005d:
            if (r0 == 0) goto L_0x00c1
            java.lang.String r1 = r8.uploadTime
            if (r1 != 0) goto L_0x00b3
        L_0x0063:
            r0 = r2
        L_0x0064:
            if (r0 == 0) goto L_0x00c7
            java.lang.String r1 = r8.userAgent
            if (r1 != 0) goto L_0x00c3
            r1 = r2
        L_0x006b:
            java.lang.String r4 = r9.userAgent
            if (r4 != 0) goto L_0x00c5
            r4 = r2
        L_0x0070:
            if (r1 != r4) goto L_0x00c7
            r0 = r2
        L_0x0073:
            if (r0 == 0) goto L_0x00d7
            java.lang.String r1 = r8.userAgent
            if (r1 != 0) goto L_0x00c9
        L_0x0079:
            r0 = r2
        L_0x007a:
            return r0
        L_0x007b:
            r0 = r3
            goto L_0x000c
        L_0x007d:
            r1 = r3
            goto L_0x0013
        L_0x007f:
            r4 = r3
            goto L_0x0018
        L_0x0081:
            r0 = r3
            goto L_0x001b
        L_0x0083:
            java.lang.String r1 = r8.time
            int r1 = r1.length()
            java.lang.String r4 = r9.time
            int r4 = r4.length()
            if (r1 == r4) goto L_0x0021
        L_0x0091:
            r0 = r3
            goto L_0x0022
        L_0x0093:
            r1 = r3
            goto L_0x0029
        L_0x0095:
            r4 = r3
            goto L_0x002e
        L_0x0097:
            r0 = r3
            goto L_0x0031
        L_0x0099:
            java.lang.String r1 = r8.clientIp
            int r1 = r1.length()
            java.lang.String r4 = r9.clientIp
            int r4 = r4.length()
            if (r1 == r4) goto L_0x0037
        L_0x00a7:
            r0 = r3
            goto L_0x0038
        L_0x00a9:
            r0 = r3
            goto L_0x0043
        L_0x00ab:
            r0 = r3
            goto L_0x004e
        L_0x00ad:
            r1 = r3
            goto L_0x0055
        L_0x00af:
            r4 = r3
            goto L_0x005a
        L_0x00b1:
            r0 = r3
            goto L_0x005d
        L_0x00b3:
            java.lang.String r1 = r8.uploadTime
            int r1 = r1.length()
            java.lang.String r4 = r9.uploadTime
            int r4 = r4.length()
            if (r1 == r4) goto L_0x0063
        L_0x00c1:
            r0 = r3
            goto L_0x0064
        L_0x00c3:
            r1 = r3
            goto L_0x006b
        L_0x00c5:
            r4 = r3
            goto L_0x0070
        L_0x00c7:
            r0 = r3
            goto L_0x0073
        L_0x00c9:
            java.lang.String r1 = r8.userAgent
            int r1 = r1.length()
            java.lang.String r4 = r9.userAgent
            int r4 = r4.length()
            if (r1 == r4) goto L_0x0079
        L_0x00d7:
            r0 = r3
            goto L_0x007a
        */
        throw new UnsupportedOperationException("Method not decompiled: Microsoft.Telemetry.Extensions.ingest.memberwiseCompareQuick(Microsoft.Telemetry.Extensions.ingest):boolean");
    }

    /* access modifiers changed from: protected */
    public boolean memberwiseCompareDeep(ingest that) {
        return ((((1 != 0 && super.memberwiseCompareDeep(that)) && (this.time == null || this.time.equals(that.time))) && (this.clientIp == null || this.clientIp.equals(that.clientIp))) && (this.uploadTime == null || this.uploadTime.equals(that.uploadTime))) && (this.userAgent == null || this.userAgent.equals(that.userAgent));
    }
}
