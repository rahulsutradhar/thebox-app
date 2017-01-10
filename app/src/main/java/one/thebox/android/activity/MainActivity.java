package one.thebox.android.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.haha.perflib.Main;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import one.thebox.android.BuildConfig;
import one.thebox.android.Events.SearchEvent;
import one.thebox.android.Helpers.CartHelper;
import one.thebox.android.Models.Box;
import one.thebox.android.Models.ExploreItem;
import one.thebox.android.Models.SearchResult;
import one.thebox.android.Models.User;
import one.thebox.android.Models.update.SettingsResponse;
import one.thebox.android.R;
import one.thebox.android.Services.MyInstanceIDListenerService;
import one.thebox.android.Services.RegistrationIntentService;
import one.thebox.android.Services.UpdateOrderService;
import one.thebox.android.ViewHelper.BoxLoader;
import one.thebox.android.ViewHelper.ShowCaseHelper;
import one.thebox.android.api.Responses.GetAllAddressResponse;
import one.thebox.android.api.Responses.MyBoxResponse;
import one.thebox.android.api.Responses.SearchAutoCompleteResponse;
import one.thebox.android.api.RestClient;
import one.thebox.android.app.MyApplication;
import one.thebox.android.fragment.AutoCompleteFragment;
import one.thebox.android.fragment.CartFragment;
import one.thebox.android.fragment.ExploreBoxesFragment;
import one.thebox.android.fragment.MyAccountFragment;
import one.thebox.android.fragment.MyBoxTabFragment;
import one.thebox.android.fragment.MyBoxesFragment;
import one.thebox.android.fragment.OrderTabFragment;
import one.thebox.android.fragment.SearchDetailFragment;
import one.thebox.android.fragment.dialog.UpdateDialogFragment;
import one.thebox.android.util.CoreGsonUtils;
import one.thebox.android.util.DisplayUtil;
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
    private LinearLayout navigationViewBottom;

    private DrawerLayout drawerLayout;
    private ImageView buttonSpecialAction, searchAction, btn_search;
    private EditText searchView;
    private String query;
    private ArrayList<ExploreItem> exploreItems = new ArrayList<>();
    private boolean callHasBeenCompleted = true;
    private GifImageView progressBar;
    private Menu menu;
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
    private AppBarLayout appBarLayout;


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
        if (PrefUtils.getBoolean(this, PREF_IS_FIRST_LOGIN, true)) {
            getAllAddresses();
        }

        // Doing it for notification so "My Deliveries" fragment can be attached by default
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (getIntent().getIntExtra(EXTRA_ATTACH_FRAGMENT_NO, 0) == 11) {
                attachMyBoxesFragment(2);
            } else if (getIntent().getIntExtra(EXTRA_ATTACH_FRAGMENT_NO, 0) == 10) {
                attachMyBoxesFragment(1);
            }
        } else {
            attachMyBoxesFragment(1);
        }

        initCart();


        if (!RestClient.is_in_development) {
            ShowCaseHelper.removeAllTutorial();

        }


        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, UniversalSearchActivity.class);
                startActivityForResult(intent, 4511);
            }
        });
        getSettingsData();


