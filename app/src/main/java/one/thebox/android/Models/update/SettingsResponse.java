package one.thebox.android.Models.update;

import java.io.Serializable;

import one.thebox.android.api.ApiResponse;

/**
 * Created by nbansal2211 on 10/01/17.
 */

public class SettingsResponse extends ApiResponse implements Serializable {
    private Setting data;

    public Setting getData() {
        return data;
    }

    public void setData(Setting data) {
        this.data = data;
    }
}
