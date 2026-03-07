package com.facebook.appevents;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import bolts.AppLinks;
import com.facebook.AccessToken;
import com.facebook.FacebookException;
import com.facebook.FacebookRequestError;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.LoggingBehavior;
import com.facebook.appevents.internal.ActivityLifecycleTracker;
import com.facebook.appevents.internal.Constants;
import com.facebook.internal.AppEventsLoggerUtility;
import com.facebook.internal.AttributionIdentifiers;
import com.facebook.internal.Logger;
import com.facebook.internal.Utility;
import com.facebook.internal.Validate;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import net.hockeyapp.android.utils.HttpURLConnectionBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AppEventsLogger {
    public static final String ACTION_APP_EVENTS_FLUSHED = "com.facebook.sdk.APP_EVENTS_FLUSHED";
    public static final String APP_EVENTS_EXTRA_FLUSH_RESULT = "com.facebook.sdk.APP_EVENTS_FLUSH_RESULT";
    public static final String APP_EVENTS_EXTRA_NUM_EVENTS_FLUSHED = "com.facebook.sdk.APP_EVENTS_NUM_EVENTS_FLUSHED";
    private static final String APP_EVENT_NAME_PUSH_OPENED = "fb_mobile_push_opened";
    public static final String APP_EVENT_PREFERENCES = "com.facebook.sdk.appEventPreferences";
    private static final String APP_EVENT_PUSH_PARAMETER_ACTION = "fb_push_action";
    private static final String APP_EVENT_PUSH_PARAMETER_CAMPAIGN = "fb_push_campaign";
    private static final int APP_SUPPORTS_ATTRIBUTION_ID_RECHECK_PERIOD_IN_SECONDS = 86400;
    private static final int FLUSH_APP_SESSION_INFO_IN_SECONDS = 30;
    private static final int FLUSH_PERIOD_IN_SECONDS = 15;
    private static final int NUM_LOG_EVENTS_TO_TRY_TO_FLUSH_AFTER = 100;
    private static final String PUSH_PAYLOAD_CAMPAIGN_KEY = "campaign";
    private static final String PUSH_PAYLOAD_KEY = "fb_push_payload";
    private static final String SOURCE_APPLICATION_HAS_BEEN_SET_BY_THIS_INTENT = "_fbSourceApplicationHasBeenSet";
    /* access modifiers changed from: private */
    public static final String TAG = AppEventsLogger.class.getCanonicalName();
    private static String anonymousAppDeviceGUID;
    /* access modifiers changed from: private */
    public static Context applicationContext;
    /* access modifiers changed from: private */
    public static ScheduledThreadPoolExecutor backgroundExecutor;
    private static FlushBehavior flushBehavior = FlushBehavior.AUTO;
    private static boolean isActivateAppEventRequested;
    private static boolean isOpenedByApplink;
    private static String pushNotificationsRegistrationId;
    private static boolean requestInFlight;
    private static String sourceApplication;
    /* access modifiers changed from: private */
    public static Map<AccessTokenAppIdPair, SessionEventsState> stateMap = new ConcurrentHashMap();
    /* access modifiers changed from: private */
    public static Object staticLock = new Object();
    private final AccessTokenAppIdPair accessTokenAppId;
    private final String contextName;

    public enum FlushBehavior {
        AUTO,
        EXPLICIT_ONLY
    }

    private enum FlushReason {
        EXPLICIT,
        TIMER,
        SESSION_CHANGE,
        PERSISTED_EVENTS,
        EVENT_THRESHOLD,
        EAGER_FLUSHING_EVENT
    }

    private enum FlushResult {
        SUCCESS,
        SERVER_ERROR,
        NO_CONNECTIVITY,
        UNKNOWN_ERROR
    }

    private static class AccessTokenAppIdPair implements Serializable {
        private static final long serialVersionUID = 1;
        private final String accessTokenString;
        private final String applicationId;

        AccessTokenAppIdPair(AccessToken accessToken) {
            this(accessToken.getToken(), FacebookSdk.getApplicationId());
        }

        AccessTokenAppIdPair(String accessTokenString2, String applicationId2) {
            this.accessTokenString = Utility.isNullOrEmpty(accessTokenString2) ? null : accessTokenString2;
            this.applicationId = applicationId2;
        }

        /* access modifiers changed from: package-private */
        public String getAccessTokenString() {
            return this.accessTokenString;
        }

        /* access modifiers changed from: package-private */
        public String getApplicationId() {
            return this.applicationId;
        }

        public int hashCode() {
            int i = 0;
            int hashCode = this.accessTokenString == null ? 0 : this.accessTokenString.hashCode();
            if (this.applicationId != null) {
                i = this.applicationId.hashCode();
            }
            return hashCode ^ i;
        }

        public boolean equals(Object o) {
            if (!(o instanceof AccessTokenAppIdPair)) {
                return false;
            }
            AccessTokenAppIdPair p = (AccessTokenAppIdPair) o;
            if (!Utility.areObjectsEqual(p.accessTokenString, this.accessTokenString) || !Utility.areObjectsEqual(p.applicationId, this.applicationId)) {
                return false;
            }
            return true;
        }

        private static class SerializationProxyV1 implements Serializable {
            private static final long serialVersionUID = -2488473066578201069L;
            private final String accessTokenString;
            private final String appId;

            private SerializationProxyV1(String accessTokenString2, String appId2) {
                this.accessTokenString = accessTokenString2;
                this.appId = appId2;
            }

            private Object readResolve() {
                return new AccessTokenAppIdPair(this.accessTokenString, this.appId);
            }
        }

        private Object writeReplace() {
            return new SerializationProxyV1(this.accessTokenString, this.applicationId);
        }
    }

    public static void activateApp(Application application) {
        activateApp(application, (String) null);
    }

    public static void activateApp(Application application, String applicationId) {
        if (!FacebookSdk.isInitialized()) {
            throw new FacebookException("The Facebook sdk must be initialized before calling activateApp");
        }
        if (applicationId == null) {
            applicationId = FacebookSdk.getApplicationId();
        }
        ActivityLifecycleTracker.startTracking(application, applicationId);
    }

    @Deprecated
    public static void activateApp(Context context) {
        if (ActivityLifecycleTracker.isTracking()) {
            Log.w(TAG, "activateApp events are being logged automatically. There's no need to call activateApp explicitly, this is safe to remove.");
            return;
        }
        FacebookSdk.sdkInitialize(context);
        activateApp(context, Utility.getMetadataApplicationId(context));
    }

    @Deprecated
    public static void activateApp(Context context, String applicationId) {
        if (ActivityLifecycleTracker.isTracking()) {
            Log.w(TAG, "activateApp events are being logged automatically. There's no need to call activateApp explicitly, this is safe to remove.");
        } else if (context == null || applicationId == null) {
            throw new IllegalArgumentException("Both context and applicationId must be non-null");
        } else {
            if (context instanceof Activity) {
                setSourceApplication((Activity) context);
            } else {
                resetSourceApplication();
                Log.d(AppEventsLogger.class.getName(), "To set source application the context of activateApp must be an instance of Activity");
            }
            FacebookSdk.publishInstallAsync(context, applicationId);
            AppEventsLogger logger = new AppEventsLogger(context, applicationId, (AccessToken) null);
            final long eventTime = System.currentTimeMillis();
            final String sourceApplicationInfo = getSourceApplication();
            backgroundExecutor.execute(new Runnable(logger) {
                final /* synthetic */ AppEventsLogger val$logger;

                {
                    this.val$logger = r1;
                }

                public void run() {
                    this.val$logger.logAppSessionResumeEvent(eventTime, sourceApplicationInfo);
                }
            });
        }
    }

    @Deprecated
    public static void deactivateApp(Context context) {
        if (ActivityLifecycleTracker.isTracking()) {
            Log.w(TAG, "deactivateApp events are being logged automatically. There's no need to call deactivateApp, this is safe to remove.");
        } else {
            deactivateApp(context, Utility.getMetadataApplicationId(context));
        }
    }

    @Deprecated
    public static void deactivateApp(Context context, String applicationId) {
        if (ActivityLifecycleTracker.isTracking()) {
            Log.w(TAG, "deactivateApp events are being logged automatically. There's no need to call deactivateApp, this is safe to remove.");
        } else if (context == null || applicationId == null) {
            throw new IllegalArgumentException("Both context and applicationId must be non-null");
        } else {
            resetSourceApplication();
            AppEventsLogger logger = new AppEventsLogger(context, applicationId, (AccessToken) null);
            final long eventTime = System.currentTimeMillis();
            backgroundExecutor.execute(new Runnable(logger) {
                final /* synthetic */ AppEventsLogger val$logger;

                {
                    this.val$logger = r1;
                }

                public void run() {
                    this.val$logger.logAppSessionSuspendEvent(eventTime);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    public void logAppSessionResumeEvent(long eventTime, String sourceApplicationInfo) {
        PersistedAppSessionInfo.onResume(applicationContext, this.accessTokenAppId, this, eventTime, sourceApplicationInfo);
    }

    /* access modifiers changed from: private */
    public void logAppSessionSuspendEvent(long eventTime) {
        PersistedAppSessionInfo.onSuspend(applicationContext, this.accessTokenAppId, this, eventTime);
    }

    public static AppEventsLogger newLogger(Context context) {
        return new AppEventsLogger(context, (String) null, (AccessToken) null);
    }

    public static AppEventsLogger newLogger(Context context, AccessToken accessToken) {
        return new AppEventsLogger(context, (String) null, accessToken);
    }

    public static AppEventsLogger newLogger(Context context, String applicationId, AccessToken accessToken) {
        return new AppEventsLogger(context, applicationId, accessToken);
    }

    public static AppEventsLogger newLogger(Context context, String applicationId) {
        return new AppEventsLogger(context, applicationId, (AccessToken) null);
    }

    public static FlushBehavior getFlushBehavior() {
        FlushBehavior flushBehavior2;
        synchronized (staticLock) {
            flushBehavior2 = flushBehavior;
        }
        return flushBehavior2;
    }

    public static void setFlushBehavior(FlushBehavior flushBehavior2) {
        synchronized (staticLock) {
            flushBehavior = flushBehavior2;
        }
    }

    public void logEvent(String eventName) {
        logEvent(eventName, (Bundle) null);
    }

    public void logEvent(String eventName, double valueToSum) {
        logEvent(eventName, valueToSum, (Bundle) null);
    }

    public void logEvent(String eventName, Bundle parameters) {
        logEvent(eventName, (Double) null, parameters, false, ActivityLifecycleTracker.getCurrentSessionGuid());
    }

    public void logEvent(String eventName, double valueToSum, Bundle parameters) {
        logEvent(eventName, Double.valueOf(valueToSum), parameters, false, ActivityLifecycleTracker.getCurrentSessionGuid());
    }

    public void logPurchase(BigDecimal purchaseAmount, Currency currency) {
        logPurchase(purchaseAmount, currency, (Bundle) null);
    }

    public void logPurchase(BigDecimal purchaseAmount, Currency currency, Bundle parameters) {
        if (purchaseAmount == null) {
            notifyDeveloperError("purchaseAmount cannot be null");
        } else if (currency == null) {
            notifyDeveloperError("currency cannot be null");
        } else {
            if (parameters == null) {
                parameters = new Bundle();
            }
            parameters.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, currency.getCurrencyCode());
            logEvent(AppEventsConstants.EVENT_NAME_PURCHASED, purchaseAmount.doubleValue(), parameters);
            eagerFlush();
        }
    }

    public void logPushNotificationOpen(Bundle payload) {
        logPushNotificationOpen(payload, (String) null);
    }

    public void logPushNotificationOpen(Bundle payload, String action) {
        String campaignId = null;
        try {
            String payloadString = payload.getString(PUSH_PAYLOAD_KEY);
            if (!Utility.isNullOrEmpty(payloadString)) {
                campaignId = new JSONObject(payloadString).getString(PUSH_PAYLOAD_CAMPAIGN_KEY);
                if (campaignId == null) {
                    Logger.log(LoggingBehavior.DEVELOPER_ERRORS, TAG, "Malformed payload specified for logging a push notification open.");
                    return;
                }
                Bundle parameters = new Bundle();
                parameters.putString(APP_EVENT_PUSH_PARAMETER_CAMPAIGN, campaignId);
                if (action != null) {
                    parameters.putString(APP_EVENT_PUSH_PARAMETER_ACTION, action);
                }
                logEvent(APP_EVENT_NAME_PUSH_OPENED, parameters);
            }
        } catch (JSONException e) {
        }
    }

    public void flush() {
        flush(FlushReason.EXPLICIT);
    }

    public static void onContextStop() {
        PersistedEvents.persistEvents(applicationContext, stateMap);
    }

    public boolean isValidForAccessToken(AccessToken accessToken) {
        return this.accessTokenAppId.equals(new AccessTokenAppIdPair(accessToken));
    }

    public static void setPushNotificationsRegistrationId(String registrationId) {
        synchronized (staticLock) {
            pushNotificationsRegistrationId = registrationId;
        }
    }

    public void logSdkEvent(String eventName, Double valueToSum, Bundle parameters) {
        logEvent(eventName, valueToSum, parameters, true, ActivityLifecycleTracker.getCurrentSessionGuid());
    }

    public String getApplicationId() {
        return this.accessTokenAppId.getApplicationId();
    }

    private AppEventsLogger(Context context, String applicationId, AccessToken accessToken) {
        Validate.notNull(context, "context");
        this.contextName = Utility.getActivityName(context);
        accessToken = accessToken == null ? AccessToken.getCurrentAccessToken() : accessToken;
        if (accessToken == null || (applicationId != null && !applicationId.equals(accessToken.getApplicationId()))) {
            this.accessTokenAppId = new AccessTokenAppIdPair((String) null, applicationId == null ? Utility.getMetadataApplicationId(context) : applicationId);
        } else {
            this.accessTokenAppId = new AccessTokenAppIdPair(accessToken);
        }
        synchronized (staticLock) {
            if (applicationContext == null) {
                applicationContext = context.getApplicationContext();
            }
        }
        initializeTimersIfNeeded();
    }

    private static void initializeTimersIfNeeded() {
        synchronized (staticLock) {
            if (backgroundExecutor == null) {
                backgroundExecutor = new ScheduledThreadPoolExecutor(1);
                backgroundExecutor.scheduleAtFixedRate(new Runnable() {
                    public void run() {
                        if (AppEventsLogger.getFlushBehavior() != FlushBehavior.EXPLICIT_ONLY) {
                            AppEventsLogger.flushAndWait(FlushReason.TIMER);
                        }
                    }
                }, 0, 15, TimeUnit.SECONDS);
                backgroundExecutor.scheduleAtFixedRate(new Runnable() {
                    public void run() {
                        Set<String> applicationIds = new HashSet<>();
                        synchronized (AppEventsLogger.staticLock) {
                            for (AccessTokenAppIdPair accessTokenAppId : AppEventsLogger.stateMap.keySet()) {
                                applicationIds.add(accessTokenAppId.getApplicationId());
                            }
                        }
                        for (String applicationId : applicationIds) {
                            Utility.queryAppSettings(applicationId, true);
                        }
                    }
                }, 0, 86400, TimeUnit.SECONDS);
            }
        }
    }

    private void logEvent(String eventName, Double valueToSum, Bundle parameters, boolean isImplicitlyLogged, @Nullable UUID currentSessionId) {
        logEvent(applicationContext, new AppEvent(this.contextName, eventName, valueToSum, parameters, isImplicitlyLogged, currentSessionId), this.accessTokenAppId);
    }

    private static void logEvent(final Context context, final AppEvent event, final AccessTokenAppIdPair accessTokenAppId2) {
        FacebookSdk.getExecutor().execute(new Runnable() {
            public void run() {
                AppEventsLogger.getSessionEventsState(context, accessTokenAppId2).addEvent(event);
                AppEventsLogger.flushIfNecessary();
            }
        });
        if (!event.isImplicit && !isActivateAppEventRequested) {
            if (event.getName() == AppEventsConstants.EVENT_NAME_ACTIVATED_APP) {
                isActivateAppEventRequested = true;
            } else {
                Logger.log(LoggingBehavior.APP_EVENTS, "AppEvents", "Warning: Please call AppEventsLogger.activateApp(...)from the long-lived activity's onResume() methodbefore logging other app events.");
            }
        }
    }

    static void eagerFlush() {
        if (getFlushBehavior() != FlushBehavior.EXPLICIT_ONLY) {
            flush(FlushReason.EAGER_FLUSHING_EVENT);
        }
    }

    /* access modifiers changed from: private */
    public static void flushIfNecessary() {
        synchronized (staticLock) {
            if (getFlushBehavior() != FlushBehavior.EXPLICIT_ONLY && getAccumulatedEventCount() > 100) {
                flush(FlushReason.EVENT_THRESHOLD);
            }
        }
    }

    private static int getAccumulatedEventCount() {
        int result;
        synchronized (staticLock) {
            result = 0;
            for (SessionEventsState state : stateMap.values()) {
                result += state.getAccumulatedEventCount();
            }
        }
        return result;
    }

    /* access modifiers changed from: private */
    public static SessionEventsState getSessionEventsState(Context context, AccessTokenAppIdPair accessTokenAppId2) {
        AttributionIdentifiers attributionIdentifiers = null;
        if (stateMap.get(accessTokenAppId2) == null) {
            attributionIdentifiers = AttributionIdentifiers.getAttributionIdentifiers(context);
        }
        synchronized (staticLock) {
            try {
                SessionEventsState state = stateMap.get(accessTokenAppId2);
                if (state == null) {
                    SessionEventsState state2 = new SessionEventsState(attributionIdentifiers, context.getPackageName(), getAnonymousAppDeviceGUID(context));
                    try {
                        stateMap.put(accessTokenAppId2, state2);
                        state = state2;
                    } catch (Throwable th) {
                        th = th;
                        SessionEventsState sessionEventsState = state2;
                        throw th;
                    }
                }
                return state;
            } catch (Throwable th2) {
                th = th2;
                throw th;
            }
        }
    }

    private static SessionEventsState getSessionEventsState(AccessTokenAppIdPair accessTokenAppId2) {
        SessionEventsState sessionEventsState;
        synchronized (staticLock) {
            sessionEventsState = stateMap.get(accessTokenAppId2);
        }
        return sessionEventsState;
    }

    private static void flush(final FlushReason reason) {
        FacebookSdk.getExecutor().execute(new Runnable() {
            public void run() {
                AppEventsLogger.flushAndWait(reason);
            }
        });
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:10:?, code lost:
        r1 = buildAndExecuteRequests(r6, r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x004b, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x004c, code lost:
        com.facebook.internal.Utility.logd(TAG, "Caught unexpected exception while flushing: ", r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:0x0018, code lost:
        accumulatePersistedEvents();
        r1 = null;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void flushAndWait(com.facebook.appevents.AppEventsLogger.FlushReason r6) {
        /*
            java.lang.Object r5 = staticLock
            monitor-enter(r5)
            boolean r4 = requestInFlight     // Catch:{ all -> 0x0048 }
            if (r4 == 0) goto L_0x0009
            monitor-exit(r5)     // Catch:{ all -> 0x0048 }
        L_0x0008:
            return
        L_0x0009:
            r4 = 1
            requestInFlight = r4     // Catch:{ all -> 0x0048 }
            java.util.HashSet r3 = new java.util.HashSet     // Catch:{ all -> 0x0048 }
            java.util.Map<com.facebook.appevents.AppEventsLogger$AccessTokenAppIdPair, com.facebook.appevents.AppEventsLogger$SessionEventsState> r4 = stateMap     // Catch:{ all -> 0x0048 }
            java.util.Set r4 = r4.keySet()     // Catch:{ all -> 0x0048 }
            r3.<init>(r4)     // Catch:{ all -> 0x0048 }
            monitor-exit(r5)     // Catch:{ all -> 0x0048 }
            accumulatePersistedEvents()
            r1 = 0
            com.facebook.appevents.AppEventsLogger$FlushStatistics r1 = buildAndExecuteRequests(r6, r3)     // Catch:{ Exception -> 0x004b }
        L_0x0020:
            java.lang.Object r5 = staticLock
            monitor-enter(r5)
            r4 = 0
            requestInFlight = r4     // Catch:{ all -> 0x0054 }
            monitor-exit(r5)     // Catch:{ all -> 0x0054 }
            if (r1 == 0) goto L_0x0008
            android.content.Intent r2 = new android.content.Intent
            java.lang.String r4 = "com.facebook.sdk.APP_EVENTS_FLUSHED"
            r2.<init>(r4)
            java.lang.String r4 = "com.facebook.sdk.APP_EVENTS_NUM_EVENTS_FLUSHED"
            int r5 = r1.numEvents
            r2.putExtra(r4, r5)
            java.lang.String r4 = "com.facebook.sdk.APP_EVENTS_FLUSH_RESULT"
            com.facebook.appevents.AppEventsLogger$FlushResult r5 = r1.result
            r2.putExtra(r4, r5)
            android.content.Context r4 = applicationContext
            android.support.v4.content.LocalBroadcastManager r4 = android.support.v4.content.LocalBroadcastManager.getInstance(r4)
            r4.sendBroadcast(r2)
            goto L_0x0008
        L_0x0048:
            r4 = move-exception
            monitor-exit(r5)     // Catch:{ all -> 0x0048 }
            throw r4
        L_0x004b:
            r0 = move-exception
            java.lang.String r4 = TAG
            java.lang.String r5 = "Caught unexpected exception while flushing: "
            com.facebook.internal.Utility.logd(r4, r5, r0)
            goto L_0x0020
        L_0x0054:
            r4 = move-exception
            monitor-exit(r5)     // Catch:{ all -> 0x0054 }
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.facebook.appevents.AppEventsLogger.flushAndWait(com.facebook.appevents.AppEventsLogger$FlushReason):void");
    }

    private static FlushStatistics buildAndExecuteRequests(FlushReason reason, Set<AccessTokenAppIdPair> keysToFlush) {
        GraphRequest request;
        FlushStatistics flushResults = new FlushStatistics();
        boolean limitEventUsage = FacebookSdk.getLimitEventAndDataUsage(applicationContext);
        List<GraphRequest> requestsToExecute = new ArrayList<>();
        for (AccessTokenAppIdPair accessTokenAppId2 : keysToFlush) {
            SessionEventsState sessionEventsState = getSessionEventsState(accessTokenAppId2);
            if (!(sessionEventsState == null || (request = buildRequestForSession(accessTokenAppId2, sessionEventsState, limitEventUsage, flushResults)) == null)) {
                requestsToExecute.add(request);
            }
        }
        if (requestsToExecute.size() <= 0) {
            return null;
        }
        Logger.log(LoggingBehavior.APP_EVENTS, TAG, "Flushing %d events due to %s.", Integer.valueOf(flushResults.numEvents), reason.toString());
        for (GraphRequest request2 : requestsToExecute) {
            request2.executeAndWait();
        }
        return flushResults;
    }

    private static class FlushStatistics {
        public int numEvents;
        public FlushResult result;

        private FlushStatistics() {
            this.numEvents = 0;
            this.result = FlushResult.SUCCESS;
        }
    }

    private static GraphRequest buildRequestForSession(final AccessTokenAppIdPair accessTokenAppId2, final SessionEventsState sessionEventsState, boolean limitEventUsage, final FlushStatistics flushState) {
        String applicationId = accessTokenAppId2.getApplicationId();
        Utility.FetchedAppSettings fetchedAppSettings = Utility.queryAppSettings(applicationId, false);
        final GraphRequest postRequest = GraphRequest.newPostRequest((AccessToken) null, String.format("%s/activities", new Object[]{applicationId}), (JSONObject) null, (GraphRequest.Callback) null);
        Bundle requestParameters = postRequest.getParameters();
        if (requestParameters == null) {
            requestParameters = new Bundle();
        }
        requestParameters.putString("access_token", accessTokenAppId2.getAccessTokenString());
        if (pushNotificationsRegistrationId != null) {
            requestParameters.putString("device_token", pushNotificationsRegistrationId);
        }
        postRequest.setParameters(requestParameters);
        if (fetchedAppSettings == null) {
            return null;
        }
        int numEvents = sessionEventsState.populateRequest(postRequest, fetchedAppSettings.supportsImplicitLogging(), limitEventUsage);
        if (numEvents == 0) {
            return null;
        }
        flushState.numEvents += numEvents;
        postRequest.setCallback(new GraphRequest.Callback() {
            public void onCompleted(GraphResponse response) {
                AppEventsLogger.handleResponse(accessTokenAppId2, postRequest, response, sessionEventsState, flushState);
            }
        });
        return postRequest;
    }

    /* access modifiers changed from: private */
    public static void handleResponse(AccessTokenAppIdPair accessTokenAppId2, GraphRequest request, GraphResponse response, SessionEventsState sessionEventsState, FlushStatistics flushState) {
        String prettyPrintedEvents;
        FacebookRequestError error = response.getError();
        String resultDescription = "Success";
        FlushResult flushResult = FlushResult.SUCCESS;
        if (error != null) {
            if (error.getErrorCode() == -1) {
                resultDescription = "Failed: No Connectivity";
                flushResult = FlushResult.NO_CONNECTIVITY;
            } else {
                resultDescription = String.format("Failed:\n  Response: %s\n  Error %s", new Object[]{response.toString(), error.toString()});
                flushResult = FlushResult.SERVER_ERROR;
            }
        }
        if (FacebookSdk.isLoggingBehaviorEnabled(LoggingBehavior.APP_EVENTS)) {
            try {
                prettyPrintedEvents = new JSONArray((String) request.getTag()).toString(2);
            } catch (JSONException e) {
                prettyPrintedEvents = "<Can't encode events for debug logging>";
            }
            Logger.log(LoggingBehavior.APP_EVENTS, TAG, "Flush completed\nParams: %s\n  Result: %s\n  Events JSON: %s", request.getGraphObject().toString(), resultDescription, prettyPrintedEvents);
        }
        sessionEventsState.clearInFlightAndStats(error != null);
        if (flushResult == FlushResult.NO_CONNECTIVITY) {
            PersistedEvents.persistEvents(applicationContext, accessTokenAppId2, sessionEventsState);
        }
        if (flushResult != FlushResult.SUCCESS && flushState.result != FlushResult.NO_CONNECTIVITY) {
            flushState.result = flushResult;
        }
    }

    private static int accumulatePersistedEvents() {
        PersistedEvents persistedEvents = PersistedEvents.readAndClearStore(applicationContext);
        int result = 0;
        for (AccessTokenAppIdPair accessTokenAppId2 : persistedEvents.keySet()) {
            SessionEventsState sessionEventsState = getSessionEventsState(applicationContext, accessTokenAppId2);
            List<AppEvent> events = persistedEvents.getEvents(accessTokenAppId2);
            sessionEventsState.accumulatePersistedEvents(events);
            result += events.size();
        }
        return result;
    }

    private static void notifyDeveloperError(String message) {
        Logger.log(LoggingBehavior.DEVELOPER_ERRORS, "AppEvents", message);
    }

    private static void setSourceApplication(Activity activity) {
        ComponentName callingApplication = activity.getCallingActivity();
        if (callingApplication != null) {
            String callingApplicationPackage = callingApplication.getPackageName();
            if (callingApplicationPackage.equals(activity.getPackageName())) {
                resetSourceApplication();
                return;
            }
            sourceApplication = callingApplicationPackage;
        }
        Intent openIntent = activity.getIntent();
        if (openIntent == null || openIntent.getBooleanExtra(SOURCE_APPLICATION_HAS_BEEN_SET_BY_THIS_INTENT, false)) {
            resetSourceApplication();
            return;
        }
        Bundle applinkData = AppLinks.getAppLinkData(openIntent);
        if (applinkData == null) {
            resetSourceApplication();
            return;
        }
        isOpenedByApplink = true;
        Bundle applinkReferrerData = applinkData.getBundle("referer_app_link");
        if (applinkReferrerData == null) {
            sourceApplication = null;
            return;
        }
        sourceApplication = applinkReferrerData.getString("package");
        openIntent.putExtra(SOURCE_APPLICATION_HAS_BEEN_SET_BY_THIS_INTENT, true);
    }

    static void setSourceApplication(String applicationPackage, boolean openByAppLink) {
        sourceApplication = applicationPackage;
        isOpenedByApplink = openByAppLink;
    }

    static String getSourceApplication() {
        String openType = "Unclassified";
        if (isOpenedByApplink) {
            openType = "Applink";
        }
        if (sourceApplication != null) {
            return openType + "(" + sourceApplication + ")";
        }
        return openType;
    }

    static void resetSourceApplication() {
        sourceApplication = null;
        isOpenedByApplink = false;
    }

    public static String getAnonymousAppDeviceGUID(Context context) {
        if (anonymousAppDeviceGUID == null) {
            synchronized (staticLock) {
                if (anonymousAppDeviceGUID == null) {
                    anonymousAppDeviceGUID = context.getSharedPreferences(APP_EVENT_PREFERENCES, 0).getString("anonymousAppDeviceGUID", (String) null);
                    if (anonymousAppDeviceGUID == null) {
                        anonymousAppDeviceGUID = "XZ" + UUID.randomUUID().toString();
                        context.getSharedPreferences(APP_EVENT_PREFERENCES, 0).edit().putString("anonymousAppDeviceGUID", anonymousAppDeviceGUID).apply();
                    }
                }
            }
        }
        return anonymousAppDeviceGUID;
    }

    private static class SessionEventsState {
        public static final String ENCODED_EVENTS_KEY = "encoded_events";
        public static final String EVENT_COUNT_KEY = "event_count";
        public static final String NUM_SKIPPED_KEY = "num_skipped";
        private final int MAX_ACCUMULATED_LOG_EVENTS = 1000;
        private List<AppEvent> accumulatedEvents = new ArrayList();
        private String anonymousAppDeviceGUID;
        private AttributionIdentifiers attributionIdentifiers;
        private List<AppEvent> inFlightEvents = new ArrayList();
        private int numSkippedEventsDueToFullBuffer;
        private String packageName;

        public SessionEventsState(AttributionIdentifiers identifiers, String packageName2, String anonymousGUID) {
            this.attributionIdentifiers = identifiers;
            this.packageName = packageName2;
            this.anonymousAppDeviceGUID = anonymousGUID;
        }

        public synchronized void addEvent(AppEvent event) {
            if (this.accumulatedEvents.size() + this.inFlightEvents.size() >= 1000) {
                this.numSkippedEventsDueToFullBuffer++;
            } else {
                this.accumulatedEvents.add(event);
            }
        }

        public synchronized int getAccumulatedEventCount() {
            return this.accumulatedEvents.size();
        }

        public synchronized void clearInFlightAndStats(boolean moveToAccumulated) {
            if (moveToAccumulated) {
                this.accumulatedEvents.addAll(this.inFlightEvents);
            }
            this.inFlightEvents.clear();
            this.numSkippedEventsDueToFullBuffer = 0;
        }

        public int populateRequest(GraphRequest request, boolean includeImplicitEvents, boolean limitEventUsage) {
            synchronized (this) {
                int numSkipped = this.numSkippedEventsDueToFullBuffer;
                this.inFlightEvents.addAll(this.accumulatedEvents);
                this.accumulatedEvents.clear();
                JSONArray jsonArray = new JSONArray();
                for (AppEvent event : this.inFlightEvents) {
                    if (includeImplicitEvents || !event.getIsImplicit()) {
                        jsonArray.put(event.getJSONObject());
                    }
                }
                if (jsonArray.length() == 0) {
                    return 0;
                }
                populateRequest(request, numSkipped, jsonArray, limitEventUsage);
                return jsonArray.length();
            }
        }

        public synchronized List<AppEvent> getEventsToPersist() {
            List<AppEvent> result;
            result = this.accumulatedEvents;
            this.accumulatedEvents = new ArrayList();
            return result;
        }

        public synchronized void accumulatePersistedEvents(List<AppEvent> events) {
            this.accumulatedEvents.addAll(events);
        }

        private void populateRequest(GraphRequest request, int numSkipped, JSONArray events, boolean limitEventUsage) {
            JSONObject publishParams;
            try {
                publishParams = AppEventsLoggerUtility.getJSONObjectForGraphAPICall(AppEventsLoggerUtility.GraphAPIActivityType.CUSTOM_APP_EVENTS, this.attributionIdentifiers, this.anonymousAppDeviceGUID, limitEventUsage, AppEventsLogger.applicationContext);
                if (this.numSkippedEventsDueToFullBuffer > 0) {
                    publishParams.put("num_skipped_events", numSkipped);
                }
            } catch (JSONException e) {
                publishParams = new JSONObject();
            }
            request.setGraphObject(publishParams);
            Bundle requestParameters = request.getParameters();
            if (requestParameters == null) {
                requestParameters = new Bundle();
            }
            String jsonString = events.toString();
            if (jsonString != null) {
                requestParameters.putByteArray("custom_events_file", getStringAsByteArray(jsonString));
                request.setTag(jsonString);
            }
            request.setParameters(requestParameters);
        }

        private byte[] getStringAsByteArray(String jsonString) {
            try {
                return jsonString.getBytes(HttpURLConnectionBuilder.DEFAULT_CHARSET);
            } catch (UnsupportedEncodingException e) {
                Utility.logd("Encoding exception: ", (Exception) e);
                return null;
            }
        }
    }

    static class AppEvent implements Serializable {
        private static final long serialVersionUID = 1;
        private static final HashSet<String> validatedIdentifiers = new HashSet<>();
        /* access modifiers changed from: private */
        public boolean isImplicit;
        private JSONObject jsonObject;
        private String name;

        public AppEvent(String contextName, String eventName, Double valueToSum, Bundle parameters, boolean isImplicitlyLogged, @Nullable UUID currentSessionId) {
            try {
                validateIdentifier(eventName);
                this.name = eventName;
                this.isImplicit = isImplicitlyLogged;
                this.jsonObject = new JSONObject();
                this.jsonObject.put("_eventName", eventName);
                this.jsonObject.put(Constants.LOG_TIME_APP_EVENT_KEY, System.currentTimeMillis() / 1000);
                this.jsonObject.put("_ui", contextName);
                if (currentSessionId != null) {
                    this.jsonObject.put("_session_id", currentSessionId);
                }
                if (valueToSum != null) {
                    this.jsonObject.put("_valueToSum", valueToSum.doubleValue());
                }
                if (this.isImplicit) {
                    this.jsonObject.put("_implicitlyLogged", "1");
                }
                if (parameters != null) {
                    for (String key : parameters.keySet()) {
                        validateIdentifier(key);
                        Object value = parameters.get(key);
                        if ((value instanceof String) || (value instanceof Number)) {
                            this.jsonObject.put(key, value.toString());
                        } else {
                            throw new FacebookException(String.format("Parameter value '%s' for key '%s' should be a string or a numeric type.", new Object[]{value, key}));
                        }
                    }
                }
                if (!this.isImplicit) {
                    Logger.log(LoggingBehavior.APP_EVENTS, "AppEvents", "Created app event '%s'", this.jsonObject.toString());
                }
            } catch (JSONException jsonException) {
                Logger.log(LoggingBehavior.APP_EVENTS, "AppEvents", "JSON encoding for app event failed: '%s'", jsonException.toString());
                this.jsonObject = null;
            } catch (FacebookException e) {
                Logger.log(LoggingBehavior.APP_EVENTS, "AppEvents", "Invalid app event name or parameter:", e.toString());
                this.jsonObject = null;
            }
        }

        public String getName() {
            return this.name;
        }

        private AppEvent(String jsonString, boolean isImplicit2) throws JSONException {
            this.jsonObject = new JSONObject(jsonString);
            this.isImplicit = isImplicit2;
        }

        public boolean getIsImplicit() {
            return this.isImplicit;
        }

        public JSONObject getJSONObject() {
            return this.jsonObject;
        }

        private void validateIdentifier(String identifier) throws FacebookException {
            boolean alreadyValidated;
            if (identifier == null || identifier.length() == 0 || identifier.length() > 40) {
                if (identifier == null) {
                    identifier = "<None Provided>";
                }
                throw new FacebookException(String.format(Locale.ROOT, "Identifier '%s' must be less than %d characters", new Object[]{identifier, 40}));
            }
            synchronized (validatedIdentifiers) {
                alreadyValidated = validatedIdentifiers.contains(identifier);
            }
            if (alreadyValidated) {
                return;
            }
            if (identifier.matches("^[0-9a-zA-Z_]+[0-9a-zA-Z _-]*$")) {
                synchronized (validatedIdentifiers) {
                    validatedIdentifiers.add(identifier);
                }
                return;
            }
            throw new FacebookException(String.format("Skipping event named '%s' due to illegal name - must be under 40 chars and alphanumeric, _, - or space, and not start with a space or hyphen.", new Object[]{identifier}));
        }

        private static class SerializationProxyV1 implements Serializable {
            private static final long serialVersionUID = -2488473066578201069L;
            private final boolean isImplicit;
            private final String jsonString;

            private SerializationProxyV1(String jsonString2, boolean isImplicit2) {
                this.jsonString = jsonString2;
                this.isImplicit = isImplicit2;
            }

            private Object readResolve() throws JSONException {
                return new AppEvent(this.jsonString, this.isImplicit);
            }
        }

        private Object writeReplace() {
            return new SerializationProxyV1(this.jsonObject.toString(), this.isImplicit);
        }

        public String toString() {
            return String.format("\"%s\", implicit: %b, json: %s", new Object[]{this.jsonObject.optString("_eventName"), Boolean.valueOf(this.isImplicit), this.jsonObject.toString()});
        }
    }

    static class PersistedAppSessionInfo {
        private static final String PERSISTED_SESSION_INFO_FILENAME = "AppEventsLogger.persistedsessioninfo";
        private static final Runnable appSessionInfoFlushRunnable = new Runnable() {
            public void run() {
                PersistedAppSessionInfo.saveAppSessionInformation(AppEventsLogger.applicationContext);
            }
        };
        private static Map<AccessTokenAppIdPair, FacebookTimeSpentData> appSessionInfoMap;
        private static boolean hasChanges = false;
        private static boolean isLoaded = false;
        private static final Object staticLock = new Object();

        PersistedAppSessionInfo() {
        }

        /* JADX WARNING: Removed duplicated region for block: B:21:0x004d A[Catch:{ FileNotFoundException -> 0x0040, Exception -> 0x005e, all -> 0x005b }] */
        /* JADX WARNING: Removed duplicated region for block: B:32:0x0089 A[Catch:{ FileNotFoundException -> 0x0040, Exception -> 0x005e, all -> 0x005b }] */
        /* JADX WARNING: Removed duplicated region for block: B:37:0x00a4 A[Catch:{ FileNotFoundException -> 0x0040, Exception -> 0x005e, all -> 0x005b }] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private static void restoreAppSessionInformation(android.content.Context r7) {
            /*
                r1 = 0
                java.lang.Object r4 = staticLock
                monitor-enter(r4)
                boolean r3 = isLoaded     // Catch:{ all -> 0x005b }
                if (r3 != 0) goto L_0x003e
                java.io.ObjectInputStream r2 = new java.io.ObjectInputStream     // Catch:{ FileNotFoundException -> 0x0040, Exception -> 0x005e }
                java.lang.String r3 = "AppEventsLogger.persistedsessioninfo"
                java.io.FileInputStream r3 = r7.openFileInput(r3)     // Catch:{ FileNotFoundException -> 0x0040, Exception -> 0x005e }
                r2.<init>(r3)     // Catch:{ FileNotFoundException -> 0x0040, Exception -> 0x005e }
                java.lang.Object r3 = r2.readObject()     // Catch:{ FileNotFoundException -> 0x00bb, Exception -> 0x00b8, all -> 0x00b5 }
                java.util.HashMap r3 = (java.util.HashMap) r3     // Catch:{ FileNotFoundException -> 0x00bb, Exception -> 0x00b8, all -> 0x00b5 }
                appSessionInfoMap = r3     // Catch:{ FileNotFoundException -> 0x00bb, Exception -> 0x00b8, all -> 0x00b5 }
                com.facebook.LoggingBehavior r3 = com.facebook.LoggingBehavior.APP_EVENTS     // Catch:{ FileNotFoundException -> 0x00bb, Exception -> 0x00b8, all -> 0x00b5 }
                java.lang.String r5 = "AppEvents"
                java.lang.String r6 = "App session info loaded"
                com.facebook.internal.Logger.log(r3, r5, r6)     // Catch:{ FileNotFoundException -> 0x00bb, Exception -> 0x00b8, all -> 0x00b5 }
                com.facebook.internal.Utility.closeQuietly(r2)     // Catch:{ all -> 0x00b2 }
                java.lang.String r3 = "AppEventsLogger.persistedsessioninfo"
                r7.deleteFile(r3)     // Catch:{ all -> 0x00b2 }
                java.util.Map<com.facebook.appevents.AppEventsLogger$AccessTokenAppIdPair, com.facebook.appevents.FacebookTimeSpentData> r3 = appSessionInfoMap     // Catch:{ all -> 0x00b2 }
                if (r3 != 0) goto L_0x0037
                java.util.HashMap r3 = new java.util.HashMap     // Catch:{ all -> 0x00b2 }
                r3.<init>()     // Catch:{ all -> 0x00b2 }
                appSessionInfoMap = r3     // Catch:{ all -> 0x00b2 }
            L_0x0037:
                r3 = 1
                isLoaded = r3     // Catch:{ all -> 0x00b2 }
                r3 = 0
                hasChanges = r3     // Catch:{ all -> 0x00b2 }
                r1 = r2
            L_0x003e:
                monitor-exit(r4)     // Catch:{ all -> 0x005b }
                return
            L_0x0040:
                r3 = move-exception
            L_0x0041:
                com.facebook.internal.Utility.closeQuietly(r1)     // Catch:{ all -> 0x005b }
                java.lang.String r3 = "AppEventsLogger.persistedsessioninfo"
                r7.deleteFile(r3)     // Catch:{ all -> 0x005b }
                java.util.Map<com.facebook.appevents.AppEventsLogger$AccessTokenAppIdPair, com.facebook.appevents.FacebookTimeSpentData> r3 = appSessionInfoMap     // Catch:{ all -> 0x005b }
                if (r3 != 0) goto L_0x0054
                java.util.HashMap r3 = new java.util.HashMap     // Catch:{ all -> 0x005b }
                r3.<init>()     // Catch:{ all -> 0x005b }
                appSessionInfoMap = r3     // Catch:{ all -> 0x005b }
            L_0x0054:
                r3 = 1
                isLoaded = r3     // Catch:{ all -> 0x005b }
                r3 = 0
                hasChanges = r3     // Catch:{ all -> 0x005b }
                goto L_0x003e
            L_0x005b:
                r3 = move-exception
            L_0x005c:
                monitor-exit(r4)     // Catch:{ all -> 0x005b }
                throw r3
            L_0x005e:
                r0 = move-exception
            L_0x005f:
                java.lang.String r3 = com.facebook.appevents.AppEventsLogger.TAG     // Catch:{ all -> 0x0097 }
                java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x0097 }
                r5.<init>()     // Catch:{ all -> 0x0097 }
                java.lang.String r6 = "Got unexpected exception: "
                java.lang.StringBuilder r5 = r5.append(r6)     // Catch:{ all -> 0x0097 }
                java.lang.String r6 = r0.toString()     // Catch:{ all -> 0x0097 }
                java.lang.StringBuilder r5 = r5.append(r6)     // Catch:{ all -> 0x0097 }
                java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x0097 }
                android.util.Log.d(r3, r5)     // Catch:{ all -> 0x0097 }
                com.facebook.internal.Utility.closeQuietly(r1)     // Catch:{ all -> 0x005b }
                java.lang.String r3 = "AppEventsLogger.persistedsessioninfo"
                r7.deleteFile(r3)     // Catch:{ all -> 0x005b }
                java.util.Map<com.facebook.appevents.AppEventsLogger$AccessTokenAppIdPair, com.facebook.appevents.FacebookTimeSpentData> r3 = appSessionInfoMap     // Catch:{ all -> 0x005b }
                if (r3 != 0) goto L_0x0090
                java.util.HashMap r3 = new java.util.HashMap     // Catch:{ all -> 0x005b }
                r3.<init>()     // Catch:{ all -> 0x005b }
                appSessionInfoMap = r3     // Catch:{ all -> 0x005b }
            L_0x0090:
                r3 = 1
                isLoaded = r3     // Catch:{ all -> 0x005b }
                r3 = 0
                hasChanges = r3     // Catch:{ all -> 0x005b }
                goto L_0x003e
            L_0x0097:
                r3 = move-exception
            L_0x0098:
                com.facebook.internal.Utility.closeQuietly(r1)     // Catch:{ all -> 0x005b }
                java.lang.String r5 = "AppEventsLogger.persistedsessioninfo"
                r7.deleteFile(r5)     // Catch:{ all -> 0x005b }
                java.util.Map<com.facebook.appevents.AppEventsLogger$AccessTokenAppIdPair, com.facebook.appevents.FacebookTimeSpentData> r5 = appSessionInfoMap     // Catch:{ all -> 0x005b }
                if (r5 != 0) goto L_0x00ab
                java.util.HashMap r5 = new java.util.HashMap     // Catch:{ all -> 0x005b }
                r5.<init>()     // Catch:{ all -> 0x005b }
                appSessionInfoMap = r5     // Catch:{ all -> 0x005b }
            L_0x00ab:
                r5 = 1
                isLoaded = r5     // Catch:{ all -> 0x005b }
                r5 = 0
                hasChanges = r5     // Catch:{ all -> 0x005b }
                throw r3     // Catch:{ all -> 0x005b }
            L_0x00b2:
                r3 = move-exception
                r1 = r2
                goto L_0x005c
            L_0x00b5:
                r3 = move-exception
                r1 = r2
                goto L_0x0098
            L_0x00b8:
                r0 = move-exception
                r1 = r2
                goto L_0x005f
            L_0x00bb:
                r3 = move-exception
                r1 = r2
                goto L_0x0041
            */
            throw new UnsupportedOperationException("Method not decompiled: com.facebook.appevents.AppEventsLogger.PersistedAppSessionInfo.restoreAppSessionInformation(android.content.Context):void");
        }

        /* JADX WARNING: Unknown top exception splitter block from list: {B:24:0x0057=Splitter:B:24:0x0057, B:12:0x002e=Splitter:B:12:0x002e} */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        static void saveAppSessionInformation(android.content.Context r7) {
            /*
                r1 = 0
                java.lang.Object r4 = staticLock
                monitor-enter(r4)
                boolean r3 = hasChanges     // Catch:{ all -> 0x0053 }
                if (r3 == 0) goto L_0x002e
                java.io.ObjectOutputStream r2 = new java.io.ObjectOutputStream     // Catch:{ Exception -> 0x0030 }
                java.io.BufferedOutputStream r3 = new java.io.BufferedOutputStream     // Catch:{ Exception -> 0x0030 }
                java.lang.String r5 = "AppEventsLogger.persistedsessioninfo"
                r6 = 0
                java.io.FileOutputStream r5 = r7.openFileOutput(r5, r6)     // Catch:{ Exception -> 0x0030 }
                r3.<init>(r5)     // Catch:{ Exception -> 0x0030 }
                r2.<init>(r3)     // Catch:{ Exception -> 0x0030 }
                java.util.Map<com.facebook.appevents.AppEventsLogger$AccessTokenAppIdPair, com.facebook.appevents.FacebookTimeSpentData> r3 = appSessionInfoMap     // Catch:{ Exception -> 0x0061, all -> 0x005e }
                r2.writeObject(r3)     // Catch:{ Exception -> 0x0061, all -> 0x005e }
                r3 = 0
                hasChanges = r3     // Catch:{ Exception -> 0x0061, all -> 0x005e }
                com.facebook.LoggingBehavior r3 = com.facebook.LoggingBehavior.APP_EVENTS     // Catch:{ Exception -> 0x0061, all -> 0x005e }
                java.lang.String r5 = "AppEvents"
                java.lang.String r6 = "App session info saved"
                com.facebook.internal.Logger.log(r3, r5, r6)     // Catch:{ Exception -> 0x0061, all -> 0x005e }
                com.facebook.internal.Utility.closeQuietly(r2)     // Catch:{ all -> 0x005b }
                r1 = r2
            L_0x002e:
                monitor-exit(r4)     // Catch:{ all -> 0x0053 }
                return
            L_0x0030:
                r0 = move-exception
            L_0x0031:
                java.lang.String r3 = com.facebook.appevents.AppEventsLogger.TAG     // Catch:{ all -> 0x0056 }
                java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x0056 }
                r5.<init>()     // Catch:{ all -> 0x0056 }
                java.lang.String r6 = "Got unexpected exception: "
                java.lang.StringBuilder r5 = r5.append(r6)     // Catch:{ all -> 0x0056 }
                java.lang.String r6 = r0.toString()     // Catch:{ all -> 0x0056 }
                java.lang.StringBuilder r5 = r5.append(r6)     // Catch:{ all -> 0x0056 }
                java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x0056 }
                android.util.Log.d(r3, r5)     // Catch:{ all -> 0x0056 }
                com.facebook.internal.Utility.closeQuietly(r1)     // Catch:{ all -> 0x0053 }
                goto L_0x002e
            L_0x0053:
                r3 = move-exception
            L_0x0054:
                monitor-exit(r4)     // Catch:{ all -> 0x0053 }
                throw r3
            L_0x0056:
                r3 = move-exception
            L_0x0057:
                com.facebook.internal.Utility.closeQuietly(r1)     // Catch:{ all -> 0x0053 }
                throw r3     // Catch:{ all -> 0x0053 }
            L_0x005b:
                r3 = move-exception
                r1 = r2
                goto L_0x0054
            L_0x005e:
                r3 = move-exception
                r1 = r2
                goto L_0x0057
            L_0x0061:
                r0 = move-exception
                r1 = r2
                goto L_0x0031
            */
            throw new UnsupportedOperationException("Method not decompiled: com.facebook.appevents.AppEventsLogger.PersistedAppSessionInfo.saveAppSessionInformation(android.content.Context):void");
        }

        static void onResume(Context context, AccessTokenAppIdPair accessTokenAppId, AppEventsLogger logger, long eventTime, String sourceApplicationInfo) {
            synchronized (staticLock) {
                getTimeSpentData(context, accessTokenAppId).onResume(logger, eventTime, sourceApplicationInfo);
                onTimeSpentDataUpdate();
            }
        }

        static void onSuspend(Context context, AccessTokenAppIdPair accessTokenAppId, AppEventsLogger logger, long eventTime) {
            synchronized (staticLock) {
                getTimeSpentData(context, accessTokenAppId).onSuspend(logger, eventTime);
                onTimeSpentDataUpdate();
            }
        }

        private static FacebookTimeSpentData getTimeSpentData(Context context, AccessTokenAppIdPair accessTokenAppId) {
            restoreAppSessionInformation(context);
            FacebookTimeSpentData result = appSessionInfoMap.get(accessTokenAppId);
            if (result != null) {
                return result;
            }
            FacebookTimeSpentData result2 = new FacebookTimeSpentData();
            appSessionInfoMap.put(accessTokenAppId, result2);
            return result2;
        }

        private static void onTimeSpentDataUpdate() {
            if (!hasChanges) {
                hasChanges = true;
                AppEventsLogger.backgroundExecutor.schedule(appSessionInfoFlushRunnable, 30, TimeUnit.SECONDS);
            }
        }
    }

    static class PersistedEvents {
        static final String PERSISTED_EVENTS_FILENAME = "AppEventsLogger.persistedevents";
        private Context context;
        private HashMap<AccessTokenAppIdPair, List<AppEvent>> persistedEvents = new HashMap<>();

        private PersistedEvents(Context context2) {
            this.context = context2;
        }

        public static PersistedEvents readAndClearStore(Context context2) {
            PersistedEvents persistedEvents2;
            synchronized (AppEventsLogger.staticLock) {
                persistedEvents2 = new PersistedEvents(context2);
                persistedEvents2.readAndClearStore();
            }
            return persistedEvents2;
        }

        public static void persistEvents(Context context2, AccessTokenAppIdPair accessTokenAppId, SessionEventsState eventsToPersist) {
            Map<AccessTokenAppIdPair, SessionEventsState> map = new HashMap<>();
            map.put(accessTokenAppId, eventsToPersist);
            persistEvents(context2, map);
        }

        public static void persistEvents(Context context2, Map<AccessTokenAppIdPair, SessionEventsState> eventsToPersist) {
            synchronized (AppEventsLogger.staticLock) {
                PersistedEvents persistedEvents2 = readAndClearStore(context2);
                for (Map.Entry<AccessTokenAppIdPair, SessionEventsState> entry : eventsToPersist.entrySet()) {
                    List<AppEvent> events = entry.getValue().getEventsToPersist();
                    if (events.size() != 0) {
                        persistedEvents2.addEvents(entry.getKey(), events);
                    }
                }
                persistedEvents2.write();
            }
        }

        public Set<AccessTokenAppIdPair> keySet() {
            return this.persistedEvents.keySet();
        }

        public List<AppEvent> getEvents(AccessTokenAppIdPair accessTokenAppId) {
            return this.persistedEvents.get(accessTokenAppId);
        }

        private void write() {
            ObjectOutputStream oos = null;
            try {
                ObjectOutputStream oos2 = new ObjectOutputStream(new BufferedOutputStream(this.context.openFileOutput(PERSISTED_EVENTS_FILENAME, 0)));
                try {
                    oos2.writeObject(this.persistedEvents);
                    Utility.closeQuietly(oos2);
                    ObjectOutputStream objectOutputStream = oos2;
                } catch (Exception e) {
                    e = e;
                    oos = oos2;
                    try {
                        Log.d(AppEventsLogger.TAG, "Got unexpected exception: " + e.toString());
                        Utility.closeQuietly(oos);
                    } catch (Throwable th) {
                        th = th;
                        Utility.closeQuietly(oos);
                        throw th;
                    }
                } catch (Throwable th2) {
                    th = th2;
                    oos = oos2;
                    Utility.closeQuietly(oos);
                    throw th;
                }
            } catch (Exception e2) {
                e = e2;
                Log.d(AppEventsLogger.TAG, "Got unexpected exception: " + e.toString());
                Utility.closeQuietly(oos);
            }
        }

        private void readAndClearStore() {
            ObjectInputStream ois = null;
            try {
                ObjectInputStream ois2 = new ObjectInputStream(new BufferedInputStream(this.context.openFileInput(PERSISTED_EVENTS_FILENAME)));
                try {
                    this.context.getFileStreamPath(PERSISTED_EVENTS_FILENAME).delete();
                    this.persistedEvents = (HashMap) ois2.readObject();
                    Utility.closeQuietly(ois2);
                    ObjectInputStream objectInputStream = ois2;
                } catch (FileNotFoundException e) {
                    ois = ois2;
                    Utility.closeQuietly(ois);
                } catch (Exception e2) {
                    e = e2;
                    ois = ois2;
                    try {
                        Log.d(AppEventsLogger.TAG, "Got unexpected exception: " + e.toString());
                        Utility.closeQuietly(ois);
                    } catch (Throwable th) {
                        th = th;
                        Utility.closeQuietly(ois);
                        throw th;
                    }
                } catch (Throwable th2) {
                    th = th2;
                    ois = ois2;
                    Utility.closeQuietly(ois);
                    throw th;
                }
            } catch (FileNotFoundException e3) {
                Utility.closeQuietly(ois);
            } catch (Exception e4) {
                e = e4;
                Log.d(AppEventsLogger.TAG, "Got unexpected exception: " + e.toString());
                Utility.closeQuietly(ois);
            }
        }

        public void addEvents(AccessTokenAppIdPair accessTokenAppId, List<AppEvent> eventsToPersist) {
            if (!this.persistedEvents.containsKey(accessTokenAppId)) {
                this.persistedEvents.put(accessTokenAppId, new ArrayList());
            }
            this.persistedEvents.get(accessTokenAppId).addAll(eventsToPersist);
        }
    }
}
