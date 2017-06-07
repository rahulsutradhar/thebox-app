package one.thebox.android.Models.reschedule;

import java.io.Serializable;

/**
 * Created by developers on 05/06/17.
 */

public class RescheduleReason implements Serializable {

    private String uuid;

    private String name;

    private boolean enabled;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
