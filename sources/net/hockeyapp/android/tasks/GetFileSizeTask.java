package net.hockeyapp.android.tasks;

import android.content.Context;
import java.io.IOException;
import java.net.URL;
import net.hockeyapp.android.listeners.DownloadFileListener;

public class GetFileSizeTask extends DownloadFileTask {
    private long mSize;

    public GetFileSizeTask(Context context, String urlString, DownloadFileListener notifier) {
        super(context, urlString, notifier);
    }

    /* access modifiers changed from: protected */
    public Long doInBackground(Void... args) {
        try {
            return Long.valueOf((long) createConnection(new URL(getURLString()), 6).getContentLength());
        } catch (IOException e) {
            e.printStackTrace();
            return 0L;
        }
    }

    /* access modifiers changed from: protected */
    public void onProgressUpdate(Integer... args) {
    }

    /* access modifiers changed from: protected */
    public void onPostExecute(Long result) {
        this.mSize = result.longValue();
        if (this.mSize > 0) {
            this.mNotifier.downloadSuccessful(this);
        } else {
            this.mNotifier.downloadFailed(this, false);
        }
    }

    public long getSize() {
        return this.mSize;
    }
}
