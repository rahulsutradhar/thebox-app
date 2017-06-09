package one.thebox.android.api.Responses.user;

import java.io.Serializable;

import one.thebox.android.Models.user.User;
import one.thebox.android.api.ApiResponse;

/**
 * Created by developers on 02/06/17.
 */

public class UpdateUserInfoResponse extends ApiResponse implements Serializable {

    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
