package one.thebox.android.activity;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.PeriodicTask;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import one.thebox.android.Events.FABVisibilityOrderFactorEvent;
import one.thebox.android.Events.FABVisibilityTabFactorEvent;
import one.thebox.android.Events.UpdateOrderItemEvent;
import one.thebox.android.Helpers.cart.CartHelper;
import one.thebox.android.Helpers.cart.ProductQuantity;
import one.thebox.android.Models.items.Box;
import one.thebox.android.Models.items.Category;
import one.thebox.android.Models.promotion.PromotionalOffer;
import one.thebox.android.Models.search.SearchResult;
import one.thebox.android.Models.user.User;
import one.thebox.android.Models.notifications.Params;
import one.thebox.android.Models.update.CommonPopupDetails;
import one.thebox.android.Models.update.Setting;
import one.thebox.android.R;
import one.thebox.android.activity.order.OrderCalenderActivity;
import one.thebox.android.app.Keys;
import one.thebox.android.app.TheBox;
import one.thebox.android.fragment.dialog.UpdateDialogFragment;
import one.thebox.android.services.SettingService;
import one.thebox.android.services.notification.MyInstanceIDListenerService;
import one.thebox.android.services.notification.MyTaskService;
import one.thebox.android.services.notification.RegistrationIntentService;
import one.thebox.android.app.Constants;
import one.thebox.android.fragment.CartFragment;
import one.thebox.android.fragment.MyAccountFragment;
import one.thebox.android.fragment.MyBoxTabFragment;
import one.thebox.android.fragment.SearchDetailFragment;
import one.thebox.android.util.CoreGsonUtils;
import one.thebox.android.util.OnFragmentInteractionListener;
import one.thebox.android.util.PrefUtils;

import static one.thebox.android.fragment.SearchDetailFragment.BROADCAST_EVENT_TAB;

/**
 * Created by Ajeet Kumar Meena on 8/10/15.
 * <p>
 * Modified by Developers on07/06/2017.
 */
