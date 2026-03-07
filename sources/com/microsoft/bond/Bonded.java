package com.microsoft.bond;

import com.microsoft.bond.BondSerializable;
import com.microsoft.bond.internal.Marshaler;
import java.io.IOException;
import java.io.InputStream;

public class Bonded<T extends BondSerializable> implements BondSerializable {
    private ProtocolReader Data;
    private T Value;

    public Bonded() {
    }

    public Bonded(ProtocolReader data) throws IOException {
        read(data);
    }

    public Bonded(ProtocolReader data, SchemaDef schema) throws IOException {
        read(data, schema);
    }

    public Bonded(T value) {
        this.Value = value;
    }

    public T getValue() {
        return this.Value;
    }

    public void deserialize(BondSerializable value) throws IOException {
        value.read(this.Data);
    }

    public BondSerializable clone() {
        if (this.Data == null) {
            return new Bonded(this.Value);
        }
        try {
            return new Bonded(this.Data.cloneReader());
        } catch (IOException e) {
            return null;
        }
    }

    public void reset() {
        this.Value = null;
        this.Data = null;
    }

    public void read(ProtocolReader reader) throws IOException {
        readNested(reader);
    }

    public void read(ProtocolReader reader, BondSerializable schema) throws IOException {
        readNested(reader);
    }

    public void readNested(ProtocolReader reader) throws IOException {
        this.Value = null;
        this.Data = reader.cloneReader();
        reader.skip(BondDataType.BT_STRUCT);
    }

    public void unmarshal(InputStream input) throws IOException {
        Marshaler.unmarshal(input, this);
    }

    public void unmarshal(InputStream input, BondSerializable schema) throws IOException {
        Marshaler.unmarshal(input, (SchemaDef) schema, this);
    }

    public void write(ProtocolWriter writer) throws IOException {
        if (this.Data != null) {
            Transcoder.transcode(writer, this.Data.cloneReader());
        } else {
            this.Value.write(writer);
        }
    }

    public void writeNested(ProtocolWriter writer, boolean isBase) throws IOException {
        write(writer);
    }

    public void marshal(ProtocolWriter writer) throws IOException {
        Marshaler.marshal(this, writer);
    }

    public boolean memberwiseCompare(Object that) {
        if (this.Value != null) {
            return this.Value.memberwiseCompare(that);
        }
        return false;
    }
}
