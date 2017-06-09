package one.thebox.android.Events;

import one.thebox.android.Models.items.Category;

/**
 * Created by Ajeet Kumar Meena on 11-05-2016.
 */
public class OnCategorySelectEvent {
    private Category category;

    public OnCategorySelectEvent(Category category) {
        this.category = category;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
