package one.thebox.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import one.thebox.android.R;
import one.thebox.android.api.ApiResponse;
import one.thebox.android.app.MyApplication;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Ajeet Kumar Meena on 8/10/15.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView signIn, createAccount, facebookButton, googleButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
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
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.sign_in: {
                startActivity(new Intent(this, SignInActivity.class));
                break;
            }
            case R.id.create_account: {
                startActivity(new Intent(this, MobileNumberActivity.class));
                break;
            }
            case R.id.facebook_button: {
                MyApplication.getAPIService().test("qwerretrtrewt").enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {

                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {

                    }
                });
                break;
            }
            case R.id.google_button: {
                break;
            }
        }
    }

}
