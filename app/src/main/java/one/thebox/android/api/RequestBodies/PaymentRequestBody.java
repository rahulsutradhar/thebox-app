package one.thebox.android.api.RequestBodies;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import one.thebox.android.Models.AddressAndOrder;

/**
 * Created by Ajeet Kumar Meena on 30-04-2016.
 */
public class PaymentRequestBody implements Serializable{

    @SerializedName("address_order_dates")
    private ArrayList<AddressAndOrder> addressAndOrders;

    public PaymentRequestBody(ArrayList<AddressAndOrder> addressAndOrders) {
        this.addressAndOrders = addressAndOrders;
    }
}
