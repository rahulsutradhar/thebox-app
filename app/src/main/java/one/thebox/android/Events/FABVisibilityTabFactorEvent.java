package one.thebox.android.Events;

/**
 * Created by developers on 14/08/17.
 */

public class FABVisibilityTabFactorEvent {

    private boolean visible;

    public FABVisibilityTabFactorEvent(boolean visible) {
        this.visible = visible;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
