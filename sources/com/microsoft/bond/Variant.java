package com.microsoft.bond;

import com.microsoft.bond.ProtocolReader;
import com.microsoft.bond.internal.Marshaler;
import com.microsoft.bond.internal.ReadHelper;
import com.microsoft.cll.android.EventEnums;
import java.io.IOException;
import java.io.InputStream;

public class Variant implements BondSerializable, BondMirror {
    private double double_value;
    private long int_value;
    private boolean nothing;
    private String string_value;
    private long uint_value;
    private String wstring_value;

    public BondSerializable clone() {
        return null;
    }

    public final long getUint_value() {
        return this.uint_value;
    }

    public final void setUint_value(long value) {
        this.uint_value = value;
    }

    public final long getInt_value() {
        return this.int_value;
    }

    public final void setInt_value(long value) {
        this.int_value = value;
    }

    public final double getDouble_value() {
        return this.double_value;
    }

    public final void setDouble_value(double value) {
        this.double_value = value;
    }

    public final String getString_value() {
        return this.string_value;
    }

    public final void setString_value(String value) {
        this.string_value = value;
    }

    public final String getWstring_value() {
        return this.wstring_value;
    }

    public final void setWstring_value(String value) {
        this.wstring_value = value;
    }

    public final boolean getNothing() {
        return this.nothing;
    }

    public final void setNothing(boolean value) {
        this.nothing = value;
    }

    public static class Schema {
        /* access modifiers changed from: private */
        public static final Metadata double_value_metadata = new Metadata();
        /* access modifiers changed from: private */
        public static final Metadata int_value_metadata = new Metadata();
        public static final Metadata metadata = new Metadata();
        /* access modifiers changed from: private */
        public static final Metadata nothing_metadata = new Metadata();
        public static final SchemaDef schemaDef = new SchemaDef();
        /* access modifiers changed from: private */
        public static final Metadata string_value_metadata = new Metadata();
        /* access modifiers changed from: private */
        public static final Metadata uint_value_metadata = new Metadata();
        /* access modifiers changed from: private */
        public static final Metadata wstring_value_metadata = new Metadata();

        static {
            metadata.setName("Variant");
            metadata.setQualified_name("com.microsoft.bond.Variant");
            uint_value_metadata.setName("uint_value");
            uint_value_metadata.getDefault_value().setUint_value(0);
            int_value_metadata.setName("int_value");
            int_value_metadata.getDefault_value().setInt_value(0);
            double_value_metadata.setName("double_value");
            double_value_metadata.getDefault_value().setDouble_value(EventEnums.SampleRate_0_percent);
            string_value_metadata.setName("string_value");
            wstring_value_metadata.setName("wstring_value");
            nothing_metadata.setName("nothing");
            nothing_metadata.getDefault_value().setUint_value(0);
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
                    field.setMetadata(uint_value_metadata);
                    field.getType().setId(BondDataType.BT_UINT64);
                    structDef.getFields().add(field);
                    FieldDef field2 = new FieldDef();
                    field2.setId(1);
                    field2.setMetadata(int_value_metadata);
                    field2.getType().setId(BondDataType.BT_INT64);
                    structDef.getFields().add(field2);
                    FieldDef field3 = new FieldDef();
                    field3.setId(2);
                    field3.setMetadata(double_value_metadata);
                    field3.getType().setId(BondDataType.BT_DOUBLE);
                    structDef.getFields().add(field3);
                    FieldDef field4 = new FieldDef();
                    field4.setId(3);
                    field4.setMetadata(string_value_metadata);
                    field4.getType().setId(BondDataType.BT_STRING);
                    structDef.getFields().add(field4);
                    FieldDef field5 = new FieldDef();
                    field5.setId(4);
                    field5.setMetadata(wstring_value_metadata);
                    field5.getType().setId(BondDataType.BT_WSTRING);
                    structDef.getFields().add(field5);
                    FieldDef field6 = new FieldDef();
                    field6.setId(5);
                    field6.setMetadata(nothing_metadata);
                    field6.getType().setId(BondDataType.BT_BOOL);
                    structDef.getFields().add(field6);
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
                return Long.valueOf(this.uint_value);
            case 1:
                return Long.valueOf(this.int_value);
            case 2:
                return Double.valueOf(this.double_value);
            case 3:
                return this.string_value;
            case 4:
                return this.wstring_value;
            case 5:
                return Boolean.valueOf(this.nothing);
            default:
                return null;
        }
    }

