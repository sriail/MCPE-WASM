package com.microsoft.xbox.toolkit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.util.Map;

public class GsonUtil {

    public interface JsonBodyBuilder {
        void buildBody(JsonWriter jsonWriter) throws IOException;
    }

    public static <T> T deserializeJson(InputStream stream, Class<T> resultClass) {
        return deserializeJson(createMinimumGsonBuilder().create(), stream, resultClass);
    }

    public static <T> T deserializeJson(String input, Class<T> resultClass) {
        return deserializeJson(createMinimumGsonBuilder().create(), input, resultClass);
    }

    public static <T> T deserializeJson(InputStream stream, Class<T> resultClass, Type typeForAdapter, Object typeAdapter) {
        return deserializeJson(createMinimumGsonBuilder().registerTypeAdapter(typeForAdapter, typeAdapter).create(), stream, resultClass);
    }

    public static <T> T deserializeJson(InputStream stream, Class<T> resultClass, Map<Type, Object> adapters) {
        GsonBuilder builder = createMinimumGsonBuilder();
        for (Map.Entry<Type, Object> e : adapters.entrySet()) {
            builder.registerTypeAdapter(e.getKey(), e.getValue());
        }
        return deserializeJson(builder.create(), stream, resultClass);
    }

    public static <T> T deserializeJson(String input, Class<T> resultClass, Type typeForAdapter, Object typeAdapter) {
        return deserializeJson(createMinimumGsonBuilder().registerTypeAdapter(typeForAdapter, typeAdapter).create(), input, resultClass);
    }

    /* JADX WARNING: Removed duplicated region for block: B:19:0x0025 A[SYNTHETIC, Splitter:B:19:0x0025] */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x002a A[SYNTHETIC, Splitter:B:22:0x002a] */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x0033 A[SYNTHETIC, Splitter:B:27:0x0033] */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x0038 A[SYNTHETIC, Splitter:B:30:0x0038] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static <T> T deserializeJson(com.google.gson.Gson r7, java.io.InputStream r8, java.lang.Class<T> r9) {
        /*
            r2 = 0
            r0 = 0
            r4 = 0
            java.io.InputStreamReader r3 = new java.io.InputStreamReader     // Catch:{ Exception -> 0x0022, all -> 0x0030 }
            r3.<init>(r8)     // Catch:{ Exception -> 0x0022, all -> 0x0030 }
            java.io.BufferedReader r1 = new java.io.BufferedReader     // Catch:{ Exception -> 0x004b, all -> 0x0044 }
            r1.<init>(r3)     // Catch:{ Exception -> 0x004b, all -> 0x0044 }
            java.lang.Object r4 = r7.fromJson((java.io.Reader) r1, r9)     // Catch:{ Exception -> 0x004e, all -> 0x0047 }
            if (r1 == 0) goto L_0x0016
            r1.close()     // Catch:{ Exception -> 0x003c }
        L_0x0016:
            if (r3 == 0) goto L_0x0052
            r3.close()     // Catch:{ Exception -> 0x001e }
            r0 = r1
            r2 = r3
        L_0x001d:
            return r4
        L_0x001e:
            r5 = move-exception
            r0 = r1
            r2 = r3
            goto L_0x001d
        L_0x0022:
            r5 = move-exception
        L_0x0023:
            if (r0 == 0) goto L_0x0028
            r0.close()     // Catch:{ Exception -> 0x003e }
        L_0x0028:
            if (r2 == 0) goto L_0x001d
            r2.close()     // Catch:{ Exception -> 0x002e }
            goto L_0x001d
        L_0x002e:
            r5 = move-exception
            goto L_0x001d
        L_0x0030:
            r5 = move-exception
        L_0x0031:
            if (r0 == 0) goto L_0x0036
            r0.close()     // Catch:{ Exception -> 0x0040 }
        L_0x0036:
            if (r2 == 0) goto L_0x003b
            r2.close()     // Catch:{ Exception -> 0x0042 }
        L_0x003b:
            throw r5
        L_0x003c:
            r5 = move-exception
            goto L_0x0016
        L_0x003e:
            r5 = move-exception
            goto L_0x0028
        L_0x0040:
            r6 = move-exception
            goto L_0x0036
        L_0x0042:
            r6 = move-exception
            goto L_0x003b
        L_0x0044:
            r5 = move-exception
            r2 = r3
            goto L_0x0031
        L_0x0047:
            r5 = move-exception
            r0 = r1
            r2 = r3
            goto L_0x0031
        L_0x004b:
            r5 = move-exception
            r2 = r3
            goto L_0x0023
        L_0x004e:
            r5 = move-exception
            r0 = r1
            r2 = r3
            goto L_0x0023
        L_0x0052:
            r0 = r1
            r2 = r3
            goto L_0x001d
        */
        throw new UnsupportedOperationException("Method not decompiled: com.microsoft.xbox.toolkit.GsonUtil.deserializeJson(com.google.gson.Gson, java.io.InputStream, java.lang.Class):java.lang.Object");
    }

    public static <T> T deserializeJson(Gson gson, String input, Class<T> resultClass) {
        try {
            return gson.fromJson(input, resultClass);
        } catch (Exception e) {
            return null;
        }
    }

    public static GsonBuilder createMinimumGsonBuilder() {
        return new GsonBuilder().excludeFieldsWithModifiers(128);
    }

    public static String toJsonString(Object obj) {
        return new Gson().toJson(obj);
    }

    public static String buildJsonBody(JsonBodyBuilder builder) throws IOException {
        JsonWriter w;
        StringWriter out = new StringWriter();
        try {
            w = new JsonWriter(out);
            builder.buildBody(w);
            String stringWriter = out.toString();
            w.close();
            out.close();
            return stringWriter;
        } catch (Throwable th) {
            out.close();
            throw th;
        }
    }
}
