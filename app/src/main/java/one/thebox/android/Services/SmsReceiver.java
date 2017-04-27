package one.thebox.android.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import one.thebox.android.Events.SmsEvent;
import one.thebox.android.app.TheBox;

/**
 * Created by Ajeet Kumar Meena on 18-04-2016.
 */
public class SmsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction())) {
            Bundle bundle = intent.getExtras();
            SmsMessage[] messages = null;
            String messageFrom;
            try {
                if (bundle != null) {
                    final Object[] pdusObj = (Object[]) bundle.get("pdus");
                    messages = new SmsMessage[pdusObj.length];
                    for (int i = 0; i < messages.length; i++) {

                        messages[i] = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                        messageFrom = messages[i].getOriginatingAddress();
                        String messageBody = messages[i].getMessageBody();

                        if (messageFrom.contains("THEBOX")) {
                            String otp = extractOtp(messageBody);

                            EventBus.getDefault().post(new SmsEvent(otp));
                        }
                    }
                }

            } catch (Exception e) {

            }
        }
    }

    private String extractOtp(String messageBody) {
        Pattern otpPattern = Pattern.compile("(|^)\\d{6}");
        String otp = null;
        Matcher matcher = otpPattern.matcher(messageBody);
        if (matcher.find()) {
            otp = matcher.group(0);
        } else {
            //something went wrong
        }

        return otp;
    }
}
