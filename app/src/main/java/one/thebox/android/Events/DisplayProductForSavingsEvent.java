package one.thebox.android.Events;

/**
 * Created by developers on 07/06/17.
 */

public class DisplayProductForSavingsEvent {

    private String boxUuid;

    private String boxTitle;

    public DisplayProductForSavingsEvent(String boxUuid, String boxTitle) {
        this.boxUuid = boxUuid;
        this.boxTitle = boxTitle;
    }

    public String getBoxUuid() {
        return boxUuid;
    }

    public void setBoxUuid(String boxUuid) {
        this.boxUuid = boxUuid;
    }

    public String getBoxTitle() {
        return boxTitle;
    }

    public void setBoxTitle(String boxTitle) {
        this.boxTitle = boxTitle;
    }
}
