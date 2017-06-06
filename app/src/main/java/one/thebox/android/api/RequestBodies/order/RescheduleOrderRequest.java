package one.thebox.android.api.RequestBodies.order;

import java.io.Serializable;

/**
 * Created by developers on 06/06/17.
 */

public class RescheduleOrderRequest implements Serializable {

    private long timestamp;

    public RescheduleOrderRequest(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
