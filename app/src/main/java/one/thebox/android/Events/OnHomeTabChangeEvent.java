package one.thebox.android.Events;

/**
 * Created by vaibhav on 31/08/16.
 */
public class OnHomeTabChangeEvent {

    private int position;

    public OnHomeTabChangeEvent(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

}
