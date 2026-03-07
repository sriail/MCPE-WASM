package com.microsoft.xbox.idp.telemetry.utc;

import Microsoft.Telemetry.Base;
import android.support.v4.media.TransportMediator;
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
import com.microsoft.xbox.xle.app.ImageUtil;
import java.io.IOException;
import java.io.InputStream;

public class CommonData extends Base {
    private String accessibilityInfo;
    private String additionalInfo;
    private String appName;
    private String appSessionId;
    private String clientLanguage;
    private String deviceModel;
    private String eventVersion;
    private int network;
    private String sandboxId;
    private String titleDeviceId;
    private String titleSessionId;
    private String userId;
    private String xsapiVersion;

    public BondSerializable clone() {
        return null;
    }

    public final String getEventVersion() {
        return this.eventVersion;
    }

    public final void setEventVersion(String value) {
        this.eventVersion = value;
    }

    public final String getDeviceModel() {
        return this.deviceModel;
    }

    public final void setDeviceModel(String value) {
        this.deviceModel = value;
    }

    public final String getXsapiVersion() {
        return this.xsapiVersion;
    }

    public final void setXsapiVersion(String value) {
        this.xsapiVersion = value;
    }

    public final String getAppName() {
        return this.appName;
    }

    public final void setAppName(String value) {
        this.appName = value;
    }

    public final String getClientLanguage() {
        return this.clientLanguage;
    }

    public final void setClientLanguage(String value) {
        this.clientLanguage = value;
    }

    public final int getNetwork() {
        return this.network;
    }

    public final void setNetwork(int value) {
        this.network = value;
    }

    public final String getSandboxId() {
        return this.sandboxId;
    }

    public final void setSandboxId(String value) {
        this.sandboxId = value;
    }

    public final String getAppSessionId() {
        return this.appSessionId;
    }

    public final void setAppSessionId(String value) {
        this.appSessionId = value;
    }

    public final String getUserId() {
        return this.userId;
    }

    public final void setUserId(String value) {
        this.userId = value;
    }

    public final String getAdditionalInfo() {
        return this.additionalInfo;
    }

    public final void setAdditionalInfo(String value) {
        this.additionalInfo = value;
    }

    public final String getAccessibilityInfo() {
        return this.accessibilityInfo;
    }

    public final void setAccessibilityInfo(String value) {
        this.accessibilityInfo = value;
    }

    public final String getTitleDeviceId() {
        return this.titleDeviceId;
    }

    public final void setTitleDeviceId(String value) {
        this.titleDeviceId = value;
    }

    public final String getTitleSessionId() {
        return this.titleSessionId;
    }

    public final void setTitleSessionId(String value) {
        this.titleSessionId = value;
    }

    public static class Schema {
        /* access modifiers changed from: private */
        public static final Metadata accessibilityInfo_metadata = new Metadata();
        /* access modifiers changed from: private */
        public static final Metadata additionalInfo_metadata = new Metadata();
        /* access modifiers changed from: private */
        public static final Metadata appName_metadata = new Metadata();
        /* access modifiers changed from: private */
        public static final Metadata appSessionId_metadata = new Metadata();
        /* access modifiers changed from: private */
        public static final Metadata clientLanguage_metadata = new Metadata();
        /* access modifiers changed from: private */
        public static final Metadata deviceModel_metadata = new Metadata();
        /* access modifiers changed from: private */
        public static final Metadata eventVersion_metadata = new Metadata();
        public static final Metadata metadata = new Metadata();
        /* access modifiers changed from: private */
        public static final Metadata network_metadata = new Metadata();
        /* access modifiers changed from: private */
        public static final Metadata sandboxId_metadata = new Metadata();
        public static final SchemaDef schemaDef = new SchemaDef();
        /* access modifiers changed from: private */
        public static final Metadata titleDeviceId_metadata = new Metadata();
        /* access modifiers changed from: private */
        public static final Metadata titleSessionId_metadata = new Metadata();
        /* access modifiers changed from: private */
        public static final Metadata userId_metadata = new Metadata();
        /* access modifiers changed from: private */
        public static final Metadata xsapiVersion_metadata = new Metadata();

