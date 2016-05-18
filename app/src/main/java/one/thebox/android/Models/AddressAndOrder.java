package one.thebox.android.Models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import one.thebox.android.app.MyApplication;
import one.thebox.android.util.Constants;
import one.thebox.android.util.PrefUtils;

/**
 * Created by Ajeet Kumar Meena on 29-04-2016.
 */
public class AddressAndOrder implements Serializable {
    @SerializedName("address")
    private int addressId;
    @SerializedName("order")
    private int orderId;
    @SerializedName("date")
    private Date oderDate;
    private User user;

    private transient Address address;
    private transient Order order;

    public AddressAndOrder(int addressId, int orderId) {
        this.addressId = addressId;
        this.orderId = orderId;
        this.user = PrefUtils.getUser(MyApplication.getInstance());
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

    public Date getOderDate() {
        return oderDate;
    }

    public void setOderDate(Date oderDate) {
        this.oderDate = oderDate;
    }

    public int getAddressId() {
        return addressId;
    }

    public void setAddressId(int address) {
        this.addressId = address;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
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

    public Address getAddress() {
        RealmList<Address> addresses = user.getAddresses();
        for (Address address : addresses) {
            if (address.getId() == addressId) {
                return address;
            }
        }
        return null;
    }

    public Order getOrder() {
        Realm realm = MyApplication.getRealm();
        RealmQuery<Order> query = realm.where(Order.class)
                .equalTo(Order.FIELD_ID, orderId);
        RealmResults<Order> realmResults = query.findAll();
        return realmResults.get(0);
    }
}
