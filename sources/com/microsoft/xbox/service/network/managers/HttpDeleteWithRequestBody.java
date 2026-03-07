package com.microsoft.xbox.service.network.managers;

import java.net.URI;
import org.apache.http.client.methods.HttpPost;

public class HttpDeleteWithRequestBody extends HttpPost {
    public HttpDeleteWithRequestBody(URI url) {
        super(url);
    }

    public String getMethod() {
        return "DELETE";
    }
}
