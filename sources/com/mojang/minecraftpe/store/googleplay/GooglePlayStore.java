package com.mojang.minecraftpe.store.googleplay;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import com.googleplay.util.IabHelper;
import com.googleplay.util.IabResult;
import com.googleplay.util.Inventory;
import com.googleplay.util.Purchase;
import com.googleplay.util.SkuDetails;
import com.mojang.minecraftpe.ActivityListener;
import com.mojang.minecraftpe.MainActivity;
import com.mojang.minecraftpe.store.Product;
import com.mojang.minecraftpe.store.Store;
import com.mojang.minecraftpe.store.StoreListener;
import java.util.ArrayList;
import java.util.Arrays;
import org.json.JSONException;
import org.json.JSONObject;

public class GooglePlayStore extends BroadcastReceiver implements Store, ActivityListener {
    static final String IAB_BROADCAST_ACTION = "com.android.vending.billing.PURCHASES_UPDATED";
    MainActivity mActivity;
    IabHelper mIabHelper;
    StoreListener mListener;
    int mPurchaseRequestCode = MainActivity.RESULT_GOOGLEPLAY_PURCHASE;

    public GooglePlayStore(MainActivity activity, String licenseKey, StoreListener listener) {
        this.mListener = listener;
        this.mActivity = activity;
        this.mActivity.addListener(this);
        this.mIabHelper = new IabHelper(this.mActivity, licenseKey);
        this.mIabHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                Log.i("GooglePlayStore", "onIabSetupFinished: " + result.getResponse() + ", " + result.getMessage());
                GooglePlayStore.this.mActivity.registerReceiver(this, new IntentFilter(GooglePlayStore.IAB_BROADCAST_ACTION));
                GooglePlayStore.this.mListener.onStoreInitialized(result.isSuccess());
            }
        });
    }

    public String getStoreId() {
        return "android.googleplay";
    }

    public void onReceive(Context context, Intent intent) {
        Log.i("GooglePlayStore", "IabBroadcastReceiver received message PURCHASES_UPDATED");
        queryPurchases();
    }

    public void queryProducts(final String[] productIds) {
        this.mActivity.runOnUiThread(new Runnable() {
            public void run() {
                GooglePlayStore.this.mIabHelper.queryInventoryAsync(true, Arrays.asList(productIds), new IabHelper.QueryInventoryFinishedListener() {
                    public void onQueryInventoryFinished(IabResult result, Inventory inv) {
                        if (result.isSuccess()) {
                            ArrayList<Product> products = new ArrayList<>();
                            if (result.isSuccess()) {
                                for (String productId : productIds) {
                                    SkuDetails skuDetails = inv.getSkuDetails(productId);
                                    if (skuDetails != null) {
                                        products.add(new Product(productId, skuDetails.getPrice()));
                                    }
                                }
                            }
                            GooglePlayStore.this.mListener.onQueryProductsSuccess((Product[]) products.toArray(new Product[0]));
                            return;
                        }
                        GooglePlayStore.this.mListener.onQueryProductsFail();
                    }
                });
            }
        });
    }

    public void purchase(final String productId, final boolean isSubscription, final String payload) {
        this.mActivity.runOnUiThread(new Runnable() {
            public void run() {
                String itemType;
                if (isSubscription) {
                    IabHelper iabHelper = GooglePlayStore.this.mIabHelper;
                    itemType = IabHelper.ITEM_TYPE_SUBS;
                } else {
                    IabHelper iabHelper2 = GooglePlayStore.this.mIabHelper;
                    itemType = IabHelper.ITEM_TYPE_INAPP;
                }
                GooglePlayStore.this.mIabHelper.launchPurchaseFlow(GooglePlayStore.this.mActivity, productId, itemType, GooglePlayStore.this.mPurchaseRequestCode, new IabHelper.OnIabPurchaseFinishedListener() {
                    public void onIabPurchaseFinished(IabResult result, Purchase info) {
                        if (result.isSuccess()) {
                            GooglePlayStore.this.mListener.onPurchaseSuccessful(productId, GooglePlayStore.this.createReceipt(info));
                        } else if (result.getResponse() == -1005) {
                            GooglePlayStore.this.mListener.onPurchaseCanceled(productId);
                        } else {
                            GooglePlayStore.this.mListener.onPurchaseFailed(productId);
                        }
                    }
                }, payload);
            }
        });
    }

    public void acknowledgePurchase(final String receipt, String productType) {
        if (productType.equals("Consumable")) {
            this.mActivity.runOnUiThread(new Runnable() {
                public void run() {
                    Purchase purchase = GooglePlayStore.this.parseReceipt(receipt);
                    if (purchase != null) {
                        GooglePlayStore.this.mIabHelper.consumeAsync(purchase, (IabHelper.OnConsumeFinishedListener) null);
                    }
                }
            });
        }
    }

    public void queryPurchases() {
        this.mActivity.runOnUiThread(new Runnable() {
            public void run() {
                GooglePlayStore.this.mIabHelper.queryInventoryAsync(false, new IabHelper.QueryInventoryFinishedListener() {
                    public void onQueryInventoryFinished(IabResult result, Inventory inv) {
                        if (result.isSuccess()) {
                            ArrayList<com.mojang.minecraftpe.store.Purchase> purchases = new ArrayList<>();
                            if (result.isSuccess()) {
                                for (Purchase purchase : inv.getAllPurchases()) {
                                    purchases.add(new com.mojang.minecraftpe.store.Purchase(purchase.getSku(), GooglePlayStore.this.createReceipt(purchase), purchase.getPurchaseState() == 0));
                                }
                            }
                            GooglePlayStore.this.mListener.onQueryPurchasesSuccess((com.mojang.minecraftpe.store.Purchase[]) purchases.toArray(new com.mojang.minecraftpe.store.Purchase[0]));
                            return;
                        }
                        GooglePlayStore.this.mListener.onQueryPurchasesFail();
                    }
                });
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (this.mIabHelper != null) {
            this.mIabHelper.handleActivityResult(requestCode, resultCode, data);
        }
    }

    public void onDestroy() {
        this.mActivity.removeListener(this);
        if (this.mIabHelper != null) {
            this.mIabHelper.dispose();
        }
        this.mIabHelper = null;
    }

    public void destructor() {
        onDestroy();
    }

    /* access modifiers changed from: private */
    public String createReceipt(Purchase purchase) {
        JSONObject json = new JSONObject();
        try {
            json.put("itemtype", purchase.getItemType());
            json.put("originaljson", purchase.getOriginalJson());
            json.put("signature", purchase.getSignature());
            return json.toString();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    /* access modifiers changed from: private */
    public Purchase parseReceipt(String receipt) {
        try {
            JSONObject json = new JSONObject(receipt);
            return new Purchase(json.getString("itemtype"), json.getString("originaljson"), json.getString("signature"));
        } catch (JSONException e) {
            return null;
        }
    }
}
