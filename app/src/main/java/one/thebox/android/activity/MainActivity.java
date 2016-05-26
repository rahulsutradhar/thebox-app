package one.thebox.android.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import one.thebox.android.Events.SearchEvent;
import one.thebox.android.Helpers.CartHelper;
import one.thebox.android.Models.ExploreItem;
import one.thebox.android.Models.SearchResult;
import one.thebox.android.Models.User;
import one.thebox.android.R;
import one.thebox.android.Services.MyInstanceIDListenerService;
import one.thebox.android.Services.RegistrationIntentService;
import one.thebox.android.ViewHelper.BoxLoader;
import one.thebox.android.api.Responses.GetAllAddressResponse;
import one.thebox.android.api.Responses.SearchAutoCompleteResponse;
import one.thebox.android.app.MyApplication;
import one.thebox.android.fragment.AutoCompleteFragment;
import one.thebox.android.fragment.ExploreBoxesFragment;
import one.thebox.android.fragment.MyAccountFragment;
import one.thebox.android.fragment.MyBoxesFragment;
import one.thebox.android.fragment.OrderTabFragment;
import one.thebox.android.fragment.SearchDetailFragment;
import one.thebox.android.util.Constants;
import one.thebox.android.util.CoreGsonUtils;
import one.thebox.android.util.OnFragmentInteractionListener;
import one.thebox.android.util.PrefUtils;
import pl.droidsonroids.gif.GifImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Ajeet Kumar Meena on 8/10/15.
 */
