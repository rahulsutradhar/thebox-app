package one.thebox.android.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import one.thebox.android.R;
import one.thebox.android.app.MyApplication;

/**
 * Created by harsh on 10/12/15.
 */
public class BaseActivity extends AppCompatActivity {

    boolean isLoginActivity = false;
    private Toolbar toolbar;
    private TextView toolbarTitle = null;
    private boolean hasTransparentTitle = false;

    @Override
    protected void onPostResume() {
        super.onPostResume();
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


    public Toolbar getToolbar() {
        return toolbar;
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (hasTransparentTitle) {
                toolbar.setTitleTextColor(Color.argb(0, 255, 255, 255));
            }
        }
        //setToolbarFont();
    }

    /*private void setToolbarFont() {
        for (int i = 0; i < toolbar.getChildCount(); ++i) {
            View child = toolbar.getChildAt(i);

            // assuming that the title is the first instance of TextView
            // you can also check if the title string matches
            if (child instanceof TextView) {
                toolbarTitle = (TextView) child;
                break;
            }
        }
        if (toolbarTitle != null)
            toolbarTitle.setTypeface(Montserrat.getRoboto(this, Montserrat.MONTSERRAT_REGULAR));
    }*/

    public TextView getToolbarTitle() {
        return toolbarTitle;
    }

    public void makeTitleTransparent() {
        hasTransparentTitle = true;
        if (toolbar != null) {
            toolbar.setTitleTextColor(Color.argb(0, 255, 255, 255));
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setStatusBarTranslucent(boolean makeTranslucent) {
        if (makeTranslucent) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }
}
