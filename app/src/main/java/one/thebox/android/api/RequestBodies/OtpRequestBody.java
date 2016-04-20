package one.thebox.android.api.RequestBodies;

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
        private int otp;

        public User(String phoneNumber, int otp) {
            this.phoneNumber = phoneNumber;
            this.otp = otp;
        }
    }
}
