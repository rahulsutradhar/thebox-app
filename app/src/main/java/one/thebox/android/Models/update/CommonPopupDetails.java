package one.thebox.android.Models.update;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by developers on 02/05/17.
 */

public class CommonPopupDetails implements Serializable {

    private String title;

    private String content;

    @SerializedName("button_text")
    private String buttonText;

    private boolean show;

    /****************************************
     * Getters & Setters
     ***************************************/
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getButtonText() {
        return buttonText;
    }

    public void setButtonText(String buttonText) {
        this.buttonText = buttonText;
    }

    public boolean isShow() {
        return show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }
}
