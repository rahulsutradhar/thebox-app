package one.thebox.android.api.Responses;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import one.thebox.android.Models.User;
import one.thebox.android.api.ApiResponse;

/**
 * Created by 32 on 25-04-2016.
 */
public class AddressesApiResponse extends ApiResponse implements Serializable {
    @SerializedName("success")
    private boolean success;
    @SerializedName("info")
    private String info;
    @SerializedName("address")
    private User.Address address;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public User.Address getAddress() {
        return address;
    }

    public void setAddress(User.Address address) {
        this.address = address;
    }
}
