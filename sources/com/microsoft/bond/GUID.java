package com.microsoft.bond;

import com.microsoft.bond.ProtocolReader;
import com.microsoft.bond.internal.Marshaler;
import com.microsoft.bond.internal.ReadHelper;
import java.io.IOException;
import java.io.InputStream;

public class GUID implements BondSerializable, BondMirror {
    private int Data1;
    private short Data2;
    private short Data3;
    private long Data4;

    public BondSerializable clone() {
        return null;
    }

    public final int getData1() {
        return this.Data1;
    }

    public final void setData1(int value) {
        this.Data1 = value;
    }

    public final short getData2() {
        return this.Data2;
    }

    public final void setData2(short value) {
        this.Data2 = value;
    }

    public final short getData3() {
        return this.Data3;
    }

    public final void setData3(short value) {
        this.Data3 = value;
    }

    public final long getData4() {
        return this.Data4;
    }

    public final void setData4(long value) {
        this.Data4 = value;
    }

    public static class Schema {
        /* access modifiers changed from: private */
        public static final Metadata Data1_metadata = new Metadata();
        /* access modifiers changed from: private */
        public static final Metadata Data2_metadata = new Metadata();
        /* access modifiers changed from: private */
        public static final Metadata Data3_metadata = new Metadata();
        /* access modifiers changed from: private */
        public static final Metadata Data4_metadata = new Metadata();
        public static final Metadata metadata = new Metadata();
        public static final SchemaDef schemaDef = new SchemaDef();

        static {
            metadata.setName("GUID");
            metadata.setQualified_name("com.microsoft.bond.GUID");
            Data1_metadata.setName("Data1");
            Data1_metadata.getDefault_value().setUint_value(0);
            Data2_metadata.setName("Data2");
            Data2_metadata.getDefault_value().setUint_value(0);
            Data3_metadata.setName("Data3");
            Data3_metadata.getDefault_value().setUint_value(0);
            Data4_metadata.setName("Data4");
            Data4_metadata.getDefault_value().setUint_value(0);
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
                    field.setId(0);
                    field.setMetadata(Data1_metadata);
                    field.getType().setId(BondDataType.BT_UINT32);
                    structDef.getFields().add(field);
                    FieldDef field2 = new FieldDef();
                    field2.setId(1);
                    field2.setMetadata(Data2_metadata);
                    field2.getType().setId(BondDataType.BT_UINT16);
                    structDef.getFields().add(field2);
                    FieldDef field3 = new FieldDef();
                    field3.setId(2);
                    field3.setMetadata(Data3_metadata);
                    field3.getType().setId(BondDataType.BT_UINT16);
                    structDef.getFields().add(field3);
                    FieldDef field4 = new FieldDef();
                    field4.setId(3);
                    field4.setMetadata(Data4_metadata);
                    field4.getType().setId(BondDataType.BT_UINT64);
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
            case 0:
                return Integer.valueOf(this.Data1);
            case 1:
                return Short.valueOf(this.Data2);
            case 2:
                return Short.valueOf(this.Data3);
            case 3:
                return Long.valueOf(this.Data4);
            default:
                return null;
        }
    }

    public void setField(FieldDef fieldDef, Object value) {
        switch (fieldDef.getId()) {
            case 0:
                this.Data1 = ((Integer) value).intValue();
                return;
            case 1:
                this.Data2 = ((Short) value).shortValue();
                return;
            case 2:
                this.Data3 = ((Short) value).shortValue();
                return;
            case 3:
                this.Data4 = ((Long) value).longValue();
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

    public GUID() {
        reset();
    }

    public void reset() {
        reset("GUID", "com.microsoft.bond.GUID");
    }

    /* access modifiers changed from: protected */
    public void reset(String name, String qualifiedName) {
        this.Data1 = 0;
        this.Data2 = 0;
        this.Data3 = 0;
        this.Data4 = 0;
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
            this.Data1 = reader.readUInt32();
        }
        if (!canOmitFields || !reader.readFieldOmitted()) {
            this.Data2 = reader.readUInt16();
        }
        if (!canOmitFields || !reader.readFieldOmitted()) {
            this.Data3 = reader.readUInt16();
        }
        if (!canOmitFields || !reader.readFieldOmitted()) {
            this.Data4 = reader.readUInt64();
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
                    case 0:
                        this.Data1 = ReadHelper.readUInt32(reader, fieldTag.type);
                        break;
                    case 1:
                        this.Data2 = ReadHelper.readUInt16(reader, fieldTag.type);
                        break;
                    case 2:
                        this.Data3 = ReadHelper.readUInt16(reader, fieldTag.type);
                        break;
                    case 3:
                        this.Data4 = ReadHelper.readUInt64(reader, fieldTag.type);
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
        if (!canOmitFields || ((long) this.Data1) != Schema.Data1_metadata.getDefault_value().getUint_value()) {
            writer.writeFieldBegin(BondDataType.BT_UINT32, 0, Schema.Data1_metadata);
            writer.writeUInt32(this.Data1);
            writer.writeFieldEnd();
        } else {
            writer.writeFieldOmitted(BondDataType.BT_UINT32, 0, Schema.Data1_metadata);
        }
        if (!canOmitFields || ((long) this.Data2) != Schema.Data2_metadata.getDefault_value().getUint_value()) {
            writer.writeFieldBegin(BondDataType.BT_UINT16, 1, Schema.Data2_metadata);
            writer.writeUInt16(this.Data2);
            writer.writeFieldEnd();
        } else {
            writer.writeFieldOmitted(BondDataType.BT_UINT16, 1, Schema.Data2_metadata);
        }
        if (!canOmitFields || ((long) this.Data3) != Schema.Data3_metadata.getDefault_value().getUint_value()) {
            writer.writeFieldBegin(BondDataType.BT_UINT16, 2, Schema.Data3_metadata);
            writer.writeUInt16(this.Data3);
            writer.writeFieldEnd();
        } else {
            writer.writeFieldOmitted(BondDataType.BT_UINT16, 2, Schema.Data3_metadata);
        }
        if (!canOmitFields || this.Data4 != Schema.Data4_metadata.getDefault_value().getUint_value()) {
            writer.writeFieldBegin(BondDataType.BT_UINT64, 3, Schema.Data4_metadata);
            writer.writeUInt64(this.Data4);
            writer.writeFieldEnd();
        } else {
            writer.writeFieldOmitted(BondDataType.BT_UINT64, 3, Schema.Data4_metadata);
        }
        writer.writeStructEnd(isBase);
    }

    public boolean memberwiseCompare(Object obj) {
        if (obj == null) {
            return false;
        }
        GUID that = (GUID) obj;
        if (!memberwiseCompareQuick(that) || !memberwiseCompareDeep(that)) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: protected */
    public boolean memberwiseCompareQuick(GUID that) {
        boolean equals;
        boolean equals2;
        boolean equals3;
        if (1 == 0 || this.Data1 != that.Data1) {
            equals = false;
        } else {
            equals = true;
        }
        if (!equals || this.Data2 != that.Data2) {
            equals2 = false;
        } else {
            equals2 = true;
        }
        if (!equals2 || this.Data3 != that.Data3) {
            equals3 = false;
        } else {
            equals3 = true;
        }
        if (!equals3 || this.Data4 != that.Data4) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: protected */
    public boolean memberwiseCompareDeep(GUID that) {
        return true;
    }
}
