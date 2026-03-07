package com.microsoft.xbox.xle.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.microsoft.xbox.service.network.managers.friendfinder.PhoneContactInfo;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.ui.CustomTypefaceTextView;
import com.microsoft.xbox.xle.app.XLEUtil;
import com.microsoft.xboxtcui.R;

public class FriendFinderPhoneInviteListAdapter extends ArrayAdapter<PhoneContactInfo.Contact> {
    public FriendFinderPhoneInviteListAdapter(Context context, int resource) {
        super(context, resource);
        XLEAssert.fail("This isn't supported yet.");
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        int i = 0;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.friendfinder_phone_invite_list_item, parent, false);
            viewHolder = new ViewHolder();
            CustomTypefaceTextView unused = viewHolder.contactNameTextView = (CustomTypefaceTextView) convertView.findViewById(R.id.friendfinder_phone_invite_name);
            CustomTypefaceTextView unused2 = viewHolder.onXboxTextView = (CustomTypefaceTextView) convertView.findViewById(R.id.friendfinder_phone_invite_name_onxbox);
            CustomTypefaceTextView unused3 = viewHolder.checkTextView = (CustomTypefaceTextView) convertView.findViewById(R.id.friendfinder_phone_invite_checkbox);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        PhoneContactInfo.Contact contact = (PhoneContactInfo.Contact) getItem(position);
        boolean isChecked = ((ListView) parent).isItemChecked(position);
        convertView.setBackgroundResource(isChecked ? R.color.XboxOneGreen : 17170445);
        if (contact != null) {
            XLEUtil.updateTextAndVisibilityIfNotNull(viewHolder.contactNameTextView, contact.displayName, 0);
            viewHolder.onXboxTextView.setVisibility(contact.isOnXbox ? 0 : 8);
            CustomTypefaceTextView access$300 = viewHolder.checkTextView;
            if (!isChecked) {
                i = 4;
            }
            access$300.setVisibility(i);
        }
        return convertView;
    }

    private static class ViewHolder {
        /* access modifiers changed from: private */
        public CustomTypefaceTextView checkTextView;
        /* access modifiers changed from: private */
        public CustomTypefaceTextView contactNameTextView;
        /* access modifiers changed from: private */
        public CustomTypefaceTextView onXboxTextView;

        private ViewHolder() {
        }
    }
}
