package one.thebox.android.Models.update;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by nbansal2211 on 03/01/17.
 */

public class Setting implements Serializable {

    private boolean new_version_available;

    private boolean force_update;

    @SerializedName("update_popup_details")
    private UpdatePopupDetails updatePopupDetails;

    @SerializedName("show_details")
    private CommonPopupDetails commonPopupDetails;

    @SerializedName("user_data_available")
    private boolean userDataAvailable;


    /***************************************
     * Getter Setter
     ***************************************/

    public UpdatePopupDetails getUpdatePopupDetails() {
        return updatePopupDetails;
    }

    public void setUpdatePopupDetails(UpdatePopupDetails updatePopupDetails) {
        this.updatePopupDetails = updatePopupDetails;
    }

    public boolean isNew_version_available() {
        return new_version_available;
    }

    public void setNew_version_available(boolean new_version_available) {
        this.new_version_available = new_version_available;
    }

    public boolean isForce_update() {
        return force_update;
    }

    public void setForce_update(boolean force_update) {
        this.force_update = force_update;
    }

    public boolean isUserDataAvailable() {
        return userDataAvailable;
    }

    public void setUserDataAvailable(boolean userDataAvailable) {
        this.userDataAvailable = userDataAvailable;
    }

    public CommonPopupDetails getCommonPopupDetails() {
        return commonPopupDetails;
    }

    public void setCommonPopupDetails(CommonPopupDetails commonPopupDetails) {
        this.commonPopupDetails = commonPopupDetails;
    }
}
