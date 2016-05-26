package one.thebox.android.Models;

/**
 * Created by Ajeet Kumar Meena on 25-05-2016.
 */

public class IntStringObject {
    private int integer;
    private String string;

    public IntStringObject(int integer, String string) {
        this.integer = integer;
        this.string = string;
    }

    public int getInteger() {
        return integer;
    }

    public void setInteger(int integer) {
        this.integer = integer;
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }
}
