package one.thebox.android.api.RequestBodies;

import java.io.Serializable;

/**
 * Created by Ajeet Kumar Meena on 21-04-2016.
 */
public class StoreUserInfoRequestBody implements Serializable {

    private User user;

    public StoreUserInfoRequestBody(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public static class User {
        private String phonenumber;
        private String email;
        private String name;
        private String localitycode;
        private double lat;
        private double lng;

        public User(String phonenumber, String email, String name, String localitycode) {
            this.phonenumber = phonenumber;
            this.email = email;
            this.name = name;
            this.localitycode = localitycode;
            this.lat = 0.0;
            this.lng = 0.0;
        }

        public User(String phonenumber, String email, String name, double lat, double lng) {
            this.phonenumber = phonenumber;
            this.email = email;
            this.name = name;
            this.lat = lat;
            this.lng = lng;
        }

        public String getPhonenumber() {
            return phonenumber;
        }

        public void setPhonenumber(String phonenumber) {
            this.phonenumber = phonenumber;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLocalitycode() {
            return localitycode;
        }

        public void setLocalitycode(String localitycode) {
            this.localitycode = localitycode;
        }
    }
}
