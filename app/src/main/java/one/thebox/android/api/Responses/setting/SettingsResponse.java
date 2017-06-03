package one.thebox.android.api.Responses.setting;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import one.thebox.android.Models.update.Setting;
import one.thebox.android.api.ApiResponse;

/**
 * Created by developers on 02/06/17.
 */

public class SettingsResponse extends ApiResponse implements Serializable {

    @SerializedName("data")
    private Setting setting;

    public Setting getSetting() {
        return setting;
    }

    public void setSetting(Setting setting) {
        this.setting = setting;
    }
}
