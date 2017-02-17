package one.thebox.android.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.lang.annotation.Annotation;

import okhttp3.ResponseBody;
import one.thebox.android.Events.SmsEvent;
import one.thebox.android.Helpers.CartHelper;
import one.thebox.android.Helpers.OrderHelper;
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
import retrofit2.Converter;

/**
 * Created by Ajeet Kumar Meena on 8/10/15.
 */
public class OtpVerificationActivity extends BaseActivity implements View.OnClickListener {

    private final static String EXTRA_PHONE_NUMBER = "extra_phone_number";
    private final static String EXTRA_IS_SIGN_UP_ACTIVITY = "extra_is_sign_up_activity";
    private EditText otpVerificationEditText;
    private TextView resendButton, noCodeButton, doneButton, toPhoneNumberTextView;
    private String phoneNumber;
    private String otp;
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

        //track keyboard done option to procees
        otpVerificationEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    //check if otp is valid
                    if (isValidOtp()) {
                        //hide keyboard
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getContentView().getWindowToken(), 0);

                        //request server
                        verifyOtpFromServer(phoneNumber, otp);
                    }
                    return true;
                }
                return false;
            }
        });
    }

    @Subscribe
    public void onSmsEvent(SmsEvent smsEvent) {
        if (smsEvent.getMessage().contains("awesome") || smsEvent.getMessage().toLowerCase().contains("the box")) {
            String otpString = smsEvent.getMessage().substring(smsEvent.getMessage().length() - 6, smsEvent.getMessage().length());

            if (otpString != null || !otpString.isEmpty()) {
                otpVerificationEditText.setText(otpString);
            }
            //request server with otp
            verifyOtpFromServer(phoneNumber, otpString);
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
                    //request server with otp
                    verifyOtpFromServer(phoneNumber, otp);
                }
                break;
            }

            case R.id.button_resend: {
                if (isSignUpActivity) {
                    requestResentOtp(phoneNumber);
                } else {
                    requestResentOtp(phoneNumber);
                }
            }
        }
    }


    /**
     * Verify otp from server
     */
    public void verifyOtpFromServer(String phoneNumber, String otp) {

        final BoxLoader dialog = new BoxLoader(this).show();
        MyApplication.getAPIService()
                .verifyOtp(new OtpRequestBody(new OtpRequestBody.User(phoneNumber, otp)))
                .enqueue(new Callback<UserSignInSignUpResponse>() {
                    @Override
                    public void onResponse(Call<UserSignInSignUpResponse> call, Response<UserSignInSignUpResponse> response) {
                        dialog.dismiss();
                        try {
                            if (response.isSuccessful()) {
                                if (response.body().getUser() != null) {
                                    PrefUtils.saveUser(OtpVerificationActivity.this, response.body().getUser());
                                    PrefUtils.saveToken(OtpVerificationActivity.this, response.body().getUser().getAuthToken());
                                    CartHelper.saveOrdersToRealm(response.body().getCart());
                                    OrderHelper.addAndNotify(response.body().getOrders());
                                    if (response.body().getUser().getEmail() != null && !response.body().getUser().getEmail().isEmpty()) {

                                        Intent intent = new Intent(OtpVerificationActivity.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();

                                    } else {
                                        Intent intent = new Intent(OtpVerificationActivity.this, FillUserInfoActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            } else {
                                if (response != null && !response.isSuccessful() && response.errorBody() != null) {
                                    //parse error send by the server and show message
                                    Converter<ResponseBody, one.thebox.android.api.Responses.UserSignInSignUpResponse> errorConverter =
                                            MyApplication.getRetrofit().responseBodyConverter(one.thebox.android.api.Responses.UserSignInSignUpResponse.class,
                                                    new Annotation[0]);
                                    one.thebox.android.api.Responses.UserSignInSignUpResponse error = errorConverter.convert(
                                            response.errorBody());
                                    //display error message
                                    otpVerificationEditText.setError(error.getInfo());
                                }

                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(OtpVerificationActivity.this, "Something went wrong. Please try again. ", Toast.LENGTH_SHORT).show();

                        }

                    }

                    @Override
                    public void onFailure(Call<UserSignInSignUpResponse> call, Throwable t) {
                        dialog.dismiss();
                        Toast.makeText(OtpVerificationActivity.this, "Something went wrong. Please try again. ", Toast.LENGTH_SHORT).show();
                    }
                });

    }


    private boolean isValidOtp() {
        if (otpVerificationEditText.getText().toString().isEmpty()) {
            otpVerificationEditText.setError("Otp could not be empty");
            return false;
        }
        otp = otpVerificationEditText.getText().toString();
        return true;
    }

    /**
     * Resend Otp to user Phone
     */
    public void requestResentOtp(String phoneNumber) {
        final BoxLoader dialog = new BoxLoader(this).show();
        MyApplication.getAPIService().signIn(new CreateUserRequestBody(new CreateUserRequestBody.User(phoneNumber)))
                .enqueue(new Callback<UserSignInSignUpResponse>() {
                    @Override
                    public void onResponse(Call<UserSignInSignUpResponse> call, Response<UserSignInSignUpResponse> response) {
                        dialog.dismiss();
                    }

                    @Override
                    public void onFailure(Call<UserSignInSignUpResponse> call, Throwable t) {
                        dialog.dismiss();
                        Toast.makeText(OtpVerificationActivity.this, "Something went wrong. Please try again", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
