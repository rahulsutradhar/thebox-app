package one.thebox.android.api.Responses;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import one.thebox.android.Models.User;
import one.thebox.android.api.ApiResponse;

/**
 * Created by Ajeet Kumar Meena on 27-04-2016.
 */
public class GetAllAddressResponse extends ApiResponse implements Serializable {
    @SerializedName("success")
    private boolean success;
    @SerializedName("info")
    private String info;
    @SerializedName("addresses")
    private ArrayList<User.Address> userAddresses;

    public boolean isSuccess() {
        return success;
    }

    public String getInfo() {
        return info;
    }

    public ArrayList<User.Address> getUserAddresses() {
        return userAddresses;
    }
}
