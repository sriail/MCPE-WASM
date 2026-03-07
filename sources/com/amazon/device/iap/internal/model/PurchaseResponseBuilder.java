package com.amazon.device.iap.internal.model;

import com.amazon.device.iap.model.PurchaseResponse;
import com.amazon.device.iap.model.Receipt;
import com.amazon.device.iap.model.RequestId;
import com.amazon.device.iap.model.UserData;

public class PurchaseResponseBuilder {
    private Receipt receipt;
    private RequestId requestId;
    private PurchaseResponse.RequestStatus requestStatus;
    private UserData userData;

    public PurchaseResponse build() {
        return new PurchaseResponse(this);
    }

    public RequestId getRequestId() {
        return this.requestId;
    }

    public PurchaseResponse.RequestStatus getRequestStatus() {
        return this.requestStatus;
    }

    public UserData getUserData() {
        return this.userData;
    }

    public Receipt getReceipt() {
        return this.receipt;
    }

    public PurchaseResponseBuilder setRequestId(RequestId requestId2) {
        this.requestId = requestId2;
        return this;
    }

    public PurchaseResponseBuilder setRequestStatus(PurchaseResponse.RequestStatus requestStatus2) {
        this.requestStatus = requestStatus2;
        return this;
    }

    public PurchaseResponseBuilder setUserData(UserData userData2) {
        this.userData = userData2;
        return this;
    }

    public PurchaseResponseBuilder setReceipt(Receipt receipt2) {
        this.receipt = receipt2;
        return this;
    }
}
