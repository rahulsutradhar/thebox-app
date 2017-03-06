package one.thebox.android.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import one.thebox.android.R;
import one.thebox.android.ViewHelper.Montserrat;

/**
 * Created by harsh on 10/12/15.
 */
public abstract class BaseActivity extends AppCompatActivity {

    boolean isLoginActivity = false;
    private Toolbar toolbar;
    private TextView toolbarTitle = null;
    private boolean hasTransparentTitle = false;
    private boolean shouldHandleDrawer;
    private boolean shouldCloseActivityOnBackPress;

    public void closeActivityOnBackPress(boolean shouldCloseActivityOnBackPress) {
        this.shouldCloseActivityOnBackPress = true;
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }


    public Toolbar getToolbar() {
        return toolbar;
    }

    public void setToolbar(Toolbar toolbar) {
        this.toolbar = toolbar;
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setHomeButtonEnabled(true);
            }

            if (hasTransparentTitle) {
                toolbar.setTitleTextColor(Color.argb(0, 255, 255, 255));
            }
        }
        setToolbarFont();
    }

    private void setToolbarFont() {
        if (toolbar != null) {
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
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

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
                if (!shouldHandleDrawer) {
                    finish();
                }
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setStatusBarTranslucent(boolean makeTranslucent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (makeTranslucent) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            } else {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }
        }
    }

    public void shouldHandleDrawer() {
        this.shouldHandleDrawer = true;
    }

    public void setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            // clear FLAG_TRANSLUCENT_STATUS flag:
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            // finally change the color
            window.setStatusBarColor(color);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && shouldCloseActivityOnBackPress) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public View getContentView() {
        return findViewById(android.R.id.content);
    }

    public void setEditTextFocus(EditText searchEditText, boolean isFocused) {
        searchEditText.setCursorVisible(isFocused);
        searchEditText.setFocusable(isFocused);
        searchEditText.setFocusableInTouchMode(isFocused);
        if (isFocused) {
            searchEditText.requestFocus();
        } else {
            InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(searchEditText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
