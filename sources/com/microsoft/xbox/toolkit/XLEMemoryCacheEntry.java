package com.microsoft.xbox.toolkit;

import com.facebook.share.internal.ShareConstants;

public class XLEMemoryCacheEntry<V> {
    private int byteCount;
    private V data;

    public XLEMemoryCacheEntry(V data2, int byteCount2) {
        if (data2 == null) {
            throw new IllegalArgumentException(ShareConstants.WEB_DIALOG_PARAM_DATA);
        } else if (byteCount2 <= 0) {
            throw new IllegalArgumentException("byteCount");
        } else {
            this.data = data2;
            this.byteCount = byteCount2;
        }
    }

    public int getByteCount() {
        return this.byteCount;
    }

    public V getValue() {
        return this.data;
    }
}
