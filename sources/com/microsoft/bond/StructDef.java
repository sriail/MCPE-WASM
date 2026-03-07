package com.microsoft.bond;

import com.facebook.GraphRequest;
import com.microsoft.bond.FieldDef;
import com.microsoft.bond.Metadata;
import com.microsoft.bond.ProtocolReader;
import com.microsoft.bond.TypeDef;
import com.microsoft.bond.internal.Marshaler;
import com.microsoft.bond.internal.ReadHelper;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

public class StructDef implements BondSerializable, BondMirror {
    private TypeDef base_def;
    private ArrayList<FieldDef> fields;
    private Metadata metadata;

    public BondSerializable clone() {
        return null;
    }

    public final Metadata getMetadata() {
        return this.metadata;
    }

    public final void setMetadata(Metadata value) {
        this.metadata = value;
    }

    public final TypeDef getBase_def() {
        return this.base_def;
    }

    public final void setBase_def(TypeDef value) {
        this.base_def = value;
    }

    public final ArrayList<FieldDef> getFields() {
        return this.fields;
    }

    public final void setFields(ArrayList<FieldDef> value) {
        this.fields = value;
    }

    public static class Schema {
        /* access modifiers changed from: private */
        public static final Metadata base_def_metadata = new Metadata();
        /* access modifiers changed from: private */
        public static final Metadata fields_metadata = new Metadata();
        public static final Metadata metadata = new Metadata();
        /* access modifiers changed from: private */
        public static final Metadata metadata_metadata = new Metadata();
        public static final SchemaDef schemaDef = new SchemaDef();

        static {
            metadata.setName("StructDef");
            metadata.setQualified_name("com.microsoft.bond.StructDef");
            metadata_metadata.setName("metadata");
            base_def_metadata.setName("base_def");
            fields_metadata.setName(GraphRequest.FIELDS_PARAM);
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
                    field.setMetadata(metadata_metadata);
                    field.setType(Metadata.Schema.getTypeDef(schema));
                    structDef.getFields().add(field);
                    FieldDef field2 = new FieldDef();
                    field2.setId(1);
                    field2.setMetadata(base_def_metadata);
                    field2.getType().setId(BondDataType.BT_LIST);
                    field2.getType().setElement(new TypeDef());
                    field2.getType().setElement(TypeDef.Schema.getTypeDef(schema));
                    structDef.getFields().add(field2);
                    FieldDef field3 = new FieldDef();
                    field3.setId(2);
                    field3.setMetadata(fields_metadata);
                    field3.getType().setId(BondDataType.BT_LIST);
                    field3.getType().setElement(new TypeDef());
                    field3.getType().setElement(FieldDef.Schema.getTypeDef(schema));
                    structDef.getFields().add(field3);
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
                return this.metadata;
            case 1:
                return this.base_def;
            case 2:
                return this.fields;
            default:
                return null;
        }
    }

    public void setField(FieldDef fieldDef, Object value) {
        switch (fieldDef.getId()) {
            case 0:
                this.metadata = (Metadata) value;
                return;
            case 1:
                this.base_def = (TypeDef) value;
                return;
            case 2:
                this.fields = (ArrayList) value;
                return;
            default:
                return;
        }
    }

    public BondMirror createInstance(StructDef structDef) {
        if (Metadata.Schema.metadata == structDef.getMetadata()) {
            return new Metadata();
        }
        if (TypeDef.Schema.metadata == structDef.getMetadata()) {
            return new TypeDef();
        }
        if (FieldDef.Schema.metadata == structDef.getMetadata()) {
            return new FieldDef();
        }
        return null;
    }

    public SchemaDef getSchema() {
        return getRuntimeSchema();
    }

    public static SchemaDef getRuntimeSchema() {
        return Schema.schemaDef;
    }

    public StructDef() {
        reset();
    }

    public void reset() {
        reset("StructDef", "com.microsoft.bond.StructDef");
    }

