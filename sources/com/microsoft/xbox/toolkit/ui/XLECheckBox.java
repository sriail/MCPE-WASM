package com.microsoft.xbox.toolkit.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.widget.ExploreByTouchHelper;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import com.microsoft.xbox.toolkit.ui.util.LibCompat;
import com.microsoft.xboxtcui.R;

public class XLECheckBox extends ViewGroup {
    /* access modifiers changed from: private */
    public final CheckBox checkBox;
    private final TextView subText;
    private final TextView text;

    public XLECheckBox(Context context) {
        super(context);
        this.checkBox = new CheckBox(context);
        this.text = new TextView(context);
        this.subText = new TextView(context);
    }

    public XLECheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.checkBox = new CheckBox(context, attrs);
        this.text = new TextView(context, attrs);
        this.subText = new TextView(context, attrs);
        initialize(context, attrs);
    }

    public XLECheckBox(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.checkBox = new CheckBox(context, attrs);
        this.text = new TextView(context, attrs);
        this.subText = new TextView(context, attrs);
        initialize(context, attrs);
    }

    public CharSequence getText() {
        return this.text.getText();
    }

    public void setText(CharSequence text2) {
        this.text.setText(text2);
    }

    public CharSequence getSubText() {
        return this.subText.getText();
    }

    public void setSubText(CharSequence subText2) {
        this.subText.setText(subText2);
    }

    public boolean isChecked() {
        return this.checkBox.isChecked();
    }

    public void setChecked(boolean checked) {
        this.checkBox.setChecked(checked);
    }

    public void toggle() {
        this.checkBox.toggle();
    }

    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        this.checkBox.setEnabled(enabled);
        this.text.setEnabled(enabled);
        this.subText.setEnabled(enabled);
    }

    public void setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener listener) {
        this.checkBox.setOnCheckedChangeListener(listener);
    }

    private void initialize(Context context, AttributeSet attrs) {
        this.checkBox.setButtonDrawable(R.drawable.apptheme_btn_check_holo_light);
        addView(this.checkBox, new ViewGroup.LayoutParams(-2, -2));
        this.text.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                XLECheckBox.this.checkBox.toggle();
            }
        });
        addView(this.text, new ViewGroup.LayoutParams(-2, -2));
        addView(this.subText, new ViewGroup.LayoutParams(-2, -2));
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.XLECheckBox);
        try {
            if (!isInEditMode()) {
                LibCompat.setTextAppearance(this.text, a.getResourceId(R.styleable.XLECheckBox_textStyle, -1));
                this.text.setTypeface(FontManager.Instance().getTypeface(context, a.getString(R.styleable.XLECheckBox_textTypefaceSource)));
                LibCompat.setTextAppearance(this.subText, a.getResourceId(R.styleable.XLECheckBox_subTextStyle, -1));
                this.subText.setTypeface(FontManager.Instance().getTypeface(context, a.getString(R.styleable.XLECheckBox_subTextTypefaceSource)));
            }
            this.text.setText(a.getString(R.styleable.XLECheckBox_text));
            this.subText.setText(a.getString(R.styleable.XLECheckBox_subText));
        } finally {
            a.recycle();
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int w = View.MeasureSpec.getSize(widthMeasureSpec);
        int wMyMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int wChildMode = wMyMode == 0 ? 0 : ExploreByTouchHelper.INVALID_ID;
        int h = View.MeasureSpec.getSize(heightMeasureSpec);
        int hMyMode = View.MeasureSpec.getMode(heightMeasureSpec);
        int hChildMode = hMyMode == 0 ? 0 : ExploreByTouchHelper.INVALID_ID;
        int xCur = getPaddingLeft();
        int yCur = getPaddingTop();
        this.checkBox.measure(View.MeasureSpec.makeMeasureSpec(Math.max((w - xCur) - getPaddingRight(), 0), wChildMode), View.MeasureSpec.makeMeasureSpec(Math.max((h - yCur) - getPaddingBottom(), 0), hChildMode));
        int xCur2 = xCur + this.checkBox.getMeasuredWidth();
        this.text.measure(View.MeasureSpec.makeMeasureSpec(Math.max((w - xCur2) - getPaddingRight(), 0), wChildMode), View.MeasureSpec.makeMeasureSpec(Math.max((h - yCur) - getPaddingBottom(), 0), hChildMode));
        int yCur2 = yCur + Math.max(this.checkBox.getMeasuredHeight(), this.text.getMeasuredHeight());
        this.subText.measure(View.MeasureSpec.makeMeasureSpec(Math.max((w - xCur2) - getPaddingRight(), 0), wChildMode), View.MeasureSpec.makeMeasureSpec(Math.max((h - yCur2) - getPaddingBottom(), 0), hChildMode));
        int xCur3 = xCur2 + Math.max(this.text.getMeasuredWidth(), this.subText.getMeasuredWidth());
        int yCur3 = yCur2 + this.subText.getMeasuredHeight();
        int xCur4 = xCur3 + getPaddingRight();
        int yCur4 = yCur3 + getPaddingBottom();
        setMeasuredDimension(wMyMode == 0 ? xCur4 : Math.min(xCur4, w), hMyMode == 0 ? yCur4 : Math.min(yCur4, h));
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        int lCheckbox = getPaddingLeft();
        int cCheckbox = getPaddingTop() + Math.max(this.checkBox.getMeasuredHeight() / 2, this.text.getMeasuredHeight() / 2);
        int tCheckbox = cCheckbox - (this.checkBox.getMeasuredWidth() / 2);
        this.checkBox.layout(lCheckbox, tCheckbox, this.checkBox.getMeasuredWidth() + lCheckbox, this.checkBox.getMeasuredHeight() + tCheckbox);
        int lText = lCheckbox + this.checkBox.getMeasuredWidth();
        int tText = cCheckbox - (this.text.getMeasuredHeight() / 2);
        this.text.layout(lText, tText, this.text.getMeasuredWidth() + lText, this.text.getMeasuredHeight() + tText);
        int lSubText = lText;
        int tSubText = tText + this.text.getMeasuredHeight();
        this.subText.layout(lSubText, tSubText, this.subText.getMeasuredWidth() + lSubText, this.subText.getMeasuredHeight() + tSubText);
    }
}
