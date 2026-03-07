package com.microsoft.xbox.service.model.sls;

import com.microsoft.xbox.toolkit.GsonUtil;

public class NeverListRequest {
    public long xuid;

    public NeverListRequest(long xuid2) {
        this.xuid = xuid2;
    }

    public static String getNeverListRequestBody(NeverListRequest neverListRequest) {
        return GsonUtil.toJsonString(neverListRequest);
    }
}
