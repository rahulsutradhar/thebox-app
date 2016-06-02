package one.thebox.android.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import one.thebox.android.Events.SmsEvent;
import one.thebox.android.R;
import one.thebox.android.ViewHelper.BoxLoader;
import one.thebox.android.api.RequestBodies.CreateUserRequestBody;
import one.thebox.android.api.RequestBodies.OtpRequestBody;
import one.thebox.android.api.Responses.UserSignInSignUpResponse;
import one.thebox.android.app.MyApplication;
import one.thebox.android.util.PrefUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Ajeet Kumar Meena on 8/10/15.
 */
public class OtpVerificationActivity extends BaseActivity implements View.OnClickListener {

    private final static String EXTRA_PHONE_NUMBER = "extra_phone_number";
    private final static String EXTRA_IS_SIGN_UP_ACTIVITY = "extra_is_sign_up_activity";
    private EditText otpVerificationEditText;
    private TextView resendButton, noCodeButton, doneButton, toPhoneNumberTextView;
    private String phoneNumber;
    private int otp;
    private boolean isSignUpActivity;

    public static Intent getInstance(Context context, String phoneNumber, boolean isSignUpActivity) {
        return new Intent(context, OtpVerificationActivity.class)
                .putExtra(EXTRA_PHONE_NUMBER, "+91" + phoneNumber)
                .putExtra(EXTRA_IS_SIGN_UP_ACTIVITY, isSignUpActivity);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        initVariables();
        initViews();
        setStatusBarColor(getResources().getColor(R.color.black));
    }

    private void initVariables() {
        phoneNumber = getIntent().getStringExtra(EXTRA_PHONE_NUMBER);
        isSignUpActivity = getIntent().getBooleanExtra(EXTRA_IS_SIGN_UP_ACTIVITY, false);
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
        if (smsEvent.getMessage().contains("awesome") || smsEvent.getMessage().toLowerCase().contains("the box")) {
            String otpString = smsEvent.getMessage().substring(smsEvent.getMessage().length() - 6, smsEvent.getMessage().length());
            otp = Integer.parseInt(otpString);
            final BoxLoader dialog =   new BoxLoader(this).show();
            MyApplication.getAPIService()
                    .verifyOtp(new OtpRequestBody(new OtpRequestBody.User(phoneNumber, otp)))
                    .enqueue(new Callback<UserSignInSignUpResponse>() {
                        @Override
                        public void onResponse(Call<UserSignInSignUpResponse> call, Response<UserSignInSignUpResponse> response) {
                            dialog.dismiss();
                            if (response.body() != null) {
                                if (response.body().isSuccess()) {
                                    if (response.body().getUser() != null) {
                                        PrefUtils.saveUser(OtpVerificationActivity.this, response.body().getUser());
                                        PrefUtils.saveToken(OtpVerificationActivity.this, response.body().getUser().getAuthToken());
                                        if (response.body().getUser().getEmail() != null && !response.body().getUser().getEmail().isEmpty()) {
                                            startActivity(new Intent(OtpVerificationActivity.this, MainActivity.class).addFlags((Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK)));
                                            finish();
                                        } else {
                                            startActivity(new Intent(OtpVerificationActivity.this, FillUserInfoActivity.class));
                                            finish();
                                        }
                                    }
                                } else {
                                    Toast.makeText(OtpVerificationActivity.this, response.body().getInfo(), Toast.LENGTH_SHORT).show();
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
                    final BoxLoader dialog =   new BoxLoader(this).show();
                    MyApplication.getAPIService()
                            .verifyOtp(new OtpRequestBody(new OtpRequestBody.User(phoneNumber, otp)))
                            .enqueue(new Callback<UserSignInSignUpResponse>() {
                                @Override
                                public void onResponse(Call<UserSignInSignUpResponse> call, Response<UserSignInSignUpResponse> response) {
                                    dialog.dismiss();
                                    if (response.body() != null) {
                                        if (response.body().isSuccess()) {
                                            if (response.body().getUser() != null) {
                                                PrefUtils.saveUser(OtpVerificationActivity.this, response.body().getUser());
                                                PrefUtils.saveToken(OtpVerificationActivity.this, response.body().getUser().getAuthToken());
                                                if (response.body().getUser().getEmail() != null && !response.body().getUser().getEmail().isEmpty()) {
                                                    startActivity(new Intent(OtpVerificationActivity.this, MainActivity.class).addFlags((Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK)));                                                    finish();
                                                } else {
                                                    startActivity(new Intent(OtpVerificationActivity.this, FillUserInfoActivity.class));
                                                    finish();
                                                }
                                            }
                                        } else {
                                            Toast.makeText(OtpVerificationActivity.this, response.body().getInfo(), Toast.LENGTH_SHORT).show();
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
                    final BoxLoader dialog =   new BoxLoader(this).show();
                    MyApplication.getAPIService().signIn(new CreateUserRequestBody(new CreateUserRequestBody.User(phoneNumber)))
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
                    final BoxLoader dialog =   new BoxLoader(this).show();
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

    private boolean isValidOtp() {
        if (otpVerificationEditText.getText().toString().isEmpty()) {
            otpVerificationEditText.setError("Otp could not be empty");
            return false;
        }
        otp = Integer.parseInt(otpVerificationEditText.getText().toString());
        return true;
    }
}
