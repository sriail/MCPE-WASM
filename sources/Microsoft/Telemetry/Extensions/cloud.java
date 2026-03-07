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

public class cloud extends Extension {
    private String deploymentUnit;
    private String environment;
    private String location;
    private String name;
    private String role;
    private String roleInstance;
    private String roleVer;

    public BondSerializable clone() {
        return null;
    }

    public final String getName() {
        return this.name;
    }

    public final void setName(String value) {
        this.name = value;
    }

    public final String getRole() {
        return this.role;
    }

    public final void setRole(String value) {
        this.role = value;
    }

    public final String getRoleInstance() {
        return this.roleInstance;
    }

    public final void setRoleInstance(String value) {
        this.roleInstance = value;
    }

    public final String getLocation() {
        return this.location;
    }

    public final void setLocation(String value) {
        this.location = value;
    }

    public final String getRoleVer() {
        return this.roleVer;
    }

    public final void setRoleVer(String value) {
        this.roleVer = value;
    }

    public final String getEnvironment() {
        return this.environment;
    }

    public final void setEnvironment(String value) {
        this.environment = value;
    }

    public final String getDeploymentUnit() {
        return this.deploymentUnit;
    }

    public final void setDeploymentUnit(String value) {
        this.deploymentUnit = value;
    }

    public static class Schema {
        /* access modifiers changed from: private */
        public static final Metadata deploymentUnit_metadata = new Metadata();
        /* access modifiers changed from: private */
        public static final Metadata environment_metadata = new Metadata();
        /* access modifiers changed from: private */
        public static final Metadata location_metadata = new Metadata();
        public static final Metadata metadata = new Metadata();
        /* access modifiers changed from: private */
        public static final Metadata name_metadata = new Metadata();
        /* access modifiers changed from: private */
        public static final Metadata roleInstance_metadata = new Metadata();
        /* access modifiers changed from: private */
        public static final Metadata roleVer_metadata = new Metadata();
        /* access modifiers changed from: private */
        public static final Metadata role_metadata = new Metadata();
        public static final SchemaDef schemaDef = new SchemaDef();

        static {
            metadata.setName("cloud");
            metadata.setQualified_name("Microsoft.Telemetry.Extensions.cloud");
            metadata.getAttributes().put("Description", "Describes the service related fields populated by the cloud service.");
            name_metadata.setName("name");
            name_metadata.setModifier(Modifier.Required);
            name_metadata.getAttributes().put("Description", "Name of the service.");
            role_metadata.setName("role");
            role_metadata.setModifier(Modifier.Required);
            role_metadata.getAttributes().put("Description", "Service role.");
            roleInstance_metadata.setName("roleInstance");
            roleInstance_metadata.setModifier(Modifier.Required);
            roleInstance_metadata.getAttributes().put("Description", "Instance id of the deployed role instance generating the event.");
            location_metadata.setName("location");
            location_metadata.setModifier(Modifier.Required);
            location_metadata.getAttributes().put("Description", "Deployed location of the role instance (canonical name of datacenter, e.g. 'East US')");
            roleVer_metadata.setName("roleVer");
            roleVer_metadata.getAttributes().put("Description", "Build version of the role. Recommended formats are either semantic version, or NT style: <MajorVersion>.<MinorVersion>.<Optional MileStone?>, <BuildNumber>.<Architecture>.<Branch>.<yyMMdd-hhmm>, e.g. 130.0.4590.3525.amd64fre.rd_fabric_n.140618-1229.");
            environment_metadata.setName("environment");
            environment_metadata.getAttributes().put("Description", "Service deployment environment or topology (e.g. Prod, PPE, ChinaProd).");
            deploymentUnit_metadata.setName("deploymentUnit");
            deploymentUnit_metadata.getAttributes().put("Description", "Service deployment or scale unit (for partitioned services).");
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
                    field.setMetadata(name_metadata);
                    field.getType().setId(BondDataType.BT_STRING);
                    structDef.getFields().add(field);
                    FieldDef field2 = new FieldDef();
                    field2.setId(20);
                    field2.setMetadata(role_metadata);
                    field2.getType().setId(BondDataType.BT_STRING);
                    structDef.getFields().add(field2);
                    FieldDef field3 = new FieldDef();
                    field3.setId(30);
                    field3.setMetadata(roleInstance_metadata);
                    field3.getType().setId(BondDataType.BT_STRING);
                    structDef.getFields().add(field3);
                    FieldDef field4 = new FieldDef();
                    field4.setId(40);
                    field4.setMetadata(location_metadata);
                    field4.getType().setId(BondDataType.BT_STRING);
                    structDef.getFields().add(field4);
                    FieldDef field5 = new FieldDef();
                    field5.setId(50);
                    field5.setMetadata(roleVer_metadata);
                    field5.getType().setId(BondDataType.BT_STRING);
                    structDef.getFields().add(field5);
                    FieldDef field6 = new FieldDef();
                    field6.setId(60);
                    field6.setMetadata(environment_metadata);
                    field6.getType().setId(BondDataType.BT_STRING);
                    structDef.getFields().add(field6);
                    FieldDef field7 = new FieldDef();
                    field7.setId(70);
                    field7.setMetadata(deploymentUnit_metadata);
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
                return this.name;
            case 20:
                return this.role;
            case 30:
                return this.roleInstance;
            case MotionEventCompat.AXIS_GENERIC_9 /*40*/:
                return this.location;
            case GraphRequest.MAXIMUM_BATCH_SIZE /*50*/:
                return this.roleVer;
            case 60:
                return this.environment;
            case 70:
                return this.deploymentUnit;
            default:
                return null;
        }
    }

