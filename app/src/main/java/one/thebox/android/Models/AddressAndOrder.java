package one.thebox.android.Models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import one.thebox.android.util.Constants;

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
        calendar.setTime(this.oderDate);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        String monthName = new SimpleDateFormat("MMMM").format(calendar.getTime());
        String dayName = new SimpleDateFormat("EEEE").format(calendar.getTime());
        String hour = new SimpleDateFormat("hh").format(calendar.getTime());
        String minute = new SimpleDateFormat("mm").format(calendar.getTime());
        return getSlotString(hour) + ", " + dayOfMonth + " " + monthName + ", " + dayName;
    }

    public static String getDateString(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        String monthName = new SimpleDateFormat("MMMM").format(calendar.getTime());
        String dayName = new SimpleDateFormat("EEEE").format(calendar.getTime());
        String hour = new SimpleDateFormat("hh").format(calendar.getTime());
        String minute = new SimpleDateFormat("mm").format(calendar.getTime());
        return getSlotString(hour) + ", " + dayOfMonth + " " + monthName + ", " + dayName;
    }

    public static String getSlotString(String hour) {
        for (int i = 0; i < Constants.DATE_RANGE.length; i++) {
            String temp = Constants.DATE_RANGE[i];
            temp = temp.substring(0, 2);
            if (temp.equals(hour)) {
                return Constants.DATE_RANGE[i];
            }
        }
        return "";
    }

   /* public static Date getNextSlotDate(Date currentDate) {

    }
*/
}
