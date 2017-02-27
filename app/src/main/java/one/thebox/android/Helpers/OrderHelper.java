package one.thebox.android.Helpers;

import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

import io.realm.Realm;
import io.realm.RealmList;
import one.thebox.android.Events.UpdateCartEvent;
import one.thebox.android.Events.UpdateOrderItemEvent;
import one.thebox.android.Events.UpdateUpcomingDeliveriesEvent;
import one.thebox.android.Models.Order;
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

    public static void build_and_show_order_delivered_notification() {
        NotificationInfo.NotificationAction content_action = new NotificationInfo.NotificationAction(10, "");
        NotificationInfo notificationInfo = new NotificationInfo(10, "Delivery Done", "Why don't you add more items for next delivery?", 0, content_action);
        new NotificationHelper(TheBox.getInstance(), notificationInfo).show();
        Log.v("Notification shown", "Here");
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


    private static void saveToRealm(final RealmList<Order> orders) {
        Realm realm = TheBox.getRealm();
        realm.beginTransaction();
        realm.where(Order.class).notEqualTo(Order.FIELD_ID, PrefUtils.getUser(TheBox.getInstance()).getCartId()).findAll().deleteAllFromRealm();
        realm.copyToRealmOrUpdate(orders);
        realm.commitTransaction();
        sendUpdateOrderItemBroadcast();
    }


    private static void saveToRealm(final Order order) {
        Realm realm = TheBox.getRealm();
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(order);
        realm.commitTransaction();
        sendUpdateOrderItemBroadcast();
    }

    public interface OnOrdersFetched {
        void OnOrdersFetched();
    }

    private static void sendUpdateOrderItemBroadcast() {
        EventBus.getDefault().post(new UpdateUpcomingDeliveriesEvent());
        EventBus.getDefault().post(new UpdateCartEvent(1));
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
}
