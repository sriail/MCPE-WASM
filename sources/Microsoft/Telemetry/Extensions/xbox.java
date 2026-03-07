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
import com.microsoft.xbox.xle.app.ImageUtil;
import java.io.IOException;
import java.io.InputStream;

public class xbox extends Extension {
    private String deviceType;
    private String eventSequence;
    private String expiryTimestamp;
    private String isDevelopmentAccount;
    private String isTestAccount;
    private String issueTimestamp;
    private String sandboxId;
    private String signedInUsers;
    private String sti;
    private String titleId;
    private String xblDeviceId;

    public BondSerializable clone() {
        return null;
    }

    public final String getSti() {
        return this.sti;
    }

    public final void setSti(String value) {
        this.sti = value;
    }

    public final String getEventSequence() {
        return this.eventSequence;
    }

    public final void setEventSequence(String value) {
        this.eventSequence = value;
    }

    public final String getIssueTimestamp() {
        return this.issueTimestamp;
    }

    public final void setIssueTimestamp(String value) {
        this.issueTimestamp = value;
    }

    public final String getExpiryTimestamp() {
        return this.expiryTimestamp;
    }

    public final void setExpiryTimestamp(String value) {
        this.expiryTimestamp = value;
    }

    public final String getSandboxId() {
        return this.sandboxId;
    }

    public final void setSandboxId(String value) {
        this.sandboxId = value;
    }

    public final String getDeviceType() {
        return this.deviceType;
    }

    public final void setDeviceType(String value) {
        this.deviceType = value;
    }

    public final String getXblDeviceId() {
        return this.xblDeviceId;
    }

    public final void setXblDeviceId(String value) {
        this.xblDeviceId = value;
    }

    public final String getSignedInUsers() {
        return this.signedInUsers;
    }

    public final void setSignedInUsers(String value) {
        this.signedInUsers = value;
    }

    public final String getIsDevelopmentAccount() {
        return this.isDevelopmentAccount;
    }

    public final void setIsDevelopmentAccount(String value) {
        this.isDevelopmentAccount = value;
    }

    public final String getIsTestAccount() {
        return this.isTestAccount;
    }

    public final void setIsTestAccount(String value) {
        this.isTestAccount = value;
    }

    public final String getTitleId() {
        return this.titleId;
    }

    public final void setTitleId(String value) {
        this.titleId = value;
    }

    public static class Schema {
        /* access modifiers changed from: private */
        public static final Metadata deviceType_metadata = new Metadata();
        /* access modifiers changed from: private */
        public static final Metadata eventSequence_metadata = new Metadata();
        /* access modifiers changed from: private */
        public static final Metadata expiryTimestamp_metadata = new Metadata();
        /* access modifiers changed from: private */
        public static final Metadata isDevelopmentAccount_metadata = new Metadata();
        /* access modifiers changed from: private */
        public static final Metadata isTestAccount_metadata = new Metadata();
        /* access modifiers changed from: private */
        public static final Metadata issueTimestamp_metadata = new Metadata();
        public static final Metadata metadata = new Metadata();
        /* access modifiers changed from: private */
        public static final Metadata sandboxId_metadata = new Metadata();
        public static final SchemaDef schemaDef = new SchemaDef();
        /* access modifiers changed from: private */
        public static final Metadata signedInUsers_metadata = new Metadata();
        /* access modifiers changed from: private */
        public static final Metadata sti_metadata = new Metadata();
        /* access modifiers changed from: private */
        public static final Metadata titleId_metadata = new Metadata();
        /* access modifiers changed from: private */
        public static final Metadata xblDeviceId_metadata = new Metadata();

