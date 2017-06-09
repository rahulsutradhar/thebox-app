package one.thebox.android.api.Responses.authentication;

import java.io.Serializable;

import one.thebox.android.Models.user.User;
import one.thebox.android.api.ApiResponse;

/**
 * Created by developers on 27/05/17.
 */

public class ResendOtpResponse extends ApiResponse implements Serializable {

    private User user;


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
