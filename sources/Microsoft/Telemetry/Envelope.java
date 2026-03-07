package Microsoft.Telemetry;

import Microsoft.Telemetry.Base;
import Microsoft.Telemetry.Extension;
import android.support.v4.media.TransportMediator;
import android.support.v4.view.MotionEventCompat;
import com.facebook.GraphRequest;
import com.facebook.share.internal.ShareConstants;
import com.microsoft.bond.BondDataType;
import com.microsoft.bond.BondMirror;
import com.microsoft.bond.BondSerializable;
import com.microsoft.bond.Bonded;
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
import java.util.HashMap;
import java.util.Map;

public class Envelope implements BondSerializable, BondMirror {
    private String appId;
    private String appVer;
    private String cV;
    private Bonded<Base> data;
    private String epoch;
    private HashMap<String, Bonded<Extension>> ext;
    private long flags;
    private String iKey;
    private String name;
    private String os;
    private String osVer;
    private double popSample;
    private long seqNum;
    private HashMap<String, String> tags;
    private String time;
    private String ver;

    public BondSerializable clone() {
        return null;
    }

    public final String getVer() {
        return this.ver;
    }

    public final void setVer(String value) {
        this.ver = value;
    }

    public final String getName() {
        return this.name;
    }

    public final void setName(String value) {
        this.name = value;
    }

    public final String getTime() {
        return this.time;
    }

    public final void setTime(String value) {
        this.time = value;
    }

    public final double getPopSample() {
        return this.popSample;
    }

    public final void setPopSample(double value) {
        this.popSample = value;
    }

    public final String getEpoch() {
        return this.epoch;
    }

    public final void setEpoch(String value) {
        this.epoch = value;
    }

    public final long getSeqNum() {
        return this.seqNum;
    }

    public final void setSeqNum(long value) {
        this.seqNum = value;
    }

    public final String getIKey() {
        return this.iKey;
    }

    public final void setIKey(String value) {
        this.iKey = value;
    }

    public final long getFlags() {
        return this.flags;
    }

    public final void setFlags(long value) {
        this.flags = value;
    }

    public final String getOs() {
        return this.os;
    }

    public final void setOs(String value) {
        this.os = value;
    }

    public final String getOsVer() {
        return this.osVer;
    }

    public final void setOsVer(String value) {
        this.osVer = value;
    }

    public final String getAppId() {
        return this.appId;
    }

    public final void setAppId(String value) {
        this.appId = value;
    }

    public final String getAppVer() {
        return this.appVer;
    }

    public final void setAppVer(String value) {
        this.appVer = value;
    }

    public final String getCV() {
        return this.cV;
    }

    public final void setCV(String value) {
        this.cV = value;
    }

    public final HashMap<String, String> getTags() {
        return this.tags;
    }

    public final void setTags(HashMap<String, String> value) {
        this.tags = value;
    }

    public final HashMap<String, Bonded<Extension>> getExt() {
        return this.ext;
    }

    public final void setExt(HashMap<String, Bonded<Extension>> value) {
        this.ext = value;
    }

    public final Bonded<Base> getData() {
        return this.data;
    }

    public final void setData(Bonded<Base> value) {
        this.data = value;
    }

    public static class Schema {
        /* access modifiers changed from: private */
        public static final Metadata appId_metadata = new Metadata();
        /* access modifiers changed from: private */
        public static final Metadata appVer_metadata = new Metadata();
        /* access modifiers changed from: private */
        public static final Metadata cV_metadata = new Metadata();
        /* access modifiers changed from: private */
        public static final Metadata data_metadata = new Metadata();
        /* access modifiers changed from: private */
        public static final Metadata epoch_metadata = new Metadata();
        /* access modifiers changed from: private */
        public static final Metadata ext_metadata = new Metadata();
        /* access modifiers changed from: private */
        public static final Metadata flags_metadata = new Metadata();
        /* access modifiers changed from: private */
        public static final Metadata iKey_metadata = new Metadata();
        public static final Metadata metadata = new Metadata();
        /* access modifiers changed from: private */
        public static final Metadata name_metadata = new Metadata();
        /* access modifiers changed from: private */
        public static final Metadata osVer_metadata = new Metadata();
        /* access modifiers changed from: private */
        public static final Metadata os_metadata = new Metadata();
        /* access modifiers changed from: private */
        public static final Metadata popSample_metadata = new Metadata();
        public static final SchemaDef schemaDef = new SchemaDef();
        /* access modifiers changed from: private */
        public static final Metadata seqNum_metadata = new Metadata();
        /* access modifiers changed from: private */
        public static final Metadata tags_metadata = new Metadata();
        /* access modifiers changed from: private */
        public static final Metadata time_metadata = new Metadata();
        /* access modifiers changed from: private */
        public static final Metadata ver_metadata = new Metadata();

