package one.thebox.android.Helpers;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;


import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import one.thebox.android.Models.BoxItem;
import one.thebox.android.Models.ItemConfig;
import one.thebox.android.Models.Order;
import one.thebox.android.Models.UserItem;
import one.thebox.android.app.Constants;
import one.thebox.android.app.TheBox;
import one.thebox.android.fragment.SearchDetailFragment;
import one.thebox.android.util.PrefUtils;

/**
 * Created by Ajeet Kumar Meena on 19-05-2016.
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


    public static void sendUpdateNoItemsInCartBroadcast(int numberOfItem) {
        Intent intent = new Intent(SearchDetailFragment.BROADCAST_EVENT_TAB);
        // add data
        intent.putExtra(Constants.EXTRA_ITEMS_IN_CART, numberOfItem);
        intent.putExtra(SearchDetailFragment.EXTRA_NUMBER_OF_TABS, numberOfItem);
        LocalBroadcastManager.getInstance(TheBox.getInstance()).sendBroadcast(intent);
    }


    /**
     * Refactor
     */

    /**
     * Add Box Item to Cart
     */
    public static void addBoxItemToCart(final BoxItem boxItem) {
        Realm realm = TheBox.getRealm();
        realm.executeTransactionAsync
                (new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realm.copyToRealmOrUpdate(boxItem);
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(TheBox.getAppContext(), "Added To Cart", Toast.LENGTH_SHORT).show();
                        sendUpdateNoItemsInCartBroadcast(getCartSize());

                    }
                }, new Realm.Transaction.OnError() {
                    @Override
                    public void onError(Throwable error) {

                    }
                });
    }

    /**
     * Remove Box Item from Cart
     */
    public static void removeItemFromCart(final BoxItem boxItem) {
        Realm realm = TheBox.getRealm();
        realm.executeTransactionAsync
                (new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realm.where(BoxItem.class).equalTo("uuid", boxItem.getUuid()).findFirst().deleteFromRealm();
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(TheBox.getAppContext(), "Remove from Cart", Toast.LENGTH_SHORT).show();
                        sendUpdateNoItemsInCartBroadcast(getCartSize());
                    }
                }, new Realm.Transaction.OnError() {
                    @Override
                    public void onError(Throwable error) {

                    }
                });
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
        Toast.makeText(TheBox.getAppContext(), "Update Quantity", Toast.LENGTH_SHORT).show();
        /*realm.executeTransactionAsync
                (new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {

                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(TheBox.getAppContext(), "Update Quantity", Toast.LENGTH_SHORT).show();
                    }
                }, new Realm.Transaction.OnError() {
                    @Override
                    public void onError(Throwable error) {

                    }
                });*/
    }

    /**
     * Update ItemConfig in Cart
     */
    public static void updateItemConfigInCart(final BoxItem boxItem) {
        Realm realm = TheBox.getRealm();
        realm.executeTransactionAsync
                (new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        if (boxItem.getSelectedItemConfig() != null) {
                            BoxItem boxItem1 = realm.where(BoxItem.class).equalTo("uuid", boxItem.getUuid()).findFirst();
                            if (boxItem1 != null) {
                                realm.copyToRealmOrUpdate(boxItem);
                            }
                        }
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(TheBox.getAppContext(), "Update ItemConfig", Toast.LENGTH_SHORT).show();
                    }
                }, new Realm.Transaction.OnError() {
                    @Override
                    public void onError(Throwable error) {
                        Toast.makeText(TheBox.getAppContext(), "Update ItemConfig Failed " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    /**
     * Get Cart Items
     */
    public static RealmList<BoxItem> getCartItems() {
        return getCart();
    }

    /**
     * Get Cart Size
     */
    public static int getCartSize() {
        int size = 0;
        if (getCart() != null) {
            size = getCart().size();
        }
        return size;
    }

    /**
     * Is cart is Empty
     */
    public static boolean isCartEmpty() {
        boolean flag = true;
        if (getCart().size() > 0) {
            flag = false;
        }
        return flag;
    }

    /**
     * Get Cart
     */
    public static RealmList<BoxItem> getCart() {
        RealmList<BoxItem> cart = new RealmList<>();
        RealmResults<BoxItem> realmResults = TheBox.getRealm().where(BoxItem.class).notEqualTo("uuid", "").greaterThan("quantity", 0).findAll();
        if (realmResults.isLoaded()) {
            if (realmResults.size() > 0)
                cart.addAll(realmResults.subList(0, realmResults.size()));
        }
        return cart;
    }

    /**
     * Update Cart After fetching from Server
     */
    public static void updateCart(final RealmList<BoxItem> boxItems) {
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
                        Toast.makeText(TheBox.getAppContext(), "Updated Cart", Toast.LENGTH_SHORT).show();
                    }
                }, new Realm.Transaction.OnError() {
                    @Override
                    public void onError(Throwable error) {
                        Toast.makeText(TheBox.getAppContext(), "Update ItemConfig Failed " + error.getMessage(), Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(TheBox.getAppContext(), "Cleared Cart", Toast.LENGTH_SHORT).show();
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
            price = price + (boxItem.getQuantity() * boxItem.getSelectedItemConfig().getPrice());
        }
        return price;
    }

}
