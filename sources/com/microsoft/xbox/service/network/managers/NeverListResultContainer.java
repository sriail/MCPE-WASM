package com.microsoft.xbox.service.network.managers;

import java.util.ArrayList;
import java.util.Iterator;

public final class NeverListResultContainer {

    public static class NeverListResult {
        public ArrayList<NeverUser> users = new ArrayList<>();

        public void add(String xuid) {
            this.users.add(new NeverUser(xuid));
        }

        public NeverUser remove(String xuid) {
            Iterator i$ = this.users.iterator();
            while (i$.hasNext()) {
                NeverUser user = i$.next();
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

    public static class NeverUser {
        public String xuid;

        public NeverUser(String xuid2) {
            this.xuid = xuid2;
        }
    }
}
