package one.thebox.android.activity;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import java.lang.annotation.Annotation;

import okhttp3.ResponseBody;
import one.thebox.android.R;
import one.thebox.android.ViewHelper.BoxLoader;
import one.thebox.android.adapter.BaseRecyclerAdapter;
import one.thebox.android.api.RequestBodies.CreateUserRequestBody;
import one.thebox.android.api.Responses.UserSignInSignUpResponse;
import one.thebox.android.app.MyApplication;
import one.thebox.android.util.PrefUtils;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.RuntimePermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;

@RuntimePermissions
public class SignInActivity extends BaseActivity implements View.OnClickListener {

    private TextView signInButton;
    private EditText mobileNumberEditText;
    private String phoneNumber;
    private String countryCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        initViews();
        setStatusBarColor(getResources().getColor(R.color.black));
    }

    private void initViews() {
        signInButton = (TextView) findViewById(R.id.button_sign_in);
        mobileNumberEditText = (EditText) findViewById(R.id.edit_text_mobile_number);
        //default for India
        countryCode = "+91";
        signInButton.setOnClickListener(this);
        mobileNumberEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 10) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getContentView().getWindowToken(), 0);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //track keyboard done option
        mobileNumberEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (isValidMobileNumber()) {
                        //close keyboard
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getContentView().getWindowToken(), 0);

                        //request server
                        requestSignin();
                    }
                    return true;
                }

                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.button_sign_in: {
                if (isValidMobileNumber()) {
                    SignInActivityPermissionsDispatcher.singInWithCheck(this);
                    break;
                }
            }
        }
    }

    @NeedsPermission({Manifest.permission.READ_SMS, Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS})
    public void singIn() {
        requestSignin();
    }

    /**
     * Request Signin
     */
    public void requestSignin() {
        final BoxLoader dialog = new BoxLoader(this).show();
        MyApplication.getAPIService()
                .signIn(new CreateUserRequestBody(new CreateUserRequestBody.User(countryCode + phoneNumber)))
                .enqueue(new Callback<UserSignInSignUpResponse>() {
                    @Override
                    public void onResponse(Call<UserSignInSignUpResponse> call, Response<UserSignInSignUpResponse> response) {
                        dialog.dismiss();
                        try {
                            if (response.isSuccessful()) {
                                if (response.body().isSuccess()) {
                                    //request successful
                                    startActivity(OtpVerificationActivity.getInstance(SignInActivity.this, phoneNumber, false));
                                } else {
                                    mobileNumberEditText.setError(response.body().getInfo());
                                }
                            } else {
                                if (response != null && !response.isSuccessful() && response.errorBody() != null) {
                                    //parse error send by the server and show message
                                    Converter<ResponseBody, UserSignInSignUpResponse> errorConverter =
                                            MyApplication.getRetrofit().responseBodyConverter(one.thebox.android.api.Responses.UserSignInSignUpResponse.class,
                                                    new Annotation[0]);
                                    one.thebox.android.api.Responses.UserSignInSignUpResponse error = errorConverter.convert(
                                            response.errorBody());
                                    //display error message
                                    mobileNumberEditText.setError(error.getInfo());
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(SignInActivity.this, "Something went wrong. Please try again", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<UserSignInSignUpResponse> call, Throwable t) {
                        dialog.dismiss();
                        Toast.makeText(SignInActivity.this, "Something went wrong. Please try again", Toast.LENGTH_SHORT).show();
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


    @OnPermissionDenied({Manifest.permission.READ_SMS, Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS})
    public void showDeniedPermissionToast() {
        Toast.makeText(this, "Sms read permission is required to auto detect otp", Toast.LENGTH_SHORT).show();
    }

    @OnNeverAskAgain({Manifest.permission.READ_SMS, Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS})
    public void showNeverAskForCamera() {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        SignInActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }
}
