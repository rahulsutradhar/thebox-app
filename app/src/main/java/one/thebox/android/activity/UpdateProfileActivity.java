package one.thebox.android.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import one.thebox.android.R;

public class UpdateProfileActivity extends BaseActivity {

    private ImageView verifyPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        setTitle("Update Profile");
        initViews();
    }

    private void initViews() {
        verifyPhoneNumber = (ImageView) findViewById(R.id.button_verify_phone);
        verifyPhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UpdateProfileActivity.this, ConfirmOtpActivity.class));
            }
        });
    }

    @Override
    void onClick(int id) {

    }
}
