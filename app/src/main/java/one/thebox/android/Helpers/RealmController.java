package one.thebox.android.Helpers;

import java.util.List;

import io.realm.Realm;
import one.thebox.android.Models.BoxItem;
import one.thebox.android.Models.UserItem;
import one.thebox.android.app.MyApplication;

/**
 * Created by nbansal2211 on 18/11/16.
 */

public class RealmController {

    public static void addAllBoxItems(List<BoxItem> items) {
        Realm realm = MyApplication.getRealm();
        realm.beginTransaction();
        for (BoxItem item : items) {
            realm.copyToRealmOrUpdate(item);
        }
        realm.commitTransaction();
    }

    public static void addAllUserItems(List<UserItem> items) {
        Realm realm = MyApplication.getRealm();
        realm.beginTransaction();
        for (UserItem item : items) {
            realm.copyToRealmOrUpdate(item);
        }
        realm.commitTransaction();
    }
}