package com.microsoft.bond;

import com.microsoft.bond.BondSerializable;
import com.microsoft.bond.ProtocolReader;
import com.microsoft.bond.internal.Marshaler;
import com.microsoft.bond.internal.ReadHelper;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;

public class Vector<T extends BondSerializable, U extends BondSerializable> implements BondSerializable, BondMirror {
    private Class<T> generic_type_T;
    private Class<U> generic_type_U;
    private ArrayList<T> value;

    public BondSerializable clone() {
        return null;
    }

    public final ArrayList<T> getValue() {
        return this.value;
    }

    public final void setValue(ArrayList<T> value2) {
        this.value = value2;
    }

    public static class Schema {
        public static final Metadata metadata = new Metadata();
        public static final SchemaDef schemaDef = new SchemaDef();
        /* access modifiers changed from: private */
        public static final Metadata value_metadata = new Metadata();

        static {
            metadata.setName("Vector");
            metadata.setQualified_name("com.microsoft.bond.Vector");
            value_metadata.setName("value");
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
                    field.setMetadata(value_metadata);
                    field.getType().setId(BondDataType.BT_LIST);
                    field.getType().setElement(new TypeDef());
                    field.getType().getElement().setId(BondDataType.BT_STRUCT);
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
            case 0:
                return this.value;
            default:
                return null;
        }
    }

    public void setField(FieldDef fieldDef, Object value2) {
        switch (fieldDef.getId()) {
            case 0:
                this.value = (ArrayList) value2;
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

    public Vector() {
        Type[] genericTypes = getGenericTypeArguments();
        int typeIndex = 0 + 1;
        this.generic_type_T = (Class) genericTypes[0];
        int i = typeIndex + 1;
        this.generic_type_U = (Class) genericTypes[typeIndex];
        reset();
    }

    public void reset() {
        reset("Vector", "com.microsoft.bond.Vector");
    }

    /* access modifiers changed from: protected */
    public void reset(String name, String qualifiedName) {
        if (this.value == null) {
            this.value = new ArrayList<>();
        } else {
            this.value.clear();
        }
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
            readFieldImpl_value(reader, BondDataType.BT_LIST);
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
                        readFieldImpl_value(reader, fieldTag.type);
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

    private void readFieldImpl_value(ProtocolReader reader, BondDataType typeInPayload) throws IOException {
        ReadHelper.validateType(typeInPayload, BondDataType.BT_LIST);
        ProtocolReader.ListTag tag1 = reader.readContainerBegin();
        this.value.ensureCapacity(tag1.size);
        for (int i3 = 0; i3 < tag1.size; i3++) {
            T element2 = null;
            try {
                element2 = (BondSerializable) this.generic_type_T.newInstance();
                element2.readNested(reader);
            } catch (IllegalAccessException | InstantiationException e) {
            }
            this.value.add(element2);
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
        int size1 = this.value.size();
        if (!canOmitFields || size1 != 0) {
            writer.writeFieldBegin(BondDataType.BT_LIST, 0, Schema.value_metadata);
            writer.writeContainerBegin(size1, BondDataType.BT_STRUCT);
            Iterator i$ = this.value.iterator();
            while (i$.hasNext()) {
                ((BondSerializable) i$.next()).writeNested(writer, false);
            }
            writer.writeContainerEnd();
            writer.writeFieldEnd();
        } else {
            writer.writeFieldOmitted(BondDataType.BT_LIST, 0, Schema.value_metadata);
        }
        writer.writeStructEnd(isBase);
    }

    public boolean memberwiseCompare(Object obj) {
        if (obj == null) {
            return false;
        }
        Vector<T, U> that = (Vector) obj;
        if (!memberwiseCompareQuick(that) || !memberwiseCompareDeep(that)) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:13:0x0018  */
    /* JADX WARNING: Removed duplicated region for block: B:19:0x002e  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean memberwiseCompareQuick(com.microsoft.bond.Vector<T, U> r6) {
        /*
            r5 = this;
            r2 = 1
            r3 = 0
            r0 = 1
            if (r0 == 0) goto L_0x001e
            java.util.ArrayList<T> r1 = r5.value
            if (r1 != 0) goto L_0x001a
            r1 = r2
        L_0x000a:
            java.util.ArrayList<T> r4 = r6.value
            if (r4 != 0) goto L_0x001c
            r4 = r2
        L_0x000f:
            if (r1 != r4) goto L_0x001e
            r0 = r2
        L_0x0012:
            if (r0 == 0) goto L_0x002e
            java.util.ArrayList<T> r1 = r5.value
            if (r1 != 0) goto L_0x0020
        L_0x0018:
            r0 = r2
        L_0x0019:
            return r0
        L_0x001a:
            r1 = r3
            goto L_0x000a
        L_0x001c:
            r4 = r3
            goto L_0x000f
        L_0x001e:
            r0 = r3
            goto L_0x0012
        L_0x0020:
            java.util.ArrayList<T> r1 = r5.value
            int r1 = r1.size()
            java.util.ArrayList<T> r4 = r6.value
            int r4 = r4.size()
            if (r1 == r4) goto L_0x0018
        L_0x002e:
            r0 = r3
            goto L_0x0019
        */
        throw new UnsupportedOperationException("Method not decompiled: com.microsoft.bond.Vector.memberwiseCompareQuick(com.microsoft.bond.Vector):boolean");
    }

    /* access modifiers changed from: protected */
    public boolean memberwiseCompareDeep(Vector<T, U> that) {
        if (1 != 0 && this.value != null && this.value.size() != 0) {
            for (int i1 = 0; i1 < this.value.size(); i1++) {
                BondSerializable bondSerializable = (BondSerializable) this.value.get(i1);
                BondSerializable bondSerializable2 = (BondSerializable) that.value.get(i1);
                if (1 == 0) {
                    break;
                }
            }
        }
        return true;
    }

    private Type[] getGenericTypeArguments() {
        return ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments();
    }
}
