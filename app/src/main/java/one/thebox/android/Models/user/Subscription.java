package one.thebox.android.Models.user;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;
import one.thebox.android.Models.UserItem;
import one.thebox.android.Models.items.SubscribeItem;

/**
 * Created by developers on 20/02/17.
 */

public class Subscription implements Serializable {

    /**
     * Box id
     */
    @SerializedName("box_id")
    private int boxId;


    /**
     * Refactor
     */

    /**
     * Box Uuid
     */
    @PrimaryKey
    @SerializedName("uuid")
    private String boxUuid;


    /**
     * Title
     */
    private String title;

    /**
     * UserItems
     */
    @SerializedName("subscribed_items")
    private List<SubscribeItem> subscribeItems;

    @Ignore
    private List<SubscribeItem> allSubscribeItem = new ArrayList<>();

    /******************************
     * Getter Setter
     ******************************/

    public int getBoxId() {
        return boxId;
    }

    public void setBoxId(int boxId) {
        this.boxId = boxId;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<SubscribeItem> getSubscribeItems() {
        return subscribeItems;
    }

    public void setSubscribeItems(List<SubscribeItem> subscribeItems) {
        this.subscribeItems = subscribeItems;
    }

    public List<SubscribeItem> getAllSubscribeItem() {
        return allSubscribeItem;
    }

    public void setAllSubscribeItem(List<SubscribeItem> allSubscribeItem) {
        this.allSubscribeItem = allSubscribeItem;
    }

    public String getBoxUuid() {
        return boxUuid;
    }

    public void setBoxUuid(String boxUuid) {
        this.boxUuid = boxUuid;
    }
}
