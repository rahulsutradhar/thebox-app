package one.thebox.android.api.Responses.address;

import java.io.Serializable;

import one.thebox.android.Models.address.Address;
import one.thebox.android.api.ApiResponse;

/**
 * Created by developers on 02/06/17.
 */

public class AddressResponse extends ApiResponse implements Serializable {

    private Address address;

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}
