package net.hockeyapp.android;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Toast;
import com.microsoft.onlineid.ui.AddAccountActivity;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import net.hockeyapp.android.objects.FeedbackUserDataElement;
import net.hockeyapp.android.tasks.ParseFeedbackTask;
import net.hockeyapp.android.tasks.SendFeedbackTask;
import net.hockeyapp.android.utils.AsyncTaskUtils;
import net.hockeyapp.android.utils.HockeyLog;
import net.hockeyapp.android.utils.PrefsUtil;
import net.hockeyapp.android.utils.Util;

public class FeedbackManager {
    private static final String BROADCAST_ACTION = "net.hockeyapp.android.SCREENSHOT";
    private static final int BROADCAST_REQUEST_CODE = 1;
    private static final int SCREENSHOT_NOTIFICATION_ID = 1;
    private static Activity currentActivity;
    private static String identifier = null;
    private static FeedbackManagerListener lastListener = null;
    private static boolean notificationActive = false;
    private static BroadcastReceiver receiver = null;
    private static FeedbackUserDataElement requireUserEmail;
    private static FeedbackUserDataElement requireUserName;
    private static String urlString = null;
    private static String userEmail;
    private static String userName;

    public static void register(Context context) {
        String appIdentifier = Util.getAppIdentifier(context);
        if (appIdentifier == null || appIdentifier.length() == 0) {
            throw new IllegalArgumentException("HockeyApp app identifier was not configured correctly in manifest or build configuration.");
        }
        register(context, appIdentifier);
    }

    public static void register(Context context, String appIdentifier) {
        register(context, appIdentifier, (FeedbackManagerListener) null);
    }

    public static void register(Context context, String appIdentifier, FeedbackManagerListener listener) {
        register(context, Constants.BASE_URL, appIdentifier, listener);
    }

    public static void register(Context context, String urlString2, String appIdentifier, FeedbackManagerListener listener) {
        if (context != null) {
            identifier = Util.sanitizeAppIdentifier(appIdentifier);
            urlString = urlString2;
            lastListener = listener;
            Constants.loadFromContext(context);
        }
    }

    public static void unregister() {
        lastListener = null;
    }

    public static void showFeedbackActivity(Context context, Uri... attachments) {
        showFeedbackActivity(context, (Bundle) null, attachments);
    }

    public static void showFeedbackActivity(Context context, Bundle extras, Uri... attachments) {
        if (context != null) {
            Class cls = null;
            if (lastListener != null) {
                cls = lastListener.getFeedbackActivityClass();
            }
            if (cls == null) {
                cls = FeedbackActivity.class;
            }
            Intent intent = new Intent();
            if (extras != null && !extras.isEmpty()) {
                intent.putExtras(extras);
            }
            intent.setFlags(268435456);
            intent.setClass(context, cls);
            intent.putExtra("url", getURLString(context));
            intent.putExtra(FeedbackActivity.EXTRA_INITIAL_USER_NAME, userName);
            intent.putExtra(FeedbackActivity.EXTRA_INITIAL_USER_EMAIL, userEmail);
            intent.putExtra(FeedbackActivity.EXTRA_INITIAL_ATTACHMENTS, attachments);
            context.startActivity(intent);
        }
    }

    public static void checkForAnswersAndNotify(final Context context) {
        String token = PrefsUtil.getInstance().getFeedbackTokenFromPrefs(context);
        if (token != null) {
            int lastMessageId = context.getSharedPreferences(ParseFeedbackTask.PREFERENCES_NAME, 0).getInt(ParseFeedbackTask.ID_LAST_MESSAGE_SEND, -1);
            SendFeedbackTask sendFeedbackTask = new SendFeedbackTask(context, getURLString(context), (String) null, (String) null, (String) null, (String) null, (List<Uri>) null, token, new Handler() {
                public void handleMessage(Message msg) {
                    String responseString = msg.getData().getString(SendFeedbackTask.BUNDLE_FEEDBACK_RESPONSE);
                    if (responseString != null) {
                        ParseFeedbackTask task = new ParseFeedbackTask(context, responseString, (Handler) null, "fetch");
                        task.setUrlString(FeedbackManager.getURLString(context));
                        AsyncTaskUtils.execute(task);
                    }
                }
            }, true);
            sendFeedbackTask.setShowProgressDialog(false);
            sendFeedbackTask.setLastMessageId(lastMessageId);
            AsyncTaskUtils.execute(sendFeedbackTask);
        }
    }

