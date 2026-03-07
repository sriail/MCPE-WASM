package com.microsoft.xbox.service.network.managers;

import com.microsoft.xbox.service.model.serialization.UTCDateConverterGson;
import com.microsoft.xbox.toolkit.GsonUtil;
import com.microsoft.xbox.toolkit.XLEConstants;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;

public interface IFollowerPresenceResult {

    public static class ActivityRecord {
        public BroadcastRecord broadcast;
        public String richPresence;
    }

    public static class BroadcastRecord {
        public String id;
        public String provider;
        public String session;
        public int viewers;
    }

    public static class LastSeenRecord {
        public String deviceType;
        public String titleName;
    }

    public static class UserPresence {
        private BroadcastRecord broadcastRecord;
        private boolean broadcastRecordSet;
        public ArrayList<DeviceRecord> devices;
        public LastSeenRecord lastSeen;
        public String state;
        public String xuid;

        public BroadcastRecord getBroadcastRecord(long titleId) {
            if (!this.broadcastRecordSet) {
                if ("Online".equalsIgnoreCase(this.state)) {
                    Iterator<DeviceRecord> it = this.devices.iterator();
                    loop0:
                    while (true) {
                        if (!it.hasNext()) {
                            break;
                        }
                        DeviceRecord device = it.next();
                        if (device.isXboxOne()) {
                            Iterator i$ = device.titles.iterator();
                            while (i$.hasNext()) {
                                TitleRecord title = i$.next();
                                if (title.id == titleId && title.isRunningInFullOrFill() && title.activity != null && title.activity.broadcast != null) {
                                    this.broadcastRecord = title.activity.broadcast;
                                    break loop0;
                                }
                            }
                            continue;
                        }
                    }
                }
                this.broadcastRecordSet = true;
            }
            return this.broadcastRecord;
        }

        public int getBroadcastingViewerCount(long titleId) {
            BroadcastRecord r = getBroadcastRecord(titleId);
            if (r == null) {
                return 0;
            }
            return r.viewers;
        }

        public long getXboxOneNowPlayingTitleId() {
            long result = -1;
            if ("Online".equalsIgnoreCase(this.state)) {
                Iterator<DeviceRecord> it = this.devices.iterator();
                while (it.hasNext()) {
                    DeviceRecord device = it.next();
                    if (device.isXboxOne()) {
                        Iterator i$ = device.titles.iterator();
                        while (true) {
                            if (!i$.hasNext()) {
                                break;
                            }
                            TitleRecord title = i$.next();
                            if (title.isRunningInFullOrFill()) {
                                result = title.id;
                                break;
                            }
                        }
                    }
                }
            }
            return result;
        }

        public Date getXboxOneNowPlayingDate() {
            Date result = null;
            if ("Online".equalsIgnoreCase(this.state)) {
                Iterator<DeviceRecord> it = this.devices.iterator();
                while (it.hasNext()) {
                    DeviceRecord device = it.next();
                    if (device.isXboxOne()) {
                        Iterator i$ = device.titles.iterator();
                        while (true) {
                            if (!i$.hasNext()) {
                                break;
                            }
                            TitleRecord title = i$.next();
                            if (title.isRunningInFullOrFill()) {
                                result = title.lastModified;
                                break;
                            }
                        }
                    }
                }
            }
            return result;
        }
    }

    public static class DeviceRecord {
        public ArrayList<TitleRecord> titles;
        public String type;

        public boolean isXboxOne() {
            return "XboxOne".equalsIgnoreCase(this.type);
        }

        public boolean isXbox360() {
            return "Xbox360".equalsIgnoreCase(this.type);
        }
    }

    public static class TitleRecord {
        public ActivityRecord activity;
        public long id;
        public Date lastModified;
        public String name;
        public String placement;

        public boolean isRunningInFullOrFill() {
            return "Full".equalsIgnoreCase(this.placement) || "Fill".equalsIgnoreCase(this.placement);
        }

        public boolean isDash() {
            return this.id == XLEConstants.DASH_TITLE_ID;
        }
    }

    public static class FollowersPresenceResult {
        public ArrayList<UserPresence> userPresence;

        public static FollowersPresenceResult deserialize(InputStream stream) {
            UserPresence[] data = (UserPresence[]) GsonUtil.deserializeJson(stream, UserPresence[].class, (Type) Date.class, (Object) new UTCDateConverterGson.UTCDateConverterJSONDeserializer());
            if (data == null) {
                return null;
            }
            FollowersPresenceResult result = new FollowersPresenceResult();
            result.userPresence = new ArrayList<>(Arrays.asList(data));
            return result;
        }
    }
}
