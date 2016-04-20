package one.thebox.android.Models;

/**
 * Created by Ajeet Kumar Meena on 13-04-2016.
 */
public class SearchResult {
    public static class SearchSuggestion{
        private boolean isSelected;

        public boolean isSelected() {
            return isSelected;
        }

        public void setSelected(boolean selected) {
            isSelected = selected;
        }
    }
}
