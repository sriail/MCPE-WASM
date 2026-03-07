package com.microsoft.xbox.idp.compat;

import android.app.Activity;

public abstract class BaseActivity extends Activity {
    public boolean hasFragment(int fragmentId) {
        return getFragmentManager().findFragmentById(fragmentId) != null;
    }

    public void addFragment(int fragmentId, BaseFragment fragment) {
        getFragmentManager().beginTransaction().add(fragmentId, fragment).commit();
    }
}
