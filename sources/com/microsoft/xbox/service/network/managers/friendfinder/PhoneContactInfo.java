package com.microsoft.xbox.service.network.managers.friendfinder;

import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Base64;
import com.microsoft.xbox.service.model.XPrivilegeConstants;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.xle.app.XLEUtil;
import com.microsoft.xboxtcui.XboxTcuiSdk;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Set;
import net.hockeyapp.android.utils.HttpURLConnectionBuilder;

public class PhoneContactInfo {
    public static final int MinimumPhoneLength = 7;
    private static PhoneContactInfo instance = new PhoneContactInfo();
    private ArrayList<Contact> contacts;
    private final String[][] countryCodes = {new String[]{"93", "AF", ""}, new String[]{"355", "AL", ""}, new String[]{"213", "DZ", ""}, new String[]{"376", "AD", ""}, new String[]{"244", "AO", ""}, new String[]{"672", "AQ", ""}, new String[]{"54", "AR", ""}, new String[]{"374", "AM", ""}, new String[]{"297", "AW", ""}, new String[]{"61", "AU", ""}, new String[]{"43", "AT", ""}, new String[]{"994", "AZ", ""}, new String[]{"973", "BH", ""}, new String[]{"880", "BD", ""}, new String[]{"375", "BY", ""}, new String[]{"32", "BE", ""}, new String[]{"501", "BZ", ""}, new String[]{"229", "BJ", ""}, new String[]{"975", "BT", ""}, new String[]{"591", "BO", ""}, new String[]{"387", "BA", ""}, new String[]{"267", "BW", ""}, new String[]{"55", "BR", ""}, new String[]{"673", "BN", ""}, new String[]{"359", "BG", ""}, new String[]{"226", "BF", ""}, new String[]{"95", "MM", ""}, new String[]{"257", "BI", ""}, new String[]{"855", "KH", ""}, new String[]{"237", "CM", ""}, new String[]{"1", "CA", ""}, new String[]{"238", "CV", ""}, new String[]{"236", "CF", ""}, new String[]{"235", "TD", ""}, new String[]{"56", "CL", ""}, new String[]{"86", "CN", ""}, new String[]{"61", "CX", ""}, new String[]{"61", "CC", ""}, new String[]{"57", "CO", ""}, new String[]{"269", "KM", ""}, new String[]{"242", "CG", ""}, new String[]{"243", "CD", ""}, new String[]{"682", "CK", ""}, new String[]{"506", "CR", ""}, new String[]{"385", "HR", ""}, new String[]{"53", "CU", ""}, new String[]{"357", "CY", ""}, new String[]{"420", "CZ", ""}, new String[]{"45", "DK", ""}, new String[]{"253", "DJ", ""}, new String[]{"670", "TL", ""}, new String[]{"593", "EC", ""}, new String[]{"20", "EG", ""}, new String[]{"503", "SV", ""}, new String[]{"240", "GQ", ""}, new String[]{"291", "ER", ""}, new String[]{"372", "EE", ""}, new String[]{"251", "ET", ""}, new String[]{"500", "FK", ""}, new String[]{"298", "FO", ""}, new String[]{"679", "FJ", ""}, new String[]{"358", "FI", ""}, new String[]{"33", "FR", ""}, new String[]{"689", "PF", ""}, new String[]{"241", "GA", ""}, new String[]{"220", "GM", ""}, new String[]{"995", "GE", ""}, new String[]{"49", "DE", ""}, new String[]{"233", "GH", ""}, new String[]{"350", "GI", ""}, new String[]{"30", "GR", ""}, new String[]{"299", "GL", ""}, new String[]{"502", "GT", ""}, new String[]{"224", "GN", ""}, new String[]{XPrivilegeConstants.XPRIVILEGE_PURCHASE_CONTENT, "GW", ""}, new String[]{"592", "GY", ""}, new String[]{"509", "HT", ""}, new String[]{"504", "HN", ""}, new String[]{"852", "HK", ""}, new String[]{"36", "HU", ""}, new String[]{"91", "IN", ""}, new String[]{"62", "ID", ""}, new String[]{"98", "IR", ""}, new String[]{"964", "IQ", ""}, new String[]{"353", "IE", ""}, new String[]{"44", "IM", ""}, new String[]{"972", "IL", ""}, new String[]{"39", "IT", ""}, new String[]{"225", "CI", ""}, new String[]{"81", "JP", ""}, new String[]{"962", "JO", ""}, new String[]{"7", "KZ", ""}, new String[]{"254", "KE", ""}, new String[]{"686", "KI", ""}, new String[]{"965", "KW", ""}, new String[]{"996", "KG", ""}, new String[]{"856", "LA", ""}, new String[]{"371", "LV", ""}, new String[]{"961", "LB", ""}, new String[]{"266", "LS", ""}, new String[]{"231", "LR", ""}, new String[]{"218", "LY", ""}, new String[]{"423", "LI", ""}, new String[]{"370", "LT", ""}, new String[]{"352", "LU", ""}, new String[]{"853", "MO", ""}, new String[]{"389", "MK", ""}, new String[]{"261", "MG", ""}, new String[]{"265", "MW", ""}, new String[]{"60", "MY", ""}, new String[]{"960", "MV", ""}, new String[]{"223", "ML", ""}, new String[]{"356", "MT", ""}, new String[]{"692", "MH", ""}, new String[]{"222", "MR", ""}, new String[]{"230", "MU", ""}, new String[]{"262", "YT", ""}, new String[]{"52", "MX", ""}, new String[]{"691", "FM", ""}, new String[]{"373", "MD", ""}, new String[]{"377", "MC", ""}, new String[]{"976", "MN", ""}, new String[]{"382", "ME", ""}, new String[]{"212", "MA", ""}, new String[]{"258", "MZ", ""}, new String[]{"264", "NA", ""}, new String[]{"674", "NR", ""}, new String[]{"977", "NP", ""}, new String[]{"31", "NL", ""}, new String[]{"599", "AN", ""}, new String[]{"687", "NC", ""}, new String[]{"64", "NZ", ""}, new String[]{"505", "NI", ""}, new String[]{"227", "NE", ""}, new String[]{"234", "NG", ""}, new String[]{"683", "NU", ""}, new String[]{"850", "KP", ""}, new String[]{"47", "NO", ""}, new String[]{"968", "OM", ""}, new String[]{"92", "PK", ""}, new String[]{"680", "PW", ""}, new String[]{"507", "PA", ""}, new String[]{"675", "PG", ""}, new String[]{"595", "PY", ""}, new String[]{"51", "PE", ""}, new String[]{"63", "PH", ""}, new String[]{"870", "PN", ""}, new String[]{"48", "PL", ""}, new String[]{"351", "PT", ""}, new String[]{"1", "PR", ""}, new String[]{"974", "QA", ""}, new String[]{"40", "RO", ""}, new String[]{"7", "RU", ""}, new String[]{"250", "RW", ""}, new String[]{"590", "BL", ""}, new String[]{"685", "WS", ""}, new String[]{"378", "SM", ""}, new String[]{"239", "ST", ""}, new String[]{"966", "SA", ""}, new String[]{XPrivilegeConstants.XPRIVILEGE_PII_ACCESS, "SN", ""}, new String[]{"381", "RS", ""}, new String[]{"248", "SC", ""}, new String[]{"232", "SL", ""}, new String[]{"65", "SG", ""}, new String[]{"421", "SK", ""}, new String[]{"386", "SI", ""}, new String[]{"677", "SB", ""}, new String[]{XPrivilegeConstants.XPRIVILEGE_COMMUNICATIONS, "SO", ""}, new String[]{"27", "ZA", ""}, new String[]{"82", "KR", ""}, new String[]{"34", "ES", ""}, new String[]{"94", "LK", ""}, new String[]{"290", "SH", ""}, new String[]{"508", "PM", ""}, new String[]{XPrivilegeConstants.XPRIVILEGE_PROFILE_VIEWING, "SD", ""}, new String[]{"597", "SR", ""}, new String[]{"268", "SZ", ""}, new String[]{"46", "SE", ""}, new String[]{"41", "CH", ""}, new String[]{"963", "SY", ""}, new String[]{"886", "TW", ""}, new String[]{"992", "TJ", ""}, new String[]{XPrivilegeConstants.XPRIVILEGE_ADD_FRIEND, "TZ", ""}, new String[]{"66", "TH", ""}, new String[]{"228", "TG", ""}, new String[]{"690", "TK", ""}, new String[]{"676", "TO", ""}, new String[]{"216", "TN", ""}, new String[]{"90", "TR", ""}, new String[]{"993", "TM", ""}, new String[]{"688", "TV", ""}, new String[]{"971", "AE", ""}, new String[]{"256", "UG", ""}, new String[]{"44", "GB", ""}, new String[]{"380", "UA", ""}, new String[]{"598", "UY", ""}, new String[]{"1", "US", ""}, new String[]{"998", "UZ", ""}, new String[]{"678", "VU", ""}, new String[]{"39", "VA", ""}, new String[]{"58", "VE", ""}, new String[]{"84", "VN", ""}, new String[]{"681", "WF", ""}, new String[]{"967", "YE", ""}, new String[]{"260", "ZM", ""}, new String[]{"263", "ZW", ""}};
    private boolean isXboxContactsUpdated;
    private String phoneNumberFromSim;
    private String profilePhoneNumber;
    private String region;
    private String userEnteredNumber;

