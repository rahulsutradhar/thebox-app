package one.thebox.android.Helpers.cart;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;


import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import one.thebox.android.Models.items.BoxItem;
import one.thebox.android.Models.items.ItemConfig;
import one.thebox.android.Models.order.Order;
import one.thebox.android.Models.items.UserItem;
import one.thebox.android.app.Constants;
import one.thebox.android.app.TheBox;
import one.thebox.android.fragment.SearchDetailFragment;
import one.thebox.android.util.PrefUtils;

/**
 * Created by Ajeet Kumar Meena on 19-05-2016.
 * <p>
 * Updated by Developers on 31-05-2017.
 */
public class CartHelper {

    /**
     * Add Box Item to CartProduct
     */
    public static void addBoxItemToCart(final BoxItem boxItem) {
        Realm realm = TheBox.getRealm();
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(boxItem);
        realm.commitTransaction();

        sendUpdateNoItemsInCartBroadcast(getCartSize());
        //storing in memory
        ProductQuantity.addNewProduct(boxItem);
    }

    /**
     * Remove Box Item from CartProduct
     */
    public static void removeItemFromCart(final BoxItem boxItem) {
        //removing from memory
        ProductQuantity.removeProduct(boxItem);

        Realm realm = TheBox.getRealm();
        realm.beginTransaction();
        realm.where(BoxItem.class).equalTo("uuid", boxItem.getUuid()).findFirst().deleteFromRealm();
        realm.commitTransaction();
        sendUpdateNoItemsInCartBroadcast(getCartSize());
    }

    /**
     * Update Quantity in CartProduct
     */
    public static void updateQuantityInCart(final BoxItem boxItem, final int quantity) {
        Realm realm = TheBox.getRealm();
        realm.beginTransaction();
        BoxItem boxItem1 = realm.where(BoxItem.class).equalTo("uuid", boxItem.getUuid()).findFirst();
        if (boxItem1 != null) {
            boxItem1.setQuantity(quantity);
        }
        realm.commitTransaction();

        //updating Quantity from memory
        ProductQuantity.updateQuantity(boxItem, quantity);
    }

    /**
     * Update Quantity inside cart
     */
    public static BoxItem updateQuantityInsideCart(final BoxItem boxItem, final int quantity) {
        BoxItem updatedBoxItem;
        Realm realm = TheBox.getRealm();
        realm.beginTransaction();
        updatedBoxItem = realm.where(BoxItem.class).equalTo("uuid", boxItem.getUuid()).findFirst();
        updatedBoxItem.setQuantity(quantity);
        realm.commitTransaction();

        //updating Quantity from memory
        ProductQuantity.updateQuantity(boxItem, quantity);
        return updatedBoxItem;
    }

    /**
     * Update ItemConfig in CartProduct
     */
    public static void updateItemConfigInCart(final BoxItem boxItem, final ItemConfig itemConfig) {
        Realm realm = TheBox.getRealm();
        realm.executeTransactionAsync
                (new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        if (itemConfig != null) {
                            ItemConfig updatedItemConfig = realm.where(ItemConfig.class).equalTo("uuid", itemConfig.getUuid()).findFirst();

                            if (updatedItemConfig != null) {
                                BoxItem boxItem1 = realm.where(BoxItem.class).equalTo("uuid", boxItem.getUuid()).findFirst();
                                boxItem1.setSelectedItemConfig(updatedItemConfig);
                            }
                        }
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        //updating itemconfig in memory
                        ProductQuantity.updateItemConfig(boxItem, itemConfig);
                    }
                }, new Realm.Transaction.OnError() {
                    @Override
                    public void onError(Throwable error) {
                        Toast.makeText(TheBox.getAppContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Update ItemConfig inside CartProduct
     */
    public static BoxItem updateItemConfigInsideCart(final BoxItem boxItem, ItemConfig itemConfig) {
        BoxItem updatedBoxItem = null;
        Realm realm = TheBox.getRealm();
        realm.beginTransaction();
        if (itemConfig != null) {
            updatedBoxItem = realm.where(BoxItem.class).equalTo("uuid", boxItem.getUuid()).findFirst();
            updatedBoxItem.setSelectedItemConfig(itemConfig);
        }
        realm.commitTransaction();

        //updating itemconfig in memory
        ProductQuantity.updateItemConfig(boxItem, itemConfig);

        return updatedBoxItem;
    }

    /**
     * Get CartProduct Size
     */
    public static int getCartSize() {
        int size = 0;

        size = TheBox.getRealm().where(BoxItem.class).notEqualTo("uuid", "").greaterThan("quantity", 0).findAll().size();

        return size;
    }

    /**
     * Is cart is Empty
     */
    public static boolean isCartEmpty() {
        boolean flag = true;
        if (getCartSize() > 0) {
            flag = false;
        }
        return flag;
    }

    /**
     * Get CartProduct
     */
    public static ArrayList<BoxItem> getCart() {
        ArrayList<BoxItem> cart = new ArrayList<>();
        RealmResults<BoxItem> realmResults = TheBox.getRealm().where(BoxItem.class).notEqualTo("uuid", "").greaterThan("quantity", 0).findAll();
        if (realmResults.isLoaded()) {
            if (realmResults.size() > 0)
                cart.addAll(realmResults);
        }
        return cart;
    }

    /**
     * Update CartProduct After fetching from Server; when app opens
     */
    public static void updateCart(final RealmList<BoxItem> boxItems) {
        //remove all item
        clearCart(false);
        //not update all item
        Realm realm = TheBox.getRealm();
        realm.executeTransactionAsync
                (new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realm.copyToRealmOrUpdate(boxItems);
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        sendUpdateNoItemsInCartBroadcast(getCartSize());
                    }
                }, new Realm.Transaction.OnError() {
                    @Override
                    public void onError(Throwable error) {
                    }
                });
    }

    /**
     * Clear CartProduct
     */
    public static void clearCart(final boolean shallBroadCast) {
        Realm realm = TheBox.getRealm();
        realm.executeTransactionAsync
                (new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        RealmResults<BoxItem> realmResults = realm.where(BoxItem.class).notEqualTo("uuid", "").greaterThan("quantity", 0).findAll();
                        realmResults.deleteAllFromRealm();
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        //cart is empty
                        if (shallBroadCast) {
                            sendUpdateNoItemsInCartBroadcast(0);
                        }
                    }
                }, new Realm.Transaction.OnError() {
                    @Override
                    public void onError(Throwable error) {

                    }
                });
    }

    /**
     * Calculate Total Price of CartProduct
     */
    public static float getCartPrice() {
        float price = 0;
        for (BoxItem boxItem : getCart()) {
            price += (boxItem.getQuantity() * boxItem.getSelectedItemConfig().getPrice());
        }
        return price;
    }

    /**
     * Calculate Total Savings in CartProduct
     */
    public static float getTotalSavings() {
        float savings = 0;
        for (BoxItem boxItem : getCart()) {
            savings += (boxItem.getQuantity() * boxItem.getSelectedItemConfig().getSavings());
        }
        return savings;
    }

    /**
     * Send Broadcast to show number of item in CartProduct status
     */
    public static void sendUpdateNoItemsInCartBroadcast(int numberOfItem) {
        Intent intent = new Intent(SearchDetailFragment.BROADCAST_EVENT_TAB);
        // add data
        intent.putExtra(Constants.EXTRA_ITEMS_IN_CART, numberOfItem);
        intent.putExtra(SearchDetailFragment.EXTRA_NUMBER_OF_TABS, numberOfItem);
        LocalBroadcastManager.getInstance(TheBox.getInstance()).sendBroadcast(intent);
    }

}
