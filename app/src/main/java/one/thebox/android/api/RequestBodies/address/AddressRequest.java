package one.thebox.android.api.RequestBodies.address;

import java.io.Serializable;

import one.thebox.android.Models.address.Address;

/**
 * Created by developers on 02/06/17.
 */

public class AddressRequest implements Serializable {

    private Address address;

    public AddressRequest(Address address) {
        this.address = address;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}
