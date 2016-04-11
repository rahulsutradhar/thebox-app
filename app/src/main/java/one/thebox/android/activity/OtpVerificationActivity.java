package one.thebox.android.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import one.thebox.android.R;

/**
 * Created by Ajeet Kumar Meena on 8/10/15.
 */
public class OtpVerificationActivity extends BaseActivity implements View.OnClickListener {

    EditText otpVerifictionEditText;
    TextView resendButton, noCodeButton, doneButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        initViews();
    }

    private void initViews() {
        otpVerifictionEditText = (EditText) findViewById(R.id.otp_number_edit_text);
        resendButton = (TextView) findViewById(R.id.button_resend);
        noCodeButton = (TextView) findViewById(R.id.button_no_code);
        doneButton = (TextView) findViewById(R.id.done_button);
        doneButton.setOnClickListener(this);
    }

    @Override
    void onClick(int id) {
        switch (id) {
            case R.id.done_button: {
                startActivity(new Intent(this, SignUpActivity.class));
                finish();
                break;
            }
        }
    }
}
