package one.thebox.android.api.RequestBodies.authentication;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by developers on 27/05/17.
 */

public class SmsOtpRequestBody implements Serializable {

    @SerializedName("phone_number")
    private String phoneNumber;

    public SmsOtpRequestBody(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
