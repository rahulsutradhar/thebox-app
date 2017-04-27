package one.thebox.android.Events;

/**
 * Created by Ajeet Kumar Meena on 19-04-2016.
 */
public class SmsEvent {

    private String otp;

    public SmsEvent(String otp) {
        this.otp = otp;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}
