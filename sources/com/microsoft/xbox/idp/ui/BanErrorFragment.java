package com.microsoft.xbox.idp.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.microsoft.xbox.idp.R;
import com.microsoft.xbox.idp.compat.BaseFragment;

public class BanErrorFragment extends BaseFragment {
    public static final String ARG_GAMER_TAG = "ARG_GAMER_TAG";
    private static final String TAG = BanErrorFragment.class.getSimpleName();

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.xbid_fragment_error_ban, container, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        String gamerTag;
        super.onViewCreated(view, savedInstanceState);
        Bundle args = getArguments();
        if (args == null) {
            gamerTag = "";
            Log.e(TAG, "No arguments provided");
        } else if (args.containsKey("ARG_GAMER_TAG")) {
            gamerTag = args.getString("ARG_GAMER_TAG");
        } else {
            gamerTag = "";
            Log.e(TAG, "No ARG_GAMER_TAG provided");
        }
        ((TextView) view.findViewById(R.id.xbid_greeting_text)).setText(getString(R.string.xbid_ban_error_header_android, new Object[]{gamerTag}));
    }
}
