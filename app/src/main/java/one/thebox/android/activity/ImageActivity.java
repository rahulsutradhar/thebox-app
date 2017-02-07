package one.thebox.android.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;

import one.thebox.android.R;
import one.thebox.android.adapter.CustomPagerAdapter;
import one.thebox.android.fragment.ImageFragment;
import one.thebox.android.util.OnFragmentInteractionListener;

public class ImageActivity extends BaseActivity implements CustomPagerAdapter.PagerAdapterInterface<String> ,OnFragmentInteractionListener {

    public static final String KEY_IMAGE = "imageKey";
    private static final String KEY_IMAGES = "imagesKey";
    private static final String KEY_POSITION = "position";
    String imageUrl;
    ArrayList<String> images = new ArrayList<>();
    private ViewPager pager;
    private CustomPagerAdapter<String> adapter;

    public static void startActivity(Context ctx, String image) {
        ArrayList<String> images = new ArrayList<>();
        images.add(image);
        startActivity(ctx, images, 0);
    }

    public static void startActivity(Context ctx, ArrayList<String> images, int selectedPosition) {
        Bundle b = new Bundle();
        b.putStringArrayList(KEY_IMAGES, images);
        b.putInt(KEY_POSITION, selectedPosition);
        Intent i = new Intent(ctx, ImageActivity.class);
        i.putExtras(b);
        ctx.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        initToolBar(getString(R.string.app_name));
        int position = getIntent().getIntExtra(KEY_POSITION, 0);
        pager = (ViewPager) findViewById(R.id.viewPager);
        images = getIntent().getStringArrayListExtra(KEY_IMAGES);
        adapter = new CustomPagerAdapter<>(getSupportFragmentManager(), images, this);
        pager.setAdapter(adapter);
        pager.setCurrentItem(position);
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


    @Override
    public Fragment getFragmentItem(int position, String listItem) {
        return ImageFragment.getInstance(listItem);
    }

    @Override
    public CharSequence getPageTitle(int position, String listItem) {
        return "";
    }

    @Override
    public void showDrawerToggle(boolean showDrawerToggle) {

    }
}