        static {
            metadata.setName("Envelope");
            metadata.setQualified_name("Microsoft.Telemetry.Envelope");
            metadata.getAttributes().put("Description", "System variables for a telemetry item (Part A)");
            ver_metadata.setName("ver");
            ver_metadata.setModifier(Modifier.Required);
            ver_metadata.getAttributes().put("Name", "SchemaVersion");
            name_metadata.setName("name");
            name_metadata.setModifier(Modifier.Required);
            name_metadata.getAttributes().put("Name", "DataTypeName");
            time_metadata.setName("time");
            time_metadata.setModifier(Modifier.Required);
            time_metadata.getAttributes().put("Name", "DateTime");
            popSample_metadata.setName("popSample");
            popSample_metadata.getAttributes().put("Name", "SamplingRate");
            popSample_metadata.getDefault_value().setDouble_value(100.0d);
            epoch_metadata.setName("epoch");
            epoch_metadata.getAttributes().put("Name", "Epoch");
            seqNum_metadata.setName("seqNum");
            seqNum_metadata.getAttributes().put("Name", "SequenceNumber");
            seqNum_metadata.getDefault_value().setInt_value(0);
            iKey_metadata.setName("iKey");
            iKey_metadata.getAttributes().put("Name", "InstrumentationKey");
            flags_metadata.setName("flags");
            flags_metadata.getAttributes().put("Name", "TelemetryProperties");
            flags_metadata.getDefault_value().setInt_value(0);
            os_metadata.setName("os");
            os_metadata.getAttributes().put("Name", "OsPlatform");
            osVer_metadata.setName("osVer");
            osVer_metadata.getAttributes().put("Name", "OsVersion");
            appId_metadata.setName("appId");
            appId_metadata.getAttributes().put("Name", "ApplicationId");
            appVer_metadata.setName("appVer");
            appVer_metadata.getAttributes().put("Name", "ApplicationVersion");
            cV_metadata.setName("cV");
            cV_metadata.getAttributes().put("Name", "CorrelationVector");
            tags_metadata.setName("tags");
            tags_metadata.getAttributes().put("Name", "Tags");
            ext_metadata.setName("ext");
            ext_metadata.getAttributes().put("Name", "Extensions");
            data_metadata.setName(ShareConstants.WEB_DIALOG_PARAM_DATA);
            data_metadata.getAttributes().put("Name", "TelemetryData");
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
                    FieldDef field = new FieldDef();
                    field.setId(10);
                    field.setMetadata(ver_metadata);
                    field.getType().setId(BondDataType.BT_STRING);
                    structDef.getFields().add(field);
                    FieldDef field2 = new FieldDef();
                    field2.setId(20);
                    field2.setMetadata(name_metadata);
                    field2.getType().setId(BondDataType.BT_STRING);
                    structDef.getFields().add(field2);
                    FieldDef field3 = new FieldDef();
                    field3.setId(30);
                    field3.setMetadata(time_metadata);
                    field3.getType().setId(BondDataType.BT_STRING);
                    structDef.getFields().add(field3);
                    FieldDef field4 = new FieldDef();
                    field4.setId(40);
                    field4.setMetadata(popSample_metadata);
                    field4.getType().setId(BondDataType.BT_DOUBLE);
                    structDef.getFields().add(field4);
                    FieldDef field5 = new FieldDef();
                    field5.setId(50);
                    field5.setMetadata(epoch_metadata);
                    field5.getType().setId(BondDataType.BT_STRING);
                    structDef.getFields().add(field5);
                    FieldDef field6 = new FieldDef();
                    field6.setId(60);
                    field6.setMetadata(seqNum_metadata);
                    field6.getType().setId(BondDataType.BT_INT64);
                    structDef.getFields().add(field6);
                    FieldDef field7 = new FieldDef();
                    field7.setId(70);
                    field7.setMetadata(iKey_metadata);
                    field7.getType().setId(BondDataType.BT_STRING);
                    structDef.getFields().add(field7);
                    FieldDef field8 = new FieldDef();
                    field8.setId(80);
                    field8.setMetadata(flags_metadata);
                    field8.getType().setId(BondDataType.BT_INT64);
                    structDef.getFields().add(field8);
                    FieldDef field9 = new FieldDef();
                    field9.setId(90);
                    field9.setMetadata(os_metadata);
                    field9.getType().setId(BondDataType.BT_STRING);
                    structDef.getFields().add(field9);
                    FieldDef field10 = new FieldDef();
                    field10.setId(100);
                    field10.setMetadata(osVer_metadata);
                    field10.getType().setId(BondDataType.BT_STRING);
                    structDef.getFields().add(field10);
                    FieldDef field11 = new FieldDef();
                    field11.setId(110);
                    field11.setMetadata(appId_metadata);
                    field11.getType().setId(BondDataType.BT_STRING);
                    structDef.getFields().add(field11);
                    FieldDef field12 = new FieldDef();
                    field12.setId(120);
                    field12.setMetadata(appVer_metadata);
                    field12.getType().setId(BondDataType.BT_STRING);
                    structDef.getFields().add(field12);
                    FieldDef field13 = new FieldDef();
                    field13.setId(130);
                    field13.setMetadata(cV_metadata);
                    field13.getType().setId(BondDataType.BT_STRING);
                    structDef.getFields().add(field13);
                    FieldDef field14 = new FieldDef();
                    field14.setId(500);
                    field14.setMetadata(tags_metadata);
                    field14.getType().setId(BondDataType.BT_MAP);
                    field14.getType().setKey(new TypeDef());
                    field14.getType().setElement(new TypeDef());
                    field14.getType().getKey().setId(BondDataType.BT_STRING);
                    field14.getType().getElement().setId(BondDataType.BT_STRING);
                    structDef.getFields().add(field14);
                    FieldDef field15 = new FieldDef();
                    field15.setId(510);
                    field15.setMetadata(ext_metadata);
                    field15.getType().setId(BondDataType.BT_MAP);
                    field15.getType().setKey(new TypeDef());
                    field15.getType().setElement(new TypeDef());
                    field15.getType().getKey().setId(BondDataType.BT_STRING);
                    field15.getType().setElement(Extension.Schema.getTypeDef(schema));
                    structDef.getFields().add(field15);
                    FieldDef field16 = new FieldDef();
                    field16.setId(999);
                    field16.setMetadata(data_metadata);
                    field16.setType(Base.Schema.getTypeDef(schema));
                    structDef.getFields().add(field16);
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
                return this.ver;
            case 20:
                return this.name;
            case 30:
                return this.time;
            case MotionEventCompat.AXIS_GENERIC_9 /*40*/:
                return Double.valueOf(this.popSample);
            case GraphRequest.MAXIMUM_BATCH_SIZE /*50*/:
                return this.epoch;
            case 60:
                return Long.valueOf(this.seqNum);
            case 70:
                return this.iKey;
            case 80:
                return Long.valueOf(this.flags);
            case 90:
                return this.os;
            case ImageUtil.TINY /*100*/:
                return this.osVer;
            case 110:
                return this.appId;
            case 120:
                return this.appVer;
            case TransportMediator.KEYCODE_MEDIA_RECORD /*130*/:
                return this.cV;
            case 500:
                return this.tags;
            case 510:
                return this.ext;
            case 999:
                return this.data;
            default:
                return null;
        }
    }

