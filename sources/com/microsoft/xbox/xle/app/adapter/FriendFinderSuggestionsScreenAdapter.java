package com.microsoft.xbox.xle.app.adapter;

import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.ui.CustomTypefaceTextView;
import com.microsoft.xbox.toolkit.ui.XLEButton;
import com.microsoft.xbox.toolkit.ui.XLEListView;
import com.microsoft.xbox.xle.app.activity.FriendFinder.FriendFinderSuggestionsScreenViewModel;
import com.microsoft.xbox.xle.viewmodel.AdapterBase;
import com.microsoft.xboxtcui.R;
import com.microsoft.xboxtcui.XboxTcuiSdk;
import java.util.ArrayList;

public class FriendFinderSuggestionsScreenAdapter extends AdapterBase {
    /* access modifiers changed from: private */
    public XLEButton advanceButton = ((XLEButton) findViewById(R.id.friendfinder_suggestions_button));
    private ViewGroup emptyListHeaderContainer = ((ViewGroup) findViewById(R.id.friendfinder_suggestions_empty_header_container));
    private FrameLayout listHeaderContainer;
    private ViewGroup listHeaderGroup = ((ViewGroup) findViewById(R.id.friendfinder_suggestions_header));
    private FrameLayout loadingOverlay = ((FrameLayout) findViewById(R.id.friendfinder_suggestions_loading));
    private CustomTypefaceTextView subtitleTextView = ((CustomTypefaceTextView) findViewById(R.id.friendfinder_suggestions_subtitle));
    /* access modifiers changed from: private */
    public FriendFinderSuggestionsListAdapter suggestionsListAdapter;
    /* access modifiers changed from: private */
    public XLEListView suggestionsListView = ((XLEListView) findViewById(R.id.friendfinder_suggestions_list));
    private CustomTypefaceTextView titleTextView = ((CustomTypefaceTextView) findViewById(R.id.friendfinder_suggestions_title));
    /* access modifiers changed from: private */
    public FriendFinderSuggestionsScreenViewModel viewModel;

    public FriendFinderSuggestionsScreenAdapter(FriendFinderSuggestionsScreenViewModel viewModel2) {
        super(viewModel2);
        this.viewModel = viewModel2;
        XLEAssert.assertNotNull(this.titleTextView);
        XLEAssert.assertNotNull(this.subtitleTextView);
        XLEAssert.assertNotNull(this.suggestionsListView);
        XLEAssert.assertNotNull(this.emptyListHeaderContainer);
        XLEAssert.assertNotNull(this.listHeaderGroup);
        XLEAssert.assertNotNull(this.advanceButton);
        XLEAssert.assertNotNull(this.loadingOverlay);
        this.listHeaderContainer = new FrameLayout(XboxTcuiSdk.getActivity());
        this.suggestionsListView.addHeaderView(this.listHeaderContainer, (Object) null, false);
        this.suggestionsListView.setChoiceMode(2);
    }

    public void onStart() {
        super.onStart();
        this.suggestionsListAdapter = new FriendFinderSuggestionsListAdapter(XboxTcuiSdk.getActivity(), R.layout.friendfinder_suggestions_list_item, true);
        this.suggestionsListView.setAdapter(this.suggestionsListAdapter);
        this.suggestionsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            /* JADX WARNING: type inference failed for: r4v0, types: [android.widget.AdapterView<?>, android.widget.AdapterView, android.view.ViewGroup] */
            /* JADX WARNING: Unknown variable types count: 1 */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void onItemClick(android.widget.AdapterView<?> r4, android.view.View r5, int r6, long r7) {
                /*
                    r3 = this;
                    android.widget.Adapter r1 = r4.getAdapter()
                    r1.getView(r6, r5, r4)
                    com.microsoft.xbox.xle.app.adapter.FriendFinderSuggestionsScreenAdapter r1 = com.microsoft.xbox.xle.app.adapter.FriendFinderSuggestionsScreenAdapter.this
                    com.microsoft.xbox.toolkit.ui.XLEListView r1 = r1.suggestionsListView
                    int r0 = r1.getCheckedItemCount()
                    if (r0 != 0) goto L_0x001f
                    com.microsoft.xbox.xle.app.adapter.FriendFinderSuggestionsScreenAdapter r1 = com.microsoft.xbox.xle.app.adapter.FriendFinderSuggestionsScreenAdapter.this
                    com.microsoft.xbox.toolkit.ui.XLEButton r1 = r1.advanceButton
                    int r2 = com.microsoft.xboxtcui.R.string.FriendFinder_Phone_Next_ButtonText
                    r1.setText(r2)
                L_0x001e:
                    return
                L_0x001f:
                    r1 = 1
                    if (r0 != r1) goto L_0x002e
                    com.microsoft.xbox.xle.app.adapter.FriendFinderSuggestionsScreenAdapter r1 = com.microsoft.xbox.xle.app.adapter.FriendFinderSuggestionsScreenAdapter.this
                    com.microsoft.xbox.toolkit.ui.XLEButton r1 = r1.advanceButton
                    int r2 = com.microsoft.xboxtcui.R.string.Profile_Profile_AddFriend
                    r1.setText(r2)
                    goto L_0x001e
                L_0x002e:
                    com.microsoft.xbox.xle.app.adapter.FriendFinderSuggestionsScreenAdapter r1 = com.microsoft.xbox.xle.app.adapter.FriendFinderSuggestionsScreenAdapter.this
                    com.microsoft.xbox.toolkit.ui.XLEButton r1 = r1.advanceButton
                    int r2 = com.microsoft.xboxtcui.R.string.FriendFinder_AddFriends
                    r1.setText(r2)
                    goto L_0x001e
                */
                throw new UnsupportedOperationException("Method not decompiled: com.microsoft.xbox.xle.app.adapter.FriendFinderSuggestionsScreenAdapter.AnonymousClass1.onItemClick(android.widget.AdapterView, android.view.View, int, long):void");
            }
        });
        this.advanceButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SparseBooleanArray sparseCheckedPositions = FriendFinderSuggestionsScreenAdapter.this.suggestionsListView.getCheckedItemPositions();
                ArrayList<Integer> checkedPositions = new ArrayList<>();
                for (int i = 0; i < FriendFinderSuggestionsScreenAdapter.this.suggestionsListAdapter.getCount() + 1; i++) {
                    if (sparseCheckedPositions.get(i)) {
                        checkedPositions.add(Integer.valueOf(i - 1));
                    }
                }
                if (checkedPositions.size() > 0) {
                    FriendFinderSuggestionsScreenAdapter.this.viewModel.addSuggestions(checkedPositions);
                } else {
                    FriendFinderSuggestionsScreenAdapter.this.viewModel.navigateToSkip();
                }
            }
        });
    }

    /* access modifiers changed from: protected */
    public void updateViewOverride() {
        this.loadingOverlay.setVisibility(this.viewModel.isBusy() ? 0 : 8);
        this.titleTextView.setText(this.viewModel.getTitle());
        this.subtitleTextView.setText(this.viewModel.getSubtitle());
        this.suggestionsListAdapter.clear();
        this.suggestionsListAdapter.addAll(this.viewModel.getSuggestions());
        this.suggestionsListAdapter.notifyDataSetChanged();
        this.emptyListHeaderContainer.removeAllViews();
        this.listHeaderContainer.removeAllViews();
        if (this.suggestionsListAdapter.getCount() > 0) {
            this.listHeaderContainer.addView(this.listHeaderGroup);
        } else {
            this.emptyListHeaderContainer.addView(this.listHeaderGroup);
        }
    }
}
