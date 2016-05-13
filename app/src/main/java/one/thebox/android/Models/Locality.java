package one.thebox.android.Models;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Ajeet Kumar Meena on 21-04-2016.
 */
public class Locality extends RealmObject {
    @Ignore
    public static final String FIELD_CODE = "code";

    @Ignore
    public static final String FIELD_NAME = "name";

    @PrimaryKey
    private int code;
    private String name;

    public Locality() {
    }

    public Locality(int code, String name) {
        this.code = code;
        this.name = name;
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
}
