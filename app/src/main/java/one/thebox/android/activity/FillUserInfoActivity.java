package one.thebox.android.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;

import one.thebox.android.Models.user.User;
import one.thebox.android.Models.update.Setting;
import one.thebox.android.R;
import one.thebox.android.ViewHelper.BoxLoader;
import one.thebox.android.activity.address.AddressActivity;
import one.thebox.android.api.RequestBodies.user.UpdateUserInforRequest;
import one.thebox.android.api.Responses.user.UpdateUserInfoResponse;
import one.thebox.android.app.Constants;
import one.thebox.android.app.TheBox;
import one.thebox.android.services.AuthenticationService;
import one.thebox.android.services.SettingService;
import one.thebox.android.util.AppUtil;
import one.thebox.android.util.FusedLocationService;
import one.thebox.android.util.PrefUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static one.thebox.android.app.Constants.EXTRA_ADDRESS_TYPE;

public class FillUserInfoActivity extends BaseActivity implements View.OnClickListener {


    private static final int REQ_CODE_GET_LOCATION = 101;
    String name, email;
    private TextView submitButton;
    private EditText nameEditText, emailEditText;
    private AuthenticationService authenticationService;
    private double latitude = 0.0, longitude = 0.0;
    private int locationPermisionCounter = 0;
    private Toolbar toolbar;
    private boolean isMerge;

    private LinearLayout progressIndicatorLayout;
    private View progressStep1, progressStep2, progressStep3, progressStep4, progressStep5, progressStep6;
    private TextView progressStepToCheckoutText;

    private TextInputLayout textInputLayoutName;
    private TextInputLayout textInputLayoutEmail;

    private Setting setting;

    public static Intent newInstance(Context context, boolean isMerge) {
        return new Intent(context, FillUserInfoActivity.class)
                .putExtra(Constants.EXTRA_IS_CART_MERGING, isMerge);
    }


    private boolean locationRefreshed;
    private FusedLocationService.MyLocation latLng = new FusedLocationService.MyLocation("0.0", "0.0");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        //Tootalbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        initViews();
        initVariable();

