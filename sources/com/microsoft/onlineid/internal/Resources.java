package com.microsoft.onlineid.internal;

import android.content.Context;
import android.content.res.Resources;
import java.util.Locale;

public class Resources {
    private final Context _appContext;

    public Resources(Context appContext) {
        this._appContext = appContext;
    }

    public String getString(String name) {
        try {
            return this._appContext.getString(getIdentifierByType(name, "string"));
        } catch (Resources.NotFoundException e) {
            Assertion.check(false, String.format(Locale.US, "String resource with name %s not found", new Object[]{name}));
            return null;
        }
    }

    public int getDimensionPixelSize(String name) {
        try {
            return this._appContext.getResources().getDimensionPixelSize(getIdentifierByType(name, "dimen"));
        } catch (Resources.NotFoundException e) {
            Assertion.check(false, String.format(Locale.US, "Dimen resource with name %s not found", new Object[]{name}));
            return 0;
        }
    }

    public int getLayout(String name) {
        try {
            return getIdentifierByType(name, "layout");
        } catch (Resources.NotFoundException e) {
            Assertion.check(false, String.format(Locale.US, "Layout resource with name %s not found", new Object[]{name}));
            return 0;
        }
    }

    public int getId(String name) {
        try {
            return getIdentifierByType(name, "id");
        } catch (Resources.NotFoundException e) {
            Assertion.check(false, String.format(Locale.US, "Id resource with name %s not found", new Object[]{name}));
            return 0;
        }
    }

    public int getMenu(String name) {
        try {
            return getIdentifierByType(name, "menu");
        } catch (Resources.NotFoundException e) {
            Assertion.check(false, String.format(Locale.US, "Menu resource with name %s not found", new Object[]{name}));
            return 0;
        }
    }

    public static String getString(Context appContext, String name) {
        return new Resources(appContext).getString(name);
    }

    public String getSdkVersion() {
        return getString("sdk_version_name");
    }

    public static String getSdkVersion(Context appContext) {
        return new Resources(appContext).getSdkVersion();
    }

    private int getIdentifierByType(String name, String type) {
        try {
            return this._appContext.getResources().getIdentifier(name, type, this._appContext.getPackageName());
        } catch (Resources.NotFoundException e) {
            Assertion.check(false, String.format(Locale.US, "%s resource with name %s not found", new Object[]{type, name}));
            return 0;
        }
    }
}
