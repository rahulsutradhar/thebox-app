package one.thebox.android.activity;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import one.thebox.android.R;
import one.thebox.android.ViewHelper.BoxLoader;
import one.thebox.android.api.RequestBodies.CreateUserRequestBody;
import one.thebox.android.api.Responses.UserSignInSignUpResponse;
import one.thebox.android.app.TheBox;
import one.thebox.android.util.PrefUtils;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.RuntimePermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Ajeet Kumar Meena on 8/10/15.
 */
@RuntimePermissions
public class MobileNumberActivity extends BaseActivity implements View.OnClickListener {

    private TextView submitButton;
    private EditText mobileNumberEditText;
    private String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enter_mobile_number_activity);
        initViews();
        setStatusBarColor(getResources().getColor(R.color.black));
    }

    private void initViews() {
        submitButton = (TextView) findViewById(R.id.submit_button);
        mobileNumberEditText = (EditText) findViewById(R.id.edit_text_mobile_number);
        submitButton.setOnClickListener(this);
        mobileNumberEditText.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.submit_button: {
                if (isValidMobileNumber()) {
                    //submitMobileNumber();
                    MobileNumberActivityPermissionsDispatcher.submitMobileNumberWithCheck(this);
                }

                break;
            }
            case R.id.edit_text_mobile_number: {
                break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MobileNumberActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);

    }

    @NeedsPermission({Manifest.permission.READ_SMS, Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS})
    public void submitMobileNumber() {
        final BoxLoader dialog =   new BoxLoader(this).show();
        TheBox.getAPIService().createNewUser(new CreateUserRequestBody(new CreateUserRequestBody.User("+91" + phoneNumber)))
                .enqueue(new Callback<UserSignInSignUpResponse>() {
                    @Override
                    public void onResponse(Call<UserSignInSignUpResponse> call, Response<UserSignInSignUpResponse> response) {
                        dialog.dismiss();
                        if (response.body() != null) {
                            if (response.body().isSuccess()) {
                                startActivity(OtpVerificationActivity.getInstance(MobileNumberActivity.this, phoneNumber, true));
                            } else {
                                Toast.makeText(MobileNumberActivity.this, response.body().getInfo(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<UserSignInSignUpResponse> call, Throwable t) {
                        dialog.dismiss();
                    }
                });

    }

    public boolean isValidMobileNumber() {
        phoneNumber = mobileNumberEditText.getText().toString();
        if (phoneNumber.isEmpty()) {
            mobileNumberEditText.setError("Mobile Number could not be empty");
            return false;
        }
        if (phoneNumber.length() < 10) {
            mobileNumberEditText.setError("Invalid Mobile Number");
            return false;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!PrefUtils.getToken(this).isEmpty()) {
            finish();
        }
    }

    @OnPermissionDenied({Manifest.permission.READ_SMS, Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS})
    public void showDeniedPermissionToast() {
        Toast.makeText(this, "Sms read permission is required to auto detect otp", Toast.LENGTH_SHORT).show();
    }

    @OnNeverAskAgain({Manifest.permission.READ_SMS, Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS})
    public void showNeverAskForCamera() {
        Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
    }
}
