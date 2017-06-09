package one.thebox.android.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.lang.annotation.Annotation;
import java.util.HashMap;

import okhttp3.ResponseBody;
import one.thebox.android.Events.SmsEvent;
import one.thebox.android.Models.user.User;
import one.thebox.android.R;
import one.thebox.android.ViewHelper.BoxLoader;
import one.thebox.android.api.RequestBodies.authentication.ResendOtpRequestBody;
import one.thebox.android.api.RequestBodies.authentication.VerifyOtpRequestBody;
import one.thebox.android.api.Responses.authentication.ResendOtpResponse;
import one.thebox.android.api.Responses.authentication.VerifyOtpResponse;
import one.thebox.android.app.Keys;
import one.thebox.android.app.TheBox;
import one.thebox.android.services.AuthenticationService;
import one.thebox.android.services.SettingService;
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
    private final static String EXTRA_USER_UUID = "extra_user_uuid";

    private EditText otpVerificationEditText;
    private TextView resendButton, doneButton, toPhoneNumberTextView;
    private String phoneNumber;
    private String uuid;
    private String otp;
    private AuthenticationService authenticationService;
    private int requestCounter = 0;
    BoxLoader dialog;

    public static Intent getInstance(Context context, String phoneNumber, String uuid) {
        return new Intent(context, OtpVerificationActivity.class)
                .putExtra(EXTRA_PHONE_NUMBER, "+91" + phoneNumber)
                .putExtra(EXTRA_USER_UUID, uuid);


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);
        initVariables();
        initViews();
        setStatusBarColor(getResources().getColor(R.color.black));
    }

    private void initVariables() {
        phoneNumber = getIntent().getStringExtra(EXTRA_PHONE_NUMBER);
        uuid = getIntent().getStringExtra(EXTRA_USER_UUID);
        authenticationService = new AuthenticationService();
    }

    private void initViews() {
        otpVerificationEditText = (EditText) findViewById(R.id.edit_text_mobile_number);
        resendButton = (TextView) findViewById(R.id.button_resend);
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

                        //verfiy otp authentication
                        verifyOtpAuthtentication();
                    }
                    return true;
                }
                return false;
            }
        });
    }

    @Subscribe
    public void onSmsEvent(SmsEvent smsEvent) {
        String otpString = smsEvent.getOtp();
        if (otpString != null || !otpString.isEmpty()) {
            otpVerificationEditText.setText(otpString);
        }
        //verfiy otp authentication
        verifyOtpAuthtentication();
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
                    //verfiy otp authentication
                    verifyOtpAuthtentication();
                }
                break;
            }

            case R.id.button_resend: {
                requestResentOtp();
            }
        }
    }

    /**
     * Verify Otp fro Authentication
     */
    public void verifyOtpAuthtentication() {

        dialog = new BoxLoader(this).show();

        //unique id and otp number
        TheBox.getAPIService()
                .verifyOtpAuth(new VerifyOtpRequestBody(uuid, otp))
                .enqueue(new Callback<VerifyOtpResponse>() {
                    @Override
                    public void onResponse(Call<VerifyOtpResponse> call, Response<VerifyOtpResponse> response) {
                        try {
                            if (response.isSuccessful()) {
                                if (response.body().isStatus()) {

                                    //save data in preferance
                                    PrefUtils.saveUser(OtpVerificationActivity.this, response.body().getUser());

                                    if (response.body().getUser().getAccessToken() != null) {
                                        String accessToken = "Token token=\"" + response.body().getUser().getAccessToken() + "\"";
                                        PrefUtils.saveToken(OtpVerificationActivity.this, accessToken);
                                    }

                                    PrefUtils.putBoolean(OtpVerificationActivity.this, Keys.IS_AUTHENTICATED, true);

                                    //set user information to crashlytics
                                    authenticationService.setUserDataToCrashlytics();
                                    //set user information to CleverTap upon Login
                                    authenticationService.setCleverTapOnLogin();

                                    /*User Login Event*/
                                    setUserLoginEventCleverTap(response.body().getUser());

                                    //Setting Api Call then move to Home
                                    fetchSettingsfromServer();

                                } else {
                                    dialog.dismiss();
                                    otpVerificationEditText.setError(response.body().getMessage());
                                }

                            } else {
                                dialog.dismiss();
                                if (response != null && !response.isSuccessful() && response.errorBody() != null) {
                                    //parse error send by the server and show message
                                    Converter<ResponseBody, VerifyOtpResponse> errorConverter =
                                            TheBox.getRetrofit().responseBodyConverter(one.thebox.android.api.Responses.authentication.RequestOtpResponse.class,
                                                    new Annotation[0]);
                                    one.thebox.android.api.Responses.authentication.VerifyOtpResponse error = errorConverter.convert(
                                            response.errorBody());

                                    //display error message
                                    otpVerificationEditText.setError(error.getMessage());
                                }
                            }
                        } catch (Exception e) {
                            dialog.dismiss();
                            e.printStackTrace();
                            Toast.makeText(OtpVerificationActivity.this, "Something went wrong. Please try again. ", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<VerifyOtpResponse> call, Throwable t) {
                        dialog.dismiss();
                        Toast.makeText(OtpVerificationActivity.this, "Something went wrong. Please try again. ", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    /**
     * Check if otp is not null
     */
    private boolean isValidOtp() {
        if (otpVerificationEditText.getText().toString().isEmpty()) {
            otpVerificationEditText.setError("Otp could not be empty");
            return false;
        }
        otp = otpVerificationEditText.getText().toString();
        return true;
    }

    /**
     * Request Server to send Setting data
     */
    public void fetchSettingsfromServer() {
        requestCounter++;
        new SettingService().fetchSettingsFromServer(this, this, 1);
    }

    /**
     * Check if Setting call is Successful
     */
    public void setServerResponseForSettingsCall(boolean isSuccess) {
        if (isSuccess) {
            dialog.dismiss();
            navigateToHome();
        } else {
            if (requestCounter > 1) {
                dialog.dismiss();
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
                requestCounter = 0;
                navigateToHome();
            } else {
                fetchSettingsfromServer();
            }
        }
    }

    public void navigateToHome() {
        Intent intent = new Intent(OtpVerificationActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    /**
     * ClverTap Event
     */
    public void setUserLoginEventCleverTap(User user) {
        try {
            HashMap<String, Object> userLogin = new HashMap<>();
            userLogin.put("Phone", user.getPhoneNumber());
            userLogin.put("Unique Id", user.getUuid());
            userLogin.put("User_id", user.getId());
            if (user.getEmail() != null) {
                if (!user.getEmail().isEmpty()) {
                    userLogin.put("Email", user.getEmail());
                }
            }
            if (user.getName() != null) {
                if (!user.getName().isEmpty()) {
                    userLogin.put("Name", user.getName());
                }
            }

            TheBox.getCleverTap().event.push("Login", userLogin);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Resend Otp to user Phone
     */
    public void requestResentOtp() {
        final BoxLoader dialog = new BoxLoader(this).show();
        TheBox.getAPIService()
                .resendOtpAuth(new ResendOtpRequestBody(uuid))
                .enqueue(new Callback<ResendOtpResponse>() {
                    @Override
                    public void onResponse(Call<ResendOtpResponse> call, Response<ResendOtpResponse> response) {
                        dialog.dismiss();
                        try {
                            if (response.isSuccessful()) {
                                //if status is false parse error message
                                if (!response.body().isStatus()) {
                                    if (response != null && !response.isSuccessful() && response.errorBody() != null) {
                                        //parse error send by the server and show message
                                        Converter<ResponseBody, ResendOtpResponse> errorConverter =
                                                TheBox.getRetrofit().responseBodyConverter(one.thebox.android.api.Responses.authentication.RequestOtpResponse.class,
                                                        new Annotation[0]);
                                        one.thebox.android.api.Responses.authentication.ResendOtpResponse error = errorConverter.convert(
                                                response.errorBody());

                                        //display error message
                                        Toast.makeText(OtpVerificationActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                                    }

                                }
                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(OtpVerificationActivity.this, "Something went wrong. Please try again. ", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResendOtpResponse> call, Throwable t) {
                        dialog.dismiss();
                        Toast.makeText(OtpVerificationActivity.this, "Something went wrong. Please try again. ", Toast.LENGTH_SHORT).show();
                    }
                });
    }


}
