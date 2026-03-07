package Microsoft.Telemetry;

import Microsoft.Telemetry.Base;
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
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class Data<TDomain extends BondSerializable> extends Base {
    private TDomain baseData;
    private Class<TDomain> generic_type_TDomain = ((Class) getGenericTypeArguments()[0]);

    public BondSerializable clone() {
        return null;
    }

    public final TDomain getBaseData() {
        return this.baseData;
    }

    public final void setBaseData(TDomain value) {
        this.baseData = value;
    }

    public static class Schema {
        /* access modifiers changed from: private */
        public static final Metadata baseData_metadata = new Metadata();
        public static final Metadata metadata = new Metadata();
        public static final SchemaDef schemaDef = new SchemaDef();

        static {
            metadata.setName("Data");
            metadata.setQualified_name("Microsoft.Telemetry.Data");
            metadata.getAttributes().put("Description", "Data struct to contain both B and C sections.");
            baseData_metadata.setName("baseData");
            baseData_metadata.setModifier(Modifier.Required);
            baseData_metadata.getAttributes().put("Name", "Item");
            baseData_metadata.getAttributes().put("Description", "Container for data item (B section).");
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
                    field.setId(20);
                    field.setMetadata(baseData_metadata);
                    field.getType().setId(BondDataType.BT_STRUCT);
                    structDef.getFields().add(field);
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
            case 20:
                return this.baseData;
            default:
                return null;
        }
    }

    public void setField(FieldDef fieldDef, Object value) {
        switch (fieldDef.getId()) {
            case 20:
                this.baseData = (BondSerializable) value;
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

    public Data() {
        int i = 0 + 1;
    }

    public void reset() {
        reset("Data", "Microsoft.Telemetry.Data");
    }

    /* access modifiers changed from: protected */
    public void reset(String name, String qualifiedName) {
        super.reset(name, qualifiedName);
        this.baseData = null;
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
            readFieldImpl_baseData(reader, BondDataType.BT_STRUCT);
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
                    case 20:
                        readFieldImpl_baseData(reader, fieldTag.type);
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

    private void readFieldImpl_baseData(ProtocolReader reader, BondDataType typeInPayload) throws IOException {
        try {
            this.baseData = (BondSerializable) this.generic_type_TDomain.newInstance();
            this.baseData.readNested(reader);
        } catch (IllegalAccessException | InstantiationException e) {
        }
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
        writer.writeFieldBegin(BondDataType.BT_STRUCT, 20, Schema.baseData_metadata);
        this.baseData.writeNested(writer, false);
        writer.writeFieldEnd();
        writer.writeStructEnd(isBase);
    }

    public boolean memberwiseCompare(Object obj) {
        if (obj == null) {
            return false;
        }
        Data<TDomain> that = (Data) obj;
        if (!memberwiseCompareQuick(that) || !memberwiseCompareDeep(that)) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: protected */
    public boolean memberwiseCompareQuick(Data<TDomain> that) {
        return 1 != 0 && super.memberwiseCompareQuick(that);
    }

    /* access modifiers changed from: protected */
    public boolean memberwiseCompareDeep(Data<TDomain> that) {
        return 1 != 0 && super.memberwiseCompareDeep(that);
    }

    private Type[] getGenericTypeArguments() {
        return ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments();
    }
}
