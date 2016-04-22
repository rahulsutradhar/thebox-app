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
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import one.thebox.android.Events.SearchEvent;
import one.thebox.android.Models.User;
import one.thebox.android.R;
import one.thebox.android.api.Responses.LocalitiesResponse;
import one.thebox.android.api.Responses.SearchAutoCompleteResponse;
import one.thebox.android.app.MyApplication;
import one.thebox.android.fragment.BillsFragment;
import one.thebox.android.fragment.ExploreBoxesFragment;
import one.thebox.android.fragment.MyAccountFragment;
import one.thebox.android.fragment.MyBoxesFragment;
import one.thebox.android.fragment.MyOrdersFragment;
import one.thebox.android.fragment.SearchResultFragment;
import one.thebox.android.util.PrefUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Ajeet Kumar Meena on 8/10/15.
 */
public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, SearchView.OnQueryTextListener {

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ImageView buttonSpecialAction;
    private EditText searchView;
    private String query;
    private boolean callHasBeenCompleted = true;
    private ProgressBar progressBar;
    private FrameLayout searchViewHolder;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    Call<SearchAutoCompleteResponse> call;
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
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        user = PrefUtils.getUser(this);
        shouldHandleDrawer();
        initViews();
        setupSearchView();
        setupNavigationDrawer();
        attachExploreBoxes();
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
                attachBillsFragment();
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

    private void attachBillsFragment() {
        searchViewHolder.setVisibility(View.GONE);
        buttonSpecialAction.setVisibility(View.GONE);
        buttonSpecialAction.setOnClickListener(null);
        getToolbar().setTitle("My Orders");
        BillsFragment fragment = new BillsFragment();
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
        searchViewHolder.setVisibility(View.GONE);
        buttonSpecialAction.setVisibility(View.GONE);
        buttonSpecialAction.setOnClickListener(null);
        getToolbar().setTitle("My Boxes");
        MyBoxesFragment fragment = new MyBoxesFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment, "My Boxes");
        fragmentTransaction.commit();
    }

    @Override
    void onClick(int id) {
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
        if (getActiveFragmentTag() == null || !getActiveFragmentTag().equals("Explore Boxes")) {
            getToolbar().setTitle("Search");
            SearchResultFragment fragment = new SearchResultFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.frame, fragment, "Search Result").addToBackStack("Explore Boxes");
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
}
