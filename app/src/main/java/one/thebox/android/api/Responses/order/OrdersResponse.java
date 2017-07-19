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

    @SerializedName("months")
    private Vector<CalenderMonth> calenderMonths;

    @SerializedName("years")
    private Vector<CalenderYear> calenderYears;

    @SerializedName("current_month")
    private int currentMonth;

    @SerializedName("current_year")
    private int currentYear;


    public OrdersResponse() {

    }

    public ArrayList<Order> getOrders() {
        return orders;
    }

    public void setOrders(ArrayList<Order> orders) {
        this.orders = orders;
    }

    public Vector<CalenderMonth> getCalenderMonths() {
        return calenderMonths;
    }

    public void setCalenderMonths(Vector<CalenderMonth> calenderMonths) {
        this.calenderMonths = calenderMonths;
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
}
