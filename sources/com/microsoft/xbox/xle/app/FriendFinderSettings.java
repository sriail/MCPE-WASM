package com.microsoft.xbox.xle.app;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.microsoft.xbox.service.model.friendfinder.RecommendationTypeIcon;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class FriendFinderSettings {
    private static HashMap<String, RecommendationTypeIcon> icons;
    public String ICONS;

    public enum IconImageSize {
        UNKNOWN,
        SMALL,
        MEDIUM,
        LARGE
    }

    public void getIconsFromJson(String jsonStr) {
        icons = new HashMap<>();
        try {
            ArrayList<RecommendationTypeIcon> iconList = (ArrayList) new Gson().fromJson(jsonStr, new TypeToken<ArrayList<RecommendationTypeIcon>>() {
            }.getType());
            if (iconList != null) {
                Iterator i$ = iconList.iterator();
                while (i$.hasNext()) {
                    RecommendationTypeIcon icon = i$.next();
                    icons.put(icon.type.toLowerCase(), icon);
                }
            }
        } catch (Exception e) {
        }
    }

    public static String getIconBySize(String type, IconImageSize size) {
        RecommendationTypeIcon icon;
        if (!(icons == null || icons.size() <= 0 || (icon = icons.get(type.toLowerCase())) == null)) {
            switch (size) {
                case SMALL:
                    return icon.small;
                case MEDIUM:
                    return icon.medium;
                case LARGE:
                    return icon.large;
            }
        }
        return null;
    }
}
