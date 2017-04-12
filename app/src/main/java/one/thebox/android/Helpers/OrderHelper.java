package one.thebox.android.Helpers;

import android.util.Log;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import one.thebox.android.Events.UpdateCartEvent;
import one.thebox.android.Events.UpdateOrderItemEvent;
import one.thebox.android.Events.UpdateUpcomingDeliveriesEvent;
import one.thebox.android.Models.Address;
import one.thebox.android.Models.AddressAndOrder;
import one.thebox.android.Models.Order;
import one.thebox.android.Models.User;
import one.thebox.android.Models.UserItem;
import one.thebox.android.api.Responses.OrdersApiResponse;
import one.thebox.android.app.TheBox;
import one.thebox.android.util.NotificationHelper;
import one.thebox.android.util.NotificationInfo;
import one.thebox.android.util.PrefUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Ajeet Kumar Meena on 15-06-2016.
 */

public class OrderHelper {

    public static void addAndNotify(RealmList<Order> orders) {
        if (orders == null) {
            return;
        }
        saveToRealm(orders);
    }

    public static void addAndNotify(Order order) {
        if (order == null) {
            return;
        }
        saveToRealm(order);
    }

    public static void addAndNotify(RealmList<Order> orders, int notify) {
        if (orders == null) {
            return;
        }
        saveToRealm(orders, notify);
    }

    public static void build_and_show_order_delivered_notification() {
        NotificationInfo.NotificationAction content_action = new NotificationInfo.NotificationAction(10, "");
        NotificationInfo notificationInfo = new NotificationInfo(10, "Delivery Done", "Why don't you add more items for next delivery?", 0, content_action);
        new NotificationHelper(TheBox.getInstance(), notificationInfo).show();
        Log.v("Notification shown", "Here");
    }

