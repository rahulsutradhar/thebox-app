package one.thebox.android.api.RequestBodies;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by 32 on 08-06-2016.
 */

public class RegistrationIdRequestBody implements Serializable {

    private Device device;

    public RegistrationIdRequestBody(String registrationId) {
        this.device = new Device(registrationId);
    }

    private class Device {
        @SerializedName("registration_id")
        String registrationId;

        public Device(String registrationId) {
            this.registrationId = registrationId;
        }
    }
}
