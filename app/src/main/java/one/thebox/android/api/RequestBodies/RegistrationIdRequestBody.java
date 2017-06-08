package one.thebox.android.api.RequestBodies;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by 32 on 08-06-2016.
 */

public class RegistrationIdRequestBody implements Serializable {

    @SerializedName("registration_id")
    String registrationId;

    public RegistrationIdRequestBody(String registrationId) {
        this.registrationId = registrationId;
    }

    public String getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
    }
}
