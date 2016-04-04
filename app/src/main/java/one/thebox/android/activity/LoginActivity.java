package one.thebox.android.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import one.thebox.android.R;

public class LoginActivity extends BaseActivity {

    @Override
    protected void onResume() {
        verifyIfLoggedOut();
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void submitClicked(View view) {
        Toast.makeText(this, "submit clicked", Toast.LENGTH_SHORT).show();
    }

    public void pickLocationClicked(View view) {
        Toast.makeText(this, "location clicked", Toast.LENGTH_SHORT).show();

    }
}
