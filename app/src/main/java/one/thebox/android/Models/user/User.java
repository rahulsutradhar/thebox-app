package one.thebox.android.Models.user;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;
import one.thebox.android.Models.address.Address;

/**
 * Created by Ajeet Kumar Meena on 19-04-2016.
 */
public class User extends RealmObject implements Serializable {
    @Ignore
    public static final String FIELD_USER_ID = "id";
    @Ignore
    public static final String FIELD_EMAIL = "id";
    @Ignore
    public static final String FIELD_PHONE_NUMBER = "id";
    @Ignore
    public static final String FIELD_IS_OTP_CONFIRMED = "id";
    @Ignore
    public static final String FIELD_NAME = "id";
    @Ignore
    public static final String FIELD_LOCALITY_CODE = "id";
    @Ignore
    public static final String FIELD_AUTH_TOKEN = "id";
    @Ignore
    public static final String FIELD_ADDRESSES = "addresses";


    /**
     * User Unique Id
     */
    @PrimaryKey
    private String uuid;

    @SerializedName("access_token")
    private String accessToken;

    @SerializedName("name")
    private String name;

    @SerializedName("email")
    private String email;

    @SerializedName("phone_number")
    private String phoneNumber;

    private RealmList<Address> addresses;


    /**
     * Old
     */
    @SerializedName("id")
    private int id;

    @SerializedName("otp_secret_key_confirmed")
    private boolean isOtpConfirmed;


    @SerializedName("localitycode")
    private String localityCode;


    @SerializedName("cart_id")
    private int cartId;

    public User() {
    }

    public User(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.phoneNumber = user.getPhoneNumber();
        this.isOtpConfirmed = user.isOtpConfirmed();
        this.name = user.getName();
        this.localityCode = user.getLocalityCode();
        this.accessToken = user.getAccessToken();
    }

    public User(int id, String email, String phoneNumber, boolean isOtpConfirmed, String name, String localityCode, String accessToken) {
        this.id = id;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.isOtpConfirmed = isOtpConfirmed;
        this.name = name;
        this.localityCode = localityCode;
        this.accessToken = accessToken;
    }

    public int getCartId() {
        return cartId;
    }

    public void setCartId(int cartId) {
        this.cartId = cartId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isOtpConfirmed() {
        return isOtpConfirmed;
    }

    public void setOtpConfirmed(boolean otpConfirmed) {
        isOtpConfirmed = otpConfirmed;
    }


    public String getLocalityCode() {
        return localityCode;
    }

    public void setLocalityCode(String localityCode) {
        this.localityCode = localityCode;
    }

    /**
     * New
     */


    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }


    public String getName() {
        if (name != null) {
            return name;
        }
        return "";
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public RealmList<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(RealmList<Address> addresses) {
        this.addresses = addresses;
    }
}
