package one.thebox.android.Models.reschedule;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by developers on 01/04/17.
 */

public class Reschedule implements Serializable {

    private String title;

    private String description;

    private int priority;

    @SerializedName("deliveries")
    private ArrayList<Delivery> deliveries;

    @SerializedName("merge_description")
    private String mergeDescription;

    private boolean visible;

    /*******************************
     * Getter Setter
     *******************************/
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public ArrayList<Delivery> getDeliveries() {
        return deliveries;
    }

    public void setDeliveries(ArrayList<Delivery> deliveries) {
        this.deliveries = deliveries;
    }

    public String getMergeDescription() {
        return mergeDescription;
    }

    public void setMergeDescription(String mergeDescription) {
        this.mergeDescription = mergeDescription;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
