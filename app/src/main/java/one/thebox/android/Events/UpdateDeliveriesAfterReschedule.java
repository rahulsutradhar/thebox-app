package one.thebox.android.Events;

/**
 * Created by developers on 07/04/17.
 */

public class UpdateDeliveriesAfterReschedule {

    private int notifyTo;

    private boolean shallNotifyAll;

    public UpdateDeliveriesAfterReschedule() {

    }

    public int getNotifyTo() {
        return notifyTo;
    }

    public void setNotifyTo(int notifyTo) {
        this.notifyTo = notifyTo;
    }

    public boolean isShallNotifyAll() {
        return shallNotifyAll;
    }

    public void setShallNotifyAll(boolean shallNotifyAll) {
        this.shallNotifyAll = shallNotifyAll;
    }
}
