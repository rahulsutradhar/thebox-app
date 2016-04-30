package one.thebox.android.Models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Ajeet Kumar Meena on 29-04-2016.
 */
public class AddressAndOrder implements Serializable {
    @SerializedName("address")
    private User.Address address;
    @SerializedName("order")
    private Order order;
    @SerializedName("date")
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

    public String getDateString() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(oderDate);
        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        String monthName = new SimpleDateFormat("MMM").format(calendar.getTime());
        String dayName = new SimpleDateFormat("EEE").format(calendar.getTime());
        return hour + ":" + minute + ", " + dayOfMonth + " " + monthName + ", " + dayName;
    }

    public static String getDateString(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        String monthName = new SimpleDateFormat("MMM").format(calendar.getTime());
        String dayName = new SimpleDateFormat("EEE").format(calendar.getTime());
        return hour + ":" + minute + ", " + dayOfMonth + " " + monthName + ", " + dayName;
    }
}
