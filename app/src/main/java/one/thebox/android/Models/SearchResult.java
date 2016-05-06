package one.thebox.android.Models;

/**
 * Created by Ajeet Kumar Meena on 13-04-2016.
 */
public class SearchResult {
    private int id;
    private String result;

    public SearchResult(String result) {
        this.result = result;
    }

    public SearchResult(int id, String result) {
        this.id = id;
        this.result = result;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public static class SearchSuggestion {
        private boolean isSelected;

        public boolean isSelected() {
            return isSelected;
        }

        public void setSelected(boolean selected) {
            isSelected = selected;
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
