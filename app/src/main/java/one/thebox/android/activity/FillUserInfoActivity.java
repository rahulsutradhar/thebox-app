package one.thebox.android.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import one.thebox.android.R;

public class FillUserInfoActivity extends AppCompatActivity implements View.OnClickListener {

    private Button submitButton;
    private EditText nameEditText, nameEmail, localityEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        initViews();
    }

    private void initViews() {
        submitButton = (Button) findViewById(R.id.button_submit);
        submitButton.setOnClickListener(this);
        nameEditText = (EditText) findViewById(R.id.edit_text_name);
        nameEmail = (EditText) findViewById(R.id.edit_text_email);
        localityEditText = (EditText) findViewById(R.id.edit_text_locality);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.button_submit: {
                if (isValidInfo()) {
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                }
                break;
            }
        }
    }

    public boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }


    public boolean isValidInfo() {
        if (nameEditText.getText().toString().isEmpty()) {
            nameEditText.setError("Name could not be empty!");
            return false;
        }
        if (nameEmail.getText().toString().isEmpty()) {
            nameEmail.setError("Email could not be empty");
            return false;
        }
        if (!isValidEmail(nameEmail.getText().toString())) {
            nameEmail.setError("Invalid email address");
            return false;
        }
        if (localityEditText.toString().isEmpty()) {
            localityEditText.setText("Locality could not be empty");
            return false;
        }
        return true;
    }
}
