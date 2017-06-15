package one.thebox.android.Models.address;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;
import one.thebox.android.Models.address.Locality;

public class Address extends RealmObject implements Serializable {

    @PrimaryKey
    private String uuid;

    @SerializedName("society")
    private String society;

    @SerializedName("flatno")
    private String flat;

    @SerializedName("street")
    private String street;

    private int label;

    @SerializedName("locality_uuid")
    private String localityUuid;

    private boolean isCurrentAddress;


    public Address() {
    }


    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getSociety() {
        return society;
    }

    public void setSociety(String society) {
        this.society = society;
    }

    public String getFlat() {
        return flat;
    }

    public void setFlat(String flat) {
        this.flat = flat;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public int getLabel() {
        return label;
    }

    public void setLabel(int label) {
        this.label = label;
    }

    public String getLocalityUuid() {
        return localityUuid;
    }

    public void setLocalityUuid(String localityUuid) {
        this.localityUuid = localityUuid;
    }

    public boolean isCurrentAddress() {
        return isCurrentAddress;
    }

    public void setCurrentAddress(boolean currentAddress) {
        isCurrentAddress = currentAddress;
    }
}