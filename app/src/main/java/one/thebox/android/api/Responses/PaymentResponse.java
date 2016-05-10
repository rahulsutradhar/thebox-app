package one.thebox.android.api.Responses;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import one.thebox.android.api.ApiResponse;

/**
 * Created by Ajeet Kumar Meena on 02-05-2016.
 */
public class PaymentResponse extends ApiResponse implements Serializable {
    @SerializedName("success")
    private boolean success;
    @SerializedName("info")
    private String info;
    @SerializedName("orders_to_pay_for")
    private String orders;

    public boolean isSuccess() {
        return success;
    }

    public String getInfo() {
        return info;
    }

    public String getOrders() {
        return orders;
    }
}