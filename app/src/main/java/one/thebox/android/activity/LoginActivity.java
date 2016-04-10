package one.thebox.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import one.thebox.android.R;

/**
 * Created by Ajeet Kumar Meena on 8/10/15.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private TextView signIn, createAccount, facebookButton, googleButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_login);
        initViews();
    }

    private void initViews() {
        signIn = (TextView) findViewById(R.id.sign_in);
        createAccount = (TextView) findViewById(R.id.create_account);
        facebookButton = (TextView) findViewById(R.id.facebook_button);
        googleButton = (TextView) findViewById(R.id.facebook_button);
        signIn.setOnClickListener(this);
        createAccount.setOnClickListener(this);
        facebookButton.setOnClickListener(this);
        googleButton.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.sign_in: {
                break;
            }
            case R.id.create_account: {
                startActivity(new Intent(this,MobileNumberActivity.class));
                finish();
                break;
            }
            case R.id.facebook_button: {
                break;
            }
            case R.id.google_button: {
                break;
            }
        }
    }
}
