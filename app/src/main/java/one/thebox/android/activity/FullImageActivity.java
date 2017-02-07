package one.thebox.android.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;

import com.squareup.picasso.Picasso;

import one.thebox.android.R;
import one.thebox.android.app.AppConstants;
import one.thebox.android.util.Constants;
import ooo.oxo.library.widget.TouchImageView;

public class FullImageActivity extends BaseActivity {
    public static void showImage(String imageUrl, Context context) {
        Intent intent = new Intent(context, FullImageActivity.class);
        intent.putExtra("imageUrl",imageUrl);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);
        String imageUrl = getIntent().getStringExtra("imageUrl");
        initToolBar(getString(R.string.app_name));
        if (!TextUtils.isEmpty(imageUrl)) {
            TouchImageView touchImageView = (TouchImageView) findViewById(R.id.iv_full_image);
            Picasso.with(this).load(imageUrl).into(touchImageView);
        }
    }
    public void initToolBar(String title) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setHomeAsUpIndicator(getHomeIcon());
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

//        toolbar.setBackgroundColor(colorCode);
//        setStatusBarColor(colorCode);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));
    }

}
