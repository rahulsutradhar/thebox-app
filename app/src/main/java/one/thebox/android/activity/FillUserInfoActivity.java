package one.thebox.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;

import one.thebox.android.Models.Locality;
import one.thebox.android.R;
import one.thebox.android.ViewHelper.BoxLoader;
import one.thebox.android.api.RequestBodies.StoreUserInfoRequestBody;
import one.thebox.android.api.Responses.LocalitiesResponse;
import one.thebox.android.api.Responses.UserSignInSignUpResponse;
import one.thebox.android.app.MyApplication;
import one.thebox.android.util.Constants;
import one.thebox.android.util.PrefUtils;
import pl.droidsonroids.gif.GifImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FillUserInfoActivity extends BaseActivity implements View.OnClickListener {

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
                        call = MyApplication.getAPIService().getAllLocalities(PrefUtils.getToken(FillUserInfoActivity.this), editable.toString());
                        call.enqueue(localitiesResponseCallback);
                    } else {
                        call.cancel();
                        call = MyApplication.getAPIService().getAllLocalities(PrefUtils.getToken(FillUserInfoActivity.this), editable.toString());
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
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.button_submit: {
                if (isValidInfo()) {
                    final BoxLoader dialog = new BoxLoader(this).show();
                    MyApplication
                            .getAPIService()
                            .storeUserInfo(PrefUtils.getToken(this)
                                    , new StoreUserInfoRequestBody(new StoreUserInfoRequestBody
                                            .User(PrefUtils.getUser(this).getPhoneNumber(), email, name, String.valueOf(codeSelected))))
                            .enqueue(new Callback<UserSignInSignUpResponse>() {
                                @Override
                                public void onResponse(Call<UserSignInSignUpResponse> call, Response<UserSignInSignUpResponse> response) {
                                    dialog.dismiss();
                                    if (response.body() != null) {
                                        if (response.body().isSuccess()) {
                                            if (response.body().getUser() != null) {
                                                PrefUtils.saveUser(FillUserInfoActivity.this, response.body().getUser());
                                                PrefUtils.saveToken(FillUserInfoActivity.this, response.body().getUser().getAuthToken());
                                                startActivity(new Intent(FillUserInfoActivity.this, MainActivity.class));
                                                finish();
                                            }
                                        } else {
                                            Toast.makeText(FillUserInfoActivity.this, response.body().getInfo(), Toast.LENGTH_SHORT).show();
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
        }
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
