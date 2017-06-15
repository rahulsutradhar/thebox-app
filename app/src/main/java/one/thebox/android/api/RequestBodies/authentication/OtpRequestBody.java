package one.thebox.android.api.RequestBodies.authentication;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Ajeet Kumar Meena on 18-04-2016.
 */
public class OtpRequestBody implements Serializable {

    @SerializedName("user")
    private User user;

    public OtpRequestBody(User user) {
        this.user = user;
    }

    public static class User {
        @SerializedName("phonenumber")
        private String phoneNumber;
        @SerializedName("otp")
        private String otp;

        public User(String phoneNumber, String otp) {
            this.phoneNumber = phoneNumber;
            this.otp = otp;
        }
    }
}