    public void setField(FieldDef fieldDef, Object value) {
        switch (fieldDef.getId()) {
            case 10:
                this.name = (String) value;
                return;
            case 20:
                this.role = (String) value;
                return;
            case 30:
                this.roleInstance = (String) value;
                return;
            case MotionEventCompat.AXIS_GENERIC_9 /*40*/:
                this.location = (String) value;
                return;
            case GraphRequest.MAXIMUM_BATCH_SIZE /*50*/:
                this.roleVer = (String) value;
                return;
            case 60:
                this.environment = (String) value;
                return;
            case 70:
                this.deploymentUnit = (String) value;
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
        reset("cloud", "Microsoft.Telemetry.Extensions.cloud");
    }

    /* access modifiers changed from: protected */
    public void reset(String name2, String qualifiedName) {
        super.reset(name2, qualifiedName);
        this.name = "";
        this.role = "";
        this.roleInstance = "";
        this.location = "";
        this.roleVer = "";
        this.environment = "";
        this.deploymentUnit = "";
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
            this.name = reader.readString();
        }
        if (!canOmitFields || !reader.readFieldOmitted()) {
            this.role = reader.readString();
        }
        if (!canOmitFields || !reader.readFieldOmitted()) {
            this.roleInstance = reader.readString();
        }
        if (!canOmitFields || !reader.readFieldOmitted()) {
            this.location = reader.readString();
        }
        if (!canOmitFields || !reader.readFieldOmitted()) {
            this.roleVer = reader.readString();
        }
        if (!canOmitFields || !reader.readFieldOmitted()) {
            this.environment = reader.readString();
        }
        if (!canOmitFields || !reader.readFieldOmitted()) {
            this.deploymentUnit = reader.readString();
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
                        this.name = ReadHelper.readString(reader, fieldTag.type);
                        break;
                    case 20:
                        this.role = ReadHelper.readString(reader, fieldTag.type);
                        break;
                    case 30:
                        this.roleInstance = ReadHelper.readString(reader, fieldTag.type);
                        break;
                    case MotionEventCompat.AXIS_GENERIC_9 /*40*/:
                        this.location = ReadHelper.readString(reader, fieldTag.type);
                        break;
                    case GraphRequest.MAXIMUM_BATCH_SIZE /*50*/:
                        this.roleVer = ReadHelper.readString(reader, fieldTag.type);
                        break;
                    case 60:
                        this.environment = ReadHelper.readString(reader, fieldTag.type);
                        break;
                    case 70:
                        this.deploymentUnit = ReadHelper.readString(reader, fieldTag.type);
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
        writer.writeFieldBegin(BondDataType.BT_STRING, 10, Schema.name_metadata);
        writer.writeString(this.name);
        writer.writeFieldEnd();
        writer.writeFieldBegin(BondDataType.BT_STRING, 20, Schema.role_metadata);
        writer.writeString(this.role);
        writer.writeFieldEnd();
        writer.writeFieldBegin(BondDataType.BT_STRING, 30, Schema.roleInstance_metadata);
        writer.writeString(this.roleInstance);
        writer.writeFieldEnd();
        writer.writeFieldBegin(BondDataType.BT_STRING, 40, Schema.location_metadata);
        writer.writeString(this.location);
        writer.writeFieldEnd();
        if (!canOmitFields || this.roleVer != Schema.roleVer_metadata.getDefault_value().getString_value()) {
            writer.writeFieldBegin(BondDataType.BT_STRING, 50, Schema.roleVer_metadata);
            writer.writeString(this.roleVer);
            writer.writeFieldEnd();
        } else {
            writer.writeFieldOmitted(BondDataType.BT_STRING, 50, Schema.roleVer_metadata);
        }
        if (!canOmitFields || this.environment != Schema.environment_metadata.getDefault_value().getString_value()) {
            writer.writeFieldBegin(BondDataType.BT_STRING, 60, Schema.environment_metadata);
            writer.writeString(this.environment);
            writer.writeFieldEnd();
        } else {
            writer.writeFieldOmitted(BondDataType.BT_STRING, 60, Schema.environment_metadata);
        }
        if (!canOmitFields || this.deploymentUnit != Schema.deploymentUnit_metadata.getDefault_value().getString_value()) {
            writer.writeFieldBegin(BondDataType.BT_STRING, 70, Schema.deploymentUnit_metadata);
            writer.writeString(this.deploymentUnit);
            writer.writeFieldEnd();
        } else {
            writer.writeFieldOmitted(BondDataType.BT_STRING, 70, Schema.deploymentUnit_metadata);
        }
        writer.writeStructEnd(isBase);
    }

    public boolean memberwiseCompare(Object obj) {
        if (obj == null) {
            return false;
        }
        cloud that = (cloud) obj;
        if (!memberwiseCompareQuick(that) || !memberwiseCompareDeep(that)) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:102:0x00c1  */
    /* JADX WARNING: Removed duplicated region for block: B:108:0x00db  */
    /* JADX WARNING: Removed duplicated region for block: B:114:0x00f5  */
    /* JADX WARNING: Removed duplicated region for block: B:120:0x010f  */
    /* JADX WARNING: Removed duplicated region for block: B:126:0x0129  */
    /* JADX WARNING: Removed duplicated region for block: B:132:0x0143  */
    /* JADX WARNING: Removed duplicated region for block: B:138:0x015d  */
    /* JADX WARNING: Removed duplicated region for block: B:17:0x0021  */
    /* JADX WARNING: Removed duplicated region for block: B:19:0x0024  */
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
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean memberwiseCompareQuick(Microsoft.Telemetry.Extensions.cloud r6) {
        /*
            r5 = this;
            r2 = 1
            r3 = 0
            r0 = 1
            if (r0 == 0) goto L_0x00a7
            boolean r1 = super.memberwiseCompareQuick(r6)
            if (r1 == 0) goto L_0x00a7
            r0 = r2
        L_0x000c:
            if (r0 == 0) goto L_0x00b0
            java.lang.String r1 = r5.name
            if (r1 != 0) goto L_0x00aa
            r1 = r2
        L_0x0013:
            java.lang.String r4 = r6.name
            if (r4 != 0) goto L_0x00ad
            r4 = r2
        L_0x0018:
            if (r1 != r4) goto L_0x00b0
            r0 = r2
        L_0x001b:
            if (r0 == 0) goto L_0x00c1
            java.lang.String r1 = r5.name
            if (r1 != 0) goto L_0x00b3
        L_0x0021:
            r0 = r2
        L_0x0022:
            if (r0 == 0) goto L_0x00ca
            java.lang.String r1 = r5.role
            if (r1 != 0) goto L_0x00c4
            r1 = r2
        L_0x0029:
            java.lang.String r4 = r6.role
            if (r4 != 0) goto L_0x00c7
            r4 = r2
        L_0x002e:
            if (r1 != r4) goto L_0x00ca
            r0 = r2
        L_0x0031:
            if (r0 == 0) goto L_0x00db
            java.lang.String r1 = r5.role
            if (r1 != 0) goto L_0x00cd
        L_0x0037:
            r0 = r2
        L_0x0038:
            if (r0 == 0) goto L_0x00e4
            java.lang.String r1 = r5.roleInstance
            if (r1 != 0) goto L_0x00de
            r1 = r2
        L_0x003f:
            java.lang.String r4 = r6.roleInstance
            if (r4 != 0) goto L_0x00e1
            r4 = r2
        L_0x0044:
            if (r1 != r4) goto L_0x00e4
            r0 = r2
        L_0x0047:
            if (r0 == 0) goto L_0x00f5
            java.lang.String r1 = r5.roleInstance
            if (r1 != 0) goto L_0x00e7
        L_0x004d:
            r0 = r2
        L_0x004e:
            if (r0 == 0) goto L_0x00fe
            java.lang.String r1 = r5.location
            if (r1 != 0) goto L_0x00f8
            r1 = r2
        L_0x0055:
            java.lang.String r4 = r6.location
            if (r4 != 0) goto L_0x00fb
            r4 = r2
        L_0x005a:
            if (r1 != r4) goto L_0x00fe
            r0 = r2
        L_0x005d:
            if (r0 == 0) goto L_0x010f
            java.lang.String r1 = r5.location
            if (r1 != 0) goto L_0x0101
        L_0x0063:
            r0 = r2
        L_0x0064:
            if (r0 == 0) goto L_0x0118
            java.lang.String r1 = r5.roleVer
            if (r1 != 0) goto L_0x0112
            r1 = r2
        L_0x006b:
            java.lang.String r4 = r6.roleVer
            if (r4 != 0) goto L_0x0115
            r4 = r2
        L_0x0070:
            if (r1 != r4) goto L_0x0118
            r0 = r2
        L_0x0073:
            if (r0 == 0) goto L_0x0129
            java.lang.String r1 = r5.roleVer
            if (r1 != 0) goto L_0x011b
        L_0x0079:
            r0 = r2
        L_0x007a:
            if (r0 == 0) goto L_0x0132
            java.lang.String r1 = r5.environment
            if (r1 != 0) goto L_0x012c
            r1 = r2
        L_0x0081:
            java.lang.String r4 = r6.environment
            if (r4 != 0) goto L_0x012f
            r4 = r2
        L_0x0086:
            if (r1 != r4) goto L_0x0132
            r0 = r2
        L_0x0089:
            if (r0 == 0) goto L_0x0143
            java.lang.String r1 = r5.environment
            if (r1 != 0) goto L_0x0135
        L_0x008f:
            r0 = r2
        L_0x0090:
            if (r0 == 0) goto L_0x014c
            java.lang.String r1 = r5.deploymentUnit
            if (r1 != 0) goto L_0x0146
            r1 = r2
        L_0x0097:
            java.lang.String r4 = r6.deploymentUnit
            if (r4 != 0) goto L_0x0149
            r4 = r2
        L_0x009c:
            if (r1 != r4) goto L_0x014c
            r0 = r2
        L_0x009f:
            if (r0 == 0) goto L_0x015d
            java.lang.String r1 = r5.deploymentUnit
            if (r1 != 0) goto L_0x014f
        L_0x00a5:
            r0 = r2
        L_0x00a6:
            return r0
        L_0x00a7:
            r0 = r3
            goto L_0x000c
        L_0x00aa:
            r1 = r3
            goto L_0x0013
        L_0x00ad:
            r4 = r3
            goto L_0x0018
        L_0x00b0:
            r0 = r3
            goto L_0x001b
        L_0x00b3:
            java.lang.String r1 = r5.name
            int r1 = r1.length()
            java.lang.String r4 = r6.name
            int r4 = r4.length()
            if (r1 == r4) goto L_0x0021
        L_0x00c1:
            r0 = r3
            goto L_0x0022
        L_0x00c4:
            r1 = r3
            goto L_0x0029
        L_0x00c7:
            r4 = r3
            goto L_0x002e
        L_0x00ca:
            r0 = r3
            goto L_0x0031
        L_0x00cd:
            java.lang.String r1 = r5.role
            int r1 = r1.length()
            java.lang.String r4 = r6.role
            int r4 = r4.length()
            if (r1 == r4) goto L_0x0037
        L_0x00db:
            r0 = r3
            goto L_0x0038
        L_0x00de:
            r1 = r3
            goto L_0x003f
        L_0x00e1:
            r4 = r3
            goto L_0x0044
        L_0x00e4:
            r0 = r3
            goto L_0x0047
        L_0x00e7:
            java.lang.String r1 = r5.roleInstance
            int r1 = r1.length()
            java.lang.String r4 = r6.roleInstance
            int r4 = r4.length()
            if (r1 == r4) goto L_0x004d
        L_0x00f5:
            r0 = r3
            goto L_0x004e
        L_0x00f8:
            r1 = r3
            goto L_0x0055
        L_0x00fb:
            r4 = r3
            goto L_0x005a
        L_0x00fe:
            r0 = r3
            goto L_0x005d
        L_0x0101:
            java.lang.String r1 = r5.location
            int r1 = r1.length()
            java.lang.String r4 = r6.location
            int r4 = r4.length()
            if (r1 == r4) goto L_0x0063
        L_0x010f:
            r0 = r3
            goto L_0x0064
        L_0x0112:
            r1 = r3
            goto L_0x006b
        L_0x0115:
            r4 = r3
            goto L_0x0070
        L_0x0118:
            r0 = r3
            goto L_0x0073
        L_0x011b:
            java.lang.String r1 = r5.roleVer
            int r1 = r1.length()
            java.lang.String r4 = r6.roleVer
            int r4 = r4.length()
            if (r1 == r4) goto L_0x0079
        L_0x0129:
            r0 = r3
            goto L_0x007a
        L_0x012c:
            r1 = r3
            goto L_0x0081
        L_0x012f:
            r4 = r3
            goto L_0x0086
        L_0x0132:
            r0 = r3
            goto L_0x0089
        L_0x0135:
            java.lang.String r1 = r5.environment
            int r1 = r1.length()
            java.lang.String r4 = r6.environment
            int r4 = r4.length()
            if (r1 == r4) goto L_0x008f
        L_0x0143:
            r0 = r3
            goto L_0x0090
        L_0x0146:
            r1 = r3
            goto L_0x0097
        L_0x0149:
            r4 = r3
            goto L_0x009c
        L_0x014c:
            r0 = r3
            goto L_0x009f
        L_0x014f:
            java.lang.String r1 = r5.deploymentUnit
            int r1 = r1.length()
            java.lang.String r4 = r6.deploymentUnit
            int r4 = r4.length()
            if (r1 == r4) goto L_0x00a5
        L_0x015d:
            r0 = r3
            goto L_0x00a6
        */
        throw new UnsupportedOperationException("Method not decompiled: Microsoft.Telemetry.Extensions.cloud.memberwiseCompareQuick(Microsoft.Telemetry.Extensions.cloud):boolean");
    }

    /* access modifiers changed from: protected */
    public boolean memberwiseCompareDeep(cloud that) {
        return (((((((1 != 0 && super.memberwiseCompareDeep(that)) && (this.name == null || this.name.equals(that.name))) && (this.role == null || this.role.equals(that.role))) && (this.roleInstance == null || this.roleInstance.equals(that.roleInstance))) && (this.location == null || this.location.equals(that.location))) && (this.roleVer == null || this.roleVer.equals(that.roleVer))) && (this.environment == null || this.environment.equals(that.environment))) && (this.deploymentUnit == null || this.deploymentUnit.equals(that.deploymentUnit));
    }
}
