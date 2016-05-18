package one.thebox.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import one.thebox.android.R;

public class MobileActivity extends BaseActivity {

    @Override
    protected void onResume() {
//        verifyIfLoggedOut();
        super.onResume();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile);
    }

    public void submitClicked(View view) {
        startActivity(new Intent(this, LoginActivity.class));
    }
}
