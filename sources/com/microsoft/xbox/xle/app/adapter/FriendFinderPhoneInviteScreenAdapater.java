package com.microsoft.xbox.xle.app.adapter;

import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.ui.CustomTypefaceTextView;
import com.microsoft.xbox.xle.app.activity.FriendFinder.FriendFinderPhoneInviteScreenViewModel;
import com.microsoft.xbox.xle.viewmodel.AdapterBase;
import com.microsoft.xboxtcui.R;
import com.microsoft.xboxtcui.XboxTcuiSdk;

public class FriendFinderPhoneInviteScreenAdapater extends AdapterBase {
    private FriendFinderPhoneInviteListAdapter contactsListAdapter;
    private ListView contactsListView = ((ListView) findViewById(R.id.friendfinder_suggestions_list));
    private FrameLayout loadingLayout = ((FrameLayout) findViewById(R.id.friendfinder_suggestions_loading));
    private CustomTypefaceTextView subtitleTextView = ((CustomTypefaceTextView) findViewById(R.id.friendfinder_suggestions_subtitle));
    private CustomTypefaceTextView titleTextView = ((CustomTypefaceTextView) findViewById(R.id.friendfinder_suggestions_title));
    private FriendFinderPhoneInviteScreenViewModel viewModel;

    public FriendFinderPhoneInviteScreenAdapater(FriendFinderPhoneInviteScreenViewModel viewModel2) {
        super(viewModel2);
        XLEAssert.fail("This isn't supported yet.");
        this.viewModel = viewModel2;
        XLEAssert.assertNotNull(this.titleTextView);
        XLEAssert.assertNotNull(this.subtitleTextView);
        XLEAssert.assertNotNull(this.contactsListView);
        XLEAssert.assertNotNull(this.loadingLayout);
        this.titleTextView.setText(R.string.FriendFinder_PhoneInviteFriends_Dialog_Title);
        this.subtitleTextView.setText(XboxTcuiSdk.getResources().getString(R.string.FriendFinder_PhoneInviteFriends_Dialog_Text).replace("-", "\n\n"));
        this.contactsListView.setChoiceMode(2);
    }

    public void onStart() {
        super.onStart();
        this.contactsListAdapter = new FriendFinderPhoneInviteListAdapter(XboxTcuiSdk.getActivity(), R.layout.friendfinder_phone_invite_list_item);
        this.contactsListView.setAdapter(this.contactsListAdapter);
        this.contactsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            /* JADX WARNING: type inference failed for: r2v0, types: [android.widget.AdapterView<?>, android.widget.AdapterView, android.view.ViewGroup] */
            /* JADX WARNING: Unknown variable types count: 1 */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void onItemClick(android.widget.AdapterView<?> r2, android.view.View r3, int r4, long r5) {
                /*
                    r1 = this;
                    android.widget.Adapter r0 = r2.getAdapter()
                    r0.getView(r4, r3, r2)
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: com.microsoft.xbox.xle.app.adapter.FriendFinderPhoneInviteScreenAdapater.AnonymousClass1.onItemClick(android.widget.AdapterView, android.view.View, int, long):void");
            }
        });
    }

    /* access modifiers changed from: protected */
    public void updateViewOverride() {
        this.loadingLayout.setVisibility(this.viewModel.isBusy() ? 0 : 8);
        this.contactsListAdapter.clear();
        this.contactsListAdapter.addAll(this.viewModel.getContacts());
        this.contactsListAdapter.notifyDataSetChanged();
    }
}
