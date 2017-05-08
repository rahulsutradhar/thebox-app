package one.thebox.android.Helpers;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;


import io.realm.Realm;
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
                                clearCart();
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

    public static void clearCart() {
        final int cartId = PrefUtils.getUser(TheBox.getInstance()).getCartId();
        Realm realm = TheBox.getRealm();
        realm.executeTransactionAsync
                (new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        Order order = realm.where(Order.class).notEqualTo(Order.FIELD_ID, 0)
                                .equalTo(Order.FIELD_ID, cartId).findFirst();
                        order.getUserItems().clear();
                        order.setTotalPrice(0);
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        //cart is empty
                        sendUpdateNoItemsInCartBroadcast(0);
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
}
