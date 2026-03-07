package com.microsoft.xbox.idp.util;

import android.net.Uri;
import android.text.TextUtils;
import com.microsoft.xbox.xle.app.ImageUtil;

public class HttpUtil {

    public enum ImageSize {
        SMALL(64, 64),
        MEDIUM(208, 208),
        LARGE(ImageUtil.MEDIUM_TABLET, ImageUtil.MEDIUM_TABLET);
        
        /* access modifiers changed from: private */
        public final int h;
        /* access modifiers changed from: private */
        public final int w;

        private ImageSize(int w2, int h2) {
            this.w = w2;
            this.h = h2;
        }
    }

    public static Uri.Builder getImageSizeUrlParams(Uri.Builder b, ImageSize sz) {
        return b.appendQueryParameter("w", Integer.toString(sz.w)).appendQueryParameter("h", Integer.toString(sz.h));
    }

    public static String getEndpoint(Uri uri) {
        return uri.getScheme() + "://" + uri.getEncodedAuthority();
    }

    public static String getPathAndQuery(Uri uri) {
        String path = uri.getEncodedPath();
        String query = uri.getEncodedQuery();
        String fragment = uri.getEncodedFragment();
        StringBuffer sb = new StringBuffer();
        sb.append(path);
        if (!TextUtils.isEmpty(query)) {
            sb.append("?").append(query);
        }
        if (!TextUtils.isEmpty(fragment)) {
            sb.append("#").append(fragment);
        }
        return sb.toString();
    }

    public static HttpCall appendCommonParameters(HttpCall httpCall, String version) {
        httpCall.setXboxContractVersionHeaderValue(version);
        httpCall.setContentTypeHeaderValue("application/json");
        httpCall.setRetryAllowed(true);
        return httpCall;
    }
}
