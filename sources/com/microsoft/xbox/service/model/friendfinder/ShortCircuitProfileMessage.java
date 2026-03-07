package com.microsoft.xbox.service.model.friendfinder;

import android.provider.Settings;
import com.microsoft.xbox.service.network.managers.friendfinder.PhoneContactInfo;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.network.XLEHttpStatusAndStream;
import com.microsoft.xboxtcui.XboxTcuiSdk;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import net.hockeyapp.android.utils.HttpURLConnectionBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ShortCircuitProfileMessage {

    public static class Application {
        public String name;
    }

    public enum MsgType {
        Add,
        AddXbox,
        Delete,
        PhoneVerification,
        Edit
    }

    public static class PhoneState {
        public boolean hasXboxApplication;
        public boolean isVerified;
    }

    public static class ShortCircuitProfileRequest {
        private String country;
        private MsgType msgType;
        private String phoneNumber;
        private String token;
        private boolean viaVoiceCall;

        public ShortCircuitProfileRequest(MsgType msgType2, String phoneNumber2, String country2) {
            this.msgType = msgType2;
            this.phoneNumber = phoneNumber2;
            this.country = country2;
        }

        public ShortCircuitProfileRequest(MsgType msgType2, String phoneNumber2, String country2, boolean viaVoiceCall2) {
            this.msgType = msgType2;
            this.phoneNumber = phoneNumber2;
            this.country = country2;
            this.viaVoiceCall = viaVoiceCall2;
        }

        public ShortCircuitProfileRequest(MsgType msgType2, String phoneNumber2, String country2, String token2) {
            this(msgType2, phoneNumber2, country2);
            this.token = token2;
        }

        public String toString() {
            try {
                JSONObject root = new JSONObject();
                JSONArray attributes = new JSONArray();
                root.put("Attributes", attributes);
                JSONObject attribute = new JSONObject();
                attributes.put(attribute);
                attribute.put("Name", "PersonalContactProfile.Phones");
                switch (this.msgType) {
                    case Add:
                        attribute.put("Add", getAddMessageContent());
                        break;
                    case AddXbox:
                        attribute.put("Edit", getAddXboxMessageContent());
                        break;
                    case Edit:
                        attribute.put("Edit", getEditMessageContent());
                        break;
                    case Delete:
                        attribute.put("Delete", getDeleteMessageContent());
                        break;
                    case PhoneVerification:
                        attribute.put("Edit", getPhoneVerificationMessageContent());
                        break;
                }
                return root.toString();
            } catch (JSONException e) {
                XLEAssert.assertTrue("Failed to create JSON object - " + e.getMessage(), false);
                return null;
            }
        }

        private JSONArray getAddMessageContent() {
            try {
                JSONArray array = new JSONArray();
                JSONObject addJson = new JSONObject();
                array.put(addJson);
                addJson.put("Country", this.country);
                addJson.put("Label", "Phone_Other");
                addJson.put("Name", this.phoneNumber);
                addJson.put("Searchable", true);
                addJson.put("VerifyLanguage", Locale.getDefault().toString());
                if (this.viaVoiceCall) {
                    addJson.put("VerifyMethod", "VOICE");
                } else {
                    addJson.put("VerifyMethod", "SMS");
                }
                JSONArray applications = new JSONArray();
                addJson.put("AddSearchableApplications", applications);
                JSONObject application = new JSONObject();
                applications.put(application);
                application.put("Name", "XBOX");
                return array;
            } catch (JSONException e) {
                XLEAssert.assertTrue("Failed to create JSON object - " + e.getMessage(), false);
                return null;
            }
        }

        private JSONArray getEditMessageContent() {
            try {
                JSONArray array = new JSONArray();
                JSONObject addJson = new JSONObject();
                array.put(addJson);
                addJson.put("Country", this.country);
                addJson.put("Name", this.phoneNumber);
                addJson.put("Searchable", true);
                addJson.put("VerifyLanguage", Locale.getDefault().toString());
                if (this.viaVoiceCall) {
                    addJson.put("VerifyMethod", "VOICE");
                } else {
                    addJson.put("VerifyMethod", "SMS");
                }
                JSONArray applications = new JSONArray();
                addJson.put("AddSearchableApplications", applications);
                JSONObject application = new JSONObject();
                applications.put(application);
                application.put("Name", "XBOX");
                return array;
            } catch (JSONException e) {
                XLEAssert.assertTrue("Failed to create JSON object - " + e.getMessage(), false);
                return null;
            }
        }

        private JSONArray getAddXboxMessageContent() {
            try {
                JSONArray array = new JSONArray();
                JSONObject addJson = new JSONObject();
                array.put(addJson);
                addJson.put("Country", this.country);
                addJson.put("Name", this.phoneNumber);
                JSONArray applications = new JSONArray();
                addJson.put("AddSearchableApplications", applications);
                JSONObject application = new JSONObject();
                applications.put(application);
                application.put("Name", "XBOX");
                return array;
            } catch (JSONException e) {
                XLEAssert.assertTrue("Failed to create JSON object - " + e.getMessage(), false);
                return null;
            }
        }

        private JSONArray getDeleteMessageContent() {
            try {
                JSONArray array = new JSONArray();
                JSONObject deleteJson = new JSONObject();
                array.put(deleteJson);
                deleteJson.put("Country", this.country);
                deleteJson.put("Name", this.phoneNumber);
                return array;
            } catch (JSONException e) {
                XLEAssert.assertTrue("Failed to create JSON object - " + e.getMessage(), false);
                return null;
            }
        }

        private JSONArray getPhoneVerificationMessageContent() {
            try {
                JSONArray array = new JSONArray();
                JSONObject editJson = new JSONObject();
                array.put(editJson);
                editJson.put("Country", this.country);
                editJson.put("Name", this.phoneNumber);
                editJson.put("Token", this.token);
                return array;
            } catch (JSONException e) {
                XLEAssert.assertTrue("Failed to create JSON object - " + e.getMessage(), false);
                return null;
            }
        }
    }

    public static class UploadPhoneContactsRequest {
        private ArrayList<PhoneContactInfo.Contact> contacts;
        private String phoneNumberNormalized;

        public UploadPhoneContactsRequest(ArrayList<PhoneContactInfo.Contact> contacts2, String myPhoneNumber) {
            this.contacts = contacts2;
            this.phoneNumberNormalized = PhoneContactInfo.sha2Encryption(myPhoneNumber);
        }

        public String toString() {
            String accountName = Settings.Secure.getString(XboxTcuiSdk.getContentResolver(), "android_id");
            try {
                JSONObject root = new JSONObject();
                JSONArray aliases = new JSONArray();
                root.put("Aliases", aliases);
                Iterator<PhoneContactInfo.Contact> it = this.contacts.iterator();
                while (it.hasNext()) {
                    PhoneContactInfo.Contact contact = it.next();
                    JSONObject contactJson = new JSONObject();
                    aliases.put(contactJson);
                    contactJson.put("Type", "phone");
                    JSONArray aliasJson = new JSONArray();
                    contactJson.put("Alias", aliasJson);
                    Iterator i$ = contact.phoneNumbers.iterator();
                    while (i$.hasNext()) {
                        aliasJson.put(PhoneContactInfo.sha2Encryption(i$.next()));
                    }
                    JSONObject contactHandleJson = new JSONObject();
                    contactJson.put("ContactHandle", contactHandleJson);
                    contactHandleJson.put("SourceId", "DCON");
                    contactHandleJson.put("ObjectId", contact.id);
                    contactHandleJson.put("AccountName", accountName + "-" + contact.displayName);
                }
                return root.toString();
            } catch (JSONException e) {
                XLEAssert.assertTrue("Failed to create JSON object - " + e.getMessage(), false);
                return null;
            }
        }
    }

    public static class UploadPhoneContactsResponse {
        private Set<String> aliases;
        public boolean isErrorResponse;

        private void foundAlias(String alias) {
            if (this.aliases == null) {
                this.aliases = new HashSet();
            }
            this.aliases.add(alias);
        }

        public Set<String> getXboxPhoneContacts() {
            return this.aliases;
        }

        public static UploadPhoneContactsResponse parseJson(String jsonStr) {
            JSONObject contactHandler;
            UploadPhoneContactsResponse response = new UploadPhoneContactsResponse();
            if (!JavaUtil.isNullOrEmpty(jsonStr)) {
                try {
                    JSONObject root = new JSONObject(jsonStr);
                    if (root.length() > 0) {
                        JSONArray aliases2 = root.getJSONArray("FoundAliases");
                        if (aliases2 != null && aliases2.length() > 0) {
                            for (int i = 0; i < aliases2.length(); i++) {
                                JSONObject aliasJson = aliases2.getJSONObject(i);
                                if (!aliasJson.isNull("ContactHandle") && (contactHandler = aliasJson.getJSONObject("ContactHandle")) != null) {
                                    String objectId = contactHandler.optString("ObjectId");
                                    if (!JavaUtil.isNullOrEmpty(objectId)) {
                                        response.foundAlias(objectId);
                                    }
                                }
                            }
                        }
                        if (!root.isNull("error")) {
                            response.isErrorResponse = true;
                        }
                    }
                } catch (JSONException e) {
                    XLEAssert.assertTrue("Failed to parse JSON string - " + e.getMessage(), false);
                }
            }
            return response;
        }
    }

    public static class PhoneInfo {
        public ArrayList<Application> addSearchableApplications;
        public String country;
        public String countryName;
        public ArrayList<Application> deleteSearchableApplications;
        public boolean hasSearchableApplications;
        public String label;
        public String name;
        public boolean searchable;
        public ArrayList<Application> searchableApplications;
        public String source;
        public String state;
        public String suggestedVerifyMethod;
        public String type;

        public PhoneState isVerified(String phoneNumber) {
            this.name = this.name.replace("+", "");
            String phoneNumber2 = phoneNumber.replace("+", "");
            PhoneState phoneState = null;
            if (this.name != null && (this.name.contains(phoneNumber2) || phoneNumber2.contains(this.name))) {
                phoneState = new PhoneState();
                phoneState.isVerified = this.state.equalsIgnoreCase("Verified");
                Iterator i$ = this.searchableApplications.iterator();
                while (i$.hasNext()) {
                    if (i$.next().name.equalsIgnoreCase("XBOX")) {
                        phoneState.hasXboxApplication = true;
                    }
                }
            }
            return phoneState;
        }

        public static PhoneInfo parseJson(JSONObject jsonObject) {
            XLEAssert.assertNotNull(jsonObject);
            PhoneInfo info = new PhoneInfo();
            boolean gotValue = false;
            try {
                if (!jsonObject.isNull("_type")) {
                    info.type = jsonObject.getString("_type");
                    gotValue = true;
                }
                if (!jsonObject.isNull("Country")) {
                    info.country = jsonObject.getString("Country");
                    gotValue = true;
                }
                if (!jsonObject.isNull("CountryName")) {
                    info.countryName = jsonObject.getString("CountryName");
                    gotValue = true;
                }
                if (!jsonObject.isNull("Label")) {
                    info.label = jsonObject.getString("Label");
                    gotValue = true;
                }
                if (!jsonObject.isNull("Source")) {
                    info.source = jsonObject.getString("Source");
                    gotValue = true;
                }
                if (!jsonObject.isNull("State")) {
                    info.state = jsonObject.getString("State");
                    gotValue = true;
                }
                if (!jsonObject.isNull("SuggestedVerifyMethod")) {
                    info.suggestedVerifyMethod = jsonObject.getString("SuggestedVerifyMethod");
                    gotValue = true;
                }
                if (!jsonObject.isNull("Name")) {
                    info.name = jsonObject.getString("Name");
                    gotValue = true;
                }
                if (!jsonObject.isNull("SearchableApplications")) {
                    JSONArray appsJson = jsonObject.getJSONArray("SearchableApplications");
                    info.searchableApplications = new ArrayList<>();
                    for (int i = 0; i < appsJson.length(); i++) {
                        JSONObject appJson = appsJson.getJSONObject(i);
                        if (appJson != null) {
                            String name2 = appJson.getString("Name");
                            if (!JavaUtil.isNullOrEmpty(name2)) {
                                Application app = new Application();
                                app.name = name2;
                                info.searchableApplications.add(app);
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                XLEAssert.assertTrue("Failed to parse JSON string - " + e.getMessage(), false);
            }
            if (!gotValue) {
                return null;
            }
            return info;
        }
    }

    public static class ErrorReturn {
        public String code;
        public int httpResult;
        public String message;
        public String phoneCountry;
        public String phoneNumber;
        public String subCode;

        public static ErrorReturn parseJson(JSONObject jsonObject) {
            XLEAssert.assertNotNull(jsonObject);
            ErrorReturn error = new ErrorReturn();
            try {
                if (!jsonObject.isNull("Code")) {
                    error.code = jsonObject.getString("Code");
                }
                if (!jsonObject.isNull("HttpResult")) {
                    error.httpResult = jsonObject.getInt("HttpResult");
                }
                if (!jsonObject.isNull("Message")) {
                    error.message = jsonObject.getString("Message");
                }
                if (!jsonObject.isNull("PhoneCountry")) {
                    error.phoneCountry = jsonObject.getString("PhoneCountry");
                }
                if (!jsonObject.isNull("PhoneNumber")) {
                    error.phoneNumber = jsonObject.getString("PhoneNumber");
                }
                if (!jsonObject.isNull("SubCode")) {
                    error.subCode = jsonObject.getString("SubCode");
                }
            } catch (JSONException e) {
                XLEAssert.assertTrue("Failed to parse JSON string - " + e.getMessage(), false);
            }
            return error;
        }
    }

    public static class PhoneInfoAttribute {
        public Integer intValue;
        public String name;
        public String strValue;
        public ArrayList<PhoneInfo> value;

        public PhoneState isVerified(String phoneNumber) {
            if (this.value != null) {
                Iterator i$ = this.value.iterator();
                while (i$.hasNext()) {
                    PhoneState phoneState = i$.next().isVerified(phoneNumber);
                    if (phoneState != null) {
                        return phoneState;
                    }
                }
            }
            return null;
        }

        public static PhoneInfoAttribute parseJson(JSONObject jsonObject) {
            XLEAssert.assertNotNull(jsonObject);
            PhoneInfoAttribute attribute = new PhoneInfoAttribute();
            try {
                if (!jsonObject.isNull("Name")) {
                    attribute.name = jsonObject.getString("Name");
                }
                if (!jsonObject.isNull("Value")) {
                    if (attribute.value == null) {
                        attribute.value = new ArrayList<>();
                    }
                    JSONArray obj = jsonObject.optJSONArray("Value");
                    if (obj != null) {
                        attribute.value = new ArrayList<>();
                        for (int i = 0; i < obj.length(); i++) {
                            PhoneInfo info = PhoneInfo.parseJson(obj.getJSONObject(i));
                            if (info != null) {
                                attribute.value.add(info);
                            }
                        }
                    } else {
                        int v = jsonObject.optInt("Value", -1);
                        if (v >= 0) {
                            attribute.intValue = Integer.valueOf(v);
                        } else {
                            String str = jsonObject.optString("Value");
                            if (str != null) {
                                attribute.strValue = str;
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                XLEAssert.assertTrue("Failed to parse JSON string - " + e.getMessage(), false);
            }
            return attribute;
        }
    }

    public static class PhoneId {
        public String cid;
        public String puid;

        public static PhoneId parseJson(JSONObject jsonObject) {
            XLEAssert.assertNotNull(jsonObject);
            PhoneId phoneId = null;
            try {
                if (jsonObject.isNull("Cid")) {
                    if (0 == 0) {
                        phoneId = new PhoneId();
                    }
                    phoneId.cid = jsonObject.getString("Cid");
                }
                PhoneId phoneId2 = phoneId;
                try {
                    if (!jsonObject.isNull("Puid")) {
                        return phoneId2;
                    }
                    if (phoneId2 == null) {
                        phoneId = new PhoneId();
                    } else {
                        phoneId = phoneId2;
                    }
                    phoneId.puid = jsonObject.getString("Puid");
                    return phoneId;
                } catch (JSONException e) {
                    e = e;
                    phoneId = phoneId2;
                    XLEAssert.assertTrue("Failed to parse JSON string - " + e.getMessage(), false);
                    return phoneId;
                }
            } catch (JSONException e2) {
                e = e2;
            }
        }
    }

    public static class PhoneInfoView {
        public ArrayList<PhoneInfoAttribute> attributes;
        public PhoneId id;

        public PhoneState isVerified(String phoneNumber) {
            if (this.attributes != null) {
                Iterator i$ = this.attributes.iterator();
                while (i$.hasNext()) {
                    PhoneState phoneState = i$.next().isVerified(phoneNumber);
                    if (phoneState != null) {
                        return phoneState;
                    }
                }
            }
            return null;
        }

        public static PhoneInfoView parseJson(JSONObject jsonObject) {
            XLEAssert.assertNotNull(jsonObject);
            PhoneInfoView view = null;
            try {
                if (!jsonObject.isNull("Id")) {
                    if (0 == 0) {
                        view = new PhoneInfoView();
                    }
                    view.id = PhoneId.parseJson(jsonObject.getJSONObject("Id"));
                }
                PhoneInfoView view2 = view;
                try {
                    if (jsonObject.isNull("Attributes")) {
                        return view2;
                    }
                    if (view2 == null) {
                        view = new PhoneInfoView();
                    } else {
                        view = view2;
                    }
                    JSONArray attributesJson = jsonObject.getJSONArray("Attributes");
                    view.attributes = new ArrayList<>();
                    for (int i = 0; i < attributesJson.length(); i++) {
                        PhoneInfoAttribute attribute = PhoneInfoAttribute.parseJson(attributesJson.getJSONObject(i));
                        if (attribute != null) {
                            view.attributes.add(attribute);
                        }
                    }
                    return view;
                } catch (JSONException e) {
                    e = e;
                    view = view2;
                    XLEAssert.assertTrue("Failed to parse JSON string - " + e.getMessage(), false);
                    return view;
                }
            } catch (JSONException e2) {
                e = e2;
                XLEAssert.assertTrue("Failed to parse JSON string - " + e.getMessage(), false);
                return view;
            }
        }
    }

    public static class ShortCircuitProfileResponse {
        public ErrorReturn error;
        public ArrayList<PhoneInfoView> views;

        public PhoneState isVerified(String phoneNumber) {
            if (this.views != null) {
                Iterator i$ = this.views.iterator();
                while (i$.hasNext()) {
                    PhoneState phoneState = i$.next().isVerified(phoneNumber);
                    if (phoneState != null) {
                        return phoneState;
                    }
                }
            }
            return null;
        }

        public String getXboxNumber() {
            if (this.views != null) {
                Iterator<PhoneInfoView> it = this.views.iterator();
                while (it.hasNext()) {
                    PhoneInfoView infoView = it.next();
                    if (infoView.attributes != null) {
                        Iterator<PhoneInfoAttribute> it2 = infoView.attributes.iterator();
                        while (it2.hasNext()) {
                            PhoneInfoAttribute attribute = it2.next();
                            if (attribute.value != null) {
                                Iterator<PhoneInfo> it3 = attribute.value.iterator();
                                while (it3.hasNext()) {
                                    PhoneInfo info = it3.next();
                                    String phoneNumber = info.name;
                                    if (info.name != null) {
                                        Iterator i$ = info.searchableApplications.iterator();
                                        while (i$.hasNext()) {
                                            if (i$.next().name.equalsIgnoreCase("XBOX")) {
                                                return phoneNumber;
                                            }
                                        }
                                        continue;
                                    }
                                }
                                continue;
                            }
                        }
                        continue;
                    }
                }
            }
            return null;
        }

        public static ShortCircuitProfileResponse parseJson(String jsonStr) {
            ShortCircuitProfileResponse response;
            JSONArray errors;
            ShortCircuitProfileResponse response2 = new ShortCircuitProfileResponse();
            if (!JavaUtil.isNullOrEmpty(jsonStr)) {
                try {
                    JSONObject root = new JSONObject(jsonStr);
                    if (root.length() <= 0) {
                        return response2;
                    }
                    JSONArray views2 = root.getJSONArray("Views");
                    if (views2 == null || views2.length() <= 0) {
                        response = response2;
                    } else {
                        response = new ShortCircuitProfileResponse();
                        try {
                            response.views = new ArrayList<>();
                            for (int i = 0; i < views2.length(); i++) {
                                PhoneInfoView phoneInfoView = PhoneInfoView.parseJson(views2.getJSONObject(i));
                                if (phoneInfoView != null) {
                                    response.views.add(phoneInfoView);
                                }
                            }
                        } catch (JSONException e) {
                            e = e;
                            response2 = response;
                            XLEAssert.assertTrue("Failed to parse JSON string - " + e.getMessage(), false);
                            return response2;
                        }
                    }
                    if (root.isNull("Errors") || (errors = root.getJSONArray("Errors")) == null || errors.length() <= 0) {
                        response2 = response;
                    } else {
                        response2 = new ShortCircuitProfileResponse();
                        response2.error = ErrorReturn.parseJson(errors.getJSONObject(0));
                    }
                } catch (JSONException e2) {
                    e = e2;
                    XLEAssert.assertTrue("Failed to parse JSON string - " + e.getMessage(), false);
                    return response2;
                }
            }
            return response2;
        }
    }

    public static String getMessage(XLEHttpStatusAndStream stream) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream.stream, HttpURLConnectionBuilder.DEFAULT_CHARSET), 4096);
            StringBuilder sb = new StringBuilder();
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    return sb.toString();
                }
                sb.append(line + "\n");
            }
        } catch (IOException ioe) {
            XLEAssert.assertTrue("Failed to read ShortCircuitProfileMessage string - " + ioe.getMessage(), false);
            return null;
        }
    }
}