public class MainActivity extends BaseActivity implements
        NavigationView.OnNavigationItemSelectedListener
        , View.OnClickListener, OnFragmentInteractionListener,
        FragmentManager.OnBackStackChangedListener {

    public static final String EXTRA_ATTACH_FRAGMENT_NO = "extra_tab_no";
    public static final String EXTRA_ATTACH_FRAGMENT_DATA = "extra_attach_fragment_data";
    private static final String PREF_IS_FIRST_LOGIN = "is_first_login";
    public static boolean isSearchFragmentIsAttached = false;
    private Call<SearchAutoCompleteResponse> call;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ImageView buttonSpecialAction;
    private EditText searchView;
    private String query;
    private boolean callHasBeenCompleted = true;
    private GifImageView progressBar;
    Callback<SearchAutoCompleteResponse> searchAutoCompleteResponseCallback = new Callback<SearchAutoCompleteResponse>() {
        @Override
        public void onResponse(Call<SearchAutoCompleteResponse> call, Response<SearchAutoCompleteResponse> response) {
            progressBar.setVisibility(View.GONE);
            callHasBeenCompleted = true;
            if (response.body() != null) {
                EventBus.getDefault().post(new SearchEvent(query, response.body()));
            }
        }

        @Override
        public void onFailure(Call<SearchAutoCompleteResponse> call, Throwable t) {
            progressBar.setVisibility(View.GONE);
            callHasBeenCompleted = true;
        }
    };
    private FrameLayout searchViewHolder;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private User user;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startService(new Intent(this, RegistrationIntentService.class));
        startService(new Intent(this, MyInstanceIDListenerService.class));
        user = PrefUtils.getUser(this);
        fragmentManager = getSupportFragmentManager();
        shouldHandleDrawer();
        initViews();
        setupNavigationDrawer();
        setStatusBarTranslucent(true);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getContentView().getWindowToken(), 0);
        if (PrefUtils.getBoolean(this, Constants.PREF_SHOULD_OPEN_EXPLORE_BOXES)) {
            attachExploreBoxes();
        } else {
            attachMyBoxesFragment();
        }
        if (PrefUtils.getBoolean(this, PREF_IS_FIRST_LOGIN, true)) {
            getAllAddresses();
        }
        initCart();
    }

    private void initCart() {
        CartHelper.saveCartItemsIfRequire();
    }

    private void setupNavigationDrawer() {
        fragmentManager.addOnBackStackChangedListener(this);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        TextView userNameTextView = (TextView) headerView.findViewById(R.id.user_name_text_view);
        userNameTextView.setText(user.getName());
        setToolbar((Toolbar) findViewById(R.id.toolbar));
        setSupportActionBar(getToolbar());
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, getToolbar(), R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getContentView().getWindowToken(), 0);
                super.onDrawerOpened(drawerView);
            }
        };
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        navigationView.setCheckedItem(R.id.explore_boxes);
    }

    private void initViews() {
        searchView = (EditText) findViewById(R.id.search);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        buttonSpecialAction = (ImageView) findViewById(R.id.button_special_action);
        progressBar = (GifImageView) findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);
        buttonSpecialAction.setOnClickListener(this);
        searchViewHolder = (FrameLayout) findViewById(R.id.search_view_holder);
        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                query = s.toString();
                attachSearchResultFragment();
                if (s.length() > 0) {
                    progressBar.setVisibility(View.VISIBLE);
                    if (callHasBeenCompleted) {
                        callHasBeenCompleted = false;
                        call = MyApplication.getAPIService().searchAutoComplete(PrefUtils.getToken(MainActivity.this), query);
                        call.enqueue(searchAutoCompleteResponseCallback);
                    } else {
                        call.cancel();
                        call = MyApplication.getAPIService().searchAutoComplete(PrefUtils.getToken(MainActivity.this), query);
                        call.enqueue(searchAutoCompleteResponseCallback);
                    }
                } else {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        if (menuItem.isChecked()) {
            menuItem.setChecked(false);
            //menuItem.getActionView();
        } else {
            menuItem.setChecked(true);
        }

        drawerLayout.closeDrawers();
        switch (menuItem.getItemId()) {
            case R.id.my_boxes:
                attachMyBoxesFragment();
                return true;
            case R.id.my_account:
                attachMyAccountFragment();
                return true;
            case R.id.view_bill:
                attachOrderFragment();
                return true;
            case R.id.explore_boxes: {
                attachExploreBoxes();
                return true;
            }
        }
        return true;
    }

    private void attachExploreBoxes() {
        clearBackStack();
        getToolbar().setSubtitle(null);
        ExploreBoxesFragment fragment = new ExploreBoxesFragment();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment, "Explore Boxes");
        fragmentTransaction.commit();
    }

    private void attachOrderFragment() {
        clearBackStack();
        OrderTabFragment fragment = new OrderTabFragment();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment, "Bills");
        fragmentTransaction.commit();
    }

    private void attachOrderFragmentWithBackStack() {
        OrderTabFragment fragment = new OrderTabFragment();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment, "Bills").addToBackStack("Orders");
        fragmentTransaction.commit();
    }

    private void attachMyAccountFragment() {
        clearBackStack();
        MyAccountFragment fragment = new MyAccountFragment();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment, "My Account");
        fragmentTransaction.commit();
    }

    private void attachMyBoxesFragment() {
        clearBackStack();
        MyBoxesFragment fragment = new MyBoxesFragment();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment, "My Boxes");
        fragmentTransaction.commit();
    }

    private void attachSearchResultFragment() {
        getToolbar().setSubtitle(null);
        if (!isSearchFragmentIsAttached) {
            isSearchFragmentIsAttached = true;
            getToolbar().setTitle("Search");
            AutoCompleteFragment fragment = new AutoCompleteFragment();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame, fragment, "Search Result").addToBackStack("Explore Boxes");
            fragmentTransaction.commit();
        }
    }

    private void attachSearchDetailFragment(SearchResult query) {
        getToolbar().setSubtitle(null);
        searchView.getText().clear();
        searchViewHolder.setVisibility(View.VISIBLE);
        buttonSpecialAction.setVisibility(View.VISIBLE);
        buttonSpecialAction.setImageResource(R.drawable.ic_box);
        buttonSpecialAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attachMyBoxesFragment();
            }
        });
        SearchDetailFragment fragment = SearchDetailFragment.getInstance(query);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment).addToBackStack("Search Details");
        fragmentTransaction.commit();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getContentView().getWindowToken(), 0);
    }

    private void attachExploreItemDetailFragment(ExploreItem exploreItem) {
        getToolbar().setSubtitle(null);
        searchView.getText().clear();
        searchViewHolder.setVisibility(View.VISIBLE);
        buttonSpecialAction.setVisibility(View.VISIBLE);
        buttonSpecialAction.setImageResource(R.drawable.ic_box);
        buttonSpecialAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attachMyBoxesFragment();
            }
        });
        SearchDetailFragment fragment = SearchDetailFragment.getInstance(exploreItem);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment).addToBackStack("Search Details");
        fragmentTransaction.commit();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getContentView().getWindowToken(), 0);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.search: {
                break;
            }
        }
    }

    private void searchFor(String newText) {
        attachSearchResultFragment();
    }

    private void openSearchResultActivity(String query) {

    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }

    public String getActiveFragmentTag() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            return null;
        }
        return getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.isDrawerIndicatorEnabled() &&
                actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        } else if (item.getItemId() == android.R.id.home &&
                getSupportFragmentManager().popBackStackImmediate()) {
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public void getAllAddresses() {
        final BoxLoader dialog = new BoxLoader(this).show();
        MyApplication.getAPIService().getAllAddresses(PrefUtils.getToken(this))
                .enqueue(new Callback<GetAllAddressResponse>() {
                    @Override
                    public void onResponse(Call<GetAllAddressResponse> call, Response<GetAllAddressResponse> response) {
                        dialog.dismiss();
                        if (response.body() != null) {
                            if (response.body().isSuccess()) {
                                PrefUtils.putBoolean(MainActivity.this, PREF_IS_FIRST_LOGIN, false);
                                User user = PrefUtils.getUser(MainActivity.this);
                                user.setAddresses(response.body().getUserAddresses());
                                PrefUtils.saveUser(MainActivity.this, user);
                            } else {
                                Toast.makeText(MainActivity.this, response.body().getInfo(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<GetAllAddressResponse> call, Throwable t) {
                        dialog.dismiss();
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        switch (intent.getIntExtra(EXTRA_ATTACH_FRAGMENT_NO, 0)) {
            case 0: {
                attachExploreBoxes();
                break;
            }
            case 1: {
                attachMyBoxesFragment();
                break;
            }
            case 3: {
                attachOrderFragmentWithBackStack();
                break;
            }
            case 4: {
                attachSearchDetailFragment
                        (CoreGsonUtils.fromJson
                                (intent.getStringExtra(EXTRA_ATTACH_FRAGMENT_DATA), SearchResult.class));
                break;
            }
            case 5: {
                attachExploreItemDetailFragment(CoreGsonUtils.fromJson
                        (intent.getStringExtra(EXTRA_ATTACH_FRAGMENT_DATA), ExploreItem.class));
                break;
            }
            case 6: {
                attachCategoriesFragment(intent);
                break;
            }
        }
    }

    private void attachCategoriesFragment(Intent intent) {
        getToolbar().setSubtitle(null);
        searchView.getText().clear();
        searchViewHolder.setVisibility(View.VISIBLE);
        buttonSpecialAction.setVisibility(View.VISIBLE);
        buttonSpecialAction.setImageResource(R.drawable.ic_box);
        buttonSpecialAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attachMyBoxesFragment();
            }
        });
        ArrayList<Integer> catIds = CoreGsonUtils.fromJsontoArrayList(intent.getStringExtra(SearchDetailFragment.EXTRA_MY_BOX_CATEGORIES_ID), Integer.class);
        int selectedPosition = intent.getIntExtra(SearchDetailFragment.EXTRA_CLICK_POSITION, 0);
        String boxName = intent.getStringExtra(SearchDetailFragment.BOX_NAME);
        SearchDetailFragment fragment = SearchDetailFragment.getInstance(catIds, selectedPosition, boxName);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment).addToBackStack("Search Details");
        fragmentTransaction.commit();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getContentView().getWindowToken(), 0);
    }

    @Override
    public void showDrawerToggle(boolean showDrawerToggle) {
     /*   ActionBar actionBar = getSupportActionBar();
        actionBarDrawerToggle.setDrawerIndicatorEnabled(showDrawerToggle);
        actionBarDrawerToggle.syncState();
       *//* if (!showDrawerToggle) {
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }*/
    }

    @Override
    public void onBackStackChanged() {
        actionBarDrawerToggle.setDrawerIndicatorEnabled(fragmentManager.getBackStackEntryCount() == 0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(fragmentManager.getBackStackEntryCount() > 0);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    public void clearBackStack() {
        for (int i = 0; i < fragmentManager.getBackStackEntryCount(); ++i) {
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    public ImageView getButtonSpecialAction() {
        return buttonSpecialAction;
    }

    public EditText getSearchView() {
        return searchView;
    }

    public FrameLayout getSearchViewHolder() {
        return searchViewHolder;
    }
}
