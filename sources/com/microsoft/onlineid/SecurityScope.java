package com.microsoft.onlineid;

import com.microsoft.onlineid.internal.Objects;
import com.microsoft.onlineid.internal.Strings;
import java.util.Locale;

public class SecurityScope implements ISecurityScope {
    private static final long serialVersionUID = 1;
    private String _oAuthString;
    private final String _policy;
    private final String _target;

    public SecurityScope(String target, String policy) {
        Strings.verifyArgumentNotNullOrEmpty(target, "target");
        this._target = target;
        this._policy = policy;
    }

    public String getTarget() {
        return this._target;
    }

    public String getPolicy() {
        return this._policy;
    }

    public int hashCode() {
        return Objects.hashCode(toString());
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ISecurityScope)) {
            return false;
        }
        ISecurityScope scope = (ISecurityScope) other;
        if (!getTarget().equalsIgnoreCase(scope.getTarget()) || !Strings.equalsIgnoreCase(getPolicy(), scope.getPolicy())) {
            return false;
        }
        return true;
    }

    public String toString() {
        if (this._oAuthString == null) {
            this._oAuthString = String.format(Locale.US, "service::%s::%s", new Object[]{this._target, this._policy});
        }
        return this._oAuthString;
    }
}