        static {
            metadata.setName("CommonData");
            metadata.setQualified_name("com.microsoft.xbox.idp.telemetry.utc.CommonData");
            metadata.getAttributes().put("Description", "OnlineId base event with required fields");
            eventVersion_metadata.setName("eventVersion");
            eventVersion_metadata.setModifier(Modifier.Required);
            eventVersion_metadata.getAttributes().put("Description", "The event's version in the form of A.B.C where each subfield is the version for Part A, B, or C respectively.  This helps the backend cookers and processers adjust to different versions of the event schema");
            deviceModel_metadata.setName("deviceModel");
            deviceModel_metadata.setModifier(Modifier.Required);
            deviceModel_metadata.getAttributes().put("Description", "The specific model of the device.  On Android this is from the constant: android.os.Build.MODEL.  NOTE: For completeness, one should prepend android.os.Build.MANUFACTURER to this value if the MFG name is not part of the model name.");
            xsapiVersion_metadata.setName("xsapiVersion");
            xsapiVersion_metadata.setModifier(Modifier.Required);
            xsapiVersion_metadata.getAttributes().put("Description", "The xsapi version.  Should get this from the xsapi build properties");
            appName_metadata.setName("appName");
            appName_metadata.setModifier(Modifier.Required);
            appName_metadata.getAttributes().put("Description", "The application name");
            clientLanguage_metadata.setName("clientLanguage");
            clientLanguage_metadata.setModifier(Modifier.Required);
            clientLanguage_metadata.getAttributes().put("Description", "The system language-region (for example, en-US = english in USA).");
            network_metadata.setName("network");
            network_metadata.setModifier(Modifier.Required);
            network_metadata.getAttributes().put("Description", "The network connection being used (0 = unknown | 1 = wifi | 2 = cellular | 3 = wired)");
            network_metadata.getDefault_value().setUint_value(0);
            sandboxId_metadata.setName("sandboxId");
            sandboxId_metadata.setModifier(Modifier.Required);
            sandboxId_metadata.getAttributes().put("Description", "The xsapi sandbox for service calls");
            appSessionId_metadata.setName("appSessionId");
            appSessionId_metadata.setModifier(Modifier.Required);
            appSessionId_metadata.getAttributes().put("Description", "The sessionId for the app; gets set on first use of telemetry -- useful for binding events together into scenarios and analyzing flow");
            userId_metadata.setName("userId");
            userId_metadata.setModifier(Modifier.Required);
            userId_metadata.getAttributes().put("Description", "The User Id");
            additionalInfo_metadata.setName("additionalInfo");
            additionalInfo_metadata.setModifier(Modifier.Required);
            additionalInfo_metadata.getAttributes().put("Description", "The json key-value collection of data that gives greater meaning to the event");
            accessibilityInfo_metadata.setName("accessibilityInfo");
            accessibilityInfo_metadata.setModifier(Modifier.Required);
            accessibilityInfo_metadata.getAttributes().put("Description", "The json key-value collection of accessibility settings -- information within will differ by platform");
            titleDeviceId_metadata.setName("titleDeviceId");
            titleDeviceId_metadata.setModifier(Modifier.Required);
            titleDeviceId_metadata.getAttributes().put("Description", "The device guid from the title hosting xsapi idp or tcui");
            titleSessionId_metadata.setName("titleSessionId");
            titleSessionId_metadata.setModifier(Modifier.Required);
            titleSessionId_metadata.getAttributes().put("Description", "The session guid from the title hosting xsapi idp or tcui");
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
                    structDef.setBase_def(Base.Schema.getTypeDef(schema));
                    FieldDef field = new FieldDef();
                    field.setId(10);
                    field.setMetadata(eventVersion_metadata);
                    field.getType().setId(BondDataType.BT_WSTRING);
                    structDef.getFields().add(field);
                    FieldDef field2 = new FieldDef();
                    field2.setId(20);
                    field2.setMetadata(deviceModel_metadata);
                    field2.getType().setId(BondDataType.BT_WSTRING);
                    structDef.getFields().add(field2);
                    FieldDef field3 = new FieldDef();
                    field3.setId(30);
                    field3.setMetadata(xsapiVersion_metadata);
                    field3.getType().setId(BondDataType.BT_WSTRING);
                    structDef.getFields().add(field3);
                    FieldDef field4 = new FieldDef();
                    field4.setId(40);
                    field4.setMetadata(appName_metadata);
                    field4.getType().setId(BondDataType.BT_WSTRING);
                    structDef.getFields().add(field4);
                    FieldDef field5 = new FieldDef();
                    field5.setId(50);
                    field5.setMetadata(clientLanguage_metadata);
                    field5.getType().setId(BondDataType.BT_WSTRING);
                    structDef.getFields().add(field5);
                    FieldDef field6 = new FieldDef();
                    field6.setId(60);
                    field6.setMetadata(network_metadata);
                    field6.getType().setId(BondDataType.BT_UINT32);
                    structDef.getFields().add(field6);
                    FieldDef field7 = new FieldDef();
                    field7.setId(70);
                    field7.setMetadata(sandboxId_metadata);
                    field7.getType().setId(BondDataType.BT_WSTRING);
                    structDef.getFields().add(field7);
                    FieldDef field8 = new FieldDef();
                    field8.setId(80);
                    field8.setMetadata(appSessionId_metadata);
                    field8.getType().setId(BondDataType.BT_WSTRING);
                    structDef.getFields().add(field8);
                    FieldDef field9 = new FieldDef();
                    field9.setId(90);
                    field9.setMetadata(userId_metadata);
                    field9.getType().setId(BondDataType.BT_WSTRING);
                    structDef.getFields().add(field9);
                    FieldDef field10 = new FieldDef();
                    field10.setId(100);
                    field10.setMetadata(additionalInfo_metadata);
                    field10.getType().setId(BondDataType.BT_WSTRING);
                    structDef.getFields().add(field10);
                    FieldDef field11 = new FieldDef();
                    field11.setId(110);
                    field11.setMetadata(accessibilityInfo_metadata);
                    field11.getType().setId(BondDataType.BT_WSTRING);
                    structDef.getFields().add(field11);
                    FieldDef field12 = new FieldDef();
                    field12.setId(120);
                    field12.setMetadata(titleDeviceId_metadata);
                    field12.getType().setId(BondDataType.BT_WSTRING);
                    structDef.getFields().add(field12);
                    FieldDef field13 = new FieldDef();
                    field13.setId(130);
                    field13.setMetadata(titleSessionId_metadata);
                    field13.getType().setId(BondDataType.BT_WSTRING);
                    structDef.getFields().add(field13);
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
                return this.eventVersion;
            case 20:
                return this.deviceModel;
            case 30:
                return this.xsapiVersion;
            case MotionEventCompat.AXIS_GENERIC_9:
                return this.appName;
            case GraphRequest.MAXIMUM_BATCH_SIZE:
                return this.clientLanguage;
            case 60:
                return Integer.valueOf(this.network);
            case 70:
                return this.sandboxId;
            case 80:
                return this.appSessionId;
            case 90:
                return this.userId;
            case ImageUtil.TINY /*100*/:
                return this.additionalInfo;
            case 110:
                return this.accessibilityInfo;
            case 120:
                return this.titleDeviceId;
            case TransportMediator.KEYCODE_MEDIA_RECORD:
                return this.titleSessionId;
            default:
                return null;
        }
    }

