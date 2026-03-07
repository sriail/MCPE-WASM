package com.microsoft.xbox.service.network.managers;

import java.util.ArrayList;
import java.util.Iterator;

public final class MutedListResultContainer {

    public static class MutedListResult {
        public ArrayList<MutedUser> users = new ArrayList<>();

        public void add(String xuid) {
            this.users.add(new MutedUser(xuid));
        }

        public MutedUser remove(String xuid) {
            Iterator i$ = this.users.iterator();
            while (i$.hasNext()) {
                MutedUser user = i$.next();
                if (user.xuid.equalsIgnoreCase(xuid)) {
                    this.users.remove(user);
                    return user;
                }
            }
            return null;
        }

        public boolean contains(String xuid) {
            Iterator i$ = this.users.iterator();
            while (i$.hasNext()) {
                if (i$.next().xuid.equalsIgnoreCase(xuid)) {
                    return true;
                }
            }
            return false;
        }
    }

    public static class MutedUser {
        public String xuid;

        public MutedUser(String xuid2) {
            this.xuid = xuid2;
        }
    }
}
