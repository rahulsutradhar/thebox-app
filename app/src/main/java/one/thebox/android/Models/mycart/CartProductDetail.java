package one.thebox.android.Models.mycart;

import java.io.Serializable;
import java.util.List;

import one.thebox.android.Models.items.BoxItem;

/**
 * Created by developers on 22/07/17.
 */

public class CartProductDetail implements Serializable {

    private String boxUuid;
    private String boxTitle;

    private List<BoxItem> boxItems;

    public CartProductDetail(String boxUuid, String boxTitle, List<BoxItem> boxItems) {
        this.boxUuid = boxUuid;
        this.boxTitle = boxTitle;
        this.boxItems = boxItems;
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

    public List<BoxItem> getBoxItems() {
        return boxItems;
    }

    public void setBoxItems(List<BoxItem> boxItems) {
        this.boxItems = boxItems;
    }
}
