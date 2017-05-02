package one.thebox.android.api.Responses;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.RealmList;
import one.thebox.android.Models.address.Address;
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

    private RealmList<Address> userAddresses;

    public boolean isSuccess() {
        return success;
    }

    public String getInfo() {
        return info;
    }

    public RealmList<Address> getUserAddresses() {
        return userAddresses;
    }
}
