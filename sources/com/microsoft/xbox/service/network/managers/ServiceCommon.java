package com.microsoft.xbox.service.network.managers;

import android.text.TextUtils;
import com.google.gson.JsonObject;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.StreamUtil;
import com.microsoft.xbox.toolkit.TimeMonitor;
import com.microsoft.xbox.toolkit.UrlUtil;
import com.microsoft.xbox.toolkit.XLEErrorCode;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.network.HttpClientFactory;
import com.microsoft.xbox.toolkit.network.XLEHttpStatusAndStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import net.hockeyapp.android.utils.HttpURLConnectionBuilder;
import org.apache.http.Header;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ByteArrayEntity;

public class ServiceCommon {
    public static final int MaxBIErrorParamLength = 2048;

    public static void AddWebHeaders(HttpUriRequest httpRequest, List<Header> headers) {
        if (headers != null) {
            for (Header header : headers) {
                httpRequest.addHeader(header);
            }
        }
    }

    public static int deleteWithStatus(String url, List<Header> headers) throws XLEException {
        URI uri = UrlUtil.getEncodedUri(url);
        String url2 = uri.toString();
        new TimeMonitor();
        XLEHttpStatusAndStream statusAndStream = excuteHttpRequest(new HttpDelete(uri), url2, headers, false, 0);
        statusAndStream.close();
        return statusAndStream.statusCode;
    }

    public static boolean delete(String url, List<Header> headers) throws XLEException {
        int statusCode = deleteWithStatus(url, headers);
        return statusCode == 200 || statusCode == 204;
    }

    public static boolean delete(String url, List<Header> headers, String body) throws XLEException {
        try {
            return JavaUtil.isNullOrEmpty(body) ? delete(url, headers) : delete(url, headers, body.getBytes(HttpURLConnectionBuilder.DEFAULT_CHARSET));
        } catch (UnsupportedEncodingException e) {
            throw new XLEException(5, (Throwable) e);
        }
    }

    public static boolean delete(String url, List<Header> headers, byte[] body) throws XLEException {
        boolean value = false;
        URI uri = UrlUtil.getEncodedUri(url);
        String url2 = uri.toString();
        new TimeMonitor();
        HttpDeleteWithRequestBody httpDelete = new HttpDeleteWithRequestBody(uri);
        if (body != null && body.length > 0) {
            try {
                httpDelete.setEntity(new ByteArrayEntity(body));
            } catch (Exception e) {
                throw new XLEException(5, (Throwable) e);
            }
        }
        XLEHttpStatusAndStream statusAndStream = excuteHttpRequest(httpDelete, url2, headers, false, 0);
        if (statusAndStream.statusCode == 200 || statusAndStream.statusCode == 204) {
            value = true;
        }
        statusAndStream.close();
        return value;
    }

    private static void ParseHttpResponseForStatus(String url, int statusCode, String statusLine) throws XLEException {
        ParseHttpResponseForStatus(url, statusCode, statusLine, (InputStream) null);
    }

    private static void ParseHttpResponseForStatus(String url, int statusCode, String statusLine, InputStream stream) throws XLEException {
        if (statusCode >= 200 && statusCode < 400) {
            return;
        }
        if (statusCode == -1) {
            throw new XLEException(3);
        } else if (statusCode == 401 || statusCode == 403) {
            throw new XLEException(XLEErrorCode.NOT_AUTHORIZED);
        } else if (statusCode == 400) {
            if (stream == null) {
                throw new XLEException(15);
            }
            throw new XLEException(15, (String) null, (Throwable) null, StreamUtil.ReadAsString(stream));
        } else if (statusCode == 500) {
            throw new XLEException(13);
        } else if (statusCode == 503) {
            throw new XLEException(18);
        } else if (statusCode == 404) {
            throw new XLEException(21);
        } else {
            throw new XLEException(4);
        }
    }

    public static XLEHttpStatusAndStream getStreamAndStatus(String url, List<Header> headers) throws XLEException {
        XLEHttpStatusAndStream statusAndStream = getStreamAndStatus(url, headers, true, 0);
        if (statusAndStream == null || JavaUtil.isNullOrEmpty(statusAndStream.redirectUrl)) {
            return statusAndStream;
        }
        return getStreamAndStatus(statusAndStream.redirectUrl, headers);
    }

    private static XLEHttpStatusAndStream getStreamAndStatus(String url, List<Header> headers, boolean urlEncode, int timeoutOverride) throws XLEException {
        return getStreamAndStatus(url, headers, urlEncode, timeoutOverride, false);
    }