//      new ShowCaseHelper(this, 0).show("Search", "Search for an item, brand or category", searchViewHolder);
    }

    private void initCart() {
        CartHelper.saveCartItemsIfRequire();
    }

    public void setupNavigationDrawer() {
        fragmentManager.addOnBackStackChangedListener(this);
        navigationView.setNavigationItemSelectedListener(this);
        //navigationViewBottom.setNavigationItemSelectedListener(this);
        this.menu = navigationView.getMenu();
        addBoxesToMenu();
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
/*
        navigationView.setCheckedItem(R.id.explore_boxes);
*/
    }

    private void initViews() {
        searchView = (EditText) findViewById(R.id.search);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationViewBottom = (LinearLayout) findViewById(R.id.navigation_drawer_bottom);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        buttonSpecialAction = (ImageView) findViewById(R.id.button_special_action);
        btn_search = (ImageView) findViewById(R.id.btn_search);
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
                    getSupportFragmentManager().popBackStackImmediate();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });


        appBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);
        searchAction = (ImageView) findViewById(R.id.image_view_search_action);
        navigationViewBottom.setOnClickListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(final MenuItem menuItem) {
        if (menuItem.isChecked()) {
            menuItem.setChecked(false);
            //menuItem.getActionView();
        } else {
            menuItem.setChecked(true);
        }
        drawerLayout.closeDrawers();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                handleDrawer(menuItem);
            }
        }, 350);
        return true;
    }

    public boolean handleDrawer(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.my_boxes:
                attachMyBoxesFragment(0);
                return true;
            case R.id.my_account:
                attachMyAccountFragment();
                return true;
            case R.id.view_bill:
                attachOrderFragment();
                return true;
           /* case R.id.explore_boxes: {
                attachExploreBoxes();
                return true;
            }*/
            default: {
                String menuName = (String) menuItem.getTitle();
                openBoxByName(menuName);
            }
        }
        return true;
    }

    private void getSettingsData() {
        // Remove hardcoding of version
        // 13 is to test
        MyApplication.getAPIService().getSettings(PrefUtils.getToken(this), BuildConfig.VERSION_CODE + "")
                .enqueue(new Callback<SettingsResponse>() {
                    @Override
                    public void onResponse(Call<SettingsResponse> call, Response<SettingsResponse> response) {
                        if (response != null && response.body() != null) {
                            checkAppUpdate(response.body());
                        }
                    }

                    @Override
                    public void onFailure(Call<SettingsResponse> call, Throwable t) {
                    }
                });
    }

    private void checkAppUpdate(SettingsResponse response) {

        if (null != response.getData() && response.getData().isNew_version_available()) {
            if (isPopupRequiredToDisplay() || response.getData().isForce_update()) {
                if (null != response.getData().getUpdatePopupDetails()) {
                    UpdateDialogFragment f = UpdateDialogFragment.getInstance(response.getData().getUpdatePopupDetails(), response.getData().isForce_update());
                    f.show(this.getSupportFragmentManager(), "Update");
                    if (!response.getData().isForce_update()) {
                        saveCacheTime();
                    }
                }
            }
        }
    }

    private void saveCacheTime() {
        long min_5 = 5 * 60 * 1000;
        PrefUtils.putLong(this, PrefUtils.KEY_SETTING_CACHE, System.currentTimeMillis() + min_5);
    }

    private boolean isPopupRequiredToDisplay() {
        long currentTime = System.currentTimeMillis();
        long prevTime = PrefUtils.getLong(this, PrefUtils.KEY_SETTING_CACHE, currentTime);
        return prevTime <= currentTime;
    }


    private void openBoxByName(String name) {
        if (name.equals("FAQs")) {
            startActivity(TermsOfUserActivity.getIntent(this, true));
        } else if (name.equals("Terms of Use")) {
            startActivity(new Intent(MainActivity.this, TermsOfUserActivity.class));
        } else {
            ExploreItem selectedExploreItem = null;
            for (ExploreItem exploreItem : exploreItems) {
                if (exploreItem.getTitle().equals(name)) {
                    selectedExploreItem = exploreItem;
                    break;
                }
            }
            attachExploreItemDetailFragment(selectedExploreItem);
        }
    }

    private void attachOrderFragment() {
        clearBackStack();
        CartFragment fragment = new CartFragment();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment, "Bills");
        fragmentTransaction.commit();
        appBarLayout.setExpanded(true, true);
    }

    private void attachOrderFragmentWithBackStack() {

        CartFragment fragment = new CartFragment();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment, "Bills").addToBackStack("Orders");
        fragmentTransaction.commit();
        appBarLayout.setExpanded(true, true);
    }

    private void attachMyAccountFragment() {
        clearBackStack();

        MyAccountFragment fragment = new MyAccountFragment();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment, "My Account");
        fragmentTransaction.commit();
        appBarLayout.setExpanded(true, true);
    }

    private void openContactUsActivity() {
        startActivity(new Intent(MainActivity.this, ContactUsActivity.class));
        drawerLayout.closeDrawers();
    }

    private void attachMyBoxesFragment(int default_position) {
        clearBackStack();
        MyBoxTabFragment fragment = new MyBoxTabFragment();

        Bundle bundle = new Bundle();
        bundle.putInt("default_position", default_position);
        fragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment, "My Boxes");
        fragmentTransaction.commit();
        appBarLayout.setExpanded(true, true);
    }

    private void attachMyBoxesFragmentWithBackStack() {
        attachMyBoxesFragment(1);
    }

    private void attachSearchResultFragment() {
        getToolbar().setSubtitle(null);

        if (!isSearchFragmentIsAttached) {
            isSearchFragmentIsAttached = true;
            getToolbar().setTitle(null);
            AutoCompleteFragment fragment = new AutoCompleteFragment();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame, fragment, "Search Result").addToBackStack("Explore Boxes");
            fragmentTransaction.commit();
        }
        appBarLayout.setExpanded(true, true);
    }

    public void attachSearchDetailFragment(SearchResult query) {
        getToolbar().setSubtitle(null);

        searchView.getText().clear();
//        searchViewHolder.setVisibility(View.VISIBLE);
        searchViewHolder.setVisibility(View.GONE);
        btn_search.setVisibility(View.VISIBLE);

        buttonSpecialAction.setVisibility(View.VISIBLE);

        buttonSpecialAction.setImageResource(R.drawable.ic_box);
        buttonSpecialAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attachMyBoxesFragment(0);
            }
        });
        SearchDetailFragment fragment = SearchDetailFragment.getInstance(query);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment).addToBackStack("Search Details");
        fragmentTransaction.commit();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getContentView().getWindowToken(), 0);
        appBarLayout.setExpanded(true, true);
    }

    private void attachExploreItemDetailFragment(ExploreItem exploreItem) {
        getToolbar().setSubtitle(null);

        searchView.getText().clear();
//        searchViewHolder.setVisibility(View.VISIBLE);
        searchViewHolder.setVisibility(View.GONE);
        btn_search.setVisibility(View.VISIBLE);
        buttonSpecialAction.setVisibility(View.VISIBLE);
        buttonSpecialAction.setImageResource(R.drawable.ic_box);
        buttonSpecialAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attachMyBoxesFragment(0);
            }
        });
        SearchDetailFragment fragment = SearchDetailFragment.getInstance(exploreItem);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment).addToBackStack("Search Details");
        fragmentTransaction.commit();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getContentView().getWindowToken(), 0);
        appBarLayout.setExpanded(true, true);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.search: {
                break;
            }
            case R.id.navigation_drawer_bottom:
                openContactUsActivity();
                break;
        }
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
                                if (response.body().getUserAddresses() != null && !response.body().getUserAddresses().isEmpty()) {
                                    response.body().getUserAddresses().get(0).setCurrentAddress(true);
                                }
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
/*
                attachExploreBoxes();
*/
                break;
            }

            //Attaching Myitems fragment
            case 1: {
                attachMyBoxesFragment(0);
                break;
            }

            //Attaching MyDeliveries fragment
            case 2: {
                attachMyBoxesFragment(2);
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
            case 7: {
                attachMyBoxesFragmentWithBackStack();
                break;
            }

            //Notifications
            case 10: {
                attachMyBoxesFragment(1);
                break;
            }
            case 11: {
                attachMyBoxesFragment(1);
                break;
            }
            case 12: {
                attachMyBoxesFragment(2);
                break;
            }
        }
    }

    private void attachCategoriesFragment(Intent intent) {
        getToolbar().setSubtitle(null);

        searchView.getText().clear();
        searchViewHolder.setVisibility(View.GONE);
        buttonSpecialAction.setVisibility(View.VISIBLE);
        buttonSpecialAction.setVisibility(View.VISIBLE);
        buttonSpecialAction.setImageResource(R.drawable.ic_box);
        buttonSpecialAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attachMyBoxesFragment(0);
            }
        });

        ArrayList<Integer> catIds = CoreGsonUtils.fromJsontoArrayList(intent.getStringExtra(SearchDetailFragment.EXTRA_MY_BOX_CATEGORIES_ID), Integer.class);
        ArrayList<Integer> user_catIds = CoreGsonUtils.fromJsontoArrayList(intent.getStringExtra(SearchDetailFragment.EXTRA_MY_BOX_USER_CATEGORIES_ID), Integer.class);
        int selectedPosition = intent.getIntExtra(SearchDetailFragment.EXTRA_CLICK_POSITION, 0);
        String boxName = intent.getStringExtra(SearchDetailFragment.BOX_NAME);
        SearchDetailFragment fragment = SearchDetailFragment.getInstance(catIds, user_catIds, selectedPosition, boxName);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment).addToBackStack("Search Details");
        fragmentTransaction.commit();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getContentView().getWindowToken(), 0);
        appBarLayout.setExpanded(true, true);
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

    private void clearBackStack() {
        for (int i = 0; i < fragmentManager.getBackStackEntryCount(); ++i) {
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    public ImageView getButtonSpecialAction() {
        return buttonSpecialAction;
    }

    public ImageView getButtonSearch() {
        return btn_search;
    }

    public EditText getSearchView() {
        return searchView;
    }

    public FrameLayout getSearchViewHolder() {
        return searchViewHolder;
    }

    public void setSearchView(EditText searchView) {
        this.searchView = searchView;
    }

    public DrawerLayout getDrawerLayout() {
        return drawerLayout;
    }

    public void setDrawerLayout(DrawerLayout drawerLayout) {
        this.drawerLayout = drawerLayout;
    }

    public ImageView getSearchAction() {
        return searchAction;
    }

    public void setSearchAction(ImageView searchAction) {
        this.searchAction = searchAction;
    }

    public ArrayList<ExploreItem> getAllExploreItems() {
        ArrayList<ExploreItem> exploreItems = new ArrayList<>();
        Realm realm = MyApplication.getRealm();
        RealmQuery<Box> query = realm.where(Box.class);
        RealmResults<Box> realmResults = query.notEqualTo(Box.FIELD_ID, 0).findAll();
        RealmList<Box> boxes = new RealmList<>();
        boxes.addAll(realmResults.subList(0, realmResults.size()));
        for (Box box : boxes) {
            exploreItems.add(new ExploreItem(box.getBoxId(), box.getBoxDetail().getTitle()));
        }
        return exploreItems;
    }

    public void addBoxesToMenu() {
        if (exploreItems == null || exploreItems.isEmpty()) {
            exploreItems = getAllExploreItems();
            for (ExploreItem exploreItem : exploreItems) {
                menu.add(exploreItem.getTitle());
            }
            if (exploreItems != null && !exploreItems.isEmpty()) {
                menu.add("FAQs");
                menu.add("Terms of Use");
            }

        }
        navigationView.invalidate();
    }
}

