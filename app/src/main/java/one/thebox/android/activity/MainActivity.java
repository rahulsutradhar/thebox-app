package one.thebox.android.activity;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import one.thebox.android.R;
import one.thebox.android.fragment.Bills;
import one.thebox.android.fragment.ExploreBoxes;
import one.thebox.android.fragment.HomeFragment;
import one.thebox.android.fragment.MyAccount;
import one.thebox.android.fragment.MyBoxes;
import one.thebox.android.fragment.MyOrders;

/**
 * Created by Ajeet Kumar Meena on 8/10/15.
 */
public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        shouldHandleDrawer();
        initViews();
        setupNavigationDrawer();
        attachMyBoxesFragment();
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
        navigationView.setCheckedItem(R.id.my_boxes);
        setTitle("My Boxes");
    }

    private void initViews() {
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
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
                setTitle("My Boxes");
                attachMyBoxesFragment();
                return true;
            case R.id.my_account:
                setTitle("Account");
                attachMyAccountFragment();
                return true;
            case R.id.view_bill:
                setTitle("Bills");
                attachBillsFragment();
                return true;
            case R.id.my_orders:
                setTitle("Orders");
                attachMyOrders();
                return true;
            case R.id.explore_boxes: {
                setTitle("Explore");
                attachExploreBoxes();
                return true;
            }
        }
        return true;
    }

    private void attachExploreBoxes() {
        ExploreBoxes fragment = new ExploreBoxes();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment);
        fragmentTransaction.commit();
    }

    private void attachMyOrders() {
        MyOrders fragment = new MyOrders();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment);
        fragmentTransaction.commit();
    }

    private void attachBillsFragment() {
        Bills fragment = new Bills();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment);
        fragmentTransaction.commit();
    }

    private void attachMyAccountFragment() {
        MyAccount fragment = new MyAccount();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment);
        fragmentTransaction.commit();
    }

    private void attachMyBoxesFragment() {
        MyBoxes fragment = new MyBoxes();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment);
        fragmentTransaction.commit();
    }

    @Override
    void onClick(int id) {

    }
}
