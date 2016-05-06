package one.thebox.android.api.Responses;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import one.thebox.android.Models.UserItem;
import one.thebox.android.api.ApiResponse;

/**
 * Created by Ajeet Kumar Meena on 05-05-2016.
 */
public class MergeSubscriptionResponse extends ApiResponse implements Serializable {

    @SerializedName("useritem")
    UserItem userItem;

    public UserItem getUserItem() {
        return userItem;
    }
}