    /* access modifiers changed from: protected */
    public void reset(String name, String qualifiedName) {
        this.metadata = new Metadata();
        this.base_def = null;
        if (this.fields == null) {
            this.fields = new ArrayList<>();
        } else {
            this.fields.clear();
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
            this.metadata.read(reader);
        }
        if (!canOmitFields || !reader.readFieldOmitted()) {
            readFieldImpl_base_def(reader, BondDataType.BT_LIST);
        }
        if (!canOmitFields || !reader.readFieldOmitted()) {
            readFieldImpl_fields(reader, BondDataType.BT_LIST);
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
                        ReadHelper.validateType(fieldTag.type, BondDataType.BT_STRUCT);
                        this.metadata.readNested(reader);
                        break;
                    case 1:
                        readFieldImpl_base_def(reader, fieldTag.type);
                        break;
                    case 2:
                        readFieldImpl_fields(reader, fieldTag.type);
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

    private void readFieldImpl_base_def(ProtocolReader reader, BondDataType typeInPayload) throws IOException {
        ReadHelper.validateType(typeInPayload, BondDataType.BT_LIST);
        ProtocolReader.ListTag tag1 = reader.readContainerBegin();
        ReadHelper.validateType(tag1.type, BondDataType.BT_STRUCT);
        if (tag1.size == 1) {
            if (this.base_def == null) {
                this.base_def = new TypeDef();
            }
            this.base_def.readNested(reader);
        } else if (tag1.size != 0) {
        }
        reader.readContainerEnd();
    }

    private void readFieldImpl_fields(ProtocolReader reader, BondDataType typeInPayload) throws IOException {
        ReadHelper.validateType(typeInPayload, BondDataType.BT_LIST);
        ProtocolReader.ListTag tag1 = reader.readContainerBegin();
        ReadHelper.validateType(tag1.type, BondDataType.BT_STRUCT);
        this.fields.ensureCapacity(tag1.size);
        for (int i3 = 0; i3 < tag1.size; i3++) {
            FieldDef element2 = new FieldDef();
            element2.readNested(reader);
            this.fields.add(element2);
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
        int size1;
        boolean canOmitFields = writer.hasCapability(ProtocolCapability.CAN_OMIT_FIELDS);
        writer.writeStructBegin(Schema.metadata, isBase);
        writer.writeFieldBegin(BondDataType.BT_STRUCT, 0, Schema.metadata_metadata);
        this.metadata.writeNested(writer, false);
        writer.writeFieldEnd();
        if (this.base_def != null) {
            size1 = 1;
        } else {
            size1 = 0;
        }
        if (!canOmitFields || size1 != 0) {
            writer.writeFieldBegin(BondDataType.BT_LIST, 1, Schema.base_def_metadata);
            writer.writeContainerBegin(size1, BondDataType.BT_STRUCT);
            if (size1 != 0) {
                this.base_def.writeNested(writer, false);
            }
            writer.writeContainerEnd();
            writer.writeFieldEnd();
        } else {
            writer.writeFieldOmitted(BondDataType.BT_LIST, 1, Schema.base_def_metadata);
        }
        int size2 = this.fields.size();
        if (!canOmitFields || size2 != 0) {
            writer.writeFieldBegin(BondDataType.BT_LIST, 2, Schema.fields_metadata);
            writer.writeContainerBegin(size2, BondDataType.BT_STRUCT);
            Iterator i$ = this.fields.iterator();
            while (i$.hasNext()) {
                i$.next().writeNested(writer, false);
            }
            writer.writeContainerEnd();
            writer.writeFieldEnd();
        } else {
            writer.writeFieldOmitted(BondDataType.BT_LIST, 2, Schema.fields_metadata);
        }
        writer.writeStructEnd(isBase);
    }

    public boolean memberwiseCompare(Object obj) {
        if (obj == null) {
            return false;
        }
        StructDef that = (StructDef) obj;
        if (!memberwiseCompareQuick(that) || !memberwiseCompareDeep(that)) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:11:0x0014  */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x0027  */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x0043  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean memberwiseCompareQuick(com.microsoft.bond.StructDef r6) {
        /*
            r5 = this;
            r2 = 1
            r3 = 0
            r0 = 1
            if (r0 == 0) goto L_0x002d
            com.microsoft.bond.TypeDef r1 = r5.base_def
            if (r1 != 0) goto L_0x0029
            r1 = r2
        L_0x000a:
            com.microsoft.bond.TypeDef r4 = r6.base_def
            if (r4 != 0) goto L_0x002b
            r4 = r2
        L_0x000f:
            if (r1 != r4) goto L_0x002d
            r0 = r2
        L_0x0012:
            if (r0 == 0) goto L_0x0033
            java.util.ArrayList<com.microsoft.bond.FieldDef> r1 = r5.fields
            if (r1 != 0) goto L_0x002f
            r1 = r2
        L_0x0019:
            java.util.ArrayList<com.microsoft.bond.FieldDef> r4 = r6.fields
            if (r4 != 0) goto L_0x0031
            r4 = r2
        L_0x001e:
            if (r1 != r4) goto L_0x0033
            r0 = r2
        L_0x0021:
            if (r0 == 0) goto L_0x0043
            java.util.ArrayList<com.microsoft.bond.FieldDef> r1 = r5.fields
            if (r1 != 0) goto L_0x0035
        L_0x0027:
            r0 = r2
        L_0x0028:
            return r0
        L_0x0029:
            r1 = r3
            goto L_0x000a
        L_0x002b:
            r4 = r3
            goto L_0x000f
        L_0x002d:
            r0 = r3
            goto L_0x0012
        L_0x002f:
            r1 = r3
            goto L_0x0019
        L_0x0031:
            r4 = r3
            goto L_0x001e
        L_0x0033:
            r0 = r3
            goto L_0x0021
        L_0x0035:
            java.util.ArrayList<com.microsoft.bond.FieldDef> r1 = r5.fields
            int r1 = r1.size()
            java.util.ArrayList<com.microsoft.bond.FieldDef> r4 = r6.fields
            int r4 = r4.size()
            if (r1 == r4) goto L_0x0027
        L_0x0043:
            r0 = r3
            goto L_0x0028
        */
        throw new UnsupportedOperationException("Method not decompiled: com.microsoft.bond.StructDef.memberwiseCompareQuick(com.microsoft.bond.StructDef):boolean");
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x0025  */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x007c  */
    /* JADX WARNING: Removed duplicated region for block: B:57:0x008c A[LOOP:0: B:27:0x0035->B:57:0x008c, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:59:0x005f A[EDGE_INSN: B:59:0x005f->B:41:0x005f ?: BREAK  , SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean memberwiseCompareDeep(com.microsoft.bond.StructDef r9) {
        /*
            r8 = this;
            r5 = 1
            r6 = 0
            r0 = 1
            if (r0 == 0) goto L_0x006a
            com.microsoft.bond.Metadata r4 = r8.metadata
            if (r4 != 0) goto L_0x0060
        L_0x0009:
            r0 = r5
        L_0x000a:
            if (r0 == 0) goto L_0x0026
            com.microsoft.bond.TypeDef r4 = r8.base_def
            if (r4 == 0) goto L_0x0026
            if (r0 == 0) goto L_0x0070
            com.microsoft.bond.TypeDef r4 = r8.base_def
            if (r4 != 0) goto L_0x006c
            r4 = r5
        L_0x0017:
            com.microsoft.bond.TypeDef r7 = r9.base_def
            if (r7 != 0) goto L_0x006e
            r7 = r5
        L_0x001c:
            if (r4 != r7) goto L_0x0070
            r0 = r5
        L_0x001f:
            if (r0 == 0) goto L_0x007c
            com.microsoft.bond.TypeDef r4 = r8.base_def
            if (r4 != 0) goto L_0x0072
        L_0x0025:
            r0 = r5
        L_0x0026:
            if (r0 == 0) goto L_0x005f
            java.util.ArrayList<com.microsoft.bond.FieldDef> r4 = r8.fields
            if (r4 == 0) goto L_0x005f
            java.util.ArrayList<com.microsoft.bond.FieldDef> r4 = r8.fields
            int r4 = r4.size()
            if (r4 == 0) goto L_0x005f
            r1 = 0
        L_0x0035:
            java.util.ArrayList<com.microsoft.bond.FieldDef> r4 = r8.fields
            int r4 = r4.size()
            if (r1 >= r4) goto L_0x005f
            java.util.ArrayList<com.microsoft.bond.FieldDef> r4 = r8.fields
            java.lang.Object r2 = r4.get(r1)
            com.microsoft.bond.FieldDef r2 = (com.microsoft.bond.FieldDef) r2
            java.util.ArrayList<com.microsoft.bond.FieldDef> r4 = r9.fields
            java.lang.Object r3 = r4.get(r1)
            com.microsoft.bond.FieldDef r3 = (com.microsoft.bond.FieldDef) r3
            if (r0 == 0) goto L_0x0082
            if (r2 != 0) goto L_0x007e
            r7 = r5
        L_0x0052:
            if (r3 != 0) goto L_0x0080
            r4 = r5
        L_0x0055:
            if (r7 != r4) goto L_0x0082
            r0 = r5
        L_0x0058:
            if (r0 == 0) goto L_0x008a
            if (r2 != 0) goto L_0x0084
        L_0x005c:
            r0 = r5
        L_0x005d:
            if (r0 != 0) goto L_0x008c
        L_0x005f:
            return r0
        L_0x0060:
            com.microsoft.bond.Metadata r4 = r8.metadata
            com.microsoft.bond.Metadata r7 = r9.metadata
            boolean r4 = r4.memberwiseCompare(r7)
            if (r4 != 0) goto L_0x0009
        L_0x006a:
            r0 = r6
            goto L_0x000a
        L_0x006c:
            r4 = r6
            goto L_0x0017
        L_0x006e:
            r7 = r6
            goto L_0x001c
        L_0x0070:
            r0 = r6
            goto L_0x001f
        L_0x0072:
            com.microsoft.bond.TypeDef r4 = r8.base_def
            com.microsoft.bond.TypeDef r7 = r9.base_def
            boolean r4 = r4.memberwiseCompare(r7)
            if (r4 != 0) goto L_0x0025
        L_0x007c:
            r0 = r6
            goto L_0x0026
        L_0x007e:
            r7 = r6
            goto L_0x0052
        L_0x0080:
            r4 = r6
            goto L_0x0055
        L_0x0082:
            r0 = r6
            goto L_0x0058
        L_0x0084:
            boolean r4 = r2.memberwiseCompare(r3)
            if (r4 != 0) goto L_0x005c
        L_0x008a:
            r0 = r6
            goto L_0x005d
        L_0x008c:
            int r1 = r1 + 1
            goto L_0x0035
        */
        throw new UnsupportedOperationException("Method not decompiled: com.microsoft.bond.StructDef.memberwiseCompareDeep(com.microsoft.bond.StructDef):boolean");
    }
}
