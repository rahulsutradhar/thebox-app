package one.thebox.android.api.Responses;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.RealmList;
import one.thebox.android.Models.Box;
import one.thebox.android.Models.update.Setting;
import one.thebox.android.Models.update.UpdatePopupDetails;
import one.thebox.android.api.ApiResponse;

/**
 * Created by Ajeet Kumar Meena on 26-04-2016.
 */
public class MyBoxResponse extends ApiResponse implements Serializable {

    @SerializedName("userbox")
    private RealmList<Box> boxes;

    @SerializedName("next_order_status")
    private String next_order_status;

    @SerializedName("monthly_bill")
    private String monthly_bill;

    @SerializedName("total_no_of_items")
    private String total_no_of_items;


    public MyBoxResponse(RealmList<Box> boxes, String next_order_status) {
        this.boxes = boxes;
        this.next_order_status = next_order_status;
    }

    public RealmList<Box> getBoxes() {
        return boxes;
    }

    public void setBoxes(RealmList<Box> boxes) {
        this.boxes = boxes;
    }

    public String getNextOrderStatus() {
        return next_order_status;
    }

    public void setNextOrderStatus(String next_order_status) {
        this.boxes = boxes;
    }

    public String getMonthly_bill() {
        return monthly_bill;
    }

    public void setMonthly_bill(String monthly_bill) {
        this.monthly_bill = monthly_bill;
    }

    public String getTotal_no_of_items() {
        return total_no_of_items;
    }

    public void setTotal_no_of_items(String total_no_of_items) {
        this.total_no_of_items = total_no_of_items;
    }
}
