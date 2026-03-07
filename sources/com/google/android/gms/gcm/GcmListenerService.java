package com.google.android.gms.gcm;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import java.util.Iterator;

public abstract class GcmListenerService extends Service {
    private int zzaLy;
    private int zzaLz = 0;
    private final Object zzpV = new Object();

    private void zzm(Intent intent) {
        PendingIntent pendingIntent = (PendingIntent) intent.getParcelableExtra("com.google.android.gms.gcm.PENDING_INTENT");
        if (pendingIntent != null) {
            try {
                pendingIntent.send();
            } catch (PendingIntent.CanceledException e) {
                Log.e("GcmListenerService", "Notification pending intent canceled");
            }
        }
        if (zzx(intent.getExtras())) {
            zza.zzf(this, intent);
        }
    }

    @TargetApi(11)
    private void zzn(final Intent intent) {
        if (Build.VERSION.SDK_INT >= 11) {
            AsyncTask.THREAD_POOL_EXECUTOR.execute(new Runnable() {
                public void run() {
                    GcmListenerService.this.zzo(intent);
                }
            });
        } else {
            new AsyncTask<Void, Void, Void>() {
                /* access modifiers changed from: protected */
                /* renamed from: zzb */
                public Void doInBackground(Void... voidArr) {
                    GcmListenerService.this.zzo(intent);
                    return null;
                }
            }.execute(new Void[0]);
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void zzo(android.content.Intent r4) {
        /*
            r3 = this;
            java.lang.String r1 = r4.getAction()     // Catch:{ all -> 0x004a }
            r0 = -1
            int r2 = r1.hashCode()     // Catch:{ all -> 0x004a }
            switch(r2) {
                case 214175003: goto L_0x003c;
                case 366519424: goto L_0x0032;
                default: goto L_0x000c;
            }     // Catch:{ all -> 0x004a }
        L_0x000c:
            switch(r0) {
                case 0: goto L_0x0046;
                case 1: goto L_0x004f;
                default: goto L_0x000f;
            }     // Catch:{ all -> 0x004a }
        L_0x000f:
            java.lang.String r0 = "GcmListenerService"
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x004a }
            r1.<init>()     // Catch:{ all -> 0x004a }
            java.lang.String r2 = "Unknown intent action: "
            java.lang.StringBuilder r1 = r1.append(r2)     // Catch:{ all -> 0x004a }
            java.lang.String r2 = r4.getAction()     // Catch:{ all -> 0x004a }
            java.lang.StringBuilder r1 = r1.append(r2)     // Catch:{ all -> 0x004a }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x004a }
            android.util.Log.d(r0, r1)     // Catch:{ all -> 0x004a }
        L_0x002b:
            r3.zzyh()     // Catch:{ all -> 0x004a }
            com.google.android.gms.gcm.GcmReceiver.completeWakefulIntent(r4)
            return
        L_0x0032:
            java.lang.String r2 = "com.google.android.c2dm.intent.RECEIVE"
            boolean r1 = r1.equals(r2)     // Catch:{ all -> 0x004a }
            if (r1 == 0) goto L_0x000c
            r0 = 0
            goto L_0x000c
        L_0x003c:
            java.lang.String r2 = "com.google.android.gms.gcm.NOTIFICATION_DISMISS"
            boolean r1 = r1.equals(r2)     // Catch:{ all -> 0x004a }
            if (r1 == 0) goto L_0x000c
            r0 = 1
            goto L_0x000c
        L_0x0046:
            r3.zzp(r4)     // Catch:{ all -> 0x004a }
            goto L_0x002b
        L_0x004a:
            r0 = move-exception
            com.google.android.gms.gcm.GcmReceiver.completeWakefulIntent(r4)
            throw r0
        L_0x004f:
            android.os.Bundle r0 = r4.getExtras()     // Catch:{ all -> 0x004a }
            boolean r0 = zzx(r0)     // Catch:{ all -> 0x004a }
            if (r0 == 0) goto L_0x002b
            com.google.android.gms.gcm.zza.zzg(r3, r4)     // Catch:{ all -> 0x004a }
            goto L_0x002b
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.gcm.GcmListenerService.zzo(android.content.Intent):void");
    }

    private void zzp(Intent intent) {
        String stringExtra = intent.getStringExtra("message_type");
        if (stringExtra == null) {
            stringExtra = GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE;
        }
        char c = 65535;
        switch (stringExtra.hashCode()) {
            case -2062414158:
                if (stringExtra.equals(GoogleCloudMessaging.MESSAGE_TYPE_DELETED)) {
                    c = 1;
                    break;
                }
                break;
            case 102161:
                if (stringExtra.equals(GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE)) {
                    c = 0;
                    break;
                }
                break;
            case 814694033:
                if (stringExtra.equals(GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR)) {
                    c = 3;
                    break;
                }
                break;
            case 814800675:
                if (stringExtra.equals(GoogleCloudMessaging.MESSAGE_TYPE_SEND_EVENT)) {
                    c = 2;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                if (zzx(intent.getExtras())) {
                    zza.zze(this, intent);
                }
                zzq(intent);
                return;
            case 1:
                onDeletedMessages();
                return;
            case 2:
                onMessageSent(intent.getStringExtra("google.message_id"));
                return;
            case 3:
                onSendError(intent.getStringExtra("google.message_id"), intent.getStringExtra("error"));
                return;
            default:
                Log.w("GcmListenerService", "Received message with unknown type: " + stringExtra);
                return;
        }
    }

    private void zzq(Intent intent) {
        Bundle extras = intent.getExtras();
        extras.remove("message_type");
        extras.remove("android.support.content.wakelockid");
        if (zzb.zzy(extras)) {
            if (!zzb.zzaI(this)) {
                zzb.zzc(this, getClass()).zzA(extras);
                return;
            }
            if (zzx(intent.getExtras())) {
                zza.zzh(this, intent);
            }
            zzb.zzz(extras);
        }
        String string = extras.getString("from");
        extras.remove("from");
        zzw(extras);
        onMessageReceived(string, extras);
    }

    static void zzw(Bundle bundle) {
        Iterator it = bundle.keySet().iterator();
        while (it.hasNext()) {
            String str = (String) it.next();
            if (str != null && str.startsWith("google.c.")) {
                it.remove();
            }
        }
    }

    static boolean zzx(Bundle bundle) {
        return "1".equals(bundle.getString("google.c.a.e"));
    }

    private void zzyh() {
        synchronized (this.zzpV) {
            this.zzaLz--;
            if (this.zzaLz == 0) {
                zzhd(this.zzaLy);
            }
        }
    }

    public final IBinder onBind(Intent intent) {
        return null;
    }

    public void onDeletedMessages() {
    }

    public void onMessageReceived(String from, Bundle data) {
    }

    public void onMessageSent(String msgId) {
    }

    public void onSendError(String msgId, String error) {
    }

    public final int onStartCommand(Intent intent, int flags, int startId) {
        synchronized (this.zzpV) {
            this.zzaLy = startId;
            this.zzaLz++;
        }
        if (intent == null) {
            zzyh();
            return 2;
        }
        if ("com.google.android.gms.gcm.NOTIFICATION_OPEN".equals(intent.getAction())) {
            zzm(intent);
            zzyh();
            GcmReceiver.completeWakefulIntent(intent);
        } else {
            zzn(intent);
        }
        return 3;
    }

    /* access modifiers changed from: package-private */
    public boolean zzhd(int i) {
        return stopSelfResult(i);
    }
}
