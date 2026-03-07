package com.mojang.minecraftpe.store;

public interface Store {
    void acknowledgePurchase(String str, String str2);

    void destructor();

    String getStoreId();

    void purchase(String str, boolean z, String str2);

    void queryProducts(String[] strArr);

    void queryPurchases();
}
