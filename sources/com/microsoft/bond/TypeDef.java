package com.microsoft.bond;

import com.microsoft.bond.ProtocolReader;
import com.microsoft.bond.internal.Marshaler;
import com.microsoft.bond.internal.ReadHelper;
import java.io.IOException;
import java.io.InputStream;

public class TypeDef implements BondSerializable, BondMirror {
    private boolean bonded_type;
    private TypeDef element;
    private BondDataType id;
    private TypeDef key;
    private short struct_def;

    public BondSerializable clone() {
        return null;
    }

    public final BondDataType getId() {
        return this.id;
    }

    public final void setId(BondDataType value) {
        this.id = value;
    }

    public final short getStruct_def() {
        return this.struct_def;
    }

    public final void setStruct_def(short value) {
        this.struct_def = value;
    }

    public final TypeDef getElement() {
        return this.element;
    }

    public final void setElement(TypeDef value) {
        this.element = value;
    }

    public final TypeDef getKey() {
        return this.key;
    }

    public final void setKey(TypeDef value) {
        this.key = value;
    }

    public final boolean getBonded_type() {
        return this.bonded_type;
    }

    public final void setBonded_type(boolean value) {
        this.bonded_type = value;
    }

    public static class Schema {
        /* access modifiers changed from: private */
        public static final Metadata bonded_type_metadata = new Metadata();
        /* access modifiers changed from: private */
        public static final Metadata element_metadata = new Metadata();
        /* access modifiers changed from: private */
        public static final Metadata id_metadata = new Metadata();
        /* access modifiers changed from: private */
        public static final Metadata key_metadata = new Metadata();
        public static final Metadata metadata = new Metadata();
        public static final SchemaDef schemaDef = new SchemaDef();
        /* access modifiers changed from: private */
        public static final Metadata struct_def_metadata = new Metadata();

        static {
            metadata.setName("TypeDef");
            metadata.setQualified_name("com.microsoft.bond.TypeDef");
            id_metadata.setName("id");
            id_metadata.getDefault_value().setInt_value((long) BondDataType.BT_STRUCT.getValue());
            struct_def_metadata.setName("struct_def");
            struct_def_metadata.getDefault_value().setUint_value(0);
            element_metadata.setName("element");
            key_metadata.setName("key");
            bonded_type_metadata.setName("bonded_type");
            bonded_type_metadata.getDefault_value().setUint_value(0);
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
                    field.setMetadata(id_metadata);
                    field.getType().setId(BondDataType.BT_INT32);
                    structDef.getFields().add(field);
                    FieldDef field2 = new FieldDef();
                    field2.setId(1);
                    field2.setMetadata(struct_def_metadata);
                    field2.getType().setId(BondDataType.BT_UINT16);
                    structDef.getFields().add(field2);
                    FieldDef field3 = new FieldDef();
                    field3.setId(2);
                    field3.setMetadata(element_metadata);
                    field3.getType().setId(BondDataType.BT_LIST);
                    field3.getType().setElement(new TypeDef());
                    field3.getType().setElement(getTypeDef(schema));
                    structDef.getFields().add(field3);
                    FieldDef field4 = new FieldDef();
                    field4.setId(3);
                    field4.setMetadata(key_metadata);
                    field4.getType().setId(BondDataType.BT_LIST);
                    field4.getType().setElement(new TypeDef());
                    field4.getType().setElement(getTypeDef(schema));
                    structDef.getFields().add(field4);
                    FieldDef field5 = new FieldDef();
                    field5.setId(4);
                    field5.setMetadata(bonded_type_metadata);
                    field5.getType().setId(BondDataType.BT_BOOL);
                    structDef.getFields().add(field5);
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
                return this.id;
            case 1:
                return Short.valueOf(this.struct_def);
            case 2:
                return this.element;
            case 3:
                return this.key;
            case 4:
                return Boolean.valueOf(this.bonded_type);
            default:
                return null;
        }
    }

