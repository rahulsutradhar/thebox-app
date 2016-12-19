package one.thebox.android.Models.user;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by nbansal2211 on 17/12/16.
 */
public class UserItemResponse {

    @SerializedName("data")
    @Expose
    private List<BoxData> data = null;

    public List<BoxData> getData() {
        return data;
    }

    public void setData(List<BoxData> data) {
        this.data = data;
    }

}
