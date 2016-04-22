package one.thebox.android.Models;

/**
 * Created by Ajeet Kumar Meena on 21-04-2016.
 */
public class Categories {
    String categoryName;

    public Categories(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
