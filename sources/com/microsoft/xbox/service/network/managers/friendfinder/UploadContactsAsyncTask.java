package com.microsoft.xbox.service.network.managers.friendfinder;

import com.microsoft.xbox.service.model.friendfinder.ShortCircuitProfileMessage;
import com.microsoft.xbox.service.network.managers.ServiceManagerFactory;
import com.microsoft.xbox.service.network.managers.friendfinder.PhoneContactInfo;
import com.microsoft.xbox.toolkit.AsyncActionStatus;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.NetworkAsyncTask;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEException;
import java.util.ArrayList;

public class UploadContactsAsyncTask extends NetworkAsyncTask<AsyncActionStatus> {
    private static final int MAX_UPLOAD_NUM_PER_REQUEST = 100;
    private UploadContactsCompleted callback;
    private String phoneNumber;

    public interface UploadContactsCompleted {
        void onResult(AsyncActionStatus asyncActionStatus);
    }

    public UploadContactsAsyncTask(UploadContactsCompleted callback2) {
        this.callback = callback2;
        if (!JavaUtil.isNullOrEmpty(PhoneContactInfo.getInstance().getProfileNumber())) {
            this.phoneNumber = PhoneContactInfo.getInstance().getProfileNumber();
        } else if (!JavaUtil.isNullOrEmpty(PhoneContactInfo.getInstance().getUserEnteredNumber())) {
            this.phoneNumber = PhoneContactInfo.getInstance().getUserEnteredNumber();
        } else if (!JavaUtil.isNullOrEmpty(PhoneContactInfo.getInstance().getPhoneNumberFromSim())) {
            this.phoneNumber = PhoneContactInfo.getInstance().getPhoneNumberFromSim();
        }
    }

    /* access modifiers changed from: protected */
    public boolean checkShouldExecute() {
        return true;
    }

    /* access modifiers changed from: protected */
    public void onNoAction() {
    }

    /* access modifiers changed from: protected */
    public AsyncActionStatus onError() {
        return AsyncActionStatus.FAIL;
    }

    /* access modifiers changed from: protected */
    public AsyncActionStatus loadDataInBackground() {
        try {
            if (JavaUtil.isNullOrEmpty(this.phoneNumber)) {
                ShortCircuitProfileMessage.ShortCircuitProfileResponse profile = ServiceManagerFactory.getInstance().getSLSServiceManager().getMyShortCircuitProfile();
                if (profile == null) {
                    return AsyncActionStatus.FAIL;
                }
                String profileNumber = profile.getXboxNumber();
                if (JavaUtil.isNullOrEmpty(profileNumber)) {
                    return AsyncActionStatus.FAIL;
                }
                PhoneContactInfo.getInstance().setProfileNumber(profileNumber);
                this.phoneNumber = profileNumber;
            }
            if (uploadContactsSucceeded()) {
                return AsyncActionStatus.SUCCESS;
            }
        } catch (XLEException e) {
        }
        return AsyncActionStatus.FAIL;
    }

    private boolean uploadContactsSucceeded() throws XLEException {
        ArrayList<PhoneContactInfo.Contact> contacts = PhoneContactInfo.getInstance().getContacts();
        if (contacts == null) {
            return false;
        }
        if (contacts.size() == 0) {
            return true;
        }
        if (contacts.size() > 100) {
            return batchUploadContacts(contacts);
        }
        return uploadContacts(contacts);
    }

    private boolean batchUploadContacts(ArrayList<PhoneContactInfo.Contact> contacts) throws XLEException {
        boolean z;
        XLEAssert.assertNotNull(contacts);
        if (contacts.size() > 100) {
            z = true;
        } else {
            z = false;
        }
        XLEAssert.assertTrue(z);
        int startUploadIndex = 0;
        boolean moreContactsToUpload = true;
        while (moreContactsToUpload) {
            int endUploadIndex = startUploadIndex + 100;
            if (endUploadIndex >= contacts.size()) {
                endUploadIndex = contacts.size();
                moreContactsToUpload = false;
            }
            if (!uploadContacts(new ArrayList(contacts.subList(startUploadIndex, endUploadIndex)))) {
                return false;
            }
            startUploadIndex = endUploadIndex;
        }
        return true;
    }

    private boolean uploadContacts(ArrayList<PhoneContactInfo.Contact> contacts) throws XLEException {
        boolean z;
        XLEAssert.assertNotNull(contacts);
        if (contacts.size() <= 0 || contacts.size() > 100) {
            z = false;
        } else {
            z = true;
        }
        XLEAssert.assertTrue(z);
        ShortCircuitProfileMessage.UploadPhoneContactsResponse uploadContactsResponse = ServiceManagerFactory.getInstance().getSLSServiceManager().updatePhoneContacts(new ShortCircuitProfileMessage.UploadPhoneContactsRequest(contacts, this.phoneNumber));
        if (uploadContactsResponse == null || uploadContactsResponse.isErrorResponse) {
            return false;
        }
        PhoneContactInfo.getInstance().updateXboxContacts(uploadContactsResponse.getXboxPhoneContacts());
        return true;
    }

    /* access modifiers changed from: protected */
    public void onPreExecute() {
    }

    /* access modifiers changed from: protected */
    public void onPostExecute(AsyncActionStatus status) {
        if (this.callback != null) {
            this.callback.onResult(status);
        }
    }
}
