package com.microsoft.cll.android;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TicketManager {
    private final String TAG = "AndroidCll-TicketManager";
    private final ITicketCallback callback;
    private final ILogger logger;
    private boolean needDeviceTicket = true;
    private final Map<String, String> tickets;

    public TicketManager(ITicketCallback callback2, ILogger logger2) {
        this.callback = callback2;
        this.logger = logger2;
        this.tickets = new HashMap();
    }

    public TicketHeaders getHeaders(boolean shouldForceRefresh) {
        if (this.callback == null || this.tickets.isEmpty()) {
            return null;
        }
        TicketHeaders headers = new TicketHeaders();
        headers.authXToken = this.callback.getAuthXToken(shouldForceRefresh);
        headers.xtokens = this.tickets;
        if (!this.needDeviceTicket) {
            return headers;
        }
        headers.msaDeviceTicket = this.callback.getMsaDeviceTicket(shouldForceRefresh);
        return headers;
    }

    public void addTickets(List<String> ids) {
        if (ids != null && this.callback != null) {
            for (String ticketId : ids) {
                if (!this.tickets.containsKey(ticketId)) {
                    this.logger.info("AndroidCll-TicketManager", "Getting ticket for " + ticketId);
                    TicketObject ticket = this.callback.getXTicketForXuid(ticketId);
                    String ticketString = ticket.ticket;
                    if (ticket.hasDeviceClaims) {
                        this.needDeviceTicket = false;
                        ticketString = "rp:" + ticketString;
                    }
                    this.tickets.put(ticketId, ticketString);
                } else {
                    this.logger.info("AndroidCll-TicketManager", "We already have a ticket for this id, skipping.");
                }
            }
        }
    }

    public void clean() {
        this.tickets.clear();
        this.needDeviceTicket = true;
    }
}
