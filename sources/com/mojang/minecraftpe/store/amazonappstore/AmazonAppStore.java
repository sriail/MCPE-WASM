package com.mojang.minecraftpe.store.amazonappstore;

import android.content.Context;
import com.amazon.device.iap.PurchasingListener;
import com.amazon.device.iap.PurchasingService;
import com.amazon.device.iap.model.FulfillmentResult;
import com.amazon.device.iap.model.ProductDataResponse;
import com.amazon.device.iap.model.PurchaseResponse;
import com.amazon.device.iap.model.PurchaseUpdatesResponse;
import com.amazon.device.iap.model.Receipt;
import com.amazon.device.iap.model.RequestId;
import com.amazon.device.iap.model.UserDataResponse;
import com.mojang.minecraftpe.store.Product;
import com.mojang.minecraftpe.store.Purchase;
import com.mojang.minecraftpe.store.Store;
import com.mojang.minecraftpe.store.StoreListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.json.JSONException;
import org.json.JSONObject;

public class AmazonAppStore implements Store {
    /* access modifiers changed from: private */
    public StoreListener mListener;
    /* access modifiers changed from: private */
    public Map<RequestId, String> mProductIdRequestMapping = new HashMap();
    private PurchasingListener mPurchasingListener = new PurchasingListener() {
        public void onUserDataResponse(UserDataResponse userDataResponse) {
        }

        public void onPurchaseUpdatesResponse(PurchaseUpdatesResponse purchaseUpdatesResponse) {
            if (purchaseUpdatesResponse.getRequestStatus() == PurchaseUpdatesResponse.RequestStatus.SUCCESSFUL) {
                ArrayList<Purchase> purchases = new ArrayList<>();
                String userId = purchaseUpdatesResponse.getUserData().getUserId();
                for (Receipt receipt : purchaseUpdatesResponse.getReceipts()) {
                    purchases.add(new Purchase(receipt.getSku(), AmazonAppStore.this.createReceipt(userId, receipt.getReceiptId()), !receipt.isCanceled()));
                }
                AmazonAppStore.this.mListener.onQueryPurchasesSuccess((Purchase[]) purchases.toArray(new Purchase[0]));
                return;
            }
            AmazonAppStore.this.mListener.onQueryPurchasesFail();
        }

        public void onPurchaseResponse(PurchaseResponse purchaseResponse) {
            String productId = (String) AmazonAppStore.this.mProductIdRequestMapping.remove(purchaseResponse.getRequestId());
            if (purchaseResponse.getRequestStatus() == PurchaseResponse.RequestStatus.SUCCESSFUL) {
                AmazonAppStore.this.mListener.onPurchaseSuccessful(productId, AmazonAppStore.this.createReceipt(purchaseResponse));
            } else {
                AmazonAppStore.this.mListener.onPurchaseFailed(productId);
            }
        }

        public void onProductDataResponse(ProductDataResponse productDataResponse) {
            if (productDataResponse.getRequestStatus() == ProductDataResponse.RequestStatus.SUCCESSFUL) {
                ArrayList<Product> products = new ArrayList<>();
                Set<String> unavailableSkus = productDataResponse.getUnavailableSkus();
                Map<String, com.amazon.device.iap.model.Product> productDataMap = productDataResponse.getProductData();
                for (String productKey : productDataMap.keySet()) {
                    if (!unavailableSkus.contains(productKey)) {
                        com.amazon.device.iap.model.Product productEntry = productDataMap.get(productKey);
                        products.add(new Product(productEntry.getSku() != null ? productEntry.getSku().replace(".child", "") : "", productEntry.getPrice() != null ? productEntry.getPrice() : ""));
                    }
                }
                AmazonAppStore.this.mListener.onQueryProductsSuccess((Product[]) products.toArray(new Product[0]));
                return;
            }
            AmazonAppStore.this.mListener.onQueryProductsFail();
        }
    };
    private final String subscriptionKey = ".subscription";

    public AmazonAppStore(Context context, StoreListener listener) {
        this.mListener = listener;
        PurchasingService.registerListener(context, this.mPurchasingListener);
        listener.onStoreInitialized(true);
    }

    public String getStoreId() {
        return "android.amazonappstore";
    }

    public void queryProducts(String[] productIds) {
        String[] finalProductIds = new String[productIds.length];
        for (int i = 0; i < productIds.length; i++) {
            if (productIds[i].indexOf(".subscription") != -1) {
                finalProductIds[i] = productIds[i] + ".child";
            } else {
                finalProductIds[i] = productIds[i];
            }
        }
        PurchasingService.getProductData(new HashSet<>(Arrays.asList(finalProductIds)));
    }

    public void purchase(String productId, boolean isSubscription, String payload) {
        this.mProductIdRequestMapping.put(PurchasingService.purchase(productId), productId);
    }

    public void acknowledgePurchase(String receipt, String productType) {
        try {
            PurchasingService.notifyFulfillment(new JSONObject(receipt).getString("receiptId"), FulfillmentResult.FULFILLED);
        } catch (JSONException e) {
        }
    }

    public void queryPurchases() {
        PurchasingService.getPurchaseUpdates(true);
    }

    public void destructor() {
    }

    /* access modifiers changed from: private */
    public String createReceipt(String userId, String receiptId) {
        JSONObject json = new JSONObject();
        try {
            json.put("userId", userId);
            json.put("receiptId", receiptId);
            return json.toString();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    /* access modifiers changed from: private */
    public String createReceipt(PurchaseResponse purchaseResponse) {
        return createReceipt(purchaseResponse.getUserData().getUserId(), purchaseResponse.getReceipt().getReceiptId());
    }
}
