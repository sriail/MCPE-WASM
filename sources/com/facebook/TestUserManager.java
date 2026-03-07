package com.facebook;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import com.facebook.internal.NativeProtocol;
import com.facebook.internal.ServerProtocol;
import com.facebook.internal.Utility;
import com.facebook.share.internal.ShareConstants;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TestUserManager {
    static final /* synthetic */ boolean $assertionsDisabled = (!TestUserManager.class.desiredAssertionStatus());
    private static final String LOG_TAG = "TestUserManager";
    private Map<String, JSONObject> appTestAccounts;
    private String testApplicationId;
    private String testApplicationSecret;

    private enum Mode {
        PRIVATE,
        SHARED
    }

    public TestUserManager(String testApplicationSecret2, String testApplicationId2) {
        if (Utility.isNullOrEmpty(testApplicationId2) || Utility.isNullOrEmpty(testApplicationSecret2)) {
            throw new FacebookException("Must provide app ID and secret");
        }
        this.testApplicationSecret = testApplicationSecret2;
        this.testApplicationId = testApplicationId2;
    }

    public AccessToken getAccessTokenForPrivateUser(List<String> permissions) {
        return getAccessTokenForUser(permissions, Mode.PRIVATE, (String) null);
    }

    public AccessToken getAccessTokenForSharedUser(List<String> permissions) {
        return getAccessTokenForSharedUser(permissions, (String) null);
    }

    public AccessToken getAccessTokenForSharedUser(List<String> permissions, String uniqueUserTag) {
        return getAccessTokenForUser(permissions, Mode.SHARED, uniqueUserTag);
    }

    public synchronized String getTestApplicationId() {
        return this.testApplicationId;
    }

    public synchronized String getTestApplicationSecret() {
        return this.testApplicationSecret;
    }

    private AccessToken getAccessTokenForUser(List<String> permissions, Mode mode, String uniqueUserTag) {
        JSONObject testAccount;
        retrieveTestAccountsForAppIfNeeded();
        if (Utility.isNullOrEmpty(permissions)) {
            permissions = Arrays.asList(new String[]{"email", "publish_actions"});
        }
        if (mode == Mode.PRIVATE) {
            testAccount = createTestAccount(permissions, mode, uniqueUserTag);
        } else {
            testAccount = findOrCreateSharedTestAccount(permissions, mode, uniqueUserTag);
        }
        return new AccessToken(testAccount.optString("access_token"), this.testApplicationId, testAccount.optString("id"), permissions, (Collection<String>) null, AccessTokenSource.TEST_USER, (Date) null, (Date) null);
    }

    private synchronized void retrieveTestAccountsForAppIfNeeded() {
        if (this.appTestAccounts == null) {
            this.appTestAccounts = new HashMap();
            GraphRequest.setDefaultBatchApplicationId(this.testApplicationId);
            Bundle parameters = new Bundle();
            parameters.putString("access_token", getAppAccessToken());
            GraphRequest requestTestUsers = new GraphRequest((AccessToken) null, "app/accounts/test-users", parameters, (HttpMethod) null);
            requestTestUsers.setBatchEntryName("testUsers");
            requestTestUsers.setBatchEntryOmitResultOnSuccess(false);
            Bundle testUserNamesParam = new Bundle();
            testUserNamesParam.putString("access_token", getAppAccessToken());
            testUserNamesParam.putString("ids", "{result=testUsers:$.data.*.id}");
            testUserNamesParam.putString(GraphRequest.FIELDS_PARAM, "name");
            GraphRequest requestTestUserNames = new GraphRequest((AccessToken) null, "", testUserNamesParam, (HttpMethod) null);
            requestTestUserNames.setBatchEntryDependsOn("testUsers");
            List<GraphResponse> responses = GraphRequest.executeBatchAndWait(requestTestUsers, requestTestUserNames);
            if (responses == null || responses.size() != 2) {
                throw new FacebookException("Unexpected number of results from TestUsers batch query");
            }
            populateTestAccounts(responses.get(0).getJSONObject().optJSONArray(ShareConstants.WEB_DIALOG_PARAM_DATA), responses.get(1).getJSONObject());
        }
    }

    private synchronized void populateTestAccounts(JSONArray testAccounts, JSONObject userAccountsMap) {
        for (int i = 0; i < testAccounts.length(); i++) {
            JSONObject testAccount = testAccounts.optJSONObject(i);
            try {
                testAccount.put("name", userAccountsMap.optJSONObject(testAccount.optString("id")).optString("name"));
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Could not set name", e);
            }
            storeTestAccount(testAccount);
        }
    }

    private synchronized void storeTestAccount(JSONObject testAccount) {
        this.appTestAccounts.put(testAccount.optString("id"), testAccount);
    }

    private synchronized JSONObject findTestAccountMatchingIdentifier(String identifier) {
        JSONObject testAccount;
        Iterator i$ = this.appTestAccounts.values().iterator();
        while (true) {
            if (!i$.hasNext()) {
                testAccount = null;
                break;
            }
            testAccount = i$.next();
            if (testAccount.optString("name").contains(identifier)) {
                break;
            }
        }
        return testAccount;
    }

    /* access modifiers changed from: package-private */
    public final String getAppAccessToken() {
        return this.testApplicationId + "|" + this.testApplicationSecret;
    }

    private JSONObject findOrCreateSharedTestAccount(List<String> permissions, Mode mode, String uniqueUserTag) {
        JSONObject testAccount = findTestAccountMatchingIdentifier(getSharedTestAccountIdentifier(permissions, uniqueUserTag));
        return testAccount != null ? testAccount : createTestAccount(permissions, mode, uniqueUserTag);
    }

    private String getSharedTestAccountIdentifier(List<String> permissions, String uniqueUserTag) {
        return validNameStringFromInteger((((long) getPermissionsString(permissions).hashCode()) & 4294967295L) ^ (uniqueUserTag != null ? ((long) uniqueUserTag.hashCode()) & 4294967295L : 0));
    }

    private String validNameStringFromInteger(long i) {
        String s = Long.toString(i);
        StringBuilder result = new StringBuilder("Perm");
        char lastChar = 0;
        for (char c : s.toCharArray()) {
            if (c == lastChar) {
                c = (char) (c + 10);
            }
            result.append((char) ((c + 'a') - 48));
            lastChar = c;
        }
        return result.toString();
    }

    private JSONObject createTestAccount(List<String> permissions, Mode mode, String uniqueUserTag) {
        Bundle parameters = new Bundle();
        parameters.putString("installed", ServerProtocol.DIALOG_RETURN_SCOPES_TRUE);
        parameters.putString(NativeProtocol.RESULT_ARGS_PERMISSIONS, getPermissionsString(permissions));
        parameters.putString("access_token", getAppAccessToken());
        if (mode == Mode.SHARED) {
            parameters.putString("name", String.format("Shared %s Testuser", new Object[]{getSharedTestAccountIdentifier(permissions, uniqueUserTag)}));
        }
        GraphResponse response = new GraphRequest((AccessToken) null, String.format("%s/accounts/test-users", new Object[]{this.testApplicationId}), parameters, HttpMethod.POST).executeAndWait();
        FacebookRequestError error = response.getError();
        JSONObject testAccount = response.getJSONObject();
        if (error != null) {
            return null;
        }
        if (!$assertionsDisabled && testAccount == null) {
            throw new AssertionError();
        } else if (mode != Mode.SHARED) {
            return testAccount;
        } else {
            try {
                testAccount.put("name", parameters.getString("name"));
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Could not set name", e);
            }
            storeTestAccount(testAccount);
            return testAccount;
        }
    }

    private String getPermissionsString(List<String> permissions) {
        return TextUtils.join(",", permissions);
    }
}