    public void setField(FieldDef fieldDef, Object value) {
        switch (fieldDef.getId()) {
            case 0:
                this.uint_value = ((Long) value).longValue();
                return;
            case 1:
                this.int_value = ((Long) value).longValue();
                return;
            case 2:
                this.double_value = ((Double) value).doubleValue();
                return;
            case 3:
                this.string_value = (String) value;
                return;
            case 4:
                this.wstring_value = (String) value;
                return;
            case 5:
                this.nothing = ((Boolean) value).booleanValue();
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

    public Variant() {
        reset();
    }

    public void reset() {
        reset("Variant", "com.microsoft.bond.Variant");
    }

    /* access modifiers changed from: protected */
    public void reset(String name, String qualifiedName) {
        this.uint_value = 0;
        this.int_value = 0;
        this.double_value = EventEnums.SampleRate_0_percent;
        this.string_value = "";
        this.wstring_value = "";
        this.nothing = false;
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
            this.uint_value = reader.readUInt64();
        }
        if (!canOmitFields || !reader.readFieldOmitted()) {
            this.int_value = reader.readInt64();
        }
        if (!canOmitFields || !reader.readFieldOmitted()) {
            this.double_value = reader.readDouble();
        }
        if (!canOmitFields || !reader.readFieldOmitted()) {
            this.string_value = reader.readString();
        }
        if (!canOmitFields || !reader.readFieldOmitted()) {
            this.wstring_value = reader.readWString();
        }
        if (!canOmitFields || !reader.readFieldOmitted()) {
            this.nothing = reader.readBool();
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
                        this.uint_value = ReadHelper.readUInt64(reader, fieldTag.type);
                        break;
                    case 1:
                        this.int_value = ReadHelper.readInt64(reader, fieldTag.type);
                        break;
                    case 2:
                        this.double_value = ReadHelper.readDouble(reader, fieldTag.type);
                        break;
                    case 3:
                        this.string_value = ReadHelper.readString(reader, fieldTag.type);
                        break;
                    case 4:
                        this.wstring_value = ReadHelper.readWString(reader, fieldTag.type);
                        break;
                    case 5:
                        this.nothing = ReadHelper.readBool(reader, fieldTag.type);
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
        boolean z = true;
        boolean canOmitFields = writer.hasCapability(ProtocolCapability.CAN_OMIT_FIELDS);
        writer.writeStructBegin(Schema.metadata, isBase);
        if (!canOmitFields || this.uint_value != Schema.uint_value_metadata.getDefault_value().getUint_value()) {
            writer.writeFieldBegin(BondDataType.BT_UINT64, 0, Schema.uint_value_metadata);
            writer.writeUInt64(this.uint_value);
            writer.writeFieldEnd();
        } else {
            writer.writeFieldOmitted(BondDataType.BT_UINT64, 0, Schema.uint_value_metadata);
        }
        if (!canOmitFields || this.int_value != Schema.int_value_metadata.getDefault_value().getInt_value()) {
            writer.writeFieldBegin(BondDataType.BT_INT64, 1, Schema.int_value_metadata);
            writer.writeInt64(this.int_value);
            writer.writeFieldEnd();
        } else {
            writer.writeFieldOmitted(BondDataType.BT_INT64, 1, Schema.int_value_metadata);
        }
        if (!canOmitFields || this.double_value != Schema.double_value_metadata.getDefault_value().getDouble_value()) {
            writer.writeFieldBegin(BondDataType.BT_DOUBLE, 2, Schema.double_value_metadata);
            writer.writeDouble(this.double_value);
            writer.writeFieldEnd();
        } else {
            writer.writeFieldOmitted(BondDataType.BT_DOUBLE, 2, Schema.double_value_metadata);
        }
        if (!canOmitFields || this.string_value != Schema.string_value_metadata.getDefault_value().getString_value()) {
            writer.writeFieldBegin(BondDataType.BT_STRING, 3, Schema.string_value_metadata);
            writer.writeString(this.string_value);
            writer.writeFieldEnd();
        } else {
            writer.writeFieldOmitted(BondDataType.BT_STRING, 3, Schema.string_value_metadata);
        }
        if (!canOmitFields || this.wstring_value != Schema.wstring_value_metadata.getDefault_value().getWstring_value()) {
            writer.writeFieldBegin(BondDataType.BT_WSTRING, 4, Schema.wstring_value_metadata);
            writer.writeWString(this.wstring_value);
            writer.writeFieldEnd();
        } else {
            writer.writeFieldOmitted(BondDataType.BT_WSTRING, 4, Schema.wstring_value_metadata);
        }
        if (canOmitFields) {
            boolean z2 = this.nothing;
            if (Schema.nothing_metadata.getDefault_value().getUint_value() == 0) {
                z = false;
            }
            if (z2 == z) {
                writer.writeFieldOmitted(BondDataType.BT_BOOL, 5, Schema.nothing_metadata);
                writer.writeStructEnd(isBase);
            }
        }
        writer.writeFieldBegin(BondDataType.BT_BOOL, 5, Schema.nothing_metadata);
        writer.writeBool(this.nothing);
        writer.writeFieldEnd();
        writer.writeStructEnd(isBase);
    }

    public boolean memberwiseCompare(Object obj) {
        if (obj == null) {
            return false;
        }
        Variant that = (Variant) obj;
        if (!memberwiseCompareQuick(that) || !memberwiseCompareDeep(that)) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x0041  */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x0044  */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x0057  */
    /* JADX WARNING: Removed duplicated region for block: B:44:0x0060  */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x0084  */
    /* JADX WARNING: Removed duplicated region for block: B:61:0x009a  */
    /* JADX WARNING: Removed duplicated region for block: B:62:0x009c  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean memberwiseCompareQuick(com.microsoft.bond.Variant r9) {
        /*
            r8 = this;
            r2 = 1
            r3 = 0
            r0 = 1
            if (r0 == 0) goto L_0x0062
            long r4 = r8.uint_value
            long r6 = r9.uint_value
            int r1 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r1 != 0) goto L_0x0062
            r0 = r2
        L_0x000e:
            if (r0 == 0) goto L_0x0064
            long r4 = r8.int_value
            long r6 = r9.int_value
            int r1 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r1 != 0) goto L_0x0064
            r0 = r2
        L_0x0019:
            if (r0 == 0) goto L_0x006e
            double r4 = r8.double_value
            boolean r1 = java.lang.Double.isNaN(r4)
            if (r1 == 0) goto L_0x0066
            double r4 = r9.double_value
            boolean r1 = java.lang.Double.isNaN(r4)
            if (r1 == 0) goto L_0x006e
        L_0x002b:
            r0 = r2
        L_0x002c:
            if (r0 == 0) goto L_0x0074
            java.lang.String r1 = r8.string_value
            if (r1 != 0) goto L_0x0070
            r1 = r2
        L_0x0033:
            java.lang.String r4 = r9.string_value
            if (r4 != 0) goto L_0x0072
            r4 = r2
        L_0x0038:
            if (r1 != r4) goto L_0x0074
            r0 = r2
        L_0x003b:
            if (r0 == 0) goto L_0x0084
            java.lang.String r1 = r8.string_value
            if (r1 != 0) goto L_0x0076
        L_0x0041:
            r0 = r2
        L_0x0042:
            if (r0 == 0) goto L_0x008a
            java.lang.String r1 = r8.wstring_value
            if (r1 != 0) goto L_0x0086
            r1 = r2
        L_0x0049:
            java.lang.String r4 = r9.wstring_value
            if (r4 != 0) goto L_0x0088
            r4 = r2
        L_0x004e:
            if (r1 != r4) goto L_0x008a
            r0 = r2
        L_0x0051:
            if (r0 == 0) goto L_0x009a
            java.lang.String r1 = r8.wstring_value
            if (r1 != 0) goto L_0x008c
        L_0x0057:
            r0 = r2
        L_0x0058:
            if (r0 == 0) goto L_0x009c
            boolean r1 = r8.nothing
            boolean r4 = r9.nothing
            if (r1 != r4) goto L_0x009c
            r0 = r2
        L_0x0061:
            return r0
        L_0x0062:
            r0 = r3
            goto L_0x000e
        L_0x0064:
            r0 = r3
            goto L_0x0019
        L_0x0066:
            double r4 = r8.double_value
            double r6 = r9.double_value
            int r1 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r1 == 0) goto L_0x002b
        L_0x006e:
            r0 = r3
            goto L_0x002c
        L_0x0070:
            r1 = r3
            goto L_0x0033
        L_0x0072:
            r4 = r3
            goto L_0x0038
        L_0x0074:
            r0 = r3
            goto L_0x003b
        L_0x0076:
            java.lang.String r1 = r8.string_value
            int r1 = r1.length()
            java.lang.String r4 = r9.string_value
            int r4 = r4.length()
            if (r1 == r4) goto L_0x0041
        L_0x0084:
            r0 = r3
            goto L_0x0042
        L_0x0086:
            r1 = r3
            goto L_0x0049
        L_0x0088:
            r4 = r3
            goto L_0x004e
        L_0x008a:
            r0 = r3
            goto L_0x0051
        L_0x008c:
            java.lang.String r1 = r8.wstring_value
            int r1 = r1.length()
            java.lang.String r4 = r9.wstring_value
            int r4 = r4.length()
            if (r1 == r4) goto L_0x0057
        L_0x009a:
            r0 = r3
            goto L_0x0058
        L_0x009c:
            r0 = r3
            goto L_0x0061
        */
        throw new UnsupportedOperationException("Method not decompiled: com.microsoft.bond.Variant.memberwiseCompareQuick(com.microsoft.bond.Variant):boolean");
    }

    /* access modifiers changed from: protected */
    public boolean memberwiseCompareDeep(Variant that) {
        return (1 != 0 && (this.string_value == null || this.string_value.equals(that.string_value))) && (this.wstring_value == null || this.wstring_value.equals(that.wstring_value));
    }
}
