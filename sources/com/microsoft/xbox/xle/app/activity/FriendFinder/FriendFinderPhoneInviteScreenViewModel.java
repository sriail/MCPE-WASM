package com.microsoft.xbox.xle.app.activity.FriendFinder;

import android.content.Intent;
import android.net.Uri;
import com.microsoft.xbox.service.model.friendfinder.FriendFinderType;
import com.microsoft.xbox.service.network.managers.friendfinder.PhoneContactInfo;
import com.microsoft.xbox.toolkit.AsyncActionStatus;
import com.microsoft.xbox.toolkit.NetworkAsyncTask;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.ui.ActivityParameters;
import com.microsoft.xbox.toolkit.ui.NavigationManager;
import com.microsoft.xbox.toolkit.ui.ScreenLayout;
import com.microsoft.xbox.xle.app.adapter.FriendFinderPhoneInviteScreenAdapater;
import com.microsoft.xbox.xle.telemetry.helpers.UTCFriendFinder;
import com.microsoft.xbox.xle.viewmodel.ViewModelBase;
import com.microsoft.xboxtcui.R;
import com.microsoft.xboxtcui.XboxTcuiSdk;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

public class FriendFinderPhoneInviteScreenViewModel extends ViewModelBase {
    private Comparator<PhoneContactInfo.Contact> contactComparator = new Comparator<PhoneContactInfo.Contact>() {
        public int compare(PhoneContactInfo.Contact lhs, PhoneContactInfo.Contact rhs) {
            return lhs.displayName.compareTo(rhs.displayName);
        }
    };
    private ArrayList<PhoneContactInfo.Contact> contactsList = new ArrayList<>();
    /* access modifiers changed from: private */
    public boolean isUploadingContacts;
    private UploadContactsAsyncTask uploadContactsAsyncTask;

    public FriendFinderPhoneInviteScreenViewModel(ScreenLayout screenLayout) {
        super(screenLayout);
        XLEAssert.fail("This isn't supported yet.");
        this.adapter = new FriendFinderPhoneInviteScreenAdapater(this);
    }

    public ArrayList<PhoneContactInfo.Contact> getContacts() {
        return this.contactsList;
    }

    public void addContacts(ArrayList<Integer> contactsToInvite) {
        StringBuffer phoneNumbers = new StringBuffer();
        Iterator i$ = contactsToInvite.iterator();
        while (i$.hasNext()) {
            Integer i = i$.next();
            if (phoneNumbers.length() > 0) {
                phoneNumbers.append(',');
            }
            phoneNumbers.append(this.contactsList.get(i.intValue()).phoneNumbers.get(0));
        }
        Intent smsIntent = new Intent("android.intent.action.SENDTO");
        smsIntent.setData(Uri.parse("smsto:" + Uri.encode(phoneNumbers.toString())));
        smsIntent.putExtra("sms_body", XboxTcuiSdk.getResources().getString(R.string.FriendFinder_PhoneInviteFriends_Message));
        smsIntent.putExtra("address", phoneNumbers.toString());
        if (!XboxTcuiSdk.getActivity().getPackageManager().queryIntentActivities(smsIntent, 0).isEmpty()) {
            XboxTcuiSdk.getActivity().startActivity(smsIntent);
        }
        ActivityParameters params = new ActivityParameters();
        params.putFriendFinderDone(true);
        try {
            NavigationManager.getInstance().PushScreen(FriendFinderHomeScreen.class, params);
        } catch (XLEException e) {
        }
    }

    /* access modifiers changed from: protected */
    public void onStartOverride() {
    }

    public void onRehydrate() {
        this.adapter = new FriendFinderPhoneInviteScreenAdapater(this);
    }

    /* access modifiers changed from: protected */
    public void onStopOverride() {
        cancelActiveTasks();
    }

    private void cancelActiveTasks() {
        if (this.uploadContactsAsyncTask != null) {
            this.uploadContactsAsyncTask.cancel();
            this.uploadContactsAsyncTask = null;
        }
    }

    public boolean isBusy() {
        return this.isUploadingContacts;
    }

    public void load(boolean forceRefresh) {
        cancelActiveTasks();
        if (PhoneContactInfo.getInstance().isXboxContactsUpdated()) {
            this.contactsList = PhoneContactInfo.getInstance().getContacts();
            Collections.sort(this.contactsList, this.contactComparator);
            return;
        }
        this.uploadContactsAsyncTask = new UploadContactsAsyncTask();
        this.uploadContactsAsyncTask.load(true);
    }

    public boolean onBackButtonPressed() {
        UTCFriendFinder.trackBackButtonPressed(getScreen().getName(), FriendFinderType.PHONE);
        return super.onBackButtonPressed();
    }

    /* access modifiers changed from: private */
    public void onUploadContactsTaskCompleted(AsyncActionStatus status) {
        this.isUploadingContacts = false;
        this.contactsList = PhoneContactInfo.getInstance().getContacts();
        Collections.sort(this.contactsList, this.contactComparator);
        updateAdapter();
    }

    private class UploadContactsAsyncTask extends NetworkAsyncTask<AsyncActionStatus> {
        private UploadContactsAsyncTask() {
        }

        /* access modifiers changed from: protected */
        public boolean checkShouldExecute() {
            return false;
        }

        /* access modifiers changed from: protected */
        public void onNoAction() {
            FriendFinderPhoneInviteScreenViewModel.this.onUploadContactsTaskCompleted(AsyncActionStatus.NO_CHANGE);
        }

        /* access modifiers changed from: protected */
        public AsyncActionStatus onError() {
            return null;
        }

        /* access modifiers changed from: protected */
        public AsyncActionStatus loadDataInBackground() {
            return AsyncActionStatus.SUCCESS;
        }

        /* access modifiers changed from: protected */
        public void onPreExecute() {
            boolean unused = FriendFinderPhoneInviteScreenViewModel.this.isUploadingContacts = true;
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(AsyncActionStatus status) {
            FriendFinderPhoneInviteScreenViewModel.this.onUploadContactsTaskCompleted(status);
        }
    }
}
