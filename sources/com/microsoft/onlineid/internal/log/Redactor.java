package com.microsoft.onlineid.internal.log;

import java.util.Locale;

public class Redactor {
    protected static final String RedactedStringEmptyReplacement = "";
    protected static final String RedactedStringNullReplacement = "(null)";
    protected static final String RedactedStringReplacement = "*(%d)*";
    protected static final String RedactedStringStarReplacement = "***";

    private enum RedactionType {
        Email,
        Password,
        String
    }

    public static boolean shouldRedact() {
        return Logger.shouldRedact();
    }

    public static String redactEmail(String str) {
        return doRedact(str, RedactionType.Email);
    }

    public static String redactPassword(String str) {
        return doRedact(str, RedactionType.Password);
    }

    public static String redactString(String str) {
        return doRedact(str, RedactionType.String);
    }

    private static String doRedact(String stringToRedact, RedactionType type) {
        if (stringToRedact == null) {
            return RedactedStringNullReplacement;
        }
        if (stringToRedact.isEmpty()) {
            return "";
        }
        switch (type) {
            case Email:
                return String.format(Locale.getDefault(), RedactedStringReplacement, new Object[]{Integer.valueOf(stringToRedact.length())});
            case Password:
                return RedactedStringStarReplacement;
            case String:
                return String.format(Locale.getDefault(), RedactedStringReplacement, new Object[]{Integer.valueOf(stringToRedact.length())});
            default:
                return RedactedStringStarReplacement;
        }
    }
}