public class MainActivity extends BaseActivity implements
        NavigationView.OnNavigationItemSelectedListener
        , View.OnClickListener, OnFragmentInteractionListener,
        FragmentManager.OnBackStackChangedListener {

    public static final String TASK_TAG_PERIODIC = "periodic_task";
    private static final String TAG = "MainActivity";

    /**
     * GcmNetworkmanger for push notification heartbeat
     */
    private GcmNetworkManager mGcmNetworkManager;

    private NavigationView navigationView;
    private LinearLayout navigationViewBottom;

    private DrawerLayout drawerLayout;
    private ImageView buttonSpecialAction, searchAction, btn_search, chatbutton;
    private Menu menu;
    private int numberOfItemIncart = -1;
    private TextView userNameTextView;
    private Setting setting = new Setting();
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private User user;
    private FragmentManager fragmentManager;
    private AppBarLayout appBarLayout;
    private FloatingActionButton floatingActionButton;
    private int currentYear, currentMonth;
    private boolean orderAvailable = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startService(new Intent(this, RegistrationIntentService.class));
        startService(new Intent(this, MyInstanceIDListenerService.class));

        //get_gcm_network_manager
        mGcmNetworkManager = GcmNetworkManager.getInstance(this);

        user = PrefUtils.getUser(this);
        setting = new SettingService().getSettings(this);
        fragmentManager = getSupportFragmentManager();
        shouldHandleDrawer();
        initViews();
        setupNavigationDrawer();

        //update app settings
        updateAppSettings();

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getContentView().getWindowToken(), 0);

        // Doing it for notification so "My Deliveries" fragment can be attached by default
        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            onNewIntent(getIntent());

        } else {
            attachMyBoxesFragment(1, false);

        }

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, UniversalSearchActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter(BROADCAST_EVENT_TAB));

        //Preference to load Subscription when user open the app
        PrefUtils.putBoolean(this, Keys.LOAD_CAROUSEL, true);


        setCartOnToolBar();
        //Check for App Update
        checkAppUpdate();
        //check for showing message to user usign dialog
        checkForCommonDialog();
    }

    /**
     * Update Setting Data For the APP
     */
    public void updateAppSettings() {
        try {
            if (setting.getCartItems() != null) {
                //update Cart
                CartHelper.updateCart(setting.getParsedCartItems());
                //synced memory with cart
                ProductQuantity.syncedWithCart(setting.getParsedCartUuids(), this);
            }

            if (setting.getCartPollingTime() != 0) {
                Constants.UPDATE_CART_POLLING_TIME = setting.getCartPollingTime();
            }

            /**
             * Parse Promotional Message
             */
            if (setting.getPromotionalOffers() != null) {
                ArrayList<PromotionalOffer> firstTimeOffers = new ArrayList<>();
                ArrayList<PromotionalOffer> tutorialMessage = new ArrayList<>();
                ArrayList<PromotionalOffer> storedTutorialMessages = CoreGsonUtils.fromJsontoArrayList(
                        PrefUtils.getString(this, Constants.EXTRA_PROMOTIONAL_TUTORIAL), PromotionalOffer.class);

                for (PromotionalOffer promotionalOffer : setting.getPromotionalOffers()) {
                    //offer shown to first time user
                    if (promotionalOffer.isFirstTime()) {
                        firstTimeOffers.add(promotionalOffer);
                    } else {
                        //tutorial shown to non- first time users
                        if (storedTutorialMessages != null) {
                            if (!storedTutorialMessages.isEmpty()) {
                                boolean doesExit = false;
                                for (PromotionalOffer storeTutorial : storedTutorialMessages) {
                                    //check if this offer exist earlier
                                    if (promotionalOffer.getUuid().equalsIgnoreCase(storeTutorial.getUuid())) {
                                        doesExit = true;
                                        //copy the checked status of this offer
                                        promotionalOffer.setChecked(storeTutorial.isChecked());
                                        tutorialMessage.add(promotionalOffer);
                                        break;
                                    }
                                }
                                if (!doesExit) {
                                    promotionalOffer.setChecked(false);
                                    tutorialMessage.add(promotionalOffer);
                                }

                            } else {
                                promotionalOffer.setChecked(false);
                                tutorialMessage.add(promotionalOffer);
                            }
                        } else {
                            promotionalOffer.setChecked(false);
                            tutorialMessage.add(promotionalOffer);
                        }

                    }
                }

                //sort tutorial according to the priority
                if (!tutorialMessage.isEmpty()) {
                    Collections.sort(tutorialMessage, new Comparator<PromotionalOffer>() {
                        @Override
                        public int compare(PromotionalOffer o1, PromotionalOffer o2) {
                            if (o1.getPriority() < o2.getPriority()) {
                                return -1;
                            } else {
                                return 1;
                            }
                        }
                    });
                }

                //save the values in preferences
                PrefUtils.putString(this, Constants.EXTRA_PROMOTIONAL_OFFER_FIRST_TIME, CoreGsonUtils.toJson(firstTimeOffers));
                PrefUtils.putString(this, Constants.EXTRA_PROMOTIONAL_TUTORIAL, CoreGsonUtils.toJson(tutorialMessage));

                firstTimeOffers.clear();
                tutorialMessage.clear();
                if (storedTutorialMessages != null) {
                    storedTutorialMessages.clear();
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //Preference to load Subscription when user open the app be false
        PrefUtils.putBoolean(this, Keys.LOAD_CAROUSEL, false);

    }


    @Subscribe
    public void onUpdateOrderEvent(UpdateOrderItemEvent onUpdateOrderItem) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setCartOnToolBar();
            }
        });
    }


    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (intent.getExtras() != null) {
                        numberOfItemIncart = intent.getIntExtra(Constants.EXTRA_ITEMS_IN_CART, -1);
                    }
                    setCartOnToolBar();
                }
            });

        }
    };

    private void setCartOnToolBar() {

        FrameLayout cartFrame = (FrameLayout) findViewById(R.id.frame_cart_icon);
        TextView noOfItemsInCart = (TextView) findViewById(R.id.no_of_items_in_cart);
        int numberOfItems;
        if (numberOfItemIncart == -1) {
            numberOfItems = ProductQuantity.getCartSize();
        } else {
            numberOfItems = numberOfItemIncart;
        }

        if (numberOfItems > 0) {
            cartFrame.setVisibility(View.VISIBLE);
            noOfItemsInCart.setVisibility(View.VISIBLE);
            noOfItemsInCart.setText(String.valueOf(numberOfItems));
            cartFrame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try {
                        //check if cart fragment is visible or not
                        CartFragment cartFragment = (CartFragment) fragmentManager.findFragmentByTag("cart_fragment");
                        if (cartFragment != null && cartFragment.isVisible()) {
                            //blank
                        } else {

                            /**
                             * CleverTap Event Cart Icon clicked
                             */

                            startActivity(new Intent(MainActivity.this, MainActivity.class).putExtra(Constants.EXTRA_ATTACH_FRAGMENT_NO, 3));
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            noOfItemsInCart.setText("");
            noOfItemsInCart.setVisibility(View.GONE);
            cartFrame.setVisibility(View.GONE);
        }
    }

    public void setupNavigationDrawer() {
        fragmentManager.addOnBackStackChangedListener(this);
        navigationView.setNavigationItemSelectedListener(this);
        //navigationViewBottom.setNavigationItemSelectedListener(this);
        this.menu = navigationView.getMenu();
        addBoxesToMenu();
        View headerView = navigationView.getHeaderView(0);
        userNameTextView = (TextView) headerView.findViewById(R.id.user_name_text_view);
        userNameTextView.setText(user.getName());
        setToolbar((Toolbar) findViewById(R.id.toolbar));
        setSupportActionBar(getToolbar());
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        putToggleListener();
    }

    public void putToggleListener() {
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
    }

    private void initViews() {
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationViewBottom = (LinearLayout) findViewById(R.id.navigation_drawer_bottom);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.floating_action_button);
        buttonSpecialAction = (ImageView) findViewById(R.id.button_special_action);
        chatbutton = (ImageView) findViewById(R.id.chat_button);
        btn_search = (ImageView) findViewById(R.id.btn_search);
        buttonSpecialAction.setOnClickListener(this);

        appBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);
        navigationViewBottom.setOnClickListener(this);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, OrderCalenderActivity.class);
                intent.putExtra(Constants.EXTRA_CALENDER_SELECTED_YEAR, currentYear);
                intent.putExtra(Constants.EXTRA_CALENDER_SELECTED_MONTH, currentMonth);
                startActivity(intent);
            }
        });
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
                attachMyBoxesFragment(0, false);
                return true;
            case R.id.my_account:
                attachMyAccountFragment();
                return true;
            case R.id.view_cart:
                attachCartFragment();
                return true;
            default: {
                String menuName = (String) menuItem.getTitle();
                openBoxByName(menuName);
            }
        }
        return true;
    }


    /**
     * Check for App Update Option
     */
    private void checkAppUpdate() {
        try {
            if (setting != null) {
                if (setting.isNew_version_available()) {
                    if (isPopupRequiredToDisplay() || setting.isForce_update()) {
                        if (null != setting.getUpdatePopupDetails()) {
                            UpdateDialogFragment dialogFragment = UpdateDialogFragment.getInstance(setting.getUpdatePopupDetails(),
                                    setting.isForce_update());
                            dialogFragment.show(fragmentManager, "Update");
                            if (!setting.isForce_update()) {
                                saveCacheTime();
                            }
                        }
                    }
                }
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * Check for Common Dialog
     */
    private void checkForCommonDialog() {
        try {
            if (setting != null) {
                if (setting.getCommonPopupDetails() != null) {
                    //if false then display popup
                    if (!PrefUtils.getBoolean(this, Keys.COMMON_DIALOG_POPUP)) {

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //show the popup or dialog
                                displayCommonDialog(setting.getCommonPopupDetails());
                            }
                        }, 2000);


                    }
                }
            }
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }
    }

    private void displayCommonDialog(CommonPopupDetails commonPopupDetails) {
        try {

            final Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setContentView(R.layout.dialog_common_details);
            dialog.getWindow().getAttributes().width = LinearLayout.LayoutParams.FILL_PARENT;
            dialog.show();

            TextView header = (TextView) dialog.findViewById(R.id.header_title);
            TextView content = (TextView) dialog.findViewById(R.id.text_content);
            TextView okayButtonText = (TextView) dialog.findViewById(R.id.okay);
            CardView okayButton = (CardView) dialog.findViewById(R.id.holder_button);

            header.setText(Html.fromHtml(commonPopupDetails.getTitle()));
            content.setText(Html.fromHtml(commonPopupDetails.getContent()));
            okayButtonText.setText(commonPopupDetails.getButtonText());
            content.setMovementMethod(ScrollingMovementMethod.getInstance());

            okayButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //set in the prefferences, popup shown
                    PrefUtils.putBoolean(MainActivity.this, Keys.COMMON_DIALOG_POPUP, true);

                    dialog.dismiss();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
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

    /**
     * Called when we click on Navigation Drawer Item
     */
    private void openBoxByName(String name) {
        if (name.equals("FAQs")) {
            Intent intent = new Intent(MainActivity.this, HotLineActivity.class);
            intent.putExtra(Constants.EXTRA_NAVIGATE_TO_HOTLINE_FAQ, true);
            startActivity(intent);
        } else if (name.equals("Terms of Use")) {
            startActivity(new Intent(MainActivity.this, TermsOfUserActivity.class));
        } else {
            //search from setting Box List
            searchBoxUuidForBoxTitle(name);
        }
    }

    /**
     * Attach Cart Fragment
     */
    private void attachCartFragment() {
        CartFragment fragment = new CartFragment();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container_overlay, fragment, "cart_fragment").addToBackStack("cart_fragment");
        fragmentTransaction.commit();
    }

    /**
     * My Account Fragment
     */
    private void attachMyAccountFragment() {
        MyAccountFragment fragment = new MyAccountFragment();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment, "My_Account").addToBackStack("My_Account");
        fragmentTransaction.commit();
        appBarLayout.setExpanded(true, true);
    }

    private void openContactUsActivity() {
        startActivity(new Intent(MainActivity.this, HotLineActivity.class));
        drawerLayout.closeDrawers();
    }

    /**
     * Box tab Fragment
     *
     * @param default_position
     * @param show_loader
     */
    public void attachMyBoxesFragment(int default_position, boolean show_loader) {
        clearBackStack();
        MyBoxTabFragment fragment = new MyBoxTabFragment();

        chatbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openContactUsActivity();
            }
        });

        Bundle bundle = new Bundle();
        bundle.putInt("default_position", default_position);
        bundle.putBoolean("show_loader", show_loader);
        fragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment, "My_Boxes");
        fragmentTransaction.commit();
        appBarLayout.setExpanded(true, true);
    }


    /**
     * Called from Search; when you search
     */
    public void attachSearchDetailFragment(SearchResult searchResult) {
        getToolbar().setSubtitle(null);
        btn_search.setVisibility(View.VISIBLE);

        buttonSpecialAction.setVisibility(View.VISIBLE);
        buttonSpecialAction.setImageResource(R.drawable.ic_box);
        buttonSpecialAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attachMyBoxesFragment(0, false);
            }
        });

        SearchDetailFragment fragment = SearchDetailFragment.getInstance(searchResult);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment).addToBackStack("Search_Details");
        fragmentTransaction.commit();

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getContentView().getWindowToken(), 0);
        appBarLayout.setExpanded(true, true);
    }

    /**
     * Attach Search Detail Fragment; when you have Box Uuid and Box Title.
     * Fetch category in the Fragment and display
     *
     * @param boxUuid
     * @param boxTitle
     */
    private void attachSearchDetailFragmentForCategory(String boxUuid, String boxTitle) {
        getToolbar().setSubtitle(null);

        btn_search.setVisibility(View.VISIBLE);

        buttonSpecialAction.setVisibility(View.VISIBLE);
        buttonSpecialAction.setImageResource(R.drawable.ic_box);
        buttonSpecialAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attachMyBoxesFragment(0, false);
            }
        });

        SearchDetailFragment fragment = SearchDetailFragment.getInstance(boxUuid, boxTitle);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment).addToBackStack("Search_Details");
        fragmentTransaction.commit();

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getContentView().getWindowToken(), 0);
        appBarLayout.setExpanded(true, true);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.navigation_drawer_bottom:
                openContactUsActivity();
                break;
        }
    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {


        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStackImmediate();
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            //exit app
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


    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (userNameTextView.getText().toString().isEmpty()) {
                user = PrefUtils.getUser(this);
                if (user.getName() != null && !user.getName().isEmpty()) {
                    userNameTextView.setText(user.getName());
                }
            }
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }

        startPeriodicTask();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);

        // For the purposes of this sample, cancel all tasks when the app is stopped.
        mGcmNetworkManager.cancelAllTasks(MyTaskService.class);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        switch (intent.getIntExtra(Constants.EXTRA_ATTACH_FRAGMENT_NO, 0)) {
            case 0: {
                attachMyBoxesFragment(1, true);
                break;
            }

            //Attaching Myitems fragment
            //Called after payment is confirmed
            case 1: {
                attachMyBoxesFragment(0, true);
                break;
            }

            //Attaching MyDeliveries fragment
            case 2: {
                attachMyBoxesFragment(2, false);
                break;
            }

            case 3: {
                //attach cart and display
                attachCartFragment();
                break;
            }
            case 4: {
                /**
                 * Search Item- navigate via this to Browse category
                 */
                attachSearchDetailFragment(CoreGsonUtils.fromJson(
                        intent.getStringExtra(Constants.EXTRA_SEARCH_RESULT_DATA), SearchResult.class));
                break;
            }
            case 5: {
                /**
                 * Called from Suggested Box CartProductDetail to open Search Detail Fragment
                 */
                attachSearchDetailFragmentForBoxUuid(intent);
                break;
            }
            case 6: {
                attachCategoriesFragment(intent);
                break;
            }

            /**
             * Notifications Action Handiling
             */
            case 9: {
                attachMyBoxesFragment(2, false);
                break;
            }
            case 10: {
                attachMyBoxesFragment(1, false);
                break;
            }
            case 21: {
                //attach category Fragment
                performCategoryNotification(intent);
                break;
            }
            case 12: {
                attachMyBoxesFragment(2, false);
                break;
            }

            case 13: {
                attachMyBoxesFragment(0, false);
                break;
            }
            default:
                attachMyBoxesFragment(1, false);
        }
    }

    /**
     * Get Data and opens Search Detail Fragment for the Box Uuid
     *
     * @param intent
     */
    public void attachSearchDetailFragmentForBoxUuid(Intent intent) {
        try {
            String boxUuid = intent.getStringExtra(Constants.EXTRA_BOX_UUID);
            String boxTitle = intent.getStringExtra(Constants.EXTRA_BOX_NAME);
            attachSearchDetailFragmentForCategory(boxUuid, boxTitle);
        } catch (Exception e) {

        }
    }

    /**
     * Category Notification
     *
     * @param intent
     */
    //TODO need to re-configure this
    private void performCategoryNotification(Intent intent) {
        //check if Box Fragment is visible or not

        //bad technic, need to reafactor
        attachMyBoxesFragment(1, false);
        attachCategoriesFragmentForNotifications(intent);
    }

    /**
     * Display Category Tab with Products
     */
    private void attachCategoriesFragment(Intent intent) {
        getToolbar().setSubtitle(null);
        buttonSpecialAction.setVisibility(View.VISIBLE);
        buttonSpecialAction.setImageResource(R.drawable.ic_box);
        buttonSpecialAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attachMyBoxesFragment(0, false);
            }
        });


        if (intent.getExtras() != null) {
            ArrayList<Category> categories = CoreGsonUtils.fromJsontoArrayList(intent.getStringExtra(Constants.EXTRA_BOX_CATEGORY), Category.class);
            int clickedPosition = intent.getIntExtra(Constants.EXTRA_CLICK_POSITION, 0);
            String boxName = intent.getStringExtra(Constants.EXTRA_BOX_NAME);
            String clickedCategoryUid = intent.getStringExtra(Constants.EXTRA_CLICKED_CATEGORY_UID);

            SearchDetailFragment fragment = SearchDetailFragment.getInstance(categories, clickedCategoryUid, clickedPosition, boxName);
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame, fragment).addToBackStack("Search_Details");
            fragmentTransaction.commit();

            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getContentView().getWindowToken(), 0);
            appBarLayout.setExpanded(true, true);
        }
    }

    /**
     * Attach Search Detail Fragment on click from Notifications
     */
    public void attachCategoriesFragmentForNotifications(Intent intent) {
        //parse the Parameters
        Params params = CoreGsonUtils.fromJson(intent.getStringExtra(Constants.EXTRA_NOTIFICATION_PARAMETER), Params.class);

        getToolbar().setSubtitle(null);
        buttonSpecialAction.setVisibility(View.VISIBLE);
        buttonSpecialAction.setImageResource(R.drawable.ic_box);
        buttonSpecialAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attachMyBoxesFragment(1, false);
            }
        });

        SearchDetailFragment fragment = SearchDetailFragment.getInstance(params.getCategoryId(), params.getBoxName());
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment).addToBackStack("Search_Details");
        fragmentTransaction.commit();

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getContentView().getWindowToken(), 0);
        appBarLayout.setExpanded(true, true);

    }

    @Override
    public void showDrawerToggle(boolean showDrawerToggle) {

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

    public ImageView getChatbutton() {
        return chatbutton;
    }

    public ImageView getButtonSearch() {
        return btn_search;
    }

    public DrawerLayout getDrawerLayout() {
        return drawerLayout;
    }

    public void setDrawerLayout(DrawerLayout drawerLayout) {
        this.drawerLayout = drawerLayout;
    }

    public boolean isOrderAvailable() {
        return orderAvailable;
    }

    public void setOrderAvailable(boolean orderAvailable) {
        this.orderAvailable = orderAvailable;
    }

    /**
     * Add Box Name to menu in navigation drawer layout
     */
    public void addBoxesToMenu() {

        if (setting.getBoxes() != null) {
            for (Box box : setting.getBoxes()) {
                menu.add(box.getTitle());
            }
        }
        menu.add("FAQs");
        menu.add("Terms of Use");

        navigationView.invalidate();
    }

    public void startPeriodicTask() {
        Log.d(TAG, "startPeriodicTask");

        // [START start_periodic_task]
        PeriodicTask task = new PeriodicTask.Builder()
                .setService(MyTaskService.class)
                .setTag(TASK_TAG_PERIODIC)
                .setPeriod(300L)
                .build();

        mGcmNetworkManager.schedule(task);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            //search results
            if (requestCode == 1) {
                if (data.getExtras() != null) {
                    onNewIntent(data);
                }
            }/**
             * Add or Edit User Address from MyAccountFragment
             * Edit User Profile
             *
             * 2 - Address
             * 3- User Profile
             */
            else if (requestCode == 2 || requestCode == 3) {
                if (data.getExtras() != null) {
                    MyAccountFragment myAccountFragment = (MyAccountFragment) fragmentManager.findFragmentByTag("My_Account");
                    if (myAccountFragment != null && myAccountFragment.isVisible()) {
                        myAccountFragment.onActivityResult(requestCode, resultCode, data);
                    }
                }
            }
        } catch (ActivityNotFoundException anf) {
            anf.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Search Box Uuid for Box Title
     */
    public void searchBoxUuidForBoxTitle(String title) {
        if (setting.getBoxes() != null) {
            if (setting.getBoxes().size() > 0) {
                for (Box box : setting.getBoxes()) {
                    if (box.getTitle().equalsIgnoreCase(title)) {
                        navigateToSearchDetailFragment(box.getUuid(), box.getTitle());
                        break;
                    }
                }

            }
        }
    }

    /**
     * Move to Search Detail Fragment
     * fetch category and Display product
     */
    public void navigateToSearchDetailFragment(String boxUuid, String boxTitle) {
        attachSearchDetailFragmentForCategory(boxUuid, boxTitle);
    }

    @Subscribe
    public void eventFABTabFactorVisibility(final FABVisibilityTabFactorEvent fabVisibilityTabFactorEvent) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isOrderAvailable()) {
                    if (fabVisibilityTabFactorEvent != null) {
                        if (fabVisibilityTabFactorEvent.isVisible()) {
                            floatingActionButton.setVisibility(View.VISIBLE);
                        } else {
                            floatingActionButton.setVisibility(View.GONE);
                        }
                    } else {
                        floatingActionButton.setVisibility(View.GONE);
                    }
                } else {
                    floatingActionButton.setVisibility(View.GONE);
                }
            }
        });
    }

    @Subscribe
    public void eventFABOrderFactorVisibility(final FABVisibilityOrderFactorEvent fabVisibilityOrderFactorEvent) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (fabVisibilityOrderFactorEvent != null) {
                    if (fabVisibilityOrderFactorEvent.isOrderAvailable()) {
                        setOrderAvailable(true);

                        currentMonth = fabVisibilityOrderFactorEvent.getCurrentMonth();
                        currentYear = fabVisibilityOrderFactorEvent.getCurrentYear();
                    } else {
                        setOrderAvailable(false);
                    }
                }
            }
        });
    }

}

