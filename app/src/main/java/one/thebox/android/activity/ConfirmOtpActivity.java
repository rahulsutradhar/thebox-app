package one.thebox.android.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import one.thebox.android.R;

public class ConfirmOtpActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_otp);
        setTitle("Confirm OTP");
    }

    @Override
    void onClick(int id) {

    }
}
