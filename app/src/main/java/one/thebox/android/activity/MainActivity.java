package one.thebox.android.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import one.thebox.android.Events.SearchEvent;
import one.thebox.android.Events.TabEvent;
import one.thebox.android.Models.User;
import one.thebox.android.R;
import one.thebox.android.Services.MyInstanceIDListenerService;
import one.thebox.android.Services.RegistrationIntentService;
import one.thebox.android.api.Responses.GetAllAddressResponse;
import one.thebox.android.api.Responses.SearchAutoCompleteResponse;
import one.thebox.android.app.MyApplication;
import one.thebox.android.fragment.AllItemsFragment;
import one.thebox.android.fragment.ExploreBoxesFragment;
import one.thebox.android.fragment.MyAccountFragment;
import one.thebox.android.fragment.MyBoxesFragment;
import one.thebox.android.fragment.OrderTabFragment;
import one.thebox.android.util.CoreGsonUtils;
import one.thebox.android.util.NotificationHelper;
import one.thebox.android.util.NotificationInfo;
import one.thebox.android.util.PrefUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Ajeet Kumar Meena on 8/10/15.
 */
public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, SearchView.OnQueryTextListener, View.OnClickListener {

    public static final String EXTRA_TAB_NO = "extra_tab_no";
    private static final String PREF_IS_FIRST_LOGIN = "is_first_login";
    public static boolean isSearchFragmentIsAttached = false;
    Call<SearchAutoCompleteResponse> call;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ImageView buttonSpecialAction;
    private EditText searchView;
    private String query;
    private boolean callHasBeenCompleted = true;
    private ProgressBar progressBar;
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
    private boolean isRegistered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startService(new Intent(this, RegistrationIntentService.class));
        startService(new Intent(this, MyInstanceIDListenerService.class));
        user = PrefUtils.getUser(this);
        shouldHandleDrawer();
        closeActivityOnBackPress(true);
        initViews();
        setupSearchView();
        setupNavigationDrawer();
        attachExploreBoxes();
        setStatusBarTranslucent(true);
        if (PrefUtils.getBoolean(this, PREF_IS_FIRST_LOGIN, true)) {
            getAllAddresses();
        }
    }

    private void setupNavigationDrawer() {
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        TextView userNameTextView = (TextView) headerView.findViewById(R.id.user_name_text_view);
        userNameTextView.setText(user.getName());
        setToolbar((Toolbar) findViewById(R.id.toolbar));
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, getToolbar(), R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        setSupportActionBar(getToolbar());
        actionBarDrawerToggle.syncState();
        navigationView.setCheckedItem(R.id.explore_boxes);
        setTitle("Explore Boxes");
        searchView.setVisibility(View.VISIBLE);

    }

    private void setupSearchView() {
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
    }

    private void initViews() {
        searchView = (EditText) findViewById(R.id.search);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        buttonSpecialAction = (ImageView) findViewById(R.id.button_special_action);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
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
                setTitle("");
                attachExploreBoxes();
                return true;
            }
        }
        return true;
    }

    private void attachExploreBoxes() {
        searchViewHolder.setVisibility(View.VISIBLE);
        buttonSpecialAction.setVisibility(View.GONE);
        buttonSpecialAction.setOnClickListener(null);
        getToolbar().setTitle("Explore Boxes");
        ExploreBoxesFragment fragment = new ExploreBoxesFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment, "Explore Boxes");
        fragmentTransaction.commit();
    }

    private void attachOrderFragment() {
        searchViewHolder.setVisibility(View.GONE);
        buttonSpecialAction.setVisibility(View.GONE);
        buttonSpecialAction.setOnClickListener(null);
        getToolbar().setTitle("My Orders");
        OrderTabFragment fragment = new OrderTabFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment, "Bills");
        fragmentTransaction.commit();
    }

    private void attachMyAccountFragment() {
        searchViewHolder.setVisibility(View.GONE);
        buttonSpecialAction.setVisibility(View.VISIBLE);
        buttonSpecialAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, UpdateProfileActivity.class));
            }
        });
        getToolbar().setTitle("My Account");
        MyAccountFragment fragment = new MyAccountFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment, "My Account");
        fragmentTransaction.commit();
    }

    private void attachMyBoxesFragment() {
        searchViewHolder.setVisibility(View.VISIBLE);
        buttonSpecialAction.setVisibility(View.GONE);
        buttonSpecialAction.setOnClickListener(null);
        getToolbar().setTitle("My Boxes");
        MyBoxesFragment fragment = new MyBoxesFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment, "My Boxes");
        fragmentTransaction.commit();
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

    @Override
    public boolean onQueryTextSubmit(String query) {
        openSearchResultActivity(query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        searchFor(newText);
        return true;
    }

    private void searchFor(String newText) {
        attachSearchResultFragment();
    }

    private void attachSearchResultFragment() {
        if (!isSearchFragmentIsAttached) {
            isSearchFragmentIsAttached = true;
            getToolbar().setTitle("Search");
            AllItemsFragment fragment = new AllItemsFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame, fragment, "Search Result").addToBackStack("Explore Boxes");
            fragmentTransaction.commit();
        }
    }

    private void openSearchResultActivity(String query) {

    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
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
        return actionBarDrawerToggle.onOptionsItemSelected(item);
    }

    public void getAllAddresses() {
        final MaterialDialog dialog = new MaterialDialog.Builder(this).progressIndeterminateStyle(true).progress(true, 0).show();
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

    @Subscribe

    public void onTabEvent(TabEvent tabEvent) {
        switch (tabEvent.getTabNo()) {
            case 1: {
                navigationView.setCheckedItem(R.id.my_boxes);
                break;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ArrayList<NotificationInfo.NotificationAction> notificationActions = new ArrayList<>();
        notificationActions.add(new NotificationInfo.NotificationAction(0, ""));
        notificationActions.add(new NotificationInfo.NotificationAction(0, ""));
        notificationActions.add(new NotificationInfo.NotificationAction(0, ""));
        NotificationInfo notificationInfo = new NotificationInfo();
        notificationInfo.setContentImageUrl("http://longspark.org/wp-content/uploads/2015/11/art-therapy-career2.jpg");
        notificationInfo.setContentText("content");
        notificationInfo.setContentTitle("title");
        notificationInfo.setLargeIcon("http://icons.iconarchive.com/icons/iconsmind/outline/512/Box-Open-icon.png");
        notificationInfo.setNegativeButtonIconId(0);
        notificationInfo.setNotificationActions(notificationActions);
        notificationInfo.setNegativeButtonText("Negative Button");
        notificationInfo.setPositiveButtonIconId(0);
        notificationInfo.setPositiveButtonText("Positive Button");
        notificationInfo.setNotificationId(1);
        new NotificationHelper(this, notificationInfo).show();
        Log.d("Notification Info", CoreGsonUtils.toJson(notificationInfo));

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!isRegistered) {
            EventBus.getDefault().register(this);
            isRegistered = true;
        }
    }

    @Override
    protected void onStop() {
        // EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        switch (intent.getIntExtra(EXTRA_TAB_NO, 0)) {
            case 1: {
                attachMyBoxesFragment();
                break;
            }
            case 3: {
                attachOrderFragment();
                break;
            }
        }
    }
}
