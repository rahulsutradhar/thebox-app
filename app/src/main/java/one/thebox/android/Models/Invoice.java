package one.thebox.android.Models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/**
 * Created by vaibhav on 26/07/16.
 */
public class Invoice extends RealmObject implements Serializable {
    @Ignore
    public static final String FIELD_ID = "id";

    @PrimaryKey
    @SerializedName("id")
    private int id;
    @SerializedName("useritem_id")
    private int useritem_id;
    @SerializedName("quantity")
    private int quantity;
    @SerializedName("order_id")
    private int orderId;

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public Invoice() {
    }

    public Invoice(int id, int useritem_id, int quantity) {
        this.id = id;
        this.quantity = quantity;
        this.useritem_id = useritem_id;
    }

    public int getUseritem_id() {
        return useritem_id;
    }

    public int getInvoice_quantity() {
        return quantity;
    }

    public int getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        Invoice invoice = (Invoice) o;
        return invoice != null && this.id == invoice.getId()
                && useritem_id == invoice.getUseritem_id() && this.orderId == invoice.getOrderId();
    }

}
