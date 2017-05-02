package one.thebox.android.Models.address;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Ajeet Kumar Meena on 21-04-2016.
 */
public class Locality extends RealmObject implements Serializable {
    @Ignore
    public static final String FIELD_CODE = "code";

    @Ignore
    public static final String FIELD_NAME = "name";

    @PrimaryKey
    private int code;
    private String name;
    private int pincode;

    public Locality() {
    }

    public Locality(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public Locality(String name, int pincode) {
        this.name = name;
        this.pincode = pincode;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPincode() {
        return pincode;
    }

    public void setPincode(int pincode) {
        this.pincode = pincode;
    }
}