    public static FeedbackManagerListener getLastListener() {
        return lastListener;
    }

    /* access modifiers changed from: private */
    public static String getURLString(Context context) {
        return urlString + "api/2/apps/" + identifier + "/feedback/";
    }

    public static FeedbackUserDataElement getRequireUserName() {
        return requireUserName;
    }

    public static void setRequireUserName(FeedbackUserDataElement requireUserName2) {
        requireUserName = requireUserName2;
    }

    public static FeedbackUserDataElement getRequireUserEmail() {
        return requireUserEmail;
    }

    public static void setRequireUserEmail(FeedbackUserDataElement requireUserEmail2) {
        requireUserEmail = requireUserEmail2;
    }

    public static void setUserName(String userName2) {
        userName = userName2;
    }

    public static void setUserEmail(String userEmail2) {
        userEmail = userEmail2;
    }

    public static void setActivityForScreenshot(Activity activity) {
        currentActivity = activity;
        if (!notificationActive) {
            startNotification();
        }
    }

    public static void unsetCurrentActivityForScreenshot(Activity activity) {
        if (currentActivity != null && currentActivity == activity) {
            endNotification();
            currentActivity = null;
        }
    }

    public static void takeScreenshot(final Context context) {
        View view = currentActivity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        final Bitmap bitmap = view.getDrawingCache();
        String filename = currentActivity.getLocalClassName();
        File dir = Constants.getHockeyAppStorageDir();
        File result = new File(dir, filename + ".jpg");
        int suffix = 1;
        while (result.exists()) {
            result = new File(dir, filename + "_" + suffix + ".jpg");
            suffix++;
        }
        new AsyncTask<File, Void, Boolean>() {
            /* access modifiers changed from: protected */
            public Boolean doInBackground(File... args) {
                try {
                    FileOutputStream out = new FileOutputStream(args[0]);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    out.close();
                    return true;
                } catch (IOException e) {
                    HockeyLog.error("Could not save screenshot.", (Throwable) e);
                    return false;
                }
            }

            /* access modifiers changed from: protected */
            public void onPostExecute(Boolean success) {
                if (!success.booleanValue()) {
                    Toast.makeText(context, "Screenshot could not be created. Sorry.", 1).show();
                }
            }
        }.execute(new File[]{result});
        MediaScannerClient client = new MediaScannerClient(result.getAbsolutePath());
        MediaScannerConnection connection = new MediaScannerConnection(currentActivity, client);
        client.setConnection(connection);
        connection.connect();
        Toast.makeText(context, "Screenshot '" + result.getName() + "' is available in gallery.", 1).show();
    }

    private static void startNotification() {
        notificationActive = true;
        int iconId = currentActivity.getResources().getIdentifier("ic_menu_camera", "drawable", AddAccountActivity.PlatformName);
        Intent intent = new Intent();
        intent.setAction(BROADCAST_ACTION);
        ((NotificationManager) currentActivity.getSystemService("notification")).notify(1, Util.createNotification(currentActivity, PendingIntent.getBroadcast(currentActivity, 1, intent, 1073741824), "HockeyApp Feedback", "Take a screenshot for your feedback.", iconId));
        if (receiver == null) {
            receiver = new BroadcastReceiver() {
                public void onReceive(Context context, Intent intent) {
                    FeedbackManager.takeScreenshot(context);
                }
            };
        }
        currentActivity.registerReceiver(receiver, new IntentFilter(BROADCAST_ACTION));
    }

    private static void endNotification() {
        notificationActive = false;
        currentActivity.unregisterReceiver(receiver);
        ((NotificationManager) currentActivity.getSystemService("notification")).cancel(1);
    }

    private static class MediaScannerClient implements MediaScannerConnection.MediaScannerConnectionClient {
        private MediaScannerConnection connection;
        private String path;

        private MediaScannerClient(String path2) {
            this.connection = null;
            this.path = path2;
        }

        public void setConnection(MediaScannerConnection connection2) {
            this.connection = connection2;
        }

        public void onMediaScannerConnected() {
            if (this.connection != null) {
                this.connection.scanFile(this.path, (String) null);
            }
        }

        public void onScanCompleted(String path2, Uri uri) {
            HockeyLog.verbose(String.format("Scanned path %s -> URI = %s", new Object[]{path2, uri.toString()}));
            this.connection.disconnect();
        }
    }
}
