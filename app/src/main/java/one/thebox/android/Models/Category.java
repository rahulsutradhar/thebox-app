package one.thebox.android.Models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Ajeet Kumar Meena on 21-04-2016.
 */
public class Category implements Serializable {

    @SerializedName("id")
    private int id;
    @SerializedName("title")
    private String title;
    @SerializedName("box_id")
    private int boxId;
    private int noOfItems;

    public Category(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public Category(int id, String title, int boxId, int noOfItems) {
        this.id = id;
        this.title = title;
        this.boxId = boxId;
        this.noOfItems = noOfItems;
    }

    public int getBoxId() {
        return boxId;
    }

    public void setBoxId(int boxId) {
        this.boxId = boxId;
    }

    public int getNoOfItems() {
        return noOfItems;
    }

    public void setNoOfItems(int noOfItems) {
        this.noOfItems = noOfItems;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
