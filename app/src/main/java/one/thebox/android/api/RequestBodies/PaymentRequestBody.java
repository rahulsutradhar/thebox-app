package one.thebox.android.api.RequestBodies;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import one.thebox.android.Models.AddressAndOrder;

/**
 * Created by Ajeet Kumar Meena on 30-04-2016.
 */
public class PaymentRequestBody implements Serializable {

    @SerializedName("address_order_dates")
    private ArrayList<AddressOrderDate> addressAndOrders = new ArrayList<>();

    public PaymentRequestBody(ArrayList<AddressAndOrder> addressAndOrders) {
        for (AddressAndOrder addressAndOrder : addressAndOrders) {
            this.addressAndOrders.add(new AddressOrderDate(
                    new AddressOrderDate.Address(addressAndOrder.getAddress().getId()),
                    new AddressOrderDate.Order(addressAndOrder.getOrder().getId()),
                    addressAndOrder.getDateString()
            ));
        }
    }

    public static class AddressOrderDate {

        @SerializedName("address")
        Address address;
        @SerializedName("order")
        Order order;
        @SerializedName("date")
        private String date;

        public AddressOrderDate(Address address, Order order, String date) {
            this.address = address;
            this.order = order;
            this.date = date;
        }

        public static class Address {
            @SerializedName("id")
            private int addressId;

            public Address(int addressId) {
                this.addressId = addressId;
            }
        }

        public static class Order {
            @SerializedName("id")
            private int orderId;

            public Order(int orderId) {
                this.orderId = orderId;
            }
        }
    }
}
