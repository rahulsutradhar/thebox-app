package one.thebox.android.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;

import one.thebox.android.Models.Locality;
import one.thebox.android.R;
import one.thebox.android.ViewHelper.BoxLoader;
import one.thebox.android.api.RequestBodies.StoreUserInfoRequestBody;
import one.thebox.android.api.Responses.LocalitiesResponse;
import one.thebox.android.api.Responses.UserSignInSignUpResponse;
import one.thebox.android.app.TheBox;
import one.thebox.android.services.AuthenticationService;
import one.thebox.android.util.AppUtil;
import one.thebox.android.util.Constants;
import one.thebox.android.util.FusedLocationService;
import one.thebox.android.util.PrefUtils;
import pl.droidsonroids.gif.GifImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FillUserInfoActivity extends BaseActivity implements View.OnClickListener {

    private static final int REQ_CODE_GET_LOCATION = 101;
    String name, email, locality;
    Call<LocalitiesResponse> call;
    private Button submitButton;
    private EditText nameEditText, emailEditText;
    private AutoCompleteTextView localityAutoCompleteTextView;
    private GifImageView progressBar;
    private boolean callHasBeenCompleted = true;
    //    {"code":400072,"name":"Powai"}
    private ArrayList<Locality> localities = new ArrayList<>();
    private String[] localitiesSuggestions = new String[0];
    private AuthenticationService authenticationService;

    Callback<LocalitiesResponse> localitiesResponseCallback = new Callback<LocalitiesResponse>() {
        @Override
        public void onResponse(Call<LocalitiesResponse> call, Response<LocalitiesResponse> response) {
            progressBar.setVisibility(View.GONE);
            callHasBeenCompleted = true;
            if (response.body() != null && response.body().getLocalities() != null && !response.body().getLocalities().isEmpty()) {
                localities = new ArrayList<>(response.body().getLocalities());
                localitiesSuggestions = new String[localities.size()];
                for (int i = 0; i < localities.size(); i++) {
                    localitiesSuggestions[i] = localities.get(i).getName();
                }
                setAutoCompleteAdapter();
            }
        }

        @Override
        public void onFailure(Call<LocalitiesResponse> call, Throwable t) {
            progressBar.setVisibility(View.GONE);
            callHasBeenCompleted = true;
        }
    };
    private int codeSelected;
    private boolean locationRefreshed;
    private FusedLocationService.MyLocation latLng = new FusedLocationService.MyLocation("0.0", "0.0");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        initViews();
        setupAutoCompleteTextView();
        setStatusBarColor(getResources().getColor(R.color.black));
    }

    private void setupAutoCompleteTextView() {
        localityAutoCompleteTextView.setDropDownBackgroundDrawable(this.getResources().getDrawable(R.drawable.autocomplete_dropdown));
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.item_autocomplete, localitiesSuggestions);
        localityAutoCompleteTextView.setAdapter(arrayAdapter);
        localityAutoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    codeSelected = 0;
                    progressBar.setVisibility(View.VISIBLE);
                    if (callHasBeenCompleted) {
                        callHasBeenCompleted = false;
                        call = TheBox.getAPIService().getAllLocalities(PrefUtils.getToken(FillUserInfoActivity.this), editable.toString());
                        call.enqueue(localitiesResponseCallback);
                    } else {
                        call.cancel();
                        call = TheBox.getAPIService().getAllLocalities(PrefUtils.getToken(FillUserInfoActivity.this), editable.toString());
                        call.enqueue(localitiesResponseCallback);
                    }
                } else {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
        localityAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                codeSelected = localities.get(position).getCode();
            }
        });
    }

    private void setAutoCompleteAdapter() {
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.item_autocomplete, localitiesSuggestions);
        localityAutoCompleteTextView.setAdapter(arrayAdapter);
    }

    private void initViews() {
        submitButton = (Button) findViewById(R.id.button_submit);
        submitButton.setOnClickListener(this);
        nameEditText = (EditText) findViewById(R.id.edit_text_name);
        emailEditText = (EditText) findViewById(R.id.edit_text_email);
        progressBar = (GifImageView) findViewById(R.id.progress_bar);
        localityAutoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.edit_text_locality);
        progressBar.setVisibility(View.GONE);
        localityAutoCompleteTextView.setText(Constants.POWAI_LOCALITY.getName());
        localityAutoCompleteTextView.setFocusable(false);
        localityAutoCompleteTextView.setFocusableInTouchMode(false); // user touches widget on phone with touch screen
        localityAutoCompleteTextView.setClickable(false); //
        codeSelected = Constants.POWAI_LOCALITY.getCode();

        authenticationService = new AuthenticationService();
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
            return;
        }
    }

    // Location Marking Issue
    private void checkGPSenable() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            getLocationPermission();
        } else {
            buildAlertMessageNoGps();
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
        TheBox
                .getAPIService()
                .storeUserInfo(PrefUtils.getToken(this)
                        , new StoreUserInfoRequestBody(new StoreUserInfoRequestBody
                                .User(PrefUtils.getUser(this).getPhoneNumber(), email, name, String.valueOf(codeSelected), latLng.getLatitude(), latLng.getLongitude())))
                .enqueue(new Callback<UserSignInSignUpResponse>() {
                    @Override
                    public void onResponse(Call<UserSignInSignUpResponse> call, Response<UserSignInSignUpResponse> response) {
                        dialog.dismiss();
                        try {
                            if (response.body() != null) {
                                if (response.body().isSuccess()) {
                                    if (response.body().getUser() != null) {
                                        PrefUtils.saveUser(FillUserInfoActivity.this, response.body().getUser());
                                        PrefUtils.saveToken(FillUserInfoActivity.this, response.body().getUser().getAuthToken());
                                        //update crashlytics data when user fills details
                                        authenticationService.setUserDataToCrashlytics();

                                        startActivity(new Intent(FillUserInfoActivity.this, MainActivity.class));
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
            nameEditText.setError("Name could not be empty!");
            return false;
        }
        if (emailEditText.getText().toString().isEmpty()) {
            emailEditText.setError("Email could not be empty");
            return false;
        }
        if (!isValidEmail(emailEditText.getText().toString())) {
            emailEditText.setError("Invalid email address");
            return false;
        }
        if (localityAutoCompleteTextView.getText().toString().isEmpty()) {
            localityAutoCompleteTextView.setError("Locality could not be empty");
            return false;
        }
        if (codeSelected == 0) {
            localityAutoCompleteTextView.setError("Locality don't exist");
            return false;
        }
        name = nameEditText.getText().toString().trim();
        email = emailEditText.getText().toString().trim();
        locality = localityAutoCompleteTextView.getText().toString().trim();
        return true;
    }
}
