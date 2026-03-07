package com.microsoft.bond;

import com.microsoft.bond.ProtocolReader;
import com.microsoft.bond.StructDef;
import com.microsoft.bond.TypeDef;
import com.microsoft.bond.internal.Marshaler;
import com.microsoft.bond.internal.ReadHelper;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

public class SchemaDef implements BondSerializable, BondMirror {
    private TypeDef root;
    private ArrayList<StructDef> structs;

    public BondSerializable clone() {
        return null;
    }

    public final ArrayList<StructDef> getStructs() {
        return this.structs;
    }

    public final void setStructs(ArrayList<StructDef> value) {
        this.structs = value;
    }

    public final TypeDef getRoot() {
        return this.root;
    }

    public final void setRoot(TypeDef value) {
        this.root = value;
    }

    public static class Schema {
        public static final Metadata metadata = new Metadata();
        /* access modifiers changed from: private */
        public static final Metadata root_metadata = new Metadata();
        public static final SchemaDef schemaDef = new SchemaDef();
        /* access modifiers changed from: private */
        public static final Metadata structs_metadata = new Metadata();

        static {
            metadata.setName("SchemaDef");
            metadata.setQualified_name("com.microsoft.bond.SchemaDef");
            structs_metadata.setName("structs");
            root_metadata.setName("root");
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
                    field.setMetadata(structs_metadata);
                    field.getType().setId(BondDataType.BT_LIST);
                    field.getType().setElement(new TypeDef());
                    field.getType().setElement(StructDef.Schema.getTypeDef(schema));
                    structDef.getFields().add(field);
                    FieldDef field2 = new FieldDef();
                    field2.setId(1);
                    field2.setMetadata(root_metadata);
                    field2.setType(TypeDef.Schema.getTypeDef(schema));
                    structDef.getFields().add(field2);
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
                return this.structs;
            case 1:
                return this.root;
            default:
                return null;
        }
    }

    public void setField(FieldDef fieldDef, Object value) {
        switch (fieldDef.getId()) {
            case 0:
                this.structs = (ArrayList) value;
                return;
            case 1:
                this.root = (TypeDef) value;
                return;
            default:
                return;
        }
    }

