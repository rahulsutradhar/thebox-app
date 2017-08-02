package one.thebox.android.api.Responses.order;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Vector;

import one.thebox.android.Models.order.CalenderMonth;
import one.thebox.android.Models.order.CalenderYear;
import one.thebox.android.Models.order.Order;
import one.thebox.android.api.ApiResponse;

/**
 * Created by developers on 06/06/17.
 */

public class OrdersResponse extends ApiResponse implements Serializable {

    private ArrayList<Order> orders;

    @SerializedName("years")
    private Vector<CalenderYear> calenderYears;

    @SerializedName("current_month")
    private int currentMonth;

    @SerializedName("current_year")
    private int currentYear;

    @SerializedName("last_orders")
    private boolean lastOrder;

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;


    public OrdersResponse() {

    }

    public ArrayList<Order> getOrders() {
        return orders;
    }

    public void setOrders(ArrayList<Order> orders) {
        this.orders = orders;
    }

    public int getCurrentMonth() {
        return currentMonth;
    }

    public void setCurrentMonth(int currentMonth) {
        this.currentMonth = currentMonth;
    }

    public int getCurrentYear() {
        return currentYear;
    }

    public void setCurrentYear(int currentYear) {
        this.currentYear = currentYear;
    }

    public Vector<CalenderYear> getCalenderYears() {
        return calenderYears;
    }

    public void setCalenderYears(Vector<CalenderYear> calenderYears) {
        this.calenderYears = calenderYears;
    }

    public boolean isLastOrder() {
        return lastOrder;
    }

    public void setLastOrder(boolean lastOrder) {
        this.lastOrder = lastOrder;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
