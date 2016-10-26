package one.thebox.android.api.RequestBodies;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by vaibhav on 14/10/16.
 */

public class OnlinePaymentRequest implements Serializable {
    @SerializedName("order_id")
    int order_id;
    @SerializedName("razorpay_payment_id")
    String razorpay_payment_id;
    @SerializedName("date")
    private String order_date;

    public OnlinePaymentRequest(int order_id,String razorpay_payment_id,String order_date) {
        this.order_id = order_id;
        this.razorpay_payment_id = razorpay_payment_id;
        this.order_date = order_date;
    }

    public OnlinePaymentRequest(int merge_to_this_order_id,String razorpay_payment_id) {
        this.order_id = merge_to_this_order_id;
        this.razorpay_payment_id = razorpay_payment_id;
    }
}
