package one.thebox.android.Models.order;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by developers on 10/07/17.
 */

public class CalenderYear implements Serializable {

    private String name;

    private int priority;

    @SerializedName("current_year")
    private boolean currentYear;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public boolean isCurrentYear() {
        return currentYear;
    }

    public void setCurrentYear(boolean currentYear) {
        this.currentYear = currentYear;
    }
}
