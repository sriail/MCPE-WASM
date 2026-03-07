package com.microsoft.xboxtcui;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import com.microsoft.xbox.service.model.XPrivilegeConstants;
import com.microsoft.xbox.toolkit.ui.ActivityParameters;
import com.microsoft.xbox.xle.app.activity.FriendFinder.FriendFinderHomeScreen;
import com.microsoft.xbox.xle.app.activity.Profile.ProfileScreen;
import com.microsoft.xboxtcui.XboxTcuiWindowDialog;
import java.lang.reflect.Field;
import java.util.Map;

public class Interop {
    /* access modifiers changed from: private */
    public static final String TAG = Interop.class.getSimpleName();
    /* access modifiers changed from: private */
    public static final XboxTcuiWindowDialog.DetachedCallback detachedCallback = new XboxTcuiWindowDialog.DetachedCallback() {
        public void onDetachedFromWindow() {
            Interop.tcui_completed_callback(0);
        }
    };

    /* access modifiers changed from: private */
    public static native void tcui_completed_callback(int i);

    public static void ShowProfileCardUI(Activity activity, String meXuid, String targetProfileXuid, String privileges) {
        final Activity activityToUse;
        Log.i(TAG, "TCUI- ShowProfileCardUI: meXuid:" + meXuid);
        Log.i(TAG, "TCUI- ShowProfileCardUI: targeProfileXuid:" + targetProfileXuid);
        Log.i(TAG, "TCUI- ShowProfileCardUI: privileges:" + privileges);
        Activity foregroundActivity = getForegroundActivity();
        if (foregroundActivity == null) {
            activityToUse = activity;
        } else {
            activityToUse = foregroundActivity;
        }
        final ActivityParameters params = new ActivityParameters();
        params.putMeXuid(meXuid);
        params.putSelectedProfile(targetProfileXuid);
        params.putPrivileges(privileges);
        activity.runOnUiThread(new Runnable() {
            public void run() {
                try {
                    XboxTcuiWindowDialog df = new XboxTcuiWindowDialog(activityToUse, ProfileScreen.class, params);
                    df.setDetachedCallback(Interop.detachedCallback);
                    df.show();
                } catch (Exception ex) {
                    Log.i(Interop.TAG, Log.getStackTraceString(ex));
                    Interop.tcui_completed_callback(1);
                }
            }
        });
    }

    public static void ShowFriendFinder(Activity activity, String meXuid, String privileges) {
        final Activity activityToUse;
        Log.i(TAG, "TCUI- ShowFriendFinder - meXuid:" + meXuid);
        Log.i(TAG, "TCUI- ShowFriendFinder: privileges:" + privileges);
        if (privileges.contains(XPrivilegeConstants.XPRIVILEGE_ADD_FRIEND)) {
            Activity foregroundActivity = getForegroundActivity();
            if (foregroundActivity == null) {
                activityToUse = activity;
            } else {
                activityToUse = foregroundActivity;
            }
            final ActivityParameters params = new ActivityParameters();
            params.putMeXuid(meXuid);
            params.putPrivileges(privileges);
            activityToUse.runOnUiThread(new Runnable() {
                public void run() {
                    try {
                        XboxTcuiWindowDialog df = new XboxTcuiWindowDialog(activityToUse, FriendFinderHomeScreen.class, params);
                        df.setDetachedCallback(Interop.detachedCallback);
                        df.show();
                    } catch (Exception ex) {
                        Log.i(Interop.TAG, Log.getStackTraceString(ex));
                        Interop.tcui_completed_callback(1);
                    }
                }
            });
            return;
        }
        tcui_completed_callback(1);
    }

    public static void ShowUserProfile(Context context, String targetXboxUserId) {
        Log.i(TAG, "Deeplink - ShowUserProfile");
        if (XboxAppDeepLinker.showUserProfile(context, targetXboxUserId)) {
            tcui_completed_callback(0);
        } else {
            tcui_completed_callback(1);
        }
    }

    public static void ShowTitleHub(Context context, String targetTitleId) {
        Log.i(TAG, "Deeplink - ShowTitleHub");
        if (XboxAppDeepLinker.showTitleHub(context, targetTitleId)) {
            tcui_completed_callback(0);
        } else {
            tcui_completed_callback(1);
        }
    }

    public static void ShowTitleAchievements(Context context, String targetTitleId) {
        Log.i(TAG, "Deeplink - ShowTitleAchievements");
        if (XboxAppDeepLinker.showTitleAchievements(context, targetTitleId)) {
            tcui_completed_callback(0);
        } else {
            tcui_completed_callback(1);
        }
    }

    public static void ShowUserSettings(Context context) {
        Log.i(TAG, "Deeplink - ShowUserSettings");
        if (XboxAppDeepLinker.showUserSettings(context)) {
            tcui_completed_callback(0);
        } else {
            tcui_completed_callback(1);
        }
    }

    public static void ShowAddFriends(Context context) {
        Log.i(TAG, "Deeplink - ShowAddFriends");
        if (XboxAppDeepLinker.showAddFriends(context)) {
            tcui_completed_callback(0);
        } else {
            tcui_completed_callback(1);
        }
    }

    private static Activity getForegroundActivity() {
        try {
            Class activityThreadClass = Class.forName("android.app.ActivityThread");
            Object activityThread = activityThreadClass.getMethod("currentActivityThread", new Class[0]).invoke((Object) null, new Object[0]);
            Field activitiesField = activityThreadClass.getDeclaredField("mActivities");
            activitiesField.setAccessible(true);
            for (Object activityRecord : ((Map) activitiesField.get(activityThread)).values()) {
                Class activityRecordClass = activityRecord.getClass();
                Field pausedField = activityRecordClass.getDeclaredField("paused");
                pausedField.setAccessible(true);
                if (!pausedField.getBoolean(activityRecord)) {
                    Field activityField = activityRecordClass.getDeclaredField("activity");
                    activityField.setAccessible(true);
                    return (Activity) activityField.get(activityRecord);
                }
            }
        } catch (Exception ex) {
            Log.i(TAG, Log.getStackTraceString(ex));
        }
        return null;
    }
}
