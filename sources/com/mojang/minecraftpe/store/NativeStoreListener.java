package com.mojang.minecraftpe.store;

public class NativeStoreListener implements StoreListener {
    long mStoreListener;

    public native void onPurchaseCanceled(long j, String str);

    public native void onPurchaseFailed(long j, String str);

    public native void onPurchaseSuccessful(long j, String str, String str2);

    public native void onQueryProductsFail(long j);

    public native void onQueryProductsSuccess(long j, Product[] productArr);

    public native void onQueryPurchasesFail(long j);

    public native void onQueryPurchasesSuccess(long j, Purchase[] purchaseArr);

    public native void onStoreInitialized(long j, boolean z);

    public NativeStoreListener(long storeListener) {
        this.mStoreListener = storeListener;
    }

    public void onStoreInitialized(boolean available) {
        onStoreInitialized(this.mStoreListener, available);
    }

    public void onQueryProductsSuccess(Product[] products) {
        onQueryProductsSuccess(this.mStoreListener, products);
    }

    public void onQueryProductsFail() {
        onQueryProductsFail(this.mStoreListener);
    }

    public void onPurchaseSuccessful(String productId, String receipt) {
        onPurchaseSuccessful(this.mStoreListener, productId, receipt);
    }

    public void onPurchaseCanceled(String productId) {
        onPurchaseCanceled(this.mStoreListener, productId);
    }

    public void onPurchaseFailed(String productId) {
        onPurchaseFailed(this.mStoreListener, productId);
    }

    public void onQueryPurchasesSuccess(Purchase[] purchases) {
        onQueryPurchasesSuccess(this.mStoreListener, purchases);
    }

    public void onQueryPurchasesFail() {
        onQueryPurchasesFail(this.mStoreListener);
    }
}
