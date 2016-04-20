package one.thebox.android.api.RequestBodies;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Ajeet Kumar Meena on 18-04-2016.
 */
public class CreateUserRequestBody implements Serializable {

    @SerializedName("user")
    private User user;

    public CreateUserRequestBody(User user) {
        this.user = user;
    }

    public static class User {

        @SerializedName("phonenumber")
        private String phoneNumber;


        public User(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }
    }
}
