package one.thebox.android.Helpers;

import android.util.Log;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Observable;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import one.thebox.android.Models.BoxItem;
import one.thebox.android.Models.Order;
import one.thebox.android.Models.UserItem;
import one.thebox.android.app.MyApplication;

/**
 * Created by nbansal2211 on 09/11/16.
 */

public class RealmChangeManager {
    private static RealmChangeManager instance;
    private Realm realm;
    private HashMap<String, HashSet<DBChangeListener>> listenerMap = new HashMap<>();

    public static RealmChangeManager getInstance() {
        if (instance == null) {
            instance = new RealmChangeManager();
            instance.realm = MyApplication.getRealm();
            instance.init();
        }
        return instance;
    }

    public void addOrderListener(DBChangeListener listener) {
        addDBChangeListener(DBChangeListener.ORDER, listener);
    }

    public void addUserItemListener(DBChangeListener listener) {
        addDBChangeListener(DBChangeListener.USER_ITEM, listener);
    }

    public void addBoxItemListener(DBChangeListener listener) {
        addDBChangeListener(DBChangeListener.BOX_ITEM, listener);
    }

    private void addDBChangeListener(String classType, DBChangeListener listener) {
        HashSet<DBChangeListener> listeners;
        if (listenerMap.containsKey(classType)) {
            listeners = listenerMap.get(classType);
        } else {
            listeners = new HashSet<>();
        }
        listeners.add(listener);
        listenerMap.put(classType, listeners);
    }

    private void init() {
        setOrderChangeListener();
        setUserItemChangeListener();
        setBoxItemChangeListener();
    }

    public void setOrderChangeListener() {
        RealmResults<Order> orderRealmResults = realm.where(Order.class).findAll();
        orderRealmResults.addChangeListener(new RealmChangeListener<RealmResults<Order>>() {
            @Override
            public void onChange(RealmResults<Order> element) {
                Log.d("Order Changes", "Element Changed");
            }
        });
    }

    public void setUserItemChangeListener() {
        RealmResults<UserItem> orderRealmResults = realm.where(UserItem.class).findAllAsync();
        orderRealmResults.addChangeListener(new RealmChangeListener<RealmResults<UserItem>>() {
            @Override
            public void onChange(RealmResults<UserItem> element) {
                Log.d("UserItem Changes", "Element Changed");
            }
        });
    }

    public void setBoxItemChangeListener() {
        RealmResults<BoxItem> orderRealmResults = realm.where(BoxItem.class).findAllAsync();
        orderRealmResults.addChangeListener(new RealmChangeListener<RealmResults<BoxItem>>() {
            @Override
            public void onChange(RealmResults<BoxItem> element) {
                Log.d("BoxItem Changes", "Element Changed");
            }
        });
    }


    public static interface DBChangeListener {
        String ORDER = "order";
        String USER_ITEM = "userItem";
        String BOX_ITEM = "boxItem";

        public void onDBChanged();
    }
}
