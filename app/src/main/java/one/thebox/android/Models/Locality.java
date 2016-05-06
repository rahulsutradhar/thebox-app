package one.thebox.android.Models;

/**
 * Created by Ajeet Kumar Meena on 21-04-2016.
 */
public class Locality {
    private int code;
    private String name;

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