    public void setField(FieldDef fieldDef, Object value) {
        switch (fieldDef.getId()) {
            case 0:
                this.id = (BondDataType) value;
                return;
            case 1:
                this.struct_def = ((Short) value).shortValue();
                return;
            case 2:
                this.element = (TypeDef) value;
                return;
            case 3:
                this.key = (TypeDef) value;
                return;
            case 4:
                this.bonded_type = ((Boolean) value).booleanValue();
                return;
            default:
                return;
        }
    }

    public BondMirror createInstance(StructDef structDef) {
        if (Schema.metadata == structDef.getMetadata()) {
            return new TypeDef();
        }
        return null;
    }

    public SchemaDef getSchema() {
        return getRuntimeSchema();
    }

    public static SchemaDef getRuntimeSchema() {
        return Schema.schemaDef;
    }

    public TypeDef() {
        reset();
    }

    public void reset() {
        reset("TypeDef", "com.microsoft.bond.TypeDef");
    }

    /* access modifiers changed from: protected */
    public void reset(String name, String qualifiedName) {
        this.id = BondDataType.BT_STRUCT;
        this.struct_def = 0;
        this.element = null;
        this.key = null;
        this.bonded_type = false;
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
            this.id = BondDataType.fromValue(reader.readInt32());
        }
        if (!canOmitFields || !reader.readFieldOmitted()) {
            this.struct_def = reader.readUInt16();
        }
        if (!canOmitFields || !reader.readFieldOmitted()) {
            readFieldImpl_element(reader, BondDataType.BT_LIST);
        }
        if (!canOmitFields || !reader.readFieldOmitted()) {
            readFieldImpl_key(reader, BondDataType.BT_LIST);
        }
        if (!canOmitFields || !reader.readFieldOmitted()) {
            this.bonded_type = reader.readBool();
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
                        this.id = BondDataType.fromValue(ReadHelper.readInt32(reader, fieldTag.type));
                        break;
                    case 1:
                        this.struct_def = ReadHelper.readUInt16(reader, fieldTag.type);
                        break;
                    case 2:
                        readFieldImpl_element(reader, fieldTag.type);
                        break;
                    case 3:
                        readFieldImpl_key(reader, fieldTag.type);
                        break;
                    case 4:
                        this.bonded_type = ReadHelper.readBool(reader, fieldTag.type);
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

    private void readFieldImpl_element(ProtocolReader reader, BondDataType typeInPayload) throws IOException {
        ReadHelper.validateType(typeInPayload, BondDataType.BT_LIST);
        ProtocolReader.ListTag tag1 = reader.readContainerBegin();
        ReadHelper.validateType(tag1.type, BondDataType.BT_STRUCT);
        if (tag1.size == 1) {
            if (this.element == null) {
                this.element = new TypeDef();
            }
            this.element.readNested(reader);
        } else if (tag1.size != 0) {
        }
        reader.readContainerEnd();
    }

    private void readFieldImpl_key(ProtocolReader reader, BondDataType typeInPayload) throws IOException {
        ReadHelper.validateType(typeInPayload, BondDataType.BT_LIST);
        ProtocolReader.ListTag tag1 = reader.readContainerBegin();
        ReadHelper.validateType(tag1.type, BondDataType.BT_STRUCT);
        if (tag1.size == 1) {
            if (this.key == null) {
                this.key = new TypeDef();
            }
            this.key.readNested(reader);
        } else if (tag1.size != 0) {
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
        int size3;
        int size4;
        boolean z = true;
        boolean canOmitFields = writer.hasCapability(ProtocolCapability.CAN_OMIT_FIELDS);
        writer.writeStructBegin(Schema.metadata, isBase);
        if (!canOmitFields || ((long) this.id.getValue()) != Schema.id_metadata.getDefault_value().getInt_value()) {
            writer.writeFieldBegin(BondDataType.BT_INT32, 0, Schema.id_metadata);
            writer.writeInt32(this.id.getValue());
            writer.writeFieldEnd();
        } else {
            writer.writeFieldOmitted(BondDataType.BT_INT32, 0, Schema.id_metadata);
        }
        if (!canOmitFields || ((long) this.struct_def) != Schema.struct_def_metadata.getDefault_value().getUint_value()) {
            writer.writeFieldBegin(BondDataType.BT_UINT16, 1, Schema.struct_def_metadata);
            writer.writeUInt16(this.struct_def);
            writer.writeFieldEnd();
        } else {
            writer.writeFieldOmitted(BondDataType.BT_UINT16, 1, Schema.struct_def_metadata);
        }
        if (this.element != null) {
            size3 = 1;
        } else {
            size3 = 0;
        }
        if (!canOmitFields || size3 != 0) {
            writer.writeFieldBegin(BondDataType.BT_LIST, 2, Schema.element_metadata);
            writer.writeContainerBegin(size3, BondDataType.BT_STRUCT);
            if (size3 != 0) {
                this.element.writeNested(writer, false);
            }
            writer.writeContainerEnd();
            writer.writeFieldEnd();
        } else {
            writer.writeFieldOmitted(BondDataType.BT_LIST, 2, Schema.element_metadata);
        }
        if (this.key != null) {
            size4 = 1;
        } else {
            size4 = 0;
        }
        if (!canOmitFields || size4 != 0) {
            writer.writeFieldBegin(BondDataType.BT_LIST, 3, Schema.key_metadata);
            writer.writeContainerBegin(size4, BondDataType.BT_STRUCT);
            if (size4 != 0) {
                this.key.writeNested(writer, false);
            }
            writer.writeContainerEnd();
            writer.writeFieldEnd();
        } else {
            writer.writeFieldOmitted(BondDataType.BT_LIST, 3, Schema.key_metadata);
        }
        if (canOmitFields) {
            boolean z2 = this.bonded_type;
            if (Schema.bonded_type_metadata.getDefault_value().getUint_value() == 0) {
                z = false;
            }
            if (z2 == z) {
                writer.writeFieldOmitted(BondDataType.BT_BOOL, 4, Schema.bonded_type_metadata);
                writer.writeStructEnd(isBase);
            }
        }
        writer.writeFieldBegin(BondDataType.BT_BOOL, 4, Schema.bonded_type_metadata);
        writer.writeBool(this.bonded_type);
        writer.writeFieldEnd();
        writer.writeStructEnd(isBase);
    }

    public boolean memberwiseCompare(Object obj) {
        if (obj == null) {
            return false;
        }
        TypeDef that = (TypeDef) obj;
        if (!memberwiseCompareQuick(that) || !memberwiseCompareDeep(that)) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:19:0x0026  */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x003b  */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x004d  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean memberwiseCompareQuick(com.microsoft.bond.TypeDef r6) {
        /*
            r5 = this;
            r2 = 1
            r3 = 0
            r0 = 1
            if (r0 == 0) goto L_0x003d
            com.microsoft.bond.BondDataType r1 = r5.id
            com.microsoft.bond.BondDataType r4 = r6.id
            if (r1 != r4) goto L_0x003d
            r0 = r2
        L_0x000c:
            if (r0 == 0) goto L_0x003f
            short r1 = r5.struct_def
            short r4 = r6.struct_def
            if (r1 != r4) goto L_0x003f
            r0 = r2
        L_0x0015:
            if (r0 == 0) goto L_0x0045
            com.microsoft.bond.TypeDef r1 = r5.element
            if (r1 != 0) goto L_0x0041
            r1 = r2
        L_0x001c:
            com.microsoft.bond.TypeDef r4 = r6.element
            if (r4 != 0) goto L_0x0043
            r4 = r2
        L_0x0021:
            if (r1 != r4) goto L_0x0045
            r0 = r2
        L_0x0024:
            if (r0 == 0) goto L_0x004b
            com.microsoft.bond.TypeDef r1 = r5.key
            if (r1 != 0) goto L_0x0047
            r1 = r2
        L_0x002b:
            com.microsoft.bond.TypeDef r4 = r6.key
            if (r4 != 0) goto L_0x0049
            r4 = r2
        L_0x0030:
            if (r1 != r4) goto L_0x004b
            r0 = r2
        L_0x0033:
            if (r0 == 0) goto L_0x004d
            boolean r1 = r5.bonded_type
            boolean r4 = r6.bonded_type
            if (r1 != r4) goto L_0x004d
            r0 = r2
        L_0x003c:
            return r0
        L_0x003d:
            r0 = r3
            goto L_0x000c
        L_0x003f:
            r0 = r3
            goto L_0x0015
        L_0x0041:
            r1 = r3
            goto L_0x001c
        L_0x0043:
            r4 = r3
            goto L_0x0021
        L_0x0045:
            r0 = r3
            goto L_0x0024
        L_0x0047:
            r1 = r3
            goto L_0x002b
        L_0x0049:
            r4 = r3
            goto L_0x0030
        L_0x004b:
            r0 = r3
            goto L_0x0033
        L_0x004d:
            r0 = r3
            goto L_0x003c
        */
        throw new UnsupportedOperationException("Method not decompiled: com.microsoft.bond.TypeDef.memberwiseCompareQuick(com.microsoft.bond.TypeDef):boolean");
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:16:0x001e  */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x003a  */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x004c  */
    /* JADX WARNING: Removed duplicated region for block: B:44:0x005e  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean memberwiseCompareDeep(com.microsoft.bond.TypeDef r6) {
        /*
            r5 = this;
            r2 = 1
            r3 = 0
            r0 = 1
            if (r0 == 0) goto L_0x001f
            com.microsoft.bond.TypeDef r1 = r5.element
            if (r1 == 0) goto L_0x001f
            if (r0 == 0) goto L_0x0040
            com.microsoft.bond.TypeDef r1 = r5.element
            if (r1 != 0) goto L_0x003c
            r1 = r2
        L_0x0010:
            com.microsoft.bond.TypeDef r4 = r6.element
            if (r4 != 0) goto L_0x003e
            r4 = r2
        L_0x0015:
            if (r1 != r4) goto L_0x0040
            r0 = r2
        L_0x0018:
            if (r0 == 0) goto L_0x004c
            com.microsoft.bond.TypeDef r1 = r5.element
            if (r1 != 0) goto L_0x0042
        L_0x001e:
            r0 = r2
        L_0x001f:
            if (r0 == 0) goto L_0x003b
            com.microsoft.bond.TypeDef r1 = r5.key
            if (r1 == 0) goto L_0x003b
            if (r0 == 0) goto L_0x0052
            com.microsoft.bond.TypeDef r1 = r5.key
            if (r1 != 0) goto L_0x004e
            r1 = r2
        L_0x002c:
            com.microsoft.bond.TypeDef r4 = r6.key
            if (r4 != 0) goto L_0x0050
            r4 = r2
        L_0x0031:
            if (r1 != r4) goto L_0x0052
            r0 = r2
        L_0x0034:
            if (r0 == 0) goto L_0x005e
            com.microsoft.bond.TypeDef r1 = r5.key
            if (r1 != 0) goto L_0x0054
        L_0x003a:
            r0 = r2
        L_0x003b:
            return r0
        L_0x003c:
            r1 = r3
            goto L_0x0010
        L_0x003e:
            r4 = r3
            goto L_0x0015
        L_0x0040:
            r0 = r3
            goto L_0x0018
        L_0x0042:
            com.microsoft.bond.TypeDef r1 = r5.element
            com.microsoft.bond.TypeDef r4 = r6.element
            boolean r1 = r1.memberwiseCompare(r4)
            if (r1 != 0) goto L_0x001e
        L_0x004c:
            r0 = r3
            goto L_0x001f
        L_0x004e:
            r1 = r3
            goto L_0x002c
        L_0x0050:
            r4 = r3
            goto L_0x0031
        L_0x0052:
            r0 = r3
            goto L_0x0034
        L_0x0054:
            com.microsoft.bond.TypeDef r1 = r5.key
            com.microsoft.bond.TypeDef r4 = r6.key
            boolean r1 = r1.memberwiseCompare(r4)
            if (r1 != 0) goto L_0x003a
        L_0x005e:
            r0 = r3
            goto L_0x003b
        */
        throw new UnsupportedOperationException("Method not decompiled: com.microsoft.bond.TypeDef.memberwiseCompareDeep(com.microsoft.bond.TypeDef):boolean");
    }
}
