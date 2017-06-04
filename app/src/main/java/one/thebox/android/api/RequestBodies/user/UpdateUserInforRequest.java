package one.thebox.android.api.RequestBodies.user;

import java.io.Serializable;

/**
 * Created by developers on 02/06/17.
 */

public class UpdateUserInforRequest implements Serializable {

    private String name;

    private String email;

    private String lat;
    private String lng;

    public UpdateUserInforRequest(String name, String email, String lat, String lng) {
        this.name = name;
        this.email = email;
        this.lat = lat;
        this.lng = lng;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }
}
