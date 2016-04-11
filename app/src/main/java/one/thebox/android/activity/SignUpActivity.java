package one.thebox.android.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import one.thebox.android.R;

public class SignUpActivity extends BaseActivity {

    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        initViews();
    }

    private void initViews() {
        submitButton = (Button) findViewById(R.id.sign_up_button);
        submitButton.setOnClickListener(this);
    }

    @Override
    void onClick(int id) {
        switch (id) {
            case R.id.sign_up_button: {
                startActivity(new Intent(this, MainActivity.class));
                finish();
                break;
            }
        }
    }
}
