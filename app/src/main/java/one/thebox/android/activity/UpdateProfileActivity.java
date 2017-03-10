package one.thebox.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import one.thebox.android.Models.User;
import one.thebox.android.R;
import one.thebox.android.ViewHelper.BoxLoader;
import one.thebox.android.api.RequestBodies.StoreUserInfoRequestBody;
import one.thebox.android.api.Responses.UserSignInSignUpResponse;
import one.thebox.android.app.TheBox;
import one.thebox.android.util.CoreGsonUtils;
import one.thebox.android.util.PrefUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateProfileActivity extends BaseActivity implements View.OnClickListener {

    private Button submitButton;
    private EditText nameEditText, emailEditText;
    private EditText editMobileNumberActivity;
    private ImageView editMobileNumberButton;
    private String name, email, mobile = "";
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Update Profile");
        setContentView(R.layout.activity_update_profile);
        initViews();
        setupViews();
    }

    private void setupViews() {
        user = PrefUtils.getUser(this);
        mobile = user.getPhoneNumber();
        nameEditText.setText(user.getName());
        emailEditText.setText(user.getEmail());
        editMobileNumberActivity.setText(user.getPhoneNumber());
    }

    private void initViews() {
        submitButton = (Button) findViewById(R.id.button_submit);
        submitButton.setOnClickListener(this);
        nameEditText = (EditText) findViewById(R.id.edit_text_name);
        emailEditText = (EditText) findViewById(R.id.edit_text_email);
        editMobileNumberActivity = (EditText) findViewById(R.id.edit_text_locality);
        editMobileNumberButton = (ImageView) findViewById(R.id.image_view_update_mobile_number);
        editMobileNumberButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.button_submit: {
                updateUserProfileToServer();
                break;
            }
            case R.id.image_view_update_mobile_number: {
                try {
                    if (!mobile.equalsIgnoreCase("")) {
                        startActivity(UpdatePhoneNumberActivity.getInstance(this, mobile.substring(3)));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

    /**
     * Request server to Update User Profile
     */
    public void updateUserProfileToServer() {

        if (isValidInfo() && hasChanges()) {
            final BoxLoader dialog = new BoxLoader(this).show();
            TheBox.getAPIService().updateProfile(
                    PrefUtils.getToken(this), new StoreUserInfoRequestBody(new StoreUserInfoRequestBody.User
                            (mobile, email, name, PrefUtils.getUser(UpdateProfileActivity.this).getLocalityCode())))
                    .enqueue(new Callback<UserSignInSignUpResponse>() {
                        @Override
                        public void onResponse(Call<UserSignInSignUpResponse> call, Response<UserSignInSignUpResponse> response) {
                            dialog.dismiss();
                            if (response.body() != null) {
                                if (response.body().isSuccess()) {
                                    saveUserProfileToLocal(response.body().getUser());
                                    Toast.makeText(UpdateProfileActivity.this, response.body().getInfo(), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(UpdateProfileActivity.this, response.body().getInfo(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<UserSignInSignUpResponse> call, Throwable t) {
                            dialog.dismiss();
                        }
                    });

        } else {
            if (!hasChanges()) {
                Toast.makeText(this, "You have made no changes", Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }

    /**
     * Save Use Profile to shared Preference
     */
    public void saveUserProfileToLocal(User user) {

        User user1 = PrefUtils.getUser(this);
        if (user1.getAddresses() != null) {
            if (user1.getAddresses().size() > 0) {
                user.setAddresses(user1.getAddresses());
            }
        }
        //update user in Preferences
        PrefUtils.saveUser(this, user);
        //set Results
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("UserProfile", CoreGsonUtils.toJson(user));
        setResult(3, intent);
        finish();
    }

    private boolean hasChanges() {
        if (name.equals(user.getName()) && email.equals(user.getEmail())) {
            return false;
        }
        return true;
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
        if (editMobileNumberActivity.getText().toString().isEmpty()) {
            editMobileNumberActivity.setError("Locality could not be empty");
            return false;
        }

        name = nameEditText.getText().toString().trim();
        email = emailEditText.getText().toString().trim();
        mobile = editMobileNumberActivity.getText().toString().trim();
        return true;
    }
}
