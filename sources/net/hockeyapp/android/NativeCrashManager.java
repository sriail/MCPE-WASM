package net.hockeyapp.android;

import android.app.Activity;
import android.util.Log;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.util.Date;
import java.util.UUID;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;

public class NativeCrashManager {
    public static void handleDumpFiles(Activity activity, String identifier) {
        for (String dumpFilename : searchForDumpFiles()) {
            Log.d("HockeyApp", "Located this dump file: " + dumpFilename);
            String logFilename = createLogFile();
            if (logFilename != null) {
                uploadDumpAndLog(activity, identifier, dumpFilename, logFilename);
            }
        }
    }

    public static String createLogFile() {
        Date now = new Date();
        try {
            String filename = UUID.randomUUID().toString();
            String path = Constants.FILES_PATH + "/" + filename + ".faketrace";
            Log.d("HockeyApp", "Writing unhandled exception to: " + path);
            BufferedWriter write = new BufferedWriter(new FileWriter(path));
            write.write("Package: " + Constants.APP_PACKAGE + "\n");
            write.write("Version Code: " + Constants.APP_VERSION + "\n");
            write.write("Version Name: " + Constants.APP_VERSION_NAME + "\n");
            write.write("Android: " + Constants.ANDROID_VERSION + "\n");
            write.write("Manufacturer: " + Constants.PHONE_MANUFACTURER + "\n");
            write.write("Model: " + Constants.PHONE_MODEL + "\n");
            write.write("Date: " + now + "\n");
            write.write("\n");
            write.write("MinidumpContainer");
            write.flush();
            write.close();
            return filename + ".faketrace";
        } catch (Exception e) {
            return null;
        }
    }

    public static void uploadDumpAndLog(final Activity activity, final String identifier, final String dumpFilename, final String logFilename) {
        new Thread() {
            public void run() {
                try {
                    DefaultHttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost("https://rink.hockeyapp.net/api/2/apps/" + identifier + "/crashes/upload");
                    MultipartEntity entity = new MultipartEntity();
                    entity.addPart("attachment0", new FileBody(new File(Constants.FILES_PATH, dumpFilename)));
                    entity.addPart("log", new FileBody(new File(Constants.FILES_PATH, logFilename)));
                    httpPost.setEntity(entity);
                    httpClient.execute(httpPost);
                    Log.d("HockeyApp", "Succesfully Uploaded dump file: " + dumpFilename);
                } catch (Exception e) {
                    Log.d("HockeyApp", "Error uploading dump file: " + dumpFilename);
                    e.printStackTrace();
                } finally {
                    activity.deleteFile(logFilename);
                    activity.deleteFile(dumpFilename);
                }
            }
        }.start();
    }

    private static String[] searchForDumpFiles() {
        if (Constants.FILES_PATH != null) {
            Log.d("HockeyApp", "Searching for dump files in " + Constants.FILES_PATH);
            File dir = new File(Constants.FILES_PATH + "/");
            if (dir.mkdir() || dir.exists()) {
                return dir.list(new FilenameFilter() {
                    public boolean accept(File dir, String name) {
                        return name.endsWith(".dmp");
                    }
                });
            }
            return new String[0];
        }
        Log.d("HockeyApp", "Can't search for exception as file path is null.");
        return new String[0];
    }
}
