package com.microsoft.onlineid.internal.log;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.widget.Toast;
import com.microsoft.onlineid.internal.Assertion;

public class SendLogsHandler {
    protected static final long SendKeyEventIntervalMillis = 5000;
    protected static final String ToastMsg = "Press the 'volume down' button %d more time(s) to send logs.";
    private Context _activityContext;
    private Context _applicationContext;
    private ErrorReportManager _errorReport;
    private byte _sendLogsKeyCounter;
    private long _startTime;
    private Toast _toast;

    public SendLogsHandler(Activity activityContext) {
        this(activityContext, (ErrorReportManager) null);
    }

    protected SendLogsHandler(Context context, ErrorReportManager errorReporter) {
        this._applicationContext = null;
        this._activityContext = null;
        this._startTime = 0;
        this._sendLogsKeyCounter = -1;
        this._activityContext = context;
        this._errorReport = errorReporter;
    }

    protected SendLogsHandler(Activity activityContext, ErrorReportManager errorReporter) {
        boolean z;
        boolean z2 = true;
        this._applicationContext = null;
        this._activityContext = null;
        this._startTime = 0;
        this._sendLogsKeyCounter = -1;
        this._activityContext = activityContext;
        if (activityContext != null) {
            z = true;
        } else {
            z = false;
        }
        Assertion.check(z);
        this._applicationContext = activityContext.getApplicationContext();
        Assertion.check(this._applicationContext == null ? false : z2);
        this._errorReport = errorReporter == null ? new ErrorReportManager(this._applicationContext) : errorReporter;
    }

    public void setSendScreenshot(boolean sendScreenshotNewValue) {
        this._errorReport.setSendScreenshot(sendScreenshotNewValue);
    }

    public void setSendLogs(boolean sendLogsNewValue) {
        this._errorReport.setSendLogs(sendLogsNewValue);
    }

    public void trySendLogsOnKeyEvent(int keyCode) {
        switch (keyCode) {
            case 24:
                this._sendLogsKeyCounter = 2;
                showToast(String.format(ToastMsg, new Object[]{Byte.valueOf(this._sendLogsKeyCounter)}));
                this._startTime = getTimeMillis();
                return;
            case MotionEventCompat.AXIS_TILT:
                long elapsed = getTimeMillis() - this._startTime;
                if (this._sendLogsKeyCounter < 0 || elapsed >= SendKeyEventIntervalMillis) {
                    this._sendLogsKeyCounter = -1;
                    return;
                }
                this._sendLogsKeyCounter = (byte) (this._sendLogsKeyCounter - 1);
                if (this._sendLogsKeyCounter > 0) {
                    showToast(String.format(ToastMsg, new Object[]{Byte.valueOf(this._sendLogsKeyCounter)}));
                    return;
                }
                sendLogs();
                this._sendLogsKeyCounter = -1;
                return;
            default:
                return;
        }
    }

    public void sendLogs() {
        this._errorReport.generateAndSendReportWithUserPermission(this._activityContext);
    }

    public void sendLogs(String userFeedback) {
        this._errorReport.generateAndSendReportWithUserPermission(this._activityContext, userFeedback);
    }

    /* access modifiers changed from: protected */
    public void showToast(String msg) {
        if (this._toast == null) {
            this._toast = Toast.makeText(this._applicationContext, msg, 1);
        } else {
            this._toast.setText(msg);
        }
        this._toast.show();
    }

    /* access modifiers changed from: protected */
    public long getTimeMillis() {
        return System.currentTimeMillis();
    }
}
