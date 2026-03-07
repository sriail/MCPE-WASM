package com.microsoft.onlineid.internal.configuration;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.Set;

public abstract class AbstractSettings {
    protected final SharedPreferences _preferences;

    /* access modifiers changed from: protected */
    public abstract Editor edit();

    protected AbstractSettings(Context applicationContext, String storageName) {
        this._preferences = applicationContext.getSharedPreferences(storageName, 0);
    }

    /* access modifiers changed from: protected */
    public boolean getBoolean(ISetting<? extends Boolean> setting) {
        return this._preferences.getBoolean(setting.getSettingName(), ((Boolean) setting.getDefaultValue()).booleanValue());
    }

    /* access modifiers changed from: protected */
    public int getInt(ISetting<? extends Integer> setting) {
        return this._preferences.getInt(setting.getSettingName(), ((Integer) setting.getDefaultValue()).intValue());
    }

    /* access modifiers changed from: protected */
    public String getString(ISetting<? extends String> setting) {
        return this._preferences.getString(setting.getSettingName(), (String) setting.getDefaultValue());
    }

    /* access modifiers changed from: protected */
    public Set<String> getStringSet(ISetting<? extends Set<String>> setting) {
        return this._preferences.getStringSet(setting.getSettingName(), (Set) setting.getDefaultValue());
    }

    public static class Editor {
        protected final SharedPreferences.Editor _editor;

        protected Editor(SharedPreferences.Editor editor) {
            this._editor = editor;
        }

        public boolean commit() {
            return this._editor.commit();
        }

        /* access modifiers changed from: protected */
        public Editor clear() {
            this._editor.clear();
            return this;
        }

        /* access modifiers changed from: protected */
        public Editor setBoolean(ISetting<? extends Boolean> setting, boolean value) {
            this._editor.putBoolean(setting.getSettingName(), value);
            return this;
        }

        /* access modifiers changed from: protected */
        public Editor setInt(ISetting<? extends Integer> setting, int value) {
            this._editor.putInt(setting.getSettingName(), value);
            return this;
        }

        /* access modifiers changed from: protected */
        public Editor setString(ISetting<? extends String> setting, String value) {
            this._editor.putString(setting.getSettingName(), value);
            return this;
        }

        /* access modifiers changed from: protected */
        public Editor setStringSet(ISetting<? extends Set<String>> setting, Set<String> value) {
            this._editor.putStringSet(setting.getSettingName(), value);
            return this;
        }
    }
}