    /**
     * Used to notify any perticular List
     */
    public static void getOrderAndNotify(final int notifyTo, final boolean shallNotifyAll) {
        TheBox.getAPIService().getMyOrders(PrefUtils.getToken(TheBox.getInstance())).enqueue(
                new Callback<OrdersApiResponse>() {
                    @Override
                    public void onResponse(Call<OrdersApiResponse> call, Response<OrdersApiResponse> response) {
                        if (response.body() != null) {
                            if (response.body().isSuccess()) {
                                if (shallNotifyAll) {
                                    addAndNotify(response.body().getOrders());
                                } else {
                                    addAndNotify(response.body().getOrders(), notifyTo);
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<OrdersApiResponse> call, Throwable t) {

                    }
                }
        );
    }

    public static void getOrderAndNotify(final Boolean show_notification) {
        TheBox.getAPIService().getMyOrders(PrefUtils.getToken(TheBox.getInstance())).enqueue(
                new Callback<OrdersApiResponse>() {
                    @Override
                    public void onResponse(Call<OrdersApiResponse> call, Response<OrdersApiResponse> response) {
                        if (response.body() != null) {
                            if (response.body().isSuccess()) {
                                addAndNotify(response.body().getOrders());
                                if (show_notification == true) {
                                    build_and_show_order_delivered_notification();
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<OrdersApiResponse> call, Throwable t) {

                    }
                }
        );
    }


    public static void updateUserItemAndNotifiy(final UserItem userItem) {
        Realm realm = TheBox.getRealm();
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(userItem);
        realm.commitTransaction();

        sendUpdateOrderItemBroadcast();
    }

    public static void updateUserItemAndNotifiy(final UserItem userItem, int notifyTo) {
        Realm realm = TheBox.getRealm();
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(userItem);
        realm.commitTransaction();

        sendBroadCast(notifyTo);
    }

    public static void sendBroadCast(int notigfy) {
        switch (notigfy) {
            case 1:
                //subscription
                sendBroadCastToSubscription();
                break;
            case 2:
                //deliveries
                sendBroadCastToDeliveries();
                break;
            case 3:
                //cart
                sendBroadCastToCart();
                break;
            case 4:
                //subscription and Delivery
                sendBroadCastToSubscription();
                sendBroadCastToDeliveries();
                break;
        }
    }

    public static void updateUserItem(final UserItem userItem) {
        Realm realm = TheBox.getRealm();
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(userItem);
        realm.commitTransaction();
    }


    private static void saveToRealm(final RealmList<Order> orders) {
        Realm realm = TheBox.getRealm();
        realm.beginTransaction();
        realm.where(Order.class).notEqualTo(Order.FIELD_ID, PrefUtils.getUser(TheBox.getInstance()).getCartId()).findAll().deleteAllFromRealm();
        realm.copyToRealmOrUpdate(orders);
        realm.commitTransaction();
        sendUpdateOrderItemBroadcast();
    }

    private static void saveToRealm(final RealmList<Order> orders, int notifyTo) {
        Realm realm = TheBox.getRealm();
        realm.beginTransaction();
        realm.where(Order.class).notEqualTo(Order.FIELD_ID, PrefUtils.getUser(TheBox.getInstance()).getCartId()).findAll().deleteAllFromRealm();
        realm.copyToRealmOrUpdate(orders);
        realm.commitTransaction();

        sendBroadCast(notifyTo);
    }


    /**
     * Used to update the cart items
     */
    private static void saveToRealm(final Order order) {
        try {
            final Realm superRealm1 = TheBox.getRealm();
            superRealm1.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    if (order.getUserItems() != null) {
                        if (order.getUserItems().size() > 0) {
                            realm.copyToRealmOrUpdate(order);
                        } else {
                            //delete the order row
                            realm.where(Order.class).equalTo("id", order.getId()).findFirst().deleteFromRealm();
                        }
                    }
                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {
                    sendUpdateOrderItemBroadcast();
                }
            }, new Realm.Transaction.OnError() {
                @Override
                public void onError(Throwable error) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface OnOrdersFetched {
        void OnOrdersFetched();
    }

    private static void sendUpdateOrderItemBroadcast() {
        EventBus.getDefault().post(new UpdateUpcomingDeliveriesEvent());
        EventBus.getDefault().post(new UpdateCartEvent(1));
        EventBus.getDefault().post(new UpdateOrderItemEvent());
    }

    private static void sendBroadCastToDeliveries() {
        EventBus.getDefault().post(new UpdateUpcomingDeliveriesEvent());
    }

    private static void sendBroadCastToCart() {
        EventBus.getDefault().post(new UpdateCartEvent(1));
    }

    private static void sendBroadCastToSubscription() {
        EventBus.getDefault().post(new UpdateOrderItemEvent());
    }

    public static void getOrderAndNotifySynchronusly() {
        try {
            RealmList<Order> realmList = TheBox.getAPIService().getMyOrders(PrefUtils.getToken(TheBox.getInstance())).execute().body().getOrders();
            Realm realm = TheBox.getRealm();
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(realmList);
            realm.commitTransaction();
            sendUpdateOrderItemBroadcast();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static synchronized ArrayList<AddressAndOrder> getAddressAndOrder(RealmList<Order> orders) {
        ArrayList<AddressAndOrder> addressAndOrders = new ArrayList<AddressAndOrder>();
        Address address = null;
        try {
            User user = PrefUtils.getUser(TheBox.getAppContext());
            if (user.getAddresses().size() > 0) {
                address = user.getAddresses().first();
            }
            if (address != null) {
                for (Order order : orders) {
                    addressAndOrders.add(new AddressAndOrder(address.getId(), order.getId()));
                }
            }
        } catch (Exception e) {
        }
        return addressAndOrders;
    }

    public static synchronized boolean isOrderExist() {
        boolean flag = false;

        Realm realm = TheBox.getRealm();
        RealmResults<Order> realmResults = realm.where(Order.class)
                .notEqualTo(Order.FIELD_ID, PrefUtils.getUser(TheBox.getInstance()).getCartId()).findAll()
                .where().notEqualTo("cart", true).findAll();

        if (realmResults.isLoaded()) {
            if (realmResults != null) {
                if (realmResults.size() > 0) {
                    flag = true;
                }
            }
        }

        return flag;
    }

}
