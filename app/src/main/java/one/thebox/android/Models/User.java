package one.thebox.android.Models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Ajeet Kumar Meena on 19-04-2016.
 */
public class User implements Serializable{
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

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}