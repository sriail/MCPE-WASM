package com.microsoft.xbox.xle.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Button;
import android.widget.Checkable;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.microsoft.xbox.toolkit.XLERValueHelper;
import com.microsoft.xbox.toolkit.ui.FontManager;
import com.microsoft.xboxtcui.R;

public class IconFontToggleButton extends LinearLayout implements Checkable {
    private boolean checked;
    private String checkedIcon;
    private String checkedText;
    private TextView iconTextView;
    private TextView labelTextView;
    private String uncheckedIcon;
    private String uncheckedText;

    public IconFontToggleButton(Context context) {
        super(context);
    }

    public IconFontToggleButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews(context, attrs);
    }

    public IconFontToggleButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initViews(context, attrs);
    }

    private void initViews(Context context, AttributeSet attrs) {
        ((LayoutInflater) context.getSystemService("layout_inflater")).inflate(R.layout.iconfont_toggle_btn_view, this, true);
        this.iconTextView = (TextView) findViewById(R.id.iconfont_toggle_btn_icon);
        this.labelTextView = (TextView) findViewById(R.id.iconfont_toggle_btn_text);
        TypedArray a = context.obtainStyledAttributes(attrs, XLERValueHelper.getStyleableRValueArray("CustomTypeface"));
        String typeface = a.getString(XLERValueHelper.getStyleableRValue("CustomTypeface_typefaceSource"));
        a.recycle();
        TypedArray a2 = context.obtainStyledAttributes(attrs, XLERValueHelper.getStyleableRValueArray("IconFontToggleButton"));
        this.checkedText = a2.getString(XLERValueHelper.getStyleableRValue("IconFontToggleButton_text_checked"));
        this.uncheckedText = a2.getString(XLERValueHelper.getStyleableRValue("IconFontToggleButton_text_unchecked"));
        this.checkedIcon = a2.getString(XLERValueHelper.getStyleableRValue("IconFontToggleButton_icon_checked"));
        this.uncheckedIcon = a2.getString(XLERValueHelper.getStyleableRValue("IconFontToggleButton_icon_unchecked"));
        float iconSize = (float) a2.getDimensionPixelSize(XLERValueHelper.getStyleableRValue("IconFontToggleButton_icon_size"), -1);
        if (iconSize != -1.0f) {
            this.iconTextView.setTextSize(0, iconSize);
        }
        a2.recycle();
        applyCustomTypeface(context, typeface);
        setFocusable(true);
    }

    private void applyCustomTypeface(Context context, String typefaceSource) {
        if (typefaceSource != null && this.labelTextView != null) {
            this.labelTextView.setTypeface(FontManager.Instance().getTypeface(getContext(), typefaceSource));
        }
    }

    public boolean isChecked() {
        return this.checked;
    }

    public void setChecked(boolean checked2) {
        this.checked = checked2;
        sendAccessibilityEvent(1);
        if (this.labelTextView != null) {
            this.labelTextView.setText(this.checked ? this.checkedText : this.uncheckedText);
            this.labelTextView.setVisibility(0);
        }
        if (this.iconTextView != null) {
            this.iconTextView.setText(this.checked ? this.checkedIcon : this.uncheckedIcon);
            this.iconTextView.setVisibility(0);
        }
        invalidate();
    }

    public void toggle() {
        setChecked(!this.checked);
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClickable(true);
        info.setClassName(Button.class.getName());
    }

    public void setCheckedText(String text) {
        this.checkedText = text;
    }

    public void setUncheckedText(String text) {
        this.uncheckedText = text;
    }
}
