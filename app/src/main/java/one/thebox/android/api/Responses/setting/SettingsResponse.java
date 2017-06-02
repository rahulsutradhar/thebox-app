package one.thebox.android.api.Responses.setting;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import one.thebox.android.Models.update.Setting;

/**
 * Created by developers on 02/06/17.
 */

public class SettingsResponse implements Serializable {

    @SerializedName("data")
    private Setting setting;

    public Setting getSetting() {
        return setting;
    }

    public void setSetting(Setting setting) {
        this.setting = setting;
    }
}