        static {
            metadata.setName("xbox");
            metadata.setQualified_name("Microsoft.Telemetry.Extensions.xbox");
            metadata.getAttributes().put("Description", "Describes the XBox related fields and might be populated by the console.");
            sti_metadata.setName("sti");
            sti_metadata.getAttributes().put("Description", "XBox supporting token index.");
            eventSequence_metadata.setName("eventSequence");
            eventSequence_metadata.getAttributes().put("Description", "XBox event sequence.");
            issueTimestamp_metadata.setName("issueTimestamp");
            issueTimestamp_metadata.getAttributes().put("Description", "Xbox token issue timestamp.");
            expiryTimestamp_metadata.setName("expiryTimestamp");
            expiryTimestamp_metadata.getAttributes().put("Description", "XBox token expiry timestamp.");
            sandboxId_metadata.setName("sandboxId");
            sandboxId_metadata.getAttributes().put("Description", "Xbox sandboxId.");
            deviceType_metadata.setName("deviceType");
            deviceType_metadata.getAttributes().put("Description", "XBox device type.");
            xblDeviceId_metadata.setName("xblDeviceId");
            xblDeviceId_metadata.getAttributes().put("Description", "Xbox live deviceId.");
            signedInUsers_metadata.setName("signedInUsers");
            signedInUsers_metadata.getAttributes().put("Description", "XBox signed in Xuids.");
            isDevelopmentAccount_metadata.setName("isDevelopmentAccount");
            isDevelopmentAccount_metadata.getAttributes().put("Description", "XBox is development account.");
            isTestAccount_metadata.setName("isTestAccount");
            isTestAccount_metadata.getAttributes().put("Description", "XBox is test account.");
            titleId_metadata.setName("titleId");
            titleId_metadata.getAttributes().put("Description", "XBox titleId.");
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
                    field.setMetadata(sti_metadata);
                    field.getType().setId(BondDataType.BT_STRING);
                    structDef.getFields().add(field);
                    FieldDef field2 = new FieldDef();
                    field2.setId(20);
                    field2.setMetadata(eventSequence_metadata);
                    field2.getType().setId(BondDataType.BT_STRING);
                    structDef.getFields().add(field2);
                    FieldDef field3 = new FieldDef();
                    field3.setId(30);
                    field3.setMetadata(issueTimestamp_metadata);
                    field3.getType().setId(BondDataType.BT_STRING);
                    structDef.getFields().add(field3);
                    FieldDef field4 = new FieldDef();
                    field4.setId(40);
                    field4.setMetadata(expiryTimestamp_metadata);
                    field4.getType().setId(BondDataType.BT_STRING);
                    structDef.getFields().add(field4);
                    FieldDef field5 = new FieldDef();
                    field5.setId(50);
                    field5.setMetadata(sandboxId_metadata);
                    field5.getType().setId(BondDataType.BT_STRING);
                    structDef.getFields().add(field5);
                    FieldDef field6 = new FieldDef();
                    field6.setId(60);
                    field6.setMetadata(deviceType_metadata);
                    field6.getType().setId(BondDataType.BT_STRING);
                    structDef.getFields().add(field6);
                    FieldDef field7 = new FieldDef();
                    field7.setId(70);
                    field7.setMetadata(xblDeviceId_metadata);
                    field7.getType().setId(BondDataType.BT_STRING);
                    structDef.getFields().add(field7);
                    FieldDef field8 = new FieldDef();
                    field8.setId(80);
                    field8.setMetadata(signedInUsers_metadata);
                    field8.getType().setId(BondDataType.BT_STRING);
                    structDef.getFields().add(field8);
                    FieldDef field9 = new FieldDef();
                    field9.setId(90);
                    field9.setMetadata(isDevelopmentAccount_metadata);
                    field9.getType().setId(BondDataType.BT_STRING);
                    structDef.getFields().add(field9);
                    FieldDef field10 = new FieldDef();
                    field10.setId(100);
                    field10.setMetadata(isTestAccount_metadata);
                    field10.getType().setId(BondDataType.BT_STRING);
                    structDef.getFields().add(field10);
                    FieldDef field11 = new FieldDef();
                    field11.setId(110);
                    field11.setMetadata(titleId_metadata);
                    field11.getType().setId(BondDataType.BT_STRING);
                    structDef.getFields().add(field11);
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
                return this.sti;
            case 20:
                return this.eventSequence;
            case 30:
                return this.issueTimestamp;
            case MotionEventCompat.AXIS_GENERIC_9 /*40*/:
                return this.expiryTimestamp;
            case GraphRequest.MAXIMUM_BATCH_SIZE /*50*/:
                return this.sandboxId;
            case 60:
                return this.deviceType;
            case 70:
                return this.xblDeviceId;
            case 80:
                return this.signedInUsers;
            case 90:
                return this.isDevelopmentAccount;
            case ImageUtil.TINY /*100*/:
                return this.isTestAccount;
            case 110:
                return this.titleId;
            default:
                return null;
        }
    }

