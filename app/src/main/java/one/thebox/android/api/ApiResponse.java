package one.thebox.android.api;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Represents the model of any expected response from API server
 * <p/>
 * Created by Ajeet Kumar Meena on 30.08.15.
 */
public class ApiResponse implements Serializable {

    int responsecode;

    boolean status;

    String message;

    /**
     * @return Api response code. Only 200 => success
     */
    public int getResponsecode() {
        return responsecode;
    }

    /**
     * @return Api response message. Contains additional message from server about the
     * last request or more info on reason for failure.
     */
    public String getMessage() {
        return message;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
