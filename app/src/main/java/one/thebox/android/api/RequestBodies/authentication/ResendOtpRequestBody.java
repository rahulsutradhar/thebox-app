package one.thebox.android.api.RequestBodies.authentication;

import java.io.Serializable;

/**
 * Created by developers on 27/05/17.
 */

public class ResendOtpRequestBody implements Serializable {

    private String uuid;

    public ResendOtpRequestBody(String uuid) {
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
