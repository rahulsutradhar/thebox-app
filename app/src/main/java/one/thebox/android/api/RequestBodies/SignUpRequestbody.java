package one.thebox.android.api.RequestBodies;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Ajeet Kumar Meena on 18-04-2016.
 */
public class SignUpRequestBody implements Serializable{
    public static class User{
        @SerializedName("phonenumber")
        private String phoneNumber;
        @SerializedName("auth_token")
        private String authToken;
        @SerializedName("email")
        private String email;
        @SerializedName("name")
        private String name;
        @SerializedName("localitycode")
        private String localityCode;

        public User(String phoneNumber, String authToken, String email, String name, String localityCode) {
            this.phoneNumber = phoneNumber;
            this.authToken = authToken;
            this.email = email;
            this.name = name;
            this.localityCode = localityCode;
        }
    }
}