    public void setField(FieldDef fieldDef, Object value) {
        switch (fieldDef.getId()) {
            case 10:
                this.sti = (String) value;
                return;
            case 20:
                this.eventSequence = (String) value;
                return;
            case 30:
                this.issueTimestamp = (String) value;
                return;
            case MotionEventCompat.AXIS_GENERIC_9 /*40*/:
                this.expiryTimestamp = (String) value;
                return;
            case GraphRequest.MAXIMUM_BATCH_SIZE /*50*/:
                this.sandboxId = (String) value;
                return;
            case 60:
                this.deviceType = (String) value;
                return;
            case 70:
                this.xblDeviceId = (String) value;
                return;
            case 80:
                this.signedInUsers = (String) value;
                return;
            case 90:
                this.isDevelopmentAccount = (String) value;
                return;
            case ImageUtil.TINY /*100*/:
                this.isTestAccount = (String) value;
                return;
            case 110:
                this.titleId = (String) value;
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
        reset("xbox", "Microsoft.Telemetry.Extensions.xbox");
    }

    /* access modifiers changed from: protected */
    public void reset(String name, String qualifiedName) {
        super.reset(name, qualifiedName);
        this.sti = "";
        this.eventSequence = "";
        this.issueTimestamp = "";
        this.expiryTimestamp = "";
        this.sandboxId = "";
        this.deviceType = "";
        this.xblDeviceId = "";
        this.signedInUsers = "";
        this.isDevelopmentAccount = "";
        this.isTestAccount = "";
        this.titleId = "";
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
            this.sti = reader.readString();
        }
        if (!canOmitFields || !reader.readFieldOmitted()) {
            this.eventSequence = reader.readString();
        }
        if (!canOmitFields || !reader.readFieldOmitted()) {
            this.issueTimestamp = reader.readString();
        }
        if (!canOmitFields || !reader.readFieldOmitted()) {
            this.expiryTimestamp = reader.readString();
        }
        if (!canOmitFields || !reader.readFieldOmitted()) {
            this.sandboxId = reader.readString();
        }
        if (!canOmitFields || !reader.readFieldOmitted()) {
            this.deviceType = reader.readString();
        }
        if (!canOmitFields || !reader.readFieldOmitted()) {
            this.xblDeviceId = reader.readString();
        }
        if (!canOmitFields || !reader.readFieldOmitted()) {
            this.signedInUsers = reader.readString();
        }
        if (!canOmitFields || !reader.readFieldOmitted()) {
            this.isDevelopmentAccount = reader.readString();
        }
        if (!canOmitFields || !reader.readFieldOmitted()) {
            this.isTestAccount = reader.readString();
        }
        if (!canOmitFields || !reader.readFieldOmitted()) {
            this.titleId = reader.readString();
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
                        this.sti = ReadHelper.readString(reader, fieldTag.type);
                        break;
                    case 20:
                        this.eventSequence = ReadHelper.readString(reader, fieldTag.type);
                        break;
                    case 30:
                        this.issueTimestamp = ReadHelper.readString(reader, fieldTag.type);
                        break;
                    case MotionEventCompat.AXIS_GENERIC_9 /*40*/:
                        this.expiryTimestamp = ReadHelper.readString(reader, fieldTag.type);
                        break;
                    case GraphRequest.MAXIMUM_BATCH_SIZE /*50*/:
                        this.sandboxId = ReadHelper.readString(reader, fieldTag.type);
                        break;
                    case 60:
                        this.deviceType = ReadHelper.readString(reader, fieldTag.type);
                        break;
                    case 70:
                        this.xblDeviceId = ReadHelper.readString(reader, fieldTag.type);
                        break;
                    case 80:
                        this.signedInUsers = ReadHelper.readString(reader, fieldTag.type);
                        break;
                    case 90:
                        this.isDevelopmentAccount = ReadHelper.readString(reader, fieldTag.type);
                        break;
                    case ImageUtil.TINY /*100*/:
                        this.isTestAccount = ReadHelper.readString(reader, fieldTag.type);
                        break;
                    case 110:
                        this.titleId = ReadHelper.readString(reader, fieldTag.type);
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
        if (!canOmitFields || this.sti != Schema.sti_metadata.getDefault_value().getString_value()) {
            writer.writeFieldBegin(BondDataType.BT_STRING, 10, Schema.sti_metadata);
            writer.writeString(this.sti);
            writer.writeFieldEnd();
        } else {
            writer.writeFieldOmitted(BondDataType.BT_STRING, 10, Schema.sti_metadata);
        }
        if (!canOmitFields || this.eventSequence != Schema.eventSequence_metadata.getDefault_value().getString_value()) {
            writer.writeFieldBegin(BondDataType.BT_STRING, 20, Schema.eventSequence_metadata);
            writer.writeString(this.eventSequence);
            writer.writeFieldEnd();
        } else {
            writer.writeFieldOmitted(BondDataType.BT_STRING, 20, Schema.eventSequence_metadata);
        }
        if (!canOmitFields || this.issueTimestamp != Schema.issueTimestamp_metadata.getDefault_value().getString_value()) {
            writer.writeFieldBegin(BondDataType.BT_STRING, 30, Schema.issueTimestamp_metadata);
            writer.writeString(this.issueTimestamp);
            writer.writeFieldEnd();
        } else {
            writer.writeFieldOmitted(BondDataType.BT_STRING, 30, Schema.issueTimestamp_metadata);
        }
        if (!canOmitFields || this.expiryTimestamp != Schema.expiryTimestamp_metadata.getDefault_value().getString_value()) {
            writer.writeFieldBegin(BondDataType.BT_STRING, 40, Schema.expiryTimestamp_metadata);
            writer.writeString(this.expiryTimestamp);
            writer.writeFieldEnd();
        } else {
            writer.writeFieldOmitted(BondDataType.BT_STRING, 40, Schema.expiryTimestamp_metadata);
        }
        if (!canOmitFields || this.sandboxId != Schema.sandboxId_metadata.getDefault_value().getString_value()) {
            writer.writeFieldBegin(BondDataType.BT_STRING, 50, Schema.sandboxId_metadata);
            writer.writeString(this.sandboxId);
            writer.writeFieldEnd();
        } else {
            writer.writeFieldOmitted(BondDataType.BT_STRING, 50, Schema.sandboxId_metadata);
        }
        if (!canOmitFields || this.deviceType != Schema.deviceType_metadata.getDefault_value().getString_value()) {
            writer.writeFieldBegin(BondDataType.BT_STRING, 60, Schema.deviceType_metadata);
            writer.writeString(this.deviceType);
            writer.writeFieldEnd();
        } else {
            writer.writeFieldOmitted(BondDataType.BT_STRING, 60, Schema.deviceType_metadata);
        }
        if (!canOmitFields || this.xblDeviceId != Schema.xblDeviceId_metadata.getDefault_value().getString_value()) {
            writer.writeFieldBegin(BondDataType.BT_STRING, 70, Schema.xblDeviceId_metadata);
            writer.writeString(this.xblDeviceId);
            writer.writeFieldEnd();
        } else {
            writer.writeFieldOmitted(BondDataType.BT_STRING, 70, Schema.xblDeviceId_metadata);
        }
        if (!canOmitFields || this.signedInUsers != Schema.signedInUsers_metadata.getDefault_value().getString_value()) {
            writer.writeFieldBegin(BondDataType.BT_STRING, 80, Schema.signedInUsers_metadata);
            writer.writeString(this.signedInUsers);
            writer.writeFieldEnd();
        } else {
            writer.writeFieldOmitted(BondDataType.BT_STRING, 80, Schema.signedInUsers_metadata);
        }
        if (!canOmitFields || this.isDevelopmentAccount != Schema.isDevelopmentAccount_metadata.getDefault_value().getString_value()) {
            writer.writeFieldBegin(BondDataType.BT_STRING, 90, Schema.isDevelopmentAccount_metadata);
            writer.writeString(this.isDevelopmentAccount);
            writer.writeFieldEnd();
        } else {
            writer.writeFieldOmitted(BondDataType.BT_STRING, 90, Schema.isDevelopmentAccount_metadata);
        }
        if (!canOmitFields || this.isTestAccount != Schema.isTestAccount_metadata.getDefault_value().getString_value()) {
            writer.writeFieldBegin(BondDataType.BT_STRING, 100, Schema.isTestAccount_metadata);
            writer.writeString(this.isTestAccount);
            writer.writeFieldEnd();
        } else {
            writer.writeFieldOmitted(BondDataType.BT_STRING, 100, Schema.isTestAccount_metadata);
        }
        if (!canOmitFields || this.titleId != Schema.titleId_metadata.getDefault_value().getString_value()) {
            writer.writeFieldBegin(BondDataType.BT_STRING, 110, Schema.titleId_metadata);
            writer.writeString(this.titleId);
            writer.writeFieldEnd();
        } else {
            writer.writeFieldOmitted(BondDataType.BT_STRING, 110, Schema.titleId_metadata);
        }
        writer.writeStructEnd(isBase);
    }

    public boolean memberwiseCompare(Object obj) {
        if (obj == null) {
            return false;
        }
        xbox that = (xbox) obj;
        if (!memberwiseCompareQuick(that) || !memberwiseCompareDeep(that)) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:108:0x00bb  */
    /* JADX WARNING: Removed duplicated region for block: B:110:0x00be  */
    /* JADX WARNING: Removed duplicated region for block: B:121:0x00d1  */
    /* JADX WARNING: Removed duplicated region for block: B:123:0x00d4  */
    /* JADX WARNING: Removed duplicated region for block: B:134:0x00e7  */
    /* JADX WARNING: Removed duplicated region for block: B:136:0x00ea  */
    /* JADX WARNING: Removed duplicated region for block: B:147:0x00fd  */
    /* JADX WARNING: Removed duplicated region for block: B:154:0x0119  */
    /* JADX WARNING: Removed duplicated region for block: B:160:0x0133  */
    /* JADX WARNING: Removed duplicated region for block: B:166:0x014d  */
    /* JADX WARNING: Removed duplicated region for block: B:172:0x0167  */
    /* JADX WARNING: Removed duplicated region for block: B:178:0x0181  */
    /* JADX WARNING: Removed duplicated region for block: B:17:0x0021  */
    /* JADX WARNING: Removed duplicated region for block: B:184:0x019b  */
    /* JADX WARNING: Removed duplicated region for block: B:190:0x01b5  */
    /* JADX WARNING: Removed duplicated region for block: B:196:0x01cf  */
    /* JADX WARNING: Removed duplicated region for block: B:19:0x0024  */
    /* JADX WARNING: Removed duplicated region for block: B:202:0x01e9  */
    /* JADX WARNING: Removed duplicated region for block: B:208:0x0203  */
    /* JADX WARNING: Removed duplicated region for block: B:214:0x021d  */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x0037  */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x003a  */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x004d  */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x0050  */
    /* JADX WARNING: Removed duplicated region for block: B:56:0x0063  */
    /* JADX WARNING: Removed duplicated region for block: B:58:0x0066  */
    /* JADX WARNING: Removed duplicated region for block: B:69:0x0079  */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x007c  */
    /* JADX WARNING: Removed duplicated region for block: B:82:0x008f  */
    /* JADX WARNING: Removed duplicated region for block: B:84:0x0092  */
    /* JADX WARNING: Removed duplicated region for block: B:95:0x00a5  */
    /* JADX WARNING: Removed duplicated region for block: B:97:0x00a8  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean memberwiseCompareQuick(Microsoft.Telemetry.Extensions.xbox r6) {
        /*
            r5 = this;
            r2 = 1
            r3 = 0
            r0 = 1
            if (r0 == 0) goto L_0x00ff
            boolean r1 = super.memberwiseCompareQuick(r6)
            if (r1 == 0) goto L_0x00ff
            r0 = r2
        L_0x000c:
            if (r0 == 0) goto L_0x0108
            java.lang.String r1 = r5.sti
            if (r1 != 0) goto L_0x0102
            r1 = r2
        L_0x0013:
            java.lang.String r4 = r6.sti
            if (r4 != 0) goto L_0x0105
            r4 = r2
        L_0x0018:
            if (r1 != r4) goto L_0x0108
            r0 = r2
        L_0x001b:
            if (r0 == 0) goto L_0x0119
            java.lang.String r1 = r5.sti
            if (r1 != 0) goto L_0x010b
        L_0x0021:
            r0 = r2
        L_0x0022:
            if (r0 == 0) goto L_0x0122
            java.lang.String r1 = r5.eventSequence
            if (r1 != 0) goto L_0x011c
            r1 = r2
        L_0x0029:
            java.lang.String r4 = r6.eventSequence
            if (r4 != 0) goto L_0x011f
            r4 = r2
        L_0x002e:
            if (r1 != r4) goto L_0x0122
            r0 = r2
        L_0x0031:
            if (r0 == 0) goto L_0x0133
            java.lang.String r1 = r5.eventSequence
            if (r1 != 0) goto L_0x0125
        L_0x0037:
            r0 = r2
        L_0x0038:
            if (r0 == 0) goto L_0x013c
            java.lang.String r1 = r5.issueTimestamp
            if (r1 != 0) goto L_0x0136
            r1 = r2
        L_0x003f:
            java.lang.String r4 = r6.issueTimestamp
            if (r4 != 0) goto L_0x0139
            r4 = r2
        L_0x0044:
            if (r1 != r4) goto L_0x013c
            r0 = r2
        L_0x0047:
            if (r0 == 0) goto L_0x014d
            java.lang.String r1 = r5.issueTimestamp
            if (r1 != 0) goto L_0x013f
        L_0x004d:
            r0 = r2
        L_0x004e:
            if (r0 == 0) goto L_0x0156
            java.lang.String r1 = r5.expiryTimestamp
            if (r1 != 0) goto L_0x0150
            r1 = r2
        L_0x0055:
            java.lang.String r4 = r6.expiryTimestamp
            if (r4 != 0) goto L_0x0153
            r4 = r2
        L_0x005a:
            if (r1 != r4) goto L_0x0156
            r0 = r2
        L_0x005d:
            if (r0 == 0) goto L_0x0167
            java.lang.String r1 = r5.expiryTimestamp
            if (r1 != 0) goto L_0x0159
        L_0x0063:
            r0 = r2
        L_0x0064:
            if (r0 == 0) goto L_0x0170
            java.lang.String r1 = r5.sandboxId
            if (r1 != 0) goto L_0x016a
            r1 = r2
        L_0x006b:
            java.lang.String r4 = r6.sandboxId
            if (r4 != 0) goto L_0x016d
            r4 = r2
        L_0x0070:
            if (r1 != r4) goto L_0x0170
            r0 = r2
        L_0x0073:
            if (r0 == 0) goto L_0x0181
            java.lang.String r1 = r5.sandboxId
            if (r1 != 0) goto L_0x0173
        L_0x0079:
            r0 = r2
        L_0x007a:
            if (r0 == 0) goto L_0x018a
            java.lang.String r1 = r5.deviceType
            if (r1 != 0) goto L_0x0184
            r1 = r2
        L_0x0081:
            java.lang.String r4 = r6.deviceType
            if (r4 != 0) goto L_0x0187
            r4 = r2
        L_0x0086:
            if (r1 != r4) goto L_0x018a
            r0 = r2
        L_0x0089:
            if (r0 == 0) goto L_0x019b
            java.lang.String r1 = r5.deviceType
            if (r1 != 0) goto L_0x018d
        L_0x008f:
            r0 = r2
        L_0x0090:
            if (r0 == 0) goto L_0x01a4
            java.lang.String r1 = r5.xblDeviceId
            if (r1 != 0) goto L_0x019e
            r1 = r2
        L_0x0097:
            java.lang.String r4 = r6.xblDeviceId
            if (r4 != 0) goto L_0x01a1
            r4 = r2
        L_0x009c:
            if (r1 != r4) goto L_0x01a4
            r0 = r2
        L_0x009f:
            if (r0 == 0) goto L_0x01b5
            java.lang.String r1 = r5.xblDeviceId
            if (r1 != 0) goto L_0x01a7
        L_0x00a5:
            r0 = r2
        L_0x00a6:
            if (r0 == 0) goto L_0x01be
            java.lang.String r1 = r5.signedInUsers
            if (r1 != 0) goto L_0x01b8
            r1 = r2
        L_0x00ad:
            java.lang.String r4 = r6.signedInUsers
            if (r4 != 0) goto L_0x01bb
            r4 = r2
        L_0x00b2:
            if (r1 != r4) goto L_0x01be
            r0 = r2
        L_0x00b5:
            if (r0 == 0) goto L_0x01cf
            java.lang.String r1 = r5.signedInUsers
            if (r1 != 0) goto L_0x01c1
        L_0x00bb:
            r0 = r2
        L_0x00bc:
            if (r0 == 0) goto L_0x01d8
            java.lang.String r1 = r5.isDevelopmentAccount
            if (r1 != 0) goto L_0x01d2
            r1 = r2
        L_0x00c3:
            java.lang.String r4 = r6.isDevelopmentAccount
            if (r4 != 0) goto L_0x01d5
            r4 = r2
        L_0x00c8:
            if (r1 != r4) goto L_0x01d8
            r0 = r2
        L_0x00cb:
            if (r0 == 0) goto L_0x01e9
            java.lang.String r1 = r5.isDevelopmentAccount
            if (r1 != 0) goto L_0x01db
        L_0x00d1:
            r0 = r2
        L_0x00d2:
            if (r0 == 0) goto L_0x01f2
            java.lang.String r1 = r5.isTestAccount
            if (r1 != 0) goto L_0x01ec
            r1 = r2
        L_0x00d9:
            java.lang.String r4 = r6.isTestAccount
            if (r4 != 0) goto L_0x01ef
            r4 = r2
        L_0x00de:
            if (r1 != r4) goto L_0x01f2
            r0 = r2
        L_0x00e1:
            if (r0 == 0) goto L_0x0203
            java.lang.String r1 = r5.isTestAccount
            if (r1 != 0) goto L_0x01f5
        L_0x00e7:
            r0 = r2
        L_0x00e8:
            if (r0 == 0) goto L_0x020c
            java.lang.String r1 = r5.titleId
            if (r1 != 0) goto L_0x0206
            r1 = r2
        L_0x00ef:
            java.lang.String r4 = r6.titleId
            if (r4 != 0) goto L_0x0209
            r4 = r2
        L_0x00f4:
            if (r1 != r4) goto L_0x020c
            r0 = r2
        L_0x00f7:
            if (r0 == 0) goto L_0x021d
            java.lang.String r1 = r5.titleId
            if (r1 != 0) goto L_0x020f
        L_0x00fd:
            r0 = r2
        L_0x00fe:
            return r0
        L_0x00ff:
            r0 = r3
            goto L_0x000c
        L_0x0102:
            r1 = r3
            goto L_0x0013
        L_0x0105:
            r4 = r3
            goto L_0x0018
        L_0x0108:
            r0 = r3
            goto L_0x001b
        L_0x010b:
            java.lang.String r1 = r5.sti
            int r1 = r1.length()
            java.lang.String r4 = r6.sti
            int r4 = r4.length()
            if (r1 == r4) goto L_0x0021
        L_0x0119:
            r0 = r3
            goto L_0x0022
        L_0x011c:
            r1 = r3
            goto L_0x0029
        L_0x011f:
            r4 = r3
            goto L_0x002e
        L_0x0122:
            r0 = r3
            goto L_0x0031
        L_0x0125:
            java.lang.String r1 = r5.eventSequence
            int r1 = r1.length()
            java.lang.String r4 = r6.eventSequence
            int r4 = r4.length()
            if (r1 == r4) goto L_0x0037
        L_0x0133:
            r0 = r3
            goto L_0x0038
        L_0x0136:
            r1 = r3
            goto L_0x003f
        L_0x0139:
            r4 = r3
            goto L_0x0044
        L_0x013c:
            r0 = r3
            goto L_0x0047
        L_0x013f:
            java.lang.String r1 = r5.issueTimestamp
            int r1 = r1.length()
            java.lang.String r4 = r6.issueTimestamp
            int r4 = r4.length()
            if (r1 == r4) goto L_0x004d
        L_0x014d:
            r0 = r3
            goto L_0x004e
        L_0x0150:
            r1 = r3
            goto L_0x0055
        L_0x0153:
            r4 = r3
            goto L_0x005a
        L_0x0156:
            r0 = r3
            goto L_0x005d
        L_0x0159:
            java.lang.String r1 = r5.expiryTimestamp
            int r1 = r1.length()
            java.lang.String r4 = r6.expiryTimestamp
            int r4 = r4.length()
            if (r1 == r4) goto L_0x0063
        L_0x0167:
            r0 = r3
            goto L_0x0064
        L_0x016a:
            r1 = r3
            goto L_0x006b
        L_0x016d:
            r4 = r3
            goto L_0x0070
        L_0x0170:
            r0 = r3
            goto L_0x0073
        L_0x0173:
            java.lang.String r1 = r5.sandboxId
            int r1 = r1.length()
            java.lang.String r4 = r6.sandboxId
            int r4 = r4.length()
            if (r1 == r4) goto L_0x0079
        L_0x0181:
            r0 = r3
            goto L_0x007a
        L_0x0184:
            r1 = r3
            goto L_0x0081
        L_0x0187:
            r4 = r3
            goto L_0x0086
        L_0x018a:
            r0 = r3
            goto L_0x0089
        L_0x018d:
            java.lang.String r1 = r5.deviceType
            int r1 = r1.length()
            java.lang.String r4 = r6.deviceType
            int r4 = r4.length()
            if (r1 == r4) goto L_0x008f
        L_0x019b:
            r0 = r3
            goto L_0x0090
        L_0x019e:
            r1 = r3
            goto L_0x0097
        L_0x01a1:
            r4 = r3
            goto L_0x009c
        L_0x01a4:
            r0 = r3
            goto L_0x009f
        L_0x01a7:
            java.lang.String r1 = r5.xblDeviceId
            int r1 = r1.length()
            java.lang.String r4 = r6.xblDeviceId
            int r4 = r4.length()
            if (r1 == r4) goto L_0x00a5
        L_0x01b5:
            r0 = r3
            goto L_0x00a6
        L_0x01b8:
            r1 = r3
            goto L_0x00ad
        L_0x01bb:
            r4 = r3
            goto L_0x00b2
        L_0x01be:
            r0 = r3
            goto L_0x00b5
        L_0x01c1:
            java.lang.String r1 = r5.signedInUsers
            int r1 = r1.length()
            java.lang.String r4 = r6.signedInUsers
            int r4 = r4.length()
            if (r1 == r4) goto L_0x00bb
        L_0x01cf:
            r0 = r3
            goto L_0x00bc
        L_0x01d2:
            r1 = r3
            goto L_0x00c3
        L_0x01d5:
            r4 = r3
            goto L_0x00c8
        L_0x01d8:
            r0 = r3
            goto L_0x00cb
        L_0x01db:
            java.lang.String r1 = r5.isDevelopmentAccount
            int r1 = r1.length()
            java.lang.String r4 = r6.isDevelopmentAccount
            int r4 = r4.length()
            if (r1 == r4) goto L_0x00d1
        L_0x01e9:
            r0 = r3
            goto L_0x00d2
        L_0x01ec:
            r1 = r3
            goto L_0x00d9
        L_0x01ef:
            r4 = r3
            goto L_0x00de
        L_0x01f2:
            r0 = r3
            goto L_0x00e1
        L_0x01f5:
            java.lang.String r1 = r5.isTestAccount
            int r1 = r1.length()
            java.lang.String r4 = r6.isTestAccount
            int r4 = r4.length()
            if (r1 == r4) goto L_0x00e7
        L_0x0203:
            r0 = r3
            goto L_0x00e8
        L_0x0206:
            r1 = r3
            goto L_0x00ef
        L_0x0209:
            r4 = r3
            goto L_0x00f4
        L_0x020c:
            r0 = r3
            goto L_0x00f7
        L_0x020f:
            java.lang.String r1 = r5.titleId
            int r1 = r1.length()
            java.lang.String r4 = r6.titleId
            int r4 = r4.length()
            if (r1 == r4) goto L_0x00fd
        L_0x021d:
            r0 = r3
            goto L_0x00fe
        */
        throw new UnsupportedOperationException("Method not decompiled: Microsoft.Telemetry.Extensions.xbox.memberwiseCompareQuick(Microsoft.Telemetry.Extensions.xbox):boolean");
    }

    /* access modifiers changed from: protected */
    public boolean memberwiseCompareDeep(xbox that) {
        return (((((((((((1 != 0 && super.memberwiseCompareDeep(that)) && (this.sti == null || this.sti.equals(that.sti))) && (this.eventSequence == null || this.eventSequence.equals(that.eventSequence))) && (this.issueTimestamp == null || this.issueTimestamp.equals(that.issueTimestamp))) && (this.expiryTimestamp == null || this.expiryTimestamp.equals(that.expiryTimestamp))) && (this.sandboxId == null || this.sandboxId.equals(that.sandboxId))) && (this.deviceType == null || this.deviceType.equals(that.deviceType))) && (this.xblDeviceId == null || this.xblDeviceId.equals(that.xblDeviceId))) && (this.signedInUsers == null || this.signedInUsers.equals(that.signedInUsers))) && (this.isDevelopmentAccount == null || this.isDevelopmentAccount.equals(that.isDevelopmentAccount))) && (this.isTestAccount == null || this.isTestAccount.equals(that.isTestAccount))) && (this.titleId == null || this.titleId.equals(that.titleId));
    }
}
