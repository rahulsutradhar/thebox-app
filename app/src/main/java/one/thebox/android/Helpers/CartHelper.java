package one.thebox.android.Helpers;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;


import io.realm.Realm;
import one.thebox.android.Models.Order;
import one.thebox.android.Models.UserItem;
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
                //Toast.makeText(getActivity(), "success", Toast.LENGTH_SHORT).showTimeSlotBottomSheet();
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

    public static void addOrUpdateUserItem(final UserItem userItem, Order cart) {
//        if (userItem.getNextDeliveryScheduledAt() != null) {
//            return;
//        }
        final int cartId = PrefUtils.getUser(TheBox.getInstance()).getCartId();
        Realm realm = TheBox.getRealm();

        realm.beginTransaction();
        realm.copyToRealmOrUpdate(userItem);
        realm.commitTransaction();

        if (cart != null) {
            OrderHelper.addAndNotify(cart);
            sendUpdateNoItemsInCartBroadcast(cart.getUserItems().size());
        }


//        realm.executeTransactionAsync
//                (new Realm.Transaction() {
//                    @Override
//                    public void execute(Realm realm) {
//                        Order order = realm.where(Order.class).equalTo(Order.FIELD_ID, cartId).findFirst();
//                        RealmList<UserItem> userItems = order.getUserItems();
//                        boolean arrayContainsUserItem = false;
//                        for (int i = 0; i < userItems.size(); i++) {
//                            if (userItem.getId() == userItems.get(i).getId()) {
//                                userItems.set(i, userItem);
//                                arrayContainsUserItem = true;
//                                break;
//                            }
//                        }
//                        if (!arrayContainsUserItem) {
//                            userItems.add(userItem);
//                        }
//                        sendUpdateNoItemsInCartBroadcast(userItems.size());
//                    }
//                }, new Realm.Transaction.OnSuccess() {
//                    @Override
//                    public void onSuccess() {
//                    }
//                }, new Realm.Transaction.OnError() {
//                    @Override
//                    public void onError(Throwable error) {
//
//                    }
//                });

    }

    public static void removeUserItem(final int userItemId, Order cart) {
        final int cartId = PrefUtils.getUser(TheBox.getInstance()).getCartId();
        Realm realm = TheBox.getRealm();
        realm.beginTransaction();
        realm.where(UserItem.class).equalTo("id", userItemId).findFirst().deleteFromRealm();
        realm.commitTransaction();
        if (cart != null) {
            OrderHelper.addAndNotify(cart);
            sendUpdateNoItemsInCartBroadcast(cart.getUserItems().size());
        }
//
//        Order order = realm.where(Order.class).equalTo(Order.FIELD_ID, cart.getId()).findFirst();
//        RealmList<UserItem> userItems = order.getUserItems();
//        for (int i = 0; i < userItems.size(); i++) {
//            if (userItemId == userItems.get(i).getId()) {
//                if (userItems.get(i).getNextDeliveryScheduledAt() != null) {
//                    return;
//                }
//                userItems.remove(i);
//            }
//        }
//        sendUpdateNoItemsInCartBroadcast(userItems.size());

//        realm.executeTransactionAsync
//                (new Realm.Transaction() {
//                    @Override
//                    public void execute(Realm realm) {
//                        Order order = realm.where(Order.class).equalTo(Order.FIELD_ID, cartId).findFirst();
//                        RealmList<UserItem> userItems = order.getUserItems();
//                        boolean arrayContainsUserItem = false;
//                        for (int i = 0; i < userItems.size(); i++) {
//                            if (userItemId == userItems.get(i).getId()) {
//                                if (userItems.get(i).getNextDeliveryScheduledAt() != null) {
//                                    return;
//                                }
//                                userItems.remove(i);
//                            }
//                        }
//                        sendUpdateNoItemsInCartBroadcast(userItems.size());
//                    }
//                }, new Realm.Transaction.OnSuccess() {
//                    @Override
//                    public void onSuccess() {
//                    }
//                }, new Realm.Transaction.OnError() {
//                    @Override
//                    public void onError(Throwable error) {
//
//                    }
//                });
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
        Log.d("PAYMENT_ITEM_AF_ORDR ", numberOfItem + " ");
        Intent intent = new Intent(SearchDetailFragment.BROADCAST_EVENT_TAB);
        // add data
        intent.putExtra(SearchDetailFragment.EXTRA_NUMBER_OF_TABS, numberOfItem);
        LocalBroadcastManager.getInstance(TheBox.getInstance()).sendBroadcast(intent);
    }
}