    public class Contact {
        public String displayName;
        public String id;
        public boolean isOnXbox;
        public boolean isSelected;
        public ArrayList<String> phoneNumbers;

        public Contact(String id2, String displayName2) {
            this.id = id2;
            this.displayName = displayName2;
        }

        public void addPhoneNumber(String number) {
            if (this.phoneNumbers == null) {
                this.phoneNumbers = new ArrayList<>();
            }
            this.phoneNumbers.add(number);
        }
    }

    private PhoneContactInfo() {
        for (int i = 0; i < this.countryCodes.length; i++) {
            String countryName = new Locale("", this.countryCodes[i][1]).getDisplayCountry();
            XLEAssert.assertFalse("Failed to get country name : " + this.countryCodes[i][1], JavaUtil.isNullOrEmpty(countryName));
            this.countryCodes[i][2] = countryName;
        }
    }

    public static PhoneContactInfo getInstance() {
        return instance;
    }

    public boolean isXboxContactsUpdated() {
        return this.isXboxContactsUpdated;
    }

    public static String normalizePhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.length() < 7) {
            return null;
        }
        String phoneNumber2 = phoneNumber.toLowerCase();
        if (phoneNumber2.indexOf("ext") >= 0 || phoneNumber2.indexOf("x") >= 0) {
            return null;
        }
        StringBuffer sb = new StringBuffer(phoneNumber2.length());
        for (int i = 0; i < phoneNumber2.length(); i++) {
            char c = phoneNumber2.charAt(i);
            if (Character.isDigit(c)) {
                sb.append(c);
            }
        }
        if (sb.length() >= 7) {
            return sb.toString();
        }
        return null;
    }

    public static String sha2Encryption(String msg) {
        if (JavaUtil.isNullOrEmpty(msg)) {
            return msg;
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.reset();
            byte[] byteData = digest.digest(msg.getBytes(HttpURLConnectionBuilder.DEFAULT_CHARSET));
            return Base64.encodeToString(byteData, 0, byteData.length, 10);
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
            return null;
        }
    }

    public String getCountryCode() {
        return getContryCodeFromRegion(getRegion());
    }

    public String getPhoneNumberFromSim() {
        if (this.phoneNumberFromSim == null) {
            try {
                String phoneNumber = ((TelephonyManager) XboxTcuiSdk.getSystemService("phone")).getLine1Number();
                String region2 = getRegion();
                if (!JavaUtil.isNullOrEmpty(phoneNumber) && !JavaUtil.isNullOrEmpty(region2)) {
                    String countryCode = getCountryCode();
                    if (phoneNumber.startsWith(countryCode)) {
                        this.region = region2;
                        this.phoneNumberFromSim = phoneNumber.substring(countryCode.length());
                    }
                }
            } catch (SecurityException e) {
                this.phoneNumberFromSim = "";
            }
        }
        return this.phoneNumberFromSim;
    }

    public void setUserEnteredNumber(String number) {
        this.userEnteredNumber = number;
    }

    public String getUserEnteredNumber() {
        return this.userEnteredNumber;
    }

    public void setProfileNumber(String number) {
        this.profilePhoneNumber = number;
    }

    public String getProfileNumber() {
        return this.profilePhoneNumber;
    }

    public String getRegionWithCode() {
        String region2 = getInstance().getRegion();
        String code = getInstance().getCountryCode();
        if (JavaUtil.isNullOrEmpty(region2) || JavaUtil.isNullOrEmpty(code)) {
            return null;
        }
        return region2 + "-" + code;
    }

    public String getRegion() {
        if (this.region == null) {
            this.region = ((TelephonyManager) XboxTcuiSdk.getSystemService("phone")).getSimCountryIso().toUpperCase();
        }
        if (JavaUtil.isNullOrEmpty(this.region)) {
            this.region = Locale.getDefault().getCountry();
        }
        return this.region;
    }

    /* JADX WARNING: CFG modification limit reached, blocks count: 141 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.util.ArrayList<com.microsoft.xbox.service.network.managers.friendfinder.PhoneContactInfo.Contact> getContacts() {
        /*
            r15 = this;
            r14 = 0
            java.util.ArrayList<com.microsoft.xbox.service.network.managers.friendfinder.PhoneContactInfo$Contact> r1 = r15.contacts
            if (r1 != 0) goto L_0x00c5
            android.content.ContentResolver r0 = com.microsoft.xboxtcui.XboxTcuiSdk.getContentResolver()     // Catch:{ SecurityException -> 0x00be }
            android.net.Uri r1 = android.provider.ContactsContract.Contacts.CONTENT_URI     // Catch:{ SecurityException -> 0x00be }
            r2 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            android.database.Cursor r7 = r0.query(r1, r2, r3, r4, r5)     // Catch:{ SecurityException -> 0x00be }
            if (r7 != 0) goto L_0x0034
            r1 = r14
        L_0x0016:
            return r1
        L_0x0017:
            if (r6 == 0) goto L_0x0031
            java.util.ArrayList<java.lang.String> r1 = r6.phoneNumbers     // Catch:{ SecurityException -> 0x00be }
            boolean r1 = com.microsoft.xbox.xle.app.XLEUtil.isNullOrEmpty(r1)     // Catch:{ SecurityException -> 0x00be }
            if (r1 != 0) goto L_0x0031
            java.util.ArrayList<com.microsoft.xbox.service.network.managers.friendfinder.PhoneContactInfo$Contact> r1 = r15.contacts     // Catch:{ SecurityException -> 0x00be }
            if (r1 != 0) goto L_0x002c
            java.util.ArrayList r1 = new java.util.ArrayList     // Catch:{ SecurityException -> 0x00be }
            r1.<init>()     // Catch:{ SecurityException -> 0x00be }
            r15.contacts = r1     // Catch:{ SecurityException -> 0x00be }
        L_0x002c:
            java.util.ArrayList<com.microsoft.xbox.service.network.managers.friendfinder.PhoneContactInfo$Contact> r1 = r15.contacts     // Catch:{ SecurityException -> 0x00be }
            r1.add(r6)     // Catch:{ SecurityException -> 0x00be }
        L_0x0031:
            r12.close()     // Catch:{ SecurityException -> 0x00be }
        L_0x0034:
            boolean r1 = r7.moveToNext()     // Catch:{ SecurityException -> 0x00be }
            if (r1 == 0) goto L_0x00c2
            java.lang.String r1 = "_id"
            int r1 = r7.getColumnIndex(r1)     // Catch:{ SecurityException -> 0x00be }
            java.lang.String r10 = r7.getString(r1)     // Catch:{ SecurityException -> 0x00be }
            java.lang.String r1 = "display_name"
            int r1 = r7.getColumnIndex(r1)     // Catch:{ SecurityException -> 0x00be }
            java.lang.String r11 = r7.getString(r1)     // Catch:{ SecurityException -> 0x00be }
            java.lang.String r1 = "has_phone_number"
            int r1 = r7.getColumnIndex(r1)     // Catch:{ SecurityException -> 0x00be }
            java.lang.String r1 = r7.getString(r1)     // Catch:{ SecurityException -> 0x00be }
            int r1 = java.lang.Integer.parseInt(r1)     // Catch:{ SecurityException -> 0x00be }
            if (r1 <= 0) goto L_0x0034
            android.net.Uri r1 = android.provider.ContactsContract.CommonDataKinds.Phone.CONTENT_URI     // Catch:{ SecurityException -> 0x00be }
            r2 = 0
            java.lang.String r3 = "contact_id = ?"
            r4 = 1
            java.lang.String[] r4 = new java.lang.String[r4]     // Catch:{ SecurityException -> 0x00be }
            r5 = 0
            r4[r5] = r10     // Catch:{ SecurityException -> 0x00be }
            r5 = 0
            android.database.Cursor r12 = r0.query(r1, r2, r3, r4, r5)     // Catch:{ SecurityException -> 0x00be }
            r6 = 0
            java.lang.String r8 = r15.getCountryCode()     // Catch:{ SecurityException -> 0x00be }
        L_0x0073:
            boolean r1 = r12.moveToNext()     // Catch:{ SecurityException -> 0x00be }
            if (r1 == 0) goto L_0x0017
            java.lang.String r1 = "data1"
            int r1 = r12.getColumnIndex(r1)     // Catch:{ SecurityException -> 0x00be }
            java.lang.String r13 = r12.getString(r1)     // Catch:{ SecurityException -> 0x00be }
            boolean r1 = com.microsoft.xbox.toolkit.JavaUtil.isNullOrEmpty(r13)     // Catch:{ SecurityException -> 0x00be }
            if (r1 != 0) goto L_0x0073
            java.lang.String r13 = normalizePhoneNumber(r13)     // Catch:{ SecurityException -> 0x00be }
            boolean r1 = com.microsoft.xbox.toolkit.JavaUtil.isNullOrEmpty(r13)     // Catch:{ SecurityException -> 0x00be }
            if (r1 != 0) goto L_0x0073
            if (r6 != 0) goto L_0x009a
            com.microsoft.xbox.service.network.managers.friendfinder.PhoneContactInfo$Contact r6 = new com.microsoft.xbox.service.network.managers.friendfinder.PhoneContactInfo$Contact     // Catch:{ SecurityException -> 0x00be }
            r6.<init>(r10, r11)     // Catch:{ SecurityException -> 0x00be }
        L_0x009a:
            r6.addPhoneNumber(r13)     // Catch:{ SecurityException -> 0x00be }
            boolean r1 = com.microsoft.xbox.toolkit.JavaUtil.isNullOrEmpty(r8)     // Catch:{ SecurityException -> 0x00be }
            if (r1 != 0) goto L_0x0073
            boolean r1 = r13.startsWith(r8)     // Catch:{ SecurityException -> 0x00be }
            if (r1 != 0) goto L_0x0073
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ SecurityException -> 0x00be }
            r1.<init>()     // Catch:{ SecurityException -> 0x00be }
            java.lang.StringBuilder r1 = r1.append(r8)     // Catch:{ SecurityException -> 0x00be }
            java.lang.StringBuilder r1 = r1.append(r13)     // Catch:{ SecurityException -> 0x00be }
            java.lang.String r1 = r1.toString()     // Catch:{ SecurityException -> 0x00be }
            r6.addPhoneNumber(r1)     // Catch:{ SecurityException -> 0x00be }
            goto L_0x0073
        L_0x00be:
            r9 = move-exception
            r1 = r14
            goto L_0x0016
        L_0x00c2:
            r7.close()     // Catch:{ SecurityException -> 0x00be }
        L_0x00c5:
            java.util.ArrayList<com.microsoft.xbox.service.network.managers.friendfinder.PhoneContactInfo$Contact> r1 = r15.contacts
            goto L_0x0016
        */
        throw new UnsupportedOperationException("Method not decompiled: com.microsoft.xbox.service.network.managers.friendfinder.PhoneContactInfo.getContacts():java.util.ArrayList");
    }

    public void updateXboxContacts(Set<String> aliases) {
        this.isXboxContactsUpdated = true;
        if (!XLEUtil.isNullOrEmpty(aliases)) {
            Enumeration iter = Collections.enumeration(this.contacts);
            while (iter.hasMoreElements() && !aliases.isEmpty()) {
                Contact contact = (Contact) iter.nextElement();
                if (aliases.contains(contact.id)) {
                    aliases.remove(contact.id);
                    contact.isOnXbox = true;
                }
            }
        }
    }

    public ArrayList<String> getCountryNames() {
        ArrayList<String> countries = new ArrayList<>();
        for (String[] strArr : this.countryCodes) {
            countries.add(strArr[2]);
        }
        Collections.sort(countries);
        return countries;
    }

    public String getRegionFromCountryName(String countryName) {
        for (int i = 0; i < this.countryCodes.length; i++) {
            if (TextUtils.equals(countryName, this.countryCodes[i][2])) {
                return this.countryCodes[i][1];
            }
        }
        return null;
    }

    public String getContryCodeFromRegion(String region2) {
        for (int i = 0; i < this.countryCodes.length; i++) {
            if (TextUtils.equals(region2, this.countryCodes[i][1])) {
                return this.countryCodes[i][0];
            }
        }
        return null;
    }

    public String getCountryNameFromRegion(String region2) {
        for (int i = 0; i < this.countryCodes.length; i++) {
            if (TextUtils.equals(region2, this.countryCodes[i][1])) {
                return this.countryCodes[i][2];
            }
        }
        return null;
    }
}
