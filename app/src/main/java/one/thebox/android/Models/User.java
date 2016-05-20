package one.thebox.android.Models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Ajeet Kumar Meena on 19-04-2016.
 */
public class User extends RealmObject implements Serializable {
    @Ignore
    public static final String FIELD_USER_ID = "userId";
    @Ignore
    public static final String FIELD_EMAIL = "userId";
    @Ignore
    public static final String FIELD_PHONE_NUMBER = "userId";
    @Ignore
    public static final String FIELD_IS_OTP_CONFIRMED = "userId";
    @Ignore
    public static final String FIELD_NAME = "userId";
    @Ignore
    public static final String FIELD_LOCALITY_CODE = "userId";
    @Ignore
    public static final String FIELD_AUTH_TOKEN = "userId";
    @Ignore
    public static final String FIELD_ADDRESSES = "addresses";

    @PrimaryKey
    @SerializedName("id")
    private int userId;
    @SerializedName("email")
    private String email;
    @SerializedName("phonenumber")
    private String phoneNumber;
    @SerializedName("otp_secret_key_confirmed")
    private boolean isOtpConfirmed;
    @SerializedName("name")
    private String name;
    @SerializedName("localitycode")
    private String localityCode;
    @SerializedName("auth_token")
    private String authToken;
    @SerializedName("cart_id")
    private int cartId;
    private RealmList<Address> addresses;

    public User() {
    }

    public User(User user) {
        this.userId = user.getUserId();
        this.email = user.getEmail();
        this.phoneNumber = user.getPhoneNumber();
        this.isOtpConfirmed = user.isOtpConfirmed();
        this.name = user.getName();
        this.localityCode = user.getLocalityCode();
        this.authToken = user.getAuthToken();
        this.addresses = new RealmList<>();
        if (user.getAddresses() != null) {
            this.addresses.addAll(user.getAddresses());
        }
    }

    public User(int userId, String email, String phoneNumber, boolean isOtpConfirmed, String name, String localityCode, String authToken) {
        this.userId = userId;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.isOtpConfirmed = isOtpConfirmed;
        this.name = name;
        this.localityCode = localityCode;
        this.authToken = authToken;
    }

    public int getCartId() {
        return cartId;
    }

    public void setCartId(int cartId) {
        this.cartId = cartId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isOtpConfirmed() {
        return isOtpConfirmed;
    }

    public void setOtpConfirmed(boolean otpConfirmed) {
        isOtpConfirmed = otpConfirmed;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocalityCode() {
        return localityCode;
    }

    public void setLocalityCode(String localityCode) {
        this.localityCode = localityCode;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public RealmList<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(RealmList<Address> addresses) {
        this.addresses = addresses;
    }
}