    private static XLEHttpStatusAndStream getStreamAndStatus(String url, List<Header> headers, boolean urlEncode, int timeoutOverride, boolean addUserObjectFromBadRequestResponse) throws XLEException {
        URI uri = null;
        if (urlEncode) {
            uri = UrlUtil.getEncodedUri(url);
        } else {
            try {
                uri = new URI(url);
            } catch (URISyntaxException e) {
            }
        }
        return excuteHttpRequest(new HttpGet(uri), uri.toString(), headers, true, timeoutOverride, addUserObjectFromBadRequestResponse);
    }

    public static XLEHttpStatusAndStream postStringWithStatus(String url, List<Header> headers, String body) throws XLEException {
        try {
            return postStreamWithStatus(url, headers, body.getBytes(HttpURLConnectionBuilder.DEFAULT_CHARSET));
        } catch (UnsupportedEncodingException e) {
            throw new XLEException(5, (Throwable) e);
        }
    }

    public static XLEHttpStatusAndStream postStreamWithStatus(String url, List<Header> headers, byte[] body) throws XLEException {
        URI uri = UrlUtil.getEncodedUri(url);
        String url2 = uri.toString();
        HttpPost post = new HttpPost(uri);
        if (body != null && body.length > 0) {
            try {
                post.setEntity(new ByteArrayEntity(body));
            } catch (Exception e) {
                throw new XLEException(5, (Throwable) e);
            }
        }
        return excuteHttpRequest(post, url2, headers, false, 0);
    }

    public static XLEHttpStatusAndStream putStringWithStatus(String url, List<Header> headers, String body) throws XLEException {
        try {
            return putStreamWithStatus(url, headers, body.getBytes(HttpURLConnectionBuilder.DEFAULT_CHARSET));
        } catch (UnsupportedEncodingException e) {
            throw new XLEException(5, (Throwable) e);
        }
    }

    public static XLEHttpStatusAndStream putStreamWithStatus(String url, List<Header> headers, byte[] body) throws XLEException {
        URI uri = UrlUtil.getEncodedUri(url);
        String url2 = uri.toString();
        HttpPut put = new HttpPut(uri);
        if (body != null && body.length > 0) {
            try {
                put.setEntity(new ByteArrayEntity(body));
            } catch (Exception e) {
                throw new XLEException(5, (Throwable) e);
            }
        }
        return excuteHttpRequest(put, url2, headers, false, 0);
    }

    private static XLEHttpStatusAndStream excuteHttpRequest(HttpUriRequest request, String url, List<Header> headers, boolean expectResponseEntity, int timeoutOverride) throws XLEException {
        return excuteHttpRequest(request, url, headers, expectResponseEntity, timeoutOverride, false);
    }

    private static XLEHttpStatusAndStream excuteHttpRequest(HttpUriRequest request, String url, List<Header> headers, boolean expectResponseEntity, int timeoutOverride, boolean addUserObjectFromResponse) throws XLEException {
        AddWebHeaders(request, headers);
        new XLEHttpStatusAndStream();
        XLEHttpStatusAndStream rv = HttpClientFactory.networkOperationsFactory.getHttpClient(timeoutOverride).getHttpStatusAndStreamInternal(request, true);
        if (!addUserObjectFromResponse) {
            try {
                ParseHttpResponseForStatus(url, rv.statusCode, rv.statusLine);
            } catch (XLEException e) {
                JsonObject callStackJson = new JsonObject();
                JsonObject responseJson = new JsonObject();
                String responseDescription = "";
                int responseStatusCode = rv == null ? 0 : rv.statusCode;
                String requestUrl = "";
                if (request != null) {
                    String method = request.getMethod();
                }
                if (rv != null && !TextUtils.isEmpty(rv.statusLine)) {
                    responseDescription = rv.statusLine.length() > 2048 ? rv.statusLine.substring(0, 2048) : rv.statusLine;
                }
                if (!(request == null || request.getURI() == null)) {
                    requestUrl = request.getURI().toString();
                }
                if (requestUrl.length() > 2048) {
                    requestUrl = requestUrl.substring(0, 2048);
                }
                callStackJson.addProperty("Request", requestUrl);
                responseJson.addProperty("code", (Number) Integer.valueOf(responseStatusCode));
                responseJson.addProperty("description", responseDescription);
                callStackJson.add("Response", responseJson);
                throw e;
            }
        } else {
            ParseHttpResponseForStatus(url, rv.statusCode, rv.statusLine, rv.stream);
        }
        if (rv.stream != null || !expectResponseEntity) {
            return rv;
        }
        throw new XLEException(7);
    }
}
