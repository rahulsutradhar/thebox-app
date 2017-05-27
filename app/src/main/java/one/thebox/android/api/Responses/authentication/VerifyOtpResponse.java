package one.thebox.android.api.Responses.authentication;

import java.io.Serializable;

import one.thebox.android.Models.User;
import one.thebox.android.api.ApiResponse;

/**
 * Created by developers on 27/05/17.
 */

public class VerifyOtpResponse extends ApiResponse implements Serializable {

    private User user;

    /***********************************
     * Getter Setter
     ***********************************/

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
