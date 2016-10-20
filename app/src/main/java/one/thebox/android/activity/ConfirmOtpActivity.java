package one.thebox.android.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import one.thebox.android.Events.SmsEvent;
import one.thebox.android.Helpers.CartHelper;
import one.thebox.android.Models.User;
import one.thebox.android.R;
import one.thebox.android.ViewHelper.BoxLoader;
import one.thebox.android.api.RequestBodies.CreateUserRequestBody;
import one.thebox.android.api.RequestBodies.OtpRequestBody;
import one.thebox.android.api.RequestBodies.StoreUserInfoRequestBody;
import one.thebox.android.api.Responses.UserSignInSignUpResponse;
import one.thebox.android.app.MyApplication;
import one.thebox.android.util.PrefUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConfirmOtpActivity extends BaseActivity implements View.OnClickListener {

    EditText otpVerificationEditText;
    TextView resendButton, noCodeButton, doneButton, toPhoneNumberTextView;
    String phoneNumber;
    private String otp;
    private final static String EXTRA_PHONE_NUMBER = "extra_phone_number";
    private final static String EXTRA_IS_SIGN_UP_ACTIVITY = "extra_is_sign_up_activity";
    private boolean isSignUpActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_otp);
        setTitle("Confirm Otp");
        initVariables();
        initViews();
    }

    private void initVariables() {
        phoneNumber = getIntent().getStringExtra(EXTRA_PHONE_NUMBER);
        isSignUpActivity = getIntent().getBooleanExtra(EXTRA_IS_SIGN_UP_ACTIVITY, false);
    }

    public static Intent getInstance(Context context, String phoneNumber, boolean isSignUpActivity) {
        return new Intent(context, ConfirmOtpActivity.class)
                .putExtra(EXTRA_PHONE_NUMBER, "+91" + phoneNumber)
                .putExtra(EXTRA_IS_SIGN_UP_ACTIVITY, isSignUpActivity);

    }

    private void initViews() {
        otpVerificationEditText = (EditText) findViewById(R.id.edit_text_mobile_number);
        resendButton = (TextView) findViewById(R.id.button_resend);
        noCodeButton = (TextView) findViewById(R.id.button_no_code);
        doneButton = (TextView) findViewById(R.id.done_button);
        toPhoneNumberTextView = (TextView) findViewById(R.id.text_view_to_phone_number);
        resendButton.setOnClickListener(this);
        doneButton.setOnClickListener(this);
        toPhoneNumberTextView.setText("to " + phoneNumber);
    }

    @Subscribe
    public void onSmsEvent(SmsEvent smsEvent) {
        if (smsEvent.getMessage().contains("awesome")) {
            otp = smsEvent.getMessage().substring(smsEvent.getMessage().length() - 6, smsEvent.getMessage().length());
            final BoxLoader dialog = new BoxLoader(this).show();
            MyApplication.getAPIService()
                    .verifyOtp(new OtpRequestBody(new OtpRequestBody.User(phoneNumber, otp)))
                    .enqueue(new Callback<UserSignInSignUpResponse>() {
                        @Override
                        public void onResponse(Call<UserSignInSignUpResponse> call, Response<UserSignInSignUpResponse> response) {
                            dialog.dismiss();
                            if (response.body() != null) {
                                if (response.body().isSuccess()) {
                                    if (response.body().getUser() != null) {
                                        PrefUtils.saveUser(ConfirmOtpActivity.this, response.body().getUser());
                                        PrefUtils.saveToken(ConfirmOtpActivity.this, response.body().getUser().getAuthToken());
                                        CartHelper.saveOrdersToRealm(response.body().getCart());
                                        if (response.body().getUser().getEmail() != null && !response.body().getUser().getEmail().isEmpty()) {
                                            startActivity(new Intent(ConfirmOtpActivity.this, MainActivity.class));
                                            finish();
                                        } else {
                                            startActivity(new Intent(ConfirmOtpActivity.this, FillUserInfoActivity.class));
                                            finish();
                                        }
                                    }
                                } else {
                                    Toast.makeText(ConfirmOtpActivity.this, response.body().getInfo(), Toast.LENGTH_SHORT).show();
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

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.done_button: {
                if (isValidOtp()) {
                    final BoxLoader dialog = new BoxLoader(this).show();
                    MyApplication.getAPIService()
                            .verifyOtp(new OtpRequestBody(new OtpRequestBody.User(phoneNumber, otp)))
                            .enqueue(new Callback<UserSignInSignUpResponse>() {
                                @Override
                                public void onResponse(Call<UserSignInSignUpResponse> call, Response<UserSignInSignUpResponse> response) {
                                    dialog.dismiss();
                                    if (response.body() != null) {
                                        if (response.body().isSuccess()) {
                                            if (response.body().getUser() != null) {
                                                PrefUtils.saveToken(ConfirmOtpActivity.this, response.body().getUser().getAuthToken());
                                                CartHelper.saveOrdersToRealm(response.body().getCart());
                                                if (response.body().getUser().getEmail() != null && !response.body().getUser().getEmail().isEmpty()) {
                                                    User user = PrefUtils.getUser(ConfirmOtpActivity.this);
                                                    final BoxLoader dialog = new BoxLoader(ConfirmOtpActivity.this).show();
                                                    MyApplication.getAPIService().updateProfile(
                                                            PrefUtils.getToken(ConfirmOtpActivity.this), new StoreUserInfoRequestBody(
                                                                    new StoreUserInfoRequestBody.User
                                                                            (phoneNumber, user.getEmail(), user.getName(), PrefUtils.getUser(ConfirmOtpActivity.this).getLocalityCode())))
                                                            .enqueue(new Callback<UserSignInSignUpResponse>() {
                                                                @Override
                                                                public void onResponse(Call<UserSignInSignUpResponse> call, Response<UserSignInSignUpResponse> response) {
                                                                    dialog.dismiss();
                                                                    if (response.body() != null) {
                                                                        if (response.body().isSuccess()) {
                                                                            Toast.makeText(ConfirmOtpActivity.this, response.body().getInfo(), Toast.LENGTH_SHORT).show();
                                                                            PrefUtils.saveUser(ConfirmOtpActivity.this, response.body().getUser());
                                                                            startActivity(new Intent(ConfirmOtpActivity.this, MainActivity.class));
                                                                            finish();

                                                                        } else {
                                                                            Toast.makeText(ConfirmOtpActivity.this, response.body().getInfo(), Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }
                                                                }

                                                                @Override
                                                                public void onFailure(Call<UserSignInSignUpResponse> call, Throwable t) {
                                                                    dialog.dismiss();
                                                                }
                                                            });

                                                } else {
                                                    //startActivity(new Intent(ConfirmOtpActivity.this, FillUserInfoActivity.class));
                                                    //finish();
                                                }
                                            }
                                        } else {
                                            Toast.makeText(ConfirmOtpActivity.this, response.body().getInfo(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<UserSignInSignUpResponse> call, Throwable t) {
                                    dialog.dismiss();
                                }
                            });
                }
                break;
            }

            case R.id.button_resend: {
                if (isSignUpActivity) {
                    final BoxLoader dialog = new BoxLoader(this).show();
                    MyApplication.getAPIService().createNewUser(new CreateUserRequestBody(new CreateUserRequestBody.User(phoneNumber)))
                            .enqueue(new Callback<UserSignInSignUpResponse>() {
                                @Override
                                public void onResponse(Call<UserSignInSignUpResponse> call, Response<UserSignInSignUpResponse> response) {
                                    dialog.dismiss();
                                }

                                @Override
                                public void onFailure(Call<UserSignInSignUpResponse> call, Throwable t) {
                                    dialog.dismiss();
                                }
                            });
                } else {
                    final BoxLoader dialog = new BoxLoader(this).show();
                    MyApplication.getAPIService()
                            .signIn(new CreateUserRequestBody(new CreateUserRequestBody.User(phoneNumber)))
                            .enqueue(new Callback<UserSignInSignUpResponse>() {
                                @Override
                                public void onResponse(Call<UserSignInSignUpResponse> call, Response<UserSignInSignUpResponse> response) {
                                    dialog.dismiss();
                                }

                                @Override
                                public void onFailure(Call<UserSignInSignUpResponse> call, Throwable t) {
                                    dialog.dismiss();
                                }
                            });
                }
            }
        }
    }

    public boolean isValidOtp() {
        if (otpVerificationEditText.getText().toString().isEmpty()) {
            otpVerificationEditText.setError("Otp could not be empty");
            return false;
        }
        otp = otpVerificationEditText.getText().toString();
        return true;
    }
}