        setStatusBarColor(getResources().getColor(R.color.primary_dark));
    }


    private void initViews() {
        submitButton = (TextView) findViewById(R.id.button_submit);
        submitButton.setOnClickListener(this);
        nameEditText = (EditText) findViewById(R.id.edit_text_name);
        emailEditText = (EditText) findViewById(R.id.edit_text_email);
        textInputLayoutName = (TextInputLayout) findViewById(R.id.name_text_input);
        textInputLayoutEmail = (TextInputLayout) findViewById(R.id.email_text_input);

        //toolbar
        progressIndicatorLayout = (LinearLayout) findViewById(R.id.progress_indicator);
        progressStepToCheckoutText = (TextView) findViewById(R.id.progress_step_text);
        progressStep1 = (View) findViewById(R.id.progress_step1);
        progressStep2 = (View) findViewById(R.id.progress_step2);
        progressStep3 = (View) findViewById(R.id.progress_step3);
        progressStep4 = (View) findViewById(R.id.progress_step4);
        progressStep5 = (View) findViewById(R.id.progress_step5);
        progressStep6 = (View) findViewById(R.id.progress_step6);


        authenticationService = new AuthenticationService();
    }

    private void initVariable() {
        try {
            isMerge = getIntent().getBooleanExtra(Constants.EXTRA_IS_CART_MERGING, false);
            setting = new SettingService().getSettings(this);
            showProgressIndicatorToolbar();
        } catch (Exception e) {

        }
    }

    public void showProgressIndicatorToolbar() {
        if (setting.isFirstOrder()) {
            progressIndicatorLayout.setVisibility(View.VISIBLE);
        } else {
            progressIndicatorLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.button_submit: {
                if (isValidInfo()) {
                    getUserLocation();
                }
                break;
            }
        }
    }

    private void getUserLocation() {
        checkGPSenable();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_GET_LOCATION) {
            checkGPSenable();
            return;
        }
    }

    // Location Marking Issue
    private void checkGPSenable() {
        locationPermisionCounter++;
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            getLocationPermission();
        } else {
            if (locationPermisionCounter > 1) {
                updateUserInfoToServer();
            } else {
                buildAlertMessageNoGps();
            }
        }
    }


    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.gps_error)
                .setCancelable(false)
                .setPositiveButton("Turn On", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), REQ_CODE_GET_LOCATION);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                        updateUserInfoToServer();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void getLocationPermission() {
        if (AppUtil.checkPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)) {
            setLocation();
            return;
        }
        new TedPermission(this).setPermissions(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        setLocation();
                    }

                    @Override
                    public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                        updateUserInfoToServer();
                    }
                }).check();
    }

    private void setLocation() {
        new FusedLocationService(this) {
            @Override
            protected void onSuccess(MyLocation mLastKnownLocation) {
                if (mLastKnownLocation != null) {
                    if (!locationRefreshed) {
                        locationRefreshed = true;
                        setLocation();
                        return;
                    }
                    latLng = new MyLocation(mLastKnownLocation.getLongitude(), mLastKnownLocation.getLatitude());
                    latitude = latLng.getLatitude();
                    longitude = latLng.getLongitude();
                    updateUserInfoToServer();
                }
            }

            @Override
            protected void onFailed(ConnectionResult connectionResult) {
                latLng = new MyLocation("0.0", "0.0");
                locationRefreshed = false;
                updateUserInfoToServer();
            }
        };
    }


    /**
     * Update UserInfo To Server
     */
    private void updateUserInfoToServer() {
        final BoxLoader dialog = new BoxLoader(this).show();
        UpdateUserInforRequest updateUserInforRequest = new UpdateUserInforRequest(name, email, String.valueOf(latitude), String.valueOf(longitude));
        TheBox.getAPIService()
                .updateUserInfo(PrefUtils.getToken(this), updateUserInforRequest)
                .enqueue(new Callback<UpdateUserInfoResponse>() {
                    @Override
                    public void onResponse(Call<UpdateUserInfoResponse> call, Response<UpdateUserInfoResponse> response) {
                        dialog.dismiss();
                        try {
                            if (response.isSuccessful()) {
                                if (response.body() != null) {
                                    updateUserDetailsLocally(response.body().getUser());
                                }
                            } else {
                                if (response.code() == Constants.UNAUTHORIZED) {
                                    new AuthenticationService().navigateToLogin(FillUserInfoActivity.this);
                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(FillUserInfoActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<UpdateUserInfoResponse> call, Throwable t) {
                        dialog.dismiss();
                        Toast.makeText(FillUserInfoActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    /**
     * Update User Details Locally
     */
    public void updateUserDetailsLocally(User userResponse) {
        //updating user data locally
        User user = PrefUtils.getUser(this);
        if (userResponse.getName() != null) {
            user.setName(userResponse.getName());
        }
        if (userResponse.getEmail() != null) {
            user.setEmail(userResponse.getEmail());
        }

        //updating setting data locally
        SettingService settingService = new SettingService();
        Setting setting = settingService.getSettings(this);
        setting.setUserDataAvailable(true);
        settingService.setSettings(this, setting);

        PrefUtils.saveUser(this, user);

        //update crashlytics data when user fills details
        authenticationService.setUserDataToCrashlytics();
        //update clevertap data when user fills details
        authenticationService.setCleverTapUserProfile();

        //when user fills form move to Address Activity
        addDeliverAddress();

    }


    /**
     * Open Add Address Form
     */
    public void addDeliverAddress() {
        //open add address fragment blank
        Intent intent = new Intent(this, AddressActivity.class);
        /**
         * 1- My Account Fragment
         * 2- Cart Fragment
         */
        intent.putExtra("called_from", 2);
        /**
         * 1- add address
         * 2- edit address
         */
        intent.putExtra(EXTRA_ADDRESS_TYPE, 1);
        intent.putExtra(Constants.EXTRA_IS_CART_MERGING, isMerge);
        startActivity(intent);
        finish();
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
            textInputLayoutName.setErrorEnabled(true);
            textInputLayoutName.setError("Name could not be empty!");
            return false;
        } else {
            textInputLayoutName.setErrorEnabled(false);
            textInputLayoutName.setError("");
        }


        if (emailEditText.getText().toString().isEmpty()) {
            textInputLayoutEmail.setErrorEnabled(true);
            textInputLayoutEmail.setError("Email could not be empty");
            return false;
        } else {
            textInputLayoutEmail.setErrorEnabled(false);
            textInputLayoutEmail.setError("");
        }


        if (!isValidEmail(emailEditText.getText().toString())) {
            textInputLayoutEmail.setErrorEnabled(true);
            textInputLayoutEmail.setError("Invalid email address");
            return false;
        } else {
            textInputLayoutEmail.setErrorEnabled(false);
            textInputLayoutEmail.setError("");
        }

        //check for locality
        name = nameEditText.getText().toString().trim();
        email = emailEditText.getText().toString().trim();
        return true;
    }
}
