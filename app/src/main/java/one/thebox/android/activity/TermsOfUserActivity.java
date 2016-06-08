package one.thebox.android.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

import one.thebox.android.R;

public class TermsOfUserActivity extends BaseActivity {

    private static final String EXTRA_IS_FAQ = "extra_is_faq";
    private boolean isFaq;

    public static Intent getIntent(Context context, boolean isFAQ) {
        return new Intent(context,TermsOfUserActivity.class).putExtra(EXTRA_IS_FAQ,isFAQ);
    }
    private WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initVariable();
        setContentView(R.layout.activity_terms_of_user);
        initViews();
        if(isFaq) {
            setTitle("FAQs");
        } else {
            setTitle("Terms of use");
        }
    }

    private void initViews() {
        webView = (WebView) findViewById(R.id.web_view);
        if(isFaq) {
            webView.loadUrl("file:///android_asset/FAQs.html");
        }else {
            webView.loadUrl("file:///android_asset/index.html");
        }
    }

    private void initVariable() {
        isFaq = getIntent().getBooleanExtra(EXTRA_IS_FAQ,false);
    }
}
