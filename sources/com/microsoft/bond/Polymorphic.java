package com.microsoft.bond;

import com.microsoft.bond.ProtocolReader;
import com.microsoft.bond.internal.Marshaler;
import com.microsoft.bond.internal.ReadHelper;
import java.io.IOException;
import java.io.InputStream;

public class Polymorphic implements BondSerializable, BondMirror {
    private String bond_meta;

    public BondSerializable clone() {
        return null;
    }

    public final String getBond_meta() {
        return this.bond_meta;
    }

    public final void setBond_meta(String value) {
        this.bond_meta = value;
    }

    public static class Schema {
        /* access modifiers changed from: private */
        public static final Metadata bond_meta_metadata = new Metadata();
        public static final Metadata metadata = new Metadata();
        public static final SchemaDef schemaDef = new SchemaDef();

        static {
            metadata.setName("Polymorphic");
            metadata.setQualified_name("com.microsoft.bond.Polymorphic");
            metadata.getAttributes().put("polymorphic", "");
            bond_meta_metadata.setName("bond_meta");
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
                    field.setId(Short.MIN_VALUE);
                    field.setMetadata(bond_meta_metadata);
                    field.getType().setId(BondDataType.BT_STRING);
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
            case Short.MIN_VALUE:
                return this.bond_meta;
            default:
                return null;
        }
    }

    public void setField(FieldDef fieldDef, Object value) {
        switch (fieldDef.getId()) {
            case Short.MIN_VALUE:
                this.bond_meta = (String) value;
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

    public Polymorphic() {
        reset();
    }

    public void reset() {
        reset("Polymorphic", "com.microsoft.bond.Polymorphic");
    }

    /* access modifiers changed from: protected */
    public void reset(String name, String qualifiedName) {
        this.bond_meta = qualifiedName;
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
            this.bond_meta = reader.readString();
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
                    case 32768:
                        this.bond_meta = ReadHelper.readString(reader, fieldTag.type);
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
        if (!canOmitFields || this.bond_meta != Schema.bond_meta_metadata.getDefault_value().getString_value()) {
            writer.writeFieldBegin(BondDataType.BT_STRING, 32768, Schema.bond_meta_metadata);
            writer.writeString(this.bond_meta);
            writer.writeFieldEnd();
        } else {
            writer.writeFieldOmitted(BondDataType.BT_STRING, 32768, Schema.bond_meta_metadata);
        }
        writer.writeStructEnd(isBase);
    }

    public boolean memberwiseCompare(Object obj) {
        if (obj == null) {
            return false;
        }
        Polymorphic that = (Polymorphic) obj;
        if (!memberwiseCompareQuick(that) || !memberwiseCompareDeep(that)) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:13:0x0018  */
    /* JADX WARNING: Removed duplicated region for block: B:19:0x002e  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean memberwiseCompareQuick(com.microsoft.bond.Polymorphic r6) {
        /*
            r5 = this;
            r2 = 1
            r3 = 0
            r0 = 1
            if (r0 == 0) goto L_0x001e
            java.lang.String r1 = r5.bond_meta
            if (r1 != 0) goto L_0x001a
            r1 = r2
        L_0x000a:
            java.lang.String r4 = r6.bond_meta
            if (r4 != 0) goto L_0x001c
            r4 = r2
        L_0x000f:
            if (r1 != r4) goto L_0x001e
            r0 = r2
        L_0x0012:
            if (r0 == 0) goto L_0x002e
            java.lang.String r1 = r5.bond_meta
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
            java.lang.String r1 = r5.bond_meta
            int r1 = r1.length()
            java.lang.String r4 = r6.bond_meta
            int r4 = r4.length()
            if (r1 == r4) goto L_0x0018
        L_0x002e:
            r0 = r3
            goto L_0x0019
        */
        throw new UnsupportedOperationException("Method not decompiled: com.microsoft.bond.Polymorphic.memberwiseCompareQuick(com.microsoft.bond.Polymorphic):boolean");
    }

    /* access modifiers changed from: protected */
    public boolean memberwiseCompareDeep(Polymorphic that) {
        return 1 != 0 && (this.bond_meta == null || this.bond_meta.equals(that.bond_meta));
    }
}
