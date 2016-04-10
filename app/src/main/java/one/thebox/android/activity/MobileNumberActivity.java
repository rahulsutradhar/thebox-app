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
public class MobileNumberActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView submitButton;
    private EditText mobileNumberEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enter_mobile_number_activity);
        initViews();
    }

    private void initViews() {
        submitButton = (TextView) findViewById(R.id.submit_button);
        mobileNumberEditText = (EditText) findViewById(R.id.otp_number_edit_text);
        submitButton.setOnClickListener(this);
        mobileNumberEditText.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.submit_button: {
                startActivity(new Intent(this,OtpVerificationActivity.class));
                finish();
                break;
            }
            case R.id.otp_number_edit_text: {
                break;
            }
        }
    }
}
