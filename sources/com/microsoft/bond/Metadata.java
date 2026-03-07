package com.microsoft.bond;

import com.microsoft.bond.ProtocolReader;
import com.microsoft.bond.Variant;
import com.microsoft.bond.internal.Marshaler;
import com.microsoft.bond.internal.ReadHelper;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class Metadata implements BondSerializable, BondMirror {
    private HashMap<String, String> attributes;
    private Variant default_value;
    private Modifier modifier;
    private String name;
    private String qualified_name;

    public BondSerializable clone() {
        return null;
    }

    public final String getName() {
        return this.name;
    }

    public final void setName(String value) {
        this.name = value;
    }

    public final String getQualified_name() {
        return this.qualified_name;
    }

    public final void setQualified_name(String value) {
        this.qualified_name = value;
    }

    public final HashMap<String, String> getAttributes() {
        return this.attributes;
    }

    public final void setAttributes(HashMap<String, String> value) {
        this.attributes = value;
    }

    public final Modifier getModifier() {
        return this.modifier;
    }

    public final void setModifier(Modifier value) {
        this.modifier = value;
    }

    public final Variant getDefault_value() {
        return this.default_value;
    }

    public final void setDefault_value(Variant value) {
        this.default_value = value;
    }

    public static class Schema {
        /* access modifiers changed from: private */
        public static final Metadata attributes_metadata = new Metadata();
        /* access modifiers changed from: private */
        public static final Metadata default_value_metadata = new Metadata();
        public static final Metadata metadata = new Metadata();
        /* access modifiers changed from: private */
        public static final Metadata modifier_metadata = new Metadata();
        /* access modifiers changed from: private */
        public static final Metadata name_metadata = new Metadata();
        /* access modifiers changed from: private */
        public static final Metadata qualified_name_metadata = new Metadata();
        public static final SchemaDef schemaDef = new SchemaDef();

        static {
            metadata.setName("Metadata");
            metadata.setQualified_name("com.microsoft.bond.Metadata");
            name_metadata.setName("name");
            qualified_name_metadata.setName("qualified_name");
            attributes_metadata.setName("attributes");
            modifier_metadata.setName("modifier");
            modifier_metadata.getDefault_value().setInt_value((long) Modifier.Optional.getValue());
            default_value_metadata.setName("default_value");
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
                    field.setMetadata(name_metadata);
                    field.getType().setId(BondDataType.BT_STRING);
                    structDef.getFields().add(field);
                    FieldDef field2 = new FieldDef();
                    field2.setId(1);
                    field2.setMetadata(qualified_name_metadata);
                    field2.getType().setId(BondDataType.BT_STRING);
                    structDef.getFields().add(field2);
                    FieldDef field3 = new FieldDef();
                    field3.setId(2);
                    field3.setMetadata(attributes_metadata);
                    field3.getType().setId(BondDataType.BT_MAP);
                    field3.getType().setKey(new TypeDef());
                    field3.getType().setElement(new TypeDef());
                    field3.getType().getKey().setId(BondDataType.BT_STRING);
                    field3.getType().getElement().setId(BondDataType.BT_STRING);
                    structDef.getFields().add(field3);
                    FieldDef field4 = new FieldDef();
                    field4.setId(3);
                    field4.setMetadata(modifier_metadata);
                    field4.getType().setId(BondDataType.BT_INT32);
                    structDef.getFields().add(field4);
                    FieldDef field5 = new FieldDef();
                    field5.setId(4);
                    field5.setMetadata(default_value_metadata);
                    field5.setType(Variant.Schema.getTypeDef(schema));
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
                return this.name;
            case 1:
                return this.qualified_name;
            case 2:
                return this.attributes;
            case 3:
                return this.modifier;
            case 4:
                return this.default_value;
            default:
                return null;
        }
    }

    public void setField(FieldDef fieldDef, Object value) {
        switch (fieldDef.getId()) {
            case 0:
                this.name = (String) value;
                return;
            case 1:
                this.qualified_name = (String) value;
                return;
            case 2:
                this.attributes = (HashMap) value;
                return;
            case 3:
                this.modifier = (Modifier) value;
                return;
            case 4:
                this.default_value = (Variant) value;
                return;
            default:
                return;
        }
    }

    public BondMirror createInstance(StructDef structDef) {
        if (Variant.Schema.metadata == structDef.getMetadata()) {
            return new Variant();
        }
        return null;
    }

    public SchemaDef getSchema() {
        return getRuntimeSchema();
    }

    public static SchemaDef getRuntimeSchema() {
        return Schema.schemaDef;
    }

    public Metadata() {
        reset();
    }

    public void reset() {
        reset("Metadata", "com.microsoft.bond.Metadata");
    }

    /* access modifiers changed from: protected */
    public void reset(String name2, String qualifiedName) {
        this.name = "";
        this.qualified_name = "";
        if (this.attributes == null) {
            this.attributes = new HashMap<>();
        } else {
            this.attributes.clear();
        }
        this.modifier = Modifier.Optional;
        this.default_value = new Variant();
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
            this.name = reader.readString();
        }
        if (!canOmitFields || !reader.readFieldOmitted()) {
            this.qualified_name = reader.readString();
        }
        if (!canOmitFields || !reader.readFieldOmitted()) {
            readFieldImpl_attributes(reader, BondDataType.BT_MAP);
        }
        if (!canOmitFields || !reader.readFieldOmitted()) {
            this.modifier = Modifier.fromValue(reader.readInt32());
        }
        if (!canOmitFields || !reader.readFieldOmitted()) {
            this.default_value.read(reader);
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
                        this.name = ReadHelper.readString(reader, fieldTag.type);
                        break;
                    case 1:
                        this.qualified_name = ReadHelper.readString(reader, fieldTag.type);
                        break;
                    case 2:
                        readFieldImpl_attributes(reader, fieldTag.type);
                        break;
                    case 3:
                        this.modifier = Modifier.fromValue(ReadHelper.readInt32(reader, fieldTag.type));
                        break;
                    case 4:
                        ReadHelper.validateType(fieldTag.type, BondDataType.BT_STRUCT);
                        this.default_value.readNested(reader);
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

    private void readFieldImpl_attributes(ProtocolReader reader, BondDataType typeInPayload) throws IOException {
        ReadHelper.validateType(typeInPayload, BondDataType.BT_MAP);
        ProtocolReader.MapTag tag1 = reader.readMapContainerBegin();
        for (int i2 = 0; i2 < tag1.size; i2++) {
            this.attributes.put(ReadHelper.readString(reader, tag1.keyType), ReadHelper.readString(reader, tag1.valueType));
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
        if (!canOmitFields || this.name != Schema.name_metadata.getDefault_value().getString_value()) {
            writer.writeFieldBegin(BondDataType.BT_STRING, 0, Schema.name_metadata);
            writer.writeString(this.name);
            writer.writeFieldEnd();
        } else {
            writer.writeFieldOmitted(BondDataType.BT_STRING, 0, Schema.name_metadata);
        }
        if (!canOmitFields || this.qualified_name != Schema.qualified_name_metadata.getDefault_value().getString_value()) {
            writer.writeFieldBegin(BondDataType.BT_STRING, 1, Schema.qualified_name_metadata);
            writer.writeString(this.qualified_name);
            writer.writeFieldEnd();
        } else {
            writer.writeFieldOmitted(BondDataType.BT_STRING, 1, Schema.qualified_name_metadata);
        }
        int size3 = this.attributes.size();
        if (!canOmitFields || size3 != 0) {
            writer.writeFieldBegin(BondDataType.BT_MAP, 2, Schema.attributes_metadata);
            writer.writeContainerBegin(this.attributes.size(), BondDataType.BT_STRING, BondDataType.BT_STRING);
            for (Map.Entry<String, String> e4 : this.attributes.entrySet()) {
                writer.writeString(e4.getKey());
                writer.writeString(e4.getValue());
            }
            writer.writeContainerEnd();
            writer.writeFieldEnd();
        } else {
            writer.writeFieldOmitted(BondDataType.BT_MAP, 2, Schema.attributes_metadata);
        }
        if (!canOmitFields || ((long) this.modifier.getValue()) != Schema.modifier_metadata.getDefault_value().getInt_value()) {
            writer.writeFieldBegin(BondDataType.BT_INT32, 3, Schema.modifier_metadata);
            writer.writeInt32(this.modifier.getValue());
            writer.writeFieldEnd();
        } else {
            writer.writeFieldOmitted(BondDataType.BT_INT32, 3, Schema.modifier_metadata);
        }
        writer.writeFieldBegin(BondDataType.BT_STRUCT, 4, Schema.default_value_metadata);
        this.default_value.writeNested(writer, false);
        writer.writeFieldEnd();
        writer.writeStructEnd(isBase);
    }

    public boolean memberwiseCompare(Object obj) {
        if (obj == null) {
            return false;
        }
        Metadata that = (Metadata) obj;
        if (!memberwiseCompareQuick(that) || !memberwiseCompareDeep(that)) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:13:0x0018  */
    /* JADX WARNING: Removed duplicated region for block: B:15:0x001b  */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x002e  */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x0031  */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x0044  */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x004d  */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x0063  */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x0079  */
    /* JADX WARNING: Removed duplicated region for block: B:61:0x008f  */
    /* JADX WARNING: Removed duplicated region for block: B:62:0x0091  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean memberwiseCompareQuick(com.microsoft.bond.Metadata r6) {
        /*
            r5 = this;
            r2 = 1
            r3 = 0
            r0 = 1
            if (r0 == 0) goto L_0x0053
            java.lang.String r1 = r5.name
            if (r1 != 0) goto L_0x004f
            r1 = r2
        L_0x000a:
            java.lang.String r4 = r6.name
            if (r4 != 0) goto L_0x0051
            r4 = r2
        L_0x000f:
            if (r1 != r4) goto L_0x0053
            r0 = r2
        L_0x0012:
            if (r0 == 0) goto L_0x0063
            java.lang.String r1 = r5.name
            if (r1 != 0) goto L_0x0055
        L_0x0018:
            r0 = r2
        L_0x0019:
            if (r0 == 0) goto L_0x0069
            java.lang.String r1 = r5.qualified_name
            if (r1 != 0) goto L_0x0065
            r1 = r2
        L_0x0020:
            java.lang.String r4 = r6.qualified_name
            if (r4 != 0) goto L_0x0067
            r4 = r2
        L_0x0025:
            if (r1 != r4) goto L_0x0069
            r0 = r2
        L_0x0028:
            if (r0 == 0) goto L_0x0079
            java.lang.String r1 = r5.qualified_name
            if (r1 != 0) goto L_0x006b
        L_0x002e:
            r0 = r2
        L_0x002f:
            if (r0 == 0) goto L_0x007f
            java.util.HashMap<java.lang.String, java.lang.String> r1 = r5.attributes
            if (r1 != 0) goto L_0x007b
            r1 = r2
        L_0x0036:
            java.util.HashMap<java.lang.String, java.lang.String> r4 = r6.attributes
            if (r4 != 0) goto L_0x007d
            r4 = r2
        L_0x003b:
            if (r1 != r4) goto L_0x007f
            r0 = r2
        L_0x003e:
            if (r0 == 0) goto L_0x008f
            java.util.HashMap<java.lang.String, java.lang.String> r1 = r5.attributes
            if (r1 != 0) goto L_0x0081
        L_0x0044:
            r0 = r2
        L_0x0045:
            if (r0 == 0) goto L_0x0091
            com.microsoft.bond.Modifier r1 = r5.modifier
            com.microsoft.bond.Modifier r4 = r6.modifier
            if (r1 != r4) goto L_0x0091
            r0 = r2
        L_0x004e:
            return r0
        L_0x004f:
            r1 = r3
            goto L_0x000a
        L_0x0051:
            r4 = r3
            goto L_0x000f
        L_0x0053:
            r0 = r3
            goto L_0x0012
        L_0x0055:
            java.lang.String r1 = r5.name
            int r1 = r1.length()
            java.lang.String r4 = r6.name
            int r4 = r4.length()
            if (r1 == r4) goto L_0x0018
        L_0x0063:
            r0 = r3
            goto L_0x0019
        L_0x0065:
            r1 = r3
            goto L_0x0020
        L_0x0067:
            r4 = r3
            goto L_0x0025
        L_0x0069:
            r0 = r3
            goto L_0x0028
        L_0x006b:
            java.lang.String r1 = r5.qualified_name
            int r1 = r1.length()
            java.lang.String r4 = r6.qualified_name
            int r4 = r4.length()
            if (r1 == r4) goto L_0x002e
        L_0x0079:
            r0 = r3
            goto L_0x002f
        L_0x007b:
            r1 = r3
            goto L_0x0036
        L_0x007d:
            r4 = r3
            goto L_0x003b
        L_0x007f:
            r0 = r3
            goto L_0x003e
        L_0x0081:
            java.util.HashMap<java.lang.String, java.lang.String> r1 = r5.attributes
            int r1 = r1.size()
            java.util.HashMap<java.lang.String, java.lang.String> r4 = r6.attributes
            int r4 = r4.size()
            if (r1 == r4) goto L_0x0044
        L_0x008f:
            r0 = r3
            goto L_0x0045
        L_0x0091:
            r0 = r3
            goto L_0x004e
        */
        throw new UnsupportedOperationException("Method not decompiled: com.microsoft.bond.Metadata.memberwiseCompareQuick(com.microsoft.bond.Metadata):boolean");
    }

    /* access modifiers changed from: protected */
    public boolean memberwiseCompareDeep(Metadata that) {
        boolean equals;
        boolean z;
        boolean equals2 = (1 != 0 && (this.name == null || this.name.equals(that.name))) && (this.qualified_name == null || this.qualified_name.equals(that.qualified_name));
        if (equals2 && this.attributes != null && this.attributes.size() != 0) {
            for (Map.Entry<String, String> e3 : this.attributes.entrySet()) {
                String val1 = e3.getValue();
                String val2 = that.attributes.get(e3.getKey());
                if (!equals2 || !that.attributes.containsKey(e3.getKey())) {
                    equals2 = false;
                } else {
                    equals2 = true;
                }
                if (equals2) {
                    if (equals2) {
                        if (val1 == null) {
                            z = true;
                        } else {
                            z = false;
                        }
                        if (z == (val2 == null)) {
                            equals = true;
                            if ((!equals && (val1 == null || val1.length() == val2.length())) || (val1 != null && !val1.equals(val2))) {
                                equals2 = false;
                                continue;
                            } else {
                                equals2 = true;
                                continue;
                            }
                        }
                    }
                    equals = false;
                    if (!equals && (val1 == null || val1.length() == val2.length())) {
                    }
                    equals2 = false;
                    continue;
                }
                if (!equals2) {
                    break;
                }
            }
        }
        return equals2 && (this.default_value == null || this.default_value.memberwiseCompare(that.default_value));
    }
}
