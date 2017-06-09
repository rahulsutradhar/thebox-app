package one.thebox.android.Helpers.cart;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;


import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import one.thebox.android.Models.items.BoxItem;
import one.thebox.android.Models.ItemConfig;
import one.thebox.android.Models.order.Order;
import one.thebox.android.Models.UserItem;
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
    private static final String EXTRA_HAS_SAVED_ORDER = "extra_has_saved_order";

    public static void saveCartItemsIfRequire() {
        if (!PrefUtils.getBoolean(TheBox.getInstance(), EXTRA_HAS_SAVED_ORDER, false)) {
            saveOrders();
        }
    }

    private static void saveOrders() {
    }

    public static void saveOrdersToRealm(final Order order) {
        final Realm superRealm = TheBox.getRealm();
        superRealm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(order);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                sendUpdateNoItemsInCartBroadcast(order.getUserItems().size());
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Toast.makeText(TheBox.getInstance(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public static int getNumberOfItemsInCart() {
        Order order = TheBox.getRealm()
                .where(Order.class)
                .notEqualTo(Order.FIELD_ID, 0)
                .equalTo(Order.FIELD_ID, PrefUtils.getUser(TheBox.getInstance()).getCartId())
                .findFirst();
        if (order == null || order.getId() == 0) {
            return 0;
        } else {
            return order.getUserItems().size();
        }
    }

    public static void addOrUpdateUserItem(final UserItem userItem, final Order cart) {
        Realm realm = TheBox.getRealm();
        realm.executeTransactionAsync
                (new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realm.copyToRealmOrUpdate(userItem);

                        //update cart
                        if (cart != null) {
                            if (cart.getUserItems() != null) {
                                if (cart.getUserItems().size() > 0) {
                                    realm.copyToRealmOrUpdate(cart);
                                } else {
                                    //delete the order row
                                    realm.where(Order.class).equalTo("id", cart.getId()).findFirst().deleteFromRealm();
                                }
                            }
                        }
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        if (cart != null) {
                            // OrderHelper.addAndNotify(cart);
                            sendUpdateNoItemsInCartBroadcast(cart.getUserItems().size());
                        }
                    }
                }, new Realm.Transaction.OnError() {
                    @Override
                    public void onError(Throwable error) {
                    }
                });
    }

    public static void removeUserItem(final int userItemId, final Order cart) {

        Realm realm = TheBox.getRealm();
        realm.executeTransactionAsync
                (new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realm.where(UserItem.class).equalTo("id", userItemId).findFirst().deleteFromRealm();

                        //update cart
                        if (cart != null) {
                            if (cart.getUserItems() != null) {
                                if (cart.getUserItems().size() > 0) {
                                    realm.copyToRealmOrUpdate(cart);
                                } else {
                                    //delete the order row
                                    realm.where(Order.class).equalTo("id", cart.getId()).findFirst().deleteFromRealm();
                                }
                            }
                        }
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {

                        if (cart != null) {
                            // OrderHelper.addAndNotify(cart);
                            if (cart.getUserItems() != null) {
                                sendUpdateNoItemsInCartBroadcast(cart.getUserItems().size());
                            } else {
                                clearCart(false);
                                sendUpdateNoItemsInCartBroadcast(0);
                            }
                        }

                    }
                }, new Realm.Transaction.OnError() {
                    @Override
                    public void onError(Throwable error) {
                    }
                });

    }


    /**
     * Refactor
     */

    /**
     * Add Box Item to Cart
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
     * Remove Box Item from Cart
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
     * Update Quantity in Cart
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
     * Update ItemConfig in Cart
     */
    public static void updateItemConfigInCart(final BoxItem boxItem, final ItemConfig itemConfig) {
        Realm realm = TheBox.getRealm();
        realm.executeTransactionAsync
                (new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        if (itemConfig != null) {
                            BoxItem boxItem1 = realm.where(BoxItem.class).equalTo("uuid", boxItem.getUuid()).findFirst();
                            boxItem1.setSelectedItemConfig(itemConfig);
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
                    }
                });
    }

    /**
     * Update ItemConfig inside Cart
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
     * Get Cart Size
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
     * Get Cart
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
     * Update Cart After fetching from Server; when app opens
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
                    }
                }, new Realm.Transaction.OnError() {
                    @Override
                    public void onError(Throwable error) {
                    }
                });
    }

    /**
     * Clear Cart
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
     * Calculate Total Price of Cart
     */
    public static float getCartPrice() {
        float price = 0;
        for (BoxItem boxItem : getCart()) {
            price += (boxItem.getQuantity() * boxItem.getSelectedItemConfig().getPrice());
        }
        return price;
    }

    /**
     * Send Broadcast to show number of item in Cart status
     */
    public static void sendUpdateNoItemsInCartBroadcast(int numberOfItem) {
        Intent intent = new Intent(SearchDetailFragment.BROADCAST_EVENT_TAB);
        // add data
        intent.putExtra(Constants.EXTRA_ITEMS_IN_CART, numberOfItem);
        intent.putExtra(SearchDetailFragment.EXTRA_NUMBER_OF_TABS, numberOfItem);
        LocalBroadcastManager.getInstance(TheBox.getInstance()).sendBroadcast(intent);
    }

}
