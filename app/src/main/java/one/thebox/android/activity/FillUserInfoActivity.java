package one.thebox.android.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;

import io.realm.RealmList;
import one.thebox.android.Models.order.Order;
import one.thebox.android.Models.User;
import one.thebox.android.Models.update.Setting;
import one.thebox.android.R;
import one.thebox.android.ViewHelper.BoxLoader;
import one.thebox.android.activity.address.AddressActivity;
import one.thebox.android.api.RequestBodies.StoreUserInfoRequestBody;
import one.thebox.android.api.RequestBodies.user.UpdateUserInforRequest;
import one.thebox.android.api.Responses.UserSignInSignUpResponse;
import one.thebox.android.api.Responses.user.UpdateUserInfoResponse;
import one.thebox.android.app.Constants;
import one.thebox.android.app.TheBox;
import one.thebox.android.services.AuthenticationService;
import one.thebox.android.services.SettingService;
import one.thebox.android.util.AppUtil;
import one.thebox.android.util.CoreGsonUtils;
import one.thebox.android.util.FusedLocationService;
import one.thebox.android.util.PrefUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static one.thebox.android.app.Constants.EXTRA_ADDRESS_TYPE;
import static one.thebox.android.app.Constants.EXTRA_LIST_ORDER;

public class FillUserInfoActivity extends BaseActivity implements View.OnClickListener {

    private RealmList<Order> orders;

    private static final int REQ_CODE_GET_LOCATION = 101;
    String name, email;
    private TextView submitButton;
    private EditText nameEditText, emailEditText;
    private AuthenticationService authenticationService;
    private double latitude = 0.0, longitude = 0.0;
    private int locationPermisionCounter = 0;
    private View parentView;

    private boolean isMerge;

    private TextInputLayout textInputLayoutName;
    private TextInputLayout textInputLayoutEmail;

    /**
     * Refactor
     */
    public static Intent newInstance(Context context, boolean isMerge) {
        return new Intent(context, FillUserInfoActivity.class)
                .putExtra(Constants.EXTRA_IS_CART_MERGING, isMerge);
    }


    /**
     * OLD
     */
    public static Intent newInstance(Context context, RealmList<Order> orders) {
        return new Intent(context, FillUserInfoActivity.class)
                .putExtra(EXTRA_LIST_ORDER, CoreGsonUtils.toJson(orders));
    }

    private boolean locationRefreshed;
    private FusedLocationService.MyLocation latLng = new FusedLocationService.MyLocation("0.0", "0.0");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        setToolbar((Toolbar) findViewById(R.id.toolbar));
        setSupportActionBar(getToolbar());
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("User Information");

        initViews();
        initVariable();

        setStatusBarColor(getResources().getColor(R.color.primary_dark));
    }


    private void initViews() {
        parentView = (CoordinatorLayout) findViewById(R.id.parent_layout);
        submitButton = (TextView) findViewById(R.id.button_submit);
        submitButton.setOnClickListener(this);
        nameEditText = (EditText) findViewById(R.id.edit_text_name);
        emailEditText = (EditText) findViewById(R.id.edit_text_email);
        textInputLayoutName = (TextInputLayout) findViewById(R.id.name_text_input);
        textInputLayoutEmail = (TextInputLayout) findViewById(R.id.email_text_input);

        authenticationService = new AuthenticationService();
    }

    private void initVariable() {
        try {
            isMerge = getIntent().getBooleanExtra(Constants.EXTRA_IS_CART_MERGING, false);

            orders = CoreGsonUtils.fromJsontoRealmList(getIntent().getStringExtra(EXTRA_LIST_ORDER), Order.class);
        } catch (Exception e) {

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
        UpdateUserInforRequest updateUserInforRequest = new UpdateUserInforRequest(name, email, String.valueOf(latLng), String.valueOf(longitude));
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


    private void fillUserInfo() {
        final BoxLoader dialog = new BoxLoader(this).show();
        TheBox.getAPIService()
                .storeUserInfo(PrefUtils.getToken(this)
                        , new StoreUserInfoRequestBody(new StoreUserInfoRequestBody
                                .User(PrefUtils.getUser(this).getPhoneNumber(), email, name, latitude, longitude)))
                .enqueue(new Callback<UserSignInSignUpResponse>() {
                    @Override
                    public void onResponse(Call<UserSignInSignUpResponse> call, Response<UserSignInSignUpResponse> response) {
                        dialog.dismiss();
                        try {
                            if (response.body() != null) {
                                if (response.body().isSuccess()) {
                                    if (response.body().getUser() != null) {
                                        //restore address
                                        User user = PrefUtils.getUser(FillUserInfoActivity.this);
                                        if (user.getAddresses() != null) {
                                            response.body().getUser().setAddresses(user.getAddresses());
                                        }
                                        PrefUtils.saveUser(FillUserInfoActivity.this, response.body().getUser());
                                        PrefUtils.saveToken(FillUserInfoActivity.this, response.body().getUser().getAccessToken());

                                        //update crashlytics data when user fills details
                                        authenticationService.setUserDataToCrashlytics();
                                        //update clevertap data when user fills details
                                        authenticationService.setCleverTapUserProfile();

                                        //when user fills form move to Address Activity
                                        addDeliverAddress();
                                    }
                                } else {
                                    Toast.makeText(FillUserInfoActivity.this, response.body().getInfo(), Toast.LENGTH_SHORT).show();
                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<UserSignInSignUpResponse> call, Throwable t) {
                        dialog.dismiss();
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
