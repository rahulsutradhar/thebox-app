package one.thebox.android.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import one.thebox.android.Models.User;
import one.thebox.android.R;
import one.thebox.android.api.RequestBodies.CreateUserRequestBody;
import one.thebox.android.api.RequestBodies.StoreUserInfoRequestBody;
import one.thebox.android.api.Responses.UserSignInSignUpResponse;
import one.thebox.android.app.MyApplication;
import one.thebox.android.util.PrefUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdatePhoneNumberActivity extends BaseActivity implements View.OnClickListener {

    private TextView updateButton;
    private EditText mobileNumberEditText;
    private String mobileNumber;
    private static final String EXTRA_MOBILE_NUMBER = "extra_mobile_number";
    private User user;

    public static Intent getInstance(Context context, String mobileNumber) {
        return new Intent(context, UpdatePhoneNumberActivity.class).putExtra(EXTRA_MOBILE_NUMBER, mobileNumber);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_phone_number);
        setTitle("Edit Mobile Number");
        initVariables();
        initViews();
    }

    private void initVariables() {
        mobileNumber = getIntent().getStringExtra(EXTRA_MOBILE_NUMBER);
        user = PrefUtils.getUser(this);
    }

    private void initViews() {
        updateButton = (TextView) findViewById(R.id.button_update);
        mobileNumberEditText = (EditText) findViewById(R.id.edit_text_mobile_number);
        updateButton.setOnClickListener(this);
        mobileNumberEditText.setText(mobileNumber);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.button_update: {
                if (isValidInfo()) {
                    singIn();
                }
                break;
            }
        }
    }

    public boolean isValidInfo() {
        if (mobileNumberEditText.getText().toString().isEmpty()) {
            mobileNumberEditText.setError("Mobile Number could not be empty");
            return false;
        }
        mobileNumber = mobileNumberEditText.getText().toString();
        return true;
    }

    public void singIn() {
        final MaterialDialog dialog = new MaterialDialog.Builder(this).progressIndeterminateStyle(true).progress(true, 0).show();
        MyApplication.getAPIService()
                .signIn(new CreateUserRequestBody(new CreateUserRequestBody.User("+91" + mobileNumber)))
                .enqueue(new Callback<UserSignInSignUpResponse>() {
                    @Override
                    public void onResponse(Call<UserSignInSignUpResponse> call, Response<UserSignInSignUpResponse> response) {
                        dialog.dismiss();
                        if (response.body() != null) {
                            if (response.body().isSuccess()) {
                                startActivity(ConfirmOtpActivity.getInstance(UpdatePhoneNumberActivity.this, mobileNumber, false));
                            } else {
                                Toast.makeText(UpdatePhoneNumberActivity.this, response.body().getInfo(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<UserSignInSignUpResponse> call, Throwable t) {
                        dialog.dismiss();
                    }
                });
    }
}
