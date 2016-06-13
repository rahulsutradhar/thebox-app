package one.thebox.android.Events;

/**
 * Created by Ajeet Kumar Meena on 11-06-2016.
 */

public class ShowSpecialCardEvent {
    private boolean isVisible;

    public ShowSpecialCardEvent(boolean isVisible) {
        this.isVisible = isVisible;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }
}