    public void setField(FieldDef fieldDef, Object value) {
        switch (fieldDef.getId()) {
            case 10:
                this.ver = (String) value;
                return;
            case 20:
                this.name = (String) value;
                return;
            case 30:
                this.time = (String) value;
                return;
            case MotionEventCompat.AXIS_GENERIC_9 /*40*/:
                this.popSample = ((Double) value).doubleValue();
                return;
            case GraphRequest.MAXIMUM_BATCH_SIZE /*50*/:
                this.epoch = (String) value;
                return;
            case 60:
                this.seqNum = ((Long) value).longValue();
                return;
            case 70:
                this.iKey = (String) value;
                return;
            case 80:
                this.flags = ((Long) value).longValue();
                return;
            case 90:
                this.os = (String) value;
                return;
            case ImageUtil.TINY /*100*/:
                this.osVer = (String) value;
                return;
            case 110:
                this.appId = (String) value;
                return;
            case 120:
                this.appVer = (String) value;
                return;
            case TransportMediator.KEYCODE_MEDIA_RECORD /*130*/:
                this.cV = (String) value;
                return;
            case 500:
                this.tags = (HashMap) value;
                return;
            case 510:
                this.ext = (HashMap) value;
                return;
            case 999:
                this.data = (Bonded) value;
                return;
            default:
                return;
        }
    }

    public BondMirror createInstance(StructDef structDef) {
        if (Extension.Schema.metadata == structDef.getMetadata()) {
            return new Extension();
        }
        if (Base.Schema.metadata == structDef.getMetadata()) {
            return new Base();
        }
        return null;
    }

    public SchemaDef getSchema() {
        return getRuntimeSchema();
    }

    public static SchemaDef getRuntimeSchema() {
        return Schema.schemaDef;
    }

    public Envelope() {
        reset();
    }

    public void reset() {
        reset("Envelope", "Microsoft.Telemetry.Envelope");
    }

    /* access modifiers changed from: protected */
    public void reset(String name2, String qualifiedName) {
        this.ver = "";
        this.name = "";
        this.time = "";
        this.popSample = 100.0d;
        this.epoch = "";
        this.seqNum = 0;
        this.iKey = "";
        this.flags = 0;
        this.os = "";
        this.osVer = "";
        this.appId = "";
        this.appVer = "";
        this.cV = "";
        if (this.tags == null) {
            this.tags = new HashMap<>();
        } else {
            this.tags.clear();
        }
        if (this.ext == null) {
            this.ext = new HashMap<>();
        } else {
            this.ext.clear();
        }
        this.data = new Bonded<>();
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
        if (!canOmitFields || !reader.readFieldOmitted()) {
            this.ver = reader.readString();
        }
        if (!canOmitFields || !reader.readFieldOmitted()) {
            this.name = reader.readString();
        }
        if (!canOmitFields || !reader.readFieldOmitted()) {
            this.time = reader.readString();
        }
        if (!canOmitFields || !reader.readFieldOmitted()) {
            this.popSample = reader.readDouble();
        }
        if (!canOmitFields || !reader.readFieldOmitted()) {
            this.epoch = reader.readString();
        }
        if (!canOmitFields || !reader.readFieldOmitted()) {
            this.seqNum = reader.readInt64();
        }
        if (!canOmitFields || !reader.readFieldOmitted()) {
            this.iKey = reader.readString();
        }
        if (!canOmitFields || !reader.readFieldOmitted()) {
            this.flags = reader.readInt64();
        }
        if (!canOmitFields || !reader.readFieldOmitted()) {
            this.os = reader.readString();
        }
        if (!canOmitFields || !reader.readFieldOmitted()) {
            this.osVer = reader.readString();
        }
        if (!canOmitFields || !reader.readFieldOmitted()) {
            this.appId = reader.readString();
        }
        if (!canOmitFields || !reader.readFieldOmitted()) {
            this.appVer = reader.readString();
        }
        if (!canOmitFields || !reader.readFieldOmitted()) {
            this.cV = reader.readString();
        }
        if (!canOmitFields || !reader.readFieldOmitted()) {
            readFieldImpl_tags(reader, BondDataType.BT_MAP);
        }
        if (!canOmitFields || !reader.readFieldOmitted()) {
            readFieldImpl_ext(reader, BondDataType.BT_MAP);
        }
        if (!canOmitFields || !reader.readFieldOmitted()) {
            this.data.read(reader);
        }
        reader.readStructEnd();
    }

    /* access modifiers changed from: protected */
    public boolean readTagged(ProtocolReader reader, boolean isBase) throws IOException {
        ProtocolReader.FieldTag fieldTag;
        boolean isPartial;
        reader.readStructBegin(isBase);
        while (true) {
            fieldTag = reader.readFieldBegin();
            if (fieldTag.type != BondDataType.BT_STOP && fieldTag.type != BondDataType.BT_STOP_BASE) {
                switch (fieldTag.id) {
                    case 10:
                        this.ver = ReadHelper.readString(reader, fieldTag.type);
                        break;
                    case 20:
                        this.name = ReadHelper.readString(reader, fieldTag.type);
                        break;
                    case 30:
                        this.time = ReadHelper.readString(reader, fieldTag.type);
                        break;
                    case MotionEventCompat.AXIS_GENERIC_9 /*40*/:
                        this.popSample = ReadHelper.readDouble(reader, fieldTag.type);
                        break;
                    case GraphRequest.MAXIMUM_BATCH_SIZE /*50*/:
                        this.epoch = ReadHelper.readString(reader, fieldTag.type);
                        break;
                    case 60:
                        this.seqNum = ReadHelper.readInt64(reader, fieldTag.type);
                        break;
                    case 70:
                        this.iKey = ReadHelper.readString(reader, fieldTag.type);
                        break;
                    case 80:
                        this.flags = ReadHelper.readInt64(reader, fieldTag.type);
                        break;
                    case 90:
                        this.os = ReadHelper.readString(reader, fieldTag.type);
                        break;
                    case ImageUtil.TINY /*100*/:
                        this.osVer = ReadHelper.readString(reader, fieldTag.type);
                        break;
                    case 110:
                        this.appId = ReadHelper.readString(reader, fieldTag.type);
                        break;
                    case 120:
                        this.appVer = ReadHelper.readString(reader, fieldTag.type);
                        break;
                    case TransportMediator.KEYCODE_MEDIA_RECORD /*130*/:
                        this.cV = ReadHelper.readString(reader, fieldTag.type);
                        break;
                    case 500:
                        readFieldImpl_tags(reader, fieldTag.type);
                        break;
                    case 510:
                        readFieldImpl_ext(reader, fieldTag.type);
                        break;
                    case 999:
                        ReadHelper.validateType(fieldTag.type, BondDataType.BT_STRUCT);
                        this.data.readNested(reader);
                        break;
                    default:
                        reader.skip(fieldTag.type);
                        break;
                }
                reader.readFieldEnd();
            }
        }
        if (fieldTag.type == BondDataType.BT_STOP_BASE) {
            isPartial = true;
        } else {
            isPartial = false;
        }
        reader.readStructEnd();
        return isPartial;
    }