    public void setField(FieldDef fieldDef, Object value) {
        switch (fieldDef.getId()) {
            case 10:
                this.eventVersion = (String) value;
                return;
            case 20:
                this.deviceModel = (String) value;
                return;
            case 30:
                this.xsapiVersion = (String) value;
                return;
            case MotionEventCompat.AXIS_GENERIC_9:
                this.appName = (String) value;
                return;
            case GraphRequest.MAXIMUM_BATCH_SIZE:
                this.clientLanguage = (String) value;
                return;
            case 60:
                this.network = ((Integer) value).intValue();
                return;
            case 70:
                this.sandboxId = (String) value;
                return;
            case 80:
                this.appSessionId = (String) value;
                return;
            case 90:
                this.userId = (String) value;
                return;
            case ImageUtil.TINY /*100*/:
                this.additionalInfo = (String) value;
                return;
            case 110:
                this.accessibilityInfo = (String) value;
                return;
            case 120:
                this.titleDeviceId = (String) value;
                return;
            case TransportMediator.KEYCODE_MEDIA_RECORD:
                this.titleSessionId = (String) value;
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
        reset("CommonData", "com.microsoft.xbox.idp.telemetry.utc.CommonData");
    }

    /* access modifiers changed from: protected */
    public void reset(String name, String qualifiedName) {
        super.reset(name, qualifiedName);
        this.eventVersion = "";
        this.deviceModel = "";
        this.xsapiVersion = "";
        this.appName = "";
        this.clientLanguage = "";
        this.network = 0;
        this.sandboxId = "";
        this.appSessionId = "";
        this.userId = "";
        this.additionalInfo = "";
        this.accessibilityInfo = "";
        this.titleDeviceId = "";
        this.titleSessionId = "";
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
            this.eventVersion = reader.readWString();
        }
        if (!canOmitFields || !reader.readFieldOmitted()) {
            this.deviceModel = reader.readWString();
        }
        if (!canOmitFields || !reader.readFieldOmitted()) {
            this.xsapiVersion = reader.readWString();
        }
        if (!canOmitFields || !reader.readFieldOmitted()) {
            this.appName = reader.readWString();
        }
        if (!canOmitFields || !reader.readFieldOmitted()) {
            this.clientLanguage = reader.readWString();
        }
        if (!canOmitFields || !reader.readFieldOmitted()) {
            this.network = reader.readUInt32();
        }
        if (!canOmitFields || !reader.readFieldOmitted()) {
            this.sandboxId = reader.readWString();
        }
        if (!canOmitFields || !reader.readFieldOmitted()) {
            this.appSessionId = reader.readWString();
        }
        if (!canOmitFields || !reader.readFieldOmitted()) {
            this.userId = reader.readWString();
        }
        if (!canOmitFields || !reader.readFieldOmitted()) {
            this.additionalInfo = reader.readWString();
        }
        if (!canOmitFields || !reader.readFieldOmitted()) {
            this.accessibilityInfo = reader.readWString();
        }
        if (!canOmitFields || !reader.readFieldOmitted()) {
            this.titleDeviceId = reader.readWString();
        }
        if (!canOmitFields || !reader.readFieldOmitted()) {
            this.titleSessionId = reader.readWString();
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
                        this.eventVersion = ReadHelper.readWString(reader, fieldTag.type);
                        break;
                    case 20:
                        this.deviceModel = ReadHelper.readWString(reader, fieldTag.type);
                        break;
                    case 30:
                        this.xsapiVersion = ReadHelper.readWString(reader, fieldTag.type);
                        break;
                    case MotionEventCompat.AXIS_GENERIC_9:
                        this.appName = ReadHelper.readWString(reader, fieldTag.type);
                        break;
                    case GraphRequest.MAXIMUM_BATCH_SIZE:
                        this.clientLanguage = ReadHelper.readWString(reader, fieldTag.type);
                        break;
                    case 60:
                        this.network = ReadHelper.readUInt32(reader, fieldTag.type);
                        break;
                    case 70:
                        this.sandboxId = ReadHelper.readWString(reader, fieldTag.type);
                        break;
                    case 80:
                        this.appSessionId = ReadHelper.readWString(reader, fieldTag.type);
                        break;
                    case 90:
                        this.userId = ReadHelper.readWString(reader, fieldTag.type);
                        break;
                    case ImageUtil.TINY /*100*/:
                        this.additionalInfo = ReadHelper.readWString(reader, fieldTag.type);
                        break;
                    case 110:
                        this.accessibilityInfo = ReadHelper.readWString(reader, fieldTag.type);
                        break;
                    case 120:
                        this.titleDeviceId = ReadHelper.readWString(reader, fieldTag.type);
                        break;
                    case TransportMediator.KEYCODE_MEDIA_RECORD:
                        this.titleSessionId = ReadHelper.readWString(reader, fieldTag.type);
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
        writer.writeFieldBegin(BondDataType.BT_WSTRING, 10, Schema.eventVersion_metadata);
        writer.writeWString(this.eventVersion);
        writer.writeFieldEnd();
        writer.writeFieldBegin(BondDataType.BT_WSTRING, 20, Schema.deviceModel_metadata);
        writer.writeWString(this.deviceModel);
        writer.writeFieldEnd();
        writer.writeFieldBegin(BondDataType.BT_WSTRING, 30, Schema.xsapiVersion_metadata);
        writer.writeWString(this.xsapiVersion);
        writer.writeFieldEnd();
        writer.writeFieldBegin(BondDataType.BT_WSTRING, 40, Schema.appName_metadata);
        writer.writeWString(this.appName);
        writer.writeFieldEnd();
        writer.writeFieldBegin(BondDataType.BT_WSTRING, 50, Schema.clientLanguage_metadata);
        writer.writeWString(this.clientLanguage);
        writer.writeFieldEnd();
        writer.writeFieldBegin(BondDataType.BT_UINT32, 60, Schema.network_metadata);
        writer.writeUInt32(this.network);
        writer.writeFieldEnd();
        writer.writeFieldBegin(BondDataType.BT_WSTRING, 70, Schema.sandboxId_metadata);
        writer.writeWString(this.sandboxId);
        writer.writeFieldEnd();
        writer.writeFieldBegin(BondDataType.BT_WSTRING, 80, Schema.appSessionId_metadata);
        writer.writeWString(this.appSessionId);
        writer.writeFieldEnd();
        writer.writeFieldBegin(BondDataType.BT_WSTRING, 90, Schema.userId_metadata);
        writer.writeWString(this.userId);
        writer.writeFieldEnd();
        writer.writeFieldBegin(BondDataType.BT_WSTRING, 100, Schema.additionalInfo_metadata);
        writer.writeWString(this.additionalInfo);
        writer.writeFieldEnd();
        writer.writeFieldBegin(BondDataType.BT_WSTRING, 110, Schema.accessibilityInfo_metadata);
        writer.writeWString(this.accessibilityInfo);
        writer.writeFieldEnd();
        writer.writeFieldBegin(BondDataType.BT_WSTRING, 120, Schema.titleDeviceId_metadata);
        writer.writeWString(this.titleDeviceId);
        writer.writeFieldEnd();
        writer.writeFieldBegin(BondDataType.BT_WSTRING, TransportMediator.KEYCODE_MEDIA_RECORD, Schema.titleSessionId_metadata);
        writer.writeWString(this.titleSessionId);
        writer.writeFieldEnd();
        writer.writeStructEnd(isBase);
    }

    public boolean memberwiseCompare(Object obj) {
        if (obj == null) {
            return false;
        }
        CommonData that = (CommonData) obj;
        if (!memberwiseCompareQuick(that) || !memberwiseCompareDeep(that)) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:101:0x00b1  */
    /* JADX WARNING: Removed duplicated region for block: B:112:0x00c4  */
    /* JADX WARNING: Removed duplicated region for block: B:114:0x00c7  */
    /* JADX WARNING: Removed duplicated region for block: B:125:0x00da  */
    /* JADX WARNING: Removed duplicated region for block: B:127:0x00dd  */
    /* JADX WARNING: Removed duplicated region for block: B:138:0x00f0  */
    /* JADX WARNING: Removed duplicated region for block: B:140:0x00f3  */
    /* JADX WARNING: Removed duplicated region for block: B:151:0x0106  */
    /* JADX WARNING: Removed duplicated region for block: B:153:0x0109  */
    /* JADX WARNING: Removed duplicated region for block: B:164:0x011c  */
    /* JADX WARNING: Removed duplicated region for block: B:171:0x0138  */
    /* JADX WARNING: Removed duplicated region for block: B:177:0x0152  */
    /* JADX WARNING: Removed duplicated region for block: B:17:0x0021  */
    /* JADX WARNING: Removed duplicated region for block: B:183:0x016c  */
    /* JADX WARNING: Removed duplicated region for block: B:189:0x0186  */
    /* JADX WARNING: Removed duplicated region for block: B:195:0x01a0  */
    /* JADX WARNING: Removed duplicated region for block: B:196:0x01a3  */
    /* JADX WARNING: Removed duplicated region for block: B:19:0x0024  */
    /* JADX WARNING: Removed duplicated region for block: B:202:0x01bd  */
    /* JADX WARNING: Removed duplicated region for block: B:208:0x01d7  */
    /* JADX WARNING: Removed duplicated region for block: B:214:0x01f1  */
    /* JADX WARNING: Removed duplicated region for block: B:220:0x020b  */
    /* JADX WARNING: Removed duplicated region for block: B:226:0x0225  */
    /* JADX WARNING: Removed duplicated region for block: B:232:0x023f  */
    /* JADX WARNING: Removed duplicated region for block: B:238:0x0259  */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x0037  */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x003a  */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x004d  */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x0050  */
    /* JADX WARNING: Removed duplicated region for block: B:56:0x0063  */
    /* JADX WARNING: Removed duplicated region for block: B:58:0x0066  */
    /* JADX WARNING: Removed duplicated region for block: B:69:0x0079  */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x0082  */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x0085  */
    /* JADX WARNING: Removed duplicated region for block: B:86:0x0098  */
    /* JADX WARNING: Removed duplicated region for block: B:88:0x009b  */
    /* JADX WARNING: Removed duplicated region for block: B:99:0x00ae  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean memberwiseCompareQuick(com.microsoft.xbox.idp.telemetry.utc.CommonData r6) {
        /*
            r5 = this;
            r2 = 1
            r3 = 0
            r0 = 1
            if (r0 == 0) goto L_0x011e
            boolean r1 = super.memberwiseCompareQuick(r6)
            if (r1 == 0) goto L_0x011e
            r0 = r2
        L_0x000c:
            if (r0 == 0) goto L_0x0127
            java.lang.String r1 = r5.eventVersion
            if (r1 != 0) goto L_0x0121
            r1 = r2
        L_0x0013:
            java.lang.String r4 = r6.eventVersion
            if (r4 != 0) goto L_0x0124
            r4 = r2
        L_0x0018:
            if (r1 != r4) goto L_0x0127
            r0 = r2
        L_0x001b:
            if (r0 == 0) goto L_0x0138
            java.lang.String r1 = r5.eventVersion
            if (r1 != 0) goto L_0x012a
        L_0x0021:
            r0 = r2
        L_0x0022:
            if (r0 == 0) goto L_0x0141
            java.lang.String r1 = r5.deviceModel
            if (r1 != 0) goto L_0x013b
            r1 = r2
        L_0x0029:
            java.lang.String r4 = r6.deviceModel
            if (r4 != 0) goto L_0x013e
            r4 = r2
        L_0x002e:
            if (r1 != r4) goto L_0x0141
            r0 = r2
        L_0x0031:
            if (r0 == 0) goto L_0x0152
            java.lang.String r1 = r5.deviceModel
            if (r1 != 0) goto L_0x0144
        L_0x0037:
            r0 = r2
        L_0x0038:
            if (r0 == 0) goto L_0x015b
            java.lang.String r1 = r5.xsapiVersion
            if (r1 != 0) goto L_0x0155
            r1 = r2
        L_0x003f:
            java.lang.String r4 = r6.xsapiVersion
            if (r4 != 0) goto L_0x0158
            r4 = r2
        L_0x0044:
            if (r1 != r4) goto L_0x015b
            r0 = r2
        L_0x0047:
            if (r0 == 0) goto L_0x016c
            java.lang.String r1 = r5.xsapiVersion
            if (r1 != 0) goto L_0x015e
        L_0x004d:
            r0 = r2
        L_0x004e:
            if (r0 == 0) goto L_0x0175
            java.lang.String r1 = r5.appName
            if (r1 != 0) goto L_0x016f
            r1 = r2
        L_0x0055:
            java.lang.String r4 = r6.appName
            if (r4 != 0) goto L_0x0172
            r4 = r2
        L_0x005a:
            if (r1 != r4) goto L_0x0175
            r0 = r2
        L_0x005d:
            if (r0 == 0) goto L_0x0186
            java.lang.String r1 = r5.appName
            if (r1 != 0) goto L_0x0178
        L_0x0063:
            r0 = r2
        L_0x0064:
            if (r0 == 0) goto L_0x018f
            java.lang.String r1 = r5.clientLanguage
            if (r1 != 0) goto L_0x0189
            r1 = r2
        L_0x006b:
            java.lang.String r4 = r6.clientLanguage
            if (r4 != 0) goto L_0x018c
            r4 = r2
        L_0x0070:
            if (r1 != r4) goto L_0x018f
            r0 = r2
        L_0x0073:
            if (r0 == 0) goto L_0x01a0
            java.lang.String r1 = r5.clientLanguage
            if (r1 != 0) goto L_0x0192
        L_0x0079:
            r0 = r2
        L_0x007a:
            if (r0 == 0) goto L_0x01a3
            int r1 = r5.network
            int r4 = r6.network
            if (r1 != r4) goto L_0x01a3
            r0 = r2
        L_0x0083:
            if (r0 == 0) goto L_0x01ac
            java.lang.String r1 = r5.sandboxId
            if (r1 != 0) goto L_0x01a6
            r1 = r2
        L_0x008a:
            java.lang.String r4 = r6.sandboxId
            if (r4 != 0) goto L_0x01a9
            r4 = r2
        L_0x008f:
            if (r1 != r4) goto L_0x01ac
            r0 = r2
        L_0x0092:
            if (r0 == 0) goto L_0x01bd
            java.lang.String r1 = r5.sandboxId
            if (r1 != 0) goto L_0x01af
        L_0x0098:
            r0 = r2
        L_0x0099:
            if (r0 == 0) goto L_0x01c6
            java.lang.String r1 = r5.appSessionId
            if (r1 != 0) goto L_0x01c0
            r1 = r2
        L_0x00a0:
            java.lang.String r4 = r6.appSessionId
            if (r4 != 0) goto L_0x01c3
            r4 = r2
        L_0x00a5:
            if (r1 != r4) goto L_0x01c6
            r0 = r2
        L_0x00a8:
            if (r0 == 0) goto L_0x01d7
            java.lang.String r1 = r5.appSessionId
            if (r1 != 0) goto L_0x01c9
        L_0x00ae:
            r0 = r2
        L_0x00af:
            if (r0 == 0) goto L_0x01e0
            java.lang.String r1 = r5.userId
            if (r1 != 0) goto L_0x01da
            r1 = r2
        L_0x00b6:
            java.lang.String r4 = r6.userId
            if (r4 != 0) goto L_0x01dd
            r4 = r2
        L_0x00bb:
            if (r1 != r4) goto L_0x01e0
            r0 = r2
        L_0x00be:
            if (r0 == 0) goto L_0x01f1
            java.lang.String r1 = r5.userId
            if (r1 != 0) goto L_0x01e3
        L_0x00c4:
            r0 = r2
        L_0x00c5:
            if (r0 == 0) goto L_0x01fa
            java.lang.String r1 = r5.additionalInfo
            if (r1 != 0) goto L_0x01f4
            r1 = r2
        L_0x00cc:
            java.lang.String r4 = r6.additionalInfo
            if (r4 != 0) goto L_0x01f7
            r4 = r2
        L_0x00d1:
            if (r1 != r4) goto L_0x01fa
            r0 = r2
        L_0x00d4:
            if (r0 == 0) goto L_0x020b
            java.lang.String r1 = r5.additionalInfo
            if (r1 != 0) goto L_0x01fd
        L_0x00da:
            r0 = r2
        L_0x00db:
            if (r0 == 0) goto L_0x0214
            java.lang.String r1 = r5.accessibilityInfo
            if (r1 != 0) goto L_0x020e
            r1 = r2
        L_0x00e2:
            java.lang.String r4 = r6.accessibilityInfo
            if (r4 != 0) goto L_0x0211
            r4 = r2
        L_0x00e7:
            if (r1 != r4) goto L_0x0214
            r0 = r2
        L_0x00ea:
            if (r0 == 0) goto L_0x0225
            java.lang.String r1 = r5.accessibilityInfo
            if (r1 != 0) goto L_0x0217
        L_0x00f0:
            r0 = r2
        L_0x00f1:
            if (r0 == 0) goto L_0x022e
            java.lang.String r1 = r5.titleDeviceId
            if (r1 != 0) goto L_0x0228
            r1 = r2
        L_0x00f8:
            java.lang.String r4 = r6.titleDeviceId
            if (r4 != 0) goto L_0x022b
            r4 = r2
        L_0x00fd:
            if (r1 != r4) goto L_0x022e
            r0 = r2
        L_0x0100:
            if (r0 == 0) goto L_0x023f
            java.lang.String r1 = r5.titleDeviceId
            if (r1 != 0) goto L_0x0231
        L_0x0106:
            r0 = r2
        L_0x0107:
            if (r0 == 0) goto L_0x0248
            java.lang.String r1 = r5.titleSessionId
            if (r1 != 0) goto L_0x0242
            r1 = r2
        L_0x010e:
            java.lang.String r4 = r6.titleSessionId
            if (r4 != 0) goto L_0x0245
            r4 = r2
        L_0x0113:
            if (r1 != r4) goto L_0x0248
            r0 = r2
        L_0x0116:
            if (r0 == 0) goto L_0x0259
            java.lang.String r1 = r5.titleSessionId
            if (r1 != 0) goto L_0x024b
        L_0x011c:
            r0 = r2
        L_0x011d:
            return r0
        L_0x011e:
            r0 = r3
            goto L_0x000c
        L_0x0121:
            r1 = r3
            goto L_0x0013
        L_0x0124:
            r4 = r3
            goto L_0x0018
        L_0x0127:
            r0 = r3
            goto L_0x001b
        L_0x012a:
            java.lang.String r1 = r5.eventVersion
            int r1 = r1.length()
            java.lang.String r4 = r6.eventVersion
            int r4 = r4.length()
            if (r1 == r4) goto L_0x0021
        L_0x0138:
            r0 = r3
            goto L_0x0022
        L_0x013b:
            r1 = r3
            goto L_0x0029
        L_0x013e:
            r4 = r3
            goto L_0x002e
        L_0x0141:
            r0 = r3
            goto L_0x0031
        L_0x0144:
            java.lang.String r1 = r5.deviceModel
            int r1 = r1.length()
            java.lang.String r4 = r6.deviceModel
            int r4 = r4.length()
            if (r1 == r4) goto L_0x0037
        L_0x0152:
            r0 = r3
            goto L_0x0038
        L_0x0155:
            r1 = r3
            goto L_0x003f
        L_0x0158:
            r4 = r3
            goto L_0x0044
        L_0x015b:
            r0 = r3
            goto L_0x0047
        L_0x015e:
            java.lang.String r1 = r5.xsapiVersion
            int r1 = r1.length()
            java.lang.String r4 = r6.xsapiVersion
            int r4 = r4.length()
            if (r1 == r4) goto L_0x004d
        L_0x016c:
            r0 = r3
            goto L_0x004e
        L_0x016f:
            r1 = r3
            goto L_0x0055
        L_0x0172:
            r4 = r3
            goto L_0x005a
        L_0x0175:
            r0 = r3
            goto L_0x005d
        L_0x0178:
            java.lang.String r1 = r5.appName
            int r1 = r1.length()
            java.lang.String r4 = r6.appName
            int r4 = r4.length()
            if (r1 == r4) goto L_0x0063
        L_0x0186:
            r0 = r3
            goto L_0x0064
        L_0x0189:
            r1 = r3
            goto L_0x006b
        L_0x018c:
            r4 = r3
            goto L_0x0070
        L_0x018f:
            r0 = r3
            goto L_0x0073
        L_0x0192:
            java.lang.String r1 = r5.clientLanguage
            int r1 = r1.length()
            java.lang.String r4 = r6.clientLanguage
            int r4 = r4.length()
            if (r1 == r4) goto L_0x0079
        L_0x01a0:
            r0 = r3
            goto L_0x007a
        L_0x01a3:
            r0 = r3
            goto L_0x0083
        L_0x01a6:
            r1 = r3
            goto L_0x008a
        L_0x01a9:
            r4 = r3
            goto L_0x008f
        L_0x01ac:
            r0 = r3
            goto L_0x0092
        L_0x01af:
            java.lang.String r1 = r5.sandboxId
            int r1 = r1.length()
            java.lang.String r4 = r6.sandboxId
            int r4 = r4.length()
            if (r1 == r4) goto L_0x0098
        L_0x01bd:
            r0 = r3
            goto L_0x0099
        L_0x01c0:
            r1 = r3
            goto L_0x00a0
        L_0x01c3:
            r4 = r3
            goto L_0x00a5
        L_0x01c6:
            r0 = r3
            goto L_0x00a8
        L_0x01c9:
            java.lang.String r1 = r5.appSessionId
            int r1 = r1.length()
            java.lang.String r4 = r6.appSessionId
            int r4 = r4.length()
            if (r1 == r4) goto L_0x00ae
        L_0x01d7:
            r0 = r3
            goto L_0x00af
        L_0x01da:
            r1 = r3
            goto L_0x00b6
        L_0x01dd:
            r4 = r3
            goto L_0x00bb
        L_0x01e0:
            r0 = r3
            goto L_0x00be
        L_0x01e3:
            java.lang.String r1 = r5.userId
            int r1 = r1.length()
            java.lang.String r4 = r6.userId
            int r4 = r4.length()
            if (r1 == r4) goto L_0x00c4
        L_0x01f1:
            r0 = r3
            goto L_0x00c5
        L_0x01f4:
            r1 = r3
            goto L_0x00cc
        L_0x01f7:
            r4 = r3
            goto L_0x00d1
        L_0x01fa:
            r0 = r3
            goto L_0x00d4
        L_0x01fd:
            java.lang.String r1 = r5.additionalInfo
            int r1 = r1.length()
            java.lang.String r4 = r6.additionalInfo
            int r4 = r4.length()
            if (r1 == r4) goto L_0x00da
        L_0x020b:
            r0 = r3
            goto L_0x00db
        L_0x020e:
            r1 = r3
            goto L_0x00e2
        L_0x0211:
            r4 = r3
            goto L_0x00e7
        L_0x0214:
            r0 = r3
            goto L_0x00ea
        L_0x0217:
            java.lang.String r1 = r5.accessibilityInfo
            int r1 = r1.length()
            java.lang.String r4 = r6.accessibilityInfo
            int r4 = r4.length()
            if (r1 == r4) goto L_0x00f0
        L_0x0225:
            r0 = r3
            goto L_0x00f1
        L_0x0228:
            r1 = r3
            goto L_0x00f8
        L_0x022b:
            r4 = r3
            goto L_0x00fd
        L_0x022e:
            r0 = r3
            goto L_0x0100
        L_0x0231:
            java.lang.String r1 = r5.titleDeviceId
            int r1 = r1.length()
            java.lang.String r4 = r6.titleDeviceId
            int r4 = r4.length()
            if (r1 == r4) goto L_0x0106
        L_0x023f:
            r0 = r3
            goto L_0x0107
        L_0x0242:
            r1 = r3
            goto L_0x010e
        L_0x0245:
            r4 = r3
            goto L_0x0113
        L_0x0248:
            r0 = r3
            goto L_0x0116
        L_0x024b:
            java.lang.String r1 = r5.titleSessionId
            int r1 = r1.length()
            java.lang.String r4 = r6.titleSessionId
            int r4 = r4.length()
            if (r1 == r4) goto L_0x011c
        L_0x0259:
            r0 = r3
            goto L_0x011d
        */
        throw new UnsupportedOperationException("Method not decompiled: com.microsoft.xbox.idp.telemetry.utc.CommonData.memberwiseCompareQuick(com.microsoft.xbox.idp.telemetry.utc.CommonData):boolean");
    }

    /* access modifiers changed from: protected */
    public boolean memberwiseCompareDeep(CommonData that) {
        return ((((((((((((1 != 0 && super.memberwiseCompareDeep(that)) && (this.eventVersion == null || this.eventVersion.equals(that.eventVersion))) && (this.deviceModel == null || this.deviceModel.equals(that.deviceModel))) && (this.xsapiVersion == null || this.xsapiVersion.equals(that.xsapiVersion))) && (this.appName == null || this.appName.equals(that.appName))) && (this.clientLanguage == null || this.clientLanguage.equals(that.clientLanguage))) && (this.sandboxId == null || this.sandboxId.equals(that.sandboxId))) && (this.appSessionId == null || this.appSessionId.equals(that.appSessionId))) && (this.userId == null || this.userId.equals(that.userId))) && (this.additionalInfo == null || this.additionalInfo.equals(that.additionalInfo))) && (this.accessibilityInfo == null || this.accessibilityInfo.equals(that.accessibilityInfo))) && (this.titleDeviceId == null || this.titleDeviceId.equals(that.titleDeviceId))) && (this.titleSessionId == null || this.titleSessionId.equals(that.titleSessionId));
    }
}
