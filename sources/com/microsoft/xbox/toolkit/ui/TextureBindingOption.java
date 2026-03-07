package com.microsoft.xbox.toolkit.ui;

import com.microsoft.xbox.toolkit.XLERValueHelper;

public class TextureBindingOption {
    public static final int DO_NOT_SCALE = -1;
    public static final int DO_NOT_USE_PLACEHOLDER = -1;
    public static final TextureBindingOption DefaultBindingOption = new TextureBindingOption();
    public static final int DefaultResourceIdForEmpty = XLERValueHelper.getDrawableRValue("empty");
    public static final int DefaultResourceIdForError = XLERValueHelper.getDrawableRValue("error");
    public static final int DefaultResourceIdForLoading = XLERValueHelper.getDrawableRValue("empty");
    public static final TextureBindingOption KeepAsIsBindingOption = new TextureBindingOption(-1, -1, -1, -1, false);
    public final int height;
    public final int resourceIdForError;
    public final int resourceIdForLoading;
    public final boolean useFileCache;
    public final int width;

    public TextureBindingOption() {
        this(-1, -1, DefaultResourceIdForLoading, DefaultResourceIdForError, false);
    }

    public TextureBindingOption(int width2, int height2) {
        this(width2, height2, true);
    }

    public TextureBindingOption(int width2, int height2, boolean useFileCache2) {
        this(width2, height2, DefaultResourceIdForLoading, DefaultResourceIdForError, useFileCache2);
    }

    public TextureBindingOption(int width2, int height2, int resourceForLoading, int resourceForError, boolean useFileCache2) {
        this.width = width2;
        this.height = height2;
        this.resourceIdForLoading = resourceForLoading;
        this.resourceIdForError = resourceForError;
        this.useFileCache = useFileCache2;
    }

    public static TextureBindingOption createDoNotScale(int resourceForLoading, int resourceForError, boolean useFileCache2) {
        return new TextureBindingOption(-1, -1, resourceForLoading, resourceForError, useFileCache2);
    }

    public boolean equals(Object rhsuntyped) {
        if (this == rhsuntyped) {
            return true;
        }
        if (!(rhsuntyped instanceof TextureBindingOption)) {
            return false;
        }
        TextureBindingOption rhs = (TextureBindingOption) rhsuntyped;
        if (this.width == rhs.width && this.height == rhs.height && this.resourceIdForError == rhs.resourceIdForError && this.resourceIdForLoading == rhs.resourceIdForLoading) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        throw new UnsupportedOperationException();
    }
}
