package one.thebox.android.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import one.thebox.android.R;
import one.thebox.android.app.MyApplication;

/**
 * Created by harsh on 10/12/15.
 */
public class BaseActivity extends AppCompatActivity {

    boolean isLoginActivity = false;

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (!isLoginActivity)
            verifyIfLoggedIn();
    }

    // Verifying if user logged out and start MainActivity if not
    public void verifyIfLoggedOut() {
        isLoginActivity = true;

        boolean isLoggedIn = MyApplication.getInstance().getAccountUtil().hasAccount();

        if (isLoggedIn) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        }
    }

    // Verifying if user logged in and start LoginActivity if not
    public void verifyIfLoggedIn() {
        boolean isLoggedIn = MyApplication.getInstance().getAccountUtil().hasAccount();

        if (!isLoggedIn) {

            MyApplication.getInstance().clearUserData(null);

            Toast.makeText(this, getString(R.string.logout_message), Toast.LENGTH_LONG).show();

            Intent intent = new Intent(this, MobileActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }
}
