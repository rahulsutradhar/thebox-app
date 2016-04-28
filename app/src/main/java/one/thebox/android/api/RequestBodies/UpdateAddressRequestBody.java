package one.thebox.android.api.RequestBodies;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Ajeet Kumar Meena on 27-04-2016.
 */
public class UpdateAddressRequestBody implements Serializable {
    @SerializedName("address")
    Address address;

    public UpdateAddressRequestBody(Address address) {
        this.address = address;
    }

    public static class Address {
        @SerializedName("id")
        private int id;
        @SerializedName("code")
        private int code;
        @SerializedName("label")
        private int label;
        @SerializedName("flatno")
        private String flatNo;
        @SerializedName("society")
        private String society;
        @SerializedName("street")
        private String street;

        public Address(int id, int code, int label, String flatNo, String society, String street) {
            this.id = id;
            this.code = code;
            this.label = label;
            this.flatNo = flatNo;
            this.society = society;
            this.street = street;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getLabel() {
            return label;
        }

        public void setLabel(int label) {
            this.label = label;
        }

        public int getId() {
            return id;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getFlatNo() {
            return flatNo;
        }

        public void setFlatNo(String flatNo) {
            this.flatNo = flatNo;
        }

        public String getSociety() {
            return society;
        }

        public void setSociety(String society) {
            this.society = society;
        }

        public String getStreet() {
            return street;
        }

        public void setStreet(String street) {
            this.street = street;
        }
    }
}
