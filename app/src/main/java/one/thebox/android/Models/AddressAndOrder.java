package one.thebox.android.Models;

import java.util.Date;

/**
 * Created by Ajeet Kumar Meena on 29-04-2016.
 */
public class AddressAndOrder {
    private User.Address address;
    private Order order;
    private Date oderDate;

    public AddressAndOrder(User.Address address, Order order) {
        this.address = address;
        this.order = order;
    }

    public Date getOderDate() {
        return oderDate;
    }

    public void setOderDate(Date oderDate) {
        this.oderDate = oderDate;
    }

    public User.Address getAddress() {
        return address;
    }

    public void setAddress(User.Address address) {
        this.address = address;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}