    private void readFieldImpl_tags(ProtocolReader reader, BondDataType typeInPayload) throws IOException {
        ReadHelper.validateType(typeInPayload, BondDataType.BT_MAP);
        ProtocolReader.MapTag tag1 = reader.readMapContainerBegin();
        for (int i2 = 0; i2 < tag1.size; i2++) {
            this.tags.put(ReadHelper.readString(reader, tag1.keyType), ReadHelper.readString(reader, tag1.valueType));
        }
        reader.readContainerEnd();
    }

    private void readFieldImpl_ext(ProtocolReader reader, BondDataType typeInPayload) throws IOException {
        ReadHelper.validateType(typeInPayload, BondDataType.BT_MAP);
        ProtocolReader.MapTag tag1 = reader.readMapContainerBegin();
        ReadHelper.validateType(tag1.valueType, BondDataType.BT_STRUCT);
        for (int i2 = 0; i2 < tag1.size; i2++) {
            Bonded<Extension> val4 = new Bonded<>();
            String key3 = ReadHelper.readString(reader, tag1.keyType);
            val4.readNested(reader);
            this.ext.put(key3, val4);
        }
        reader.readContainerEnd();
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
        writer.writeFieldBegin(BondDataType.BT_STRING, 10, Schema.ver_metadata);
        writer.writeString(this.ver);
        writer.writeFieldEnd();
        writer.writeFieldBegin(BondDataType.BT_STRING, 20, Schema.name_metadata);
        writer.writeString(this.name);
        writer.writeFieldEnd();
        writer.writeFieldBegin(BondDataType.BT_STRING, 30, Schema.time_metadata);
        writer.writeString(this.time);
        writer.writeFieldEnd();
        if (!canOmitFields || this.popSample != Schema.popSample_metadata.getDefault_value().getDouble_value()) {
            writer.writeFieldBegin(BondDataType.BT_DOUBLE, 40, Schema.popSample_metadata);
            writer.writeDouble(this.popSample);
            writer.writeFieldEnd();
        } else {
            writer.writeFieldOmitted(BondDataType.BT_DOUBLE, 40, Schema.popSample_metadata);
        }
        if (!canOmitFields || this.epoch != Schema.epoch_metadata.getDefault_value().getString_value()) {
            writer.writeFieldBegin(BondDataType.BT_STRING, 50, Schema.epoch_metadata);
            writer.writeString(this.epoch);
            writer.writeFieldEnd();
        } else {
            writer.writeFieldOmitted(BondDataType.BT_STRING, 50, Schema.epoch_metadata);
        }
        if (!canOmitFields || this.seqNum != Schema.seqNum_metadata.getDefault_value().getInt_value()) {
            writer.writeFieldBegin(BondDataType.BT_INT64, 60, Schema.seqNum_metadata);
            writer.writeInt64(this.seqNum);
            writer.writeFieldEnd();
        } else {
            writer.writeFieldOmitted(BondDataType.BT_INT64, 60, Schema.seqNum_metadata);
        }
        if (!canOmitFields || this.iKey != Schema.iKey_metadata.getDefault_value().getString_value()) {
            writer.writeFieldBegin(BondDataType.BT_STRING, 70, Schema.iKey_metadata);
            writer.writeString(this.iKey);
            writer.writeFieldEnd();
        } else {
            writer.writeFieldOmitted(BondDataType.BT_STRING, 70, Schema.iKey_metadata);
        }
        if (!canOmitFields || this.flags != Schema.flags_metadata.getDefault_value().getInt_value()) {
            writer.writeFieldBegin(BondDataType.BT_INT64, 80, Schema.flags_metadata);
            writer.writeInt64(this.flags);
            writer.writeFieldEnd();
        } else {
            writer.writeFieldOmitted(BondDataType.BT_INT64, 80, Schema.flags_metadata);
        }
        if (!canOmitFields || this.os != Schema.os_metadata.getDefault_value().getString_value()) {
            writer.writeFieldBegin(BondDataType.BT_STRING, 90, Schema.os_metadata);
            writer.writeString(this.os);
            writer.writeFieldEnd();
        } else {
            writer.writeFieldOmitted(BondDataType.BT_STRING, 90, Schema.os_metadata);
        }
        if (!canOmitFields || this.osVer != Schema.osVer_metadata.getDefault_value().getString_value()) {
            writer.writeFieldBegin(BondDataType.BT_STRING, 100, Schema.osVer_metadata);
            writer.writeString(this.osVer);
            writer.writeFieldEnd();
        } else {
            writer.writeFieldOmitted(BondDataType.BT_STRING, 100, Schema.osVer_metadata);
        }
        if (!canOmitFields || this.appId != Schema.appId_metadata.getDefault_value().getString_value()) {
            writer.writeFieldBegin(BondDataType.BT_STRING, 110, Schema.appId_metadata);
            writer.writeString(this.appId);
            writer.writeFieldEnd();
        } else {
            writer.writeFieldOmitted(BondDataType.BT_STRING, 110, Schema.appId_metadata);
        }
        if (!canOmitFields || this.appVer != Schema.appVer_metadata.getDefault_value().getString_value()) {
            writer.writeFieldBegin(BondDataType.BT_STRING, 120, Schema.appVer_metadata);
            writer.writeString(this.appVer);
            writer.writeFieldEnd();
        } else {
            writer.writeFieldOmitted(BondDataType.BT_STRING, 120, Schema.appVer_metadata);
        }
        if (!canOmitFields || this.cV != Schema.cV_metadata.getDefault_value().getString_value()) {
            writer.writeFieldBegin(BondDataType.BT_STRING, TransportMediator.KEYCODE_MEDIA_RECORD, Schema.cV_metadata);
            writer.writeString(this.cV);
            writer.writeFieldEnd();
        } else {
            writer.writeFieldOmitted(BondDataType.BT_STRING, TransportMediator.KEYCODE_MEDIA_RECORD, Schema.cV_metadata);
        }
        int size11 = this.tags.size();
        if (!canOmitFields || size11 != 0) {
            writer.writeFieldBegin(BondDataType.BT_MAP, 500, Schema.tags_metadata);
            writer.writeContainerBegin(this.tags.size(), BondDataType.BT_STRING, BondDataType.BT_STRING);
            for (Map.Entry<String, String> e12 : this.tags.entrySet()) {
                writer.writeString(e12.getKey());
                writer.writeString(e12.getValue());
            }
            writer.writeContainerEnd();
            writer.writeFieldEnd();
        } else {
            writer.writeFieldOmitted(BondDataType.BT_MAP, 500, Schema.tags_metadata);
        }
        int size13 = this.ext.size();
        if (!canOmitFields || size13 != 0) {
            writer.writeFieldBegin(BondDataType.BT_MAP, 510, Schema.ext_metadata);
            writer.writeContainerBegin(this.ext.size(), BondDataType.BT_STRING, BondDataType.BT_STRUCT);
            for (Map.Entry<String, Bonded<Extension>> e14 : this.ext.entrySet()) {
                writer.writeString(e14.getKey());
                e14.getValue().writeNested(writer, false);
            }
            writer.writeContainerEnd();
            writer.writeFieldEnd();
        } else {
            writer.writeFieldOmitted(BondDataType.BT_MAP, 510, Schema.ext_metadata);
        }
        writer.writeFieldBegin(BondDataType.BT_STRUCT, 999, Schema.data_metadata);
        this.data.writeNested(writer, false);
        writer.writeFieldEnd();
        writer.writeStructEnd(isBase);
    }

