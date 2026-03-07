package com.microsoft.xbox.xle.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.microsoft.xbox.service.model.friendfinder.FriendFinderSuggestionModel;
import com.microsoft.xbox.service.network.managers.IPeopleHubResult;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.ui.CustomTypefaceTextView;
import com.microsoft.xbox.toolkit.ui.XLERoundedUniversalImageView;
import com.microsoft.xbox.toolkit.ui.XLEUniversalImageView;
import com.microsoft.xbox.xle.app.FriendFinderSettings;
import com.microsoft.xbox.xle.app.XLEUtil;
import com.microsoft.xboxtcui.R;
import java.net.URI;

public class FriendFinderSuggestionsListAdapter extends ArrayAdapter<FriendFinderSuggestionModel> {
    private boolean containsHeader;
    private URI facebookImageUri;

    public FriendFinderSuggestionsListAdapter(Context context, int resource, boolean containsHeader2) {
        super(context, resource);
        this.containsHeader = containsHeader2;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        int i;
        int i2;
        boolean isFacebook;
        int i3;
        int i4;
        int i5 = 17170445;
        int i6 = 0;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.friendfinder_suggestions_list_item, parent, false);
            viewHolder = new ViewHolder();
            XLERoundedUniversalImageView unused = viewHolder.gamerpicImageView = (XLERoundedUniversalImageView) convertView.findViewById(R.id.friendfinder_suggestions_item_image);
            XLEUniversalImageView unused2 = viewHolder.iconImageView = (XLEUniversalImageView) convertView.findViewById(R.id.friendfinder_suggestions_item_icon_image);
            CustomTypefaceTextView unused3 = viewHolder.gamertagTextView = (CustomTypefaceTextView) convertView.findViewById(R.id.friendfinder_suggestions_item_gamertag);
            CustomTypefaceTextView unused4 = viewHolder.realNameTextView = (CustomTypefaceTextView) convertView.findViewById(R.id.friendfinder_suggestions_item_realname);
            CustomTypefaceTextView unused5 = viewHolder.iconTextView = (CustomTypefaceTextView) convertView.findViewById(R.id.friendfinder_suggestions_item_icon_text);
            CustomTypefaceTextView unused6 = viewHolder.presenceTextView = (CustomTypefaceTextView) convertView.findViewById(R.id.friendfinder_suggestions_item_presence);
            CustomTypefaceTextView unused7 = viewHolder.checkTextView = (CustomTypefaceTextView) convertView.findViewById(R.id.friendfinder_suggestions_item_check);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        FriendFinderSuggestionModel model = (FriendFinderSuggestionModel) getItem(position);
        ListView listView = (ListView) parent;
        if (this.containsHeader) {
            i = 1;
        } else {
            i = 0;
        }
        boolean isChecked = listView.isItemChecked(i + position);
        if (isChecked) {
            i2 = R.color.white_15_percent;
        } else {
            i2 = 17170445;
        }
        convertView.setBackgroundResource(i2);
        CustomTypefaceTextView access$500 = viewHolder.iconTextView;
        if (!isChecked) {
            i5 = R.color.white_15_percent;
        }
        access$500.setBackgroundResource(i5);
        if (model != null) {
            viewHolder.gamerpicImageView.setImageURI2(model.imageUri, R.drawable.gamerpic_missing, R.drawable.gamerpic_missing);
            XLEUtil.updateTextAndVisibilityIfNotNull(viewHolder.gamertagTextView, model.gamerTag, 0);
            XLEUtil.updateTextAndVisibilityIfNotNull(viewHolder.realNameTextView, model.realName, 0);
            XLEUtil.updateTextAndVisibilityIfNotNull(viewHolder.presenceTextView, model.presence, 0);
            if (model.recommendationType == IPeopleHubResult.RecommendationType.FacebookFriend) {
                isFacebook = true;
            } else {
                isFacebook = false;
            }
            XLEUniversalImageView access$200 = viewHolder.iconImageView;
            if (isFacebook) {
                i3 = 0;
            } else {
                i3 = 4;
            }
            access$200.setVisibility(i3);
            CustomTypefaceTextView access$5002 = viewHolder.iconTextView;
            if (isFacebook) {
                i4 = 4;
            } else {
                i4 = 0;
            }
            access$5002.setVisibility(i4);
            if (isFacebook) {
                if (this.facebookImageUri == null) {
                    String imageUriPath = FriendFinderSettings.getIconBySize(IPeopleHubResult.RecommendationType.FacebookFriend.name(), FriendFinderSettings.IconImageSize.MEDIUM);
                    if (!JavaUtil.isNullOrEmpty(imageUriPath)) {
                        this.facebookImageUri = URI.create(imageUriPath);
                    }
                }
                viewHolder.iconImageView.setImageURI2(this.facebookImageUri);
            }
            CustomTypefaceTextView access$700 = viewHolder.checkTextView;
            if (!isChecked) {
                i6 = 4;
            }
            access$700.setVisibility(i6);
        }
        return convertView;
    }

    private static class ViewHolder {
        /* access modifiers changed from: private */
        public CustomTypefaceTextView checkTextView;
        /* access modifiers changed from: private */
        public XLERoundedUniversalImageView gamerpicImageView;
        /* access modifiers changed from: private */
        public CustomTypefaceTextView gamertagTextView;
        /* access modifiers changed from: private */
        public XLEUniversalImageView iconImageView;
        /* access modifiers changed from: private */
        public CustomTypefaceTextView iconTextView;
        /* access modifiers changed from: private */
        public CustomTypefaceTextView presenceTextView;
        /* access modifiers changed from: private */
        public CustomTypefaceTextView realNameTextView;

        private ViewHolder() {
        }
    }
}