    public BondMirror createInstance(StructDef structDef) {
        if (StructDef.Schema.metadata == structDef.getMetadata()) {
            return new StructDef();
        }
        if (TypeDef.Schema.metadata == structDef.getMetadata()) {
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

    public SchemaDef() {
        reset();
    }

    public void reset() {
        reset("SchemaDef", "com.microsoft.bond.SchemaDef");
    }

    /* access modifiers changed from: protected */
    public void reset(String name, String qualifiedName) {
        if (this.structs == null) {
            this.structs = new ArrayList<>();
        } else {
            this.structs.clear();
        }
        this.root = new TypeDef();
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
            readFieldImpl_structs(reader, BondDataType.BT_LIST);
        }
        if (!canOmitFields || !reader.readFieldOmitted()) {
            this.root.read(reader);
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
                        readFieldImpl_structs(reader, fieldTag.type);
                        break;
                    case 1:
                        ReadHelper.validateType(fieldTag.type, BondDataType.BT_STRUCT);
                        this.root.readNested(reader);
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

    private void readFieldImpl_structs(ProtocolReader reader, BondDataType typeInPayload) throws IOException {
        ReadHelper.validateType(typeInPayload, BondDataType.BT_LIST);
        ProtocolReader.ListTag tag1 = reader.readContainerBegin();
        ReadHelper.validateType(tag1.type, BondDataType.BT_STRUCT);
        this.structs.ensureCapacity(tag1.size);
        for (int i3 = 0; i3 < tag1.size; i3++) {
            StructDef element2 = new StructDef();
            element2.readNested(reader);
            this.structs.add(element2);
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
        int size1 = this.structs.size();
        if (!canOmitFields || size1 != 0) {
            writer.writeFieldBegin(BondDataType.BT_LIST, 0, Schema.structs_metadata);
            writer.writeContainerBegin(size1, BondDataType.BT_STRUCT);
            Iterator i$ = this.structs.iterator();
            while (i$.hasNext()) {
                i$.next().writeNested(writer, false);
            }
            writer.writeContainerEnd();
            writer.writeFieldEnd();
        } else {
            writer.writeFieldOmitted(BondDataType.BT_LIST, 0, Schema.structs_metadata);
        }
        writer.writeFieldBegin(BondDataType.BT_STRUCT, 1, Schema.root_metadata);
        this.root.writeNested(writer, false);
        writer.writeFieldEnd();
        writer.writeStructEnd(isBase);
    }

    public boolean memberwiseCompare(Object obj) {
        if (obj == null) {
            return false;
        }
        SchemaDef that = (SchemaDef) obj;
        if (!memberwiseCompareQuick(that) || !memberwiseCompareDeep(that)) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:13:0x0018  */
    /* JADX WARNING: Removed duplicated region for block: B:19:0x002e  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean memberwiseCompareQuick(com.microsoft.bond.SchemaDef r6) {
        /*
            r5 = this;
            r2 = 1
            r3 = 0
            r0 = 1
            if (r0 == 0) goto L_0x001e
            java.util.ArrayList<com.microsoft.bond.StructDef> r1 = r5.structs
            if (r1 != 0) goto L_0x001a
            r1 = r2
        L_0x000a:
            java.util.ArrayList<com.microsoft.bond.StructDef> r4 = r6.structs
            if (r4 != 0) goto L_0x001c
            r4 = r2
        L_0x000f:
            if (r1 != r4) goto L_0x001e
            r0 = r2
        L_0x0012:
            if (r0 == 0) goto L_0x002e
            java.util.ArrayList<com.microsoft.bond.StructDef> r1 = r5.structs
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
            java.util.ArrayList<com.microsoft.bond.StructDef> r1 = r5.structs
            int r1 = r1.size()
            java.util.ArrayList<com.microsoft.bond.StructDef> r4 = r6.structs
            int r4 = r4.size()
            if (r1 == r4) goto L_0x0018
        L_0x002e:
            r0 = r3
            goto L_0x0019
        */
        throw new UnsupportedOperationException("Method not decompiled: com.microsoft.bond.SchemaDef.memberwiseCompareQuick(com.microsoft.bond.SchemaDef):boolean");
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x0052 A[LOOP:0: B:7:0x0012->B:31:0x0052, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x003c A[EDGE_INSN: B:36:0x003c->B:21:0x003c ?: BREAK  , SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean memberwiseCompareDeep(com.microsoft.bond.SchemaDef r9) {
        /*
            r8 = this;
            r4 = 1
            r5 = 0
            r0 = 1
            if (r0 == 0) goto L_0x003c
            java.util.ArrayList<com.microsoft.bond.StructDef> r6 = r8.structs
            if (r6 == 0) goto L_0x003c
            java.util.ArrayList<com.microsoft.bond.StructDef> r6 = r8.structs
            int r6 = r6.size()
            if (r6 == 0) goto L_0x003c
            r1 = 0
        L_0x0012:
            java.util.ArrayList<com.microsoft.bond.StructDef> r6 = r8.structs
            int r6 = r6.size()
            if (r1 >= r6) goto L_0x003c
            java.util.ArrayList<com.microsoft.bond.StructDef> r6 = r8.structs
            java.lang.Object r2 = r6.get(r1)
            com.microsoft.bond.StructDef r2 = (com.microsoft.bond.StructDef) r2
            java.util.ArrayList<com.microsoft.bond.StructDef> r6 = r9.structs
            java.lang.Object r3 = r6.get(r1)
            com.microsoft.bond.StructDef r3 = (com.microsoft.bond.StructDef) r3
            if (r0 == 0) goto L_0x0048
            if (r2 != 0) goto L_0x0044
            r7 = r4
        L_0x002f:
            if (r3 != 0) goto L_0x0046
            r6 = r4
        L_0x0032:
            if (r7 != r6) goto L_0x0048
            r0 = r4
        L_0x0035:
            if (r0 == 0) goto L_0x0050
            if (r2 != 0) goto L_0x004a
        L_0x0039:
            r0 = r4
        L_0x003a:
            if (r0 != 0) goto L_0x0052
        L_0x003c:
            if (r0 == 0) goto L_0x005f
            com.microsoft.bond.TypeDef r6 = r8.root
            if (r6 != 0) goto L_0x0055
        L_0x0042:
            r0 = r4
        L_0x0043:
            return r0
        L_0x0044:
            r7 = r5
            goto L_0x002f
        L_0x0046:
            r6 = r5
            goto L_0x0032
        L_0x0048:
            r0 = r5
            goto L_0x0035
        L_0x004a:
            boolean r6 = r2.memberwiseCompare(r3)
            if (r6 != 0) goto L_0x0039
        L_0x0050:
            r0 = r5
            goto L_0x003a
        L_0x0052:
            int r1 = r1 + 1
            goto L_0x0012
        L_0x0055:
            com.microsoft.bond.TypeDef r6 = r8.root
            com.microsoft.bond.TypeDef r7 = r9.root
            boolean r6 = r6.memberwiseCompare(r7)
            if (r6 != 0) goto L_0x0042
        L_0x005f:
            r0 = r5
            goto L_0x0043
        */
        throw new UnsupportedOperationException("Method not decompiled: com.microsoft.bond.SchemaDef.memberwiseCompareDeep(com.microsoft.bond.SchemaDef):boolean");
    }
}