    public boolean memberwiseCompare(Object obj) {
        if (obj == null) {
            return false;
        }
        Envelope that = (Envelope) obj;
        if (!memberwiseCompareQuick(that) || !memberwiseCompareDeep(that)) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:105:0x00c5  */
    /* JADX WARNING: Removed duplicated region for block: B:107:0x00c8  */
    /* JADX WARNING: Removed duplicated region for block: B:118:0x00db  */
    /* JADX WARNING: Removed duplicated region for block: B:120:0x00de  */
    /* JADX WARNING: Removed duplicated region for block: B:131:0x00f1  */
    /* JADX WARNING: Removed duplicated region for block: B:133:0x00f4  */
    /* JADX WARNING: Removed duplicated region for block: B:13:0x0018  */
    /* JADX WARNING: Removed duplicated region for block: B:144:0x0107  */
    /* JADX WARNING: Removed duplicated region for block: B:146:0x010a  */
    /* JADX WARNING: Removed duplicated region for block: B:157:0x011d  */
    /* JADX WARNING: Removed duplicated region for block: B:159:0x0120  */
    /* JADX WARNING: Removed duplicated region for block: B:15:0x001b  */
    /* JADX WARNING: Removed duplicated region for block: B:170:0x0133  */
    /* JADX WARNING: Removed duplicated region for block: B:176:0x014c  */
    /* JADX WARNING: Removed duplicated region for block: B:182:0x0166  */
    /* JADX WARNING: Removed duplicated region for block: B:188:0x0180  */
    /* JADX WARNING: Removed duplicated region for block: B:191:0x018b  */
    /* JADX WARNING: Removed duplicated region for block: B:197:0x01a5  */
    /* JADX WARNING: Removed duplicated region for block: B:198:0x01a8  */
    /* JADX WARNING: Removed duplicated region for block: B:204:0x01c2  */
    /* JADX WARNING: Removed duplicated region for block: B:205:0x01c5  */
    /* JADX WARNING: Removed duplicated region for block: B:211:0x01df  */
    /* JADX WARNING: Removed duplicated region for block: B:217:0x01f9  */
    /* JADX WARNING: Removed duplicated region for block: B:223:0x0213  */
    /* JADX WARNING: Removed duplicated region for block: B:229:0x022d  */
    /* JADX WARNING: Removed duplicated region for block: B:235:0x0247  */
    /* JADX WARNING: Removed duplicated region for block: B:241:0x0261  */
    /* JADX WARNING: Removed duplicated region for block: B:247:0x027b  */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x002e  */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x0031  */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x0044  */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x0057  */
    /* JADX WARNING: Removed duplicated region for block: B:47:0x005a  */
    /* JADX WARNING: Removed duplicated region for block: B:58:0x006d  */
    /* JADX WARNING: Removed duplicated region for block: B:62:0x0078  */
    /* JADX WARNING: Removed duplicated region for block: B:64:0x007b  */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x008e  */
    /* JADX WARNING: Removed duplicated region for block: B:79:0x0099  */
    /* JADX WARNING: Removed duplicated region for block: B:81:0x009c  */
    /* JADX WARNING: Removed duplicated region for block: B:92:0x00af  */
    /* JADX WARNING: Removed duplicated region for block: B:94:0x00b2  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean memberwiseCompareQuick(Microsoft.Telemetry.Envelope r9) {
        /*
            r8 = this;
            r2 = 1
            r3 = 0
            r0 = 1
            if (r0 == 0) goto L_0x013b
            java.lang.String r1 = r8.ver
            if (r1 != 0) goto L_0x0135
            r1 = r2
        L_0x000a:
            java.lang.String r4 = r9.ver
            if (r4 != 0) goto L_0x0138
            r4 = r2
        L_0x000f:
            if (r1 != r4) goto L_0x013b
            r0 = r2
        L_0x0012:
            if (r0 == 0) goto L_0x014c
            java.lang.String r1 = r8.ver
            if (r1 != 0) goto L_0x013e
        L_0x0018:
            r0 = r2
        L_0x0019:
            if (r0 == 0) goto L_0x0155
            java.lang.String r1 = r8.name
            if (r1 != 0) goto L_0x014f
            r1 = r2
        L_0x0020:
            java.lang.String r4 = r9.name
            if (r4 != 0) goto L_0x0152
            r4 = r2
        L_0x0025:
            if (r1 != r4) goto L_0x0155
            r0 = r2
        L_0x0028:
            if (r0 == 0) goto L_0x0166
            java.lang.String r1 = r8.name
            if (r1 != 0) goto L_0x0158
        L_0x002e:
            r0 = r2
        L_0x002f:
            if (r0 == 0) goto L_0x016f
            java.lang.String r1 = r8.time
            if (r1 != 0) goto L_0x0169
            r1 = r2
        L_0x0036:
            java.lang.String r4 = r9.time
            if (r4 != 0) goto L_0x016c
            r4 = r2
        L_0x003b:
            if (r1 != r4) goto L_0x016f
            r0 = r2
        L_0x003e:
            if (r0 == 0) goto L_0x0180
            java.lang.String r1 = r8.time
            if (r1 != 0) goto L_0x0172
        L_0x0044:
            r0 = r2
        L_0x0045:
            if (r0 == 0) goto L_0x018b
            double r4 = r8.popSample
            boolean r1 = java.lang.Double.isNaN(r4)
            if (r1 == 0) goto L_0x0183
            double r4 = r9.popSample
            boolean r1 = java.lang.Double.isNaN(r4)
            if (r1 == 0) goto L_0x018b
        L_0x0057:
            r0 = r2
        L_0x0058:
            if (r0 == 0) goto L_0x0194
            java.lang.String r1 = r8.epoch
            if (r1 != 0) goto L_0x018e
            r1 = r2
        L_0x005f:
            java.lang.String r4 = r9.epoch
            if (r4 != 0) goto L_0x0191
            r4 = r2
        L_0x0064:
            if (r1 != r4) goto L_0x0194
            r0 = r2
        L_0x0067:
            if (r0 == 0) goto L_0x01a5
            java.lang.String r1 = r8.epoch
            if (r1 != 0) goto L_0x0197
        L_0x006d:
            r0 = r2
        L_0x006e:
            if (r0 == 0) goto L_0x01a8
            long r4 = r8.seqNum
            long r6 = r9.seqNum
            int r1 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r1 != 0) goto L_0x01a8
            r0 = r2
        L_0x0079:
            if (r0 == 0) goto L_0x01b1
            java.lang.String r1 = r8.iKey
            if (r1 != 0) goto L_0x01ab
            r1 = r2
        L_0x0080:
            java.lang.String r4 = r9.iKey
            if (r4 != 0) goto L_0x01ae
            r4 = r2
        L_0x0085:
            if (r1 != r4) goto L_0x01b1
            r0 = r2
        L_0x0088:
            if (r0 == 0) goto L_0x01c2
            java.lang.String r1 = r8.iKey
            if (r1 != 0) goto L_0x01b4
        L_0x008e:
            r0 = r2
        L_0x008f:
            if (r0 == 0) goto L_0x01c5
            long r4 = r8.flags
            long r6 = r9.flags
            int r1 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r1 != 0) goto L_0x01c5
            r0 = r2
        L_0x009a:
            if (r0 == 0) goto L_0x01ce
            java.lang.String r1 = r8.os
            if (r1 != 0) goto L_0x01c8
            r1 = r2
        L_0x00a1:
            java.lang.String r4 = r9.os
            if (r4 != 0) goto L_0x01cb
            r4 = r2
        L_0x00a6:
            if (r1 != r4) goto L_0x01ce
            r0 = r2
        L_0x00a9:
            if (r0 == 0) goto L_0x01df
            java.lang.String r1 = r8.os
            if (r1 != 0) goto L_0x01d1
        L_0x00af:
            r0 = r2
        L_0x00b0:
            if (r0 == 0) goto L_0x01e8
            java.lang.String r1 = r8.osVer
            if (r1 != 0) goto L_0x01e2
            r1 = r2
        L_0x00b7:
            java.lang.String r4 = r9.osVer
            if (r4 != 0) goto L_0x01e5
            r4 = r2
        L_0x00bc:
            if (r1 != r4) goto L_0x01e8
            r0 = r2
        L_0x00bf:
            if (r0 == 0) goto L_0x01f9
            java.lang.String r1 = r8.osVer
            if (r1 != 0) goto L_0x01eb
        L_0x00c5:
            r0 = r2
        L_0x00c6:
            if (r0 == 0) goto L_0x0202
            java.lang.String r1 = r8.appId
            if (r1 != 0) goto L_0x01fc
            r1 = r2
        L_0x00cd:
            java.lang.String r4 = r9.appId
            if (r4 != 0) goto L_0x01ff
            r4 = r2
        L_0x00d2:
            if (r1 != r4) goto L_0x0202
            r0 = r2
        L_0x00d5:
            if (r0 == 0) goto L_0x0213
            java.lang.String r1 = r8.appId
            if (r1 != 0) goto L_0x0205
        L_0x00db:
            r0 = r2
        L_0x00dc:
            if (r0 == 0) goto L_0x021c
            java.lang.String r1 = r8.appVer
            if (r1 != 0) goto L_0x0216
            r1 = r2
        L_0x00e3:
            java.lang.String r4 = r9.appVer
            if (r4 != 0) goto L_0x0219
            r4 = r2
        L_0x00e8:
            if (r1 != r4) goto L_0x021c
            r0 = r2
        L_0x00eb:
            if (r0 == 0) goto L_0x022d
            java.lang.String r1 = r8.appVer
            if (r1 != 0) goto L_0x021f
        L_0x00f1:
            r0 = r2
        L_0x00f2:
            if (r0 == 0) goto L_0x0236
            java.lang.String r1 = r8.cV
            if (r1 != 0) goto L_0x0230
            r1 = r2
        L_0x00f9:
            java.lang.String r4 = r9.cV
            if (r4 != 0) goto L_0x0233
            r4 = r2
        L_0x00fe:
            if (r1 != r4) goto L_0x0236
            r0 = r2
        L_0x0101:
            if (r0 == 0) goto L_0x0247
            java.lang.String r1 = r8.cV
            if (r1 != 0) goto L_0x0239
        L_0x0107:
            r0 = r2
        L_0x0108:
            if (r0 == 0) goto L_0x0250
            java.util.HashMap<java.lang.String, java.lang.String> r1 = r8.tags
            if (r1 != 0) goto L_0x024a
            r1 = r2
        L_0x010f:
            java.util.HashMap<java.lang.String, java.lang.String> r4 = r9.tags
            if (r4 != 0) goto L_0x024d
            r4 = r2
        L_0x0114:
            if (r1 != r4) goto L_0x0250
            r0 = r2
        L_0x0117:
            if (r0 == 0) goto L_0x0261
            java.util.HashMap<java.lang.String, java.lang.String> r1 = r8.tags
            if (r1 != 0) goto L_0x0253
        L_0x011d:
            r0 = r2
        L_0x011e:
            if (r0 == 0) goto L_0x026a
            java.util.HashMap<java.lang.String, com.microsoft.bond.Bonded<Microsoft.Telemetry.Extension>> r1 = r8.ext
            if (r1 != 0) goto L_0x0264
            r1 = r2
        L_0x0125:
            java.util.HashMap<java.lang.String, com.microsoft.bond.Bonded<Microsoft.Telemetry.Extension>> r4 = r9.ext
            if (r4 != 0) goto L_0x0267
            r4 = r2
        L_0x012a:
            if (r1 != r4) goto L_0x026a
            r0 = r2
        L_0x012d:
            if (r0 == 0) goto L_0x027b
            java.util.HashMap<java.lang.String, com.microsoft.bond.Bonded<Microsoft.Telemetry.Extension>> r1 = r8.ext
            if (r1 != 0) goto L_0x026d
        L_0x0133:
            r0 = r2
        L_0x0134:
            return r0
        L_0x0135:
            r1 = r3
            goto L_0x000a
        L_0x0138:
            r4 = r3
            goto L_0x000f
        L_0x013b:
            r0 = r3
            goto L_0x0012
        L_0x013e:
            java.lang.String r1 = r8.ver
            int r1 = r1.length()
            java.lang.String r4 = r9.ver
            int r4 = r4.length()
            if (r1 == r4) goto L_0x0018
        L_0x014c:
            r0 = r3
            goto L_0x0019
        L_0x014f:
            r1 = r3
            goto L_0x0020
        L_0x0152:
            r4 = r3
            goto L_0x0025
        L_0x0155:
            r0 = r3
            goto L_0x0028
        L_0x0158:
            java.lang.String r1 = r8.name
            int r1 = r1.length()
            java.lang.String r4 = r9.name
            int r4 = r4.length()
            if (r1 == r4) goto L_0x002e
        L_0x0166:
            r0 = r3
            goto L_0x002f
        L_0x0169:
            r1 = r3
            goto L_0x0036
        L_0x016c:
            r4 = r3
            goto L_0x003b
        L_0x016f:
            r0 = r3
            goto L_0x003e
        L_0x0172:
            java.lang.String r1 = r8.time
            int r1 = r1.length()
            java.lang.String r4 = r9.time
            int r4 = r4.length()
            if (r1 == r4) goto L_0x0044
        L_0x0180:
            r0 = r3
            goto L_0x0045
        L_0x0183:
            double r4 = r8.popSample
            double r6 = r9.popSample
            int r1 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r1 == 0) goto L_0x0057
        L_0x018b:
            r0 = r3
            goto L_0x0058
        L_0x018e:
            r1 = r3
            goto L_0x005f
        L_0x0191:
            r4 = r3
            goto L_0x0064
        L_0x0194:
            r0 = r3
            goto L_0x0067
        L_0x0197:
            java.lang.String r1 = r8.epoch
            int r1 = r1.length()
            java.lang.String r4 = r9.epoch
            int r4 = r4.length()
            if (r1 == r4) goto L_0x006d
        L_0x01a5:
            r0 = r3
            goto L_0x006e
        L_0x01a8:
            r0 = r3
            goto L_0x0079
        L_0x01ab:
            r1 = r3
            goto L_0x0080
        L_0x01ae:
            r4 = r3
            goto L_0x0085
        L_0x01b1:
            r0 = r3
            goto L_0x0088
        L_0x01b4:
            java.lang.String r1 = r8.iKey
            int r1 = r1.length()
            java.lang.String r4 = r9.iKey
            int r4 = r4.length()
            if (r1 == r4) goto L_0x008e
        L_0x01c2:
            r0 = r3
            goto L_0x008f
        L_0x01c5:
            r0 = r3
            goto L_0x009a
        L_0x01c8:
            r1 = r3
            goto L_0x00a1
        L_0x01cb:
            r4 = r3
            goto L_0x00a6
        L_0x01ce:
            r0 = r3
            goto L_0x00a9
        L_0x01d1:
            java.lang.String r1 = r8.os
            int r1 = r1.length()
            java.lang.String r4 = r9.os
            int r4 = r4.length()
            if (r1 == r4) goto L_0x00af
        L_0x01df:
            r0 = r3
            goto L_0x00b0
        L_0x01e2:
            r1 = r3
            goto L_0x00b7
        L_0x01e5:
            r4 = r3
            goto L_0x00bc
        L_0x01e8:
            r0 = r3
            goto L_0x00bf
        L_0x01eb:
            java.lang.String r1 = r8.osVer
            int r1 = r1.length()
            java.lang.String r4 = r9.osVer
            int r4 = r4.length()
            if (r1 == r4) goto L_0x00c5
        L_0x01f9:
            r0 = r3
            goto L_0x00c6
        L_0x01fc:
            r1 = r3
            goto L_0x00cd
        L_0x01ff:
            r4 = r3
            goto L_0x00d2
        L_0x0202:
            r0 = r3
            goto L_0x00d5
        L_0x0205:
            java.lang.String r1 = r8.appId
            int r1 = r1.length()
            java.lang.String r4 = r9.appId
            int r4 = r4.length()
            if (r1 == r4) goto L_0x00db
        L_0x0213:
            r0 = r3
            goto L_0x00dc
        L_0x0216:
            r1 = r3
            goto L_0x00e3
        L_0x0219:
            r4 = r3
            goto L_0x00e8
        L_0x021c:
            r0 = r3
            goto L_0x00eb
        L_0x021f:
            java.lang.String r1 = r8.appVer
            int r1 = r1.length()
            java.lang.String r4 = r9.appVer
            int r4 = r4.length()
            if (r1 == r4) goto L_0x00f1
        L_0x022d:
            r0 = r3
            goto L_0x00f2
        L_0x0230:
            r1 = r3
            goto L_0x00f9
        L_0x0233:
            r4 = r3
            goto L_0x00fe
        L_0x0236:
            r0 = r3
            goto L_0x0101
        L_0x0239:
            java.lang.String r1 = r8.cV
            int r1 = r1.length()
            java.lang.String r4 = r9.cV
            int r4 = r4.length()
            if (r1 == r4) goto L_0x0107
        L_0x0247:
            r0 = r3
            goto L_0x0108
        L_0x024a:
            r1 = r3
            goto L_0x010f
        L_0x024d:
            r4 = r3
            goto L_0x0114
        L_0x0250:
            r0 = r3
            goto L_0x0117
        L_0x0253:
            java.util.HashMap<java.lang.String, java.lang.String> r1 = r8.tags
            int r1 = r1.size()
            java.util.HashMap<java.lang.String, java.lang.String> r4 = r9.tags
            int r4 = r4.size()
            if (r1 == r4) goto L_0x011d
        L_0x0261:
            r0 = r3
            goto L_0x011e
        L_0x0264:
            r1 = r3
            goto L_0x0125
        L_0x0267:
            r4 = r3
            goto L_0x012a
        L_0x026a:
            r0 = r3
            goto L_0x012d
        L_0x026d:
            java.util.HashMap<java.lang.String, com.microsoft.bond.Bonded<Microsoft.Telemetry.Extension>> r1 = r8.ext
            int r1 = r1.size()
            java.util.HashMap<java.lang.String, com.microsoft.bond.Bonded<Microsoft.Telemetry.Extension>> r4 = r9.ext
            int r4 = r4.size()
            if (r1 == r4) goto L_0x0133
        L_0x027b:
            r0 = r3
            goto L_0x0134
        */
        throw new UnsupportedOperationException("Method not decompiled: Microsoft.Telemetry.Envelope.memberwiseCompareQuick(Microsoft.Telemetry.Envelope):boolean");
    }

