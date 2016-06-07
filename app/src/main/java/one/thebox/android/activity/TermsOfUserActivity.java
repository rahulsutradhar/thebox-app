package one.thebox.android.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

import one.thebox.android.R;

public class TermsOfUserActivity extends BaseActivity {

    private WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_of_user);
        initViews();
        setTitle("Terms of use");
    }

    private void initViews() {
        webView = (WebView) findViewById(R.id.web_view);
        webView.loadUrl("file:///android_asset/index.html");
    }
}
