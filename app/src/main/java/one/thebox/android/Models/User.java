package one.thebox.android.Models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Ajeet Kumar Meena on 19-04-2016.
 */
public class User implements Serializable {
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

    private ArrayList<Address> addresses;

    public User(int userId, String email, String phoneNumber, boolean isOtpConfirmed, String name, String localityCode, String authToken) {
        this.userId = userId;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.isOtpConfirmed = isOtpConfirmed;
        this.name = name;
        this.localityCode = localityCode;
        this.authToken = authToken;
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

    public ArrayList<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(ArrayList<Address> addresses) {
        this.addresses = addresses;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public static class Address {
        public static final String ADDRESS_TYPE_HOME = "Home";
        public static final String ADDRESS_TYPE_OFFICE = "Office";
        public static final String ADDRESS_TYPE_OTHER = "other";
        public final String[] ADDRESS_TYPES = {ADDRESS_TYPE_HOME, ADDRESS_TYPE_OFFICE, ADDRESS_TYPE_OTHER};
        private int type;
        private String label;
        private String flat;
        private String street;
        private Locality locality;
        private boolean isCurrentAddress;

        public Address(int type, String label, String flat, String street, Locality locality, boolean isCurrentAddress) {
            this.type = type;
            this.label = label;
            this.flat = flat;
            this.street = street;
            this.locality = locality;
            this.isCurrentAddress = isCurrentAddress;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getFlat() {
            return flat;
        }

        public void setFlat(String flat) {
            this.flat = flat;
        }

        public String getStreet() {
            return street;
        }

        public void setStreet(String street) {
            this.street = street;
        }

        public Locality getLocality() {
            return locality;
        }

        public void setLocality(Locality locality) {
            this.locality = locality;
        }

        public boolean isCurrentAddress() {
            return isCurrentAddress;
        }

        public void setCurrentAddress(boolean currentAddress) {
            isCurrentAddress = currentAddress;
        }

        public String getAddressTypeName(int type){
            return ADDRESS_TYPES[type];
        }
    }
}
