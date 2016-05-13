package one.thebox.android.Models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

public class Address extends RealmObject implements Serializable {
    @Ignore
    public static final String FIELD_ID = "id";
    @Ignore
    public static final String FIELD_TYPE = "type";
    @Ignore
    public static final String FIELD_SOCIETY = "society";
    @Ignore
    public static final String FIELD_STREET = "street";
    @Ignore
    public static final String FIELD_LOCALITY = "locality";
    @Ignore
    public static final String FIELD_IS_CURRENT_ADDRESS = "isCurrentAddress";
    @Ignore
    public static final String FIELD_LABEL = "get_label";
    @Ignore
    public static final String FIELD_CODE = "code";
    @Ignore
    public static final String ADDRESS_TYPE_HOME = "Home";
    @Ignore
    public static final String ADDRESS_TYPE_OFFICE = "Office";
    @Ignore
    public static final String ADDRESS_TYPE_OTHER = "Other";
    @Ignore
    public static final String[] ADDRESS_TYPES = {ADDRESS_TYPE_HOME, ADDRESS_TYPE_OFFICE, ADDRESS_TYPE_OTHER};
    @PrimaryKey
    @SerializedName("id")
    private int id;
    private int type;
    @SerializedName("society")
    private String society;
    @SerializedName("flatno")
    private String flat;
    @SerializedName("street")
    private String street;
    @SerializedName("locality")
    private Locality locality;
    private boolean isCurrentAddress;
    @SerializedName("get_label")
    private String label;
    @SerializedName("code")
    private int code;

    public Address() {
    }

    public Address(int id, int type, String society, String flat, String street, Locality locality, boolean isCurrentAddress, String label) {
        this.id = id;
        this.type = type;
        this.society = society;
        this.flat = flat;
        this.street = street;
        this.locality = locality;
        this.isCurrentAddress = isCurrentAddress;
        this.label = label;
    }

    public static String getAddressTypeName(int type) {
        return ADDRESS_TYPES[type];
    }

    public static int getTypeFromLabel(String label) {
        for (int i = 0; i < ADDRESS_TYPES.length; i++) {
            if (ADDRESS_TYPES[i].equals(label)) {
                return i;
            }
        }
        return 0;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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

    public Locality getLocality() {
        return locality;
    }

    public void setLocality(Locality locality) {
        this.locality = locality;
    }

    public boolean isCurrentAddress() {
        return isCurrentAddress;
    }

    public void setCurrentAddress(boolean currentAddress) {
        isCurrentAddress = currentAddress;
    }
}