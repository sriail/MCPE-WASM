package com.microsoft.onlineid.internal.storage;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import com.microsoft.onlineid.ISecurityScope;
import com.microsoft.onlineid.Ticket;
import com.microsoft.onlineid.analytics.ClientAnalytics;
import com.microsoft.onlineid.analytics.IClientAnalytics;
import com.microsoft.onlineid.internal.Assertion;
import com.microsoft.onlineid.internal.Objects;
import com.microsoft.onlineid.internal.Strings;
import com.microsoft.onlineid.internal.log.Logger;
import com.microsoft.onlineid.internal.sso.BundleMarshaller;
import com.microsoft.onlineid.internal.sso.BundleMarshallerException;
import com.microsoft.onlineid.internal.storage.Storage;
import com.microsoft.onlineid.sts.AuthenticatorUserAccount;
import com.microsoft.onlineid.sts.DeviceIdentity;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class TypedStorage {
    protected static final String AccountKeyToken = "Account";
    protected static final String AccountsCollectionKey = "Accounts";
    protected static final String ClockSkewKey = "ClockSkew";
    protected static final Object CollectionLock = new Object();
    protected static final String ConfigLastDownloadedTimeKey = "ConfigLastDownloadedTime";
    protected static final String DeviceBasedFlightsKey = "DeviceBasedFlights";
    protected static final String DeviceFlightOverrideKey = "DeviceFlightOverride";
    protected static final String DeviceIdentityKey = "Device";
    protected static final String FormatSeparator = "|";
    protected static final String LastBackupPushedTimeKey = "LastBackupPushedTime";
    protected static final String LastBackupReceivedTimeKey = "LastBackupReceivedTime";
    protected static final String SdkVersionKey = "SdkVersion";
    protected static final String TicketCollectionKeyToken = "Tickets";
    protected static final String TicketKeyToken = "Ticket";
    protected final Storage _storage;

    public TypedStorage(Context applicationContext) {
        Objects.verifyArgumentNotNull(applicationContext, "applicationContext");
        this._storage = new Storage(applicationContext);
    }

    protected TypedStorage(Storage storage) {
        this._storage = storage;
    }

    public DeviceIdentity readDeviceIdentity() {
        return (DeviceIdentity) this._storage.readObject(DeviceIdentityKey, getDeviceIdentitySerializer());
    }

    public void writeDeviceIdentity(DeviceIdentity identity) {
        this._storage.edit().writeObject(DeviceIdentityKey, identity, getDeviceIdentitySerializer()).apply();
    }

    public void deleteDeviceIdentity() {
        this._storage.edit().remove(DeviceIdentityKey).apply();
    }

    /* access modifiers changed from: protected */
    public void storeTicket(String accountID, String appID, Ticket ticket) {
        writeToCollection(constructTicketCollectionKey(accountID), constructTicketKey(accountID, appID, ticket.getScope()), ticket, getTicketSerializer());
    }

    /* access modifiers changed from: protected */
    public void removeTicket(String accountID, String appID, ISecurityScope scope) {
        removeFromCollection(constructTicketCollectionKey(accountID), constructTicketKey(accountID, appID, scope));
    }

    /* access modifiers changed from: protected */
    public void removeTickets(String accountID) {
        removeCollection(constructTicketCollectionKey(accountID));
    }

    private boolean hasTickets(String accountID) {
        return hasCollection(constructTicketCollectionKey(accountID));
    }

    /* access modifiers changed from: protected */
    public Ticket getTicket(String accountID, String appID, ISecurityScope securityScope) {
        return (Ticket) readFromCollection(constructTicketCollectionKey(accountID), constructTicketKey(accountID, appID, securityScope), getTicketSerializer());
    }

    /* access modifiers changed from: protected */
    public <T> T readFromCollection(String collectionKey, String valueKey, ISerializer<T> serializer) {
        String encoded = this._storage.readString(valueKey);
        T result = null;
        if (encoded != null) {
            try {
                result = serializer.deserialize(encoded);
                if (result == null) {
                    removeFromCollection(collectionKey, valueKey);
                }
            } catch (IOException e) {
                Logger.warning(String.format(Locale.US, "Value in storage at '%s' was corrupt.", new Object[]{valueKey}));
                if (0 == 0) {
                    removeFromCollection(collectionKey, valueKey);
                }
            } catch (Throwable th) {
                if (0 == 0) {
                    removeFromCollection(collectionKey, valueKey);
                }
                throw th;
            }
        }
        return result;
    }

    public void writeClockSkew(long skew) {
        this._storage.edit().writeLong(ClockSkewKey, skew).apply();
    }

    public long readClockSkew() {
        return this._storage.readLong(ClockSkewKey, 0);
    }

    public boolean clearSynchronous() {
        boolean commit;
        synchronized (CollectionLock) {
            commit = this._storage.edit().clear().commit();
        }
        return commit;
    }

    public AuthenticatorUserAccount readAccount(String accountPuid) {
        return (AuthenticatorUserAccount) readFromCollection(AccountsCollectionKey, constructAccountKey(accountPuid), getAccountSerializer());
    }

    public void writeAccount(AuthenticatorUserAccount account) {
        Strings.verifyArgumentNotNullOrEmpty(account.getPuid(), "account.PUID");
        writeToCollection(AccountsCollectionKey, constructAccountKey(account.getPuid()), account, getAccountSerializer());
    }

    public void removeAccount(String accountPuid) {
        removeFromCollection(AccountsCollectionKey, constructAccountKey(accountPuid));
        removeTickets(accountPuid);
    }

    public boolean hasAccounts() {
        return hasCollection(AccountsCollectionKey);
    }

    public Set<AuthenticatorUserAccount> readAllAccounts() {
        return readCollection(AccountsCollectionKey, getAccountSerializer());
    }

    public Bundle retrieveBackup() {
        Bundle backup = new Bundle();
        DeviceIdentity deviceIdentity = readDeviceIdentity();
        if (deviceIdentity != null) {
            backup.putBundle(BundleMarshaller.BackupDeviceKey, BundleMarshaller.deviceAccountToBundle(deviceIdentity));
        }
        ArrayList<Bundle> accountsBundles = new ArrayList<>();
        for (AuthenticatorUserAccount account : readAllAccounts()) {
            accountsBundles.add(BundleMarshaller.userAccountToBundle(account));
            if (deviceIdentity == null) {
                Assertion.check(!hasTickets(account.getPuid()));
            }
        }
        if (!accountsBundles.isEmpty()) {
            backup.putParcelableArrayList(BundleMarshaller.BackupUsersKey, accountsBundles);
        }
        return backup;
    }

    public void storeBackup(Bundle backup) throws BundleMarshallerException {
        Bundle deviceBundle = backup.getBundle(BundleMarshaller.BackupDeviceKey);
        String serializedDeviceIdentity = null;
        if (deviceBundle != null) {
            try {
                serializedDeviceIdentity = getDeviceIdentitySerializer().serialize(BundleMarshaller.deviceAccountFromBundle(deviceBundle));
            } catch (IOException e) {
                throw new StorageException((Throwable) e);
            }
        }
        List<Bundle> accountsBundles = backup.getParcelableArrayList(BundleMarshaller.BackupUsersKey);
        Map<String, String> serializedAccounts = new HashMap<>();
        ISerializer<AuthenticatorUserAccount> accountSerializer = getAccountSerializer();
        if (accountsBundles != null) {
            for (Bundle accountBundle : accountsBundles) {
                try {
                    AuthenticatorUserAccount account = BundleMarshaller.userAccountFromBundle(accountBundle);
                    serializedAccounts.put(constructAccountKey(account.getPuid()), accountSerializer.serialize(account));
                } catch (IOException e2) {
                    throw new StorageException((Throwable) e2);
                } catch (BundleMarshallerException e3) {
                    Logger.error("Encountered an error while trying to unbundle accounts.", e3);
                    ClientAnalytics.get().logException(e3);
                }
            }
        }
        synchronized (CollectionLock) {
            Storage.Editor editor = this._storage.edit();
            if (serializedDeviceIdentity != null) {
                editor.writeString(DeviceIdentityKey, serializedDeviceIdentity);
            }
            for (String accountKey : this._storage.readStringSet(AccountsCollectionKey)) {
                replaceCollection(constructTicketCollectionKeyFromAccountKey(accountKey), (Map<String, String>) null, editor);
            }
            replaceCollection(AccountsCollectionKey, serializedAccounts, editor);
            editor.writeLong(LastBackupReceivedTimeKey, System.currentTimeMillis());
            editor.apply();
        }
    }

    public void writeLastBackupPushedTime() {
        this._storage.edit().writeLong(LastBackupPushedTimeKey, System.currentTimeMillis()).apply();
    }

    public long readLastBackupPushedTime() {
        return this._storage.readLong(LastBackupPushedTimeKey, 0);
    }

    public void writeLastBackupReceivedTime() {
        this._storage.edit().writeLong(LastBackupReceivedTimeKey, System.currentTimeMillis()).apply();
    }

    public long readLastBackupReceivedTime() {
        return this._storage.readLong(LastBackupReceivedTimeKey, 0);
    }

    public void writeConfigLastDownloadedTime() {
        this._storage.edit().writeLong(ConfigLastDownloadedTimeKey, System.currentTimeMillis()).apply();
    }

    public long readConfigLastDownloadedTime() {
        return this._storage.readLong(ConfigLastDownloadedTimeKey, 0);
    }

    public void writeDeviceBasedFlights(Set<Integer> deviceFlights) {
        HashSet<String> deviceFlightsString = new HashSet<>(deviceFlights.size());
        for (Integer deviceFlight : deviceFlights) {
            deviceFlightsString.add(deviceFlight.toString());
        }
        this._storage.edit().writeStringSet(DeviceBasedFlightsKey, deviceFlightsString).apply();
    }

    public Set<Integer> readDeviceBasedFlights() {
        Set<String> deviceFlightsString = this._storage.readStringSet(DeviceBasedFlightsKey);
        HashSet<Integer> deviceFlights = new HashSet<>(deviceFlightsString.size());
        for (String deviceFlight : deviceFlightsString) {
            deviceFlights.add(Integer.valueOf(Integer.parseInt(deviceFlight)));
        }
        return deviceFlights;
    }

    public void writeDeviceFlightOverrideEnabled(boolean shouldOverride) {
        this._storage.edit().writeBoolean(DeviceFlightOverrideKey, shouldOverride).apply();
    }

    public boolean readDeviceFlightOverrideEnabled() {
        return this._storage.readBoolean(DeviceFlightOverrideKey, false);
    }

    public void writeSdkVersion(String version) {
        this._storage.edit().writeString(SdkVersionKey, version).apply();
    }

    public String readSdkVersion() {
        return this._storage.readString(SdkVersionKey);
    }

    /* access modifiers changed from: protected */
    public <T> void writeToCollection(String collectionKey, String valueKey, T value, ISerializer<T> serializer) {
        Assertion.check(value != null, "Attempted to write null value to collection.");
        try {
            String encoded = serializer.serialize(value);
            synchronized (CollectionLock) {
                Set<String> keys = this._storage.readStringSet(collectionKey);
                Storage.Editor editor = this._storage.edit();
                if (!keys.contains(valueKey)) {
                    HashSet hashSet = new HashSet(keys);
                    hashSet.add(valueKey);
                    editor.writeStringSet(collectionKey, hashSet);
                    HashSet hashSet2 = hashSet;
                }
                editor.writeString(valueKey, encoded).apply();
            }
        } catch (IOException e) {
            throw new StorageException((Throwable) e);
        }
    }

    /* access modifiers changed from: protected */
    public void removeFromCollection(String collectionKey, String... valueKeys) {
        removeFromCollection(collectionKey, (Collection<String>) Arrays.asList(valueKeys));
    }

    /* access modifiers changed from: protected */
    public void removeFromCollection(String collectionKey, Collection<String> valueKeys) {
        if (!valueKeys.isEmpty()) {
            Storage.Editor editor = this._storage.edit();
            for (String key : valueKeys) {
                editor.remove(key);
            }
            synchronized (CollectionLock) {
                Set<String> keys = new HashSet<>(this._storage.readStringSet(collectionKey));
                keys.removeAll(valueKeys);
                if (keys.isEmpty()) {
                    editor.remove(collectionKey);
                } else {
                    editor.writeStringSet(collectionKey, keys);
                }
                editor.apply();
            }
        }
    }

    /* access modifiers changed from: protected */
    public boolean hasCollection(String collectionKey) {
        boolean z;
        synchronized (CollectionLock) {
            z = !this._storage.readStringSet(collectionKey).isEmpty();
        }
        return z;
    }

    /* access modifiers changed from: protected */
    public <T> Set<T> readCollection(String collectionKey, ISerializer<T> serializer) {
        String str;
        HashMap hashMap = new HashMap();
        synchronized (CollectionLock) {
            Set<String> keys = this._storage.readStringSet(collectionKey);
            Set<String> retainedKeys = new HashSet<>(keys);
            for (String key : keys) {
                String value = this._storage.readString(key);
                if (value != null) {
                    hashMap.put(key, value);
                } else {
                    Assertion.check(false, "Stored collection value was null.");
                    retainedKeys.remove(key);
                }
            }
            if (retainedKeys.size() != keys.size()) {
                Logger.error("Key set was out of sync for collection: " + collectionKey);
                int index = collectionKey.indexOf(FormatSeparator);
                IClientAnalytics iClientAnalytics = ClientAnalytics.get();
                if (index > 0) {
                    str = collectionKey.substring(0, index);
                } else {
                    str = collectionKey;
                }
                iClientAnalytics.logEvent(ClientAnalytics.StorageCategory, ClientAnalytics.CollectionConsistencyError, str);
                this._storage.edit().writeStringSet(collectionKey, retainedKeys).apply();
            }
        }
        Set<T> result = Collections.emptySet();
        try {
            return serializer.deserializeAll(hashMap);
        } catch (IOException e) {
            Logger.error("Unable to deserialize indexed collection.", e);
            return result;
        }
    }

    /* access modifiers changed from: protected */
    public <T> void replaceCollection(String collectionKey, Map<String, T> values, ISerializer<T> serializer) {
        try {
            Map<String, String> encoded = serializer.serializeAll(values);
            synchronized (CollectionLock) {
                Storage.Editor editor = this._storage.edit();
                replaceCollection(collectionKey, encoded, editor);
                editor.apply();
            }
        } catch (IOException e) {
            throw new StorageException((Throwable) e);
        }
    }

    private <T> void replaceCollection(String collectionKey, Map<String, String> values, Storage.Editor editor) {
        for (String key : this._storage.readStringSet(collectionKey)) {
            editor.remove(key);
        }
        if (values != null) {
            for (Map.Entry<String, String> entry : values.entrySet()) {
                editor.writeString(entry.getKey(), entry.getValue());
            }
            editor.writeStringSet(collectionKey, values.keySet());
            return;
        }
        editor.remove(collectionKey);
    }

    /* access modifiers changed from: protected */
    public void removeCollection(String collectionKey) {
        synchronized (CollectionLock) {
            Storage.Editor editor = this._storage.edit();
            replaceCollection(collectionKey, (Map<String, String>) null, editor);
            editor.apply();
        }
    }

    /* access modifiers changed from: protected */
    public ISerializer<AuthenticatorUserAccount> getAccountSerializer() {
        return new ObjectStreamSerializer();
    }

    /* access modifiers changed from: protected */
    public ISerializer<DeviceIdentity> getDeviceIdentitySerializer() {
        return new ObjectStreamSerializer();
    }

    /* access modifiers changed from: protected */
    public ISerializer<Ticket> getTicketSerializer() {
        return new ObjectStreamSerializer();
    }

    protected static String constructKey(Object... tokens) {
        return TextUtils.join(FormatSeparator, tokens);
    }

    protected static String constructAccountKey(String accountPuid) {
        return constructKey(AccountKeyToken, accountPuid.toLowerCase(Locale.US));
    }

    protected static String constructTicketKey(String accountPuid, String appID, ISecurityScope securityScope) {
        Objects.verifyArgumentNotNull(securityScope.getTarget(), "Ticket target");
        Objects.verifyArgumentNotNull(securityScope.getPolicy(), "Ticket policy");
        return constructKey(TicketKeyToken, accountPuid.toLowerCase(Locale.US), appID.toLowerCase(Locale.US), securityScope.getTarget().toLowerCase(Locale.US), securityScope.getPolicy().toLowerCase(Locale.US));
    }

    protected static String constructTicketCollectionKey(String accountPuid) {
        return constructKey(TicketCollectionKeyToken, accountPuid.toLowerCase(Locale.US));
    }

    protected static String constructTicketCollectionKeyFromAccountKey(String accountKey) {
        return accountKey.replace(AccountKeyToken, TicketCollectionKeyToken);
    }
}
