package com.microsoft.cll.android;

public class TicketObject {
    public boolean hasDeviceClaims;
    public String ticket;

    public TicketObject(String ticket2, boolean hasDeviceClaims2) {
        this.ticket = ticket2;
        this.hasDeviceClaims = hasDeviceClaims2;
    }
}
