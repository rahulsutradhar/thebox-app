package one.thebox.android.api.RequestBodies;

/**
 * Created by Ajeet Kumar Meena on 25-04-2016.
 */
public class CategoryBoxItemsRequestBody {

    private Category category;

    public CategoryBoxItemsRequestBody(Category category) {
        this.category = category;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public static class Category {
        int id;

        public Category(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }
}
