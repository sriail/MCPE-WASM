package com.microsoft.onlineid.internal.ui;

import com.microsoft.onlineid.internal.ui.PropertyBag;

public interface IWebPropertyProvider {
    String getProperty(PropertyBag.Key key);

    boolean handlesProperty(PropertyBag.Key key);

    void setProperty(PropertyBag.Key key, String str);
}
