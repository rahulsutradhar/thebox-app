package one.thebox.android.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;

import one.thebox.android.Models.AddressAndOrder;
import one.thebox.android.Models.User;
import one.thebox.android.Models.address.Locality;
import one.thebox.android.R;
import one.thebox.android.ViewHelper.BoxLoader;
import one.thebox.android.adapter.address.LocalitySpinnerAdapter;
import one.thebox.android.api.RequestBodies.StoreUserInfoRequestBody;
import one.thebox.android.api.Responses.LocalityResponse;
import one.thebox.android.api.Responses.UserSignInSignUpResponse;
import one.thebox.android.app.TheBox;
import one.thebox.android.services.AuthenticationService;
import one.thebox.android.util.AppUtil;
import one.thebox.android.util.Constants;
import one.thebox.android.util.CoreGsonUtils;
import one.thebox.android.util.CustomSnackBar;
import one.thebox.android.util.FusedLocationService;
import one.thebox.android.util.PrefUtils;
import pl.droidsonroids.gif.GifImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FillUserInfoActivity extends BaseActivity implements View.OnClickListener {

    public static final String EXTRA_ADDRESS_AND_ORDERS = "extra_address_and_orders";
    private static final String IS_RESCHEDULING = "is_rescheduling";

    private ArrayList<AddressAndOrder> addressAndOrders;
    private boolean is_rescheduling = false;

    private ArrayList<Locality> localities;
    private int pincode = 0;
    private LocalitySpinnerAdapter spinnerAdapter;
    private static final int REQ_CODE_GET_LOCATION = 101;
    String name, email, locality;
    private TextView submitButton, errorMessageLocality;
    private EditText nameEditText, emailEditText;
    private Spinner spinner;
    private AutoCompleteTextView localityAutoCompleteTextView;
    private GifImageView progressBar;
    private AuthenticationService authenticationService;
    private double latitude = 0.0, longitude = 0.0;
    private int locationPermisionCounter = 0;
    private View parentView;
    private int localityFetchCount = 0;

    private TextInputLayout textInputLayoutName;
    private TextInputLayout textInputLayoutEmail;

    public static Intent newInstance(Context context, ArrayList<AddressAndOrder> addressAndOrders, boolean is_rescheduling) {
        return new Intent(context, FillUserInfoActivity.class)
                .putExtra(EXTRA_ADDRESS_AND_ORDERS, CoreGsonUtils.toJson(addressAndOrders))
                .putExtra(IS_RESCHEDULING, is_rescheduling);
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

        setUpSpinner();
        //fetch from server
        fetchAllLocality();

        setStatusBarColor(getResources().getColor(R.color.primary_dark));
    }


    private void initViews() {
        parentView = (CoordinatorLayout) findViewById(R.id.parent_layout);
        submitButton = (TextView) findViewById(R.id.button_submit);
        submitButton.setOnClickListener(this);
        errorMessageLocality = (TextView) findViewById(R.id.error_message_locality);
        nameEditText = (EditText) findViewById(R.id.edit_text_name);
        emailEditText = (EditText) findViewById(R.id.edit_text_email);
        textInputLayoutName = (TextInputLayout) findViewById(R.id.name_text_input);
        textInputLayoutEmail = (TextInputLayout) findViewById(R.id.email_text_input);
        spinner = (Spinner) findViewById(R.id.spinnerLocality);

        this.localities = new ArrayList<>();
        Locality locality = Constants.Default_LOCALITY;
        this.localities.add(locality);
        authenticationService = new AuthenticationService();
    }

    private void initVariable() {
        try {
            addressAndOrders = CoreGsonUtils.fromJsontoArrayList(getIntent().getStringExtra(EXTRA_ADDRESS_AND_ORDERS), AddressAndOrder.class);
            is_rescheduling = getIntent().getBooleanExtra(IS_RESCHEDULING, false);
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

    /**
     * Fetch All Locality
     */
    public void fetchAllLocality() {
        localityFetchCount++;
        TheBox.getAPIService().getLocality()
                .enqueue(new Callback<LocalityResponse>() {
                    @Override
                    public void onResponse(Call<LocalityResponse> call, Response<LocalityResponse> response) {

                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                localities.clear();
                                localities.addAll(response.body().getLocalities());

                                setUpSpinner();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<LocalityResponse> call, Throwable t) {
                        Toast.makeText(TheBox.getInstance(), "No Internnet", Toast.LENGTH_SHORT).show();
                        CustomSnackBar.showLongSnackBar(parentView, "Please check your Internet connection");
                        if (localityFetchCount < 2) {
                            fetchAllLocality();
                        }
                    }
                });

    }

    public void setUpSpinner() {
        try {
            if (localities.size() > 0) {
                spinnerAdapter = new LocalitySpinnerAdapter(this, localities);
                spinner.setAdapter(spinnerAdapter);
                spinner.setSelection(0);
                pincode = localities.get(0).getPincode();
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        pincode = localities.get(position).getPincode();
                        spinner.setSelection(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });


            }
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        } catch (NullPointerException npe) {
            npe.printStackTrace();
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
                fillUserInfo();
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
                        fillUserInfo();
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
                        fillUserInfo();
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
                    fillUserInfo();
                }
            }

            @Override
            protected void onFailed(ConnectionResult connectionResult) {
                latLng = new MyLocation("0.0", "0.0");
                locationRefreshed = false;
                fillUserInfo();
            }
        };
    }

    private void fillUserInfo() {
        final BoxLoader dialog = new BoxLoader(this).show();
        TheBox.getAPIService()
                .storeUserInfo(PrefUtils.getToken(this)
                        , new StoreUserInfoRequestBody(new StoreUserInfoRequestBody
                                .User(PrefUtils.getUser(this).getPhoneNumber(), email, name, String.valueOf(pincode), latitude, longitude)))
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
                                        PrefUtils.saveToken(FillUserInfoActivity.this, response.body().getUser().getAuthToken());

                                        //update crashlytics data when user fills details
                                        authenticationService.setUserDataToCrashlytics();
                                        //update clevertap data when user fills details
                                        authenticationService.setCleverTapUserProfile();

                                        //navigate to time slot Activity
                                        startActivity(ConfirmTimeSlotActivity.newInstance(FillUserInfoActivity.this,
                                                addressAndOrders, false));
                                        finish();
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

        if (pincode == 0) {
            errorMessageLocality.setVisibility(View.VISIBLE);
            errorMessageLocality.setText("Please select your Locality");
        } else {
            errorMessageLocality.setText("");
            errorMessageLocality.setVisibility(View.GONE);
        }

        //check for locality
        name = nameEditText.getText().toString().trim();
        email = emailEditText.getText().toString().trim();
        return true;
    }
}
