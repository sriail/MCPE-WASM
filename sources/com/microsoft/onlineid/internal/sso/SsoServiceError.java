package com.microsoft.onlineid.internal.sso;

import android.util.SparseArray;

public enum SsoServiceError {
    Unknown(1),
    ClientNotAuthorized(2),
    UnsupportedClientVersion(3),
    StorageException(4),
    IllegalArgumentException(5),
    AccountNotFound(6),
    NetworkException(7),
    StsException(8),
    InvalidResponseException(9),
    MasterRedirectException(10),
    ClientConfigUpdateNeededException(11);
    
    private static final SparseArray<SsoServiceError> _lookup = null;
    private int _code;

    static {
        int i;
        _lookup = new SparseArray<>();
        for (SsoServiceError error : values()) {
            _lookup.put(error.getCode(), error);
        }
    }

    private SsoServiceError(int code) {
        this._code = code;
    }

    public int getCode() {
        return this._code;
    }

    public static SsoServiceError get(int code) {
        return _lookup.get(code);
    }
}
