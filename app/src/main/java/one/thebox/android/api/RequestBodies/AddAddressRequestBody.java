package one.thebox.android.api.RequestBodies;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import one.thebox.android.Models.User;

/**
 * Created by 32 on 25-04-2016.
 */
public class AddAddressRequestBody implements Serializable {
    @SerializedName("address")
    Address address;

    public AddAddressRequestBody(Address address) {
        this.address = address;
    }

    public static class Address {
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

        public Address(int code, int label, String flatNo, String society, String street) {
            this.code = code;
            this.label = label;
            this.flatNo = flatNo;
            this.society = society;
            this.street = street;
        }

        public int getLabel() {
            return label;
        }

        public void setLabel(int label) {
            this.label = label;
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
