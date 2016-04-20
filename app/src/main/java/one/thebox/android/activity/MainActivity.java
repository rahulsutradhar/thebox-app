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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import one.thebox.android.R;
import one.thebox.android.fragment.BillsFragment;
import one.thebox.android.fragment.ExploreBoxesFragment;
import one.thebox.android.fragment.MyAccountFragment;
import one.thebox.android.fragment.MyBoxesFragment;
import one.thebox.android.fragment.MyOrdersFragment;
import one.thebox.android.fragment.SearchResultFragment;

/**
 * Created by Ajeet Kumar Meena on 8/10/15.
 */
public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, SearchView.OnQueryTextListener {

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ImageView buttonSpecialAction;
    private EditText searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        shouldHandleDrawer();
        initViews();
        setupSearchView();
        setupNavigationDrawer();
        attachExploreBoxes();
    }

    private void setupNavigationDrawer() {
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, getToolbar(), R.string.openDrawer, R.string.closeDrawer) {

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
        buttonSpecialAction.setOnClickListener(this);
        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                attachSearchResultFragment();
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
                searchView.setVisibility(View.GONE);
                buttonSpecialAction.setVisibility(View.GONE);
                buttonSpecialAction.setOnClickListener(null);
                searchView.setVisibility(View.VISIBLE);
                setTitle("My Boxes");
                attachMyBoxesFragment();
                return true;
            case R.id.my_account:
                searchView.setVisibility(View.GONE);
                buttonSpecialAction.setVisibility(View.VISIBLE);
                searchView.setVisibility(View.GONE);
                buttonSpecialAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(MainActivity.this,UpdateProfileActivity.class));
                    }
                });
                setTitle("Account");
                attachMyAccountFragment();
                return true;
            case R.id.view_bill:
                searchView.setVisibility(View.GONE);
                buttonSpecialAction.setVisibility(View.GONE);
                buttonSpecialAction.setOnClickListener(null);
                searchView.setVisibility(View.VISIBLE);
                setTitle("BillsFragment");
                attachBillsFragment();
                return true;

            case R.id.explore_boxes: {
                setTitle("");
                buttonSpecialAction.setVisibility(View.GONE);
                searchView.setVisibility(View.VISIBLE);
                buttonSpecialAction.setOnClickListener(null);
                searchView.setVisibility(View.VISIBLE);
                setTitle("Explore Boxes");
                attachExploreBoxes();
                return true;
            }
        }
        return true;
    }

    private void attachExploreBoxes() {
        ExploreBoxesFragment fragment = new ExploreBoxesFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment,"Explore Boxes");
        fragmentTransaction.commit();
    }

    private void attachMyOrders() {
        MyOrdersFragment fragment = new MyOrdersFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment,"My Orders");
        fragmentTransaction.commit();
    }

    private void attachBillsFragment() {
        BillsFragment fragment = new BillsFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment, "Bills");
        fragmentTransaction.commit();
    }

    private void attachMyAccountFragment() {
        MyAccountFragment fragment = new MyAccountFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment, "My Account");
        fragmentTransaction.commit();
    }

    private void attachMyBoxesFragment() {
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
        if(getActiveFragmentTag()==null || !getActiveFragmentTag().equals("Explore Boxes")) {
            SearchResultFragment fragment = new SearchResultFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.frame, fragment,"Search Result").addToBackStack("Explore Boxes");
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
}
