package one.thebox.android.Models.update;

import com.google.gson.annotations.SerializedName;

/**
 * Created by nbansal2211 on 03/01/17.
 */

public class UpdatePopupDetails {
    private String icon_url;
    private String title;
    private String subtext1;
    private String subtext2;
    private String subtext3;
    @SerializedName("button_text")
    private String buttonText;

    public String getIcon_url() {
        return icon_url;
    }

    public void setIcon_url(String icon_url) {
        this.icon_url = icon_url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtext1() {
        return subtext1;
    }

    public void setSubtext1(String subtext1) {
        this.subtext1 = subtext1;
    }

    public String getSubtext2() {
        return subtext2;
    }

    public void setSubtext2(String subtext2) {
        this.subtext2 = subtext2;
    }

    public String getSubtext3() {
        return subtext3;
    }

    public void setSubtext3(String subtext3) {
        this.subtext3 = subtext3;
    }

    public String getButtonText() {
        return buttonText;
    }

    public void setButtonText(String buttonText) {
        this.buttonText = buttonText;
    }
}
