package com.mojang.android;

import android.widget.TextView;

public class TextViewReader implements StringValue {
    private TextView _view;

    public TextViewReader(TextView view) {
        this._view = view;
    }

    public String getStringValue() {
        return this._view.getText().toString();
    }
}
