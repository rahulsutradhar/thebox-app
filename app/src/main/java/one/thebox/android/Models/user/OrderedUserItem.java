package one.thebox.android.Models.user;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;
import one.thebox.android.Models.UserItem;

/**
 * Created by developers on 20/02/17.
 */

public class OrderedUserItem extends RealmObject implements Serializable {

    /**
     * Box id
     */
    @PrimaryKey
    @SerializedName("box_id")
    private int boxId;

    /**
     * Title
     */
    private String title;

    /**
     * UserItems
     */
    @SerializedName("useritems")
    private RealmList<UserItem> userItems;

    @Ignore
    private List<UserItem> allUserItems = new RealmList<>();


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

    public RealmList<UserItem> getUserItems() {
        return userItems;
    }

    public void setUserItems(RealmList<UserItem> userItems) {
        this.userItems = userItems;
    }


    public List<UserItem> getAllUserItems() {
        return allUserItems;
    }

    public void setAllUserItems(List<UserItem> allUserItems) {
        this.allUserItems = allUserItems;
    }
}
