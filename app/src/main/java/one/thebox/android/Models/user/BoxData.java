package one.thebox.android.Models.user;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import one.thebox.android.Models.items.UserItem;
import one.thebox.android.app.AppConstants;

public class BoxData extends RealmObject implements AppConstants.viewTypeInterface {

    @Override
    public int getViewType() {
        return AppConstants.ViewType.BOX_HEADER_ITEM;
    }

    @PrimaryKey
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("user_items")
    @Expose
    private RealmList<UserItem> user_items = null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public RealmList<UserItem> getUser_items() {
        return user_items;
    }

    public void setUser_items(RealmList<UserItem> user_items) {
        this.user_items = user_items;
    }

}
