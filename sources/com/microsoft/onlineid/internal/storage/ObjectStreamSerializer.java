package com.microsoft.onlineid.internal.storage;

import android.util.Base64;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ObjectStreamSerializer<ObjectType> implements ISerializer<ObjectType> {
    public ObjectType deserialize(String serialized) throws IOException {
        if (serialized == null) {
            return null;
        }
        try {
            ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(Base64.decode(serialized, 2)));
            try {
                ObjectType object = input.readObject();
                input.close();
                return object;
            } catch (ClassNotFoundException ex) {
                throw new IOException(ex);
            } catch (ClassCastException ex2) {
                throw new IOException(ex2);
            } catch (Throwable th) {
                input.close();
                throw th;
            }
        } catch (IllegalArgumentException ex3) {
            throw new IOException(ex3);
        }
    }

    public Set<ObjectType> deserializeAll(Map<String, String> serializedMap) throws IOException {
        if (serializedMap.isEmpty()) {
            return Collections.emptySet();
        }
        Set<ObjectType> objects = new HashSet<>();
        for (String serialized : serializedMap.values()) {
            objects.add(deserialize(serialized));
        }
        return objects;
    }

    /* JADX INFO: finally extract failed */
    public String serialize(ObjectType object) throws IOException {
        if (object == null) {
            return null;
        }
        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        ObjectOutputStream output = new ObjectOutputStream(byteArray);
        try {
            output.writeObject(object);
            output.close();
            return Base64.encodeToString(byteArray.toByteArray(), 2);
        } catch (Throwable th) {
            output.close();
            throw th;
        }
    }

    public Map<String, String> serializeAll(Map<String, ObjectType> objectMap) throws IOException {
        if (objectMap.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, String> serializedMap = new HashMap<>();
        for (Map.Entry<String, ObjectType> entry : objectMap.entrySet()) {
            serializedMap.put(entry.getKey(), serialize(entry.getValue()));
        }
        return serializedMap;
    }
}
