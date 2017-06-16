package one.thebox.android.Models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by developers on 16/06/17.
 */

public class Meta implements Serializable {

    @SerializedName("total_items")
    private int totalItems;

    @SerializedName("page")
    private int currentPage;

    @SerializedName("per_page")
    private int perPage;

    /*****************************************
     * Getter Setter
     *****************************************/

    public int getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getPerPage() {
        return perPage;
    }

    public void setPerPage(int perPage) {
        this.perPage = perPage;
    }
}
