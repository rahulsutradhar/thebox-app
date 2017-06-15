package one.thebox.android.fragment;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;

import one.thebox.android.Events.OnCategorySelectEvent;
import one.thebox.android.Events.ShowSpecialCardEvent;
import one.thebox.android.Events.TabEvent;
import one.thebox.android.Helpers.cart.CartHelper;
import one.thebox.android.Models.items.Category;
import one.thebox.android.Models.search.SearchResult;
import one.thebox.android.R;
import one.thebox.android.ViewHelper.AppBarObserver;
import one.thebox.android.ViewHelper.ConnectionErrorViewHelper;
import one.thebox.android.ViewHelper.ViewPagerAdapter;
import one.thebox.android.activity.MainActivity;
import one.thebox.android.api.Responses.boxes.BoxCategoryResponse;
import one.thebox.android.app.Constants;
import one.thebox.android.app.TheBox;
import one.thebox.android.util.CoreGsonUtils;
import one.thebox.android.util.PrefUtils;
import pl.droidsonroids.gif.GifImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchDetailFragment extends BaseFragment implements AppBarObserver.OnOffsetChangeListener {

    public static final String BOX_NAME = "box_name";
    private static final String EXTRA_CAT_ID = "extra_cat_id";
    public static final String EXTRA_NUMBER_OF_TABS = "extra_number_of_tabs";
    public static final String BROADCAST_EVENT_TAB = "broadcast_event_tab";
    private String boxName;
    private View rootView;
    private LinearLayout linearLayoutHolder;
    private GifImageView progressBar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private ArrayList<Category> categories = new ArrayList<>();
    private String categoryUid;
    private String boxUuid;
    private SearchResult searchResult;
    private String searchQuery = "";


    private int clickPosition;
    private TextView noResultFound, itemsInCart, savings;

    private CardView specialCardView;
    private ConnectionErrorViewHelper connectionErrorViewHelper;
    public static int POSITION_OF_VIEW_PAGER;
    private boolean previousScrollAction = false;
    private int noOfTabs;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (getActivity() == null) {
                return;
            }
            int temp = noOfTabs;
            noOfTabs = intent.getIntExtra(EXTRA_NUMBER_OF_TABS, 0);
            if (noOfTabs > 0) {
                if (temp != noOfTabs)
                    showSpecialCardEvent(new ShowSpecialCardEvent(true));
            } else {
                showSpecialCardEvent(new ShowSpecialCardEvent(false));
            }
        }
    };

    public SearchDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Called from Box Category
     *
     * @param categories
     * @param categoryUid
     * @param clickPosition
     * @param boxName
     */
    public static SearchDetailFragment getInstance(ArrayList<Category> categories, String categoryUid, int clickPosition, String boxName) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.EXTRA_BOX_CATEGORY, CoreGsonUtils.toJson(categories));
        bundle.putString(Constants.EXTRA_CLICKED_CATEGORY_UID, categoryUid);
        bundle.putInt(Constants.EXTRA_CLICK_POSITION, clickPosition);
        bundle.putString(Constants.EXTRA_BOX_NAME, boxName);
        SearchDetailFragment searchDetailFragment = new SearchDetailFragment();
        searchDetailFragment.setArguments(bundle);
        return searchDetailFragment;
    }

    /**
     * Called from Main Activity To fetch category For box UUid
     * On Click Navigation item
     *
     * @param boxUuid
     * @param boxName
     * @return
     */
    public static SearchDetailFragment getInstance(String boxUuid, String boxName) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.EXTRA_BOX_UUID, boxUuid);
        bundle.putString(Constants.EXTRA_BOX_NAME, boxName);
        SearchDetailFragment searchDetailFragment = new SearchDetailFragment();
        searchDetailFragment.setArguments(bundle);
        return searchDetailFragment;
    }

    /**
     * Called When you Do Search
     *
     * @param searchResult
     * @return
     */
    public static SearchDetailFragment getInstance(SearchResult searchResult) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.EXTRA_SEARCH_RESULT, CoreGsonUtils.toJson(searchResult));
        SearchDetailFragment searchDetailFragment = new SearchDetailFragment();
        searchDetailFragment.setArguments(bundle);
        return searchDetailFragment;
    }

    /**
     * Call for Notification Click
     */
    public static SearchDetailFragment getInstance(int catId, String boxName) {
        Bundle bundle = new Bundle();
        bundle.putInt(EXTRA_CAT_ID, catId);
        bundle.putString(BOX_NAME, boxName);
        SearchDetailFragment searchDetailFragment = new SearchDetailFragment();
        searchDetailFragment.setArguments(bundle);
        return searchDetailFragment;
    }

    private void initVariables() {
        try {

            categories = CoreGsonUtils.fromJsontoArrayList(getArguments().getString(Constants.EXTRA_BOX_CATEGORY), Category.class);
            clickPosition = getArguments().getInt(Constants.EXTRA_CLICK_POSITION);
            categoryUid = getArguments().getString(Constants.EXTRA_CLICKED_CATEGORY_UID);
            boxName = getArguments().getString(Constants.EXTRA_BOX_NAME);
            boxUuid = getArguments().getString(Constants.EXTRA_BOX_UUID);
            searchResult = CoreGsonUtils.fromJson(getArguments().getString(Constants.EXTRA_SEARCH_RESULT), SearchResult.class);

            if (searchResult != null) {
                boxUuid = searchResult.getBoxUuid();
                categoryUid = searchResult.getCategoryUuid();
                searchQuery = searchResult.getTitle();
                boxName = searchResult.getBoxTitle();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void initViews() {
        linearLayoutHolder = (LinearLayout) rootView.findViewById(R.id.holder);
        progressBar = (GifImageView) rootView.findViewById(R.id.progress_bar);
        tabLayout = (TabLayout) rootView.findViewById(R.id.tabs);
        viewPager = (ViewPager) rootView.findViewById(R.id.viewPager);
        onTabEvent(new TabEvent(CartHelper.getCartSize()));
        connectionErrorViewHelper = new ConnectionErrorViewHelper(rootView, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check data and decide for navigation
                checkAndDecideNavigation();
            }
        });
        noResultFound = (TextView) rootView.findViewById(R.id.no_result_found);
        specialCardView = (CardView) rootView.findViewById(R.id.special_card);
        specialCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MainActivity.class).putExtra(MainActivity.EXTRA_ATTACH_FRAGMENT_NO, 3));
            }
        });
        itemsInCart = (TextView) rootView.findViewById(R.id.items_in_cart);
        savings = (TextView) rootView.findViewById(R.id.savings);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        initVariables();

        if (boxName != null) {
            if (!boxName.isEmpty()) {
                setToolbarTitle();
            }
        }
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_search_detail, container, false);
            initViews();
            setupAppBarObserver();
            checkAndDecideNavigation();
        }
        return rootView;
    }

    /**
     * Check And Decide navigation
     */
    public void checkAndDecideNavigation() {
        if (!categories.isEmpty()) {
            //when category list is passed
            setupViewPagerAndTabsForBoxCategory();
        } else {

            /**
             * when you have box UUID is passed
             * Request Serve and fetch All Categories
             */
            getCategoriesForBoxUuid();

        }

    }


    /**
     * Set The Tabs When clicked category from Box
     */
    private void setupViewPagerAndTabsForBoxCategory() {
        try {
            if (getActivity() == null) {
                return;
            }
            progressBar.setVisibility(View.GONE);
            final ViewPagerAdapter adapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager(), getActivity());
            for (int i = 0; i < categories.size(); i++) {
                Fragment fragment = SearchDetailItemsFragment.getInstance(categories.get(i), categories, i, searchQuery);
                adapter.addFragment(fragment, categories.get(i));
            }
            viewPager.setAdapter(adapter);
            tabLayout.setupWithViewPager(viewPager);
            int length = tabLayout.getTabCount();
            tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    tab.setCustomView(adapter.getTabView(tab.getCustomView(), tab.getPosition(), true));
                    viewPager.setCurrentItem(tab.getPosition());
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                    tab.setCustomView(adapter.getTabView(tab.getCustomView(), tab.getPosition(), false));
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });
            for (int i = 0; i < length; i++) {
                if (i == 0) {
                    tabLayout.getTabAt(i).setCustomView(adapter.getTabView(i, true));
                } else {
                    tabLayout.getTabAt(i).setCustomView(adapter.getTabView(i, false));
                }
            }
            viewPager.setCurrentItem(clickPosition);
            POSITION_OF_VIEW_PAGER = clickPosition;
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    POSITION_OF_VIEW_PAGER = position;
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });

            /**
             * Save CleverTap Event; BrowseCategory
             */
            setCleverTapEventBrowseCategory(categories.get(POSITION_OF_VIEW_PAGER));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Request Server And Fetch Cartegories
     */
    public void getCategoriesForBoxUuid() {
        progressBar.setVisibility(View.VISIBLE);
        connectionErrorViewHelper.isVisible(false);

        TheBox.getAPIService()
                .getCategories(PrefUtils.getToken(getActivity()), boxUuid)
                .enqueue(new Callback<BoxCategoryResponse>() {
                    @Override
                    public void onResponse(Call<BoxCategoryResponse> call, Response<BoxCategoryResponse> response) {
                        progressBar.setVisibility(View.GONE);
                        try {
                            if (response.isSuccessful()) {
                                if (response.body() != null) {
                                    categories.clear();
                                    categories.addAll(response.body().getCategories());
                                    //check for category UUID
                                    checkSearchedCategory();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<BoxCategoryResponse> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        connectionErrorViewHelper.isVisible(true);

                    }
                });
    }

    /**
     * Check Category UUID Searched for and then Display
     */
    public void checkSearchedCategory() {
        if (categoryUid != null) {
            if (!categoryUid.isEmpty()) {
                int index = 0;
                //search catgory in the list and find the position
                for (Category category : categories) {
                    if (category.getUuid().equalsIgnoreCase(categoryUid)) {
                        clickPosition = index;
                        break;
                    }
                    index++;
                }
                //set the view pager and tab
                setupViewPagerAndTabsForBoxCategory();
            } else {
                //set the view pager and tab
                setupViewPagerAndTabsForBoxCategory();
            }
        } else {
            //set the view pager and tab
            setupViewPagerAndTabsForBoxCategory();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).getSearchViewHolder().setVisibility(View.GONE);
        ((MainActivity) getActivity()).getButtonSearch().setVisibility(View.VISIBLE);
        ((MainActivity) getActivity()).getChatbutton().setVisibility(View.GONE);

        ((MainActivity) getActivity()).getButtonSpecialAction().setVisibility(View.VISIBLE);
        ((MainActivity) getActivity()).getButtonSpecialAction().setImageDrawable(getResources().getDrawable(R.drawable.ic_thebox_identity_mono));
        ((MainActivity) getActivity()).getButtonSpecialAction().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MainActivity.class)
                        .putExtra(MainActivity.EXTRA_ATTACH_FRAGMENT_NO, 7));
            }
        });

        ((MainActivity) getActivity()).getSearchAction().setVisibility(View.VISIBLE);
        ((MainActivity) getActivity()).getSearchAction().setImageResource(R.drawable.menu_icon);
        ((MainActivity) getActivity()).getSearchAction().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).getDrawerLayout().openDrawer(Gravity.LEFT);
            }
        });


        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver,
                new IntentFilter(BROADCAST_EVENT_TAB));
    }


    public void setToolbarTitle() {
        ((MainActivity) getActivity()).getToolbar().setTitle(boxName);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onOffsetChange(int offset, int dOffset) {
        specialCardView.setTranslationY(-offset);
    }

    private void setupAppBarObserver() {
        AppBarObserver appBarObserver;
        Activity activity = getActivity();
        AppBarLayout appBarLayout = (AppBarLayout) activity
                .findViewById(R.id.app_bar_layout);
        if (appBarLayout != null) {
            appBarObserver = AppBarObserver.observe(appBarLayout);
            appBarObserver.addOffsetChangeListener(this);
        }
    }

    @Subscribe
    public void showSpecialCard() {

    }

    @Subscribe
    public void OnCategorySelectEvent(OnCategorySelectEvent onCategorySelectEvent) {
        int tabPosition = getTabPosition(onCategorySelectEvent.getCategory());
        if (tabPosition != -1) {
            viewPager.setCurrentItem(tabPosition);
        }
    }


    public int getTabPosition(Category selectedCategory) {
        int tabPosition = -1;
        if (!categories.isEmpty()) {
            int index = 0;
            for (Category category : categories) {
                if (category.getUuid().equalsIgnoreCase(selectedCategory.getUuid())) {
                    tabPosition = index;
                    break;
                }
                index++;
            }
        }
        return tabPosition;
    }

    public void onTabEvent(TabEvent tabEvent) {
        if (getActivity() == null) {
            return;
        }
    }

    @Subscribe
    public void showSpecialCardEvent(ShowSpecialCardEvent showSpecialCardEvent) {
        if (noOfTabs == 1) {
            itemsInCart.setText("You have " + noOfTabs + " item in your cart");
        } else if (noOfTabs > 1) {
            itemsInCart.setText("You have " + noOfTabs + " items in your cart");
        }
        if (previousScrollAction != showSpecialCardEvent.isVisible()) {
            if (showSpecialCardEvent.isVisible()) {
                specialCardView.setVisibility(View.VISIBLE);
                specialCardView.startAnimation(AnimationUtils.loadAnimation(TheBox.getInstance(), R.anim.passport_options_popup));

            } else {
                specialCardView.setVisibility(View.GONE);
                specialCardView.startAnimation(AnimationUtils.loadAnimation(TheBox.getInstance(), R.anim.passport_options_popdown));
            }
            previousScrollAction = showSpecialCardEvent.isVisible();
        }
    }

    /**
     * CleverTap Event
     */
    public void setCleverTapEventBrowseCategory(Category category) {
        try {
            HashMap<String, Object> hashMap = new HashMap<>();

            hashMap.put("box_title", getArguments().getString(BOX_NAME));
            hashMap.put("category_uuid", category.getUuid());
            hashMap.put("category_title", category.getTitle());
            hashMap.put("number_of_items", category.getNumberOfItem());

            TheBox.getCleverTap().event.push("browse_category", hashMap);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