    /* access modifiers changed from: protected */
    public boolean memberwiseCompareDeep(Envelope that) {
        boolean equals;
        boolean equals2;
        boolean z;
        boolean equals3;
        boolean z2;
        boolean equals4 = (((((((((1 != 0 && (this.ver == null || this.ver.equals(that.ver))) && (this.name == null || this.name.equals(that.name))) && (this.time == null || this.time.equals(that.time))) && (this.epoch == null || this.epoch.equals(that.epoch))) && (this.iKey == null || this.iKey.equals(that.iKey))) && (this.os == null || this.os.equals(that.os))) && (this.osVer == null || this.osVer.equals(that.osVer))) && (this.appId == null || this.appId.equals(that.appId))) && (this.appVer == null || this.appVer.equals(that.appVer))) && (this.cV == null || this.cV.equals(that.cV));
        if (equals4 && this.tags != null && this.tags.size() != 0) {
            for (Map.Entry<String, String> e3 : this.tags.entrySet()) {
                String val1 = e3.getValue();
                String val2 = that.tags.get(e3.getKey());
                if (!equals4 || !that.tags.containsKey(e3.getKey())) {
                    equals4 = false;
                } else {
                    equals4 = true;
                }
                if (equals4) {
                    if (equals4) {
                        if (val1 == null) {
                            z2 = true;
                        } else {
                            z2 = false;
                        }
                        if (z2 == (val2 == null)) {
                            equals3 = true;
                            if ((!equals3 && (val1 == null || val1.length() == val2.length())) || (val1 != null && !val1.equals(val2))) {
                                equals4 = false;
                                continue;
                            } else {
                                equals4 = true;
                                continue;
                            }
                        }
                    }
                    equals3 = false;
                    if (!equals3 && (val1 == null || val1.length() == val2.length())) {
                    }
                    equals4 = false;
                    continue;
                }
                if (!equals4) {
                    break;
                }
            }
        }
        if (equals4 && this.ext != null && this.ext.size() != 0) {
            for (Map.Entry<String, Bonded<Extension>> e6 : this.ext.entrySet()) {
                Bonded<Extension> val4 = e6.getValue();
                Bonded<Extension> val5 = that.ext.get(e6.getKey());
                if (!equals4 || !that.ext.containsKey(e6.getKey())) {
                    equals = false;
                } else {
                    equals = true;
                }
                if (equals) {
                    if (equals) {
                        if (val4 == null) {
                            z = true;
                        } else {
                            z = false;
                        }
                        if (z == (val5 == null)) {
                            equals2 = true;
                            if (equals2 || (val4 != null && !val4.memberwiseCompare(val5))) {
                                equals = false;
                                continue;
                            } else {
                                equals = true;
                                continue;
                            }
                        }
                    }
                    equals2 = false;
                    if (equals2) {
                    }
                    equals = false;
                    continue;
                }
                if (!equals4) {
                    break;
                }
            }
        }
        return equals4 && (this.data == null || this.data.memberwiseCompare(that.data));
    }
}
