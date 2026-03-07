package com.microsoft.xbox.toolkit.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import com.microsoft.xbox.toolkit.XLERValueHelper;
import java.net.URI;

public class XLEImageViewFast extends XLEImageView {
    private TextureBindingOption option;
    protected int pendingBitmapResourceId = -1;
    private String pendingFilePath = null;
    protected URI pendingUri = null;
    private boolean useFileCache = true;

    public XLEImageViewFast(Context context) {
        super(context);
        setSoundEffectsEnabled(false);
    }

    public XLEImageViewFast(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            TypedArray a = context.obtainStyledAttributes(attrs, XLERValueHelper.getStyleableRValueArray("XLEImageViewFast"));
            setImageResource(a.getResourceId(XLERValueHelper.getStyleableRValue("XLEImageViewFast_src"), -1));
            a.recycle();
            setSoundEffectsEnabled(false);
        }
    }

    public void setImageResource(int resourceId) {
        if (hasSize()) {
            bindToResourceId(resourceId);
        } else {
            this.pendingBitmapResourceId = resourceId;
        }
    }

    public void setImageURI2(URI uri) {
        if (hasSize()) {
            bindToUri(uri);
        } else {
            this.pendingUri = uri;
        }
    }

    public void setImageURI2(URI uri, boolean useFilaCache) {
        this.useFileCache = useFilaCache;
        this.option = new TextureBindingOption(getWidth(), getHeight(), this.useFileCache);
        if (hasSize()) {
            bindToUri(uri, this.option);
        } else {
            this.pendingUri = uri;
        }
    }

    public void setImageURI2(URI uri, int loadingResourceId, int errorResourceId) {
        this.option = new TextureBindingOption(getWidth(), getHeight(), loadingResourceId, errorResourceId, this.useFileCache);
        if (hasSize()) {
            bindToUri(uri, this.option);
        } else {
            this.pendingUri = uri;
        }
    }

    public void setImageFilePath(String filePath) {
        if (hasSize()) {
            bindToFilePath(filePath);
        } else {
            this.pendingFilePath = filePath;
        }
    }

    public void setImageURI(Uri uri) {
        throw new UnsupportedOperationException();
    }

    /* access modifiers changed from: protected */
    public boolean hasSize() {
        return getWidth() > 0 && getHeight() > 0;
    }

    private void bindToResourceId(int resourceId) {
        this.pendingBitmapResourceId = -1;
        TextureManager.Instance().bindToView(resourceId, (ImageView) this, getWidth(), getHeight());
    }

    /* access modifiers changed from: protected */
    public void bindToUri(URI uri) {
        this.pendingUri = null;
        bindToUri(uri, new TextureBindingOption(getWidth(), getHeight(), this.useFileCache));
    }

    private void bindToUri(URI uri, TextureBindingOption option2) {
        this.pendingUri = null;
        this.option = null;
        TextureManager.Instance().bindToView(uri, this, option2);
    }

    private void bindToFilePath(String filePath) {
        this.pendingFilePath = null;
        TextureManager.Instance().bindToViewFromFile(filePath, this, getWidth(), getHeight());
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(resolveSize(0, widthMeasureSpec), resolveSize(0, heightMeasureSpec));
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (hasSize()) {
            if (this.pendingBitmapResourceId >= 0) {
                bindToResourceId(this.pendingBitmapResourceId);
            }
            if (this.pendingUri != null || (this.pendingUri == null && this.option != null)) {
                if (this.option != null) {
                    bindToUri(this.pendingUri, new TextureBindingOption(getWidth(), getHeight(), this.option.resourceIdForLoading, this.option.resourceIdForError, this.option.useFileCache));
                } else {
                    bindToUri(this.pendingUri);
                }
            }
            if (this.pendingFilePath != null) {
                bindToFilePath(this.pendingFilePath);
            }
        }
    }

    public void setOnClickListener(View.OnClickListener listener) {
        super.setOnClickListener(TouchUtil.createOnClickListener(listener));
    }
}
