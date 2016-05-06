package one.thebox.android.Events;

/**
 * Created by Ajeet Kumar Meena on 19-04-2016.
 */
public class SmsEvent {
    private String mobileNumber;
    private String message;

    public SmsEvent(String mobileNumber, String message) {
        this.mobileNumber = mobileNumber;
        this.message = message;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
