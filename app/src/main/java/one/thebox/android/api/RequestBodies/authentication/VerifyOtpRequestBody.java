package one.thebox.android.api.RequestBodies.authentication;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by developers on 27/05/17.
 */

public class VerifyOtpRequestBody implements Serializable {

    /**
     * Unique Id
     */
    private String uuid;

    /**
     * Otp number
     */
    @SerializedName("verf_code")
    private String otpNumber;

    public VerifyOtpRequestBody(String uuid, String otpNumber) {
        this.uuid = uuid;
        this.otpNumber = otpNumber;
    }

    /**
     * Getter Setter
     */

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getOtpNumber() {
        return otpNumber;
    }

    public void setOtpNumber(String otpNumber) {
        this.otpNumber = otpNumber;
    }
}
